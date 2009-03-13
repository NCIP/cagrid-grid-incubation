xquery version "1.0";

declare namespace session="http://exist-db.org/xquery/session";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
  
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";
  
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   

declare namespace request="http://exist-db.org/xquery/request";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $compound_id := request:get-parameter("compound_id", "")

let $administered_item := lib-util:mdrElement("context", $compound_id)
return
  if (request:get-parameter("as-xml",()))
  then (lib-rendering:as-xml($administered_item))
   else
      (
      let $administered_item_name := administered-item:preferred-name($administered_item)
      let $title:=concat('Context: ', $administered_item_name)
      let $content := <div xmlns="http://www.w3.org/1999/xhtml">  {
         lib-rendering:render_administered_item($administered_item)
         }</div>
      return lib-rendering:txfrm-webpage($title, $content, true(),  true(),$compound_id, 'context.xquery')
      )

       
