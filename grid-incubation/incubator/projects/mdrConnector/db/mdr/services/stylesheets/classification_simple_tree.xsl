<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    
    <!--modified SJH 12/11/08 to accept proper SKOS!-->
    
    <!--xsl:variable name="root"/-->
    <xsl:param name="root"/>
    <xsl:template match="/">
        <xsl:apply-templates select="/rdf:RDF/rdf:Description[@rdf:about = $root]"/>
        <xsl:apply-templates select="/rdf:RDF/skos:Concept[@rdf:about = $root]"/>
    </xsl:template>
    <!--
    <xsl:template match="rdf:Description[@rdf:about = $root]">
        <xsl:element name="{substring-after(@rdf:about, '#')}">
            <xsl:attribute name="prefix"><xsl:value-of select="substring-before(@rdf:about, '#')"/></xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="substring-after(@rdf:about, '#')"/>
            </xsl:attribute>
            <xsl:attribute name="label">
                <xsl:value-of select="skos:prefLabel"/>
            </xsl:attribute>
            <xsl:apply-templates select="skos:narrower"/>
        </xsl:element>
    </xsl:template>
    -->
    <xsl:template match="rdf:Description|skos:Concept">
        <xsl:element name="node">
            <xsl:attribute name="prefix">
                <xsl:value-of select="substring-before(@rdf:about, '#')"/>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:value-of select="substring-after(@rdf:about, '#')"/>
            </xsl:attribute>
            <xsl:attribute name="label">
                <xsl:value-of select="skos:prefLabel"/>
            </xsl:attribute>
            <xsl:apply-templates select="skos:narrower"/>
            
            <!--find nodes asserting this node is broader-->
            <xsl:apply-templates select="//*[skos:broader/@rdf:resource = current()/@rdf:about]"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="skos:narrower">
        <xsl:apply-templates select="/rdf:RDF/*[@rdf:about = current()/@rdf:resource]"/>
    </xsl:template>
</xsl:stylesheet>