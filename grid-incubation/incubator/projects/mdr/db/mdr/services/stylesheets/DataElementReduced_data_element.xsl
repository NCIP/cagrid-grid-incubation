<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:der-xs="http://cancergrid.org/schema/DataElementReduced" xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179" version="2.0">
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
        <cgMDR:Data_Element item_registration_authority_identifier="{der-xs:reg-auth}" data_identifier="{der-xs:data-element-data-identifier}" version="{$defaults/defaults/data_element_version}">
            <xsl:call-template name="admin-content"/>
            <xsl:apply-templates select="der-xs:other-names/der-xs:name"/>
            <cgMDR:data_element_precision/>
            <cgMDR:representation_class_qualifier/>
            <cgMDR:representing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:value-domain-data-identifier, '-', $defaults/defaults/enumerated_value_domain_version)"/>
            </cgMDR:representing>
            <cgMDR:typed_by>
                <xsl:value-of select="if (der-xs:values/der-xs:enumerated) then 'GB-CANCERGRID-000010-1' else ''"/>
            </cgMDR:typed_by>
            <cgMDR:expressing>
                <xsl:value-of select="concat(der-xs:reg-auth, '-', der-xs:dec-data-identifier, '-', $defaults/defaults/enumerated_value_domain_version)"/>
            </cgMDR:expressing>
            <cgMDR:exemplified_by>
                <cgMDR:data_element_example_item/>
            </cgMDR:exemplified_by>
            <cgMDR:input_to deriving="">
                <cgMDR:derivation_rule_specification/>
            </cgMDR:input_to>
            <cgMDR:field_name preferred="false"/>
            <cgMDR:question_text preferred="false"/>
        </cgMDR:Data_Element>
    </xsl:template>
    <xsl:template match="der-xs:other-names/der-xs:name">
        <cgMDR:having>
            <cgMDR:context_identifier>
                <xsl:value-of select="$defaults/defaults/context_identifier"/>
            </cgMDR:context_identifier>
            <cgMDR:containing>
                <cgMDR:language_section_language_identifier>
                    <cgMDR:country_identifier>
                        <xsl:value-of select="$defaults/defaults/country_identifier"/>
                    </cgMDR:country_identifier>
                    <cgMDR:language_identifier>
                        <xsl:value-of select="$defaults/defaults/language_identifier"/>
                    </cgMDR:language_identifier>
                </cgMDR:language_section_language_identifier>
                <cgMDR:name>
                    <xsl:value-of select="."/>
                </cgMDR:name>
                <cgMDR:definition_text/>
                <cgMDR:preferred_designation>false</cgMDR:preferred_designation>
                <cgMDR:definition_source_reference/>
            </cgMDR:containing>
        </cgMDR:having>
    </xsl:template>
</xsl:stylesheet>