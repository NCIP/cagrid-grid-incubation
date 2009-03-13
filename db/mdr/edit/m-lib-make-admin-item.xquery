module namespace lib-make-admin-item="http://www.cancergrid.org/xquery/library/make-admin-item";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  

declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";

import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
at "../web/m-lib-rendering.xquery";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
at "../library/m-administered-item.xquery";  

declare function lib-make-admin-item:current-date() as xs:string
{
   substring-before(xs:string(current-date()),'Z')
};

declare function lib-make-admin-item:administration-record(
    $administrative-note as xs:string,
    $administrative-status as xs:string,
    $registration-status as xs:string
    ) as element(cgMDR:administered_item_administration_record)
{
    element cgMDR:administered_item_administration_record {
        element cgMDR:administrative_note {$administrative-note},
        element cgMDR:administrative_status {$administrative-status},
        element cgMDR:creation_date {lib-make-admin-item:current-date()},
        element cgMDR:effective_date {lib-make-admin-item:current-date()},
        element cgMDR:last_change_date {lib-make-admin-item:current-date()},
        element cgMDR:registration_status {$registration-status}
    }
};

declare function lib-make-admin-item:custodians(
    $administered-by as xs:string,
    $registered-by as xs:string,
    $submitted-by as xs:string
    ) as element()*
{
    element cgMDR:administered_by {$administered-by},
    element cgMDR:registered_by {$registered-by},
    element cgMDR:submitted_by {$submitted-by}
};

declare function lib-make-admin-item:having(
    $context-identifier as xs:string,
    $country-identifier as xs:string,
    $language-identifier as xs:string,
    $name as xs:string,
    $definition as xs:string?,
    $preferred-designation as xs:boolean,
    $definition-source-reference as xs:string?
    ) as element(cgMDR:having)
{
    element cgMDR:having {
        element cgMDR:context_identifier {$context-identifier},
        element cgMDR:containing {
            element cgMDR:language_section_language_identifier {
                element cgMDR:country_identifier {$country-identifier},
                element cgMDR:language_identifier {$language-identifier}
            },
            element cgMDR:name {$name},
            element cgMDR:definition_text {$definition}, 
            element cgMDR:preferred_designation {$preferred-designation},
            element cgMDR:definition_source_reference {$definition-source-reference}
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




