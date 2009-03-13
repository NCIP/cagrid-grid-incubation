xquery version "1.0";

(: ~
 : Module Name:             Review results webpage
 : Module Version           0.1
 : Date                     22nd January 2007
 : Copyright                The cancergrid consortium
 :
 : Module overview          Reports on the approval of a data element
 :
 :)
 
(:~
 :    @author Steve Harris
~ :)


import module namespace lib-util="http://www.cancergrid.org/xquery/library/util"
  at "../library/m-lib-util.xquery";

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    
    
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";

let $id as xs:string := request:get-parameter("id",'')

for $score in lib-util:mdrElements('data-element-rating')[data(./@admin-item-id) = $id]
return data($score/@rating)