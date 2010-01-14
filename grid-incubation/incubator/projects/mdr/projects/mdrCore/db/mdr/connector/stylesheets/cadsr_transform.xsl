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
    
    <xsl:template match="gov.nih.nci.cadsr.domain.Context">
    	<context>
            <name><xsl:value-of select="name"/></name>
            <description><xsl:value-of select="description"/></description>
            <version><xsl:value-of select="version"/></version>
        </context>
    </xsl:template>
    
    <xsl:template match="gov.nih.nci.cadsr.domain.DataElement">
        <!--<data-element xmlns="http://cagrid.org/schema/result-set">-->
        <data-element>
            <names>
                <id>cabio.nci.nih.gov_<xsl:value-of select="publicID"/>_<xsl:value-of select="version"/>
                </id>
                <preferred>
                    <xsl:value-of select="longName"/>
                </preferred>
                <all-names>
                    <name>
                        <xsl:value-of select="preferredName"/>
                    </name>
                    <name>
                        <xsl:value-of select="longName"/>
                    </name>
                </all-names>
            </names>
            <definition>
                <value>
                    <xsl:value-of select="preferredDefinition"/>
                </value>
            </definition>
            <workflow-status>
                <xsl:value-of select="workflowStatusName"/>
            </workflow-status>
            <registration-status>
                <xsl:value-of select="registrationStatus"/>
            </registration-status>
            <xsl:apply-templates select="context/gov.nih.nci.cadsr.domain.Context"/>
            <xsl:apply-templates select="valueDomain"/>
            <!--
                <xsl:apply-templates select="dataElementConcept/DataElementConcept/objectClass/ObjectClass"/>
                <xsl:apply-templates select="dataElementConcept/DataElementConcept/property/Property"/>
            -->
                <xsl:apply-templates select="dataElementConcept"/>
        </data-element>
    </xsl:template>
    <xsl:template match="valueDomain">
        <xsl:apply-templates/>
    </xsl:template> 
    
      <xsl:template match="dataElementConcept">
          <xsl:apply-templates select="gov.nih.nci.cadsr.domain.DataElementConcept/property/gov.nih.nci.cadsr.domain.Property"></xsl:apply-templates>
          <xsl:apply-templates select="gov.nih.nci.cadsr.domain.DataElementConcept/objectClass/gov.nih.nci.cadsr.domain.ObjectClass"></xsl:apply-templates>
          
      </xsl:template>
    
    <xsl:template match="NonenumeratedValueDomain|gov.nih.nci.cadsr.domain.NonenumeratedValueDomain">
        <!--<values xmlns="http://cagrid.org/schema/result-set">-->
        <values>
            <non-enumerated>
                <data-type>
                    <xsl:value-of select="datatypeName"/>
                </data-type>
                <units/>
            </non-enumerated>
        </values>
    </xsl:template>
    
    <xsl:template match="gov.nih.nci.cadsr.domain.EnumeratedValueDomain|EnumeratedValueDomain">
        <!--<values xmlns="http://cagrid.org/schema/result-set">-->
        <values>
            <enumerated>
                <xsl:apply-templates select="valueDomainPermissibleValueCollection/gov.nih.nci.cadsr.domain.ValueDomainPermissibleValue/permissibleValue/gov.nih.nci.cadsr.domain.PermissibleValue"/>
            </enumerated>
        </values>
    </xsl:template>
    
    <xsl:template match="valueDomainPermissibleValueCollection/gov.nih.nci.cadsr.domain.ValueDomainPermissibleValue/permissibleValue/gov.nih.nci.cadsr.domain.PermissibleValue">
        <!--<valid-value xmlns="http://cagrid.org/schema/result-set">-->
        <valid-value>
            <code>
                <xsl:value-of select="value"/>
            </code>
            <meaning>
                <xsl:value-of select="valueMeaning/gov.nih.nci.cadsr.domain.ValueMeaning/shortMeaning"/>
            </meaning>
            <xsl:apply-templates select="valueMeaning/gov.nih.nci.cadsr.domain.ValueMeaning/conceptDerivationRule/ConceptDerivationRule/componentConceptCollection"/>
        </valid-value>
    </xsl:template>
    
    <xsl:template match="gov.nih.nci.cadsr.domain.DataElementConcept/objectClass/gov.nih.nci.cadsr.domain.ObjectClass|gov.nih.nci.cadsr.domain.ObjectClass">
        <!--<data-element xmlns="http://cagrid.org/schema/result-set">-->
        <object-class>
            <names>
                <id>cabio.nci.nih.gov_<xsl:value-of select="publicID"/>_<xsl:value-of select="version"/>
                </id>
                <preferred>
                    <xsl:value-of select="longName"/>
                </preferred>
                <all-names>
                    <name>
                        <xsl:value-of select="preferredName"/>
                    </name>
                    <name>
                        <xsl:value-of select="longName"/>
                    </name>
                </all-names>
            </names>
            <definition>
                <value>
                    <xsl:value-of select="preferredDefinition"/>
                </value>
            </definition>
            <workflow-status>
                <xsl:value-of select="workflowStatusName"/>
            </workflow-status>
            <registration-status>
                <xsl:value-of select="registrationStatus"/>
            </registration-status>
            <xsl:apply-templates select="context/gov.nih.nci.cadsr.domain.Context"/>
            <xsl:apply-templates select="conceptDerivationRule"/>
        </object-class>
    </xsl:template>
    
    <xsl:template match="gov.nih.nci.cadsr.domain.DataElementConcept/property/gov.nih.nci.cadsr.domain.Property|gov.nih.nci.cadsr.domain.Property">
        <!--<data-element xmlns="http://cagrid.org/schema/result-set">-->
        <property>
            <names>
                <id>cabio.nci.nih.gov_<xsl:value-of select="publicID"/>_<xsl:value-of select="version"/>
                </id>
                <preferred>
                    <xsl:value-of select="longName"/>
                </preferred>
                <all-names>
                    <name>
                        <xsl:value-of select="preferredName"/>
                    </name>
                    <name>
                        <xsl:value-of select="longName"/>
                    </name>
                </all-names>
            </names>
            <definition>
                <value>
                    <xsl:value-of select="preferredDefinition"/>
                </value>
            </definition>
            <workflow-status>
                <xsl:value-of select="workflowStatusName"/>
            </workflow-status>
            <registration-status>
                <xsl:value-of select="registrationStatus"/>
            </registration-status>
            <xsl:apply-templates select="context/gov.nih.nci.cadsr.domain.Context"/>
            <xsl:apply-templates select="conceptDerivationRule"/>
        </property>
    </xsl:template>
    <!-- added -->
    <xsl:template match="conceptDerivationRule">
            <xsl:apply-templates select="gov.nih.nci.cadsr.domain.ConceptDerivationRule/componentConceptCollection"/>
    </xsl:template>	
    <!-- end added -->
    <xsl:template match="componentConceptCollection">
        <conceptCollection>
            <xsl:apply-templates select="gov.nih.nci.cadsr.domain.ComponentConcept"/>
        </conceptCollection>
    </xsl:template>	
    
    <xsl:template match="gov.nih.nci.cadsr.domain.ComponentConcept">
        <conceptRef>
            <id>
                <xsl:value-of select="concept/gov.nih.nci.cadsr.domain.Concept/preferredName"/>
            </id>
            <source>
                <xsl:value-of select="concept/gov.nih.nci.cadsr.domain.Concept/origin"/>
            </source>
            <name>
                <xsl:value-of select="concept/gov.nih.nci.cadsr.domain.Concept/longName"/>
            </name>
            <definition>
                <xsl:value-of select="concept/gov.nih.nci.cadsr.domain.Concept/preferredDefinition"/>
            </definition>           
        </conceptRef>
    </xsl:template>
    
    
    <!-- Filter out extra nodes -->
    <!-- removed dataElementConcept| -->
    <xsl:template match="recordCounter|questionCollection|workflowStatusDescription|unresolvedIssue|registrationStatus|publicID|concept|origin|modifiedBy|latestVersionIndicator|endDate|deletedIndicator|dateModified|dateCreated|createdBy|changeNote|beginDate|version|prequestionCollection|dataElementDerivationCollection|parentDataElementRelationshipsCollection|derivedDataElement|childDataElementRelationshipsCollection|context|administeredComponentClassSchemeItemCollection|designationCollection|referenceDocumentCollection|administeredComponentContactCollection|definitionCollection|validValueCollection|parentValueDomainRelationshipCollection|dataElementCollection|childValueDomainRelationshipCollection|childDataElementConceptRelationshipCollection|represention|conceptualDomain"/>
</xsl:stylesheet>
