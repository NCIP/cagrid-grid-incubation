xquery version "1.0";

(: ~
 : Module Name:             new DataElementConcept webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     25 March 2009
 :
 : Copyright                caGrid
 :
 : Module overview          Creates a new DataElementConcept
 :
 :)
 

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

declare function local:DataElementConcept(
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
   $preferred as xs:string?
   ) as xs:string
{
   let $version := '0.1'
   
   let $object_class_id := request:get-parameter('object_class_id','')
   let $property_id := request:get-parameter('property_id','')
   let $conceptual_domain_id := request:get-parameter('conceptual_domain_id','')
   let $object_class_uri := request:get-parameter('object_class_uri','')
   let $property_uri := request:get-parameter('property_uri','')

   let $data-identifier-oc := lib-forms:generate-id()
   let $data-identifier-pr := lib-forms:generate-id()
 
   let $full-identifier-oc := concat($reg-auth, '_', $data-identifier-oc, '_', $version)
   let $full-identifier-pr := concat($reg-auth, '_', $data-identifier-pr, '_', $version)

   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   
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
                    element openMDR:data_element_concept_conceptual_domain {$conceptual_domain_id},
                    element openMDR:data_element_concept_object_class {
                      if ($object_class_id) then ($object_class_id) else ($full-identifier-oc)
                    },
                    element openMDR:data_element_concept_property {
                      if ($property_id) then ($property_id) else ($full-identifier-pr)
                    }
    )

let $object-class := 
   (
   element openMDR:Object_Class {
        lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier-oc,$version),
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
        element openMDR:reference_uri {$object_class_uri}
        }
   )
   
let $property := 
   (
    element openMDR:Property {
        lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier-pr,$version),
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
        element openMDR:reference_uri {$property_uri}
        }
   )

   
   (: compose the document :)
   let $document :=
      element openMDR:Data_Element_Concept {
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier,$version),
            $content
           }
      
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
   $reg-auth as xs:string?,
   $administrative-note  as xs:string?,
   $administrative-status  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by  as xs:string?,
   $registered-by  as xs:string?,
   $context-ids as xs:string*,
   $country-identifiers as xs:string*,
   $language-identifiers as xs:string*,
   $names as xs:string*,
   $definitions as xs:string*,
   $sources as xs:string*,
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
                This form will allow you to create a new DataElementConcept in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="new_DataElementConcept" action="newDataElementConcept.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
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
               <tr>
                  <td class="left_header_cell">Object Class URI</td>
                  <td align="left" colspan="2">
                     {lib-forms:find-concept-id('object_class_uri','get object class concept',request:get-parameter('object_class_uri',''))}
                  </td>
              </tr>
              <tr>
                  <td class="left_header_cell"> </td>
                  <td align="left" colspan="2"><i> or select existing </i> 
                  {lib-forms:make-select-admin-item('object_class','object_class_id', request:get-parameter('object_class_id',''))}
                  </td>
               </tr>
               <tr>
                  <td class="left_header_cell">Property URI</td>
                  <td align="left" colspan="2">
                     {lib-forms:find-concept-id('property_uri','get property concept',request:get-parameter('property_uri',''))}
                  </td>
              </tr>
              <tr>
                  <td class="left_header_cell"> </td>
                  <td align="left" colspan="2"><i> or select existing </i> 
                     {lib-forms:make-select-admin-item('property','property_id', request:get-parameter('property_id',''))}
                  </td>
               </tr>
               
                <tr>
                  <td class="left_header_cell">Conceptual Domain</td>
                  <td align="left" colspan="2">
                     {lib-forms:make-select-admin-item('conceptual_domain','conceptual_domain_id', request:get-parameter('conceptual_domain_id',''))}
                  </td>
               </tr>
            </table>
                     
                <table class="section">
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
         <p>DataElementConcept class created</p>
         <p><a href="../xquery/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../xquery/newDataElementConcept.xquery">Create another DataElementConcept</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $title as xs:string := "Creating a New DataElementConcept"
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
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store')
      then 
         (
         if (
               local:DataElementConcept
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
                     $preferred
                  ) = 'stored'
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
               $action,
               $preferred
               )
         )

