<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cagrid.org/schema/query" xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">
    <xsl:output method="text"/>
    <xsl:template match="/">
	 	<xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&amp;org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;codingSchemeName=NCI_Thesaurus&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"/>
	 </xsl:template>
</xsl:stylesheet>