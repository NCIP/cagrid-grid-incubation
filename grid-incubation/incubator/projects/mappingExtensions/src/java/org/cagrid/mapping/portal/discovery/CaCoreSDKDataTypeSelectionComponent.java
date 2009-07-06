package org.cagrid.mapping.portal.discovery;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.datatype.sdkmapping4.SDK40Processor;
import org.cagrid.datatype.sdkmapping41.SDK41Processor;
import org.cagrid.mapping.portal.CaCoreSDKBrowserPanel;

public class CaCoreSDKDataTypeSelectionComponent extends NamespaceTypeDiscoveryComponent {
    public static final String GLOBUS_LOCATION_ENV = "GLOBUS_LOCATION";

	private static final Log LOG = LogFactory.getLog(CaCoreSDKDataTypeSelectionComponent.class);

	private CaCoreSDKBrowserPanel mappingPanel = null;

	public static final String DEPLOY_PROPERTIES_FILENAME = "deploy.properties";

	public CaCoreSDKDataTypeSelectionComponent(DiscoveryExtensionDescriptionType desc, NamespacesType currentNamespaces) {
		super(desc, currentNamespaces);
		initialize();
	}

	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.weighty = 1.0D;
		gridBagConstraints.gridy = 1;
		this.setLayout(new GridBagLayout());
		this.add(getMappingPanel());
	}

	private CaCoreSDKBrowserPanel getMappingPanel() {
		if (this.mappingPanel == null) {
			this.mappingPanel = new CaCoreSDKBrowserPanel();
		}
		return this.mappingPanel;
	}

	@Override
	public NamespaceType[] createNamespaceType(File schemaDestinationDir, NamespaceReplacementPolicy replacementPolicy,
			MultiEventProgressBar progress) {

		ServiceInformation serviceInformation = null;
		try {
			serviceInformation = loadServiceInformation(schemaDestinationDir);
		} catch (Exception e) {
			addError("Unable to load the service");
			LOG.error(e);
			return null;
		}

		File caCoreSDKDir = new File(getMappingPanel().getSdkDir());
		Properties projectProperties;
		try {
			projectProperties = loadCaCoreProjectProperties(caCoreSDKDir);
		} catch (IOException e) {
			addError("Unable to load the caCORE SDK deploy.properties file");
			LOG.error(e);
			return null;
		}
		
		File projectConfigFile = createProjectConfigFile(projectProperties, caCoreSDKDir, serviceInformation);
		
		NamespaceType[] namespaceTypes;
		try {
			namespaceTypes = getNamespacesFromConfigFile(projectConfigFile, schemaDestinationDir);
		} catch (Exception e) {
			addError("Unable to map the schemas to namespaces");
			LOG.error(e);
			return null;
		}
		
		try {
			copyCaCoreJarToService(schemaDestinationDir);
			copyCaCoreRemoteLibJarsToService(schemaDestinationDir);
		} catch (Exception e) {
			addError("Unable to copy the caCORE SDK jar");
			return null;
		}
		
		try {
			if (CaCoreSDKBrowserPanel.CA_CORE_SDK_V40.equals(getMappingPanel().getSdkVersion())) {
				SDK40Processor sdkProcessor = new SDK40Processor(new File(getMappingPanel().getSdkDir()));
				sdkProcessor.updateWsdd(serviceInformation);
			} else {
				SDK41Processor sdkProcessor = new SDK41Processor(new File(getMappingPanel().getSdkDir()));
				sdkProcessor.updateWsdd(serviceInformation);
			}
		} catch (Exception e) {
			addError("Unable to edit the WSDDs.");
			LOG.error(e);
			return null;
		}


		return namespaceTypes;
	}

	private ServiceInformation loadServiceInformation(File schemaDestinationDir) throws Exception {
		ServiceInformation serviceInformation = null;
		serviceInformation = new ServiceInformation(new File(schemaDestinationDir + "/../.."));
		return serviceInformation;
	}

	private Properties loadCaCoreProjectProperties(File caCoreSDKDir) throws IOException {
		File deployPropertiesFile = new File(caCoreSDKDir, "conf" + File.separator + DEPLOY_PROPERTIES_FILENAME);

		Properties deployProperties = new Properties();
		FileInputStream fis;
		fis = new FileInputStream(deployPropertiesFile);
		deployProperties.load(fis);
		fis.close();
		return deployProperties;

	}
	
	private File createProjectConfigFile(Properties projectProperties, File caCoreSDKDir, ServiceInformation serviceInformation) {
		File libOutDir = new File(serviceInformation.getBaseDirectory(), "lib");

		String projectName = projectProperties.getProperty("PROJECT_NAME");
		File remoteClientDir = new File(caCoreSDKDir, "output" + File.separator + projectName + File.separator
				+ "package" + File.separator + "remote-client");
		LOG.debug("Creating a jar to contain the remote configuration of the caCORE SDK system");
		File remoteConfigDir = new File(remoteClientDir, "conf");
		String configJarName = projectName + "-config.jar";
		File configJar = new File(libOutDir, configJarName);
		try {
			JarUtilities.jarDirectory(remoteConfigDir, configJar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return configJar;
	}
	
	private NamespaceType[] getNamespacesFromConfigFile(File projectConfigFile, File schemaDestinationDir) throws Exception {
        JarFile configJar = new JarFile(projectConfigFile);
        
        // copy all the XSDs into a temporary location.  Must do this so 
        // schemas which reference each other can be resolved
        File tempXsdDir = File.createTempFile("TempCaCoreXSDs", "dir");
        tempXsdDir.delete();
        tempXsdDir.mkdirs();
        Enumeration<JarEntry> entries = configJar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".xsd")) {
                StringBuffer schemaText = JarUtilities.getFileContents(configJar, entry.getName());
                File schemaFile = new File(tempXsdDir, new File(entry.getName()).getName());
                Utils.stringBufferToFile(schemaText, schemaFile.getAbsolutePath());
            }
        }
        configJar.close();

		List<NamespaceType> namespaceTypes = new ArrayList<NamespaceType>();

        // iterate the schemas and try to map them to domain packages
        for (File xsdFile : tempXsdDir.listFiles()) {

    		NamespaceType nsType = CommonTools.createNamespaceType(xsdFile.getAbsolutePath(), schemaDestinationDir);
    		SchemaElementType[] schemaElements = nsType.getSchemaElement();
    		for (SchemaElementType schemaElement : schemaElements) {
    			if (CaCoreSDKBrowserPanel.CA_CORE_SDK_V40.equals(getMappingPanel().getSdkVersion())) {
    				schemaElement.setSerializer(SDK40Processor.SERIALIZER);
    				schemaElement.setDeserializer(SDK40Processor.DESERIALIZER);
    			} else {
    				schemaElement.setSerializer(SDK41Processor.SERIALIZER);
    				schemaElement.setDeserializer(SDK41Processor.DESERIALIZER);
    			}
    			schemaElement.setClassName(schemaElement.getType());
    		}
    		nsType.setGenerateStubs(false);
    		nsType.setLocation(xsdFile.getName());
    		namespaceTypes.add(nsType);
    		
    		File destinationSchemaFile = new File(schemaDestinationDir, xsdFile.getName());
    		FileUtils.copyFile(xsdFile, destinationSchemaFile);

        }
        Utils.deleteDir(tempXsdDir);
		
		NamespaceType[] nsTypeArray = new NamespaceType[namespaceTypes.size()];
		namespaceTypes.toArray(nsTypeArray);
		
		return nsTypeArray;
	}
	
	private void copyCaCoreJarToService(File schemaDestinationDir) throws IOException {

		File selectedDir = new File(getMappingPanel().getSdkDir()+"/output");

		File[] outputContents = selectedDir.listFiles(new FileFilter() {
			public boolean accept(File path) {
				return path.isDirectory() && !path.getName().startsWith(".");
			}
		});

		File applicationOutDir = outputContents[0];

		// find the beans jar
		File beansJar = new File(applicationOutDir, "package/remote-client/lib/" + applicationOutDir.getName() + "-beans.jar");
		if (!beansJar.canRead()) {
			String errorMsg = "Unable to locate file: " + beansJar.getAbsolutePath();
			addError(errorMsg);
			throw new FileNotFoundException(errorMsg);			
		}
		
		File destJar = new File(schemaDestinationDir + "/../../lib", beansJar.getName());
		FileUtils.copyFile(beansJar, destJar);

	}
	
	private void copyCaCoreRemoteLibJarsToService(File schemaDestinationDir) throws IOException {
        File globusLocation = new File(System.getenv(GLOBUS_LOCATION_ENV));
        File globusLib = new File(globusLocation, "lib");
        File[] globusJars = globusLib.listFiles(new FileFilters.JarFileFilter());
        Set<String> globusJarNames = new HashSet<String>();
        for (File jar : globusJars) {
            globusJarNames.add(jar.getName());
        }
        
		File selectedDir = new File(getMappingPanel().getSdkDir()+"/output");

		File[] outputContents = selectedDir.listFiles(new FileFilter() {
			public boolean accept(File path) {
				return path.isDirectory() && !path.getName().startsWith(".");
			}
		});

		File applicationOutDir = outputContents[0];
        
        // copy in libraries from the remote lib dir that DON'T collide with Globus's
        LOG.debug("Copying libraries from remote client directory");
        File[] remoteLibs = new File(applicationOutDir, "package/remote-client/lib").listFiles(new FileFilters.JarFileFilter());
        for (File lib : remoteLibs) {
            String libName = lib.getName();
            if (!globusJarNames.contains(libName)) {
                LOG.debug(libName + " copied to the service");
                File libOutput = new File(schemaDestinationDir + "/../../lib", libName);
                Utils.copyFile(lib, libOutput);
            } else {
                LOG.debug(libName + " appears to conflict with a Globus library," +
                        " and was NOT copied to the service");
            }
        }

	}


}
