xquery version "1.0";

(: ~
 : Module Name:             new object class webpage and XQuery
 :
 : Module Version           1.0
 :
 : Date                     25th October 2009
 :
 : Copyright                The cangrid consortium
 :
 : Module overview          Creates reference documents
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
    
declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace xdt = "http://xdt.gate2.net/v1.0";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace util="http://exist-db.org/xquery/util";

declare function local:reference-document(
     $project_long_name as xs:string?,
     $project_short_name as xs:string?,
     $project_version as xs:string?,
     $project_description as xs:string?,
     $service_url as xs:string?,
     $file as xs:string?,
     $action as xs:string?
   ) as xs:boolean
{
   let $version := '0.1'
   let $data-identifier := lib-forms:generate-id()
   let $doc-name := $data-identifier
   let $message := lib-forms:store-annotated-model(request:get-uploaded-file('file'),request:get-uploaded-file-name('file'),'application/octet-stream') 
   let $document :=
      element openMDR:models {
           attribute annotated_model_identifier {$data-identifier},
           element openMDR:annotated_model_project_long_name{$project_long_name},
           element openMDR:annotated_model_project_short_name {$project_short_name},
           element openMDR:annotated_model_project_description {$project_description},
           element openMDR:service_url {$service_url},
           element openMDR:annotated_model_uri {concat('../data/models/documents/',request:get-uploaded-file-name('file'))},
           element openMDR:file_name {request:get-uploaded-file-name('file')},
           element openMDR:file_type {'application/octet-stream'}
           }
      
   
   let $collection := 'models'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newAnnotatedModel.xquery&amp;",$message)))
};

declare function local:input-page(
   $message as xs:string?,
   $project_long_name as xs:string?,
     $project_short_name as xs:string?,
     $project_version as xs:string?,
     $project_description as xs:string?,
     $service_url as xs:string?,
     $file as xs:string?,
     $action as xs:string?
   ) {

   let $skip-uri := substring-after($action,'delete uri entry')
   let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0

   return
   <div xmlns="http://www.w3.org/1999/xhtml">
 
      
      <table class="layout">
          <tr>
             <td>
                This form will allow you to upload the annotated model
             </td>
          </tr>
          <tr><td>
          <form name="new_reference_document" action="newAnnotatedModel.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
             <div class="section">
                    <table class="section">
                        <tr>
                           <td class="row-header-cell" colspan="6">Annotated Model</td>
                        </tr>
                        
                         <tr>
                          <td class="left_header_cell">Annotated XMI File</td><td><input type="FILE" name="file"/></td>
                         </tr>
                         
                        <tr>
                          <td class="left_header_cell">Project Long Name</td><td>{lib-forms:input-element('project_long_name', 80, $project_long_name)}</td>
                        </tr>

                        <tr>
                          <td class="left_header_cell">Project Short Name</td><td>{lib-forms:input-element('project_short_name', 80, $project_short_name)}</td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">Project Version</td><td>{lib-forms:input-element('project_version', 80, $project_version)}</td>
                        </tr>

                        <tr>
                          <td class="left_header_cell">Project Description</td><td>{lib-forms:input-element('project_description', 80, $project_description)}</td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">Service URL</td><td>{lib-forms:input-element('service_url', 80, $service_url)}</td>
                        </tr>                
                        
                        
                        
                        <tr>
                           <td class="row-header-cell" colspan="6">Store</td>
                        </tr>
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
   <div xmlns="http://www.w3.org/1999/xhtml">   
         <p>Annotated Model created</p>
         <p><a href="../edit/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../edit/newAnnotatedModel.xquery">Create annotated model</a></p>    
      </div>
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   
session:create(),

   let $project_long_name := request:get-parameter('project_long_name','')
   let $project_short_name := request:get-parameter('project_short_name','')
   let $project_version := request:get-parameter('project_version','')
   let $project_description := request:get-parameter('project_description','')
   let $service_url := request:get-parameter('service_url','')
   let $file := request:get-parameter('file','')
   let $action := request:get-parameter('update','')
   let $log := util:log-system-out('........................................')
   let $log := util:log-system-out($project_long_name)
   let $log := util:log-system-out('........................................')
   return
   
      lib-rendering:txfrm-webpage(
      $project_long_name,
      if ($action='Store')
      then 
         (
         if (
               local:reference-document
                  (
                     $project_long_name,
                     $project_short_name,
                     $project_version,
                     $project_description,
                     $service_url,
                     $file,
                     $action
                  )
            ) 
         then local:success-page()  
         else (local:input-page(
            'could not store document',
                     $project_long_name,
                     $project_short_name,
                     $project_version,
                     $project_description,
                     $service_url,
                     $file,
                     $action
                  )
               )
         )
      else local:input-page
               (
               '',
                  $project_long_name,
                  $project_short_name,
                  $project_version,
                  $project_description,
                  $service_url,
                  $file,
                  $action
               )
         )
