<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:xlink="http://www.w3.org/1999/xlink">
    
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="queryResponse">
        <!--<result-set xmlns="http://cancergrid.org/schema/result-set">-->
        <result-set>
            <xsl:apply-templates select="class"/>
        </result-set>
    </xsl:template>
    
    <xsl:template match="class">
        <xsl:element name="{replace(@name, 'gov.nih.nci.*.domain.', '')}">
            <!--<xsl:copy-of select="@recordNumber"/>-->
            <xsl:apply-templates select="field"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="field">
        <xsl:element name="{replace(@name, 'gov.nih.nci.*.domain.', '')}">
            <xsl:copy-of select="@xlink:*"/>
            <xsl:choose>
                <xsl:when test="node()/node()">
                    <xsl:apply-templates select="class"/>
                </xsl:when>
                <xsl:when test="@xlink:*"/>                
                <xsl:otherwise> 
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
