xquery version "1.0";

(: ~
: Module Name:             Classification Extraction
:
: Module Version           1.0
:
: Date                     05-03-2007
:
: Copyright                The cancergrid consortium
:
: Module overview          Renders a Data element for viewing by the user of the metadata repository
:
:)

(:~
:    Commented on refactoring SJH
:
:    @author Steve Harris
:    @version 1.0
~ :)

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";

import module namespace 
lib-util="http://www.cancergrid.org/xquery/library/util" 
at "../library/m-lib-util.xquery";

import module namespace 
lib-forms="http://www.cancergrid.org/xquery/library/forms" 
at "../edit/m-lib-forms.xquery";  

   (:update any IDs:)
   for $class in collection(lib-util:classificationPath())//class[own-slots/own-slot[@slot-name='data-identifier']/entry/value='default']
   return
      update value $class//own-slot[@slot-name='data-identifier']//value with lib-forms:generate-id(),

   (:make documents:)
   for $class in collection(lib-util:classificationPath())//class
   let $identifier := $class/own-slots/own-slot[@slot-name='data-identifier']//value
   let $parent := $class/own-slots/own-slot[@slot-name=':DIRECT-SUPERCLASSES']//value
   let $value := $class/own-slots/own-slot[@slot-name=':NAME']//value
   for $parent-class in collection(lib-util:classificationPath())//class[own-slots/own-slot[@slot-name=':NAME']//value = $parent]
   let $parent-id := $parent-class/own-slots/own-slot[@slot-name='data-identifier']//value
   return
      lib-forms:store-document( 
         element cgMDR:Classification_Scheme_Item {
            attribute classification_scheme_item_identifier {$identifier},
            attribute contained_in {"GB-CANCERGRID-000026-1.0"},
            if ($parent-id = 'root')
            then (element cgMDR:association{
               attribute associationTarget{''}
               }
                 )
            else(
            element cgMDR:association {
               attribute associationTarget{$parent-id},
               element cgMDR:classification_scheme_item_relationship_type_description {'broaderTerm'}
               }
            ),
            element cgMDR:classification_scheme_item_value {$value}
         }
      )
