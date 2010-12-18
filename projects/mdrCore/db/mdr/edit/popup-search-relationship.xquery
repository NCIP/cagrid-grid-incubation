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
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cagrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";      
   
import module namespace 
  lib-forms="http://www.cagrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
import module namespace 
  lib-rendering="http://www.cagrid.org/xquery/library/rendering" 
  at "../web/m-lib-rendering.xquery";  
  
  
declare function local:action-button($value as xs:string, $control as xs:string, $text as xs:string, $name as xs:string) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"button"},
      attribute name {"update"},
      attribute value {$text},
      attribute onclick {
         concat(
            "{window.opener.document.getElementById('", $control, "').value='",$value,"';",
            "window.opener.document.getElementById('", $control, "-div').innerHTML='",$name,"';","self.close();}")}
   }
};   

declare function local:action-button($value as xs:string, $control as xs:string, $text as xs:string, 
$name as xs:string, $form-name as xs:string?) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"submit"},
      attribute name {"update"},
      attribute value {$text},
      
      if($form-name > '') then (   
          attribute onclick {
             concat(
                "{window.opener.document.getElementById('", $control, "').value='",$value,"';",
                "window.opener.document.getElementById('", $control, "-div').innerHTML='",$name,"';",
                "window.opener.document.", $form-name, ".submit();", "self.close();}")}
      )
      else(
      attribute onclick {
         concat(
            "{window.opener.document.getElementById('", $control, "').value='",$value,"';",
            "window.opener.document.getElementById('", $control, "-div').innerHTML='",$name,"';","self.close();}")}      
      )         
   }
}; 

declare function local:cancel-button($form-name as xs:string, $control as xs:string, $value as xs:string, $text as xs:string) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"submit"},
      attribute name {"update"},
      attribute value {$text},
      if(session:get-attribute("old_cd_id") > '') then(
        attribute onclick {
            concat(
                "{window.opener.document.getElementById('", $control, "').value='",$value,"';",
                "window.opener.document.new_value_domain.submit();", "self.close();}") }      
      )
      else(
        attribute onclick {
         concat(
            "{window.opener.document.getElementById('", $control, "').value='';",
            "window.opener.document.new_value_domain.submit();", "self.close();}") }
      )
   }
};
declare function local:popup-close($form-name as xs:string?, $control as xs:string, $value as xs:string) 
{
      if( session:get-attribute($control) > '' ) then(
        attribute onunload {
            concat(
                "{window.opener.document.getElementById('", $control, "').value='", $value, "';",
                "window.opener.document.", $form-name, ".submit();}") }      
      )
      else(
        attribute onunload {
         concat(
            "{window.opener.document.getElementById('", $control, "').value='';",
            "window.opener.document.", $form-name, ".submit();}") }
      )
      
};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $phrase as xs:string := xs:string(request:get-parameter("phrase", ""))
let $type as xs:string := xs:string(request:get-parameter("type", ""))
let $control as xs:string := xs:string(request:get-parameter("control", ""))
let $start as xs:integer := xs:integer(request:get-parameter("start", "1"))
let $count as xs:integer := xs:integer(request:get-parameter("count", "10"))
let $form-name as xs:string := xs:string(request:get-parameter("form-name", ""))

return
   <html>
      <head>
         <title>Searching for a {$type}</title>
                <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/cagrid-style.css" type="text/css"/>
                <link rel="stylesheet" href="../classification/stylesheets/treeview.css" type="text/css"/>
                <link rel="search" type="application/opensearchdescription+xml" title="OpenMDR Data Element Search" href="../web/cde_search.xquery"/>

      </head>
      <body>  <div id="central">   
         {if($form-name > "") then
              (session:set-attribute("current_form", $form-name) )
           else(),
           local:popup-close(session:get-attribute("current_form"), $control, "Cancel")}
         <form name='select-item' class='cagridForm' action='popup-search-relationship.xquery'>
            <input type='hidden' name='type' value='{$type}'/>
            <input type='hidden' name='control' value='{$control}'/>
            <div id='processImg' align="center">
                <img src="../web/images/process-loader.gif"  onload="document.getElementById('processImg').style.display = 'none';" />
            </div>
            <table class="layout">
               <tr>
                  <td>Search Phrase</td>
                  <td><input type='text' name='phrase' value='{$phrase}'/></td>
                  <td><input type="submit" value="Submit query" class="cgButton"
                       onclick="{concat("document.getElementById('", "processImg" , "').style.display = ''; window.opener.document.", session:get-attribute("current_form"), ".submit();")}"/></td>
               </tr>
               {
               let $documents := lib-util:search($type, $phrase)

               let $count-docs := count($documents)
               return
               (
                  for $document at $pos in $documents
                  let $id := lib-util:mdrElementId($document)
                  let $name := administered-item:preferred-name($document)
                  where $pos >= $start and $pos < $start + $count 
                  return 
                  if($pos mod 2 = 0) then(
                        element tr {
                        attribute class {"odd_row"},
                        element td {$id},
                        element td {$name},
                        element td {
                        local:action-button($id, $control, "use this document",$name, session:get-attribute("current_form"))
                        } 
                     })else(
                        element tr {
                        attribute class {"even_row"},
                        element td {$id},
                        element td {$name},
                        element td {
                        local:action-button($id, $control, "use this document",$name, session:get-attribute("current_form"))
                        } 
                     }
                     ),                     
                  <tr>
                     <td>
                        {
                        if ($start > 1)
                        then
                           <a href='{
                           concat('popup-search-relationship.xquery?start=1&amp;control=',
                              $control,
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
                                 "&amp;control=",
                                 $control,
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
                                    "&amp;control=",
                                    $control,
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
         </form> </div>
      </body>
   </html>
