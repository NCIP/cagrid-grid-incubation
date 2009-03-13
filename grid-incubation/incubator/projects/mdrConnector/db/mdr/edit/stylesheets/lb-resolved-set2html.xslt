<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:lg-cncpt="http://concepts.LexGrid.org/xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:lg-common="http://commonTypes.LexGrid.org/xsd" xmlns:lb-core="http://Core.DataModel.LexBIG.LexGrid.org/xsd" xmlns:lb-coll="http://Collections.DataModel.LexBIG.LexGrid.org/xsd" version="1.0">
    <!-- This transform is used by rendering routines to visualize a LexBIG resolved set.
    Input: a LexBIG resolved set from the CancerGrig LexBIG web-service.
    Output: HTML. -->
    <xsl:param name="destination-page"/>
    <xsl:param name="return-parameter"/>
    <xsl:param name="authority"/>
    <xsl:param name="resource"/>
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:template xmlns:lb-cg="http://lexbig.cancergrid.org/xsd" match="//lb-cg:resolveSetResponse/lb-cg:return">
        <table>
            <thead>
                <tr>
                    <td class="light-rule">Concept code</td>
                    <td class="light-rule" width="7%">Coding scheme version</td>
                    <td class="light-rule">Entity description</td>
                    <td class="light-rule">1st version</td>
                    <td class="light-rule">Last version</td>
                    <td class="light-rule">Concept status</td>
                    <td class="light-rule">Definition count</td>
                    <td class="light-rule">Definition(s)</td>
                    <td/>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="lb-coll:resolvedConceptReference"/>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template match="lb-coll:resolvedConceptReference">
        <xsl:variable name="id" select="lb-core:conceptCode"/>
        <xsl:variable name="version" select="lb-core:codingSchemeVersion"/>
        <tr>
            <td>
                <xsl:value-of select="$id"/>
            </td>
            <td>
                <xsl:value-of select="$version"/>
            </td>
            <td>
                <xsl:value-of select="lb-core:entityDescription/lg-common:content"/>
            </td>
            <td>
                <xsl:value-of select="lb-core:referencedEntry/lg-cncpt:firstVersion"/>
            </td>
            <td>
                <xsl:value-of select="lb-core:referencedEntry/lg-cncpt:lastVersion"/>
            </td>
            <td>
                <xsl:value-of select="lb-core:referencedEntry/lg-cncpt:conceptStatus"/>
            </td>
            <td>
                <xsl:value-of select="lb-core:referencedEntry/lg-cncpt:definitionCount"/>
            </td>
            <td>
                <ul>
                    <xsl:apply-templates select="lb-core:referencedEntry/lg-cncpt:definition"/>
                </ul>
            </td>
            <td>
                <input class="cgButton" type="submit" value="use term" onclick="var myControl = document.getElementById('return_parameter'); myControl.value='urn:lsid:{$authority}:{$resource}:{$id}:{$version}';window.document.LexBIGform.action='{$destination-page}'"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="lb-core:referencedEntry/lg-cncpt:definition">
        <li>
            <xsl:if test="../lg-cncpt:definitionCount &gt; 1 and lg-cncpt:isPreferred = 'true'">
                (Preferred:) </xsl:if>
            <xsl:value-of select="lg-cncpt:text/lg-common:content"/>
        </li>
    </xsl:template>
</xsl:stylesheet>