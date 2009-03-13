module namespace lib-reasoning-skos="http://www.cancergrid.org/xquery/library/reasoning/skos";

(: ~
: Module Name:             Reasoning across SKOS.xml
: Module Version           1.0
: Date                     27-11-2007
: Copyright                The cancergrid consortium
: Module overview          Does limited reasoning on a SKOS file 
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)

declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";


 

declare function lib-reasoning-skos:narrower(
   $about as xs:string?, 
   $terms as element()* 
   ) as xs:string*
{
    if ($about) 
    then (
        distinct-values(
            ($about, 
            for $resource-uri in $terms[string(./@rdf:about) = $about]/skos:narrower/@rdf:resource
            return lib-reasoning-skos:narrower(xs:string($resource-uri), $terms),
            for $resource-uri in $terms[string(skos:broader/@rdf:resource) = $about]/@rdf:about
            return lib-reasoning-skos:narrower(xs:string($resource-uri), $terms),
            for $related in $terms[string(./@rdf:about) = $about]/skos:related/@rdf:resource
            return xs:string($related)
             ))
        )
    else()
         
};

declare function lib-reasoning-skos:broader(
   $about as xs:string?, 
   $terms as element()* 
   ) as xs:string*
{
    if ($about) 
    then (
        distinct-values(
            ($about, 
            for $resource-uri in $terms[string(./@rdf:about) = $about]/skos:broader/@rdf:resource
            return lib-reasoning-skos:broader(xs:string($resource-uri), $terms),
            for $resource-uri in $terms[string(skos:narrower/@rdf:resource) = $about]/@rdf:about
            return lib-reasoning-skos:broader(xs:string($resource-uri), $terms)
             ))
        )
    else()
         
};
