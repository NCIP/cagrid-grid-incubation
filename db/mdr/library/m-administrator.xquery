(:~ Administrator class :)
module namespace administrator="http://www.cancergrid.org/xquery/library/administrator";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";

import module namespace lib-util="http://www.cancergrid.org/xquery/library/util"
  at "../library/m-lib-util.xquery";

declare function administrator:administrating($administered_item as node())
as node()?
{
  lib-util:mdrElements("organization")//cgMDR:Contact[@contact_identifier = $administered_item//cgMDR:administered_by]
};
