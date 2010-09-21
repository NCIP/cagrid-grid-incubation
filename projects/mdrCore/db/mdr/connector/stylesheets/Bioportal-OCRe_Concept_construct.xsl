<xsl:stylesheet xmlns:err="http://www.w3.org/2005/xqt-errors" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:fn="http://www.w3.org/2005/xpath-functions" version="2.0" exclude-result-prefixes="xs xdt err fn">
    <xsl:output method="xml" indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="data">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="page">
        <result-set>
            <xsl:apply-templates select="//numResultsTotal"/>
            <xsl:apply-templates select="//searchBean"/>
        </result-set>
    </xsl:template>
    <xsl:template match="numResultsTotal">
        <recordCounter>
            <xsl:value-of select="."/>
        </recordCounter>
    </xsl:template>
    <xsl:template match="searchBean">
        <searchBean>
            <xsl:apply-templates select="ontologyId"/>
            <xsl:apply-templates select="conceptId"/>
            <xsl:apply-templates select="conceptIdShort"/>
            <xsl:apply-templates select="preferredName"/>
        </searchBean>
    </xsl:template>
    <xsl:template match="ontologyId">
        <xsl:variable name="ontologyId" select="."/>
        <ontologyId>
            <xsl:value-of select="$ontologyId"/>
        </ontologyId>
    </xsl:template>
    <xsl:template match="conceptId">
        <conceptId>
            <xsl:value-of select="."/>
        </conceptId>
    </xsl:template>
    <xsl:template match="conceptIdShort">
        <xsl:variable name="conceptIdShort" select="."/>
        <conceptIdShort>
            <xsl:value-of select="$conceptIdShort"/>
        </conceptIdShort>
    </xsl:template>
    <xsl:template match="preferredName">
        <preferredName>
            <xsl:value-of select="."/>
        </preferredName>
        <definitionRef>
            <xsl:value-of select="'http://rest.bioontology.org/bioportal/virtual/ontology'"/>
        </definitionRef>
        <htmlURL>
            <xsl:value-of select="'http://bioportal.bioontology.org/visualize/virtual'"/>
        </htmlURL>
    </xsl:template>
    <xsl:template match="accessedResource|accessDate"/>
    
    
</xsl:stylesheet>