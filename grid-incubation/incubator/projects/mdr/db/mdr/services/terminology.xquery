xquery version "1.0";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace text="http://exist-db.org/xquery/text";

declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";
declare namespace mesh="http://www.nlm.nih.gov/mesh/2006#";

let $term := request:get-parameter("term", "")
let $start as xs:integer := xs:integer(request:get-parameter("start", 1))
let $num as xs:integer := xs:integer(request:get-parameter("num", 10))
let $end := $start + $num

let $concepts := 
    for $concept in collection('/db/mdr/data/terminology')/rdf:RDF/rdf:Description
    where matches($concept/@skos:prefLabel, $term)
    order by $concept/@skos:prefLabel
    return
        $concept

let $total := count($concepts)
let $count := 
    if ($end <= $total)
    then $end
    else $total
    
return
    if ($start <= $count)
    then
        <result-set>
        {
            for $concept in $concepts[position() = ($start to $count)]
            return
                <concept>
                    <names>
                        <id>{data($concept/@rdf:about)}</id>
                        <preferred>{data($concept/@skos:prefLabel)}</preferred>
                        <all-names>
                        {
                            for $altName in ($concept/@skos:prefLabel|$concept/@skos:altLabel|$concept/@skos:hiddenLabel|$concept/@skos:altLabel|$concept/skos:hiddenLabel|$concept/skos:altLabel)
                            order by $altName
                            return
                                <name>{data($altName)}</name>
                        }
                        </all-names>
                    </names>
                    <definition>{(data($concept/skos:scopeNote), data($concept/@skos:scopeNote))}</definition>
                    <properties>
                    </properties>
            </concept>
        }
        </result-set>
    else $total