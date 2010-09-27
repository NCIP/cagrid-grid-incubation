xquery version "1.0";

(: ~
 : Module Name:             edit value domain webpage and XQuery
 :
 : Module Version           3.0
 :
 : Date                     26th Aug 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          Edit Value Domain
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
 :    @author Puneet Mathur
 :    @author Sreekant Lalkota
 :    @version 3.0
 :    allows editing the DataElementConcept
~ :)

  import module namespace 
  lib-forms="http://www.cagrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
  import module namespace 
  lib-util="http://www.cagrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";
  
  import module namespace 
  lib-rendering="http://www.cagrid.org/xquery/library/rendering"
  at "../web/m-lib-rendering.xquery";   
  
  import module namespace 
  lib-make-admin-item="http://www.cagrid.org/xquery/library/make-admin-item" 
  at "../edit/m-lib-make-admin-item.xquery";     

  import module namespace 
   administered-item="http://www.cagrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery"; 
      
  import module namespace
    lib-uri-resolution="http://www.cagrid.org/xquery/library/resolver"
    at "../resolver/m-lib-uri-resolution.xquery";     


declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";
   


declare function local:value_domain(
   $id as xs:string,
   $reg-auth as xs:string,
   $administrative-note as xs:string,
   $administrative-status as xs:string,
   $creation-date as xs:string,
   $administered-by as xs:string,
   $submitted-by as xs:string,
   $registered-by as xs:string,
   $context-ids as xs:string*,
   $country-identifiers as xs:string*,
   $language-identifiers as xs:string*,
   $names as xs:string*,
   $definitions as xs:string*,
   $sources as xs:string*,
   $preferred as xs:string?,
   $conceptual_domain_id as xs:string?,
   $enum_datatype as xs:string?,
   $enum_uom as xs:string?,
   $char_quantity as xs:string?,
   $min_char_quantity as xs:string?,
   $value_domain_high_value as xs:string?,
   $value_domain_low_value as xs:string?,
   $value_domain_decimal_place as xs:string?,
   $value_domain_format as xs:string?,
   $values as xs:string*,
   (: added this so that the selected version is saved in the xml:)
   $proposedVersion as xs:float?,
   $registration_status as xs:string
   ) as xs:boolean
{        
   let $version := lib-forms:substring-after-last($id,'_')
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'_'),'_')
   let $doc-name := concat($id,'.xml')
   let $concept_domain := lib-util:mdrElement("conceptual_domain",$conceptual_domain_id)
   let $permissible-identifier := substring-after(lib-forms:substring-before-last($id,'_'),'_')    
   let $value_meaning_identifier := $concept_domain//openMDR:value_meaning_identifier/text()
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources), 
            
             for $meaning at $pos in $concept_domain//openMDR:value_meaning_description
                    return (
                       element openMDR:containing {
                         attribute permissible_value_identifier {$permissible-identifier},
                         element openMDR:permissible_value_begin_date {$creation-date},
                         element openMDR:value_item {$values[$pos]},
                         element openMDR:contained_in {$value_meaning_identifier[$pos]}
                       }
             ),
            element openMDR:representing {$conceptual_domain_id},
            element openMDR:value_domain_datatype {$enum_datatype},
            element openMDR:value_domain_unit_of_measure {$enum_uom},
            element openMDR:value_domain_maximum_character_quantity{$char_quantity},
            element openMDR:value_domain_minimum_character_quantity{$min_char_quantity},
            element openMDR:value_domain_high_value{$value_domain_high_value},
            element openMDR:value_domain_low_value{$value_domain_low_value},
            element openMDR:value_domain_decimal_place{$value_domain_decimal_place},            
            element openMDR:value_domain_format{$value_domain_format}
            
    )
  
   (: compose the document :)
    let $document := (
     if($values > '' ) then (
      element openMDR:Enumerated_Value_Domain {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            (:
            attribute version {$version},
            :)
            attribute version {$proposedVersion},
            $content
           }
      ) else (
       element openMDR:Non_Enumerated_Value_Domain {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            (:
            attribute version {$version},
            :)
            attribute version {$proposedVersion},
            $content
           }
      )
   )
      
   let $collection := 'value_domain'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=editValueDomain.xquery&amp;",$message)))
};

   
declare function local:input-page(
   $message as xs:string?,
   $id as xs:string?,
   $reg-auth as xs:string?,
   $administrative-note  as xs:string?,
   $administrative-status  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by  as xs:string?,
   $registered-by  as xs:string?,
   $context-ids  as xs:string*,
   $country-identifiers as xs:string*,
   $language-identifiers as xs:string*,
   $names as xs:string*,
   $definitions as xs:string*,
   $sources as xs:string*,
   $action as xs:string?,
   $preferred as xs:string?,
   $conceptual_domain_id as xs:string?,
   $enum_datatype as xs:string?,
   $enum_uom as xs:string?,
   $char_quantity as xs:string?,
   $min_char_quantity as xs:string?,
   $value_domain_high_value as xs:string?,
   $value_domain_low_value as xs:string?,
   $value_domain_decimal_place as xs:string?,
   $value_domain_format as xs:string?,
   $values as xs:string*,
   $version as xs:float?,
   $registration_status as xs:string?
   ) {
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
   return

   <div xmlns="http://www.w3.org/1999/xhtml">
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to edit a Value Domain in the metadata repository
             </td>
          </tr>
          <tr>
            <td>
                <form name="edit_value_domain" action="editValueDomain.xquery" method="post" class="cagridForm" enctype="multipart/form-data" onSubmit="return validate_editValueDomain (this)">
                <div class="section">
                {lib-forms:hidden-element('id',$id)}
                {lib-forms:hidden-element('updating','updating')}
                
                {lib-forms:edit-admin-item-edit($reg-auth,
                     $administrative-note,
                     $administrative-status,
                     $administered-by,
                     $submitted-by,
                     $registered-by,
                     $context-ids,
                     $country-identifiers,
                     $language-identifiers,
                     $names,
                     $definitions,
                     $sources,
                     $preferred,
                     $action,
                     $version,
                     $registration_status
                     )}
                                  
                    <table class="section">
                        <tr>
                            <td class="row-header-cell" colspan="6">Conceptual Domain</td>
                        </tr>
                        {
                        let $concept_domain := lib-util:mdrElement("conceptual_domain",$conceptual_domain_id)
                        
                        return                    
                        if ($conceptual_domain_id > '' or $conceptual_domain_id eq "Cancel") 
                        then (                       
                           <tr>
                                <td class="left_header_cell">Conceptual Domain ID</td>
                                <td align="left">{$conceptual_domain_id}</td>
                                <td>{session:set-attribute("conceptual_domain_id", $conceptual_domain_id)}</td>
                                <td>{lib-forms:popup-form-search('conceptual_domain','conceptual_domain_id','edit_value_domain', 'Change Relationship')}</td>                                       
                                <td>{lib-forms:hidden-element('conceptual_domain_id',$conceptual_domain_id)}</td>
                           </tr>,
                           <tr>
                                <td class="left_header_cell">Conceptual Domain Name</td>
                                <td align="left">{administered-item:preferred-name('conceptual_domain',$conceptual_domain_id)}</td>
                           </tr>,
                           <tr><td class="row-header-cell" colspan="6">Value Domain</td></tr>,
                           <tr>
                               <td class="left_header_cell">Value Domain Data Type</td>
                               <td collspan="3">{lib-forms:make-select-datatype('enum_datatype', $enum_datatype)}</td>
                           </tr>,
                           <tr>
                               <td class="left_header_cell">Value Domain Unit of Measure</td>
                               <td collspan="3">{lib-forms:make-select-uom('enum_uom',$enum_uom)}</td>
                           </tr>,                   
                           <tr>
                               <td class="left_header_cell">Value Domain Format (E.g. MM-DD-YYYY)</td>
                               <td collspan="3"><input type="text" name="value_domain_format" value='{$value_domain_format}'></input></td>
                           </tr>,                               
                              <tr>
                               <td class="left_header_cell">Value Domain Maximum Character Count</td>
                               <td collspan="3"><input type="text" name="char_quantity" value='{$char_quantity}'></input></td>
                           </tr>,                                                        
                              <tr>
                               <td class="left_header_cell">Value Domain Minimum Character Count</td>
                               <td collspan="3"><input type="text" name="min_char_quantity" value='{$min_char_quantity}'></input></td>
                           </tr>,
                            <tr>
                                   <td class="left_header_cell">Value Domain High Value</td>
                                   <td collspan="3"><input type="text" name="value_domain_high_value" value='{$value_domain_high_value}'></input></td>
                               </tr>,
                                <tr>
                                   <td class="left_header_cell">Value Domain Low Value</td>
                                   <td collspan="3"><input type="text" name="value_domain_low_value" value='{$value_domain_low_value}'></input></td>
                               </tr>,
                                <tr>
                                   <td class="left_header_cell">Value Domain Decimal Place</td>
                                   <td collspan="3"><input type="text" name="value_domain_decimal_place" value='{$value_domain_decimal_place}'></input></td>
                               </tr>,
                           if ($concept_domain//openMDR:value_meaning_description > '') 
                           then 
                           (
                                   <tr>
                                   <td class="left_header_cell">Possible Values</td>
                                   <td>meaning</td>
                                   <td>value</td>
                                   </tr>,                                    
                                        for $meaning at $pos in $concept_domain//openMDR:value_meaning_description
                                        return (
                                           <tr  class="thickBorder td">
                                              <td class="left_header_cell">Permissable Value {$pos}</td>
                                              <td>{$meaning}</td>
                                              <td>{lib-forms:input-element('values', 20, $values[$pos])}</td>                                       
                                           </tr>
                                        ) 
                           ) 
                           else () 
                    ) 
                    else (
                                 <tr>
                                    <td class="left_header_cell">Choose Conceptual Domain</td>
                                    <td align="left">{lib-forms:make-select-form-admin-item('conceptual_domain','conceptual_domain_id',$conceptual_domain_id,'edit_value_domain', 'Select Relationship')}</td>   
                               </tr>                
                   )
               }    
               </table>
                    <table class="section">
                          <tr><td class="row-header-cell" colspan="6">Store</td></tr>
                          <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Save Changes"/></td>
                          <td colspan="4"><input type="button"  name="update" value="Cancel" 
                              onClick= "{concat("location.href='../web/value_domain.xquery?compound_id=", $id, "';")}" /></td>
                              
                          </tr>    
                    </table>
              </div>
          </form>
          </td></tr>
          <tr><td>{$message}</td></tr>
        </table>
     </div>
   };
   
   
declare function local:success-page() 
{
   let $calling-page := request:get-parameter("calling-page","")
   return
       <div xmlns="http://www.w3.org/1999/xhtml">
           <table class="layout">
              <tr>
                 <td>
                    Value Domain <b>{request:get-parameter('names',())}</b> modified
                 </td>
              </tr>
              <tr>
              </tr>
              <tr>
                <td><a href='../web/contents.xquery?start=1&amp;extent=5&amp;previous=1&amp;next=6&amp;last=1&amp;count=0&amp;recordlimit=0&amp;letter=&amp;type=value_domain'>View existing Value Domains</a>
                </td>
              </tr>
                 <tr>
                <td><a href="newValueDomain.xquery">Create New Value Domain</a>
                </td>
              </tr>
            </table>
      </div>

};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $updating := request:get-parameter('updating','')
   let $title as xs:string := concat("Editing ValueDomain ", $id)
   let $element := lib-util:mdrElement("value_domain",$id)
   let $action := request:get-parameter('update','')
   
   let $ireg-auth := string($element/@item_registration_authority_identifier)
   let $iadministrative-note := string($element//openMDR:administrative_note)
   let $iadministrative-status := string($element//openMDR:administrative_status)
   let $iadministered-by := string($element//openMDR:administered_by)
   let $isubmitted-by := string($element//openMDR:submitted_by)
   let $iregistered-by := string($element//openMDR:registered_by)
   let $icontext-ids := $element//openMDR:context_identifier
   let $icountry-identifiers := $element//openMDR:country_identifier
   let $ilanguage-identifiers := $element//openMDR:language_identifier
   let $inames := $element//openMDR:name
   let $idefinitions := $element//openMDR:definition_text
   let $isources := $element//openMDR:definition_source_reference
   let $ipreferred := string(fn:index-of($element//openMDR:preferred_designation,'true'))

    let $iconceptual_domain_id := if($element//openMDR:representing/text() > '') 
                                    then ($element//openMDR:representing/text()) else ("")
    let $iconceptual_domain := lib-util:mdrElement("conceptual_domain",$iconceptual_domain_id)
    let $ivalues := $element//openMDR:value_item
    let $ienum_datatype := $element//openMDR:value_domain_datatype
    let $ienum_uom := $element//openMDR:value_domain_unit_of_measure
    let $ichar_quantity := $element//openMDR:value_domain_maximum_character_quantity
    let $imin_char_quantity := $element//openMDR:value_domain_minimum_character_quantity
    let $ivalue_domain_high_value := $element//openMDR:value_domain_high_value
    let $ivalue_domain_low_value := $element//openMDR:value_domain_low_value
    let $ivalue_domain_decimal_place := $element//openMDR:value_domain_decimal_place
    let $ivalue_domain_format := $element//openMDR:value_domain_format  
    
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $creation-date := string($element//openMDR:creation_date)
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $context-ids := request:get-parameter('context-ids',())
   let $country-identifiers := request:get-parameter('country-identifiers',())
   let $language-identifiers := request:get-parameter('language-identifiers',())
   let $names := request:get-parameter('names',())
   let $definitions := request:get-parameter('definitions',())
   let $sources := request:get-parameter('sources',())
   let $preferred := request:get-parameter('preferred','')
   let $conceptual_domain_id := request:get-parameter('conceptual_domain_id','')
   let $values := request:get-parameter('values',())
   let $enum_datatype := request:get-parameter('enum_datatype','')
   let $enum_uom := request:get-parameter('enum_uom','')
   let $char_quantity := request:get-parameter('char_quantity','')
   let $min_char_quantity := request:get-parameter('min_char_quantity','')
   let $value_domain_high_value := request:get-parameter('value_domain_high_value','')
   let $value_domain_low_value := request:get-parameter('value_domain_low_value','')
   let $value_domain_decimal_place := request:get-parameter('value_domain_decimal_place','')
   
   let $value_domain_format := request:get-parameter('value_domain_format','')
   let $iversion := data($element/@version)
   let $version := request:get-parameter('version','')
   let $version := $iversion
   (:getting proposed version and release version :)
   let $proposedNextVersion := request:get-parameter('proposedNextVersion',$iversion)
   let $version := round-half-to-even(xs:float($proposedNextVersion),2)
   
   let $iregistration_status := string($element//openMDR:registration_status)
   let $registration_status := request:get-parameter('registration_status',$iregistration_status)
   
   return
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Save Changes')
      then 
         (
         if (
               local:value_domain
                  (
                     $id,
                     $reg-auth,
                     $administrative-note,
                     $administrative-status,
                     $creation-date,
                     $administered-by,
                     $submitted-by,
                     $registered-by,
                     $context-ids,
                     $country-identifiers,
                     $language-identifiers,
                     $names,
                     $definitions,
                     $sources,
                     $preferred,
                     $conceptual_domain_id,
                     $enum_datatype,
                     $enum_uom,
                     $char_quantity,
                     $min_char_quantity,
                     $value_domain_high_value,
                     $value_domain_low_value,
                     $value_domain_decimal_place,
                     $value_domain_format,
                     $values,
                      (: added this so that the version gets saved:)
                     $version,
                     $registration_status
                  )
            ) 
         then (local:success-page()  )
         else (local:input-page(
            'could not store document',
                     $id,
                     $reg-auth,
                     $administrative-note,
                     $administrative-status,
                     $administered-by,
                     $submitted-by,
                     $registered-by,
                     $context-ids,
                     $country-identifiers,
                     $language-identifiers,
                     $names,
                     $definitions,
                     $sources,
                     $action,
                     $preferred,
                     $conceptual_domain_id,
                     $enum_datatype,
                     $enum_uom,
                     $char_quantity,
                     $min_char_quantity,
                     $value_domain_high_value,
                     $value_domain_low_value,
                     $value_domain_decimal_place,
                     $value_domain_format,
                     $values,
                      (: added this so that the version gets saved:)
                     $iversion,
                     $registration_status
                     )
               )
         )
       else if ($action='Change Relationship')
         then(
               let $conceptual_domain_id as xs:string? := ""     
             
               return
               local:input-page
               (
               '',
               $id,
               $reg-auth,
               $administrative-note,
               $administrative-status,
               $administered-by,
               $submitted-by,
               $registered-by,
               $context-ids,
               $country-identifiers,
               $language-identifiers,
               $names,
               $definitions,
               $sources,
               $action,
               $preferred,
               $conceptual_domain_id,
               $enum_datatype,
               $enum_uom,
               $char_quantity,
               $min_char_quantity,
               $value_domain_high_value,
               $value_domain_low_value,
               $value_domain_decimal_place,
               $value_domain_format,
               $values,
                (: added this so that the version gets saved:)
                     $iversion,
                     $registration_status
               )
         )
         else if($conceptual_domain_id eq "Cancel")
         then(  
               local:input-page
               (
               '',
               $id,
               $reg-auth,
               $administrative-note,
               $administrative-status,
               $administered-by,
               $submitted-by,
               $registered-by,
               $context-ids,
               $country-identifiers,
               $language-identifiers,
               $names,
               $definitions,
               $sources,
               $action,
               $preferred,
               session:get-attribute("conceptual_domain_id"),
               $enum_datatype,
               $enum_uom,
               $char_quantity,
               $min_char_quantity,
               $value_domain_high_value,
               $value_domain_low_value,
               $value_domain_decimal_place,
               $value_domain_format,
               $values,
                (: added this so that the version gets saved:)
                     $iversion,
                     $registration_status
               ),
               session:set-attribute("conceptual_domain_id", $iconceptual_domain_id)
        ) 
        else (         
         if ($updating ='updating')
         then (
         
         local:input-page
               (
               '',
               $id,
               $reg-auth,
               $administrative-note,
               $administrative-status,
               $administered-by,
               $submitted-by,
               $registered-by,
               $context-ids,
               $country-identifiers,
               $language-identifiers,
               $names,
               $definitions,
               $sources,
               $action,
               $preferred,
               $conceptual_domain_id,
               $enum_datatype,
               $enum_uom,
               $char_quantity,
               $min_char_quantity,
               $value_domain_high_value,
               $value_domain_low_value,
               $value_domain_decimal_place,
               $value_domain_format,
               $values,
                (: added this so that the version gets saved:)
                     $iversion,
                     $registration_status
               )
         ) else (
               local:input-page
               (
               '',
               $id,
               $ireg-auth,
               $iadministrative-note,
               $iadministrative-status,
               $iadministered-by,
               $isubmitted-by,
               $iregistered-by,
               $icontext-ids,
               $icountry-identifiers,
               $ilanguage-identifiers,
               $inames,
               $idefinitions,
               $isources,
               $action,
               $ipreferred,
               $iconceptual_domain_id,
               $ienum_datatype,
               $ienum_uom,
               $ichar_quantity,
               $imin_char_quantity,
               $ivalue_domain_high_value,
               $ivalue_domain_low_value,
               $ivalue_domain_decimal_place,
               $ivalue_domain_format,
               $ivalues,
                (: added this so that the version gets saved:)
                     $iversion,
                     $registration_status
               )
         )
       )    
    )
       

