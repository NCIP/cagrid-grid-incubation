xquery version "1.0";
(: ~
: Module Name:             Reference URI search popup
:
: Module Version           1.0
:
: Date                     08 Jul 2008
:
: Copyright                The cancergrid consortium
:
: Module overview          Finds a reference uri for an administered item
:
:)

(:~
:    @author Andrew Tsui
:    @version 1.0
~ :)
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace rs="http://cancergrid.org/schema/result-set";
    
import module namespace 
      lib-qs="http://www.cancergrid.org/xquery/library/query_service" 
      at "../connector/m-lib-qs.xqm";  
  
declare function local:action-button($value as xs:string, $control as xs:string, $text as xs:string, $name as xs:string) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"submit"},
      attribute name {"update"},
      attribute value {$text},
      attribute onclick {
      concat("window.opener.document.getElementById('",$control,"').value='",$value,"';",
            "window.opener.document.getElementById('", $control, "-div').innerHTML='",$name,"';",
            "self.close();")}
      }
   
}; 

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $resource as xs:string := xs:string(request:get-parameter("resource", "EVS-DescLogicConcept"))
let $element as xs:string := xs:string(request:get-parameter("element", ""))
let $form-name as xs:string := xs:string(request:get-parameter("form-name", ""))
let $phrase as xs:string := xs:string(request:get-parameter("phrase", ""))
let $start as xs:int := xs:int(request:get-parameter("start", 0))
let $count as xs:int := xs:int(request:get-parameter("count", 10))

let $concepts := if ($phrase)
                            then lib-qs:query($resource, (), $phrase, $start, $count)//rs:concept
                            else ()

return
   <html>
      <head>
          <title>Searching for a reference uri</title>
          <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/cancergrid-style.css" type="text/css"/>
          <link rel="stylesheet" href="../classification/stylesheets/treeview.css" type="text/css"/>
          <link rel="search" type="application/opensearchdescription+xml" title="CancerGrid Data Element Search" href="../web/cde_search.xquery"/>
          
          <script type="text/javascript" src="popup.js"/>

      </head>
      <body>
         <form name='select-item' class='cancergridForm' action='popup-search-reference-uri.xquery'>
            <input type='hidden' name='element' value='{$element}'/>
            <table class="layout">
               <tr>
                  <td>Search Phrase</td>
                  <td>
                      <input type='text' name='phrase' value='{$phrase}'/>
                      <select name="resource">
                          <option value="cgMDR-Local-Terminology" selected="selected">Local Terminology</option>
                          <option value="EVS-DescLogicConcept">EVS</option>
                      </select>
                  </td>
                  <td><input type="submit" value="Submit query" class="cgButton"/></td>
               </tr>
            </table>
            <table class="layout" style="margin: 5px;">
               {
                  for $concept in $concepts
                  let $id := 
                      if (starts-with($concept/rs:names/rs:id, 'US-NCICB-CACORE-EVS-DESCLOGICCONCEPT'))
                      then concat("urn:lsid:ncicb.nci.nih.gov:nci-thesaurus:", tokenize($concept/rs:names/rs:id, '-')[last()])
                      else $concept/rs:names/rs:id
                  let $name := $concept/rs:names/rs:preferred
                  return
                      <tr>
                          <td style="vertical-align:top;width:350px;padding:5px;">{$id}</td>
                          <td style="vertical-align:top;width:400px;padding:5px;">{$name}</td>
                          <td style="vertical-align:top;">{local:action-button($id, $element, "use this term", $name)}</td>
                     </tr>
               }
            </table>
         </form>
      </body>
   </html>
