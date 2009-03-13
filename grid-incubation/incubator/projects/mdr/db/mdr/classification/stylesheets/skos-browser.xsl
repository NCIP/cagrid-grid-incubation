<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:iso="http://www.isotc211.org/19115/" xmlns:iaaaterm="http://iaaa.cps.unizar.es/iaaaterms/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:dcterms="http://purl.org/dc/terms/" version="2.0">
    <xsl:include href="lib-rendering.xsl"/>
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
            <head>
                <title>Terminologies and Ontologies in use</title>
                <link rel="stylesheet" href="../web/treeview/treeview.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/cancergrid-style.css" type="text/css"/>
                <script src="treeview/treeview.js" type="text/javascript"/>
            </head>
            <body>
                <xsl:call-template name="page-header"/>
                <br/>
                <h2 class="title">Terminologies and Ontologies in use</h2>
                <br/>
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="schemes-in-use">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="scheme">
        <xsl:apply-templates select="thesaurusdc"/>
        <xsl:apply-templates select="rdf:RDF"/>
    </xsl:template>
    <xsl:template match="thesaurusdc">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="*">
        <tr>
            <td>
                <xsl:value-of select="local-name()"/>
            </td>
            <td>
                <xsl:value-of select="."/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="rdf:RDF">
        <table class="layout">
            <xsl:apply-templates>
                <xsl:sort select="skos:prefLabel"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <xsl:template match="rdf:Description">
        <tr>
            <td colspan="3" class="left_header_cell">
                <a name="{@rdf:about}">term: <xsl:value-of select="skos:prefLabel"/>
                </a>
            </td>
        </tr>
        <xsl:apply-templates mode="rdf">
            <xsl:sort select="local-name()"/>
        </xsl:apply-templates>
    </xsl:template>
    <xsl:template match="*" mode="rdf">
        <tr>
            <td/>
            <td>
                <xsl:value-of select="local-name()"/>
            </td>
            <td>
                <a href="#{@rdf:resource}">
                    <xsl:call-template name="preferred-name">
                        <xsl:with-param name="resource" select="@rdf:resource"/>
                    </xsl:call-template>
                </a>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="preferred-name">
        <xsl:param name="resource"/>
        <xsl:variable name="preferred-name" select="//rdf:Description[@rdf:about=$resource]/skos:prefLabel"/>
        <xsl:choose>
            <xsl:when test="exists($preferred-name)">
                <xsl:value-of select="//rdf:Description[@rdf:about=$resource]/skos:prefLabel"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$resource"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="skos:prefLabel|skos:definition|skos:altLabel" mode="rdf">
        <tr>
            <td/>
            <td>
                <xsl:value-of select="local-name()"/>
            </td>
            <td>
                <xsl:value-of select="."/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>