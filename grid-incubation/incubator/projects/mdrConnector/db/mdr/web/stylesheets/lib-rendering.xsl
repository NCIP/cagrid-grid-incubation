<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:iaaaterm="http://iaaa.cps.unizar.es/iaaaterms/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:cgResolver="http://www.cancergrid.org/schema/cgResolver" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179" version="2.0">
    <xsl:include href="lib-rendering-new.xsl"/>
    <xsl:output method="html" media-type="text/html" indent="yes" doctype-public="-//W3C//DTD XHTML 1,0 Transitional//EN" doctype-system="http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd" omit-xml-declaration="no" exclude-result-prefixes="cgMDR dc iaaaterm xs rdf cgResolver skos dcterms ISO11179"/>
    <xsl:param name="type">data_element</xsl:param>
    
    <!-- verbatim render of generated html -->
    <xsl:template match="html:div">
        <xsl:copy-of select="child::node()"/>
    </xsl:template>
    
    <!--deprecated, use html:div template above....-->
    <xsl:template match="direct-render">
        <xsl:copy-of select="child::node()"/>
    </xsl:template>
    
    <!--page parts-->
    <!--renders the classification list-->
    <xsl:template match="filter-links">
        Filtered by: 
        
        <xsl:choose>
            <xsl:when test="empty(filter-link)">(no filter applied)</xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="filter-link"/>
                <a class="term-link" href="javascript:showTerm('')">(clear filter)</a>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="filter-link">
        <xsl:element name="a">
            <xsl:attribute name="class">term-link</xsl:attribute>
            <xsl:attribute name="href">
                <xsl:value-of select="concat('javascript:showTerm(''',@resource,''')')"/>
            </xsl:attribute>
            <xsl:value-of select="."/>; </xsl:element>
    </xsl:template>
    
    
    <!--**************************************-->
    <!-- XSL Templates for list pages         -->
    <!--**************************************-->
    <xsl:template match="content-by-letter">
        <xsl:call-template name="type-menu"/>
        <xsl:call-template name="alphabet-links"/>
        <br/>
        <div class="content_one_pane">
            <xsl:apply-templates select="tabular-content"/>
        </div>
    </xsl:template>
    <xsl:template match="content-by-search">
        <xsl:call-template name="type-menu"/>
        <xsl:variable name="phrase" select="//phrase"/>
        <xsl:variable name="action" select="//action"/>
        <br/>
        <div class="content_one_pane">
            <div class="centre_block">
                <form name="frmSrch" method="get" class="cancergrid" action="{$action}"> Search
                    phrase <input type="text" name="phrase" value="{$phrase}"/>
                    <input class="cgButton" type="submit" value="Submit query"/>
                </form>
            </div>
            <xsl:apply-templates select="tabular-content"/>
        </div>
    </xsl:template>
    <xsl:template match="content-by-classification">
        <form action="classification.xquery" method="post" id="tree_view_form">
            <input type="hidden" name="showScheme" value="{show-scheme}"/>
            <input type="hidden" name="term_id" value="{term-id}"/>
            <input type="hidden" name="start" value="{start}"/>
            <input type="hidden" name="extent" value="{extent}"/>
            <table class="layout">
                <tr>
                    <td colspan="2">
                        <xsl:call-template name="type-menu"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <xsl:apply-templates select="//loaded-schemes"/>
                        <xsl:if test="exists(//show-scheme/text())">; <a href="javascript:showScheme('')">(all schemes)</a>
                        </xsl:if>
                        <br/>
                        <xsl:apply-templates select="//filter-links"/>
                        <br/>
                    </td>
                </tr>
                <tr>
                    <td class="tree-container">
                        <xsl:apply-templates select="//treeview"/>
                    </td>
                    <td width="70%" valign="top">
                        <br/>
                        <xsl:apply-templates select="//tabular-content"/>
                    </td>
                </tr>
                <tr>
                    <td/>
                    <td>
                        <xsl:apply-templates select="index-form"/>
                    </td>
                </tr>
            </table>
        </form>
    </xsl:template>
    <xsl:template match="result-set">
        <xsl:param name="empty-message"/>
        <xsl:choose>
            <xsl:when test="//data_element|//value_domain|//conceptual_domain">
                <table class="layout">
                    <tr valign="bottom">
                        <td>
                            <div class="admin_item_table_header">Name and ID</div>
                        </td>
                        <td>
                            <div class="admin_item_table_header">Definition</div>
                        </td>
                        <td>
                            <div class="admin_item_table_header">Values</div>
                        </td>
                    </tr>
                    <xsl:apply-templates mode="result-set-body"/>
                </table>
            </xsl:when>
            <xsl:when test="property|representation_class|object_class|data_element_concept">
                <table class="layout">
                    <tr>
                        <td>
                            <div class="admin_item_table_header">ID</div>
                        </td>
                        <td>
                            <div class="admin_item_table_header">name</div>
                        </td>
                        <td>
                            <div class="admin_item_table_header">description</div>
                        </td>
                    </tr>
                    <xsl:apply-templates select="." mode="result-set-body"/>
                </table>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$empty-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="property|representation_class|object_class|data_element_concept" mode="result-set-body">
        <tr>
            <td class="left_header_cell">
                <xsl:call-template name="html-anchor">
                    <xsl:with-param name="collection" select="local-name(.)"/>
                    <xsl:with-param name="id" select="names/id"/>
                    <xsl:with-param name="text" select="names/id"/>
                </xsl:call-template>
            </td>
            <td>
                <xsl:copy-of select="names/preferred"/>
            </td>
            <td>
                <xsl:value-of select="description"/>
            </td>
        </tr>
    </xsl:template>
    <!--create a table row for each data element -->
    <xsl:template match="data_element|value_domain" mode="result-set-body">
        <xsl:variable name="class" select="@class"/>
        <tr valign="top" class="{$class}">
            <td>
                <!-- ID -->
                <!-- NAME -->
                <xsl:call-template name="data-element-summary-anchor">
                    <xsl:with-param name="id">
                        <xsl:value-of select="names/id"/>
                    </xsl:with-param>
                    <xsl:with-param name="preferred-name">
                        <xsl:value-of select="names/preferred"/>
                    </xsl:with-param>
                </xsl:call-template>
                <br/>
                <font size="1">
                    <xsl:value-of select="names/id"/>
                </font>
                <br/>
                <xsl:apply-templates select="names/all-names/name"/>
            </td>
            <td>
                <!-- DESCRIPTION -->
                <xsl:value-of select="definition"/>
            </td>
            <td width="40%">
                <!-- VALUE DOMAIN -->
                <!--create a table to contain either 'Data Type' and 'Units' or 'Code and 'Meaning' -->
                <xsl:apply-templates select="values" mode="tabular-data-element-display"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="values" mode="tabular-data-element-display">
        <xsl:choose>
            <xsl:when test="data-type">
                <b>Data Type:</b>
                <xsl:value-of select="data-type"/>
                <br/>
                <b>Units:</b>
                <xsl:value-of select="units"/>
                <br/>
            </xsl:when>
            <xsl:otherwise>
                <table class="layout">
                    <tr>
                        <th width="20%">Code</th>
                        <th>Meaning</th>
                    </tr>
                    <xsl:apply-templates mode="tabular-data-element-display">
                        <xsl:sort select="valid-value/code"/>
                    </xsl:apply-templates>
                </table>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="conceptual_domain" mode="result-set-body">
        <xsl:variable name="class" select="@class"/>
        <tr valign="top" class="{$class}">
            <td>
                <!-- ID -->
                <!-- NAME -->
                <xsl:call-template name="html-anchor">
                    <xsl:with-param name="collection" select="local-name(.)"/>
                    <xsl:with-param name="id" select="names/id"/>
                    <xsl:with-param name="text" select="names/preferred"/>
                </xsl:call-template>
                <br/>
                <font size="1">
                    <xsl:value-of select="names/id"/>
                </font>
                <br/>
                <xsl:apply-templates select="names/all-names/name"/>
            </td>
            <td>
                <xsl:value-of select="definition"/>
            </td>
            <td width="40%">
                <xsl:apply-templates select="meanings" mode="tabular-data-element-display"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template mode="tabular-data-element-display" match="meanings">
        <table class="layout">
            <xsl:apply-templates select="meaning" mode="tabular-conceptual-domain-display">
                <xsl:sort select="meaning"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <!--template matches data-element and units -->
    <xsl:template match="meaning" mode="tabular-conceptual-domain-display">
        <tr>
            <td>
                <xsl:value-of select="."/>
            </td>
        </tr>
    </xsl:template>
    <!--template matches data-element and units -->
    <xsl:template match="code|meaning" mode="tabular-data-element-display">
        <td>
            <xsl:value-of select="."/>
        </td>
    </xsl:template>
    <xsl:template match="valid-value" mode="tabular-data-element-display">
        <tr>
            <xsl:apply-templates mode="tabular-data-element-display"/>
        </tr>
    </xsl:template>
    <!--**************************************************-->
    <!-- XSL Templates for the organisations list page    -->
    <!--**************************************************-->
    <xsl:template match="organisations">
        <xsl:call-template name="alphabet-links">
            <xsl:with-param name="calling-webpage">organisations.xquery</xsl:with-param>
        </xsl:call-template>
        <table class="layout">
            <tr>
                <td>
                    <div class="admin_item_table_header">organisation identifier</div>
                </td>
                <td>
                    <div class="admin_item_table_header">organisation name</div>
                </td>
                <td>
                    <div class="admin_item_table_header">organisation address</div>
                </td>
            </tr>
            <xsl:apply-templates mode="organisation_list"/>
        </table>
    </xsl:template>
    <xsl:template match="reference-documents">
        <div>
            <xsl:call-template name="alphabet-links">
                <xsl:with-param name="calling-webpage">reference-document-management.xquery</xsl:with-param>
            </xsl:call-template>
            <table class="layout">
                <tr>
                    <td>
                        <div class="admin_item_table_header">identifier</div>
                    </td>
                    <td>
                        <div class="admin_item_table_header">name</div>
                    </td>
                </tr>
                <xsl:apply-templates mode="reference-document-list"/>
            </table>
        </div>
    </xsl:template>
    <xsl:template match="reference-document" mode="reference-document-list">
        <tr>
            <td class="left_header_cell">
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:copy-of select="anchor/html:a"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cgMDR:Reference_Document">
        <xsl:variable name="uri" select="cgMDR:reference_document_uri"/>
        <table class="layout">
            <thead>
                <tr>
                    <td colspan="2">
                        <div class="admin_item_section_header">details</div>
                    </td>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="left_header_cell">Title</td>
                    <td>
                        <xsl:value-of select="cgMDR:reference_document_title"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Language</td>
                    <td>
                        <xsl:value-of select="cgMDR:reference_document_language_identifier"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">File name</td>
                    <td>
                        <xsl:value-of select="cgMDR:file_name"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Media type</td>
                    <td>
                        <xsl:value-of select="cgMDR:file_type"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Provided By</td>
                    <td>
                        <xsl:value-of select="cgMDR:provided_by"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Type</td>
                    <td>
                        <xsl:value-of select="cgMDR:reference_document_type_description"/>
                    </td>
                </tr>
                <tr>
                    <td>Document</td>
                    <td>
                        <a href="{concat('reference_document.xquery?compound_id=', string(./@reference_document_identifier),'&amp;return_doc=true')}">download</a>
                    </td>
                </tr>
            </tbody>
        </table>
    </xsl:template>
    <!--rendering contents wepage -->
    <xsl:template match="contents">
        <xsl:apply-templates select="//index" mode="type-menu"/>
        <xsl:call-template name="alphabet-links"/>
        <div class="content_one_pane_two_menu">
            <table class="layout">
                <tr>
                    <td>
                        <div class="admin_item_table_header">administered item identifier</div>
                    </td>
                    <td>
                        <div class="admin_item_table_header">data element name</div>
                    </td>
                </tr>
                <xsl:apply-templates mode="contents-list"/>
            </table>
        </div>
    </xsl:template>
    <xsl:template match="item" mode="contents-list">
        <xsl:variable name="amin-item-id" select="id"/>
        <tr class="row">
            <td>
                <xsl:value-of select="id"/>
            </td>
            <td>
                <xsl:call-template name="data-element-summary-anchor">
                    <xsl:with-param name="id" select="id"/>
                    <xsl:with-param name="preferred-name" select="name"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="data-element-summary-anchor">
        <xsl:param name="id"/>
        <xsl:param name="preferred-name"/>
        <a href="{concat('../web/data_element_summary.xquery?compound_id=',$id)}">
            <xsl:value-of select="$preferred-name"/>
        </a>
    </xsl:template>
    <xsl:template match="organization" mode="organisation_list">
        <tr>
            <td class="left_header_cell">
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:copy-of select="anchor/html:a"/>
            </td>
            <td>
                <xsl:value-of select="description"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cgMDR:Organization">
        <table>
            <tr>
                <td class="left_header_cell">Identifier</td>
                <td>
                    <xsl:value-of select="@organization_identifier"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Name</td>
                <td>
                    <xsl:value-of select="cgMDR:organization_name"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Address</td>
                <td>
                    <xsl:value-of select="cgMDR:organization_mail_address"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Contacts</td>
                <td>
                    <table>
                        <tr>
                            <th>identifier</th>
                            <th>name</th>
                            <th>title</th>
                            <th>information</th>
                        </tr>
                        <xsl:apply-templates select="cgMDR:Contact"/>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="cgMDR:Contact">
        <tr>
            <td>
                <xsl:value-of select="@contact_identifier"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:contact_name"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:contact_title"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:contact_information"/>
            </td>
        </tr>
    </xsl:template>
    <!-- maintenance webpage -->
    <xsl:template match="functions">
        <table>
            <xsl:apply-templates>
                <xsl:sort select="title"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <xsl:template match="function">
        <tr>
            <td>
                <a href="{uri}">
                    <xsl:value-of select="title"/>
                </a>
            </td>
            <td>
                <xsl:value-of select="description"/>
            </td>
        </tr>
    </xsl:template>
    <!--display test results-->
    <xsl:template match="test-results">
        <div class="content_one_pane_two_menu">
            <table class="layout">
                <tr>
                    <td>
                        <div class="admin_item_table_header">test</div>
                    </td>
                    <td>
                        <div class="admin_item_table_header">description</div>
                    </td>
                    <td>
                        <div class="admin_item_table_header">result</div>
                    </td>
                </tr>
                <xsl:apply-templates/>
            </table>
        </div>
    </xsl:template>
    <xsl:template match="test-result">
        <tr>
            <td class="left_header_cell">
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:value-of select="@description"/>
            </td>
            <td>
                <xsl:value-of select="@passed"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="loaded-schemes"> Show scheme: <xsl:apply-templates select="loaded-scheme"/>
<!--        <xsl:if test="count(loaded-scheme)>1">
            <a class="term-link" href="javascript:showScheme('')"> (all schemes) </a>
        </xsl:if>-->
    </xsl:template>
    <xsl:template match="loaded-scheme">
        <a class="term-link" href="{concat('javascript:showScheme(''', @uri, ''')')}">
            <xsl:value-of select="."/>
            <xsl:if test="following-sibling::node()">;</xsl:if>
        </a>
    </xsl:template>
    <xsl:template match="data_element_concept" mode="tabular">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="name">
        <xsl:copy-of select="text()"/>
        <br/>
    </xsl:template>
    
    <!--treeview stuff-->
    <!-- ************************************  Treeview  ************************************ -->
    <!-- ************************************ Parameters ************************************ -->
    <!-- deploy-treeview, boolean - true if you want to deploy the tree-view at the first print -->
    <xsl:param name="param-deploy-treeview" select="'false'"/>
    <!-- is the client Netscape / Mozilla or Internet Explorer. Thanks to Bill, 90% of sheeps use Internet Explorer so it will the default value-->
    <xsl:param name="param-is-netscape" select="'false'"/>
    <!-- hozizontal distance in pixels between a folder and its leaves -->
    <xsl:param name="param-shift-width" select="18"/>
    <!-- image source directory-->
    <xsl:variable name="var-simple-quote">'</xsl:variable>
    <xsl:variable name="var-slash-quote">\'</xsl:variable>
    <xsl:output indent="yes"/>
    <!--
        **
        **  Model "treeview"
        ** 
        **  This model transforms an XML treeview into an html treeview
        **  
    -->
    <xsl:template match="treeview">
        <!-- -->
        <div id="treeroot" class="treeroot">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td>
                        <!-- Apply the template folder starting with a depth in the tree of 1-->
                        <xsl:apply-templates select="folder">
                            <xsl:with-param name="depth" select="1"/>
                        </xsl:apply-templates>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>
    <!--
        **
        **  Model "folder"
        ** 
        **  This model transforms a folder element. Prints a plus (+) or minus (-)  image, the folder image and a title
        **  
    -->
    <xsl:template match="folder">
        <xsl:param name="depth"/>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <!-- If first level of depth, do not shift of $param-shift-width-->
                <xsl:if test="$depth &gt;1">
                    <td width="{$param-shift-width}"/>
                </xsl:if>
                <td>
                    <xsl:choose>
                        <xsl:when test="not(child::*)">
                            <img src="images/blank.gif" onclick="toggle(this);" alt="Class without children"/>
                        </xsl:when>
                        <xsl:when test="@expanded='true'">
                            <img src="images/minus.gif" onclick="toggle(this);" alt="Class with children, expanded"/>
                        </xsl:when>
                        <xsl:when test="@expanded='false'">
                            <img src="images/plus.gif" onclick="toggle(this);" alt="Class with children, collapsed"/>
                        </xsl:when>
                        <xsl:when test="not(@expanded)">
                            <img src="images/minus.gif" onclick="toggle(this);" alt="Class with children, collapsed"/>
                        </xsl:when>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="@code">
                            <a class="folder">
                                <xsl:attribute name="href">
                                    <xsl:value-of select="concat('javascript:showTerm(''',@code,''')')"/>
                                </xsl:attribute>
                                <!--<xsl:attribute name="id" select="@code"/>-->
                                <img src="{@img}" alt="{@alt}"/>
                                <xsl:value-of select="@title"/>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <img src="{@img}" alt="{@alt}"/>
                            <xsl:value-of select="@title"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <!-- Shall we expand all the leaves of the treeview ? no by default-->
                    <div>
                        <xsl:choose>
                            <xsl:when test="@expanded='true'">
                                <xsl:attribute name="style">display:block;</xsl:attribute>
                            </xsl:when>
                            <xsl:when test="@expanded='false'">
                                <xsl:attribute name="style">display:none;</xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="style">display:none;</xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:apply-templates select="folder">
                            <xsl:with-param name="depth" select="$depth+1"/>
                        </xsl:apply-templates>
                        <!-- print all the leaves of this folder-->
                        <xsl:apply-templates select="leaf"/>
                    </div>
                </td>
            </tr>
        </table>
    </xsl:template>
    <!--
        **
        **  Model "leaf"
        ** 
        **  This model prints an image plus the name of the element
        **  
    -->
    <xsl:template match="leaf">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td width="{$param-shift-width}"/>
                <td>
                    <a class="leaf">
                        <!-- The line is very long bu I have no choice, I called the function replace-string to replace the quotes (') by /' -->
                        <xsl:attribute name="href">javascript:showTerm('{@code}')
                            <!--selectLeaf('<xsl:call-template name="replace-string">
                                <xsl:with-param name="text" select="@code"/>
                                <xsl:with-param name="from" select="$var-simple-quote"/>
                                <xsl:with-param name="to" select="$var-slash-quote"/>
                            </xsl:call-template>')-->
                        </xsl:attribute>
                        <!-- if it is the last leaf, print a different image for the link to the folder-->
                        <!--<xsl:attribute name="id">
                            <xsl:value-of select="@code"/>
                        </xsl:attribute>-->
                        <xsl:choose>
                            <xsl:when test="position()=last()">
                                <img src="images/lastlink.gif"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <img src="images/link.gif"/>
                            </xsl:otherwise>
                        </xsl:choose>
                        <img src="{@img}">
                            <!-- if the attribut alt is present-->
                            <xsl:if test="@alt">
                                <!-- if Netscape / Mozilla -->
                                <xsl:if test="$param-is-netscape='true'">
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="@alt"/>
                                    </xsl:attribute>
                                </xsl:if>
                                <!-- if Internet Explorer -->
                                <xsl:if test="$param-is-netscape='false'">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="@alt"/>
                                    </xsl:attribute>
                                </xsl:if>
                            </xsl:if>
                        </img>
                        <xsl:value-of select="@title"/>
                    </a>
                </td>
            </tr>
        </table>
    </xsl:template>
    <!--
        **
        **  Model "replace-string"
        ** 
        **  reusable replace-string function **  
    -->
    <xsl:template name="replace-string">
        <xsl:param name="text"/>
        <xsl:param name="from"/>
        <xsl:param name="to"/>
        <xsl:choose>
            <xsl:when test="contains($text, $from)">
                <xsl:variable name="before" select="substring-before($text, $from)"/>
                <xsl:variable name="after" select="substring-after($text, $from)"/>
                <xsl:variable name="prefix" select="concat($before, $to)"/>
                <xsl:value-of select="$before"/>
                <xsl:value-of select="$to"/>
                <xsl:call-template name="replace-string">
                    <xsl:with-param name="text" select="$after"/>
                    <xsl:with-param name="from" select="$from"/>
                    <xsl:with-param name="to" select="$to"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="function-tests">
        <table class="layout">
            <tr>
                <th>function-name</th>
                <th>invocation</th>
                <th>result</th>
                <th>passed</th>
            </tr>
            <xsl:apply-templates mode="function-test-results"/>
        </table>
    </xsl:template>
    <xsl:template match="function-test" mode="function-test-results">
        <tr>
            <td>
                <xsl:value-of select="name"/>
            </td>
            <td>
                <xsl:value-of select="invocation"/>
            </td>
            <td>
                <xsl:value-of select="result"/>
            </td>
            <td>
                <xsl:value-of select="passed"/>
            </td>
        </tr>
    </xsl:template>
    <!--render data element complete -->
    <xsl:template match="cgMDR:data_element_complete">
        <xsl:apply-templates select="cgMDR:Data_Element" mode="data-element-summary"/>
    </xsl:template>
    <xsl:template match="cgMDR:Data_Element" mode="data-element-summary">
        <table class="layout">
            <tr>
                <td>
                    <xsl:call-template name="admin-item-header-summary"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:call-template name="naming"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="cgMDR:representing"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:call-template name="related-data-elements"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="//reference-document-section"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:call-template name="data-element-conceptual-framework"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="cgMDR:administered_item_administration_record"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template name="data-element-summary-preferred-name">
        <xsl:value-of select="//cgMDR:data_element_complete/cgMDR:Data_Element//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
    </xsl:template>
    <xsl:template name="admin-item-preferred-name">
        <xsl:value-of select="//cgMDR:Data_Element//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
    </xsl:template>
    <xsl:template name="admin-item-preferred-definition">
        <xsl:value-of select="//cgMDR:Data_Element//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:definition_text"/>
    </xsl:template>
    <xsl:template name="registrar">
        <xsl:param name="registrar-id"/>
        <xsl:value-of select="//cgMDR:Registration_Authority/cgMDR:represented_by[cgMDR:registrar_identifier=$registrar-id]/cgMDR:registrar_contact/cgMDR:contact_name"/>,<br/>
        <xsl:value-of select="//cgMDR:Registration_Authority/cgMDR:represented_by[cgMDR:registrar_identifier=$registrar-id]/cgMDR:registrar_contact/cgMDR:contact_title"/>, <br/>
        <xsl:value-of select="//cgMDR:Registration_Authority[cgMDR:represented_by/cgMDR:registrar_identifier=$registrar-id]/cgMDR:organization_name"/>
    </xsl:template>
    <xsl:template name="naming">
        <table class="section">
            <tr>
                <td colspan="2">
                    <div class="admin_item_section_header">Naming</div>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Naming</td>
                <td>
                    <table class="section">
                        <tr>
                            <td>
                                <div class="admin_item_table_header">language</div>
                            </td>
                            <td>
                                <div class="admin_item_table_header">name</div>
                            </td>
                            <td>
                                <div class="admin_item_table_header">preferred</div>
                            </td>
                        </tr>
                        <xsl:apply-templates select="cgMDR:having/cgMDR:containing">
                            <xsl:sort select="cgMDR:name" order="ascending"/>
                        </xsl:apply-templates>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="cgMDR:containing">
        <tr>
            <td>
                <xsl:apply-templates select="cgMDR:language_section_language_identifier"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:name"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:preferred_designation"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cgMDR:representing">
        <table class="section">
            <tr>
                <td colspan="3">
                    <div class="admin_item_section_header">Value Domain</div>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Type</td>
                <td colspan="2">
                    <xsl:choose>
                        <xsl:when test="//cgMDR:Enumerated_Value_Domain">enumerated</xsl:when>
                        <xsl:otherwise>non-enumerated</xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Full Record</td>
                <td colspan="2">
                    <xsl:call-template name="html-anchor">
                        <xsl:with-param name="id" select="."/>
                        <xsl:with-param name="collection">value_domain</xsl:with-param>
                        <xsl:with-param name="text" select="//cgMDR:Non_Enumerated_Value_Domain//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name|//cgMDR:Enumerated_Value_Domain//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Summary</td>
                <td>
                    <xsl:apply-templates select="//cgMDR:Enumerated_Value_Domain" mode="value-domain"/>
                    <xsl:apply-templates select="//cgMDR:Non_Enumerated_Value_Domain"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="cgMDR:Enumerated_Value_Domain" mode="value-domain">
        <table class="section">
            <tr>
                <td width="25%">
                    <div class="admin_item_table_header">permissible value</div>
                </td>
                <td width="50%">
                    <div class="admin_item_table_header">value meaning</div>
                </td>
                <td width="25%">
                    <div class="admin_item_table_header">reference</div>
                </td>
            </tr>
            <xsl:apply-templates select="cgMDR:containing" mode="value-domain">
                <xsl:sort select="cgMDR:value_item"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <xsl:template match="cgMDR:Non_Enumerated_Value_Domain">
        <table class="section" padding="0">
            <tr>
                <td class="left_header_cell">Datatype</td>
                <td>
                    <xsl:apply-templates select="//cgMDR:cgDatatype"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Unit of Measure</td>
                <td>
                    <xsl:apply-templates select="//cgMDR:Unit_of_Measure"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Precision</td>
                <td>
                    <xsl:value-of select="//cgMDR:unit_of_measure_precision"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Maximum Character Quantity</td>
                <td>
                    <xsl:value-of select="//cgMDR:value_domain_maximum_character_quantity"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Format</td>
                <td>
                    <xsl:value-of select="//cgMDR:value_domain_format"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="cgMDR:containing" mode="value-domain">
        <tr>
            <td>
                <xsl:value-of select="cgMDR:value_item"/>
            </td>
            <xsl:variable name="identifier" select="cgMDR:contained_in"/>
            <td>
                <xsl:value-of select="//cgMDR:Value_Meaning[cgMDR:value_meaning_identifier=$identifier]/cgMDR:value_meaning_description"/>
            </td>
            <td>
                <xsl:call-template name="uri-resolver">
                    <xsl:with-param name="urn" select="//cgMDR:Value_Meaning[cgMDR:value_meaning_identifier=$identifier]/cgMDR:reference_uri"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="related-data-elements">
        <xsl:choose>
            <xsl:when test="//asserted-here/*|//asserted-elsewhere/*">
                <table class="section">
                    <tr>
                        <td colspan="2">
                            <div class="admin_item_section_header">Related Data Elements</div>
                        </td>
                    </tr>
                    <xsl:if test="//asserted-here/*">
                        <tr>
                            <td class="left_header_cell">asserted by this data element</td>
                            <td>
                                <table class="section">
                                    <xsl:call-template name="related-to-table-header"/>
                                    <xsl:apply-templates select="cgMDR:input_to"/>
                                </table>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:if test="//asserted-elsewhere/*">
                        <tr>
                            <td class="left_header_cell">asserted by other data elements</td>
                            <td>
                                <table class="section">
                                    <xsl:call-template name="related-to-table-header"/>
                                    <xsl:apply-templates select="//asserted-elsewhere/cgMDR:Data_Element[//cgMDR:registration_status!='Superseded']" mode="related">
                                        <xsl:sort select="@item_registration_authority_identifier"/>
                                        <xsl:sort select="@data_identifier"/>
                                        <xsl:sort select="@version"/>
                                    </xsl:apply-templates>
                                </table>
                            </td>
                        </tr>
                    </xsl:if>
                </table>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="related-to-table-header">
        <tr>
            <td width="25%">
                <div class="admin_item_table_header">id</div>
            </td>
            <td width="45%">
                <div class="admin_item_table_header">preferred name</div>
            </td>
            <td width="13%">
                <div class="admin_item_table_header">how related</div>
            </td>
            <td width="17%">
                <div class="admin_item_table_header">status</div>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cgMDR:input_to">
        <xsl:variable name="reg-auth">
            <xsl:value-of select="concat(substring-before(@deriving,'-'),'-',substring-before(substring-after(@deriving,'-'),'-'))"/>
        </xsl:variable>
        <xsl:variable name="data-ident">
            <xsl:value-of select="substring-before(substring-after(@deriving,concat($reg-auth,'-')),'-')"/>
        </xsl:variable>
        <xsl:variable name="ver">
            <xsl:value-of select="substring-after(@deriving,concat($reg-auth,'-',$data-ident,'-'))"/>
        </xsl:variable>
        <xsl:variable name="related-name">
            <xsl:value-of select="//asserted-here/cgMDR:Data_Element[@item_registration_authority_identifier=$reg-auth and @data_identifier=$data-ident and @version=$ver]//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
        </xsl:variable>
        <xsl:variable name="related-status">
            <xsl:value-of select="//asserted-here/cgMDR:Data_Element[@item_registration_authority_identifier=$reg-auth and @data_identifier=$data-ident and @version=$ver]//cgMDR:registration_status"/>
        </xsl:variable>
        <tr>
            <td>
                <xsl:value-of select="@deriving"/>
            </td>
            <td>
                <xsl:value-of select="$related-name"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:derivation_rule_specification"/>
            </td>
            <td>
                <xsl:value-of select="$related-status"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cgMDR:Data_Element" mode="related">
        <xsl:variable name="admin-item-id">
            <xsl:call-template name="admin-item-identifier"/>
        </xsl:variable>
        <tr>
            <td>
                <xsl:value-of select="@item_registration_authority_identifier"/>-<xsl:value-of select="@data_identifier"/>
                <xsl:value-of select="@version"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:having/cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:input_to[@deriving=$admin-item-id]/cgMDR:derivation_rule_specification"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:administered_item_administration_record/cgMDR:registration_status"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cgMDR:administered_item_administration_record">
        <table class="section">
            <tr>
                <td colspan="2">
                    <div class="admin_item_section_header">Administration</div>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Administrative Status <i>
                        <a href="../web/simple-type-documentation.xquery?type-name=Administrative_Status"> explain </a>
                    </i>
                </td>
                <td>
                    <xsl:value-of select="cgMDR:administrative_status"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Administered By</td>
                <td>
                    <xsl:call-template name="administrator">
                        <xsl:with-param name="administrator-id" select="../cgMDR:administered_by"/>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Creation On</td>
                <td>
                    <xsl:value-of select="cgMDR:creation_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Effective From</td>
                <td>
                    <xsl:value-of select="cgMDR:effective_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Last Changed On</td>
                <td>
                    <xsl:value-of select="cgMDR:last_change_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Effective until</td>
                <td>
                    <xsl:value-of select="cgMDR:until_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Submitted By</td>
                <td>
                    <xsl:call-template name="submitter">
                        <xsl:with-param name="submitter-id" select="../cgMDR:submitted_by"/>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Explanatory Comments</td>
                <td>
                    <xsl:value-of select="cgMDR:explanatory_comment"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="reference-document-section">
        <table class="section">
            <tr>
                <td colspan="4">
                    <div class="admin_item_section_header">Reference Documents</div>
                </td>
            </tr>
            <tr>
                <td width="30%">
                    <div class="admin_item_table_header">id</div>
                </td>
                <td>
                    <div class="admin_item_table_header">language</div>
                </td>
                <td>
                    <div class="admin_item_table_header">title</div>
                </td>
                <td>
                    <div class="admin_item_table_header">type</div>
                </td>
            </tr>
            <xsl:apply-templates select="//cgMDR:Reference_Document" mode="tabular"/>
        </table>
    </xsl:template>
    <xsl:template match="cgMDR:Reference_Document" mode="tabular">
        <xsl:variable name="ref-doc-uri">
            <xsl:value-of select="cgMDR:reference_document_uri"/>
        </xsl:variable>
        <xsl:variable name="ref-doc-id">
            <xsl:value-of select="@reference_document_identifier"/>
        </xsl:variable>
        <tr>
            <td>
                <xsl:choose>
                    <xsl:when test="starts-with($ref-doc-uri,'http://')">
                        <a href="{$ref-doc-uri}">
                            <xsl:value-of select="$ref-doc-id"/>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <a href="{concat('../../data/reference_document/', $ref-doc-uri)}">
                            <xsl:value-of select="$ref-doc-id"/>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td>
                <xsl:value-of select="cgMDR:reference_document_language_identifier"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:reference_document_title"/>
            </td>
            <td>
                <xsl:value-of select="cgMDR:reference_document_type_description"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="administrator">
        <xsl:param name="administrator-id"/>
        <xsl:value-of select="//cgMDR:Contact[@contact_identifier=$administrator-id]/cgMDR:contact_name"/>
        <br/>
        <xsl:value-of select="//cgMDR:Contact[@contact_identifier=$administrator-id]/cgMDR:contact_title"/>
    </xsl:template>
    <xsl:template name="submitter">
        <xsl:param name="submitter-id"/>
        <xsl:value-of select="//cgMDR:Contact[@contact_identifier=$submitter-id]/cgMDR:contact_name"/>
        <br/>
        <xsl:value-of select="//cgMDR:Contact[@contact_identifier=$submitter-id]/cgMDR:contact_title"/>
    </xsl:template>
    <xsl:template name="data-element-conceptual-framework">
        <xsl:variable name="oc-uri">
            <xsl:value-of select="//cgMDR:Object_Class//cgMDR:reference_uri"/>
        </xsl:variable>
        <xsl:variable name="p-uri">
            <xsl:value-of select="//cgMDR:Property//cgMDR:reference_uri"/>
        </xsl:variable>
        <div class="section">
            <table class="section">
                <tr>
                    <td colspan="4">
                        <div class="admin_item_section_header">Conceptual Framework</div>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Expresses data element concept <br/>(full
                        record)</td>
                    <td colspan="3">
                        <xsl:call-template name="html-anchor">
                            <xsl:with-param name="text" select="//cgMDR:Data_Element_Concept//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
                            <xsl:with-param name="collection">data_element_concept</xsl:with-param>
                            <xsl:with-param name="id" select="cgMDR:expressing"/>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Concept or concept expression</td>
                    <td>
                        <xsl:call-template name="html-anchor">
                            <xsl:with-param name="text" select="//cgMDR:Object_Class//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
                            <xsl:with-param name="collection">object_class</xsl:with-param>
                            <xsl:with-param name="id" select="//cgMDR:Data_Element_Concept//cgMDR:data_element_concept_object_class"/>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="uri-resolver">
                            <xsl:with-param name="urn" select="$oc-uri"/>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Property measured</td>
                    <td>
                        <xsl:call-template name="html-anchor">
                            <xsl:with-param name="text" select="//cgMDR:Property//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
                            <xsl:with-param name="collection">property</xsl:with-param>
                            <xsl:with-param name="id" select="//cgMDR:Data_Element_Concept//cgMDR:data_element_concept_property"/>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="uri-resolver">
                            <xsl:with-param name="urn" select="$p-uri"/>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Typed by representation class</td>
                    <td colspan="3">
                        <xsl:call-template name="html-anchor">
                            <xsl:with-param name="text" select="//cgMDR:Representation_Class//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
                            <xsl:with-param name="collection">representation_class</xsl:with-param>
                            <xsl:with-param name="id" select="cgMDR:typed_by"/>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Representation class qualifier </td>
                    <td colspan="3">
                        <xsl:choose>
                            <xsl:when test="cgMDR:representation_class_qualifier = ''">unqualified</xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="cgMDR:representation_class_qualifier"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>
    <!-- testing -->
    <!-- renders the normal css rollover button-->
    <xsl:template name="admin-item-header">
        <table class="section">
            <tr>
                <td colspan="2">
                    <h3>Administered Item - Preferred Name: <xsl:call-template name="admin-item-preferred-name"/>
                    </h3>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Administered Item Identifier</td>
                <td colspan="2">
                    <xsl:call-template name="admin-item-identifier"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Registration Status <i>
                        <a href="../web/simple-type-documentation.xquery?type-name=Registration_Status"> explain </a>
                    </i>
                </td>
                <td>
                    <xsl:value-of select="//cgMDR:registration_status"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Definition</td>
                <td>
                    <xsl:call-template name="admin-item-preferred-definition"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Full Record</td>
                <td>
                    <xsl:variable name="collection" select="lower-case(local-name(.))"/>
                    <xsl:call-template name="html-anchor">
                        <xsl:with-param name="id">
                            <xsl:call-template name="admin-item-identifier"/>
                        </xsl:with-param>
                        <xsl:with-param name="collection">
                            <xsl:value-of select="$collection"/>
                        </xsl:with-param>
                        <xsl:with-param name="text">
                            <xsl:call-template name="admin-item-identifier"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Registered By</td>
                <td>
                    <xsl:call-template name="registrar">
                        <xsl:with-param name="registrar-id">
                            <xsl:value-of select="//cgMDR:registered_by"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell"/>
                <td/>
            </tr>
        </table>
    </xsl:template>
    <xsl:template name="admin-item-header-summary">
        <table class="section">
            <tr>
                <td colspan="2">
                    <h3>Administered Item - Preferred Name: <xsl:value-of select="//cgMDR:data_element_complete/cgMDR:Data_Element//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name"/>
                    </h3>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Administered Item Identifier</td>
                <td colspan="2">
                    <xsl:call-template name="admin-item-identifier"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Registration Status <i>
                        <a href="../web/simple-type-documentation.xquery?type-name=Registration_Status"> explain </a>
                    </i>
                </td>
                <td>
                    <xsl:value-of select="//cgMDR:data_element_complete/cgMDR:Data_Element//cgMDR:registration_status"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Definition</td>
                <td>
                    <xsl:value-of select="//cgMDR:data_element_complete/cgMDR:Data_Element//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:definition_text"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Full Record</td>
                <td>
                    <xsl:variable name="collection" select="lower-case(local-name(.))"/>
                    <xsl:call-template name="html-anchor">
                        <xsl:with-param name="id">
                            <xsl:call-template name="admin-item-identifier"/>
                        </xsl:with-param>
                        <xsl:with-param name="collection">
                            <xsl:value-of select="$collection"/>
                        </xsl:with-param>
                        <xsl:with-param name="text">
                            <xsl:call-template name="admin-item-identifier"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Registered By</td>
                <td>
                    <xsl:call-template name="registrar">
                        <xsl:with-param name="registrar-id">
                            <xsl:value-of select="//cgMDR:data_element_complete/cgMDR:Data_Element/cgMDR:registered_by"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell"/>
                <td/>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="broken-refs">
        <table class="layout">
            <hr>
                <th>from</th>
                <th>to</th>
                <th>id</th>
            </hr>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    <xsl:template match="broken-ref">
        <tr>
            <td>
                <xsl:value-of select="@from"/>
            </td>
            <td>
                <xsl:value-of select="@to"/>
            </td>
            <td>
                <xsl:call-template name="html-anchor">
                    <xsl:with-param name="collection" select="@from"/>
                    <xsl:with-param name="id" select="@id"/>
                    <xsl:with-param name="text" select="@id"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>
    <!--skos templates-->
    <xsl:template match="schemes-in-use">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="scheme">
        <xsl:apply-templates select="thesaurusdc"/>
        <xsl:variable name="uri" select="uri"/>
        <table class="layout">
            <tr>
                <td class="left_header_cell">URI</td>
                <td>
                    <a href="{$uri}">
                        <xsl:value-of select="$uri"/>
                    </a>
                </td>
            </tr>
        </table>
        <xsl:apply-templates select="rdf:RDF"/>
    </xsl:template>
    <xsl:template match="*" mode="name-value-pair">
        <tr>
            <td class="left_header_cell">
                <xsl:value-of select="cgMDR:sentence-case(local-name())"/>
            </td>
            <td>
                <xsl:value-of select="."/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="rdf:RDF">
        <table class="layout">
            <xsl:apply-templates>
                <xsl:sort select="skos:prefLabel[@xml:lang='en']"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <xsl:template match="rdf:Description|skos:Concept">
        <tr>
            <td colspan="3" class="left_header_cell">
                <a name="{substring-after(@rdf:about,'#')}">term: <xsl:value-of select="skos:prefLabel"/>
                </a>
            </td>
        </tr>
        <xsl:apply-templates mode="rdf">
            <xsl:sort select="local-name()"/>
        </xsl:apply-templates>
    </xsl:template>
    <xsl:template match="*" mode="rdf">
        <tr>
            <td/>
            <td>
                <xsl:value-of select="local-name()"/>
            </td>
            <td>
                <a href="#{substring-after(@rdf:resource,'#')}">
                    <xsl:call-template name="preferred-name">
                        <xsl:with-param name="resource" select="@rdf:resource"/>
                    </xsl:call-template>
                </a>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="preferred-name">
        <xsl:param name="resource"/>
        <xsl:variable name="preferred-name" select="//rdf:Description[@rdf:about=$resource]/skos:prefLabel"/>
        <xsl:choose>
            <xsl:when test="exists($preferred-name)">
                <xsl:value-of select="//rdf:Description[@rdf:about=$resource]/skos:prefLabel"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$resource"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="skos:prefLabel|skos:definition|skos:altLabel" mode="rdf">
        <tr>
            <td/>
            <td>
                <xsl:value-of select="local-name()"/>
            </td>
            <td>
                <xsl:value-of select="."/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="broken-links">
        <table>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    <xsl:template match="broken-link">
        <tr>
            <td>
                <xsl:value-of select="@href"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="cgMDR:cgDatatype">
        <xsl:value-of select="cgMDR:datatype_name"/> (<xsl:value-of select="cgMDR:datatype_scheme_reference"/>) </xsl:template>
    <xsl:template match="cgMDR:Unit_of_Measure">
        <xsl:value-of select="cgMDR:unit_of_measure_name"/>
    </xsl:template>
    <xsl:template match="cgMDR:reference_uri">
        <table class="layout">
            <tr>
                <td class="left_header_cell"> Concept reference </td>
                <td>
                    <xsl:call-template name="uri-resolver">
                        <xsl:with-param name="urn" select="."/>
                    </xsl:call-template>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="supporting-classes"/>
    <xsl:template match="cgMDR:Data_Element" mode="#default">
        <table class="layout">
            <tr>
                <td>
                    <xsl:call-template name="admin-item-header"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:call-template name="naming"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="cgMDR:representing"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:call-template name="related-data-elements"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="//reference-document-section"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:call-template name="data-element-conceptual-framework"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="cgMDR:administered_item_administration_record"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:call-template name="classification"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template name="classification">
        <table class="section">
            <tr>
                <td colspan="4">
                    <div class="admin_item_section_header">Classification</div>
                </td>
            </tr>
            <tr>
                <td width="30%">
                    <div class="admin_item_table_header">uri</div>
                </td>
                <td>
                    <div class="admin_item_table_header">term</div>
                </td>
            </tr>
            <xsl:apply-templates mode="classification" select="//rdf:Description"/>
        </table>
    </xsl:template>
    <xsl:template match="rdf:Description" mode="classification">
        <tr>
            <td>
                <xsl:value-of select="@rdf:about"/>
            </td>
            <td>
                <xsl:value-of select="skos:prefLabel"/>
            </td>
        </tr>
    </xsl:template>
    <!--reusable content parts -->
    <xsl:template match="cgMDR:language_section_language_identifier|reference_document_language_identifier">
        <xsl:value-of select="cgMDR:country_identifier"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="cgMDR:language_identifier"/>
    </xsl:template>
    <xsl:template match="cgMDR:administered_by|cgMDR:registered_by|cgMDR:submitted_by">
        <table class="layout">
            <xsl:apply-templates select="." mode="name-value-pair"/>
        </table>
    </xsl:template>
    <xsl:template match="cgMDR:having">
        <tr>
            <td class="left_header_cell">Context</td>
            <td>
                <xsl:call-template name="html-anchor">
                    <xsl:with-param name="collection">context</xsl:with-param>
                    <xsl:with-param name="id" select="cgMDR:context_identifier"/>
                    <xsl:with-param name="text" select="cgMDR:context_identifier"/>
                </xsl:call-template>
            </td>
        </tr>
        <tr>
            <td>
                <table class="section">
                    <tr>
                        <td colspan="2">
                            <div class="admin_item_section_header">Naming</div>
                        </td>
                    </tr>
                    <tr>
                        <td class="left_header_cell">Naming</td>
                        <td>
                            <table class="section">
                                <tr>
                                    <td>
                                        <div class="admin_item_table_header">language</div>
                                    </td>
                                    <td>
                                        <div class="admin_item_table_header">name</div>
                                    </td>
                                    <td>
                                        <div class="admin_item_table_header">preferred</div>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="cgMDR:containing">
                                    <xsl:sort select="cgMDR:name" order="ascending"/>
                                </xsl:apply-templates>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>