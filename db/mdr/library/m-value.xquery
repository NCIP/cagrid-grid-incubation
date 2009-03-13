(:~ ISO 11179 value class :)
module namespace ISO_value="http://www.cancergrid.org/xquery/library/value";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";

(:~
  Constructor
  @param $permissible_value Permissible value
:)
declare function ISO_value:used_in($permissible_value as node())
as node()?
{$permissible_value//cgMDR:value_item};

(:~
  Item
  @param $value Value
:)
declare function ISO_value:item($value as node())
as xs:string
{string($value)};
