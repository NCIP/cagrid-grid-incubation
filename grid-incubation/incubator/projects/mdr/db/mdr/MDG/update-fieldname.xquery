xquery version "1.0";

(: ~
 : Module Name:             Update Field Name
 :
 : Module Version           1.0
 :
 : Date                     21st October 2008
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          Creates fieldnames for elements that don't have them....
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";

import module 
   namespace lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
  
import module namespace 
  lib-util="http://www.cagrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";
  
import module namespace 
administered-item="http://www.cagrid.org/xquery/library/administered-item" 
at "../library/m-administered-item.xquery";   
  
element update {
    for $element in lib-util:mdrElements('data_element')
    let $name := administered-item:preferred-name($element)
    where empty($element//openMDR:field_name/text())
    order by $element//@data_identifier
    return update replace $element//openMDR:field_name with
        element openMDR:field_name {
            attribute preferred {'true'},
            normalize-space(lower-case(replace($name,'[ \-().,'']','_')))
            }
}
