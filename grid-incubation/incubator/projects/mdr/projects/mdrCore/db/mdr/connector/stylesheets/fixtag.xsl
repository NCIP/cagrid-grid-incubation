<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="queryResponse">
        <result-set>
            <xsl:apply-templates select="class"/>
            <xsl:apply-templates select="next"/>
            <xsl:apply-templates select="pages"/>
        </result-set>
    </xsl:template>
    <xsl:template match="class">
        <xsl:element name="{@name}">
            <xsl:apply-templates select="field"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="field">
        <xsl:element name="{@name}">
            <xsl:copy-of select="@xlink:*"/>
            <xsl:choose>
                <xsl:when test="node()/node()">
                    <xsl:apply-templates select="class"/>
                </xsl:when>
                <xsl:when test="@xlink:*"/>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    <xsl:template match="previous">
        <xsl:element name="previous">
            <prevname>
                <xsl:value-of select="."/>
            </prevname>
            <prevurl>
                <xsl:apply-templates select="@xlink:href"/>
            </prevurl>
        </xsl:element>
    </xsl:template>
    <xsl:template match="next">
        <xsl:element name="next">
            <nextname>
                <xsl:value-of select="."/>
            </nextname>
            <nexturl>
                <xsl:apply-templates select="@xlink:href"/>
            </nexturl>
        </xsl:element>
    </xsl:template>
    <xsl:template match="pages">
        <pages>
            <xsl:for-each select="page">
                <page>
                    <pagenum>
                        <xsl:value-of select="."/>
                    </pagenum>
                    <pageurl>
                        <xsl:value-of select="@xlink:href"/>
                    </pageurl>
                </page>
            </xsl:for-each>
        </pages>
    </xsl:template>   
    
<!--    
    <xsl:template match="pages">
        <pages>
        <xsl:for-each select="page">
            <pagelink><xsl:value-of select="@xlink:href"/>
            </pagelink>
            <page><xsl:value-of select="." /></page>
        </xsl:for-each>
        </pages>
    </xsl:template>    
-->
</xsl:stylesheet><!--
    <xsl:template match="pages">         
    <xsl:for-each select="page">
    <page>
    <a href="{@xlink:href}">
    <xsl:value-of select="." />
    </a>
    </page>
    </xsl:for-each>        
    </xsl:template>    
    
    
    <xsl:template match="pages">
    <xsl:element name="pages">          
        <page>
            <xsl:apply-templates select="page"/>
        </page>
        <pagelink>
            <xsl:apply-templates select="page/@xlink:href"/>
        </pagelink>
    </xsl:element>
</xsl:template>
--><!-- 
    <xsl:for-each select="page">
    <page>
    <xsl:value-of select="page"/>
    
    </page>
<pagelink>
    <xsl:value-of select="pagelink"/>
</pagelink>
</xsl:for-each>

--><!--
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="queryResponse">
        <result-set>
            <xsl:apply-templates select="class"/>
            <xsl:apply-templates select="next"/>
            <xsl:apply-templates select="pages"/>         
        </result-set>
    </xsl:template>
    <xsl:template match="class">
        <xsl:element name="{@name}">
            <xsl:apply-templates select="field"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="field">
        <xsl:element name="{@name}">
            <xsl:copy-of select="@xlink:*"/>
            <xsl:choose>
                <xsl:when test="node()/node()">
                    <xsl:apply-templates select="class"/>
                </xsl:when>
                <xsl:when test="@xlink:*"/>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template> 
    <xsl:template match="next">
        <links>
            <xsl:apply-templates select="@xlink:href"/>
            <xsl:apply-templates select="pages"/>
        </links>
    </xsl:template>
    <xsl:template match="pages">
        <xsl:element name="pages">
            <page>
                <xsl:apply-templates select="page"/>
            </page>
            <pagelink>
                <xsl:apply-templates select="page/@xlink:href"/>
            </pagelink>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
--><!-- orig
 
<xsl:stylesheet xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
    	<xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="queryResponse">
	 	<result-set>
   		<xsl:apply-templates select="class"/>
		</result-set>
	</xsl:template>
	<xsl:template match="class">    
	    <xsl:element name="{@name}">    
	        <xsl:apply-templates select="field"/>
	    </xsl:element>
	</xsl:template>
	<xsl:template match="field">        
	    <xsl:element name="{@name}">
	        <xsl:copy-of select="@xlink:*"/>
	        <xsl:choose>
	            <xsl:when test="node()/node()">
	                <xsl:apply-templates select="class"/>
	            </xsl:when>
	            <xsl:when test="@xlink:*"/>
	            <xsl:otherwise>
	                <xsl:value-of select="."/>
	            </xsl:otherwise>
	        </xsl:choose>
	    </xsl:element>
	</xsl:template>
</xsl:stylesheet>
-->