<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:err="http://www.w3.org/2005/xqt-errors" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:fn="http://www.w3.org/2005/xpath-functions" version="2.0" exclude-result-prefixes="xs xdt err fn">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:apply-templates select="//queryResponse"/>
    </xsl:template>
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>