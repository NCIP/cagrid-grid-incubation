<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cagrid.org/schema/query" version="2.0">
    <xsl:output method="text"/>
    <xsl:template match="/">
        <xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&amp;org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;codingSchemeName=NCI_Thesaurus&amp;codingSchemeVersion=09.09c&amp;pageSize=50&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"/>
    </xsl:template>
</xsl:stylesheet>
<!-- 
    http://lexevsapi.nci.nih.gov/lexevsapi50/GetXML?query=org.LexGrid.codingSchemes.CodingScheme&org.LexGrid.codingSchemes.CodingScheme[@_codingSchemeName=NCI_Thesaurus]
    <queryResponse>
    <recordCounter>1</recordCounter>
    <class name='org.LexGrid.codingSchemes.CodingScheme' recordNumber='1'>
    <field name='_codingSchemeName'>NCI_Thesaurus</field>
    <field name='_codingSchemeURI'>http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#</field>
    <field name='_formalName'>NCI Thesaurus</field>
    <field name='_defaultLanguage'>en</field>
    <field name='_approxNumConcepts'>86375</field>
    <field name='_representsVersion'>09.09c</field>
    <field name='_localNameList'>NCI Thesaurus; NCI_Thesaurus; Thesaurus.owl; ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl; </field>
    <field name='_sourceList'/>
    
09.09c

-->
<!--
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cagrid.org/schema/query" xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">
    <xsl:output method="text"/>
    <xsl:template match="/">
	 	<xsl:value-of select="concat(/q:query/q:serviceUrl, '?query=org.LexGrid.concepts.Concept,org.LexGrid.commonTypes.EntityDescription&org.LexGrid.commonTypes.EntityDescription[@_content=*',replace(/q:query/q:term, ' ', '%20'),'*]&codingSchemeName=NCI_Thesaurus&startIndex=',/q:query/q:startIndex,'&resultCounter=',/q:query/q:numResults)"/>
	 </xsl:template>
</xsl:stylesheet>
 -->