import module namespace 
lib-forms="http://www.cancergrid.org/xquery/library/forms"
at "../edit/m-lib-forms.xquery";

declare option exist:serialize "media-type=application/xml";

<new-id xmlns="">
   <id>{lib-forms:generate-id()}</id>
</new-id>
