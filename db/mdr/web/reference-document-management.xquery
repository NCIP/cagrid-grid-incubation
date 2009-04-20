xquery version "1.0";

(: ~
 : Module Name:             Reference Document Management
 :
 : Module Version           1.0
 :
 : Date                     12th Feb 2008
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          maintains reference documents
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

import module 
   namespace lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
  
import module namespace 
  lib-util="http://www.cagrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),
let $letter := request:get-parameter("letter", ())
let $use-stylesheet := request:get-parameter("use-stylesheet","")
let $user := request:get-session-attribute("username")

return
  lib-rendering:txfrm-webpage("Reference Documents",
   <reference-documents>  
      {
      for $item in lib-util:mdrElements('reference_document')
      let $name:= $item/openMDR:reference_document_title
      let $id := $item/@reference_document_identifier
      let $anchor := <a xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en" href='../web/reference_document.xquery?compound_id={$id}'>{$name}</a>
      where starts-with(lower-case($name),$letter)
      order by $name
      return
         element reference-document {
               attribute id {$id},
               element anchor {$anchor},
               element name {$name}, $item
         }
      }
   </reference-documents>
   )