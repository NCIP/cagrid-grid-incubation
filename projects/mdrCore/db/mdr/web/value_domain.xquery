xquery version "1.0";

(: ~
 : Module Name:             Value domain webpage
 :
 : Module Version           3.0
 :
 : Date                     27th September 2006
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          Renders both types of value domain for viewing by the user of the metadata repository
 :
 :)
 
(:~
 :    
 :    @author Steve Harris
 :    @version 1.0
 :
 :    @author Rakesh Dhaval
 :    @version 3.0
~ :)

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";

import module namespace 
   lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";
   
import module namespace
  value-meaning="http://www.cagrid.org/xquery/library/value-meaning"
  at "../library/m-value-meaning.xquery";
  
  import module namespace
  value-domain="http://www.cagrid.org/xquery/library/value-domain"
  at "../library/m-value-domain.xquery";
  
import module namespace 
   administered-item="http://www.cagrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   

import module namespace
  permissible-value="http://www.cagrid.org/xquery/library/permissible-value"
  at "../library/m-permissible-value.xquery";

import module namespace
  value="http://www.cagrid.org/xquery/library/value"
  at "../library/m-value.xquery";


declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace util="http://exist-db.org/xquery/util";
(: Using eXist-predefined namespace: xmldb :)


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),

let $compound_id := request:get-parameter("compound_id", "")
let $administered_item := lib-util:mdrElement("value_domain", $compound_id)
     
   (:
   let $log := util:log-system-err($administered_item)
   let $log := util:log-system-err(permissible-value:contained_in($administered_item))
   let $log := util:log-system-err(permissible_value/openMDR:contained_in/text())/openMDR:value_meaning_description :)
return
    if (request:get-parameter("as-xml",()))
    then (lib-rendering:as-xml($administered_item))
   else(
      let $administered_item_name := administered-item:preferred-name($administered_item)
      let $representation_class_id := xs:string($administered_item//openMDR:typed_by)
      let $conceptual_domain_id := $administered_item//openMDR:representing/text()
      let $conceptual_domain := lib-util:mdrElement("conceptual_domain", $conceptual_domain_id)
      let $values := $administered_item//openMDR:value_item
      let $permissible_value_begin_date := $administered_item//openMDR:permissible_value_begin_date     
      let $title:=concat('Value Domain: ',$administered_item_name)
   
      let $content := <div xmlns="http://www.w3.org/1999/xhtml">  {
            lib-rendering:render_administered_item($administered_item),
            if (lower-case(local-name($administered_item))='non_enumerated_value_domain') then
            ()
            else
            (
              <div class="section">
                 <table class="section">
                    <tr><td colspan="2"><div class="admin_item_section_header">Permissible values</div></td></tr>

                    <tr><td colspan="2">
                    {
                         <table class="section">
                            <tr>
                               <td class="left_header_cell"><div class="admin_item_table_header">permissible value</div></td>
                               <td><div class="admin_item_table_header">value meaning</div></td>
                               <td><div class="admin_item_table_header">begin date</div></td>
                            </tr>
                            {
                            
                               if ($conceptual_domain//openMDR:value_meaning_description > '') 
                               then 
                               (                                       
                                    for $meaning at $pos in $conceptual_domain//openMDR:value_meaning_description
                                    return (
                                       <tr>
                                          <td class="left_header_cell">{$values[$pos]}</td>
                                          <td >{$meaning}</td>
                                          <td >{$permissible_value_begin_date[$pos]/text()}</td>
                                       </tr>
                                    ) 
                               ) 
                               else () 
                            
                            }
                        </table>
                    }
                    </td></tr>
                 </table>
              </div>
            ),         
   
         lib-rendering:render_value_domain_common_properties($administered_item),
         lib-rendering:related-administered-items($administered_item),                
         lib-rendering:value_domain_used_in_data_elements($compound_id)
      }</div>
      return 
         lib-rendering:txfrm-webpage($title, $content, true(),  true(), $compound_id, 'value_domain.xquery','../edit/editValueDomain.xquery')
   )