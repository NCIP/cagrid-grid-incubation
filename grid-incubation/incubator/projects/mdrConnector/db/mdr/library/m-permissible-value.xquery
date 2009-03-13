(:~ Permissible value class :)
module namespace permissible-value="http://www.cancergrid.org/xquery/library/permissible-value";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";

(:~
  Constructor
  @param $value_domain Value domain
  
  cgMDR:contained_in added to prevent false iterations
:)
declare function permissible-value:contained_in($value_domain as element())
as node()*
{
   $value_domain//cgMDR:containing[cgMDR:contained_in]
};

(:~
  Begin date
  @param $permissible_value Permissible value
:)
declare function permissible-value:begin_date($permissible_value as element())
as xs:date?
{
  for $date in $permissible_value/cgMDR:permissible_value_begin_date
  return xs:date($date)
};
