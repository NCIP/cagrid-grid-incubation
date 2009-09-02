xquery version "1.0";


(: ~
 : Module Name:             edit DataElement webpage and XQuery
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

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";


declare function local:DataElement(
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
   $data_element_concept_id as xs:string?,
   $value_domain_id as xs:string?,
   $example as xs:string?,
   $precision as xs:string?
) as xs:string
{
   let $version := lib-forms:substring-after-last($id,'-')
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'-'),'-')
   let $doc-name := concat($id,'.xml')

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
                    element openMDR:data_element_precision {$precision},
                    element openMDR:representing {$value_domain_id},
                    element openMDR:expressing {$data_element_concept_id},
                    element openMDR:exemplified_by {
                        element openMDR:data_element_example_item {$example} 
                        }
                    )

    (:
        DEC:
        https://localhost:8443/exist/rest/db/mdr/edit/editDataElementConcept.xquery?id=cagrid.org-07faf2af-4fbf-4b7e-97d9-0947fe63334b-0.1
        
        VD:
        https://localhost:8443/exist/rest/db/mdr/edit/editValueDomain.xquery?id=cagrid.org-0785fe37-45cf-4f4f-ab64-b83c4ddf241b-0.1
    :) 
   (: compose the document :)
   let $document :=
        element openMDR:Data_Element {
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier,$version),
            $content
           }
      
   let $collection := 'data_element'
   let $message := lib-forms:store-document($document) 
   
   return
    if(lib-forms:store-document($document)='stored')
          then 'stored'
          else ( response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=editDataElement.xquery&amp;","Could not store data element"))) )
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
   $data_element_concept_id as xs:string?,
   $value_domain_id as xs:string?,
   $example as xs:string?,
   $precision as xs:string?
   ) {
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return

   <div xmlns="http://www.w3.org/1999/xhtml">
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to edit a DataElement in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="edit_dataelement" action="editDataElement.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
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
                     
                     
           <table class="layout">
              <tr>
                  <td class="left_header_cell">Data Element Concept</td>
                  <td align="left" colspan="2">
                  {lib-forms:make-select-admin-item('data_element_concept','data_element_concept_id', $data_element_concept_id)}
                  </td>
               </tr>
               <tr>
                  <td class="left_header_cell">Value Domain</td>
                  <td align="left" colspan="2">
                     {lib-forms:make-select-admin-item('value_domain','value_domain_id', $value_domain_id)}
                  </td>
              </tr>
              <tr><td class="left_header_cell">Example</td><td colspan="2">{lib-forms:text-area-element('example', 5, 70, request:get-parameter('example',''))}</td></tr>
              <tr><td class="left_header_cell">Precision</td><td colspan="2">{lib-forms:input-element('precision', 70,request:get-parameter('precision',''))}</td></tr>
       
                    
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
         <p>DataElement modified</p>
         <p><a href="../xquery/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../xquery/newDataElement.xquery">Create another DataElement class</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $updating := request:get-parameter('updating','')
   let $title as xs:string := concat("Editing DataElement ", $id)
   let $element := lib-util:mdrElement("data_element",$id)
   let $log := util:log-system-err($element)

   let $action := request:get-parameter('update','')
   let $log := util:log-system-err($action)
   
   let $ireg-auth := string($element/@item_registration_authority_identifier)
   let $log := util:log-system-err($ireg-auth)
   
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

   let $log := util:log-system-err($iregistered-by)

   let $idata_element_concept_id := $element//openMDR:expressing/text()
   let $ivalue_domain_id := $element//openMDR:representing/text()
   let $iexample := string($element//openMDR:data_element_example_item/text())
   let $iprecision := $element//openMDR:data_element_precision/text()
   
    let $log := util:log-system-err($idata_element_concept_id)
    let $log := util:log-system-err($ivalue_domain_id)
    let $log := util:log-system-err($iexample)
    let $log := util:log-system-err($iprecision)

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
   
   let $data_element_concept_id := request:get-parameter('data_element_concept_id','')
   let $value_domain_id := request:get-parameter('value_domain_id','')
   let $example := request:get-parameter('example','')
   let $precision := request:get-parameter('precision','')
      
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store Changes')
      then 
         (
         if (
               local:DataElement
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
                     $data_element_concept_id,
                     $value_domain_id,
                     $example,
                     $precision
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
                     $data_element_concept_id,
                     $value_domain_id,
                     $example,
                     $precision                     
                  )
               )
         )
      else (
         if ($updating ='updating')
         then (
               local:input-page(
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
               $data_element_concept_id,
               $value_domain_id,
               $example,
               $precision               
               )
         ) else (
               local:input-page(
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
               $idata_element_concept_id,
               $ivalue_domain_id,
               $iexample,
               $iprecision               
               )
         )
       )
       
    )
       

