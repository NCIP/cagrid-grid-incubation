xquery version "1.0";

(: ~
 : Module Name:             Delete 'steves test' data elements
 :
 : Module Version           1.0
 :
 : Date                     21st October 2008
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          Tidies the registry by removing test case data elements and classes
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

import module 
   namespace lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
  
import module namespace 
  lib-util="http://www.cancergrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";
  
<query>
{
for $element in lib-util:mdrElements()[contains(.//cgMDR:name,'steve')]
return update delete $element/*
}
</query>
    
