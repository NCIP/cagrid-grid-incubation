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
 * IntroductionConfigurationManager
 * Manages configuration options for the introduction panel to the 
 * i2b2/ontomapper data service wizard
 * 
 * @author David
 */
public class IntroductionConfigurationManager extends BaseConfigurationManager {
    
    private String queryProcessorClassName = null;

    public IntroductionConfigurationManager(ServiceExtensionDescriptionType extensionDescription,
        ServiceInformation serviceInformation) {
        super(extensionDescription, serviceInformation);
    }


    public void applyConfigration() throws Exception {
        String value = getQueryProcessorClassname();
        if (value == null) {
            // introduce's schema validator chokes on nulls in the value
            value = "";
        }
        CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
            DataServiceConstants.QUERY_PROCESSOR_CLASS_PROPERTY, 
            queryProcessorClassName, false);
    }
    
    
    public void setQueryProcessorClassName(String classname) {
        this.queryProcessorClassName = classname;
    }
    
    
    public String getQueryProcessorClassname() {
        return queryProcessorClassName;
    }
}
