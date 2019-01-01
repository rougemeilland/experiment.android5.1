using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System;
using System.Collections.Generic;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using Palmtree;
using Palmtree.Applications;

namespace TestApp
{
    internal class FF14RecipeWalker
        : WebSiteWalker
    {
        #region MaterialInfo の定義

        private class MaterialInfo
            : IEquatable<MaterialInfo>
        {
            #region パブリックメソッド

            public object SerializeToObject(bool full)
            {
                var m_dic = new Dictionary<string, object>();
                m_dic["ItemName"] = ItemName;
                if (full)
                    m_dic["IsCrystal"] = IsCrystal;
                m_dic["ItemCount"] = ItemCount;
                return (m_dic);
            }

            #endregion

            #region パブリックプロパティ

            public string ItemName { get; set; }
            public bool IsCrystal { get; set; }
            public int ItemCount { get; set; }

            #endregion

            #region object から継承されたメンバ

            public override bool Equals(object o)
            {
                if (o == null || GetType() != o.GetType())
                    return (false);
                return (Equals((MaterialInfo)o));
            }

            public override int GetHashCode()
            {
                return (ItemName.GetHashCode() ^ IsCrystal.GetHashCode() ^ ItemCount.GetHashCode());
            }

            public bool Equals(MaterialInfo o)
            {
                if (o == null || GetType() != o.GetType())
                    return (false);
                if (!ItemName.Equals(o.ItemName))
                    return (false);
                if (!IsCrystal.Equals(o.IsCrystal))
                    return (false);
                if (!ItemCount.Equals(o.ItemCount))
                    return (false);
                return (true);
            }

            #endregion
        }

        #endregion

        #region MaterialInfoEqualityComparer の定義

        private class MaterialInfoEqualityComparer
            : IEqualityComparer<MaterialInfo>
        {
            #region プライベートフィールド

            private bool _full;

            #endregion

            #region コンストラクタ

            public MaterialInfoEqualityComparer(bool full)
            {
                _full = full;
            }

            #endregion

            #region IEqualityComparer<MaterialInfo> のメンバ

            public bool Equals(MaterialInfo x, MaterialInfo y)
            {
                if (x == null)
                    return (y == null);
                else if (y == null)
                    return (false);
                else
                {
                    if (!x.ItemName.Equals(y.ItemName))
                        return (false);
                    if (!x.ItemCount.Equals(y.ItemCount))
                        return (false);
                    if (_full)
                    {
                        if (!x.IsCrystal.Equals(y.IsCrystal))
                            return (false);
                    }
                    return (true);
                }
            }

            public int GetHashCode(MaterialInfo o)
            {
                var code = o.ItemName.GetHashCode() ^ o.ItemCount.GetHashCode();
                if (_full)
                    code ^= o.IsCrystal.GetHashCode();
                return (code);
            }

            #endregion
        }

        #endregion

        #region ClassID の定義

        private enum ClassID
        {
            木工師 = 0,
            鍛冶師 = 1,
            甲冑師 = 2,
            彫金師 = 3,
            革細工師 = 4,
            裁縫師 = 5,
            錬金術師 = 6,
            調理師 = 7,
        }

        #endregion

        #region RecipeInfo の定義

        private class RecipeInfo
            : IEquatable<RecipeInfo>
        {
            #region パブリックプロパティ

            public string Lang { get; set; }
            public ClassID ClassID { get; set; }
            public string RecipeItemName { get; set; }
            public int RecipeItemCount { get; set; }
            public MaterialInfo[] Materials { get; set; }

            #endregion

            #region パブリックメソッド

            public object SerializeToObject(bool full)
            {
                var dic = new Dictionary<string, object>();
                if (full)
                    dic["Lang"] = Lang;
                dic["ClassID"] = (int)ClassID;
                dic["RecipeItemName"] = RecipeItemName;
                dic["RecipeItemCount"] = RecipeItemCount;
                dic["Materials"] = Materials.Where(material => material.IsCrystal == false).Select(material => material.SerializeToObject(full)).ToArray();
                return (dic);
            }

            #endregion

            #region object から継承されたメンバ

            public override bool Equals(object o)
            {
                if (o == null || GetType() != o.GetType())
                    return (false);
                return (Equals((RecipeInfo)o));
            }

            public override int GetHashCode()
            {
                var code = Lang.GetHashCode() ^ ClassID.GetHashCode() ^ RecipeItemName.GetHashCode() ^ RecipeItemCount.GetHashCode();
                foreach (var material in Materials)
                    code ^= material.GetHashCode();
                return (code);
            }

            public bool Equals(RecipeInfo o)
            {
                if (o == null || GetType() != o.GetType())
                    return (false);
                if (!Lang.Equals(o.Lang))
                    return (false);
                if (!ClassID.Equals(o.ClassID))
                    return (false);
                if (!RecipeItemName.Equals(o.RecipeItemName))
                    return (false);
                if (!RecipeItemCount.Equals(o.RecipeItemCount))
                    return (false);
                if (!Materials.Length.Equals(o.Materials.Length))
                    return (false);
                if (!Materials.Zip(o.Materials, (m1, m2) => m1.Equals(m2)).All(item => item == true))
                    return (false);
                return (true);
            }

            #endregion

        }

        #endregion

        #region RecipeInfoEqualityComparer の定義

        private class RecipeInfoEqualityComparer
            : IEqualityComparer<RecipeInfo>
        {
            #region プライベートフィールド

            private bool _full;
            private IEqualityComparer<MaterialInfo> _material_comparer;

            #endregion

            #region コンストラクタ

            public RecipeInfoEqualityComparer(bool full)
            {
                _full = full;
                _material_comparer = new MaterialInfoEqualityComparer(full);
            }

            #endregion

            #region IEqualityComparer<RecipeInfo> のメンバ

            public bool Equals(RecipeInfo x, RecipeInfo y)
            {
                if (x == null)
                    return (y == null);
                else if (y == null)
                    return (false);
                else
                {
                    if (!x.RecipeItemName.Equals(y.RecipeItemName))
                        return (false);
                    if (!x.RecipeItemCount.Equals(y.RecipeItemCount))
                        return (false);
                    if (!x.Materials.Length.Equals(y.Materials.Length))
                        return (false);
                    if (!x.Materials.Zip(y.Materials, (m1, m2) => _material_comparer.Equals(m1, m2)).All(item => item == true))
                        return (false);
                    if (_full)
                    {
                        if (!x.Lang.Equals(y.Lang))
                            return (false);
                        if (!x.ClassID.Equals(y.ClassID))
                            return (false);
                    }
                    return (true);
                }
            }

            public int GetHashCode(RecipeInfo o)
            {
                var code = o.RecipeItemName.GetHashCode() ^ o.RecipeItemCount.GetHashCode();
                foreach (var material in o.Materials)
                    code ^= material.GetHashCode();
                if (_full)
                {
                    code ^= o.Lang.GetHashCode();
                    code ^= o.ClassID.GetHashCode();
                }
                return (code);
            }

            #endregion

        }

        #endregion

        #region プライベートフィールド

        private const string _host_name_pattern_text = "(?<lang>na|eu|jp|fr|de?)\\.finalfantasyxiv\\.com";
        private const string _class_page_url_pattern_text = "(http://(?<lang>na|eu|jp|fr|de?)\\.finalfantasyxiv\\.com)?/lodestone/playguide/db/recipe/\\?category2=(?<class_id>[0-9]+?)(&amp;page=[0-9]+)?";
        private const string _recipe_page_url_pattern_text = "(http://(?<lang>na|eu|jp|fr|de?)\\.finalfantasyxiv\\.com)?/lodestone/playguide/db/recipe/(?<global_recipe_id>[0-9A-Fa-f]+?)/";
        private const string _item_page_url_pattern_text = "(http://(?<lang>na|eu|jp|fr|de?)\\.finalfantasyxiv\\.com)?/lodestone/playguide/db/item/(?<global_item_id>[0-9A-Fa-f]+?)/";
        private static Regex _host_name_pattern = new Regex(_host_name_pattern_text, RegexOptions.Compiled);
        private static Regex _class_page_url_pattern = new Regex(_class_page_url_pattern_text, RegexOptions.Compiled);
        private static Regex _recipe_page_url_pattern = new Regex(_recipe_page_url_pattern_text, RegexOptions.Compiled);
        private static Regex _item_page_url_pattern = new Regex(_item_page_url_pattern_text, RegexOptions.Compiled);
        private static Regex _class_page_link_pattern = new Regex("href=\"(?<url>" + _class_page_url_pattern_text + "?)\"", RegexOptions.Compiled);
        private static Regex _recipe_page_link_pattern = new Regex("href=\"(?<url>" + _recipe_page_url_pattern_text + "?)\"", RegexOptions.Compiled);
        private static Regex _recipe_class_name_pattern = new Regex("<p class=\"db-view__item__text__job_name\">(?<class_name>[^<]+?)</p>", RegexOptions.Compiled);
        private static Regex _recipe_producted_item_name_pattern = new Regex("<h2 class=\"db-view__item__text__name\\s+txt-rarity_[a-z]+\">(?<producted_item_name>[^<]+?)</h2>", RegexOptions.Compiled);
        private static Regex _recipe_producted_item_count_pattern = new Regex("<li><span>(完成個数|Total Crafted|Total fabriqué|Anzahl)</span>&nbsp;(?<producted_item_count>[^<]+?)</li>", RegexOptions.Compiled);
        private static Regex _recipe_material_item_pattern = new Regex("<div class=\"db-view__data__reward__item__name\">\\s*<span class=\"db-view__item_num\">(?<material_count>[^<]+?)</span>\\s*<div class=\"db-view__data__reward__item__name__wrapper\">\\s*<a href=\"(?<material_url>[^\"]*?)\" class=\"db_popup\"><strong>(?<material_name>[^<]+?)</strong></a>\\s*</div>\\s*</div>", RegexOptions.Compiled);
        private static Dictionary<string, string> _crystal_global_item_ids = new[]
        {
            "27623d06a42", // ファイアシャード
            "f2dfd367f1e", // アイスシャード
            "843899bc8f6", // ウィンドシャード
            "ef4394eb49b", // アースシャード
            "6fed6129996", // ライトニングシャード
            "9f967ad6b5a", // ウォーターシャード
            "75403105d7f", // ファイアクリスタル
            "2d800a06851", // アイスクリスタル
            "5db117cd77b", // ウィンドクリスタル
            "ff6eae7bdcb", // アースクリスタル
            "b21a951916e", // ライトニングクリスタル
            "44d3f4cb26b", // ウォータークリスタル
            "e8ccb4b1f81", // ファイアクラスター
            "4dbe0ec0a76", // アイスクラスター
            "b61cbfd2d6e", // ウィンドクラスター
            "c97c6825890", // アースクラスター
            "06061fd121b", // ライトニングクラスター
            "e3d82488f80", // ウォータークラスター
        }.ToDictionary(item => item, item => item);
        private static ICollection<RecipeInfo> _recipes = new List<RecipeInfo>();

        #endregion

        protected override IEnumerable<WebSiteResourceContext> ParseContent(WebSiteResourceContext context, string document)
        {
            if (context is FF14RecipeResourceContext)
                return (ParseContentImp((FF14RecipeResourceContext)context, document));
            else
                throw new ApplicationException();
        }

        private IEnumerable<FF14RecipeResourceContext> ParseContentImp(FF14RecipeResourceContext context, string document)
        {
            switch (context.ResourceType)
            {
                case FF14RecipeResourceType.ClassList:
                    return (ParseClassContent(context, document));
                case FF14RecipeResourceType.RecipeList:
                    return (ParseRecipeContent(context, document));
                default:
                    throw new ApplicationException();
            }
        }

        private IEnumerable<FF14RecipeResourceContext> ParseClassContent(FF14RecipeResourceContext context, string document)
        {
            Match m;
            if (!(m = _recipe_class_name_pattern.Match(document)).Success)
                throw new ApplicationException();
            var class_name = WebUtility.HtmlDecode(m.Groups["class_name"].Value).Trim();
            if (!(m = _recipe_producted_item_name_pattern.Match(document)).Success)
                throw new ApplicationException();
            var producted_item_name = WebUtility.HtmlDecode(m.Groups["producted_item_name"].Value).Trim();
            if (!(m = _recipe_producted_item_count_pattern.Match(document)).Success)
                throw new ApplicationException();
            var producted_item_count = int.Parse(WebUtility.HtmlDecode(m.Groups["producted_item_count"].Value).Trim());
            var materials = _recipe_material_item_pattern.Matches(document)
                            .Cast<Match>()
                            .Select(item =>
                            {
                                var material_url = new Uri(base_url, WebUtility.HtmlDecode(item.Groups["material_url"].Value.Trim()));
                                var global_material_item_id = WebUtility.HtmlDecode(_item_page_url_pattern.Match(material_url.AbsoluteUri).Groups["global_item_id"].Value).Trim();
                                return (new MaterialInfo
                                {
                                    ItemName = WebUtility.HtmlDecode(item.Groups["material_name"].Value.Trim()),
                                    IsCrystal = _crystal_global_item_ids.ContainsKey(global_material_item_id),
                                    ItemCount = int.Parse(WebUtility.HtmlDecode(item.Groups["material_count"].Value.Trim())),
                                });
                            })
                            .ToArray();
            var recipe = new RecipeInfo
            {
                Lang = lang,
                ClassID = class_id,
                RecipeItemName = producted_item_name,
                RecipeItemCount = producted_item_count,
                Materials = materials,
            };
            lock (_recipes)
            {
                _recipes.Add(recipe);
                //Console.Write("*");
            }
        }


        private IEnumerable<FF14RecipeResourceContext> ParseRecipeContent(FF14RecipeResourceContext context, string document)
        {
        }


        private string get_lang(Uri url)
        {
            switch (WebUtility.HtmlDecode(_recipe_page_url_pattern.Match(url.AbsoluteUri).Groups["lang"].Value).Trim())
            {
                case "eu":
                case "na":
                    lang = "en";
                    break;
                case "fr":
                    lang = "fr";
                    break;
                case "de":
                    lang = "de";
                    break;
                case "jp":
                    lang = "ja";
                    break;
                default:
                    throw new ApplicationException();
            }
        }

        static async Task WalkRecipePage(Uri recipe_url, Uri referer, ClassID class_id, Action progress)
        {
            if (!recipe_url.IsAbsoluteUri)
                throw new ArgumentException();
            string lang;
            switch (WebUtility.HtmlDecode(_recipe_page_url_pattern.Match(recipe_url.AbsoluteUri).Groups["lang"].Value).Trim())
            {
                case "eu":
                case "na":
                    lang = "en";
                    break;
                case "fr":
                    lang = "fr";
                    break;
                case "de":
                    lang = "de";
                    break;
                case "jp":
                    lang = "ja";
                    break;
                default:
                    throw new ApplicationException();
            }
            var html = await ReadHtml(recipe_url, referer);
            ++_page_count;
            ParseRecipePage(recipe_url, lang, class_id, html);
            progress();
        }

        static async Task WalkClassPage(Uri class_url, Uri referer, Action progress)
        {
            if (!class_url.IsAbsoluteUri)
                throw new ArgumentException();
            //var y = _class_page_url_pattern.Match(class_url.AbsoluteUri);
            //var x = y.Groups["class_id"].Value;
            var class_id = (ClassID)int.Parse(_class_page_url_pattern.Match(class_url.AbsoluteUri).Groups["class_id"].Value);
            var html = await ReadHtml(class_url, referer);
            ++_page_count;
            var child_class_tasks = _class_page_link_pattern.Matches(html)
                                    .Cast<Match>()
                                    .Select(m => new Uri(class_url, WebUtility.HtmlDecode(m.Groups["url"].Value)))
                                    .Where(url => _url_flags.TestAndSet(url))
                                    .Select(url => Task.Run(() => WalkClassPage(url, class_url, progress)))
                                    .ToArray();
            var recipe_tasks = _recipe_page_link_pattern.Matches(html)
                               .Cast<Match>()
                               .Select(m => new Uri(class_url, WebUtility.HtmlDecode(m.Groups["url"].Value)))
                               .Where(url => _url_flags.TestAndSet(url))
                               .Select(url => Task.Run(() => WalkRecipePage(url, class_url, class_id, progress)))
                               .ToArray();
            _total_page_count += child_class_tasks.Length + recipe_tasks.Length;
            html = null;
            progress();
            foreach (var task in child_class_tasks.Concat(recipe_tasks))
                await task;
        }

        public void Walk()
        {
        }



        static void Main(string[] args)
        {
            _url_flags.Clear();
            _recipes.Clear();
            var dir = new FileInfo(typeof(Program).Assembly.Location).Directory.Parent.Parent.Parent;
            var file = new FileInfo(Path.Combine(dir.FullName, "FF14.Applications.FFXIVAPPPlugin.SynthesisAnalyzer", "recipes.json"));
            var file_c = new FileInfo(Path.Combine(dir.FullName, "FF14.Applications.FFXIVAPPPlugin.SynthesisAnalyzer", "recipes.json.gz"));
            using (var stream = file.Create())
            using (var stream_c = file_c.Create())
            using (var stream_gz = new GZipStream(stream_c, CompressionMode.Compress, true))
            using (var writer = new StreamWriter(stream))
            using (var writer_c = new StreamWriter(stream_gz))
            {
                var time_reading_start = DateTime.Now;
                Console.Write("読み込み: 0%\r");
                WalkClassPage(new Uri("http://jp.finalfantasyxiv.com/lodestone/playguide/db/recipe/?category2=0"), null, () =>
                {
                    var percent = (double)_page_count / _total_page_count;
                    var 経過時間 = DateTime.Now - time_reading_start;
                    var 残り時間 = 経過時間.Multiply((1.0 - percent) / percent);
                    var 完了時刻 = DateTime.Now + 残り時間;
                    Console.Write(string.Format("読み込み: {0:P0}, 経過時間: {1:F0}分, 残り時間: {2:F0}分, 完了予定時刻: {3:HH:mm:ss} {4}    \r", percent, 経過時間.TotalMinutes, 残り時間.TotalMinutes, 完了時刻, "**********..........".Substring(10 - _page_count % 10, 10)));
                }).Wait();
                Console.WriteLine(string.Format("読み込み: 完了, 所要時間: {0:F0}分                                                           ", (DateTime.Now - time_reading_start).TotalMinutes));
                var source = _recipes
                             .Distinct(new RecipeInfoEqualityComparer(false))
                             .GroupBy(recipe => recipe.RecipeItemName)
                             .Select(g => new { RecipeItemName = g.Key, Recipes = g.OrderBy(item => item.ClassID).ThenBy(item => item.RecipeItemCount).ThenBy(item => item.Lang).ToArray() })
                             .OrderBy(item => item.RecipeItemName)
                             .Select(item =>
                             {
                                 var dic = new Dictionary<string, object>();
                                 dic["RecipeItemName"] = item.RecipeItemName;
                                 dic["Recipes"] = item.Recipes.Select(recipe => recipe.SerializeToObject(false)).ToArray();
                                 return (dic);
                             })
                            .ToArray();
                var _writing_count = 0;
                var time_writing_start = DateTime.Now;
                Console.Write("書き込み: 0%\r");
                foreach (var o in source)
                {
                    var line = SimpleJson.Serialize(o);
                    writer.WriteLine(line);
                    writer_c.WriteLine(line);
                    ++_writing_count;
                    var percent = (double)_writing_count / source.Length;
                    var 経過時間 = DateTime.Now - time_writing_start;
                    var 残り時間 = 経過時間.Multiply((1.0 - percent) / percent);
                    var 完了時刻 = DateTime.Now + 残り時間;
                    Console.Write(string.Format("書き込み: {0:P0}, 経過時間:{1:F0}分, 残り時間:{2:F0}分, 完了予定時刻:{3:HH:mm:ss} {4}     \r", percent, 経過時間.TotalMinutes, 残り時間.TotalMinutes, 完了時刻, "**********..........".Substring(10 - _writing_count % 10, 10)));
                    //Console.Write("*");
                }
                Console.WriteLine(string.Format("書き込み: 完了, 所要時間: {0:F0}分                                                           ", (DateTime.Now - time_writing_start).TotalMinutes));
            }
            Console.WriteLine("Ok.");
            Console.ReadLine();
        }
    }
}