import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
test="http://www.cancergrid.org/xquery/library/test" 
at "../library/test.xquery";

import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";
declare namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

let $types := ("conceptual_domain", "value_domain", "object_class", "data_type",
    "classification_scheme", "context", "data_element", "data_element_concept",
    "data-element-rating", "organization", "property", "reference_document",
    "registration_authority", "representation_class", "unit_of_measure")

return
    lib-rendering:txfrm-webpage(
        "lib-util tests",
        element function-tests {
            test:expression("lib-util:mdrElementId/1", lib-util:mdrElementId($test:organization-GB-CANCERGRID-000002-1), "lib-util:mdrElementId($test:organization-GB-CANCERGRID-000002-1)", "GB-CANCERGRID-000002-1"),
            test:expression("lib-util:mdrElementId/1", lib-util:mdrElementId($test:reference-document-06E6647D0), "lib-util:mdrElementId($test:reference-document-06E6647D0)", "06E6647D0"),
            test:expression("lib-util:mdrElementId/1", lib-util:mdrElementId($test:data-type-0D6579A48), "lib-util:mdrElementId($test:data-type-0D6579A48)", "0D6579A48"),
            test:expression("lib-util:mdrElementId/1", lib-util:mdrElementId($test:unit-of-measure-0EABB5D21), "lib-util:mdrElementId($test:unit-of-measure-0EABB5D21)", "0EABB5D21"),
            test:expression("lib-util:mdrElementId/1", lib-util:mdrElementId($test:context-GB-CANCERGRID-000001-1), "lib-util:mdrElementId($test:context-GB-CANCERGRID-000001-1)", "GB-CANCERGRID-000001-1"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:conceptual-domain-GB-CANCERGRID-0E57DB56A-0.1), "lib-util:mdrElementType($test:conceptual-domain-GB-CANCERGRID-0E57DB56A-0.1)", "conceptual_domain"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:value-domain-GB-CANCERGRID-006BB338C-0.1), "lib-util:mdrElementType($test:value-domain-GB-CANCERGRID-006BB338C-0.1)", "value_domain"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:object-class-GB-CANCERGRID-3E5B7BE69-0.1), "lib-util:mdrElementType($test:object-class-GB-CANCERGRID-3E5B7BE69-0.1)", "object_class"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:data-type-0D6579A48), "lib-util:mdrElementType($test:data-type-0D6579A48)", "data_type"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:classification-scheme-GB-CANCERGRID-000026-1.0), "lib-util:mdrElementType($test:classification-scheme-GB-CANCERGRID-000026-1.0)", "classification_scheme"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:context-GB-CANCERGRID-000001-1), "lib-util:mdrElementType($test:context-GB-CANCERGRID-000001-1)", "context"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:data-element-GB-CANCERGRID-0A296A56A-0.1), "lib-util:mdrElementType($test:data-element-GB-CANCERGRID-0A296A56A-0.1)", "data_element"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:data-element-concept-GB-CANCERGRID-3B90B6D4C-0.1), "lib-util:mdrElementType($test:data-element-concept-GB-CANCERGRID-3B90B6D4C-0.1)", "data_element_concept"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:organization-GB-CANCERGRID-000002-1), "lib-util:mdrElementType($test:organization-GB-CANCERGRID-000002-1)", "organization"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:property-GB-CANCERGRID-4A4B57291-0.1), "lib-util:mdrElementType($test:property-GB-CANCERGRID-4A4B57291-0.1)", "property"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:reference-document-06E6647D0), "lib-util:mdrElementType($test:reference-document-06E6647D0)", "reference_document"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:registration-authority-GB-CANCERGRID-CA), "lib-util:mdrElementType($test:registration-authority-GB-CANCERGRID-CA)", "registration_authority"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:representation-class-GB-CANCERGRID-000010-1), "lib-util:mdrElementType($test:representation-class-GB-CANCERGRID-000010-1)", "representation_class"),
            test:expression("lib-util:mdrElementType/1", lib-util:mdrElementType($test:unit-of-measure-0EABB5D21), "lib-util:mdrElementType($test:unit-of-measure-0EABB5D21)", "unit_of_measure"),

            (: MDR types consistency :)
            for $t in $types
            let $cond :=
                concat("not(empty(lib-util:mdrElements('", $t, "'))) and ",
                    "(every $e in lib-util:mdrElements('", $t, "') satisfies ",
                    "(lib-util:mdrElementType($e) eq '", $t, "'))")
            return test:condition("lib-util:mdrElements/1 and lib-util:mdrElementType/1",
                util:eval($cond), $cond),

            (: IDs uniqueness :)
            for $t in $types
            let $cond :=
                concat(
                    "let $ids := ",
                        "for $e in lib-util:mdrElements('", $t, "'), ",
                            "$id in lib-util:mdrElementId($e) ",
                        "order by $id ",
                        "return $id ",
                    "return (count($ids) eq count(distinct-values($ids)))"
                )
            return test:condition("lib-util:mdrElements/1 and lib-util:mdrElementId/1",
                util:eval($cond), $cond),

            (: Universal belonging to a type :)
            let $cond :=
                concat(
                    "every $e in lib-util:mdrElements() satisfies ",
                    "(count(lib-util:mdrElementType($e)) eq 1)"
                )
            return test:condition("lib-util:mdrElements/0 and lib-util:mdrElementType/1",
                util:eval($cond), $cond),

            (: Universal existence of an ID :)
            let $cond :=
                concat(
                    "every $e in lib-util:mdrElements() satisfies ",
                    "(count(lib-util:mdrElementId($e)) eq 1)"
                )
            return test:condition("lib-util:mdrElements/0 and lib-util:mdrElementId/1",
                util:eval($cond), $cond),

            (: Inclusiveness in the universal :)
            for $t in $types
            let $cond :=
                concat("every $e in lib-util:mdrElements('", $t, "') satisfies (lib-util:mdrElements()[. is $e])")
            return test:condition("lib-util:mdrElements/1 and lib-util:mdrElements/0",
                util:eval($cond), $cond),

            let $cond :=
                concat(
                    "every $e in lib-util:mdrElements() satisfies ",
                    "($e is lib-util:mdrElement(lib-util:mdrElementType($e), lib-util:mdrElementId($e)))"
                )
            return test:condition("lib-util:mdrElements/0, lib-util:mdrElementType/1, lib-util:mdrElementId/1, and lib-util:mdrElement/2",
                util:eval($cond), $cond),

            (: These tests are done in the previous test, but may help to 
                isolate a problem when the previous test is not passed :)
            for $t in $types
            let $cond :=
                concat(
                    "every $e in lib-util:mdrElements('", $t, "') satisfies ",
                    "($e is lib-util:mdrElement(lib-util:mdrElementType($e), lib-util:mdrElementId($e)))"
                )
            return test:condition("lib-util:mdrElements/1, lib-util:mdrElementType/1, lib-util:mdrElementId/1, and lib-util:mdrElement/2",
                util:eval($cond), $cond),

            for $t in $types
            let $cond :=
                concat(
                    "every $e in lib-util:mdrElements('", $t, "')[1] satisfies ",
                    (: lib-util:mdrElement/1 is time consumable, so test only one element :)
                    "($e is lib-util:mdrElement(lib-util:mdrElementId($e)))"
                )
            return test:condition("lib-util:mdrElements/1, lib-util:mdrElementId/1, and lib-util:mdrElement/1",
                util:eval($cond), $cond)
        }
    )
