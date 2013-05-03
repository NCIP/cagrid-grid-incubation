/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.datatype.sdkmapping4;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.utilities.WsddUtil;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.datatype.sdkmapping4.encoding.SDK40EncodingUtils;
import org.cagrid.grape.utils.CompositeErrorDialog;

public class SDK40Processor {
    private File sdkDirectory = null;
    private Properties deployProperties = null;
   
    public static final String OLD_CASTOR_JAR_NAME = "castor-0.9.9.jar";
    
    public static final String SERIALIZER = "org.cagrid.datatype.sdkmapping4.encoding.SDK40SerializerFactory";
    public static final String DESERIALIZER = "org.cagrid.datatype.sdkmapping4.encoding.SDK40DeserializerFactory";
    
    private static final Log LOG = LogFactory.getLog(SDK40Processor.class);
    
    public SDK40Processor(File sdkDirectory) {
    	this.sdkDirectory = sdkDirectory;
    }

    public void updateWsdd(ServiceInformation serviceInfo) throws Exception {
        deleteOldCastorJar(serviceInfo);
        createMarshallingFiles(serviceInfo);
        editWsddForCastorMappings(serviceInfo);
    }
      
    private void deleteOldCastorJar(ServiceInformation info) {
        File castorLib = new File(info.getBaseDirectory().getAbsolutePath() 
            + File.separator + "lib" + File.separator + OLD_CASTOR_JAR_NAME);
        LOG.debug("Deleting old castor jar (" + castorLib.getAbsolutePath() + ")");
        castorLib.delete();
    }
    
    private void createMarshallingFiles(ServiceInformation serviceInfo) {
        try {
            String projectName = getDeployPropertiesFromSdkDir().getProperty(
                    "PROJECT_NAME");
            File remoteClientDir = new File(sdkDirectory, 
                    "output" + File.separator + projectName + File.separator + 
                    "package" + File.separator + "remote-client");
            File remoteConfigDir = new File(remoteClientDir, "conf");

                
            LOG.debug("Extracting castor marshalling and unmarshalling files");
            StringBuffer marshallingMappingFile = Utils.fileToStringBuffer(
                new File(remoteConfigDir, CastorMappingUtil.CASTOR_MARSHALLING_MAPPING_FILE));
            StringBuffer unmarshallingMappingFile = Utils.fileToStringBuffer(
                new File(remoteConfigDir, CastorMappingUtil.CASTOR_UNMARSHALLING_MAPPING_FILE));
            // copy the mapping files to the service's source dir + base package name
            String marshallOut = CastorMappingUtil.getMarshallingCastorMappingFileName(serviceInfo);
            String unmarshallOut = CastorMappingUtil.getUnmarshallingCastorMappingFileName(serviceInfo);
            Utils.stringBufferToFile(marshallingMappingFile, marshallOut);
            Utils.stringBufferToFile(unmarshallingMappingFile, unmarshallOut);
        } catch (IOException ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error extracting castor mapping files", ex.getMessage(), ex);
        }

    }
    
    
    private void editWsddForCastorMappings(ServiceInformation info) throws Exception {
        String mainServiceName = info.getIntroduceServiceProperties().getProperty(
            IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
        ServiceType mainService = CommonTools.getService(info.getServices(), mainServiceName);
        String servicePackageName = mainService.getPackageName();
        String packageDir = servicePackageName.replace('.', File.separatorChar);
        // find the client source directory, where the client-config will be located
        File clientConfigFile = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "src"
            + File.separator + packageDir + File.separator + "client" + File.separator + "client-config.wsdd");
        if (!clientConfigFile.exists()) {
            throw new CodegenExtensionException("Client config file " + clientConfigFile.getAbsolutePath()
                + " not found!");
        }
        // fine the server-config.wsdd, located in the service's root directory
        File serverConfigFile = new File(info.getBaseDirectory().getAbsolutePath() + File.separator
            + "server-config.wsdd");
        if (!serverConfigFile.exists()) {
            throw new CodegenExtensionException("Server config file " 
                + serverConfigFile.getAbsolutePath() + " not found!");
        }
        
        // edit the marshalling castor mapping to avoid serializing associations
        String marshallingXmlText = Utils.fileToStringBuffer(
            new File(CastorMappingUtil.getMarshallingCastorMappingFileName(info))).toString();
        String editedMarshallingText = CastorMappingUtil.removeAssociationMappings(marshallingXmlText);
        String editedMarshallingFileName = CastorMappingUtil.getEditedMarshallingCastorMappingFileName(info);
        Utils.stringBufferToFile(new StringBuffer(editedMarshallingText), editedMarshallingFileName);
        
        // edit the UNmarshalling castor mapping to avoid DEserializing associations
        String unmarshallingXmlText = Utils.fileToStringBuffer(
            new File(CastorMappingUtil.getUnmarshallingCastorMappingFileName(info))).toString();
        String editedUnmarshallingText = CastorMappingUtil.removeAssociationMappings(unmarshallingXmlText);
        String editedUnmarshallingFileName = CastorMappingUtil.getEditedUnmarshallingCastorMappingFileName(info);
        Utils.stringBufferToFile(new StringBuffer(editedUnmarshallingText), editedUnmarshallingFileName);
        
        // set properties in the client to use the edited marshaller
        WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
            SDK40EncodingUtils.CASTOR_MARSHALLER_PROPERTY, 
            CastorMappingUtil.getEditedMarshallingCastorMappingName(info));
        // and the edited unmarshaller
        WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
            SDK40EncodingUtils.CASTOR_UNMARSHALLER_PROPERTY, 
            CastorMappingUtil.getEditedUnmarshallingCastorMappingName(info));
        
        // set properties in the server to use the edited marshaller
        WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(),
            info.getServices().getService(0).getName(),
            SDK40EncodingUtils.CASTOR_MARSHALLER_PROPERTY,
            CastorMappingUtil.getEditedMarshallingCastorMappingName(info));
        // and the edited unmarshaller
        WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(),
            info.getServices().getService(0).getName(),
            SDK40EncodingUtils.CASTOR_UNMARSHALLER_PROPERTY,
            CastorMappingUtil.getEditedUnmarshallingCastorMappingName(info));
    }
    
    private Properties getDeployPropertiesFromSdkDir() throws IOException {
        if (this.deployProperties == null) {
            LOG.debug("Loading deploy.properties file");
            File propertiesFile = getDeployPropertiesFile();
            deployProperties = new Properties();
            FileInputStream fis = new FileInputStream(propertiesFile);
            deployProperties.load(fis);
            fis.close();
        }
        return deployProperties;
    }
    
    private File getDeployPropertiesFile() {
        if (sdkDirectory != null) {
            File propertiesFile = new File(sdkDirectory, "conf" + File.separator + "deploy.properties");
            return propertiesFile;
        }
        return null;
    }


}
