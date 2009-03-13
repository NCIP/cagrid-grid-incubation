xquery version "1.0";

(:~
 : Regression tests
 :
 : Copyright (C) 2007 The CancerGrid Consortium
 : @author Igor Toujilov
 : @author Steve Harris
 :)

import module namespace 
    rendering="http://www.cancergrid.org/xquery/library/rendering"
    at "../web/m-lib-rendering.xquery";

import module namespace 
    lib-util="http://www.cancergrid.org/xquery/library/util"
    at "../library/m-lib-util.xquery";

declare namespace transform="http://exist-db.org/xquery/transform";
declare namespace cgRegression="http://www.cancergrid.org/schema/cgRegression";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace util="http://exist-db.org/xquery/util";

declare function local:xmldb-uri($package-name as xs:string, $file-name as xs:string) as xs:string
{
   concat('xmldb:exist://',lib-util:getResourcePath($package-name),$file-name)
};

declare function local:absolute-uri($package-name as xs:string, $file-name as xs:string) as xs:anyURI
{
    (: When this main module is invoked using HTTPS, documents/scripts under testing
        should still be accessed using HTTP. Because when they accessed using HTTPS
        from containing HTTPS session, we will get an exception like this:

        sun.security.validator.ValidatorException: PKIX path building failed:
        sun.security.provider.certpath.SunCertPathBuilderException: unable to find
        valid certification path to requested target
    :)

    concat("http://", request:request-servername(), ":",
        string(doc(concat(lib-util:rootURI(), "/config.xml"))/config/common/@port),
        substring-before(request:get-uri(), "regression-test"), $package-name,
        "/", $file-name
    ) cast as xs:anyURI
};

(:~
    Test a web document by invoking its HTTP URL and then comparing its output
    with a sample document (when it is provided). The test is passed,
    if the HTTP response after stripping its session information (when it exists)
    is deep-equal to the sample document.

    @param $http-uri A URI of the web document under the test. This URI may point to a script, generating the document under test, or the document itself. This URI should be relative to its package name defined by the $package-name parameter
    @param $package-name A name of a package where the $http-uri resides
    @param $sample-doc-name The sample document name, relative to the $package-name package
    @param $test-id An ID of the test. This ID is unique locally in the $package-name package
    @param $test-description A description of the test
    @param $test-type Test type
    @return Test result

    If $sample-doc-name parameter is empty, the document comparison
    is not executed but output document availability is checked instead.
    When $http-uri points to an XQuery script, this test also detects XQuery
    errors because in this case the output document is not available.
:)
declare function local:test($http-uri as xs:string, $package-name as xs:string, 
    $sample-doc-name as xs:string?, $test-id as xs:string,
    $test-description as xs:string, $test-type as xs:string) as element(test-result)
{
    let $sample-doc :=
        if ($sample-doc-name)
        then doc(local:xmldb-uri($package-name, $sample-doc-name))
        else ()

    let $passed :=
        util:catch("java.lang.Exception",
            if ($test-type="web-page-comparison") 
            then 
               if ($sample-doc-name) then
                   if (
                       deep-equal(
                           transform:transform(
                               doc(local:absolute-uri($package-name,$http-uri)),
                               local:xmldb-uri('regression-test',"strip-session.xsl"),
                               ()),
                           transform:transform(
                               $sample-doc,
                               local:xmldb-uri('regression-test',"strip-session.xsl"),
                               ())
                       )
                   )
                   then "yes" else "no"
                else
                   if (doc-available(local:absolute-uri($package-name,$http-uri)))
                      then "yes" else "no"
            else (),
            "there has been a java error - could not execute test")

    return
       element test-result {
          attribute id {$test-id},
          attribute module {$package-name},
          attribute description {$test-description},
          attribute passed {$passed}
(:          ,
          attribute resource {local:absolute-uri($package-name,$http-uri)},
          element content {doc(local:absolute-uri($package-name,$http-uri))/html},
          if ($sample-doc-name) 
          then element comparison {$sample-doc/html} 
          else ():)
          
       }
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

let $as-xml := request:get-parameter("as-xml","false")

let $content as element() := element test-results { 
      for $test in collection(lib-util:rootURI())[ends-with(document-uri(.),"/tests.xml")]//cgRegression:mdr-test
      where $test/@skip != "true" or empty($test/@skip)    
      return local:test(
         $test/cgRegression:script,
         $test/cgRegression:module,
         $test/cgRegression:comparison, 
         $test/cgRegression:test-name, 
         $test/cgRegression:test-desc,
         $test/@type)
   }


return
   if ($as-xml = 'true') 
   then $content
   else rendering:txfrm-webpage("Regression Test Results", $content, false(),"", "regression-test.xquery")
