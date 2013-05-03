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
package org.cagrid.i2b2.ontomapper.style.wizard.config;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

/**
 * BaseConfigurationManager
 * Base class for handling the configuration of a data service backed 
 * by i2b2/ontomapper
 * 
 * @author David
 */
public abstract class BaseConfigurationManager {

    private ServiceInformation serviceInformation = null;
    private ServiceExtensionDescriptionType extensionDescription = null;
    
    public BaseConfigurationManager(ServiceExtensionDescriptionType extensionDescription, ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
        this.extensionDescription = extensionDescription;
    }
    
    
    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }
    
    
    public ServiceExtensionDescriptionType getExtensionDescription() {
        return extensionDescription;
    }
        
    
    protected void setServiceProperty(String shortKey, String value) {
        setServiceProperty(shortKey, value, false);
    }
    
    
    protected void setServiceProperty(String shortKey, String value, boolean fromEtc) {
        if (value == null) {
            // introduce's schema validator chokes on null values
            value = "";
        }
        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + shortKey, value, fromEtc);
    }
    
    
    public String getServiceProperty(String shortKey) throws Exception {
        String fullKey =DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + shortKey;
        String value = null;
        if (CommonTools.servicePropertyExists(getServiceInformation().getServiceDescriptor(), fullKey)) {
            value = CommonTools.getServicePropertyValue(
                getServiceInformation().getServiceDescriptor(), fullKey);
        }
        return value;
    }
    
    
    public abstract void applyConfigration() throws Exception;
}
