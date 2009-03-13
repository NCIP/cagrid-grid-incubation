import module namespace
permissible-value="http://www.cancergrid.org/xquery/library/permissible-value"
at "../library/m-permissible-value.xquery";

import module namespace 
test="http://www.cancergrid.org/xquery/library/test" 
at "../library/test.xquery";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

lib-rendering:txfrm-webpage(
    "permissible-value tests",
    element function-tests {
        test:expression("permissible-value:contained_in/1 and permissible-value:begin_date/1",
            for $v in
                permissible-value:contained_in(
                    lib-util:mdrElement('value_domain', 'GB-CANCERGRID-6EB677D37-0.1')
                )
            return permissible-value:begin_date($v),

            "
            for $v in
                permissible-value:contained_in(
                    lib-util:mdrElement('value_domain', 'GB-CANCERGRID-6EB677D37-0.1')
                )
            return permissible-value:begin_date($v)
            ",

            (xs:date("2007-01-09Z"), xs:date("2007-01-09Z"), xs:date("2007-01-09Z"))
        )
    }
)
