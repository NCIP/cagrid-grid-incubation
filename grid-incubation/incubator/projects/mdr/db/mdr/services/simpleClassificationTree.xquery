import module namespace 
   lib-search="http://www.cancergrid.org/xquery/library/search" 
   at "m-lib-search.xquery";

declare namespace request="http://exist-db.org/xquery/request";

declare option exist:serialize "method=xml media-type=text/xml"; 

let $scheme := request:get-parameter("scheme", ())
return
    if ($scheme)
    then <result-set>{lib-search:simpleClassificationTree($scheme)}</result-set>
    else ()
