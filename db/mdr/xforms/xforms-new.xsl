<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:cgMDR="http://www.cancergrid.org/schema/cgMDR" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:ISO11179="http://www.cancergrid.org/schema/ISO11179" version="2.0">
    <xsl:output media-type="application/xhtml+xml" method="html" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
    <xsl:param name="new-identifier" as="xs:string"/>
    <xsl:param name="user">guest</xsl:param>
    <xsl:template match="xforms:submission[@id='write-instance']">
        <xforms:submission id="write-instance" action="new.xml" method="put" replace="instance" instance="main"/>
        
        <!--work out the file name for submit-->
        <xforms:action ev:observer="myModel" ev:event="xforms-submit">
            <xforms:load resource="javascript:newSaveSubmit('myModel', 'main')"/>
        </xforms:action>
    </xsl:template>
    <xsl:template match="xforms:model">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
            <xforms:instance id="administered-by" src="administered-by.xquery"/>
            <xforms:instance id="submitted-by" src="submitted-by.xquery"/>
            <xforms:instance id="registered-by" src="registered-by.xquery"/>
            <xforms:instance id="context-identifier" src="context-identifier.xquery"/>
            <xforms:instance id="datatype" src="datatype.xquery"/>
            <xforms:instance id="unit-of-measure" src="unit-of-measure.xquery"/>
            <xforms:instance id="item-registration-authority-identifier" src="item-registration-authority-identifier.xquery"/>
            <xforms:instance id="typed-by" src="typed-by.xquery"/>
            <xforms:instance id="provided-by" src="provided-by.xquery"/>
            <xforms:instance id="new-id" src="new-id.xquery"/>
            <xforms:submission id="get-new-id" action="new-id.xquery" method="get" replace="instance" instance="new-id"/>
        </xsl:copy>
    </xsl:template>

    
    <!--live lists-->
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:administered_by')]">
        <xforms:select1 ref="//cgMDR:administered_by">
            <xforms:label>Administered By</xforms:label>
            <xforms:itemset nodeset="instance('administered-by')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:submitted_by')]">
        <xforms:select1 ref="//cgMDR:submitted_by">
            <xforms:label>Submitted By</xforms:label>
            <xforms:itemset nodeset="instance('submitted-by')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:registered_by')]">
        <xforms:select1 ref="//cgMDR:registered_by">
            <xforms:label>Registered By</xforms:label>
            <xforms:itemset nodeset="instance('registered-by')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:context_identifier')]">
        <xforms:select1 ref="//cgMDR:context_identifier">
            <xforms:label>Context identifier for following names</xforms:label>
            <xforms:itemset nodeset="instance('context-identifier')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:value_domain_datatype')]">
        <xforms:select1 ref="//cgMDR:value_domain_datatype">
            <xforms:label>Datatype</xforms:label>
            <xforms:itemset nodeset="instance('datatype')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:value_domain_unit_of_measure')]">
        <xforms:select1 ref="//cgMDR:value_domain_unit_of_measure">
            <xforms:label>Unit of Measure</xforms:label>
            <xforms:itemset nodeset="instance('unit-of-measure')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'item_registration_authority_identifier')]">
        <xforms:select1 ref="//@item_registration_authority_identifier" selection="open">
            <xforms:label>Item Registration Authority Identifier</xforms:label>
            <xforms:itemset nodeset="instance('item-registration-authority-identifier')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:typed_by')]">
        <xforms:select1 ref="//cgMDR:typed_by">
            <xforms:label>Typed by representation class</xforms:label>
            <xforms:itemset nodeset="instance('typed-by')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:provided_by')]">
        <xforms:select1 ref="//cgMDR:provided_by">
            <xforms:label>Reference document provided by</xforms:label>
            <xforms:itemset nodeset="instance('provided-by')//item">
                <xforms:label ref="label"/>
                <xforms:value ref="value"/>
            </xforms:itemset>
        </xforms:select1>
    </xsl:template>
    <xsl:template match="xforms:input[ends-with(@ref,'cgMDR:document_base64')]">
        <upload xmlns="http://www.w3.org/2002/xforms" ref="//cgMDR:document_base64">
            <xforms:label>Upload document</xforms:label>
            <xforms:mediatype ref="//cgMDR:reference_document_mime_type"/>
            <xforms:filename ref="//cgMDR:reference_document_uri"/>
        </upload>
    </xsl:template>
    <xsl:template match="@data_identifier">
        <xsl:attribute name="data_identifier" select="$new-identifier"/>
    </xsl:template>
    <xsl:template match="@reference_document_identifier">
        <xsl:attribute name="reference_document_identifier" select="$new-identifier"/>
    </xsl:template>
    <xsl:template match="cgMDR:value_meaning_identifier">
        <cgMDR:value_meaning_identifier>
            <xsl:value-of select="$new-identifier"/>
        </cgMDR:value_meaning_identifier>
    </xsl:template>
    <xsl:template match="cgMDR:value_meaning_begin_date">
        <cgMDR:value_meaning_begin_date>
            <xsl:value-of select="substring-before(string(current-date()),'Z')"/>
        </cgMDR:value_meaning_begin_date>
    </xsl:template>
    <xsl:template match="xforms:insert[ends-with(@nodeset, 'cgMDR:classified_by')]">
        <xsl:variable name="at" select="@at"/>
        <xforms:action ev:event="DOMActivate">
            <xforms:insert at="last()" position="after" nodeset="//cgMDR:classified_by"/>
            <xforms:setvalue ref="//cgMDR:classified_by[{$at}]" value=""/>
            <xforms:load resource="javascript:findClassifier('cgMDR:classified_by');"/>
        </xforms:action>
    </xsl:template>
    <xsl:template match="xforms:insert[ends-with(@nodeset, 'cgMDR:described_by')]">
        <xsl:variable name="at" select="@at"/>
        <xforms:action ev:event="DOMActivate">
            <xforms:insert at="last()" position="after" nodeset="//cgMDR:described_by"/>
            <xforms:setvalue ref="//cgMDR:described_by[{$at}]" value=""/>
            <xforms:load resource="javascript:findRefDoc('cgMDR:described_by');"/>
        </xforms:action>
    </xsl:template>
    <xsl:template match="xforms:insert[ends-with(@nodeset, 'cgMDR:Value_Meaning')]">
        <xsl:variable name="at" select="@at"/>
        <xforms:action ev:event="DOMActivate">
            <xforms:insert at="{$at}" position="after" nodeset="//cgMDR:Value_Meaning"/>
            <xforms:setvalue ref="//cgMDR:Value_Meaning[index('repeat6')]/cgMDR:value_meaning_identifier" value="instance('new-id')//id"/>
            <xforms:setvalue ref="//cgMDR:Value_Meaning[index('repeat6')]/cgMDR:value_meaning_begin_date" value="substring-before(now(),'T')"/>
            <xforms:setvalue ref="//cgMDR:Value_Meaning[index('repeat6')]/cgMDR:value_meaning_description"/>
            <xforms:setvalue ref="//cgMDR:Value_Meaning[index('repeat6')]/cgMDR:value_meaning_end_date" value="'2020-01-01'"/>
            <xforms:setvalue ref="//cgMDR:Value_Meaning[index('repeat6')]/cgMDR:reference_uri"/>
            <xforms:send submission="get-new-id"/>
        </xforms:action>
    </xsl:template>
    <xsl:template match="xforms:bind[ends-with(@nodeset,'cgMDR:last_change_date')]">
        <xforms:bind nodeset="//cgMDR:last_change_date" type="xs:date" calculate="substring-before(now(),'T')"/>
    </xsl:template>
    <xsl:template match="xforms:bind[ends-with(@nodeset,'cgMDR:creation_date')]">
        <xforms:bind nodeset="//cgMDR:creation_date" type="xs:date" calculate="substring-before(now(),'T')"/>
        <xforms:bind nodeset="//cgMDR:document_base64" type="xs:base64Binary" required="true()"/>
    </xsl:template>
    <xsl:template match="xforms:insert[ends-with(@nodeset, 'cgMDR:containing')]">]
        <xsl:variable name="at" select="@at"/>
        <xforms:action ev:event="DOMActivate">
            <xforms:insert at="last()" position="after" nodeset="//cgMDR:containing"/>
            <xforms:setvalue ref="//cgMDR:containing[index('repeat6')]/@permissible_value_identifier" value="instance('new-id')//id"/>
            <xforms:setvalue ref="//cgMDR:containing[index('repeat6')]/cgMDR:permissible_value_begin_date" value="substring-before(now(),'T')"/>
            <xforms:setvalue ref="//cgMDR:containing[index('repeat6')]/cgMDR:value_item" value=""/>
            <xforms:setvalue ref="//cgMDR:containing[index('repeat6')]/cgMDR:contained_in" value=""/>
        </xforms:action>
    </xsl:template>
    <xsl:template match="div[@class='welcome']">
        <div class="welcome">welcome: <xsl:value-of select="$user"/>
        </div>
    </xsl:template>
    
    <!--traversal templates-->
    <xsl:template match="/">
        <xsl:apply-templates select="*|@*|text()|comment()"/>
    </xsl:template>
    <xsl:template match="*|@*|comment()">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()|comment()"/>
        </xsl:copy>
    </xsl:template>
    
    
    <!--from lib-rendering-->
    <xsl:template name="css-rollover-button">
        <xsl:param name="link"/>
        <xsl:param name="alt"/>
        <xsl:param name="text"/>
        <xsl:param name="onclick" required="no"/>
        <xsl:element name="div">
            <xsl:attribute name="class">cssnav</xsl:attribute>
            <xsl:element name="a">
                <xsl:if test="$link &gt; ''">
                    <xsl:attribute name="href" select="$link"/>
                </xsl:if>
                <xsl:attribute name="target">_top</xsl:attribute>
                <xsl:if test="$onclick&gt;''">
                    <xsl:attribute name="onclick" select="$onclick"/>
                </xsl:if>
                <img src="../web/images/greybutton.bmp" alt="{$alt}"/>
                <span>
                    <xsl:value-of select="$text"/>
                </span>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="xhtml:body">
        <body>
            <xsl:call-template name="page-header"/>
            <xsl:apply-templates select="*|@*|text()|comment()"/>
        </body>
    </xsl:template>
    <xsl:template name="page-header">
        <div>
            <a href="../web/homepage.xquery">
                <img src="../web/images/main_logo.gif" alt="cancer grid header"/>
            </a>
            <br/>
            <h2 class="title">cancergrid metadata registry</h2>
            <br/>
            <table>
                <tr>
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
                            <xsl:with-param name="text">classification</xsl:with-param>
                            <xsl:with-param name="alt">classification</xsl:with-param>
                            <xsl:with-param name="link">../web/classification.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">documentation</xsl:with-param>
                            <xsl:with-param name="alt">documentation</xsl:with-param>
                            <xsl:with-param name="link">../web/documentation.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">reference documents</xsl:with-param>
                            <xsl:with-param name="alt">reference documents</xsl:with-param>
                            <xsl:with-param name="link">../web/reference-document-management.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">model</xsl:with-param>
                            <xsl:with-param name="alt">model</xsl:with-param>
                            <xsl:with-param name="link">../model/index.htm</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">terminologies</xsl:with-param>
                            <xsl:with-param name="alt">terminologies</xsl:with-param>
                            <xsl:with-param name="link">../classification/used-schemes.xquery</xsl:with-param>
                        </xsl:call-template>
                    </td>
                    <xsl:choose>
                        <xsl:when test="$user='guest'">
                            <td>
                                <xsl:call-template name="css-rollover-button">
                                    <xsl:with-param name="text">login</xsl:with-param>
                                    <xsl:with-param name="alt">login</xsl:with-param>
                                    <xsl:with-param name="link">../web/login.xquery</xsl:with-param>
                                </xsl:call-template>
                            </td>
                            <td/>
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
                        </xsl:otherwise>
                    </xsl:choose>
                    <td>
                        <xsl:call-template name="css-rollover-button">
                            <xsl:with-param name="text">back</xsl:with-param>
                            <xsl:with-param name="alt">back</xsl:with-param>
                            <xsl:with-param name="onclick">history.go(-1)</xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
            </table>
            <xsl:call-template name="welcome"/>
        </div>
    </xsl:template>
    <xsl:template name="welcome">
        <br/>
        <div class="welcome">welcome: <xsl:value-of select="$user"/>
        </div>
    </xsl:template>
</xsl:stylesheet>