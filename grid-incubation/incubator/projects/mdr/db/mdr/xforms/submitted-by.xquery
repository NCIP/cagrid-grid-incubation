declare namespace xforms="http://www.w3.org/2002/xforms";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   

declare option exist:serialize "media-type=application/xml method=xml";

<submitted-by>
{
   for $item in lib-util:mdrElements('organization')
         let $name:= data($item//cgMDR:organization_name)
         return
            for $contact in $item//cgMDR:Contact
               let $value := data($contact//@contact_identifier)
               let $label := concat($contact/cgMDR:contact_name, ' at ',$name)
                  return 
                     <item>
                        <label>{$label}</label>
                        <value>{$value}</value>
                     </item>
}
</submitted-by>

