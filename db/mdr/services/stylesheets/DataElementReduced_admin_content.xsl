<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:der-xs="http://cancergrid.org/schema/DataElementReduced" xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179" version="2.0">

    <!--xsl:variable name="defaults">
        <xsl:copy-of select="doc($defaults_file)"/>
    </xsl:variable-->
    <xsl:template name="admin-content">
        <cgMDR:administered_item_administration_record>
            <cgMDR:administrative_note/>
            <cgMDR:administrative_status>
                <xsl:value-of select="$defaults/defaults/administrative_status"/>
            </cgMDR:administrative_status>
            <cgMDR:creation_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </cgMDR:creation_date>
            <cgMDR:effective_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </cgMDR:effective_date>
            <cgMDR:last_change_date>
                <xsl:value-of select="format-date(current-date(),'[Y]-[M01]-[D01]')"/>
            </cgMDR:last_change_date>
            <cgMDR:registration_status>
                <xsl:value-of select="$defaults/defaults/registration_status"/>
            </cgMDR:registration_status>
        </cgMDR:administered_item_administration_record>
        <cgMDR:administered_by>
            <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:administrator"/>
        </cgMDR:administered_by>
        <cgMDR:registered_by>
            <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:registrar"/>
        </cgMDR:registered_by>
        <cgMDR:submitted_by>
            <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:submitter"/>
        </cgMDR:submitted_by>
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
                    <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:preferred-name"/>
                </cgMDR:name>
                <cgMDR:definition_text>
                    <xsl:value-of select="/der-xs:new-data-element-reduced/der-xs:definition"/>
                </cgMDR:definition_text>
                <cgMDR:preferred_designation>true</cgMDR:preferred_designation>
                <cgMDR:definition_source_reference/>
            </cgMDR:containing>
        </cgMDR:having>
    </xsl:template>
</xsl:stylesheet>