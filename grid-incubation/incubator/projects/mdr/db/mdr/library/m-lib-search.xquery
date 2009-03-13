module namespace lib-search="http://www.cancergrid.org/xquery/library/search";

(: ~
 : Module Name:             trial designer xml document
 :
 : Module Version           1.0
 :
 : Date                               31st October 2006
 :
 : Copyright                       The cancergrid consortium
 :
 : Module overview          outputs the expected message for the trial designer plug-in
 :
 :)
 
(:~
 :    @author Steve Harris
 :	@author Andrew Tsui
 :    @author Maria Lin
 :    @version 0.1
~ :)

  
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace
   value-domain="http://www.cancergrid.org/xquery/library/value-domain"
   at "../library/m-value-domain.xquery"; 
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    

import module namespace
value-meaning="http://www.cancergrid.org/xquery/library/value-meaning"
at "../library/m-value-meaning.xquery";
   
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace util="http://exist-db.org/xquery/util";
(: declare namespace exist="http://exist-db.org"; :)

declare option exist:serialize "method=xml media-type=text/xml"; 


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
let $value-domain := lib-util:mdrElement('value_domain',$data-element//cgMDR:representing)
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
                   for $containing in $value-domain//cgMDR:Enumerated_Value_Domain/cgMDR:containing
                   let $value := data($containing/cgMDR:value_item)
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

declare function lib-search:dataElementSearch($compound_id as xs:string) as element()* {

let $data-element := lib-util:mdrElement('data_element',$compound_id)
let $long-name :=administered-item:preferred-name($data-element)
let $name := lib-util:mdrElementId($data-element)
let $version := $data-element//@version
let $definition := administered-item:preferred-definition($data-element)
let $value-domain := lib-util:mdrElement('value_domain',$data-element//cgMDR:representing)
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
                   for $containing in $value-domain//cgMDR:Enumerated_Value_Domain/cgMDR:containing
                   let $value := data($containing/cgMDR:value_item)
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



(: Andrew modification :)

declare function local:searchCDE($phrase as xs:string) as element()*
{
      let $admin-items :=
      for $administered-item in lib-util:mdrElements("data_element")
            [.//cgMDR:registration_status ne 'Superseded']
            [.//cgMDR:name&=$phrase or .//cgMDR:definition_text&=$phrase]
         let $administered-item-id := lib-util:mdrElementId($administered-item)
         let $value-domain-id := data($administered-item//cgMDR:representing)
         let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
         let $data-type := lib-util:mdrElement("data_type", data($value-domain//cgMDR:value_domain_datatype))
         let $uom := lib-util:mdrElement("unit_of_measure", data($value-domain//cgMDR:value_domain_unit_of_measure))
         let $preferred-name := $administered-item//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name
         order by $preferred-name
         return
            element data-element{
               element names {
                  element id {$administered-item-id},
                  element preferred {data($preferred-name)},
                  element all-names {
                  for $name in $administered-item//cgMDR:name
                  where data($name) != $preferred-name  
                  return element name {data($name)}
                  }},
               element definition {administered-item:preferred-definition($administered-item)},
               element values {
               
                  if (value-domain:type($value-domain) = 'enumerated value domain')
                  then(
                     for $value in $value-domain//cgMDR:containing
                     where $value/cgMDR:value_item >""
                     order by $value/cgMDR:value_item
                     return
                     element valid-value{
                     element code {data($value/cgMDR:value_item)},
                     element meaning {data(value-meaning:value-meaning($value/cgMDR:contained_in)//cgMDR:value_meaning_description)}
                     }
                     )
                  else(
                     element data-type {data($data-type//cgMDR:datatype_name)},
                     element units {
                        if (data($uom//cgMDR:unit_of_measure_name)>"")
                        then (data($uom//cgMDR:unit_of_measure_name))
                        else ("(not applicable)")}
                  )
               }
            
            }
      
      for $item in $admin-items
      return
         element data-element{$item/*}
   };

declare function lib-search:dataElementListSearch($term as xs:string, $start as xs:integer, $num as xs:integer) as node() {

   let $term := replace($term, "\*", ".*")
      
   let $data-elements := local:searchCDE($term) 
   let $count := count($data-elements)
   let $end := if ($start+$num <= $count)
   then $start+$num
   else $count
   return
   if ($start > $count or $count = 0)
   then <nil/>
   else
   <result-set>
   {
   for $index in ($start+1 to $end)
   return
       $data-elements[$index]
   }
   </result-set>

};
