xquery version "1.0";

(: ~
 : Module Name:             Property Summary
 :
 : Module Version           1.0
 :
 : Date                               26th October 2006
 :
 : Copyright                       The cancergrid consortium
 :
 : Module overview         Displays supported representation classes
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    
   
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
let $use-stylesheet := request:get-parameter("use-stylesheet","")

let $user := request:get-session-attribute("username")
return

lib-rendering:txfrm-webpage("Supported Properties", 
   element properties {
      let $regexp := concat("^(", $letter, ").*")
      
      for $item in lib-util:mdrElements('property')
         let $id := data(lib-util:mdrElementId($item))
         let $name:= administered-item:preferred-name($item)
         let $anchor := 
             <a xmlns="http://www.w3.org/1999/xhtml" href='../web/property.xquery?compound_id={$id}'>
                 {$name}
             </a>
         let $description := data($item//cgMDR:containing[cgMDR:preferred_designation=true()]/cgMDR:definition_text)
         where matches($name, $regexp, 'i') and $item//cgMDR:registration_status ne 'Superseded' 
         order by $anchor
         return
         element property {
            attribute id {$id},
            element anchor {$anchor},
            element description {$description}
         }
   })