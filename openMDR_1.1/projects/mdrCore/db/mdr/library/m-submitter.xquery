(:~ Submitter class :)
module namespace submitter="http://www.cagrid.org/xquery/library/submitter";

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";

import module namespace lib-util="http://www.cagrid.org/xquery/library/util"
  at "../library/m-lib-util.xquery";

(:~
  Constructor

:)
declare function submitter:submitting($administered_item as node()) as node()?
{
  lib-util:mdrElements("organization")//openMDR:Contact[@contact_identifier = $administered_item//openMDR:submitted_by]
};
