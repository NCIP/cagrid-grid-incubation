xquery version "1.0";
(: ~
 :
 : Module Name:             maintenance.xquery
 : Module Version           4
 : Date                     11th November 2006
 : Copyright                The cancergrid consortium
 : Module overview          maintenance functions
 :
 :)
 
(:~
 :
 :   @author Steve Harris
 :   @since July 20, 2006
 :   @version 4
 :
 :   version 4: new secure version.  Rejects user if not logged in
 :   content creation via infopath deprecated
 :
~ :)

declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace request="http://exist-db.org/xquery/request";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
      
import module namespace 
      lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
      at "../web/m-lib-rendering.xquery";
      
      
declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
      
session:create(),
let $doc := doc(concat("xmldb:exist://", lib-util:getResourcePath('edit'), "documents/maintenance.xml"))
return
   if  (lib-util:checkLogin() = false())
   then response:redirect-to(xs:anyURI('../web/login.xquery?calling_page=maintenance.xquery'))
   else lib-rendering:txfrm-webpage("Maintenance", $doc)
    
