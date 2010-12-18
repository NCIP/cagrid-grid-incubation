xquery version "1.0";

(: ~
 : Module Name:             edit DataElementConcept webpage and XQuery
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

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request"; 
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";

declare function local:data_element_concept(
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
   $object_class_id as xs:string?,
   $property_id as xs:string?,
    (: added this so that the selected version is saved in the xml:)
   $proposedVersion as xs:float?,
   $registration_status as xs:string
   
   ) as xs:string
   {
   let $version := '0.1'
   let $object_class_uri := request:get-parameter('object_class_uri','')
   let $property_uri := request:get-parameter('property_uri','')

   let $data-identifier-oc := substring-after(lib-forms:substring-before-last($id,'_'),'_')   
   let $data-identifier-pr := substring-after(lib-forms:substring-before-last($id,'_'),'_')
   let $full-identifier-oc := concat($reg-auth, '_', $data-identifier-oc, '_', $version)
   let $full-identifier-pr := concat($reg-auth, '_', $data-identifier-pr, '_', $version)
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'_'),'_')
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
 
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
                    
                    element openMDR:data_element_concept_conceptual_domain {$conceptual_domain_id},
                    element openMDR:data_element_concept_object_class {$object_class_id},
                    element openMDR:data_element_concept_property {$property_id}
                    )
                    
                    
let $object-class := 
   (
   element openMDR:Object_Class {
        lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier-oc,$version),
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
        element openMDR:reference_uri {$object_class_uri}
        }
   )
   
let $property := 
   (
    element openMDR:DataElementConcept {
        lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier-pr,$version),
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
        element openMDR:reference_uri {$property_uri}
        }
   )
   
   (: compose the document :)
   let $document :=
      element openMDR:Data_Element_Concept {
            (:11111111111111 adding proposed version for version:)
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier,string($proposedVersion)),
            $content
           }
   let $collection := 'data_element_concept'
   let $message := lib-forms:store-document($document) 
   
   return
   if ((
      if ($object_class_id > '')
      then 'stored'
      else lib-forms:store-document($object-class))='stored' 
      )
   then (
      if ((
         if ($property_id > '')
         then 'stored'
         else lib-forms:store-document($property))='stored')
      then (
          if(lib-forms:store-document($document)='stored')
          then 'stored'
          else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;","Could not store data element concept"))) )
          )
      else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;","Could not store property"))) )
   )
   else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;","Could not store object class"))) )

};

declare function local:input-page(
   $message as xs:string?,
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
   $action as xs:string?,
   
   $conceptual_domain_id as xs:string?,
   $object_class_id as xs:string?,
   $property_id as xs:string?,
    (:11111111111111111:)
   $version as xs:float?,
   $registration_status as xs:string
   ) 
   
   {
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return
    <div xmlns="http://www.w3.org/1999/xhtml">
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to Edit a DataElementConcept in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="edit_DataElementConcept" action="editDataElementConcept.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
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
               <tr>
                  <td class="left_header_cell">Object Class URI <font color="red">*</font></td>
                  <td align="left" colspan="2">
                    <a href="../edit/newObjectClass.xquery">Create a New Object Class</a>
                  </td>
              </tr>
              <tr>
                  <td class="left_header_cell"> </td>
                  <td align="left" colspan="2"><i> or select existing </i> 
                  {
                    if($object_class_id eq "Cancel")  then (
                        lib-forms:make-select-form-admin-item-edit-false('object_class','object_class_id', session:get-attribute("object_class_id"),'edit_DataElementConcept', 'Change Relationship'),
                        session:set-attribute("object_class_id", "") )
                    
                    else if($object_class_id != "")  then (
                        session:set-attribute("object_class_id", $object_class_id),
                        lib-forms:make-select-form-admin-item-edit-false('object_class','object_class_id', $object_class_id,'edit_DataElementConcept', 'Change Relationship'))
                    
                    else(
                       lib-forms:make-select-form-admin-item-edit-false('object_class','object_class_id', $object_class_id,'edit_DataElementConcept', 'Select Relationship') 
                  )
                  }
                  </td>
               </tr>
               <tr>
                  <td class="left_header_cell">Property URI <font color="red">*</font></td>
                  <td align="left" colspan="2">
                        <a href="../edit/newProperty.xquery">Create a New Property</a>
                  </td>
              </tr>
              <tr>
                  <td class="left_header_cell"> </td>
                  <td align="left" colspan="2"><i> or select existing </i> 
                   {
                    if($property_id eq "Cancel")  then (
                        lib-forms:make-select-form-admin-item-edit-false('property','property_id', session:get-attribute("property_id"),'edit_DataElementConcept', 'Change Relationship'),
                        session:set-attribute("property_id", "") )
                    
                    else if($property_id != "")  then (
                        session:set-attribute("property_id", $property_id),
                        lib-forms:make-select-form-admin-item-edit-false('property','property_id', $property_id,'edit_DataElementConcept', 'Change Relationship'))
                    
                    else(
                       lib-forms:make-select-form-admin-item-edit-false('property','property_id', $property_id,'edit_DataElementConcept', 'Select Relationship') 
                  )
                  }
                  </td>
               </tr>
               
                <tr>
                  <td class="left_header_cell">Conceptual Domain <font color="red">*</font></td>
                  <td align="left" colspan="2">
                   {
                    if($conceptual_domain_id eq "Cancel")  then (
                        lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id', session:get-attribute("conceptual_domain_id"),'edit_DataElementConcept', 'Change Relationship'),
                        session:set-attribute("conceptual_domain_id", "") )
                    
                    else if($conceptual_domain_id != "")  then (
                        session:set-attribute("conceptual_domain_id", $conceptual_domain_id),
                        lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id', $conceptual_domain_id,'edit_DataElementConcept', 'Change Relationship'))
                    
                    else(
                       lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id', $conceptual_domain_id,'edit_DataElementConcept', 'Select Relationship') 
                  )
                  }
                  </td>
               </tr>
            </table>
                     
                <table class="section">
                 <tr><td class="row-header-cell" colspan="6">Store</td></tr>
                      <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Save Changes" onClick="return validate_dataelementconceptedit ()"/></td>
                      <td colspan="4"><input type="button"  name="update" value="Cancel" 
                              onClick= "{concat("location.href='../web/data_element_concept.xquery?compound_id=", $id, "';")}" /></td>
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
                    Data Element Concept <b>{request:get-parameter('names',())}</b> modified
                 </td>
              </tr>
              <tr>
                <td><a href='../web/contents.xquery?start=1&amp;extent=5&amp;previous=1&amp;next=6&amp;last=1&amp;count=0&amp;recordlimit=0&amp;letter=&amp;type=data_element_concept'>View existing Data Element Concepts</a>
                </td>
              </tr>
                 <tr>
                <td><a href="newDataElementConcept.xquery">Create New Data Element Concept</a>
                </td>
              </tr>
            </table>
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   
   let $updating := request:get-parameter('updating','')
   let $title as xs:string := "Editing DataElementConcept"
   let $element := lib-util:mdrElement("data_element_concept",$id)   
   let $action := request:get-parameter('update','')

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
   let $object_class_id := request:get-parameter('object_class_id','')
   let $property_id := request:get-parameter('property_id','')
   (:
   let $property_uri := request:get-parameter('property_uri','')
   let $object_class_uri := request:get-parameter('object_class_uri',())
   :)
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
   
    let $iconceptual_domain_id := $element//openMDR:data_element_concept_conceptual_domain   
    let $iobject_class_id:=$element//openMDR:data_element_concept_object_class
    let $iproperty_id:=$element//openMDR:data_element_concept_property
    let $iobject_class_uri :=$element//openMDR:object_class_qualifier
    let $iproperty_uri :=$element//openMDR:property_qualifier
    
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
               local:data_element_concept
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
                     $object_class_id,
                     $property_id,
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
                     $preferred,
                     $action,
                     $conceptual_domain_id,
                     $object_class_id,
                     $property_id,
                     (: added this so that the version gets saved:)
                     $iversion,
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
               $preferred,
               $action,
               $conceptual_domain_id,
               $object_class_id,
               $property_id,
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
               $ipreferred,
               $action,
               $iconceptual_domain_id,
               $iobject_class_id,
               $iproperty_id,
               (: added this so that the version gets saved:)
                     $iversion,
                     $registration_status
               )
         )
       )
       
    )
       

