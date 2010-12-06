module namespace lib-forms="http://www.cagrid.org/xquery/library/forms";

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  

declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";

import module namespace 
   lib-rendering="http://www.cagrid.org/xquery/library/rendering" 
   at "../web/m-lib-rendering.xquery";

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   administered-item="http://www.cagrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    
   
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

declare function lib-forms:substring-before-last 
  ( $arg as xs:string? ,
    $delim as xs:string )  as xs:string {
       
   if (matches($arg, lib-forms:escape-for-regex($delim)))
   then replace($arg,
            concat('^(.*)', lib-forms:escape-for-regex($delim),'.*'),
            '$1')
   else ''
 } ;
 
declare function lib-forms:escape-for-regex 
  ( $arg as xs:string? )  as xs:string {
       
   replace($arg,
           '(\.|\[|\]|\\|\||\-|\^|\$|\?|\*|\+|\{|\}|\(|\))','\\$1')
 } ;
 
declare function lib-forms:substring-after-last 
  ( $arg as xs:string? ,
    $delim as xs:string )  as xs:string {
       
   replace ($arg,concat('^.*',lib-forms:escape-for-regex($delim)),'')
 } ;   

declare function lib-forms:generate-id() as xs:string
{
    util:uuid()
};


(:finders for relationships:)
declare function lib-forms:find-concept-id(
         $control-name as xs:string,
         $button-label as xs:string,
         $recieved_value as xs:string
         ) as element()*
{
    let $id := lib-forms:generate-id()
    let $javascript as xs:string := 
        concat("window.open('popup-search-reference-uri.xquery?element=", $id,
            "','Popup_Window','resizable=yes,width=1100,height=400,scrollbars=1,menubar=no,left=100,top=100')")
    return
        (<input type="text" name="{$control-name}" id="{$id}" size="70" value="{$recieved_value}"/>,
            element input {
                attribute type {'button'},
                attribute value {$button-label},
                attribute class {'cgButton'},
                attribute onclick {$javascript}            
            })
      
};

(:Editable false:)
declare function lib-forms:find-concept-id-edit-false(
         $control-name as xs:string,
         $button-label as xs:string,
         $recieved_value as xs:string
         ) as element()*
{
    let $id := lib-forms:generate-id()
    let $javascript as xs:string := 
        concat("window.open('popup-search-reference-uri.xquery?element=", $id,
            "','Popup_Window','resizable=yes,width=1100,height=400,scrollbars=1,menubar=no,left=100,top=100')")
    return
        (<input type="text" name="{$control-name}" id="{$id}" size="70" value="{$recieved_value}" readonly="readonly"/>,
            element input {
                attribute type {'button'},
                attribute value {$button-label},
                attribute class {'cgButton'},
                attribute onclick {$javascript}            
            })
      
};

(:added this for new conceptual domain class:)
(:finders for relationships:)
declare function lib-forms:find-concept-id-CD(
         $control-name as xs:string,
         $button-label as xs:string,
         $recieved_value as xs:string*
         ) as element()*
{
    let $id := lib-forms:generate-id()
    let $javascript as xs:string := 
        concat("window.open('popup-search-reference-uri.xquery?element=", $id,
            "','Popup_Window','resizable=yes,width=1100,height=400,scrollbars=1,menubar=no,left=100,top=100')")
    return
        (<input type="text" name="{$control-name}" readonly="true" id="{$id}" size="30" value="{$recieved_value}" />,
            element input {
                attribute type {'button'},
                attribute value {$button-label},
                attribute class {'cgButton'},
                attribute onclick {$javascript}            
            })
      
};

(: atomic functions for producing form elements :)
declare function lib-forms:select-from-contexts-enum($select-name as xs:string,$received-value as xs:string) as node()
{
   element select {
      attribute name {$select-name},
      attribute id {$select-name},
      lib-forms:blank-filler(),
     for $item in lib-util:mdrElements('context')
      let $name:= data($item//openMDR:name/text())
      let $id := concat(data($item//openMDR:Context/@item_registration_authority_identifier),'_', data($item//openMDR:Context/@data_identifier),'_',data($item//openMDR:Context/@version))
      (:let $id:= data($item//openMDR:context_identifier/text()):)      
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
   }

};


declare function lib-forms:select-from-contexts-enum-default($select-name as xs:string,$received-value as xs:string,$default as xs:string?) as node()
{
   element select {
      attribute name {$select-name},
       attribute id {$select-name},
      lib-forms:default-filler($default),
     for $item in lib-util:mdrElements('context')
      let $name:= data($item//openMDR:name/text())
      let $id := concat(data($item//openMDR:Context/@item_registration_authority_identifier),'_', data($item//openMDR:Context/@data_identifier),'_',data($item//openMDR:Context/@version))
      (:let $id:= data($item//openMDR:context_identifier/text()):)      
      order by $name
      return lib-forms:select-filler($id, $name, $default)
   }

};

declare function lib-forms:default-filler($default as xs:string) as node()
{
      if($default > '') then (
      for $default-item in lib-util:mdrElement('context',$default)
      let $def-name := data($default-item//openMDR:name/text())
      let $id := concat(data($default-item//openMDR:Context/@item_registration_authority_identifier),'_', data($default-item//openMDR:Context/@data_identifier),'_',data($default-item//openMDR:Context/@version))
      return
      element option {
        attribute value {''}, $def-name
      }
      ) else (lib-forms:blank-filler())
};

declare function lib-forms:select-from-simpleType-enum($simple-type-name as xs:string, $select-name as xs:string, $useDocumentation as xs:boolean, $received-value as xs:string) as node()
{
   element select {
      attribute id {$select-name},
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

(:
This function takes in the organization name as well along with id and name
and concats with the name to be displayed in the administered by and
submitted by dropdown in creation of administered items
:)
declare function lib-forms:select-filler-nameAndOrg($id as xs:string*, $name as xs:string*, $received-value as xs:string?, $org as xs:string*) as node()
{
   element option {
      attribute value {$id},
      if ($id=$received-value) then (attribute selected {'selected'}) else (),
      concat($name,', ',$org)
      }
};



declare function lib-forms:make-select-submitted_by($received-value as xs:string) as node()
{
   element select {
      attribute id {'submitted-by'},
      attribute name {'submitted-by'}, 
      lib-forms:blank-filler(),      
      for $item in lib-util:mdrElements('organization')//openMDR:Contact 
      let $name:= data($item//openMDR:contact_name)
      let $id:= data($item//@contact_identifier)
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

(:
This function is to display the name,organization name values in drop down
for submitted_by field in creation of administered objects screen
:)
declare function lib-forms:make-select-submitted_by-nameAndOrg($received-value as xs:string) as node()
{
   element select {
      attribute id {'submitted-by'},
      attribute name {'submitted-by'}, 
      lib-forms:blank-filler(),      
      for $item in lib-util:mdrElements('organization')//openMDR:Organization
      for $u at $pos in $item//openMDR:Contact
      let $name:= data($item//openMDR:Contact[$pos]//openMDR:contact_name)
      let $id:= data($item//openMDR:Contact[$pos]//@contact_identifier)
      let $organization := data($item//openMDR:organization_name)
      order by $name
      return lib-forms:select-filler-nameAndOrg($id, $name, $received-value,data($organization))
      }
};

declare function lib-forms:make-select-administered_by($received-value as xs:string) as node()
{ 
   element select {
      attribute id {'administered-by'},
      attribute name {'administered-by'},
      lib-forms:blank-filler(),      
      for $item in lib-util:mdrElements('organization')//openMDR:Contact 
      let $name:= data($item//openMDR:contact_name)
      let $id:= data($item//@contact_identifier)
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

(:
Function to display the name and organization in the drop down
in the create new administered item screen
:)
declare function lib-forms:make-select-administered_by-nameAndOrg($received-value as xs:string) as node()
{ 
   element select {
      attribute id {'administered-by'},
      attribute name {'administered-by'},
      lib-forms:blank-filler(),
              
      for $item in lib-util:mdrElements('organization')//openMDR:Organization
      for $u at $pos in $item//openMDR:Contact
      let $name:= data($item//openMDR:Contact[$pos]//openMDR:contact_name)
      
      let $id:= data($item//openMDR:Contact[$pos]//@contact_identifier)
      let $organization := data($item//openMDR:organization_name)
      order by $name 
      return lib-forms:select-filler-nameAndOrg($id, $name, $received-value, data($organization))
      }
};

declare function lib-forms:make-select-registered_by($received-value as xs:string) as node()
{
element select {
    attribute id {'registered-by'},
   attribute name {'registered-by'},
   lib-forms:blank-filler(),
   for $reg-auth in lib-util:mdrElements('registration_authority')//openMDR:Registration_Authority
   return
      element optgroup {
         attribute label {$reg-auth//openMDR:organization_name},
   
         for $item in $reg-auth//openMDR:represented_by
         let $name:= data($item//openMDR:contact_name)
         let $id:= data($item//openMDR:registrar_identifier)
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
      attribute size {'97%'}
      },
      element div {
         attribute id {concat($select-name,'-div')},
         administered-item:preferred-name($collection,$received-value)
      },
      lib-forms:popup-search($collection, $select-name)
};

declare function lib-forms:make-select-admin-item-edit-false($collection as xs:string, $select-name as xs:string, $received-value as xs:string) as node()*
{
   element input {
      attribute type {'text'},
   
      attribute name {$select-name},
      attribute id {$select-name},
      attribute value {$received-value},
      attribute size {'97%'},
      attribute readonly {'readonly'}
      },
      element div {
         attribute id {concat($select-name,'-div')},
         administered-item:preferred-name($collection,$received-value)
      },
      lib-forms:popup-search($collection, $select-name)
};

declare function lib-forms:make-select-form-admin-item($collection as xs:string, $select-name as xs:string, $received-value as xs:string, 
$form-name as xs:string, $button-name as xs:string) as node()*
{
    element input {
      attribute type {'text'},
   
      attribute name {$select-name},
      attribute id {$select-name},
      attribute value {$received-value},
      attribute size {'97%'}
      },
      element div {
         attribute id {concat($select-name,'-div')},
         administered-item:preferred-name($collection,$received-value),
         attribute value {administered-item:preferred-name($collection,$received-value)}
      },
      lib-forms:popup-form-search($collection, $select-name, $form-name, $button-name)     
};

declare function lib-forms:make-select-form-admin-item-edit-false($collection as xs:string, $select-name as xs:string, $received-value as xs:string, 
$form-name as xs:string, $button-name as xs:string) as node()*
{
    element input {
      attribute type {'text'},
   
      attribute name {$select-name},
      attribute id {$select-name},
      attribute value {$received-value},
      attribute size {'97%'},
      attribute readonly {'readonly'}
      },
      element div {
         attribute id {concat($select-name,'-div')},
         administered-item:preferred-name($collection,$received-value)
      },
      lib-forms:popup-form-search($collection, $select-name, $form-name, $button-name)     
};

declare function lib-forms:make-select-admin-item($collection as xs:string, $select-name as xs:string, $received-value as xs:string, $source_formname as xs:string) as node()*
{
   element input {
      attribute type {'text'},
   
      attribute name {$select-name},
      attribute id {$select-name},
      attribute value {$received-value},
      attribute value {$source_formname},
      attribute size {'97%'}
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
      let $title := substring(string($item/openMDR:reference_document_title),0,60)
      return lib-forms:select-filler($id, $title, $received-value)
      }
};
declare function lib-forms:make-select-value-meaning($select-name as xs:string, $conceptual-domain-id as xs:string, $received-value as xs:string?) as node()
{
      element select {
         attribute name {$select-name},
         lib-forms:blank-filler(),
         for $value-meaning in lib-util:mdrElement('conceptual_domain',$conceptual-domain-id)//openMDR:Value_Meaning
            let $name := $value-meaning/openMDR:value_meaning_description
            let $id := $value-meaning/openMDR:value_meaning_identifier
            return lib-forms:select-filler($id, $name, $received-value)
            }
};

declare function lib-forms:make-select-registration-authority($received-value as xs:string) as node()
{
   element select {
      attribute id {'registration-authority'},
      attribute name {'registration-authority'},
      lib-forms:blank-filler(),
      for $item in lib-util:mdrElements('registration_authority')
         let $name:= data($item//openMDR:organization_name)
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

declare function lib-forms:hidden-element-multi($name as xs:string, $value as xs:string*) as node()
{
   <input type="hidden" name="{$name}" value="{$value}"/>
};

declare function lib-forms:hidden-array-element($name as xs:string, $values as xs:string*) as node()*
{
    for $value in $values
    return
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
      let $name:= data(concat($classification-item//@contained_in, ': ', $classification-item//openMDR:classification_scheme_item_value))
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
   lib-forms:make-select-datatype('datatype',$received-value)
};

declare function lib-forms:make-select-datatype($control as xs:string?, $received-value as xs:string?) as node()
{
   element select {
      attribute name {$control},
      lib-forms:blank-filler(),
      for $item in lib-util:mdrElements('data_type')
      let $id:= data($item//@datatype_identifier)
      let $name:= concat(data($item//openMDR:datatype_name), ': ', data($item//openMDR:datatype_scheme_reference))
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

declare function lib-forms:make-select-uom($received-value as xs:string?) as node()
{
    lib-forms:make-select-uom("uom",$received-value)
};

declare function lib-forms:make-select-uom($control as xs:string?, $received-value as xs:string?) as node()
{
   element select {
      attribute name {"control"},
      lib-forms:blank-filler(),
      for $item in lib-util:mdrElements('unit_of_measure')
      let $id:= data($item//@unit_of_measure_identifier)
      let $name:= data($item//openMDR:unit_of_measure_name)
      order by $name
      return lib-forms:select-filler($id, $name, $received-value)
      }
};

declare function lib-forms:radio($name as xs:string, $value as xs:string, $received-value as xs:string?) as node()
{
      element input {
      attribute id {$name},
         attribute type {'radio'},
         attribute name {$name},
         attribute value {$value},
         if ($received-value eq 'true') then (attribute checked {'checked'}) else ()
         }
      
};

(: functions for rendering forms and form elements :)

declare function lib-forms:wrap-form-contents($title as xs:string, $form-content as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="edit_admin_item" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};

declare function lib-forms:wrap-form-contents-withFooter($title as xs:string, $form-content as element()*,$form_name as xs:string*,$element as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="{$form_name}" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      <font size="2">Items Selected/Created :</font> {$element}
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};

declare function lib-forms:wrap-form-contents-withFooter($title as xs:string, $form-content as element()*,$form_name as xs:string*,$element as element()*,$element2 as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="{$form_name}" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      <table>
        <td><font size="2">Items Selected/Created :</font></td> 
        <td>{$element}</td>
        <td>{$element2}</td>
      </table>
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};

declare function lib-forms:wrap-form-contents-withFooter($title as xs:string, $form-content as element()*,$form_name as xs:string*,$element as element()*,$element2 as element()*,$element3 as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="{$form_name}" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      <table>
        <td><font size="2">Items Selected/Created :</font></td> 
        <td>{$element}</td>
        <td>{$element2}</td>
        <td>{$element3}</td>
      </table>
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};

declare function lib-forms:wrap-form-contents-withFooter($title as xs:string, $form-content as element()*,$form_name as xs:string*,$element as element()*,$element2 as element()*,$element3 as element()*,$element4 as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="{$form_name}" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      <table>
         <td><font size="2">Items Selected/Created :</font></td> 
         <td>{$element}</td>
         <td>{$element2}</td>
         <td>{$element3}</td>
         <td>{$element4}</td>
      </table>
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};

declare function lib-forms:wrap-form-contents-withFooter($title as xs:string, $form-content as element()*,$form_name as xs:string*,$element as element()*,$element2 as element()*,$element3 as element()*,$element4 as element()*,$element5 as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="{$form_name}" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      <table>
         <td><font size="2">Items Selected/Created :</font></td> 
         <td>{$element}</td>
         <td>{$element2}</td>
         <td>{$element3}</td>
         <td>{$element4}</td>
         <td>{$element5}</td>
      </table>
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};

declare function lib-forms:wrap-form-contents-withFooter($title as xs:string, $form-content as element()*,$form_name as xs:string*,$element as element()*,$element2 as element()*,$element3 as element()*,$element4 as element()*,$element5 as element()*,$element6 as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="{$form_name}" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
      {$form-content}
      </form>
      <table>
         <td><font size="2">Items Selected/Created :</font></td> 
         <td>{$element}</td>
         <td>{$element2}</td>
         <td>{$element3}</td>
         <td>{$element4}</td>
         <td>{$element5}</td>
         <td>{$element6}</td>
      </table>
      </div>
      return lib-rendering:txfrm-webpage($title, $content)
};
declare function lib-forms:wrap-form-name-contents($title as xs:string,$form_name as xs:string, $form-content as element()*) as node()*
{
   let $content as node() :=
      <div xmlns="http://www.w3.org/1999/xhtml">
      <form name="newDataElementWizard_cd" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}" enctype="multipart/form-data">
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
   for $classifier at $classifier-seq in $admin-item//openMDR:classified_by
   for $classification_item in lib-util:mdrElements('classification_scheme_item')
   where data($classification_item//@classification_scheme_item_identifier) = data($classifier/.)
   return
         <tr>
            <td class="left_spacer_cell"/>   
            <td>{$classifier-seq}</td>
            <td>{data($classifier/.)}{lib-forms:hidden-element('classified-by', data($classifier/.))}</td>
            <td>{data($classification_item//openMDR:classification_scheme_item_value)}</td>
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


declare function lib-forms:edit-admin-item(
   $reg-auth as xs:string?,
   $administrative-note  as xs:string?,
   $administrative-status  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by  as xs:string?,
   $registered-by  as xs:string?,
   $context-ids as xs:string*,
   $country-identifiers as xs:string*,
   $language-identifiers as xs:string*,
   $names as xs:string*,
   $definitions as xs:string*,
   $sources as xs:string*,
   $preferred as xs:string?,
   $action as xs:string?,
   (:1111111111111111111111111111111:)
   $version,
   $registration_status
){
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
   let $test as xs:string? := ""
   return
   
<table class="section">
   <tr><td class="row-header-cell" colspan="6">Standard Administered Item Metadata</td></tr>
   <tr>
   <td>
   <div class="tabber">
     <div id='validate'class="tabbertab">
         <h2>Administered Item Metadata</h2>
         <p>
            <table class="section">
                <tr><td class="left_header_cell">Registration Authority <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registration-authority($reg-auth)} </td></tr>
                <tr><td class="left_header_cell">Version</td><td colspan="5"> {$version}{lib-forms:radio('label',$version,'true')} </td></tr>
                <tr><td class="left_header_cell">Registered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registered_by($registered-by)} </td></tr>
                <tr><td class="left_header_cell">Administered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-administered_by-nameAndOrg($administered-by)} </td></tr>
                <tr><td class="left_header_cell">Submitted by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-submitted_by-nameAndOrg($submitted-by)} </td></tr>
                <tr><td class="left_header_cell">Administrative Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                <tr><td class="left_header_cell">Registration Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Registration_Status','registration_status', false(), $registration_status)}</td></tr>
                <tr><td class="left_header_cell">Administrative Note</td><td colspan="5">{lib-forms:text-area-element('administrative-note', 5, 70,$administrative-note)}</td></tr>        
            </table>
          </p>
     </div>
        {
        if($names > '') then (
        
     
        for $name at $pos in $names
        let $location := (if($pos > $skip-name-index and $skip-name-index > 0) then (util:eval($pos - 1)) else ($pos))
        where $pos != $skip-name-index and $name > ""
        return
        (
        <div class="section">
          <!--<h2>Preferred Name {$location}</h2>-->
          <p>
              <table class="section">
                <tr><td class="row-header-cell" colspan="6">Preferred Name</td></tr>
                <tr>
                   <td class="left_header_cell">Context <font color="red">*</font></td>
                   <td colspan="5">
                      {lib-forms:select-from-contexts-enum('context-ids',$context-ids[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Name <font color="red">*</font></td>
                   <td colspan="5">
                      {lib-forms:input-element('names',70,$name)}
                   </td>
                </tr>
        
                <tr>
                   <td class="left_header_cell">Preferred</td>
                   <td>
                   <!--
                      {lib-forms:radio('preferred', xs:string($pos), xs:string(($preferred = xs:string($pos))))}
                   -->
                      {lib-forms:radio('preferred', xs:string($pos), 'true')}
                   
                   </td>
                </tr>
        
        
                <tr>
                   <td class="left_header_cell">Definition</td>
                   <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, $definitions[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Language Identifier</td>
                   <td colspan="5">
                      {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(), $country-identifiers[$pos])}
                      {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), $language-identifiers[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Source</td>
                   <td colspan="5">{lib-forms:input-element('sources',70,$sources[$pos])}</td>
                </tr>
            
                <!-- commenting this as we do not support adding another name
                <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button(concat('delete naming entry ',$location), 'action' ,'')}</td></tr>
                <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add another name', 'action' ,'')}</td></tr>-->
             </table>
          </p>
       </div>
       )
       
        
        
        ) else (),
        
        if((($names > '') != xs:boolean('true')) or $action = 'add another name')  then (
          <div class="section">
          
          <!--{
          if($names > '') then (
            <h2>New Naming Entry</h2>
           ) else (
            <h2>Preferred Name</h2>
           )
          }-->
              <p>
                  <table class="section">
                    {
                     if($names > '') then(
                        <tr><td class="row-header-cell" colspan="6">New Naming Entry</td></tr>
                      )else(
                       <tr><td class="row-header-cell" colspan="6">Preferred Name</td></tr>
                      )
                    }
                    <tr>
                       <td class="left_header_cell">Context <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:select-from-contexts-enum('context-ids','')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Name <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:input-element('names',70,'')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Preferred</td>
                       <td>
                          {lib-forms:radio('preferred', '1', 'true')}
                       </td>
                    </tr>
            
                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, '')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(),'US')}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), 'eng')}
                      </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources',70,'')}</td>
                    </tr>
        <!--
        {
        if($names > '') then (
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add this new name', 'action' ,'')}</td></tr>
        ) else (
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add another name', 'action' ,'')}</td></tr>
        )
        }
        -->
              </table>
          </p>
      </div>
      ) else ()
      
      
      }
      
  </div>
  </td>
  </tr>          

</table>
};


declare function lib-forms:edit-admin-item(
   $reg-auth as xs:string?,
   $administrative-note  as xs:string?,
   $administrative-status  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by  as xs:string?,
   $registered-by  as xs:string?,
   $action as xs:string?
){
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
   return
   
<table class="section">
   <tr><td class="row-header-cell" colspan="6">Standard Administered Item Metadata</td></tr>
   <tr>
   <td>
   <div class="tabber">
     <div class="tabbertab">
         <h2>Administered Item Metadata</h2>
         <p>
            <table class="section">
                <tr><td class="left_header_cell">Registration Authority</td><td colspan="5"> {lib-forms:make-select-registration-authority($reg-auth)} </td></tr>
                <tr><td class="left_header_cell">Registered by</td><td colspan="5"> {lib-forms:make-select-registered_by($registered-by)} </td></tr>
                <tr><td class="left_header_cell">Administered by</td><td colspan="5"> {lib-forms:make-select-administered_by($administered-by)} </td></tr>
                <tr><td class="left_header_cell">Submitted by</td><td colspan="5"> {lib-forms:make-select-submitted_by($submitted-by)} </td></tr>
                <tr><td class="left_header_cell">Administrative Status</td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                <tr><td class="left_header_cell">Administrative Note</td><td colspan="5">{lib-forms:text-area-element('administrative-note', 5, 70,$administrative-note)}</td></tr>
            </table>
          </p>
     </div>
        {
        if($names > '') then (
        
     
        for $name at $pos in $names
        let $location := (if($pos > $skip-name-index and $skip-name-index > 0) then (util:eval($pos - 1)) else ($pos))
        where $pos != $skip-name-index and $name > ""
        return
        (
        <div class="tabbertab">
          <h2>Naming {$location}</h2>
          <p>
              <table class="section">

                <tr>
                   <td class="left_header_cell">Context</td>
                   <td colspan="5">
                      {lib-forms:select-from-contexts-enum('context-ids',$context-ids[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Name <font color="red">*</font></td>
                   <td colspan="5">
                      {lib-forms:input-element('names',70,$name)}
                   </td>
                </tr>
        
                <tr>
                   <td class="left_header_cell">Preferred</td>
                   <td>
                   <!--
                      {lib-forms:radio('preferred', xs:string($pos), xs:string(($preferred = xs:string($pos))))}
                   -->
                      {lib-forms:radio('preferred', xs:string($pos), 'true')}
                   </td>
                </tr>
        
        
                <tr>
                   <td class="left_header_cell">Definition</td>
                   <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, $definitions[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Language Identifier</td>
                   <td colspan="5">
                      {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(), $country-identifiers[$pos])}
                      {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), $language-identifiers[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Source</td>
                   <td colspan="5">{lib-forms:input-element('sources',70,$sources[$pos])}</td>
                </tr>
            
                <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button(concat('delete naming entry ',$location), 'action' ,'')}</td></tr>
                <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add another name', 'action' ,'')}</td></tr>
             </table>
          </p>
       </div>
       )
       
        
        
        ) else (),
        
        if((($names > '') != xs:boolean('true')) or $action = 'add another name')  then (
          <div class="tabbertab">
          {
          if($names > '') then (
            <h2>New Naming Entry</h2>
           ) else (
            <h2>Name 1</h2>
           )
          }
              <p>
                  <table class="section">
              
                    <tr>
                       <td class="left_header_cell">Context</td>
                       <td colspan="5">
                          {lib-forms:select-from-contexts-enum('context-ids','')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Name <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:input-element('names',70,'')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Preferred</td>
                       <td>
                          {lib-forms:radio('preferred', '1', '')}
                       </td>
                    </tr>
            
                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, '')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(),'')}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), '')}
                      </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources',70,'')}</td>
                    </tr>
        {
        if($names > '') then (
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add this new name', 'action' ,'')}</td></tr>
        ) else (
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add another name', 'action' ,'')}</td></tr>
        )
        }
        
              </table>
          </p>
      </div>
      ) else ()
      
      
      }
      
  </div>
  </td>
  </tr>          

</table>
};

(: edit object class adding this for edit object class:)
declare function lib-forms:edit-admin-item-edit(
 $reg-auth as xs:string?,
   $administrative-note  as xs:string?,
   $administrative-status  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by  as xs:string?,
   $registered-by  as xs:string?,
   $context-ids as xs:string*,
   $country-identifiers as xs:string*,
   $language-identifiers as xs:string*,
   $names as xs:string*,
   $definitions as xs:string*,
   $sources as xs:string*,
   $preferred as xs:string?,
   $action as xs:string?,
   
   $version as xs:float?,
   $registration_status as xs:string?
){
 let $proposedNextVersion := $version + 0.1
 let $proposedReleaseVersion := ceiling($proposedNextVersion)
 
 let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
   let $test as xs:string? := ""
   return
   
<table class="section">
   <tr><td class="row-header-cell" colspan="6">Standard Administered Item Metadata</td></tr>
   <tr>
   <td>
   <div class="tabber">
     <div id='validate'class="tabbertab">
         <h2>Administered Item Metadata</h2>
         <p>
            <table class="section">
                <tr><td class="left_header_cell">Registration Authority <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registration-authority($reg-auth)} </td></tr>
                <tr><td class="left_header_cell">Existing Version</td><td colspan="5"> {$version}{lib-forms:radio('version',string($version),'true')} </td></tr>
                <tr><td class="left_header_cell">Proposed/Release Version</td><td colspan="5"> {round-half-to-even(xs:float($proposedNextVersion),2)}{lib-forms:radio('proposedNextVersion',string($proposedNextVersion),'true')}  {$proposedReleaseVersion}{lib-forms:radio('proposedNextVersion',string($proposedReleaseVersion),'false')}</td></tr>
                <tr><td class="left_header_cell">Registered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-registered_by($registered-by)} </td></tr>
                <tr><td class="left_header_cell">Administered by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-administered_by-nameAndOrg($administered-by)} </td></tr>
                <tr><td class="left_header_cell">Submitted by <font color="red">*</font></td><td colspan="5"> {lib-forms:make-select-submitted_by-nameAndOrg($submitted-by)} </td></tr>
                <tr><td class="left_header_cell">Administrative Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                <tr><td class="left_header_cell">Registration Status <font color="red">*</font></td><td colspan="5">{lib-forms:select-from-simpleType-enum('Registration_Status','registration_status', false(), $registration_status)}</td></tr>
                <tr><td class="left_header_cell">Administrative Note</td><td colspan="5">{lib-forms:text-area-element('administrative-note', 5, 70,$administrative-note)}</td></tr>        
            </table>
          </p>
     </div>
        {
        if($names > '') then (
        
     
        for $name at $pos in $names
        let $location := (if($pos > $skip-name-index and $skip-name-index > 0) then (util:eval($pos - 1)) else ($pos))
        where $pos != $skip-name-index and $name > ""
        return
        (
        <div class="section">
        <!--<div class="tabbertab">
          <h2>Preferred Name {$location}</h2>-->
          <p>
              <table class="section">
                <tr><td class="row-header-cell" colspan="6">Preferred Name</td></tr>
                <tr>
                   <td class="left_header_cell">Context <font color="red">*</font></td>
                   <td colspan="5">
                      {lib-forms:select-from-contexts-enum('context-ids',$context-ids[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Name <font color="red">*</font></td>
                   <td colspan="5">
                      {lib-forms:input-element('names',70,$name)}
                   </td>
                </tr>
        
                <tr>
                   <td class="left_header_cell">Preferred</td>
                   <td>
                   <!--
                      {lib-forms:radio('preferred', xs:string($pos), xs:string(($preferred = xs:string($pos))))}
                   -->
                      {lib-forms:radio('preferred', xs:string($pos), 'true')}
                   
                   </td>
                </tr>
        
        
                <tr>
                   <td class="left_header_cell">Definition</td>
                   <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, $definitions[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Language Identifier</td>
                   <td colspan="5">
                      {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(), $country-identifiers[$pos])}
                      {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), $language-identifiers[$pos])}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Source</td>
                   <td colspan="5">{lib-forms:input-element('sources',70,$sources[$pos])}</td>
                </tr>
            
                <!-- disabling this feature to add additional name
                <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button(concat('delete naming entry ',$location), 'action' ,'')}</td></tr>
                <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add another name', 'action' ,'')}</td></tr>-->
             </table>
          </p>
       </div>
       )
       
        
        
        ) else (),
        
        if((($names > '') != xs:boolean('true')) or $action = 'add another name')  then (
          <div class="section">
          <!--<div class="tabbertab">
          {
          if($names > '') then (
            <h2>New Naming Entry</h2>
           ) else (
            <h2>Preferred Name</h2>
           )
          }-->
              <p>
                  <table class="section">
                     {
                        if($names > '') then (
                          <tr><td class="row-header-cell" colspan="6">New Naming Entry</td></tr>
                         ) else (
                          <tr><td class="row-header-cell" colspan="6">Preferred Name</td></tr>
                         )
                     }
                    <tr>
                       <td class="left_header_cell">Context <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:select-from-contexts-enum('context-ids','')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Name <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:input-element('names',70,'')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Preferred</td>
                       <td>
                          {lib-forms:radio('preferred', '1', 'true')}
                       </td>
                    </tr>
            
                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions', 5, 70, '')}
                       </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers', false(),'')}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers', false(), '')}
                      </td>
                    </tr>
                    
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources',70,'')}</td>
                    </tr>
        <!--
        {
        if($names > '') then (
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add this new name', 'action' ,'')}</td></tr>
        ) else (
                    <tr><td class="left_header_cell"/><td colspan="5">{lib-forms:action-button('add another name', 'action' ,'')}</td></tr>
        )
        }
        -->
              </table>
          </p>
      </div>
      ) else ()
      
      
      }
      
  </div>
  </td>
  </tr>          

</table>

};


declare function lib-forms:edit-admin-item-only(
   $reg-auth as xs:string?,
   $administrative-note  as xs:string?,
   $administrative-status  as xs:string?,
   $administered-by  as xs:string?,
   $submitted-by  as xs:string?,
   $registered-by  as xs:string?,
   $action as xs:string?
){
   let $skip-name := substring-after($action,'delete naming entry')
   let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
   return
   
<table class="section">
   <tr><td class="row-header-cell" colspan="6">Standard Administered Item Metadata</td></tr>
   <tr>
   <td>
   <div class="tabber">
     <div class="tabbertab">
         <h2>Administered Item Metadata</h2>
         <p>
            <table class="section">
                <tr><td class="left_header_cell">Registration Authority</td><td colspan="5"> {lib-forms:make-select-registration-authority($reg-auth)} </td></tr>
                <tr><td class="left_header_cell">Registered by</td><td colspan="5"> {lib-forms:make-select-registered_by($registered-by)} </td></tr>
                <tr><td class="left_header_cell">Administered by</td><td colspan="5"> {lib-forms:make-select-administered_by($administered-by)} </td></tr>
                <tr><td class="left_header_cell">Submitted by</td><td colspan="5"> {lib-forms:make-select-submitted_by($submitted-by)} </td></tr>
                <tr><td class="left_header_cell">Administrative Status</td><td colspan="5">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(), $administrative-status)}</td></tr>
                <tr><td class="left_header_cell">Administrative Note</td><td colspan="5">{lib-forms:text-area-element('administrative-note', 5, 70,$administrative-note)}</td></tr>
            </table>
          </p>
     </div>
  </div>
  </td>
  </tr>          

</table>
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
         for $ref-doc at $ref-doc-seq in $admin-item//openMDR:described_by
         let $ref-doc-id := data($ref-doc/.) 
         let $document := lib-util:mdrElement('reference_document',$ref-doc)
         let $title := data($document//openMDR:reference_document_title)
         let $lang := data($document//openMDR:reference_document_language_identifier)
         let $type := data($document//openMDR:reference_document_type_description)
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
      for $having at $having-id in $admin-item//openMDR:having
      for $containing at $containing-id in $having/openMDR:containing
      let $pos := max($containing-id) * ($having-id - 1) + $containing-id
      return
      (
      <tr>
         <td class="left_spacer_cell"/>
         <td>{$pos}</td>
         <td>{lib-forms:make-select-admin-item('context','context',$having/openMDR:context_identifier)}</td>
         <td>{lib-forms:input-element('name',97,data($containing/openMDR:name))}</td>
         <td>{lib-forms:select-from-simpleType-enum('Country_Identifier','name-country-identifier', false(), data($containing//openMDR:country_identifier))}</td> 
         <td>{lib-forms:select-from-simpleType-enum('Language_Identifier','name-language-identifier', false(),data($containing//openMDR:language_identifier))}</td>
         <td>{lib-forms:radio('preferred', data($pos), data($containing/openMDR:preferred_designation))}</td>
         <td>{lib-forms:action-button($lib-forms:action-name-delete, $lib-forms:action-name-control-name, $pos),lib-forms:action-button($lib-forms:action-update-body, 'action' ,0)}</td></tr>,
         <tr>
         <td class="left_spacer_cell"/>
         <td/>
         <td colspan="6">{lib-forms:text-area-element('definition',5,76,data($containing/openMDR:definition_text))}</td>
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
      element openMDR:classified_by {$classified-by}
      ),

   if (empty($classified-by-new)) then ()
   else (
      element openMDR:classified_by {$classified-by-new}   
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
   element openMDR:administered_item_administration_record {
      element openMDR:administrative_note {$administrative-note},
      element openMDR:administrative_status {$administrative-status},
      element openMDR:change_description {$change-description},
      element openMDR:creation_date {$creation-date},
      element openMDR:effective_date {$effective-date},
      element openMDR:explanatory_comment {$explanatory-comment},
      element openMDR:last_change_date {$last-change-date},
      element openMDR:origin {$origin},
      element openMDR:registration_status {$registration-status},
      element openMDR:unresolved_issue {$unresolved-issue}
   },
   element openMDR:administered_by {$administered-by},
   element openMDR:registered_by {$registered-by}, 
   element openMDR:submitted_by {$submitted-by}
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
   else (element openMDR:described_by{$ref-doc}),
   
   if (empty($ref-doc-new)) 
   then ()
   else (element openMDR:described_by{$ref-doc-new})
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
            element openMDR:having {
               element openMDR:context_identifier {$having},
               element openMDR:containing {
                  element openMDR:language_section_language_identifier {
                     element openMDR:country_identifier {$name-country-identifier[$having-seq]},
                     element openMDR:language_identifier {$name-language-identifier[$having-seq]}
                  },
                  element openMDR:name {$name},
                  element openMDR:definition_text {$definition[$having-seq]},
                  element openMDR:preferred_designation {
                     if ($preferred='new') then(false())
                     else(
                        if ($preferred = $having-seq) then (true()) else (false()))
                        },
                        
                  element openMDR:definition_source_reference {$definition-src[$having-seq]}
               }
            }
         ),
         if ($add-name = true()) then (
            element openMDR:having {
               element openMDR:context_identifier {$context-new},
               element openMDR:containing {
                  element openMDR:language_section_language_identifier {
                     element openMDR:country_identifier {$name-country-identifier-new},
                     element openMDR:language_identifier {$name-language-identifier-new}
                  },
                  element openMDR:name {$name-new},
                  element openMDR:definition_text {$definition-new},
                  element openMDR:preferred_designation {if ($preferred = 'new') then (true()) else (false())},
                  element openMDR:definition_source_reference {$definition-src-new}
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
         element openMDR:related_to {
            element openMDR:value_domain_relationship_type_description {$how-related[$related-to-seq]},
            element openMDR:related_to {$related-to}
            }
      ),
   if ($add-related) then (
      element openMDR:related_to {
         element openMDR:value_domain_relationship_type_description {$relationship-type-new},
         element openMDR:related_to {$related-to-new}
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
   element openMDR:typed_by {$representation-class},
   element openMDR:value_domain_datatype {$data-type},
   if ($max-char-qty) then (element openMDR:value_domain_maximum_character_quantity {$max-char-qty}) else (),
   element openMDR:value_domain_format {$format}, 
   element openMDR:value_domain_unit_of_measure {$uom}
};

declare function lib-forms:value-domain-common($admin-item as node()) as node()*
{   
   <table class="layout">
   <tr><td class="left_header_cell" colspan="3">value domain common</td></tr>
   <tr><td class="left_spacer_cell"/><td class="left_header_cell">typed by representation class</td><td>{lib-forms:make-select-admin-item('representation_class','representation-class', data($admin-item//openMDR:typed_by))}</td></tr>
   <tr><td/><td class="left_header_cell">data type</td><td>{lib-forms:make-select-datatype(data($admin-item//openMDR:value_domain_datatype))}</td></tr>
   <tr><td/><td class="left_header_cell">unit of measure</td><td>{lib-forms:make-select-uom(data($admin-item//openMDR:value_domain_unit_of_measure))}</td></tr>
   <tr><td/><td class="left_header_cell">maximum character quantity</td><td>{lib-forms:input-element('max-char-qty',97,data($admin-item//openMDR:value_domain_maximum_character_quantity))}</td></tr>
   <tr><td/><td class="left_header_cell">format</td><td>{lib-forms:input-element('format',97,data($admin-item//openMDR:value_domain_format))}</td></tr>
   <tr><td/><td colspan="2">{lib-forms:action-button($lib-forms:action-update-body, 'action', 0),lib-forms:reset-button()}</td></tr>
   </table>,
   <br/>
};

declare function lib-forms:concept-domain-common($dimensionality as xs:string?) as node()
{
   element openMDR:dimensionality {$dimensionality}
};

declare function lib-forms:edit-conceptual-domain($admin-item as node()) as node()*
{
   <table class="layout">
   <tr><td class="left_header_cell" colspan="3">non-enumerated value domain specific</td></tr>
   <tr><td class="left_spacer_cell"/><td class="left_header_cell">dimensionality</td><td>{lib-forms:input-element('dimensionality',97,data($admin-item//openMDR:dimensionality))}</td></tr>
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
      for $related-to at $related-to-seq in $admin-item//openMDR:related_to[openMDR:related_to]
      return
         (
         <tr>
         <td/>
         <td>{$related-to-seq}</td>
         <td>{string($related-to/openMDR:related_to)}</td>
         <td>{lib-forms:make-select-admin-item('value_domain', 'related-item', string($related-to/openMDR:related_to))}</td>
         <td>{lib-forms:select-from-simpleType-enum('relationship_type_name','relationship',false(),string($related-to/openMDR:value_domain_relationship_type_description))}</td>
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
   
   let $resource_location := concat(lib-util:getCollectionPath('reference_document'),'/documents')
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

(:added this Function to store the annotated models in the right location:)
declare function lib-forms:store-annotated-model($document as item(), $doc-name as xs:string, 
         $mime-type as xs:string) as xs:string
{
   let $user := session:get-attribute("username")
   let $password := session:get-attribute("password")
   let $is-ssl :=  lib-util:checkSSL()
   
   let $resource_location := concat(lib-util:getCollectionPath('models'),'/documents')
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
            element message{concat("Document not stored: ", "")},
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
   element message{concat("Document not stored: ", "")},
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
      attribute value {'select relationship'},
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


declare function lib-forms:popup-form-search($type as xs:string, $control as xs:string, 
$form-name as xs:string, $button-name as xs:string) as element() 
{
   element input {
      attribute type {'submit'},
      attribute name {"update"},
      attribute value {$button-name},
      attribute class {'cgButton'},
      attribute onclick {
         concat("window.open('popup-search-relationship.xquery?control=",
            $control, 
            "&amp;type=",
            $type,
            "&amp;form-name=",
            $form-name,
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
       [data(./openMDR:association/@associationTarget) = $item_id]
    let $item_identifier := data($item//@classification_scheme_item_identifier)
    let $item_value := data($item//openMDR:classification_scheme_item_value)
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
      [.//openMDR:classified_by=$item_id]
      [.//openMDR:administered_item_administration_record/openMDR:registration_status!='Superseded']
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
    let $version as xs:string := lib-forms:substring-after-last($admin-item-identifier, '_')
    let $new-version as xs:string :=
       if (matches($version,'.\..\..'))
       then (       
            concat(substring-before($version, '.'), '.', xs:string(xs:double(substring-after($version,"."))+0.01))
           )
       
       else xs:string(floor(xs:double($version)*100 + 1) div 100) 
             
    return
        concat(lib-forms:substring-before-last($admin-item-identifier, '_'), '_', $new-version)
};

