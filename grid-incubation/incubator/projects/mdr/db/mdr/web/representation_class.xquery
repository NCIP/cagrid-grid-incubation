xquery version "1.0";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";
(: Using eXist-predefined namespaces: request, util, xmldb :)


import module namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
  at "../web/m-lib-rendering.xquery";

import module namespace lib-util="http://www.cancergrid.org/xquery/library/util"
  at "../library/m-lib-util.xquery";

import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery"; 
   
   
declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   

session:create(),
let $id := request:get-parameter("compound_id", "")

for $administered_item in lib-util:mdrElement("representation_class", $id)
    let $administered_item_name := administered-item:preferred-name($administered_item)
    let $title:=concat('Representation Class: ', $administered_item_name)
    let $content := <div xmlns="http://www.w3.org/1999/xhtml"> {lib-rendering:render_administered_item($administered_item)}</div>

    return
        if   (request:get-parameter("as-xml",()))
        then (lib-rendering:as-xml($administered_item))
        else (lib-rendering:txfrm-webpage($title, $content, false(), false(), $id, 'representation_class.xquery'))
