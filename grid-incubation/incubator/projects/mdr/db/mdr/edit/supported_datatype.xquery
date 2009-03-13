xquery version "1.0";

(: ~
 : Module Name:             datatype webpage and XQuery
 :
 : Module Version           1.0
 :
 : Date                               22nd September 2006
 :
 : Copyright                       The cancergrid consortium
 :
 : Module overview         maintains datatype resources
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

  
import module namespace 
  lib-forms="http://www.cancergrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
import module namespace 
  lib-util="http://www.cancergrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";
  
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

declare variable $title as xs:string := "Supported Datatypes";

declare function local:page-success() as element(div)
{
      <div xmlns="http://www.w3.org/1999/xhtml" class="message"> Datatype stored.  
      <a href="../edit/supported_datatype.xquery">Return to calling page</a>
      </div>
};

declare function local:page-edit($message as xs:string) as element(div)
{
   <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="new_datatype" action="{session:encode-url(request:get-uri())}" method="post" class="cancergridForm">
         <div class="section">
            <table class="section">
               <tr><td colspan="2">This form allows you to maintain the data types supported by this metadata repository</td></tr>
               <tr><td colspan="2"><p><div class="message">{$message}</div></p></td></tr>
               <tr><td class="left_header_cell">name</td><td>{lib-forms:input-element('name',92,'')}</td></tr>
               <tr><td class="left_header_cell">scheme</td><td>{lib-forms:select-from-simpleType-enum('datatype_scheme_reference','scheme',false(),'')}</td></tr>
               <tr><td class="left_header_cell">description</td><td>{lib-forms:text-area-element('description', 5, 57, '')}</td></tr>
               <tr><td class="left_header_cell">annotation</td><td>{lib-forms:input-element('annotation',92,'')}</td></tr>
               <tr><td class="left_header_cell"></td><td colspan="5"><input type="submit" name="new" value="Store"/></td></tr>    
            </table>
         </div>
      </form>

      <p>
      
         <table class="layout">
            <tr>

            <td><div class="admin_item_table_header">data type</div></td>
            <td><div class="admin_item_table_header">scheme</div></td>
            <td><div class="admin_item_table_header">description</div></td>
            <td><div class="admin_item_table_header">annotation</div></td>
            </tr>
            {
            
            for $datatype in lib-util:mdrElements("data_type")
               let $id := data($datatype//@datatype_identifier)
               let $name :=data($datatype//cgMDR:datatype_name)
               let $scheme :=data($datatype//cgMDR:datatype_scheme_reference)
               let $annotation := data($datatype//cgMDR:datatype_annotation)
               let $description := data($datatype//cgMDR:datatype_description)                   
               order by $name
               return
               <tr>
                  <td>{$name}</td>
                  <td>{$scheme}</td>
                  <td>{$description}</td>
                  <td>{$annotation}</td>
               </tr>
            }
            
         </table>
      </p>
           

   </div>
   };
   
declare function local:document() as element(cgMDR:cgDatatype)
   {
   element cgMDR:cgDatatype
         {
            attribute datatype_identifier {lib-forms:generate-id()},
            element cgMDR:datatype_annotation {request:get-parameter('annotation', ())},
            element cgMDR:datatype_description {request:get-parameter('description', ())},             
            element cgMDR:datatype_name {request:get-parameter('name','please supply name')},
            element cgMDR:datatype_scheme_reference {request:get-parameter('scheme',())}
         }
   };
   
   
   declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   
   
session:create(),
lib-rendering:txfrm-webpage($title, <div xmlns="http://www.w3.org/1999/xhtml"> {
   if (request:get-parameter('new','') = 'Store')
   then 
   (
      let $message := lib-forms:store-document(local:document())
      return
         if ($message = "stored") 
         then local:page-success()
         else local:page-edit($message)
   )
   else local:page-edit('')
   }</div>
)


