using System;
using Palmtree.Applications;

namespace TestApp
{
    internal enum FF14RecipeResourceType
    {
        ClassList = 1,
        RecipeList = 2,
    };

    internal class FF14RecipeResourceContext
        : WebSiteResourceContext
    {
        #region コンストラクタ

        public FF14RecipeResourceContext(Uri url, Uri referer, FF14RecipeResourceType resource_type)
            : base(url, referer)
        {
            ResourceType = resource_type;
        }

        #endregion

        #region パブリックプロパティ

        public FF14RecipeResourceType ResourceType { get; private set; }

        #endregion
    }
}