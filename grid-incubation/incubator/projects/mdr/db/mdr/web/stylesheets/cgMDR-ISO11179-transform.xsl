<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179" version="1.0" exclude-result-prefixes="cgMDR">
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
    <xsl:template match="cgMDR:administered_by">
        <xsl:element name="ISO11179:administered_by">
            <xsl:variable name="contact_identifier">
                <xsl:value-of select="."/>
            </xsl:variable>
            <xsl:element name="ISO11179:stewardship_contact">
                <xsl:apply-templates select="//cgMDR:Contact[@contact_identifier=$contact_identifier]"/>
            </xsl:element>
            <xsl:element name="ISO11179:Organization">
                <xsl:variable name="organization_identifier">
                    <xsl:value-of select="//cgMDR:Organization[//@contact_identifier=$contact_identifier]/@organization_identifier"/>
                </xsl:variable>
                <xsl:apply-templates select="//cgMDR:Organization[@organization_identifier=$organization_identifier]"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!--expand submitted by-->
    <xsl:template match="cgMDR:submitted_by">
        <xsl:element name="ISO11179:submitted_by">
            <xsl:variable name="contact_identifier">
                <xsl:value-of select="."/>
            </xsl:variable>
            <xsl:element name="ISO11179:submission_contact">
                <xsl:apply-templates select="//cgMDR:Contact[@contact_identifier=$contact_identifier]"/>
            </xsl:element>
            <xsl:element name="ISO11179:Organization">
                <xsl:variable name="organization_identifier">
                    <xsl:value-of select="//cgMDR:Organization[//@contact_identifier=$contact_identifier]/@organization_identifier"/>
                </xsl:variable>
                <xsl:apply-templates select="//cgMDR:Organization[@organization_identifier=$organization_identifier]"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <!--expand registered by-->
    <xsl:template match="cgMDR:registered_by">
        <xsl:element name="ISO11179:registered_by">
            <xsl:variable name="contact_identifier" select="."/>
            <xsl:variable name="organization_identifier">
                <xsl:value-of select="//cgMDR:Registration_Authority[//cgMDR:registrar_identifier=$contact_identifier]/@organization_identifier"/>
            </xsl:variable>
            <xsl:apply-templates select="//cgMDR:Registration_Authority[@organization_identifier=$organization_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:Registration_Authority">
        <xsl:apply-templates select="cgMDR:organization_name"/>
        <xsl:apply-templates select="cgMDR:organization_mail_address"/>
        <xsl:apply-templates select="cgMDR:registration_authority_identifier"/>
        <xsl:apply-templates select="cgMDR:documentation_language_identifier"/>
        <xsl:apply-templates select="cgMDR:represented_by"/>
    </xsl:template>
    <xsl:template match="cgMDR:registration_authority_identifier">
        <xsl:element name="ISO11179:registration_authority_identifier">
            <xsl:apply-templates select="cgMDR:international_code_designator"/>
            <xsl:apply-templates select="cgMDR:organization_identifier"/>
            <xsl:apply-templates select="cgMDR:organization_part_identifier"/>
            <xsl:apply-templates select="cgMDR:OPI_Source"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:represented_by">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:registrar_contact">
        <xsl:element name="ISO11179:registrar_contact">
            <xsl:apply-templates select="cgMDR:contact_name"/>
            <xsl:apply-templates select="cgMDR:contact_title"/>
            <xsl:apply-templates select="cgMDR:contact_information"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:registrar_identifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:organization_name">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:Organization">
        <xsl:element name="ISO11179:organization_name">
            <xsl:value-of select="./cgMDR:organization_name"/>
        </xsl:element>
        <xsl:element name="ISO11179:organization_mail_address">
            <xsl:value-of select="./cgMDR:organization_mail_address"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:contact_information">
        <xsl:element name="ISO11179:contact_information">
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <!--reorder this element-->
    <xsl:template match="cgMDR:Contact">
        <xsl:apply-templates select="cgMDR:contact_name"/>
        <xsl:apply-templates select="cgMDR:contact_title"/>
        <xsl:apply-templates select="cgMDR:contact_information"/>
    </xsl:template>
    <xsl:template match="cgMDR:email">
        <xsl:value-of select="text()"/>
    </xsl:template>
    <xsl:template match="cgMDR:language_section_language_identifier">
        <xsl:element name="ISO11179:language_section_language_identifier">
            <xsl:apply-templates select="cgMDR:language_identifier"/>
            <xsl:apply-templates select="cgMDR:country_identifier"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:country_identifier|cgMDR:language_identifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:containing" mode="naming">
        <xsl:element name="ISO11179:containing_entry">
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:containing">
        <xsl:variable name="contained_in" select="cgMDR:contained_in"/>
        <xsl:element name="ISO11179:containing">
            <xsl:apply-templates select="cgMDR:permissible_value_begin_date"/>
            <xsl:apply-templates select="cgMDR:permissible_value_end_date"/>
            <xsl:element name="ISO11179:has">
                <xsl:apply-templates select="//cgMDR:Value_Meaning[cgMDR:value_meaning_identifier=$contained_in]" mode="value_domain"/>
            </xsl:element>
            <xsl:apply-templates select="cgMDR:value_item"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:administered_item_administration_record">
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
            <xsl:apply-templates select="cgMDR:registration_status"/>
            <xsl:apply-templates select="cgMDR:administrative_status"/>
            <xsl:apply-templates select="cgMDR:creation_date"/>
            <xsl:apply-templates select="cgMDR:last_change_date"/>
            <xsl:apply-templates select="cgMDR:effective_date"/>
            <xsl:apply-templates select="cgMDR:until_date"/>
            <xsl:apply-templates select="cgMDR:change_description"/>
            <xsl:apply-templates select="cgMDR:administrative_note"/>
            <xsl:apply-templates select="cgMDR:explanatory_comment"/>
            <xsl:apply-templates select="cgMDR:unresolved_issue"/>
            <xsl:apply-templates select="cgMDR:origin"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:name">
        <xsl:element name="ISO11179:naming_entry">
            <xsl:call-template name="changeNamespace"/>
            <xsl:element name="ISO11179:preferred_designation">
                <xsl:value-of select="../cgMDR:preferred_designation"/>
            </xsl:element>
            <xsl:apply-templates select="../cgMDR:definition_text" mode="naming"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:definition_text" mode="naming">
        <xsl:element name="ISO11179:specifically_referencing">
            <xsl:element name="ISO11179:definition_text">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:element name="ISO11179:preferred_definition">
                <xsl:value-of select="../cgMDR:preferred_designation"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:definition_text"/>
    <xsl:template match="cgMDR:having">
        <xsl:element name="ISO11179:having">
            <xsl:apply-templates select="cgMDR:containing" mode="naming"/>
            <xsl:apply-templates select="cgMDR:context_identifier"/>
        </xsl:element>
    </xsl:template>
    
    <!--reference document processing -->
    <xsl:template match="cgMDR:described_by">
        <xsl:variable name="identifier" select="text()"/>
        <xsl:apply-templates select="//cgMDR:Reference_Document[@reference_document_identifier=$identifier]"/>
    </xsl:template>
    <xsl:template match="cgMDR:Reference_Document">
        <xsl:element name="ISO11179:described_by">
            <xsl:element name="ISO11179:reference_document_identifier">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:apply-templates select="cgMDR:reference_document_type_description"/>
            <xsl:apply-templates select="cgMDR:reference_document_language_identifier"/>
            <xsl:apply-templates select="cgMDR:reference_document_title"/>
            <xsl:apply-templates select="cgMDR:provided_by"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:provided_by">
        <xsl:element name="ISO11179:provided_by">
            <xsl:element name="ISO11179:organization_name">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:reference_document_type_description">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:reference_document_language_identifier">
        <xsl:element name="ISO11179:reference_document_language_identifier">
            <xsl:element name="ISO11179:language_identifier">
                <xsl:value-of select="."/>
            </xsl:element>
            <xsl:element name="ISO11179:country_identifier">GB</xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:reference_document_title">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:preferred_designation"/>
    <xsl:template match="cgMDR:Data_Element">
        <xsl:element name="ISO11179:Data_Element">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="cgMDR:representation_class_qualifier"/>
            <xsl:apply-templates select="cgMDR:data_element_precision"/>
            <xsl:apply-templates select="cgMDR:representing"/>
            <xsl:apply-templates select="cgMDR:expressing"/>
            <xsl:apply-templates select="cgMDR:typed_by"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:definition_source_reference"/>
    <xsl:template match="@version"/>
    <xsl:template match="@data_identifier"/>
    <xsl:template match="@item_registration_authority_identifier"/>
    <xsl:template match="username"/>
    <xsl:template match="@reference_document_identifier"/>
    <xsl:template match="cgMDR:reviewer"/>
    <xsl:template match="cgMDR:reference_document_uri"/>
    <xsl:template match="@permissible_value_identifier"/>
    <xsl:template match="supporting-classes"/>
    <xsl:template match="cgMDR:data_element_complete">
        <xsl:apply-templates select="@*|*|text()|comment()"/>
    </xsl:template>
    <xsl:template match="cgMDR:context_identifier">
        <xsl:choose>
            <xsl:when test="ancestor::cgMDR:Context"/>
            <xsl:otherwise>
                <xsl:variable name="data_identifier">
                    <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
                </xsl:variable>
                <xsl:element name="ISO11179:Context">
                    <xsl:apply-templates select="//cgMDR:Context[@data_identifier=$data_identifier]"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="cgMDR:Context">
        <xsl:call-template name="admin-item-ordering"/>
    </xsl:template>
    <xsl:template match="cgMDR:classified_by"><!--work in progress--></xsl:template>
    <xsl:template match="cgMDR:data_element_precision">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:representation_class_qualifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:exemplified_by">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:representing">
        <xsl:element name="ISO11179:representing">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:representing" mode="value_domain">
        <xsl:element name="ISO11179:representing_conceptual_domain">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    
    <!-- process typed_by representation_class -->
    <xsl:template match="cgMDR:typed_by">
        <xsl:element name="ISO11179:typed_by">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:Representation_Class">
        <xsl:call-template name="admin-item-ordering"/>
    </xsl:template>
    
    <!--link to data element concept-->
    <xsl:template match="cgMDR:expressing">
        <xsl:element name="ISO11179:expressing">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:Non_Enumerated_Value_Domain">
        <xsl:element name="ISO11179:Non_Enumerated_Value_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="cgMDR:value_domain_datatype"/>
            <xsl:apply-templates select="cgMDR:value_domain_unit_of_measure"/>
            <xsl:apply-templates select="cgMDR:value_domain_maximum_character_quantity"/>
            <xsl:apply-templates select="cgMDR:value_domain_format"/>
            <xsl:apply-templates select="cgMDR:typed_by"/>
            <xsl:apply-templates select="cgMDR:representing" mode="value_domain"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:Enumerated_Value_Domain">
        <xsl:element name="ISO11179:Enumerated_Value_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="cgMDR:value_domain_datatype"/>
            <xsl:apply-templates select="cgMDR:value_domain_unit_of_measure"/>
            <xsl:apply-templates select="cgMDR:value_domain_maximum_character_quantity"/>
            <xsl:apply-templates select="cgMDR:value_domain_format"/>
            <xsl:apply-templates select="cgMDR:typed_by"/>
            <xsl:apply-templates select="cgMDR:containing"/>
            <xsl:apply-templates select="cgMDR:representing" mode="value_domain"/>
        </xsl:element>
    </xsl:template>
    <xsl:template name="admin-item-ordering">
        <xsl:apply-templates select="cgMDR:administered_item_administration_record"/>
        <xsl:apply-templates select="cgMDR:registered_by"/>
        <xsl:apply-templates select="cgMDR:described_by"/>
        <xsl:apply-templates select="cgMDR:classified_by"/>
        <xsl:apply-templates select="cgMDR:submitted_by"/>
        <xsl:apply-templates select="cgMDR:administered_by"/>
        <xsl:apply-templates select="cgMDR:having"/>
    </xsl:template>
    <xsl:template match="cgMDR:contained_in">
        <xsl:element name="ISO11179:value_meaning_identifier">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:Data_Element_Concept">
        <xsl:call-template name="admin-item-ordering"/>
        <xsl:apply-templates select="cgMDR:data_element_concept_object_class"/>
        <xsl:apply-templates select="cgMDR:data_element_concept_property"/>
        <xsl:apply-templates select="cgMDR:data_element_concept_conceptual_domain"/>
    </xsl:template>
    <xsl:template match="cgMDR:data_element_concept_conceptual_domain">
        <xsl:element name="ISO11179:having_conceptual_domain">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    
    
    <!-- change document order -->
    <xsl:template match="cgMDR:Value_Meaning">
        <xsl:element name="ISO11179:containing">
            <xsl:apply-templates select="cgMDR:value_meaning_identifier"/>
            <xsl:apply-templates select="cgMDR:value_meaning_description"/>
            <xsl:apply-templates select="cgMDR:value_meaning_begin_date"/>
            <xsl:apply-templates select="cgMDR:value_meaning_end_date"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:Value_Meaning" mode="value_domain">
        <xsl:apply-templates select="cgMDR:value_meaning_identifier"/>
        <xsl:apply-templates select="cgMDR:value_meaning_description"/>
        <xsl:apply-templates select="cgMDR:value_meaning_begin_date"/>
        <xsl:apply-templates select="cgMDR:value_meaning_end_date"/>
    </xsl:template>
    <!-- drop reference uris -->
    <xsl:template match="cgMDR:reference_uri"/>
    <xsl:template match="cgMDR:value_domain_datatype">
        <xsl:variable name="datatype_identifier" select="."/>
        <xsl:element name="ISO11179:value_domain_datatype">
            <xsl:apply-templates select="//*[@datatype_identifier=$datatype_identifier]/*"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:datatype_annotation|cgMDR:datatype_description|cgMDR:datatype_name|cgMDR:datatype_scheme_reference">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:value_domain_unit_of_measure">
        <xsl:variable name="uom_identifier" select="."/>
        <xsl:if test="$uom_identifier&gt;''">
            <xsl:element name="ISO11179:value_domain_unit_of_measure">
                <xsl:apply-templates select="//*[@unit_of_measure_identifier=$uom_identifier]/*"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <xsl:template match="cgMDR:dimensionality">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="value_meaning_begin_date">
        <xsl:element name="ISO11179:value_meaning_begin_date">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:object_class_qualifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:data_element_concept_object_class">
        <xsl:element name="ISO11179:data_element_concept_object_class">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>
    
    <!-- retrieve and include property document -->
    <xsl:template match="cgMDR:data_element_concept_property">
        <xsl:element name="ISO11179:data_element_concept_property">
            <xsl:variable name="data_identifier">
                <xsl:value-of select="substring-before(substring-after(substring-after(.,'-'),'-'),'-')"/>
            </xsl:variable>
            <xsl:apply-templates select="//*[@data_identifier=$data_identifier]"/>
        </xsl:element>
    </xsl:template>

    <!-- reorder contents -->
    <xsl:template match="cgMDR:Concept|cgMDR:Concept_Expression|cgMDR:Property">
        <xsl:call-template name="admin-item-ordering"/>
    </xsl:template>
    <xsl:template match="cgMDR:Enumerated_Conceptual_Domain">
        <xsl:element name="ISO11179:Enumerated_Conceptual_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="cgMDR:Value_Meaning"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:Non_Enumerated_Conceptual_Domain">
        <xsl:element name="ISO11179:Non_Enumerated_Conceptual_Domain">
            <xsl:call-template name="admin-item-ordering"/>
            <xsl:apply-templates select="cgMDR:dimensionality"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:input_to">
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
    <xsl:template match="cgMDR:organization_mail_address">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:documentation_language_identifier">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:derivation_rule_specification">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:related_to">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:data_element_concept_relationship_type_description">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:value_domain_format">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:value_domain_maximum_character_quantity">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:permissible_value_begin_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:value_item">
        <xsl:element name="ISO11179:has_value">
            <xsl:call-template name="changeNamespace"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="cgMDR:registration_status">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:administrative_status">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:creation_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:last_change_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:effective_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:until_date">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:change_description">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:administrative_note">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:explanatory_comment">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:unresolved_issue">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
    <xsl:template match="cgMDR:origin">
        <xsl:call-template name="changeNamespace"/>
    </xsl:template>
</xsl:stylesheet>