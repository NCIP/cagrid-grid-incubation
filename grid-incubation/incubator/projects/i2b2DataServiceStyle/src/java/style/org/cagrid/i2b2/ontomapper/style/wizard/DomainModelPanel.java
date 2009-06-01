package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DomainModelPanel
 * Panel which allows a service developer to select the domain model
 * used in their i2b2 data service
 * 
 * @author David
 */
public class DomainModelPanel extends AbstractWizardPanel {
    
    private static final Log LOG = LogFactory.getLog(DomainModelPanel.class);

    public DomainModelPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        initialize();
    }


    public String getPanelShortName() {
        return "Domain Model";
    }


    public String getPanelTitle() {
        return "Select Domain Model";
    }


    public void update() {
        // TODO Auto-generated method stub

    }
    
    
    private void initialize() {
        
    }
}
