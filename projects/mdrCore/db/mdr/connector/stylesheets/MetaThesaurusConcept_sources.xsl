<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"  xmlns:xlink="http://www.w3.org/1999/xlink">
    
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    
    <xsl:template match="/">
       <xsl:apply-templates select="doc('http://cabio.nci.nih.gov/cacore32/GetXML?query=gov.nih.nci.evs.domain.Source&amp;gov.nih.nci.evs.domain.Source&amp;pageSize=100')/xlink:httpQuery/queryResponse"/>
    </xsl:template>

    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="@recordNumber"/>
    
</xsl:stylesheet>
 