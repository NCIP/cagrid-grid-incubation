package org.cagrid.iso21090.portal.discovery;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.cagrid.iso21090.portal.discovery.constants.Constants;


public class ISO21090TypeSelectionComponent extends NamespaceTypeDiscoveryComponent {

    public ISO21090TypeSelectionComponent(DiscoveryExtensionDescriptionType descriptor, NamespacesType currentNamespaces) {
        super(descriptor, currentNamespaces);
        // TODO Auto-generated constructor stub
    }


    @Override
    public NamespaceType[] createNamespaceType(File schemaDir, NamespaceReplacementPolicy arg1,
        MultiEventProgressBar arg2) {

        // check the namespace replacement policy and see what to do if the
        // stuff we plan to add already exists
        // if (replacementPolicy.equals(NamespaceReplacementPolicy.IGNORE)) {

        // copy the schemas
        
        try {
            copyISOSchemas(schemaDir);
        } catch (IOException e) {
            addError("Problem copying schemas:" + e.getMessage());
            setErrorCauseThrowable(e);
            return null;
        }

        // create the namespace types with commontools
        //  <ns2:Namespace location="./ISO_datatypes_Narrative.xsd" namespace="uri:iso.org:21090" packageName="org.iso._21090" xsi:type="ns2:NamespaceType">
        //  <ns2:Namespace generateStubs="false" location="./ISO_extensions.xsd" namespace="http://iso21090.nci.nih.gov" packageName="gov.nih.nci.iso21090.extensions" xsi:type="ns2:NamespaceType">
        //   <ns2:SchemaElement className="Bl" deserializer="gov.nih.nci.iso21090.grid.ser.JaxbDeserializerFactory" serializer="gov.nih.nci.iso21090.grid.ser.JaxbSerializerFactory" type="Bl" xsi:type="ns2:SchemaElementType"/>

        // walk thru them and configure the serializers

        return null;

    }


    private void copyISOSchemas(File schemaDir) throws IOException {
        System.out.println("Copying schemas to " + schemaDir);
        File extensionSchemaDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator
            + Constants.EXTENSION_NAME + File.separator + "schema");
        List<File> schemaFiles = Utils.recursiveListFiles(extensionSchemaDir, new FileFilters.XSDFileFilter());
        for (File schemaFile : schemaFiles) {
            String subname = schemaFile.getCanonicalPath().substring(
                extensionSchemaDir.getCanonicalPath().length() + File.separator.length());
            copySchema(subname, schemaDir);
        }
    }


    private void copySchema(String schemaName, File outputDir) throws IOException {
        File schemaFile = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + Constants.EXTENSION_NAME
            + File.separator + "schema" + File.separator + schemaName);
        System.out.println("Copying schema from " + schemaFile.getAbsolutePath());
        File outputFile = new File(outputDir + File.separator + schemaName);
        System.out.println("Saving schema to " + outputFile.getAbsolutePath());
        Utils.copyFile(schemaFile, outputFile);
    }

}
