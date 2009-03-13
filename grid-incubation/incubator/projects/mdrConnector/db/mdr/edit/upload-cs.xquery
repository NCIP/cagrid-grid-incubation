xquery version "1.0";
(: ~
: Module Name:             Load CS
:
: Module Version           1.0
:
: Date                     06 Nov 2008
:
: Copyright                The cancergrid consortium
:
: Module overview          Loads a classification scheme into the registry
:
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

import module 
   namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
   
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery"; 

import module namespace 
    lib-make-admin-item="http://www.cancergrid.org/xquery/library/make-admin-item"
    at "../edit/m-lib-make-admin-item.xquery";   

import module namespace 
lib-forms="http://www.cancergrid.org/xquery/library/forms"
at "../edit/m-lib-forms.xquery";   
   
   
(:declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";:)

declare function local:defaults() as element()
{
    doc(concat(lib-util:editPath(),"documents/defaults.xml"))/cgMDR:edit-defaults
};

declare function local:cs-admin-record() as element()
{    
    let $reg-auth := 
         for $item-reg-auth-id in lib-util:mdrElements("registration_authority")[.//cgMDR:registrar_identifier = request:get-parameter("registered-by",())]//@organization_identifier
         return data($item-reg-auth-id[1])
    
    let $defaults := local:defaults()
    
    return         
        element cgMDR:Classification_Scheme {
        lib-make-admin-item:identifier-attributes(
            $reg-auth,
            lib-forms:generate-id(),
            $defaults/cgMDR:Classification_Scheme/@version),
        lib-make-admin-item:administration-record(
            '',
            $defaults/cgMDR:administrative_status,
            $defaults/cgMDR:registration_status),
        lib-make-admin-item:custodians(
            request:get-parameter("administered-by",()),
            request:get-parameter("registered-by",()),
            request:get-parameter("submitted-by",())),
        element cgMDR:described_by {},
        lib-make-admin-item:having(
            $defaults//cgMDR:context_identifier,
            $defaults//cgMDR:country_identifier,
            $defaults//cgMDR:language_identifier,
            request:get-parameter("preferred-name",()),
            request:get-parameter("definition",()),
            true(), ()),
        element cgMDR:referenceURI {request:get-uploaded-file-name("upload")}
    }
    };


session:create(),

    
let $action := request:get-parameter("update","")
let $content := util:parse(request:get-parameter("upload", ""))



    
return
    if ($action="update")
    then (
        lib-rendering:txfrm-webpage("Upload and register classification scheme", 
            <div>{lib-forms:store-resource('data/classification_scheme',
                   request:get-uploaded-file-name("upload"),
                   $content),
                   lib-forms:store-document(local:cs-admin-record())}</div>)
       )
    else 
    lib-rendering:txfrm-webpage("Upload and register classification scheme", 
        <div xmlns="http://www.w3.org/1999/xhtml">
            <form name="upload-cs" 
                method="post" 
                class="cancergridForm" 
                action="/exist/rest//db/mdr/edit/upload-cs.xquery"
                enctype="multipart/form-data">
                
                      <table class="layout">
                         <tr>
                             <td class="left_header_cell">Preferred Name</td>
                             <td>{lib-forms:input-element('preferred-name',50,'')}</td>
                         </tr>
         
                         <tr>
                            <td class="left_header_cell">Definition</td>
                            <td>{lib-forms:text-area-element('definition', 5, 30, '')}</td>
                         </tr>
                         
                         <tr>
                            <td class="left_header_cell">I am:</td>
                            <td>{lib-forms:make-select-submitted_by('')}</td>
                         </tr>

                         <tr>
                            <td class="left_header_cell">Who is working for: </td>
                            <td>{lib-forms:make-select-administered_by('')}</td>
                         </tr>
                         
                         <tr>
                            <td class="left_header_cell">Creating data elements on behalf of : </td>
                            <td>{lib-forms:make-select-registered_by('')}</td>
                         </tr>
                        <tr>
                            <td class="left_header_cell">Select classification scheme</td>
                            <td>
                                <input type="file" name="upload"/><br/>
                                <input type="submit" value="update" name="update"/>
                            </td>
                        </tr>
                </table>
            </form>
        </div>)
   
   
   
   
