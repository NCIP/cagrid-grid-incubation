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
package org.cagrid.gaards.ui.csm.groups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.client.LocalGroup;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;


public class LocalGroupPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private LocalGroup group; // @jve:decl-index=0:
    private JLabel jLabel = null;
    private JTextField groupName = null;
    private JLabel jLabel1 = null;
    private JTextField groupId = null;
    private JLabel jLabel2 = null;
    private JTextField lastUpdated = null;
    private JLabel jLabel3 = null;
    private JTextField description = null;
    private JButton modify = null;
    private GroupsPanel groupsPanel;


    /**
     * This is the default constructor
     */
    public LocalGroupPanel(GroupsPanel groupsPanel) {
        super();
        this.groupsPanel = groupsPanel;
        initialize();

    }


    public void setGroup(LocalGroup group) {
        this.group = group;
        getGroupId().setText(String.valueOf(this.group.getId()));
        getGroupName().setText(this.group.getName());
        getDescription().setText(this.group.getDescription());
        Calendar c = this.group.getLastUpdated();
        if (c != null) {
            getLastUpdated().setText(DateFormat.getDateInstance().format(c.getTime()));
        }
        this.modify.setEnabled(true);
    }


    public void clearAll() {
        this.group = null;
        getGroupId().setText("");
        getGroupName().setText("");
        getDescription().setText("");
        getLastUpdated().setText("");
        this.modify.setEnabled(false);
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 0;
        gridBagConstraints12.gridwidth = 2;
        gridBagConstraints12.gridy = 4;
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.anchor = GridBagConstraints.WEST;
        gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints6.gridx = 1;
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints5.anchor = GridBagConstraints.WEST;
        gridBagConstraints5.gridy = 2;
        jLabel3 = new JLabel();
        jLabel3.setText("Description");
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.gridy = 3;
        gridBagConstraints4.anchor = GridBagConstraints.WEST;
        gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints4.weightx = 1.0;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.anchor = GridBagConstraints.WEST;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridy = 3;
        jLabel2 = new JLabel();
        jLabel2.setText("Last Updated");
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.gridx = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.anchor = GridBagConstraints.WEST;
        gridBagConstraints11.gridy = 0;
        gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints11.gridx = 0;
        jLabel1 = new JLabel();
        jLabel1.setText("Id");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints1.weightx = 1.0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.gridx = 0;
        jLabel = new JLabel();
        jLabel.setText("Name");
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Local Group",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
            null, LookAndFeel.getPanelLabelColor()));
        this.add(jLabel, gridBagConstraints);
        this.add(getGroupName(), gridBagConstraints1);
        this.add(jLabel1, gridBagConstraints11);
        this.add(getGroupId(), gridBagConstraints2);
        this.add(jLabel2, gridBagConstraints3);
        this.add(getLastUpdated(), gridBagConstraints4);
        this.add(jLabel3, gridBagConstraints5);
        this.add(getDescription(), gridBagConstraints6);
        this.add(getModify(), gridBagConstraints12);
    }


    /**
     * This method initializes groupName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGroupName() {
        if (groupName == null) {
            groupName = new JTextField();
        }
        return groupName;
    }


    /**
     * This method initializes groupId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGroupId() {
        if (groupId == null) {
            groupId = new JTextField();
            groupId.setEditable(false);
        }
        return groupId;
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
        }
        return lastUpdated;
    }


    /**
     * This method initializes description
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDescription() {
        if (description == null) {
            description = new JTextField();
        }
        return description;
    }


    /**
     * This method initializes modify
     * 
     * @return javax.swing.JButton
     */
    private JButton getModify() {
        if (modify == null) {
            modify = new JButton();
            modify.setText("Modify");
            modify.setEnabled(false);
            modify.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            modifyGroup();
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
        return modify;
    }


    private void modifyGroup() {
        this.modify.setEnabled(false);
        this.group.setName(Utils.clean(getGroupName().getText()));
        this.group.setDescription(Utils.clean(getDescription().getText()));
        this.groupsPanel.modifyGroup(group);
        this.modify.setEnabled(true);
    }

}
