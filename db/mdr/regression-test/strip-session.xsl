<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<!-- Strip session information.
Input: HTML.
Output: HTML.
-->
    <xsl:output method="xml" encoding="UTF-8" indent="no" media-type="text/html"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="@*|*|text()|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="@href|@action">
        <xsl:attribute name="{name()}">
            <xsl:if test="contains(.,';jsessionid=') and contains(.,'?')">
                <xsl:value-of select="concat(substring-before(.,';jsessionid='),'?',substring-after(.,'?'))"/>
            </xsl:if>
            <xsl:if test="contains(.,';jsessionid=') and not(contains(.,'?'))">
                <xsl:value-of select="substring-before(.,';jsessionid=')"/>
            </xsl:if>
            <xsl:if test="not(contains(.,';jsessionid='))">
                <xsl:value-of select="."/>
            </xsl:if>
        </xsl:attribute>
    </xsl:template>
</xsl:stylesheet>