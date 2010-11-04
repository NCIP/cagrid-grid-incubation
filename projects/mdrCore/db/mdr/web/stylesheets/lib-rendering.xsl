<xsl:stylesheet xmlns:html="http://www.w3.org/1999/xhtml" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:openMDR="http://www.cagrid.org/schema/openMDR" xmlns:cgResolver="http://www.cagrid.org/schema/cgResolver" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:iaaaterm="http://iaaa.cps.unizar.es/iaaaterms/" xmlns:exist="http://exist.sourceforge.net/NS/exist" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns="http://www.w3.org/1999/xhtml" xmlns:ISO11179="http://www.cagrid.org/schema/ISO11179" version="2.0">
    <xsl:include href="lib-rendering-new.xsl"/>
    <xsl:output method="html" media-type="text/html" indent="yes" doctype-public="-//W3C//DTD XHTML 1,0 Transitional//EN" doctype-system="http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd" omit-xml-declaration="no" exclude-result-prefixes="openMDR dc iaaaterm xs rdf cgResolver skos dcterms ISO11179"/>
    
    
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
                <form name="frmSrch" method="get" class="cagrid" action="{$action}"> Search
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
    <xsl:template match="data_element" mode="result-set-body">
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
    <xsl:template match="value_domain" mode="result-set-body">
        <xsl:variable name="class" select="@class"/>
        <tr valign="top" class="{$class}">
            <td>
                <!-- ID -->
                <!-- NAME -->
                <xsl:call-template name="value-domain-summary-anchor">
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
    <xsl:template match="openMDR:Reference_Document">
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
                        <xsl:value-of select="openMDR:reference_document_title"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Language</td>
                    <td>
                        <xsl:value-of select="openMDR:reference_document_language_identifier"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">File name</td>
                    <td>
                        <xsl:value-of select="openMDR:file_name"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Media type</td>
                    <td>
                        <xsl:value-of select="openMDR:file_type"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Provided By</td>
                    <td>
                        <xsl:value-of select="openMDR:provided_by"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Type</td>
                    <td>
                        <xsl:value-of select="openMDR:reference_document_type_description"/>
                    </td>
                </tr>
                <tr>
                    <td>Document</td>
                    <td>
                        <a href="{string(./openMDR:reference_document_uri)}">download</a>
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
        <a href="{concat('../web/data_element.xquery?compound_id=',$id)}">
            <xsl:value-of select="$preferred-name"/>
        </a>
    </xsl:template>
    <xsl:template name="value-domain-summary-anchor">
        <xsl:param name="id"/>
        <xsl:param name="preferred-name"/>
        <a href="{concat('../web/value_domain.xquery?compound_id=',$id)}">
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
    <xsl:template match="openMDR:Organization">
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
                    <xsl:value-of select="openMDR:organization_name"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Address</td>
                <td>
                    <xsl:value-of select="openMDR:organization_mail_address"/>
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
                        <xsl:apply-templates select="openMDR:Contact"/>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="openMDR:Contact">
        <tr>
            <td>
                <xsl:value-of select="@contact_identifier"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:contact_name"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:contact_title"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:contact_information"/>
            </td>
        </tr>
    </xsl:template>
    
    <!-- maintenance webpage -->
    
    <!-- Works: commenting for tab rendering below
    <xsl:template match="functions">
        <table>
            <xsl:apply-templates>
                <xsl:sort select="title"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <xsl:template match="subheading">
        <table border="0" cellpadding="4" class="layout">
            <tr>
                <th colspan="2">
                    <xsl:value-of select="@title"/>
                </th>
            </tr>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    <xsl:template match="function">
        <tr>
            <td class="left_header_cell">
                <a href="{uri}">
                    <xsl:value-of select="title"/>
                </a>
            </td>
            <td class="">
                <xsl:value-of select="description"/>
            </td>
        </tr>
        <xsl:text>
        </xsl:text>
    </xsl:template>
    -->
    
    <!-- Rearranging the Maintenance Page in tabs-->
    <xsl:template match="functions">
        <table class="section" border="0" cellpadding="4">
            <table class="section">
                <tr>
                    <td>
                        <div class="tabber">
                            <xsl:apply-templates select="subheading"/>
                        </div>
                    </td>
                </tr>
            </table>
        </table>
    </xsl:template>
    <xsl:template match="subheading">
        <div class="tabbertab">
            <p>
                <h2>
                    <xsl:value-of select="@title"/>
                </h2>
            </p>
            <xsl:apply-templates select="function"/>
        </div>
    </xsl:template>
    <xsl:template match="function">
        <table class="section">
            <tr>
                <td class="left_header_cell">
                    <a href="{uri}">
                        <xsl:value-of select="title"/>
                    </a>
                </td>
                <td colspan="5">
                    <xsl:value-of select="description"/>
                </td>
            </tr>
        </table>
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
    <xsl:template match="openMDR:data_element_complete">
        <xsl:apply-templates select="openMDR:Data_Element" mode="data-element-summary"/>
    </xsl:template>
    <xsl:template match="openMDR:Data_Element" mode="data-element-summary">
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
                    <xsl:apply-templates select="openMDR:representing"/>
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
                    <xsl:apply-templates select="openMDR:administered_item_administration_record"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template name="data-element-summary-preferred-name">
        <xsl:value-of select="//openMDR:data_element_complete/openMDR:Data_Element//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
    </xsl:template>
    <xsl:template name="admin-item-preferred-name">
        <xsl:value-of select="//openMDR:Data_Element//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
    </xsl:template>
    <xsl:template name="admin-item-preferred-definition">
        <xsl:value-of select="//openMDR:Data_Element//openMDR:containing[openMDR:preferred_designation='true']/openMDR:definition_text"/>
    </xsl:template>
    <xsl:template name="registrar">
        <xsl:param name="registrar-id"/>
        <xsl:value-of select="//openMDR:Registration_Authority/openMDR:represented_by[openMDR:registrar_identifier=$registrar-id]/openMDR:registrar_contact/openMDR:contact_name"/>,<br/>
        <xsl:value-of select="//openMDR:Registration_Authority/openMDR:represented_by[openMDR:registrar_identifier=$registrar-id]/openMDR:registrar_contact/openMDR:contact_title"/>, <br/>
        <xsl:value-of select="//openMDR:Registration_Authority[openMDR:represented_by/openMDR:registrar_identifier=$registrar-id]/openMDR:organization_name"/>
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
                        <xsl:apply-templates select="openMDR:having/openMDR:containing">
                            <xsl:sort select="openMDR:name" order="ascending"/>
                        </xsl:apply-templates>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="openMDR:containing">
        <tr>
            <td>
                <xsl:apply-templates select="openMDR:language_section_language_identifier"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:name"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:preferred_designation"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="openMDR:representing">
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
                        <xsl:when test="//openMDR:Enumerated_Value_Domain">enumerated</xsl:when>
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
                        <xsl:with-param name="text" select="//openMDR:Non_Enumerated_Value_Domain//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name|//openMDR:Enumerated_Value_Domain//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Summary</td>
                <td>
                    <xsl:apply-templates select="//openMDR:Enumerated_Value_Domain" mode="value-domain"/>
                    <xsl:apply-templates select="//openMDR:Non_Enumerated_Value_Domain"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="openMDR:Enumerated_Value_Domain" mode="value-domain">
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
            <xsl:apply-templates select="openMDR:containing" mode="value-domain">
                <xsl:sort select="openMDR:value_item"/>
            </xsl:apply-templates>
        </table>
    </xsl:template>
    <xsl:template match="openMDR:Non_Enumerated_Value_Domain">
        <table class="section" padding="0">
            <tr>
                <td class="left_header_cell">Datatype</td>
                <td>
                    <xsl:apply-templates select="//openMDR:cgDatatype"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Unit of Measure</td>
                <td>
                    <xsl:apply-templates select="//openMDR:Unit_of_Measure"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Precision</td>
                <td>
                    <xsl:value-of select="//openMDR:unit_of_measure_precision"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Maximum Character Quantity</td>
                <td>
                    <xsl:value-of select="//openMDR:value_domain_maximum_character_quantity"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Format</td>
                <td>
                    <xsl:value-of select="//openMDR:value_domain_format"/>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="openMDR:containing" mode="value-domain">
        <tr>
            <td>
                <xsl:value-of select="openMDR:value_item"/>
            </td>
            <xsl:variable name="identifier" select="openMDR:contained_in"/>
            <td>
                <xsl:value-of select="//openMDR:Value_Meaning[openMDR:value_meaning_identifier=$identifier]/openMDR:value_meaning_description"/>
            </td>
            <td>
                <xsl:call-template name="uri-resolver">
                    <xsl:with-param name="urn" select="//openMDR:Value_Meaning[openMDR:value_meaning_identifier=$identifier]/openMDR:reference_uri"/>
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
                                    <xsl:apply-templates select="openMDR:input_to"/>
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
                                    <xsl:apply-templates select="//asserted-elsewhere/openMDR:Data_Element[//openMDR:registration_status!='Superseded']" mode="related">
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
    <xsl:template match="openMDR:input_to">
        <xsl:variable name="reg-auth">
            <xsl:value-of select="concat(substring-before(@deriving,'_'),'_',substring-before(substring-after(@deriving,'_'),'_'))"/>
        </xsl:variable>
        <xsl:variable name="data-ident">
            <xsl:value-of select="substring-before(substring-after(@deriving,concat($reg-auth,'_')),'_')"/>
        </xsl:variable>
        <xsl:variable name="ver">
            <xsl:value-of select="substring-after(@deriving,concat($reg-auth,'_',$data-ident,'_'))"/>
        </xsl:variable>
        <xsl:variable name="related-name">
            <xsl:value-of select="//asserted-here/openMDR:Data_Element[@item_registration_authority_identifier=$reg-auth and @data_identifier=$data-ident and @version=$ver]//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
        </xsl:variable>
        <xsl:variable name="related-status">
            <xsl:value-of select="//asserted-here/openMDR:Data_Element[@item_registration_authority_identifier=$reg-auth and @data_identifier=$data-ident and @version=$ver]//openMDR:registration_status"/>
        </xsl:variable>
        <tr>
            <td>
                <xsl:value-of select="@deriving"/>
            </td>
            <td>
                <xsl:value-of select="$related-name"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:derivation_rule_specification"/>
            </td>
            <td>
                <xsl:value-of select="$related-status"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="openMDR:Data_Element" mode="related">
        <xsl:variable name="admin-item-id">
            <xsl:call-template name="admin-item-identifier"/>
        </xsl:variable>
        <tr>
            <td>
                <xsl:value-of select="@item_registration_authority_identifier"/>_<xsl:value-of select="@data_identifier"/>_<xsl:value-of select="@version"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:having/openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:input_to[@deriving=$admin-item-id]/openMDR:derivation_rule_specification"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:administered_item_administration_record/openMDR:registration_status"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="openMDR:administered_item_administration_record">
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
                    <xsl:value-of select="openMDR:administrative_status"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Administered By</td>
                <td>
                    <xsl:call-template name="administrator">
                        <xsl:with-param name="administrator-id" select="../openMDR:administered_by"/>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Creation On</td>
                <td>
                    <xsl:value-of select="openMDR:creation_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Effective From</td>
                <td>
                    <xsl:value-of select="openMDR:effective_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Last Changed On</td>
                <td>
                    <xsl:value-of select="openMDR:last_change_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Effective until</td>
                <td>
                    <xsl:value-of select="openMDR:until_date"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Submitted By</td>
                <td>
                    <xsl:call-template name="submitter">
                        <xsl:with-param name="submitter-id" select="../openMDR:submitted_by"/>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Explanatory Comments</td>
                <td>
                    <xsl:value-of select="openMDR:explanatory_comment"/>
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
            <xsl:apply-templates select="//openMDR:Reference_Document" mode="tabular"/>
        </table>
    </xsl:template>
    <xsl:template match="openMDR:Reference_Document" mode="tabular">
        <xsl:variable name="ref-doc-uri">
            <xsl:value-of select="openMDR:reference_document_uri"/>
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
                <xsl:value-of select="openMDR:reference_document_language_identifier"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:reference_document_title"/>
            </td>
            <td>
                <xsl:value-of select="openMDR:reference_document_type_description"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template name="administrator">
        <xsl:param name="administrator-id"/>
        <xsl:value-of select="//openMDR:Contact[@contact_identifier=$administrator-id]/openMDR:contact_name"/>
        <br/>
        <xsl:value-of select="//openMDR:Contact[@contact_identifier=$administrator-id]/openMDR:contact_title"/>
    </xsl:template>
    <xsl:template name="submitter">
        <xsl:param name="submitter-id"/>
        <xsl:value-of select="//openMDR:Contact[@contact_identifier=$submitter-id]/openMDR:contact_name"/>
        <br/>
        <xsl:value-of select="//openMDR:Contact[@contact_identifier=$submitter-id]/openMDR:contact_title"/>
    </xsl:template>
    <xsl:template name="data-element-conceptual-framework">
        <xsl:variable name="oc-uri">
            <xsl:value-of select="//openMDR:Object_Class//openMDR:reference_uri"/>
        </xsl:variable>
        <xsl:variable name="p-uri">
            <xsl:value-of select="//openMDR:Property//openMDR:reference_uri"/>
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
                            <xsl:with-param name="text" select="//openMDR:Data_Element_Concept//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
                            <xsl:with-param name="collection">data_element_concept</xsl:with-param>
                            <xsl:with-param name="id" select="openMDR:expressing"/>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Concept or concept expression</td>
                    <td>
                        <xsl:call-template name="html-anchor">
                            <xsl:with-param name="text" select="//openMDR:Object_Class//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
                            <xsl:with-param name="collection">object_class</xsl:with-param>
                            <xsl:with-param name="id" select="//openMDR:Data_Element_Concept//openMDR:data_element_concept_object_class"/>
                        </xsl:call-template>
                    </td>
                    <!--  
                    <td>
                        <xsl:call-template name="uri-resolver">
                            <xsl:with-param name="urn" select="$oc-uri"/>
                        </xsl:call-template>
                    </td>
                    -->
                </tr>
                <tr>
                    <td class="left_header_cell">Property measured</td>
                    <td>
                        <xsl:call-template name="html-anchor">
                            <xsl:with-param name="text" select="//openMDR:Property//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
                            <xsl:with-param name="collection">property</xsl:with-param>
                            <xsl:with-param name="id" select="//openMDR:Data_Element_Concept//openMDR:data_element_concept_property"/>
                        </xsl:call-template>
                    </td>
                    <!--  
                    <td>
                        <xsl:call-template name="uri-resolver">
                            <xsl:with-param name="urn" select="$p-uri"/>
                        </xsl:call-template>
                    </td>
                    -->
                </tr>
                <tr>
                    <td class="left_header_cell">Typed by representation class</td>
                    <td colspan="3">
                        <xsl:call-template name="html-anchor">
                            <xsl:with-param name="text" select="//openMDR:Representation_Class//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
                            <xsl:with-param name="collection">representation_class</xsl:with-param>
                            <xsl:with-param name="id" select="openMDR:typed_by"/>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Representation class qualifier </td>
                    <td colspan="3">
                        <xsl:choose>
                            <xsl:when test="openMDR:representation_class_qualifier = ''">unqualified</xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="openMDR:representation_class_qualifier"/>
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
                    <xsl:value-of select="//openMDR:registration_status"/>
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
                            <xsl:value-of select="//openMDR:registered_by"/>
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
                    <h3>Administered Item - Preferred Name: <xsl:value-of select="//openMDR:data_element_complete/openMDR:Data_Element//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name"/>
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
                    <xsl:value-of select="//openMDR:data_element_complete/openMDR:Data_Element//openMDR:registration_status"/>
                </td>
            </tr>
            <tr>
                <td class="left_header_cell">Definition</td>
                <td>
                    <xsl:value-of select="//openMDR:data_element_complete/openMDR:Data_Element//openMDR:containing[openMDR:preferred_designation='true']/openMDR:definition_text"/>
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
                            <xsl:value-of select="//openMDR:data_element_complete/openMDR:Data_Element/openMDR:registered_by"/>
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
                <xsl:value-of select="openMDR:sentence-case(local-name())"/>
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
    <xsl:template match="openMDR:cgDatatype">
        <xsl:value-of select="openMDR:datatype_name"/> (<xsl:value-of select="openMDR:datatype_scheme_reference"/>) </xsl:template>
    <xsl:template match="openMDR:Unit_of_Measure">
        <xsl:value-of select="openMDR:unit_of_measure_name"/>
    </xsl:template>
    <xsl:template match="openMDR:reference_uri">
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
    <xsl:template match="openMDR:Data_Element" mode="#default">
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
                    <xsl:apply-templates select="openMDR:representing"/>
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
                    <xsl:apply-templates select="openMDR:administered_item_administration_record"/>
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
    <xsl:template match="openMDR:language_section_language_identifier|reference_document_language_identifier">
        <xsl:value-of select="openMDR:country_identifier"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="openMDR:language_identifier"/>
    </xsl:template>
    <xsl:template match="openMDR:administered_by|openMDR:registered_by|openMDR:submitted_by">
        <table class="layout">
            <xsl:apply-templates select="." mode="name-value-pair"/>
        </table>
    </xsl:template>
    <xsl:template match="openMDR:having">
        <tr>
            <td class="left_header_cell">Context</td>
            <td>
                <xsl:call-template name="html-anchor">
                    <xsl:with-param name="collection">context</xsl:with-param>
                    <xsl:with-param name="id" select="openMDR:context_identifier"/>
                    <xsl:with-param name="text" select="openMDR:context_identifier"/>
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
                                <xsl:apply-templates select="openMDR:containing">
                                    <xsl:sort select="openMDR:name" order="ascending"/>
                                </xsl:apply-templates>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="annotated-models">
        <div>
            <xsl:call-template name="alphabet-links">
                <xsl:with-param name="calling-webpage">annotated-model-management.xquery</xsl:with-param>
            </xsl:call-template>
            <table class="layout">
                <tr>
                    <td>
                        <div class="admin_item_table_header">Name</div>
                    </td>
                    <td width="60%">
                        <div class="admin_item_table_header">Description</div>
                    </td>
                    <td>
                        <div class="admin_item_table_header">Submitted by</div>
                    </td>
                </tr>
                <xsl:apply-templates mode="annotated-model-list"/>
            </table>
        </div>
    </xsl:template>
   
    <xsl:template match="annotated-model" mode="annotated-model-list">
        <xsl:param name="start" select="start"></xsl:param>
        <xsl:param name="extent" select="extent"></xsl:param>
        <xsl:variable name="pos" select="pos"></xsl:variable>
        <xsl:variable name="until" select="number($start+$extent)"></xsl:variable>      
        
        <xsl:if test="(number($pos) ge number($start)) and (number($pos) &lt; number($until))"> 
            <tr>
            <td class="left_header_cell">
                <xsl:copy-of select="anchor/html:a"/>
            </td>
            <td class="left_header_cell">
                <xsl:value-of select="description"/>
            </td>
            <td class="left_header_cell">
                <xsl:variable name="contactinfo" select="submitted_by/openMDR:Organization/openMDR:Contact/openMDR:contact_information"/>
                <xsl:variable name="contactname" select="submitted_by/openMDR:Organization/openMDR:Contact/openMDR:contact_name"/>
                <xsl:variable name="contacttitle" select="submitted_by/openMDR:Organization/openMDR:Contact/openMDR:contact_title"/>
                <xsl:variable name="orgname" select="submitted_by/openMDR:Organization/openMDR:organization_name"/>
                <xsl:variable name="orgmailaddr" select="submitted_by/openMDR:Organization/openMDR:organization_mail_address"/>
                <table>
                    <xsl:if test="$contactname &gt; ''">
                        <tr>
                            <i>Name:</i>
                            <xsl:value-of select="$contactname"/>
                        </tr>
                    </xsl:if>
                    <br/>
                    <xsl:if test="$contacttitle &gt; ''">
                        <tr>
                            <i>Title:</i>
                            <xsl:value-of select="$contacttitle"/>
                        </tr>
                    </xsl:if>
                    <br/>
                    <xsl:if test="$contactinfo &gt; ''">
                        <tr>
                            <i>Email:</i>
                            <xsl:value-of select="$contactinfo"/>
                        </tr>
                    </xsl:if>
                    <br/>
                    <xsl:if test="$orgname &gt; ''">
                        <tr>
                            <i>Org Name:</i>
                            <xsl:value-of select="$orgname"/>
                        </tr>
                    </xsl:if>
                    <br/>
                    <xsl:if test="$orgmailaddr &gt; ''">
                        <tr>
                            <i>Address:</i>
                            <xsl:value-of select="$orgmailaddr"/>
                        </tr>
                    </xsl:if>
                </table>
            </td>
            </tr>
        </xsl:if>
    </xsl:template>
    <xsl:template match="openMDR:models">
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
                    <td class="left_header_cell">Project Long Name</td>
                    <td>
                        <xsl:value-of select="openMDR:annotated_model_project_long_name"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Project Short Name</td>
                    <td>
                        <xsl:value-of select="openMDR:annotated_model_project_short_name"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Project Description</td>
                    <td>
                        <xsl:value-of select="openMDR:annotated_model_project_description"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Service URL</td>
                    <td>
                        <xsl:value-of select="openMDR:service_url"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">File Name</td>
                    <td>
                        <xsl:value-of select="openMDR:file_name"/>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Type</td>
                    <td>
                        <xsl:value-of select="openMDR:file_type"/>
                    </td>
                </tr>
                <!-- <tr>
                    <td class="left_header_cell">Latest version <xsl:value-of select="@version"/>
                    </td>
                    <td>
                        <a href="{string(./openMDR:annotated_model_uri)}">download</a>
                    </td>
                </tr>
                <tr>
                    <td class="left_header_cell">Previous Version</td>
                    <td>
                        <a href="{string(./openMDR:previous_annotated_model_uri)}">download</a>
                    </td>
                    <td>
                    </td>
                </tr> -->
                <tr>
                    <xsl:variable name="dataid" select="string(./@data_identifier)"></xsl:variable>
                    <xsl:variable name="port" select="string(document('/db/mdr/config.xml')/config/common/@port)"></xsl:variable>
                    <xsl:variable name="modelLoc" select="concat('http://localhost:',$port,'/exist/rest/db/mdr/data/models')"></xsl:variable>
                    <xsl:for-each select="document($modelLoc)"> 
                        <xsl:for-each select="/exist:result/exist:collection/exist:resource">
                            <xsl:variable name="filename" select="@name"></xsl:variable>
                            <xsl:variable name="id" select="substring-before(substring-after($filename,'_'),'_')"></xsl:variable>
                            <xsl:variable name="idandversion" select="@version"></xsl:variable>
                            <xsl:variable name="version" select="substring-before(substring-after(substring-after($filename,'_'),'_'),'.xml')"></xsl:variable>
                            <xsl:if test="$id eq $dataid">
                              <tr>
                                <td class="left_header_cell">Version <xsl:value-of select="$version"></xsl:value-of></td>
                                  <td> <xsl:variable name="xmiloc" select="document(concat('http://localhost:9090/exist/rest/db/mdr/data/models/',$filename))/openMDR:models/openMDR:annotated_model_uri"></xsl:variable>
                                    <a href="{$xmiloc}">download</a>
                                  </td>
                              </tr>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:for-each>  
                </tr>
            </tbody>
        </table>
    </xsl:template>
    
    <!-- Added these as we do not need the types to be displayed for annotated models -->
    <xsl:template match="content-by-letter-annotated-model">
        <br/>
        <div class="content_one_pane">
            <xsl:apply-templates select="tabular-content-annotated-model"/>
        </div>
    </xsl:template>
    
    <!-- Displaying the annotated models as the content -->
    <xsl:template match="tabular-content-annotated-model">
        <xsl:apply-templates select="annotated-models"/>
        
        <xsl:apply-templates select="index"/>
        </xsl:template>
</xsl:stylesheet>