<xsl:stylesheet xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:variable name="buffer">
            <xsl:apply-templates select="*|text()|comment()"/>
        </xsl:variable>
        <xsl:copy-of select="$buffer" copy-namespaces="no"/>
    </xsl:template>
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="searchBean">
        <concept>
            <names>
                <id>rest.bioontology.org_BioPortal-ICD9_<xsl:value-of select="conceptIdShort"/>
                </id>
                <preferred>
                    <xsl:value-of select="preferredName"/>
                </preferred>
                <all-names>
                    <name>
                        
                    </name>
                </all-names>
            </names>
            <definitionURL>
                <xsl:attribute name="xlink:type">
                    <xsl:value-of select="'simple'"/>
                </xsl:attribute>
                <xsl:attribute name="xlink:href">
                    <xsl:value-of select="concat(definitionRef,'/',ontologyId,'/',encode-for-uri(conceptIdShort))"/>
                </xsl:attribute>
            </definitionURL>
            <htmlURL>
                <xsl:attribute name="xlink:type">
                    <xsl:value-of select="'simple'"/>
                </xsl:attribute>
                <xsl:attribute name="xlink:href">
                    <xsl:value-of select="concat(htmlURL,'/',ontologyId,'/','?conceptid=',conceptIdShort)"/>
                </xsl:attribute>
            </htmlURL>
            <original_data>
                <ontologyId>
                    <xsl:value-of select="ontologyId"/>
                </ontologyId>
                <conceptId>
                    <xsl:value-of select="conceptId"/>
                </conceptId>
                <conceptIdShort>
                    <xsl:value-of select="conceptIdShort"/>
                </conceptIdShort>
                <preferredName>
                    <xsl:value-of select="preferredName"/>
                </preferredName>
                <definitionRef>
                    <xsl:value-of select="definitionRef"/>
                </definitionRef>
            </original_data>
        </concept>
    </xsl:template>
    <xsl:template match="recordCounter">
        <recordCounter>
            <xsl:value-of select="."/>
        </recordCounter>
    </xsl:template> 
    <!-- Filter out extra nodes -->
</xsl:stylesheet>