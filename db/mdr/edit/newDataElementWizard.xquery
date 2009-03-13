xquery version "1.0";

(: ~
 : Module Name:             new vanilla data element
 :
 : Module Version           1.0
 :
 : Date                               25th October 2006
 :
 : Copyright                       The cancergrid consortium
 :
 : Module overview          creates a data element through a wizard format
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
    
import module namespace 
    lib-make-admin-item="http://www.cancergrid.org/xquery/library/make-admin-item" 
    at "../edit/m-lib-make-admin-item.xquery";      
    
import module namespace 
   lib-supersede="http://www.cancergrid.org/xquery/library/supersede"
   at "../edit/m-lib-supersede.xquery";
   
import module namespace 
   administered-item="http://www.cancergrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    
   
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace validation="http://exist-db.org/xquery/validation";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";

(: wizard pages :)

declare function local:page-button($button-text as xs:string) as element(input)
{
   <input type="submit" name="move" value="{$button-text}" class="cgButton"/>
};



declare function local:find-concept-id(
         $control-name as xs:string,
         $button-label as xs:string
         ) as element()*
{
    let $javascript as xs:string := 
        concat("window.open('popup-search-reference-uri.xquery?element=", $control-name, 
            "','Popup_Window','resizable=yes,width=1100,height=400,menubar=no,left=100,top=100')")
    return
        (lib-forms:input-element($control-name, 70, request:get-parameter($control-name,'')),
            element div { attribute id {concat($control-name, '-div')} },
            element input {
                attribute type {'button'},
                attribute value {$button-label},
                attribute class {'cgButton'},
                attribute onclick {$javascript}            
            })
      
};

declare function local:associated-refdocs($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 6 - reference documents"
   let $content as node()* := 
            (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
            <tr><td class="left_header_cell"/><td width="40%">{local:page-button("page 5")}</td><td>{local:page-button("page 7")}</td></tr>
            <tr><td class="left_header_cell">Reference document 1</td><td colspan="2"> {lib-forms:make-select-refdoc('refdoc1', request:get-parameter('refdoc1',''))} </td></tr>
            <tr><td class="left_header_cell">Reference document 2</td><td colspan="2"> {lib-forms:make-select-refdoc('refdoc2', request:get-parameter('refdoc2',''))} </td></tr>
            <tr><td class="left_header_cell">Reference document 3</td><td colspan="2"> {lib-forms:make-select-refdoc('refdoc3', request:get-parameter('refdoc3',''))} </td></tr>
            </table>
            {local:hidden-controls-page1(),local:hidden-controls-page2(), local:hidden-controls-page3(), local:hidden-controls-page4(), local:hidden-controls-page5e(),local:hidden-controls-page5n()}

            </div>
             )
             
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-page6()
{
lib-forms:hidden-element('refdoc1', request:get-parameter('refdoc1','')),
lib-forms:hidden-element('refdoc2',request:get-parameter('refdoc2','')),
lib-forms:hidden-element('refdoc3',request:get-parameter('refdoc3',''))
};

declare function local:admin-item-details($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 1 - common administrative item details"
   let $content as node()* := 
            (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
            <tr><td class="left_header_cell">Navigation</td><td width="40%"></td><td>{local:page-button("page 2")}</td></tr>
            <tr><td class="left_header_cell">Registration Authority</td><td colspan="2"> {lib-forms:make-select-registration-authority(request:get-parameter('registration-authority',''))} </td></tr>
            <tr><td class="left_header_cell">Registered by</td><td colspan="2"> {lib-forms:make-select-registered_by(request:get-parameter('registered-by',''))} </td></tr>
            <tr><td class="left_header_cell">Administered by</td><td colspan="2"> {lib-forms:make-select-administered_by(request:get-parameter('administered-by',''))} </td></tr>
            <tr><td class="left_header_cell">Submitted by</td><td colspan="2"> {lib-forms:make-select-submitted_by(request:get-parameter('submitted-by',''))} </td></tr>
            <tr><td class="left_header_cell">Administrative Status</td><td colspan="2">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(),request:get-parameter('administrative-status',''))}</td></tr>
            <tr><td class="left_header_cell">Administrative Note</td><td colspan="2">{lib-forms:text-area-element('administrative-note', 5, 70, request:get-parameter('administrative-note',''))}</td></tr>
            </table>
            {local:hidden-controls-page2(), 
            local:hidden-controls-page3(), 
            local:hidden-controls-page4(), 
            local:hidden-controls-page5e(),
            local:hidden-controls-page5n(),
            local:hidden-controls-page6()}

            </div>
             )
             
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-page1()
{
lib-forms:hidden-element('registration-authority', request:get-parameter('registration-authority','')),
lib-forms:hidden-element('registered-by',request:get-parameter('registered-by','')),
lib-forms:hidden-element('administered-by',request:get-parameter('administered-by','')),
lib-forms:hidden-element('submitted-by',request:get-parameter('submitted-by','')),
lib-forms:hidden-element('administrative-status', request:get-parameter('administrative-status','')),
lib-forms:hidden-element('administrative-note', request:get-parameter('administrative-note',''))
};


declare function local:data-element-details($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 2 - data element details"
   let $content as node()* := 
            (
          <div xmlns="http://www.w3.org/1999/xhtml">
         <table class="layout">
         <tr><td class="left_header_cell"/><td width="40%">{local:page-button("page 1")}</td><td>{local:page-button("page 3")}</td></tr>
         <tr><td class="left_header_cell">Preferred Name</td><td colspan="2">{lib-forms:input-element('name', 70, request:get-parameter('name',''))}</td></tr>
         <tr><td class="left_header_cell">Preferred Definition</td><td colspan="2">{lib-forms:text-area-element('definition', 5, 70, request:get-parameter('definition',''))}</td></tr>
         <tr><td class="left_header_cell">Context of preferred name</td><td colspan="5"> {lib-forms:make-select-admin-item('context','preferred_name_context',request:get-parameter('preferred_name_context',''))} </td></tr>
         <tr>
            <td class="left_header_cell">Language Identifier for preferred name</td><td colspan="2">
               {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifier', false(), request:get-parameter('country-identifier',''))}
               {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifier', false(),request:get-parameter('language-identifier',''))}
             </td>
         </tr>         
         <tr><td class="left_header_cell">Alternate Name</td><td colspan="2">{lib-forms:input-element('alt-name', 70,request:get-parameter('alt-name',''))}</td></tr>
         <tr><td class="left_header_cell">Context of alternate name</td><td colspan="5"> {lib-forms:make-select-admin-item('context','alt_name_context',request:get-parameter('alt_name_context',''))} </td></tr>
         <tr>
            <td class="left_header_cell">Language Identifier for alternate name</td><td colspan="2">
               {lib-forms:select-from-simpleType-enum('Country_Identifier','alt-country-identifier', false(), request:get-parameter('alt-country-identifier',''))}
               {lib-forms:select-from-simpleType-enum('Language_Identifier','alt-language-identifier', false(), request:get-parameter('alt-language-identifier',''))}
             </td>
          </tr>
         <tr><td class="left_header_cell">Representation Class</td><td colspan="5">{lib-forms:make-select-admin-item('representation_class','representation-class', request:get-parameter('representation-class',''))} </td></tr>
         <tr><td class="left_header_cell">Example</td><td colspan="2">{lib-forms:text-area-element('example', 5, 70, request:get-parameter('example',''))}</td></tr>
         <tr><td class="left_header_cell">Precision</td><td colspan="2">{lib-forms:input-element('precision', 70,request:get-parameter('precision','0'))}</td></tr>
         <tr><td></td></tr>
         </table>
         {
         local:hidden-controls-page1(), 
         local:hidden-controls-page3(), 
         local:hidden-controls-page5e(), 
         local:hidden-controls-page4(),
         local:hidden-controls-page5n(),
         local:hidden-controls-page6()
         }
           </div>
            )
   return lib-forms:wrap-form-contents($title, $content)            
};

declare function local:hidden-controls-page2()
{
lib-forms:hidden-element('name', request:get-parameter('name','')),
lib-forms:hidden-element('definition', request:get-parameter('definition','')),
lib-forms:hidden-element('preferred_name_context',request:get-parameter('referred_name_context','')),
lib-forms:hidden-element('country-identifier', request:get-parameter('country-identifier','')),
lib-forms:hidden-element('language-identifier', request:get-parameter('language-identifier','')),
lib-forms:hidden-element('alt-name', request:get-parameter('alt-name','')),
lib-forms:hidden-element('alt_name_context',request:get-parameter('alt_name_context','')),
lib-forms:hidden-element('alt-country-identifier', request:get-parameter('alt-country-identifier','')),
lib-forms:hidden-element('alt-language-identifier', request:get-parameter('alt-language-identifier','')),
lib-forms:hidden-element('representation-class', request:get-parameter('representation-class','')),
lib-forms:hidden-element('example', request:get-parameter('example','')),
lib-forms:hidden-element('precision', request:get-parameter('precision',''))
};


declare function local:data-element-concept($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 3 - Data Element Concept"
   let $content as node()* := 
            (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr>
                   <td class="left_header_cell"/><td width="40%">{local:page-button("page 2")}</td>
                   <td>{local:page-button("page 4")}</td>
               </tr>
               <tr>
                  <td class="left_header_cell">Object Class URI</td>
                  <td colspan="2">
                     {local:find-concept-id('object_class_uri','get object class concept')}
                  </td>
              </tr>
              <tr>
                  <td class="left_header_cell"> </td>
                  <td colspan="2"><i> or select existing </i> {lib-forms:make-select-admin-item('object_class','object_class_id', request:get-parameter('object_class_id',''))}
                  </td>
               </tr>
               <tr>
                  <td class="left_header_cell">Property URI</td>
                  <td colspan="2">
                     {local:find-concept-id('property_uri','get property concept')}
                  </td>
              </tr>
              <tr>
                  <td class="left_header_cell"> </td>
                  <td colspan="2"><i> or select existing </i> 
                     {lib-forms:make-select-admin-item('property','property_id', request:get-parameter('property_id',''))}
                  </td>
               </tr>
            </table>
                        {
                        local:hidden-controls-page1(), 
                        local:hidden-controls-page2(), 
                        local:hidden-controls-page5e(), 
                        local:hidden-controls-page4(),
                        local:hidden-controls-page5n(),
                        local:hidden-controls-page6()}
            </div>
             )
             
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-page3()
{
lib-forms:hidden-element('object_class_uri', request:get-parameter('object_class_uri','')), 
lib-forms:hidden-element('property_uri', request:get-parameter('property_uri','')),
lib-forms:hidden-element('property_id', request:get-parameter('property_id','')),
lib-forms:hidden-element('object_class_id', request:get-parameter('object_class_id',''))
};

declare function local:value-domain-type($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 4 - value domain type"
   let $content as node()* := 
      (
         <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr><td class="left_header_cell"/><td width="40%">{local:page-button("page 3")}</td><td>{local:page-button("page 5")}</td></tr>

               <tr>
                  <td class="left_header_cell">Value domain type</td>
                  <td colspan="2">
                  {
                  if (request:get-parameter('value-domain-type','') = "enumerated") then (
                     <input type="radio" name="value-domain-type" value="enumerated" checked="checked">enumerated</input>,
                     <input type="radio" name="value-domain-type" value="non-enumerated">non-enumerated</input>
                     )
                     else
                     (
                     <input type="radio" name="value-domain-type" value="enumerated">enumerated</input>,
                     <input type="radio" name="value-domain-type" value="non-enumerated" checked="checked">non-enumerated</input>
                     )
                  }
                  </td>
               </tr>

            </table>
            {local:hidden-controls-page1(), 
            local:hidden-controls-page2(),
            local:hidden-controls-page3(), 
            local:hidden-controls-page5e(),
            local:hidden-controls-page5n(),
            local:hidden-controls-page6()}
         </div>

   )
   return lib-forms:wrap-form-contents($title, $content)
};


declare function local:hidden-controls-page4()
{
lib-forms:hidden-element('value-domain-type', request:get-parameter('value-domain-type',''))
};

declare function local:value-domain-nonenum($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 5 - non-enumerated value domain details"
   let $content as node()* := 
      (
         <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr><td class="left_header_cell"/><td width="40%">{local:page-button("page 4")}</td><td>{local:page-button("page 6")}</td></tr>
               <tr><td class="left_header_cell">Datatype</td><td colspan="2">{lib-forms:make-select-datatype(request:get-parameter('datatype',''))}</td></tr>
               <tr><td class="left_header_cell">Unit of Measure</td><td colspan="2">{lib-forms:make-select-uom(request:get-parameter('uom',''))}</td></tr>               
            </table>
            {local:hidden-controls-page1(),
            local:hidden-controls-page2(),
            local:hidden-controls-page3(), 
            local:hidden-controls-page4(),
            local:hidden-controls-page6()}
         </div>
      )
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-page5n()
{
lib-forms:hidden-element('datatype',request:get-parameter('datatype','')),
lib-forms:hidden-element('uom',request:get-parameter('uom',''))               
};

declare function local:value-domain-enum($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 5 - enumerated value domain details"
   let $content as node()* := 
      (
         <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr><td class="left_header_cell"/><td width="40%">{local:page-button("page 4")}</td><td>{local:page-button("page 6")}</td></tr>
               <tr><td class="left_header_cell">Enumerations</td><td width="40%">value</td><td>meaning</td></tr>
               <tr>
                  <td class="left_header_cell"/>
                  <td>{lib-forms:input-element('value-1', 70, request:get-parameter('value-1',''))}</td>
                  <td>{lib-forms:input-element('meaning-1', 70, request:get-parameter('meaning-1',''))}</td>
               </tr>
               <tr>
                  <td class="left_header_cell"/>
                  <td>{lib-forms:input-element('value-2', 70, request:get-parameter('value-2',''))}</td>
                  <td>{lib-forms:input-element('meaning-2', 70, request:get-parameter('meaning-2',''))}</td>
               </tr>
               <tr>
                  <td class="left_header_cell"/>
                  <td>{lib-forms:input-element('value-3', 70, request:get-parameter('value-3',''))}</td>
                  <td>{lib-forms:input-element('meaning-3', 70, request:get-parameter('meaning-3',''))}</td>
               </tr>
               <tr>
                  <td class="left_header_cell"/>
                  <td>{lib-forms:input-element('value-4', 70, request:get-parameter('value-4',''))}</td>
                  <td>{lib-forms:input-element('meaning-4', 70, request:get-parameter('meaning-4',''))}</td>
               </tr>               
               <tr>
                  <td class="left_header_cell"/>
                  <td>{lib-forms:input-element('value-5', 70, request:get-parameter('value-5',''))}</td>
                  <td>{lib-forms:input-element('meaning-5', 70, request:get-parameter('meaning-5',''))}</td>
               </tr>               
               <tr>
                  <td class="left_header_cell"/>
                  <td>{lib-forms:input-element('value-6', 70, request:get-parameter('value-6',''))}</td>
                  <td>{lib-forms:input-element('meaning-6', 70, request:get-parameter('meaning-6',''))}</td>
               </tr>                     
               <tr>
                  <td class="left_header_cell"/>
                  <td>{lib-forms:input-element('value-7', 70, request:get-parameter('value-7',''))}</td>
                  <td>{lib-forms:input-element('meaning-7', 70, request:get-parameter('meaning-7',''))}</td>
               </tr>                     
               
            </table>
            {local:hidden-controls-page1(),
            local:hidden-controls-page2(),
            local:hidden-controls-page3(), 
            local:hidden-controls-page4(),
            local:hidden-controls-page6()}
         </div>
      )
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-page5e()
{
lib-forms:hidden-element('value-1', request:get-parameter('value-1','')),
lib-forms:hidden-element('meaning-1', request:get-parameter('meaning-1','')),
lib-forms:hidden-element('value-2', request:get-parameter('value-2','')),
lib-forms:hidden-element('meaning-2', request:get-parameter('meaning-2','')),
lib-forms:hidden-element('value-3', request:get-parameter('value-3','')),
lib-forms:hidden-element('meaning-3', request:get-parameter('meaning-3','')),
lib-forms:hidden-element('value-4', request:get-parameter('value-4','')),
lib-forms:hidden-element('meaning-4', request:get-parameter('meaning-4','')),
lib-forms:hidden-element('value-5', request:get-parameter('value-5','')),
lib-forms:hidden-element('meaning-5', request:get-parameter('meaning-5','')),
lib-forms:hidden-element('value-6', request:get-parameter('value-6','')),
lib-forms:hidden-element('meaning-6', request:get-parameter('meaning-6','')),
lib-forms:hidden-element('value-7', request:get-parameter('value-7','')),
lib-forms:hidden-element('meaning-7', request:get-parameter('meaning-7',''))
};

declare function local:execute() as node()
{

let $registration-authority :=request:get-parameter('registration-authority','')
let $registered-by := request:get-parameter('registered-by','')
let $administered-by := request:get-parameter('administered-by','')
let $submitted-by := request:get-parameter('submitted-by','')
let $administrative-status := request:get-parameter('administrative-status','')
let $administrative-note := request:get-parameter('administrative-note','')
let $name := request:get-parameter('name','')
let $definition := request:get-parameter('definition','')
let $preferred_name_context := request:get-parameter('preferred_name_context','')
let $country-identifier := request:get-parameter('country-identifier','')
let $language-identifier := request:get-parameter('language-identifier','')
let $alt-name := request:get-parameter('alt-name','')
let $alt_name_context := request:get-parameter('alt_name_context','')
let $alt-country-identifier := request:get-parameter('alt-country-identifier','')
let $alt-language-identifier := request:get-parameter('alt-language-identifier','')
let $representation-class := request:get-parameter('representation-class','')
let $example := request:get-parameter('example','')
let $object_class_uri := request:get-parameter('object_class_uri','') 
let $property_uri := request:get-parameter('property_uri','')
let $object_class_id := request:get-parameter('object_class_id','') 
let $property_id := request:get-parameter('property_id','')
let $value-domain-type :=request:get-parameter('value-domain-type','')
let $value-1 := request:get-parameter('value-1','')
let $meaning-1 := request:get-parameter('meaning-1','')
let $value-2 := request:get-parameter('value-2','')
let $meaning-2 := request:get-parameter('meaning-2','')
let $value-3 := request:get-parameter('value-3','')
let $meaning-3 := request:get-parameter('meaning-3','')
let $value-4 := request:get-parameter('value-4','')
let $meaning-4 := request:get-parameter('meaning-4','')
let $value-5 := request:get-parameter('value-5','')
let $meaning-5 := request:get-parameter('meaning-5','')
let $value-6 := request:get-parameter('value-6','')
let $meaning-6 := request:get-parameter('meaning-6','')
let $value-7 := request:get-parameter('value-7','')
let $meaning-7 := request:get-parameter('meaning-7','')
let $datatype := request:get-parameter('datatype','')
let $uom := request:get-parameter('uom','')     
let $precision := request:get-parameter('precision','0')
let $refdoc1 := request:get-parameter('refdoc1','')
let $refdoc2 := request:get-parameter('refdoc2','')
let $refdoc3 := request:get-parameter('refdoc3','')

let $version := '0.1'
let $data-identifier-de := lib-forms:generate-id()
let $data-identifier-vd := lib-forms:generate-id()
let $data-identifier-dec := lib-forms:generate-id()
let $data-identifier-cd := lib-forms:generate-id()
let $data-identifier-oc := lib-forms:generate-id()
let $data-identifier-pr := lib-forms:generate-id()

let $full-identifier-de := concat($registration-authority, '-', $data-identifier-de, '-', $version)
let $full-identifier-vd := concat($registration-authority, '-', $data-identifier-vd, '-', $version)
let $full-identifier-dec := concat($registration-authority, '-', $data-identifier-dec, '-', $version)
let $full-identifier-cd := concat($registration-authority, '-', $data-identifier-cd, '-', $version)
let $full-identifier-oc := concat($registration-authority, '-', $data-identifier-oc, '-', $version)
let $full-identifier-pr := concat($registration-authority, '-', $data-identifier-pr, '-', $version)

let $administration-record := lib-make-admin-item:administration-record(
        $administrative-note,
        $administrative-status,
        'recorded')

let $custodians := lib-make-admin-item:custodians(
        $administered-by,
        $registered-by,
        $submitted-by)
            
let $having-preferred := lib-make-admin-item:having(
        $preferred_name_context,
        $country-identifier,
        $language-identifier,
        $name,
        $definition,
        true(), ())

let $having-alt := lib-make-admin-item:having(
        $alt_name_context,
        $alt-country-identifier,
        $alt-language-identifier,
        $alt-name,
        (),
        false(), ())
            
let $described-by := (
   if ($refdoc1 > "")
   then (element cgMDR:described_by {$refdoc1})
   else(),
   if ($refdoc2 > "")
   then (element cgMDR:described_by {$refdoc2})
   else(),
   if ($refdoc3 > "")
   then (element cgMDR:described_by {$refdoc3})
   else()
)
            
let $object-class := 
   (
   element cgMDR:Object_Class {
        lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-oc,$version),
        $administration-record,
        $custodians,
        $having-preferred,   
        element cgMDR:reference_uri {$object_class_uri}
        }
   )
   
let $property := 
   (
    element cgMDR:Property {
        lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-pr,$version),
        $administration-record,
        $custodians,
        $having-preferred,   
        element cgMDR:reference_uri {$property_uri}
        }
   )
   
   let $enumerations :=
      (
      for $i in (1 to 7)
      let $vmd :=util:eval(concat("$meaning-",string($i)))
      let $pv := util:eval(concat("$value-",string($i)))
      let $vmi := lib-forms:generate-id()
      let $pvi := lib-forms:generate-id()
      return
         if ($vmd>"") then (
            element enumeration {
               element value-meaning-id {$vmi},
               element value-meaning-desc {$vmd},
               element permissible-value-id {$pvi},
               element permissible-value {$pv}
               }
         )
         else()
      )

   
let $conceptual-domain :=
      if ($value-domain-type = 'enumerated') then
      (   
      element cgMDR:Enumerated_Conceptual_Domain {
            lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-cd,$version),
            $administration-record,
            $custodians,
            $described-by,
            $having-preferred,
            for $e in $enumerations
            return 
                element cgMDR:Value_Meaning {
                    element cgMDR:value_meaning_begin_date {current-date()},
                    element cgMDR:value_meaning_description {data($e/value-meaning-desc)},
                    element cgMDR:value_meaning_identifier {data($e/value-meaning-id)}
                  }
         }
       )
       else
       (
       element cgMDR:Non_Enumerated_Conceptual_Domain {
            lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-cd,$version),
            $administration-record,
            $custodians,
            $described-by,
            $having-preferred
         }
       )

   let $value-domain :=
      if ($value-domain-type = 'enumerated') then 
      (
      element cgMDR:Enumerated_Value_Domain {
      lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-vd,$version),
         $administration-record,
         $custodians,
         $described-by,
         $having-preferred,
         element cgMDR:typed_by {$representation-class},
         element cgMDR:value_domain_datatype{$datatype},
         for $e in $enumerations
         return 
         (
         element cgMDR:containing {
            attribute permissible_value_identifier {data($e/permissible-value-id)},
            element cgMDR:permissible_value_begin_date {current-date()},
            element cgMDR:value_item {data($e/permissible-value)},
            element cgMDR:contained_in {data($e/value-meaning-id)}
            }
         ),
         element cgMDR:representing {$full-identifier-cd}        
         }
      )
      else 
      (
         element cgMDR:Non_Enumerated_Value_Domain {
         lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-vd,$version),
         $administration-record,
         $custodians,
         $described-by,         
         $having-preferred, 
         element cgMDR:typed_by {$representation-class}, 
         element cgMDR:value_domain_datatype {$datatype},
         element cgMDR:value_domain_unit_of_measure {$uom},
         element cgMDR:representing {$full-identifier-cd}         
         }      
      )
      
   let $data-element-concept := (
         element cgMDR:Data_Element_Concept {
         lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-dec,$version),
         $administration-record,
         $custodians,
         $described-by,         
         $having-preferred,
        element cgMDR:data_element_concept_conceptual_domain {$full-identifier-cd},
        element cgMDR:data_element_concept_object_class {
          if ($object_class_id) then ($object_class_id) else ($full-identifier-oc)
        },
        element cgMDR:data_element_concept_property {
          if ($property_id) then ($property_id) else ($full-identifier-pr)
        }
      }
   )
   
   let $data-element := (
      element cgMDR:Data_Element {
      lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-de,$version),
         $administration-record,
         $custodians,
         $described-by,         
         $having-preferred,
         if ($having-alt//cgMDR:name > "") then $having-alt else(),
              element cgMDR:data_element_precision {$precision},
              element cgMDR:representing {$full-identifier-vd},
              element cgMDR:typed_by {$representation-class}, 
              element cgMDR:expressing {$full-identifier-dec},
              element cgMDR:exemplified_by {
              element cgMDR:data_element_example_item {$example}
        }
      }
      )
   
   (: save documents :)
   
return
   if ((
      if ($object_class_id > '')
      then 'stored'
      else lib-forms:store-document($object-class))='stored' 
      )
   then (
      if ((
         if ($property_id > '')
         then 'stored'
         else lib-forms:store-document($property))='stored')
      then (
         if (lib-forms:store-document($conceptual-domain)='stored')
         then (
            if (lib-forms:store-document($value-domain)='stored')
            then (
               if (lib-forms:store-document($data-element-concept)='stored')
               then (
                  if (lib-forms:store-document($data-element)='stored')
                  then 
                      let $success-webpage := ( 
                          <div xmlns="http://www.w3.org/1999/xhtml">
                          <table>
                             <tr><td colspan="2">The following documents were created</td></tr>
                             <tr><td class="left_header_cell">data element</td><td>{administered-item:html-anchor($data-element)}</td></tr>
                             <tr><td class="left_header_cell">value domain</td><td>{administered-item:html-anchor($value-domain)}</td></tr>
                             <tr><td class="left_header_cell">data element concept</td><td>{administered-item:html-anchor($data-element-concept)}</td></tr>
                             <tr><td class="left_header_cell">conceptual domain</td><td>{administered-item:html-anchor($conceptual-domain)}</td></tr>
                             {
                                 if ($object_class_id) 
                                 then () 
                                 else (
                                    <tr><td class="left_header_cell">object class</td><td>{administered-item:html-anchor($object-class)}</td></tr>
                                 ),
                                 if ($property_id) 
                                 then () 
                                 else (
                                     <tr><td class="left_header_cell">property</td><td>{administered-item:html-anchor($property)}</td></tr>
                                 )
                             }
                             <tr><td class="left_header_cell"/><td width="40%"></td><td><input type="submit" name="move" value="again"/></td></tr>
                             <tr><td class="left_header_cell"/><td colspan="2">pressing 'again' will allow you to continue entering data elements with the same set of administrative values</td></tr>
                         </table>
                         {
                              local:hidden-controls-page1(),
                              lib-forms:hidden-element('preferred_name_context',request:get-parameter('preferred_name_context','')),
                              lib-forms:hidden-element('country-identifier', request:get-parameter('country-identifier','')),
                              lib-forms:hidden-element('language-identifier', request:get-parameter('language-identifier','')),
                              if (request:get-parameter('property_id','') = 'GB-CANCERGRID-000024-0.1')
                              then lib-forms:hidden-element('property_id', 'GB-CANCERGRID-000024-0.1')
                              else (),
                              if (request:get-parameter('object_class_id','') = 'GB-CANCERGRID-000025-1')
                              then lib-forms:hidden-element('object_class_id', 'GB-CANCERGRID-000025-1')
                              else(),
                              local:hidden-controls-page6()
                          }
                          </div>
                      )
                      return
                          lib-forms:wrap-form-contents("success", $success-webpage)
                  else (<div>failed to save data element</div>)
               )
               else (<div>failed to save data element concept</div>)
            )
            else (<div>failed to save value domain</div>)
         )
         else (<div>failed to save conceptual domain</div>)
      )
      else (<div>failed to save property</div>)
   )
   else (<div>failed to save object class</div>)
   
   (: show UI with links to documents :)
   

};

declare function local:confirm($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard: page 7 - confirm details"
   let $content as node()* := 
      (
         <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr><td class="left_header_cell"/><td width="40%"><input type="submit" name="move" value="page 6"/></td><td><input type="submit" name="move" value="execute"/></td></tr>
               <tr><td class="left_header_cell"/><td colspan="2">Pressing 'execute' will store the documents to the database.  You will then be offered the opportunity to edit each element.</td></tr>
            </table>
            {local:hidden-controls-page1(), 
            local:hidden-controls-page2(), 
            local:hidden-controls-page3(), 
            local:hidden-controls-page5e(), 
            local:hidden-controls-page4(), 
            local:hidden-controls-page5n(),
            local:hidden-controls-page6()}
         </div>
      )
   return lib-forms:wrap-form-contents($title, $content)
};


declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";

session:create(),
let $test as xs:string := "test"
let $next-page := request:get-parameter('move','')
let $relation :=  request:get-parameter('get-admin-item-id', ())

return


if ($next-page = 'page 1') then local:admin-item-details('')
   else(
      if ($next-page = 'page 2') then local:data-element-details('')
      else(
         if ($next-page = 'page 3') then local:data-element-concept('')
         else(
            if ($next-page = 'page 4') then local:value-domain-type('')
            else(
               if ($next-page = 'page 5' and request:get-parameter('value-domain-type','') = 'non-enumerated') then local:value-domain-nonenum('')
               else(
                  if ($next-page = 'page 5' and request:get-parameter('value-domain-type','') = 'enumerated') then local:value-domain-enum('')
                  else(
                     if ($next-page = 'page 6') then local:associated-refdocs('')
                     else(
                        if ($next-page = 'page 7') then local:confirm('')
                        else(
                           if ($next-page = 'execute') then local:execute()
                           else(
                              local:admin-item-details('')
                              )
                           )
                        )
                     )
                  )
               )
            )
         )
      )
