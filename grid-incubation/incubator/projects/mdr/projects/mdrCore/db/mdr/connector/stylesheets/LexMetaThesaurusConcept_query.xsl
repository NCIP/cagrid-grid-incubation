<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:q="http://cagrid.org/schema/query" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes" method="text"/>
    <xsl:template match="/">
        <xsl:choose>
        	<!-- this does not actually work because the query does not allow for src object -->
            <xsl:when test="contains(normalize-space(/q:query/q:src/text()), ',')">
                <xsl:variable name="sources">
                    <xsl:for-each select="tokenize(normalize-space(/q:query/q:src/text()), ',')">
                        <xsl:value-of select="concat('[Abbreviation=', normalize-space(.), ']')"/>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&amp;org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;codingSchemeName=NCI_MetaThesaurus&amp;codingSchemeVersion=200904&amp;pageSize=50&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"/>
            </xsl:when>
            <!-- this does not actually work because the query does not allow for src object -->
            <xsl:when test="normalize-space(/q:query/q:src/text()) != ''">
                <xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&amp;org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;codingSchemeName=NCI_MetaThesaurus&amp;codingSchemeVersion=200904&amp;pageSize=50&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="/q:query/q:term">
                    <xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&amp;org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;codingSchemeName=NCI_MetaThesaurus&amp;codingSchemeVersion=200904&amp;pageSize=50&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"/>
                </xsl:if>
                <xsl:if test="/q:query/q:id">
                    <xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Entity&amp;org.LexGrid.concepts.Entity[@_entityCode=*',replace(/q:query/q:term, ' ', '%20'),']')"/>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
<!-- 
    http://lexevsapi.nci.nih.gov/lexevsapi50/GetXML?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&org.LexGrid.commonTypes.EntityDescription[@_content=*pinephrine*]&startIndex=0&resultCounter=50&codingSchemeName=NCI_MetaThesaurus&codingSchemeVersion=200904
-->