package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ui.domain.DomainModelFromXmiDialog;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

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
    
    private JLabel modelFilenameLabel = null;
    private JTextField modelFilenameTextField = null;
    private JButton selectModelButton = null;
    private JList packageList = null;
    private JScrollPane packageScrollPane = null;
    
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
        // see if there's already a domain model resource property
        ServiceType service = getServiceInformation().getServices().getService(0);
        ResourcePropertyType[] properties = CommonTools.getResourcePropertiesOfType(
            service, DataServiceConstants.DOMAIN_MODEL_QNAME);
        if (properties != null && properties.length != 0) {
            if (properties[0].isPopulateFromFile() && properties[0].getFileLocation() != null) {
                getModelFilenameTextField().setText(properties[0].getFileLocation());
            }
        }
    }
    
    
    private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.BOTH;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        gridBagConstraints3.gridwidth = 3;
        gridBagConstraints3.insets = new Insets(2, 10, 10, 10);
        gridBagConstraints3.gridx = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.insets = new Insets(10, 2, 2, 10);
        gridBagConstraints2.gridy = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new Insets(10, 2, 2, 2);
        gridBagConstraints1.gridx = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(10, 10, 2, 2);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(595, 224));
        this.add(getModelFilenameLabel(), gridBagConstraints);
        this.add(getModelFilenameTextField(), gridBagConstraints1);
        this.add(getSelectModelButton(), gridBagConstraints2);
        this.add(getPackageScrollPane(), gridBagConstraints3);
    }


    /**
     * This method initializes modelFilenameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getModelFilenameLabel() {
        if (modelFilenameLabel == null) {
            modelFilenameLabel = new JLabel();
            modelFilenameLabel.setText("Model Filename:");
        }
        return modelFilenameLabel;
    }


    /**
     * This method initializes modelFilenameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getModelFilenameTextField() {
        if (modelFilenameTextField == null) {
            modelFilenameTextField = new JTextField();
            modelFilenameTextField.setEditable(false);
        }
        return modelFilenameTextField;
    }


    /**
     * This method initializes selectModelButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getSelectModelButton() {
        if (selectModelButton == null) {
            selectModelButton = new JButton();
            selectModelButton.setText("Select");
            selectModelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectDomainModel();
                }
            });
        }
        return selectModelButton;
    }


    /**
     * This method initializes packageList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getPackageList() {
        if (packageList == null) {
            packageList = new JList();
        }
        return packageList;
    }


    /**
     * This method initializes packageScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getPackageScrollPane() {
        if (packageScrollPane == null) {
            packageScrollPane = new JScrollPane();
            packageScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Packages", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            packageScrollPane.setViewportView(getPackageList());
        }
        return packageScrollPane;
    }
    
    
    private void selectDomainModel() {
        // if there's a previously selected directory, use it
        String defaultDirectory = null;
        try {
            defaultDirectory = ResourceManager.getStateProperty(ResourceManager.LAST_FILE);
        } catch (Exception ex) {
            LOG.warn("Unable to retrieve last selected file from Resource Manager: " + ex.getMessage(), ex);
        }
        FileFilter[] filters = new FileFilter[] {
            FileFilters.XML_FILTER, FileFilters.XMI_FILTER
        };
        JFileChooser chooser = new JFileChooser(defaultDirectory);
        for (FileFilter filter : filters) {
            chooser.addChoosableFileFilter(filter);
        }
        chooser.setFileFilter(filters[0]);
        chooser.setApproveButtonText("Select");
        int choice = chooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File selection = chooser.getSelectedFile();
            File serviceModel = null;
            // XML or XMI?
            if (chooser.getFileFilter() == FileFilters.XMI_FILTER) {
                LOG.debug("XMI detected... must convert!");
                // convert the XMI to a domain model
                DomainModel model = DomainModelFromXmiDialog.createDomainModel(null, selection.getAbsolutePath());
                LOG.debug("Domain Model created from XMI");
                // create a file within the service for the model
                String modelFilename = selection.getName()
                    .substring(0, selection.getName().length() - ".xmi".length());
                modelFilename += ".xml";
                serviceModel = new File(getServiceInformation().getBaseDirectory(),
                    "etc" + File.separator + modelFilename);
                LOG.debug("Serializing Domain Moodel to " + serviceModel.getAbsolutePath());
                // serialize the model
                try {
                    FileWriter writer = new FileWriter(serviceModel);
                    MetadataUtils.serializeDomainModel(model, writer);
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // TODO: fail! / show error dialog!
                }
            } else {
                // just copy the file in to the service
                serviceModel = new File(getServiceInformation().getBaseDirectory(),
                    "etc" + File.separator + selection.getName());
                try {
                    Utils.copyFile(selection, serviceModel);
                    LOG.debug("Copied selected domain model to " + serviceModel.getAbsolutePath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // TODO: fail!  show exception dialog!!
                }
            }
            // create the resource property
            ResourcePropertyType resourceProperty = new ResourcePropertyType();
            resourceProperty.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
            resourceProperty.setFileLocation(serviceModel.getName());
            resourceProperty.setPopulateFromFile(true);
            // store the RP in the service model
            storeDomainModelResourceProperty(resourceProperty);
            // set the filename's text in the UI
            getModelFilenameTextField().setText(serviceModel.getName());
            // store the selected location in the resource manager
            try {
                ResourceManager.setStateProperty(ResourceManager.LAST_FILE, selection.getAbsolutePath());
            } catch (Exception ex) {
                LOG.warn("Unable to store last selected file in Resource Manager: " + ex.getMessage(), ex);
            }
        }
    }
    
    
    private void storeDomainModelResourceProperty(ResourcePropertyType resourceProperty) {
        // have to locate the main service type
        ServiceType service = getServiceInformation().getServices().getService(0);
        // remove any existing resource property of the domain model type
        CommonTools.removeResourceProperty(service, DataServiceConstants.DOMAIN_MODEL_QNAME);
        // add the domain model RP
        CommonTools.addResourcePropety(service, resourceProperty);
    }
}
