xquery version "1.0";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
  
(:~
 : links-ref
 :
 : Copyright (C) 2007 The CancerGrid Consortium
 : @author Maria Lin
 :)


import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";

import module namespace 
    rendering="http://www.cancergrid.org/xquery/library/rendering"
    at "../web/m-lib-rendering.xquery";


declare option exist:serialize "method=xhtml media-type=text/xml"; 


declare function local:test-links($collection as xs:string) as element()* {

for $item in collection($collection)//@href
    let $docuri := document-uri($item)
    let $id := data($item)
    let $path := concat(lib-util:getServer(), "/exist/rest", lib-util:webPath(), $id)
    return
	element fromlink {
		attribute id {$docuri},
	      element tolink {
		    attribute path {$path}
		}
      }
};

let $collection := "/db/mdr/web"
return
element link-refs {
     local:test-links($collection)
}