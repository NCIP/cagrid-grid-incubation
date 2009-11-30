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
    <xsl:template match="org.LexGrid.concepts.Concept">
		<!--<concept xmlns="http://cagrid.org/schema/result-set">-->
        <concept>
            <names>
                <id>US-NCICB-CACORE-EVS-METATHESAURUSCONCEPT-<xsl:value-of select="_entityCode"/>
                </id>
                <preferred>
                    <xsl:value-of select="_presentationList/org.LexGrid.concepts.Presentation[_isPreferred='true']/_value/org.LexGrid.commonTypes.Text/_content"/>
                </preferred>
                <all-names>
                    <name>
                        <xsl:value-of select="_presentationList/org.LexGrid.concepts.Presentation[_isPreferred='true']/_value/org.LexGrid.commonTypes.Text/_content"/>
                    </name>
                </all-names>
            </names>
            <definition>
                <xsl:value-of select="_definitionList/org.LexGrid.concepts.Definition/_value/org.LexGrid.commonTypes.Text/_content"/>
            </definition>
        </concept>
    </xsl:template>
	<!-- Filter out extra nodes -->
    <xsl:template match="SemanticTypeVector|AssociationCollection|RoleCollection|Vocabulary|SecurityToken|TreeNode|EdgeProperties|HasChildren|HasParents|InverseAssociationCollection|InverseRoleCollection"/>
</xsl:stylesheet>