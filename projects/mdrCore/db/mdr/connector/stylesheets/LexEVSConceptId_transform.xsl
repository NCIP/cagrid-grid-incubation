<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:variable name="buffer">
            <xsl:apply-templates select="*|text()|comment()"/>
        </xsl:variable>
        <xsl:copy-of select="$buffer/*" copy-namespaces="no"/>
    </xsl:template>
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="org.LexGrid.concepts.Entity">
            <name>
                <xsl:value-of select="_entityCode"/>
            </name>
            <longName>
                <xsl:value-of select="_presentationList/org.LexGrid.concepts.Presentation/_isPreferred[. = 'true']/../_value/org.LexGrid.commonTypes.Text/_content"/>
            </longName>
            <preferredDefinition>
                <xsl:value-of select="_definitionList/org.LexGrid.concepts.Definition/_isPreferred[. = 'true']/../_value/org.LexGrid.commonTypes.Text/_content"/>
            </preferredDefinition>
    </xsl:template>
    <!-- Filter out extra nodes -->
    <xsl:template match="pages|_propertyList|recordCounter|questionCollection|workflowStatusDescription|unresolvedIssue|registrationStatus|publicID|concept|origin|modifiedBy|latestVersionIndicator|endDate|deletedIndicator|dateModified|dateCreated|createdBy|changeNote|beginDate|version|prequestionCollection|dataElementDerivationCollection|parentDataElementRelationshipsCollection|derivedDataElement|childDataElementRelationshipsCollection|context|administeredComponentClassSchemeItemCollection|designationCollection|referenceDocumentCollection|administeredComponentContactCollection|definitionCollection|validValueCollection|parentValueDomainRelationshipCollection|dataElementCollection|childValueDomainRelationshipCollection|childDataElementConceptRelationshipCollection|represention|conceptualDomain"/>
</xsl:stylesheet>