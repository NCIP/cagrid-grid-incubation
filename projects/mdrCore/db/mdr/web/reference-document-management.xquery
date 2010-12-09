xquery version "1.0";

(: ~
 : Module Name:             Reference Document Management
 :
 : Module Version           1.0
 :
 : Date                     12th Feb 2008
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          maintains reference documents
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";


import module 
   namespace lib-rendering="http://www.cagrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery"; 
  
import module namespace 
  lib-util="http://www.cagrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";

import module namespace 
    administered-item="http://www.cagrid.org/xquery/library/administered-item" 
    at "../library/m-administered-item.xquery";  


declare function local:getSubmittedbyContact($submittedby as xs:string?) as element()*
{
  for $item at $pos in lib-util:mdrElements('organization')
   let $org_name := lib-util:mdrElements('organization')[$pos]//openMDR:Organization
   let $contact := $org_name/openMDR:Contact[@contact_identifier eq $submittedby]
   return
    $contact
};


declare function local:getSubmittedbyOrg($submittedby as xs:string?) as element()*
{
  for $item at $pos in lib-util:mdrElements('organization')
   let $org_name := lib-util:mdrElements('organization')[$pos]//openMDR:Organization
   return
   if($org_name//openMDR:Contact[@contact_identifier eq $submittedby])
   then $org_name
   else ()
   
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),  session:set-attribute("count",xs:integer(0)),
let $letter := request:get-parameter("letter", ())
let $use-stylesheet := request:get-parameter("use-stylesheet","")
let $user := request:get-session-attribute("username")


let $start as xs:integer := xs:integer(request:get-parameter("start", 1))
let $extent as xs:integer := xs:integer(request:get-parameter("extent", 5))
let $type as xs:string := request:get-parameter("type", "reference_document")
let $title as xs:string := concat(lib-util:sentence-case(lib-util:mdrElementTypeFriendly($type)), " Listing")

let $all-items := 
   for $sorted in lib-util:mdrElements($type)[string(.//openMDR:Reference_Document/openMDR:administered_item_administration_record/openMDR:registration_status) ne 'Superseded'
   and starts-with(lower-case(.//openMDR:Reference_Document/openMDR:reference_document_title), $letter)]
   let $preferred-name := administered-item:preferred-name($sorted)
   order by lower-case($preferred-name)
   return $sorted

let $count-all-items as xs:integer := xs:integer(count($all-items))

let $new-displayed-items := 
   for $item at $record-id in $all-items
    let $data_id := string($item//openMDR:Reference_Document/@data_identifier)
    let $version := string($item//openMDR:Reference_Document/@version)
    let $next := number($record-id)+1
    let $check := 
        for $newitem at $next in $all-items
         let $new_data_id := string($newitem//openMDR:Reference_Document/@data_identifier)
         let $new_version := string($newitem//openMDR:Reference_Document/@version)
        return 
        if($data_id eq $new_data_id and number($new_version) > number($version))
         then(
             let $item := $newitem 
             return 'true')
        else ()     
   
   return
   if(contains($check,'true') eq false())
   then ($item)
   else ()
   
   
   let $content := ( <reference-documents>  
      {
      for $displayitem at $pos in $new-displayed-items 
        let $id := lib-util:mdrElementId($displayitem)
        let $count := xs:integer(session:set-attribute("count",xs:integer(session:get-attribute("count"))+1)) 
        let $reference_document_title:= $displayitem/openMDR:reference_document_title
        let $reference_document_version:= $displayitem/@version
        let $reference_document_description := $displayitem/openMDR:reference_document_type_description
        let $reference_document_submitted_by := data($displayitem/openMDR:submitted_by)
        let $getSubmittedbyContact := local:getSubmittedbyContact($reference_document_submitted_by)
        let $getSubmittedbyOrg := local:getSubmittedbyOrg($reference_document_submitted_by)
        
        let $log := util:log-system-out($getSubmittedbyOrg)
        
        let $anchor := <a xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en" href='../web/reference_document.xquery?compound_id={$id}'>{$reference_document_title}</a>
        
          
        where starts-with(lower-case($reference_document_title),$letter) 
        order by $pos
        return
            element reference-document { attribute class {if (($pos mod 2) = 0)
        then "odd_row"
        else "even_row"},
                attribute version {$reference_document_version},
                element description {$reference_document_description},
                element anchor {$anchor},
                element pos {$pos},
                element start {$start},
                element extent {$extent},
                element name {$reference_document_title}, 
                element submitted_by_contact {$getSubmittedbyContact},
                element submitted_by {$getSubmittedbyOrg},
                $displayitem
            }
      } 
   </reference-documents>)
   
   let $count-all-items as xs:integer := xs:integer(session:get-attribute("count"))
     
   return
   lib-rendering:txfrm-webpage($title, 
        element content-by-letter-reference-document {
            element tabular-content-reference-document {
            $content,
            element index {
                element action {'reference-document-management.xquery'},
                element previous {if ($start - $extent < 1) then (1) else ($start - $extent)},
                element next {$start + $extent},
                element last { if( ($count-all-items mod $extent)=0) then ((($count-all-items idiv $extent) - 1) * $extent + 1) else( ( ($count-all-items idiv $extent) * $extent) + 1) },
                element type {$type},
                element letter {$letter},
                element start {$start},
                element extent {$extent},
                element count {$count-all-items},
                element recordlimit {if ($start + $extent <= $count-all-items) then ($start + $extent - 1) else ($count-all-items)}
            }
          }
        }
)