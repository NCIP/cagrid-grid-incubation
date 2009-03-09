<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:q="http://cancergrid.org/schema/query">

    <xsl:output indent="yes" omit-xml-declaration="yes" method="text"/>

    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="contains(normalize-space(/q:query/q:src/text()), ',')">
                <xsl:variable name="sources">
                    <xsl:for-each select="tokenize(normalize-space(/q:query/q:src/text()), ',')">
                        <xsl:value-of select="concat('[Abbreviation=', normalize-space(.), ']')"/>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:value-of
                    select="concat(/q:query/q:serviceUrl, '?query=MetaThesaurusConcept&amp;MetaThesaurusConcept[name=*',replace(/q:query/q:term, ' ', '%20'),'*]/Source', $sources ,'&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"
                />
            </xsl:when>
            <xsl:when test="normalize-space(/q:query/q:src/text()) != ''">
                <xsl:value-of
                    select="concat(/q:query/q:serviceUrl, '?query=MetaThesaurusConcept&amp;MetaThesaurusConcept[name=*',replace(/q:query/q:term, ' ', '%20'),'*]/Source[Abbreviation=', /q:query/q:src,']&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"
                />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of
                    select="concat(/q:query/q:serviceUrl, '?query=MetaThesaurusConcept&amp;MetaThesaurusConcept[name=*',replace(/q:query/q:term, ' ', '%20'),'*]&amp;startIndex=',/q:query/q:startIndex,'&amp;resultCounter=',/q:query/q:numResults)"
                />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>