xquery version "1.0";

(: ~
 : Module Name:             Reference Document Management
 :
 : Module Version           1.0
 :
 : Date                     12th Feb 2008
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          maintains reference documents
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

import module 
   namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
  
import module namespace 
  lib-util="http://www.cancergrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),
let $letter := request:get-parameter("letter", "")
let $use-stylesheet := request:get-parameter("use-stylesheet","")
let $user := request:get-session-attribute("username")
let $letter := request:get-parameter("letter", "")
let $regexp := concat("^(", $letter, ").*")
return
   lib-rendering:txfrm-webpage("Reference Documents", 
   <reference-documents>  
      {
      for $item in lib-util:mdrElements('reference_document')
      let $name:= $item/cgMDR:reference_document_title/text()
      let $id := 
          if (exists($item//cgMDR:reference_document_identifier))
          then (substring-before($item//cgMDR:reference_document_identifier/text(),'.'))
          else (string($item/@reference_document_identifier))
      let $anchor := <a xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en" href='../web/reference_document.xquery?compound_id={$id}'>{$name}</a>
      where matches($name, $regexp, 'i')
      order by $name
      return
         element reference-document {
               attribute id {$id},
               element anchor {$anchor},
               element name {$name}, $item
         }
      }
   </reference-documents>)