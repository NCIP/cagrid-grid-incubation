xquery version "1.0";
(: ~
: Module Name:             Classifier search popup
:
: Module Version           1.0
:
: Date                     15 jan 2008
:
: Copyright                The cancergrid consortium
:
: Module overview          Finds a classifier for an administered item
:
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)
declare namespace request="http://exist-db.org/xquery/request";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
declare namespace skos="http://www.w3.org/2004/02/skos/core#";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
   
import module namespace 
  lib-forms="http://www.cancergrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
import module namespace 
  lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
  at "../web/m-lib-rendering.xquery";  
  
declare function local:action-button($value as xs:string, $control as xs:string, $text as xs:string, $name as xs:string) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"submit"},
      attribute name {"update"},
      attribute value {$text},
      attribute onclick {concat("updateOpener('",$control,"','",$value,"');")}
   }
}; 

declare function local:results-link($url as xs:string, $element as xs:string, $phrase as xs:string, $start as xs:integer, $count as xs:integer, $count-docs as xs:integer) as element()
{
<table class="layout">
   <tr>
      <td>
         {
         if ($start > 1)
         then <a href='{concat($url,"?start=1&amp;element=",$element,"&amp;phrase=",$phrase)}'>start</a>
         else('start')
         }
      </td>
      <td>
         {
         if ($start > 1)
         then <a href='{concat($url,"?start=",$start - $count,"&amp;element=",$element,"&amp;phrase=",$phrase)}'>previous</a>
         else('previous')
         }
      </td>
      <td>
         {
         if ($count-docs > $start + $ count)
         then <a href='{concat($url,"?start=",$start+$count,"&amp;element=",$element,"&amp;phrase=",$phrase)}'>next</a>
         else ('next')
         }
      </td>
   </tr>
</table>
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),
let $element as xs:string := xs:string(request:get-parameter("element", ""))
let $phrase as xs:string := xs:string(request:get-parameter("phrase", ""))
let $start as xs:integer := xs:integer(request:get-parameter("start", "1"))
let $count as xs:integer := xs:integer(request:get-parameter("count", "10"))

let $documents := lib-util:mdrElements('classification_scheme')//rdf:Description
         [./*&=$phrase]

let $count-docs := count($documents)

return
   <html>
      <head>
          <title>Searching for a classifier</title>
          <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/cancergrid-style.css" type="text/css"/>
          <link rel="stylesheet" href="../classification/stylesheets/treeview.css" type="text/css"/>
          <link rel="search" type="application/opensearchdescription+xml" title="CancerGrid Data Element Search" href="../web/cde_search.xquery"/>
          
          <script type="text/javascript" src="popup.js"/>

      </head>
      <body>
         <form name='select-item' class='cancergridForm' action='popup-search-classifier.xquery'>
            <input type='hidden' name='element' value='{$element}'/>
            <table class="layout">
               <tr>
                  <td>Search Phrase</td>
                  <td><input type='text' name='phrase' value='{$phrase}'/></td>
                  <td><input type="submit" value="Submit query" class="cgButton"/></td>
               </tr>
            </table>
            <table class="layout">
               {
                  for $document at $pos in $documents
                  let $id := string($document/@rdf:about)
                  let $name := $document/skos:prefLabel
                  where $pos >= $start and $pos < $start + $count 
                  return element tr {
                        element td {$id},
                        element td {$name},
                        element td {
                        local:action-button($id, $element, "use this term",$name)
                        }
                     }
               }
            </table>
            {local:results-link("popup-search-classifier.xquery", $element, $phrase, $start, $count, $count-docs)}
         </form>
      </body>
   </html>

