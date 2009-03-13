(:~ Value domain class :)
module namespace value-domain="http://www.cancergrid.org/xquery/library/value-domain";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";

(:~
  Constructor
  @param $data_element Data element
:)
declare function value-domain:representing($data_element as node())
as element()?
{
  lib-util:mdrElement("value_domain",
    xs:string($data_element//cgMDR:representing))
};

(:~
  Type
  @param $value_domain Value domain
  @return Notation for enumerated or non-enumerated domain type
:)
declare function value-domain:type($value_domain as element()?) as xs:string?
{
   replace(lower-case(local-name($value_domain)), '_', ' ')
};

declare function value-domain:unit_of_measure($value_domain as element()) as xs:string*
{
   let $uom := xs:string($value_domain//cgMDR:value_domain_unit_of_measure)
   return
      xs:string(lib-util:mdrElement('unit_of_measure', $uom)//cgMDR:unit_of_measure_name/text())
};

declare function value-domain:datatype($value_domain as element()) as xs:string?
{
   let $dt := xs:string($value_domain//cgMDR:value_domain_datatype)
   return
      xs:string(lib-util:mdrElement('data_type', $dt)//cgMDR:datatype_name)
};