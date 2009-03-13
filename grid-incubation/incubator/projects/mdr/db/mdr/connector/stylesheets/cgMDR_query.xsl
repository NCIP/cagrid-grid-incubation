<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cancergrid.org/schema/query" version="2.0">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:template match="/">
        <dataElementListSearch xmlns="http://ws.cancergrid.org/exist/wsdl">
            <term>
                <xsl:value-of select="/q:query/q:term"/>
            </term>
            <start>
                <xsl:value-of select="/q:query/q:startIndex"/>
            </start>
            <num>
                <xsl:value-of select="/q:query/q:numResults"/>
            </num>
        </dataElementListSearch>
    </xsl:template>
</xsl:stylesheet>