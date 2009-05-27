<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:lexbig="http://lexbig.cancergrid.org/xsd"
    xmlns:collections="http://Collections.DataModel.LexBIG.LexGrid.org/xsd"
    xmlns:core="http://Core.DataModel.LexBIG.LexGrid.org/xsd"
    xmlns:commonTypes="http://commonTypes.LexGrid.org/xsd"
    xmlns:concepts="http://concepts.LexGrid.org/xsd">
    
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    
    <xsl:template match="/"> 
        <xsl:variable name="buffer">
            <!--<result-set xmlns="http://cancergrid.org/schema/result-set">-->
            <result-set>
                <xsl:apply-templates select="//collections:resolvedConceptReference"/>
            </result-set>
        </xsl:variable>
        <xsl:copy-of select="$buffer" copy-namespaces="no"/>
    </xsl:template>
    
    <xsl:template match="collections:resolvedConceptReference">
        <!--<concept xmlns="http://cancergrid.org/schema/result-set">-->
        <concept>
            <names>
                <id>GB-CANCERGRID-LEXBIG-<xsl:value-of select="core:conceptCode"/></id>
                <preferred><xsl:value-of select="core:referencedEntry/concepts:presentation[concepts:representationalForm='Preferred_Name']/concepts:text/commonTypes:content"/></preferred>
                <all-names>
                    <name>
                        <xsl:value-of select="core:referencedEntry/concepts:presentation[concepts:representationalForm='Preferred_Name']/concepts:text/commonTypes:content"/>
                    </name>
                </all-names>
            </names>
            <definition><xsl:value-of select="core:referencedEntry/concepts:definition[concepts:isPreferred='true']/concepts:text/commonTypes:content"/></definition>
            <!--<properties  xmlns="http://cancergrid.org/schema/result-set">-->
            <properties>
                <!--xsl:apply-templates select="core:referencedEntry/concepts:presentation"/-->
                <xsl:apply-templates select="core:referencedEntry/concepts:property"/>
            </properties>
        </concept>
    </xsl:template>    
    
    <xsl:template match="concepts:presentation">
        <!--<property  xmlns="http://cancergrid.org/schema/result-set">-->
        <property>
            <name><xsl:value-of select="concepts:representationalForm"/></name>
            <value><xsl:value-of select="concepts:text/commonTypes:content"/></value>
        </property>
    </xsl:template>
    
    <xsl:template match="concepts:presentation[concepts:representationalForm='Preferred_Name']"/>
    
    <xsl:template match="concepts:property">
        <!--<property  xmlns="http://cancergrid.org/schema/result-set">-->
        <property>
            <name><xsl:value-of select="concepts:property"/></name>
            <value><xsl:value-of select="concepts:text/commonTypes:content"/></value>
        </property>
    </xsl:template>
    

</xsl:stylesheet>