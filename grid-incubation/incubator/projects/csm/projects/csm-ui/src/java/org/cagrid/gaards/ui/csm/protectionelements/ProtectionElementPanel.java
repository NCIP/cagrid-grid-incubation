/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.gaards.ui.csm.protectionelements;

import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.grape.LookAndFeel;


public class ProtectionElementPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JLabel nameLabel = null;
    private JLabel objectIdLabel = null;
    private JLabel attributeNameLabel = null;
    private JLabel attributeValueLabel = null;
    private JLabel typeLabel = null;
    private JTextField protectionElementName = null;
    private JTextField objectId = null;
    private JTextField attributeName = null;
    private JTextField attributeValue = null;
    private JTextField protectionElementType = null;
    private ProtectionElement protectionElement;
    private JLabel idLabel = null;
    private JTextField protectionElementId = null;
    private JLabel lastUpdatedLabel = null;
    private JTextField lastUpdated = null;
    private JPanel descriptionPanel = null;
    private JPanel informationPanel = null;
    private JScrollPane jScrollPane = null;
    private JTextArea description = null;
    private Application application;


    /**
     * This is the default constructor
     */
    public ProtectionElementPanel(Application application) {
        super();
        this.application = application;
        initialize();
    }


    public ProtectionElementPanel(ProtectionElement pe) {
        super();
        this.protectionElement = pe;
        initialize();
    }


    public ProtectionElement modifyProtectionElement() throws Exception {
        this.protectionElement.setAttribute(Utils.clean(getAttributeName().getText()));
        this.protectionElement.setAttributeValue(Utils.clean(getAttributeValue().getText()));
        this.protectionElement.setDescription(Utils.clean(getDescription().getText()));
        this.protectionElement.setName(Utils.clean(getProtectionElementName().getText()));
        this.protectionElement.setObjectId(Utils.clean(getObjectId().getText()));
        this.protectionElement.setType(Utils.clean(getProtectionElementType().getText()));
        this.protectionElement.modify();
        return protectionElement;
    }


    public ProtectionElement createProtectionElement() throws Exception {
        return this.application.createProtectionElement(Utils.clean(getProtectionElementName().getText()), Utils
            .clean(getDescription().getText()), Utils.clean(getObjectId().getText()), Utils.clean(getAttributeName()
            .getText()), Utils.clean(getAttributeValue().getText()), Utils.clean(getProtectionElementType().getText()));
    }


    public void toggleAccess(boolean enabled) {
        getProtectionElementName().setEditable(enabled);
        getObjectId().setEditable(enabled);
        getAttributeName().setEditable(enabled);
        getAttributeValue().setEditable(enabled);
        getProtectionElementType().setEditable(enabled);
        getDescription().setEditable(enabled);
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
        gridBagConstraints13.gridx = 0;
        gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints13.fill = GridBagConstraints.BOTH;
        gridBagConstraints13.weightx = 1.0D;
        gridBagConstraints13.weighty = 1.0D;
        gridBagConstraints13.gridy = 1;
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints8.weightx = 1.0D;
        gridBagConstraints8.gridy = 0;
        this.setSize(600, 400);
        this.setLayout(new GridBagLayout());
        this.add(getDescriptionPanel(), gridBagConstraints13);
        this.add(getInformationPanel(), gridBagConstraints8);
    }


    /**
     * This method initializes protectionElementName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionElementName() {
        if (protectionElementName == null) {
            protectionElementName = new JTextField();
            if (protectionElement != null) {
                protectionElementName.setText(protectionElement.getName());
            }
        }
        return protectionElementName;
    }


    /**
     * This method initializes objectId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getObjectId() {
        if (objectId == null) {
            objectId = new JTextField();
            if (protectionElement != null) {
                objectId.setText(protectionElement.getObjectId());
            }
        }
        return objectId;
    }


    /**
     * This method initializes attributeName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAttributeName() {
        if (attributeName == null) {
            attributeName = new JTextField();
            if (protectionElement != null) {
                attributeName.setText(protectionElement.getAttribute());
            }
        }
        return attributeName;
    }


    /**
     * This method initializes attributeValue
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAttributeValue() {
        if (attributeValue == null) {
            attributeValue = new JTextField();
            if (protectionElement != null) {
                attributeValue.setText(protectionElement.getAttributeValue());
            }
        }
        return attributeValue;
    }


    /**
     * This method initializes protectionElementType
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionElementType() {
        if (protectionElementType == null) {
            protectionElementType = new JTextField();
            if (protectionElement != null) {
                protectionElementType.setText(protectionElement.getType());
            }
        }
        return protectionElementType;
    }


    /**
     * This method initializes protectionElementId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionElementId() {
        if (protectionElementId == null) {
            protectionElementId = new JTextField();
            protectionElementId.setEditable(false);
            if (protectionElement != null) {
                protectionElementId.setText(String.valueOf(protectionElement.getId()));
            }
        }
        return protectionElementId;
    }


    /**
     * This method initializes lastUpdated
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastUpdated() {
        if (lastUpdated == null) {
            lastUpdated = new JTextField();
            lastUpdated.setEditable(false);
            if (protectionElement != null) {
                Calendar c = protectionElement.getLastUpdated();
                if (c != null) {
                    lastUpdated.setText(DateFormat.getDateInstance().format(c.getTime()));
                }
            }
        }
        return lastUpdated;
    }


    /**
     * This method initializes descriptionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDescriptionPanel() {
        if (descriptionPanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.BOTH;
            gridBagConstraints14.weighty = 1.0;
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.gridy = 0;
            gridBagConstraints14.weightx = 1.0;
            descriptionPanel = new JPanel();
            descriptionPanel.setBorder(BorderFactory.createTitledBorder(null, "Description",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            descriptionPanel.setLayout(new GridBagLayout());
            descriptionPanel.add(getJScrollPane(), gridBagConstraints14);
        }
        return descriptionPanel;
    }


    /**
     * This method initializes informationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInformationPanel() {
        if (informationPanel == null) {
            informationPanel = new JPanel();
            informationPanel.setLayout(new GridBagLayout());
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.gridy = 6;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
            gridBagConstraints111.gridx = 0;
            gridBagConstraints111.anchor = GridBagConstraints.WEST;
            gridBagConstraints111.gridy = 6;
            lastUpdatedLabel = new JLabel();
            lastUpdatedLabel.setText("Last Updated");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 0;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.gridy = 0;
            idLabel = new JLabel();
            idLabel.setText("Id");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 5;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 4;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridy = 3;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints5.gridx = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.gridy = 5;
            typeLabel = new JLabel();
            typeLabel.setText("Type");
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.gridy = 4;
            attributeValueLabel = new JLabel();
            attributeValueLabel.setText("Attribute Value");
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints2.gridy = 3;
            attributeNameLabel = new JLabel();
            attributeNameLabel.setText("Attribute Name");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.gridy = 2;
            objectIdLabel = new JLabel();
            objectIdLabel.setText("Object Id");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints.gridx = 0;
            nameLabel = new JLabel();
            nameLabel.setText("Name");
            informationPanel.add(nameLabel, gridBagConstraints);
            informationPanel.add(objectIdLabel, gridBagConstraints1);
            informationPanel.add(attributeNameLabel, gridBagConstraints2);
            informationPanel.add(attributeValueLabel, gridBagConstraints11);
            informationPanel.add(typeLabel, gridBagConstraints21);
            informationPanel.add(getProtectionElementName(), gridBagConstraints3);
            informationPanel.add(getObjectId(), gridBagConstraints4);
            informationPanel.add(getAttributeName(), gridBagConstraints5);
            informationPanel.add(getAttributeValue(), gridBagConstraints6);
            informationPanel.add(getProtectionElementType(), gridBagConstraints7);
            if (this.protectionElement != null) {
                informationPanel.add(idLabel, gridBagConstraints9);
                informationPanel.add(getProtectionElementId(), gridBagConstraints10);
                informationPanel.add(lastUpdatedLabel, gridBagConstraints111);
                informationPanel.add(getLastUpdated(), gridBagConstraints12);
            }
        }
        return informationPanel;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getDescription());
        }
        return jScrollPane;
    }


    /**
     * This method initializes description
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getDescription() {
        if (description == null) {
            description = new JTextArea();
            description.setWrapStyleWord(true);
            description.setLineWrap(true);
            if(protectionElement!=null){
                description.setText(protectionElement.getDescription());
            }
        }
        return description;
    }

}
