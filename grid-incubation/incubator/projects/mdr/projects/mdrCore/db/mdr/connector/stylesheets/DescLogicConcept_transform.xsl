<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" exclude-result-prefixes="#all">
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
    
    <xsl:template match="DescLogicConcept">
        <!--<concept xmlns="http://cancergrid.org/schema/result-set">-->
        <concept>
            <names>
                <id>US-NCICB-CACORE-EVS-DESCLOGICCONCEPT-<xsl:value-of select="Code"/></id>
                <preferred><xsl:value-of select="PropertyCollection/Property[Name='Preferred_Name']/Value"/></preferred>
                <all-names>
                    <name><xsl:value-of select="PropertyCollection/Property[Name='Preferred_Name']/Value"/></name>
                    <name><xsl:value-of select="Name"/></name>
                </all-names>
            </names>
            <definition><xsl:value-of select="PropertyCollection/Property[Name='DEFINITION']/Value"/></definition>
            <xsl:apply-templates select="PropertyCollection"/>
        </concept>
    </xsl:template>
    
    <xsl:template match="PropertyCollection">
        <!--<properties  xmlns="http://cancergrid.org/schema/result-set">-->
        <properties>
            <xsl:for-each select="Property">
                <xsl:sort select="Name"/>
                <xsl:if test="not(contains(Name, 'DEFINITION')) and not(contains(Name, 'Preferred_Name'))">
                    <xsl:apply-templates select="."/>                    
                </xsl:if>
            </xsl:for-each>
        </properties>
    </xsl:template>
    
    <xsl:template match="Property">
        <!--<property  xmlns="http://cancergrid.org/schema/result-set">-->
        <property>
                <name><xsl:value-of select="Name"/></name>
                <value><xsl:value-of select="Value"/></value>
            </property>
    </xsl:template>
    
    <!-- Filter out extra nodes -->
    <xsl:template match="SemanticTypeVector|AssociationCollection|RoleCollection|Vocabulary|SecurityToken|TreeNode|EdgeProperties|HasChildren|HasParents|InverseAssociationCollection|InverseRoleCollection"/>
</xsl:stylesheet>
