xquery version "1.0";
(: ~
 : Module Name:             header.xquery
 :
 : Module Version           3.22
 :
 : Date                               22nd January 2008
 :
 : Copyright                      The cancergrid consortium
 :
 : Module overview         Provides the header for the model page
 :
 :)
 
(:~
 :    @author Steve Harris
~ :)
   
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";    
   
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";   

declare namespace session="http://exist-db.org/xquery/session";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),
let $title as xs:string :='Registry Model'
(:return lib-rendering:txfrm-header($title):)

let $user:=session:get-attribute("username")
let $debug:=xs:boolean(request:get-parameter("debug","false"))
let $path:= concat("xmldb:exist://" ,lib-util:webPath(), "stylesheets/lib-rendering.xsl")
return
   transform:transform(
      <content/>, 
      $path,
      element parameters {
      element param {
         attribute name {'user'},
         attribute value {if($user) then($user) else('guest')}
         },
      element param {
         attribute name {'title'},
         attribute value {$title}
         },
      element param {
         attribute name {'footer'},
         attribute value {'false'}
      }
      }
     )