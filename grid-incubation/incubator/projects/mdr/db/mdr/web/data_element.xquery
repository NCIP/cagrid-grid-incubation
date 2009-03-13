xquery version "1.0";

(: ~
 : Module Name:             Data element  webpage
 :
 : Module Version           3.0
 :
 : Date                     1st September 2006
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Renders a Data element for viewing by the user of the metadata repository
 :
 :)
 
(:~
 :    Commented on refactoring SJH
 :
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

(: Using eXist-predefined namespace: xmldb :)


import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";

import module namespace
   value-domain="http://www.cancergrid.org/xquery/library/value-domain"
   at "../library/m-value-domain.xquery";

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";

import module namespace 
   classification-scheme-item="http://www.cancergrid.org/xquery/library/classification-scheme-item"
   at "../library/m-classification-scheme-item.xquery";
  
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $compound_id := request:get-parameter("compound_id", "")
let $administered_item := lib-util:mdrElement("data_element",$compound_id)
return

      if (request:get-parameter("as-xml",()))
      then (lib-rendering:as-xml($administered_item))
   else(
   
   
   
      let $data_element_concept_id := data($administered_item//cgMDR:expressing)
      let $representation_class_id := data($administered_item//cgMDR:typed_by)
      let $representation_class_qualifier := $administered_item//cgMDR:representation_class_qualifier
      let $value_domain_id :=  data($administered_item//cgMDR:representing)
      let $value_domain := value-domain:representing($administered_item)
      
      let $title:=concat('Data Element: ', administered-item:preferred-name($administered_item))
      let $content as element(div):= (
            <div xmlns="http://www.w3.org/1999/xhtml">  {
               lib-rendering:render_administered_item($administered_item),
      
               <div class="content_one_pane">
               <table class="section">
      
                     <tr><td colspan="2"><div class="admin_item_section_header">Value Domain</div></td></tr>
                     <tr><td>Type</td>
                     <td>{value-domain:type($value_domain)}</td></tr>
      
                     <tr><td class="left_header_cell">Full Definition</td>
                     <td>{administered-item:html-anchor('value_domain',$value_domain_id)}</td></tr>
                     
                     <tr><td colspan="2"><div class="admin_item_section_header">Conceptual Framework</div></td></tr>
                     <tr>
                       <td>Expresses data element concept</td>
                       <td>{administered-item:html-anchor("data_element_concept",$data_element_concept_id)}</td>
                     </tr>
      
                     <tr>
                       <td>Typed by representation class</td>
                       <td>{administered-item:html-anchor("representation_class",$representation_class_id)}</td>
                     </tr>
      
                     <tr>
                        <td>Representation class qualifier</td>
                        <td>{lib-rendering:qualifier($representation_class_qualifier)}</td>
                     </tr>
                     </table>
                     
                       <div class="section">
                       <table class="section">
                       <tr><td colspan="2"><div class="admin_item_section_header">Data Element Specific Attributes</div></td></tr>
                       <tr><td  class="left_header_cell">Data Element Precision</td><td>{data($administered_item//cgMDR:data_element_precision)}</td></tr>
                       <tr><td  class="left_header_cell">Derived Data Elements</td><td>

                       {if ($administered_item//cgMDR:input_to) then
                       (
                       <table class="section">
                       <tr>    
                       <td><div class="admin_item_table_header">Administered Item ID</div></td>
                       <td><div class="admin_item_table_header">Preferred Name</div></td>
                       <td><div class="admin_item_table_header">Rule</div></td>
                       </tr>
                       {
                       for $input_to in $administered_item//cgMDR:input_to
                       let $derived_id := string($input_to//@deriving)
                       let $spec := data($input_to//cgMDR:derivation_rule_specification)
                       return 
                       <tr>
                         <td>{$derived_id}</td>
                         <td>{administered-item:html-anchor("data_element",$derived_id)}</td>
                         <td>{$spec}</td>
                       </tr>
                       }
                       </table>
                       )
                       else
                       (

                       <div>No derived data elements have been defined</div>
                       )
                       }

                       </td></tr>
                       </table>
                       </div>
                     
                     <div class="section">
                     <table class="section">
                     
                     <tr><td  class="left_header_cell">Examples</td><td>
                    {if ($administered_item//cgMDR:data_element_example_item) then
                     (
                         for $example in $administered_item//cgMDR:data_element_example_item
                         return data($example)
                      )
                      else
                      (
                         <div>No examples of this data element have been defined</div>
                     )
                     }
                     </td>
                     </tr>
                  </table>
                  </div>
                  
                  
                  
                  <div class="section">
                    <table class="section">
                     <tr><td colspan="3"><div class="admin_item_section_header">Classification</div></td></tr>
                     {
                     for $classified_by in $administered_item//cgMDR:classified_by
                     let $classification_document := classification-scheme-item:resolved-instance($classified_by, 'true')
                     order by $classification_document//scheme_name, $classification_document//name
                     return 
                     <tr><td>{$classification_document//anchor/*}</td><td>{data($classification_document//name)}</td></tr>
                     }
                    </table>              
                  </div>
                   </div>
                   
                   }</div>
                   )
                   
             
        return lib-rendering:txfrm-webpage($title, $content, true(), true(), $compound_id, 'data_element.xquery')
      )
      
      
      

