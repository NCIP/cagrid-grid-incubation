xquery version "1.0";

(: ~
 : Module Name:             Orgtanisation Summary
 :
 : Module Version           1.0
 :
 : Date                     24th April 2008
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Displays organisations and links to organisation editing pages
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module 
   namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";   
    
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $letter := request:get-parameter("letter", "")
let $use-stylesheet as xs:boolean := xs:boolean(request:get-parameter("use-stylesheet","true"))
let $user := request:get-session-attribute("username")
let $regexp := concat("^(", $letter, ").*")

let $content :=    
      element organisations {
         for $item in lib-util:mdrElements('organization')
            let $compound_id := data(lib-util:mdrElementId($item))
            let $name:= string($item//cgMDR:organization_name)
            let $anchor := <a xmlns="http://www.w3.org/1999/xhtml" href='../web/organization.xquery?compound_id={$compound_id}'>{$name}</a>
            let $description := string($item//cgMDR:organization_mail_address)
            where matches($name, $regexp, 'i') 
            order by $name
            return
            element organization {
               attribute id {$compound_id},
               element anchor {$anchor},
               element description {$description}
            }}
return
   if ($use-stylesheet=true())
   then lib-rendering:txfrm-webpage("Organisations", $content)
   else $content