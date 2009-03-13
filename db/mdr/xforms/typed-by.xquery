declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery"; 
  
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   
   
declare option exist:serialize "media-type=application/xml method=xml";
  
element typed-by {
   for $admin-item in lib-util:mdrElements('representation_class')
      let $name:= administered-item:preferred-name($admin-item)
      let $id:= lib-util:mdrElementId($admin-item)
      return 
         <item>
            <label>{$name}</label>
            <value>{$id}</value>
         </item>
         
      }
