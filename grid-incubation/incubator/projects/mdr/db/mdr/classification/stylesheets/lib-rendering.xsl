<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
    <xsl:param name="user" select="guest"/>
    <xsl:param name="title" select="title"/>
    <xsl:param name="footer">true</xsl:param>
    <xsl:param as="xs:boolean" name="show-edit-buttons" select="false()"/>
    <xsl:param name="id"/>
    <xsl:param name="as-xml-link"/>
    
    

   
   <!--generic webpage template -->
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
            <head>
                <link rel="stylesheet" href="../web/stylesheets/main.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/mdr.css" type="text/css"/>
                <link rel="stylesheet" href="../web/stylesheets/cancergrid-style.css" type="text/css"/>
                <link rel="stylesheet" href="../web/treeview/treeview.css" type="text/css"/>
                <link rel="search" type="application/opensearchdescription+xml" title="CancerGrid Data Element Search" href="../web/cde_search.xquery"/>
                <script src="../web/treeview/treeview.js" type="text/javascript"/>
                <title>
                    <xsl:value-of select="$title"/>
                </title>
            </head>
            <body>
                <xsl:call-template name="page-header"/>
                <br/>
                <div class="admin_item_header">
                    <xsl:value-of select="$title"/>
                </div>
                <br/>
                <xsl:if test="$show-edit-buttons=true() and $user!='guest'">
                    <table>
                        <tr>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">supersede</xsl:with-param>
                                    <xsl:with-param name="alt">supersede</xsl:with-param>
                                    <xsl:with-param name="link">
                                        <xsl:value-of select="concat('../edit/supersede_admin_item.xquery?supersede-source=', $id)"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">edit</xsl:with-param>
                                    <xsl:with-param name="alt">edit</xsl:with-param>
                                    <xsl:with-param name="link">
                                        <xsl:value-of select="concat('../edit/editAdminItem.xquery?compound_id=', $id)"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                </xsl:if>
                <xsl:if test="$as-xml-link">
                    <table>
                        <tr>
                            <td>
                                <xsl:call-template name="css-rollover-button">
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
                        </tr>
                    </table>
                </xsl:if>
                <xsl:apply-templates/>
                <xsl:if test="$footer='true'">
                    <xsl:call-template name="copyright"/>
                    <xsl:call-template name="XHTML-transitional"/>
                </xsl:if>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="div">
        <xsl:copy-of select="."/>
    </xsl:template>
    <xsl:template match="index">
        <table class="layout">
            <tr>
                <td align="center">
                    <a href="{concat(url,'?',parameter,'=',value, '&amp;start=',1)}">first page</a>
                </td>
                <td align="center">
                    <a href="{concat(url,'?',parameter,'=',value, '&amp;start=',previous)}">previous page</a>
                </td>
                <td align="center">
                    <a href="{concat(url,'?',parameter,'=',value, '&amp;start=',next)}">next page</a>
                </td>
                <td align="center">
                    <a href="{concat(url,'?',parameter,'=',value, '&amp;start=',last)}">last page</a>
                </td>
            </tr>
            <tr>
                <td colspan="4" align="center">records <xsl:value-of select="start"/> to <xsl:value-of select="start + extent - 1"/> of <xsl:value-of select="count"/>
                </td>
            </tr>
        </table>
    </xsl:template>
   
   <!-- renders the normal css rollover button-->
    <xsl:template name="css-rollover-button">
        <xsl:param name="link"/>
        <xsl:param name="alt"/>
        <xsl:param name="text"/>
        <div class="cssnav">
            <a href="{$link}" target="_top">
                <img src="../web/images/greybutton.bmp" alt="{$alt}"/>
                <span>
                    <xsl:value-of select="$text"/>
                </span>
            </a>
        </div>
    </xsl:template>
   
   
   <!--does lower case conversion-->
    <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
    <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
    <xsl:template name="lower-case">
        <xsl:param name="toconvert"/>
        <xsl:value-of select="translate($toconvert,$ucletters,$lcletters)"/>
    </xsl:template>
   
   
   
   <!-- renders the copyright notice on the bottom of the page-->
    <xsl:template name="copyright">
        <p class="copyright"> Copyright (C) 2006 The CancerGrid Consortium (<a href="http://www.cancergrid.org">http://www.cancergrid.org</a>) </p>
    </xsl:template>
    <xsl:template name="page-header">
        <div>
            <img src="../web/images/main_logo.gif" alt="cancer grid header"/>
            <br/>
            <h2 class="title">cancergrid metadata registry</h2>
            <br/>
            <table>
                <tr>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">registry home</xsl:with-param>
                            <xsl:with-param name="alt">registry home</xsl:with-param>
                            <xsl:with-param name="link">../web/homepage.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">contents</xsl:with-param>
                            <xsl:with-param name="alt">contents</xsl:with-param>
                            <xsl:with-param name="link">../web/contents.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">search</xsl:with-param>
                            <xsl:with-param name="alt">search</xsl:with-param>
                            <xsl:with-param name="link">../web/search.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">model</xsl:with-param>
                            <xsl:with-param name="alt">model</xsl:with-param>
                            <xsl:with-param name="link">../model/index.htm</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">documentation</xsl:with-param>
                            <xsl:with-param name="alt">documentation</xsl:with-param>
                            <xsl:with-param name="link">../web/documentation.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <xsl:choose>
                        <xsl:when test="$user='guest'">
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">login</xsl:with-param>
                                    <xsl:with-param name="alt">login</xsl:with-param>
                                    <xsl:with-param name="link">../web/login.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </xsl:when>
                        <xsl:otherwise>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">logout</xsl:with-param>
                                    <xsl:with-param name="alt">logout</xsl:with-param>
                                    <xsl:with-param name="link">../web/logout.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">maintenance</xsl:with-param>
                                    <xsl:with-param name="alt">maintenance</xsl:with-param>
                                    <xsl:with-param name="link">../edit/maintenance.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">terminologies</xsl:with-param>
                                    <xsl:with-param name="alt">terminologies</xsl:with-param>
                                    <xsl:with-param name="link">../classification/used-schemes.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </xsl:otherwise>
                    </xsl:choose>
                    <td/>
                    <td/>
                    <td/>
                </tr>
            </table>
            <xsl:call-template name="welcome"/>
        </div>
    </xsl:template>

<!--renders the welcome message -->
    <xsl:template name="welcome">
        <br/>
        <div class="welcome">welcome: <xsl:value-of select="$user"/>
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
   
<!--reusable web page parts -->
    <xsl:template match="cgMDR:language_section_language_identifier|reference_document_language_identifier">
        <xsl:value-of select="cgMDR:country_identifier"/>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="cgMDR:language_identifier"/>
    </xsl:template>
   
<!--letter selectors -->
    <xsl:template name="iterate-letter">
        <xsl:param name="letter" select="'abcdefghijklmnopqrstuvwxyz'"/>
        <xsl:param name="calling-webpage"/>
        <xsl:call-template name="letter-cell">
            <xsl:with-param name="calling-webpage" select="$calling-webpage"/>
            <xsl:with-param name="contents" select="substring($letter,1,1)"/>
        </xsl:call-template>
        <xsl:if test="string-length($letter)&gt;1">
            <xsl:call-template name="iterate-letter">
                <xsl:with-param name="letter" select="substring($letter,2)"/>
                <xsl:with-param name="calling-webpage" select="$calling-webpage"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="letter-cell">
        <xsl:param name="contents"/>
        <xsl:param name="calling-webpage"/>
        <td>
            <xsl:call-template name="css-rollover-button-letter">
                <xsl:with-param name="alt" select="$contents"/>
                <xsl:with-param name="link" select="concat($calling-webpage, '?letter=',$contents,'&amp;','use-stylesheet=true')"/>
                <xsl:with-param name="text" select="$contents"/>
            </xsl:call-template>
        </td>
    </xsl:template>
    <xsl:template name="alphabet-links">
        <xsl:param name="calling-webpage"/>
        <div align="center">
            <table class="alphabet">
                <tr>
                    <xsl:call-template name="iterate-letter">
                        <xsl:with-param name="calling-webpage" select="$calling-webpage"/>
                    </xsl:call-template>
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
   
   
   
<!--for the property list webpage -->
    <xsl:template match="properties">
        <xsl:call-template name="alphabet-links">
            <xsl:with-param name="calling-webpage">supported_properties.xquery</xsl:with-param>
        </xsl:call-template>
        <table class="layout">
            <tr>
                <td>
                    <div class="admin_item_table_header">administered item identifier</div>
                </td>
                <td>
                    <div class="admin_item_table_header">name</div>
                </td>
                <td>
                    <div class="admin_item_table_header">description</div>
                </td>
            </tr>
            <xsl:apply-templates mode="property-listing"/>
        </table>
    </xsl:template>
    <xsl:template match="property" mode="property-listing">
        <tr>
            <td class="left_header_cell">
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:copy-of select="anchor/a"/>
            </td>
            <td>
                <xsl:value-of select="description"/>
            </td>
        </tr>
    </xsl:template>
   
   
<!--representation class listings -->
    <xsl:template match="representation-classes">
        <table>
            <tr>
                <td>
                    <div class="admin_item_table_header">administered item identifier</div>
                </td>
                <td>
                    <div class="admin_item_table_header">name</div>
                </td>
                <td>
                    <div class="admin_item_table_header">description</div>
                </td>
            </tr>
            <xsl:apply-templates mode="representation-class-listing"/>
        </table>
    </xsl:template>
    <xsl:template match="representation-class" mode="representation-class-listing">
        <tr>
            <td class="left_header_cell">
                <xsl:copy-of select="id"/>
            </td>
            <td>
                <xsl:copy-of select="anchor/a"/>
            </td>
            <td>
                <xsl:value-of select="description"/>
            </td>
        </tr>
    </xsl:template>


<!-- for the object class list webpages -->
    <xsl:template match="object_classes">
        <xsl:call-template name="alphabet-links">
            <xsl:with-param name="calling-webpage">supported_object_classes.xquery</xsl:with-param>
        </xsl:call-template>
        <table class="layout">
            <tr>
                <td>
                    <div class="admin_item_table_header">administered item identifier</div>
                </td>
                <td>
                    <div class="admin_item_table_header">name</div>
                </td>
                <td>
                    <div class="admin_item_table_header">description</div>
                </td>
            </tr>
            <xsl:apply-templates mode="object_class_list"/>
        </table>
    </xsl:template>
    
    
    <!--rendering contents wepage -->
    <xsl:template match="contents">
        <xsl:call-template name="alphabet-links">
            <xsl:with-param name="calling-webpage">contents.xquery</xsl:with-param>
        </xsl:call-template>
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
    <xsl:template match="object_class" mode="object_class_list">
        <tr>
            <td class="left_header_cell">
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:copy-of select="anchor/a"/>
            </td>
            <td>
                <xsl:value-of select="description"/>
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
   
   
   <!--data element tabular display-->
    <xsl:template match="result-set">
        <xsl:call-template name="alphabet-links">
            <xsl:with-param name="calling-webpage">
                <xsl:value-of select="cdeDatasetView.xquery"/>
            </xsl:with-param>
        </xsl:call-template>
        <br/>
        <xsl:call-template name="tabular-header">
            <xsl:with-param name="empty-message">Please select a letter</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="tabular-header">
        <xsl:param name="empty-message"/>
        <xsl:choose>
            <xsl:when test="data-element">
                <table class="layout">
                    <tr valign="bottom">
                        <td>
                            <div class="admin_item_table_header">CDE Name and ID</div>
                        </td>
                        <td>
                            <div class="admin_item_table_header">Alternate name</div>
                        </td>
                        <td>
                            <div class="admin_item_table_header">Definition</div>
                        </td>
                        <td>
                            <div class="admin_item_table_header">Values</div>
                        </td>
                    </tr>
                    <xsl:apply-templates select="data-element" mode="tabular-data-element-display"/>
                </table>
                <xsl:apply-templates select="index"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$empty-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="result-set-by-tree">
        <table class="layout">
            <tr>
                <td width="30%" valign="top">
                    <xsl:apply-templates select="//treeview"/>
                </td>
                <td width="70%" valign="top">
                    <xsl:call-template name="tabular-header">
                        <xsl:with-param name="empty-message">Please select a classification</xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="search-result-set">
        <xsl:variable name="phrase">
            <xsl:value-of select="//search-term"/>
        </xsl:variable>
        <xsl:variable name="action">
            <xsl:value-of select="//form-action"/>
        </xsl:variable>
        <div class="content_one_pane">
            <div class="centre_block">
                <form name="frmSrch" method="get" class="cancergrid" action="{$action}">
              Search phrase <input type="text" name="phrase" value="{$phrase}"/>
                    <input class="cgButton" type="submit" value="Submit query"/>
                </form>
            </div>
        </div>
        <xsl:call-template name="tabular-header">
            <xsl:with-param name="empty-message">Please enter a search term</xsl:with-param>
        </xsl:call-template>
    </xsl:template>
   
   <!--create a table row for each data element -->
    <xsl:template match="data-element" mode="tabular-data-element-display">
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
            </td>
            <td>
            <!-- ALTERNATIVE NAMES -->
                <xsl:value-of select="names/all-names/name"/>
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
   
   <!--should print out the column headings.  The xsl:choose should print the table headings, depending 
      on whether the values node represents a enumerated or non-enumerated data element-->
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
   
   
   
   
   <!--treeview stuff-->
    <xsl:param name="param-deploy-treeview" select="'false'"/><!-- is the client Netscape / Mozilla or Internet Explorer. Thanks to Bill, 90% of sheeps use Internet Explorer so it will the default value-->
    <xsl:param name="param-is-netscape" select="'false'"/><!-- hozizontale distance in pixels between a folder and its leaves -->
    <xsl:param name="param-shift-width" select="18"/><!-- image source directory-->
    <xsl:variable name="var-simple-quote">'</xsl:variable>
    <xsl:variable name="var-slash-quote">\'</xsl:variable>
    <xsl:output indent="yes"/><!--
      **
      **  Model "treeview"
      ** 
      **  This model transforms an XML treeview into an html treeview
      **  
   -->
    <xsl:template match="treeview"><!-- -->
        <div id="treeroot" class="treeview">
            <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td><!-- Apply the template folder starting with a depth in the tree of 1-->
                        <xsl:apply-templates select="folder">
                            <xsl:with-param name="depth" select="1"/>
                        </xsl:apply-templates>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template><!--
      **
      **  Model "folder"
      ** 
      **  This model transforms a folder element. Prints a plus (+) or minus (-)  image, the folder image and a title
      **  
   -->
    <xsl:template match="folder">
        <xsl:param name="depth"/>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr><!-- If first level of depth, do not shift of $param-shift-width-->
                <xsl:if test="$depth &gt;1">
                    <td width="{$param-shift-width}"/>
                </xsl:if>
                <td>
                    <a class="folder">
                        <xsl:if test="@code">
                            <xsl:attribute name="onclick">toggle2(this, '<xsl:value-of select="@code"/>')</xsl:attribute>
                        </xsl:if>
                        <xsl:attribute name="id">
                            <xsl:value-of select="@code"/>
                        </xsl:attribute>
                        <xsl:if test="not(@code)">
                            <xsl:attribute name="onclick">toggle(this)</xsl:attribute>
                        </xsl:if><!-- If the treeview is unfold, the image minus (-) is displayed-->
                        <xsl:if test="@expanded">
                            <xsl:if test="@expanded='true'">
                                <img src="images/minus.gif"/>
                            </xsl:if><!-- plus (+) otherwise-->
                            <xsl:if test="@expanded='false'">
                                <img src="images/plus.gif"/>
                            </xsl:if>
                        </xsl:if>
                        <xsl:if test="not(@expanded)">
                            <xsl:if test="$param-deploy-treeview = 'true'">
                                <img src="images/minus.gif"/>
                            </xsl:if>
                            <xsl:if test="$param-deploy-treeview = 'false' or not(@expanded)">
                                <img src="images/plus.gif"/>
                            </xsl:if>
                        </xsl:if>
                        <img src="{@img}"><!-- if the attribut alt is present-->
                            <xsl:if test="@alt"><!-- if Netscape / Mozilla -->
                                <xsl:if test="$param-is-netscape='true'">
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="@alt"/>
                                    </xsl:attribute>
                                </xsl:if><!-- if Internet Explorer -->
                                <xsl:if test="$param-is-netscape='false'">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="@alt"/>
                                    </xsl:attribute>
                                </xsl:if>
                            </xsl:if>
                        </img>
                        <xsl:value-of select="@title"/>
                    </a><!-- Shall we expand all the leaves of the treeview ? no by default-->
                    <div>
                        <xsl:if test="@expanded">
                            <xsl:if test="@expanded='true'">
                                <xsl:attribute name="style">display:block;</xsl:attribute>
                            </xsl:if><!-- plus (+) otherwise-->
                            <xsl:if test="@expanded='false'">
                                <xsl:attribute name="style">display:none;</xsl:attribute>
                            </xsl:if>
                        </xsl:if>
                        <xsl:if test="not(@expanded)">
                            <xsl:if test="$param-deploy-treeview = 'true'">
                                <xsl:attribute name="style">display:block;</xsl:attribute>
                            </xsl:if>
                            <xsl:if test="$param-deploy-treeview = 'false'">
                                <xsl:attribute name="style">display:none;</xsl:attribute>
                            </xsl:if>
                        </xsl:if><!-- Thanks to the magic of reccursive calls, all the descendants of the present folder are gonna be built -->
                        <xsl:apply-templates select="folder">
                            <xsl:with-param name="depth" select="$depth+1"/>
                        </xsl:apply-templates><!-- print all the leaves of this folder-->
                        <xsl:apply-templates select="leaf"/>
                    </div>
                </td>
            </tr>
        </table>
    </xsl:template><!--
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
                    <a class="leaf"><!-- The line is very long bu I have no choice, I called the function replace-string to replace the quotes (') by /' -->
                        <xsl:attribute name="onclick">selectLeaf('<xsl:call-template name="replace-string">
                                <xsl:with-param name="text" select="@code"/>
                                <xsl:with-param name="from" select="$var-simple-quote"/>
                                <xsl:with-param name="to" select="$var-slash-quote"/>
                            </xsl:call-template>')</xsl:attribute><!-- if it is the last leaf, print a different image for the link to the folder-->
                        <xsl:attribute name="id">
                            <xsl:value-of select="@code"/>
                        </xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="position()=last()">
                                <img src="images/lastlink.gif"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <img src="images/link.gif"/>
                            </xsl:otherwise>
                        </xsl:choose>
                        <img src="{@img}"><!-- if the attribut alt is present-->
                            <xsl:if test="@alt"><!-- if Netscape / Mozilla -->
                                <xsl:if test="$param-is-netscape='true'">
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="@alt"/>
                                    </xsl:attribute>
                                </xsl:if><!-- if Internet Explorer -->
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
    </xsl:template><!--
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
    <xsl:template name="XHTML-transitional">
        <p align="right">
            <a href="http://validator.w3.org/check?uri=referer">
                <img src="../web/images/valid-xhtml10.png" alt="Valid XHTML 1.0 Transitional" height="31" width="88"/>
            </a>
            <a href="http://jigsaw.w3.org/css-validator/check/referer">
                <img style="border:0;width:88px;height:31px" src="../web/images/vcss.png" alt="Valid CSS!"/>
            </a>
        </p>
    </xsl:template>
</xsl:stylesheet>