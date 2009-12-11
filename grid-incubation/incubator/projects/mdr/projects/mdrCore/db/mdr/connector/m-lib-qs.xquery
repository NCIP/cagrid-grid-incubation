module namespace lib-qs="http://www.cagrid.org/xquery/library/query_service";

declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace httpclient="http://exist-db.org/xquery/httpclient";

declare namespace c="http://cagrid.org/schema/config";
declare namespace q="http://cagrid.org/schema/query";

declare namespace util="http://exist-db.org/xquery/util";

(:~ List supported knowledge resources (names only) :)
declare function lib-qs:listResources() as node()
{
    let $config := doc("/db/mdr/connector/config.xml")
    let $transform := doc("/db/mdr/connector/stylesheets/config_filter.xsl")
    return
        <q:resources>
            { 
                for $name in data(transform:transform($config, $transform, ())/q:query_service/@name)
                return
                    <q:resource>{$name}</q:resource>
            }
        </q:resources>
};

(:~ List details of supported knowledge resources :)
declare function lib-qs:listResourcesWithDetails() as node()
{
    let $config := doc("/db/mdr/connector/config.xml")
    let $transform := doc("/db/mdr/connector/stylesheets/config_filter.xsl")
    return
        transform:transform($config, $transform, ())
};

declare function lib-qs:selectResource-form($type) as node()*
{
    let $config := doc("/db/mdr/connector/config.xml")
    for $qs in $config/c:config/c:resources/c:query_service
    return
    if ( data($qs/@category) = $type ) 
    then 
      element option {
        attribute value {$qs/@name},
        data($qs/@name)
      }
    else ()
   
};

(:~ Query knowledge resources for a URL:)
declare function lib-qs:query($request as xs:string, $resource as xs:string) as node()?
{
   let $qs := doc("/db/mdr/connector/config.xml")/c:config/c:resources/c:query_service[@name=$resource]
   let $log := util:log-system-err($request)
    return
    if ($request)  
    then
            (: Query the resource:)
            let $content := httpclient:get(xs:anyURI($request), xs:boolean("false"), ())/httpclient:body/*
            return
                (: Generate a digest of the result before return :)
                lib-qs:chain-transform($content, tokenize(data($qs/@digestSequence), ' '))

    else ()
};

(:~ Query knowledge resources :)
declare function lib-qs:query($resource as xs:string, $src as xs:string?, $term as xs:string, $startIndex as xs:int, $numResults as xs:int) as node()?
{
    let $qs := doc("/db/mdr/connector/config.xml")/c:config/c:resources/c:query_service[@name=$resource]
    let $query :=  lib-qs:makeQuery($qs, $src, $term, $startIndex, $numResults)
    let $connectionType := $qs/@connection_type
    (:
    let $log := util:log-system-err($resource)
    let $log := util:log-system-err($qs)
    let $log := util:log-system-err($query)
    :)    

    return
    if ($query)  
    then
        if ($qs/@connection_type = "REST")
        then
            (: Generate resource specific query :)
            let $request := lib-qs:chain-transform($query, tokenize(data($qs/@requestSequence), ' '))
            (:         
            let $log := util:log-system-out($request)
            let $log := util:log-system-out($qs)
            let $log := util:log-system-out($query)
            let $log := util:log-system-out($request)
            let $log := util:log-system-out(httpclient:get(xs:anyURI($request), xs:boolean("false"), ())/httpclient:body/*)
            let $log := util:log-system-out(lib-qs:chain-transform(httpclient:get(xs:anyURI($request), xs:boolean("false"), ())/httpclient:body/*, tokenize(data($qs/@digestSequence), ' ')))
            :)           
            (: Query the resource:)
            let $content := httpclient:get(xs:anyURI($request), xs:boolean("false"), ())/httpclient:body/*
            return
                (: Generate a digest of the result before return :)
                lib-qs:chain-transform($content, tokenize(data($qs/@digestSequence), ' '))
        else ()
    else ()
};

(:~ Convert user input to common query format expected by the XSLTs :)
declare function lib-qs:makeQuery($qs as node(), $src as xs:string?, $term as xs:string, $startIndex as xs:int, $numResults as xs:int) as node()?
{
            <q:query>
                <q:serviceUrl>{data($qs/@serviceUrl)}</q:serviceUrl>
                <q:resource>{data($qs/@name)}</q:resource>
                <q:src>{$src}</q:src>
                <q:term>{$term}</q:term>
                <q:startIndex>{$startIndex}</q:startIndex>
                <q:numResults>{$numResults}</q:numResults>
            </q:query>
};

(:~ Apply the specified sequence of XSLTs to the input XML. :)
declare function lib-qs:chain-transform($query as node()?, $sequence as xs:string+) as node()?
{
    (:
    let $log := util:log-system-out($sequence)
    let $log := util:log-system-out($query)
    :)   
    let $xsl := doc(concat("/db/mdr/connector/stylesheets/",$sequence[1],".xsl"))
    return
        if (count($sequence) = 1)
        then
            transform:transform($query, $xsl, ())
        else
            lib-qs:chain-transform(transform:transform($query, $xsl, ()), subsequence($sequence, 2))
};