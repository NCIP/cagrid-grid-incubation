(:~ Registrar class :)
module namespace registrar="http://www.cagrid.org/xquery/library/registrar";

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";

(:~
  Constructor

  Difference from ISO11179. In ISO11179 this function would be:
  registration_authority:registering($administered_item)

  @param $administered_item Administered item
  
  30/10/2006: fixed bug in function that only searched first registrar.  The data is not the model!
:)
declare function registrar:registering($administered_item as node())
as node()
{
  lib-util:mdrElements("registration_authority")//openMDR:represented_by[string(.//openMDR:registrar_identifier) eq string($administered_item//openMDR:registered_by)]
};
