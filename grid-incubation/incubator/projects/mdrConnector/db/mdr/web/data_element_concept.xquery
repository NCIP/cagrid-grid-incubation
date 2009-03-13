xquery version "1.0";

(: ~
 : Module Name:             Data element concept webpage
 :
 : Module Version           3.1
 :
 : Date                            21st July 2006
 :
 : Copyright                    The cancergrid consortium
 :
 : Module overview         Renders a Data element concept for viewing by the
 :                                    user of the metadata repository
 :
 :)
 
(:~
 :    3.0 Commented on refactoring SJH
 :    3.1 Now supports standard relation mechanism
 :
 :    @author Steve Harris
 :    @author Igor Toujilov
 :    @author Sui (Maria) Lin
 :    @since July 20, 2006
 :    @version 3.0
~ :)


declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
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

declare variable $compound_id as xs:string external;


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),
let $compound_id := request:get-parameter("compound_id", "")
let $administered_item := lib-util:mdrElement("data_element_concept", $compound_id)

return
      if (request:get-parameter("as-xml",()))
      then (lib-rendering:as-xml($administered_item))
   else(

    let $administered_item_name := administered-item:preferred-name($administered_item)
    let $conceptual_domain:=$administered_item//cgMDR:data_element_concept_conceptual_domain
    let $object_class:=$administered_item//cgMDR:data_element_concept_object_class
    let $property:=$administered_item//cgMDR:data_element_concept_property
    let $object_class_qualifier:=$administered_item//cgMDR:object_class_qualifier
    let $property_qualifier:=$administered_item//cgMDR:property_qualifier
    let $title:=concat('Data Element Concept: ',$administered_item_name )
    
    let $content := 
       (
       <div xmlns="http://www.w3.org/1999/xhtml"> 
            <div class="section">
               <div class="admin_item_section_header">Data Element Concept Specific Attributes</div>                
               <table class="section">
               <tr><td class="left_header_cell">Conceptual Domain</td><td>{administered-item:html-anchor("conceptual_domain",$conceptual_domain)}</td></tr>
               <tr><td class="left_header_cell">Object Class</td><td>{administered-item:html-anchor("object_class",$object_class)}</td></tr>
               <tr><td class="left_header_cell">Object Class Qualifier</td><td>{lib-rendering:qualifier($object_class_qualifier)}</td></tr>
               <tr><td class="left_header_cell">Property</td><td>{administered-item:html-anchor("property",$property)}</td></tr>
               <tr><td class="left_header_cell">Property Qualifier</td><td>{lib-rendering:qualifier($property_qualifier)}</td></tr>
               </table>
            </div>
            {lib-rendering:render_administered_item($administered_item)} 
            {lib-rendering:related-administered-items($administered_item)}
            {lib-rendering:data_element_concept_expressed_in_data_elements($compound_id)}
       </div>
       )
    

    return lib-rendering:txfrm-webpage($title, $content, true(),  true(),$compound_id, 'data_element_concept.xquery')
    )