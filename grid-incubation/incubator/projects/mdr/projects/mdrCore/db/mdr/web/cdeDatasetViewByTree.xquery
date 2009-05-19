xquery version "1.0";

(: ~
: Module Name:             Data element tabular listing
:
: Module Version           1.0
:
: Date                     06 March 2007
:
: Copyright                The cagrid consortium
:
: Module overview          Returns a reduced, tabular view on data elements
:
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";

import module namespace 
lib-util="http://www.cagrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
administered-item="http://www.cagrid.org/xquery/library/administered-item" 
at "../library/m-administered-item.xquery";   

import module namespace
value-domain="http://www.cagrid.org/xquery/library/value-domain"
at "../library/m-value-domain.xquery";

import module namespace
value-meaning="http://www.cagrid.org/xquery/library/value-meaning"
at "../library/m-value-meaning.xquery";

import module namespace 
lib-rendering="http://www.cagrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),

let $id := request:get-parameter("compound_id", ())

let $content as element() :=
   element result-set-by-tree {
   if ($id) then (
      let $admin-items :=
         for $administered-item in lib-util:mdrElements("data_element")
            [.//openMDR:registration_status ne 'Superseded'][.//openMDR:classified_by=$id]
            
         let $administered-item-id := lib-util:mdrElementId($administered-item)
         let $value-domain-id := data($administered-item//openMDR:representing)
         let $value-domain := lib-util:mdrElement("value_domain",$value-domain-id)
         let $data-type := lib-util:mdrElement("data_type", data($value-domain//openMDR:value_domain_datatype))
         let $uom := lib-util:mdrElement("unit_of_measure", data($value-domain//openMDR:value_domain_unit_of_measure))
         let $preferred-name := administered-item:preferred-name($administered-item)
         order by $preferred-name
         return
            element data-element{
                  element names {
                     element id {$administered-item-id},
                     element preferred {$preferred-name},
                     element all-names {
                        for $name in $administered-item//openMDR:name
                        where data($name) != $preferred-name  
                        return element name {data($name)}
                     }},
                     element definition {administered-item:preferred-definition($administered-item)},
                  element values {
                  
                  if (value-domain:type($value-domain) = 'enumerated value domain')
                  then(
                     for $value in $value-domain//openMDR:containing
                     where $value/openMDR:value_item >""
                     order by $value/openMDR:value_item
                     return
                        element valid-value{
                           element code {data($value/openMDR:value_item)},
                           element meaning {data(value-meaning:value-meaning($value/openMDR:contained_in)//openMDR:value_meaning_description)}
                        }
                     )
                  else(
                     element data-type {data($data-type//openMDR:datatype_name)},
                     element units {if (data($uom//openMDR:unit_of_measure_name)>"")
                                    then (data($uom//openMDR:unit_of_measure_name))
                                    else ("(not applicable)")}
                  )
                  }
                  
               }

      for $item at $row_id in $admin-items
      let $row := if (($row_id mod 2) = 0)
                  then "odd_row"
                  else "even_row"  
                  return
                     element data-element{
                        attribute class {$row},
                        $item/*})
         
      else (),
      doc(concat(lib-util:getResourcePath('treeview'),"tree-classification.xml"))
      }
      
return
   lib-rendering:txfrm-webpage("Data Elements", $content)