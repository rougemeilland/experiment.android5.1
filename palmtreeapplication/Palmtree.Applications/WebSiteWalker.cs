using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;

namespace Palmtree.Applications
{
    public abstract class WebSiteWalker
    {
        #region イベント

        public event EventHandler<ProgressedEventArgs> Progressed;

        #endregion

        #region プライベートフィールド

        private const int _default_max__concurrency_count = 5;
        private int _max_concurrency_count;
        private int _concurrency_count;
        private IDictionary<WebSiteResourceContext, object> _queued_flags;
        private Queue<WebSiteResourceContext> _pending_queue;
#if DEBUG
        private IDictionary<WebSiteResourceContext, object> _unread_urls;
#endif
        private int _total_page_count;
        private int _page_count;

        #endregion

        #region コンストラクタ

        public WebSiteWalker(int max_concurrent_access_count = _default_max__concurrency_count)
        {
            _max_concurrency_count = max_concurrent_access_count;
            _concurrency_count = 0;
            _queued_flags = new Dictionary<WebSiteResourceContext, object>();
            _pending_queue = new Queue<WebSiteResourceContext>();
#if DEBUG
            _unread_urls = new Dictionary<WebSiteResourceContext, object>();
#endif
            _total_page_count = 0;
            _page_count = 0;
        }

        #endregion

        #region パブリックメソッド


        public async Task Walk(WebSiteResourceContext initial_context)
        {
            _queued_flags.Clear();
            _pending_queue.Clear();
            _queued_flags.Add(initial_context, null);
#if DEBUG
            _unread_urls.Clear();
            _unread_urls.Add(initial_context, null);
#endif
            await WalkImp(initial_context);
#if DEBUG
            System.Diagnostics.Debug.Assert(_unread_urls.Any() == false, "未巡回のURLが存在します。");
#endif
        }

        #endregion

        #region プロテクテッドメソッド

        protected abstract IEnumerable<WebSiteResourceContext> ParseContent(WebSiteResourceContext context, string document);

        #endregion

        #region プライベートメソッド

        // 呼び出し条件:
        // 1) context が過去にアクセスされておらず (つまり IsQueued(context) が false であることが確認済み )である場合。
        private async Task WalkImp(WebSiteResourceContext context)
        {
            lock (this)
            {
                var current_concurrency_count = Interlocked.Increment(ref _concurrency_count);
                if (current_concurrency_count > _max_concurrency_count)
                {
                    _pending_queue.Enqueue(context);
                    Interlocked.Decrement(ref _concurrency_count);
                    return;
                }
            }
            try
            {
                await WalkImp2(context);
            }
            finally
            {
                Interlocked.Decrement(ref _concurrency_count);
            }
        }

        // 呼び出し条件:
        // 1) context が過去にアクセスされておらず (つまり IsQueued(context) が false であることが確認済み )、かつ
        // 2) Interlocked.Increment(ref _concurrency_count) が実行済みで復帰値が _max_concurrency_count 以下である場合。
        private async Task WalkImp2(WebSiteResourceContext context)
        {
#if DEBUG
            lock (this)
            {
                System.Diagnostics.Debug.Assert(_queued_flags.ContainsKey(context) == false, "WalkImp2の呼び出しの際に_queued_flagsに登録されていないURLが渡された。");
            }
#endif
            IEnumerable<Task> child_tasks = new Task[0];
            while (context != null)
            {
                var content = await ReadHtml(context);
                var urls = ParseContent(context, content).Where(url => !IsQueued(url)).ToArray();
                Interlocked.Add(ref _total_page_count, urls.Count());
                Interlocked.Increment(ref _page_count);
#if DEBUG
                foreach (var url in urls)
                {
                    System.Diagnostics.Debug.Assert(_unread_urls.ContainsKey(url) == false, "すでに登録されているURLを新たに登録しようとしました。");
                    _unread_urls.Add(url, null);
                }
                System.Diagnostics.Debug.Assert(_unread_urls.ContainsKey(context) == true, "登録されていないURLを削除しようとしました。");
                _unread_urls.Remove(context);
#endif
                if (urls.Any())
                {
                    context = urls.First();
                    child_tasks = child_tasks.Concat(urls.Skip(1).Select(url => WalkImp(url)).ToArray());
                }
                else
                {
                    lock (this)
                    {
                        context = _pending_queue.Any() ? _pending_queue.Dequeue() : null;
                    }
                }
            }
            foreach (var child_task in child_tasks)
                await child_task;
        }

        private bool IsQueued(WebSiteResourceContext context)
        {
            lock (_queued_flags)
            {
                if (_queued_flags.ContainsKey(context))
                    return (true);
                _queued_flags.Add(context, null);
                return (false);
            }
        }

        private async Task<string> ReadHtml(WebSiteResourceContext context)
        {
            using (var client = new HttpClient())
            {
                client.DefaultRequestHeaders.Referrer = context.Referer;
                return (await client.GetStringAsync(context.Url));
            }
        }

        #endregion

    }
}