<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:lb-coll="http://Collections.DataModel.LexBIG.LexGrid.org/xsd" version="1.0">
    <!-- This transform is used by rendering routines to visualize LexBIG match
algorithms and a UI control for selecting an algorithm from them.
Input: a LexBIG match algorithm list from the CancerGrig LexBIG web-service.
Output: HTML.
-->
    <xsl:param name="param-select"/>
    <xsl:template xmlns:lb-impl="http://Impl.LexBIG.LexGrid.org/xsd" match="//lb-impl:getMatchAlgorithmsResponse/lb-impl:return">
        <table class="invisible">
            <thead>
                <tr>
                    <td class="light-rule"/>
                    <td class="light-rule">Designation match algorithm</td>
                    <td class="light-rule">Description</td>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="lb-coll:moduleDescription"/>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template xmlns:lb-inter="http://InterfaceElements.DataModel.LexBIG.LexGrid.org/xsd" match="lb-coll:moduleDescription">
        <tr>
            <td>
                <xsl:if test="$param-select != lb-inter:name">
                    <input type="radio" name="sDesignationAlgorithm" value="{lb-inter:name}"/>
                </xsl:if>
                <xsl:if test="$param-select = lb-inter:name">
                    <input type="radio" name="sDesignationAlgorithm" value="{lb-inter:name}" checked="on"/>
                </xsl:if>
            </td>
            <td>
                <xsl:value-of select="lb-inter:name"/>
            </td>
            <td>
                <xsl:value-of select="lb-inter:description"/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>