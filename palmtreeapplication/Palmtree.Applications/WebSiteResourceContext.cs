using System;

namespace Palmtree.Applications
{
    public class WebSiteResourceContext
        : IEquatable<WebSiteResourceContext>
    {
        #region コンストラクタ

        public WebSiteResourceContext(Uri url, Uri referer)
        {
            Url = url;
            Referer = referer;
        }

        #endregion

        #region パブリックプロパティ

        public Uri Url { get; private set; }
        public Uri Referer { get; private set; }

        #endregion


        #region object から継承されたメンバ

        public override bool Equals(object o)
        {
            if (o == null || GetType() != o.GetType())
                return (false);
            return (Equals((WebSiteResourceContext)o));
        }

        public override int GetHashCode()
        {
            // コンテンツの等価性にRefererは無関係のため、Urlのみを評価する
            return (Url != null ? Url.GetHashCode() : 0);
        }

        #endregion

        #region IEquatable<WebSiteResourceContext> のメンバ

        public virtual bool Equals(WebSiteResourceContext o)
        {
            if (o == null || GetType() != o.GetType())
                return (false);

            // コンテンツの等価性にRefererは無関係のため、Urlのみを比較する
            if (!string.Equals(this.Url?.AbsoluteUri, o.Url?.AbsoluteUri))
                return (false);

            return (true);
        }

        #endregion
    }
}