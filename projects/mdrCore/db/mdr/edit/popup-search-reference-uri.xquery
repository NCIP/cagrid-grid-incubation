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
declare namespace rs="http://cagrid.org/schema/result-set";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace op="http://www.w3.org/2002/11/xquery-operators";

declare namespace c="http://cagrid.org/schema/config";
declare namespace q="http://cagrid.org/schema/query";

import module namespace 
      lib-qs="http://www.cagrid.org/xquery/library/query_service" 
      at "../connector/m-lib-qs.xquery";  
    
import module namespace
    lib-uri-resolution="http://www.cagrid.org/xquery/library/resolver"
    at "../resolver/m-lib-uri-resolution.xquery";    
    
declare function local:action-button($value as xs:string, $control as xs:string, $text as xs:string, $name as xs:string) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"button"},
      attribute name {"update"},
      attribute value {$text},
      attribute onclick {
      concat("{self.close();", "window.opener.document.getElementById('",$control,"').value='",$value,"';",
            "window.opener.document.getElementById('", $control, "-div').innerHTML='",$name,"';",
            "}")}
      }
};  

declare option exist:serialize "media-type=text/html";
session:create(),
let $resource as xs:string := xs:string(request:get-parameter("resource", "BioPortal-Ocre"))
let $element as xs:string := xs:string(request:get-parameter("element", ""))
let $form-name as xs:string := xs:string(request:get-parameter("form-name", ""))
let $phrase as xs:string := xs:string(request:get-parameter("phrase", ""))
let $control as xs:string := xs:string(request:get-parameter("control", ""))
let $start as xs:int := xs:int(request:get-parameter("start", 0))
let $count as xs:int := xs:int(request:get-parameter("count", 20))
let $request as xs:string := xs:string(request:get-parameter("request",""))

let $query :=
        if ($request != "") 
            then (lib-qs:query($request,$resource))
        else  
            if ($phrase != "") 
                then (lib-qs:query($resource, (), $phrase, $start, $count))
        else ()

let $concepts := $query//rs:concept
let $recordCounter := $query//rs:recordCounter
                    
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
        <div id="central">      
         <form name='select-item' class='cagridForm' action='popup-search-reference-uri.xquery'>
            <input type='hidden' name='element' value='{$element}'/>
             <input type='hidden' name='control' value='{$control}'/>
            <table class="layout">
               <tr>
                  <td>Search</td>
                  <td>
                      <input type='text' name='phrase' value='{$phrase}'/>
                      <select name="resource">
                           {lib-qs:selectResource-form('CONCEPT')}
                      </select>
                  </td>
                  
                  <div id='processImg' align="center">
                  <img src="../web/images/process-loader.gif" onload="document.getElementById('processImg').style.display = 'none';" />
                  </div>
                  
                  <td><input type="submit" value="Submit query" class="cgButton" onclick="document.getElementById('processImg').style.display = '';"/></td>
                  <td><input type="submit" value="Stop query" class="cgButton" onclick="this.form.reset();"/></td>
                  
               </tr>       
            </table>
            <ul>
                <li>
                    <font size="2" color="red" face="Verdana">LexEVS REST interface supports case sensitive search only at this time!
                    </font>
                </li>
            </ul>
        
            
            <table class="layout">
            {                                   
                  for $concept at $pos in $concepts
                  let $id := $concept/rs:names/rs:id
                  let $name := $concept/rs:names/rs:preferred
                  let $definition := $concept/rs:definition
                  

                  return
                  if(($pos mod 2) = 0)
                  then(
                    <tr class="odd_row">
                      <td>{lib-uri-resolution:html-anchor($id)}</td>
                      <td>{$name}: {$definition}</td>
                      <td>{local:action-button($id, $element, "use this term", $name)}</td>     
                    </tr>
                    
                  )
                  else (
                    <tr class="even_row">
                      <td>{lib-uri-resolution:html-anchor($id)}</td>
                      <td>{$name}: {$definition}</td>
                      <td>{local:action-button($id, $element, "use this term", $name)}</td>     
                    </tr>
                  )
                  
            }             
            </table>   
            <table class="layout">
            <tr class="light-rule" align="right">
            <td>
            {          
                    let $div := xs:integer(xs:integer($recordCounter) div xs:integer($count))                    
                    let $mod := xs:integer($recordCounter mod $count)
                    
                    let $div:= 
                        if (exists($div))
                        then
                            if($mod !=0)
                            then
                                let $div := xs:integer(xs:integer($div) + xs:integer(1))
                            return $div
                            else
                                let $div:=$div
                            return $div
                        else()                
                            let $query := lib-qs:getURL($resource, (), $phrase, $start, $count)
                           
                            let $increment := xs:integer(20)
                            let $div:=xs:integer($div)-xs:integer(1)
                            for $i in (0 to $div)  
                         return
                        (
                               if($i gt 0)
                               then
                               (
                                   <a href='popup-search-reference-uri.xquery?control={$control}&amp;element={$element}&amp;phrase={$phrase}&amp;request={encode-for-uri(replace($query,'startIndex=0',concat('startIndex=',$i*$increment)))}&amp;resource={$resource}'>{$i+1}</a>
                                )
                               else(
                                   <a href='popup-search-reference-uri.xquery?control={$control}&amp;phrase={$phrase}&amp;request={encode-for-uri($query)}&amp;resource={$resource}'>{$i+1}</a>                           
                               )
                        )                       
                     
            }
            </td>
              </tr>
             </table>
            </form>
            </div>
      </body>
   </html>