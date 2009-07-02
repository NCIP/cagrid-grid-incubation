xquery version "1.0";

(: ~
 : Module Name:             Orgtanisation detail
 :
 : Module Version           1.0
 :
 : Date                     24th April 2008
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          Displays organisation detail
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module 
   namespace lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";   
    
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $user := request:get-session-attribute("username")
let $id as xs:string := xs:string(request:get-parameter("compound_id",""))
let $content := lib-util:mdrElement('organization', $id)
let $title as xs:string := concat("Organisation: ", $content//openMDR:organization_name)

return
    if (request:get-parameter("as-xml",()))
    then (lib-rendering:as-xml($content))
    else lib-rendering:txfrm-webpage($title, $content, true(), true(), $id, 'organization.xquery','../edit/editOrganization.xquery')
   
