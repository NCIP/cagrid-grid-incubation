<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:strip-space elements="*"/>
   
    <xsl:template match="/">
        <result-set xmlns="http://cancergrid.org/schema/result-set">
                <xsl:apply-templates select="/result-set/*"/>
        </result-set>
    </xsl:template>

    <xsl:template match="*">
            <xsl:element name="{local-name()}" namespace="http://cancergrid.org/schema/result-set">
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates/>
            </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>