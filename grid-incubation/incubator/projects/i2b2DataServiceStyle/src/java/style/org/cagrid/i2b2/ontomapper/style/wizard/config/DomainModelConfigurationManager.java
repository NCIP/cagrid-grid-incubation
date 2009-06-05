package org.cagrid.i2b2.ontomapper.style.wizard.config;

import java.io.File;

import org.cagrid.i2b2.ontomapper.processor.I2B2QueryProcessor;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

/**
 * DomainModelConfigurationManager
 * Configuration manager for the Domain Model panel
 * 
 * @author David
 */
public class DomainModelConfigurationManager extends BaseConfigurationManager {
    
    private String domainModelFilename = null;
    
    public DomainModelConfigurationManager(ServiceExtensionDescriptionType extensionDescription,
        ServiceInformation serviceInformation) {
        super(extensionDescription, serviceInformation);
    }
    
    
    public void setDomainModelFilename(String filename) {
        this.domainModelFilename = filename;
    }
    
    
    public String getDomainModelFilename() {
        return domainModelFilename;
    }


    public void applyConfigration() throws Exception {
        // copy the domain model from its original location to the service
        File originalModel = new File(getDomainModelFilename());
        File modelCopy = new File(getServiceInformation().getBaseDirectory(), "etc" + File.separator + originalModel.getName());
        Utils.copyFile(originalModel, modelCopy);
        
        // create the domain model resource property
        ResourcePropertyType resourceProperty = new ResourcePropertyType();
        resourceProperty.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
        resourceProperty.setFileLocation(modelCopy.getName());
        resourceProperty.setPopulateFromFile(true);
        resourceProperty.setRegister(true);
        
        // add the RP to the service description
        // have to locate the main service type
        ServiceType service = getServiceInformation().getServices().getService(0);
        // remove any existing resource property of the domain model type
        CommonTools.removeResourceProperty(service, DataServiceConstants.DOMAIN_MODEL_QNAME);
        // add the domain model RP
        CommonTools.addResourcePropety(service, resourceProperty);
        
        // set the service property so the query processor can locate the domain model
        setServiceProperty(I2B2QueryProcessor.DOMAIN_MODEL_FILE_NAME, modelCopy.getName(), true);
    }
}
