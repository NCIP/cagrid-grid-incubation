<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
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
    <xsl:template match="definitionURL">
        <xsl:apply-templates/>
        <xsl:variable name="defURL" select="@xlink:href"/>
        <definition>
            <xsl:copy-of select="document($defURL)"/>
        </definition>
    </xsl:template>
</xsl:stylesheet>
