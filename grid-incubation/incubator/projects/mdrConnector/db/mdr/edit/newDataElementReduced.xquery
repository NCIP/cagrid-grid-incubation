xquery version "1.0";

(: ~
: Module Name:             create/edit data element in reduced view
: Module Version           1.0
: Date                     30th March 2007
: Copyright                The cancergrid consortium
: Module overview          allows a user to create and edit a data element
:
:)

(:~
:    @author Steve Harris
:    @version 0.1
~ :)

import module namespace 
lib-forms="http://www.cancergrid.org/xquery/library/forms"
at "../edit/m-lib-forms.xquery";

import module namespace 
lib-make-admin-item="http://www.cancergrid.org/xquery/library/make-admin-item"
at "../edit/m-lib-make-admin-item.xquery";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";   

import module namespace 
lib-supersede="http://www.cancergrid.org/xquery/library/supersede"
at "../edit/m-lib-supersede.xquery"; 

import module namespace 
administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
at "../library/m-administered-item.xquery";    

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

declare function local:success-document(
    $data-element as element(cgMDR:Data_Element),
    $value-domain as element(), 
    $dec as element(cgMDR:Data_Element_Concept),
    $conceptual-domain as element()) as element(html)
{   
    
    let $content := <div xmlns="http://www.w3.org/1999/xhtml"> {
    (: success web page here with links to the classes :)
    
    element p {"Your data element and its supporting documents have been created"}, element br {},
    
    element p {element span {"You can view a complete summary here: "}, administered-item:data-element-summary-anchor($data-element)},
    
    element p {element span {"And the individual documents can be viewed by clicking on the links below"}},
    
    element ul {
        element li {element span {"Data Element: "}, administered-item:html-anchor($data-element)},
        element li {element span {"Value Domain: "}, administered-item:html-anchor($value-domain)},
        element li {element span {"Data Element Concept: "}, administered-item:html-anchor($dec)},
        element li {element span {"Conceptual Domain: "}, administered-item:html-anchor($conceptual-domain)}
    },
    
    element p {
        element a{
            attribute href {concat("newDataElementReduced.xquery?update=new&amp;registered-by=", $data-element//cgMDR:registered_by, "&amp;administered-by=",$data-element//cgMDR:administered_by, "&amp;submitted-by=",$data-element//cgMDR:submitted_by)},
            "Click here to make another data element"}}
    }</div>
    
    return
        lib-rendering:txfrm-webpage('Success',$content)
};

declare function local:edit-document($reduced-document as element(), $action as xs:string, $type as xs:string) as element()
{
   <div>
   <form>  
      {
       element table {
          attribute align {'center'},
          element tr {
             element td {concat('Type:',$type)},
             element td {
                element input {
                   attribute type {'radio'},
                   attribute name {'type'},
                   attribute value {'enum'},
                   attribute onclick {'submit();'},
                   if ($type='enum') then attribute checked {'checked'} else (),
                   'Enumerated'
                }
             },
             element td {
                element input {
                   attribute type {'radio'},
                   attribute name {'type'},
                   attribute value {'non-enum'},
                   attribute onclick {'submit();'},
                   if ($type='non-enum') then attribute checked {'checked'} else (),
                   'Non-Enumerated'
                   }
             }
          }
       }
       }
      <table class="layout">
         <tr>
         <td>Preferred Name</td>
         <td>{lib-forms:input-element('preferred-name',50,$reduced-document//preferred-name)}</td>
         </tr>
         
         <tr>
            <td>Other Names</td>
            <td>{
               element table {
                  attribute cellspacing {"0"},
                  attribute cellpadding {"0"},
                   for $other-name at $pos-other-name in $reduced-document//name
                   let $skip := if (substring-after($action,'delete-name')='') then 0 else xs:int(substring-after($action,'delete-name'))
                   where $pos-other-name != $skip and $other-name > ""
                   return
                       element tr {
                       element td{lib-forms:input-element('other-name',50,$other-name)},
                           element td{lib-forms:action-button(concat('delete-name',xs:string($pos-other-name)), 'action' ,'0')}
                           },
                       
                   element tr {
                       element td {lib-forms:input-element('other-name',50,'')},
                       element td {lib-forms:action-button('add-other-name', 'action' ,'0')}
                       }
                   }
               }
            </td>
         </tr>
         <tr>
            <td>Definition</td>
            <td>{lib-forms:text-area-element('definition', 5, 30, $reduced-document/definition)}</td>
         </tr>
         <tr><td>Values</td>
            <td>{
               if ($type="enum")
               then (
                  element table {
                  attribute border{"1"},
                  for $value at $value-pos in $reduced-document//value
                  let $skip := if (substring-after($action,'delete-value')='') then 0 else xs:int(substring-after($action,'delete-value'))
                  where $value-pos != $skip
                  return
                      (
                      element tr {
                          element td {'code'},
                          element td {lib-forms:input-element('code',5,$value/code)}},
                      element tr {
                          element td {'meaning'},
                          element td {lib-forms:input-element('meaning',20,$value/meaning)}},
                      element tr {
                          element td {''},
                          element td {lib-forms:action-button(concat('delete-value', xs:string($value-pos)), 'action' ,'0')}
                      }),
                
                  element tr {
                      element td {'code'},
                      element td {lib-forms:input-element('code',5,"")}},
                  element tr {
                      element td {'meaning'},
                      element td {lib-forms:input-element('meaning',20,"")}},
                  element tr {
                      element td {''},                  
                      element td {lib-forms:action-button('add-valid-value', 'action' ,'0')}
                      }
               })
               else (
                   element table {
                       element tr {
                           element td {"data type"},
                           element td {lib-forms:make-select-datatype($reduced-document/values/datatype)}
                       },
                       element tr {
                           element td {"unit of measure"},
                           element td {lib-forms:make-select-uom($reduced-document/values/uom)}
                       }
                       
                   }
               )
            }
            </td>
         </tr>
         <tr>
            <td>I am:</td>
            <td>{lib-forms:make-select-submitted_by($reduced-document/submitter)}</td>
         </tr>
         <tr>
            <td>Who is working for: </td>
            <td>{lib-forms:make-select-administered_by($reduced-document/administrator)}</td>
         </tr>
         <tr>
            <td>Creating data elements on behalf of : </td>
            <td>{lib-forms:make-select-registered_by($reduced-document/registrar)}</td>
         </tr>
         <tr>
            <td/>
            <td>
               {
               lib-forms:action-button('update', 'action' ,'0'),
               lib-forms:reset-button()
               }
            </td>
         </tr>
         
      </table>
      </form>
      <p><b>Preferred name: </b>
      the name that the data element is (to be) known by in common use.
      Try to compose the name from common, well accepted words in your community - especially those
      you might find in a controlled vocabulary"
      </p>
      <p><b>Other names: </b>any other commonly used names for the data element</p>
      <p><b>Definition: </b>what the data element means.  Please give as much detail as possible, especially if
      your data element is distinct from other, similar measurements or attributes</p>
      <p><b>Values: </b>are the codes and meanings for a coded variable, or the data type and unit of measure
      for measurements or counts</p>
      <p><b>Code (coded variable): </b>the shorthand code used to identify the occurence of the category
      </p>
      <p><b>Meaning (coded variable): </b>the meaning attached to the code</p>
      </div>
   
};

declare function local:full-identifier($reg-auth as xs:string, $data-identifier as xs:string, $version as xs:string) as xs:string
{
concat($reg-auth,'-',$data-identifier,'-',$version)
};



declare function local:reduced-document() as element()
{
let $registrar := data(request:get-parameter("registered-by",()))
return
   element new-data-element-reduced {
      element preferred-name {request:get-parameter("preferred-name",())},
      element other-names {
         for $other-name in request:get-parameter("other-name",())
         where $other-name >""
         return element name {$other-name}      
      },
      element definition {request:get-parameter("definition",())},
      element values {
         for $code at $code-pos in request:get-parameter("code",())
         for $meaning at $meaning-pos in request:get-parameter("meaning",())
         where $meaning-pos = $code-pos and $code > ""
         return
            element value {
               element value-identifier{lib-forms:generate-id()},
               element meaning-identifier{lib-forms:generate-id()},
               element code {$code},
               element meaning {$meaning}
            },
         element datatype {request:get-parameter("datatype",())},
         element uom {request:get-parameter("uom",())}
      },
      element registrar {$registrar},
      element administrator {request:get-parameter("administered-by",())},
      element submitter {request:get-parameter("submitted-by",())},
      element reg-auth {
         for $item-reg-auth-id in lib-util:mdrElements("registration_authority")[.//cgMDR:registrar_identifier = $registrar]//@organization_identifier
         return data($item-reg-auth-id[1])},
      element data-element-data-identifier {lib-forms:generate-id()},
      element dec-data-identifier {lib-forms:generate-id()},
      element conceptual-domain-data-identifier {lib-forms:generate-id()},
      element value-domain-data-identifier {lib-forms:generate-id()},
      element version {data(local:defaults()//cgMDR:Data_Element/@version)}
   }
};

declare function local:defaults() as element()
{
    doc(concat(lib-util:editPath(),"documents/defaults.xml"))/cgMDR:edit-defaults
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),

let $type := request:get-parameter('type','non-enum')
let $title as xs:string := "Creating a data element"
let $action := request:get-parameter("update","")
let $debug := xs:boolean(request:get-parameter("debug","false"))

let $defaults := local:defaults()
let $doc := local:reduced-document()

let $administration-record := lib-make-admin-item:administration-record(
    '',
    $defaults/cgMDR:administrative_status,
    $defaults/cgMDR:registration_status)

let $custodians := lib-make-admin-item:custodians(
    $doc/administrator,
    $doc/registrar,
    $doc/submitter)

let $having-preferred := lib-make-admin-item:having(
    $defaults//cgMDR:context_identifier,
    $defaults//cgMDR:country_identifier,
    $defaults//cgMDR:language_identifier,
    $doc/preferred-name,
    $doc/definition,
    true(), ())

let $having-alt := (
    for $name in $doc/other-names/name
    return
        lib-make-admin-item:having(
            $defaults//cgMDR:context_identifier,
            $defaults//cgMDR:country_identifier,
            $defaults//cgMDR:language_identifier,
            $name,(),false(), ()))


let $conceptual-domain :=    
    if   ($type="enum")
    then         
        element cgMDR:Enumerated_Conceptual_Domain {
            lib-make-admin-item:identifier-attributes(
                $doc/reg-auth,
                $doc/conceptual-domain-data-identifier,
                data($defaults/cgMDR:Enumerated_Conceptual_Domain/@version)),
            $administration-record,
            $custodians,
            $having-preferred,
            for $value in $doc/values/value
            return
                element cgMDR:Value_Meaning {
                    element cgMDR:value_meaning_begin_date {lib-make-admin-item:current-date()},
                    element cgMDR:value_meaning_description {data($value/meaning)},
                    element cgMDR:value_meaning_identifier {data($value/meaning-identifier)}
                },
            element cgMDR:related_to {
               element cgMDR:related_to {},
               element cgMDR:conceptual_domain_relationship_type_description {}
               }    
            }
    else(
        element cgMDR:Non_Enumerated_Conceptual_Domain {
            lib-make-admin-item:identifier-attributes(
                $doc/reg-auth,
                $doc/conceptual-domain-data-identifier,
                data($defaults/cgMDR:Data_Element/@version)),
            $administration-record,
            $custodians,
            $having-preferred,
            element cgMDR:related_to {
               element cgMDR:related_to {},
               element cgMDR:conceptual_domain_relationship_type_description {}
               }
        }
    )

let $data-element-concept :=
    element cgMDR:Data_Element_Concept {
        lib-make-admin-item:identifier-attributes(
            $doc/reg-auth,
            $doc/dec-data-identifier,
            data($defaults/cgMDR:Data_Element/@version)),
        $administration-record,
        $custodians,
        $having-preferred,
        element cgMDR:data_element_concept_conceptual_domain {
            lib-util:mdrElementId($conceptual-domain)
            },
        $defaults/cgMDR:data_element_concept_object_class,
        $defaults/cgMDR:data_element_concept_property
    }

let $value-domain as element() : = 
    if   ($type="enum")
    then (        
        element cgMDR:Enumerated_Value_Domain {
            lib-make-admin-item:identifier-attributes(
            $doc/reg-auth,
            $doc/value-domain-data-identifier,
            data($defaults/cgMDR:Enumerated_Value_Domain/@version)),
            $administration-record,
            $custodians,
            $having-preferred,
            element cgMDR:typed_by {data($defaults//cgMDR:Enumerated_Value_Domain/cgMDR:typed_by)},        
            element cgMDR:value_domain_datatype {data($defaults//cgMDR:Enumerated_Value_Domain/cgMDR:value_domain_datatype)},
            element cgMDR:value_domain_maximum_character_quantity {},
            element cgMDR:value_domain_format {},
            element cgMDR:value_domain_unit_of_measure {data($defaults//cgMDR:Enumerated_Value_Domain/cgMDR:value_domain_unit_of_measure)},
            for $value in $doc/values/value
            return
                element cgMDR:containing {
                    attribute permissible_value_identifier {$value/value-identifier},
                    element cgMDR:permissible_value_begin_date {lib-make-admin-item:current-date()},
                    element cgMDR:value_item {data($value/code)},
                    element cgMDR:contained_in {data($value/meaning-identifier)}
                },
            element cgMDR:representing {lib-util:mdrElementId($conceptual-domain)}
        })
    else (
        element cgMDR:Non_Enumerated_Value_Domain {
            lib-make-admin-item:identifier-attributes(
                $doc/reg-auth,
                $doc/value-domain-data-identifier,
                data($defaults/cgMDR:Non_Enumerated_Value_Domain/@version)),
            $administration-record,
            $custodians,
            $having-preferred,
            
            element cgMDR:typed_by {},
            element cgMDR:value_domain_datatype {data($doc/values/datatype)},
            element cgMDR:value_domain_maximum_character_quantity {},
            element cgMDR:value_domain_format {},
            element cgMDR:value_domain_unit_of_measure {data($doc/values/uom)},
            element cgMDR:representing {lib-util:mdrElementId($conceptual-domain)}
        }
    )

let $data-element as element() :=
    element cgMDR:Data_Element {
        lib-make-admin-item:identifier-attributes(
            $doc/reg-auth,
            $doc/data-element-data-identifier,
            $doc/version),
        $administration-record,
        element cgMDR:described_by {},
        element cgMDR:classified_by {},
        $custodians,
        $having-preferred,
        $having-alt,
        
        element cgMDR:data_element_precision {},
        element cgMDR:representation_class_qualifier {},
        
        element cgMDR:representing {lib-util:mdrElementId($value-domain)},
        
        if   ($type="enum")
        then element cgMDR:typed_by {data($defaults/cgMDR:Enumerated_Value_Domain/cgMDR:typed_by)}
        else element cgMDR:typed_by {},

        element cgMDR:expressing {lib-util:mdrElementId($data-element-concept)},
        
        element cgMDR:exemplified_by {
           element cgMDR:data_element_example_item {}
           },
        
        element cgMDR:input_to {
           attribute deriving {},
           element cgMDR:derivation_rule_specification {}
           },
        
        element cgMDR:field_name {
           attribute preferred {'false'}
           },
           
        element cgMDR:question_text {
           attribute preferred {'false'}
           }
    }    
    
return 
    if ($action = "update")
    then (
       if ($debug) 
       then (
       element output {
       $data-element,
       $value-domain,
       $data-element-concept,
       $conceptual-domain
       })
       else (
       
        let $return := lib-forms:store-document($conceptual-domain)
        return
            if ($return = 'stored')
            then (
                let $return := lib-forms:store-document($data-element-concept)
                return
                    if ($return = 'stored')
                    then (
                        let $return := lib-forms:store-document($value-domain)
                        return
                            if ($return = 'stored')
                            then (
                                let $return := lib-forms:store-document($data-element)
                                return
                                    if ($return = 'stored')
                                    then (
                                        local:success-document($data-element, $value-domain, $data-element-concept, $conceptual-domain) 
                                    )
                                    else ($return)
                            )
                            else ($return)
                    )
                    else ($return)
            )
            else ($return)
    ))
    else lib-forms:wrap-form-contents($title, local:edit-document($doc, $action, $type))



