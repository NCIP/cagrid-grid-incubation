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
 : Module overview          Creates and property and displays list
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
 :
 :    @author Steve Harris
 :    @version 2.0
 :     now allows searching for concept terms 
 :
 :    @author Rakesh Dhaval
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
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";
   


declare function local:value_domain(
   $id as xs:string,
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
   $values as xs:string*
   ) as xs:boolean
{
                    
   let $log := util:log-system-err($values)
             
   let $version := lib-forms:substring-after-last($id,'-')
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'-'),'-')
   let $doc-name := concat($id,'.xml')
   let $concept_domain := lib-util:mdrElement("conceptual_domain",$conceptual_domain_id)
   let $permissible-identifier := substring-after(lib-forms:substring-before-last($id,'-'),'-')    
    
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,'Recorded'),
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
                         element openMDR:permissible_value_begin_date {current-date()},
                         element openMDR:value_item {$values[$pos]},
                         element openMDR:contained_in {$concept_domain//openMDR:value_meaning_identifier[$pos]/text()}
                       }
             ),
            element openMDR:representing {$conceptual_domain_id}
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
   $values as xs:string*
   ) {
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
   let $log := util:log-system-err($values)
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
                <form name="edit_value_domain" action="editValueDomain.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
                <div class="section">
                {lib-forms:hidden-element('id',$id)}
                {lib-forms:hidden-element('updating','updating')}
                
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
                     $action)}
                                  
                    <table class="section">
                        <tr>
                            <td class="row-header-cell" colspan="6">Conceptual Domain</td>
                        </tr>
                        {
                        let $log := util:log-system-err($values)
                        let $concept_domain := lib-util:mdrElement("conceptual_domain",$conceptual_domain_id)
                        return                    
                        if ($conceptual_domain_id > '') 
                        then (                       
                           <tr>
                                <td class="left_header_cell">Conceptual Domain ID</td>
                                <td align="left">{$conceptual_domain_id}</td>
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
                           if ($concept_domain//openMDR:value_meaning_description > '') 
                           then 
                           (
                                   <tr>
                                   <td class="left_header_cell">Possible Values</td>
                                   <td colspan="3">meaning</td>
                                   <td>value</td>
                                   </tr>,                                    
                                        for $meaning at $pos in $concept_domain//openMDR:value_meaning_description
                                        return (
                                           <tr>
                                              <td class="left_header_cell">Permissable Value {$pos}</td>
                                              <td colspan="3" >{$meaning}</td>
                                              <td>{lib-forms:input-element('values', 20, $values[$pos])}</td>
                                           </tr>
                                        ) 
                           ) 
                           else () 
                    ) 
                    else (
                           <tr>
                                <td class="left_header_cell">Choose Conceptual Domain</td>
                                <td align="left">
                                {                                    
                                    lib-forms:make-select-admin-item('conceptual_domain','conceptual_domain_id',$conceptual_domain_id)
                                }
                                </td>
                                <td>{lib-forms:action-button('select', 'action','')}</td>                                
                           </tr>                    
                   )
               }    
               </table>
                    <table class="section">
                          <tr><td class="row-header-cell" colspan="6">Store</td></tr>
                          <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Store Changes"/></td><td colspan="4"><input type="submit" name="update" value="Clear"/></td></tr>    
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
      <div>
         <p>Property modified</p>
         <p><a href="../xquery/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../xquery/newValueDomain.xquery">Create another Value Domain</a></p> 
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
   
   (: let $iconceptual_domain_id  := $element//openMDR:conceptual_domain_id   
    let test := element//openMDR:contained_in/text())/openMDR:value_meaning_description
   :)
   
   let $iconceptual_domain_id := $element//openMDR:representing/text()
   let $log := util:log-system-err($iconceptual_domain_id)
   
   let $ivalues := $element//openMDR:value_item
   let $log := util:log-system-err($ivalues)
   
   let $imeanings := $element//openMDR:conceptual_domain
   let $log := util:log-system-err($imeanings)
   
   (:
    for $permissible_value  in permissible-value:contained_in($administered_item)
      let $value := value:used_in($permissible_value)
      order by $value
      return
         <tr>
               <td class="left_header_cell">
                 {if ($value) then value:item($value) else ()}
               </td>
               <td>
                 {
                   string-join(
                       for $meaning in value-meaning:value-meaning($permissible_value/openMDR:contained_in/text())/openMDR:value_meaning_description
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
   :)
   
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
   let $conceptual_domain_id as xs:string? := request:get-parameter('conceptual_domain_id','')
   let $values := request:get-parameter('values',())

   return
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store Changes')
      then 
         (
         if (
               local:value_domain
                  (
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
                     $preferred,
                     $conceptual_domain_id,
                     $values
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
                     $values
                  )
               )
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
               $values
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
               $ivalues
               )
         )
       )
       
    )
       

