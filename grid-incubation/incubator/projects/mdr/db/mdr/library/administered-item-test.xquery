import module namespace 
administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
at "../library/m-administered-item.xquery";    

import module namespace 
test="http://www.cancergrid.org/xquery/library/test" 
at "../library/test.xquery";

import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

lib-rendering:txfrm-webpage(
    "administered-item tests",
    element function-tests {
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:context-GB-CANCERGRID-000001-1), "administered-item:preferred-name($test:context-GB-CANCERGRID-000001-1)", "Clinical Trials"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:conceptual-domain-GB-CANCERGRID-0E57DB56A-0.1), "administered-item:preferred-name($test:conceptual-domain-GB-CANCERGRID-0E57DB56A-0.1)", "Study Subject First Tamoxifen Prescription Date"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:value-domain-GB-CANCERGRID-006BB338C-0.1), "administered-item:preferred-name($test:value-domain-GB-CANCERGRID-006BB338C-0.1)", "Date of first locoregional recurrence"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:object-class-GB-CANCERGRID-3E5B7BE69-0.1), "administered-item:preferred-name($test:object-class-GB-CANCERGRID-3E5B7BE69-0.1)", "Study subject first distant recurrence observation date"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:classification-scheme-GB-CANCERGRID-000026-1.0), "administered-item:preferred-name($test:classification-scheme-GB-CANCERGRID-000026-1.0)", "CancerGrid Classification Scheme"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:data-element-GB-CANCERGRID-0A296A56A-0.1), "administered-item:preferred-name($test:data-element-GB-CANCERGRID-0A296A56A-0.1)", "Patient Full Name"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:data-element-concept-GB-CANCERGRID-3B90B6D4C-0.1), "administered-item:preferred-name($test:data-element-concept-GB-CANCERGRID-3B90B6D4C-0.1)", "Patient Full Name"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:property-GB-CANCERGRID-4A4B57291-0.1), "administered-item:preferred-name($test:property-GB-CANCERGRID-4A4B57291-0.1)", "Date of Birth"),
        test:expression("administered-item:preferred-name/1", administered-item:preferred-name($test:representation-class-GB-CANCERGRID-000010-1), "administered-item:preferred-name($test:representation-class-GB-CANCERGRID-000010-1)", "Code"),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:context-GB-CANCERGRID-000001-1), "administered-item:preferred-definition($test:context-GB-CANCERGRID-000001-1)", "ISO11179 context entry"),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:conceptual-domain-GB-CANCERGRID-0E57DB56A-0.1), "administered-item:preferred-definition($test:conceptual-domain-GB-CANCERGRID-0E57DB56A-0.1)", "The date the study subject was first prescribed tamoxifen"),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:value-domain-GB-CANCERGRID-006BB338C-0.1), "administered-item:preferred-definition($test:value-domain-GB-CANCERGRID-006BB338C-0.1)", "The date upon which the first locoregional recurrence of the disease was observed"),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:object-class-GB-CANCERGRID-3E5B7BE69-0.1), "administered-item:preferred-definition($test:object-class-GB-CANCERGRID-3E5B7BE69-0.1)", "The date of observation of the first distant recurrence of the study subject's primary tumour"),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:classification-scheme-GB-CANCERGRID-000026-1.0), "administered-item:preferred-definition($test:classification-scheme-GB-CANCERGRID-000026-1.0)", "Default classification scheme for cgMDR exported from the protege
            classification scheme in the demo follows a SKOS-like controlled vocabulary schema"),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:data-element-GB-CANCERGRID-0A296A56A-0.1), "administered-item:preferred-definition($test:data-element-GB-CANCERGRID-0A296A56A-0.1)", "Full name of patient as normally written by subject.  Order of given and family names is that normally written by the subject."),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:data-element-concept-GB-CANCERGRID-3B90B6D4C-0.1), "administered-item:preferred-definition($test:data-element-concept-GB-CANCERGRID-3B90B6D4C-0.1)", "Full name of patient as normally written by subject.  Order of given and family names is that normally written by the subject."),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:property-GB-CANCERGRID-4A4B57291-0.1), "administered-item:preferred-definition($test:property-GB-CANCERGRID-4A4B57291-0.1)", "The calendar date on which a person was born."),
        test:expression("administered-item:preferred-definition/1", administered-item:preferred-definition($test:representation-class-GB-CANCERGRID-000010-1), "administered-item:preferred-definition($test:representation-class-GB-CANCERGRID-000010-1)", "A system of valid symbols that substitute for specified values e.g. alpha, numeric, symbols and/or combinations")
    }
)
