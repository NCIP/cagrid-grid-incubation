
xquery version "1.0";

(: ~
 : Module Name:             edit context webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     31st July 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          Conceptual Domain information
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
 :    @version 0.1
 :
 :    Conceptual Domain information 
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

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";


declare function local:conceptual-domain(
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
   $meanings as xs:string*
   ) as xs:boolean
{
   let $version := '0.1'
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $doc-name := concat($new-identifier,'.xml')

   let $content := (
            
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss"),'Recorded'),
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
                  let $vmid := lib-forms:generate-id()
                  where $meaning > ""
                    return
                        element openMDR:Value_Meaning {
                          element openMDR:value_meaning_begin_date {$creation-date},
                          element openMDR:value_meaning_description {$meaning},
                          element openMDR:value_meaning_identifier {$vmid}
                        }
                    
                  
                  
                  )


   
   (: compose the document :)
   let $document := (
       if($meanings > '') then (
          element openMDR:Enumerated_Conceptual_Domain {
                attribute item_registration_authority_identifier {$reg-auth},
                attribute data_identifier {$data-identifier},
                attribute version {$version},
                $content
               }
      ) else (
          element openMDR:Non_Enumerated_Conceptual_Domain {
                attribute item_registration_authority_identifier {$reg-auth},
                attribute data_identifier {$data-identifier},
                attribute version {$version},
                $content
               }
      )
   )
      
   let $collection := 'conceptual_domain'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newConceptualDomain.xquery&amp;",$message)))
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
   $meanings as xs:string*
   ) {
   
   

   
   let $skip-name := substring-after($action,'delete value meaning')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return
   (

   
   <div xmlns="http://www.w3.org/1999/xhtml">
    
      <table class="layout">
          <tr>
             if(request:get-parameter('id','') > '' ) then (
             <td>
                This form will allow you to edit the conceptual domain in the metadata repository
             </td>
             ) else (
             <td>
                This form will allow you to create a new conceptual domain in the metadata repository
             </td>
             )
          </tr>
          <tr><td>
          <form name="new_value_domain" action="conceptualDomain.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
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
                     $action)}
                     
                     
                <table class="layout">
                   <tr><td class="row-header-cell" colspan="6">Conceptual Domain</td></tr>

                {
                 if(request:get-parameter('conceptual-domain-type','') = 'enumerated') then (
                  
                     <tr><td class="left_header_cell">Enumerated Conceptual Domain?</td>
                      <td><input type="radio" name="conceptual-domain-type" value="enumerated" checked="checked">enumerated</input></td>
                      <td><input type="radio" name="conceptual-domain-type" value="non-enumerated">non-enumerated</input></td>
                      <td>{lib-forms:action-button('update', 'action' ,'')}</td>
                     </tr>,
                     <tr><td class="row-header-cell" colspan="6">Conceptual Domain Meanings</td></tr>,
      
                     <tr>
                      <td class="left_header_cell">Value Domain Meanings</td><td>meaning</td>
                     </tr>,
                  
                      
                        for $meaning at $pos in $meanings
                        let $location := if($pos > $skip-name-index and $skip-name-index > 0) then (util:eval($pos - 1)) else ($pos)
                        where $pos != $skip-name-index and $meaning > ""
                        return (
                           <tr>
                              <td class="left_header_cell">Value {$location} Meaning</td>
                              <td>{lib-forms:input-element('meanings', 60, $meaning)}</td>
                              <td>{lib-forms:action-button(concat('delete value meaning ',$location), 'action' ,'')}</td>
                           </tr>
                           ),
                           <tr>
                              <td class="left_header_cell">New Value Meaning</td>
                              <td>{lib-forms:input-element('meanings', 60, '')}</td>
                              <td>{lib-forms:action-button('add new value meaning', 'action' ,'')}</td>
                           </tr>
                       
                 ) else ( 
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
                         <td><input type="submit" name="update" value="Store"/></td><td colspan="4"><input type="submit" name="update" value="Clear"/></td>  
                      </tr>  
                 </table>
              </div>
          </form>
          </td></tr>
          <tr><td>{$message}</td></tr>
        </table>
     </div>
     )
   };
   
declare function local:success-page() 
{
   let $calling-page := request:get-parameter("calling-page","")
   return
      <div>
         <p>Conceptual Domain created</p>
         <p><a href="../edit/maintenance.xquery">Return to Maintenance Menu</a></p>    
         <p><a href="../edit/newConceptualDoamin.xquery">Create another Conceptual Domain</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   
   let $title as xs:string := "Creating a New Conceptual Domain"
   let $id := request:get-parameter('id','')
   let $updating := request:get-parameter('updating','')
   let $action := request:get-parameter('update','')
   let $element := lib-util:mdrElement("conceptual_domain",$id)
   
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
   let $ipreferred := fn:index-of($element//openMDR:preferred_designation,'true')
   let $imeanings := $element//openMDR:value_meaning_description
   
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
   let $meanings := request:get-parameter('meanings','')
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store')
      then 
         (
         if (
               local:conceptual-domain
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
                     $meanings
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
                     $meanings
                  )
               )
         )
      else (
          if ($updating ='updating')
            then (
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
                   $meanings
                   )
            ) else (
               local:input-page
                   (
                   '',
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
                   $imeanings
                )
            )
         )
      )

