xquery version "1.0";

(: ~
 : Module Name:             Items by registration status webpage
 : Module Version           0.1
 : Date                     2nd October 2006
 : Copyright                The cancergrid consortium
 :
 : Module overview          Lists all data elements and assets of a particular registration status
 :
 :)
 
(:~
 :    Commented on refactoring SJH
 :
 :    @author Steve Harris
~ :)

import module namespace lib-util="http://www.cancergrid.org/xquery/library/util"
  at "../library/m-lib-util.xquery";
  
import module namespace 
    lib-forms="http://www.cancergrid.org/xquery/library/forms" 
    at "../edit/m-lib-forms.xquery";        
    
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    
    
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";

declare function local:make-select-refdoc($selected as xs:string) as node()
{
     <select name="reference_document">
     {lib-forms:blank-filler()}
     {
      for $item in lib-util:mdrElements('reference_document')
      let $id:= data($item//@reference_document_identifier)
      let $title := data($item//cgMDR:reference_document_title)
      where $item//cgMDR:reference_document_type_description = 'commissioning'
      return
         if ($id=$selected) 
         then (<option value="{$id}" selected="selected">{$title}</option>) 
         else(<option value="{$id}">{$title}</option>)
      }
      </select>
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $registration-status := request:get-parameter('Registration_Status','')
let $selected := request:get-parameter('reference_document','')
let $reviewer := request:get-parameter('submitted-by','')

let $title := 'Administered Items by Status'
let $content as element(div):= (
   <div>
      <form action="AdminItemByRegStatus.xquery" method="post" class="cancergridForm">
         <table class="section">
         <tr><td class="left_header_cell">registration status</td><td>{lib-forms:select-from-simpleType-enum('Registration_Status', 'Registration_Status', false(), $registration-status)}</td></tr>
         <tr><td class="left_header_cell">commissioning document</td><td>{local:make-select-refdoc($selected)}</td></tr>
         <tr><td class="left_header_cell">reviewer</td><td>{lib-forms:make-select-submitted_by($reviewer)}</td></tr>
         <tr><td class="left_header_cell">display</td><td>

         <input type="radio" name="report-type" value="summary" checked="checked">summary</input>
         <input type="radio" name="report-type" value="detailed">detailed</input>

         </td></tr>
         <tr><td class="left_header_cell"></td><td colspan="5"><input type="submit" name="submit" value="filter records"/></td></tr>
         </table>
      </form>
      
      {
      if ($registration-status >' ' and $selected >' ') then (
         <table class="layout">
         <tr><td><div class="admin_item_table_header">identifier</div></td><td><div class="admin_item_table_header">name</div></td><td><div class="admin_item_table_header">review</div></td></tr>
         {
         if (request:get-parameter('report-type','')='summary')
         then (
               for $adminItem in lib-util:mdrElements('data_element')[data(.//cgMDR:registration_status) = $registration-status][data(.//cgMDR:described_by) = $selected]
               let $name:=administered-item:preferred-name($adminItem)
               let $id:=lib-util:mdrElementId($adminItem)
               let $review-status := if (exists(lib-util:mdrElements('data-element-rating')[data(@submitted-by)= $reviewer][data(@admin-item-id)=$id]))
                                     then ("reviewed")
                                     else(element a {
                                             attribute href {
                                                session:encode-url(
                                                   xs:anyURI(
                                                      concat(
                                                      "../xquery/newContentRating.xquery?showID=",$id,
                                                      "&amp;showSubmittedBy=", $reviewer, 
                                                      "&amp;showRegistrationStatus=", $registration-status,
                                                      "&amp;showReferenceDocument=", $selected)))
                                                },
                                             "review link"
                                           })
               let $link := administered-item:data-element-summary-anchor($id)

               
               order by $name
               return
                  <tr><td>{$id}</td><td>{$link}</td><td>{$review-status}</td></tr>
         )
         else (
               for $adminItem in lib-util:mdrElements()[data(.//cgMDR:registration_status) = $registration-status][data(.//cgMDR:described_by) = $selected]
               let $name:=administered-item:preferred-name($adminItem)
               let $id:=lib-util:mdrElementId($adminItem)
               let $doc-type:=lib-util:mdrElementType($adminItem)
               let $doc-type-friendly:=lib-util:mdrElementTypeFriendly($adminItem)
               let $link := administered-item:html-anchor($doc-type,$id)
               
               order by $name
               return
                  <tr><td>{$id}</td><td>{$link}</td><td>{$doc-type-friendly}</td></tr>
               )
            }
         </table>
         )
         else()
      }
   </div> )

   return lib-rendering:txfrm-webpage($title, $content)