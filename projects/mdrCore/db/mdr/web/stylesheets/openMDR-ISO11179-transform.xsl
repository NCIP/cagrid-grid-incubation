<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:openMDR="http://www.cagrid.org/schema/openMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ISO11179="http://www.cagrid.org/schema/ISO11179" version="1.0" exclude-result-prefixes="openMDR">
    <xsl:strip-space elements="*"/>
    <xsl:output indent="yes" media-type="text/xml" method="xml"/>
    
    
    <!--visitor templates-->
    <xsl:template match="/">
        <xsl:apply-templates select="@*|*|text()|comment()"/>
    </xsl:template>
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
    
    <!--expand administered by-->
    <xsl:template match="openMDR:administered_by">
        <xsl:element name="ISO11179:administered_by">
            <xsl:variable name="contact_identifier">
                <xsl:value-of select="."/>
            </xsl:variable>
            <xsl:element name="ISO11179:stewardship_contact">
                <xsl:apply-templates select="//openMDR:Contact[@contact_identifier=$contact_identifier]"/>
            </xsl:element>
            <xsl:element name="ISO11179:Organization">
                <xsl:variable name="organization_identifier">
                    <xsl:value-of select="//openMDR:Organization[//@contact_identifier=$contact_identifier]/@organization_identifier"/>
                </xsl:variable>
                <xsl:apply-templates select="//openMDR:Organization[@organization_identifier=$organization_identifier]"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!--expand submitted by-->
    <xsl:template match="openMDR:submitted_by">
        <xsl:element name="ISO11179:submitted_by">
            <xsl:variable name="contact_identifier">
                <xsl:value-of select="."/>
            </xsl:variable>
            <xsl:element name="ISO11179:submission_contact">
                <xsl:apply-templates select="//openMDR:Contact[@contact_identifier=$contact_identifier]"/>
            </xsl:element>
            <xsl:element name="ISO11179:Organization">
                <xsl:variable name="organization_identifier">
                    <xsl:value-of select="//openMDR:Organization[//@contact_identifier=$contact_identifier]/@organization_identifier"/>
                </xsl:variable>
                <xsl:apply-templates select="//openMDR:Organization[@organization_identifier=$organization_identifier]"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!--expand registered by-->
    <xsl:template match="openMDR:registered_by">
        <xsl:element name="ISO11179:registered_by">
            <xsl:variable name="contact_identifier" select="."/>
            <xsl:variable name="organization_identifier">
                <xsl:value-of select="//openMDR:Registration_Authority[//openMDR:registrar_identifier=$contact_identifier]/@organization_identifier"/>
            </xsl:variable>
            <xsl:apply-templates select="//openMDR:Registration_Authority[@organization_identifier=$organization_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:Registration_Authority">
        <xsl:apply-templates select="openMDR:organization_name"/>
        <xsl:apply-templates select="openMDR:organization_mail_address"/>
        <xsl:apply-templates select="openMDR:registration_authority_identifier"/>
        <xsl:apply-templates select="openMDR:documentation_language_identifier"/>
        <xsl:apply-templates select="openMDR:represented_by"/>
    </xsl:template>
    <xsl:template match="openMDR:registration_authority_identifier">
        <xsl:element name="ISO11179:registration_authority_identifier">
            <xsl:apply-templates select="openMDR:international_code_designator"/>
            <xsl:apply-templates select="openMDR:organization_identifier"/>
            <xsl:apply-templates select="openMDR:organization_part_identifier"/>
            <xsl:apply-templates select="openMDR:OPI_Source"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:represented_by">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:registrar_contact">
        <xsl:element name="ISO11179:registrar_contact">
            <xsl:apply-templates select="openMDR:contact_name"/>
            <xsl:apply-templates select="openMDR:contact_title"/>
            <xsl:apply-templates select="openMDR:contact_information"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:registrar_identifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:organization_name">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:Organization">
        <xsl:element name="ISO11179:organization_name">
            <xsl:value-of select="./openMDR:organization_name"/>
        </xsl:element>
        <xsl:element name="ISO11179:organization_mail_address">
            <xsl:value-of select="./openMDR:organization_mail_address"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:contact_information">
        <xsl:element name="ISO11179:contact_information">
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <!--reorder this element-->
    <xsl:template match="openMDR:Contact">
        <xsl:apply-templates select="openMDR:contact_name"/>
        <xsl:apply-templates select="openMDR:contact_title"/>
        <xsl:apply-templates select="openMDR:contact_information"/>
    </xsl:template>
    <xsl:template match="openMDR:email">
        <xsl:value-of select="text()"/>
    </xsl:template>
    <xsl:template match="openMDR:language_section_language_identifier">
        <xsl:element name="ISO11179:language_section_language_identifier">
            <xsl:apply-templates select="openMDR:language_identifier"/>
            <xsl:apply-templates select="openMDR:country_identifier"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:country_identifier|openMDR:language_identifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:containing" mode="naming">
        <xsl:element name="ISO11179:containing_entry">
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:containing">
        <xsl:variable name="contained_in" select="openMDR:contained_in"/>
        <xsl:element name="ISO11179:containing">
            <xsl:apply-templates select="openMDR:permissible_value_begin_date"/>
            <xsl:apply-templates select="openMDR:permissible_value_end_date"/>
            <xsl:element name="ISO11179:has">
                <xsl:apply-templates select="//openMDR:Value_Meaning[openMDR:value_meaning_identifier=$contained_in]" mode="value_domain"/>
            </xsl:element>
            <xsl:apply-templates select="openMDR:value_item"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:administered_item_administration_record">
        <xsl:element name="ISO11179:administered_item_administration_record">
            <xsl:element name="ISO11179:administered_item_identifier">
                <xsl:element name="ISO11179:item_registration_authority_identifier">
                    <xsl:element name="ISO11179:international_code_designator">
                        <xsl:value-of select="substring-before(../@item_registration_authority_identifier,'-')"/>
                    </xsl:element>
                    <xsl:element name="ISO11179:organization_identifier">
                        <xsl:value-of select="substring-after(../@item_registration_authority_identifier,'-')"/>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="ISO11179:data_identifier">
                    <xsl:value-of select="../@data_identifier"/>
                </xsl:element>
                <xsl:element name="ISO11179:version">
                    <xsl:value-of select="../@version"/>
                </xsl:element>
            </xsl:element>
            <xsl:apply-templates select="openMDR:registration_status"/>
            <xsl:apply-templates select="openMDR:administrative_status"/>
            <xsl:apply-templates select="openMDR:creation_date"/>
            <xsl:apply-templates select="openMDR:last_change_date"/>
            <xsl:apply-templates select="openMDR:effective_date"/>
            <xsl:apply-templates select="openMDR:until_date"/>
            <xsl:apply-templates select="openMDR:change_description"/>
            <xsl:apply-templates select="openMDR:administrative_note"/>
            <xsl:apply-templates select="openMDR:explanatory_comment"/>
            <xsl:apply-templates select="openMDR:unresolved_issue"/>
            <xsl:apply-templates select="openMDR:origin"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:name">
        <xsl:element name="ISO11179:naming_entry">
            <xsl:call-template name="changeNamespace"/>
            <xsl:element name="ISO11179:preferred_designation">
                <xsl:value-of select="../openMDR:preferred_designation"/>
            </xsl:element>
            <xsl:apply-templates select="../openMDR:definition_text" mode="naming"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:definition_text" mode="naming">
        <xsl:element name="ISO11179:specifically_referencing">
            <xsl:element name="ISO11179:definition_text">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:element name="ISO11179:preferred_definition">
                <xsl:value-of select="../openMDR:preferred_designation"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:definition_text"/>
    <xsl:template match="openMDR:having">
        <xsl:element name="ISO11179:having">
            <xsl:apply-templates select="openMDR:containing" mode="naming"/>
            <xsl:apply-templates select="openMDR:context_identifier"/>
        </xsl:element>
    </xsl:template>
    
    <!--reference document processing -->
    <xsl:template match="openMDR:described_by">
        <xsl:variable name="identifier" select="text()"/>
        <xsl:apply-templates select="//openMDR:Reference_Document[@reference_document_identifier=$identifier]"/>
    </xsl:template>
    <xsl:template match="openMDR:Reference_Document">
        <xsl:element name="ISO11179:described_by">
            <xsl:element name="ISO11179:reference_document_identifier">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:apply-templates select="openMDR:reference_document_type_description"/>
            <xsl:apply-templates select="openMDR:reference_document_language_identifier"/>
            <xsl:apply-templates select="openMDR:reference_document_title"/>
            <xsl:apply-templates select="openMDR:provided_by"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:provided_by">
        <xsl:element name="ISO11179:provided_by">
            <xsl:element name="ISO11179:organization_name">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:reference_document_type_description">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:reference_document_language_identifier">
        <xsl:element name="ISO11179:reference_document_language_identifier">
            <xsl:element name="ISO11179:language_identifier">
                <xsl:value-of select="."/>
            </xsl:element>
            <xsl:element name="ISO11179:country_identifier">GB</xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:reference_document_title">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:preferred_designation"/>
    <xsl:template match="openMDR:Data_Element">
        <xsl:element name="ISO11179:Data_Element">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="openMDR:representation_class_qualifier"/>
            <xsl:apply-templates select="openMDR:data_element_precision"/>
            <xsl:apply-templates select="openMDR:representing"/>
            <xsl:apply-templates select="openMDR:expressing"/>
            <xsl:apply-templates select="openMDR:typed_by"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:definition_source_reference"/>
    <xsl:template match="@version"/>
    <xsl:template match="@data_identifier"/>
    <xsl:template match="@item_registration_authority_identifier"/>
    <xsl:template match="username"/>
    <xsl:template match="@reference_document_identifier"/>
    <xsl:template match="openMDR:reviewer"/>
    <xsl:template match="openMDR:reference_document_uri"/>
    <xsl:template match="@permissible_value_identifier"/>
    <xsl:template match="supporting-classes"/>
    <xsl:template match="openMDR:data_element_complete">
        <xsl:apply-templates select="@*|*|text()|comment()"/>
    </xsl:template>
    <xsl:template match="openMDR:context_identifier">
        <xsl:choose>
            <xsl:when test="ancestor::openMDR:Context"/>
            <xsl:otherwise>
                <xsl:variable name="data_identifier">
                    <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
                </xsl:variable>
                <xsl:element name="ISO11179:Context">
                    <xsl:apply-templates select="//openMDR:Context[@data_identifier=$data_identifier]"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="openMDR:Context">
        <xsl:call-template name="admin-item-ordering"/>
    </xsl:template>
    <xsl:template match="openMDR:classified_by"><!--work in progress--></xsl:template>
    <xsl:template match="openMDR:data_element_precision">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:representation_class_qualifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:exemplified_by">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:representing">
        <xsl:element name="ISO11179:representing">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:representing" mode="value_domain">
        <xsl:element name="ISO11179:representing_conceptual_domain">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    
    <!-- process typed_by representation_class -->
    <xsl:template match="openMDR:typed_by">
        <xsl:element name="ISO11179:typed_by">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:Representation_Class">
        <xsl:call-template name="admin-item-ordering"/>
    </xsl:template>
    
    <!--link to data element concept-->
    <xsl:template match="openMDR:expressing">
        <xsl:element name="ISO11179:expressing">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:Non_Enumerated_Value_Domain">
        <xsl:element name="ISO11179:Non_Enumerated_Value_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="openMDR:value_domain_datatype"/>
            <xsl:apply-templates select="openMDR:value_domain_unit_of_measure"/>
            <xsl:apply-templates select="openMDR:value_domain_maximum_character_quantity"/>
            <xsl:apply-templates select="openMDR:value_domain_format"/>
            <xsl:apply-templates select="openMDR:typed_by"/>
            <xsl:apply-templates select="openMDR:representing" mode="value_domain"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:Enumerated_Value_Domain">
        <xsl:element name="ISO11179:Enumerated_Value_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="openMDR:value_domain_datatype"/>
            <xsl:apply-templates select="openMDR:value_domain_unit_of_measure"/>
            <xsl:apply-templates select="openMDR:value_domain_maximum_character_quantity"/>
            <xsl:apply-templates select="openMDR:value_domain_format"/>
            <xsl:apply-templates select="openMDR:typed_by"/>
            <xsl:apply-templates select="openMDR:containing"/>
            <xsl:apply-templates select="openMDR:representing" mode="value_domain"/>
        </xsl:element>
    </xsl:template>
    <xsl:template name="admin-item-ordering">
        <xsl:apply-templates select="openMDR:administered_item_administration_record"/>
        <xsl:apply-templates select="openMDR:registered_by"/>
        <xsl:apply-templates select="openMDR:described_by"/>
        <xsl:apply-templates select="openMDR:classified_by"/>
        <xsl:apply-templates select="openMDR:submitted_by"/>
        <xsl:apply-templates select="openMDR:administered_by"/>
        <xsl:apply-templates select="openMDR:having"/>
    </xsl:template>
    <xsl:template match="openMDR:contained_in">
        <xsl:element name="ISO11179:value_meaning_identifier">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:Data_Element_Concept">
        <xsl:call-template name="admin-item-ordering"/>
        <xsl:apply-templates select="openMDR:data_element_concept_object_class"/>
        <xsl:apply-templates select="openMDR:data_element_concept_property"/>
        <xsl:apply-templates select="openMDR:data_element_concept_conceptual_domain"/>
    </xsl:template>
    <xsl:template match="openMDR:data_element_concept_conceptual_domain">
        <xsl:element name="ISO11179:having_conceptual_domain">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    
    
    <!-- change document order -->
    <xsl:template match="openMDR:Value_Meaning">
        <xsl:element name="ISO11179:containing">
            <xsl:apply-templates select="openMDR:value_meaning_identifier"/>
            <xsl:apply-templates select="openMDR:value_meaning_description"/>
            <xsl:apply-templates select="openMDR:value_meaning_begin_date"/>
            <xsl:apply-templates select="openMDR:value_meaning_end_date"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:Value_Meaning" mode="value_domain">
        <xsl:apply-templates select="openMDR:value_meaning_identifier"/>
        <xsl:apply-templates select="openMDR:value_meaning_description"/>
        <xsl:apply-templates select="openMDR:value_meaning_begin_date"/>
        <xsl:apply-templates select="openMDR:value_meaning_end_date"/>
    </xsl:template>
    <!-- drop reference uris -->
    <xsl:template match="openMDR:reference_uri"/>
    <xsl:template match="openMDR:value_domain_datatype">
        <xsl:variable name="datatype_identifier" select="."/>
        <xsl:element name="ISO11179:value_domain_datatype">
            <xsl:apply-templates select="//*[@datatype_identifier=$datatype_identifier]/*"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:datatype_annotation|openMDR:datatype_description|openMDR:datatype_name|openMDR:datatype_scheme_reference">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:value_domain_unit_of_measure">
        <xsl:variable name="uom_identifier" select="."/>
        <xsl:if test="$uom_identifier&gt;''">
            <xsl:element name="ISO11179:value_domain_unit_of_measure">
                <xsl:apply-templates select="//*[@unit_of_measure_identifier=$uom_identifier]/*"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <xsl:template match="openMDR:dimensionality">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="value_meaning_begin_date">
        <xsl:element name="ISO11179:value_meaning_begin_date">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:object_class_qualifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:data_element_concept_object_class">
        <xsl:element name="ISO11179:data_element_concept_object_class">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    
    <!-- retrieve and include property document -->
    <xsl:template match="openMDR:data_element_concept_property">
        <xsl:element name="ISO11179:data_element_concept_property">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>

    <!-- reorder contents -->
    <xsl:template match="openMDR:Concept|openMDR:Concept_Expression|openMDR:Property">
        <xsl:call-template name="admin-item-ordering"/>
    </xsl:template>
    <xsl:template match="openMDR:Enumerated_Conceptual_Domain">
        <xsl:element name="ISO11179:Enumerated_Conceptual_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="openMDR:Value_Meaning"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:Non_Enumerated_Conceptual_Domain">
        <xsl:element name="ISO11179:Non_Enumerated_Conceptual_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="openMDR:dimensionality"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:input_to">
        <xsl:element name="ISO11179:input_to">
            <xsl:element name="ISO11179:deriving">
                <xsl:value-of select="@deriving"/>
            </xsl:element>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="@deriving"/>
    <xsl:template match="@organization_identifier"/>
    <xsl:template name="changeNamespace">
        <xsl:element name="{concat('ISO11179:',local-name())}">
            <xsl:choose>
                <xsl:when test="*">
                    <xsl:apply-templates select="@*|*|text()|comment()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="text()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <!-- simple change namespaces -->
    <xsl:template match="openMDR:organization_mail_address">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:documentation_language_identifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:derivation_rule_specification">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:related_to">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:data_element_concept_relationship_type_description">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:value_domain_format">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:value_domain_maximum_character_quantity">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:permissible_value_begin_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:value_item">
        <xsl:element name="ISO11179:has_value">
            <xsl:call-template name="changeNamespace"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="openMDR:registration_status">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:administrative_status">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:creation_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:last_change_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:effective_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:until_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:change_description">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:administrative_note">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:explanatory_comment">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:unresolved_issue">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="openMDR:origin">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
</xsl:stylesheet>