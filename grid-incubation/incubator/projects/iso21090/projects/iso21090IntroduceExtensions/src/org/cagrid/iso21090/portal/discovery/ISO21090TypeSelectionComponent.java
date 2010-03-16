package org.cagrid.iso21090.portal.discovery;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.portal.discovery.constants.Constants;


@SuppressWarnings("serial")
public class ISO21090TypeSelectionComponent extends NamespaceTypeDiscoveryComponent {
    private static final Log LOG = LogFactory.getLog(ISO21090TypeSelectionComponent.class);


    public ISO21090TypeSelectionComponent(DiscoveryExtensionDescriptionType descriptor, NamespacesType currentNamespaces) {
        super(descriptor, currentNamespaces);
    }


    protected String getISONamespace() {
        return ExtensionTools.getProperty(getDescriptor().getProperties(), Constants.DATATYPES_NAMESPACE_KEY);
    }


    protected String getISOXSDFilename() {
        return ExtensionTools.getProperty(getDescriptor().getProperties(), Constants.DATATYPES_FILENAME_KEY);
    }


    protected String getISOPackage() {
        return ExtensionTools.getProperty(getDescriptor().getProperties(), Constants.DATATYPES_PACKAGE_KEY);
    }


    protected String getISOExtensionsNamespace() {
        return ExtensionTools.getProperty(getDescriptor().getProperties(), Constants.EXTENSION_NAMESPACE_KEY);
    }


    protected String getISOExtensionsXSDFilename() {
        return ExtensionTools.getProperty(getDescriptor().getProperties(), Constants.EXTENSION_FILENAME_KEY);
    }


    protected String getISOExtensionsPackage() {
        return ExtensionTools.getProperty(getDescriptor().getProperties(), Constants.EXTENSION_PACKAGE_KEY);
    }


    @Override
    public NamespaceType[] createNamespaceType(File schemaDir, NamespaceReplacementPolicy replacementPolicy,
        MultiEventProgressBar progress) {

        // check the namespace replacement policy and see what to do if the
        // stuff we plan to add already exists
        if (namespaceAlreadyExists(getISONamespace())) {
            if (replacementPolicy.equals(NamespaceReplacementPolicy.ERROR)) {
                String error = "Namespace ("
                    + getISONamespace()
                    + ") already exists, and policy was to error. Change the setting in the Preferences to REPLACE or IGNORE to avoid this error.";
                LOG.error(error);
                addError(error);
                return null;
            } else {
                LOG.info("The ISO21090 Datatype schema already exists, the non-ERROR policy is:"
                    + replacementPolicy.getValue());
            }
        }
        if (namespaceAlreadyExists(getISOExtensionsNamespace())) {
            if (replacementPolicy.equals(NamespaceReplacementPolicy.ERROR)) {
                String error = "Namespace ("
                    + getISOExtensionsNamespace()
                    + ") already exists, and policy was to error. Change the setting in the Preferences to REPLACE or IGNORE to avoid this error.";
                LOG.error(error);
                addError(error);
                return null;
            } else {
                LOG.info("The ISO21090 Extensions schema already exists, the non-ERROR policy is:"
                    + replacementPolicy.getValue());
            }
        }

        // copy the schemas
        File copiedISOXSDFilename = null;
        File copiedISOExtensionsXSDFilename = null;
        try {
            copiedISOXSDFilename = copySchemaFromExtensionDir(getISOXSDFilename(), schemaDir);
            copiedISOExtensionsXSDFilename = copySchemaFromExtensionDir(getISOExtensionsXSDFilename(), schemaDir);
        } catch (IOException e) {
            addError("Problem copying schemas:" + e.getMessage());
            setErrorCauseThrowable(e);
            return null;
        }

        // copy the jar files and fix classpath
        try {
            copyLibraries(getServiceDirectory(schemaDir));
        } catch (Exception e) {
            addError("Problem copying jar files:" + e.getMessage());
            setErrorCauseThrowable(e);
            return null;
        }

        NamespaceType[] createdTypes = new NamespaceType[2];
        // create the namespace types with commontools
        try {
            createdTypes[0] = CommonTools.createNamespaceType(copiedISOXSDFilename.getAbsolutePath(), schemaDir);
            createdTypes[0].setGenerateStubs(false);
            createdTypes[0].setPackageName(getISOPackage());

            createdTypes[1] = CommonTools.createNamespaceType(copiedISOExtensionsXSDFilename.getAbsolutePath(),
                schemaDir);
            createdTypes[1].setGenerateStubs(false);
            createdTypes[1].setPackageName(getISOExtensionsPackage());
        } catch (Exception e) {
            addError("Problem creating namespace types:" + e.getMessage());
            setErrorCauseThrowable(e);
            return null;
        }

        // walk thru them and configure the serializers
        // TODO: should both use the same framework? The reference service just
        // configures the extension schema
        for (SchemaElementType se : createdTypes[0].getSchemaElement()) {
            // TODO is this a valid assumption for all the types? If not, need a
            // mapping file
            se.setClassName(se.getType());
            se.setDeserializer(Constants.DESERIALIZER_FACTORY_CLASSNAME);
            se.setSerializer(Constants.SERIALIZER_FACTORY_CLASSNAME);
        }
        for (SchemaElementType se : createdTypes[1].getSchemaElement()) {
            // TODO is this a valid assumption for all the types? If not, need a
            // mapping file
            se.setClassName(se.getType());
            se.setDeserializer(Constants.DESERIALIZER_FACTORY_CLASSNAME);
            se.setSerializer(Constants.SERIALIZER_FACTORY_CLASSNAME);
        }

        return createdTypes;

    }


    /**
     * HACK: This should be done more elegantly but is limited by introduce as
     * described in this RFE:
     * http://gforge.nci.nih.gov/tracker/?func=detail&group_id
     * =25&aid=21615&atid=2252
     * 
     * @param schemaDir
     *            The provided schema directory of the service
     * @return The directory of the service
     */
    protected File getServiceDirectory(File schemaDir) {
        return new File(schemaDir + "/../..");
    }


    protected File copySchemaFromExtensionDir(String schemaName, File outputDir) throws IOException {
        File schemaFile = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + Constants.EXTENSION_NAME
            + File.separator + "schema" + File.separator + schemaName);
        LOG.debug("Copying schema from " + schemaFile.getAbsolutePath());
        File outputFile = new File(outputDir + File.separator + schemaName);
        LOG.debug("Saving schema to " + outputFile.getAbsolutePath());
        Utils.copyFile(schemaFile, outputFile);
        return outputFile;
    }


    protected void copyLibraries(File serviceDirectory) throws Exception {
        // from the lib directory
        File libDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + Constants.EXTENSION_NAME
            + File.separator + "lib");
        File[] libs = libDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                // take any jar from our extension's directory
                return (name.endsWith(".jar"));
            }
        });

        if (libs != null) {
            File[] copiedLibs = new File[libs.length];
            for (int i = 0; i < libs.length; i++) {
                File outFile = new File(serviceDirectory + File.separator + "lib" + File.separator + libs[i].getName());
                copiedLibs[i] = outFile;
                Utils.copyFile(libs[i], outFile);
            }
            modifyClasspathFile(copiedLibs, serviceDirectory);
        }

    }


    protected void modifyClasspathFile(File[] libs, File serviceDirectory) throws Exception {
        File classpathFile = new File(serviceDirectory, ".classpath");
        ExtensionUtilities.syncEclipseClasspath(classpathFile, libs);
    }

}
