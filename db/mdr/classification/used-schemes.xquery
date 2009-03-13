declare namespace transform = "http://exist-db.org/xquery/transform";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";
declare namespace dcterms="http://purl.org/dc/terms/";
declare namespace dc="http://purl.org/dc/elements/1.1/";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";    
      
   
declare option exist:serialize "indent=true 
   media-type=text/html 
   method=xhtml 
   doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN 
   doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),
let $title as xs:string :='Classification Schemes, Terminologies and Ontologies in Use'
return lib-rendering:txfrm-webpage(
   $title, 
         <schemes-in-use>
         {
         for $scheme in lib-util:mdrElements('classification_scheme')[.//cgMDR:administered_item_administration_record]
         for $graph in lib-util:mdrElements('classification_scheme')[.//@rdf:about]
         let $scheme-id := lib-util:mdrElementId($scheme)
         where $graph/skos:ConceptScheme[.//@rdf:resource=$scheme-id]
         
         
         return ($scheme, $graph, <uri>{concat(lib-util:getServer(),'/exist/rest/',lib-util:getCollectionPath('classification_scheme'),'/', util:document-name($scheme))}</uri>)
         }
         </schemes-in-use>)
         