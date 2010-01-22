<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cagrid.org/schema/query" version="2.0">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:template match="/">
    	<xsl:if test="/q:query/q:id">
    			<xsl:value-of select="concat(/q:query/q:serviceUrl, '/dataElementSummary.xquery?publicID=', replace(/q:query/q:id, ' ', '%20'), '&amp;start=',/q:query/q:startIndex,'&amp;num=',/q:query/q:numResults)"/>
    	</xsl:if>
        <xsl:if test="/q:query/q:term">
            <xsl:value-of select="concat(/q:query/q:serviceUrl, '/dataElementSummary.xquery?term=', replace(/q:query/q:term, ' ', '%20'),'&amp;start=',/q:query/q:startIndex,'&amp;num=',/q:query/q:numResults)"/>
        </xsl:if>
        <xsl:if test="/q:query/q:exactTerm">
            <xsl:value-of select="concat(/q:query/q:serviceUrl, '/dataElementSummary.xquery?exactTerm=', replace(/q:query/q:exactTerm, ' ', '%20'),'&amp;start=',/q:query/q:startIndex,'&amp;num=',/q:query/q:numResults)"/>
        </xsl:if>
        <xsl:if test="/q:query/q:contextList">
            <xsl:value-of select="concat(/q:query/q:serviceUrl, '/contextList.xquery')"/>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>

<!--
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:q="http://cagrid.org/schema/query">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <xsl:template match="/">
    	<xsl:if test="/q:query/q:term">
	        <xsl:value-of select="concat(/q:query/q:serviceUrl, '/dataElementSummary.xquery?term=', replace(/q:query/q:term, ' ', '%20'),'&amp;start=',/q:query/q:startIndex,'&amp;num=',/q:query/q:numResults)"/>
		</xsl:if>
		<xsl:if test="/q:query/q:exactTerm">
	        <xsl:value-of select="concat(/q:query/q:serviceUrl, '/dataElementSummary.xquery?term=', replace(/q:query/q:term, ' ', '%20'),'&amp;start=',/q:query/q:startIndex,'&amp;num=',/q:query/q:numResults)"/>
		</xsl:if>
		<xsl:if test="/q:query/q:contextList">
			<xsl:value-of
				select="concat(/q:query/q:serviceUrl, '/contextList.xquery')" />
		</xsl:if>		
    </xsl:template>
</xsl:stylesheet>

-->