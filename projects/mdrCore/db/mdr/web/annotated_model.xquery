xquery version "1.0";

(: ~
 : Module Name:             reference document webpage
 :
 : Module Version           1.0
 :
 : Author                   Sreekant Lalkota
 :
 : Date                     12th
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Renders a Data element for viewing by the user of the metadata repository
 :
 :)
 
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace util="http://exist-db.org/xquery/util";

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";

import module namespace 
   lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $id := request:get-parameter("compound_id", "")

let $item := lib-util:mdrElement("models", $id)
let $title := concat('Annotated Model: ', $item/openMDR:annotated_model_project_long_name)
return
   if (request:get-parameter("as-xml",()))
   then (lib-rendering:as-xml($item))
   else (
      if (request:get-parameter("return_doc", ())='true')
      then (
              let $h1 := response:set-header("Content-Disposition", concat('attachment; filename="', $item//openMDR:file_name, '"'))
              return
                 response:stream-binary($item//openMDR:annotated_model_uri, $item//openMDR:file_type)
           )
      else 
         lib-rendering:txfrm-webpage(
            $title, 
            $item, 
            true(), 
            false(), 
            $id, 
            'annotated_model.xquery','../edit/editAnnotatedModel.xquery'))
