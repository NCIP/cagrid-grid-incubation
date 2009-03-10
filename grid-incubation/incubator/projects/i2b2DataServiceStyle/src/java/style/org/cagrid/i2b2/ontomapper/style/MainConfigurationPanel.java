package org.cagrid.i2b2.ontomapper.style;

import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;


public class MainConfigurationPanel extends gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel {

    public MainConfigurationPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        // TODO Auto-generated constructor stub
    }


    @Override
    public String getPanelShortName() {
        return "I2B2 Data Service";
    }


    @Override
    public String getPanelTitle() {
        return "Configuration Panel";
    }


    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

}
