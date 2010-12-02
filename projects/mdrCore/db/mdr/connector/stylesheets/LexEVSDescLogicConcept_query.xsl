<xsl:stylesheet xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cagrid.org/schema/query" version="2.0">
    <xsl:output method="text"/>
    <xsl:variable name="versionvalue" select="doc('http://lexevsapi51.nci.nih.gov/lexevsapi51/GetXML?query=org.LexGrid.codingSchemes.CodingScheme')/xlink:httpQuery/queryResponse/class[field[@name = '_codingSchemeName']='NCI_Thesaurus']/field[@name='_representsVersion']"/>
    <xsl:template match="/">
        <xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&amp;org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;codingSchemeName=NCI_Thesaurus&amp;codingSchemeVersion=',$versionvalue,'&amp;pageSize=20&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"/>
    </xsl:template>
</xsl:stylesheet>