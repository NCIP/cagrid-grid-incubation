package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

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
        // TODO Auto-generated method stub

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
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
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
}
