(:~ ISO 11179 value class :)
module namespace ISO_value="http://www.cagrid.org/xquery/library/value";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";

(:~
  Constructor
  @param $permissible_value Permissible value
:)
declare function ISO_value:used_in($permissible_value as node())
as node()?
{$permissible_value//openMDR:value_item};

(:~
  Item
  @param $value Value
:)
declare function ISO_value:item($value as node())
as xs:string
{string($value)};
