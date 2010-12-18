(:~ Administrator class :)
module namespace administrator="http://www.cagrid.org/xquery/library/administrator";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";

import module namespace lib-util="http://www.cagrid.org/xquery/library/util"
  at "../library/m-lib-util.xquery";

declare function administrator:administrating($administered_item as node())
as node()?
{
  lib-util:mdrElements("organization")//openMDR:Contact[@contact_identifier = $administered_item//openMDR:administered_by]
};
