module namespace lib-uri-resolution="http://www.cagrid.org/xquery/library/resolver";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace cgResolver = "http://www.cagrid.org/schema/cgResolver";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";
declare namespace util="http://exist-db.org/xquery/util";

import module namespace 
lib-util="http://www.cagrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";   

declare function lib-uri-resolution:resolve($urn as xs:string?, $return-type as xs:string) as xs:anyURI
{
xs:anyURI(
if (starts-with($urn,"lexevsapi.nci.nih.gov_NCIThesaurus"))
    then (
        let $log:= util:log-system-out($urn)
        let $urn := concat("urn:",$urn)
        let $log:= util:log-system-out('RESOLVER')
        let $log := util:log-system-out(collection(lib-util:resolverPath()))
        for $resource as element()* in collection(lib-util:resolverPath())//cgResolver:resource[starts-with($urn, @urn)]
                let $log:= util:log-system-out($resource)

        let $resource-urn := data($resource/@urn)
        let $log:= util:log-system-out($resource-urn)

        return
            for $uri in $resource/cgResolver:uri  
            where $uri/@rank = "1"
            and $uri/@return=$return-type
            return replace(xs:string($urn), xs:string($resource-urn), xs:string($uri/text()))
        )
    else if (starts-with($urn,"lexevsapi.nci.nih.gov_NCIMetaThesaurusConcept"))
    then (
        let $urn := concat("urn:",$urn)
        let $log := util:log-system-out(collection(lib-util:resolverPath()))
        for $resource as element()* in collection(lib-util:resolverPath())//cgResolver:resource[starts-with($urn, @urn)]
        let $log:= util:log-system-out($resource)

        let $resource-urn := data($resource/@urn)
        let $log:= util:log-system-out($resource-urn)

        return
            for $uri in $resource/cgResolver:uri  
            where $uri/@rank = "1"
            and $uri/@return=$return-type
            return replace(xs:string($urn), xs:string($resource-urn), xs:string($uri/text()))
        )
    else if (starts-with($urn,"rest.bioontology.org_BioPortal-Ocre"))
    then (
        let $log:= util:log-system-out('RESOURCE-URN')
        let $urn := concat("urn:",$urn)
        for $resource as element()* in collection(lib-util:resolverPath())//cgResolver:resource[starts-with($urn, @urn)]
        let $resource-urn := data($resource/@urn)
        let $log:= util:log-system-out('RESOURCE-URN')
        let $log:= util:log-system-out($resource-urn)
        return
            for $uri in $resource/cgResolver:uri  
            where $uri/@rank = "1"
            and $uri/@return=$return-type
            return replace(xs:string($urn), xs:string($resource-urn), xs:string($uri/text()))
        )
    else $urn
)    
   
};

declare function lib-uri-resolution:html-anchor($urn as xs:string, $return-type as xs:string) as element(a)
{
(:
let $log:= util:log-system-err($urn)
let $log:= util:log-system-err(lib-uri-resolution:resolve($urn, $return-type))
:)
let $uri as xs:anyURI := lib-uri-resolution:resolve($urn, $return-type)
return
   element a {
      attribute href {$uri},
      attribute target {"_blank"},
      $urn
      }
};

declare function lib-uri-resolution:html-anchor($urn as xs:string) as element(a)
{
    let $log:= util:log-system-out($urn)
    
    return
    lib-uri-resolution:html-anchor($urn, 'html')
   };