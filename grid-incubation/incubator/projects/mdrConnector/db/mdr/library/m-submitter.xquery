(:~ Submitter class :)
module namespace submitter="http://www.cancergrid.org/xquery/library/submitter";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";

import module namespace lib-util="http://www.cancergrid.org/xquery/library/util"
  at "../library/m-lib-util.xquery";

(:~
  Constructor

:)
declare function submitter:submitting($administered_item as node()) as node()?
{
  lib-util:mdrElements("organization")//cgMDR:Contact[@contact_identifier = $administered_item//cgMDR:submitted_by]
};
