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
package org.cagrid.gaards.ui.csm.groups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.client.LocalGroup;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


public class AddMemberWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton add = null;

    private LocalGroup group;

    private JPanel groupPanel = null;

    private JTextField memberIdentity = null;

    private JPanel titlePanel = null;

    private JLabel jLabel = null;

    private ProgressPanel progress = null;

    private boolean memberAdded = false;

    private JButton find = null;


    /**
     * This is the default constructor
     */
    public AddMemberWindow(LocalGroup group) {
        super(GridApplication.getContext().getApplication());
        this.group = group;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 175);
        this.setContentPane(getJContentPane());
        this.setTitle("Add Member");
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 3;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.weighty = 1.0D;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getButtonPanel(), gridBagConstraints1);
            jContentPane.add(getGroupPanel(), gridBagConstraints11);
            jContentPane.add(getTitlePanel(), gridBagConstraints3);
            jContentPane.add(getProgress(), gridBagConstraints4);
        }
        return jContentPane;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getAdd(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addAdmin
     * 
     * @return javax.swing.JButton
     */
    private JButton getAdd() {
        if (add == null) {
            add = new JButton();
            add.setText("Add");
            getRootPane().setDefaultButton(add);
            add.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addMember();
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
        return add;
    }


    public boolean wasMemberAdded() {
        return memberAdded;
    }


    private void addMember() {
        try {
            getProgress().showProgress("Adding member...");
            add.setEnabled(false);
            String identity = Utils.clean(getMemberIdentity().getText());
            if (identity != null) {
                group.addMember(identity);
                memberAdded = true;
                getProgress().stopProgress("");
                dispose();
                GridApplication.getContext().showMessage("Successfully add the member.");
            } else {
                GridApplication.getContext().showMessage("Please specify a member to add.");
                getProgress().stopProgress("");
            }
        } catch (Exception e) {
            getProgress().stopProgress("Error");
            ErrorDialog.showError(e);
        } finally {
            add.setEnabled(true);
        }
    }


    /**
     * This method initializes groupPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupPanel() {
        if (groupPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Member Identity");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.weightx = 1.0;
            groupPanel = new JPanel();
            groupPanel.setLayout(new GridBagLayout());
            groupPanel.add(getMemberIdentity(), gridBagConstraints7);
            groupPanel.add(jLabel, gridBagConstraints);
            groupPanel.add(getFind(), gridBagConstraints2);
        }
        return groupPanel;
    }


    /**
     * This method initializes memberIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getMemberIdentity() {
        if (memberIdentity == null) {
            memberIdentity = new JTextField();
        }
        return memberIdentity;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Add Member", "Add a member to the " + group.getName() + " group.");
        }
        return titlePanel;
    }


    /**
     * This method initializes progress
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgress() {
        if (progress == null) {
            progress = new ProgressPanel();
            progress.stopProgress("");
        }
        return progress;
    }


    /**
     * This method initializes find
     * 
     * @return javax.swing.JButton
     */
    private JButton getFind() {
        if (find == null) {
            find = new JButton();
            find.setText("Find...");
            find.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    UserSearchDialog dialog = new UserSearchDialog();
                    dialog.setModal(true);
                    GridApplication.getContext().showDialog(dialog);
                    if (dialog.getSelectedUser() != null) {
                        getMemberIdentity().setText(dialog.getSelectedUser());
                    }

                }
            });
        }
        return find;
    }

}
