<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:template match="/"> 
        <xsl:apply-templates select="*|text()|comment()"/>
    </xsl:template>
    
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/> 
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet> 