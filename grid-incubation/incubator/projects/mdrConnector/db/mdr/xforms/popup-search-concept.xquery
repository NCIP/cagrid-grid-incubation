xquery version "1.0";
(: ~
: Module Name:             Concept search popup
:
: Module Version           1.0
:
: Date                     31 jan 2008
:
: Copyright                The cancergrid consortium
:
: Module overview          Finds a concept for an object class, property or value meaning
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
declare namespace lg-cncpt="http://concepts.LexGrid.org/xsd";
declare namespace lg-common="http://commonTypes.LexGrid.org/xsd";
declare namespace lb-coll="http://Collections.DataModel.LexBIG.LexGrid.org/xsd";
declare namespace lb-core="http://Core.DataModel.LexBIG.LexGrid.org/xsd";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";
  
import module namespace 
  lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
  at "../web/m-lib-rendering.xquery";  

import module namespace
    lb-srvc="http://www.cancergrid.org/xquery/library/LexBIG/LexBIGServiceImpl"
    at "../edit/lb-srvc.xquery";

import module namespace
    lb-comb="http://www.cancergrid.org/xquery/library/LexBIG/CombinatorialQuery"
    at "../edit/lb-comb.xquery";

declare function local:acknowledgement() as element()*
{
        <p>LexBIG is a product of Mayo Foundation for Medical Education and Research
        (MFMER)

        <a href="http://informatics.mayo.edu/LexGrid/index.php?page=lexbig">
            http://informatics.mayo.edu/LexGrid/index.php?page=lexbig
        </a>,

        Copyright (C) 2004-2006 MFMER
        
        </p>
};

declare function local:action-button($value as xs:string?, $control as xs:string?, $text as xs:string?, $name as xs:string?) as node()
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
      <td>{
         if ($start > 1)
         then <a href='{concat($url,"?start=1&amp;element=",$element,"&amp;phrase=",$phrase)}'>start</a>
         else('start')
         }
      </td>
      <td>{
         if ($start > 1)
         then <a href='{concat($url,"?start=",$start - $count,"&amp;element=",$element,"&amp;phrase=",$phrase)}'>previous</a>
         else('previous')
         }
      </td>
      <td>{
         if ($count-docs > $start + $ count)
         then <a href='{concat($url,"?start=",$start+$count,"&amp;element=",$element,"&amp;phrase=",$phrase)}'>next</a>
         else ('next')
         }
      </td>
   </tr>
</table>
};   

session:create(),
let $element as xs:string := request:get-parameter("element", "cgMDR:reference_uri")
let $phrase as xs:string := request:get-parameter("phrase", "")
let $start as xs:integer := request:get-parameter("start", "1")
let $count as xs:integer := request:get-parameter("count", "10")

let $urlLBNode as xs:anyURI := request:get-parameter("urlLBNode", "http://canmed-onc3469.medsch.ucl.ac.uk/axis2")
let $designation-algorithm as xs:string := request:get-parameter("sDesignationAlgorithm", "DoubleMetaphoneLuceneQuery")
let $coding-scheme as xs:string := request:get-parameter("sCodingScheme", "NCI_Thesaurus") 
(:invoke the webservice through resolve-set:)

let $terms := lb-comb:resolve-set(
                        $coding-scheme,
                        (), (),
                        true(),
                        $phrase,
                        "PREFERRED_ONLY",
                        $designation-algorithm,
                        (), (), (), (), (), (), (), (),
                        $start,
                        $count,
                        $urlLBNode
                        )//lb-coll:resolvedConceptReference
let $count-docs := count($terms)

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
      
         <form name='select-item' class='cancergridForm' action='popup-search-concept.xquery'>
            <input type='hidden' name='element' value='{$element}'/>
            
            <table class="layout">
               <tr>
                  <td>Search Phrase</td>
                  <td><input type='text' name='phrase' value='{$phrase}'/></td>
                  <td><input type="submit" value="Submit query" class="cgButton"/></td>
               </tr>
               <tr>
                  <td>LexBIG node</td>
                  <td>
                     <select name="urlLBNode">
                        <option value="http://canmed-onc3469.medsch.ucl.ac.uk/axis2">
                           London (http://canmed-onc3469.medsch.ucl.ac.uk/axis2)
                        </option>
                     </select>
                  </td>
                  <td/>
               </tr>
               <tr>
                  <td>
                     Coding scheme 
                  </td>
                  <td>
                     <select name="sCodingScheme">
                       <option value="NCI_Thesaurus">NCI Thesaurus</option>
                     </select>
                  </td>
               </tr>
            </table>
             {
                 transform:transform(
                     lb-srvc:get-match-algorithms(
                         $urlLBNode cast as xs:anyURI),
   
                     concat("xmldb:exist://", lib-util:editPath(),
                         "stylesheets/lb-match-algorithms2html.xslt"),
   
                     <parameters>
                         <param name="param-select"
                             value="{request:get-parameter("sDesignationAlgorithm", "")}" />
                     </parameters>
                 )
             }
        
           <table class="layout">
            <thead>
                <tr>
                    <td class="light-rule">Concept code</td>
                    <td class="light-rule">Entity description</td>
                    <td class="light-rule">Definition(s)</td>
                    <td/>
                </tr>
            </thead>
               <tbody>
                  {
                     for $term at $pos in $terms
                     let $id := string($term/lb-core:conceptCode)
                     let $ver := string($term/lb-core:codingSchemeVersion)
                     let $def := string($term/lb-core:referencedEntry/lg-cncpt:definition)
                     where $pos >= $start and $pos < $start + $count 
                     return element tr {
                        element td {$id},
                        element td {$term/lb-core:entityDescription/lg-common:content},
                        element td {$term/lb-core:referencedEntry/lg-cncpt:definition},
                        element td {local:action-button($id, $element, "use this term", $def)}
                        }
                     
                  }
               </tbody>
            </table>
            {local:results-link("popup-search-classifier.xquery", $element, $phrase, $start, $count, $count-docs)}
         </form>
         {local:acknowledgement()}
      </body>
   </html>
   
   
