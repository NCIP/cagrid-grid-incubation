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
    <xsl:template match="der-xs:new-data-element-reduced[der-xs:values/der-xs:enumerated]">
        <cgMDR:Enumerated_Value_Domain item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:value-domain-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <cgMDR:typed_by>GB-CANCERGRID-000010-1</cgMDR:typed_by>
            <cgMDR:value_domain_datatype>KRMZ1P97F</cgMDR:value_domain_datatype>
            <cgMDR:value_domain_maximum_character_quantity/>
            <cgMDR:value_domain_format/>
            <cgMDR:value_domain_unit_of_measure>MV3QWZ2YG</cgMDR:value_domain_unit_of_measure>
            <xsl:apply-templates select="der-xs:values/der-xs:enumerated/der-xs:value"/>
            <cgMDR:representing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:conceptual-domain-data-identifier, '-0.1')"/>
            </cgMDR:representing>
        </cgMDR:Enumerated_Value_Domain>
    </xsl:template>
    <xsl:template match="der-xs:values/der-xs:enumerated/der-xs:value">
        <cgMDR:containing permissible_value_identifier="{@value-identifier}">
            <cgMDR:permissible_value_begin_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </cgMDR:permissible_value_begin_date>
            <cgMDR:value_item>
                <xsl:value-of select="der-xs:code"/>
            </cgMDR:value_item>
            <cgMDR:contained_in>
                <xsl:value-of select="@meaning-identifier"/>
            </cgMDR:contained_in>
        </cgMDR:containing>
    </xsl:template>
    <xsl:template match="der-xs:new-data-element-reduced[der-xs:values/der-xs:non-enumerated]">
        <cgMDR:Non_Enumerated_Value_Domain item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:value-domain-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <cgMDR:typed_by/>
            <cgMDR:value_domain_datatype>
                <xsl:value-of select="der-xs:values/der-xs:non-enumerated/der-xs:data-type"/>
            </cgMDR:value_domain_datatype>
            <cgMDR:value_domain_maximum_character_quantity/>
            <cgMDR:value_domain_format/>
            <cgMDR:value_domain_unit_of_measure>
                <xsl:value-of select="der-xs:values/der-xs:non-enumerated/der-xs:uom"/>
            </cgMDR:value_domain_unit_of_measure>
            <cgMDR:representing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:conceptual-domain-data-identifier, '-0.1')"/>
            </cgMDR:representing>
        </cgMDR:Non_Enumerated_Value_Domain>
    </xsl:template>
</xsl:stylesheet>