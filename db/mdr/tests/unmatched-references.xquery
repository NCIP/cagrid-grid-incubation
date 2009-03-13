declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

let $data-elements := lib-util:mdrElements('data_element')[.//cgMDR:registration_status ne 'Superseded'][.//cgMDR:registration_status ne 'Retired']
let $data-element-concepts := lib-util:mdrElements('data_element_concept')[.//cgMDR:registration_status ne 'Superseded'][.//cgMDR:registration_status ne 'Retired']
let $value-domains := lib-util:mdrElements('value_domain')[.//cgMDR:registration_status ne 'Superseded'][.//cgMDR:registration_status ne 'Retired']
return
   lib-rendering:txfrm-webpage('broken references', 
         <broken-refs>
            {
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where empty(lib-util:mdrElement('representation_class',$data-element//cgMDR:typed_by)//cgMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'data_element'},
                  attribute to {'representation_class'},
                  attribute id {$id},
                  xs:string($data-element//cgMDR:typed_by)
                  },
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where empty(lib-util:mdrElement('value_domain',$data-element//cgMDR:representing)//cgMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'data_element'},
                  attribute to {'value_domain'},
                  attribute id {$id},
                  xs:string($data-element//cgMDR:representing)
                  },
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where empty(lib-util:mdrElement('data_element_concept',$data-element//cgMDR:expressing)//cgMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'data_element'},
                  attribute to {'data_element_concept'},
                  attribute id {$id},
                  xs:string($data-element//cgMDR:expressing)
                  },
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where exists($data-element//@deriving)
            return
               for $deriving in $data-element//@deriving
               where empty(lib-util:mdrElement('data_element',xs:string($deriving))//cgMDR:administered_item_administration_record)
               return
                  element broken-ref {
                     attribute from {'data_element'},
                     attribute to {'data_element'},
                     attribute id {$id},
                     xs:string($deriving)
                     },
            for $data-element-concept in $data-element-concepts
            let $id := lib-util:mdrElementId($data-element-concept)
            where empty(lib-util:mdrElement('object_class',$data-element-concept//cgMDR:data_element_concept_object_class)//cgMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'data_element_concept'},
                     attribute to {'object-class'},
                     attribute id {$id},
                     xs:string($data-element-concept//cgMDR:data_element_concept_object_class)
                     },
            for $data-element-concept in $data-element-concepts
            let $id := lib-util:mdrElementId($data-element-concept)
            where empty(lib-util:mdrElement('property',$data-element-concept//cgMDR:data_element_concept_property)//cgMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'data_element_concept'},
                     attribute to {'property'},
                     attribute id {$id},
                     xs:string($data-element-concept//cgMDR:data_element_concept_property)
                     },
            for $data-element-concept in $data-element-concepts
            let $id := lib-util:mdrElementId($data-element-concept)
            where empty(lib-util:mdrElement('conceptual_domain',$data-element-concept//cgMDR:data_element_concept_conceptual_domain)//cgMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'data_element_concept'},
                     attribute to {'conceptual_domain'},
                     attribute id {$id},
                     xs:string($data-element-concept//cgMDR:data_element_concept_conceptual_domain)
                     },
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('conceptual_domain',$value-domain//cgMDR:representing[1])//cgMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'value_domain'},
                     attribute to {'conceptual_domain'},
                     attribute id {$id},
                     xs:string($value-domain//cgMDR:representing)
                     },
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('data_type',$value-domain//cgMDR:value_domain_datatype))
            return    
                  element broken-ref {
                     attribute from {'value_domain'},
                     attribute to {'datatype'},
                     attribute id {$id},
                     xs:string($value-domain//cgMDR:value_domain_datatype)
                     },
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('representation_class',$value-domain//cgMDR:typed_by)//cgMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'value_domain'},
                  attribute to {'representation_class'},
                  attribute id {$id},
                  xs:string($value-domain//cgMDR:typed_by)
                  },
                  
                           
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('unit_of_measure', $value-domain//cgMDR:value_domain_unit_of_measure))
            return    
                  element broken-ref {
                     attribute from {'value_domain'},
                     attribute to {'unit_of_measure'},
                     attribute id {$id},
                     xs:string($value-domain//cgMDR:value_domain_unit_of_measure)
                     }       
         
            }  
         </broken-refs>
         ) 