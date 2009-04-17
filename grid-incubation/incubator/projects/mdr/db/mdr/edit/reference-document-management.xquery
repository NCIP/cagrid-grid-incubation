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


declare option exist:serialize "media-type=application/xml method=xml";

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
      let $name:= $item/openMDR:reference_document_title
      let $id := $item//@reference_document_identifier
      let $anchor := <a href='../web/reference_document.xquery?compound_id={$id}'>{$name}</a>
      where matches($name, $regexp, 'i')
      order by $name
      return
         element reference-document {
               attribute id {$id},
               element anchor {$anchor},
               element name {$name}
         }
      }
   </reference-documents>)