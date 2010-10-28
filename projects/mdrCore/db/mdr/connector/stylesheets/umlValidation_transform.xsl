<xsl:stylesheet xmlns:mdr="http://ws.cagrid.org/exist/wsdl" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:UML="omg.org/UML1.3" xmlns="http://ws.cagrid.org/exist/wsdl" version="2.0">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>
    <!--<xsl:template match="/">
        <xsl:copy-of select="mdr:return/*"/>
        </xsl:template>-->
    <xsl:template match="/">
        <xsl:for-each select="//UML:Model">
            <xsl:for-each select="./UML:Namespace.ownedElement">
                <xsl:apply-templates select="//UML:Model/UML:Namespace.ownedElement"/>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="UML:Package">
        <xsl:for-each select="./UML:Namespace.ownedElement">
            <xsl:for-each select="./UML:Class/UML:Classifier.feature">
                <xsl:for-each select="./UML:Attribute">
                    <xsl:for-each select="./UML:ModelElement.taggedValue/UML:TaggedValue[@tag='CDERef']">
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </xsl:for-each>
            </xsl:for-each>
            <!-- <xsl:apply-templates select="//UML:Package/UML:Namespace.ownedElement"></xsl:apply-templates> -->
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="UML:Namespace.ownedElement">
        <xsl:for-each select="./UML:Package">
            <xsl:apply-templates select="//UML:Namespace.ownedElement/UML:Package"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>