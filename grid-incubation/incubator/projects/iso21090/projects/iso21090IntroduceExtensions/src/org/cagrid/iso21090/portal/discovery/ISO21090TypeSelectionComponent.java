package org.cagrid.iso21090.portal.discovery;

import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.io.File;


public class ISO21090TypeSelectionComponent extends NamespaceTypeDiscoveryComponent {

    public ISO21090TypeSelectionComponent(DiscoveryExtensionDescriptionType descriptor, NamespacesType currentNamespaces) {
        super(descriptor, currentNamespaces);
        // TODO Auto-generated constructor stub
    }


    @Override
    public NamespaceType[] createNamespaceType(File arg0, NamespaceReplacementPolicy arg1, MultiEventProgressBar arg2) {
        // TODO Auto-generated method stub
        return null;
    }

}
