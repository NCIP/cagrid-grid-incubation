<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xpath-default-namespace="http://cancergrid.org/schema/config">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:template match="/">
        <resources xmlns="http://cancergrid.org/schema/query">
            <xsl:for-each select="//query_service">
                <xsl:sort select="@name"/>
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </resources>
    </xsl:template>
    <xsl:template match="query_service">
        <query_service xmlns="http://cancergrid.org/schema/query">
            <xsl:copy-of select="@category"/>
            <xsl:copy-of select="@name"/>
            <title>
                <xsl:value-of select="title"/>
            </title>
            <description>
                <xsl:value-of select="description"/>
            </description>
            <webUrl>
                <xsl:value-of select="webUrl"/>
            </webUrl>
        </query_service>
    </xsl:template>
</xsl:stylesheet>