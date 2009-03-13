import module namespace 
   lib-search="http://www.cancergrid.org/xquery/library/search" 
   at "m-lib-search.xquery";

declare option exist:serialize "method=xml media-type=text/xml"; 

<result-set>
{
lib-search:listClassificationSchemes()
}
</result-set>