xquery version "1.0";

(: ~
 : Module Name:             units of measure webpage and XQuery
 :
 : Module Version           1.0
 :
 : Date                               22nd September 2006
 :
 : Copyright                       The cancergrid consortium
 :
 : Module overview         maintains units of measure resources
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

import module namespace 
   lib-forms="http://www.cancergrid.org/xquery/library/forms"
   at "../edit/m-lib-forms.xquery";
     
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";   
     

declare function local:page-entry($message as xs:string) as node()
{     
   <div xmlns="http://www.w3.org/1999/xhtml">
     <p>
        <table class="layout">
           <tr><td colspan="3">This form allows you to maintain the units of measure supported by this metadata repository</td></tr>
           <tr><td colspan="3"><p><div class="message">{$message}</div></p></td></tr>
           <tr><td><div class="admin_item_table_header">identifier</div></td><td><div class="admin_item_table_header">name</div></td><td><div class="admin_item_table_header">precision</div></td></tr>
           {
           for $uom in lib-util:mdrElements("unit_of_measure")
              let $id := data($uom//@unit_of_measure_identifier)
              let $name :=data($uom//cgMDR:unit_of_measure_name)
              let $precision :=data($uom//cgMDR:unit_of_measure_precision)
              order by $name
              return
                 <tr><td>{$id}</td><td>{$name}</td><td>{$precision}</td></tr>
           }
        </table>
     </p>
        
      <form name="new_uom" action="{session:encode-url(request:get-uri())}" method="post" class="cancergridForm">
          <div class="section">
             <table class="section">
                <tr><td colspan="2">Add a new unit of measure to the metadata repository</td></tr>
                <tr><td class="left_header_cell">name</td><td><input name="name" type="text" size="70"/></td></tr>
                <tr><td class="left_header_cell">precision</td><td><input name="precision" type="text" size="70"/></td></tr>
                <tr><td class="left_header_cell"></td><td colspan="5"><input type="submit" name="new" value="store"/></td></tr>    
             </table>
         </div>
      </form>
   </div>
};
   
declare function local:page-success()
{
      <div xmlns="http://www.w3.org/1999/xhtml" class="message">
      Unit of measure stored.  
      <a href="supported_units_of_measure.xquery">Return to calling page</a>
      </div>
};

declare function local:uom-document() as element(cgMDR:Unit_of_Measure)
{
   let $name := request:get-parameter('name','')
   let $precision := request:get-parameter('precision','')
   let $identifier := lib-forms:generate-id()
   return
      element cgMDR:Unit_of_Measure
         {
            attribute unit_of_measure_identifier {$identifier},
            element cgMDR:unit_of_measure_name {$name},
            element cgMDR:unit_of_measure_precision {$precision}
         } 
};
   
   
   declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   
   declare variable $title as xs:string := "Supported Units of Measure";
   
   session:create(),
   
   lib-rendering:txfrm-webpage($title, 
      if (request:get-parameter('new','') = 'store')
      then 
      (
         let $message := lib-forms:store-document(local:uom-document())
         return
            if ($message='stored') 
            then local:page-success()
            else local:page-entry($message)
      )
      else local:page-entry('')
   )
