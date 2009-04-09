xquery version "1.0";

(: ~
 : Module Name:             new property webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     31st July 2007
 :
 : Copyright                The cancergrid consortium
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
~ :)

  import module namespace 
  lib-forms="http://www.cancergrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
  import module namespace 
  lib-util="http://www.cancergrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";
  
  import module namespace 
  lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
  at "../web/m-lib-rendering.xquery";   

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";


declare function local:property(
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
   $uri as xs:string?,
   $preferred as xs:string?
   ) as xs:boolean
{
   let $version := lib-forms:substring-after-last($id,'-')
   let $data-identifier := lib-forms:substring-after-last(lib-forms:substring-before-last($id,'-'),'-')
   let $doc-name := concat($id,'.xml')

   let $content := (
            element cgMDR:administered_item_administration_record {
               element cgMDR:administrative_note {$administrative-note},
               element cgMDR:administrative_status {$administrative-status},
               element cgMDR:creation_date {current-date()},
               element cgMDR:effective_date {current-date()},
               element cgMDR:last_change_date {current-date()},
               element cgMDR:registration_status {'Recorded'}
               },
               element cgMDR:administered_by {$administered-by},
               element cgMDR:registered_by {$registered-by},
               element cgMDR:submitted_by {$submitted-by},
               
               for $name at $pos in $names
               return
                  element cgMDR:having {
                     element cgMDR:context_identifier {$context-ids[$pos]},
                     element cgMDR:containing {
                        element cgMDR:language_section_language_identifier {
                           element cgMDR:country_identifier {$country-identifiers[$pos]},
                           element cgMDR:language_identifier {$language-identifiers[$pos]}
                           },
                        element cgMDR:name {$name},
                        element cgMDR:definition_text {$definitions[$pos]}, 
                        if($preferred > '') then (
                        element cgMDR:preferred_designation {(xs:int($preferred) = xs:int($pos))})
                        else (),
                        element cgMDR:definition_source_reference {$sources[$pos]}
                        }
                  },

                  element cgMDR:reference_uri {$uri})


   
   (: compose the document :)
   let $document :=
      element cgMDR:Property {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            attribute version {$version},
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
   $property_uri as xs:string?,
   $action as xs:string?,
   $preferred as xs:string?
   ) {
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return
   <div xmlns="http://www.w3.org/1999/xhtml">
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to edit a property in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="edit_property" action="editProperty.xquery" method="post" class="cancergridForm" enctype="multipart/form-data">
             <div class="section">
                {lib-forms:hidden-element('id',$id)}
                {lib-forms:hidden-element('updating','updating')}
                <table class="section">
                {
                    for $name at $pos in $names
                    where $pos != $skip-name-index and $name > ""
                    return
                    (
                    <tr><td class="row-header-cell" colspan="6">Naming entry {$pos}</td></tr>,
                    <tr>
                       <td class="left_header_cell">Context</td>
                       <td colspan="5">
                          {lib-forms:select-from-contexts-enum('context-ids',$context-ids[$pos])}
                       </td>
                    </tr>,
                    
                    <tr>
                       <td class="left_header_cell">Name</td>
                       <td colspan="5">
                          {lib-forms:input-element('names',70,$name)}
                       </td>
                    </tr>,

                    <tr>
                       <td class="left_header_cell">Preferred</td>
                       <td>
                           {lib-forms:radio('preferred', xs:string($pos), xs:string(($preferred = xs:string($pos))))}
                       </td>
                    </tr>,
         
         
                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, $definitions[$pos])}
                       </td>
                    </tr>,
                    
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(), $country-identifiers[$pos])}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), $language-identifiers[$pos])}
                       </td>
                    </tr>,
                    
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources',70,$sources[$pos])}</td>
                    </tr>,
                    
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button(concat('delete naming entry ',$pos), 'action' ,'')}</td></tr>
                    ),
                     
                    if($action = 'add new name')
                    then (
                    
                    <tr><td class="row-header-cell" colspan="6">New naming entry</td></tr>,
                    <tr>
                       <td class="left_header_cell">Context</td>
                       <td colspan="5">
                          {lib-forms:select-from-contexts-enum('context-ids','')}
                       </td>
                    </tr>,
                    
                    <tr>
                       <td class="left_header_cell">Name</td>
                       <td colspan="5">
                          {lib-forms:input-element('names',70,'')}
                       </td>
                    </tr>,
                    
                    <tr>
                       <td class="left_header_cell">Preferred</td>
                       <td>
                          {lib-forms:radio('preferred', '0', '')}
                       </td>
                    </tr>,

                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, '')}
                       </td>
                    </tr>,
                    
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(), '')}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), '')}
                       </td>
                    </tr>,
                    
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources',70,'')}</td>
                    </tr>
                    ) else ()
                  }

               <tr><td class="left_header_cell">New naming entry</td><td colspan="5">{lib-forms:action-button('add new name', 'action' ,'')}</td></tr>

                    <tr>
                       <td class="row-header-cell" colspan="6">Property specific properties</td>
                    </tr>
                    { 
                            <tr>
                               <td class="left_header_cell">uri</td>
                               <td colspan="5">
                                  {
                                     lib-forms:find-concept-id('property_uri','get concept',$property_uri)
                                  }
                               </td>
                            </tr>
                     }
                    <tr><td class="row-header-cell" colspan="6">Property metadata</td></tr>
                      <tr><td class="left_header_cell">Registration Authority</td><td colspan="5"> {lib-forms:make-select-registration-authority($reg-auth)} </td></tr>
                      <tr><td class="left_header_cell">Registered by</td><td colspan="5"> {lib-forms:make-select-registered_by($registered-by)} </td></tr>
                      <tr><td class="left_header_cell">Administered by</td><td colspan="5"> {lib-forms:make-select-administered_by($administered-by)} </td></tr>
                      <tr><td class="left_header_cell">Submitted by</td><td colspan="5"> {lib-forms:make-select-submitted_by($submitted-by)} </td></tr>
                      <tr><td class="left_header_cell">Administrative Status</td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                      <tr><td class="left_header_cell">Administrative Note</td><td colspan="5">{lib-forms:text-area-element('administrative-note', 5, 70,$administrative-note)}</td></tr>
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
         <p><a href="../xquery/newProperty.xquery">Create another property class</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $updating := request:get-parameter('updating','')
   let $title as xs:string := concat("Editing Property ", $id)
   let $document := lib-util:mdrElement("property",$id)
   
   let $ireg-auth := string($document/@item_registration_authority_identifier)
   let $iadministrative-note := string($document//cgMDR:administrative_note)
   let $iadministrative-status := string($document//cgMDR:administrative_status)
   let $iadministered-by := string($document//cgMDR:administered_by)
   let $isubmitted-by := string($document//cgMDR:submitted_by)
   let $iregistered-by := string($document//cgMDR:registered_by)
   let $icontext-ids := $document//cgMDR:context_identifier
   let $icountry-identifiers := $document//cgMDR:country_identifier
   let $ilanguage-identifiers := $document//cgMDR:language_identifier
   let $inames := $document//cgMDR:name
   let $idefinitions := $document//cgMDR:definition_text
   let $isources := $document//cgMDR:definition_source_reference
   let $iproperty_uri := string($document//cgMDR:reference_uri)
   let $ipreferred := fn:index-of($document//cgMDR:preferred_designation,'true')
   let $iaction := request:get-parameter('update','')

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
   let $property_uri := request:get-parameter('property_uri','')
   let $preferred := request:get-parameter('preferred','')
   let $action := request:get-parameter('update','')
   
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store Changes')
      then 
         (
         if (
               local:property
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
                     $property_uri,
                     $preferred
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
                     $preferred
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
               $preferred
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
               $ipreferred
               )
         )
       )
       
    )
       

