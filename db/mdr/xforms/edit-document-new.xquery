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
   

declare option exist:serialize "media-type=application/xhtml+xml indent=yes method=xhtml"; 
(: doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";:)

session:create(),
let $user:=session:get-attribute("username")
let $id as xs:string := request:get-parameter("id","")

(:value domain fudge - next version will have a simpler, joint schema :)
let $rough-type as xs:string := request:get-parameter("type","")

let $type as xs:string :=
   if ($rough-type = 'value_domain')
   then (lower-case(local-name(lib-util:mdrElement('value_domain', $id))))
   else (
      if ($rough-type = 'conceptual_domain')
      then (lower-case(local-name(lib-util:mdrElement('conceptual_domain', $id))))
      else ($rough-type))
   
let $cd-path  := 
   if ($type='enumerated_value_domain')
   then (
      let $admin-item as node():= lib-util:mdrElement('value_domain', $id)
      return
         xs:anyURI(concat(substring-before(request:get-url(),'/db'),lib-util:getCollectionPath('conceptual_domain'),'/',$admin-item//cgMDR:representing,'.xml'))
      )
   else ()

let $stylesheet-path as xs:anyURI := xs:anyURI('xmldb:exist:///db/mdr/xforms/xforms-replace-model.xsl')

let $xform-path as xs:anyURI := xs:anyURI(concat(lib-util:formsPath(), $type, '.xhtml'))

let $xform as node() := doc($xform-path)

let $instance-path as xs:anyURI := xs:anyURI(concat(substring-before(request:get-url(),'/db'),lib-util:getCollectionPath($rough-type), '/', $id, '.xml'))
let $write-to-path as xs:anyURI := xs:anyURI(replace($instance-path,'rest','webdav'))

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
 
   <html xmlns="http://www.w3.org/1999/xhtml" xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" 
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179">
    {transform:transform($xform, $stylesheet-path, $parameters)/*}
    </html>