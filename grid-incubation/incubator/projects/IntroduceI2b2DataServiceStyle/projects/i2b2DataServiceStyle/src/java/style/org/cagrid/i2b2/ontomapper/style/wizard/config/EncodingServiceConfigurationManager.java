package org.cagrid.i2b2.ontomapper.style.wizard.config;

import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import org.cagrid.i2b2.ontomapper.processor.I2B2QueryProcessor;

/**
 * EncodingServiceConfigurationManager
 * Manages the configuration of the encoding service URL for
 * the i2b2/ontomapper data service wizard
 * 
 * @author David
 */
public class EncodingServiceConfigurationManager extends BaseConfigurationManager {
    
    private String encodingServiceURL = null;

    public EncodingServiceConfigurationManager(ServiceExtensionDescriptionType extensionDescription,
        ServiceInformation serviceInformation) {
        super(extensionDescription, serviceInformation);
    }
    
    
    public void setEncodingServiceURL(String url) {
        this.encodingServiceURL = url;
    }
    
    
    public String getEncodingServiceURL() {
        return this.encodingServiceURL;
    }


    public void applyConfigration() throws Exception {
        setServiceProperty(I2B2QueryProcessor.CADSR_URL, getEncodingServiceURL());
    }
}
