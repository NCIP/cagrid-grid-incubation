declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
element rdf:RDF {
    let $phrase as xs:string := xs:string(request:get-parameter("phrase", ""))
    
    for $rdf in lib-util:mdrElements('terminology')/rdf:Description
    where $rdf/@skos:prefLabel|=$phrase 
    or $rdf/@skos:altLabel|=$phrase 
    or $rdf/skos:altLabel|=$phrase
    or $rdf/skos:hiddenLabel|=$phrase
    return
        element rdf:Description {
            attribute rdf:about { $rdf/@rdf:about },
            for $prefLabel in $rdf/@skos:prefLabel
            return element skos:prefLabel {$rdf/@skos:prefLabel},
            for $prefLabel in $rdf/@skos:altLabel
            return element skos:altLabel {$rdf/@skos:altLabel},
            $rdf/*}
}