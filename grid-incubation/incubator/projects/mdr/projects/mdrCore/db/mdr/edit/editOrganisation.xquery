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
 :    @author Rakesh Dhaval
 :    @version 3.0
 :    allows editing the Organization
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


declare function local:organisation(
   $id as xs:string,
   $reg-auth as xs:string,
   $administrative-note as xs:string,
   $administrative-status as xs:string,
   $administered-by as xs:string,
   $submitted-by as xs:string,
   $registered-by as xs:string,
   $organization_name as xs:string?,
   $organization_mail_address as xs:string?,
   $contact_name as xs:string?,
   $contact_title as xs:string?,
   $contact_information as xs:string?
   ) as xs:boolean
{
   let $version := lib-forms:substring-after-last($id,'-')
   let $data-identifier := substring-after(lib-forms:substring-before-last($id,'-'),'-')
   let $doc-name := concat($id,'.xml')
   let $organization-identifier := substring-after(lib-forms:substring-before-last($id,'-'),'-')
   let $contact-identifier := substring-after(lib-forms:substring-before-last($id,'-'),'-')
   let $log := util:log-system-err($organization-identifier)
   let $log := util:log-system-err($contact-identifier)

 
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,'Recorded'),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by)
          
   )                 
   (: compose the document :)
    let $document :=
            element openMDR:Organization {
     										attribute organization_identifier{$organization-identifier},
           							        element openMDR:organization_name{$organization_name},
                							element openMDR:organization_mail_address{$organization_mail_address},
                							element openMDR:Contact {
        							            attribute contact_identifier{$contact-identifier},
        							            element openMDR:contact_name{$contact_name},
        							            element openMDR:contact_title{$contact_title},
        							            element openMDR:contact_information{$contact_information}
    							            }
							             }
      
   let $collection := 'organisation'
   let $log := util:log-system-err($collection)
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=editProperty.xquery&amp;",$message)))
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
   $org_name as xs:string?,
   $org_mail_address as xs:string?,
   $contact-name as xs:string?,
   $contact-title as xs:string?,
   $contact-information as xs:string?,
   $action as xs:string?
   ) {
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0

   return

   <div xmlns="http://www.w3.org/1999/xhtml">
   
      <table class="layout">
          <tr>
             <td>
                This form will allow you to edit an Organisation in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="edit_organisation" action="editOrganisation.xquery" method="post" class="cagridForm" enctype="multipart/form-data">
             <div class="section">
                {lib-forms:hidden-element('id',$id)}
                {lib-forms:hidden-element('updating','updating')}               
                {lib-forms:edit-admin-item-only($reg-auth,
                     $administrative-note,
                     $administrative-status,
                     $administered-by,
                     $submitted-by,
                     $registered-by,
                     $action)}
                     
                	<table class="layout">
             		<tr><td class="row-header-cell" colspan="6">Organization</td></tr>
                {
                	<tr>
                  	<td class="left_header_cell">Organization Name</td>
                    <td><input type="text" name="org_name"></input></td>
                  </tr>,
                  <tr>
                  	<td class="left_header_cell">Organization Email Address</td>
                    <td><input type="text" name="org_mail_address"></input></td>
                  </tr>
              	}
             	</table>               	                                          
             	<table class="layout">
             		<tr><td class="row-header-cell" colspan="6">Contact</td></tr>
                {
                	<tr>
                  	<td class="left_header_cell">Name</td>
                    <td><input type="text" name="contact-name"></input></td>
                  </tr>,
                  <tr>
                  	<td class="left_header_cell">Title</td>
                    <td><input type="text" name="contact-title"></input></td>
                  </tr>,
                   <tr>
                  	<td class="left_header_cell">Information</td>
                    <td><input type="text" name="contact-information"></input></td>
                  </tr>
              	}
             	</table>  
             	
                <table class="section">
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
         <p>Property modified</p>
         <p><a href="../xquery/maintenance.xquery">Return to maintenance menu</a></p>    
         <p><a href="../xquery/newOrganisation.xquery">Create another Organization</a></p>    
      </div>
};

  

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   
   session:create(),
   let $id := request:get-parameter('id','')
   let $log := util:log-system-err($id)

   let $updating := request:get-parameter('updating','')
   let $title as xs:string := concat("Editing Organization ", $id)
   let $log := util:log-system-err($title)
    
   let $element := lib-util:mdrElement("organization",$id)
   let $log := util:log-system-err($element)
   
   let $action := request:get-parameter('update','')
   let $ireg-auth := string($element/@item_registration_authority_identifier)
   let $iadministrative-note := string($element//openMDR:administrative_note)
   let $iadministrative-status := string($element//openMDR:administrative_status)
   let $iadministered-by := string($element//openMDR:administered_by)
   let $isubmitted-by := string($element//openMDR:submitted_by)
   let $iregistered-by := string($element//openMDR:registered_by)

   let $iorganization_name := element//openMDR:organization_name
   let $log := util:log-system-err($iorganization_name)
   let $iorganization_mail_address := element//openMDR:organization_mail_address/text()   
   let $log := util:log-system-err($iorganization_mail_address)
   let $icontact_name := element//openMDR:contact_name/text()

   let $log := util:log-system-err($icontact_name)
   let $icontact_title := element//openMDR:contact_title/text()
   let $log := util:log-system-err($icontact_title)
   let $icontact_information := element//openMDR:contact_information/text()
   let $log := util:log-system-err($icontact_information)
   
   let $organization_name :=request:get-parameter('org_name','')
   let $log := util:log-system-err($organization_name)

   let $organization_mail_address :=request:get-parameter('org_mail_address','')
   let $contact_name :=request:get-parameter('contact-name','')
   let $contact_title :=request:get-parameter('contact-title','')
   let $contact_information :=request:get-parameter('contact-information','')
   let $action := request:get-parameter('update','')
   
   
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   
   let $organization_name :=request:get-parameter('org_name','')
   let $organization_mail_address :=request:get-parameter('org_mail_address','')
   let $contact_name :=request:get-parameter('contact-name','')
   let $contact_title :=request:get-parameter('contact-title','')
   let $contact_information :=request:get-parameter('contact-information','')
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store Changes')
      then 
         (
         if (
               local:organisation
                  (
                     $id,
                     $reg-auth,
                     $administrative-note,
                     $administrative-status,
                     $administered-by,
                     $submitted-by,
                     $registered-by,

                     $organization_name,
                     $organization_mail_address,
                     $contact_name,
                     $contact_title,
                     $contact_information
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
                     
                     $organization_name,
                     $organization_mail_address,
                     $contact_name,
                     $contact_title,
                     $contact_information,
                     $action
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
               
               $organization_name,
               $organization_mail_address,
               $contact_name,
               $contact_title,
               $contact_information,
               $action
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
               
               $iorganization_name,
               $iorganization_mail_address,
               $icontact_name,
               $icontact_title,
               $icontact_information,
               $action
               )
         )
       )  
    )