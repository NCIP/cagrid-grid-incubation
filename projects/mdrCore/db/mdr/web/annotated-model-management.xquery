xquery version "1.0";

(: ~
 : Module Name:             Annotated Model Management
 :
 : Module Version           1.0
 :
 : Author                   Sreekant Lalkota
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          maintains annotated models
 :
 :)
 
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace util="http://exist-db.org/xquery/util";

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
  lib-rendering:txfrm-webpage("Annotated Models",
   <annotated-models>  
      {
      for $item in lib-util:mdrElements('models')
      let $name:= $item/openMDR:annotated_model_project_long_name
      let $version:= $item/@version
      let $id := concat($item/@item_registration_authority_identifier,'_',$item/@data_identifier,'_',$item/@version)
      let $anchor := <a xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en" href='../web/annotated_model.xquery?compound_id={$id}'>{$name}</a>
      where starts-with(lower-case($name),$letter)
      order by $name
      return
         element annotated-model {
               attribute id {$id},
               attribute version {$version},
               element anchor {$anchor},
               element name {$name}, $item
         }
      }
   </annotated-models>
   )