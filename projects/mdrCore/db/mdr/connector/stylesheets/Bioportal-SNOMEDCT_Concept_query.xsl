<xsl:stylesheet xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:q="http://cagrid.org/schema/query" version="2.0">
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
                <xsl:variable name="bioportalURL">
                    <xsl:value-of select="concat(/q:query/q:serviceUrl, '/search/',/q:query/q:term,'?email=rakesh.dhaval@osumc.edu&amp;')" disable-output-escaping="yes"/>
                    <xsl:text>ontologyids=1353</xsl:text>
                </xsl:variable>
                <xsl:value-of select="$bioportalURL"/>
            </xsl:when>
            <!-- this does not actually work because the query does not allow for src object -->
            <xsl:when test="normalize-space(/q:query/q:src/text()) != ''">
                <xsl:variable name="bioportalURL">
                    <xsl:value-of select="concat(/q:query/q:serviceUrl, '/search/',/q:query/q:term,'?email=rakesh.dhaval@osumc.edu&amp;')" disable-output-escaping="yes"/>
                    <xsl:text>ontologyids=1353</xsl:text>
                </xsl:variable>
                <xsl:value-of select="$bioportalURL"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="/q:query/q:term">
                    <xsl:variable name="bioportalURL">
                        <xsl:value-of select="concat(/q:query/q:serviceUrl, '/search/',/q:query/q:term,'?email=rakesh.dhaval@osumc.edu&amp;')" disable-output-escaping="yes"/>
                        <xsl:text>ontologyids=1353</xsl:text>
                    </xsl:variable>
                    <xsl:value-of select="$bioportalURL"/>
                </xsl:if>
                <xsl:if test="/q:query/q:id">
                    <xsl:variable name="bioportalURL">
                        <xsl:value-of select="concat(/q:query/q:serviceUrl, '/search/',/q:query/q:term,'?email=rakesh.dhaval@osumc.edu&amp;')" disable-output-escaping="yes"/>
                        <xsl:text>ontologyids=1353</xsl:text>
                    </xsl:variable>
                    <xsl:value-of select="$bioportalURL"/>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>