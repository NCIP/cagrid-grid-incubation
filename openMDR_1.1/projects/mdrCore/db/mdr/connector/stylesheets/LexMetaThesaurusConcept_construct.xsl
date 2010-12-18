<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:template match="/">
        <xsl:apply-templates select="//queryResponse"/>
    </xsl:template>
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
    <!-- Expand the _presentationList and _definitionList node by reading from the given xlink -->
    <xsl:template match="field[@name=('_presentationList', '_definitionList')]">
        <field name="{@name}">
            <xsl:apply-templates select="doc(data(@xlink:href))/xlink:httpQuery/queryResponse/class"/>
        </field>
    </xsl:template>
</xsl:stylesheet>