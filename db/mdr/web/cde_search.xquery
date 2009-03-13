declare namespace request="http://exist-db.org/xquery/request";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util"
at "../library/m-lib-util.xquery";

let $xquery-path := concat(lib-util:getServer(), '/exist/rest', lib-util:webPath(), 'search.xquery')
let $suggestions-path := concat(lib-util:getServer(), '/exist/rest', lib-util:webPath(), 'search_suggestions.xquery')
let $image-path := concat(lib-util:getServer(), '/exist/rest', lib-util:imagePath())
return

<OpenSearchDescription xmlns:moz="http://www.mozilla.org/2006/browser/search/" xmlns="http://a9.com/-/spec/opensearch/1.1/">
    <ShortName>CancerGrid CDE Search</ShortName>
    <Description>CancerGrid CDE Search - First Search Plugin Test</Description>
    <InputEncoding>UTF-8</InputEncoding>
    <Url type="text/html" method="GET" template="{$xquery-path}?phrase={{searchTerms}}"/>
    <Url type="application/x-suggestions+json" template="{$suggestions-path}?phrase={{searchTerms}}"/>
    <Image height="16" width="16" type="../images/png">{$image-path}DotForIconOnLight16.png</Image>
    <Image height="16" width="16" type="../images/vnd.microsoft.icon">{$image-path}CancerGridDotOnLight.ico</Image>
    <Query role="example" searchTerms="cancer"/>
    <Attribution>Copyright (C) 2006 The CancerGrid Consortium</Attribution>
    <moz:SearchForm>{$xquery-path}</moz:SearchForm>
</OpenSearchDescription>
