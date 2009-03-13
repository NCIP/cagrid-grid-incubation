xquery version "1.0";

(: ~
: Module Name:             Relationship search popup
:
: Module Version           1.0
:
: Date                     15 jan 2008
:
: Copyright                The cancergrid consortium
:
: Module overview          Finds a document to place on the end of a relationship
:
:)

(:~
:    @author Steve Harris
:    @version 1.0
~ :)


declare namespace request="http://exist-db.org/xquery/request";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace session="http://exist-db.org/xquery/session";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";      
   
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
   
session:create(),
let $type as xs:string := request:get-parameter("type", "")
let $element as xs:string := request:get-parameter("element", "")
let $phrase as xs:string := request:get-parameter("phrase", "")
let $start as xs:integer := request:get-parameter("start", "1")
let $count as xs:integer := request:get-parameter("count", "10")

return
   <html>
      <head>
          <title>Searching for a {lib-util:mdrElementTypeFriendly($type)}</title>
          <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
          <link rel="stylesheet" href="../web/stylesheets/cancergrid-style.css" type="text/css"/>
          <link rel="stylesheet" href="../classification/stylesheets/treeview.css" type="text/css"/>
          <link rel="search" type="application/opensearchdescription+xml" title="CancerGrid Data Element Search" href="../web/cde_search.xquery"/>
          
         <script language="javascript" src="../xforms/popup.js"/>

      </head>
      <body>
         
         <form name='select-item' class='cancergridForm' action='popup-search-relationship.xquery'
         >
            <input type='hidden' name='type' value='{$type}'/>
            <input type='hidden' name='element' value='{$element}'/>
            <input type='hidden' name='start' value='{$start}'/>
            <input type='hidden' name='count' value='{count}'/>
            
            <table class="layout">
               <tr>
                  <td>Search Phrase</td>
                  <td><input type='text' name='phrase' value='{$phrase}'/></td>
                  <td><input type="submit" value="Submit query" class="cgButton"/></td>
               </tr>
               {
               let $documents := lib-util:mdrElements($type)
                        [.//cgMDR:registration_status ne 'Superseded']
                        [./*&=$phrase]

               let $count-docs := count($documents)
               return
               (
                  for $document at $pos in $documents
                  let $id := lib-util:mdrElementId($document)
                  let $name := administered-item:preferred-name($document)
                  where $pos >= $start and $pos < $start + $count 
                  return element tr {
                        element td {$id},
                        element td {$name},
                        element td {
                        local:action-button($id, $element, "use this document", $name)
                        }
                     },
                  <tr>
                     <td>
                        {
                        if ($start > 1)
                        then
                           <a href='{
                           concat('popup-search-relationship.xquery?start=1&amp;element=',
                              $element,
                              '&amp;phrase=',
                              $phrase,
                              '&amp;type=',
                              $type) 
                           }'>start</a>
                        else()
                        }
                     </td>
                     <td>{
                     if ($start > 1)
                     then (
                       element a {
                           attribute href {
                              concat("popup-search-relationship.xquery?start=",
                                 $start - $count,
                                 "&amp;element=",
                                 $element,
                                 "&amp;phrase=",
                                 $phrase,
                                 "&amp;type=",
                                 $type) 
                                 },
                           'previous'
                           }
                        )
                     else()
                     }
                     </td>
                     <td>
                     {
                        if ($count-docs > $start + $ count)
                        then
                           element a {
                              attribute href {
                                 concat("popup-search-relationship.xquery?start=",
                                    $start+$count,
                                    "&amp;element=",
                                    $element,
                                    "&amp;phrase=",
                                    $phrase,
                                    "&amp;type=",
                                    $type) 
                                    },
                              'next'}
                        else ()
                        }
                     </td>
                  </tr>
                  )
                  }
            </table>
         </form>
      </body>
   </html>
