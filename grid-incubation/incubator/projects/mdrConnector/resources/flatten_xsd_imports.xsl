<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:template match="/"> 
        <xsl:apply-templates select="*|text()|comment()"/>
    </xsl:template>
    
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/> 
        </xsl:copy>
    </xsl:template>
    
    <!--xsl:template match="wsdl:types">
        <wsdl:types>
        <xsl:for-each select="//xs:import">
            <xsl:apply-templates select="doc(@schemaLocation)"/>
        </xsl:for-each>
        <xsl:apply-templates/>
        </wsdl:types>
    </xsl:template-->
    
    <xsl:template match="xs:schema">
        <xsl:for-each select="//xs:import">
            <xsl:apply-templates select="doc(@schemaLocation)"/>
        </xsl:for-each>
        <xs:schema>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="namespace::*"></xsl:copy-of>
            <xsl:apply-templates/>
        </xs:schema>
    </xsl:template>
    
    <xsl:template match="xs:import">
        <xs:import namespace="{@namespace}"/>
    </xsl:template>
    
</xsl:stylesheet> 