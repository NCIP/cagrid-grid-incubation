<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179" version="2.0">
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="result-set">
        <html>
            <head>
                <title>Searching for a <xsl:value-of select="./@friendly-name"/>
                </title>
                <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/cancergrid-style.css" type="text/css"/>
                <link rel="stylesheet" href="../classification/stylesheets/treeview.css" type="text/css"/>
                <link rel="search" type="application/opensearchdescription+xml" title="CancerGrid Data Element Search" href="../web/cde_search.xquery"/>
                <script language="javascript" src="../xforms/popup.js"/>
            </head>
            <body>
                <form name="select-item" class="cancergridForm" action="popup-search.xquery">
                    <input type="hidden" name="type" value="{./@type}"/>
                    <input type="hidden" name="element" value="{./@element}"/>
                    <table class="layout">
                        <tr>
                            <td>Search Phrase</td>
                            <td>
                                <input type="text" name="phrase" value="{./@phrase}"/>
                            </td>
                            <td>
                                <input type="submit" value="Submit query" class="cgButton"/>
                            </td>
                        </tr>
                        <xsl:apply-templates/>
                    </table>
                    <xsl:call-template name="other-results">
                        <xsl:with-param name="start" select="@start"/>
                        <xsl:with-param name="count" select="@count"/>
                        <xsl:with-param name="count-docs" select="@count-results"/>
                        <xsl:with-param name="node" select="@node"/>
                        <xsl:with-param name="phrase" select="@phrase"/>
                    </xsl:call-template>
                </form>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="result">
        <tr>
            <td>
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:value-of select="."/>
            </td>
            <td>
                <xsl:call-template name="action-button">
                    <xsl:with-param name="control" select="@target-node-name"/>
                    <xsl:with-param name="value" select="@id"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="action-button">
        <xsl:param name="value" as="xs:string"/>
        <xsl:param name="control" as="xs:string"/>
        <xsl:variable name="jscript-call">updateOpener('<xsl:value-of select="$control"/>','<xsl:value-of select="$value"/>')</xsl:variable>
        <xsl:element name="input">
            <xsl:attribute name="class">cgButton</xsl:attribute>
            <xsl:attribute name="type">submit</xsl:attribute>
            <xsl:attribute name="name">update</xsl:attribute>
            <xsl:attribute name="value">use this record</xsl:attribute>
            <xsl:attribute name="onclick" select="$jscript-call"/>
        </xsl:element>
    </xsl:template>
    <xsl:template name="other-results">
        <xsl:param name="node" as="xs:string"/>
        <xsl:param name="phrase" as="xs:string"/>
        <xsl:param name="start" as="xs:integer"/>
        <xsl:param name="count" as="xs:integer"/>
        <xsl:param name="count-docs" as="xs:integer"/>
        <xsl:variable name="start-url">
            <a href="{concat(&#34;popup-search.xquery?start=1&amp;node=&#34;,$node,&#34;&amp;phrase=&#34;,$phrase)}">start</a>
        </xsl:variable>
        <xsl:variable name="previous-url">
            <a href="{concat(&#34;popup-search.xquery?start=&#34;,$start - $count,&#34;&amp;node=&#34;,$node,&#34;&amp;phrase=&#34;,$phrase)}">previous</a>
        </xsl:variable>
        <xsl:variable name="next-url">
            <a href="{concat(&#34;popup-search.xquery?start=&#34;,$start + $count,&#34;&amp;node=&#34;,$node,&#34;&amp;phrase=&#34;,$phrase)}">next</a>
        </xsl:variable>
        <table class="layout">
            <tr>
                <td>
                    <xsl:choose>
                        <xsl:when test="$start&gt;1">
                            <xsl:value-of select="$start-url"/>
                        </xsl:when>
                        <xsl:otherwise>start</xsl:otherwise>
                    </xsl:choose>
                </td>
                <td>
                    <xsl:choose>
                        <xsl:when test="$start&gt;1">
                            <xsl:value-of select="$previous-url"/>
                        </xsl:when>
                        <xsl:otherwise>previous</xsl:otherwise>
                    </xsl:choose>
                </td>
                <td>
                    <xsl:choose>
                        <xsl:when test="$count-docs &gt; $start + $count">
                            <xsl:value-of select="$next-url"/>
                        </xsl:when>
                        <xsl:otherwise>next</xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
        </table>
    </xsl:template>
</xsl:stylesheet>