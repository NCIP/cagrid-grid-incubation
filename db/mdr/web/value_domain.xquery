xquery version "1.0";

(: ~
 : Module Name:             Value domain webpage
 :
 : Module Version           3.0
 :
 : Date                     27th September 2006
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Renders both types of value domain for viewing by the user of the metadata repository
 :
 :)
 
(:~
 :    
 :    @author Steve Harris
 :    @version 1.0
~ :)

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";
   
import module namespace
  value-meaning="http://www.cancergrid.org/xquery/library/value-meaning"
  at "../library/m-value-meaning.xquery";
  
  import module namespace
  value-domain="http://www.cancergrid.org/xquery/library/value-domain"
  at "../library/m-value-domain.xquery";
  
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   

import module namespace
  permissible-value="http://www.cancergrid.org/xquery/library/permissible-value"
  at "../library/m-permissible-value.xquery";

import module namespace
  value="http://www.cancergrid.org/xquery/library/value"
  at "../library/m-value.xquery";


declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
(: Using eXist-predefined namespace: xmldb :)


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),

let $compound_id := request:get-parameter("compound_id", "")

let $administered_item := lib-util:mdrElement("value_domain", $compound_id)

return
    if (request:get-parameter("as-xml",()))
    then (lib-rendering:as-xml($administered_item))
   else(
      let $administered_item_name := administered-item:preferred-name($administered_item)
      let $representation_class_id := xs:string($administered_item//cgMDR:typed_by)
      let $conceptual_domain_id := $administered_item//cgMDR:representing/text()
      
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
                               for $permissible_value  in permissible-value:contained_in($administered_item)
                                  let $value := value:used_in($permissible_value)
                                  order by	$value
                                  return
                                     <tr>
                                           <td class="left_header_cell">
                                             {if ($value) then value:item($value) else ()}
                                           </td>

                                           <td>
                                             {
                                               string-join(
                                                   for $meaning in value-meaning:value-meaning($permissible_value/cgMDR:contained_in/text())/cgMDR:value_meaning_description
                                                   order by $meaning ascending
                                                   return $meaning, (: The sorting gives deterministic behaviour that is good for regression testing :)
                                                   " | "
                                               ),
                                               string-join(
                                                   for $meaning in string(value-meaning:value-meaning(string($permissible_value/cgMDR:contained_in))/cgMDR:value_meaning_description)
                                                   order by $meaning ascending
                                                   return $meaning, (: The sorting gives deterministic behaviour that is good for regression testing :)
                                                   " | "
                                               )
                                             }
                                           </td>
                                           <td>
                                           {permissible-value:begin_date($permissible_value)}
                                           </td>
                                     </tr>
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
         lib-rendering:txfrm-webpage($title, $content, true(),  true(), $compound_id, 'value_domain.xquery')
   )