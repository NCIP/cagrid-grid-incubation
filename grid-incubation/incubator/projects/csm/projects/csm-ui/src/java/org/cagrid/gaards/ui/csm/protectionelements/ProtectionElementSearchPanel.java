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
package org.cagrid.gaards.ui.csm.protectionelements;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;


public class ProtectionElementSearchPanel extends JPanel {

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
    private JButton search = null;
    private Application application;
    private ProtectionElementSearchI searchInterface;


    public ProtectionElementSearchPanel(Application application) {
        this(application, null);
    }


    /**
     * This is the default constructor
     */
    public ProtectionElementSearchPanel(Application application, ProtectionElementSearchI searchInterface) {
        super();
        this.application = application;
        this.searchInterface = searchInterface;
        initialize();
    }


    public void toggleAccess(boolean enabled) {
        getProtectionElementName().setEditable(enabled);
        getObjectId().setEditable(enabled);
        getAttributeName().setEditable(enabled);
        getAttributeValue().setEditable(enabled);
        getProtectionElementType().setEditable(enabled);
        getSearch().setEnabled(enabled);
    }


    public List<ProtectionElement> performSearch() throws Exception {
        ProtectionElementSearchCriteria crit = new ProtectionElementSearchCriteria();
        crit.setApplicationId(application.getId());
        crit.setName(Utils.clean(getProtectionElementName().getText()));
        crit.setObjectId(Utils.clean(getObjectId().getText()));
        crit.setAttribute(Utils.clean(getAttributeName().getText()));
        crit.setAttributeValue(Utils.clean(getAttributeValue().getText()));
        crit.setType(Utils.clean(getProtectionElementType().getText()));
        return this.application.getProtectionElements(crit);
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
        gridBagConstraints8.gridx = 0;
        gridBagConstraints8.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints8.gridwidth = 2;
        gridBagConstraints8.gridy = 5;
        GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
        gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints7.gridy = 4;
        gridBagConstraints7.weightx = 1.0;
        gridBagConstraints7.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints7.gridx = 1;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.gridy = 3;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.anchor = GridBagConstraints.WEST;
        gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints6.gridx = 1;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridy = 2;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.anchor = GridBagConstraints.WEST;
        gridBagConstraints5.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints5.gridx = 1;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints4.anchor = GridBagConstraints.WEST;
        gridBagConstraints4.gridx = 1;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.anchor = GridBagConstraints.WEST;
        gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints21.anchor = GridBagConstraints.WEST;
        gridBagConstraints21.gridy = 4;
        typeLabel = new JLabel();
        typeLabel.setText("Type");
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints11.anchor = GridBagConstraints.WEST;
        gridBagConstraints11.gridy = 3;
        attributeValueLabel = new JLabel();
        attributeValueLabel.setText("Attribute Value");
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints2.gridy = 2;
        attributeNameLabel = new JLabel();
        attributeNameLabel.setText("Attribute Name");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.gridy = 1;
        objectIdLabel = new JLabel();
        objectIdLabel.setText("Object Id");
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.gridx = 0;
        nameLabel = new JLabel();
        nameLabel.setText("Name");
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(null, "Search for Protection Elements",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
        this.add(nameLabel, gridBagConstraints);
        this.add(objectIdLabel, gridBagConstraints1);
        this.add(attributeNameLabel, gridBagConstraints2);
        this.add(attributeValueLabel, gridBagConstraints11);
        this.add(typeLabel, gridBagConstraints21);
        this.add(getProtectionElementName(), gridBagConstraints3);
        this.add(getObjectId(), gridBagConstraints4);
        this.add(getAttributeName(), gridBagConstraints5);
        this.add(getAttributeValue(), gridBagConstraints6);
        this.add(getProtectionElementType(), gridBagConstraints7);
        this.add(getSearch(), gridBagConstraints8);
    }


    /**
     * This method initializes protectionElementName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionElementName() {
        if (protectionElementName == null) {
            protectionElementName = new JTextField();
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
        }
        return protectionElementType;
    }


    /**
     * This method initializes search
     * 
     * @return javax.swing.JButton
     */
    private JButton getSearch() {
        if (search == null) {
            search = new JButton();
            search.setText("Search");
            search.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {

                            searchInterface.protectionElementSearch();

                        }
                    };
                    try {
                        GridApplication.getContext().executeInBackground(runner);
                    } catch (Exception t) {
                        t.getMessage();
                    }
                }
            });
        }
        return search;
    }

}
