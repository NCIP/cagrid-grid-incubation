package org.cagrid.cql2.preview.config;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.xmi.XMIParser;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.cagrid.sdkquery4.encoding.SDK40DeserializerFactory;
import gov.nih.nci.cagrid.sdkquery4.encoding.SDK40SerializerFactory;
import gov.nih.nci.cagrid.sdkquery4.processor.DomainTypesInformationUtil;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryEvent;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryEventListener;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryMapper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;

import javax.xml.namespace.QName;

import org.apache.axis.deployment.wsdd.WSDDTypeMapping;
import org.apache.axis.encoding.SerializationContext;
import org.cagrid.cql2.preview.tools.QueryRunner;
import org.jdom.Document;
import org.jdom.Element;

/** 
 *  ConfigPackageBuilder
 *  Utility to build a CQL 2 Preview configuration package
 * 
 * @author David Ervin
 * 
 * @created Apr 14, 2008 10:04:53 AM
 * @version $Id: ConfigPackageBuilder.java,v 1.9 2008/04/22 18:51:46 dervin Exp $ 
 */
public class ConfigPackageBuilder {
    // the SDK's ApplicationService spring configuration file
    public static final String APPLICATION_SPRING_CONFIG_FILENAME = "application-config-client.xml";
    // the default SDK 4.0 proxy helper
    public static final String SDK_PROXY_HELPER = "gov.nih.nci.system.client.proxy.ProxyHelperImpl";
    // my POJO returning proxy helper
    public static final String POJO_PROXY_HELPER = "gov.nih.nci.cagrid.sdkquery4.processor.PojoProxyHelperImpl";
    
    public static final String CASTOR_MARSHALLING_MAPPING_FILE = "xml-mapping.xml";
    public static final String CASTOR_UNMARSHALLING_MAPPING_FILE = "unmarshaller-xml-mapping.xml";
    
    public static final String EXT_LIB_DIR = "ext" + File.separator + "libs" + File.separator + "jars";
    
    public static final String ENV_GLOBUS_LOCATION = "GLOBUS_LOCATION";
    
    public static final QName MAPPING_QNAME = new QName("http://gov.nih.nci.cagrid.data", "ClassMappings");
    
    public static final String WSDD_NAMESPACE = "http://xml.apache.org/axis/wsdd/";
    public static final String JAVA_PROVIDER_NAMESPACE = "http://xml.apache.org/axis/wsdd/providers/java";
    
    // minimize the set of libs required
    public static final String[] REQUIRED_SDK_LIBS = {
        "asm.jar", "castor-1.0.2.jar", "hibernate3.jar", "spring.jar",
        "cglib-2.1.3.jar", "sdk-client-framework.jar", "antlr-2.7.6.jar",
        "acegi-security-1.0.4.jar"
    };
    public static final String[] REQUIRED_CAGRID_LIBS = {
        "caGrid-CQL-cql.2.0.jar", "caGrid-core.jar",
        "caGrid-data-common.jar", "caGrid-data-tools.jar",
        "caGrid-data-stubs.jar", "caGrid-data-utils.jar", 
        "caGrid-metadata-data.jar", "caGrid-metadata-common.jar", 
        "caGrid-sdkQuery4-processor.jar", "caGrid-sdkQuery4-beans.jar", 
        "cglib-nodep-2.1_3.jar"
    };
    
    private String sdkOutputDir;
    private String sdkApplicationName;
    private String xmiFilename;
    private XmiFileType xmiType;
    private String projectName;
    private String projectVersion;
    private String applicationHostName;
    private String applicationNetworkPort;
    
    public ConfigPackageBuilder() {
        
    }
    
    
    public String getApplicationHostName() {
        return applicationHostName;
    }


    public void setApplicationHostName(String applicationHostName) {
        this.applicationHostName = applicationHostName;
    }


    public String getApplicationNetworkPort() {
        return applicationNetworkPort;
    }


    public void setApplicationNetworkPort(String applicationNetworkPort) {
        this.applicationNetworkPort = applicationNetworkPort;
    }


    public String getProjectName() {
        return projectName;
    }


    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public String getProjectVersion() {
        return projectVersion;
    }


    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }


    public String getSdkApplicationName() {
        return sdkApplicationName;
    }


    public void setSdkApplicationName(String sdkApplicationName) {
        this.sdkApplicationName = sdkApplicationName;
    }


    public String getSdkOutputDir() {
        return sdkOutputDir;
    }


    public void setSdkOutputDir(String sdkOutputDir) {
        this.sdkOutputDir = sdkOutputDir;
    }


    public String getXmiFilename() {
        return xmiFilename;
    }


    public void setXmiFilename(String xmiFilename) {
        this.xmiFilename = xmiFilename;
    }


    public XmiFileType getXmiType() {
        return xmiType;
    }


    public void setXmiType(XmiFileType xmiType) {
        this.xmiType = xmiType;
    }


    public void buildConfiguration(File zipOutput) throws Exception {
        File tempDir = File.createTempFile("CQL2Preview_Config", "temp");
        tempDir.delete();
        tempDir.mkdir();

        File tempLibDir = new File(tempDir, "lib");
        tempLibDir.mkdir();
        
        File tempConfigDir = new File(tempDir, "config");
        tempConfigDir.mkdir();
        
        // locate required files from the SDK directory
        File outputDir = new File(sdkOutputDir);
        File[] outputContents = outputDir.listFiles();
        File applicationOutDir = outputContents[0]; // should be named the same as the application
        if (!applicationOutDir.getName().equals(sdkApplicationName)) {
            throw new Exception("Application name did not match output directory contents");
        }
        File packageDir = new File(applicationOutDir, "package");
        // find remote and local client directories
        File remoteClientDir = new File(packageDir, "remote-client");;
        File remoteClientLibDir = new File(remoteClientDir, "lib");
        File remoteClientConfDir = new File(remoteClientDir, "conf");
        
        // find the beans jar and copy it in
        File beansJar = new File(remoteClientLibDir, applicationOutDir.getName() + "-beans.jar");
        Utils.copyFile(beansJar, new File(tempLibDir, beansJar.getName()));
        // copy ONLY REQUIRED LIBRARIES from SDK to the package
        final Set<String> sdkRequired = new HashSet<String>();
        Collections.addAll(sdkRequired, REQUIRED_SDK_LIBS);
        FileFilter sdkLibFilter = new FileFilter() {
            public boolean accept(File path) {
                return sdkRequired.contains(path.getName());
            }
        };
        File[] sdkLibs = remoteClientLibDir.listFiles(sdkLibFilter);
        for (File sdkLib : sdkLibs) {
            Utils.copyFile(sdkLib, new File(tempLibDir, sdkLib.getName()));
        }
        
        // turn the remote config dir into a jar file
        File configJar = new File(tempLibDir, sdkApplicationName + "-config.jar");
        JarUtilities.jarDirectory(remoteClientConfDir, configJar);
        
        // make edits to the application-config-client.xml spring config in the new jar
        // extract the configuration
        StringBuffer configContents = JarUtilities.getFileContents(
            new JarFile(configJar), APPLICATION_SPRING_CONFIG_FILENAME);
        
        // replace the default bean proxy class with mine
        System.out.println("Replacing references to bean proxy class");
        int start = -1;
        while ((start = configContents.indexOf(SDK_PROXY_HELPER)) != -1) {
            configContents.replace(start, start + SDK_PROXY_HELPER.length(), POJO_PROXY_HELPER);
        }
        
        // add the edited config to the config jar file
        System.out.println("Inserting edited config in jar");
        byte[] configData = configContents.toString().getBytes();
        JarUtilities.insertEntry(configJar, APPLICATION_SPRING_CONFIG_FILENAME, configData);

        // grab the castor marshalling and unmarshalling xml mapping files
        // from the config dir and copy them into the service's package structure
        File marshallingMappingFile = new File(remoteClientConfDir, CASTOR_MARSHALLING_MAPPING_FILE);
        File unmarshallingMappingFile = new File(remoteClientConfDir, CASTOR_UNMARSHALLING_MAPPING_FILE);
        Utils.copyFile(marshallingMappingFile, new File(tempConfigDir, marshallingMappingFile.getName()));
        Utils.copyFile(unmarshallingMappingFile, new File(tempConfigDir, unmarshallingMappingFile.getName()));
        
        // domain model
        XMIParser xmiParser = new XMIParser(projectName, projectVersion);
        File xmiFile = new File(xmiFilename);
        DomainModel domainModel = xmiParser.parse(xmiFile, xmiType);
        File domainModelFile = new File(tempConfigDir, sdkApplicationName + "_domainModel.xml");
        FileWriter domainModelWriter = new FileWriter(domainModelFile);
        MetadataUtils.serializeDomainModel(domainModel, domainModelWriter);
        domainModelWriter.flush();
        domainModelWriter.close();
        
        // domain types info
        BeanTypeDiscoveryMapper beanTypesMapper = new BeanTypeDiscoveryMapper(beansJar, domainModel);
        beanTypesMapper.addBeanTypeDiscoveryEventListener(new BeanTypeDiscoveryEventListener() {
            public void typeDiscoveryBegins(BeanTypeDiscoveryEvent e) {
                System.out.println("Processing bean " + e.getBeanClassname() 
                    + " (" + e.getCurrentBean() + " of " + e.getTotalBeans() + ")");
            }
        });
        DomainTypesInformation domainTypesInfo = beanTypesMapper.discoverTypesInformation();
        File typesInfoFile = new File(tempConfigDir, sdkApplicationName + "_typesInformation.xml");
        FileWriter typesInfoWriter = new FileWriter(typesInfoFile);
        DomainTypesInformationUtil.serializeDomainTypesInformation(domainTypesInfo, typesInfoWriter);
        typesInfoWriter.flush();
        typesInfoWriter.close();
        
        // class to QName mappings
        Mappings classToQnameMappings = new Mappings();
        classToQnameMappings.setMapping(new ClassToQname[0]);
        File[] xsdFiles = remoteClientConfDir.listFiles(new java.io.FileFilter() {
            public boolean accept(File path) {
                return path.getName().toLowerCase().endsWith(".xsd");
            }
        });
        // determine the target namespace of each xsd
        Map<String, String> packageNamespaces = new HashMap<String, String>();
        Map<String, List<String>> xsdElements = new HashMap<String, List<String>>();
        for (File xsd : xsdFiles) {
            String fullName = xsd.getName();
            String cleanedName = fullName.substring(0, fullName.length() - 4);
            
            Document schemaDoc = XMLUtilities.fileNameToDocument(xsd.getAbsolutePath());
            String rawNamespace = schemaDoc.getRootElement().getAttributeValue("targetNamespace");
            packageNamespaces.put(cleanedName, rawNamespace);
            
            List elementTypes = schemaDoc.getRootElement()
                .getChildren("element", schemaDoc.getRootElement().getNamespace());
            List<String> elementNames = new ArrayList<String>();
            Iterator elementIterator = elementTypes.iterator();
            while (elementIterator.hasNext()) {
                Element element = (Element) elementIterator.next();
                String name = element.getAttributeValue("name");
                elementNames.add(name);
            }
            xsdElements.put(rawNamespace, elementNames);   
        }
        // walk classes and assign names
        List<ClassToQname> mappings = new LinkedList<ClassToQname>();
        for (UMLClass clazz: domainModel.getExposedUMLClassCollection().getUMLClass()) {
            String associatedNamespace = packageNamespaces.get(clazz.getPackageName());
            List<String> elementNames = xsdElements.get(associatedNamespace);
            if (elementNames.contains(clazz.getClassName())) {
                ClassToQname cq = new ClassToQname(
                    clazz.getPackageName() + "." + clazz.getClassName(), 
                    new QName(associatedNamespace, clazz.getClassName()).toString());
                mappings.add(cq);
            } else {
                throw new Exception("Class " + clazz.getPackageName() + "." + clazz.getClassName() + 
                    " could not be associated with an element in the namespace " + associatedNamespace);
            }
        }
        ClassToQname[] cqArray = new ClassToQname[mappings.size()];
        mappings.toArray(cqArray);
        classToQnameMappings.setMapping(cqArray);
        
        // serialize class to qname
        File classToQnameFile = new File(tempConfigDir, "classToQname.xml");
        FileWriter c2qWriter = new FileWriter(classToQnameFile);
        Utils.serializeObject(classToQnameMappings, MAPPING_QNAME, c2qWriter);
        c2qWriter.flush();
        c2qWriter.close();
        
        // create a client-config.wsdd with the right type mappings for the SDK objects
        // start with the Globus provided wsdd
        String globusDir = System.getenv(ENV_GLOBUS_LOCATION);
        File globusClientConfig = new File(globusDir, "client-config.wsdd");
        // copy it to the temp config dir
        File clientConfigWsdd = new File(tempConfigDir, globusClientConfig.getName());
        Utils.copyFile(globusClientConfig, clientConfigWsdd);
        // set the type mappings / serialization properties
        addTypeMappings(clientConfigWsdd, classToQnameMappings);
        
        // create the properties file for the query API front-end
        Properties props = new Properties();
        String url = applicationHostName + ":" + applicationNetworkPort +
            "/" + sdkApplicationName;
        props.setProperty(QueryRunner.SDK_URL_PROPERTY, url);
        props.setProperty(QueryRunner.DOMAIN_TYPES_PROPERTY, tempConfigDir.getName() + "/" + typesInfoFile.getName());
        props.setProperty(QueryRunner.DOMAIN_MODEL_PROPERTY, tempConfigDir.getName() + "/" + domainModelFile.getName());
        props.setProperty(QueryRunner.CLASS_TO_QNAME_PROPERTY, tempConfigDir.getName() + "/" + classToQnameFile.getName());
        props.setProperty(QueryRunner.CLIENT_CONFIG_WSDD_PROPERTY, tempConfigDir.getName() + "/" + clientConfigWsdd.getName());
        
        File propertiesFile = new File(tempConfigDir, "configuration.properties");
        FileOutputStream propertiesOutput = new FileOutputStream(propertiesFile);
        props.store(propertiesOutput, "Configuration for CQL 2.0 Technology Preview");
        propertiesOutput.flush();
        propertiesOutput.close();
        
        // add libraries from the build dir and lib dir to the package
        FileFilter jarFilter = new FileFilter() {
            public boolean accept(File path) {
                return path.getName().toLowerCase().endsWith(".jar");
            }
        };
        File[] buildLibs = new File("build/lib").listFiles(jarFilter);
        for (File lib : buildLibs) {
            Utils.copyFile(lib, new File(tempLibDir, lib.getName()));
        }
        File[] devLibs = new File("devlib").listFiles(jarFilter);
        for (File lib : devLibs) {
            Utils.copyFile(lib, new File(tempLibDir, lib.getName()));
        }
        final Set<String> caGridLibs = new HashSet<String>();
        Collections.addAll(caGridLibs, REQUIRED_CAGRID_LIBS);
        FileFilter caGridLibFilter = new FileFilter() {
            public boolean accept(File path) {
                return caGridLibs.contains(path.getName());
            }
        };
        File[] extLibs = new File(EXT_LIB_DIR).listFiles(caGridLibFilter);
        for (File lib : extLibs) {
            Utils.copyFile(lib, new File(tempLibDir, lib.getName()));
        }        
        
        // build an eclipse .classpath
        File eclipseClasspath = new File(tempDir, ".classpath");
        buildClasspathFile(eclipseClasspath, tempDir);
        
        // build an Ant <path> which can be used to build against the package
        File antPath = new File(tempDir, "antClasspath.xml");
        buildAntPath(antPath, tempDir);
        
        // zip up the resulting temp dir, plus libraries from the tech preview tools
        ZipUtilities.zipDirectory(tempDir, zipOutput);
        
        // throw away the temp dir
        Utils.deleteDir(tempDir);
    }
    
    
    private void buildClasspathFile(File eclipseClasspath, File baseDir) throws IOException {
        Element baseElement = new Element("classpath");
        
        // list all libraries in the package dir
        final List<File> localLibs = Utils.recursiveListFiles(baseDir, new FileFilter() {
            public boolean accept(File path) {
                return path.getName().toLowerCase().endsWith(".jar");
            }
        });
        // add each library to the .classpath
        for (File lib : localLibs) {
            String path = Utils.getRelativePath(baseDir, lib);
            Element cpElement = new Element("classpathentry");
            cpElement.setAttribute("kind", "lib");
            cpElement.setAttribute("path", path);
            baseElement.addContent(cpElement);
        }
        
        // list libraries in Globus
        File globusDir = new File(System.getenv(ENV_GLOBUS_LOCATION));
        File globusLibDir = new File(globusDir, "lib");
        File[] globusLibs = globusLibDir.listFiles(new FileFilter() {
            public boolean accept(File path) {
                String name = path.getName();
                return (name.endsWith(".jar") && !localLibs.contains(name));
            }
        });
        // add globus libraries to the classpath
        for (File lib : globusLibs) {
            String path = lib.getAbsolutePath();
            Element cpElement = new Element("classpathentry");
            cpElement.setAttribute("kind", "lib");
            cpElement.setAttribute("path", path);
            baseElement.addContent(cpElement);
        }
        
        // add an entry for the Eclipse default JRE libraries
        // <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
        Element cpElement = new Element("classpathentry");
        cpElement.setAttribute("kind", "con");
        cpElement.setAttribute("path", "org.eclipse.jdt.launching.JRE_CONTAINER");
        baseElement.addContent(cpElement);
        
        String xml = null;
        try {
            xml = XMLUtilities.formatXML(XMLUtilities.elementToString(baseElement));
        } catch (Exception ex) {
            throw new IOException("Error formatting XML: " + ex.getMessage());
        }
        
        FileWriter writer = new FileWriter(eclipseClasspath);
        writer.write(xml);
        writer.flush();
        writer.close();
    }
    
    
    private void buildAntPath(File antPathTemp, File baseDir) throws IOException {
        Element baseElement = new Element("path");
        // local libs
        Element localLibFileset = new Element("fileset");
        localLibFileset.setAttribute("dir", "lib");
        Element allJarsInclude = new Element("include");
        allJarsInclude.setAttribute("name", "*.jar");
        localLibFileset.addContent(allJarsInclude);
        baseElement.addContent(localLibFileset);
        
        // globus libs that aren't in local libs
        final List<File> localLibs = Utils.recursiveListFiles(baseDir, new FileFilter() {
            public boolean accept(File path) {
                return path.getName().toLowerCase().endsWith(".jar");
            }
        });
        File globusDir = new File(System.getenv(ENV_GLOBUS_LOCATION));
        File globusLibDir = new File(globusDir, "lib");
        File[] globusLibs = globusLibDir.listFiles(new FileFilter() {
            public boolean accept(File path) {
                String name = path.getName();
                return (name.endsWith(".jar") && !localLibs.contains(name));
            }
        });
        Element globusLibFileset = new Element("fileset");
        globusLibFileset.setAttribute("dir", globusLibDir.getAbsolutePath());
        for (File lib : globusLibs) {
            String jarName = lib.getName();
            Element include = new Element("include");
            include.setAttribute("name", jarName);
            globusLibFileset.addContent(include);
        }
        baseElement.addContent(globusLibFileset);
        
        String xml = null;
        try {
            xml = XMLUtilities.formatXML(XMLUtilities.elementToString(baseElement));
        } catch (Exception ex) {
            throw new IOException("Error formatting XML: " + ex.getMessage());
        }
        
        FileWriter writer = new FileWriter(antPathTemp);
        writer.write(xml);
        writer.flush();
        writer.close();
    }
    
    
    private void addTypeMappings(File wsddFile, Mappings classMappings) throws Exception {
        /*
         * <ns3:typeMapping xmlns:ns3="http://xml.apache.org/axis/wsdd/" 
         * xmlns:ns1="http://xml.apache.org/axis/wsdd/providers/java" 
         * xmlns:ns2="gme://caCORE.caCORE/4.0/gov.nih.nci.cacoresdk.domain.inheritance.multiplechild" 
         * encodingStyle="" 
         * serializer="gov.nih.nci.cagrid.sdkquery4.encoding.SDK40SerializerFactory" 
         * deserializer="gov.nih.nci.cagrid.sdkquery4.encoding.SDK40DeserializerFactory" 
         * type="ns1:gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.UndergraduateStudent" 
         * qname="ns2:UndergraduateStudent" />
         */
        Element wsddRoot = XMLUtilities.fileNameToDocument(wsddFile.getAbsolutePath()).getRootElement();
        for (ClassToQname cq : classMappings.getMapping()) {
            WSDDTypeMapping map = new WSDDTypeMapping();
            map.setSerializer(SDK40SerializerFactory.class.getName());
            map.setDeserializer(SDK40DeserializerFactory.class.getName());
            map.setEncodingStyle("");
            map.setLanguageSpecificType(cq.getClassName());
            map.setQName(QName.valueOf(cq.getQname()));
            String mapString = mappingToString(map);
            Element mappingElement = XMLUtilities.stringToDocument(mapString).detachRootElement();
            wsddRoot.addContent(mappingElement);
        }
        String wsddOutput = XMLUtilities.formatXML(XMLUtilities.elementToString(wsddRoot));
        FileWriter writer = new FileWriter(wsddFile);
        writer.write(wsddOutput);
        writer.flush();
        writer.close();
    }
    
    
    private String mappingToString(WSDDTypeMapping mapping) throws Exception {
        StringWriter writer = new StringWriter();
        SerializationContext context = new SerializationContext(writer, null);
        context.setPretty(true);
        context.setSendDecl(false);
        mapping.writeToContext(context);
        writer.close();

        return writer.getBuffer().toString();
    }
}
