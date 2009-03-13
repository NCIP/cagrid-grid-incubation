declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace session="http://exist-db.org/xquery/session";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery"; 

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
   at "../web/m-lib-rendering.xquery";
   

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),

let $content as element(div) :=
       <div xmlns="http://www.w3.org/1999/xhtml"> 
           {
           for $item in lib-util:mdrElements()
           for $item2 in lib-util:mdrElements()
               let $uri := document-uri($item)
               let $id := lib-util:mdrElementId($item)
               where ($item//@data_identifier = $item2//@data_identifier) 
                   and (util:collection-name($item) != util:collection-name($item2))
               return <li><a href="{$uri}">{$uri}</a></li>
           }
       </div>
return
    lib-rendering:txfrm-webpage("Documents sharing a data identifier" , $content)