declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery"; 
   
declare option exist:serialize "media-type=application/xml method=xml";

element datatype
   {
   for $item in lib-util:mdrElements('data_type')
         let $label := concat($item/cgMDR:datatype_name, ' (', $item/cgMDR:datatype_scheme_reference, ')')
         let $value := data($item/@datatype_identifier)
         return
               <item>
                  <label>{$label}</label>
                  <value>{$value}</value>
               </item>
   }