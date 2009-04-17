<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:openMDR="http://www.cagrid.org/schema/openMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:der-xs="http://cagrid.org/schema/DataElementReduced" xmlns:ISO11179="http://www.cagrid.org/schema/ISO11179" version="2.0">
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
        <openMDR:Data_Element item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:data-element-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <xsl:apply-templates select="der-xs:other-names/der-xs:name"/>
            <openMDR:data_element_precision/>
            <openMDR:representation_class_qualifier/>
            <openMDR:representing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:value-domain-data-identifier, '-', $defaults/defaults/enumerated_value_domain_version)"/>
            </openMDR:representing>
            <openMDR:typed_by>
                <xsl:value-of select="if (der-xs:values/der-xs:enumerated) then 'GB-CANCERGRID-000010-1' else ''"/>
            </openMDR:typed_by>
            <openMDR:expressing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:dec-data-identifier, '-', $defaults/defaults/enumerated_value_domain_version)"/>
            </openMDR:expressing>
            <openMDR:exemplified_by>
                <openMDR:data_element_example_item/>
            </openMDR:exemplified_by>
            <openMDR:input_to deriving="">
                <openMDR:derivation_rule_specification/>
            </openMDR:input_to>
            <openMDR:field_name preferred="false"/>
            <openMDR:question_text preferred="false"/>
        </openMDR:Data_Element>
    </xsl:template>
    <xsl:template match="der-xs:other-names/der-xs:name">
        <openMDR:having>
            <openMDR:context_identifier>
                <xsl:value-of select="$defaults/defaults/context_identifier"/>
            </openMDR:context_identifier>
            <openMDR:containing>
                <openMDR:language_section_language_identifier>
                    <openMDR:country_identifier>
                        <xsl:value-of select="$defaults/defaults/country_identifier"/>
                    </openMDR:country_identifier>
                    <openMDR:language_identifier>
                        <xsl:value-of select="$defaults/defaults/language_identifier"/>
                    </openMDR:language_identifier>
                </openMDR:language_section_language_identifier>
                <openMDR:name>
                    <xsl:value-of select="."/>
                </openMDR:name>
                <openMDR:definition_text/>
                <openMDR:preferred_designation>false</openMDR:preferred_designation>
                <openMDR:definition_source_reference/>
            </openMDR:containing>
        </openMDR:having>
    </xsl:template>
</xsl:stylesheet>