<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:der-xs="http://cancergrid.org/schema/DataElementReduced" xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:include href="DataElementReduced_admin_content.xsl"/>
    <xsl:param name="defaults_file"/>
    <xsl:variable name="defaults">
        <xsl:copy-of select="doc($defaults_file)"/>
        <!--defaults>
            <country_identifier>GB</country_identifier>
            <language_identifier>eng</language_identifier>
            <context_identifier>GB-CANCERGRID-000001-1</context_identifier>
            <data_element_version>0.1</data_element_version>
            <enumerated_value_domain_version>0.1</enumerated_value_domain_version>
            <administrative_status>scheduledForReview</administrative_status>
            <registration_status>Recorded</registration_status>
            <origin>CRUK Cancer Clinical Trials Unit, University of Birmingham</origin>
            <data_element_concept_object_class>GB-CANCERGRID-000025-1</data_element_concept_object_class>
            <data_element_concept_property>GB-CANCERGRID-000024-0.1</data_element_concept_property>
        </defaults-->
    </xsl:variable>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="der-xs:new-data-element-reduced">
        <cgMDR:Data_Element_Concept item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:dec-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <cgMDR:data_element_concept_conceptual_domain>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:conceptual-domain-data-identifier, '-0.1')"/>
            </cgMDR:data_element_concept_conceptual_domain>
            <cgMDR:data_element_concept_object_class>
                <xsl:value-of select="$defaults/defaults/data_element_concept_object_class"/>
            </cgMDR:data_element_concept_object_class>
            <cgMDR:data_element_concept_property>
                <xsl:value-of select="$defaults/defaults/data_element_concept_property"/>
            </cgMDR:data_element_concept_property>
        </cgMDR:Data_Element_Concept>
    </xsl:template>
</xsl:stylesheet>