package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.domain.DomainModelFromXmiDialog;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.style.wizard.config.DomainModelConfigurationManager;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/**
 * DomainModelPanel
 * Panel which allows a service developer to select the domain model
 * used in their i2b2 data service
 * 
 * @author David
 */
public class DomainModelPanel extends AbstractWizardPanel {
    
    private static final Log LOG = LogFactory.getLog(DomainModelPanel.class);
    
    private static final String MODEL_FILENAME_KEY = "model_filename";
    private static final String MODEL_PACAKGES_KEY = "model_packages";
    
    private JPanel mainPanel = null;
    private JLabel modelFilenameLabel = null;
    private JTextField modelFilenameTextField = null;
    private JButton selectModelButton = null;
    private JList packageList = null;
    private JScrollPane packageScrollPane = null;
    
    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationPanel = null;
    
    private DomainModelConfigurationManager configurationManager = null;
    
    public DomainModelPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.configurationManager = new DomainModelConfigurationManager(extensionDescription, info);
        configureValidation();
        initialize();
    }


    public String getPanelShortName() {
        return "Domain Model";
    }


    public String getPanelTitle() {
        return "Select Domain Model";
    }


    public void update() {
        String filename = configurationManager.getDomainModelFilename();
        if (filename != null) {
            getModelFilenameTextField().setText(filename);
        } else {
            getModelFilenameTextField().setText("");
        }
        loadDomainModelPackages();

        validateInput();
    }
    
    
    public void movingNext() {
        try {
            configurationManager.applyConfigration();
        } catch (Exception ex) {
            LOG.error("Error applying domain model configuration: " + ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1.0D;
        cons.weighty = 1.0D;
        add(getValidationPanel(), cons);
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            validationPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationPanel;
    }
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
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
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(595, 224));
            mainPanel.add(getModelFilenameLabel(), gridBagConstraints);
            mainPanel.add(getModelFilenameTextField(), gridBagConstraints1);
            mainPanel.add(getSelectModelButton(), gridBagConstraints2);
            mainPanel.add(getPackageScrollPane(), gridBagConstraints3);
        }
        return mainPanel;
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
                    validateInput();
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
            packageList = new JList(new DefaultListModel());
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
            File serviceModel = selection;
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
            }
            // set the filename with the config manager
            configurationManager.setDomainModelFilename(serviceModel.getAbsolutePath());
            // set the filename's text in the UI
            getModelFilenameTextField().setText(serviceModel.getAbsolutePath());
            // store the selected location in the resource manager
            try {
                ResourceManager.setStateProperty(ResourceManager.LAST_FILE, selection.getAbsolutePath());
            } catch (Exception ex) {
                LOG.warn("Unable to store last selected file in Resource Manager: " + ex.getMessage(), ex);
            }
        }
        loadDomainModelPackages();
    }
    
    
    private void loadDomainModelPackages() {
        File modelFile = null;
        if (getModelFilenameTextField().getText().length() != 0) {
            modelFile = new File(getModelFilenameTextField().getText());
        }
        if (modelFile != null && modelFile.exists()) {
            try {
                // read the domain model
                FileReader reader = new FileReader(modelFile);
                DomainModel model = MetadataUtils.deserializeDomainModel(reader);
                reader.close();
                // sort out package names
                Set<String> sortedPackageNames = new TreeSet<String>();
                for (UMLClass clazz : model.getExposedUMLClassCollection().getUMLClass()) {
                    sortedPackageNames.add(clazz.getPackageName());
                }
                // add packages by name to the list on the UI
                String[] packageNames = new String[sortedPackageNames.size()];
                sortedPackageNames.toArray(packageNames);
                getPackageList().setListData(packageNames);
            } catch (Exception ex) {
                ex.printStackTrace();
                // TODO: fail! / show error dialog
            }
        } else {
            getPackageList().setListData(new String[] {});
        }
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getModelFilenameTextField(), MODEL_FILENAME_KEY);
        ValidationComponentUtils.setMessageKey(getPackageList(), MODEL_PACAKGES_KEY);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    

    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        if (ValidationUtils.isBlank(getModelFilenameTextField().getText())) {
            result.add(new SimpleValidationMessage("Must supply a domain model", 
                Severity.ERROR, MODEL_FILENAME_KEY));
        } else if (getPackageList().getModel().getSize() == 0) {
            // domain models must have packages and classes
            result.add(new SimpleValidationMessage("Domain Model must contain one or more packages", 
                Severity.ERROR, MODEL_PACAKGES_KEY));
        }
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        
        // update next button enabled
        setNextEnabled(!validationModel.hasErrors());
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
}
