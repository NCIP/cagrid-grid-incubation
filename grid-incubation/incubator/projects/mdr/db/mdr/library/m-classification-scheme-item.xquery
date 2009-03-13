module namespace classification-scheme-item="http://www.cancergrid.org/xquery/library/classification-scheme-item";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    

declare function classification-scheme-item:resolved-instance($identifier as xs:string) as node()
{
    let $document := lib-util:mdrElement("classification_scheme_item",$identifier)
    let $classification_scheme_item_identifier := data($document//@classification_scheme_item_identifier)
    let $classification_scheme_identifier := data($document//@contained_in)
    let $scheme_name := administered-item:preferred-name("classification_scheme", $document//@contained_in)
    let $term_name := $document//cgMDR:classification_scheme_item_value
    let $classification_scheme_anchor := administered-item:html-anchor("classification_scheme",$classification_scheme_identifier)
    return
    <term>
        <identifier>{$classification_scheme_item_identifier}</identifier>
        <from_scheme>{$classification_scheme_identifier}</from_scheme>
        <from_scheme_name>{$scheme_name}</from_scheme_name>
        <anchor>{$classification_scheme_anchor}</anchor>
        <name>{$term_name}</name>
        {
        for $related_term in $document//cgMDR:association
        return
        <related_term>
            <identifier>{data($related_term/@associationTarget)}</identifier>
            <relation>{$related_term//cgMDR:classification_scheme_item_relationship_type_description}</relation>
        </related_term>
        }
    </term>
};

declare function classification-scheme-item:resolved-instance($identifier as xs:string, $isSKOS as xs:string) 

(:as node():)
{
    let $triples := lib-util:mdrElements("classification_scheme")//rdf:Description
    let $scheme := string($triples[@rdf:about = $identifier]/skos:inScheme/@rdf:resource)
    let $term_name := string($triples[@rdf:about = $identifier]/skos:prefLabel)
    let $classification_scheme_identifier := string($triples//rdf:Description[@rdf:about = $scheme]/cgMDR:administrationRecord/@rdf:resource)
    let $scheme_name := administered-item:preferred-name("classification_scheme", $classification_scheme_identifier )
    let $classification_scheme_anchor := administered-item:html-anchor("classification_scheme",$classification_scheme_identifier)
    return
    <term>
        <identifier>{$identifier}</identifier>
        <from_scheme>{$classification_scheme_identifier}</from_scheme>
        <from_scheme_name>{$scheme_name}</from_scheme_name>
        <anchor>{$classification_scheme_anchor}</anchor>
        <name>{$term_name}</name>
    </term>
   };