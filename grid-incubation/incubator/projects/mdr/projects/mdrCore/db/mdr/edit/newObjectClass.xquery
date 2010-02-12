xquery version "1.0";

(: ~
 : Module Name:             new object class webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                               25th October 2006
 :
 : Copyright                       The cancergrid consortium
 :
 : Module overview          Creates and Object Class and displays list
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
 :
 :    @author Steve Harris
 :    @version 2.0
 :     now allows searching for concept terms
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
declare namespace xdt = "http://xdt.gate2.net/v1.0";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace datetime = "http://exist-db.org/xquery/datetime";

declare function local:object-class(
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
   $uris as xs:string*,
   $preferred as xs:string?
   ) as xs:boolean
{
   let $version := '0.1'
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $doc-name := concat($new-identifier,'.xml')
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,'Recorded'),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources), 
                      
               for $u in $uris
               return
                  element openMDR:reference_uri {$u})


   
   (: compose the document :)
   let $document :=
      element openMDR:Object_Class {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            attribute version {$version},
            $content
           }
      
   let $collection := 'object_class'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newObjectClass.xquery&amp;",$message)))
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
   $object_class_uri as xs:string*,
   $action as xs:string?,
   $preferred as xs:string?
   ) {

   let $skip-uri := substring-after($action,'delete uri entry')
   let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0

   return
   <div xmlns="http://www.w3.org/1999/xhtml">
 
      
      <table class="layout">
          <tr>
             <td>
                This form will allow you to create a new Object Class in the metadata repository
             </td>
          </tr>
          <tr><td>
          <form name="new_ObjectClass" action="newObjectClass.xquery" method="post" class="cagridForm" enctype="multipart/form-data" onSubmit="return validate_adminItems ()">
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


                    <table class="section">
                    
                    <tr>
                       <td class="row-header-cell" colspan="6">ObjectClass specific properties</td>
                    </tr>
                    
                    
                    {   
                         for $u at $pos in $object_class_uri
                         where $pos != $skip-uri-index and $u > ""
                         return 
                         (
                         if($pos > $skip-uri-index and $skip-uri-index > 0) 
                         then (
                            <tr>
                               <td class="left_header_cell">Concept Reference {util:eval($pos - 1)}</td>
                               <td colspan="5">
                                  {
                                     lib-forms:find-concept-id('object_class_uri','get concept',$u),
                                     lib-forms:action-button(concat('delete uri entry ',util:eval($pos - 1)), 'action' ,'')
                                  }
                               </td>
                            </tr>
                         ) else (
                            <tr>
                               <td class="left_header_cell">Concept Reference {$pos}</td>
                               <td colspan="5">
                                  {
                                     lib-forms:find-concept-id('object_class_uri','get concept',$u),
                                     lib-forms:action-button(concat('delete uri entry ',$pos), 'action' ,'')
                                  }
                               </td>
                            </tr>
                         )
                          
                         )
                     }
                           
                            <tr>
                               <td class="left_header_cell">New Concept Reference</td>
                               <td colspan="5">
                                  {lib-forms:find-concept-id('object_class_uri','get concept','')}
                                  <br></br>
                                  {lib-forms:action-button('add another concept', 'action' ,'')}
                               </td>
                            </tr>
                            
                    <tr><td class="left_header_cell"></td><td><input type="submit" name="update" value="Store"/></td>
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
         <p>Object Class <b>{request:get-parameter('names',())}</b> created</p>
         <p><a href="../edit/maintenance.xquery">Return to Maintenance Menu</a></p>    
         <p><a href="../edit/newObjectClass.xquery">Create another Object Class</a></p>    
      </div>
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   
session:create(),

   let $title as xs:string := "Creating a New ObjectClass"
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
   let $object_class_uri := request:get-parameter('object_class_uri',())
   let $preferred := request:get-parameter('preferred','')
   let $action := request:get-parameter('update','')
   
   return
   
      lib-rendering:txfrm-webpage(
      $title,
      if ($action='Store')
      then 
         (
         if (
               local:object-class
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
                     $object_class_uri,
                     $preferred
                  )
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
                     $object_class_uri,
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
               $object_class_uri,
               $action,
               $preferred
               )
         )
