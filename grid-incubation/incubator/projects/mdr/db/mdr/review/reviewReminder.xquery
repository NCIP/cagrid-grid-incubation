xquery version "1.0";

(: ~
 : Module Name:             data element review reminder :
 :
 : Date                     24th January 2007
 :
 : Copyright                The cancergrid consortium
 :
 : Module overview          allows a user to record their comments on a resource
 :
 :)
 
(:~
 :    @author Steve Harris
 :    @version 0.1
~ :)


import module namespace 
   lib-forms="http://www.cancergrid.org/xquery/library/forms"
   at "../edit/m-lib-forms.xquery";
     
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";   
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";      
     
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace mail="http://exist-db.org/xquery/mail";

   for $commissioning_document in lib-util:mdrElements("reference_document")[cgMDR:reference_document_type_description="commissioning"]
   let $commissioning_document_id := lib-util:mdrElementId($commissioning_document)
      for $reviewer in $commissioning_document/cgMDR:reviewer
      for $person in lib-util:mdrElements("organization")//cgMDR:Contact[@contact_identifier = $reviewer]
      let $email-address := $person/cgMDR:contact_information
      return
         mail:send-email(
         <mail>
         <from>stephen.john.harris@gmail.com</from>
         <to>steve.harris@comlab.ox.ac.uk</to>
         <cc/>
         <subject>Data elements to review</subject>
         <message>
         <text>
         
         test
      
      </text>
         </message>
         </mail>,"smtp.ox.ac.uk",())
      