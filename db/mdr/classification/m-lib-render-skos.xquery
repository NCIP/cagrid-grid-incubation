module namespace lib-render-skos="http://www.cancergrid.org/xquery/library/rendering/skos";

(: ~
: Module Name:             Classification Extraction from SKOS.xml
:
: Module Version           1.0
:
: Date                     26-11-2007
:
: Copyright                The cancergrid consortium
:
: Module overview          Reders a SKOS file as a treeview
:
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)



declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";
declare namespace dc="http://purl.org/dc/elements/1.1/";

import module namespace 
   lib-reasoning-skos="http://www.cancergrid.org/xquery/library/reasoning/skos" 
   at "../classification/m-lib-reasoning-skos.xquery";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
   
declare function lib-render-skos:node($item as node(), $col as xs:string?) as node()*
{
    attribute title {$item/skos:prefLabel},
    attribute img {
        if ($col='red')
        then ("images/red_circle.gif")
        else ("images/white_circle.gif")
        },
    attribute expanded {"true"}
};

declare function lib-render-skos:get-child-nodes(
                     $triples as element()*, 
                     $resource as xs:anyURI) as element()*
    {
    for $item in $triples[@rdf:about = $resource]
    return
         element folder
            {
            (
            lib-render-skos:node($item, ()),
            attribute code {string($resource)}
            ,
            (for $narrower in $item/skos:narrower
            return
               lib-render-skos:get-child-nodes($triples, $narrower/@rdf:resource),
            for $narrower-asserted in $triples[skos:broader/@rdf:resource=$resource]
            return
                lib-render-skos:get-child-nodes($triples, $narrower-asserted/@rdf:about),
            if ($item/skos:related)
            then (lib-render-skos:related-nodes($triples, $item))
            else ())
            )
            }
    };
    
declare function lib-render-skos:related-nodes($triples as element()*, $item as element()*) as element()*    
{
   <folder title="see also..." img="images/red_circle.gif" expanded="false">
      {
      for $related in $item/skos:related/@rdf:resource
      let $related-item := $triples[@rdf:about = $related]
      return
         element folder {
            lib-render-skos:node($item, 'red'),
            attribute code {string($related)}
         }
      }
   </folder>
};

declare function lib-render-skos:treeview($schemes as xs:string*)
{
   let $triples :=
      if ($schemes)
      then 
         (
         let $scheme-uris :=
             for $scheme in $schemes
             return data(lib-util:mdrElements('classification_scheme')/skos:ConceptScheme[cgMDR:administrationRecord/@rdf:resource=$scheme]/@rdf:about)

         for $scheme-uri in $scheme-uris
         return
             (
             lib-util:mdrElements('classification_scheme')/*[skos:inScheme/@rdf:resource=$scheme-uri],
             lib-util:mdrElements('classification_scheme')/*[@rdf:about=$scheme-uri])
         )
      else
         (
         lib-util:mdrElements('classification_scheme')/*
         )
   
   return 
   
         <treeview>
            <folder title="Metadata Registry Classification Schemes" 
               img="images/white_circle.gif" 
               expanded="true"
               alt="Metadata Registry Classification Scheme">
               {
               for $topConcept in $triples/skos:hasTopConcept
               return lib-render-skos:get-child-nodes($triples, $topConcept/@rdf:resource)
               }   
            </folder>
         </treeview>
};

declare function lib-render-skos:prefLabel($type as xs:string, $resource as xs:string) as xs:string?
{
   for $triples in lib-util:mdrElements($type)//skos:Concept[string(@rdf:about) = string($resource)]
   let $prefLabel := $triples/skos:prefLabel
   return string($prefLabel)
};

declare function lib-render-skos:schemes() as node()
{
    element loaded-schemes
        {
            for $scheme in lib-util:mdrElements('classification_scheme')[exists(.//cgMDR:administered_item_administration_record)]
            return
                <loaded-scheme uri="{lib-util:mdrElementId($scheme)}">
                    {lib-util:mdrElementName($scheme)}
                </loaded-scheme>
        }
    
};