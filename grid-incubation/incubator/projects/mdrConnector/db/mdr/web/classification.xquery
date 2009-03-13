declare namespace transform = "http://exist-db.org/xquery/transform";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";   
   
import module namespace 
   lib-render-skos="http://www.cancergrid.org/xquery/library/rendering/skos" 
   at "../classification/m-lib-render-skos.xquery";  

import module namespace 
   lib-reasoning-skos="http://www.cancergrid.org/xquery/library/reasoning/skos" 
   at "../classification/m-lib-reasoning-skos.xquery";
   
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";  

import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   

import module namespace
   value-domain="http://www.cancergrid.org/xquery/library/value-domain"
   at "../library/m-value-domain.xquery";

import module namespace
value-meaning="http://www.cancergrid.org/xquery/library/value-meaning"
at "../library/m-value-meaning.xquery";

import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";
   
(:declare option exist:serialize "indent=true media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";:)

session:create(),
   
let $schemes as node() := lib-render-skos:schemes()
let $classifiedBy as xs:string* := request:get-parameter('term_id', ())
let $showScheme as xs:string* := request:get-parameter('showScheme',())
let $start as xs:integer := xs:integer(request:get-parameter("start",1))
let $extent as xs:integer := xs:integer(request:get-parameter("extent",5))
let $debug as xs:string := request:get-parameter('debug','false')
let $type as xs:string := request:get-parameter("type", "data_element")

let $filter as xs:string*:= lib-reasoning-skos:narrower($classifiedBy, lib-util:mdrElements('classification_scheme')//rdf:RDF/*)
let $title as xs:string := "Data Elements by Classifier"

let $all-items := 
    for $sorted in lib-util:mdrElements($type)
    [.//cgMDR:registration_status ne 'Superseded']
    let $preferred-name := administered-item:preferred-name($sorted)
    where exists(
        for $item in $filter
        where $item = $sorted//cgMDR:classified_by
        return 'true')
    order by lower-case($preferred-name)
    return $sorted
         
let $count-all-items as xs:integer := xs:integer(count($all-items))

let $displayed-items := 
    for $item at $record-id in $all-items
    where $record-id >= $start and $record-id < $start + $extent
    return $item
   
let $filter-links := 
    for $resource in $filter
    return
        <filter-link resource="{$resource}">
            {lib-render-skos:prefLabel('classification_scheme', $resource)}
        </filter-link>

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
        if ($type='classification_scheme')
        then lib-rendering:admin-item($displayed-items)
        else()
      )
                              
return
    lib-rendering:txfrm-webpage($title, 
           element content-by-classification {
      $schemes,
      element show-scheme {$showScheme},
      element term-id {$classifiedBy},
      element tabular-content {
            $content,
            element index {
            element action {'classification.xquery'},
            element previous {if ($start - $extent < 1) then (1) else ($start - $extent)},
            element next {$start + $extent},
            element last {$count-all-items - $extent},
            element type {$type},
            element start {$start},
            element extent {$extent},
            element count {$count-all-items}
            },
            element filter-links{$filter-links}
        }
        ,
            lib-render-skos:treeview($showScheme)}
    )
   

