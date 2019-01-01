using System;

namespace Palmtree.Applications
{
    public class ProgressedEventArgs
        : EventArgs
    {
        #region コンストラクタ

        public ProgressedEventArgs(double percent)
        {
            Percent = percent;
        }

        #endregion

        #region パブリックプロパティ

        public double Percent { get; private set; }

        #endregion
    }
}