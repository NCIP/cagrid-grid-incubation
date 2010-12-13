<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:openMDR="http://www.cagrid.org/schema/openMDR" xmlns:cgResolver="http://www.cagrid.org/schema/cgResolver" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:iaaaterm="http://iaaa.cps.unizar.es/iaaaterms/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns="http://www.w3.org/1999/xhtml" xmlns:ISO11179="http://www.cagrid.org/schema/ISO11179" version="2.0">
    <xsl:param name="user" select="guest"/>
    <xsl:param name="title" select="title"/>
    <xsl:param name="footer">true</xsl:param>
    <xsl:param name="show-edit-button">false</xsl:param>
    <xsl:param name="show-supersede-button">false</xsl:param>
    <xsl:param name="id"/>
    <xsl:param name="as-xml-link"/>
    <xsl:param name="edit-link"/>
    <xsl:param name="can-supersede" select="false()"/>
    <xsl:variable name="admin-item-identifier">
        <xsl:call-template name="admin-item-identifier"/>
    </xsl:variable>
    
    <!--generic webpage template -->
    <xsl:template match="/">
        <html lang="en" xml:lang="en">
            <div id="outer"/>
            <head>
                <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
                <link rel="stylesheet" href="../classification/stylesheets/treeview.css" type="text/css"/>
                <script src="../classification/stylesheets/treeview.js" type="text/javascript"/>
                <script type="text/javascript" src="../web/tabs/tabber.js"/>
                <link rel="stylesheet" href="../web/tabs/tabs.css" type="text/css" media="screen"/>
                <link rel="search" type="application/opensearchdescription+xml" title="Data Element Search" href="../web/cde_search.xquery"/>
                <script src="../web/stylesheets/web.js" type="text/javascript"/>
                <title>
                    <xsl:value-of select="$title"/>
                </title>
            </head>
            <body>
                <div id="central">
                    <a href="../web/homepage.xquery">
                        <img src="../web/images/openMDR-small-button.jpg" alt="openMDR" style="border: 1px white solid;"/>
                    </a>
                    <xsl:call-template name="welcome"/>
                    <xsl:call-template name="page-header"/>
                    <br/>
                    <div class="admin_item_header">
                        <xsl:value-of select="$title"/>
                    </div>
                    <br/>
                    <xsl:call-template name="pane-header"/>
                    <xsl:apply-templates/>
                    <xsl:if test="$footer='true'">
                        <xsl:call-template name="copyright"/>
                        <xsl:call-template name="XHTML-transitional"/>
                    </xsl:if>
                </div>
            </body>
        </html>
    </xsl:template>
    
    <!-- page header rendering-->
    <xsl:template name="page-header">
        <div id="navtop">
            <table>
                <tr align="left" valign="top">
                    <td>
                        <table>
                            <xsl:choose>
                                <xsl:when test="$user='guest'">
                                    <td>
                                        <xsl:call-template name="css-rollover-button">
                                            <xsl:with-param name="text">MAINTENANCE</xsl:with-param>
                                            <xsl:with-param name="alt">maintenance</xsl:with-param>
                                            <xsl:with-param name="link">../web/login.xquery</xsl:with-param>
                                        </xsl:call-template>
                                    </td>
                                </xsl:when>
                                <xsl:otherwise>
                                    <td>
                                        <xsl:call-template name="css-rollover-button">
                                            <xsl:with-param name="text">MAINTENANCE</xsl:with-param>
                                            <xsl:with-param name="alt">maintenance</xsl:with-param>
                                            <xsl:with-param name="link">../edit/maintenance.xquery</xsl:with-param>
                                        </xsl:call-template>
                                    </td>
                                </xsl:otherwise>
                            </xsl:choose>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">CONTENTS</xsl:with-param>
                                    <xsl:with-param name="alt">contents</xsl:with-param>
                                    <xsl:with-param name="link">../web/contents.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">SEARCH</xsl:with-param>
                                    <xsl:with-param name="alt">search</xsl:with-param>
                                    <xsl:with-param name="link">../web/search.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">REFERENCE DOCUMENTS</xsl:with-param>
                                    <xsl:with-param name="alt">reference documents</xsl:with-param>
                                    <xsl:with-param name="link">../web/reference-document-management.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">ANNOTATED MODELS</xsl:with-param>
                                    <xsl:with-param name="alt">annotated models</xsl:with-param>
                                    <xsl:with-param name="link">../web/annotated-model-management.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">openMDR WIKI</xsl:with-param>
                                    <xsl:with-param name="alt">openMDR wiki</xsl:with-param>
                                    <xsl:with-param name="link">https://cagrid.org/display/MDR/Home</xsl:with-param>
                                    <xsl:with-param name="target">_blank</xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </table>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>
    
    <!--renders the welcome message -->
    <xsl:template name="welcome">
        <div class="login">
            <xsl:choose>
                <xsl:when test="$user='guest'">
                    <a class="log_login" href="../web/login.xquery">login</a>
                </xsl:when>
                <xsl:otherwise>
                Welcome: <xsl:value-of select="$user"/>   || 
                    <a href="../web/login.xquery">logout</a>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>
    
    <!--finds the appropriate xquery to render the content-->
    <xsl:template name="html-anchor">
        <xsl:param name="collection"/>
        <xsl:param name="id"/>
        <xsl:param name="text"/>
        <xsl:variable name="reg-auth" select="sub"/>
        <a href="{concat('../web/',$collection,'.xquery?compound_id=',$id)}">
            <xsl:value-of select="$text"/>
        </a>
    </xsl:template>
    
    <!--calculates an admin-item-identifier-->
    <xsl:template name="admin-item-identifier">
        <xsl:choose>
            <xsl:when test="//openMDR:data_element_complete">
                <xsl:value-of select="/openMDR:data_element_complete/openMDR:Data_Element/@item_registration_authority_identifier"/>_<xsl:value-of select="/openMDR:data_element_complete/openMDR:Data_Element/@data_identifier"/>_<xsl:value-of select="//openMDR:data_element_complete/openMDR:Data_Element/@version"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="//@item_registration_authority_identifier"/> _<xsl:value-of select="//@data_identifier"/> _<xsl:value-of select="//@version"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- renders the copyright notice on the bottom of the page-->
    <xsl:template name="copyright">
        <p class="copyright"/>
    </xsl:template>
    <xsl:template name="css-rollover-button">
        <xsl:param name="link"/>
        <xsl:param name="alt"/>
        <xsl:param name="text"/>
        <xsl:param name="onclick" required="no"/>
        <xsl:param name="target" required="no">default</xsl:param>
        <xsl:element name="div">
            <xsl:attribute name="class">cssnav</xsl:attribute>
            <xsl:element name="a">
                <xsl:if test="$link &gt; ''">
                    <xsl:attribute name="href" select="$link"/>
                    <xsl:if test="$target != 'default'">
                        <xsl:attribute name="target" select="$target"/>
                    </xsl:if>
                </xsl:if>
                <!--                <xsl:attribute name="target">_top</xsl:attribute>-->
                <xsl:if test="$onclick&gt;''">
                    <xsl:attribute name="onclick" select="$onclick"/>
                </xsl:if>
                <img src="../web/images/newMenuButtonSkyBlue.jpg" alt="{$alt}" height="1000"/>
                <span>
                    <xsl:value-of select="$text"/>
                </span>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="css-rollover-button-inner">
        <xsl:param name="link"/>
        <xsl:param name="alt"/>
        <xsl:param name="text"/>
        <xsl:param name="onclick" required="no"/>
        <xsl:param name="target" required="no">default</xsl:param>
        <xsl:element name="div">
            <xsl:attribute name="class">cssnavinner</xsl:attribute>
            <xsl:element name="a">
                <xsl:if test="$link &gt; ''">
                    <xsl:attribute name="href" select="$link"/>
                    <xsl:if test="$target != 'default'">
                        <xsl:attribute name="target" select="$target"/>
                    </xsl:if>
                </xsl:if>
                <!--                <xsl:attribute name="target">_top</xsl:attribute>-->
                <xsl:if test="$onclick&gt;''">
                    <xsl:attribute name="onclick" select="$onclick"/>
                </xsl:if>
                <img src="../web/images/newMenuButtonLight.jpg" alt="{$alt}" height="1000"/>
                <span>
                    <xsl:value-of select="$text"/>
                </span>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="pane-header">
        <table>
            <tr>
                <xsl:if test="$show-supersede-button='true' and $user!='guest'">
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">supersede</xsl:with-param>
                            <xsl:with-param name="alt">supersede</xsl:with-param>
                            <xsl:with-param name="link">
                                <xsl:value-of select="concat('../edit/supersede_admin_item.xquery?supersede-source=', $id)"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </xsl:if>
                <xsl:if test="$show-edit-button='true' and $user!='guest'">
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">edit</xsl:with-param>
                            <xsl:with-param name="alt">edit</xsl:with-param>
                            <xsl:with-param name="link">
                                <xsl:value-of select="concat($edit-link,'?id=', $id)"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </xsl:if>
                <xsl:if test="$as-xml-link">
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">as XML</xsl:with-param>
                            <xsl:with-param name="alt">as XML</xsl:with-param>
                            <xsl:with-param name="link">
                                <xsl:choose>
                                    <xsl:when test="$id">
                                        <xsl:value-of select="concat($as-xml-link, '?compound_id=', $id, '&amp;as-xml=true')"/>
                                    </xsl:when>
                                    <xsl:when test="contains($as-xml-link,'?')">
                                        <xsl:value-of select="concat($as-xml-link, '&amp;as-xml=true')"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat($as-xml-link, '?as-xml=true')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </xsl:if>
                <td/>
            </tr>
        </table>
    </xsl:template>
    
    <!--xhtml bit -->
    <xsl:template name="XHTML-transitional">
        <p align="right">
            <a href="http://validator.w3.org/check?uri=referer">
                <img style="border:0;width:88px;height:31px" src="../web/images/valid-xhtml-blue.gif" alt="Valid XHTML 1.0 Transitional"/>
            </a>
            <a href="http://jigsaw.w3.org/css-validator/check/referer">
                <img style="border:0;width:88px;height:31px" src="../web/images/vcss-blue.gif" alt="Valid CSS!"/>
            </a>
        </p>
    </xsl:template>
    
    <!--does uri resolution-->
    <xsl:template name="uri-resolver">
        <xsl:param name="urn"/>
        <xsl:variable name="resource-urn" select="//cgResolver:resource/@urn[starts-with($urn,.)]"/>
        <xsl:variable name="resource-uri" select="//cgResolver:resource[starts-with($urn, ./@urn)]/cgResolver:uri[@rank=1]"/>
        <xsl:variable name="id" select="substring-after($urn,$resource-urn)"/>
        <xsl:variable name="uri" select="concat($resource-uri,$id)"/>
        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:value-of select="$uri"/>
            </xsl:attribute>
            <xsl:value-of select="$urn"/>
        </xsl:element>
    </xsl:template>

    <!--letter selectors -->
    <xsl:template name="iterate-letter">
        <xsl:param name="letter" select="'abcdefghijklmnopqrstuvwxyz'"/>
        <xsl:call-template name="letter-cell">
            <xsl:with-param name="contents" select="substring($letter,1,1)"/>
        </xsl:call-template>
        <xsl:if test="string-length($letter)&gt;1">
            <xsl:call-template name="iterate-letter">
                <xsl:with-param name="letter" select="substring($letter,2)"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="letter-cell">
        <xsl:param name="contents"/>
        <xsl:param name="calling-webpage"/>
        <xsl:param name="type"/>
        <td>
            <xsl:call-template name="css-rollover-button-letter">
                <xsl:with-param name="alt" select="$contents"/>
                <xsl:with-param name="link" select="concat('javascript:changeLetter(''',$contents,''')')"/>
                <xsl:with-param name="text" select="$contents"/>
            </xsl:call-template>
        </td>
    </xsl:template>
    <xsl:template name="alphabet-links">
        <xsl:param name="calling-webpage"/>
        <xsl:param name="type"/>
        <div align="center">
            <table class="alphabet">
                <tr>
                    <xsl:call-template name="iterate-letter"/>
                </tr>
            </table>
        </div>
    </xsl:template>
    <xsl:template name="css-rollover-button-letter">
        <xsl:param name="link"/>
        <xsl:param name="text"/>
        <xsl:param name="alt"/>
        <div class="cssnav_small">
            <a href="{$link}" target="_top">
                <img src="../web/images/greyletterbutton.bmp" alt="{$alt}"/>
                <span>
                    <xsl:value-of select="$text"/>
                </span>
            </a>
        </div>
    </xsl:template>
    
    <!-- indexing page paging links -->
    <xsl:template match="index">
        <form id="index" action="{action}" method="get">
            <input type="hidden" name="start" value="{start}"/>
            <input type="hidden" name="extent" value="{extent}"/>
            <input type="hidden" name="previous" value="{previous}"/>
            <input type="hidden" name="next" value="{next}"/>
            <input type="hidden" name="last" value="{last}"/>
            <input type="hidden" name="count" value="{count}"/>
            <input type="hidden" name="recordlimit" value="{recordlimit}"/>
            <input type="hidden" name="letter" value="{letter}"/>
            <input type="hidden" name="type" value="{type}"/>
            <input type="hidden" name="showScheme" value="{show-scheme}"/>
            <input type="hidden" name="term_id" value="{term-id}"/>
            <input type="hidden" name="phrase" value="{phrase}"/>
            <table class="layout" html="http://www.w3.org/1999/xhtml" id="index-form">
                <tr>
                    <td align="center">
                        <xsl:choose>
                            <xsl:when test="start=1">First Page</xsl:when>
                            <xsl:otherwise>
                                <a href="{concat('javascript:showPage(1,',extent,')')}">first page</a>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                    <td align="center">
                        <xsl:choose>
                            <xsl:when test="start=1"> No Previous Page </xsl:when>
                            <xsl:otherwise>
                                <a href="{concat('javascript:showPage(', previous, ',' , extent,')')}">previous page</a>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                    <td align="center">
                        <xsl:choose>
                            <xsl:when test="start+extent&gt;count"> No Following Pages </xsl:when>
                            <xsl:otherwise>
                                <a href="{concat('javascript:showPage(', next, ',' ,extent,')')}">next page</a>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                    <td type="button" align="center">
                        <xsl:choose>
                            <xsl:when test="start+extent&gt;count"> Last Page </xsl:when>
                            <xsl:otherwise>
                                <a href="{concat('javascript:showPage(', last, ',' ,extent,')')}">last page</a>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
                <tr>
                    <td colspan="4" align="center">Records <xsl:value-of select="start"/> to
                        <xsl:value-of select="recordlimit"/> of <xsl:value-of select="count"/>
                    </td>
                </tr>
            </table>
        </form>
    </xsl:template>
    <xsl:template match="tabular-content">
        <xsl:apply-templates select="result-set"/>
        <xsl:apply-templates select="index"/>
    </xsl:template>
    <xsl:template name="type-menu">
        <div class="left-padding">
            <table>
                <tr>
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">Data Element</xsl:with-param>
                            <xsl:with-param name="alt">Data Element</xsl:with-param>
                            <xsl:with-param name="link">javascript:changeType('data_element')</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">Value Domain</xsl:with-param>
                            <xsl:with-param name="alt">Value Domain</xsl:with-param>
                            <xsl:with-param name="link">javascript:changeType('value_domain')</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">Data Element Concept</xsl:with-param>
                            <xsl:with-param name="alt">Data Element Concept</xsl:with-param>
                            <xsl:with-param name="link">javascript:changeType('data_element_concept')</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">Object Class</xsl:with-param>
                            <xsl:with-param name="alt">Object Class</xsl:with-param>
                            <xsl:with-param name="link">javascript:changeType('object_class')</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">Property</xsl:with-param>
                            <xsl:with-param name="alt">Property</xsl:with-param>
                            <xsl:with-param name="link">javascript:changeType('property')</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button-inner">
                            <xsl:with-param name="text">Conceptual Domain</xsl:with-param>
                            <xsl:with-param name="alt">Conceptual Domain</xsl:with-param>
                            <xsl:with-param name="link">javascript:changeType('conceptual_domain')</xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>
    <xsl:function name="openMDR:sentence-case">
        <xsl:param name="input" as="xs:string"/>
        <xsl:value-of>
            <xsl:for-each select="tokenize(replace($input, '_', ' '),' ')">
                <xsl:value-of select="upper-case(.)"/>
            </xsl:for-each>
        </xsl:value-of>
    </xsl:function>
</xsl:stylesheet>