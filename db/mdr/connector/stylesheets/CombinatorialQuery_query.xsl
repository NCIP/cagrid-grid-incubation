<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cancergrid.org/schema/query" version="2.0">
    <xsl:template match="/">
        <resolveSet xmlns="http://lexbig.cancergrid.org/xsd">
            <sCodingScheme>NCI_Thesaurus</sCodingScheme>
            <sCodingSchemeVersion>&#160;</sCodingSchemeVersion>
            <sCodingSchemeTag>&#160;</sCodingSchemeTag>
            <activeConceptsOnly>true</activeConceptsOnly>
            <sDesignationTxt>
                <xsl:value-of select="/q:query/q:term"/>
            </sDesignationTxt>
            <sDesignationOption>PREFERRED_ONLY</sDesignationOption>
            <sDesignationAlgorithm>DoubleMetaphoneLuceneQuery</sDesignationAlgorithm>
            <sDesignationLanguage>&#160;</sDesignationLanguage>
            <sPropertyList>&#160;</sPropertyList>
            <sSourceList>&#160;</sSourceList>
            <sContextList>&#160;</sContextList>
            <sNameAndValueList>&#160;</sNameAndValueList>
            <sPropertyMatchText>&#160;</sPropertyMatchText>
            <sPropertyMatchAlgorithm>&#160;</sPropertyMatchAlgorithm>
            <sPropertyMatchLanguage>&#160;</sPropertyMatchLanguage>
            <iReturnItemIndexFrom>
                <xsl:value-of select="/q:query/q:startIndex"/>
            </iReturnItemIndexFrom>
            <iReturnItemIndexTo>
                <xsl:value-of select="/q:query/q:numResults"/>
            </iReturnItemIndexTo>
        </resolveSet>
    </xsl:template>
</xsl:stylesheet>