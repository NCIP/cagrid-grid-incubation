declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace xqf =  "http://www.xqueryfunctions.com" ; 
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#"; 
declare namespace dcterms="http://purl.org/dc/terms/";
declare namespace sawsdl="http://www.w3.org/ns/sawsdl";

import module namespace 
    lib-util="http://www.cancergrid.org/xquery/library/util" 
    at "../library/m-lib-util.xquery";
  
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   
   
declare function local:normalised-name($name as xs:string) as xs:string
{
    lower-case(replace(normalize-space($name),'[ \-().,'']','_'))
};

declare function local:local-name($admin-item as element()) as xs:string
{
    let $name as xs:string := administered-item:preferred-name($admin-item)
    return
        if (empty($admin-item//cgMDR:field_name[@preferred='true']/text()))
        then local:normalised-name($name)
        else (xs:string($admin-item//cgMDR:field_name[@preferred='true']/text()))
};


let $h1 := response:set-header("Content-Disposition", 'attachment; filename="registry-schema.xsd"')
let $h2 := response:set-header("Content-Type", 'application/xml')
return

        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" 
            xmlns:sawsdl="http://www.w3.org/ns/sawsdl"> 
            {
            for $value-domain in lib-util:mdrElements("value_domain")[.//cgMDR:registration_status/text() ne 'Superseded']
            for $datatype in lib-util:mdrElements("data_type")
            let $sawsdl := lib-util:mdrElementId($value-domain)
            let $local-name as xs:string := local:local-name($value-domain)
                
            where exists(//@permissible_value_identifier) or $value-domain//cgMDR:value_domain_datatype/text() = xs:string($datatype/@datatype_identifier)
            return 
                <xs:simpleType sawsdl:modelReference="{$sawsdl}" name="{$local-name}">
                    <xs:restriction base="{$datatype/cgMDR:datatype_name}">
                        {if ($value-domain//cgMDR:value_domain_format/text() > "") 
                        then (<xs:pattern value="{$value-domain//cgMDR:value_domain_format}"/>)
                        else ()}
                        {if ($value-domain//cgMDR:value_domain_maximum_character_quantity/text() > "")
                        then (<xs:maxLength value="{$value-domain//cgMDR:value_domain_maximum_character_quantity}"/>)
                        else ()}
                        {
                        for $containing in $value-domain/cgMDR:containing
                        return
                            <xs:enumeration value="{$containing/cgMDR:value_item}"/>
                        }
                     </xs:restriction>
                </xs:simpleType>
                ,
                
        
            for $data-element in lib-util:mdrElements('data_element')[.//cgMDR:registration_status/text() ne 'Superseded']
            for $value-domain in lib-util:mdrElements('value_domain')[.//cgMDR:registration_status/text() ne 'Superseded']
                let $sawsdl as xs:string := lib-util:mdrElementId($data-element)
                let $data-element-name as xs:string := local:local-name($data-element)
                let $value-domain-name as xs:string := local:local-name($value-domain)
            where $data-element//cgMDR:representing/text() = lib-util:mdrElementId($value-domain)

            return
                 <xs:element name="{$data-element-name}" type="{$value-domain-name}" sawsdl:modelReference="{$sawsdl}"/>
            }
            </xs:schema>
