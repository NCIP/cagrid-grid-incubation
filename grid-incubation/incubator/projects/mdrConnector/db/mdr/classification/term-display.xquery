declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";


import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   

let $about as xs:anyURI := xs:anyURI(concat('http://www.nlm.nih.gov/mesh/2006#', request:get-parameter("id", "")))

return
    element rdf:RDF {
        element rdf:description {
            attribute rdf:about {$about},
            for $rdf in lib-util:mdrElements('terminology')/*[@rdf:about=$about]
            return
            (
                for $prefLabel in $rdf/@skos:prefLabel
                return element skos:prefLabel {$rdf/@skos:prefLabel},
                for $prefLabel in $rdf/@skos:altLabel
                return element skos:altLabel {$rdf/@skos:altLabel},
                
                  $rdf/*
            )    ,              
            for $rdf in lib-util:mdrElements('terminology')//skos:Concept[skos:broader/@rdf:resource=$about]
            return
                <skos:narrower rdf:resource="{$rdf/@rdf:about}"/>,
            for $rdf in lib-util:mdrElements('terminology')//skos:Concept[skos:narrower/@rdf:resource=$about]
            return
                <skos:broader rdf:resource="{$rdf/@rdf:about}"/>
            }
        }