xquery version "1.0";


(: ~
 : Module Name:             new value domain webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     31st July 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          Creates and value domain and displays list
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
      @author Puneet Mathur
 :    @version 0.1
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

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request"; 
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace datetime = "http://exist-db.org/xquery/datetime";

declare function local:value_domain(
   $reg-auth as xs:string,
   $administrative-note as xs:string,
   $administrative-status as xs:string,
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
   $registration_status as xs:string
   ) as xs:boolean
{
   let $version := '0.1'
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $doc-name := concat($new-identifier,'.xml')
   let $concept_domain := lib-util:mdrElement("conceptual_domain",$conceptual_domain_id)   
   let $value_meaning-identifier  := data($concept_domain//openMDR:value_meaning_identifier)
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
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
                         attribute permissible_value_identifier {lib-forms:generate-id()},
                         element openMDR:permissible_value_begin_date {$creation-date},
                         element openMDR:value_item {$values[$pos]},
                         element openMDR:contained_in {$value_meaning-identifier[$pos]}
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
            attribute version {$version},
            $content
           }
      ) else (
       element openMDR:Non_Enumerated_Value_Domain {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            attribute version {$version},
            $content
           }
      )
   )
      
   let $collection := 'value_domain'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newValueDomain.xquery&amp;",$message)))
};

declare function local:input-page(
   $message as xs:string?,
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
   $registration_status as xs:string?
   ) {
   let $version := '0.1'
   
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return
   <div xmlns="http://www.w3.org/1999/xhtml">   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to create a new value domain in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="new_value_domain" action="newValueDomain.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
             <div class="section">
             
              
           {lib-forms:edit-admin-item($reg-auth,
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
                        <tr><td class="row-header-cell" colspan="6">Conceptual Domain</td></tr>                                                
                        {  
                            let $concept_domain := lib-util:mdrElement("conceptual_domain",$conceptual_domain_id)
                            return
                        
                            if ($conceptual_domain_id > '' or $conceptual_domain_id eq "Cancel") then (                   
                               <tr>
                                    
                                    <td class="left_header_cell">Conceptual Domain ID</td>
                                    <td align="left">{$conceptual_domain_id}</td>
                                    <td>{session:set-attribute("conceptual_domain_id", $conceptual_domain_id)}</td>
                                    <td>{lib-forms:popup-form-search('conceptual_domain','conceptual_domain_id','new_value_domain', 'Change Relationship')}</td>                             
                                    <td>{lib-forms:hidden-element('conceptual_domain_id',$conceptual_domain_id)}</td>
                               </tr>,
                               <tr>
                                    <td class="left_header_cell">Conceptual Domain Name</td>
                                    <td align="left">{administered-item:preferred-name('conceptual_domain',$conceptual_domain_id)}</td>
                               </tr>,
                               <tr><td class="row-header-cell" colspan="6">Value Domain</td></tr>,
                               <tr>
                                   <td class="left_header_cell">Value Domain Data Type</td>
                                   <td collspan="3">{lib-forms:make-select-datatype('enum_datatype', request:get-parameter('enum_datatype',''))}</td>
                               </tr>,                          
                               <tr>
                                   <td class="left_header_cell">Value Domain Unit of Measure</td>
                                   <td collspan="3">{lib-forms:make-select-uom('enum_uom',request:get-parameter('uom',''))}</td>
                               </tr>,                         
                                  <tr>
                                   <td class="left_header_cell">Value Domain Format (E.g. MM-DD-YYYY)</td>
                                   <td collspan="3"><input type="text" name="value_domain_format"></input></td>
                               </tr>,                               
                                  <tr>
                                   <td class="left_header_cell">Value Domain Maximum Character Count</td>
                                   <td collspan="3"><input type="text" name="char_quantity"></input></td>
                               </tr>,
                                <tr>
                                   <td class="left_header_cell">Value Domain Minimum Character Length</td>
                                   <td collspan="3"><input type="text" name="min_char_quantity"></input></td>
                               </tr>,
                                <tr>
                                   <td class="left_header_cell">Value Domain High Value</td>
                                   <td collspan="3"><input type="text" name="value_domain_high_value"></input></td>
                               </tr>,
                                <tr>
                                   <td class="left_header_cell">Value Domain Low Value</td>
                                   <td collspan="3"><input type="text" name="value_domain_low_value"></input></td>
                               </tr>,
                                <tr>
                                   <td class="left_header_cell">Value Domain Decimal Place</td>
                                   <td collspan="3"><input type="text" name="value_domain_decimal_place"></input></td>
                               </tr>,

                               if($concept_domain//openMDR:value_meaning_description > '') 
                               then 
                               (
                                    <tr>
                                        <td class="left_header_cell">Possible Values</td>
                                            <td>meaning</td>
                                            <td>value</td>
                                        <td/>
                                    </tr>,
                                        for $meaning at $pos in $concept_domain//openMDR:value_meaning_description
                                        return (
                                           
                                           <tr class="thickBorder td">
                                              <td class="left_header_cell">Permissable Value {$pos}</td>
                                              <td>{$meaning}</td>
                                              <td>{lib-forms:input-element('values', 20, $values[$pos])}</td>
                                           </tr>
                                        )
                               ) else ()
                            ) 
                            else ( 
                                 <tr>
                                    <td class="left_header_cell">Choose Conceptual Domain <font color="red">*</font></td>
                                    <td align="left">{lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id',$conceptual_domain_id,'new_value_domain', 'Select Relationship')}</td>
                               </tr>
                            )
                        }
                        </table>
                        <table class="section">     
                              <tr>
                                <td class="row-header-cell" colspan="6">Store</td>
                              </tr>
                              <tr>
                                <td class="left_header_cell"></td>
                                <td><input type="submit" name="update" value="Save" onClick="return validate_valueDomain (this)"/></td>
                                <td><input type="button" name="update" value="Clear" onClick="this.form.reset()"/></td>
                                <td><input type="button" name="return" value="Return to Maintenance Menu" onclick="location.href='../edit/maintenance.xquery'"/></td>
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
         <p>Value Domain class <b>{request:get-parameter('names',())}</b> created</p>
         <p><a href="../edit/maintenance.xquery">Return to Maintenance Menu</a></p>    
         <p><a href="../edit/newValueDomain.xquery">Create another value domain</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

   session:create(),
   let $title as xs:string := "Creating a New Value Domain"
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
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
   let $action := request:get-parameter('update','')
   let $conceptual_domain_id as xs:string? := request:get-parameter('conceptual_domain_id','')
   let $values := request:get-parameter('values',())
   let $enum_datatype := request:get-parameter('enum_datatype','')
   let $enum_uom := request:get-parameter('enum_uom','')
   let $char_quantity := request:get-parameter('char_quantity','')
   let $min_char_quantity := request:get-parameter('min_char_quantity','')
   let $value_domain_high_value := request:get-parameter('value_domain_high_value','')
   let $value_domain_low_value := request:get-parameter('value_domain_low_value','')
   let $value_domain_decimal_place := request:get-parameter('value_domain_decimal_place','')
   let $value_domain_format := request:get-parameter('value_domain_format','')
   let $registration_status := request:get-parameter('registration_status','')
   return   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Save')
      then 
         (
         if (
               local:value_domain
                  (
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
                     $registration_status
                  )
            ) 
         then local:success-page()  
         else (local:input-page(
            'could not store document',
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
                     $registration_status
                  )
               )
         )
      else if ($action='Change Relationship')
      then(
            
            let $conceptual_domain_id as xs:string? := ""
            return local:input-page
               (
               '',
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
               $registration_status
               )
         )
         else if($conceptual_domain_id eq "Cancel")
         then(
               local:input-page
               (
               '',
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
               $registration_status
               ),
               session:set-attribute("conceptual_domain_id", "")
               
          ) else(
          local:input-page
               (
               '',
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
               $registration_status
               )
          )
        
     )