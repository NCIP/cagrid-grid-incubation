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
   

declare option exist:serialize "media-type=application/xhtml+xml method=html doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
session:create(),
let $user:=session:get-attribute("username")
let $id as xs:string := request:get-parameter("id","")
let $admin-item as element() := lib-util:mdrElement($id)


let $type as xs:string := lower-case(local-name($admin-item))

let $stylesheet-path as xs:string := 'xmldb:exist:///db/mdr/xforms/xforms-replace-model.xsl'
let $xform-path as xs:anyURI := concat(lib-util:formsPath(), $type, '.xhtml')

let $xform as node() := doc($xform-path)

let $instance-path as xs:anyURI := concat(substring-before(request:get-url(),'/db'),document-uri($admin-item))
let $write-to-path as xs:anyURI := replace($instance-path,'rest','webdav')

let $cd-path as xs:anyURI := concat(substring-before(request:get-url(),'/db'),lib-util:getCollectionPath('conceptual_domain'),'/',$admin-item//cgMDR:representing,'.xml')

let $parameters as node() := 
            element parameters {
                  element param {
                     attribute name {'instance-path'},
                     attribute value {$instance-path}
                  },
                  element param {
                     attribute name {'write-to-path'},
                     attribute value {$write-to-path}
                  },
                  element param {
                     attribute name {'user'},
                     attribute value {$user}
                  },                       
                  element param {
                     attribute name {'new-id'},
                     attribute value {lib-forms:generate-id()}
                  },
                  element param {
                     attribute name {'cd-path'},
                     attribute value {$cd-path}
                  }                                    

               }
    
  
return
   <html xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns="http://www.w3.org/1999/xhtml"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179">
    {   transform:transform($xform, $stylesheet-path, $parameters)/*}
    </html>

