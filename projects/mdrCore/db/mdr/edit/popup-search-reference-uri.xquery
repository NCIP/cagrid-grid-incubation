xquery version "1.0";
(: ~
: Module Name:             Reference URI search popup
:
: Module Version           1.0
:
: Date                     08 Jul 2008
:
: Copyright                The cagrid consortium
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
declare namespace rs="http://cagrid.org/schema/result-set";
    
import module namespace 
      lib-qs="http://www.cagrid.org/xquery/library/query_service" 
      at "../connector/m-lib-qs.xquery";  
  
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

declare option exist:serialize "media-type=text/html";

session:create(),
let $resource as xs:string := xs:string(request:get-parameter("resource", "EVS-DescLogicConcept"))
let $element as xs:string := xs:string(request:get-parameter("element", ""))
let $form-name as xs:string := xs:string(request:get-parameter("form-name", ""))
let $phrase as xs:string := xs:string(request:get-parameter("phrase", ""))
let $start as xs:int := xs:int(request:get-parameter("start", 0))
let $count as xs:int := xs:int(request:get-parameter("count", 100))

let $concepts := if ($phrase)
                            then lib-qs:query($resource, (), $phrase, $start, $count)//rs:concept
                            else ()

return
   <html>
      <head>
          <title>Searching for a reference uri</title>
          <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/cagrid-style.css" type="text/css"/>
          <link rel="stylesheet" href="../classification/stylesheets/treeview.css" type="text/css"/>
      </head>
      <body>
         <form name='select-item' class='cagridForm' action='popup-search-reference-uri.xquery'>
            <input type='hidden' name='element' value='{$element}'/>
            <table class="layout">
               <tr>
                  <td>Search</td>
                  <td>
                      <input type='text' name='phrase' value='{$phrase}'/>
                      <select name="resource">
                           {lib-qs:selectResource-form('CONCEPT')}
                      </select>
                  </td>
                  <td><input type="submit" value="Submit query" class="cgButton"/></td>
               </tr>
            </table>
         </form>
         
          <table class="layout">
            {
                  for $concept in $concepts
                  let $id := $concept/rs:names/rs:id
                  (:
                  let $id := 
                      if (starts-with($concept/rs:names/rs:id, 'US-NCICB-CACORE-EVS-DESCLOGICCONCEPT'))
                      then concat("urn:lsid:ncicb.nci.nih.gov:nci-thesaurus:", tokenize($concept/rs:names/rs:id, '-')[last()])
                      else $concept/rs:names/rs:id
                  :)
                  let $name := $concept/rs:names/rs:preferred
                  let $definition := $concept/rs:definition

                  return
                  <tr class="light-rule">
                         <td style="vertical-align:top;width:250px;padding:5px;">{$id}</td>
                          <td style="vertical-align:top;width:600px;padding:5px;">{$name}: {$definition}</td>
                          <td style="vertical-align:top;width:100px;padding:5px;">{local:action-button($id, $element, "use this term", $name)}</td>     
                  </tr>
             }
            </table>
      </body>
   </html>
