import module namespace lib-qs="http://www.cancergrid.org/xquery/library/query_service" at "m-lib-qs.xqm";

declare namespace request="http://exist-db.org/xquery/request";

let $resource := request:get-parameter("resource", ())
let $term := request:get-parameter("term", "*")
let $src := request:get-parameter("src", ())
let $startIndex := xs:int(request:get-parameter("startIndex", 0))
let $numResults := xs:int(request:get-parameter("numResults", 5))
return
    if ($resource)
    then
        lib-qs:query($resource, $src, $term, $startIndex, $numResults)
    else ()
   