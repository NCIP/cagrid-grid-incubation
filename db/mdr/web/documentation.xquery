xquery version "1.0";

(: ~
 : Module Name:             useful documents webpage and XQuery
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

  
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";  
   
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
   at "../web/m-lib-rendering.xquery";   
   
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),

      let $title as xs:string := "Useful Documents"
      return 
         lib-rendering:txfrm-webpage($title, 
           <div xmlns="http://www.w3.org/1999/xhtml" >
           <table class="layout">
              <tr><td colspan="3">Here are a number of useful documents about ISO11179 and about maintaining definitions in your metadata repository</td></tr>
            <tr><td><div class="admin_item_table_header">name</div></td><td><div class="admin_item_table_header">provider</div></td><td><div class="admin_item_table_header">uri</div></td></tr>

              {for $doc in lib-util:mdrElements("reference_document")
                  let $name := data($doc//cgMDR:reference_document_title)
                  let $provider := data($doc//cgMDR:provided_by)
                  let $uri := data($doc//cgMDR:reference_document_uri)

                  let $url := concat(substring-before(request:get-url(),
                    "/rest/"), "/rest", lib-util:getCollectionPath("reference_document"),
                    "/", $uri)

                  where data($doc//cgMDR:reference_document_type_description)="operational"
                  order by $name
                  return
                      <tr><td>{$name}</td><td>{$provider}</td><td><a href="{$url}">{$uri}</a></td></tr>
              }
           </table>
           </div>
           )
