xquery version "1.0";
(: ~
 : Module Name:             homepage.xquery
 :
 : Module Version           3.21
 :
 : Date                               12th September 2006
 :
 : Copyright                      The cancergrid consortium
 :
 : Module overview         Initial entry point into the metadata repository
 :
 :)
 
(:~
 :    Commented on adding session.create() method by SJH
 :
 :    @author Steve Harris
 :    @author Igor Toujilov
 :    @author Sui (Maria) Lin
 :    @since July 20, 2006
 :    @version 3.21
 :
 :    Comment: 
 :    12th September 2006: added session handling code
 :    13th September 2006: corrected infopath form calling function
 :    21st November 2006:  stripping out infopath and enabling security
~ :)
   
import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering"
   at "../web/m-lib-rendering.xquery";    
   
import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";   

declare namespace session="http://exist-db.org/xquery/session";


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";


session:create(),
let $title as xs:string :='CancerGrid Metadata Registry'
return lib-rendering:txfrm-webpage(
   $title, 
   doc(concat(lib-util:webPath(),'documents/homepage.xml')))