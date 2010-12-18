module namespace lib-make-admin-item="http://www.cagrid.org/xquery/library/make-admin-item";

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  

declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace datetime="http://exist-db.org/xquery/datetime";
import module namespace 
lib-rendering="http://www.cagrid.org/xquery/library/rendering" 
at "../web/m-lib-rendering.xquery";

import module namespace 
lib-util="http://www.cagrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
administered-item="http://www.cagrid.org/xquery/library/administered-item" 
at "../library/m-administered-item.xquery";  

declare function lib-make-admin-item:administration-record(
    $administrative-note as xs:string,
    $administrative-status as xs:string,
    $creation-date,
    $registration-status as xs:string
    ) as element(openMDR:administered_item_administration_record)
{
    element openMDR:administered_item_administration_record {
        element openMDR:administrative_note {$administrative-note},
        element openMDR:administrative_status {$administrative-status},
        element openMDR:creation_date {$creation-date},
        element openMDR:effective_date {$creation-date},
        element openMDR:last_change_date {datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")},
        element openMDR:registration_status {$registration-status}
    }
};

declare function lib-make-admin-item:custodians(
    $administered-by as xs:string,
    $registered-by as xs:string,
    $submitted-by as xs:string
    ) as element()*
{
    element openMDR:administered_by {$administered-by},
    element openMDR:registered_by {$registered-by},
    element openMDR:submitted_by {$submitted-by}
};

declare function lib-make-admin-item:havings(
    $context-ids as xs:string*,
    $country-identifiers as xs:string*,
    $language-identifiers as xs:string*,
    $names as xs:string*,
    $definitions as xs:string*,
    $preferred as xs:string,
    $sources as xs:string*) as node()*
    {
    
    for $name at $pos in $names
    where $name > ''
               return (
                     lib-make-admin-item:having($context-ids[$pos],
                        $country-identifiers[$pos],
                        $language-identifiers[$pos],
                        $name,
                        $definitions[$pos],
                        if($preferred > '') then (
                        xs:boolean((xs:int($preferred) = xs:int($pos))))
                        else (xs:boolean('false')),
                        $sources[$pos]
                        )
                      )
    
    };

declare function lib-make-admin-item:having(
    $context-identifier as xs:string,
    $country-identifier as xs:string,
    $language-identifier as xs:string,
    $name as xs:string,
    $definition as xs:string?,
    $preferred-designation as xs:boolean,
    $definition-source-reference as xs:string?
    ) as element(openMDR:having)
{
    element openMDR:having {
        element openMDR:context_identifier {$context-identifier},
        element openMDR:containing {
            element openMDR:language_section_language_identifier {
                element openMDR:country_identifier {$country-identifier},
                element openMDR:language_identifier {$language-identifier}
            },
            element openMDR:name {$name},
            element openMDR:definition_text {$definition}, 
            element openMDR:preferred_designation {$preferred-designation},
            element openMDR:definition_source_reference {$definition-source-reference}
        }
    }
};

declare function lib-make-admin-item:identifier-attributes(
        $item_registration_authority_identifier as xs:string, 
        $data_identifier as xs:string, 
        $version as xs:string
        ) as attribute()*
{
        attribute item_registration_authority_identifier {$item_registration_authority_identifier},
        attribute data_identifier {$data_identifier},
        attribute version {$version}
};




