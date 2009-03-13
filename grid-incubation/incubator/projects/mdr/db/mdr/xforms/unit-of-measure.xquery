declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery"; 
   
declare option exist:serialize "media-type=application/xml method=xml";

element unit-of-measure {
   for $item in lib-util:mdrElements('unit_of_measure')
         let $label := concat($item/cgMDR:unit_of_measure_name, ' (', $item/cgMDR:unit_of_measure_precision, ')')
         let $value := data($item/@unit_of_measure_identifier)
         return
               <item>
                  <label>{$label}</label>
                  <value>{$value}</value>
               </item>
         }

