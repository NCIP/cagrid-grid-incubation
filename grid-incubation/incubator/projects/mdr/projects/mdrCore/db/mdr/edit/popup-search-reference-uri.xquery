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
declare namespace util="http://exist-db.org/xquery/util";
    
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
let $count as xs:int := xs:int(request:get-parameter("count", 50))
let $request as xs:string := xs:string(request:get-parameter("request",""))


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
            <ul>
                <li>
                    <font size="2" color="red" face="Verdana">LexEVS REST interface supports case sensitive search only at this time!
                    </font>
                </li>
            </ul>
         
          <table class="layout">
            {
                let $concepts :=
                    if ($request != "") 
                        then (lib-qs:query($request,$resource)//rs:concept)
                    else  
                        if ($phrase != "") 
                            then (lib-qs:query($resource, (), $phrase, $start, $count)//rs:concept)
                    else ()
                  for $concept in $concepts
                  let $log:=util:log-system-out($concept)
                  let $id := $concept/rs:names/rs:id
                  let $name := $concept/rs:names/rs:preferred
                  let $definition := $concept/rs:definition
                  return
                  <tr class="light-rule">
                         <td style="vertical-align:top;width:250px;padding:5px;">{lib-uri-resolution:html-anchor($id)}</td>
                          <td style="vertical-align:top;width:600px;padding:5px;">{$name}: {$definition}</td>
                          <td style="vertical-align:top;width:100px;padding:5px;">{local:action-button($id, $element, "use this term", $name)}</td>     
                  </tr>
            }             
            </table>    

            <table class="layout">
            <tr class="light-rule">
            
            {
                (:
                if (($phrase != "") and ($resource != ""))
                then
                (
                :)
                let $pages :=
                  if ($request != "") 
                        then (lib-qs:query($request,$resource)//rs:pages)
                    else  
                        if ($phrase != "") 
                            then (lib-qs:query($resource, (), $phrase, $start, $count)//rs:pages)
                    else ()
                    
(:                    let $pages := lib-qs:query($resource, (), $phrase, $start, $count)//rs:pages:)
                    for $page in $pages/rs:page
                        let $pagelink:= encode-for-uri($page/rs:pageurl)
                        let $log:= util:log-system-err($pagelink)
                        let $pagenum:= $page/rs:pagenum
                    return
                       <td style="vertical-align:top;width:250px;padding:5px;">
                       <a href='popup-search-reference-uri.xquery?request={$pagelink}&amp;resource={$resource}'>{$pagenum}</a>
                       </td>
                       (:
                )
                else ()  
                :)
            }

            {
                (:
                if (($phrase != "") and ($resource != ""))
                then
                (
                :)
                  let $prevlink :=
                  if ($request != "") 
                        then (lib-qs:query($request,$resource)//rs:previous)
                    else  
                        if ($phrase != "") 
                            then (lib-qs:query($resource, (), $phrase, $start, $count)//rs:previous)
                    else ()
                    
                return
                    <td style="vertical-align:top;width:250px;padding:5px;">
                    <a href='popup-search-reference-uri.xquery?request={encode-for-uri($prevlink/rs:prevurl)}&amp;resource={$resource}'>{$prevlink/rs:prevname}</a>
                    </td>
                    (:
                )
                else ()
                :)
             }
             
            {
                (:if (($phrase != "") and ($resource != ""))
                then
                (
                :)
                  let $nextlink :=
                  if ($request != "") 
                        then (lib-qs:query($request,$resource)//rs:next)
                    else  
                        if ($phrase != "") 
                            then (lib-qs:query($resource, (), $phrase, $start, $count)//rs:next)
                    else ()
                    
(:                let $nextlink := lib-qs:query($resource, (), $phrase, $start, $count)//rs:next:)
                return
                    <td style="vertical-align:top;width:250px;padding:5px;">
                    <a href='popup-search-reference-uri.xquery?request={encode-for-uri($nextlink/rs:nexturl)}&amp;resource={$resource}'>{$nextlink/rs:nextname}</a>
                    </td>
                (:
                )
                else ()
                :)
             }
             </tr>
             </table>
            </form>
      </body>
   </html>
   
(: works
<table class="layout">
            {
                let $concepts :=
                    if ($request != "") 
                        then (lib-qs:query($request,$resource)//rs:concept)
                    else  
                        if ($phrase != "") 
                            then (lib-qs:query($resource, (), $phrase, $start, $count)//rs:concept)
                    else ()
                  for $concept in $concepts
                  let $log:=util:log-system-out($concept)
                  let $id := $concept/rs:names/rs:id
                  let $name := $concept/rs:names/rs:preferred
                  let $definition := $concept/rs:definition
                  return
                  <tr class="light-rule">
                         <td style="vertical-align:top;width:250px;padding:5px;">{lib-uri-resolution:html-anchor($id)}</td>
                          <td style="vertical-align:top;width:600px;padding:5px;">{$name}: {$definition}</td>
                          <td style="vertical-align:top;width:100px;padding:5px;">{local:action-button($id, $element, "use this term", $name)}</td>     
                  </tr>
            }             
            </table>    

            <table class="layout">
            <tr class="light-rule">
            
            {
                if (($phrase != "") and ($resource != ""))
                then
                (
                    let $pages := lib-qs:query($resource, (), $phrase, $start, $count)//rs:pages
                    for $page in $pages/rs:page
                        let $pagelink:= encode-for-uri($page/rs:pageurl)
                        let $log:= util:log-system-err($pagelink)
                        let $pagenum:= $page/rs:pagenum
                    return
                       <td style="vertical-align:top;width:250px;padding:5px;">
                       <a href='popup-search-reference-uri.xquery?request={$pagelink}&amp;resource={$resource}'>{$pagenum}</a>
                       </td>
                )
                else ()                              
            }

            {
                if (($phrase != "") and ($resource != ""))
                then
                (
                let $nextlink := lib-qs:query($resource, (), $phrase, $start, $count)//rs:next
                return
                    <td style="vertical-align:top;width:250px;padding:5px;">
                    <a href='popup-search-reference-uri.xquery?request={encode-for-uri($nextlink/rs:nexturl)}&amp;resource={$resource}'>{$nextlink/rs:nextname}</a>
                    </td>
                )
                else ()    
             }
             </tr>
             </table>
:)


            (:        
            {
                let $pages := if ($phrase)
                    then lib-qs:query($resource, (), $phrase, $start, $count)//rs:pages
                else ()            
                for $page in $pages/rs:page
                    let $pagelink:= $page/rs:pageurl
                    let $pagenum:= $page/rs:pagenum
                return
                    <td style="vertical-align:top;width:250px;padding:5px;">
                    <a href='{$pagelink}'>{$pagenum}</a>
                    </td>    
            }
              {
                    let $nextlink := if ($phrase)
                        then lib-qs:query($resource, (), $phrase, $start, $count)//rs:next
                        else ()
                    return
                        <td style="vertical-align:top;width:250px;padding:5px;"><a href='{$nextlink/rs:nexturl}'>{$nextlink/rs:nextname}</a></td>
             }
            :)