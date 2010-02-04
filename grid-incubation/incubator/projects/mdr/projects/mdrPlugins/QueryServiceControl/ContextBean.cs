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

        public ContextBean(String aName, String aVersion, String aDescription) {
            name = aName;
            version = aVersion;
            description = aDescription;
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
                String str = "";
                if (Description.Length > 0)
                {
                    str += Description;
                }

                if (Version.Length > 0)
                {
                    if (str.Length > 0)
                    {
                        str += ", ";
                    }
                    str += Version;
                }

                return str;
            }
        }
    }
}
