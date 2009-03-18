xquery version "1.0";

(: ~
 : Module Name:             trial designer xml document
 :
 : Module Version           1.0
 :
 : Date                               19th March 2008
 :
 : Copyright                       The cancergrid consortium
 :
 : Module overview          outputs the expected message for the trial designer plug-in
 :
 :)
 
(:~
  :    @author Andrew Tsui
 :    @version 0.1
~ :)

import module namespace 
   lib-search="http://www.cancergrid.org/xquery/library/search" 
   at "m-lib-search.xquery";    

declare namespace request="http://exist-db.org/xquery/request";
declare namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "method=xml media-type=text/xml"; 

let $term := request:get-parameter("term", "*")
let $scheme as xs:anyURI := xs:anyURI(request:get-parameter("scheme", "http://www.cancergrid.org/ontologies/data-element-classification"))
let $classified-by := request:get-parameter("classified-by", ())
let $start as xs:integer := xs:integer(request:get-parameter("start", 0))
let $num as xs:integer := xs:integer(request:get-parameter("num", 11))
return
if ($classified-by)
then lib-search:dataElementListSearchByClassification($term, xs:anyURI(concat($scheme,"#",$classified-by)) ,$start, $num)
else lib-search:dataElementListSearch($term, $start, $num)
