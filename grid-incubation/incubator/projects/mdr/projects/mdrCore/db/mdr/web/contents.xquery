xquery version "1.0";

(: ~
: Module Name:             Data element tabular listing
:
: Module Version           2.0
:
: Date                     13 October 2009
:
: Copyright                The cancergrid consortium
:
: Module overview          Returns a reduced, tabular view on administered items
:
:)

(:~
:    @author Steve Harris
:    @version 2.0
~ :)

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace util="http://exist-db.org/xquery/util";

import module namespace 
lib-util="http://www.cagrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
administered-item="http://www.cagrid.org/xquery/library/administered-item" 
at "../library/m-administered-item.xquery";   

import module namespace
value-domain="http://www.cagrid.org/xquery/library/value-domain"
at "../library/m-value-domain.xquery";

import module namespace
value-meaning="http://www.cagrid.org/xquery/library/value-meaning"
at "../library/m-value-meaning.xquery";

import module namespace 
lib-rendering="http://www.cagrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";






(: declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd"; :)

session:create(),

let $letter := request:get-parameter("letter", ())
let $start as xs:integer := xs:integer(request:get-parameter("start", 1))
let $extent as xs:integer := xs:integer(request:get-parameter("extent", 5))
let $type as xs:string := request:get-parameter("type", "data_element")
let $title as xs:string := concat(lib-util:sentence-case(lib-util:mdrElementTypeFriendly($type)), " Listing")

let $all-items := 
   for $sorted in lib-util:mdrElements($type)[.//openMDR:registration_status ne 'Superseded'
   and starts-with(lower-case(.//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name), $letter)]
   let $preferred-name := administered-item:preferred-name($sorted)
   order by lower-case($preferred-name)
   return $sorted
   
let $count-all-items as xs:integer := xs:integer(count($all-items))

let $displayed-items := 
   for $item at $record-id in $all-items
   where $record-id >= $start and $record-id < $start + $extent
   return $item
   
let $content as element()* := 
    (
        if ($type='data_element')
        then lib-rendering:data-element-reduced($displayed-items)
        else (),
        if ($type='value_domain')
        then lib-rendering:value-domain-reduced($displayed-items)
        else (),
        if ($type='conceptual_domain')
        then lib-rendering:conceptual-domain-reduced($displayed-items)
        else (),
        if ($type='data_element_concept')
        then lib-rendering:admin-item($displayed-items)
        else(),
        if ($type='object_class')
        then lib-rendering:admin-item($displayed-items)
        else(),
        if ($type='property')
        then lib-rendering:admin-item($displayed-items)
        else(),
        if ($type='organisation')
        then lib-rendering:admin-item($displayed-items)
        else()
    )
    
return
    lib-rendering:txfrm-webpage($title, 
        element content-by-letter {
        element tabular-content {
        $content,
        element index {
            element action {'contents.xquery'},
            element previous {if ($start - $extent < 1) then (1) else ($start - $extent)},
            element next {$start + $extent},
            element last { if( ($count-all-items mod $extent)=0) then ((($count-all-items idiv $extent) - 1) * $extent + 1) else( ( ($count-all-items idiv $extent) * $extent) + 1) },
            element type {$type},
            element letter {$letter},
            element start {$start},
            element extent {$extent},
            element count {$count-all-items},
            element recordlimit {if ($start + $extent <= $count-all-items) then ($start + $extent - 1) else ($count-all-items)}
        }
        }}
    )