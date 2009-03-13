module namespace lib-forms="http://www.cancergrid.org/xquery/library/forms";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  

declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";

import module namespace 
   lib-rendering="http://www.cancergrid.org/xquery/library/rendering" 
   at "../web/m-lib-rendering.xquery";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    
   
declare namespace functx = "http://www.functx.com"; 
   
   
declare variable $lib-forms:action-update-body as xs:string := 'update';   
declare variable $lib-forms:action-de-associate as xs:string := 'associate data element';
declare variable $lib-forms:action-de-dissociate as xs:string := 'dissociate data element';
declare variable $lib-forms:action-de-control-name as xs:string := 'de_id';
declare variable $lib-forms:action-rd-associate as xs:string := 'associate reference';
declare variable $lib-forms:action-rd-dissociate as xs:string := 'dissociate reference';
declare variable $lib-forms:action-rd-control-name as xs:string := 'reference_id';
declare variable $lib-forms:action-name-add as xs:string := 'add this name';
declare variable $lib-forms:action-name-delete as xs:string := 'delete name';
declare variable $lib-forms:action-name-control-name as xs:string := 'name_id';
declare variable $lib-forms:action-cl-associate as xs:string := 'associate classifier';
declare variable $lib-forms:action-cl-dissociate as xs:string := 'dissociate classifier';
declare variable $lib-forms:action-cl-control-name as xs:string := 'classification_id';
declare variable $lib-forms:action-pv-add as xs:string := 'add permissible value';
declare variable $lib-forms:action-pv-delete as xs:string := 'delete permissible value';
declare variable $lib-forms:action-pv-control-name as xs:string := 'permissible_value_id';
declare variable $lib-forms:action-vm-add as xs:string := 'add value meaning';
declare variable $lib-forms:action-vm-delete as xs:string := 'delete value meaning';
declare variable $lib-forms:action-vm-control-name as xs:string := 'value_meaning_id';
declare variable $lib-forms:action-rai-add as xs:string := 'associate admin item';
declare variable $lib-forms:action-rai-delete as xs:string := 'dissociate admin item';
declare variable $lib-forms:action-rai-control-name as xs:string := 'admin_item_id';
declare variable $lib-forms:action-update-relationship as xs:string :='find relationship';
(:~
  Generate a random name with specified length and prefix
  @param $starting The prefix
  @param $length The length
  @return Name as xs:string
  recursive - adds a letter until length achieved
~:)

declare function functx:substring-before-last 
  ( $arg as xs:string? ,
    $delim as xs:string )  as xs:string {
       
   if (matches($arg, functx:escape-for-regex($delim)))
   then replace($arg,
            concat('^(.*)', functx:escape-for-regex($delim),'.*'),
            '$1')
   else ''
 } ;
 
declare function functx:escape-for-regex 
  ( $arg as xs:string? )  as xs:string {
       
   replace($arg,
           '(\.|\[|\]|\\|\||\-|\^|\$|\?|\*|\+|\{|\}|\(|\))','\\$1')
 } ;
 
declare function functx:substring-after-last 
  ( $arg as xs:string? ,
    $delim as xs:string )  as xs:string {
       
   replace ($arg,concat('^.*',functx:escape-for-regex($delim)),'')
 } ;   

(: simpler random id function :)
declare function lib-forms:random-name($starting as xs:string?, $length as xs:nonNegativeInteger)
as xs:string
{
   let $chars as xs:string* := ('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E')
   let $rand as xs:nonNegativeInteger := xs:nonNegativeInteger(util:random(15) + 1)
   return
      if (string-length($starting) lt $length)
      then lib-forms:random-name (concat($starting, $chars[$rand]),$length)
      else $starting
};


(:~
  Generate an identifier for a prospective new data element.
  Type specification is not required as mdrElementId resolves this
  ID is unique throughout the database - avoiding two admin items
  having the same identifier.
:)
declare function lib-forms:generate-id() as xs:string
{
   (: needs to accept a sequence of previous identifiers to include in its search :)
   let $new-id := lib-forms:random-name((), 9)
   return
      (:test naked ID documents:)
      if (doc-available(concat($new-id,'.xml')))
      then lib-forms:generate-id()
      else 
         if (
            for $id in lib-util:mdrElements('value_domain')//@permissible_value_identifier
            where xs:string($id) = $new-id
            return true())
         then lib-forms:generate-id()
         else
            if (
               for $id in lib-util:mdrElements('conceptual_domain')//@value_meaning_identifier
               where xs:string($id) = $new-id
               return true())
            then lib-forms:generate-id()
            else
               if (
                  for $id in lib-util:mdrElements()//@data_identifier
                  where xs:string($id) = $new-id
                  return true())
               then lib-forms:generate-id()
               else $new-id
         
};


(:finders for relationships:)

declare function lib-forms:find-concept-id(
         $control-name as xs:string, 
         $form-name as xs:string,
         $return-value-to as xs:string,
         $button-label as xs:string,
         $default-value as xs:string?
         ) as element()*
{
      lib-forms:input-element($control-name, 70, request:get-parameter($control-name,$default-value)),
      <input type="submit" class="cgButton" value="{$button-label}"
      onclick="document.{$form-name}.action='LexBIG-form.xquery?destination-page={$return-value-to}&amp;return-parameter={$control-name}'"/>   
};


(: atomic functions for producing form elements :)





declare function lib-forms:select-from-simpleType-enum($simple-type-name as xs:string, $select-name as xs:string, $useDocumentation as xs:boolean, $received-value as xs:string) as node()
{
   element select {
      attribute name {$select-name},
      lib-forms:blank-filler(),
      for $enumeration in collection(lib-util:schemaPath())//xs:simpleType[@name=$simple-type-name]//xs:enumeration
         let $id:= data($enumeration/@value)
         let $name:= (
            if ($useDocumentation) then data($enumeration/xs:annotation/xs:documentation)
            else data($enumeration/@value)
            )
         order by $name
         return lib-forms:select-filler($id, $name, $received-value)
      }

};

declare function lib-forms:select-filler($id as xs:string?, $name as xs:string?, $received-value as xs:string?) as node()
{
   element option {
      attribute value {$id},
      if ($id=$received-value) then (attribute selected {'selected'}) else (),
      $name
      }
};

declare function lib-forms:make-select-submitted_by($received-value as xs:string) as node()
{
   element select {
      attribute name {'submitted-by'}, 
      lib-forms:blank-filler(),      
      for $item in lib-util:mdrElements('organization')//cgMDR:Contact 
      let $name:= data($item//cgMDR:contact_name)
      let $id:= data($item//@contact_identifier)
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

declare function lib-forms:make-select-administered_by($received-value as xs:string) as node()
{
   element select {
      attribute name {'administered-by'},
      lib-forms:blank-filler(),      
      for $item in lib-util:mdrElements('organization')//cgMDR:Contact 
      let $name:= data($item//cgMDR:contact_name)
      let $id:= data($item//@contact_identifier)
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

declare function lib-forms:make-select-registered_by($received-value as xs:string) as node()
{
element select {
   attribute name {'registered-by'},
   lib-forms:blank-filler(),
   for $reg-auth in lib-util:mdrElements('registration_authority')//cgMDR:Registration_Authority
   return
      element optgroup {
         attribute label {$reg-auth//cgMDR:organization_name},
   
         for $item in $reg-auth//cgMDR:represented_by
         let $name:= data($item//cgMDR:contact_name)
         let $id:= data($item//cgMDR:registrar_identifier)
         order by $name
         return lib-forms:select-filler($id, $name, $received-value)
      }
      }
};

declare function lib-forms:make-select-admin-item($collection as xs:string, $select-name as xs:string, $received-value as xs:string) as node()*
{
   element input {
      attribute type {'text'},
      attribute name {$select-name},
      attribute id {$select-name},
      attribute value {$received-value},
      attribute size {'97%'},
      attribute disabled {'disabled'}
      },
      element div {
         attribute id {concat($select-name,'-div')},
         administered-item:preferred-name($collection,$received-value)
      },
      lib-forms:popup-search($collection, $select-name)
   
};

declare function lib-forms:make-select-refdoc($control-name as xs:string, $received-value as xs:string) as node()
{
   element select {
      attribute name {$control-name},
      element option {
      attribute value {''}, 'select reference document...'
      },
      for $item in lib-util:mdrElements('reference_document')
      let $id:= string($item/@reference_document_identifier)
      let $title := substring(string($item/cgMDR:reference_document_title),0,60)
      return lib-forms:select-filler($id, $title, $received-value)
      }
};
declare function lib-forms:make-select-value-meaning($select-name as xs:string, $conceptual-domain-id as xs:string, $received-value as xs:string?) as node()
{
      element select {
         attribute name {$select-name},
         lib-forms:blank-filler(),
         for $value-meaning in lib-util:mdrElement('conceptual_domain',$conceptual-domain-id)//cgMDR:Value_Meaning
            let $name := $value-meaning/cgMDR:value_meaning_description
            let $id := $value-meaning/cgMDR:value_meaning_identifier
            return lib-forms:select-filler($id, $name, $received-value)
            }
};

declare function lib-forms:make-select-registration-authority($received-value as xs:string) as node()
{
   element select {
      attribute name {'registration-authority'},
      lib-forms:blank-filler(),
      for $item in lib-util:mdrElements('registration_authority')
         let $name:= data($item//cgMDR:organization_name)
         let $id:= data($item//@organization_identifier)
         return lib-forms:select-filler($id, $name, $received-value)
         }
};

declare function lib-forms:input-element($name as xs:string, $size as xs:integer, $received-value as xs:string?) as node()
{
   <input type="text" name="{$name}" id="{$name}" size="{$size}" value="{$received-value}"/>
};

declare function lib-forms:hidden-element($name as xs:string, $value as xs:string?) as node()
{
   <input type="hidden" name="{$name}" value="{$value}"/>
};

declare function lib-forms:hidden-element($name as xs:string, $htmlID as xs:string, $value as xs:string?) as node()
{
   <input type="hidden" name="{$name}" value="{$value}" id="{$htmlID}"/>
};

declare function lib-forms:text-area-element($name as xs:string, $rows as xs:integer, $cols as xs:integer, $received-value as xs:string?) as node()
{
   element textarea {
      attribute name {$name},
      attribute rows {$rows},
      attribute cols {"62"},
      if ($received-value >"") then ($received-value) else (concat ('please complete ', $name, '...'))
      }
};

declare function lib-forms:blank-filler() as node()
{
      element option {
      attribute value {''}, 'select ...'
      }
};

declare function lib-forms:make-select-classified-by($received-value as xs:string, $control-name as xs:string) as node()
{
   element select {
      attribute name {$control-name},
      lib-forms:blank-filler(),
      for $classification-item in lib-util:mdrElements('classification_scheme_item')
      let $id:= data($classification-item//@classification_scheme_item_identifier)
      let $name:= data(concat($classification-item//@contained_in, ': ', $classification-item//cgMDR:classification_scheme_item_value))
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

declare function lib-forms:make-select-classified-by($received-value as xs:string) as node()
{
   lib-forms:make-select-classified-by($received-value, "classified_by")
};


declare function lib-forms:make-select-datatype($received-value as xs:string?) as node()
{
   element select {
      attribute name {"datatype"},
      lib-forms:blank-filler(),
      for $item in lib-util:mdrElements('data_type')
      let $id:= data($item//@datatype_identifier)
      let $name:= concat(data($item//cgMDR:datatype_name), ': ', data($item//cgMDR:datatype_scheme_reference))
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

declare function lib-forms:make-select-uom($received-value as xs:string?) as node()
{
   element select {
      attribute name {"uom"},
      lib-forms:blank-filler(),
      for $item in lib-util:mdrElements('unit_of_measure')
      let $id:= data($item//@unit_of_measure_identifier)
      let $name:= data($item//cgMDR:unit_of_measure_name)
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

declare function lib-forms:radio($name as xs:string, $value as xs:string, $received-value as xs:string?) as node()
{
      element input {
         attribute type {'radio'},
         attribute name {$name},
         attribute value {$value},
         if ($received-value = 'true') then (attribute checked {'checked'}) else ()
         }
      
};

(: functions for rendering forms and form elements :)

declare function lib-forms:wrap-form-contents($title as xs:string, $form-content as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="edit_admin_item" method="post" class="cancergridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};

declare function lib-forms:classification($admin-item as node()) as node()*
{
  <table class="layout">
      <tr><td class="left_header_cell" colspan="6">classification {lib-forms:hidden-element($lib-forms:action-cl-control-name, '0')}</td></tr>
      <tr>
         <td class="left_spacer_cell"/><td/>
         <td width="25%"><div class="admin_item_table_header">id</div></td>
         <td><div class="admin_item_table_header">term name</div></td>
         <td><div class="admin_item_table_header">action</div></td>
      </tr>
   {
   for $classifier at $classifier-seq in $admin-item//cgMDR:classified_by
   for $classification_item in lib-util:mdrElements('classification_scheme_item')
   where data($classification_item//@classification_scheme_item_identifier) = data($classifier/.)
   return
         <tr>
            <td class="left_spacer_cell"/>   
            <td>{$classifier-seq}</td>
            <td>{data($classifier/.)}{lib-forms:hidden-element('classified-by', data($classifier/.))}</td>
            <td>{data($classification_item//cgMDR:classification_scheme_item_value)}</td>
            <td>{lib-forms:action-button($lib-forms:action-cl-dissociate,$lib-forms:action-cl-control-name,$classifier-seq)}</td>
         </tr>
         }
         <tr class="light-rule"><td colspan="6"/></tr>
         <tr>
            <td class="left_spacer_cell"/>   
            <td>new classifier</td>
            <td colspan="2">{lib-forms:make-select-classified-by('','classified-by-new' )}</td>
            <td colspan="2">
               {lib-forms:action-button($lib-forms:action-cl-associate, $lib-forms:action-cl-control-name, 0), lib-forms:reset-button()}
            </td>
         </tr>
   </table>,
   <br/>
};

declare function lib-forms:admin-item-common($admin-item as node()) as node()*
{
   lib-forms:hidden-element('registration-authority', request:get-parameter('registration-authority',data($admin-item//@item_registration_authority_identifier))),
   lib-forms:hidden-element('data-identifier', request:get-parameter('data-identifier',data($admin-item//@data_identifier))),
   lib-forms:hidden-element('version', request:get-parameter('version',lib-forms:increment-version($admin-item//@version))),
   lib-forms:hidden-element('old-version',request:get-parameter('old-version',$admin-item//@version)),
   lib-forms:hidden-element('root-element',request:get-parameter('root-element',local-name($admin-item))),
   <table class="layout">
      <tr><td class="left_header_cell" colspan="3">administration record</td></tr>
      <tr><td class="left_spacer_cell"/><td class="left_header_cell">creation date {lib-forms:hidden-element('creation-date',data($admin-item//cgMDR:creation_date))}</td><td>{data($admin-item//cgMDR:creation_date)}</td></tr>
      <tr><td/><td class="left_header_cell">last changed date</td><td>{lib-forms:hidden-element('last-change-date',current-date())}{data(current-date())} - set to today's date</td></tr>
      <tr><td/><td class="left_header_cell">change description</td><td>{lib-forms:text-area-element('change-description',5,60,request:get-parameter('change_description',data($admin-item//cgMDR:change_description)))}</td></tr>
      <tr><td/><td class="left_header_cell">effective date</td><td>{lib-forms:input-element('effective-date',97,request:get-parameter('effective_date',data($admin-item//cgMDR:effective_date)))}</td></tr>
      <tr><td/><td class="left_header_cell">registratration status</td><td>{lib-forms:select-from-simpleType-enum('Registration_Status','registration-status', false(),request:get-parameter('registration-status',data($admin-item//cgMDR:registration_status)))}</td></tr>
      <tr><td/><td class="left_header_cell">administrative status</td><td>{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(),request:get-parameter('administrative-status',data($admin-item//cgMDR:administrative_status)))}</td></tr>
      <tr><td/><td class="left_header_cell">administrative note</td><td>{lib-forms:text-area-element('administrative-note',5,60,request:get-parameter('administrative-note',data($admin-item//cgMDR:administrative_note)))}</td></tr>
      <tr><td/><td class="left_header_cell">explanatory-comment</td><td>{lib-forms:text-area-element('explanatory-comment',5,60,request:get-parameter('explanatory-comment',data($admin-item//cgMDR:explanatory_comment)))}</td></tr>
      <tr><td/><td class="left_header_cell">unresolved issue</td><td>{lib-forms:text-area-element('unresolved-issue',5,60,request:get-parameter('unresolved-issue',data($admin-item//cgMDR:unresolved_issue)))}</td></tr>
      <tr><td/><td class="left_header_cell">origin</td><td>{lib-forms:input-element('origin',97,request:get-parameter('origin',(if (data($admin-item//cgMDR:origin)>'') then (data($admin-item//cgMDR:origin)) else (''))))}</td></tr>
      <tr><td/><td class="left_header_cell" colspan="3">provenance</td></tr>
      <tr><td/><td class="left_header_cell">registered by</td><td> {lib-forms:make-select-registered_by(request:get-parameter('registered-by',data($admin-item//cgMDR:registered_by)))} </td></tr>
      <tr><td/><td class="left_header_cell">administered by</td><td> {lib-forms:make-select-administered_by(request:get-parameter('administered-by',data($admin-item//cgMDR:administered_by)))} </td></tr>
      <tr><td/><td class="left_header_cell">submitted by</td><td> {lib-forms:make-select-submitted_by(request:get-parameter('submitted-by',data($admin-item//cgMDR:submitted_by)))} </td></tr>
      <tr><td/><td colspan="2">{lib-forms:action-button($lib-forms:action-update-body, 'action' ,0),lib-forms:reset-button()}</td></tr>
      </table>,
   <br/>
};

declare function lib-forms:action-button($value as xs:string, $control as xs:string, $action as xs:string) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"submit"},
      attribute name {"update"},
      attribute value {$value},
      attribute onclick {concat("{window.document.edit_admin_item.", $control, ".value = ",$action,";}")}
   }
};

declare function lib-forms:action-button($value as xs:string, $control as xs:string, $action as xs:string, $name as xs:string) as node()
{
   element input {
      attribute class {"cgButton"},
      attribute type {"submit"},
      attribute name {$name},
      attribute value {$value},
      attribute onclick {concat("{window.document.edit_admin_item.", $control, ".value = ",$action,";}")}
   }
};


declare function lib-forms:reset-button()
{
   <input class="cgButton" type="reset" value="reset"/>
};

declare function lib-forms:admin-item-reference($admin-item as node()) as node()*
{
   <table class="layout">
      <tr><td class="left_header_cell" colspan="7">reference documents {lib-forms:hidden-element($lib-forms:action-rd-control-name ,'0')}</td></tr>
      <tr>
      <td class="left_spacer_cell"/>  
      <td/>
      <td width="25%"><div class="admin_item_table_header">id</div></td>
      <td><div class="admin_item_table_header">title</div></td>
      <td><div class="admin_item_table_header">type</div></td>
      <td><div class="admin_item_table_header">lang</div></td>
      
      <td><div class="admin_item_table_header">action</div></td></tr>
      {
         for $ref-doc at $ref-doc-seq in $admin-item//cgMDR:described_by
         let $ref-doc-id := data($ref-doc/.) 
         let $document := lib-util:mdrElement('reference_document',$ref-doc)
         let $title := data($document//cgMDR:reference_document_title)
         let $lang := data($document//cgMDR:reference_document_language_identifier)
         let $type := data($document//cgMDR:reference_document_type_description)
         return
         <tr>
         <td class="left_spacer_cell"/>         
         <td>{$ref-doc-seq}</td>
         <td>{$ref-doc/.}{lib-forms:hidden-element('reference-document',$ref-doc/.)}</td>
         <td>{$title}</td>
         <td>{$type}</td>
         <td>{$lang}</td>
         <td>{lib-forms:action-button($lib-forms:action-rd-dissociate, $lib-forms:action-rd-control-name, $ref-doc-seq)}</td></tr>
      }
      <tr class="light-rule"><td colspan="7"/></tr>
      <tr>
      <td/>
      <td>new reference document</td>
      <td>{lib-forms:make-select-refdoc("reference-document-new", '')}</td>
      <td>{lib-forms:action-button($lib-forms:action-rd-associate, $lib-forms:action-rd-control-name, 0), lib-forms:reset-button()}</td>
      </tr>
   </table>,
   <br/>
};

declare function lib-forms:admin-item-naming($admin-item as node()) as node()*
{

   
   (:function will have unexpected behaviour at over nine names per contexts or nine contexts:)
   
   
   <table class="layout">
      <tr><td class="left_header_cell" colspan="8">naming {lib-forms:hidden-element($lib-forms:action-name-control-name ,'0')}</td></tr>
      <tr>
      <td class="left_spacer_cell"/>
      <td></td>
      <td width="25%"><div class="admin_item_table_header">context</div></td>
      <td><div class="admin_item_table_header">name</div></td>
      <td><div class="admin_item_table_header">country</div></td>
      <td><div class="admin_item_table_header">lang</div></td>
      <td><div class="admin_item_table_header">preferred</div></td>
      <td><div class="admin_item_table_header">action</div></td></tr>
      {
      for $having at $having-id in $admin-item//cgMDR:having
      for $containing at $containing-id in $having/cgMDR:containing
      let $pos := max($containing-id) * ($having-id - 1) + $containing-id
      return
      (
      <tr>
         <td class="left_spacer_cell"/>
         <td>{$pos}</td>
         <td>{lib-forms:make-select-admin-item('context','context',$having/cgMDR:context_identifier)}</td>
         <td>{lib-forms:input-element('name',97,data($containing/cgMDR:name))}</td>
         <td>{lib-forms:select-from-simpleType-enum('Country_Identifier','name-country-identifier', false(), data($containing//cgMDR:country_identifier))}</td> 
         <td>{lib-forms:select-from-simpleType-enum('Language_Identifier','name-language-identifier', false(),data($containing//cgMDR:language_identifier))}</td>
         <td>{lib-forms:radio('preferred', data($pos), data($containing/cgMDR:preferred_designation))}</td>
         <td>{lib-forms:action-button($lib-forms:action-name-delete, $lib-forms:action-name-control-name, $pos),lib-forms:action-button($lib-forms:action-update-body, 'action' ,0)}</td></tr>,
         <tr>
         <td class="left_spacer_cell"/>
         <td/>
         <td colspan="6">{lib-forms:text-area-element('definition',5,76,data($containing/cgMDR:definition_text))}</td>
      </tr>
      )
      }
      <tr class="light-rule"><td colspan="8"/></tr>
      <tr>
      <td class="left_spacer_cell"/><td>new name</td>
         <td>{lib-forms:make-select-admin-item('context','context-new','')}</td>
      <td>{lib-forms:input-element('name-new',97,'')}</td>
      <td>{lib-forms:select-from-simpleType-enum('Country_Identifier','name-country-identifier-new', false(), 'GB')}</td> 
      <td>{lib-forms:select-from-simpleType-enum('Language_Identifier','name-language-identifier-new', false(), 'eng')}</td>
      <td>{lib-forms:radio('preferred', 'new', 'false')}</td>
      <td>{lib-forms:action-button($lib-forms:action-name-add, $lib-forms:action-name-control-name ,0), lib-forms:reset-button()}</td></tr>
      <tr>
         <td class="left_spacer_cell"/>
         <td/>
         <td colspan="4">{lib-forms:text-area-element('definition-new',5,76,'')}</td></tr>
   </table>,<br/>
   
   
};
























(:functions for composing administered items :)
declare function lib-forms:admin-item-attributes($reg-auth as xs:string, $id as xs:string, $ver as xs:string) as node()*
{
   attribute item_registration_authority_identifier {$reg-auth},
   attribute data_identifier {$id},
   attribute version {$ver}
};

declare function lib-forms:classified-by(
      $skip-classified-by as xs:integer, 
      $classified-by as xs:string*,
      $classified-by-new as xs:string?) as node()*
{
   for $classified-by at $classified-by-seq in $classified-by
   return
      if ($skip-classified-by = $classified-by-seq) then ()
      else (
      element cgMDR:classified_by {$classified-by}
      ),

   if (empty($classified-by-new)) then ()
   else (
      element cgMDR:classified_by {$classified-by-new}   
   )
         
};

declare function lib-forms:admin-item-common(
   $administrative-note as xs:string?, 
   $administrative-status as xs:string?, 
   $change-description as xs:string?, 
   $creation-date as xs:string?, 
   $effective-date as xs:string?, 
   $explanatory-comment as xs:string?, 
   $last-change-date as xs:string?, 
   $origin as xs:string?, 
   $registration-status as xs:string?, 
   $unresolved-issue as xs:string?, 
   $administered-by as xs:string?, 
   $registered-by as xs:string?, 
   $submitted-by as xs:string?
   ) as node()*
{
   element cgMDR:administered_item_administration_record {
      element cgMDR:administrative_note {$administrative-note},
      element cgMDR:administrative_status {$administrative-status},
      element cgMDR:change_description {$change-description},
      element cgMDR:creation_date {$creation-date},
      element cgMDR:effective_date {$effective-date},
      element cgMDR:explanatory_comment {$explanatory-comment},
      element cgMDR:last_change_date {$last-change-date},
      element cgMDR:origin {$origin},
      element cgMDR:registration_status {$registration-status},
      element cgMDR:unresolved_issue {$unresolved-issue}
   },
   element cgMDR:administered_by {$administered-by},
   element cgMDR:registered_by {$registered-by}, 
   element cgMDR:submitted_by {$submitted-by}
};

declare function lib-forms:described-by(
   $skip-id as xs:integer, 
   $reference-document as xs:string*,
   $ref-doc-new as xs:string?
   ) as node()*
{
   for $ref-doc at $ref-doc-seq in $reference-document
   return
   if ($ref-doc-seq = $skip-id)
   then ()
   else (element cgMDR:described_by{$ref-doc}),
   
   if (empty($ref-doc-new)) 
   then ()
   else (element cgMDR:described_by{$ref-doc-new})
};

declare function lib-forms:having(
      $skip-name as xs:integer, 
      $add-name as xs:boolean,
      $preferred as xs:string*, 
      $context as xs:string*,
      $name as xs:string*,
      $name-country-identifier as xs:string*, 
      $name-language-identifier as xs:string*,
      $definition as xs:string*,
      $definition-src as xs:string*,
      $context-new as xs:string?,
      $name-country-identifier-new as xs:string?,
      $name-language-identifier-new as xs:string?,
      $name-new as xs:string?,
      $definition-new as xs:string?,
      $definition-src-new as xs:string?
   ) as node()*
{
      for $having at $having-seq in $context
      for $name at $naming-seq in $name
   
      where $having-seq = $naming-seq 
      return
         if ($skip-name = $having-seq) then ()
         else (
            element cgMDR:having {
               element cgMDR:context_identifier {$having},
               element cgMDR:containing {
                  element cgMDR:language_section_language_identifier {
                     element cgMDR:country_identifier {$name-country-identifier[$having-seq]},
                     element cgMDR:language_identifier {$name-language-identifier[$having-seq]}
                  },
                  element cgMDR:name {$name},
                  element cgMDR:definition_text {$definition[$having-seq]},
                  element cgMDR:preferred_designation {
                     if ($preferred='new') then(false())
                     else(
                        if ($preferred = $having-seq) then (true()) else (false()))
                        },
                        
                  element cgMDR:definition_source_reference {$definition-src[$having-seq]}
               }
            }
         ),
         if ($add-name = true()) then (
            element cgMDR:having {
               element cgMDR:context_identifier {$context-new},
               element cgMDR:containing {
                  element cgMDR:language_section_language_identifier {
                     element cgMDR:country_identifier {$name-country-identifier-new},
                     element cgMDR:language_identifier {$name-language-identifier-new}
                  },
                  element cgMDR:name {$name-new},
                  element cgMDR:definition_text {$definition-new},
                  element cgMDR:preferred_designation {if ($preferred = 'new') then (true()) else (false())},
                  element cgMDR:definition_source_reference {$definition-src-new}
               }
            }
         )
         else()      
};

declare function lib-forms:value-domain-related-to(
      $skip-related as xs:integer, 
      $add-related as xs:boolean,
      $related-to as xs:string*,
      $how-related as xs:string*,
      $related-to-new as xs:string?, 
      $relationship-type-new as xs:string?
      ) as node()*
{
   for $related-to at $related-to-seq in $related-to
   return
      if ($related-to-seq = $skip-related) then()
      else (
         element cgMDR:related_to {
            element cgMDR:value_domain_relationship_type_description {$how-related[$related-to-seq]},
            element cgMDR:related_to {$related-to}
            }
      ),
   if ($add-related) then (
      element cgMDR:related_to {
         element cgMDR:value_domain_relationship_type_description {$relationship-type-new},
         element cgMDR:related_to {$related-to-new}
         })
   
   else()
};

declare function lib-forms:value-domain(
   $representation-class as xs:string?,
   $data-type as xs:string?,
   $uom as xs:string?,
   $max-char-qty as xs:string?,
   $format as xs:string?) as node()*
{
   element cgMDR:typed_by {$representation-class},
   element cgMDR:value_domain_datatype {$data-type},
   if ($max-char-qty) then (element cgMDR:value_domain_maximum_character_quantity {$max-char-qty}) else (),
   element cgMDR:value_domain_format {$format}, 
   element cgMDR:value_domain_unit_of_measure {$uom}
};

declare function lib-forms:value-domain-common($admin-item as node()) as node()*
{   
   <table class="layout">
   <tr><td class="left_header_cell" colspan="3">value domain common</td></tr>
   <tr><td class="left_spacer_cell"/><td class="left_header_cell">typed by representation class</td><td>{lib-forms:make-select-admin-item('representation_class','representation-class', data($admin-item//cgMDR:typed_by))}</td></tr>
   <tr><td/><td class="left_header_cell">data type</td><td>{lib-forms:make-select-datatype(data($admin-item//cgMDR:value_domain_datatype))}</td></tr>
   <tr><td/><td class="left_header_cell">unit of measure</td><td>{lib-forms:make-select-uom(data($admin-item//cgMDR:value_domain_unit_of_measure))}</td></tr>
   <tr><td/><td class="left_header_cell">maximum character quantity</td><td>{lib-forms:input-element('max-char-qty',97,data($admin-item//cgMDR:value_domain_maximum_character_quantity))}</td></tr>
   <tr><td/><td class="left_header_cell">format</td><td>{lib-forms:input-element('format',97,data($admin-item//cgMDR:value_domain_format))}</td></tr>
   <tr><td/><td colspan="2">{lib-forms:action-button($lib-forms:action-update-body, 'action', 0),lib-forms:reset-button()}</td></tr>
   </table>,
   <br/>
};

declare function lib-forms:concept-domain-common($dimensionality as xs:string?) as node()
{
   element cgMDR:dimensionality {$dimensionality}
};

declare function lib-forms:edit-conceptual-domain($admin-item as node()) as node()*
{
   <table class="layout">
   <tr><td class="left_header_cell" colspan="3">non-enumerated value domain specific</td></tr>
   <tr><td class="left_spacer_cell"/><td class="left_header_cell">dimensionality</td><td>{lib-forms:input-element('dimensionality',97,data($admin-item//cgMDR:dimensionality))}</td></tr>
   <tr><td/><td colspan="2">{lib-forms:action-button($lib-forms:action-update-body, 'action', 0),lib-forms:reset-button()}</td></tr>
   </table>,
   <br/>
};

declare function lib-forms:related-value-domain($admin-item as node(), $item-type as xs:string) as node()*
{
<table class="layout">
   <tr><td class="left_header_cell" colspan="7">related admin items of the same type {lib-forms:hidden-element($lib-forms:action-rai-control-name,0)}</td></tr>
   <tr>
      <td class="left_spacer_cell"/>
      <td/>
      <td><div class="admin_item_table_header">identifier</div></td>
      <td><div class="admin_item_table_header">related item</div></td>
      <td><div class="admin_item_table_header">how related</div></td>
      <td><div class="admin_item_table_header">action</div></td>
      </tr>
      {
      for $related-to at $related-to-seq in $admin-item//cgMDR:related_to[cgMDR:related_to]
      return
         (
         <tr>
         <td/>
         <td>{$related-to-seq}</td>
         <td>{string($related-to/cgMDR:related_to)}</td>
         <td>{lib-forms:make-select-admin-item('value_domain', 'related-item', string($related-to/cgMDR:related_to))}</td>
         <td>{lib-forms:select-from-simpleType-enum('relationship_type_name','relationship',false(),string($related-to/cgMDR:value_domain_relationship_type_description))}</td>
         <td>{lib-forms:action-button($lib-forms:action-rai-delete,$lib-forms:action-rai-control-name,$related-to-seq),lib-forms:action-button($lib-forms:action-update-body, 'action', 0)}</td>
         </tr>
         )
      }
      <tr/>
      <tr class="light-rule"><td colspan="7"/></tr>
      <tr>
      <td class="left_spacer_cell"/>
      <td>new value</td>
      <td/>
         <td>{lib-forms:make-select-admin-item($item-type, 'related-item-new', '')}</td>
         <td>{lib-forms:select-from-simpleType-enum('relationship_type_name','relationship-new',false(),'')}</td>
         <td>{lib-forms:action-button($lib-forms:action-rai-add,$lib-forms:action-rai-control-name,0),lib-forms:reset-button()}</td>
      </tr>
   </table>
};

declare function lib-forms:store-reference-document($document as item(), $doc-name as xs:string, 
         $mime-type as xs:string) as xs:string
{
   let $user := session:get-attribute("username")
   let $password := session:get-attribute("password")
   let $is-ssl :=  lib-util:checkSSL()
   
   let $resource_location := lib-util:getCollectionPath('reference_document')
   return
      if (xmldb:collection-exists($resource_location) = true())
      then (
         (: check session :)
         if ($is-ssl=true() and $user and $password)
         then (
            (: authenticate :)
            if (xmldb:authenticate(concat("xmldb:exist://", $resource_location), $user, $password)=true())
            then (
               (: store :)
               if (xmldb:store($resource_location, $doc-name, $document, $mime-type) >'')
               then ('stored')
               else ('could not store')
               )
            else 'could not authenticate'
            )
         else 'no SSL, UN or PW'
         )
      else 'Could not confirm collection'
};

declare function lib-forms:store-document($document as node()) as element()
{
   let $doc-name := concat(lib-util:mdrElementId($document),'.xml')
   let $collection := lib-util:mdrElementType($document)
   let $user := session:get-attribute("username")
   let $password := session:get-attribute("password")
   let $is-ssl :=  lib-util:checkSSL()
   
   let $resource_location := concat("xmldb:exist://", lib-util:getCollectionPath($collection))
   return
      util:catch(
         "java.lang.Exception",
         if (xmldb:collection-exists($resource_location) = true())
         then (
            (: check session :)
            if ($is-ssl=true() and $user and $password)
            then (
               (: authenticate :)
               if (xmldb:login($resource_location, $user, $password)=true())
               then (
                  (: store :)
                  let $result := xmldb:store($resource_location, $doc-name, $document)
                  return
                     if ($result >'')
                     then element success {attribute location {$result}, 'stored'}
                     else element error {(concat('xmldb:store function failed. SessionID:', if(session:get-id()) then session:get-id() else "no session!") )}
                  )
               else element error{'could not authenticate - please login'}
               )
            else element error {'no SSL, username or password - please login'}
            )
         else element error {
            element message {'Could not confirm collection - please report to the developers'},
            element location {$resource_location}}
         , element error {
            element message{concat("Document not stored: ", $util:exception-message)},
            element resource-location {$resource_location}, 
            element user {$user},
            element doc-name {$doc-name},
            element is-ssl {$is-ssl},
            element collection {$collection},
            element session {if(session:get-id()) then session:get-id() else "no session!"}
            })
};   


declare function lib-forms:store-resource(
   $collection as xs:string,
   $doc-name as xs:string,
   $document as node()
   ) as element()
{

   let $user := session:get-attribute("username")
   let $password := session:get-attribute("password")
   let $is-ssl :=  lib-util:checkSSL()

let $resource_location := concat("xmldb:exist://", lib-util:getResourcePath($collection))
   return

   util:catch(
   "java.lang.Exception",
   if (xmldb:collection-exists($resource_location) = true())
   then (
   (: check session :)
   if ($is-ssl=true() and $user and $password)
   then (
   (: authenticate :)
   if (xmldb:login($resource_location, $user, $password)=true())
   then (
   (: store :)
   if (xmldb:store($resource_location, $doc-name, $document) >'')
   then element success {'stored'}
   else element error {(concat('xmldb:store function failed. SessionID:', if(session:get-id()) then session:get-id() else "no session!") )}
   )
   else element error{'could not authenticate - please login'}
   )
   else element error {'no SSL, username or password - please login'}
   )
   else element error {
   element message {'Could not confirm collection - please report to the developers'},
   element location {$resource_location}}
   , element error {
   element message{concat("Document not stored: ", $util:exception-message)},
   element resource-location {$resource_location}, 
   element user {$user},
   element doc-name {$doc-name},
   element is-ssl {$is-ssl},
   element collection {$collection},
   element session {if(session:get-id()) then session:get-id() else "no session!"}
   })
   
};


declare function lib-forms:repeating-group(
   $form-name as xs:string,
   $group-name as xs:string, 
   $group as node()*, 
   $action as xs:string
   ) as element(div)
{
   let $skip-name as xs:string := concat('skip_',$group-name)
   return
      <div>
         {lib-forms:hidden-element($skip-name,0)}
         <table>
            {
            for $group-item at $pos in $group
            where $pos != request:get-parameter($skip-name,0)
            return
               <tr>
                  <td>
                     {
                     for $name-value in $group-item/*|$group-item/@*
                     let $control-name as xs:string := local-name($name-value)
                     let $control-label as xs:string := replace($control-name,"_-"," ") 
                     return
                        <tr>
                           <td>{$control-label}</td>
                           <td>{
                              lib-forms:input-element(
                                 $control-name,
                                 70, 
                                 request:get-parameter($control-name, data($name-value)))
                               }
                           </td>
                        </tr>
                     }
                  </td>
                  <td>{lib-forms:action-button(concat($group-name, 'delete-value', $pos), 'action' ,0)}</td>
               </tr>
            }
         </table>
         {lib-forms:action-button(concat($group-name,'_add_item'), 'action' ,0)}
      </div>
};



declare function lib-forms:popup-search($type as xs:string, $control as xs:string) as element()
{
   element input {
      attribute type {'button'},
      attribute value {'update relationship'},
      attribute class {'cgButton'},
      attribute onclick {
         concat("window.open('popup-search-relationship.xquery?control=",
            $control, 
            "&amp;type=",
            $type,
            "','Popup_Window','resizable=yes,width=1000,height=350,menubar=no,left=100,top=100')")
            }
   }
};










(:treeview utility functions:)

    declare function lib-forms:get-child-node($item_id as xs:string, 
                      $scheme_id as xs:string) as element()*
    {
    for $item in lib-util:mdrElements("classification_scheme_item")
       [data(./@contained_in) = $scheme_id]
       [data(./cgMDR:association/@associationTarget) = $item_id]
    let $item_identifier := data($item//@classification_scheme_item_identifier)
    let $item_value := data($item//cgMDR:classification_scheme_item_value)
    return
            element folder
            {
            attribute title {$item_value},
            attribute img {"images/white_circle.gif"},
            attribute expanded {"false"},
            attribute code {$item_identifier},
            lib-forms:get-leaves($item_identifier), 
            lib-forms:get-child-node($item_identifier, $scheme_id)
            }
    };
    
    declare function lib-forms:get-leaves($item_id as xs:string) as element()*
    {
    for $data_element in lib-util:mdrElements('data_element')
      [.//cgMDR:classified_by=$item_id]
      [.//cgMDR:administered_item_administration_record/cgMDR:registration_status!='Superseded']
    let $admin_item_name := administered-item:preferred-name($data_element)
    let $admin_item_id := lib-util:mdrElementId($data_element)
    order by $admin_item_name 
    return 
            element{"leaf"}
            {
            attribute{"title"}{$admin_item_name},
            attribute{"img"}{"images/red_circle.gif"},
            attribute{"code"}{$admin_item_id}
            }
    };
    

declare function lib-forms:treeview-document() as node()
{
   document{
    (:<?xml-stylesheet type="text/xsl" href="treeview.xslt"?>,:)
    element{"treeview"}
    {
        for $scheme in lib-util:mdrElements("classification_scheme")
        let $classification_scheme_id := lib-util:mdrElementId($scheme)
        return
            element{"folder"}
            {
            attribute{"title"}{administered-item:preferred-name($scheme)},
            attribute{"img"}{"images/white_circle.gif"},
            attribute{"expanded"}{"false"},
            lib-forms:get-child-node("", $classification_scheme_id)
            }
        }
     }
};


declare function lib-forms:increment-version($admin-item-identifier as xs:string) as xs:string
{
    let $version as xs:string := functx:substring-after-last($admin-item-identifier, '-')
    let $new-version as xs:string :=
       if (matches($version,'.\..\..'))
       then (       
            concat(substring-before($version, '.'), '.', xs:string(xs:double(substring-after($version,"."))+0.01))
           )
       
       else xs:string(floor(xs:double($version)*100 + 1) div 100) 
             
    return
        concat(functx:substring-before-last($admin-item-identifier, '-'), '-', $new-version)
};
