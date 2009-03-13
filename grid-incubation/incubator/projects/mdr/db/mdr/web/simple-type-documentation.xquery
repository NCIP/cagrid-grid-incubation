declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery"; 

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
   at "../web/m-lib-rendering.xquery";
   
   
   declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   

session:create(),
let $as-xml := request:get-parameter("as-xml","false")
let $simple-type-name := request:get-parameter("type-name",())
let $schema := collection(lib-util:schemaPath())//xs:simpleType[@name=$simple-type-name]

let $title as xs:string := concat('Simple Type Documentation: ', $simple-type-name)
let $content := 
   if ($simple-type-name)
   then(
         <div xmlns="http://www.w3.org/1999/xhtml"> 
         <p>
         Definition: {data($schema/xs:annotation/xs:documentation)}
         </p>
         
         <table class="layout">
         <tr>
            <td><div class="admin_item_table_header">identifier</div></td><td><div class="admin_item_table_header">name</div></td><td><div class="admin_item_table_header">description</div></td>
         </tr>
{   
   
      for $enumeration in $schema//xs:enumeration
      let $id:= data($enumeration/@value)
      let $name:= data($enumeration/@value)
      let $documentation := data($enumeration/xs:annotation/xs:documentation)
      order by $name
      return 
         
            element tr {
               element td {
                  attribute class{"left_header_cell"},
                  $id},
               element td {$name},
               element td {$documentation}
               }
            }
            </table></div>
            )
   else('nothing')
   
return
    if (request:get-parameter("as-xml",()))
    then (lib-rendering:as-xml($content))
    else lib-rendering:txfrm-webpage($title,$content)

   

