xquery version "1.0";
 
import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
    lib-forms="http://www.cagrid.org/xquery/library/forms" 
    at "../edit/m-lib-forms.xquery";     
    
import module namespace 
    lib-make-admin-item="http://www.cagrid.org/xquery/library/make-admin-item" 
    at "../edit/m-lib-make-admin-item.xquery";      
    
import module namespace 
   lib-supersede="http://www.cagrid.org/xquery/library/supersede"
   at "../edit/m-lib-supersede.xquery";
   
import module namespace 
   administered-item="http://www.cagrid.org/xquery/library/administered-item" 
   at "../library/m-administered-item.xquery";    
   
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace request="http://exist-db.org/xquery/request";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace validation="http://exist-db.org/xquery/validation";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace datetime = "http://exist-db.org/xquery/datetime";

(: wizard pages :)

declare function local:page-button($button-text as xs:string) as element(input)
{
   <input id="move" type="submit" name="move" value="{$button-text}" class="cgButton" onClick="return validate(this);"/>
};

declare function local:admin-item-details($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard:- common administrative item details"
   let $version := '0.1'
   
   let $content as node()* := 
   (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
            <tr><td class="left_header_cell">Navigation</td><td width="40%"></td><td>{local:page-button("Next->Conceptual Domain")}</td></tr>
            <tr><td class="left_header_cell">Registration Authority<font color="red">*</font></td><td colspan="2"> {lib-forms:make-select-registration-authority(request:get-parameter('registration-authority',''))} </td></tr>
            <tr><td class="left_header_cell">Version</td><td colspan="5"> {$version}{lib-forms:radio('label',$version,'true')} </td></tr>
            <tr><td class="left_header_cell">Registered by<font color="red">*</font></td><td colspan="2"> {lib-forms:make-select-registered_by(request:get-parameter('registered-by',''))} </td></tr>
            <tr><td class="left_header_cell">Administered by<font color="red">*</font></td><td colspan="2"> {lib-forms:make-select-administered_by-nameAndOrg(request:get-parameter('administered-by',''))} </td></tr>
            <tr><td class="left_header_cell">Submitted by<font color="red">*</font></td><td colspan="2"> {lib-forms:make-select-submitted_by-nameAndOrg(request:get-parameter('submitted-by',''))} </td></tr>
            <tr><td class="left_header_cell">Administrative Status<font color="red">*</font></td><td colspan="2">{lib-forms:select-from-simpleType-enum('Administrative_Status','administrative-status', false(),request:get-parameter('administrative-status',''))}</td></tr>
            <tr><td class="left_header_cell">Registration Status<font color="red">*</font></td><td colspan="2"> {lib-forms:select-from-simpleType-enum('Registration_Status','registration-status', false(),request:get-parameter('registration-status',''))}</td></tr>
            <tr><td class="left_header_cell">Administrative Note</td><td colspan="2">{lib-forms:text-area-element('administrative-note', 5, 70, request:get-parameter('administrative-note',''))}</td></tr>
            </table>
            {
                local:hidden-controls-conceptual-domain(),
                local:hidden-controls-object-class(),
                local:hidden-controls-property-class(),
                local:hidden-controls-data-element-concept(), 
                local:hidden-controls-value-domain(), 
                local:hidden-controls-data-element(),
                local:hidden-controls-reference-doc()
            }

            </div>
     )
     return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-admin-items()
{
    lib-forms:hidden-element('registration-authority', request:get-parameter('registration-authority','')),
    lib-forms:hidden-element('registered-by',request:get-parameter('registered-by','')),
    lib-forms:hidden-element('administered-by',request:get-parameter('administered-by','')),
    lib-forms:hidden-element('submitted-by',request:get-parameter('submitted-by','')),
    lib-forms:hidden-element('administrative-status', request:get-parameter('administrative-status','')),
    lib-forms:hidden-element('registration-status', request:get-parameter('registration-status','')),
    lib-forms:hidden-element('administrative-note', request:get-parameter('administrative-note',''))
};


(:ADDING CONCEPTUAL DOMAIN:)
declare function local:conceptual-domain-details($message as xs:string) as node()
{
     let $form_name := 'newDataElementWizard_cd'
     let $conceptual_domain_id := request:get-parameter('conceptual_domain_id','')
     let $title as xs:string := "New Data Element Wizard: - Conceptual Domain details"
     let $skip-name := substring-after(request:get-parameter('action',''),'delete value meaning')
     let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
     let $uris := request:get-parameter('cd-ref-uri','')
     let $meanings := request:get-parameter('meanings','')
    
     let $content as node()* := 
            (
            
        <div class="tabbertab">
          <table class="layout">
              <table class="section">
               <tr><td class="left_header_cell"/><td width="40%">{local:page-button("Previous->Admin Items")}</td><td>{local:page-button("Next->Object Class")}</td></tr>
                {
                if(request:get-parameter('choose-conceptual-domain','') = 'existing') 
                then (
                    <tr><td class="left_header_cell">Select Conceptual Domain</td>
                        <td>{lib-forms:radio('choose-conceptual-domain','existing','true')}existing</td>
                        <td>{lib-forms:radio('choose-conceptual-domain','new','false')}new</td>
                        <td>{lib-forms:action-button('update', 'action' ,'','cd-enum-value-update')}</td>
                    </tr>,
                    
                    <tr>
                        <td class="left_header_cell">Select Existing Conceptual Domain</td>
                        <td>{
                         
                            if(request:get-parameter('conceptual_domain_id','') eq "Cancel")  then (
                                lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id', session:get-attribute("conceptual_domain_id"),'newDataElementWizard_cd', 'Change Relationship'),
                                session:set-attribute("conceptual_domain_id", "") )
                            
                            else if(request:get-parameter('conceptual_domain_id','') != "")  then (
                                session:set-attribute("conceptual_domain_id", request:get-parameter('conceptual_domain_id','')),
                                lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id', request:get-parameter('conceptual_domain_id',''),'newDataElementWizard_cd', 'Change Relationship'))
                            
                            else(
                                lib-forms:make-select-admin-item-edit-false('conceptual_domain','conceptual_domain_id',request:get-parameter("conceptual_domain_id",""))
                            )    
                            
                        }
                        </td>
                    </tr>                  
               )else if(request:get-parameter('choose-conceptual-domain','')='new')
               then(
                    <tr><td class="left_header_cell">Select Conceptual Domain?</td>
                         <td>{lib-forms:radio('choose-conceptual-domain','existing','false')}existing</td>
                    <td>{lib-forms:radio('choose-conceptual-domain','new','true')}new</td>
                        <td>{lib-forms:action-button('update', 'action' ,'','cd-enum-value-update')}</td>
                    </tr>,
                 
                    <tr>
                        <td class="left_header_cell">Context</td>
                        <td colspan="5">
                           {lib-forms:select-from-contexts-enum('preferred_name_context_cd',request:get-parameter('preferred_name_context_cd',''))} 
                        </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Name <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:input-element('name_cd', 70, request:get-parameter('name_cd',''))}
                       </td>
                    </tr>,
        
                    <tr>
                        <td class="left_header_cell">Definition</td>
                        <td colspan="5">{lib-forms:text-area-element('definitions_cd', 5, 70, request:get-parameter('definitions_cd',''))}
                        </td>
                    </tr>,
                
                    <tr>
                        <td class="left_header_cell">Language Identifier</td>
                        <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers_cd', false(), request:get-parameter('country-identifiers_cd','US'))}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers_cd', false(), request:get-parameter('language-identifiers_cd','eng'))}
                        </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources_cd',70,request:get-parameter('sources_cd',''))}</td>
                    </tr>,
            
                    <tr><td class="row-header-cell" colspan="6">Conceptual Domain</td></tr>,
                
                <tr>
                    <td class="left_header_cell">Conceptual Domain</td>
                    <td colspan="5">
                     {
                       if(request:get-parameter('conceptual-domain-type','') = 'enumerated') 
                        then (
                            <tr><td class="left_header_cell">Enumerated Conceptual Domain?</td>
                            <td><input type="radio" name="conceptual-domain-type" value="enumerated" checked="checked">enumerated</input></td>
                            <td><input type="radio" name="conceptual-domain-type" value="non-enumerated">non-enumerated</input></td>
                            <td>{lib-forms:action-button('update', 'action' ,'','cd-enum-value-update')}</td>
                           </tr>,
                            <tr><td class="row-header-cell" colspan="6">Conceptual Domain Meanings</td></tr>,
             
                            <tr>
                             <td class="left_header_cell">Value Domain Meanings</td><td>Meaning</td><td>Reference URI</td>
                            </tr>,
                               for $meaning at $pos in $meanings
                               let $location := if($pos > $skip-name-index and $skip-name-index > 0) then (util:eval($pos - 1)) else ($pos)
                               where $pos != $skip-name-index and $meaning > ""
                               return (
                                  <tr>
                                     <td class="left_header_cell">Value {$location} Meaning</td>
                                     <td>{lib-forms:input-element('meanings', 30, $meanings[$pos])}{lib-forms:action-button(concat('delete value meaning ',$location), 'action' ,'','cd-enum-value-update')}</td>
                                     <td>{lib-forms:find-concept-id-CD('cd-ref-uri','get concept',$uris[$pos])}</td>
                           
                                  </tr>
                                  ),
                                  <tr>
                                     <td class="left_header_cell">New Value Meaning</td>
                                     <td>{lib-forms:input-element('meanings', 30, '')}{lib-forms:action-button('add new value meaning', 'action' ,'','cd-enum-value-update')}</td>
                                     <td>{lib-forms:find-concept-id-CD('cd-ref-uri','get concept','')}</td>
                                  </tr>
                              
                        )else( 
                            <tr><td class="left_header_cell">Enumerated Conceptual Domain?</td>
                              <td><input type="radio" name="conceptual-domain-type" value="enumerated" >enumerated</input></td>
                              <td><input type="radio" name="conceptual-domain-type" value="non-enumerated" checked="checked">non-enumerated</input></td>
                              <td>{lib-forms:action-button('update', 'action' ,'','cd-enum-value-update')}</td>
                            </tr>
                         )
                     }
                    
                    </td>
                    
                </tr> 
                )else(
                    <tr>{session:set-attribute("choose-conceptual-domain", "")}</tr>,
                    <tr><td class="left_header_cell">Select Conceptual Domain?</td>
                    <td>{lib-forms:radio('choose-conceptual-domain','existing','false')}existing</td>
                    <td>{lib-forms:radio('choose-conceptual-domain','new','false')}new</td>
                   <td>{lib-forms:action-button('update', 'action' ,'','cd-enum-value-update')}</td>
                 </tr>
                )
              }   
             </table>
         
          </table>
          {
            local:hidden-controls-admin-items(),
            local:hidden-controls-object-class(),
            local:hidden-controls-property-class(),
            local:hidden-controls-data-element-concept(), 
            local:hidden-controls-value-domain(), 
            local:hidden-controls-data-element(),
            local:hidden-controls-reference-doc()
         }
       </div>
       
       )
       return lib-forms:wrap-form-name-contents($title,'newDataElementWizard_cd', $content)  
};

declare function local:hidden-controls-conceptual-domain()
{
     if(request:get-parameter('choose-conceptual-domain','') = 'new') then(
        lib-forms:hidden-element('choose-conceptual-domain',request:get-parameter('choose-conceptual-domain','')),
        lib-forms:hidden-element('preferred_name_context_cd', request:get-parameter('preferred_name_context_cd','')),
        lib-forms:hidden-element('name_cd', request:get-parameter('name_cd','')),
        lib-forms:hidden-element('definitions_cd',request:get-parameter('definitions_cd','')),
        lib-forms:hidden-element('country-identifiers_cd', request:get-parameter('country-identifiers_cd','')),
        lib-forms:hidden-element('language-identifiers_cd', request:get-parameter('language-identifiers_cd','')),
        lib-forms:hidden-element('sources_cd', request:get-parameter('sources_cd','')),
        lib-forms:hidden-element('conceptual-domain-type',request:get-parameter('conceptual-domain-type','')),(:,
        lib-forms:hidden-element('conceptual_domain_id',request:get-parameter('conceptual_domain_id','')):)
       
        let $meanings := request:get-parameter('meanings','')
        let $skip-name := substring-after(request:get-parameter('action',''),'delete value meaning')
        let $skip-name-index := if ($skip-name>'') then xs:int($skip-name) else 0
        let $uris := request:get-parameter('cd-ref-uri','')
        let $meanings := request:get-parameter('meanings','')
        let $uris := request:get-parameter('cd-ref-uri','')                
        for $meaning at $pos in $meanings
           let $location := if($pos > $skip-name-index and $skip-name-index > 0) then (util:eval($pos - 1)) else ($pos)
           where $pos != $skip-name-index and $meaning > ""
           return(
                lib-forms:hidden-element('meanings',$meanings[$pos]),
                lib-forms:hidden-element('cd-ref-uri',$uris[$pos])
           )
     )else(
         lib-forms:hidden-element('choose-conceptual-domain',request:get-parameter('choose-conceptual-domain','')),
         lib-forms:hidden-element('conceptual_domain_id',request:get-parameter('conceptual_domain_id',''))
     )
};

(:END CONCEPTUAL DOMAIN:)

(:ADDING OBJECT CLASS:)
declare function local:object-class-details($message as xs:string) as node()
{
    let $form_name := 'newDataElementWizard_oc'
    let $title as xs:string := "New Data Element Wizard: - Object Class details"
    let $object_class_uri_oc := request:get-parameter('object_class_uri_oc','')
    let $action := request:get-parameter('action_oc','')
    let $skip-uri := substring-after($action,'delete uri entry')
    let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0
    let $content as node()* := 
        (
        <div class="tabbertab">
          <table class="layout">
         
              <table class="section">
               <tr><td class="left_header_cell"/><td width="40%">{local:page-button("Previous->Conceptual Domain")}</td><td>{local:page-button("Next->Property Class")}</td></tr>
               {
                if(request:get-parameter('choose-object-class','') = 'existing') 
                then (
                    <tr><td class="left_header_cell">Select Object Class</td>
                        <td><input type="radio" name="choose-object-class" value="existing" checked="true">existing</input></td>
                        <td><input type="radio" name="choose-object-class" value="new">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','oc-concept-reference')}</td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell"> Select Existing Object Class</td>
                       
                       <td align="left" colspan="2">{
                         
                            if(request:get-parameter('object_class_id','') eq "Cancel")  then (
                                lib-forms:make-select-form-admin-item-edit-false('object_class','object_class_id', session:get-attribute("object_class_id"),'newDataElementWizard_oc', 'Change Relationship'),
                                session:set-attribute("object_class_id", "") )
                            
                            else if(request:get-parameter('object_class_id','') != "")  then (
                                session:set-attribute("object_class_id", request:get-parameter('object_class_id','')),
                                lib-forms:make-select-form-admin-item-edit-false('object_class','object_class_id', request:get-parameter('object_class_id',''),'newDataElementWizard_oc', 'Change Relationship'))
                            
                            else(
                                lib-forms:make-select-admin-item-edit-false('object_class','object_class_id',request:get-parameter("object_class_id",""))
                            )    
                            
                        }
                        </td>
                    </tr>
                )else if(request:get-parameter('choose-object-class','')='new')
                then(  
                    <tr><td class="left_header_cell">Select Object Class</td>
                        <td><input type="radio" name="choose-object-class" value="existing" >existing</input></td>
                        <td><input type="radio" name="choose-object-class" value="new" checked="true">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','oc-concept-reference')}</td>
                    </tr>,                
                    <tr>
                        <td class="left_header_cell">Context</td>
                        <td colspan="5">
                           {lib-forms:select-from-contexts-enum('preferred_name_context_oc',request:get-parameter('preferred_name_context_oc',''))} 
                        </td>
                     </tr>,
                
                    <tr>
                       <td class="left_header_cell">Name <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:input-element('name_oc', 70, request:get-parameter('name_oc',''))}
                       </td>
                    </tr>,
        
                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions_oc', 5, 70, request:get-parameter('definitions_oc',''))}
                       </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers_oc', false(), request:get-parameter('country-identifiers_oc','US'))}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers_oc', false(), request:get-parameter('language-identifiers_oc','eng'))}
                       </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources_oc',70,request:get-parameter('sources_oc',''))}</td>
                    </tr>,
            
                    <tr><td class="row-header-cell" colspan="6">Object Class specific properties</td></tr>,
                      
                         for $u at $pos in $object_class_uri_oc
                         where $pos != $skip-uri-index and $u > ""
                         return 
                         (
                            if($pos > $skip-uri-index and $skip-uri-index > 0) 
                            then (
                               <tr>
                                  <td class="left_header_cell">Concept Reference {util:eval($pos - 1)}</td>
                                  <td colspan="5">
                                     {
                                        lib-forms:find-concept-id-edit-false('object_class_uri_oc','get concept',$u),
                                        lib-forms:action-button(concat('delete uri entry ',util:eval($pos - 1)), 'action' ,'','oc-concept-reference')
                                     }
                                  </td>
                               </tr>
                            ) else (
                               <tr>
                                  <td class="left_header_cell">Concept Reference {$pos}</td>
                                  <td colspan="5">
                                     {
                                        lib-forms:find-concept-id-edit-false('object_class_uri_oc','get concept',$u),
                                        lib-forms:action-button(concat('delete uri entry ',$pos), 'action' ,'','oc-concept-reference')
                                     }
                                  </td>
                               </tr>
                            )
                          
                         ),
                     
                    <tr>
                       <td class="left_header_cell">New Concept Reference</td>
                       <td colspan="5">
                          {lib-forms:find-concept-id-edit-false('object_class_uri_oc','get concept','')}
                          <br></br>
                          {lib-forms:action-button('add another concept', 'action' ,'','oc-concept-reference')}
                       </td>
                    </tr>
                  )else(
                    <tr><td class="left_header_cell">Select Object Class</td>
                        <td><input type="radio" name="choose-object-class" value="existing">existing</input></td>
                        <td><input type="radio" name="choose-object-class" value="new">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','oc-concept-reference')}</td>
                    </tr>
                )
                }
             </table>
         
          </table>
          {
                local:hidden-controls-admin-items(),
                local:hidden-controls-conceptual-domain(),
                local:hidden-controls-property-class(),
                local:hidden-controls-data-element-concept(), 
                local:hidden-controls-value-domain(), 
                local:hidden-controls-data-element(),
                local:hidden-controls-reference-doc()
         }
       </div>
       )
       return lib-forms:wrap-form-contents($title, $content)  
};

declare function local:hidden-controls-object-class()
{
   if(request:get-parameter('choose-object-class','')='new') then(
        lib-forms:hidden-element('choose-object-class',request:get-parameter('choose-object-class','')),
        lib-forms:hidden-element('preferred_name_context_oc', request:get-parameter('preferred_name_context_oc','')),
        lib-forms:hidden-element('name_oc', request:get-parameter('name_oc','')),
        lib-forms:hidden-element('definitions_oc',request:get-parameter('definitions_oc','')),
        lib-forms:hidden-element('country-identifiers_oc', request:get-parameter('country-identifiers_oc','')),
        lib-forms:hidden-element('language-identifiers_oc', request:get-parameter('language-identifiers_oc','')),
        lib-forms:hidden-element('sources_oc', request:get-parameter('sources_oc','')),
        (:lib-forms:hidden-element('object_class_uri_oc',request:get-parameter('object_class_uri_oc','')):)
        lib-forms:hidden-element('object_class_id',request:get-parameter('object_class_id',''))
    )else(
        lib-forms:hidden-element('choose-object-class',request:get-parameter('choose-object-class','')),
        lib-forms:hidden-element('object_class_id', request:get-parameter('object_class_id',''))
    )
};
(:END OBJECT CLASS:)

(:ADDING PROPERTY CLASS:)
declare function local:property-class-details($message as xs:string) as node()
{
    let $form_name := 'newDataElementWizard_pc'
    let $title as xs:string := "New Data Element Wizard: - Property Class details"
    let $property_class_uri_pc := request:get-parameter('property_uri_pc','')
    let $action := request:get-parameter('action_pc','')
    let $skip-uri := substring-after($action,'delete uri entry')
    let $skip-uri-index := if ($skip-uri>'') then xs:int($skip-uri) else 0
    let $content as node()* := 
        (
        <div class="tabbertab">
          <table class="layout">
        
              <table class="section">
               <tr><td class="left_header_cell"/><td width="40%">{local:page-button("Previous->Object Class")}</td><td>{local:page-button("Next->Data Element Concept")}</td></tr>
               {
               if(request:get-parameter('choose-property-class','') = 'existing') 
                then (
                    <tr><td class="left_header_cell">Choose Property Class</td>
                        <td><input type="radio" name="choose-property-class" value="existing" checked="true">existing</input></td>
                        <td><input type="radio" name="choose-property-class" value="new">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','p-uri')}</td>
                    </tr>,
                    <tr>
                      <td class="left_header_cell">Select Existing Property Class </td>
                      
                      <td align="left" colspan="2">{
                         
                            if(request:get-parameter('property_id','') eq "Cancel")  then (
                                lib-forms:make-select-form-admin-item-edit-false('property','property_id', session:get-attribute("property_id"),'newDataElementWizard_pc', 'Change Relationship'),
                                session:set-attribute("property_id", "") )
                            
                            else if(request:get-parameter('property_id','') != "")  then (
                                session:set-attribute("property_id", request:get-parameter('property_id','')),
                                lib-forms:make-select-form-admin-item-edit-false('property','property_id', request:get-parameter('property_id',''),'newDataElementWizard_pc', 'Change Relationship'))
                            
                            else(
                                lib-forms:make-select-admin-item-edit-false('property','property_id',request:get-parameter('property_id',''))
                            )    
                            
                        }
                        </td>
                   </tr>    
               )else if(request:get-parameter('choose-property-class','')='new')
               then(
                    
                    <tr><td class="left_header_cell">Select Property Class</td>
                     <td><input type="radio" name="choose-property-class" value="existing">existing</input></td>
                     <td><input type="radio" name="choose-property-class" value="new" checked="true">new</input></td>
                     <td>{lib-forms:action-button('update', 'action' ,'','p-uri')}</td>
                    </tr>,
               
                    <tr>
                       <td class="left_header_cell">Context</td>
                       <td colspan="5">
                          {lib-forms:select-from-contexts-enum('preferred_name_context_pc',request:get-parameter('preferred_name_context_pc',''))} 
                       </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Name <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:input-element('name_pc', 70, request:get-parameter('name_pc',''))}
                       </td>
                    </tr>,
        
                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions_pc', 5, 70, request:get-parameter('definitions_pc',''))}
                       </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers_pc', false(), request:get-parameter('country-identifiers_pc','US'))}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers_pc', false(), request:get-parameter('language-identifiers_pc','eng'))}
                       </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources_pc',70,request:get-parameter('sources_pc',''))}</td>
                    </tr>,
            
                    <tr><td class="row-header-cell" colspan="6">Property Class specific properties</td></tr>,
                        
                         for $u at $pos in $property_class_uri_pc
                         where $pos != $skip-uri-index and $u > ""
                         return 
                         (
                         if($pos > $skip-uri-index and $skip-uri-index > 0) 
                         then (
                            <tr>
                               <td class="left_header_cell">Concept Reference {util:eval($pos - 1)}</td>
                               <td colspan="5">
                                  {
                                     lib-forms:find-concept-id-edit-false('','get concept',$u),
                                     lib-forms:action-button(concat('delete uri entry ',util:eval($pos - 1)), 'action' ,'','p-uri')
                                  }
                               </td>
                            </tr>
                         ) else (
                            <tr>
                               <td class="left_header_cell">Concept Reference {$pos}</td>
                               <td colspan="5">
                                  {
                                     lib-forms:find-concept-id-edit-false('property_class_uri_pc','get concept',$u),
                                     lib-forms:action-button(concat('delete uri entry ',$pos), 'action' ,'','p-uri')
                                  }
                               </td>
                            </tr>
                         )
                          
                         )
                    ,
                    <tr>
                       <td class="left_header_cell">New Concept Reference</td>
                       <td colspan="5">
                          {lib-forms:find-concept-id-edit-false('property_class_uri_pc','get concept','')}
                          <br></br>
                          {lib-forms:action-button('add another concept', 'action' ,'','p-uri')}
                       </td>
                    </tr>
                 )else(
                    <tr><td class="left_header_cell">Select Property Class</td>
                   <td><input type="radio" name="choose-property-class" value="existing">existing</input></td>
                   <td><input type="radio" name="choose-property-class" value="new">new</input></td>
                   <td>{lib-forms:action-button('update', 'action' ,'','p-uri')}</td>
                 </tr>
                )   
               }   
             </table>
        
          </table>
          {
                local:hidden-controls-admin-items(),
                local:hidden-controls-conceptual-domain(),
                local:hidden-controls-object-class(),
                local:hidden-controls-data-element-concept(), 
                local:hidden-controls-value-domain(), 
                local:hidden-controls-data-element(),
                local:hidden-controls-reference-doc()
         }
       </div>
       )
       return lib-forms:wrap-form-contents($title, $content)  
};

declare function local:hidden-controls-property-class()
{
    if(request:get-parameter('choose-property-class','')='new') then(
        lib-forms:hidden-element('choose-property-class',request:get-parameter('choose-property-class','')),
        lib-forms:hidden-element('preferred_name_context_pc', request:get-parameter('preferred_name_context_pc','')),
        lib-forms:hidden-element('name_pc', request:get-parameter('name_pc','')),
        lib-forms:hidden-element('definitions_pc',request:get-parameter('definitions_pc','')),
        lib-forms:hidden-element('country-identifiers_pc', request:get-parameter('country-identifiers_pc','')),
        lib-forms:hidden-element('language-identifiers_pc', request:get-parameter('language-identifiers_pc','')),
        lib-forms:hidden-element('sources_pc', request:get-parameter('sources_pc','')),
        lib-forms:hidden-element('property_class_uri_pc',request:get-parameter('property_class_uri_pc','')),
        lib-forms:hidden-element('property_id',request:get-parameter('property_id',''))
    ) else(
        lib-forms:hidden-element('choose-property-class',request:get-parameter('choose-property-class','')),
        lib-forms:hidden-element('property_id',request:get-parameter('property_id',''))
    )
};
(:END PROPERTY CLASS:)


declare function local:data-element-concept($message as xs:string) as node()
{
   let $form_name := 'newDataElementWizard_dec'
   let $title as xs:string := "New Data Element Wizard: - Data Element Concept"
   let $log := util:log-system-out('222222222222222222333333333333333344444444444444444444')
   let $log := util:log-system-out(request:get-parameter('object_class_id',''))
   let $content as node()* := 
            (
           
            <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr>
                   <td class="left_header_cell"/><td width="40%">{local:page-button("Previous->Property Class")}</td>
                   <td>{local:page-button("Next->Value Domain")}</td>
               </tr>
               {
                if(request:get-parameter('choose-data-element-concept','') = 'existing') 
                then (
                     <tr><td class="left_header_cell">Select Data Element Concept</td>
                        <td><input type="radio" name="choose-data-element-concept" value="existing" checked="true">existing</input></td>
                        <td><input type="radio" name="choose-data-element-concept" value="new">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','data-element-concept-update')}</td>
                    </tr>,
                     
                     <tr>
                       <td class="left_header_cell">Select Existing Data Element Concept</td>
                       
                       <td align="left" colspan="2">{
                         
                            if(request:get-parameter('data_element_concept_id','') eq "Cancel")  then (
                                lib-forms:make-select-form-admin-item-edit-false('data_element_concept','data_element_concept_id', session:get-attribute("data_element_concept_id"),'newDataElementWizard_dec', 'Change Relationship'),
                                session:set-attribute("data_element_concept_id", "") )
                            
                            else if(request:get-parameter('data_element_concept_id','') != "")  then (
                                session:set-attribute("data_element_concept_id", request:get-parameter('data_element_concept_id','')),
                                lib-forms:make-select-form-admin-item-edit-false('data_element_concept','data_element_concept_id', request:get-parameter('data_element_concept_id',''),'newDataElementWizard_dec', 'Change Relationship'))
                            
                            else(
                                lib-forms:make-select-admin-item-edit-false('data_element_concept','data_element_concept_id',request:get-parameter("data_element_concept_id",""))
                            )    
                            
                        }
                        </td>
                    </tr> 
               )else if(request:get-parameter('choose-data-element-concept','')='new')
               then(
                    <tr><td class="left_header_cell">Select Data Element Concept</td>
                        <td><input type="radio" name="choose-data-element-concept" value="existing">existing</input></td>
                        <td><input type="radio" name="choose-data-element-concept" value="new" checked="checked">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','data-element-concept-update')}</td>
                    </tr>,

                    <tr>
                         <td class="left_header_cell">Context</td>
                         <td colspan="5">
                            {lib-forms:select-from-contexts-enum('preferred_name_context_dec',request:get-parameter('preferred_name_context_dec',''))} 
                         </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Name <font color="red">*</font></td>
                       <td colspan="5">
                          {lib-forms:input-element('name_dec', 70, request:get-parameter('name_dec',''))}
                       </td>
                    </tr>,
        
                    <tr>
                       <td class="left_header_cell">Definition</td>
                       <td colspan="5">{lib-forms:text-area-element('definitions_dec', 5, 70, request:get-parameter('definitions_dec',''))}
                       </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers_dec', false(), request:get-parameter('country-identifiers_dec','US'))}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers_dec', false(), request:get-parameter('language-identifiers_dec','eng'))}
                       </td>
                    </tr>,
                    <tr></tr>,
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources_dec',70,request:get-parameter('sources_dec',''))}</td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Object Class URI</td>
                       <td align="left" colspan="2">
                          {lib-forms:find-concept-id-edit-false('object_class_uri','get object class concept',request:get-parameter('object_class_uri',''))}
                       </td>
                   </tr>,
                    <tr>
                        <td class="left_header_cell"> </td>
                        <td align="left" colspan="2"><i> or select existing </i> 
                        {
                        
                            if(request:get-parameter('object_class_id_dec','') eq "Cancel")  then (
                                lib-forms:make-select-form-admin-item-edit-false('object_class','object_class_id_dec', session:get-attribute("object_class_id_dec"),'newDataElementWizard_dec', 'Change Relationship'),
                                session:set-attribute("object_class_id_dec", "") 
                             )
                            else if(request:get-parameter('object_class_id_dec','') != "")  then (
                                 session:set-attribute("object_class_id_dec", request:get-parameter('object_class_id_dec','')),
                                 lib-forms:make-select-form-admin-item-edit-false('object_class','object_class_id_dec', request:get-parameter('object_class_id_dec',''),'newDataElementWizard_dec', 'Change Relationship')
                            )
                            else(
                                 lib-forms:make-select-admin-item-edit-false('object_class','object_class_id_dec', request:get-parameter('object_class_id','')) 
                            )
                        }
                        </td>
                     </tr>,
                    <tr>
                       <td class="left_header_cell">Property URI</td>
                       <td align="left" colspan="2">
                          {lib-forms:find-concept-id-edit-false('property_uri','get property concept',request:get-parameter('property_uri',''))}
                       </td>
                   </tr>,
                    <tr>
                        <td class="left_header_cell"> </td>
                        <td align="left" colspan="2"><i> or select existing </i> 
                            {
                                if(request:get-parameter('property_id_dec','') eq "Cancel")  then (
                                    lib-forms:make-select-form-admin-item-edit-false('property','property_id_dec', session:get-attribute("property_id_dec"),'new_DataElementConcept_dec', 'Change Relationship'),
                                    session:set-attribute("property_id_dec", "") )
                                
                                else if(request:get-parameter('property_id_dec','') != "")  then (
                                    session:set-attribute("property_id_dec", request:get-parameter('property_id_dec','')),
                                    lib-forms:make-select-form-admin-item-edit-false('property','property_id_dec', request:get-parameter('property_id_dec',''),'new_DataElementConcept_dec', 'Change Relationship'))
                                
                                else(
                                   lib-forms:make-select-admin-item-edit-false('property','property_id_dec', request:get-parameter('property_id','')) 
                              )                 
                            }
                        </td>
                     </tr>,
                    <tr>
                       <td class="left_header_cell">Conceptual Domain</td>
                    </tr>,
                    <tr>
                        <td class="left_header_cell"> </td>
                        <td> 
                           {
                                if(request:get-parameter('conceptual_domain_id_dec','') eq "Cancel")  then (
                                    lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id_dec', session:get-attribute("conceptual_domain_id_dec"),'new_DataElementConcept_dec', 'Change Relationship'),
                                    session:set-attribute("conceptual_domain_id", "") )
                                
                                else if(request:get-parameter('conceptual_domain_id_dec','') != "")  then (
                                    session:set-attribute("conceptual_domain_id_dec", request:get-parameter('conceptual_domain_id_dec','')),
                                    lib-forms:make-select-form-admin-item-edit-false('conceptual_domain','conceptual_domain_id_dec', request:get-parameter('conceptual_domain_id_dec',''),'new_DataElementConcept_dec', 'Change Relationship'))
                                
                                else(
                                   lib-forms:make-select-admin-item-edit-false('conceptual_domain','conceptual_domain_id_dec', request:get-parameter('conceptual_domain_id','')) 
                                )                 
                          }
                        </td>
                     </tr>
                 )else(
                    <tr><td class="left_header_cell">Select Conceptual Domain?</td>
                         <td><input type="radio" name="choose-data-element-concept" value="existing">existing</input></td>
                         <td><input type="radio" name="choose-data-element-concept" value="new">new</input></td>
                         <td>{lib-forms:action-button('update', 'action' ,'','data-element-concept-update')}</td>
                    </tr>
                )
                }    
                </table>
                    {
                        local:hidden-controls-admin-items(),
                        local:hidden-controls-conceptual-domain(),
                        local:hidden-controls-object-class(),
                        local:hidden-controls-property-class(),
                        local:hidden-controls-value-domain(), 
                        local:hidden-controls-data-element(),
                        local:hidden-controls-reference-doc() 
                    }
            </div>
             )
           
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-data-element-concept()
{
    if(request:get-parameter('choose-data-element-concept','')='new') then(
        lib-forms:hidden-element('choose-data-element-concept',request:get-parameter('choose-data-element-concept','')),
        lib-forms:hidden-element('preferred_name_context_dec', request:get-parameter('preferred_name_context_dec','')),
        lib-forms:hidden-element('name_dec', request:get-parameter('name_dec','')),
        lib-forms:hidden-element('definitions_dec',request:get-parameter('definitions_dec','')),
        lib-forms:hidden-element('country-identifiers_dec', request:get-parameter('country-identifiers_dec','')),
        lib-forms:hidden-element('language-identifiers_dec', request:get-parameter('language-identifiers_dec','')),
        lib-forms:hidden-element('sources_dec', request:get-parameter('sources_dec','')),
        lib-forms:hidden-element('object_class_uri', request:get-parameter('object_class_uri','')), 
        lib-forms:hidden-element('property_uri', request:get-parameter('property_uri','')),
        lib-forms:hidden-element('property_id_dec', request:get-parameter('property_id_dec','')),
        lib-forms:hidden-element('object_class_id_dec', request:get-parameter('object_class_id_dec','')),
        lib-forms:hidden-element('conceptual_domain_uri', request:get-parameter('conceptual_domain_uri','')),
        (:lib-forms:hidden-element('conceptual_domain_id_dec', request:get-parameter('conceptual_domain_id_dec','')),:)
        lib-forms:hidden-element('data_element_concept_id',request:get-parameter('data_element_concept_id',''))
    ) else (
        lib-forms:hidden-element('choose-data-element-concept',request:get-parameter('choose-data-element-concept','')),
        lib-forms:hidden-element('data_element_concept_id',request:get-parameter('data_element_concept_id',''))
    )
};

declare function local:value-domain-type($message as xs:string) as node()
{
   let $form_name := 'newDataElementWizard_vd'
   let $title as xs:string := "New Data Element Wizard:- value domain type"
   let $conceptual_domain_id := request:get-parameter('conceptual_domain_id','')
   let $values := ''
   let $content as node()* := 
      (
         <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr><td class="left_header_cell"/><td width="40%">{local:page-button("Previous->Data Element Concept")}</td><td>{local:page-button("Next->Data Element")}</td></tr>
                 {
                 if(request:get-parameter('choose-value-domain','') = 'existing') 
                 then (
                    <tr><td class="left_header_cell">Select Value Domain</td>
                        <td><input type="radio" name="choose-value-domain" value="existing" checked="true">existing</input></td>
                        <td><input type="radio" name="choose-value-domain" value="new">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','value-domain-update')}</td>
                    </tr>,
                    <tr>
                        <td class="left_header_cell">Select Existing Value Domain</td>
                       
                        <td align="left" colspan="2">{
                         
                            if(request:get-parameter('value_domain_id','') eq "Cancel")  then (
                                lib-forms:make-select-form-admin-item-edit-false('value_domain','value_domain_id', session:get-attribute("value_domain_id"),'newDataElementWizard_vd', 'Change Relationship'),
                                session:set-attribute("value_domain_id", "") )
                            
                            else if(request:get-parameter('value_domain_id','') != "")  then (
                                session:set-attribute("value_domain_id", request:get-parameter('value_domain_id','')),
                                lib-forms:make-select-form-admin-item-edit-false('value_domain','value_domain_id', request:get-parameter('value_domain_id',''),'newDataElementWizard_vd', 'Change Relationship'))
                            
                            else(
                                lib-forms:make-select-admin-item-edit-false('value_domain','value_domain_id',request:get-parameter("value_domain_id",""))
                            )    
                            
                        }
                        </td>
                    </tr> 
               )else if(request:get-parameter('choose-value-domain','')='new')
               then(
                     <tr><td class="left_header_cell">Select Value Domain?</td>
                        <td><input type="radio" name="choose-value-domain" value="existing">existing</input></td>
                        <td><input type="radio" name="choose-value-domain" value="new" checked="true">new</input></td>
                        <td>{lib-forms:action-button('update', 'action' ,'','value-domain-update')}</td>
                    </tr>,
                    
                    <tr>
                        <td class="left_header_cell">Context</td>
                        <td colspan="5">
                           {lib-forms:select-from-contexts-enum('preferred_name_context_vd',request:get-parameter('preferred_name_context_vd',''))} 
                        </td>
                     </tr>,
                
                     <tr>
                        <td class="left_header_cell">Name <font color="red">*</font></td>
                        <td colspan="5">
                           {lib-forms:input-element('name_vd', 70, request:get-parameter('name_vd',''))}
                        </td>
                     </tr>,
        
                     <tr>
                        <td class="left_header_cell">Definition</td>
                        <td colspan="5">{lib-forms:text-area-element('definitions_vd', 5, 70, request:get-parameter('definitions_vd',''))}
                        </td>
                     </tr>,
                
                    <tr>
                       <td class="left_header_cell">Language Identifier</td>
                       <td colspan="5">
                          {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers_vd', false(), request:get-parameter('country-identifiers_vd','US'))}
                          {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers_vd', false(), request:get-parameter('language-identifiers_vd','eng'))}
                       </td>
                    </tr>,
                
                    <tr>
                       <td class="left_header_cell">Source</td>
                       <td colspan="5">{lib-forms:input-element('sources_vd',70,request:get-parameter('sources_vd',''))}</td>
                    </tr>,
                
                    <tr><td class="row-header-cell" colspan="6">Conceptual Domain</td></tr>,                                                
                         
                            let $concept_domain := lib-util:mdrElement("conceptual_domain",$conceptual_domain_id)
                            let $log:=util:log-system-out('iiiiiiiiiiijjjjjjjjjjjjjjbhhhhhhhhhhh')
                            let $log :=util:log-system-out($concept_domain)
                            return
                        
                            if ($conceptual_domain_id > '' or $conceptual_domain_id eq "Cancel") then (                   
                               <tr>
                                    
                                    <td class="left_header_cell">Conceptual Domain ID</td>
                                    <td align="left">{$conceptual_domain_id}</td>
                                    <td>{session:set-attribute("conceptual_domain_id", $conceptual_domain_id)}</td>
                                    <td>{lib-forms:popup-form-search('conceptual_domain','conceptual_domain_id','new_value_domain', 'Change Relationship')}</td>                             
                                    <td>{lib-forms:hidden-element('conceptual_domain_id',$conceptual_domain_id)}</td>
                               </tr>,
                               <tr>
                                    <td class="left_header_cell">Conceptual Domain Name</td>
                                    <td align="left">{administered-item:preferred-name('conceptual_domain',$conceptual_domain_id)}</td>
                               </tr>,
                               <tr><td class="row-header-cell" colspan="6">Value Domain</td></tr>,
                               <tr>
                                   <td class="left_header_cell">Value Domain Data Type</td>
                                   <td collspan="3">{lib-forms:make-select-datatype('enum_datatype', request:get-parameter('enum_datatype',''))}</td>
                               </tr>,                          
                               <tr>
                                   <td class="left_header_cell">Value Domain Unit of Measure</td>
                                   <td collspan="3">{lib-forms:make-select-uom('enum_uom',request:get-parameter('uom',''))}</td>
                               </tr>,                         
                                  <tr>
                                   <td class="left_header_cell">Value Domain Format (E.g. MM-DD-YYYY)</td>
                                   <td collspan="3"><input type="text" name="value_domain_format"></input></td>
                               </tr>,                               
                                  <tr>
                                   <td class="left_header_cell">Value Domain Maximum Character Count</td>
                                   <td collspan="3"><input type="text" name="char_quantity"></input></td>
                               </tr>,
                               
                               if($concept_domain//openMDR:value_meaning_description > '') 
                               then 
                               (
                                    <tr>
                                        <td class="left_header_cell">Possible Values</td>
                                            <td>meaning</td>
                                            <td>value</td>
                                        <td/>
                                    </tr>,
                                        for $meaning at $pos in $concept_domain//openMDR:value_meaning_description
                                        return (
                                           
                                           <tr class="thickBorder td">
                                              <td class="left_header_cell">Permissable Value {$pos}</td>
                                              <td>{$meaning}</td>
                                              <td>{lib-forms:input-element('values', 20, $values[$pos])}</td>
                                           </tr>
                                        )
                               ) else ()
                            ) 
                            else ( 
                                 <tr>
                                    <td class="left_header_cell">Choose Conceptual Domain</td>
                                    <td align="left">{lib-forms:make-select-admin-item-edit-false('conceptual_domain','conceptual_domain_id',$conceptual_domain_id)}</td>
                               </tr>
                            )
                       
                )else(
                    <tr><td class="left_header_cell">Select Value Domain?</td>
                   <td><input type="radio" name="choose-value-domain" value="existing">existing</input></td>
                   <td><input type="radio" name="choose-value-domain" value="new">new</input></td>
                   <td>{lib-forms:action-button('update', 'action' ,'','value-domain-update')}</td>
                 </tr>
                )
              }   
            </table>
            {
                local:hidden-controls-admin-items(),
                local:hidden-controls-conceptual-domain(),
                local:hidden-controls-object-class(),
                local:hidden-controls-property-class(),
                local:hidden-controls-data-element-concept(), 
                local:hidden-controls-data-element(),
                local:hidden-controls-reference-doc()
            }
         </div>
        
   )
   return lib-forms:wrap-form-contents($title, $content)
};


declare function local:hidden-controls-value-domain()
{
    if(request:get-parameter('choose-value-domain','')='new') then(
        lib-forms:hidden-element('choose-value-domain',request:get-parameter('choose-value-domain','')),
        lib-forms:hidden-element('preferred_name_context_vd',request:get-parameter('preferred_name_context_vd','')),
        lib-forms:hidden-element('name_vd',request:get-parameter('name_vd','')),
        lib-forms:hidden-element('definitions_vd',request:get-parameter('definitions_vd','')),
        lib-forms:hidden-element('country-identifiers_vd',request:get-parameter('country-identifiers_vd','')),
        lib-forms:hidden-element('language-identifiers_vd',request:get-parameter('language-identifiers_vd','')),
        lib-forms:hidden-element('sources_vd',request:get-parameter('sources_vd','')),
        lib-forms:hidden-element('conceptual_domain_id',request:get-parameter('conceptual_domain_id','')),
        lib-forms:hidden-element('value_domain_id',request:get-parameter('value_domain_id',''))
    )else(
        lib-forms:hidden-element('choose-value-domain',request:get-parameter('choose-value-domain','')),
        lib-forms:hidden-element('value_domain_id',request:get-parameter('value_domain_id',''))
    )

};

declare function local:data-element-details($message as xs:string) as node()
{
   let $form_name := 'newDataElementWizard_de'
   let $title as xs:string := "New Data Element Wizard:- data element details"
   let $content as node()* := 
            (
          <div xmlns="http://www.w3.org/1999/xhtml">
         <table class="layout">
                <tr><td class="left_header_cell"/><td width="40%">{local:page-button("Previous->Value Domain")}</td><td>{local:page-button("Next->Reference Doc")}</td></tr>
                <tr>
                   <td class="left_header_cell">Context</td>
                   <td colspan="5">
                      {lib-forms:select-from-contexts-enum('preferred_name_context_de',request:get-parameter('preferred_name_context_de',''))} 
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Name <font color="red">*</font></td>
                   <td colspan="5">
                      {lib-forms:input-element('name_de', 70, request:get-parameter('name_de',''))}
                   </td>
                </tr>
        
                <tr>
                   <td class="left_header_cell">Definition</td>
                   <td colspan="5">{lib-forms:text-area-element('definitions_de', 5, 70, request:get-parameter('definitions_de',''))}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Language Identifier</td>
                   <td colspan="5">
                      {lib-forms:select-from-simpleType-enum('Country_Identifier','country-identifiers_de', false(), request:get-parameter('country-identifiers_de','US'))}
                      {lib-forms:select-from-simpleType-enum('Language_Identifier','language-identifiers_de', false(), request:get-parameter('language-identifiers_de','eng'))}
                   </td>
                </tr>
                
                <tr>
                   <td class="left_header_cell">Source</td>
                   <td colspan="5">{lib-forms:input-element('sources_de',70,request:get-parameter('sources_de',''))}</td>
                </tr>
                
                <tr>
                  <td class="left_header_cell">Data Element Concept</td>
                  <td align="left" colspan="2">
                  {
                    if(request:get-parameter('data_element_concept_id_de','') eq "Cancel")  then (
                        lib-forms:make-select-form-admin-item-edit-false('data_element_concept','data_element_concept_id_de', session:get-attribute("data_element_concept_id_de"),'newDataElementWizard_de', 'Change Relationship'),
                        session:set-attribute("data_element_concept_id_de", "") )
                    
                    else if(request:get-parameter('data_element_concept_id_de','') != "")  then (
                        session:set-attribute("data_element_concept_id_de", request:get-parameter('data_element_concept_id_de','')),
                        lib-forms:make-select-form-admin-item-edit-false('data_element_concept','data_element_concept_id_de', request:get-parameter('data_element_concept_id_de',''),'newDataElementWizard_de', 'Change Relationship'))
                    
                    else(
                       lib-forms:make-select-admin-item-edit-false('data_element_concept','data_element_concept_id_de', request:get-parameter('data_element_concept_id','')) 
                    )
                  }
                  </td>
               </tr>
               <tr>
                  <td class="left_header_cell">Value Domain</td>
                  <td align="left" colspan="2">
                  {
                    if(request:get-parameter('value_domain_id_de','') eq "Cancel")  then (
                        lib-forms:make-select-admin-item-edit-false('value_domain','value_domain_id_de', session:get-attribute("value_domain_id_de")),
                        session:set-attribute("value_domain_id_de", "") )
                    
                    else if(request:get-parameter('value_domain_id_de','') != "")  then (
                        session:set-attribute("value_domain_id_de", request:get-parameter('value_domain_id_de','')),
                        lib-forms:make-select-admin-item-edit-false('value_domain','value_domain_id_de', request:get-parameter('value_domain_id_de','')))
                    
                    else(
                       lib-forms:make-select-admin-item-edit-false('value_domain','value_domain_id_de', request:get-parameter('value_domain_id','')) 
                    )
                  }
                  </td>
              </tr>
              <tr><td class="left_header_cell">Example</td><td colspan="2">{lib-forms:text-area-element('example', 5, 70, request:get-parameter('example',''))}</td></tr>
              <tr><td class="left_header_cell">Precision</td><td colspan="2">{lib-forms:input-element('precision', 70,request:get-parameter('precision',''))}</td></tr>

         </table>
         {
                local:hidden-controls-admin-items(),
                local:hidden-controls-conceptual-domain(),
                local:hidden-controls-object-class(),
                local:hidden-controls-property-class(),
                local:hidden-controls-data-element-concept(),
                local:hidden-controls-value-domain(), 
                local:hidden-controls-reference-doc()
         }
           </div>
            )
   return lib-forms:wrap-form-contents($title, $content)            
};

declare function local:hidden-controls-data-element()
{
    lib-forms:hidden-element('name_de', request:get-parameter('name_de','')),
    lib-forms:hidden-element('definition_de', request:get-parameter('definition_de','')),
    lib-forms:hidden-element('preferred_name_context_de',request:get-parameter('preferred_name_context_de','')),
    lib-forms:hidden-element('country-identifiers_de', request:get-parameter('country-identifiers_de','')),
    lib-forms:hidden-element('language-identifiers_de', request:get-parameter('language-identifiers_de','')),
    lib-forms:hidden-element('sources_de', request:get-parameter('sources_de','')),
    lib-forms:hidden-element('data_element_concept_id_de', request:get-parameter('data_element_concept_id_de','')),
    lib-forms:hidden-element('value_domain_id_de', request:get-parameter('value_domain_id_de','')),
    lib-forms:hidden-element('example', request:get-parameter('example','')),
    lib-forms:hidden-element('precision', request:get-parameter('precision','')) 
    
};

declare function local:associated-refdocs($message as xs:string) as node()
{
   let $title as xs:string := "New Data Element Wizard:- reference documents"
   let $content as node()* := 
            (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
            <tr><td class="left_header_cell"/><td width="40%">{local:page-button("Previous->Data Element")}</td><td>{local:page-button("Next->Confirm")}</td></tr>
            <tr><td class="left_header_cell">Reference document 1</td><td colspan="2"> {lib-forms:make-select-refdoc('refdoc1', request:get-parameter('refdoc1',''))} </td></tr>
            <tr><td class="left_header_cell">Reference document 2</td><td colspan="2"> {lib-forms:make-select-refdoc('refdoc2', request:get-parameter('refdoc2',''))} </td></tr>
            <tr><td class="left_header_cell">Reference document 3</td><td colspan="2"> {lib-forms:make-select-refdoc('refdoc3', request:get-parameter('refdoc3',''))} </td></tr>
            </table>
            {
                local:hidden-controls-admin-items(),
                local:hidden-controls-conceptual-domain(),
                local:hidden-controls-object-class(),
                local:hidden-controls-property-class(),
                local:hidden-controls-data-element-concept(), 
                local:hidden-controls-value-domain(), 
                local:hidden-controls-data-element()
            }
            </div>
             )
             
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:hidden-controls-reference-doc()
{
lib-forms:hidden-element('refdoc1', request:get-parameter('refdoc1','')),
lib-forms:hidden-element('refdoc2',request:get-parameter('refdoc2','')),
lib-forms:hidden-element('refdoc3',request:get-parameter('refdoc3',''))
};

declare function local:execute() as node()
{

let $registration-authority :=request:get-parameter('registration-authority','')
let $registered-by := request:get-parameter('registered-by','')
let $administered-by := request:get-parameter('administered-by','')
let $submitted-by := request:get-parameter('submitted-by','')
let $administrative-status := request:get-parameter('administrative-status','')
let $administrative-note := request:get-parameter('administrative-note','')

let $name_cd := request:get-parameter('name_cd','')
let $definition_cd := request:get-parameter('definition_cd','')
let $preferred_name_context_cd := request:get-parameter('preferred_name_context_cd','')
let $country-identifier_cd := request:get-parameter('country-identifier_cd','')
let $language-identifier_cd := request:get-parameter('language-identifier_cd','')
let $sources_cd := request:get-parameter('sources_cd','') 

let $name_oc := request:get-parameter('name_oc','')
let $definition_oc := request:get-parameter('definition_oc','')
let $preferred_name_context_oc := request:get-parameter('preferred_name_context_oc','')
let $country-identifier_oc := request:get-parameter('country-identifier_oc','')
let $language-identifier_oc := request:get-parameter('language-identifier_oc','')
let $sources_oc := request:get-parameter('sources_oc','') 

let $name_pc := request:get-parameter('name_pc','')
let $definition_pc := request:get-parameter('definition_pc','')
let $preferred_name_context_pc := request:get-parameter('preferred_name_context_pc','')
let $country-identifier_pc := request:get-parameter('country-identifier_pc','')
let $language-identifier_pc := request:get-parameter('language-identifier_pc','')
let $sources_pc := request:get-parameter('sources_pc','') 

let $name_dec := request:get-parameter('name_dec','')
let $definition_dec := request:get-parameter('definition_dec','')
let $preferred_name_context_dec := request:get-parameter('preferred_name_context_dec','')
let $country-identifier_dec := request:get-parameter('country-identifier_dec','')
let $language-identifier_dec := request:get-parameter('language-identifier_dec','')
let $sources_dec := request:get-parameter('sources_dec','') 

let $name_vd := request:get-parameter('name_vd','')
let $definition_vd := request:get-parameter('definition_oc','')
let $preferred_name_context_vd := request:get-parameter('preferred_name_context_vd','')
let $country-identifier_vd := request:get-parameter('country-identifier_vd','')
let $language-identifier_vd := request:get-parameter('language-identifier_vd','')
let $sources_vd := request:get-parameter('sources_vd','') 

let $name_de := request:get-parameter('name_de','')
let $definition_de := request:get-parameter('definition_de','')
let $preferred_name_context_de := request:get-parameter('preferred_name_context_de','')
let $country-identifier_de := request:get-parameter('country-identifier_de','')
let $language-identifier_de := request:get-parameter('language-identifier_de','')
let $sources_de := request:get-parameter('sources_de','') 

let $representation-class := request:get-parameter('representation-class','')
let $example := request:get-parameter('example','')

let $object_class_uri := request:get-parameter('object_class_uri','') 
let $property_uri := request:get-parameter('property_uri','')
let $object_class_id := request:get-parameter('object_class_id','') 
let $property_id := request:get-parameter('property_id','')
let $value-domain-type :=request:get-parameter('value-domain-type','')
let $values := request:get-parameter('values',())
let $meanings := request:get-parameter('meanings',())
let $datatype := request:get-parameter('datatype','')
let $uom := request:get-parameter('uom','')     
let $enum_datatype := request:get-parameter('enum_datatype','')
let $enum_uom := request:get-parameter('enum_uom','')   
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

let $full-identifier-de := concat($registration-authority, '_', $data-identifier-de, '_', $version)
let $full-identifier-vd := concat($registration-authority, '_', $data-identifier-vd, '_', $version)
let $full-identifier-dec := concat($registration-authority, '_', $data-identifier-dec, '_', $version)
let $full-identifier-cd := concat($registration-authority, '_', $data-identifier-cd, '_', $version)
let $full-identifier-oc := concat($registration-authority, '_', $data-identifier-oc, '_', $version)
let $full-identifier-pr := concat($registration-authority, '_', $data-identifier-pr, '_', $version)

let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
let $registration_status := request:get-parameter('registration_status','')

let $object_class_uri_oc := request:get-parameter('object_class_uri_oc','')

let $administration-record := lib-make-admin-item:administration-record(
        $administrative-note,
        $administrative-status,$creation-date,
        $registration_status)

let $custodians := lib-make-admin-item:custodians(
        $administered-by,
        $registered-by,
        $submitted-by)
            
let $having-preferred-cd := lib-make-admin-item:having(
        $preferred_name_context_cd,
        $country-identifier_cd,
        $language-identifier_cd,
        $name_cd,
        $definition_cd,
        true(), $sources_cd)

let $having-preferred-oc := lib-make-admin-item:having(
        $preferred_name_context_oc,
        $country-identifier_oc,
        $language-identifier_oc,
        $name_oc,
        $definition_oc,
        true(), $sources_oc)

let $having-preferred-pc := lib-make-admin-item:having(
        $preferred_name_context_pc,
        $country-identifier_pc,
        $language-identifier_pc,
        $name_pc,
        $definition_pc,
        true(), $sources_pc)

let $having-preferred-dec := lib-make-admin-item:having(
        $preferred_name_context_dec,
        $country-identifier_dec,
        $language-identifier_dec,
        $name_dec,
        $definition_dec,
        true(), $sources_dec)

let $having-preferred-vd := lib-make-admin-item:having(
        $preferred_name_context_vd,
        $country-identifier_vd,
        $language-identifier_vd,
        $name_vd,
        $definition_vd,
        true(), $sources_vd)

let $having-preferred-de := lib-make-admin-item:having(
        $preferred_name_context_de,
        $country-identifier_de,
        $language-identifier_de,
        $name_de,
        $definition_de,
        true(), $sources_de)
        
let $described-by := (
   if ($refdoc1 > "")
   then (element openMDR:described_by {$refdoc1})
   else(),
   if ($refdoc2 > "")
   then (element openMDR:described_by {$refdoc2})
   else(),
   if ($refdoc3 > "")
   then (element openMDR:described_by {$refdoc3})
   else()
)
            
let $object-class := 
   (
   if(request:get-parameter('choose-object-class','')='new') then(
   element openMDR:Object_Class {
        lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-oc,$version),
        $administration-record,
        $custodians,
        $having-preferred-oc,   
        element openMDR:reference_uri {$object_class_uri_oc}
        }
    )else()    
   )
   
let $property := 
   (
    element openMDR:Property {
        lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-pr,$version),
        $administration-record,
        $custodians,
        $having-preferred-pc,   
        element openMDR:reference_uri {$property_uri}
        }
   )
   
   let $enumerations :=
      (
      for $value at $pos in $values
      let $vmd := $meanings[$pos]
      let $pv := $value
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
      element openMDR:Enumerated_Conceptual_Domain {
            lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-cd,$version),
            $administration-record,
            $custodians,
            $described-by,
            $having-preferred-cd,
            for $e in $enumerations
            return 
                element openMDR:Value_Meaning {
                    element openMDR:value_meaning_begin_date {current-date()},
                    element openMDR:value_meaning_description {data($e/value-meaning-desc)},
                    element openMDR:value_meaning_identifier {data($e/value-meaning-id)}
                  }
         }
       )
       else
       (
       element openMDR:Non_Enumerated_Conceptual_Domain {
            lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-cd,$version),
            $administration-record,
            $custodians,
            $described-by,
            $having-preferred-cd
         }
       )

   let $value-domain :=
      if ($value-domain-type = 'enumerated') then 
      (
      element openMDR:Enumerated_Value_Domain {
      lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-vd,$version),
         $administration-record,
         $custodians,
         $described-by,
         $having-preferred-vd,
         element openMDR:typed_by {$representation-class},
         if ($enum_datatype > "")
         then (element openMDR:value_domain_datatype{$enum_datatype})
         else(),
         if ($enum_uom > "")
         then (element openMDR:value_domain_unit_of_measure{$enum_uom})
         else(),
         for $e in $enumerations
         return 
         (
         element openMDR:containing {
            attribute permissible_value_identifier {data($e/permissible-value-id)},
            element openMDR:permissible_value_begin_date {current-date()},
            element openMDR:value_item {data($e/permissible-value)},
            element openMDR:contained_in {data($e/value-meaning-id)}
            }
         ),
         element openMDR:representing {$full-identifier-cd}        
         }
      )
      else 
      (
         element openMDR:Non_Enumerated_Value_Domain {
         lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-vd,$version),
         $administration-record,
         $custodians,
         $described-by,         
         $having-preferred-vd, 
         element openMDR:typed_by {$representation-class}, 
         element openMDR:value_domain_datatype {$datatype},
         element openMDR:value_domain_unit_of_measure {$uom},
         element openMDR:representing {$full-identifier-cd}         
         }      
      )
      
   let $data-element-concept := (
         element openMDR:Data_Element_Concept {
         lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-dec,$version),
         $administration-record,
         $custodians,
         $described-by,         
         $having-preferred-dec,
        element openMDR:data_element_concept_conceptual_domain {$full-identifier-cd},
        element openMDR:data_element_concept_object_class {
          if ($object_class_id) then ($object_class_id) else ($full-identifier-oc)
        },
        element openMDR:data_element_concept_property {
          if ($property_id) then ($property_id) else ($full-identifier-pr)
        }
      }
   )
   
   let $data-element := (
      element openMDR:Data_Element {
      lib-make-admin-item:identifier-attributes($registration-authority,$data-identifier-de,$version),
         $administration-record,
         $custodians,
         $described-by,         
         $having-preferred-de,
        
              element openMDR:data_element_precision {$precision},
              element openMDR:representing {$full-identifier-vd},
              element openMDR:typed_by {$representation-class}, 
              element openMDR:expressing {$full-identifier-dec},
              element openMDR:exemplified_by {
              element openMDR:data_element_example_item {$example}
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
   let $title as xs:string := "New Data Element Wizard: - confirm details"
   let $content as node()* := 
      (
         <div xmlns="http://www.w3.org/1999/xhtml">
            <table class="layout">
               <tr><td class="left_header_cell"/><td width="40%"><input type="submit" name="move" value="Previous->Reference Doc"/></td><td><input type="submit" name="move" value="Next->execute"/></td></tr>
               <tr><td class="left_header_cell"/><td colspan="2">Pressing 'execute' will store the documents to the database.  You will then be offered the opportunity to edit each element.</td></tr>
            </table>
            {
                local:hidden-controls-admin-items(),
                local:hidden-controls-conceptual-domain(),
                local:hidden-controls-object-class(),
                local:hidden-controls-property-class(),
                local:hidden-controls-data-element-concept(), 
                local:hidden-controls-value-domain(), 
                local:hidden-controls-data-element(),
                local:hidden-controls-reference-doc()
            }
         </div>
      )
   return lib-forms:wrap-form-contents($title, $content)
};

declare function local:executeCD() 
{
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $country-identifiers := request:get-parameter('country-identifiers_cd',())
   let $log := util:log-system-out('ppppppppppppdddddddddddddddddsssssssssssssssssssssss')
   let $log := util:log-system-out($country-identifiers)
   let $language-identifiers := request:get-parameter('language-identifiers_cd',())
   let $names := request:get-parameter('name_cd',())
   let $definitions := request:get-parameter('definitions_cd',())
   let $sources := request:get-parameter('sources_cd',())
   let $uri := request:get-parameter('cd-ref-uri','')
   let $preferred := '1'
   let $action := request:get-parameter('update','')
   let $meanings := request:get-parameter('meanings','')
   let $registration_status := request:get-parameter('registration-status','')
   let $context-ids := request:get-parameter('preferred_name_context_cd','')
   
   let $version := '0.1'
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $doc-name := concat($new-identifier,'.xml')
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
   
   let $content := (
            
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources), 
                  
                  for $meaning at $pos in $meanings
                  let $vmid := lib-forms:generate-id()
                  where $meaning > ""
                    return
                        element openMDR:Value_Meaning {
                          element openMDR:value_meaning_begin_date {$creation-date},
                          element openMDR:value_meaning_description {$meaning},
                          element openMDR:value_meaning_identifier {$vmid},
                          element openMDR:reference_uri {$uri[$pos]}
                        }
                 )

   (: compose the document :)
   let $document := (
       if($meanings > '') then (
          element openMDR:Enumerated_Conceptual_Domain {
                attribute item_registration_authority_identifier {$reg-auth},
                attribute data_identifier {$data-identifier},
                attribute version {$version},
                $content
               }
      ) else (
          element openMDR:Non_Enumerated_Conceptual_Domain {
                attribute item_registration_authority_identifier {$reg-auth},
                attribute data_identifier {$data-identifier},
                attribute version {$version},
                $content
               }
      )
   )
      
   let $collection := 'conceptual_domain'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newConceptualDomain.xquery&amp;",$message)))

    
};


declare function local:executeOC() 
{
   let $version := '0.1'
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $registration_status := request:get-parameter('registration-status','')
   let $data-identifier := lib-forms:generate-id()
   let $context-ids := request:get-parameter('preferred_name_context_oc',())
   let $names := request:get-parameter('name_oc',())
   let $definitions := request:get-parameter('definitions_oc',())
   let $language-identifiers := request:get-parameter('language-identifiers_oc',())
   let $country-identifiers := request:get-parameter('country-identifiers_oc',())
   let $sources := request:get-parameter('sources_oc',())
   let $uris := request:get-parameter('object_class_uri_oc',())
   let $preferred := '1'
      
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $doc-name := concat($new-identifier,'.xml')
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
      
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources), 
                      
               for $u in $uris
               return
                  element openMDR:reference_uri {$u})
   
   (: compose the document :)
   let $document :=
      element openMDR:Object_Class {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            attribute version {$version},
            $content
           }
      
   let $collection := 'object_class'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newObjectClass.xquery&amp;",$message)))

};

declare function local:executePC() 
{
   let $version := '0.1'
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $registration_status := request:get-parameter('registration-status','')
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $doc-name := concat($new-identifier,'.xml')
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
  
   let $context-ids := request:get-parameter('preferred_name_context_pc',())
   let $names := request:get-parameter('name_pc',())
   let $definitions := request:get-parameter('definitions_pc',())
   let $language-identifiers := request:get-parameter('language-identifiers_pc',())
   let $country-identifiers := request:get-parameter('country-identifiers_pc',())
   let $sources := request:get-parameter('sources_pc',())
   let $uris := request:get-parameter('property_class_uri_pc',())
   let $preferred := '1'
   
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources), 
                    for $u in $uris
                    return
                        element openMDR:reference_uri {$u})
   
   (: compose the document :)
   let $document :=
      element openMDR:Property {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            attribute version {$version},
            $content
           }
      
   let $collection := 'property'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newProperty.xquery&amp;",$message)))

};

declare function local:executeDEC() 
{
   
   let $version := '0.1'
   
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $registration_status := request:get-parameter('registration-status','')
   
   let $context-ids := request:get-parameter('preferred_name_context_dec',())
   let $country-identifiers := request:get-parameter('country-identifiers_dec',())
   let $language-identifiers := request:get-parameter('language-identifiers_dec',())
   let $names := request:get-parameter('name_dec',())
   let $definitions := request:get-parameter('definitions_dec',())
   let $sources := request:get-parameter('sources_dec',())
   let $preferred := '1'
   
   
   let $object_class_id := request:get-parameter('object_class_id_dec','')
   
   let $log := util:log-system-out('11111111111111111000000000000111111111111111')
   let $log :=util:log-system-out($object_class_id)
   
   let $property_id := request:get-parameter('property_id_dec','')
   let $conceptual_domain_id := request:get-parameter('conceptual_domain_id_dec','')
   let $object_class_uri := request:get-parameter('object_class_uri','')
   let $property_uri := request:get-parameter('property_uri','')

   let $data-identifier-oc := lib-forms:generate-id()
   let $data-identifier-pr := lib-forms:generate-id()
 
   let $full-identifier-oc := concat($reg-auth, '_', $data-identifier-oc, '_', $version)
   let $full-identifier-pr := concat($reg-auth, '_', $data-identifier-pr, '_', $version)

   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
   let $choose_dec := request:get-parameter('choose-data-element-concept','')
   
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources),
                    element openMDR:data_element_concept_conceptual_domain {$conceptual_domain_id},
                    element openMDR:data_element_concept_object_class {
                      if ($object_class_id) then ($object_class_id) else ($full-identifier-oc)
                    },
                    element openMDR:data_element_concept_property {
                      if ($property_id) then ($property_id) else ($full-identifier-pr)
                    }
    )

    let $object-class := 
       (
       element openMDR:Object_Class {
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier-oc,$version),
             lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
                lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
                lib-make-admin-item:havings(
                        $context-ids,
                        $country-identifiers,
                        $language-identifiers,
                        $names,
                        $definitions,
                        $preferred,
                        $sources),   
            element openMDR:reference_uri {$object_class_uri}
            }
       )
   
    let $property := 
       (
        element openMDR:Property {
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier-pr,$version),
             lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
                lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
                lib-make-admin-item:havings(
                        $context-ids,
                        $country-identifiers,
                        $language-identifiers,
                        $names,
                        $definitions,
                        $preferred,
                        $sources),   
            element openMDR:reference_uri {$property_uri}
            }
       )

   
   (: compose the document :)
   let $document :=
      element openMDR:Data_Element_Concept {
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier,$version),
            $content
           }
      
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
          if(lib-forms:store-document($document)='stored')
          then 'stored'
          else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;","Could not store data element concept"))) )
          )
      else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;","Could not store property"))) )
   )
   else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElementConcept.xquery&amp;","Could not store object class"))) )

};

declare function local:executeVD() 
{
   let $version := '0.1'
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $registration_status := request:get-parameter('registration-status','')
   let $conceptual_domain_id as xs:string? := request:get-parameter('conceptual_domain_id_dec','')
   
   let $context-ids := request:get-parameter('preferred_name_context_vd',())
   let $country-identifiers := request:get-parameter('country-identifiers_vd',())
   let $language-identifiers := request:get-parameter('language-identifiers_vd',())
   let $names := request:get-parameter('name_vd',())
   let $definitions := request:get-parameter('definitions_vd',())
   let $sources := request:get-parameter('sources_vd',())
   let $preferred := '1'
   let $values := request:get-parameter('values',())
   let $enum_datatype := request:get-parameter('enum_datatype','')
   let $enum_uom := request:get-parameter('enum_uom','')
   let $char_quantity := request:get-parameter('char_quantity','')
   let $value_domain_format := request:get-parameter('value_domain_format','')
   
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $doc-name := concat($new-identifier,'.xml')
   let $concept_domain := lib-util:mdrElement("conceptual_domain_dec",$conceptual_domain_id)   
   let $value_meaning-identifier  := data($concept_domain//openMDR:value_meaning_identifier)
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources),
                     
             for $meaning at $pos in $concept_domain//openMDR:value_meaning_description
                    return (
                       element openMDR:containing {
                         attribute permissible_value_identifier {lib-forms:generate-id()},
                         element openMDR:permissible_value_begin_date {$creation-date},
                         element openMDR:value_item {$values[$pos]},
                         element openMDR:contained_in {$value_meaning-identifier[$pos]}
                       }
             ),
            element openMDR:representing {$conceptual_domain_id},
            element openMDR:value_domain_datatype {$enum_datatype},
            element openMDR:value_domain_unit_of_measure {$enum_uom},
            element openMDR:value_domain_maximum_character_quantity{$char_quantity},
            element openMDR:value_domain_format{$value_domain_format}
    )
 
   (: compose the document :)
   let $document := (
     if($values > '' ) then (
      element openMDR:Enumerated_Value_Domain {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            attribute version {$version},
            $content
           }
      ) else (
       element openMDR:Non_Enumerated_Value_Domain {
            attribute item_registration_authority_identifier {$reg-auth},
            attribute data_identifier {$data-identifier},
            attribute version {$version},
            $content
           }
      )
   )
      
   let $collection := 'value_domain'
   let $message := lib-forms:store-document($document) 
   return
      if ($message='stored')
      then true()
      else response:redirect-to(xs:anyURI(concat("../web/login.xquery?calling_page=newValueDomain.xquery&amp;",$message)))

};

declare function local:executeDE() 
{
   let $version := '0.1'
   let $reg-auth := request:get-parameter('registration-authority','')
   let $administrative-note := request:get-parameter('administrative-note','')
   let $administrative-status := request:get-parameter('administrative-status','')
   let $administered-by := request:get-parameter('administered-by','')
   let $submitted-by := request:get-parameter('submitted-by','')
   let $registered-by := request:get-parameter('registered-by','')
   let $registration_status := request:get-parameter('registration-status','')
   let $data_element_concept_id := request:get-parameter('data_element_concept_id','')
   let $value_domain_id := request:get-parameter('value_domain_id','')
   let $example:= request:get-parameter('example','')
   let $precision := request:get-parameter('precision','')
   
   let $context-ids := request:get-parameter('preferred_name_context_de',())
   let $country-identifiers := request:get-parameter('country-identifiers_de',())
   let $language-identifiers := request:get-parameter('language-identifiers_de',())
   let $names := request:get-parameter('name_de',())
   let $definitions := request:get-parameter('definitions_de',())
   let $sources := request:get-parameter('sources_de',())
   let $preferred := '1'
   
   let $data-identifier := lib-forms:generate-id()
   let $new-identifier := concat($reg-auth, '_', $data-identifier, '_', $version)
   let $creation-date := datetime:format-dateTime(current-dateTime(), "MM-dd-yyyy '  ' HH:mm:ss")
   let $content := (
            lib-make-admin-item:administration-record($administrative-note,$administrative-status,$creation-date,$registration_status),            
            lib-make-admin-item:custodians($administered-by,$registered-by,$submitted-by),
            lib-make-admin-item:havings(
                    $context-ids,
                    $country-identifiers,
                    $language-identifiers,
                    $names,
                    $definitions,
                    $preferred,
                    $sources),
                    element openMDR:data_element_precision {$precision},
                    element openMDR:representing {$value_domain_id},
                    element openMDR:expressing {$data_element_concept_id},
                    element openMDR:exemplified_by {
                    element openMDR:data_element_example_item {$example} }
    )
   
   (: compose the document :)
   let $document :=
      element openMDR:Data_Element {
            lib-make-admin-item:identifier-attributes($reg-auth,$data-identifier,$version),
            $content
           }
      
   return
          if(lib-forms:store-document($document)='stored')
          then 'stored'
          else ( response:redirect-to(xs:anyURI(concat("login.xquery?calling_page=newDataElement.xquery&amp;","Could not store data element"))) )

};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
session:create(),
let $test as xs:string := "test"
let $relation :=  request:get-parameter('get-admin-item-id', ())
let $next-page := request:get-parameter('move','')
let $enum-value-update := request:get-parameter('enum-value-update','')
let $cd-enum-value-update := request:get-parameter('cd-enum-value-update','')
let $oc-concept-reference := request:get-parameter('oc-concept-reference','')
let $p-uri := request:get-parameter('p-uri','')
let $conceptual_domain_id_existing :=request:get-parameter('conceptual_domain_id_existing','')
let $data-element-concept-update := request:get-parameter('data-element-concept-update','')
let $value-domain-update := request:get-parameter('value-domain-update','')
let $choose_cd := request:get-parameter('choose-conceptual-domain','')
let $choose_oc := request:get-parameter('choose-object-class','')
let $choose_pc := request:get-parameter('choose-property-class','')
let $choose_dec := request:get-parameter('choose-data-element-concept','')
let $choose_vd := request:get-parameter('choose-value-domain','')
let $choose_de := 'new'
return
   
    if ($p-uri > '') 
        then local:property-class-details('')
    else( if ($oc-concept-reference > '') 
        then local:object-class-details('')
    else(if ($cd-enum-value-update > '') 
        then local:conceptual-domain-details('')
    else(if ($data-element-concept-update > '') 
        then local:data-element-concept('')
    else(if ($value-domain-update > '') 
        then local:value-domain-type('')
    else (if ($next-page = 'Next->Admin Item' or $next-page = 'Previous->Admin-Item') 
        then local:admin-item-details('')
    (:ADDING CONCEPTUAL DOMAIN:)
    else(if ($next-page = 'Next->Conceptual Domain' or $next-page = 'Previous->Conceptual Domain') 
        then local:conceptual-domain-details('')
    else(if ($next-page = 'Next->Object Class' or $next-page = 'Previous->Object Class') 
        then( if($choose_cd = 'new' and local:executeCD())then local:object-class-details('') else if($choose_cd='existing') then local:object-class-details('') else local:conceptual-domain-details('Cannot Save') )
    else(if ($next-page = 'Next->Property Class' or $next-page = 'Previous->Property Class') 
        then( if($choose_oc = 'new' and local:executeOC())then local:property-class-details('') else if($choose_oc='existing') then local:property-class-details('') else local:object-class-details('Cannot Save') )
    else(if ($next-page = 'Next->Data Element Concept' or $next-page = 'Previous->Data Element Concept') 
        then( if($choose_pc = 'new' and local:executePC())then local:data-element-concept('') else if($choose_pc='existing') then local:data-element-concept('') else local:property-class-details('Cannot Save') )
    else(if ($next-page = 'Next->Value Domain' or $next-page = 'Previous->Value Domain') 
        then( if($choose_dec = 'new' and local:executeDEC())then local:value-domain-type('') else if($choose_dec='existing') then local:value-domain-type('') else local:data-element-concept('Cannot Save') )
    else(if ($next-page = 'Next->Data Element' or $next-page = 'Previous->Data Element') 
       then( if($choose_vd = 'new' and local:executeVD())then local:data-element-details('') else if($choose_vd='existing') then local:data-element-details('') else local:value-domain-type('Cannot Save') )        
    else(if ($next-page = 'Next->Reference Doc' or $next-page = 'Previous->Reference Doc') 
        then( if($choose_de = 'new' and local:executeDE())then local:associated-refdocs('') else if($choose_de='existing') then local:associated-refdocs('') else local:data-element-details('Cannot Save') )
    else(if ($next-page = 'Next->Confirm' or $next-page = 'Previous->Confirm') 
        then local:confirm('')
    else(if ($next-page = 'Next->execute' or $next-page = 'Previous->execute') 
        then local:execute()
    else(local:admin-item-details('')
                              )
                           )
                        )
                     )
                  )
               )
            )
         )
      )
      )
    )))))
    
    
    