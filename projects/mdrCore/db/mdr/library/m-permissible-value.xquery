(:~ Permissible value class :)
module namespace permissible-value="http://www.cagrid.org/xquery/library/permissible-value";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";

(:~
  Constructor
  @param $value_domain Value domain
  
  openMDR:contained_in added to prevent false iterations
:)
declare function permissible-value:contained_in($value_domain as element())
as node()*
{
   $value_domain//openMDR:containing[openMDR:contained_in]
};

(:~
  Begin date
  @param $permissible_value Permissible value
:)
declare function permissible-value:begin_date($permissible_value as element())
as xs:date?
{
  for $date in $permissible_value/openMDR:permissible_value_begin_date
  return xs:date($date)
};
