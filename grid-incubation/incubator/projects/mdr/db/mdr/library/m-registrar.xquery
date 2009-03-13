(:~ Registrar class :)
module namespace registrar="http://www.cancergrid.org/xquery/library/registrar";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
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
  lib-util:mdrElements("registration_authority")//cgMDR:represented_by[string(.//cgMDR:registrar_identifier) eq string($administered_item//cgMDR:registered_by)]
};
