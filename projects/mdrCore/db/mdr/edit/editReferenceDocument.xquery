xquery version "1.0";
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

declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace xdt = "http://xdt.gate2.net/v1.0";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace datetime = "http://exist-db.org/xquery/datetime";

declare function local:reference-document(
   $language as xs:string?,
   $title as xs:string?,
   $description as xs:string?,
   $file as xs:string?,
   $provided-by as xs:string?,
   $action as xs:string?,
   $id as xs:string?,
   $version as xs:float?,
   $reg-auth  as xs:string?,
   $registered-by  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by as xs:string?,
   $administrative-status  as xs:string?,
   $registration_status  as xs:string?
   ) as xs:boolean
{
   let $idprefix := 'cagrid.org'
  (: let $data-identifier := lib-forms:generate-id():)
   (:let $data-identifier := $id:)
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'_'),'_')
   let $doc-name := concat($idprefix,'_',$data-identifier,'_',$version,'_',request:get-uploaded-file-name('file'))
   let $document-name := concat($idprefix,'_',$data-identifier,'_',$version)   
   let $message := lib-forms:store-reference-document(request:get-uploaded-file('file'),$doc-name,'application/octet-stream') 
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
   
   let $document :=
      element openMDR:Reference_Document {
           attribute item_registration_authority_identifier {$reg-auth},
           attribute reference_document_identifier {$document-name},
           attribute data_identifier {$data-identifier},
           attribute version {$version},
           
           lib-make-admin-item:administration-record('',$administrative-status,$creation-date,$registration_status),
           lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
           
           element openMDR:reference_document_language_identifier {$language},
           element openMDR:reference_document_title {$title},
           element openMDR:reference_document_type_description {$description},
           element openMDR:provided_by {$provided-by},
           element openMDR:reference_document_uri {concat('../data/reference_document/documents/',$doc-name)},
           element openMDR:file_name {request:get-uploaded-file-name('file')},
           element openMDR:file_type {'application/octet-stream'}
           }
   let $collection := 'reference_document'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newReferenceDocument.xquery&amp;",$message)))
};

declare function local:input-page(
   $message as xs:string?,
   $language as xs:string?,
   $title as xs:string?,
   $description as xs:string?,
   $file as xs:string?,
   $provided-by as xs:string?,
   $action as xs:string?,
   $id as xs:string?,
   $version as xs:float?,
   $reg-auth  as xs:string?,
   $registered-by  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by as xs:string?,
   $administrative-status  as xs:string?,
   $registration_status  as xs:string?
   ) {
    
   let $proposedNextVersion := $version + 0.1
   let $proposedReleaseVersion := ceiling($proposedNextVersion)
   
   let $skip-uri := substring-after($action,'delete uri entry')
   let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0
      
   return
   <div xmlns="http://www.w3.org/1999/xhtml">
      <table class="layout">
          <tr><td>This form will allow you to edit the reference document in the metadata repository</td></tr>
          
          <form name="edit_reference_document" action="editReferenceDocument.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
            {lib-forms:hidden-element('id',$id)}            
            <div class="section">
                    <tr><td class="row-header-cell" colspan="6">Standard Administered Item Metadata</td></tr>
                     <tr><td>
                     <div class="tabber">
                     <div id="validate" class="tabbertab">
                     <h2>Administered Item Metadata</h2>     
                     <table class="section">
                        <tr>
                              <tr><td class="left_header_cell">Registration Authority <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registration-authority($reg-auth)} </td></tr>
                              <tr><td class="left_header_cell">Existing Version</td><td colspan="5"> {$version}{lib-forms:radio('version',string($version),'true')} </td></tr>
                              <tr><td class="left_header_cell">Proposed/Release Version</td><td colspan="5"> {round-half-to-even(xs:float($proposedNextVersion),2)}{lib-forms:radio('proposedNextVersion',string($proposedNextVersion),'true')}  {$proposedReleaseVersion}{lib-forms:radio('proposedNextVersion',string($proposedReleaseVersion),'false')}</td></tr>
                              <tr><td class="left_header_cell">Registered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registered_by($registered-by)} </td></tr>
                              <tr><td class="left_header_cell">Administered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-administered_by-nameAndOrg($administered-by)} </td></tr>
                              <tr><td class="left_header_cell">Submitted by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-submitted_by-nameAndOrg($submitted-by)} </td></tr>
                              <tr><td class="left_header_cell">Administrative Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                              <tr><td class="left_header_cell">Registration Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Registration_Status','registration-status', false(), $registration_status)}</td></tr>
                        </tr>
                      </table>
                     <table class="section">
                           <tr><td class="row-header-cell" colspan="6">Reference Document</td></tr>
                           <tr><td class="left_header_cell">Title <font color="red">*</font></td><td>{lib-forms:input-element('title', 80, $title)}</td></tr>
                           <tr><td class="left_header_cell">File <font color="red">*</font></td><td><input id="file" type="FILE" name="file" value="{$file}"/></td></tr>
                           <tr><td class="left_header_cell">Language <font color="red">*</font></td><td>{lib-forms:select-from-simpleType-enum('Language_Identifier','language', false(), $language)}</td></tr>
                           <tr><td class="left_header_cell">Document Type <font color="red">*</font></td><td>{lib-forms:select-from-simpleType-enum('Reference_Document_Type','description', true(), $description)}</td></tr>
                           <tr><td class="left_header_cell">Providing Organization <font color="red">*</font></td><td>{lib-forms:input-element('provided-by', 80, $provided-by)}</td></tr>                
                           <tr><td class="row-header-cell" colspan="6">Store</td></tr>
                            
                            <tr>
                               <td class="left_header_cell"></td>
                               <td><input type="submit" name="update" value="Save Changes" onClick="return checkFile(this)"/></td>
                               <td colspan="4">
                                    <input type="button" name="update" value="Cancel"  onClick= "{concat("location.href='../web/reference_document.xquery?compound_id=", $id, "';")}" />
                               </td>
                            </tr>
                      </table>
                      </div>
                      </div>
                      </td></tr>
                 </div>
             </form>
           
          <tr><td>{$message}</td></tr>
        </table>
     </div>
};

declare function local:success-page() 
{
   let $calling-page := request:get-parameter("calling-page","")
   return
   <div xmlns="http://www.w3.org/1999/xhtml">   
         <p>Reference Document created</p>
         <p><a href="../edit/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../edit/newReferenceDocument.xquery">Create reference document</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $title := request:get-parameter('title','')
   let $language := request:get-parameter('language','')
   let $description := request:get-parameter('description','')
   let $file := request:get-parameter('file','')
   let $provided-by := request:get-parameter('provided-by','')
   let $action := request:get-parameter('update','')
   
   let $element := lib-util:mdrElement("reference_document",$id)
   let $iref-id := string($element/@reference_document_identifier)
   let $log := util:log-system-out('printing id printing id printing id')
   let $log := util:log-system-out($iref-id)
   
   let $heading := concat('Editing Reference Document ',$id)
   
   let $ilanguage := string($element//openMDR:reference_document_language_identifier)
   let $ititle := string($element//openMDR:reference_document_title)
   let $idescription := string($element//openMDR:reference_document_type_description)
   let $ifile := string($element//openMDR:reference_document_uri)
   let $iprovided-by := string($element//openMDR:provided_by)
   let $iaction := request:get-parameter('update','')
   
   let $iversion := string($element/@version)
   let $version := $iversion
   let $proposedNextVersion := request:get-parameter('proposedNextVersion',$iversion)
   let $version := round-half-to-even(xs:float($proposedNextVersion),2)
   
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $registration_status := request:get-parameter('registration-status','')
   
   let $ireg-auth := string($element/@item_registration_authority_identifier)
   let $iadministrative-note := string($element/openMDR:administered_item_administration_record/openMDR:administrative_note)
   let $iadministrative-status := string($element/openMDR:administered_item_administration_record/openMDR:administrative_status)
   let $iadministered-by := string($element//openMDR:administered_by)
   let $isubmitted-by := string($element//openMDR:submitted_by)
   let $iregistered-by := string($element//openMDR:registered_by)
   let $iregistration_status := string($element/openMDR:administered_item_administration_record/openMDR:registration_status)
   let $registration_status := request:get-parameter('registration-status','')
   
   return
   
      lib-rendering:txfrm-webpage(
      $heading,
      if ($action='Save Changes')
      then 
         (
         if (
               local:reference-document
                  (
                     $language,
                     $title,
                     $description,
                     $file,
                     $provided-by,
                     $action,
                     $id,
                     $version,
                     $reg-auth,
                     $registered-by,
                     $administered-by,
                     $submitted-by,
                     $administrative-status,
                     $registration_status
                  )
            ) 
         then local:success-page()  
         else (local:input-page(
            'could not store document',
                     $language,
                     $title,
                     $description,
                     $file,
                     $provided-by,
                     $action,
                     $iref-id,
                     $version,
                     $reg-auth,
                     $registered-by,
                     $administered-by,
                     $submitted-by,
                     $administrative-status,
                     $registration_status
                  )
               )
         )
      else local:input-page
               (
               '',
                 $ilanguage,
                 $ititle,
                 $idescription,
                 $ifile,
                 $iprovided-by,
                 $iaction,
                 $iref-id,
                 $version,
                 $ireg-auth,
                 $iregistered-by,
                 $iadministered-by,
                 $isubmitted-by,
                 $iadministrative-status,
                 $iregistration_status
               )
         )
