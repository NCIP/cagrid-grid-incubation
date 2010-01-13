<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:q="http://cagrid.org/schema/query">
    <xsl:output method="text"/>
    
    <xsl:template match="/">
    
        <xsl:if test="/q:query/q:id">
			<xsl:if test="/q:query/q:version">
				<xsl:value-of
					select="concat(/q:query/q:serviceUrl, '?query=ObjectClass&amp;ObjectClass[publicID=', replace(/q:query/q:id, ' ', '%20'),'][version=', replace(/q:query/q:version, ' ', '%20'), ']&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)" />
			</xsl:if>
			<xsl:if test="not(/q:query/q:version)">
				<xsl:value-of
					select="concat(/q:query/q:serviceUrl, '?query=ObjectClass&amp;ObjectClass[publicID=', replace(/q:query/q:id, ' ', '%20'),']&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)" />
			</xsl:if>
		</xsl:if>

		<xsl:if test="/q:query/q:term">
			<xsl:value-of
				select="concat(/q:query/q:serviceUrl, '?query=ObjectClass&amp;ObjectClass[longName=*', replace(/q:query/q:term, ' ', '%20'),'*]&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)" />
		</xsl:if>
    	
    	<xsl:if test="/q:query/q:contextList">
    		<xsl:value-of
    			select="concat(/q:query/q:serviceUrl, '?query=Context&amp;Context')" />
    	</xsl:if>
		
    </xsl:template>

</xsl:stylesheet>
