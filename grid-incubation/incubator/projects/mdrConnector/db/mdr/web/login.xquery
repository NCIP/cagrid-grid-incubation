xquery version "1.0";
(: Creates session attributes: user and password :)

(: Using eXist-predefined namespaces: request, util, xmldb :)
  
import module 
   namespace lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
  
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";  
  
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

declare function local:login($user as xs:string) as node()?
{
   let $redirect-uri as xs:anyURI := xs:anyURI(request:get-parameter('calling_page','../web/homepage.xquery'))
   let $database-uri as xs:string := "xmldb:exist:///db"
   let $pass := request:get-parameter("pass", "")
   let $login := xmldb:authenticate($database-uri, $user, $pass)
   return
       if ($login) 
       then (
         session:set-attribute("username", $user),
         session:set-attribute("password", $pass),
         response:redirect-to(session:encode-url($redirect-uri))
             ) 
       else <p>Login failed! Please retry.</p>
};

declare function local:do-login() as node()?
{
    let $user := request:get-parameter("user", ())
    return
        if ($user) then
            local:login($user)
        else ()
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:invalidate(),
session:create(),

let $title as xs:string := "login"
let $content  :=
        (
        <div xmlns="http://www.w3.org/1999/xhtml" >
           <form name="login" method="post" class="cancergridForm" action="{session:encode-url(request:get-uri())}">
               <table class="login" cellpadding="5">
                   <tr>
                       <th colspan="2" align="left">Please Login</th>
                   </tr>
                   <tr>
                       <td align="left">Username:</td>
                       <td><input name="user" type="text" size="20"/></td>
                   </tr>
                   <tr>
                       <td align="left">Password:</td>
                       <td><input name="pass" type="password" size="20"/></td>
                   </tr>
                   <tr>
                       <td colspan="2" align="left"><input type="submit" value="login"/></td>
                   </tr>
               </table>
           </form>
        </div>,
        local:do-login()
        )
return
   if (lib-util:checkSSL()=true())
   then (lib-rendering:txfrm-webpage($title,element div{$content}))
   else (
        let $servername := request:request-servername()
        return response:redirect-to(xs:anyURI(concat("https://", $servername, ":",
            string(doc(concat(lib-util:rootURI(), "/config.xml"))/config/secure/@port),
            request:get-uri())))
   )