<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cancergrid.org/schema/query" version="2.0">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:template match="/">
        <xsl:value-of select="concat(/q:query/q:serviceUrl, '?scheme=',/q:query/q:term)"/>
    </xsl:template>
</xsl:stylesheet>