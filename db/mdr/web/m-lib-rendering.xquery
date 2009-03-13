(:~ HTML lib-rendering :)
module namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";   
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace response="http://exist-db.org/xquery/response";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    

import module namespace 
   registrar="http://www.cancergrid.org/xquery/library/registrar"
   at "../library/m-registrar.xquery";

import module namespace 
   submitter="http://www.cancergrid.org/xquery/library/submitter"
   at "../library/m-submitter.xquery";  
  
import module namespace 
   administrator="http://www.cancergrid.org/xquery/library/administrator"
   at "../library/m-administrator.xquery";  
  
import module namespace 
  reference_document="http://www.cancergrid.org/xquery/library/reference-document"
  at "../library/m-reference-document.xquery";

import module namespace
  value-domain="http://www.cancergrid.org/xquery/library/value-domain"
  at "../library/m-value-domain.xquery";
  
(: refactored and in the current UI :)

declare function lib-rendering:odd-even($items) as element()*
{
    for $item at $row_id in $items
    let $row := 
        if (($row_id mod 2) = 0)
        then "odd_row"
        else "even_row"  
    return
        element {name($item)} {attribute class {$row}, $item/*}
};


declare function lib-rendering:admin-item($displayed-items) as element()
{
   element result-set {
       for $administered-item at $record-id in $displayed-items
       let $administered-item-id := lib-util:mdrElementId($administered-item)
       let $preferred-name := administered-item:preferred-name($administered-item)
       let $preferred-definition := administered-item:preferred-definition($administered-item)
       return element {lib-util:mdrElementType($administered-item)} {
             element names {
                 element id {$administered-item-id},
                 element preferred {$preferred-name},
                 element all-names {
                    for $name in $administered-item//cgMDR:name
                    where xs:string($name) != $preferred-name  
                    return element name {data($name)}
                    }   
                 },
                 element definition {administered-item:preferred-definition($administered-item)}
            }
        }
};

declare function lib-rendering:conceptual-domain-reduced($displayed-items) as element()
{
   element result-set {
     for $administered-item at $record-id in $displayed-items
     let $administered-item-id := lib-util:mdrElementId($administered-item)
     let $preferred-name := administered-item:preferred-name($administered-item)
     return
        element conceptual_domain {
              element names {
                 element id {$administered-item-id},
                 element preferred {$preferred-name},
                 element all-names {
                    for $name in $administered-item//cgMDR:name
                    where xs:string($name) != $preferred-name  
                    return element name {data($name)}
                    }   
                 },
                 element definition {administered-item:preferred-definition($administered-item)},
              element meanings {
                    for $meaning in $administered-item//cgMDR:Value_Meaning
                    order by $meaning//cgMDR:Value_Meaning
                    return
                          element meaning {xs:string($meaning/cgMDR:value_meaning_description)}
                    
              }
           }
    }
};



declare function lib-rendering:value-domain-reduced($displayed-items) as element()
{
   element result-set {
     for $administered-item at $record-id in $displayed-items
     let $administered-item-id := lib-util:mdrElementId($administered-item)
     let $data-type := lib-util:mdrElement("data_type", xs:string($administered-item//cgMDR:value_domain_datatype))
     let $uom := lib-util:mdrElement("unit_of_measure", xs:string($administered-item//cgMDR:value_domain_unit_of_measure))
     let $preferred-name := administered-item:preferred-name($administered-item)
     return
        element value_domain {
              element names {
                 element id {$administered-item-id},
                 element preferred {$preferred-name},
                 element all-names {
                    for $name in $administered-item//cgMDR:name
                    where xs:string($name) != $preferred-name  
                    return element name {data($name)}
                    }   
                 },
                 element definition {administered-item:preferred-definition($administered-item)},
              element values {
                 if (value-domain:type($administered-item) = 'enumerated value domain')
                 then(
                    let $cd := lib-util:mdrElement("conceptual_domain", $administered-item/cgMDR:representing)
                    for $value in $administered-item//cgMDR:containing
                    for $meaning in $cd//cgMDR:Value_Meaning
                    where $meaning/cgMDR:value_meaning_identifier = $value/cgMDR:contained_in
                    order by $value/cgMDR:value_item
                    return
                       element valid-value{
                          element code {xs:string($value/cgMDR:value_item)},
                          element meaning {xs:string($meaning/cgMDR:value_meaning_description)}
                       }
                    )
                 else(
                    element data-type {data($data-type//cgMDR:datatype_name)},
                    element units {if (data($uom//cgMDR:unit_of_measure_name)>"")
                                   then (data($uom//cgMDR:unit_of_measure_name))
                                   else ("(not applicable)")}
                    )
              }
           }
}
};

declare function lib-rendering:data-element-reduced($displayed-items) as element()
{
   element result-set {
     for $administered-item at $record-id in $displayed-items
     let $administered-item-id := lib-util:mdrElementId($administered-item)
     let $value-domain-id := xs:string($administered-item//cgMDR:representing)
     let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
     let $data-type := lib-util:mdrElement("data_type", xs:string($value-domain//cgMDR:value_domain_datatype[1]))
     let $uom := lib-util:mdrElement("unit_of_measure", xs:string($value-domain//cgMDR:value_domain_unit_of_measure[1]))
     let $preferred-name := administered-item:preferred-name($administered-item)
     return
        element data_element {
              attribute class {if (($record-id mod 2) = 0)
        then "odd_row"
        else "even_row"},
              element names {
                 element id {$administered-item-id},
                 element preferred {$preferred-name},
                 element all-names {
                    for $name in $administered-item//cgMDR:name
                    where xs:string($name) != $preferred-name  
                    return element name {data($name)}
                    }   
                 },
                 element definition {administered-item:preferred-definition($administered-item)},
              element values {
                 if (value-domain:type($value-domain) = 'enumerated value domain')
                 then(
                    let $cd := lib-util:mdrElement("conceptual_domain", $value-domain/cgMDR:representing)
                    for $value in $value-domain//cgMDR:containing
                    for $meaning in $cd//cgMDR:Value_Meaning
                    where $meaning/cgMDR:value_meaning_identifier = $value/cgMDR:contained_in
                    order by $value/cgMDR:value_item
                    return
                       element valid-value{
                          element code {xs:string($value/cgMDR:value_item)},
                          element meaning {xs:string($meaning/cgMDR:value_meaning_description)}
                       }
                    )
                 else(
                    element data-type {data($data-type//cgMDR:datatype_name)},
                    element units {if (data($uom//cgMDR:unit_of_measure_name)>"")
                                   then (data($uom//cgMDR:unit_of_measure_name))
                                   else ("(not applicable)")}
                    )
              }
           }
}

};








(: unrefactored - could be redundant :)
  
  
  
  
  
  

(: gathers together all administered item lib-rendering code :)

declare function lib-rendering:render_administered_item_essentials($administered_item as node())
{
let $mdrElementIdentifier := lib-util:mdrElementId($administered_item)

return
<div class="section">
    <table class="section">
        <tr><td class="left_header_cell">Identifier</td><td>{$mdrElementIdentifier}</td></tr>
        <tr><td class="left_header_cell">Registration Status</td><td>{$administered_item//cgMDR:registration_status/text()}</td></tr>
        <tr><td class="left_header_cell">Definition</td><td>{$administered_item//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:definition_text}</td></tr>
        <tr><td class="left_header_cell">Full Record</td><td>{administered-item:html-anchor("data_element",$mdrElementIdentifier)}</td></tr>
    </table>
    </div>
};

declare function lib-rendering:render_administered_item($administered_item as node()) as element(div)*
{

let $administered_item_name := administered-item:preferred-name($administered_item)
let $admin_record :=$administered_item//cgMDR:administered_item_administration_record
let $mdrElementIdentifier := lib-util:mdrElementId($administered_item)
let $registrar := lib-rendering:registrar(registrar:registering($administered_item))
let $submitter := lib-rendering:submitter(submitter:submitting($administered_item))
let $administrator := lib-rendering:administrator(administrator:administrating($administered_item)) 
let $type := lib-util:mdrElementTypeFriendly($administered_item)


return
   <div>
    <div class="section">
      <table class="section">
         <tr><td colspan="2"><h3>Administered Item - Preferred Name: {$administered_item_name}</h3></td></tr>
         <tr><td  class="left_header_cell">Administered Item Identifier</td><td>{$mdrElementIdentifier}</td></tr>
         <tr><td class="left_header_cell">Registration Status</td><td>{$admin_record/cgMDR:registration_status/text()}</td></tr>
         <tr><td class="left_header_cell">Registered By</td><td>{$registrar}</td></tr>               
         <tr><td class="left_header_cell"></td><td></td></tr>         
      </table>
    </div>
    <div class="section">
        <table class="section">
            <tr>
                <td colspan="2"><div class="admin_item_section_header">Designation</div></td>
            </tr>
        </table>
       {
           for $having in $administered_item//cgMDR:having
           let $context_id := data($having/cgMDR:context_identifier)
           return
           <table class="section">
               <tr>
                   <td  class="left_header_cell">Context</td><td>{administered-item:html-anchor("context", $context_id)}</td></tr>
               {
               for $containing in $having/cgMDR:containing
                   order by $containing//cgMDR:preferred_designation descending
                    return
                    (
                       <tr><td class="left_header_cell">Name</td><td>{
                           if ($containing//cgMDR:preferred_designation=true())
                           then concat($containing//cgMDR:name/text(),' (preferred designation)')
                           else $containing//cgMDR:name/text()
                           }
                           </td></tr>,
                       <tr><td class="left_header_cell">Definition</td><td>{$containing//cgMDR:definition_text/text()}</td></tr>,
                       <tr><td class="left_header_cell">Language</td><td>{lib-rendering:language($containing)}</td></tr>,
                       <tr><td class="left_header_cell">Definition Source Reference</td><td>{$containing//cgMDR:definition_source_reference/text()}</td></tr>,
                       <tr class="light-rule"><td class="left_header_cell"></td><td></td></tr>
                   )
               }
               <tr>
                   <td class="left_header_cell_last"></td>
                   <td></td>
               </tr>
           </table>
       }
    </div>
      
      <div class="section">
               <table class="section">
               <tr><td colspan="2" ><div class="admin_item_section_header">Administration</div></td></tr>
               <tr><td class="left_header_cell">Administrative Status</td><td>{$admin_record/cgMDR:administrative_status/text()}</td></tr>
               <tr><td class="left_header_cell">Administered By</td><td>{$administrator}</td></tr>
               <tr><td class="left_header_cell">Creation On</td><td>{$admin_record/cgMDR:creation_date/text()}</td></tr>
               <tr><td class="left_header_cell">Effective From</td><td>{$admin_record/cgMDR:effective_date/text()}</td></tr>
               <tr><td class="left_header_cell">Last Changed On</td><td>{$admin_record/cgMDR:last_change_date/text()}</td></tr>
               <tr><td class="left_header_cell">Effective until</td><td>{$admin_record/cgMDR:until_date/text()}</td></tr>
               <tr><td class="left_header_cell">Submitted By</td><td>{$submitter}</td></tr>
               <tr><td class="left_header_cell">Explanatory Comments</td><td>{$admin_record/cgMDR:explanatory_comment/text()}</td></tr>
               <tr><td class="left_header_cell"></td><td></td></tr>
               </table>
      </div>
         <div>
         {lib-rendering:reference_document_display($administered_item)}
         </div>
      </div>
};

(: function takes a qualifier - such as representation class, property and object class qualifier, and returns 'unqualified' if the field is empty:)
declare function lib-rendering:qualifier($qualifier as xs:string?) as xs:string
{
xs:string(
                      if($qualifier !="")
                      then $qualifier
                      else "unqualified"
                      )
};

declare function lib-rendering:unspecified($attribute as xs:string?) as xs:string
{
    xs:string(
                      if($attribute !="")
                      then $attribute
                      else "unspecified")
};

declare function lib-rendering:render_value_domain_common_properties($administered_item as node()) as element(div)
{
    let $representation_class_id := $administered_item//cgMDR:typed_by/text()
    let $conceptual_domain_id := $administered_item//cgMDR:representing/text()
    let $unit-of-measure := value-domain:unit_of_measure($administered_item)
    let $datatype := value-domain:datatype($administered_item)
    return
                <div class="section">
                    <table class="section">
                        <tr><td colspan="2"><div class="admin_item_section_header">Value Domain Specific Properties</div></td></tr>
                        <tr><td class="left_header_cell">Value Domain Type</td><td>{value-domain:type($administered_item)}</td></tr>

                        <tr>
                            <td class="left_header_cell">Typed by representation class</td>
                            <td>
                            {
                               if ($administered_item//cgMDR:typed_by) 
                               then administered-item:html-anchor("representation_class",$representation_class_id) 
                               else 'unspecified'}</td>
                        </tr>
                        <tr><td class="left_header_cell">Datatype</td><td>{$datatype}</td></tr>
                        <tr>
                            <td class="left_header_cell">Unit of Measure</td>
                            <td>{lib-rendering:unspecified( $unit-of-measure)}</td>
                        </tr>
                        <tr><td class="left_header_cell">Maximum Character Quantity</td><td>{lib-rendering:unspecified($administered_item/cgMDR:value_domain_maximum_character_quantity)}</td></tr>
                        <tr><td class="left_header_cell">Format</td><td>{lib-rendering:unspecified($administered_item/cgMDR:value_domain_format)}</td></tr>
                        <tr><td class="left_header_cell">Represents conceptual domain</td><td>{administered-item:html-anchor("conceptual_domain",$conceptual_domain_id)}</td></tr>
                 </table>
              </div>
            
};

declare function lib-rendering:value_domain_used_in_data_elements($value_domain_id) as element(div)
{
    <div class="section">
        <div class="admin_item_section_header">Represented by data elements</div>
        <table class="section">
            {lib-rendering:used_in_data_elements($value_domain_id)}
        </table>
    </div>

};

declare function lib-rendering:data_element_concept_expressed_in_data_elements($data_element_concept_id) as node()*
{
            <p>
                <div class="section">
                <div class="admin_item_section_header">Expressed by data elements</div>
                    <table class="sub-table">
                        {lib-rendering:used_in_data_elements($data_element_concept_id)}
                    </table>
            </div>
            </p>

};

declare function lib-rendering:used_in_data_elements($id) as node()*
{
    for $data_element in lib-util:mdrElements("data_element")
        (:let $data_element_name := administered-item:preferred-name($data_element):)
        let $data_element_id := lib-util:mdrElementId($data_element)
        let $registration_status := $data_element//cgMDR:registration_status/text()
        where $data_element//cgMDR:representing = $id or $data_element//cgMDR:expressing = $id 
        order by $data_element_id
        return
            <tr>
               <td class="left_header_cell">{$data_element_id}</td>
               <td>{administered-item:html-anchor("data_element",$data_element_id)}</td>
               <td>{$registration_status}</td>
            </tr>
};

declare function lib-rendering:related-administered-items($administered-item as element()) as node()*
{
   let $doc_type :=lib-util:mdrElementType($administered-item)
   let $type := lib-util:mdrElementTypeFriendly($doc_type)
   let $id := data(lib-util:mdrElementId($administered-item))
   return
   
      <div class="section">
         <div class="admin_item_section_header">Related {$type}s</div>              
         <table class="section">
            <tr>
               <td class="left_header_cell">Cited by this {$type}</td>
               <td>{
               if ($administered-item//cgMDR:related_to)
               then (
                  <table class="sub-table">
                     <tr><td><div class="admin_item_table_header">id</div></td><td><div class="admin_item_table_header">name</div></td><td><div class="admin_item_table_header">outgoing relation</div></td></tr>
                     {
                     for $related_domain in $administered-item//cgMDR:related_to[cgMDR:related_to]
                       let $related_domain_id := $related_domain/cgMDR:related_to/text()
                       let $how_related := if ($related_domain/cgMDR:related_to/following-sibling::* >"")
                                                           then ($related_domain/cgMDR:related_to/following-sibling::*/text())
                                                           else ($related_domain/cgMDR:related_to/preceding-sibling::*/text())
                       order by  $related_domain_id
                       return
                          <tr>
                                 <td>{$related_domain_id}</td><td>{administered-item:html-anchor($doc_type,$related_domain_id)}</td><td>{$how_related}</td>
                          </tr>
                     }
                  </table>
                  )
               else (<div>no outgoing relationships specified</div>)
               }
               </td>
            </tr>
            <tr>
               <td class="left_header_cell">Cited by other {$type}s</td>
               <td>
                    {
                        if (lib-util:mdrElements($doc_type)//cgMDR:related_to[cgMDR:related_to=$id])
                        then
                        (
                       <table class="sub-table">
                       <tr>
                          <td><div class="admin_item_table_header">id</div></td>
                          <td><div class="admin_item_table_header">name</div></td>
                          <td><div class="admin_item_table_header">incoming relation</div></td>
                       </tr>
                       {
                        for $related_domain in lib-util:mdrElements($doc_type)
                            let $related_domain_id := lib-util:mdrElementId($related_domain)
                            let $how_related := if ($related_domain//cgMDR:related_to[cgMDR:related_to=$id]/cgMDR:related_to/following-sibling::* >"")
                                                                 then ($related_domain//cgMDR:related_to[cgMDR:related_to=$id]/cgMDR:related_to/following-sibling::*/text())
                                                                 else ($related_domain//cgMDR:related_to[cgMDR:related_to=$id]/cgMDR:related_to/preceding-sibling::*/text())
                              where $related_domain//cgMDR:related_to/cgMDR:related_to=$id
                             order by  $related_domain_id
                            
                            return
                            <tr>
                            <td>{$related_domain_id}</td>
                            <td>{administered-item:html-anchor($doc_type,$related_domain_id)}</td>
                            <td>{$how_related}</td>
                            </tr>
                        }
                        </table>
                         )
                         else (<div>no incoming relationships detected</div>)
                    }
                </td></tr>
                </table>
                </div>
                
};



declare function lib-rendering:registrar($registrar as node()?) as node()
{
  <table class="invisible">
    <tr><td class="invisible">{string($registrar//cgMDR:contact_name/text())}</td></tr>
    <tr><td class="invisible">{string($registrar//cgMDR:contact_title/text())}</td></tr>
    <tr><td class="invisible">{string($registrar//cgMDR:contact_information/text())}</td></tr>
  </table>
};

declare function lib-rendering:submitter($submitter as node()?) as node()
{
      <table class="invisible">
      <tr><td class="invisible">{$submitter//cgMDR:contact_name/text()}</td></tr>
      <tr><td class="invisible">{$submitter//cgMDR:contact_title/text()}</td></tr>
      <tr><td class="invisible">{$submitter//cgMDR:contact_details/text()}</td></tr>
      </table>
};

declare function lib-rendering:administrator($administrator as node()?) as node()
{
      <table class="invisible">
      <tr><td class="invisible">{$administrator/cgMDR:contact_name/text()}</td></tr>
      <tr><td class="invisible">{$administrator/cgMDR:contact_title/text()}</td></tr>
      <tr><td class="invisible">{$administrator/cgMDR:contact_details/text()}</td></tr>
      </table>
};

declare function lib-rendering:language($containing as node()) as xdt:anyAtomicType
{
   let $a := concat(data($containing//cgMDR:country_identifier),"-",data($containing//cgMDR:language_identifier))
   return $a
};

declare function lib-rendering:txfrm-header($title as xs:string) as node()*
{
let $user:=session:get-attribute("username")
let $debug:=xs:boolean(request:get-parameter("debug","false"))
let $path:= concat("xmldb:exist://" ,lib-util:webPath(), "stylesheets/lib-rendering.xsl")
return
   transform:transform(
      <content/>, 
      $path,
      element parameters {
      element param {
         attribute name {'user'},
         attribute value {if($user) then($user) else('guest')}
         },
      element param {
         attribute name {'title'},
         attribute value {$title}
         },
      element param {
         attribute name {'footer'},
         attribute value {'false'}
      }
      }
     )
   
};


declare function lib-rendering:txfrm-webpage($title as xs:string, $content as node()*) as node()*
{
let $user:=session:get-attribute("username")
let $debug:=xs:boolean(request:get-parameter("as-xml","false"))
let $path:= concat("xmldb:exist://" ,lib-util:webPath(), "stylesheets/lib-rendering.xsl")
return
   if ($debug)
   then ($content)
   else (
   transform:transform(
      $content, 
      $path,
      element parameters {
      element param {
         attribute name {'user'},
         attribute value {if($user) then($user) else('guest')}
         },
      element param {
         attribute name {'title'},
         attribute value {$title}
         }
      }
     )
     )
   
};
declare function lib-rendering:txfrm-webpage(
   $title as xs:string, 
   $content as node()*, 
   $edit as xs:boolean, 
   $supersede as xs:boolean,
   $id as xs:string, 
   $as-xml-link as xs:string?) as node()*
{
let $user:=request:get-session-attribute("username")
let $debug:=xs:boolean(request:get-parameter("debug","false"))
let $parameters :=
   element parameters {
         element param {
         attribute name {'user'},
         attribute value {if($user) then($user) else('guest')}
         },
         element param {
            attribute name {'title'},
            attribute value {$title}
         },
         element param {
         attribute name {'show-edit-button'},
         attribute value {$edit}
         },
         element param {
         attribute name {'show-supersede-button'},
         attribute value {$supersede}
         },
         element param {
         attribute name {'id'},
         attribute value {$id}
         },        
         element param {
         attribute name {'as-xml-link'},
         attribute value {$as-xml-link}
         }}      

let $path:= concat("xmldb:exist://" ,lib-util:webPath(), "stylesheets/lib-rendering.xsl")
return
   if ($debug)
   then element debug {
         $content, 
         $path,
         $parameters     
         }
   else transform:transform(
         $content, 
         $path,
         $parameters
         )     
};

declare function lib-rendering:reference_document_display($administered_item as node()) as node()?
{
   if (data($administered_item//cgMDR:described_by)>"")  then
      (
         <div class="section">
         <table class="section">
            <tr><td colspan="4"><div class="admin_item_section_header">Reference Documents</div></td></tr>
            <tr>
               <td width="25%"><div class="admin_item_table_header">id</div></td>
               <td><div class="admin_item_table_header">language</div></td>
               <td><div class="admin_item_table_header">title</div></td>
               <td><div class="admin_item_table_header">type</div></td>
            
            </tr>
            {
            for $reference_document_id in $administered_item//cgMDR:described_by/text()
            let $doc := lib-util:mdrElement('reference_document', $reference_document_id)
            return 
            <tr>
               <td>
                  {if (starts-with($doc//cgMDR:reference_document_uri,"http://"))
                  then (<a href="{$doc//cgMDR:reference_document_uri}">{$reference_document_id}</a>)
                  else (<a href="{concat('http://163.1.125.47:8080/exist/rest//db/mdr/collections/reference_document/',$doc//cgMDR:reference_document_uri)}">{$reference_document_id}</a>)}
               </td>
               <td>{$doc//cgMDR:reference_document_language_identifier/text()}</td>
               <td>{$doc//cgMDR:reference_document_title/text()}</td>
               <td>{$doc//cgMDR:reference_document_type_description/text()}</td>
            </tr>
            }
         </table>
         </div>
      )
   else
      (               
      <div class="section">
      <table class="section">
      <tr><td colspan="4"><div class="admin_item_section_header">Reference Documents</div></td></tr>
      <tr>
      <td>No reference documents defined</td>
      </tr>
      </table>
      </div>
      )
};

(: adds an odd/even class attribute to an element in a sequence of elements :)

declare function lib-rendering:add-rowtype($rows as element()*) as element()*
{
      for $row at $row-id in $rows
      let $row-type := 
                  if (($row-id mod 2) = 0)
                  then "odd_row"
                  else "even_row"  
      return
      (: needs to be more general :)
         element {name($row)} {
            attribute class {$row-type},
            $row/*}
};


declare function lib-rendering:as-xml($content as node()) as node()
{
    let $h1 := response:set-header('Content-type','text/xml')
    return
        transform:stream-transform($content, concat("xmldb:exist://" ,lib-util:webPath(), "stylesheets/as-xml.xsl"), <parameters/>)
};