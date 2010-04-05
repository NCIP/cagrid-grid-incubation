xquery version "1.0";

(: ~
: Module Name:             Data element tabular search
:
: Module Version           1.0
:
: Date                     06 March 2007
:
: Copyright                The cagrid consortium
:
: Module overview          Returns a reduced, tabular view on data elements
:
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace text="http://exist-db.org/xquery/text";

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

declare namespace session="http://exist-db.org/xquery/session";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $phrase as xs:string:= request:get-parameter("phrase", "")[1]
let $start as xs:integer := xs:integer(request:get-parameter("start",1))
let $extent as xs:integer := xs:integer(request:get-parameter("extent",5))
let $type as xs:string := request:get-parameter("type", "data_element") 
let $title as xs:string := concat(lib-util:sentence-case(lib-util:mdrElementTypeFriendly($type)), " Search")

let $all-items :=
   for $item in lib-util:search($type, $phrase)
   let $preferred-name := administered-item:preferred-name($item)
   order by lower-case($preferred-name)
   return $item

let $displayed-items := 
   for $item at $record-id in $all-items
   where $record-id >= $start and $record-id < $start + $extent
   return $item   
   
let $count-all-items as xs:integer := xs:integer(count($all-items))

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
        else()
    )

  
return
   lib-rendering:txfrm-webpage($title, 
        element content-by-search {
        element tabular-content {
        $content,
        element index {
            element action {'search.xquery'},
            element previous {if ($start - $extent < 1) then (1) else ($start - $extent)},
            element next {$start + $extent},
            element last { ($count-all-items mod $extent) * $extent +1},
            element type {$type},
            element start {$start},
            element extent {$extent},
            element count {$count-all-items},
            element recordlimit {if ($start + $extent <= $count-all-items) then ($start + $extent - 1) else ($count-all-items)},
            element phrase {$phrase}
        }
        }}
    )
    
    