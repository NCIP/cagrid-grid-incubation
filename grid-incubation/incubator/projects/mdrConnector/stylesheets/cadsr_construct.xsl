<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="//queryResponse"/>
        <!--
        <xsl:apply-templates
            select="doc(concat(/parameters/url, '?query=DataElement&amp;DataElement[longName=',/parameters/term,']&amp;startIndex=',/parameters/startIndex,'&amp;resultCounter=',/parameters/resultCounter))/xlink:httpQuery/queryResponse"/>
            -->
    </xsl:template>

    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Expand the valueDomain and permissableValue node by reading from the given xlink -->
    <xsl:template match="field[@name=('valueDomain', 'permissibleValue')]">
        <field name="{@name}">
            <xsl:apply-templates select="doc(data(@xlink:href))/xlink:httpQuery/queryResponse/class"/>
        </field>
    </xsl:template>		
    
    <!-- Expand the specified node by reading from the given xlink -->
    <xsl:template match="class[@name='gov.nih.nci.cadsr.domain.EnumeratedValueDomain']/field[@name='valueDomainPermissibleValueCollection']">
        <field name="{@name}">
            <xsl:apply-templates select="doc(data(@xlink:href))/xlink:httpQuery/queryResponse/class"/>
        </field>
    </xsl:template>

    <xsl:template match="field[@name='valueMeaning']">
        <field name="{@name}">
            <xsl:apply-templates select="doc(data(@xlink:href))/xlink:httpQuery/queryResponse/class"/>
        </field>
    </xsl:template>
    
     <xsl:template match="class[@name=('gov.nih.nci.cadsr.domain.ValueMeaning', 'gov.nih.nci.cadsr.domain.ObjectClass','gov.nih.nci.cadsr.domain.Property')]/field[@name='conceptDerivationRule']">
        <field name="{@name}">
            <xsl:apply-templates select="doc(data(@xlink:href))/xlink:httpQuery/queryResponse/class"/>
        </field>
    </xsl:template>

    <xsl:template match="field[@name='componentConceptCollection']">
        <field name="{@name}">
            <xsl:apply-templates select="doc(data(@xlink:href))/xlink:httpQuery/queryResponse/class"/>
        </field>
    </xsl:template>
    
	<!-- Stop expanding after this one     -->
    <xsl:template match="class[@name='gov.nih.nci.cadsr.domain.ComponentConcept']/field[@name='concept']">
        <field name="{@name}">
            <xsl:copy-of select="doc(data(@xlink:href))/xlink:httpQuery/queryResponse/class"/>
        </field>
    </xsl:template>  
    
</xsl:stylesheet>
