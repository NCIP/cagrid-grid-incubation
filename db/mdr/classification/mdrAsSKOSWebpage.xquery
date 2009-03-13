declare namespace transform = "http://exist-db.org/xquery/transform";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";   
   
import module namespace 
   lib-render-skos="http://www.cancergrid.org/xquery/library/rendering/skos" 
   at "../classification/m-lib-render-skos.xquery";  

import module namespace 
   lib-reasoning-skos="http://www.cancergrid.org/xquery/library/reasoning/skos" 
   at "../classification/m-lib-reasoning-skos.xquery";
   
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";  

import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";   

import module namespace
   value-domain="http://www.cancergrid.org/xquery/library/value-domain"
   at "../library/m-value-domain.xquery";
   
import module namespace 
lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
at "../web/m-lib-rendering.xquery";
   
declare option exist:serialize "indent=true media-type=text/html method=html doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
   
let $rdf := document('/db/mdr/classification/mdrAsSKOS.xquery')
return lib-rendering:txfrm-webpage('skos',lib-render-skos:treeview($rdf))   
