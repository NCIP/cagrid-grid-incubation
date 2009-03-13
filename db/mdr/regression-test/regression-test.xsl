<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- This transform is used by rendering routines to visualize regression test
results.
Input: regression test results in XML.
Output: HTML.
-->
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:template match="//test-results">
        <table border="1">
            <thead>
                <tr>
                    <td>Test ID</td>
                    <td>Test description</td>
                    <td>Test passed</td>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="test"/>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template match="test">
        <tr>
            <td>
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:value-of select="@description"/>
            </td>
            <td>
                <xsl:value-of select="@passed"/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>