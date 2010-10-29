xquery version "1.0";

(: ~
 : Module Name:             new object class webpage and XQuery
 :
 : Module Version           1.0
 :
 : Date                     25th October 2009
 :
 : Author                   Sreekant Lalkota
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
    
import module namespace 
lib-qs="http://www.cagrid.org/xquery/library/query_service" 
at "../connector/m-lib-qs.xquery";  
      
declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace xdt = "http://xdt.gate2.net/v1.0";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace UML ="omg.org/UML1.3";
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace q="http://cagrid.org/schema/query";
declare namespace c="http://cagrid.org/schema/config";
declare namespace rs="http://cagrid.org/schema/result-set";
declare namespace xlink="http://www.w3.org/1999/xlink";
declare namespace x="http://cagrid.org/schema/result-set";

(: Validates the given UML Model :)
declare function local:validateUML($file-name as xs:string) as xs:string
{
  let $port := string(doc('/db/mdr/config.xml')/config/common/@port)
  let $file-loc := concat('http://localhost:',$port,'/exist/rest',lib-util:getCollectionPath('models'),'/documents/',$file-name)
  (: let $file-loc := concat('http://localhost:9090/exist/rest/db/mdr/data/models/documents/',$file-name):) 
  let $uml-doc := doc($file-loc)
    
  return 
  
  if(ends-with($file-name,'.xmi'))
  then(
    let $search-CDERef := $uml-doc//UML:Model
    return
    if($search-CDERef > '')
        then (
             let $transform := doc("/db/mdr/connector/stylesheets/umlValidation_transform.xsl")
             let $aftertransform := transform:transform($uml-doc,$transform,())
             
             let $check := local:checkValidity($aftertransform)
             return 
             if(count($check) eq 0 or contains($check,'invalid'))
                then 'invalid'             
             else 
                'valid'
        )
    else
        'invalid'
   )
   else 
        'invalid'
};

(:  checkValidity checks the openMDR and caDSR for the CDE
    Returns valid if the REST Query returns some results
    Returns invalid if the REST Query returns no results    
    param: aftertransform contains values of CDERef extracted from the xmi 
:)
declare function local:checkValidity($aftertransform as element()*) as xs:string* {
     for $element at $pos in $aftertransform
        let $resource := substring-before($element/@value,'_')
        let $qs := string(doc("/db/mdr/connector/config.xml")/c:config/c:resources/c:query_service[@identifier_prefix=$resource]/@name)
        let $start := 0
        let $end := 5
        
        return 
            if($qs eq 'caDSR')
            then(
                let $log := util:log-system-out('Searching caDSR for CDE with id')
                let $id := substring-before(substring-after($element/@value,'_'),'_')
                let $log := util:log-system-out($id)
                
                let $query := lib-qs:getURLcaDSR($qs, (), $id, $start, $end)
                let $response := doc($query)/xlink:httpQuery/queryResponse
                
                return 
                    if($response > '')
                        then 'valid'
                    else
                        let $invalidCDEs :=   session:set-attribute("invalidCDEs",concat(session:get-attribute('invalidCDEs'),' ',$id))
                        return 'invalid' 
             )
            else(
                if($qs eq 'openMDR')
                then(
                    let $log := util:log-system-out('Searching openMDR for CDE with id')
                    let $id := string($element/@value)
                    let $log := util:log-system-out($id)
                                  
                    let $query := lib-qs:getURLcaDSR($qs, (), $id, $start, $end)
                    let $response := doc($query)/x:result-set/x:data-element
                    return 
                         if($response > '')
                            then 'valid'
                         else
                            'invalid'                     
                )
               else(
                    'invalid'
               )
            )
};

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
   let $message := lib-forms:store-annotated-model(request:get-uploaded-file('file'),concat($data-identifier,'_',request:get-uploaded-file-name('file')),'application/octet-stream') 
   let $document :=
      element openMDR:models {
           attribute annotated_model_identifier {$data-identifier},
           element openMDR:annotated_model_project_long_name{$project_long_name},
           element openMDR:annotated_model_project_short_name {$project_short_name},
           element openMDR:annotated_model_project_description {$project_description},
           element openMDR:service_url {$service_url},
           element openMDR:annotated_model_uri {concat('../data/models/documents/',$data-identifier,'_',request:get-uploaded-file-name('file'))},
           element openMDR:file_name {request:get-uploaded-file-name('file')},
           element openMDR:file_type {'application/octet-stream'}
           }
   
   (:Calling the validate method to validate the selected xmi:)   
   let $validUML := local:validateUML(concat($data-identifier,'_',request:get-uploaded-file-name('file')))
   let $log := util:log-system-out('Valid UML document???')
   let $log := util:log-system-out($validUML)
   (::)
   
   return 
    if($validUML eq 'valid')
        then (
            let $collection := 'models'
            let $message := lib-forms:store-document($document) 
            return
               if ($message='stored')
               then true()
               else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newAnnotatedModel.xquery&amp;",$message)))
          )
    else(
        false()     
    )
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
                          <td class="left_header_cell">Annotated XMI File <font color="red">*</font></td><td><input id="file" type="FILE" name="file"/></td>
                         </tr>
                         
                        <tr>
                          <td class="left_header_cell">Project Long Name <font color="red">*</font></td><td>{lib-forms:input-element('project_long_name', 80, $project_long_name)}</td>
                        </tr>

                        <tr>
                          <td class="left_header_cell">Project Short Name <font color="red">*</font></td><td>{lib-forms:input-element('project_short_name', 80, $project_short_name)}</td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">Project Version <font color="red">*</font></td><td>{lib-forms:input-element('project_version', 1, $project_version)}</td>
                        </tr>

                        <tr>
                          <td class="left_header_cell">Project Description <font color="red">*</font></td><td>{lib-forms:input-element('project_description', 80, $project_description)}</td>
                        </tr>
                        
                        <tr>
                          <td class="left_header_cell">Service URL <font color="red">*</font></td><td>{lib-forms:input-element('service_url', 80, $service_url)}</td>
                        </tr>                
                        
                        <tr>
                           <td class="row-header-cell" colspan="6">Store</td>
                        </tr>
                        <tr>
                            <td class="left_header_cell"></td>
                            <td><input type="submit" name="update" value="Save" onClick="return validate_AnnotatedModel();"/></td>
                            <td colspan="4">
                                <input type="submit" name="update" value="Clear" onClick="this.form.reset()"/>
                            </td>
                        </tr>
                     </table>
              </div>
          </form>
          </td></tr>
          <tr><td><font size="3" color="red">{$message}</font></td></tr>
          <tr><td><font size="2" color="red">{session:get-attribute('invalidCDEs'),session:set-attribute("invalidCDEs","")}</font></td></tr>
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
  
   return
   
      lib-rendering:txfrm-webpage(
      $project_long_name,
      if ($action='Save')
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
         'Select UML (.xmi) file with valid CDEs in openMDR or caDSR. Invalid CDEs :',
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
