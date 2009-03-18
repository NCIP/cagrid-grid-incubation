xquery version "1.0";

module namespace lib-der="http://www.cancergrid.org/xquery/library/data-element-reduced";

(: ~
: Module Name         module for data element creation functions            
: Module Version     1.0
: Date                         6th May 2008
: Copyright                The cancergrid consortium
: Module overview  Support functions for data element creation
:
:)

(:~
:    @author Andrew Tsui
:    @version 0.1
~ :)

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
lib-forms="http://www.cancergrid.org/xquery/library/forms" 
at "../edit/m-lib-forms.xquery";

import module namespace 
lib-search="http://www.cancergrid.org/xquery/library/search" 
at "m-lib-search.xquery";

declare namespace der-xs = "http://cancergrid.org/schema/DataElementReduced";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179 =  "http://www.cancergrid.org/schema/ISO11179";  

(: eXist module namespace :)
declare namespace system="http://exist-db.org/xquery/system";
declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

(: Default global variable :)
declare variable $lib-der:service-path as xs:string := system:get-module-load-path();
declare variable $lib-der:mdr-path as xs:string := substring-before(system:get-module-load-path(), '/services'); 

(: simpler random id function :)
(:
declare function lib-der:random-name($starting as xs:string?, $length as xs:nonNegativeInteger) as xs:string
{
   let $chars as xs:string* := ('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E')
   let $rand as xs:nonNegativeInteger := xs:nonNegativeInteger(util:random(15) + 1)
   return
      if (string-length($starting) lt $length)
      then lib-der:random-name (concat($starting, $chars[$rand]),$length)
      else $starting
};
:)

(:~
  Generate an identifier for a prospective new data element.
  Type specification is not required as mdrElementId resolves this
  ID is unique throughout the database - avoiding two admin items
  having the same identifier.
:)
(:
declare function lib-der:generate-id() as xs:string
{
   (: needs to accept a sequence of previous identifiers to include in its search :)
   let $new-id := lib-der:random-name((), 9)
   return
      (:test naked ID documents:)
      if (doc-available(concat($new-id,'.xml')))
      then lib-der:generate-id()
      else 
         if (
            for $id in lib-util:mdrElements('value_domain')//@permissible_value_identifier
            where xs:string($id) = $new-id
            return true())
         then lib-der:generate-id()
         else
            if (
               for $id in lib-util:mdrElements('conceptual_domain')//@value_meaning_identifier
               where xs:string($id) = $new-id
               return true())
            then lib-der:generate-id()
            else
               if (
                  for $id in lib-util:mdrElements()//@data_identifier
                  where xs:string($id) = $new-id
                  return true())
               then lib-der:generate-id()
               else $new-id
         
};
:)

declare function lib-der:store-document($document as node()) as xs:string?
{
   let $doc-name := concat(lib-util:mdrElementId($document),'.xml')
   let $collection := lib-util:mdrElementType($document)
   
   let $resource_location := concat($lib-der:mdr-path, '/data/', $collection)
   return
      util:catch(
         "java.lang.Exception",
         if (xmldb:collection-exists($resource_location) = true())
         then ( 
                  xmldb:store($resource_location, $doc-name, $document)
         )
         else ()
         , util:log-system-err( 
             <error exception="{$util:exception}">
                <message>{$util:exception-message}</message>
                <resource-location>{$resource_location}</resource-location> 
                <user>{xmldb:get-current-user()}</user>
                <doc-name>{$doc-name}</doc-name>
                <collection>{$collection}</collection>
            </error>
            )
        )
};

(:~ Creating a simple(reduced) data element and all required related elements :)
declare function lib-der:create-data-element-reduced($doc as element(), $user as xs:string, $password as xs:string) as element()?
{
    let $service_db_path := 
        if (contains($lib-der:service-path, 'embedded-eXist-server'))
        then substring-after($lib-der:service-path, 'embedded-eXist-server')
        else substring-after($lib-der:service-path, 'xmldb:exist://')
    let $defaults_file :=  
        <parameters>
            <param name="defaults_file" value="{$service_db_path}/documents/defaults_xsl.xml"/> 
        </parameters> 
    let $data-element as element() := transform:transform($doc, doc(concat($lib-der:service-path, '/stylesheets/DataElementReduced_data_element.xsl')), $defaults_file)
    let $value-domain as element() : = transform:transform($doc, doc(concat($lib-der:service-path, '/stylesheets/DataElementReduced_value_domain.xsl')), $defaults_file)
    let $data-element-concept as element():= transform:transform($doc, doc(concat($lib-der:service-path, '/stylesheets/DataElementReduced_data_element_concept.xsl')), $defaults_file)
    let $conceptual-domain as element() := transform:transform($doc, doc(concat($lib-der:service-path, '/stylesheets/DataElementReduced_conceptual_domain.xsl')), $defaults_file)
    return
        util:catch(
            "java.lang.Exception",
            (
                if (xmldb:login(concat($lib-der:mdr-path, '/data'), $user, $password))
                then
                    (
                        let $conceptual-domain-path := lib-der:store-document($conceptual-domain)
                        let $data-element-concept-path := lib-der:store-document($data-element-concept)
                        let $value-domain-path := lib-der:store-document($value-domain)
                        let $data-element-path := lib-der:store-document($data-element)
                        return
                            lib-search:dataElement(lib-util:mdrElementId($data-element))
                        (:
                            <stored>
                                <conceptual-domain path="{$conceptual-domain-path}"/>
                                <data-element-concept path="{$data-element-concept-path}"/>
                                <value-domain path="{$value-domain-path}"/>
                                <data-element path="{$data-element-path}"/>
                            </stored>
                         :)
                    )
                else ()
            ),
            util:log-system-err(
                <error exception="{$util:exception}">
                    <message>{$util:exception-message}</message>
                    <user>{xmldb:get-current-user()}</user>
                    <conceptual-domain>{$conceptual-domain}</conceptual-domain>
                    <data-element-concept>{$data-element-concept}</data-element-concept>
                    <value-domain>{$value-domain}</value-domain>
                    <data-element>{$data-element}</data-element>
                </error>
            )
      )
};
 
declare function lib-der:filled-default-values($element as element())  as element()
{
    element {node-name($element)}
    {
        for $attr in $element/@*
        return
            typeswitch($attr)
                case attribute(value-identifier) return attribute value-identifier {lib-forms:generate-id()}
                case attribute(meaning-identifier) return attribute meaning-identifier {lib-forms:generate-id()}
                default return $attr,
         if (($element instance of element(der-xs:value)) and (count($element/@*) = 0)) 
         then (attribute value-identifier {lib-forms:generate-id()}, attribute meaning-identifier {lib-forms:generate-id()})
         else (),
        for $child in $element/node()
        return
            typeswitch($child)
                case element(der-xs:reg-auth) 
                    return 
                        element der-xs:reg-auth 
                        {
                             for $org in lib-util:mdrElements("registration_authority")[.//cgMDR:registrar_identifier = data($element/der-xs:registrar)]//@organization_identifier
                             return data($org[1])
                        }
                case element(der-xs:data-element-data-identifier) return element der-xs:data-element-data-identifier {lib-forms:generate-id()}
                case element(der-xs:dec-data-identifier) return element der-xs:dec-data-identifier {lib-forms:generate-id()}
                case element(der-xs:conceptual-domain-data-identifier) return element der-xs:conceptual-domain-data-identifier {lib-forms:generate-id()}
                case element(der-xs:value-domain-data-identifier) return element der-xs:value-domain-data-identifier {lib-forms:generate-id()}
                case element(der-xs:version) return element der-xs:version {0.1}
                default return
                    if ($child instance of element())
                    then lib-der:filled-default-values($child)
                    else $child
    }
};

declare function lib-der:get-data-types() as element(der-xs:datatypes)
{
    <der-xs:datatypes>
    {
        for $item in lib-util:mdrElements('data_type')
        order by $item/cgMDR:datatype_name
        return
            <der-xs:datatype id="{$item/@datatype_identifier}" name="{$item/cgMDR:datatype_name}" scheme="{$item/cgMDR:datatype_scheme_reference}"/>
    }
    </der-xs:datatypes>
};

declare function lib-der:get-uom() as element(der-xs:unit_of_measures)
{
    <der-xs:unit_of_measures>
    {
        for $item in lib-util:mdrElements('unit_of_measure')
        order by $item/cgMDR:unit_of_measure_name
        return
            <der-xs:unit_of_measure id="{$item/@unit_of_measure_identifier}" name="{$item/cgMDR:unit_of_measure_name}"/>
    }
    </der-xs:unit_of_measures>
};

declare function lib-der:get-organization-contacts() as element(der-xs:organization-contacts)
{
    <der-xs:organization-contacts>
    {
        for $item in lib-util:mdrElements('organization')//cgMDR:Contact
        order by $item/cgMDR:contact_name
        return
            <der-xs:contact id="{$item/@contact_identifier}" name="{$item/cgMDR:contact_name}" title="{$item/cgMDR:contact_title}"/>
    }
    </der-xs:organization-contacts>
};

declare function lib-der:get-reg-auth() as element(der-xs:reg-auth)
{
    <der-xs:reg-auth>
    {
        for $item in lib-util:mdrElements('registration_authority')//cgMDR:Registration_Authority
        order by $item/cgMDR:organization_name, $item/cgMDR:represented_by/cgMDR:registrar_contact/cgMDR:contact_name 
        return
            <der-xs:contact id="{$item/cgMDR:represented_by/cgMDR:registrar_identifier}" name="{$item/cgMDR:represented_by/cgMDR:registrar_contact/cgMDR:contact_name}" organization="{$item/cgMDR:organization_name}"/>
    }
    </der-xs:reg-auth>
};