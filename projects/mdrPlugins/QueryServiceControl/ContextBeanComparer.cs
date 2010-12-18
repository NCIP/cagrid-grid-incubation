using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QueryServiceControl
{
    class ContextBeanComparer : IComparer<ContextBean>
    {
        public int Compare(ContextBean bean1, ContextBean bean2)
        {
            return bean1.Name.CompareTo(bean2.Name);
        }
    }
}
