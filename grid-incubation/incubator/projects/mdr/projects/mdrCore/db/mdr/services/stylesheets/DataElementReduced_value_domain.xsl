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
    <xsl:template match="der-xs:new-data-element-reduced[der-xs:values/der-xs:enumerated]">
        <openMDR:Enumerated_Value_Domain item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:value-domain-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <openMDR:typed_by>GB-OPENMDR-000010-1</openMDR:typed_by>
            <openMDR:value_domain_datatype>KRMZ1P97F</openMDR:value_domain_datatype>
            <openMDR:value_domain_maximum_character_quantity/>
            <openMDR:value_domain_format/>
            <openMDR:value_domain_unit_of_measure>MV3QWZ2YG</openMDR:value_domain_unit_of_measure>
            <xsl:apply-templates select="der-xs:values/der-xs:enumerated/der-xs:value"/>
            <openMDR:representing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:conceptual-domain-data-identifier, '-0.1')"/>
            </openMDR:representing>
        </openMDR:Enumerated_Value_Domain>
    </xsl:template>
    <xsl:template match="der-xs:values/der-xs:enumerated/der-xs:value">
        <openMDR:containing permissible_value_identifier="{@value-identifier}">
            <openMDR:permissible_value_begin_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </openMDR:permissible_value_begin_date>
            <openMDR:value_item>
                <xsl:value-of select="der-xs:code"/>
            </openMDR:value_item>
            <openMDR:contained_in>
                <xsl:value-of select="@meaning-identifier"/>
            </openMDR:contained_in>
        </openMDR:containing>
    </xsl:template>
    <xsl:template match="der-xs:new-data-element-reduced[der-xs:values/der-xs:non-enumerated]">
        <openMDR:Non_Enumerated_Value_Domain item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:value-domain-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <openMDR:typed_by/>
            <openMDR:value_domain_datatype>
                <xsl:value-of select="der-xs:values/der-xs:non-enumerated/der-xs:data-type"/>
            </openMDR:value_domain_datatype>
            <openMDR:value_domain_maximum_character_quantity/>
            <openMDR:value_domain_format/>
            <openMDR:value_domain_unit_of_measure>
                <xsl:value-of select="der-xs:values/der-xs:non-enumerated/der-xs:uom"/>
            </openMDR:value_domain_unit_of_measure>
            <openMDR:representing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:conceptual-domain-data-identifier, '-0.1')"/>
            </openMDR:representing>
        </openMDR:Non_Enumerated_Value_Domain>
    </xsl:template>
</xsl:stylesheet>