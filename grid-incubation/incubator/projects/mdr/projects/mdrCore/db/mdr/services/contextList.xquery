xquery version "1.0";

(: ~
 : Module Name:             trial designer xml document
 :
 : Module Version           1.0
 :
 : Date                               31st October 2006
 :
 : Copyright                       The cagrid consortium
 :
 : Module overview          outputs the expected message for the trial designer plug-in
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
let $contextList := request:get-parameter("contextList", "")
return
    lib-search:contextList($contextList)
