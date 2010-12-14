
xquery version "1.0";


(: ~
 : Module Name:             edit Conceptual Domain webpage and XQuery
 :
 : Module Version           3.0
 :
 : Date                     26th Aug 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          Edit Conceptual Domain
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
 :    @author Puneet Mathur
 :    @author Sreekant Lalkota
 :    @version 1.0
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

   
declare function local:conceptual-domain(
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
   $meanings as xs:string*,
   $value_meaning_identifiers as xs:string*,
   (: added this so that the selected version is saved in the xml:)
   $proposedVersion as xs:float?,
   $value_meaning_uri as xs:string*,
   $registration_status as xs:string
   ) as xs:boolean
{
   let $version := lib-forms:substring-after-last($id,'_')
   let $vmid := substring-after(lib-forms:substring-before-last($id,'_'),'_')
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'_'),'_')
   let $doc-name := concat($id,'.xml')
    
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
                   
                                       
                    for $meaning at $pos in $meanings
                    let $vmid := if($value_meaning_identifiers[$pos] > '')
                            then $value_meaning_identifiers[$pos]
                            else lib-forms:generate-id()
                    return
                    if($meaning >'') then
                       element openMDR:Value_Meaning 
                       {
                        element openMDR:value_meaning_begin_date {$creation-date},
                        element openMDR:value_meaning_description {$meaning},
                        element openMDR:value_meaning_identifier {$vmid},
                        element openMDR:reference_uri {$value_meaning_uri[$pos]}
                       }
                       else()
                    )
                        
   (: compose the document :)
   let $document := (
       if($meanings > '') then (
          element openMDR:Enumerated_Conceptual_Domain {
                attribute item_registration_authority_identifier {$reg-auth},
                attribute data_identifier {$data-identifier},
                (:
            attribute version {$version},
            :)
            attribute version {$proposedVersion},
                $content
               }
      ) else (
          element openMDR:Non_Enumerated_Conceptual_Domain {
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
   
   let $collection := 'conceptual-domain'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=editConceptualDomain.xquery&amp;",$message)))
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
   $meanings as xs:string*,
   $value_meaning_identifiers as xs:string*,
    (:11111111111111111:)
   $version as xs:float?,
   $value_meaning_uri as xs:string*,
   $registration_status as xs:string?
   ) 
   {
       let $skip-name := substring-after($action,'delete naming entry')
       let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
       
       let $skip-uri := substring-after($action,'delete value meaning')
       let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0
       
       let $vmid := if($action eq 'updating')
            then lib-forms:generate-id() 
            else $value_meaning_identifiers
    return
        <div xmlns="http://www.w3.org/1999/xhtml">
    
        <table class="layout">
          <tr>
             <td>
                This form will allow you to Edit Conceptual Domain in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="edit_conceptual_domain" action="editConceptualDomain.xquery" method="post" class="cagridForm" enctype="multipart/form-data" onSubmit="return validate_adminItems ()">
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
                      (:111111111111111111111111:)
                     $version,
                     $registration_status
                     )}
                     
                <table class="layout">
                   <tr><td class="row-header-cell" colspan="6">Conceptual Domain</td></tr>

                {
                  
                    if(exists($meanings))
                    then (
                     <tr><td class="left_header_cell">Enumerated Conceptual Domain?</td>
                      <td><input type="radio" name="conceptual-domain-type" value="enumerated" checked="checked">enumerated</input></td>
                      <td><input type="radio" name="conceptual-domain-type" value="non-enumerated">non-enumerated</input></td>
                      <td>{lib-forms:action-button('update', 'action' ,'')}</td>
                     </tr>,
                     <tr><td class="row-header-cell" colspan="6">Conceptual Domain Meanings</td></tr>,     
                     <tr><td class="left_header_cell">Value Domain Meanings</td><td>meaning</td>  <td>Reference URI</td>                   
                     </tr>,
                     
                              
                       for $meaning at $pos in $meanings                   
                       let $location := if($pos > $skip-uri-index and $skip-uri-index > 0) then (util:eval($pos - 1)) else ($pos)
                                              
                       where $pos != $skip-uri-index and $meaning > ""
                        return (
                           <tr>
                              <td class="left_header_cell">Value {$location} Meaning</td>
                              <td>{lib-forms:input-element('meanings', 30, $meaning)}{lib-forms:action-button(concat('delete value meaning ',$location), 'action' ,'')}</td>
                              <td>{lib-forms:find-concept-id-CD('property_uri','get concept',$value_meaning_uri[$pos])}</td>
                              <td>{lib-forms:hidden-element('value_meaning_identifiers',$value_meaning_identifiers[$pos])} </td>
                           </tr>
                           ),
                           <tr>
                              <td class="left_header_cell">New Value Meaning</td>
                              <td>{lib-forms:input-element('meanings', 30, '')}{lib-forms:action-button('add new value meaning', 'action' ,'')}</td>
                               <td>{lib-forms:find-concept-id-CD('property_uri','get concept','')}</td>
                           </tr>
                    ) 
                    else 
                    ( 
                         <tr><td class="left_header_cell">Enumerated Conceptual Domain?</td>
                           <td><input type="radio" name="conceptual-domain-type" value="enumerated" >enumerated</input></td>
                           <td><input type="radio" name="conceptual-domain-type" value="non-enumerated" checked="checked">non-enumerated</input></td>
                           <td>{lib-forms:action-button('update', 'action' ,'')}</td>
                         </tr>
                    )
              }
               
            </table> 
                 <table class="section">     
                      <tr><td class="row-header-cell" colspan="6">Store</td></tr>
                      <tr><td class="left_header_cell"></td>
                         <td><input type="submit" name="update" value="Save Changes"/></td>
                         <td colspan="4"><input type="button"  name="update" value="Cancel" 
                              onClick= "{concat("location.href='../web/conceptual_domain.xquery?compound_id=", $id, "';")}" /></td>
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
                    Conceptual Domain <b>{request:get-parameter('names',())}</b> modified
                 </td>
              </tr>
              <tr>
              </tr>
              <tr>
                <td><a href='../web/contents.xquery?start=1&amp;extent=5&amp;previous=1&amp;next=6&amp;last=1&amp;count=0&amp;recordlimit=0&amp;letter=&amp;type=conceptual_domain'>View existing Conceptual Domains</a>
                </td>
              </tr>
                 <tr>
                <td><a href="newConceptualDomain.xquery">Create New Conceptual Domain</a>
                </td>
              </tr>
            </table>
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $updating := request:get-parameter('updating','')
   let $title as xs:string := concat("Editing Conceptual Domain ", $id)
   let $element := lib-util:mdrElement("conceptual_domain",$id)
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
   let $imeanings := $element//openMDR:value_meaning_description
   let $ivalue_meaning_identifiers := $element//openMDR:Value_Meaning/openMDR:value_meaning_identifier/text() 

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
   let $meanings := request:get-parameter('meanings','')
   let $value_meaning_identifiers := request:get-parameter('value_meaning_identifiers','')

   let $iversion := data($element/@version)
   let $version := request:get-parameter('version','')
   
   let $version := $iversion
   (:getting proposed version and release version :)
   let $proposedNextVersion := request:get-parameter('proposedNextVersion',$iversion)
   let $version := round-half-to-even(xs:float($proposedNextVersion),2)
   
   let $ivalue_meaning_uri := data($element//openMDR:Value_Meaning/openMDR:reference_uri)
   let $value_meaning_uri := request:get-parameter('property_uri','')
   (:let $value_meaning_uri := $ivalue_meaning_uri:)
   
   let $iregistration_status := string($element//openMDR:registration_status)
   let $registration_status := request:get-parameter('registration_status',$iregistration_status)
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Save Changes')
      then 
         (
         if (
               local:conceptual-domain
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
                     $meanings,
                     $value_meaning_identifiers,
                      (: added this so that the version gets saved:)
                     $version,
                     $value_meaning_uri,
                     $registration_status
                     
                  )
            ) 
         then (local:success-page())
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
                     $meanings,
                     $value_meaning_identifiers,
                      (: added this so that the version gets saved:)
                     $iversion,
                     $value_meaning_uri,
                     $registration_status
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
               $meanings,
               $value_meaning_identifiers,
                (: added this so that the version gets saved:)
                     $iversion,
                     $value_meaning_uri,
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
               $imeanings,
               $ivalue_meaning_identifiers,
                (: added this so that the version gets saved:)
                     $iversion,
                     $ivalue_meaning_uri,
                     $registration_status
               )
         )
       )
    )
       



