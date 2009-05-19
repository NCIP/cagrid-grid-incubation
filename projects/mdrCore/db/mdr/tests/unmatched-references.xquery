declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";

import module namespace 
lib-util="http://www.cagrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
   lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

let $data-elements := lib-util:mdrElements('data_element')[.//openMDR:registration_status ne 'Superseded'][.//openMDR:registration_status ne 'Retired']
let $data-element-concepts := lib-util:mdrElements('data_element_concept')[.//openMDR:registration_status ne 'Superseded'][.//openMDR:registration_status ne 'Retired']
let $value-domains := lib-util:mdrElements('value_domain')[.//openMDR:registration_status ne 'Superseded'][.//openMDR:registration_status ne 'Retired']
return
   lib-rendering:txfrm-webpage('broken references', 
         <broken-refs>
            {
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where empty(lib-util:mdrElement('representation_class',$data-element//openMDR:typed_by)//openMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'data_element'},
                  attribute to {'representation_class'},
                  attribute id {$id},
                  xs:string($data-element//openMDR:typed_by)
                  },
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where empty(lib-util:mdrElement('value_domain',$data-element//openMDR:representing)//openMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'data_element'},
                  attribute to {'value_domain'},
                  attribute id {$id},
                  xs:string($data-element//openMDR:representing)
                  },
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where empty(lib-util:mdrElement('data_element_concept',$data-element//openMDR:expressing)//openMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'data_element'},
                  attribute to {'data_element_concept'},
                  attribute id {$id},
                  xs:string($data-element//openMDR:expressing)
                  },
            for $data-element in $data-elements
            let $id := lib-util:mdrElementId($data-element)
            where exists($data-element//@deriving)
            return
               for $deriving in $data-element//@deriving
               where empty(lib-util:mdrElement('data_element',xs:string($deriving))//openMDR:administered_item_administration_record)
               return
                  element broken-ref {
                     attribute from {'data_element'},
                     attribute to {'data_element'},
                     attribute id {$id},
                     xs:string($deriving)
                     },
            for $data-element-concept in $data-element-concepts
            let $id := lib-util:mdrElementId($data-element-concept)
            where empty(lib-util:mdrElement('object_class',$data-element-concept//openMDR:data_element_concept_object_class)//openMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'data_element_concept'},
                     attribute to {'object-class'},
                     attribute id {$id},
                     xs:string($data-element-concept//openMDR:data_element_concept_object_class)
                     },
            for $data-element-concept in $data-element-concepts
            let $id := lib-util:mdrElementId($data-element-concept)
            where empty(lib-util:mdrElement('property',$data-element-concept//openMDR:data_element_concept_property)//openMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'data_element_concept'},
                     attribute to {'property'},
                     attribute id {$id},
                     xs:string($data-element-concept//openMDR:data_element_concept_property)
                     },
            for $data-element-concept in $data-element-concepts
            let $id := lib-util:mdrElementId($data-element-concept)
            where empty(lib-util:mdrElement('conceptual_domain',$data-element-concept//openMDR:data_element_concept_conceptual_domain)//openMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'data_element_concept'},
                     attribute to {'conceptual_domain'},
                     attribute id {$id},
                     xs:string($data-element-concept//openMDR:data_element_concept_conceptual_domain)
                     },
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('conceptual_domain',$value-domain//openMDR:representing[1])//openMDR:administered_item_administration_record)
            return    
                  element broken-ref {
                     attribute from {'value_domain'},
                     attribute to {'conceptual_domain'},
                     attribute id {$id},
                     xs:string($value-domain//openMDR:representing)
                     },
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('data_type',$value-domain//openMDR:value_domain_datatype))
            return    
                  element broken-ref {
                     attribute from {'value_domain'},
                     attribute to {'datatype'},
                     attribute id {$id},
                     xs:string($value-domain//openMDR:value_domain_datatype)
                     },
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('representation_class',$value-domain//openMDR:typed_by)//openMDR:administered_item_administration_record)
            return 
               element broken-ref {
                  attribute from {'value_domain'},
                  attribute to {'representation_class'},
                  attribute id {$id},
                  xs:string($value-domain//openMDR:typed_by)
                  },
                  
                           
            for $value-domain in $value-domains
            let $id := lib-util:mdrElementId($value-domain)
            where empty(lib-util:mdrElement('unit_of_measure', $value-domain//openMDR:value_domain_unit_of_measure))
            return    
                  element broken-ref {
                     attribute from {'value_domain'},
                     attribute to {'unit_of_measure'},
                     attribute id {$id},
                     xs:string($value-domain//openMDR:value_domain_unit_of_measure)
                     }       
         
            }  
         </broken-refs>
         ) 