declare namespace xforms="http://www.w3.org/2002/xforms";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery"; 

<xforms:select1
   ref="//cgMDR:value_domain_datatype">
   <xforms:label>Unit of Measure</xforms:label>
   <xforms:item>
      <xforms:label>Select... </xforms:label>
      <xforms:value> </xforms:value>
   </xforms:item>
   {
   for $item in collection('/db/mdr/data/unit_of_measure')/*
         let $label := concat($item/cgMDR:unit_of_measure_name, ' (', $item/cgMDR:unit_of_measure_precision, ')')
         let $value := data($item/@unit_of_measure_identifier)
         return
               <xforms:item>
                  <xforms:label>{$label}</xforms:label>
                  <xforms:value>{$value}</xforms:value>
               </xforms:item>
         }
</xforms:select1>
