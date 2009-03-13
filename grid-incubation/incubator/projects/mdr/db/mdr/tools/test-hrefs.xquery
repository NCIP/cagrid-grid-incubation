xquery version "1.0";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace xsl= "http://www.w3.org/1999/XSL/Transform";

(:~
 : links-crawler
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

declare function local:xmldb-path($location as xs:string, $file as xs:string) as xs:string
{
   concat('xmldb:exist://',lib-util:getResourcePath($location),$file)
};

declare function local:absolute-path($location as xs:string, $file as xs:string) as xs:anyURI
{
   concat(substring-before(request:get-url(), 'tools'),$location, "/", $file) cast as xs:anyURI
};

declare function local:db-path($location as xs:string, $file as xs:string) as xs:anyURI
{
   concat(lib-util:rootURI(),"/",$location, "/", $file) cast as xs:anyURI
};


declare function local:pathURI($path as xs:string) as xs:anyURI
{
   xs:anyURI($path)
};

declare function local:set-path($href as xs:string) as xs:string
{
   if (starts-with($href,"http"))
   then ($href)
   else (if (contains($href,"../"))
	   then (concat(lib-util:rootURI(),"/",substring-after($href,"../")) )
	   else (concat(lib-util:webPath(), $href) )
	   )
};

declare function local:check-path($path as xs:string) as xs:string
{
   if (util:binary-doc-available($path))
   then "true"
   else if (contains($path,"{$link}"))
	  then "unknownLink"
	  else if (contains($path,"$collection"))
	       then "unknownColl"
		 else if (contains($path,"$ref-doc-uri"))
		 	then "unknownDoc"
		 	else if (contains($path,"{uri}"))
				then "unknown"
				else if (contains($path,"@img"))
					then "unknown"
					else "false"
   

};

declare function local:check-links($docuri as xs:string) as element()* {
    for $doc in (doc($docuri)//xsl:with-param[@name="link"])

(: skip as-xml-link :)
      let $href := if (data($doc)>"")
			 then (data($doc))
			 else if (data($doc/xsl:value-of/@select)>"")
				then (concat("{",data($doc/xsl:value-of/@select),"}"))
				else ("")

  	let $path := local:set-path($href)
 	let $avail :=  local:check-path($path) 
	return
		if ($href >"") 
		then (element tolink {
		    attribute url {$path},
		    attribute type {"link"},
		    attribute href {$href},
		    attribute avail {$avail}
		}
        )
		else ()
};


(: There are different formats of href:
   1. "http" - ref to external reference, defaults to true
   2. "../"  - local reference, change to 
		e.g. from "/db/mdr/web/../model" to "/db/mdr/model"
   3. default to be under "/db/mdr/web"
:)


declare function local:test-links($collection as xs:string) as element()* {
	local:test-href($collection),
	local:test-src($collection)
};


declare function local:test-href($collection as xs:string) as element()* {

for $item in (collection($collection)//@href)     
let $docuri := document-uri($item)
    let $href := data($item)
    let $path := local:set-path($href)

    let $avail :=  if (starts-with($href,"http"))
			   then "true"
			   else local:check-path($path)
      return
	element fromlink {
		attribute url {$docuri}, 
		if (contains($href,"{$link}"))
		then (local:check-links($docuri))
		else (
			element tolink {
		    attribute url {$path},
		    attribute type {"href"},
		    attribute href {$href},
		    attribute avail {$avail}
		    }
		)
      }
};

declare function local:test-src($collection as xs:string) as element()* {

for $item in (collection($collection)//@src)     
let $docuri := document-uri($item)
    let $href := data($item)
    let $path := local:set-path($href)

    let $avail :=  if (starts-with($href,"http"))
			   then "true"
			   else local:check-path($path)
      return
	element fromlink {
		attribute url {$docuri},
	      element tolink {
		    attribute url {$path},
		    attribute type {"src"},
		    attribute href {$href},
		    attribute avail {$avail}
		}
      }

};


let $collection := "/db/mdr/web"
let $content as element() := element link-results {
     local:test-links($collection)
}
return
rendering:txfrm-webpage("Href Link references", $content)
