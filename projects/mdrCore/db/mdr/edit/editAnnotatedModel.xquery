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
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace q="http://cagrid.org/schema/query";
declare namespace c="http://cagrid.org/schema/config";
declare namespace UML ="omg.org/UML1.3";
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace xlink="http://www.w3.org/1999/xlink";
declare namespace x="http://cagrid.org/schema/result-set";
declare namespace datetime = "http://exist-db.org/xquery/datetime";

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
                     
declare function local:annotated-model(
   $project_long_name as xs:string?,
   $project_short_name as xs:string?,
   $project_version as xs:float?,
   $project_description as xs:string?,
   $service_url as xs:string?,
   $file as xs:string?,
   $action as xs:string?,
   $id as xs:string?,
   $previous-file as xs:string*,
   $reg-auth as xs:string?,
   $registered-by as xs:string?,
   $administered-by as xs:string?,
   $submitted-by as xs:string?,
   $administrative-status as xs:string?,
   $registration_status as xs:string?
   ) as xs:boolean
{
   
   let $idprefix := 'cagrid.org'
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'_'),'_')
   let $doc-name := concat($idprefix,'_',$data-identifier,'_',$project_version,'_',request:get-uploaded-file-name('file'))
   let $message := lib-forms:store-annotated-model(request:get-uploaded-file('file'),$doc-name,'application/octet-stream') 
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
   
   (:let $document :=
      element openMDR:models {
           (:attribute annotated_model_identifier {$data-identifier},
           attribute version {$project_version},:)
           attribute item_registration_authority_identifier {'cagrid.org'},
           attribute data_identifier {substring-before(substring-after($data-identifier,'_'),'_')},
           attribute version {$project_version},
           element openMDR:annotated_model_project_long_name{$project_long_name},
           element openMDR:annotated_model_project_short_name {$project_short_name},
           element openMDR:annotated_model_project_description {$project_description},
           element openMDR:service_url {$service_url},
           element openMDR:annotated_model_uri {concat('../data/models/documents/',$doc-name)},
           element openMDR:previous_annotated_model_uri {$previous-file},
           element openMDR:file_name {request:get-uploaded-file-name('file')},
           element openMDR:file_type {'application/octet-stream'}
           }:)
   
    let $document :=
      element openMDR:models {
           (:attribute annotated_model_identifier {$data-identifier},
           attribute version {$version},:)
           attribute item_registration_authority_identifier {$reg-auth},
           attribute data_identifier {$data-identifier},
           attribute version {$project_version},
           lib-make-admin-item:administration-record('',$administrative-status,$creation-date,$registration_status),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
           
           element openMDR:annotated_model_project_long_name{$project_long_name},
           element openMDR:annotated_model_project_short_name {$project_short_name},
           element openMDR:annotated_model_project_description {$project_description},
           element openMDR:service_url {$service_url},
           element openMDR:annotated_model_uri {concat('../data/models/documents/',$doc-name)},
           element openMDR:file_name {request:get-uploaded-file-name('file')},
           element openMDR:file_type {'application/octet-stream'}
    }
   
   let $validUML := local:validateUML($doc-name)
   let $log := util:log-system-out('Valid UML document???')
   let $log := util:log-system-out($validUML)
   
   (:let $collection := 'models'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newReferenceDocument.xquery&amp;",$message))):)
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
     $action as xs:string?,
     $id as xs:string?,
     $version as xs:float?,
     $reg-auth as xs:string?,
     $registered-by as xs:string?,
     $administered-by as xs:string?,
     $submitted-by as xs:string?,
     $administrative-status as xs:string?,
     $registration_status as xs:string
   ) {

   let $proposedNextVersion := $version + 0.1
   let $proposedReleaseVersion := ceiling($proposedNextVersion)
   
   let $skip-uri := substring-after($action,'delete uri entry')
   let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0
   
   return
   <div xmlns="http://www.w3.org/1999/xhtml">
      <table class="layout">
          <tr><td>This form will allow you to upload the annotated model</td></tr>
          <tr><td>
          <form name="new_annotated_model" action="editAnnotatedModel.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
             {lib-forms:hidden-element('id',$id)}        
              <div class="section">
                 <tr><td class="row-header-cell" colspan="6">Standard Administered Item Metadata</td></tr>
                 <tr><td>
                 <div class="tabber">
                  <div id='validate' class="tabbertab">
                      <table class="section">
                        <h2>Administered Item Metadata</h2>
                        <tr><td> This form will allow you to upload the annotated model</td></tr>
                        <tr>
                            <p>
                              <table class="section">
                                  <tr><td class="left_header_cell">Registration Authority <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registration-authority($reg-auth)} </td></tr>
                                  <tr><td class="left_header_cell">Existing Version</td><td colspan="5"> {$version}{lib-forms:radio('version',string($version),'true')} </td></tr>
                                  <tr><td class="left_header_cell">Proposed/Release Version</td><td colspan="5"> {$proposedNextVersion}{lib-forms:radio('proposedNextVersion',string($proposedNextVersion),'true')}  {$proposedReleaseVersion}{lib-forms:radio('proposedNextVersion',string($proposedReleaseVersion),'false')}</td></tr>
                                  <tr><td class="left_header_cell">Registered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registered_by($registered-by)} </td></tr>
                                  <tr><td class="left_header_cell">Administered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-administered_by-nameAndOrg($administered-by)} </td></tr>
                                  <tr><td class="left_header_cell">Submitted by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-submitted_by-nameAndOrg($submitted-by)} </td></tr>
                                  <tr><td class="left_header_cell">Administrative Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                                  <tr><td class="left_header_cell">Registration Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Registration_Status','registration_status', false(), $registration_status)}</td></tr>
                              </table>
                           </p>
                        </tr>
                      </table>
               
                      <table class="section">
                          <tr><td class="row-header-cell" colspan="6">Annotated Model</td></tr>
                          <tr><td class="left_header_cell">Annotated XMI File <font color="red">*</font></td><td><input id="file" type="FILE" name="file"/></td></tr>
                          <tr><td class="left_header_cell">Project Long Name <font color="red">*</font></td><td>{lib-forms:input-element('project_long_name', 80, $project_long_name)}</td></tr>
                          <tr><td class="left_header_cell">Project Short Name <font color="red">*</font></td><td>{lib-forms:input-element('project_short_name', 80, $project_short_name)}</td></tr>
                          <tr><td class="left_header_cell">Project Description <font color="red">*</font></td><td>{lib-forms:input-element('project_description', 80, $project_description)}</td></tr>
                          <tr><td class="left_header_cell">Service URL <font color="red">*</font></td><td>{lib-forms:input-element('service_url', 80, $service_url)}</td></tr>                
                          <tr><td class="row-header-cell" colspan="6">Store</td></tr>
                          
                          <tr>
                              <td class="left_header_cell"></td>
                              <td><input type="submit" name="update" value="Save" onClick="return validate_AnnotatedModel();"/></td>
                              <td colspan="4">
                                  <input type="submit" name="update" value="Clear" onClick="this.form.reset()"/>
                              </td>
                          </tr>
                       </table>
                </div>
                </div>
                </td></tr>
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
         <p>Reference Document created</p>
         <p><a href="../edit/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../edit/newReferenceDocument.xquery">Create reference document</a></p>    
      </div>
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   
   let $id := request:get-parameter('id','')
   let $project_long_name := request:get-parameter('project_long_name','')
   let $project_short_name := request:get-parameter('project_short_name','')
   let $project_version := request:get-parameter('project_version','')
   let $project_description := request:get-parameter('project_description','')
   let $service_url := request:get-parameter('service_url','')
   let $file := request:get-parameter('file','')
   let $action := request:get-parameter('update','')
   let $title := concat('Editing Annotated Model ',$id)
   
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $registration_status := request:get-parameter('registration_status','')
   
   let $element := lib-util:mdrElement("models",$id)
   let $iannotated-id := string($element/@annotated_model_identifier)
   let $iversion := string($element/@version)
   let $iproject_long_name := string($element//openMDR:annotated_model_project_long_name)
   let $iproject_short_name := string($element//openMDR:annotated_model_project_short_name)
   let $iproject_version := string($element/@version)
   let $iproject_description := string($element//openMDR:annotated_model_project_description)
   let $iservice_url := string($element//openMDR:service_url)
   let $ifile := string($element//openMDR:file_name)
   let $iaction := request:get-parameter('update','')
   
   let $previous-file := concat(string($element//openMDR:previous_annotated_model_uri),string($element//openMDR:annotated_model_uri))
   
   let $version := $iversion
   let $proposedNextVersion := request:get-parameter('proposedNextVersion',$iversion)
   let $version := round-half-to-even(xs:float($proposedNextVersion),2)
   
   let $ireg-auth := string($element/@item_registration_authority_identifier)
   let $iadministrative-note := string($element/openMDR:administered_item_administration_record/openMDR:administrative_note)
   let $iadministrative-status := string($element/openMDR:administered_item_administration_record/openMDR:administrative_status)
   let $iadministered-by := string($element//openMDR:administered_by)
   let $isubmitted-by := string($element//openMDR:submitted_by)
   let $iregistered-by := string($element//openMDR:registered_by)
   let $iregistration_status := string($element/openMDR:administered_item_administration_record/openMDR:registration_status)
   let $registration_status := request:get-parameter('registration_status','')
   let $log := util:log-system-out('pppppppppppppppppppppppoooooooooooooooooo')
   let $log := util:log-system-out($iadministrative-status)
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Save')
      then 
         (
         if (
               local:annotated-model(
                     $project_long_name,
                     $project_short_name,
                     $version,
                     $project_description,
                     $service_url,
                     $file,
                     $action,
                     $id,
                     $previous-file,
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
                'Select UML (.xmi) file with valid CDEs in openMDR or caDSR. Invalid CDEs :',
                  $project_long_name,
                  $project_short_name,
                  $project_version,
                  $project_description,
                  $service_url,
                  $file,
                  $action,
                  $id,
                  round-half-to-even(xs:float($iversion),2),
                  $reg-auth,
                  $registered-by,
                  $administered-by,
                  $submitted-by,
                  $administrative-status,
                  $registration_status
                )
               )
         )
      else local:input-page(
                '',
                  $iproject_long_name,
                  $iproject_short_name,
                  $iproject_version,
                  $iproject_description,
                  $iservice_url,
                  $ifile,
                  $iaction,
                  $id,
                  $version,
                  $ireg-auth,
                  $iregistered-by,
                  $iadministered-by,
                  $isubmitted-by,
                  $iadministrative-status,
                  $iregistration_status
                )
         )