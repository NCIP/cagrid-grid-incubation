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
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

declare function local:find-concept-id(
         $control-name as xs:string, 
         $form-name as xs:string,
         $return-value-to as xs:string,
         $button-label as xs:string
         ) as element()*
{
      lib-forms:hidden-element('destination-page',$return-value-to),
      lib-forms:hidden-element('return-parameter',$control-name),
      <input type="submit" 
         class="cgButton" 
         value="{$button-label}"
         onclick="document.{$form-name}.action='LexBIG-form.xquery'"/>   
};

declare function local:property(
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
   let $version := '0.1'
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '-', $data-identifier, '-', $version)
   let $doc-name := concat($new-identifier,'.xml')

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
                        element cgMDR:preferred_designation {(xs:int($preferred) = xs:int($pos))},
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
      else response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newProperty.xquery&amp;",$message)))
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
   $property_uri as xs:string?,
   $action as xs:string?,
   $preferred as xs:string?
   ) {
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return
   <div>
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to create a new property in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="new_property" action="newProperty.xquery" method="post" class="cancergridForm" enctype="multipart/form-data">
             <div class="section">
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
                          {lib-forms:make-select-admin-item('context','contexts',$context-ids[$pos])}
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
                          {lib-forms:radio('preferred', $pos, ($preferred = $pos))}
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
                    
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button(concat('delete naming entry ',$pos), 'action' ,0)}</td></tr>
                    ),
                     
                    <tr><td class="row-header-cell" colspan="6">New naming entry</td></tr>,
                    <tr>
                       <td class="left_header_cell">Context</td>
                       <td colspan="5">
                          {lib-forms:make-select-admin-item('context','contexts','')}
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
                  }

               <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add new name', 'action' ,0)}</td></tr>

                    <tr>
                       <td class="row-header-cell" colspan="6">Property class specific properties</td>
                    </tr>
                    
                    
                    {
                         
                            <tr>
                               <td class="left_header_cell">uri</td>
                               <td colspan="5">
                                  {
                                     lib-forms:input-element('property_uri',70, request:get-parameter('property_uri','')),
                                     local:find-concept-id('property_uri','new_property','newProperty.xquery','get concept')
                                  }
                               </td>
                            </tr>
                      
                     }
                    <tr><td class="row-header-cell" colspan="6">Property metadata</td></tr>
                      <tr><td class="left_header_cell">Registration Authority</td><td colspan="5"> {lib-forms:make-select-registration-authority('')} </td></tr>
                      <tr><td class="left_header_cell">Registered by</td><td colspan="5"> {lib-forms:make-select-registered_by('')} </td></tr>
                      <tr><td class="left_header_cell">Administered by</td><td colspan="5"> {lib-forms:make-select-administered_by('')} </td></tr>
                      <tr><td class="left_header_cell">Submitted by</td><td colspan="5"> {lib-forms:make-select-submitted_by($submitted-by)} </td></tr>
                      <tr><td class="left_header_cell">Administrative Status</td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                      <tr><td class="left_header_cell">Administrative Note</td><td colspan="5">{lib-forms:text-area-element('administrative-note', 5, 70,$administrative-note)}</td></tr>
                      <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Store"/></td><td colspan="4"><input type="submit" name="update" value="Clear"/></td></tr>    
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
         <p>Property class created</p>
         <p><a href="../xquery/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../xquery/newProperty.xquery">Create another property class</a></p>    
      </div>
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

   session:create(),
   let $title as xs:string := "Creating a New Property"
   let $reg-auth := request:get-parameter('registration-authority',())
   let $administrative-note := request:get-parameter('administrative-note',())
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $context-ids := request:get-parameter('contexts',())
   let $country-identifiers := request:get-parameter('country-identifiers','')
   let $language-identifiers := request:get-parameter('language-identifiers','')
   let $names := request:get-parameter('names',())
   let $definitions := request:get-parameter('definitions',())
   let $sources := request:get-parameter('sources',())
   let $property_uri := request:get-parameter('property_uri',())
   let $preferred := request:get-parameter('preferred',())
   let $action := request:get-parameter('update','')
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store')
      then 
         (
         if (
               local:property
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
                     $property_uri,
                     $preferred
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
                     $property_uri,
                     $action,
                     $preferred
                  )
               )
         )
      else local:input-page
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
               $property_uri,
               $action,
               $preferred
               )
         )

