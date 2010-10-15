<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:apply-templates select="//result-set"/>
    </xsl:template>
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="definition">
        <definition>  
            <!--
            <xsl:if test="success/data/classBean/relations/entry/string='DEFINITION'">
                <fdfd> fdfdsfsd</fdfd>
                <xsl:copy-of select="success/data/classBean/relations/entry"/>
            </xsl:if>  
            <xsl:value-of select="success/data/classBean/relations/entry[string='DEFINITION']/list/string"/>
            -->
            <xsl:value-of select="success/data/classBean/definitions/string"/>
        </definition>
        <conceptCode>
            <xsl:value-of select="success/data/classBean/relations/entry[string='code']/list/string"/>
        </conceptCode>
        <semanticType>
            <xsl:value-of select="success/data/classBean/relations/entry[string='Semantic_Type']/list/string"/>
        </semanticType>
        <umlsCUI>
            <xsl:value-of select="success/data/classBean/relations/entry[string='UMLS_CUI']/list/string"/>
        </umlsCUI>
    </xsl:template>
</xsl:stylesheet>