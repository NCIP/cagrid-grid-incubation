xquery version "1.0";

(: ~
 : Module Name:             Conceptual Domain
 :
 : Module Version           3.0
 :
 : Date                     15th September 2006
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Renders a conceptual domain for viewing by the user of the metadata repository
 :
 :)
 
(:~
 :    Commented on refactoring SJH
 :    15th September 2006: Added session handling code 
 :    @author Steve Harris
 :    @author Igor Toujilov
 :    @author Sui (Maria) Lin
 :    @since Sept 1st, 2006
 :    @version 3.0
~ :)

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace exist = "http://exist.sourceforge.net/NS/exist"; 

import module 
    namespace lib-util="http://www.cancergrid.org/xquery/library/util" 
    at "../library/m-lib-util.xquery";

import module 
    namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
    at "../web/m-lib-rendering.xquery"; 
    
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";
   
import module namespace
    lib-uri-resolution="http://www.cancergrid.org/xquery/library/resolver"
    at "../resolver/m-lib-uri-resolution.xquery";     

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
declare option exist:output-size-limit "20000";

session:create(),
    
let $compound_id := request:get-parameter("compound_id", "")

for $administered_item in lib-util:mdrElement("conceptual_domain",$compound_id)
    let $dimensionality := string($administered_item//cgMDR:dimensionality) 
    let $title:= concat('Conceptual Domain: ', administered-item:preferred-name($administered_item))
    return
      if (request:get-parameter("as-xml",()))
      then (lib-rendering:as-xml($administered_item))
      else(
    
      let $content as element(div) := 
         <div xmlns="http://www.w3.org/1999/xhtml"> {
             lib-rendering:render_administered_item($administered_item),
             (
                 <!--conceptual domain specific properties-->,
                 <div class="section">
                    <table class="section">
                    <tr><td colspan="2"><div class="admin_item_section_header">Conceptual Domain Specific Properties</div></td></tr>
                    <tr>
                        <td class="left_header_cell">Type</td>
                        <td>
                        {
                        if (lower-case(local-name($administered_item)) = "enumerated_conceptual_domain")
                        then (<div>enumerated conceptual domain</div>)
                        else (<div>non enumerated conceptual domain</div>)
                        }
                        </td>
                    </tr>
                    <tr><td class="left_header_cell">Dimensionality</td><td>{$dimensionality}</td></tr>
                    {
                    if ($administered_item//cgMDR:Value_Meaning)
                    then 
                    (
                    <tr>
                    <td class="left_header_cell">Value Meanings</td>
                    <td>
                    <div class="section">
                    <table class="sub-table">
                       <tr>
                          <td><div class="admin_item_table_header">meaning text</div></td>
                          <td><div class="admin_item_table_header">begin date</div></td>
                          <td><div class="admin_item_table_header">end date</div></td>
                          <td><div class="admin_item_table_header">reference</div></td>
                       </tr>
                       {
                       for $value_meaning in $administered_item//cgMDR:Value_Meaning
                       let $meaning := $value_meaning//cgMDR:value_meaning_description/text()
                       let $begins := $value_meaning//cgMDR:value_meaning_begin_date/text()
                       let $ends := $value_meaning//cgMDR:value_meaning_end_date/text()
                       let $urn as xs:string? := xs:string($value_meaning//cgMDR:reference_uri/text())
                       return 
                       <tr>
                           <td>{$meaning}</td>
                           <td>{$begins}</td>
                           <td>{$ends}</td>
                           <td>{
                               if (empty($urn))
                               then ('not specified')
                               else (lib-uri-resolution:html-anchor($urn))
                               }</td>
                       </tr>
                       }
                    </table>
                 </div>
      
                 </td>
                 </tr>
                 )
                 else()
                 }

                 </table>
                 
                 </div>
              ),
                    lib-rendering:related-administered-items($administered_item)
         }</div>
      
         return lib-rendering:txfrm-webpage($title, $content, true(), true(), $compound_id, 'conceptual_domain.xquery')
         )