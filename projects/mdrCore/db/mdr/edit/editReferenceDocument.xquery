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

declare function local:reference-document(
   $language as xs:string?,
   $title as xs:string?,
   $description as xs:string?,
   $file as xs:string?,
   $provided-by as xs:string?,
   $action as xs:string?
   ) as xs:boolean
{
   let $version := '0.1'
   let $data-identifier := lib-forms:generate-id()
   let $doc-name := $data-identifier
   let $message := lib-forms:store-reference-document(request:get-uploaded-file('file'),request:get-uploaded-file-name('file'),'application/octet-stream') 
   let $document :=
      element openMDR:Reference_Document {
           attribute reference_document_identifier {$data-identifier},
           element openMDR:reference_document_language_identifier {$language},
           element openMDR:reference_document_title {$title},
           element openMDR:reference_document_type_description {$description},
           element openMDR:provided_by {$provided-by},
           element openMDR:reference_document_uri {concat('../data/reference_document/documents/',request:get-uploaded-file-name('file'))},
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
   $action as xs:string?
   ) {
 let $skip-uri := substring-after($action,'delete uri entry')
   let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0

   return
   <div xmlns="http://www.w3.org/1999/xhtml">
 
      
      <table class="layout">
          <tr>
             <td>
                This form will allow you to edit the reference document in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="edit_reference_document" action="editReferenceDocument.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
             <div class="section">
                    <table class="section">
                        <tr>
                           <td class="row-header-cell" colspan="6">Reference Document</td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">Title</td><td>{lib-forms:input-element('title', 80, $title)}</td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">File</td><td><input type="FILE" name="file" value="{$file}"/></td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">Language</td><td>{lib-forms:select-from-simpleType-enum('Language_Identifier','language', false(), $language)}</td>
                        </tr>

                        <tr>
                          <td class="left_header_cell">Document Type</td><td>{lib-forms:select-from-simpleType-enum('Reference_Document_Type','description', true(), $description)}</td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">Providing Organization</td><td>{lib-forms:input-element('provided-by', 80, $provided-by)}</td>
                        </tr>                

                        <tr>
                           <td class="row-header-cell" colspan="6">Store</td>
                        </tr>
                        <tr>
                            <td class="left_header_cell"></td><td><input type="submit" name="update" value="Save"/></td>
                            <td colspan="4"><input type="submit" name="update" value="Clear"/></td>
                            
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
   
   let $ilanguage := string($element//openMDR:reference_document_language_identifier)
   let $ititle := string($element//openMDR:reference_document_title)
   let $idescription := string($element//openMDR:reference_document_type_description)
   let $ifile := string($element//openMDR:reference_document_uri)
   let $iprovided-by := string($element//openMDR:provided_by)
   let $iaction := request:get-parameter('update','')
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Save')
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
                     $action
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
                     $action
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
                 $iaction
               )
         )
