declare namespace xforms="http://www.w3.org/2002/xforms";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   lib-forms="http://www.cancergrid.org/xquery/library/forms"
   at "../edit/m-lib-forms.xquery";   

(:declare option exist:serialize "media-type=application/xhtml+xml indent=yes method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";:)

declare option exist:serialize "media-type=application/xhtml+xml indent=yes method=xhtml"; 


session:create(),
let $user:=session:get-attribute("username")
let $type as xs:string := request:get-parameter('type','')

let $stylesheet-path as xs:string := 'xmldb:exist:///db/mdr/xforms/xforms-new.xsl'
let $xform-path as xs:anyURI := xs:anyURI(concat(lib-util:formsPath(), $type, '.xhtml'))

let $xform as node() := doc($xform-path)

let $parameters as node() := element parameters 
   {
      element param {
         attribute name {'new-identifier'},
         attribute value {lib-forms:generate-id()}
         },
      element param {
         attribute name {'user'},
         attribute value {$user}
         }         
   }

return
   <html xmlns="http://www.w3.org/1999/xhtml"
    xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" 
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179">
    {   transform:transform($xform, $stylesheet-path, $parameters)/* }
    </html>