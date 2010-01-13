using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QueryServiceControl
{
    class ContextBean
    {
        private String name = "";
        private String version = "";
        private String description = "";

        public ContextBean() { 
        }
        
        public ContextBean(QueryServiceManager.context ctx) {
            this.name = ctx.name;
            this.version = ctx.version;
            this.description = ctx.description;
        }

        public String Name
        {
            get
            {
                return name;
            }
        }

        public String Version
        {
            get
            {
                return version;
            }
        }

        public String Description
        {
            get
            {
                return description;
            }
        }

        public String Tooltip
        {
            get
            {
                return Description + ", Version " + Version;
            }
        }
    }
}
