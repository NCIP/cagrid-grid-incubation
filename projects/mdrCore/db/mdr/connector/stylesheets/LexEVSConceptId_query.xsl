<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cagrid.org/schema/query" version="2.0">
    <xsl:output method="text"/>
    <xsl:template match="/">
        <xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Entity&amp;org.LexGrid.concepts.Entity[@_entityCode=', replace(/q:query/q:term, ' ', '%20'),']')"/>
    </xsl:template>
</xsl:stylesheet>
<!-- 
http://lexevsapi51.nci.nih.gov/lexevsapi51/GetXML?query=org.LexGrid.concepts.Entity&org.LexGrid.concepts.Entity[@_entityCode=C25155]
<xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;codingSchemeName=NCI_Thesaurus&amp;codingSchemeVersion=09.09c&amp;pageSize=50&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"/>
-->