module namespace lib-search="http://www.cagrid.org/xquery/library/search";

(: ~
 : Module Name:             trial designer xml document
 :
 : Module Version           1.0
 :
 : Date                               31st October 2006
 :
 : Copyright                       The cagrid consortium
 :
 : Module overview          outputs the expected message for the trial designer plug-in
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @author Andrew Tsui
 :    @author Maria Lin
 :    @version 0.1
~ :)

  
import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace
   value-domain="http://www.cagrid.org/xquery/library/value-domain"
   at "../library/m-value-domain.xquery"; 
   
import module namespace 
   administered-item="http://www.cagrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    

import module namespace
value-meaning="http://www.cagrid.org/xquery/library/value-meaning"
at "../library/m-value-meaning.xquery";

import module namespace 
      lib-qs="http://www.cagrid.org/xquery/library/query_service" 
      at "../connector/m-lib-qs.xquery";  

declare namespace session="http://exist-db.org/xquery/session";
declare namespace rs="http://cagrid.org/schema/result-set";
declare namespace op="http://www.w3.org/2002/11/xquery-operators";
declare namespace c="http://cagrid.org/schema/config";
declare namespace q="http://cagrid.org/schema/query";
   
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace util="http://exist-db.org/xquery/util";

(: Classification scheme related namespace :)
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";

declare function lib-search:getURL() as xs:string {
	util:catch("java.lang.Exception",
	    lib-search:restHttpURL(),
	    lib-search:soapHttpURL()
	)
};

declare function lib-search:DataEleSummaryURL($name as xs:string) as xs:string {
	concat(lib-search:getURL(),lib-util:webPath(),'data_element_summary.xquery?compound_id=',$name)
};


declare function lib-search:DataEleSummaryXmlURL($name as xs:string) as xs:string {
	concat(lib-search:getURL(),lib-util:webPath(),'data_element_summary.xquery?compound_id=',$name)
};

declare function lib-search:restHttpURL() as xs:string {
	concat('http://',request:request-servername(),':',request:request-serverport(),'/exist/rest')
};

declare function lib-search:soapHttpURL() as xs:string {
	concat('http://localhost:8080','/exist/rest')
};


(:
declare function lib-search:dataElementListSearch($term as xs:string, 
	$start as xs:string, $num as xs:string) as node() {
:)
(: let $term := replace(xs:string("${term}"), "\*", ".*") :)
(:
let $term := replace($term,"\*", ".*")
let $startIndex := xs:integer($start)
let $numResults := xs:integer($num)


let $data-elements := 
    for $de in lib-util:mdrElements('data_element')
    where matches(administered-item:preferred-name($de), $term)
    return $de
let $count := count($data-elements)
let $endIndex := if ($startIndex+$numResults <= $count)
then $startIndex+$numResults
else $count
return
    if ($startIndex > $count)
    then <cg_service><nil/></cg_service>
    else
        <cg_service xmlns:xs="http://www.w3.org/2001/XMLSchema">
        {
            for $index in ($startIndex+1 to $endIndex)
            return
           (: local:getDataElementForTrialDesigner($data-elements[$index]) :)
		lib-search:getDataElementForTrialDesigner($data-elements[$index])

        }
    </cg_service>
};
:)

(:
declare function lib-search:getDataElementForTrialDesigner($data-element as node()) as node() {
:)
(: let $data-element := lib-util:mdrElement('data_element',$compound_id) :)
(:
let $long-name :=administered-item:preferred-name($data-element)
let $name := lib-util:mdrElementId($data-element)
let $version := $data-element//@version
let $definition := administered-item:preferred-definition($data-element)
let $value-domain := lib-util:mdrElement('value_domain',$data-element//openMDR:representing)
let $data-type :=  value-domain:datatype($value-domain)
return
   element element {
      attribute name {$name},
      attribute version {$version},
      element longName {$long-name},
      element publicID {$name},
      element id {$name},
      element preferredDefinition {$definition},
      element webURL {lib-search:DataEleSummaryURL($name)},
      element xmlURL {lib-search:DataEleSummaryXmlURL($name)},
      element dataType {
         element xs:simpleType {
            element xs:restriction {
               attribute base {$data-type},
               if ( value-domain:type($value-domain) = 'enumerated value domain') then
                  (
                   for $containing in $value-domain//openMDR:Enumerated_Value_Domain/openMDR:containing
                   let $value := data($containing/openMDR:value_item)
                   order by $value
                   return element xs:enumeration {attribute value {$value}}
                  )
               else ()
               }
            }
         },
      element concepts {}
      }
};
:)

declare function lib-search:dataElementSearch($compound_id as xs:string) as element()* 
{
let $data-element := lib-util:mdrElement('data_element',$compound_id)
let $long-name :=administered-item:preferred-name($data-element)
let $name := lib-util:mdrElementId($data-element)
let $version := $data-element//@version
let $definition := administered-item:preferred-definition($data-element)
let $value-domain := lib-util:mdrElement('value_domain',$data-element//openMDR:representing)
let $data-type :=  value-domain:datatype($value-domain)
return
   element element {
      attribute name {$name},
      attribute version {$version},
      element longName {$long-name},
      element publicID {$name},
      element id {$name},
      element preferredDefinition {$definition},
      element webURL {lib-search:DataEleSummaryURL($name)},
      element xmlURL {lib-search:DataEleSummaryXmlURL($name)},
      element dataType {
         element xs:simpleType {
            element xs:restriction {
               attribute base {$data-type},
               if ( value-domain:type($value-domain) = 'enumerated value domain') then
                  (
                   for $containing in $value-domain//openMDR:Enumerated_Value_Domain/openMDR:containing
                   let $value := data($containing/openMDR:value_item)
                   order by $value
                   return element xs:enumeration {attribute value {$value}}
                  )
               else ()
               }
            }
         },
      element concepts {}
      }
};

declare function lib-search:dataElement($compound_id as xs:string) as element()
{
         let $administered-item := lib-util:mdrElement("data_element", $compound_id)
         let $administered-item-id := lib-util:mdrElementId($administered-item)
         let $value-domain-id := data($administered-item//openMDR:representing)
         let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
         let $data-type := lib-util:mdrElement("data_type", data($value-domain//openMDR:value_domain_datatype))
         let $uom := lib-util:mdrElement("unit_of_measure", data($value-domain//openMDR:value_domain_unit_of_measure))
         let $preferred-name := $administered-item//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name
         return
            <data-element>
               <names>
                  <id>{$administered-item-id}</id>
                  <preferred>{data($preferred-name)}</preferred>
                  <all-names>
                  {
                      for $name in $administered-item//openMDR:name[. != $preferred-name]
                      return <name>{data($name)}</name>
                  }
                  </all-names>
               </names>
               <definition>{administered-item:preferred-definition($administered-item)}</definition>
               <values> 
               {               
                  if (value-domain:type($value-domain) = 'enumerated value domain')
                  then 
                  (
                      <enumerated>
                      {
                         for $value in $value-domain/openMDR:containing[openMDR:value_item >""]
                         order by $value/openMDR:value_item
                         return
                             <valid-value>
                                 <code>{data($value/openMDR:value_item)}</code>
                                 <meaning>
                                 {
                                     data(lib-util:mdrElements("conceptual_domain")
                                         [.//openMDR:registration_status != 'Superseded']
                                         /openMDR:Value_Meaning[openMDR:value_meaning_identifier=$value/openMDR:contained_in]/openMDR:value_meaning_description)
                                 }
                                 </meaning>
                             </valid-value>
                      }
                      </enumerated>
                     )
                  else(
                      <non-enumerated>
                         <data-type>{data($data-type//openMDR:datatype_name)}</data-type>
                         <units> 
                         {
                            if (data($uom//openMDR:unit_of_measure_name)>"")
                            then (data($uom//openMDR:unit_of_measure_name))
                            else ("(not applicable)")
                         }
                         </units>
                     </non-enumerated>
                  )
               }
               </values>
            </data-element>
};

declare function lib-search:dataElementListSearch($term as xs:string, $start as xs:integer, $num as xs:integer) as node() 
{
   let $all-admin-items := 
       for $sorted in lib-util:search("data_element", $term)
       let $preferred-name := $sorted//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name
       order by $preferred-name 
       return 
           $sorted

    let $count-all-admin-items := count($all-admin-items)    

    let $content as element() :=
        <result-set>
           {
              for $administered-item at $record-id in $all-admin-items
                 let $administered-item-id := lib-util:mdrElementId($administered-item)
                 let $value-domain-id := data($administered-item//openMDR:representing)
                 let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
                 let $data-type := lib-util:mdrElement("data_type", data($value-domain//openMDR:value_domain_datatype))
                 let $uom := lib-util:mdrElement("unit_of_measure", data($value-domain//openMDR:value_domain_unit_of_measure))
                 let $preferred-name := data($administered-item//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name)
             where $record-id >= $start and $record-id <= $start + $num 
             return
                   element data-element
                   {
                      element names 
                      {
                          element id {$administered-item-id},
                          element preferred {$preferred-name},
                          element all-names 
                          {
                              for $name in $administered-item//openMDR:name
                              where data($name) != $preferred-name  
                              return element name {data($name)}
                          }
                      },
                     element definition {administered-item:preferred-definition($administered-item)},
                     element values 
                     {
                         if (value-domain:type($value-domain) = 'enumerated value domain')
                         then
                             element enumerated 
                             {
                                 for $value in $value-domain//openMDR:containing
                                 where $value/openMDR:value_item >""
                                 order by $value/openMDR:value_item
                                 return
                                     element valid-value
                                     {
                                         element code {data($value/openMDR:value_item)},
                                         element meaning {data(value-meaning:value-meaning($value/openMDR:contained_in)//openMDR:value_meaning_description)}
                                     }
                              }
                          else
                          element non-enumerated
                          {
                             element data-type {data($data-type//openMDR:datatype_name)},
                             element units 
                             {
                                if (data($uom//openMDR:unit_of_measure_name)>"")
                                then (data($uom//openMDR:unit_of_measure_name))
                                else ("(not applicable)")}
                            }
                       }
                   }
            }
        </result-set>
   return
       $content
          
};

(: Returns Data Element Summary :)
declare function lib-search:dataElementSummary($term as xs:string, $exactTerm as xs:string, $publicId as xs:string, $start as xs:integer, $num as xs:integer) as node() 
{
       let $all-admin-items := 
        if($publicId != "" and $term = "")
            then(
                for $sorted in lib-util:mdrElement("data_element", $publicId)
                let $preferred-name := $sorted//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name
                order by $preferred-name 
                return 
                    $sorted
                )
        else if($publicId="" and $term !="")
            then(
                for $sorted in lib-util:search("data_element", $term)
                let $preferred-name := $sorted//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name
                order by $preferred-name 
                return 
                    $sorted
               )
        else if($publicId="" and $exactTerm !="")
            then(
                for $sorted in lib-util:exactSearch("data_element", $exactTerm)
                let $preferred-name := $sorted//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name
                order by $preferred-name 
                return 
                    $sorted
               )
        else()
   
    let $count-all-admin-items := count($all-admin-items)    

    let $content as element() :=
        <result-set namespace="http://cagrid.org/schema/result-set">
           {
              for $administered-item at $record-id in $all-admin-items
             
                 let $administered-item-id := lib-util:mdrElementId($administered-item)
                 let $value-domain-id := data($administered-item//openMDR:representing)
                 let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
                 let $data-element-concept-id := data($administered-item//openMDR:expressing)
                 let $data-element-concept := lib-util:mdrElement("data_element_concept",$data-element-concept-id)
                 let $object-class-id := data($data-element-concept//openMDR:data_element_concept_object_class)
                 let $object-class := lib-util:mdrElement("object_class",$object-class-id)
                 let $object-class-preferred-name := data($object-class//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name)
                 let $property-class-id := data($data-element-concept//openMDR:data_element_concept_property)
                 let $property-class := lib-util:mdrElement("property",$property-class-id)
                 let $property-class-preferred-name := data($property-class//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name)
                 let $data-type := lib-util:mdrElement("data_type", data($value-domain//openMDR:value_domain_datatype))
                 let $uom := lib-util:mdrElement("unit_of_measure", data($value-domain//openMDR:value_domain_unit_of_measure))
                 let $preferred-name := data($administered-item//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name)
                 let $context-id := data($administered-item//openMDR:having//openMDR:context_identifier)
                 let $context := lib-util:mdrElement("context",$context-id)
                 
             where $record-id >= $start and $record-id <= $start + $num 
             return
                   element data-element
                   {
                      element names 
                      {
                          element id {$administered-item-id},
                          element preferred {$preferred-name},
                          element all-names 
                          {
                              for $name in $administered-item//openMDR:name
                              where data($name) != $preferred-name  
                              return element name {data($name)}
                          }                    
                      },
                     element definition {
                        element value{
                            administered-item:preferred-definition($administered-item)
                        },
                         element source{
                            element abbreviation{
                            },
                            element code{
                                data($administered-item//openMDR:having//openMDR:containing//openMDR:definition_source_reference)                                
                            },
                            element description{
                            }
                        }
                     },
                     element workflow-status
                      {
                            data($administered-item//openMDR:administered_item_administration_record//openMDR:administrative_status)
                      },
                     element registration-status
                      {
                            data($administered-item//openMDR:administered_item_administration_record//openMDR:registration_status)
                      },
                     element context
                     {
                        element name {data($context//openMDR:having//openMDR:containing//openMDR:name)},
                        element version {data($context/@version)},
                        element description {data($context//openMDR:having//openMDR:containing//openMDR:definition_text)}
                     },
                     element values 
                     {
                         if (value-domain:type($value-domain) = 'enumerated value domain')
                         then
                             element enumerated 
                             {
                                 for $value in $value-domain//openMDR:containing
                                 where $value/openMDR:value_item >""
                                 order by $value/openMDR:value_item
                                 return
                                     element valid-value
                                     {
                                         element code {data($value/openMDR:value_item)},
                                         element meaning {data(value-meaning:value-meaning($value/openMDR:contained_in)//openMDR:value_meaning_description)}
                                     }
                              }
                          else
                             element non-enumerated
                             {
                                element data-type {data($data-type//openMDR:datatype_name)},
                                element units 
                                {
                                   if (data($uom//openMDR:unit_of_measure_name)>"")
                                   then (data($uom//openMDR:unit_of_measure_name))
                                   else ("(not applicable)")}
                               }
                       },
                       element object-class
                       {
                               element names 
                              {
                                  element id {$object-class-id},
                                  element preferred {$object-class-preferred-name},
                                  element all-names 
                                  {
                                      for $name in $object-class//openMDR:name
                                      where data($name) != $object-class-preferred-name
                                      return element name {data($name)}
                                  }                    
                              },
                             element definition {
                                element value{
                                    administered-item:preferred-definition($object-class)
                                },
                                 element source{
                                    element abbreviation{
                                    },
                                    element code{
                                        data($object-class//openMDR:having//openMDR:containing//openMDR:definition_source_reference)                                
                                    },
                                    element description{
                                    }
                                }
                             },
                             element workflow-status
                              {
                                    data($object-class//openMDR:administered_item_administration_record//openMDR:administrative_status)
                              },
                             element registration-status
                              {
                                    data($object-class//openMDR:administered_item_administration_record//openMDR:registration_status)
                              },
                          element conceptCollection{
                           let $resource := lib-qs:selectResource-form('CONCEPTID')
                           let $request:=""
                           
                           for $u in $object-class//openMDR:reference_uri
                               let $phrase-id := tokenize(data($u),'_')[last()]
                               let $conceptRef :=
                               if ($request != "") 
                                    then (lib-qs:query($request,$resource))
                                else  
                                if ($phrase-id != "") 
                                    then (lib-qs:query($resource, (), $phrase-id, $start, $num))
                                else ()                             
                           return
                                $conceptRef    
                           }
                        },
                       
                       element property
                       {
                            element names 
                              {
                                  element id {$property-class-id},
                                  element preferred {$property-class-preferred-name},
                                  element all-names 
                                  {
                                      for $name in $property-class//openMDR:name
                                      where data($name) != $property-class-preferred-name
                                      return element name {data($name)}
                                  }                    
                              },
                             element definition {
                                element value{
                                    administered-item:preferred-definition($property-class)
                                },
                                 element source{
                                    element abbreviation{
                                    },
                                    element code{
                                        data($property-class//openMDR:having//openMDR:containing//openMDR:definition_source_reference)                                
                                    },
                                    element description{
                                    }
                                }
                             },
                             element workflow-status
                              {
                                    data($property-class//openMDR:administered_item_administration_record//openMDR:administrative_status)
                              },
                             element registration-status
                              {
                                    data($property-class//openMDR:administered_item_administration_record//openMDR:registration_status)
                              },
                          element conceptCollection{
                           let $resource := lib-qs:selectResource-form('CONCEPTID')
                           let $request:=""                     
                           for $u in $property-class//openMDR:reference_uri
                               let $phrase-id := tokenize(data($u),'_')[last()]
                               let $conceptRef :=
                               if ($request != "") 
                                    then (lib-qs:query($request,$resource))
                                else  
                                if ($phrase-id != "") 
                                    then (lib-qs:query($resource, (), $phrase-id, $start, $num))
                                else ()                             
                           return
                           $conceptRef                                                    
                           }
                        }
                   }
            }
        </result-set>   
   return
       $content
};

(: Returns a list of all the contexts present in the metadata registry :)
declare function lib-search:contextList($term as xs:string) as node() 
{

    let $all-contexts :=  
        for $sorted in lib-util:mdrDocuments("context")
        let $preferred-name := data($sorted//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name)
        order by $preferred-name 
    return 
           $sorted
    let $count-allcontexts := count($all-contexts)    

    let $content as element() :=
        <result-set namespace="http://cagrid.org/schema/result-set">
           {
              for $context at $record-id in $all-contexts
              return
                 element context
                 {
                    element name {data($context//openMDR:having//openMDR:containing//openMDR:name)},
                    element version {data($context//@version)},
                    element description {data($context//openMDR:having//openMDR:containing//openMDR:definition_text)}
                 }                  
            }
        </result-set>   
   return
       $content
};


(: Classification:)
declare function local:searchCDEByClassification($phrase as xs:string, $classified-by as xs:anyURI) as element()*
{
      let $admin-items :=
      for $administered-item in lib-search:mdrElementsByClassification("data_element", $classified-by)
            [.//openMDR:registration_status ne 'Superseded']
            [.//openMDR:name&=$phrase or .//openMDR:definition_text&=$phrase]
         let $administered-item-id := lib-util:mdrElementId($administered-item)
         let $value-domain-id := data($administered-item//openMDR:representing)
         let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
         let $data-type := lib-util:mdrElement("data_type", data($value-domain//openMDR:value_domain_datatype))
         let $uom := lib-util:mdrElement("unit_of_measure", data($value-domain//openMDR:value_domain_unit_of_measure))
         let $preferred-name := $administered-item//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name
         order by $preferred-name
         return
            element data-element{
               element names {
                  element id {$administered-item-id},
                  element preferred {data($preferred-name)},
                  element all-names {
                  for $name in $administered-item//openMDR:name
                  where data($name) != $preferred-name  
                  return element name {data($name)}
                  }},
               element definition {administered-item:preferred-definition($administered-item)},
               element values 
               {               
                  if (value-domain:type($value-domain) = 'enumerated value domain')
                  then
                  (
                      element enumerated
                      {
                         for $value in $value-domain//openMDR:containing
                         where $value/openMDR:value_item >""
                         order by $value/openMDR:value_item
                         return
                             element valid-value
                             {
                                 element code {data($value/openMDR:value_item)},
                                 element meaning {data(value-meaning:value-meaning($value/openMDR:contained_in)//openMDR:value_meaning_description)}
                             }
                      }
                      
                     )
                  else(
                      element non-enumerated
                      {
                     element data-type {data($data-type//openMDR:datatype_name)},
                     element units 
                     {
                        if (data($uom//openMDR:unit_of_measure_name)>"")
                        then (data($uom//openMDR:unit_of_measure_name))
                        else ("(not applicable)")
                     }
                     }
                  )
               }
            
            }
      
      for $item in $admin-items
      return
         element data-element{$item/*}
};

declare function lib-search:dataElementListSearchByClassification($term as xs:string, $classified-by as xs:anyURI, $start as xs:integer, $num as xs:integer) as node() 
{

   let $all-admin-items := 
       for $sorted in lib-util:searchWithClassification("data_element", $classified-by, $term)
       let $preferred-name := $sorted//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name
       order by $preferred-name 
       return 
           $sorted

    let $count-all-admin-items := count($all-admin-items)    

    let $content as element() :=
        <result-set>
           {
              for $administered-item at $record-id in $all-admin-items
                 let $administered-item-id := lib-util:mdrElementId($administered-item)
                 let $value-domain-id := data($administered-item//openMDR:representing)
                 let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
                 let $data-type := lib-util:mdrElement("data_type", data($value-domain//openMDR:value_domain_datatype))
                 let $uom := lib-util:mdrElement("unit_of_measure", data($value-domain//openMDR:value_domain_unit_of_measure))
                 let $preferred-name := data($administered-item//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name)
             where $record-id >= $start and $record-id <= $start + $num 
             return
                   element data-element
                   {
                      element names 
                      {
                          element id {$administered-item-id},
                          element preferred {$preferred-name},
                          element all-names 
                          {
                              for $name in $administered-item//openMDR:name
                              where data($name) != $preferred-name  
                              return element name {data($name)}
                          }
                      },
                     element definition {administered-item:preferred-definition($administered-item)},
                     element values 
                     {
                         if (value-domain:type($value-domain) = 'enumerated value domain')
                         then
                             element enumerated 
                             {
                                 for $value in $value-domain//openMDR:containing
                                 where $value/openMDR:value_item >""
                                 order by $value/openMDR:value_item
                                 return
                                     element valid-value
                                     {
                                         element code {data($value/openMDR:value_item)},
                                         element meaning {data(value-meaning:value-meaning($value/openMDR:contained_in)//openMDR:value_meaning_description)}
                                     }
                              }
                          else
                          element non-enumerated
                          {
                             element data-type {data($data-type//openMDR:datatype_name)},
                             element units 
                             {
                                if (data($uom//openMDR:unit_of_measure_name)>"")
                                then (data($uom//openMDR:unit_of_measure_name))
                                else ("(not applicable)")}
                            }
                       }
                   }
            }
        </result-set>
   return
       $content
};

(:~
    Return all data element classified by the given concept
:)
declare function lib-search:mdrElementsByClassification($mdr-element-type as xs:string, $classified-by as xs:anyURI) as element()*
{
   collection(lib-util:getCollectionPath($mdr-element-type))/*[openMDR:classified_by=$classified-by]
};

(:~
    Generate a list of available classification schemes
    TODO: Remove absolute path
:)
declare function lib-search:listClassificationSchemes() as node()*
{
(:        for $scheme in collection("/db/mdr/data/classification_scheme")/rdf:RDF/rdf:Description[rdf:type/@rdf:resource='http://www.w3.org/2004/02/skos/core#ConceptScheme']/@rdf:about:)
        for $scheme in lib-util:mdrElements("classification_scheme")[exists(.//openMDR:having)]
        return
(:            <classification_scheme uri="{$scheme//openMDR:referenceURI/text()}">{$scheme//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name[1]/text()}</classification_scheme>
:)
<classification_scheme uri="{$scheme//openMDR:referenceURI/text()}">{lib-util:mdrElementName($scheme)}</classification_scheme>
};

(:~
    Generate a simple XML view of the classification scheme from SKOS
    TODO: Remove absolute path
:)
declare function lib-search:simpleClassificationTree($scheme as xs:string) as node()
{
    let $concepts :=
            <rdf:RDF xmlns:openMDR="http://www.cagrid.org/schema/openMDR" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:skos="http://www.w3.org/2004/02/skos/core#">
            {
                for $concept in collection("/db/mdr/data/classification_scheme")/rdf:RDF/*[skos:inScheme/@rdf:resource = $scheme]
                return
                    $concept
            }
            </rdf:RDF>
        let $stylesheet := doc("/db/mdr/services/stylesheets/classification_simple_tree.xsl")
        let $params := 
            <parameters>
                <param name="root" value="{data(collection("/db/mdr/data/classification_scheme")/rdf:RDF/*[@rdf:about = $scheme]/skos:hasTopConcept/@rdf:resource)}"/>
            </parameters>
        return
        
            transform:transform($concepts, $stylesheet, $params)
};