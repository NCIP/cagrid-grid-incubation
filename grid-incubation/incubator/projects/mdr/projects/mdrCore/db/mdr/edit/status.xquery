
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
      @author Puneet Mathur
 :    @version 0.2
 :
 :    Display System Status 
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
declare namespace request="http://exist-db.org/xquery/request"; 
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";

declare namespace system = "http://exist-db.org/xquery/system";
declare namespace status = "http://exist-db.org/xquery/admin-interface/status";

declare function status:status-line($key as xs:string, $value as xs:string) as element()
{
    <tr>
        <td class="key">{$key}:</td>
        <td>{$value}</td>
    </tr>
};
   
 
declare function local:success-page() 
{
   let $calling-page := request:get-parameter("calling-page","")
   return
      <div xmlns="http://www.w3.org/1999/xhtml">
       <table class="layout">
          <tr/>
            <tr><th colspan="2">General</th></tr>
            {
                status:status-line("eXist Version", system:get-version()),
                status:status-line("eXist Build", system:get-build()),
                status:status-line("eXist Home", system:get-exist-home()),
                status:status-line("SVN Revision", system:get-revision()),
                status:status-line("Java Vendor", util:system-property("java.vendor")),
                status:status-line("Java Version", util:system-property("java.version")),
                status:status-line("Operating System", 
                    concat(util:system-property("os.name"), " ", util:system-property("os.version"),
                        " ", util:system-property("os.arch"))
                )
            }
            <tr/><tr/><tr/><tr/>
            <tr><th colspan="2">Memory Usage</th></tr>
            {
                    let $max := system:get-memory-max() idiv 1024,
                    $current := system:get-memory-total() idiv 1024,
                    $free := system:get-memory-free() idiv 1024
                return (
                    status:status-line("Max. Memory", concat($max, "K")),
                    status:status-line("Current Total", concat($current, "K")),
                    status:status-line("Free", concat($free, "K"))
                )
            }
            <tr> </tr>
            <tr> </tr>
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
   let $title as xs:string := concat("System Status", $id)


   return 
      lib-rendering:txfrm-webpage(
      $title,
      local:success-page()
         )
       

