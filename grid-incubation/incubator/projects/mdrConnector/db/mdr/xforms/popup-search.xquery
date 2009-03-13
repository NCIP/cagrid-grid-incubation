xquery version "1.0";

(: ~
: Module Name:             General search popup
:
: Module Version           1.0
:
: Date                     11 feb 2008
:
: Copyright                The cancergrid consortium
:
: Module overview          Finds a document to place on the end of a relationship
:
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)


declare namespace request="http://exist-db.org/xquery/request";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace session="http://exist-db.org/xquery/session";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";      
   
import module namespace 
  lib-forms="http://www.cancergrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
import module namespace 
  lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
  at "../web/m-lib-rendering.xquery";  


session:create(),
let $type as xs:string := request:get-parameter("type", "")
let $phrase as xs:string := request:get-parameter("phrase", "")
let $start as xs:integer := xs:integer(request:get-parameter("start", "1"))
let $count as xs:integer := xs:integer(request:get-parameter("count", "10"))
let $friendly-name as xs:string := lib-util:mdrElementTypeFriendly($type)
let $target-node as xs:string := request:get-parameter("node", "")

let $results as element()* :=
   (
      for $doc as element()* at $pos in lib-util:mdrElements($type)
      let $id as xs:string := lib-util:mdrElementId($doc)
      let $name as xs:string := lib-util:mdrElementName($doc)
      
      where (
         if ($doc//cgMDR:registration_status)
         then ($doc//cgMDR:registration_status ne 'Superseded')
         else true()
         )
         and contains($name, $phrase)
      order by $name
      return <result id="{$id}" target-node-name="{$target-node}">{$name}</result>
   )
   
let $count-results as xs:integer := count($results)   

return
   transform:stream-transform(
      <result-set type="{$type}" 
         phrase="{$phrase}" 
         start="{$start}" 
         count="{$count}"
         friendly-name="{$friendly-name}"
         count-results="{$count-results}"
         node="{$target-node}">
      {
         for $result at $pos in $results
         where $pos >= $start and $pos < $start + $count
         return $result           
      }
      </result-set>,
      concat("xmldb:exist://" ,lib-util:formsPath(), "/xforms-popup.xsl"),
      <parameters/>)
 
