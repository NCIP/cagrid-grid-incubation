xquery version "1.0";

(: ~
 : Module Name:             Conceptual Domain
 :
 : Module Version           .0
 :
 : Date                     15th September 2006
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Renders a conceptual domain for viewing by the user of the metadata repository
 :
 :)
 
(:~
 :    Commented on refactoring SJH
 :    12th Feb 2008: remodelled for new xslt rendering
 :    15th September 2006: Added session handling code 
 :    @author Steve Harris
 :    @author Igor Toujilov
 :    @author Sui (Maria) Lin
 :    @since Sept 1st, 2006
 :    @version 3.0
~ :)


declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";


import module 
    cgResolver = "http://www.cancergrid.org/schema/cgResolver"; 
    at "../resolver/m-lib-uri-resolution.xquery";
    
session:create(),
    
let $compound_id as xs:string := request:get-parameter("compound_id", "")
let $type as xs:string := request:get-parameter("type", "")
for $administered_item in lib-util:mdrElement($type,$compound_id)
let $path:= concat("xmldb:exist://" ,lib-util:webPath(), "stylesheets/lib-rendering.xsl")

return
   transform:transform(
      $administered_item,
      $path,      
      element parameters {
         element param {
            attribute name {'user'},
            attribute value {if($user) then($user) else('guest')}
            }
      }
   )
   