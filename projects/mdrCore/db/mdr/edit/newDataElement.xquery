xquery version "1.0";

(: ~
 : Module Name:             new DataElement webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     25 March 2009
 :
 : Copyright                caGrid
 :
 : Module overview          Creates a new DataElement
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
declare namespace request="http://exist-db.org/xquery/request"; 
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace datetime = "http://exist-db.org/xquery/datetime";

declare function local:DataElement(
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
   $registration_status as xs:string
   ) as xs:string
{
   let $version := '0.1'
   
   let $data_element_concept_id := request:get-parameter('data_element_concept_id','')
   let $value_domain_id := request:get-parameter('value_domain_id','')
   let $example:= request:get-parameter('example','')
   let $precision := request:get-parameter('precision','')

   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
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
                    element openMDR:data_element_precision {$precision},
                    element openMDR:representing {$value_domain_id},
                    element openMDR:expressing {$data_element_concept_id},
                    element openMDR:exemplified_by {
                    element openMDR:data_element_example_item {$example} }
    )
   
   (: compose the document :)
   let $document :=
      element openMDR:Data_Element {
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier,$version),
            $content
           }
      
   return
          if(lib-forms:store-document($document)='stored')
          then 'stored'
          else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElement.xquery&amp;","Could not store data element"))) )
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
   $preferred as xs:string?,
   $registration_status as xs:string?
   ) {
   (:1111111111111111111:)
   let $version := '0.1'
   
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return
   <div xmlns="http://www.w3.org/1999/xhtml">
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to create a new DataElement in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="new_DataElement" action="newDataElement.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
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
                      (:11111111111111111:)
                     $version,
                     $registration_status
                     )}
                     
               <table class="layout">
              <tr>
                  <td class="left_header_cell">Data Element Concept</td>
                  <td align="left" colspan="2">
                  {
                    if(request:get-parameter('data_element_concept_id','') eq "Cancel")  then (
                        lib-forms:make-select-form-admin-item('data_element_concept','data_element_concept_id', session:get-attribute("data_element_concept_id"),'new_DataElement', 'Change Relationship'),
                        session:set-attribute("data_element_concept_id", "") )
                    
                    else if(request:get-parameter('data_element_concept_id','') != "")  then (
                        session:set-attribute("data_element_concept_id", request:get-parameter('data_element_concept_id','')),
                        lib-forms:make-select-form-admin-item('data_element_concept','data_element_concept_id', request:get-parameter('data_element_concept_id',''),'new_DataElement', 'Change Relationship'))
                    
                    else(
                       lib-forms:make-select-form-admin-item('data_element_concept','data_element_concept_id', request:get-parameter('data_element_concept_id',''),'new_DataElement', 'Select Relationship') 
                  )
                  }
                  </td>
               </tr>
               <tr>
                  <td class="left_header_cell">Value Domain</td>
                  <td align="left" colspan="2">
                  {
                    if(request:get-parameter('value_domain_id','') eq "Cancel")  then (
                        lib-forms:make-select-form-admin-item('value_domain','value_domain_id', session:get-attribute("value_domain_id"),'new_DataElement', 'Change Relationship'),
                        session:set-attribute("value_domain_id", "") )
                    
                    else if(request:get-parameter('value_domain_id','') != "")  then (
                        session:set-attribute("value_domain_id", request:get-parameter('value_domain_id','')),
                        lib-forms:make-select-form-admin-item('value_domain','value_domain_id', request:get-parameter('value_domain_id',''),'new_DataElement', 'Change Relationship'))
                    
                    else(
                       lib-forms:make-select-form-admin-item('value_domain','value_domain_id', request:get-parameter('value_domain_id',''),'new_DataElement', 'Select Relationship') 
                  )
                  }
                  </td>
              </tr>
              <tr><td class="left_header_cell">Example</td><td colspan="2">{lib-forms:text-area-element('example', 5, 70, request:get-parameter('example',''))}</td></tr>
              <tr><td class="left_header_cell">Precision</td><td colspan="2">{lib-forms:input-element('precision', 70,request:get-parameter('precision',''))}</td></tr>
            </table>
                     
                <table class="section">
                      <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Store" onClick="return validate_adminItems ()"/></td>
                      <td colspan="4"><input type="button" name="update" value="Clear" onClick="this.form.reset()"/></td></tr>    
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
         <p>DataElement class <b>{request:get-parameter('names',())}</b> created</p>
         <p><a href="../edit/maintenance.xquery">Return to Maintenance Menu</a></p>    
         <p><a href="../edit/newDataElement.xquery">Create another DataElement</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $title as xs:string := "Creating a New DataElement"
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
   let $registration_status := request:get-parameter('registration_status','')
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store')
      then 
         (
         if (
               local:DataElement
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
                     $registration_status
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
                     $preferred,
                     $registration_status
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
               $preferred,
               $registration_status
               )
         )

