xquery version "1.0";

(: ~
 : Module Name:             new data element concept XQuery
 :
 : Module Version           1.0
 :
 : Date                     17th September 2007
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Creates a DEC and optionally an object class and property
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

  
  import module namespace 
  lib-forms="http://www.cancergrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
  import module namespace 
  administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
  at "../library/m-administered-item.xquery";  
  
  import module namespace 
  lib-util="http://www.cancergrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";
  
  import module namespace 
  lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
  at "../web/m-lib-rendering.xquery";   
    
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

declare variable $local:default-version as xs:string := "1.0";

declare function local:admin-item-identifier(
   $registration-authority as xs:string, 
   $data-identifier as xs:string)
{
   concat($registration-authority, '-', $data-identifier, '-', $local:default-version)
};

declare function local:find-concept-id(
         $control-name as xs:string, 
         $form-name as xs:string,
         $return-value-to as xs:string,
         $button-label as xs:string
         ) as element()*
{
      lib-forms:input-element($control-name, 70, request:get-parameter($control-name,'')),
      <input type="submit" 
         class="cgButton" 
         value="{$button-label}"
         onclick="document.{$form-name}.action='LexBIG-form.xquery?destination-page={$return-value-to}&amp;return-parameter={$control-name}'"/>   
};

declare function local:find-admin-item($control-name as xs:string, 
         $form-name as xs:string,
         $return-value-to as xs:string,
         $type as xs:string,
         $button-label as xs:string
         ) as element()*
{
      lib-forms:input-element($control-name, 35, request:get-parameter($control-name,'')),
      <input type="submit" class="cgButton" value="{$button-label}"
      onclick="document.{$form-name}.action='getRelationshipTarget.xquery?destination-page={$return-value-to}&amp;return-parameter={$control-name}&amp;type={$type}'"/>   
};

declare function local:identifier-attributes (
   $data-identifier as xs:string,
   $registration-authority as xs:string) as attribute()*
{
   attribute item_registration_authority_identifier {$registration-authority},
   attribute data_identifier {$data-identifier},
   attribute version {$local:default-version}
};

declare function local:administration-record (
   $registered-by as xs:string,
   $administered-by as xs:string,
   $submitted-by as xs:string,
   $administrative-status as xs:string,
   $administrative-note as xs:string,
   $registration-status as xs:string
   ) as element()*
{
   element cgMDR:administered_item_administration_record {
      element cgMDR:administrative_note {$administrative-note},
      element cgMDR:administrative_status {$administrative-status}, 
      element cgMDR:creation_date {current-date()},
      element cgMDR:effective_date {current-date()}, 
      element cgMDR:last_change_date {current-date()},
      element cgMDR:registration_status {$registration-status}
      },
   element cgMDR:administered_by {$administered-by}, 
   element cgMDR:registered_by {$registered-by},
   element cgMDR:submitted_by {$submitted-by}

};


declare function local:form($message as xs:string?) as element(table)
{

   <table class="layout" xmlns="http://www.w3.org/1999/xhtml">
         <tr><td class="left_header_cell">Registration Authority</td><td colspan="2"> {lib-forms:make-select-registration-authority(request:get-parameter('registration-authority',''))} </td></tr>
         <tr><td class="left_header_cell">Registered by</td><td colspan="2"> {lib-forms:make-select-registered_by(request:get-parameter('registered-by',''))} </td></tr>
         <tr><td class="left_header_cell">Administered by</td><td colspan="2"> {lib-forms:make-select-administered_by(request:get-parameter('administered-by',''))} </td></tr>
         <tr><td class="left_header_cell">Submitted by</td><td colspan="2"> {lib-forms:make-select-submitted_by(request:get-parameter('submitted-by',''))} </td></tr>
         <tr><td class="left_header_cell">Administrative Status</td><td colspan="2">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(),request:get-parameter('administrative-status',''))}</td></tr>
         <tr><td class="left_header_cell">Registration Status</td><td colspan="2">{lib-forms:select-from-simpleType-enum('Registration_Status','registration-status', false(),request:get-parameter('registration-status',''))}</td></tr>
         <tr><td class="left_header_cell">Administrative Note</td><td colspan="2">{lib-forms:text-area-element('administrative-note', 5, 70, request:get-parameter('administrative-note',''))}</td></tr>
         <tr><td class="left_header_cell">Preferred Name</td><td colspan="2">{lib-forms:input-element('name', 70, request:get-parameter('name',''))}</td></tr>
         <tr><td class="left_header_cell">Preferred Definition</td><td colspan="2">{lib-forms:text-area-element('definition', 5, 70, request:get-parameter('definition',''))}</td></tr>
         <tr><td class="left_header_cell">Context of preferred name</td><td colspan="5"> {lib-forms:make-select-admin-item('context','preferred-name-context',request:get-parameter('preferred-name-context',''))} </td></tr>
         <tr>
            <td class="left_header_cell">Language Identifier for preferred name</td><td colspan="2">
               {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifier', false(), request:get-parameter('country-identifier',''))}
               {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifier', false(),request:get-parameter('language-identifier',''))}
             </td>
         </tr>
         
         <tr>
            <td class="left_header_cell">Object Class</td>
            <td>
               {
               local:find-concept-id('object_class_uri','edit_admin_item','newDataElementConcept.xquery','get object class concept'),
               lib-forms:input-element('oc_name', 70, request:get-parameter('oc_name','')),
               lib-forms:text-area-element('oc_definition', 5, 70, request:get-parameter('oc_definition',''))
               }
            </td>
            </tr>
            <tr>
            <td></td>
            <td colspan="2"><i> or select existing </i> 
               {local:find-admin-item('object_class_id','edit_admin_item','newDataElementConcept.xquery','object_class', 'find object class')}
            </td>
         </tr>
         <tr>
            <td class="left_header_cell">Property</td>
            <td>
               {
               local:find-concept-id('property_uri','edit_admin_item','newDataElementConcept.xquery','get property concept'),
               lib-forms:input-element('prop_name', 70, request:get-parameter('prop_name','')),
               lib-forms:text-area-element('prop_definition', 5, 70, request:get-parameter('prop_definition',''))
               }
            </td>
        </tr>
        <tr>
            <td  class="left_header_cell"><i> or select existing </i></td>
            <td colspan="2">
                {local:find-admin-item('property_id','edit_admin_item','newDataElementConcept.xquery','property', 'find property')}
            </td>
        </tr>

         <tr>
            <td class="left_header_cell">Conceptual Domain</td>
            <td colspan="2"> 
               {local:find-admin-item('conceptual_domain_id','edit_admin_item','newDataElementConcept.xquery','conceptual_domain', 'find conceptual domain')}
            </td>
         </tr>


      <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Store"  class="cgButton" /></td><td colspan="4"><input type="submit" name="update" value="Clear"  class="cgButton" /></td></tr>    
      <tr><td>{$message}</td></tr>
   </table>
};

declare function local:object-class(
   $registered-by as xs:string,
   $administered-by as xs:string,
   $submitted-by as xs:string,
   $administrative-status as xs:string,
   $administrative-note as xs:string,
   $registration-status as xs:string,
   $data-identifier as xs:string,
   $country-identifier as xs:string,
   $language-identifier as xs:string,
   $registration-authority as xs:string,
   $context-id as xs:string,
   $oc_name as xs:string,
   $oc_definition as xs:string,
   $object_class_uri as xs:string
   ) as xs:boolean
{
   let $document :=
      element cgMDR:Concept_Relationship {
         local:identifier-attributes (
            $data-identifier,
            $registration-authority),      
         local:administration-record (
            $registered-by,
            $administered-by,
            $submitted-by,
            $administrative-status,
            $administrative-note,
            $registration-status
            ),
            
         element cgMDR:having {
            element cgMDR:context_identifier {$context-id},
            element cgMDR:containing {
               element cgMDR:language_section_language_identifier {
                   element cgMDR:country_identifier {$country-identifier}, 
                   element cgMDR:language_identifier {$language-identifier}
                   },
               element cgMDR:name {$oc_name},
               element cgMDR:definition_text {$oc_definition},
               element cgMDR:preferred_designation {'true'}
               }
            },
         element cgMDR:reference_uri {$object_class_uri}
         }   
         
   let $collection := 'object_class'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;",$message)))

};

declare function local:property(
   $registered-by as xs:string,
   $administered-by as xs:string,
   $submitted-by as xs:string,
   $administrative-status as xs:string,
   $administrative-note as xs:string,
   $registration-status as xs:string,
   $data-identifier as xs:string,
   $country-identifier as xs:string,
   $language-identifier as xs:string,
   $registration-authority as xs:string,
   $context-id as xs:string,
   $prop_name as xs:string,
   $prop_definition as xs:string,
   $property_uri as xs:string
   ) as xs:boolean
{
   let $document := 
      element cgMDR:Property {
         local:identifier-attributes (
            $data-identifier,
            $registration-authority),
            
         local:administration-record (
            $registered-by,
            $administered-by,
            $submitted-by,
            $administrative-status,
            $administrative-note,
            $registration-status
            ),
   
         element cgMDR:having {
               element cgMDR:context_identifier {$context-id},
               element cgMDR:containing {
                  element cgMDR:language_section_language_identifier {
                      element cgMDR:country_identifier {$country-identifier}, 
                      element cgMDR:language_identifier {$language-identifier}
                      },
                  element cgMDR:name {$prop_name},
                  element cgMDR:definition_text {$prop_definition},
                  element cgMDR:preferred_designation {'true'}
                  }
               },
         element cgMDR:reference_uri {$property_uri}
         }   
      let $collection := 'property'
      let $message := lib-forms:store-document($document) 
      return
         if ($message='stored')
         then true()
         else response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;",$message)))

};

declare function local:data-element-concept(
   $registered-by as xs:string,
   $administered-by as xs:string,
   $submitted-by as xs:string,
   $administrative-status as xs:string,
   $administrative-note as xs:string,
   $registration-status as xs:string,
   $name as xs:string,
   $definition as xs:string,
   $country-identifier as xs:string,
   $language-identifier as xs:string,
   $data-identifier as xs:string,
   $registration-authority as xs:string,
   $context-id as xs:string,
   $conceptual-domain-id as xs:string,
   $object-class-id as xs:string,
   $property-id as xs:string
   ) (:as xs:boolean:) as element()
{
   let $document := 
      element cgMDR:Data_Element_Concept {
         local:identifier-attributes (
            $data-identifier,
            $registration-authority),
            
         local:administration-record (
            $registered-by,
            $administered-by,
            $submitted-by,
            $administrative-status,
            $administrative-note,
            $registration-status
            ),
            
         element cgMDR:having {
            element cgMDR:context_identifier {$context-id},
            element cgMDR:containing {
               element cgMDR:language_section_language_identifier {
                   element cgMDR:country_identifier {$country-identifier}, 
                   element cgMDR:language_identifier {$language-identifier}
                   },
               element cgMDR:name {$name},
               element cgMDR:definition_text {$definition},
               element cgMDR:preferred_designation {'true'}
               }
            },
         element cgMDR:having_conceptual_domain {$conceptual-domain-id},
         element cgMDR:data_element_concept_object_class {$object-class-id},
         element cgMDR:data_element_concept_property {$property-id}
         }

   (:
   let $collection := 'data_element_concept'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;",$message)))
   :)
   return $document
};


declare function local:success-page($oc-id as xs:string, $prop-id as xs:string, $dec-id as xs:string) 
{
   let $calling-page := request:get-parameter("calling-page","")
   return
      <div>
         <p>Data Element Concept created<br/>
         <p>Data Element Concept {administered-item:html-anchor('data_element_concept',$dec-id)}</p>
         <p>Object Class {administered-item:html-anchor('object_class',$oc-id)}</p>
         <p>Property {administered-item:html-anchor('property',$prop-id)}</p></p>
         <p><a href="../xquery/newDataElementConcept.xquery">Create another data element concept</a></p>
         <p><a href="../xquery/maintenance.xquery">Return to maintenance menu</a></p>
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),

   let $title as xs:string := "Creating a New Data Element Concept"
   
   let $dec-data-id := lib-forms:generate-id()
   let $oc-data-id := lib-forms:generate-id()
   let $prop-data-id := lib-forms:generate-id()
   
   let $registration-authority :=request:get-parameter('registration-authority','')
   let $registered-by := request:get-parameter('registered-by','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $registration-status := request:get-parameter('registration-status','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $name := request:get-parameter('name','')
   let $definition := request:get-parameter('definition','')
   let $context := request:get-parameter('preferred-name-context','')
   let $country-identifier := request:get-parameter('country-identifier','')
   let $language-identifier := request:get-parameter('language-identifier','')
   let $object-class-uri := request:get-parameter('object_class_uri','') 
   let $property-uri := request:get-parameter('property_uri','')
   let $object-class-id := request:get-parameter('object_class_id','') 
   let $property-id := request:get-parameter('property_id','')
   let $conceptual-domain-id := request:get-parameter('conceptual_domain_id','')
   
   let $context := request:get-parameter('preferred-name-context','')
   let $action := request:get-parameter('update','')
   
   let $prop_name as xs:string := request:get-parameter('prop_name','')
   let $prop_definition as xs:string := request:get-parameter('prop_definition','')
   let $oc_name as xs:string := request:get-parameter('oc_name','')
   let $oc_definition as xs:string := request:get-parameter('oc_definition','')


   return
      lib-forms:wrap-form-contents($title,
            if ($action='Store')
(:            then local:data-element-concept($registered-by,$administered-by,
                              $submitted-by,$administrative-status,$administrative-note,
                              $registration-status,$name,$definition,$country-identifier,
                              $language-identifier,$dec-data-id,$registration-authority,
                              $context,$conceptual-domain-id,
                              if ($object-class-id='')
                              then (local:admin-item-identifier($registration-authority, $oc-data-id))
                              else ($object-class-id),
                              if ($property-id='')
                              then (local:admin-item-identifier($registration-authority, $prop-data-id))
                              else ($property-id))
:)                              
            then  
               (
                  if (local:property($registered-by,$administered-by,$submitted-by,
                        $administrative-status,$administrative-note,$registration-status,
                        $prop-data-id,$country-identifier,$language-identifier,$registration-authority,
                        $context,$prop_name,$prop_definition,$property-uri))
                  then (
                      if (local:object-class($registered-by,$administered-by,$submitted-by,
                           $administrative-status,$administrative-note,$registration-status,
                           $oc-data-id,$country-identifier,$language-identifier,
                           $registration-authority,$context,$oc_name,$oc_definition,$object-class-uri))
                      then (
                         if (local:data-element-concept($registered-by,$administered-by,
                              $submitted-by,$administrative-status,$administrative-note,
                              $registration-status,$name,$definition,$country-identifier,
                              $language-identifier,$dec-data-id,$registration-authority,
                              $context,$conceptual-domain-id,
                              if ($object-class-id='')
                              then (local:admin-item-identifier($registration-authority, $oc-data-id))
                              else ($object-class-id),
                              if ($property-id='')
                              then (local:admin-item-identifier($registration-authority, $prop-data-id))
                              else ($property-id)
                              ))
                         then (local:success-page(
                              if ($object-class-id='')
                              then (local:admin-item-identifier($registration-authority, $oc-data-id))
                              else ($object-class-id),
                              if ($property-id='')
                              then (local:admin-item-identifier($registration-authority, $prop-data-id))
                              else ($property-id),
                              local:admin-item-identifier(
                                       $registration-authority, 
                                       $dec-data-id) 
                                   ))
                         else ((local:form('could not store data-element concept')))
                         )
                      else ((local:form('could not store object class')))
                     )
                  else ((local:form('could not store property')))
               )
            else local:form('')
         )
