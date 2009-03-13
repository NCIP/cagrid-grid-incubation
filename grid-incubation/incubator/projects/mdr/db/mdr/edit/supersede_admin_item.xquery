xquery version "1.0";

(: ~
 : Module Name:            Supersede Admin Item Webpage
 :
 : Module Version          0.1
 :
 : Date                    26th October 2006
 :
 : Copyright               The cancergrid consortium
 :
 : Module overview         Supersedes an administered item with another.  
 :                         Used for consolidating the metadata repository
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @since 26th October 2006
 :    @version 0.1
 :
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

   
import module namespace 
   lib-supersede="http://www.cancergrid.org/xquery/library/supersede"
   at "../edit/m-lib-supersede.xquery";   
    
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace util="http://exist-db.org/xquery/util";

declare function local:content($supersede-source as xs:string?, $source as node()?, $supersede-target as xs:string?, $target as node()?) as element(form)
{
   <form xmlns="http://www.w3.org/1999/xhtml" name="supersede_admin_item" action="{session:encode-url(request:get-uri())}" method="post" class="cancergridForm" enctype="multipart/form-data">
      <table>
         <tr>
            <td>supersession source</td>
            <td>{lib-forms:input-element('supersede-source', 40, $supersede-source)}</td>
            <td></td>
            {
            if(exists($source)) then 
            (
            <td>{administered-item:html-anchor(substring-after(util:collection-name($source),'/db/mdr/data/'),lib-util:mdrElementId($source))}</td>
            )
            else
            (<td colspan="2"/>)
            }
         </tr>
         <tr>
            <td>supersession target</td>
            <td>{lib-forms:input-element('supersede-target', 40, $supersede-target)}</td>
            <td></td>
            {
            if(exists($target)) then 
            (
            <td>{administered-item:html-anchor(substring-after(util:collection-name($target),'/db/mdr/data/'),lib-util:mdrElementId($target))}</td>
            )
            else
            (<td colspan="2"/>)
            }
         </tr>
         
         <tr><td/><td colspan="2"><input type="submit" name="action" value="validate"/></td><td>{local:valid($source, $target)}</td></tr>
         <tr><td/><td colspan="3"><input type="submit" name="action" value="supersede"/></td></tr>
      
            {
            
            let $other-admin-items := lib-util:mdrElements()[.//cgMDR:registration_status != "Superseded"]
            [data(.//*)=$supersede-source or data(.//@*)=$supersede-source]
            return
                if ($other-admin-items)
                then (
                     <tr><td/><td colspan="3">The following resources are related to the supersession source and will be redirected to the supersession target</td></tr>,
                     <tr><td/><td><div class="admin_item_table_header">type</div></td><td><div class="admin_item_table_header">id</div></td><td><div class="admin_item_table_header">name</div></td></tr>,
                    
                    for $admin-item in $other-admin-items
                    let $collection := lib-util:mdrElementType($admin-item)
                    let $id := lib-util:mdrElementId($admin-item)  
                       return 
                          <tr><td></td><td>{$collection}</td><td>{$id}</td><td>{administered-item:html-anchor($collection,$id)}</td></tr>
            )
            else ()
            }
            
            
      </table>
   </form>
};

declare function local:valid($source as node()?, $target as node()?) as xs:string
{
   if (exists($source))
   then 
      (
      if (exists($target))
      then 
         (
         if (local-name($source) = local-name($target))
         then 
            (
            if (lib-util:mdrElementId($source) = lib-util:mdrElementId($target)) 
            then ('source and target identifiers are the same')
            else('valid')
            )
         else('source and target identifiers are valid but of different types')
         )
      else('source admin item identifier is valid, but the target admin item identifier is invalid')
      )
   else ('source admin item identifier is invalid')
};



declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),

let $supersede-source := request:get-parameter('supersede-source',())
let $supersede-target := request:get-parameter('supersede-target',())
let $action := request:get-parameter('action',())

let $source := 
    if ($supersede-source)
    then lib-util:mdrElement($supersede-source)
    else()
    
let $target := 
    if ($supersede-target)
    then lib-util:mdrElement($supersede-target)
    else ()

let $collection := 
    if ($supersede-source)
    then (lib-util:mdrElementType($source))
    else ()

let $title as xs:string := 'Supersede an administered item'

return 
   lib-forms:wrap-form-contents($title, 
      if ($action="supersede" and local:valid($source, $target)='valid')
      then 
      (lib-supersede:admin-item(
               $collection, 
               $supersede-source, 
               $supersede-target          
               )          
      )
      else (local:content($supersede-source, $source, $supersede-target, $target))
   )
   
   
   
   