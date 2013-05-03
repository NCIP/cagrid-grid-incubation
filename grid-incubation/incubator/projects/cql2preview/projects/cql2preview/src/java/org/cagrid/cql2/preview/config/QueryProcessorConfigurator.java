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
package org.cagrid.cql2.preview.config;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/** 
 *  QueryProcessorConfigurator
 *  Tool to build a configuration package for the CQL 2 Query Processor to
 *  handle an arbitrary installation of the caCORE SDK version 4.0
 *  
 *  TODO: add validation to UI fields
 * 
 * @author David Ervin
 * 
 * @created Apr 4, 2008 2:17:30 PM
 * @version $Id: QueryProcessorConfigurator.java,v 1.12 2008/04/15 19:19:38 dervin Exp $ 
 */
public class QueryProcessorConfigurator extends JFrame {
    // keys for validation of fields
    private static final String SDK_OUTPUT_DIR_KEY = "caCORE SDK 'output' directory";
    private static final String DOMAIN_MODEL_KEY = "Domain Model XMI filename";
    private static final String DOMAIN_MODEL_NAME_KEY = "Domain Model project name";
    private static final String DOMAIN_MODEL_VERSION_KEY = "Domain Model version";
    private static final String APPLICATION_NAME_KEY = "SDK Application name";
    private static final String REMOTE_HOST_KEY = "SDK Application host name";
    private static final String REMOTE_PORT_KEY = "SDK Application host network port";
    
    private static String lastDirectory = null;
    
    private JLabel sdkOutputDirLabel = null;
    private JTextField sdkOutputDirTextField = null;
    private JButton sdkOutputDirBrowseButton = null;
    private JPanel sdkOutputPanel = null;
    private JLabel modelFileLabel = null;
    private JTextField modelFileTextField = null;
    private JButton modelFileBrowseButton = null;
    private JLabel projectNameLabel = null;
    private JTextField projectNameTextField = null;
    private JLabel projectVersionLabel = null;
    private JTextField projectVersionTextField = null;
    private JRadioButton eaXmiTypeRadioButton = null;
    private JRadioButton argoXmiTypeRadioButton = null;
    private JPanel xmiTypePanel = null;
    private JPanel domainModelPanel = null;
    private JLabel applicationNameLabel = null;
    private JTextField applicationNameTextField = null;
    private JLabel remoteHostnameLabel = null;
    private JTextField remoteHostnameTextField = null;
    private JLabel networkPortLabel = null;
    private JTextField networkPortTextField = null;
    private JPanel serviceConfigPanel = null;
    private JButton buildConfigurationButton = null;
    private JButton exitButton = null;
    private JPanel buttonPanel = null;
    private JPanel mainPanel = null;
    
    private IconFeedbackPanel validationPanel = null;
    private ValidationResultModel validationModel = null;
    private DocumentChangeAdapter textFieldChangeListener = null;
    
    private ConfigPackageBuilder packageBuilder = null;


    public QueryProcessorConfigurator() {
        super();
        setTitle("Query Processor Configurator");
        validationModel = new DefaultValidationResultModel();
        packageBuilder = new ConfigPackageBuilder();
        textFieldChangeListener = new DocumentChangeAdapter() {
            public void documentEdited(DocumentEvent e) {
                validateInput();
            }
        };
        configureValidation();
        initialize();
    }
    
    
    private void initialize() {
        this.setSize(new Dimension(450, 376));
        this.setContentPane(getValidationPanel());
    }
    

    /**
     * This method initializes sdkOutputDirLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getSdkOutputDirLabel() {
        if (sdkOutputDirLabel == null) {
            sdkOutputDirLabel = new JLabel();
            sdkOutputDirLabel.setText("SDK Output Dir:");
        }
        return sdkOutputDirLabel;
    }


    /**
     * This method initializes sdkOutputDirTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getSdkOutputDirTextField() {
        if (sdkOutputDirTextField == null) {
            sdkOutputDirTextField = new JTextField();
            sdkOutputDirTextField.getDocument()
                .addDocumentListener(this.textFieldChangeListener);
        }
        return sdkOutputDirTextField;
    }


    /**
     * This method initializes sdkOutputDirBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getSdkOutputDirBrowseButton() {
        if (sdkOutputDirBrowseButton == null) {
            sdkOutputDirBrowseButton = new JButton();
            sdkOutputDirBrowseButton.setText("Browse");
            sdkOutputDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    browseForSdkOutput();
                }
            });
        }
        return sdkOutputDirBrowseButton;
    }


    /**
     * This method initializes sdkOutputPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSdkOutputPanel() {
        if (sdkOutputPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            sdkOutputPanel = new JPanel();
            sdkOutputPanel.setLayout(new GridBagLayout());
            sdkOutputPanel.setBorder(BorderFactory.createTitledBorder(
                null, "SDK Output", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, null));
            sdkOutputPanel.add(getSdkOutputDirLabel(), gridBagConstraints);
            sdkOutputPanel.add(getSdkOutputDirTextField(), gridBagConstraints1);
            sdkOutputPanel.add(getSdkOutputDirBrowseButton(), gridBagConstraints2);
        }
        return sdkOutputPanel;
    }


    /**
     * This method initializes modelFileLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getModelFileLabel() {
        if (modelFileLabel == null) {
            modelFileLabel = new JLabel();
            modelFileLabel.setText("Model File:");
        }
        return modelFileLabel;
    }


    /**
     * This method initializes modelFileTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getModelFileTextField() {
        if (modelFileTextField == null) {
            modelFileTextField = new JTextField();
            modelFileTextField.getDocument()
                .addDocumentListener(this.textFieldChangeListener);
        }
        return modelFileTextField;
    }


    /**
     * This method initializes modelFileBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getModelFileBrowseButton() {
        if (modelFileBrowseButton == null) {
            modelFileBrowseButton = new JButton();
            modelFileBrowseButton.setText("Browse");
            modelFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    browseForModel();
                }
            });
        }
        return modelFileBrowseButton;
    }


    /**
     * This method initializes projectNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectNameLabel() {
        if (projectNameLabel == null) {
            projectNameLabel = new JLabel();
            projectNameLabel.setText("Project Name:");
        }
        return projectNameLabel;
    }


    /**
     * This method initializes projectNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectNameTextField() {
        if (projectNameTextField == null) {
            projectNameTextField = new JTextField();
            projectNameTextField.getDocument()
                .addDocumentListener(this.textFieldChangeListener);
        }
        return projectNameTextField;
    }


    /**
     * This method initializes projectVersionLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getProjectVersionLabel() {
        if (projectVersionLabel == null) {
            projectVersionLabel = new JLabel();
            projectVersionLabel.setText("Project Version:");
        }
        return projectVersionLabel;
    }


    /**
     * This method initializes projectVersionTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getProjectVersionTextField() {
        if (projectVersionTextField == null) {
            projectVersionTextField = new JTextField();
            projectVersionTextField.getDocument()
                .addDocumentListener(this.textFieldChangeListener);
        }
        return projectVersionTextField;
    }


    /**
     * This method initializes eaXmiTypeRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getEaXmiTypeRadioButton() {
        if (eaXmiTypeRadioButton == null) {
            eaXmiTypeRadioButton = new JRadioButton();
            eaXmiTypeRadioButton.setText("Enterprise Architect");
        }
        return eaXmiTypeRadioButton;
    }


    /**
     * This method initializes argoXmiTypeRadioButton	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getArgoXmiTypeRadioButton() {
        if (argoXmiTypeRadioButton == null) {
            argoXmiTypeRadioButton = new JRadioButton();
            argoXmiTypeRadioButton.setText("ArgoUML");
        }
        return argoXmiTypeRadioButton;
    }


    /**
     * This method initializes xmiTypePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getXmiTypePanel() {
        if (xmiTypePanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 0;
            xmiTypePanel = new JPanel();
            xmiTypePanel.setLayout(new GridBagLayout());
            xmiTypePanel.setBorder(BorderFactory.createTitledBorder(
                null, "XMI Type", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, null));
            xmiTypePanel.add(getEaXmiTypeRadioButton(), gridBagConstraints3);
            xmiTypePanel.add(getArgoXmiTypeRadioButton(), gridBagConstraints4);
            
            // button group keeps everything sane
            ButtonGroup group = new ButtonGroup();
            group.add(getEaXmiTypeRadioButton());
            group.add(getArgoXmiTypeRadioButton());
            group.setSelected(getEaXmiTypeRadioButton().getModel(), true);
        }
        return xmiTypePanel;
    }


    /**
     * This method initializes domainModelPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getDomainModelPanel() {
        if (domainModelPanel == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.gridwidth = 3;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 3;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridwidth = 2;
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 3;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 2;
            gridBagConstraints9.weightx = 1.0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.gridwidth = 2;
            gridBagConstraints9.gridx = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridy = 2;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 2;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 0;
            domainModelPanel = new JPanel();
            domainModelPanel.setLayout(new GridBagLayout());
            domainModelPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Domain Model", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, null));
            domainModelPanel.add(getModelFileLabel(), gridBagConstraints5);
            domainModelPanel.add(getModelFileTextField(), gridBagConstraints6);
            domainModelPanel.add(getModelFileBrowseButton(), gridBagConstraints7);
            domainModelPanel.add(getProjectNameLabel(), gridBagConstraints8);
            domainModelPanel.add(getProjectNameTextField(), gridBagConstraints9);
            domainModelPanel.add(getProjectVersionLabel(), gridBagConstraints10);
            domainModelPanel.add(getProjectVersionTextField(), gridBagConstraints11);
            domainModelPanel.add(getXmiTypePanel(), gridBagConstraints12);
        }
        return domainModelPanel;
    }


    /**
     * This method initializes applicationNameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getApplicationNameLabel() {
        if (applicationNameLabel == null) {
            applicationNameLabel = new JLabel();
            applicationNameLabel.setText("Application Name:");
        }
        return applicationNameLabel;
    }


    /**
     * This method initializes applicationNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getApplicationNameTextField() {
        if (applicationNameTextField == null) {
            applicationNameTextField = new JTextField();
            applicationNameTextField.getDocument()
                .addDocumentListener(this.textFieldChangeListener);
        }
        return applicationNameTextField;
    }


    /**
     * This method initializes remoteHostnameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getRemoteHostnameLabel() {
        if (remoteHostnameLabel == null) {
            remoteHostnameLabel = new JLabel();
            remoteHostnameLabel.setText("Remote Host Name:");
        }
        return remoteHostnameLabel;
    }


    /**
     * This method initializes remoteHostnameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getRemoteHostnameTextField() {
        if (remoteHostnameTextField == null) {
            remoteHostnameTextField = new JTextField();
            remoteHostnameTextField.getDocument()
                .addDocumentListener(this.textFieldChangeListener);
        }
        return remoteHostnameTextField;
    }


    /**
     * This method initializes networkPortLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getNetworkPortLabel() {
        if (networkPortLabel == null) {
            networkPortLabel = new JLabel();
            networkPortLabel.setText("Network Port:");
        }
        return networkPortLabel;
    }


    /**
     * This method initializes networkPortTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getNetworkPortTextField() {
        if (networkPortTextField == null) {
            networkPortTextField = new JTextField();
            networkPortTextField.getDocument()
                .addDocumentListener(this.textFieldChangeListener);
        }
        return networkPortTextField;
    }


    /**
     * This method initializes serviceConfigPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getServiceConfigPanel() {
        if (serviceConfigPanel == null) {
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints18.gridy = 2;
            gridBagConstraints18.weightx = 1.0;
            gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints18.gridx = 1;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints17.gridy = 2;
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints16.gridy = 1;
            gridBagConstraints16.weightx = 1.0;
            gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints16.gridx = 1;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints15.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints15.gridy = 1;
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.gridy = 0;
            gridBagConstraints14.weightx = 1.0;
            gridBagConstraints14.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints14.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridy = 0;
            serviceConfigPanel = new JPanel();
            serviceConfigPanel.setLayout(new GridBagLayout());
            serviceConfigPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Application Configuration", TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, null, null));
            serviceConfigPanel.add(getApplicationNameLabel(), gridBagConstraints13);
            serviceConfigPanel.add(getApplicationNameTextField(), gridBagConstraints14);
            serviceConfigPanel.add(getRemoteHostnameLabel(), gridBagConstraints15);
            serviceConfigPanel.add(getRemoteHostnameTextField(), gridBagConstraints16);
            serviceConfigPanel.add(getNetworkPortLabel(), gridBagConstraints17);
            serviceConfigPanel.add(getNetworkPortTextField(), gridBagConstraints18);
        }
        return serviceConfigPanel;
    }


    /**
     * This method initializes buildConfigurationButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getBuildConfigurationButton() {
        if (buildConfigurationButton == null) {
            buildConfigurationButton = new JButton();
            buildConfigurationButton.setText("Build Configuration");
            buildConfigurationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // prompt user for save location
                    JFileChooser chooser = new JFileChooser(".");
                    chooser.setFileFilter(new FileFilter() {
                        public String getDescription() {
                            return "ZIP files (*.zip)";
                        }
                        
                        
                        public boolean accept(File path) {
                            return path.isDirectory() || path.getName().toLowerCase().endsWith(".zip");
                        }
                    });
                    int choice = chooser.showSaveDialog(QueryProcessorConfigurator.this);
                    if (choice == JFileChooser.APPROVE_OPTION) {
                        String selectedFile = chooser.getSelectedFile().getAbsolutePath();
                        if (!(selectedFile.endsWith(".zip") || selectedFile.endsWith(".ZIP"))) {
                            selectedFile += ".zip";
                        }
                        // set up the config builder
                        packageBuilder.setApplicationHostName(getRemoteHostnameTextField().getText());
                        packageBuilder.setApplicationNetworkPort(getNetworkPortTextField().getText());
                        packageBuilder.setProjectName(getProjectNameTextField().getText());
                        packageBuilder.setProjectVersion(getProjectVersionTextField().getText());
                        packageBuilder.setSdkApplicationName(getApplicationNameTextField().getText());
                        packageBuilder.setSdkOutputDir(getSdkOutputDirTextField().getText());
                        packageBuilder.setXmiFilename(getModelFileTextField().getText());
                        packageBuilder.setXmiType(
                            getEaXmiTypeRadioButton().isSelected() ? XmiFileType.SDK_40_EA : XmiFileType.SDK_40_ARGO);
                        try {
                            packageBuilder.buildConfiguration(new File(selectedFile));
                            String[] message = new String[] {
                                "Package built; saved as",
                                selectedFile
                            };
                            JOptionPane.showMessageDialog(QueryProcessorConfigurator.this, message);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            String[] message = new String[] {
                                "Error creating configuration package:",
                                ex.getMessage()
                            };
                            JOptionPane.showMessageDialog(QueryProcessorConfigurator.this, message);
                        }
                    }
                }
            });
        }
        return buildConfigurationButton;
    }


    /**
     * This method initializes exitButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit");
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return exitButton;
    }


    /**
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
            gridBagConstraints20.gridx = 1;
            gridBagConstraints20.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints20.gridy = 0;
            GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
            gridBagConstraints19.gridx = 0;
            gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints19.gridy = 0;
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridBagLayout());
            buttonPanel.add(getBuildConfigurationButton(), gridBagConstraints19);
            buttonPanel.add(getExitButton(), gridBagConstraints20);
        }
        return buttonPanel;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
            gridBagConstraints24.gridx = 0;
            gridBagConstraints24.anchor = GridBagConstraints.EAST;
            gridBagConstraints24.gridy = 3;
            GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
            gridBagConstraints23.gridx = 0;
            gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints23.weightx = 1.0D;
            gridBagConstraints23.gridy = 2;
            GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
            gridBagConstraints22.gridx = 0;
            gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints22.weightx = 1.0D;
            gridBagConstraints22.gridy = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getSdkOutputPanel(), gridBagConstraints21);
            mainPanel.add(getDomainModelPanel(), gridBagConstraints22);
            mainPanel.add(getServiceConfigPanel(), gridBagConstraints23);
            mainPanel.add(getButtonPanel(), gridBagConstraints24);
        }
        return mainPanel;
    }
    
    
    // -------
    // helpers
    // -------
    
    
    private void browseForSdkOutput() {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonText("Select");
        int choice = chooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String selection = chooser.getSelectedFile().getAbsolutePath();
            getSdkOutputDirTextField().setText(selection);
            lastDirectory = selection;
        }
    }
    
    
    private void browseForModel() {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "XMI Models (*.xmi | *.uml | *.xml)";
            }
            
            
            public boolean accept(File path) {
                if (path.isFile()) {
                    String name = path.getName().toLowerCase();
                    return name.endsWith(".xmi") || name.endsWith(".uml") || name.endsWith(".xml");
                }
                return path.isDirectory();
            }
        });
        chooser.setApproveButtonText("Select");
        int choice = chooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String selection = chooser.getSelectedFile().getAbsolutePath();
            getModelFileTextField().setText(selection);
            lastDirectory = selection;
        }
    }
    
    
    // ----------
    // validation
    // ----------
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            validationPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationPanel;
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getSdkOutputDirTextField(), SDK_OUTPUT_DIR_KEY);
        ValidationComponentUtils.setMessageKey(getModelFileTextField(), DOMAIN_MODEL_KEY);
        ValidationComponentUtils.setMessageKey(getProjectNameTextField(), DOMAIN_MODEL_NAME_KEY);
        ValidationComponentUtils.setMessageKey(getProjectVersionTextField(), DOMAIN_MODEL_VERSION_KEY);
        ValidationComponentUtils.setMessageKey(getApplicationNameTextField(), APPLICATION_NAME_KEY);
        ValidationComponentUtils.setMessageKey(getRemoteHostnameTextField(), REMOTE_HOST_KEY);
        ValidationComponentUtils.setMessageKey(getNetworkPortTextField(), REMOTE_PORT_KEY);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        String sdkOutputFilename = getSdkOutputDirTextField().getText();
        if (ValidationUtils.isBlank(sdkOutputFilename)) {
            result.add(new SimpleValidationMessage(
                SDK_OUTPUT_DIR_KEY + " cannot be blank", Severity.ERROR, SDK_OUTPUT_DIR_KEY));
        } else {
            File sdkOutputDir = new File(sdkOutputFilename);
            if (!sdkOutputDir.exists() || !sdkOutputDir.isDirectory()) {
                result.add(new SimpleValidationMessage(
                    SDK_OUTPUT_DIR_KEY + " must be a valid directory", Severity.ERROR, SDK_OUTPUT_DIR_KEY));
            }
        }
        
        String domainModelFilename = getModelFileTextField().getText();
        if (ValidationUtils.isBlank(domainModelFilename)) {
            result.add(new SimpleValidationMessage(
                DOMAIN_MODEL_KEY + " cannot be blank", Severity.ERROR, DOMAIN_MODEL_KEY));
        } else {
            File domainModel = new File(domainModelFilename);
            if (!domainModel.exists() || !domainModel.canRead()) {
                result.add(new SimpleValidationMessage(
                    DOMAIN_MODEL_KEY + " must be a valid file", Severity.ERROR, DOMAIN_MODEL_KEY));
            }
        }
        
        if (ValidationUtils.isBlank(getProjectNameTextField().getText())) {
            result.add(new SimpleValidationMessage(
                DOMAIN_MODEL_NAME_KEY + " cannot be blank", Severity.ERROR, DOMAIN_MODEL_NAME_KEY));
        }
        
        if (ValidationUtils.isBlank(getProjectVersionTextField().getText())) {
            result.add(new SimpleValidationMessage(
                DOMAIN_MODEL_VERSION_KEY + " cannot be blank", Severity.ERROR, DOMAIN_MODEL_VERSION_KEY));
        }
        
        String applicationName = getApplicationNameTextField().getText();
        if (ValidationUtils.isBlank(applicationName)) {
            result.add(new SimpleValidationMessage(
                APPLICATION_NAME_KEY + " cannot be blank", Severity.ERROR, APPLICATION_NAME_KEY));
        } else {
            if (!ValidationComponentUtils.hasError(getSdkOutputDirTextField(), result)) {
                // verify the application name matches what the output dir has
                File outputDir = new File(getSdkOutputDirTextField().getText());
                File[] outputContents = outputDir.listFiles();
                File applicationOutDir = outputContents[0]; // should be named the same as the application
                if (!applicationOutDir.getName().equals(applicationName)) {
                    result.add(new SimpleValidationMessage(
                        APPLICATION_NAME_KEY + " must match expected from output directory (" + applicationOutDir.getName() + ")",
                        Severity.ERROR, APPLICATION_NAME_KEY));
                }
            }            
        }
        
        String appHostname = getRemoteHostnameTextField().getText();
        if (ValidationUtils.isBlank(appHostname)) {
            result.add(new SimpleValidationMessage(
                REMOTE_HOST_KEY + " cannot be blank", Severity.ERROR, REMOTE_HOST_KEY));
        } else {
            try {
                new URL(appHostname);
            } catch (Exception ex) {
                result.add(new SimpleValidationMessage(
                    REMOTE_HOST_KEY + " is not a valid URL: " + ex.getMessage(), Severity.ERROR, REMOTE_HOST_KEY));
            }
        }
        
        String appPort = getNetworkPortTextField().getText();
        if (ValidationUtils.isBlank(appPort)) {
            result.add(new SimpleValidationMessage(
                REMOTE_PORT_KEY + " cannot be blank", Severity.ERROR, REMOTE_PORT_KEY));
        } else {
            try {
                Integer.valueOf(appPort);
            } catch (Exception ex) {
                result.add(new SimpleValidationMessage(
                    REMOTE_PORT_KEY + " must be an integer: " + ex.getMessage(), Severity.ERROR, REMOTE_PORT_KEY));
            }
        }

        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        
        // update the build button's enabled state
        getBuildConfigurationButton().setEnabled(!validationModel.hasErrors());
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
    

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Error setting system look and feel");
        }
        QueryProcessorConfigurator config = new QueryProcessorConfigurator();
        config.setVisible(true);
        config.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
