<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
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

    <xsl:template match="MetaThesaurusConcept">
        <!--<concept xmlns="http://cancergrid.org/schema/result-set">-->
        <concept>
            <names>
                <id>US-NCICB-CACORE-EVS-METATHESAURUSCONCEPT-<xsl:value-of select="Cui"/></id>
                <preferred>
                    <xsl:value-of select="Name"/>
                </preferred>
                <all-names>
                    <xsl:apply-templates select="AtomCollection/Atom"/>
                </all-names>
            </names>
            <definition>
                <xsl:copy-of select="DefinitionCollection/Definition"/>
            </definition>
            <!--<properties  xmlns="http://cancergrid.org/schema/result-set">-->
            <properties>
                <xsl:apply-templates select="SemanticTypeCollection"/>
                <xsl:apply-templates select="SynonymCollection"/>
            </properties>
        </concept>
    </xsl:template>

    <xsl:template match="Atom">
        <!--<name xmlns="http://cancergrid.org/schema/result-set">-->
        <name>
            <!--
            <code>
                <xsl:value-of select="Code"/>
            </code>
            <name>
                <xsl:value-of select="Name"/>
            </name>
            <source>
                <xsl:value-of select="replace(Origin, '/', '-')"/>
            </source>
            -->
            <xsl:value-of select="Name"/> &#91;<xsl:value-of select="Origin"/>&#93;
        </name>
    </xsl:template>

    <xsl:template match="SemanticTypeCollection">
        <!--<property  xmlns="http://cancergrid.org/schema/result-set">-->
        <property>
            <name>Semantic_Type</name>
            <value>
                <xsl:value-of select="SemanticType/Name"/>
            </value>
        </property>
    </xsl:template>

    <xsl:template match="SynonymCollection">
        <xsl:variable name="synonym" select="tokenize(normalize-space(.), ';')"/>
        <xsl:for-each select="$synonym">
            <xsl:if test="normalize-space(.) != ''">
                <!--<property  xmlns="http://cancergrid.org/schema/result-set">-->
                <property>
                    <name>Synonym</name>
                    <value>
                        <xsl:value-of select="normalize-space(.)"/>
                    </value>
                </property>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
