xquery version "1.0";

(: ~
 : Module Name:             Data Element Summary service
 :
 : Module Version           1.0
 :
 : Date                     26 November 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          openMDR REST service outputs the Data Element Summary
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
 :    @version 0.1
~ :)

import module namespace 
   lib-search="http://www.cagrid.org/xquery/library/search" 
   at "m-lib-search.xquery";    


declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

declare option exist:serialize "method=xml media-type=text/xml"; 

session:create(),
let $term := request:get-parameter("term", "")
let $exactTerm := request:get-parameter("exactTerm", "")
let $publicId := request:get-parameter("publicID","")
let $start as xs:integer := xs:integer(request:get-parameter("start", 0))
let $num as xs:integer := xs:integer(request:get-parameter("num", 10))
return
    lib-search:dataElementSummary($term, $exactTerm, $publicId, $start, $num)