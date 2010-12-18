<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:openMDR="http://www.cagrid.org/schema/openMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:der-xs="http://cagrid.org/schema/DataElementReduced" xmlns:ISO11179="http://www.cagrid.org/schema/ISO11179" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:include href="DataElementReduced_admin_content.xsl"/>
    <xsl:param name="defaults_file"/>
    <xsl:variable name="defaults">
        <xsl:copy-of select="doc($defaults_file)"/>
        <!--defaults>
            <country_identifier>GB</country_identifier>
            <language_identifier>eng</language_identifier>
            <context_identifier>GB-OPENMDR-000001-1</context_identifier>
            <data_element_version>0.1</data_element_version>
            <enumerated_value_domain_version>0.1</enumerated_value_domain_version>
            <administrative_status>scheduledForReview</administrative_status>
            <registration_status>Recorded</registration_status>
            <origin>CRUK Cancer Clinical Trials Unit, University of Birmingham</origin>
            <data_element_concept_object_class>GB-OPENMDR-000025-1</data_element_concept_object_class>
            <data_element_concept_property>GB-OPENMDR-000024-0.1</data_element_concept_property>
        </defaults-->
    </xsl:variable>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="der-xs:new-data-element-reduced">
        <openMDR:Data_Element_Concept item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:dec-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <openMDR:data_element_concept_conceptual_domain>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:conceptual-domain-data-identifier, '-0.1')"/>
            </openMDR:data_element_concept_conceptual_domain>
            <openMDR:data_element_concept_object_class>
                <xsl:value-of select="$defaults/defaults/data_element_concept_object_class"/>
            </openMDR:data_element_concept_object_class>
            <openMDR:data_element_concept_property>
                <xsl:value-of select="$defaults/defaults/data_element_concept_property"/>
            </openMDR:data_element_concept_property>
        </openMDR:Data_Element_Concept>
    </xsl:template>
</xsl:stylesheet>