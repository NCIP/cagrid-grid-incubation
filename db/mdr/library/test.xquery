(:~ Common test convenience variables and functions :)
module namespace test="http://www.cancergrid.org/xquery/library/test";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

declare variable $test:classification-scheme-item-1AD30C1B7 :=
    lib-util:mdrElement("classification_scheme_item", "1AD30C1B7");

declare variable $test:organization-GB-CANCERGRID-000002-1 :=
    lib-util:mdrElement("organization", "GB-CANCERGRID-000002-1");

declare variable $test:reference-document-06E6647D0 :=
    lib-util:mdrElement("reference_document", "06E6647D0");

declare variable $test:data-type-0D6579A48 :=
    lib-util:mdrElement("data_type", "0D6579A48");

declare variable $test:unit-of-measure-0EABB5D21 :=
    lib-util:mdrElement("unit_of_measure", "0EABB5D21");

declare variable $test:data-element-rating-87CA6EB31 :=
    lib-util:mdrElement("data-element-rating", "87CA6EB31");

declare variable $test:context-GB-CANCERGRID-000001-1 :=
    lib-util:mdrElement("context", "GB-CANCERGRID-000001-1");

declare variable $test:conceptual-domain-GB-CANCERGRID-0E57DB56A-0.1 :=
    lib-util:mdrElement("conceptual_domain", "GB-CANCERGRID-0E57DB56A-0.1");

declare variable $test:value-domain-GB-CANCERGRID-006BB338C-0.1 :=
    lib-util:mdrElement("value_domain", "GB-CANCERGRID-006BB338C-0.1");

declare variable $test:object-class-GB-CANCERGRID-3E5B7BE69-0.1 :=
    lib-util:mdrElement("object_class", "GB-CANCERGRID-3E5B7BE69-0.1");

declare variable $test:classification-scheme-GB-CANCERGRID-000026-1.0 :=
    lib-util:mdrElement("classification_scheme", "GB-CANCERGRID-000026-1.0");

declare variable $test:data-element-GB-CANCERGRID-0A296A56A-0.1 :=
    lib-util:mdrElement("data_element", "GB-CANCERGRID-0A296A56A-0.1");

declare variable $test:data-element-concept-GB-CANCERGRID-3B90B6D4C-0.1 :=
    lib-util:mdrElement("data_element_concept", "GB-CANCERGRID-3B90B6D4C-0.1");

declare variable $test:property-GB-CANCERGRID-4A4B57291-0.1 :=
    lib-util:mdrElement("property", "GB-CANCERGRID-4A4B57291-0.1");

declare variable $test:registration-authority-GB-CANCERGRID-CA :=
    lib-util:mdrElement("registration_authority", "GB-CANCERGRID-CA");

declare variable $test:representation-class-GB-CANCERGRID-000010-1 :=
    lib-util:mdrElement("representation_class", "GB-CANCERGRID-000010-1");

(:~
    Test an expression
    @param $function-name Function name(s) under the testing. The preferable function name format is name/n, where n is arity. Multiple function names may be separated by any delimiters
    @param $value Value of the expression
    @param $expression XQuery source code of the expression. This string will not be evaluated; it is for the presentation purpose only
    @param $expectation Expected value of the expression
    @return The test result. The test will pass if deep-equal($value,$expectation)
:)
declare function test:expression($function-name as xs:string,
    $value as item()*, $expression as xs:string, $expectation as item()*)
as element(function-test)
{
    element function-test {
        element name {$function-name},
        element invocation {$expression},
        element result {string-join(for $v in $value return string($v), " ")},
        element passed {deep-equal($value,$expectation)}
    }
};

(:~
    Test a condition
    @param $function-name Function name(s) under the testing. The preferable function name format is name/n, where n is arity. Multiple function names may be separated by any delimiters
    @param $value Value of the condition
    @param $expression XQuery source code of the condition. This string will not be evaluated; it is for the presentation purpose only
    @return The test result. The test will pass if the condition is true
:)
declare function test:condition($function-name as xs:string, $value as xs:boolean,
    $expression as xs:string) as element(function-test)
{
    element function-test {
        element name {$function-name},
        element invocation {$expression},
        element result {$value},
        element passed {$value}
    }
};
