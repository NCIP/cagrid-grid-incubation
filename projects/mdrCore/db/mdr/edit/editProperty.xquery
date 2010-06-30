
xquery version "1.0";

(: ~
 : Module Name:             edit property webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     31st July 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          edit property information
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
 :    @author Puneet Mathur
 :    @version 0.1
 :
 :    Edit Property information 
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
declare namespace request="http://exist-db.org/xquery/request"; 
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";


declare function local:property(
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
   $uris as xs:string*,
   $preferred as xs:string?,
   (: added this so that the selected version is saved in the xml:)
   $proposedVersion as xs:float?,
   $registration_status as xs:string
   
   ) as xs:boolean
{
   let $version := lib-forms:substring-after-last($id,'_')
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
                    
                    for $u in $uris
                    return
                    element openMDR:reference_uri {$u})


   
   (: compose the document :)
   let $document :=
      element openMDR:Property {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            (:
            attribute version {$version},
            :)
            attribute version {$proposedVersion},
            $content
           }
      
   let $collection := 'property'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=editProperty.xquery&amp;",$message)))
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
   $property_uri as xs:string*,
   $action as xs:string?,
   $preferred as xs:string?,
   
   (:11111111111111111:)
   $version as xs:float?,
   $registration_status as xs:string?
   ) 
   {

   let $skip-uri := substring-after($action,'delete uri entry')
   let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0
   return
   <div xmlns="http://www.w3.org/1999/xhtml">
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to edit a property in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="edit_property" action="editProperty.xquery" method="post" class="cagridForm" enctype="multipart/form-data" onSubmit="return validate_adminItems ()">
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
                   
                <table class="section">       
                       
                    <tr>
                       <td class="row-header-cell" colspan="6">Property specific properties</td>
                    </tr>
                       {                     
                         for $u at $pos in $property_uri
                         where $pos != $skip-uri-index and $u > ""
                         return 
                            (
                                if($pos > $skip-uri-index and $skip-uri-index > 0) 
                                then (
                                    <tr>
                                    <td class="left_header_cell">Concept Reference {util:eval($pos - 1)}</td>
                                    <td colspan="5">
                                       {
                                          lib-forms:find-concept-id-edit-false('property_uri','get concept',$u),
                                          lib-forms:action-button(concat('delete uri entry ',util:eval($pos - 1)), 'action' ,'')
                                       }
                                    </td>
                                    </tr>
                                ) 
                                else (
                                   <tr>
                                      <td class="left_header_cell">Concept Reference {$pos}</td>
                                      <td colspan="5">
                                         {
                                            lib-forms:find-concept-id-edit-false('property_uri','get concept',$u),
                                            lib-forms:action-button(concat('delete uri entry ',$pos), 'action' ,'')
                                         }
                                      </td>
                                   </tr>
                                )
                          
                           )
                     }
                     {
                           if($action = 'add another concept') then (
                           <tr>
                               <td class="left_header_cell">New Concept Reference</td>
                               <td colspan="5">
                                  {lib-forms:find-concept-id-edit-false('property_uri','get concept','')}
                                  <br></br>
                                  {lib-forms:action-button('add another concept', 'action' ,'')}
                               </td>
                            </tr>
                           ) else (
                           <tr>
                               <td class="left_header_cell">New Concept Reference</td>
                               <td colspan="5">
                                  {lib-forms:action-button('add another concept', 'action' ,'')}
                               </td>
                            </tr>
                           )
                    }
                    
                      <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Save Changes"/></td>
                      <td colspan="4"><input type="button"  name="update" value="Cancel" 
                              onClick= "{concat("location.href='../web/property.xquery?compound_id=", $id, "';")}" /></td>
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
                    Property <b>{request:get-parameter('names',())}</b> modified
                 </td>
              </tr>
              <tr>
              </tr>
              <tr>
                <td><a href='../web/contents.xquery?start=1&amp;extent=5&amp;previous=1&amp;next=6&amp;last=1&amp;count=0&amp;recordlimit=0&amp;letter=&amp;type=property'>View existing Property Classes</a>
                </td>
              </tr>
                 <tr>
                <td><a href="newProperty.xquery">Create New Property</a>
                </td>
              </tr>
            </table>
      </div>

};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $updating := request:get-parameter('updating','')
   let $title as xs:string := concat("Editing Property ", $id)
   let $element := lib-util:mdrElement("property",$id)
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
   let $iproperty_uri := $element//openMDR:reference_uri
   let $ipreferred := string(fn:index-of($element//openMDR:preferred_designation,'true'))
   let $iaction := request:get-parameter('update','')

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
   let $property_uri := request:get-parameter('property_uri',())
   let $preferred := request:get-parameter('preferred','')
   let $action := request:get-parameter('update','')
      
   (:11111111111111111111111111:)
    let $log := util:log-system-out('printing iversion................')
   let $iversion := data($element/@version)
   let $version := request:get-parameter('version','')
   let $log := util:log-system-out($iversion)   
   
   let $log := util:log-system-out($iversion)
   let $log := util:log-system-out($version)
   let $version := $iversion
   (:getting proposed version and release version :)
   let $proposedNextVersion := request:get-parameter('proposedNextVersion',$iversion)
   let $log := util:log-system-out('printing proposed version.....from here...........')
   let $version := round-half-to-even(xs:float($proposedNextVersion),2)
   let $log := util:log-system-out('printing proposed version.....from here...........')
   let $log := util:log-system-out($version)
   
   let $iregistration_status := string($element//openMDR:registration_status)
   let $registration_status := request:get-parameter('registration_status',$iregistration_status)  

   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Save Changes')
      then 
         (
         if (
               local:property
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
                     $property_uri,
                     $preferred,
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
                     $property_uri,
                     $action,
                     $preferred,
                      (: added this so that the version gets saved:)
                     $version,
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
               $property_uri,
               $action,
               $preferred,
                (: added this so that the version gets saved:)
                     $version,
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
               $iproperty_uri,
               $action,
               $ipreferred,
                (: added this so that the version gets saved:)
                     $version,
                     $registration_status
               )
         )
       )
       
    )
