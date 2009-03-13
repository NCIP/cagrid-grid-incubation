xquery version "1.0";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
(: Using eXist-predefined namespace: xmldb :)


import module 
    namespace lib-util="http://www.cancergrid.org/xquery/library/util" 
    at "../library/m-lib-util.xquery";
    
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";     

import module 
    namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
    at "../web/m-lib-rendering.xquery";
    
import module namespace
    lib-uri-resolution="http://www.cancergrid.org/xquery/library/resolver"
    at "../resolver/m-lib-uri-resolution.xquery";      

declare function local:content($administered_item) as element(div)
{
   
    <div xmlns="http://www.w3.org/1999/xhtml">{
      (if ($administered_item//cgMDR:reference_uri)
      then (
          <div class="section">
          <table class="section">
             <tr><td colspan="2"><div class="admin_item_section_header">Object Class Specific Attributes</div></td></tr>
             {
             for $reference_uris in $administered_item//cgMDR:reference_uri
             let $reference_uri := data($reference_uris//cgMDR:reference_uri)
             return
                 <tr><td  class="left_header_cell">Reference URI</td><td>{lib-uri-resolution:html-anchor($reference_uri)}</td></tr>
             }                       
          </table>
          </div>

      )
      else (
            <div class="section">
                 <table class="section">
                    <tr><td colspan="2"><div class="admin_item_section_header">Object Class Specific Attributes</div></td></tr>
                    <tr><td  colspan="2">reference URI or relationships undefined</td></tr>
                 </table>
            </div>
      ),
      lib-rendering:render_administered_item($administered_item))

   }
   </div>
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
   let $compound_id := request:get-parameter("compound_id", "")
   let $administered_item := lib-util:mdrElement("object_class",$compound_id)
   let $title:=concat('Object class: ',administered-item:preferred-name($administered_item))
   return
      if (request:get-parameter("as-xml",()))
      then (lib-rendering:as-xml($administered_item))
      else(lib-rendering:txfrm-webpage($title, local:content($administered_item), true(), true(), $compound_id, 'object_class.xquery'))
