xquery version "1.0";

(: ~
 : Module Name:             data element rating
 :
 : Module Version           1.0
 :
 : Date                     19th January 2007
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
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
    lib-forms="http://www.cancergrid.org/xquery/library/forms" 
    at "../edit/m-lib-forms.xquery";
    
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace validation="http://exist-db.org/xquery/validation";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";    
    
declare function local:rating-form($message as xs:string) as element()
{
   element table {
      element tr {
         element td {
            attribute class {"left_header_cell"},
            "Reviewed Data Element"
            },
         element td {
            lib-forms:input-element('ID', 93, request:get-parameter('showID',()))
            }
      },
      element tr {
         element td {
            attribute class {"left_header_cell"},
            "Reviewer"
         },
         element td {
            lib-forms:make-select-submitted_by(request:get-parameter('showSubmittedBy',''))
            } 
      },
      element tr {
         element tr {
         element td {
            attribute class {"left_header_cell"},
            "Rating"
         },
         element td {
         0,<input type="radio" name="rating" value="0"></input>,
         <input type="radio" name="rating" value="1"></input>,
         <input type="radio" name="rating" value="2"></input>,
         <input type="radio" name="rating" value="3"></input>,
         <input type="radio" name="rating" value="4"></input>,
         <input type="radio" name="rating" value="5"></input>,
         <input type="radio" name="rating" value="6"></input>,        
         <input type="radio" name="rating" value="7"></input>,
         <input type="radio" name="rating" value="8"></input>,
         <input type="radio" name="rating" value="9">9</input>
         }          
      },
      element tr {
         element td {
            attribute class {"left_header_cell"},
            "Comment"
            },
         element td {
            lib-forms:text-area-element('comment', 20, 70, '')
            }
      },
      element tr {
         element td {
            attribute class {"left_header_cell"}
            },
         element td {
            element input {
               attribute type {"submit"},
               attribute name {"score"},
               attribute value {"action"}
            },
            lib-forms:reset-button()
            }
      },
      element tr {
         element td {
            attribute colspan {"2"},
               element p {
               "When reviewing a data element you should consider 
               if its names are: correct; complete and if the 
               preferred name is the best name for the data element.  In particular, think of the ideal case report form question"
               },
               
               element p {
               "Is the definition adequate or are there any other definitions that need recording?"
               },

               element p {
               "Does the value domain reflect current practice?  Are there any other value domains we should register?"
               },

               element p {
               "Is the data element too closely defined - so as to be difficult to re-use - or is it too loose to facilitate standardisation?"
               },
                  element p {
               "Should we promote this as a standard, or should it simply be recorded as having been used by Neat, Tango or neoTango?"
               }

   }
   },
   $message, 
   lib-forms:hidden-element('Registration_Status',request:get-parameter('showRegistrationStatus','')),
   lib-forms:hidden-element('reference_document',request:get-parameter('showReferenceDocument',''))
}}
};

declare function local:rating() as element()
{
   element data-element-rating {
      attribute rating-identifier {lib-forms:generate-id()},
      attribute admin-item-id {request:get-parameter('ID',())},
      attribute submitted-by {request:get-parameter('submitted-by',())},
      attribute rating {request:get-parameter('rating',())},
      attribute review-date {current-date()},
      request:get-parameter('comment',())
      }
};

declare function local:content-success() as element()
{
   let $uri as xs:anyURI := session:encode-url(xs:anyURI(concat("../xquery/AdminItemByRegStatus.xquery?",
                              "Registration_Status=", request:get-parameter('Registration_Status',''),
                              "&amp;reference_document=", request:get-parameter('reference_document',''),
                              "&amp;submitted-by=", request:get-parameter('submitted-by',''),
                              "&amp;report-type=summary"
                              )))
   return
   <div>
   <p>
   Thankyou for your review.  Your comments are invaluable for the community.
   </p>
   <p>
   You can continue to review content by clicking 
   <a href="{$uri}">here</a>
   </p>
   </div>
};

   session:create(),
   let $title as xs:string := "Rating a data element"
   let $action := request:get-parameter("score",())
   return
      if ($action='action') 
      then (
         let $result as xs:string := lib-forms:store-document(local:rating())
         return
            if ($result='stored')
            then (lib-forms:wrap-form-contents($title, element div {local:content-success()}))
            else (lib-forms:wrap-form-contents($title, local:rating-form($result)))
         )
      else (lib-forms:wrap-form-contents($title, local:rating-form('')))