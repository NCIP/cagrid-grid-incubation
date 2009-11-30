
xquery version "1.0";

(: ~
 : Module Name:             edit context webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     31st July 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          edit context information
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
 :    @version 0.1
 :
 :    Edit Context information 
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

   
declare function local:success-page() 
{
   let $calling-page := request:get-parameter("calling-page","")
   return
      <div xmlns="http://www.w3.org/1999/xhtml">
       <table class="layout">
          <tr>
             <td>
                Administrative module to manage users Coming soon... 
             </td>
          </tr>
              <tr>
              </tr>
          <tr>
            <td><a href='maintenance.xquery'>Return to maintenance menu</a>
            </td>
          </tr>
        </table>
      </div>
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $title as xs:string := concat(" User Management", $id)


   return 
      lib-rendering:txfrm-webpage(
      $title,
      local:success-page()
         )
       

