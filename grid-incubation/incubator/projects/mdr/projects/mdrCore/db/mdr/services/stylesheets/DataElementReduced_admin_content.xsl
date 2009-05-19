<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:openMDR="http://www.cagrid.org/schema/openMDR" xmlns="http://www.cagrid.org/schema/openMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:der-xs="http://cagrid.org/schema/DataElementReduced" xmlns:ISO11179="http://www.cagrid.org/schema/ISO11179" version="2.0">

    <!--xsl:variable name="defaults">
        <xsl:copy-of select="doc($defaults_file)"/>
    </xsl:variable-->
    <xsl:template name="admin-content">
        <openMDR:administered_item_administration_record>
            <openMDR:administrative_note/>
            <openMDR:administrative_status>
                <xsl:value-of select="$defaults/defaults/administrative_status"/>
            </openMDR:administrative_status>
            <openMDR:creation_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </openMDR:creation_date>
            <openMDR:effective_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </openMDR:effective_date>
            <openMDR:last_change_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </openMDR:last_change_date>
            <openMDR:registration_status>
                <xsl:value-of select="$defaults/defaults/registration_status"/>
            </openMDR:registration_status>
        </openMDR:administered_item_administration_record>
        <openMDR:administered_by>
            <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:administrator"/>
        </openMDR:administered_by>
        <openMDR:registered_by>
            <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:registrar"/>
        </openMDR:registered_by>
        <openMDR:submitted_by>
            <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:submitter"/>
        </openMDR:submitted_by>
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
                    <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:preferred-name"/>
                </openMDR:name>
                <openMDR:definition_text>
                    <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:definition"/>
                </openMDR:definition_text>
                <openMDR:preferred_designation>true</openMDR:preferred_designation>
                <openMDR:definition_source_reference/>
            </openMDR:containing>
        </openMDR:having>
    </xsl:template>
</xsl:stylesheet>