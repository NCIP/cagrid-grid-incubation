import module namespace 
lib-forms="http://www.cancergrid.org/xquery/library/forms"
at "../edit/m-lib-forms.xquery";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";   

import module namespace 
lib-supersede="http://www.cancergrid.org/xquery/library/supersede"
at "../edit/m-lib-supersede.xquery";  

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

let $directories := (
         "classification_scheme",
         "conceptual_domain",
         "context",
         "data_element",
         "data_element_concept",
         "data_type",
         "object_class",
         "organization", 
         "property",
         "reference_document",
         "registration_authority",
         "representation_class",
         "unit_of_measure",
         "value_domain"
         )
                     
let $resources := (
         "classification",
         "edit",
         "library",
         "model",
         "resolver",
         "schemas",
         "services",
         "web",
         "xforms"
         )
         
let $handy-paths :=
   (
   element function-test{
   element name {'lib-util:webPath'},
   element invocation {'lib-util:webPath()'},
   element result {lib-util:webPath()},
   element passed {(lib-util:webPath()=lib-util:getResourcePath('web'))}
   },
   element function-test{
   element name {'lib-util:schemaPath'},
   element invocation {'lib-util:schemaPath()'},
   element result {lib-util:schemaPath()},
   element passed {(lib-util:schemaPath()=lib-util:getResourcePath('schemas'))}
   },
   element function-test{
   element name {'lib-util:classificationPath'},
   element invocation {'lib-util:classificationPath()'},
   element result {lib-util:classificationPath()},
   element passed {(lib-util:classificationPath()=lib-util:getResourcePath('classification'))}
   },
   element function-test{
   element name {'lib-util:formsPath'},
   element invocation {'lib-util:formsPath()'},
   element result {lib-util:formsPath()},
   element passed {(lib-util:formsPath()=lib-util:getResourcePath('xforms'))}
   }
   
   )
   

let $tests :=
   for $directory in $directories
   return 
      element function-test {
         element name {'lib-util:getCollectionPath'},
         element invocation {concat('lib-util:getCollectionPath(''', $directory, ''')')},
         element result {lib-util:getCollectionPath($directory)},
         element passed {xmldb:collection-exists(lib-util:getCollectionPath($directory))}
      }
let $tests2 :=      
    for $resource in $resources
    return
       element function-test {
       element name {'lib-util:getResourcePath'},
       element invocation {concat('lib-util:getResourcePath(''', $resource, ''')')},
       element result {lib-util:getResourcePath($resource)},
       element passed {xmldb:collection-exists(lib-util:getResourcePath($resource))}
    }
    
let $tests3 := (

    let $identifier := 
        (for $element in collection(lib-util:getCollectionPath('data_element'))[.//cgMDR:registration_status ne 'Superseded'][1]/*
        return concat(
            xs:string($element//@item_registration_authority_identifier), '-', 
            xs:string($element//@data_identifier), '-', 
            xs:string($element//@version)))
        
    return        
    (
    element function-test {
       element name {'lib-util:mdrElement'},
       element invocation {concat('lib-util:mdrElement(''',$identifier,''')')},
       element result {lib-util:mdrElementId(lib-util:mdrElement($identifier))},
       element passed {
           lib-util:mdrElementId(lib-util:mdrElement($identifier)) = $identifier
                   }
        },
    element function-test {
       element name {'lib-util:mdrElementType'},
       element invocation {concat('lib-util:mdrElementType(lib-util:mdrElement(''',$identifier,'''))')},
       element result {'data_element'},
       element passed {
           lib-util:mdrElementType(lib-util:mdrElement($identifier)) = "data_element"
                   }
        }
        )
    )        
         
return lib-rendering:txfrm-webpage("lib-util path tests", element function-tests{$tests, $tests2, $handy-paths, $tests3})
