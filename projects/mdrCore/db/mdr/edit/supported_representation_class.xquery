xquery version "1.0";

(: ~
 : Module Name:             Representation Class Summary
 : Module Version           1.0
 : Date                     5th December 2006
 : Copyright               The cancergrid consortium
 :
 : Module overview          Displays supported representation classes
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
  
import module namespace 
   administered-item="http://www.cagrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery"; 
   
import module namespace 
   lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";
   
   
   declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   
session:create(),
lib-rendering:txfrm-webpage("Representation Class Listing",
   element representation-classes {
      for $item in lib-util:mdrElements('representation_class')
      let $id := lib-util:mdrElementId($item)
      let $anchor := administered-item:html-anchor('representation_class', $id)
      let $description := $item//openMDR:containing[openMDR:preferred_designation=true()]/openMDR:definition_text/text()
      order by $anchor
      return 
         element representation-class
         {
            element id {$id},
            element anchor {$anchor},
            element description {$description}
         }
      })
