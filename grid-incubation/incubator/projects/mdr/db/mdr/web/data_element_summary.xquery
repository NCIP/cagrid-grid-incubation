xquery version "1.0";

(: ~
 : Module Name:             Data element summary xml dump
 :
 : Module Version           3.0
 :
 : Date                            12th September 2006
 :
 : Copyright                    The cancergrid consortium
 :
 : Module overview        Returns a complete data element, together with all supporting classes including
 :                                       its value domain, data element concept, conceptual domain, object class
 :                                       property, representation class, related data elements, reference documents
 :                                       for viewing by the user of the metadata repository
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
declare namespace xqf =  "http://www.xqueryfunctions.com" ; 
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#"; 
declare namespace dcterms="http://purl.org/dc/terms/";


import module namespace 
    lib-util="http://www.cancergrid.org/xquery/library/util" 
    at "../library/m-lib-util.xquery";

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";
   
   import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery"; 

declare function  xqf:distinct-nodes 
  ( $nodes as node()* )  as node()* {

       
   for $node at $x in $nodes
   return $node[fn:not(some $otherNode
                       in fn:subsequence($nodes, 1, $x - 1)
                       satisfies $otherNode is $node)]
 
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $user := request:get-session-attribute("username")
let $compound_id := request:get-parameter("compound_id", "")
let $debug as xs:boolean := xs:boolean(request:get-parameter("debug","false"))
let $as-xml as xs:boolean := xs:boolean(request:get-parameter("as-xml","false"))

for $administered_item in lib-util:mdrElement("data_element",$compound_id)
    let $data_element_concept_id := data($administered_item//cgMDR:expressing)
    let $representation_class_id := data($administered_item//cgMDR:typed_by)
    let $value_domain_id := data($administered_item//cgMDR:representing)
    let $value_domain := lib-util:mdrElement("value_domain",$value_domain_id)
    let $data_element_concept := lib-util:mdrElement("data_element_concept",$data_element_concept_id)
    let $representation_class := lib-util:mdrElement("representation_class",$representation_class_id)
    let $datatype := lib-util:mdrElement("data_type", data($value_domain//cgMDR:value_domain_datatype))
    let $title := concat("Data Element Summary for ", administered-item:preferred-name($administered_item)) 
    
    let $triples := 
       for $classifier in $administered_item/cgMDR:classified_by
       for $term in lib-util:mdrElements("classification_scheme")//rdf:Description
       where data($classifier) = data($term/@rdf:about)
       return $term
    let $uom := lib-util:mdrElement("unit_of_measure", data($value_domain//cgMDR:value_domain_unit_of_measure))
    

    let $content :=
    document {
       element cgMDR:data_element_complete 
       {
        $administered_item,
        element supporting-classes {
           element resolver {doc(lib-util:getResourceLocation('resolver','directory.xml'))},
           element classifiers {$triples},
           element username {session:get-attribute("username")},
           $value_domain,
           $data_element_concept,

           (: conceptual domain :)
           if (exists($value_domain//cgMDR:representing))
           then 
              (
               lib-util:mdrElement("conceptual_domain", $value_domain//cgMDR:representing)         
              )
            else
            (
               xqf:distinct-nodes(
               for $conceptual_domain in lib-util:mdrElements("conceptual_domain")
               for $contained_in in $value_domain//cgMDR:contained_in
               where $conceptual_domain//cgMDR:value_meaning_identifier = $contained_in
               
               and $conceptual_domain//cgMDR:registration_status !='Superseded'
               return $conceptual_domain
               )
            ),
           
           $representation_class,
           $datatype,
           $uom,

           for $registration_authority in lib-util:mdrElements("registration_authority")
           where $registration_authority//cgMDR:registrar_identifier=$administered_item//cgMDR:registered_by
           return $registration_authority,

           for $organisation in lib-util:mdrElements("organization")
           where data($organisation//cgMDR:Contact/@contact_identifier)=$administered_item//cgMDR:administered_by
           or data($organisation//cgMDR:Contact/@contact_identifier)=$administered_item//cgMDR:submitted_by
           return $organisation,

           for $context_id in distinct-values($administered_item//cgMDR:context_identifier)
           return lib-util:mdrElement('context',$context_id),
        
           element reference-document-section {
           for $refdoc in lib-util:mdrElements("reference_document")
           for $refdoc_id in $administered_item//cgMDR:described_by
           where data($refdoc//@reference_document_identifier) = $refdoc_id
           return $refdoc
           },
        
           lib-util:mdrElement('object_class', $data_element_concept//cgMDR:data_element_concept_object_class),
           lib-util:mdrElement('property', $data_element_concept//cgMDR:data_element_concept_property),
        
           element related-data-elements {
              element asserted-here {
                 for $related in $administered_item//cgMDR:input_to/@deriving
                 return lib-util:mdrElement("data_element",data($related))
                 },
              element asserted-elsewhere {
                 for $related in lib-util:mdrElements('data_element')
                 where data($related//cgMDR:input_to/@deriving) = lib-util:mdrElementId($administered_item)
                 and $related//cgMDR:registration_status!='Superseded'
                 return $related           
                 }
              }
           }
        }
     }
                   
                   
return
    if ($debug)
    then $content
    else 
        if ($as-xml) 
        then 
            let $h1 := response:set-header('Content-type','text/xml')
            return
                util:serialize(transform:stream-transform($content, 
                concat('xmldb:exist://', lib-util:webPath(), 'stylesheets/cgMDR-ISO11179-transform.xsl'),
                <parameters/>), 'method=xml indent=yes media-type=test/xml')
        else
            lib-rendering:txfrm-webpage($title, $content, false(), false(), $compound_id, 'data_element_summary.xquery')
