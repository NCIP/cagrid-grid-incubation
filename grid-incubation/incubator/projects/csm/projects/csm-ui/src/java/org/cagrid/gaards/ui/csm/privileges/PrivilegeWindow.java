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
package org.cagrid.gaards.ui.csm.privileges;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.client.CSM;
import org.cagrid.gaards.csm.client.Privilege;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


public class PrivilegeWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton create = null;

    private CSM csm;

    private JPanel groupPanel = null;

    private JTextField privilegeName = null;

    private JPanel titlePanel = null;

    private JLabel jLabel = null;

    private JLabel jLabel1 = null;

    private JTextField description = null;

    private ProgressPanel progress = null;

    private boolean privilegeModified = false;

    private boolean add = false;

    private Privilege privilege;

    private JLabel jLabel2 = null;

    private JLabel jLabel3 = null;

    private JTextField privilegeId = null;

    private JTextField lastUpdated = null;


    /**
     * This is the default constructor
     */
    public PrivilegeWindow(CSM csm) {
        this(csm, null);
    }


    public PrivilegeWindow(CSM csm, Privilege privilege) {
        super(GridApplication.getContext().getApplication());
        this.csm = csm;
        this.privilege = privilege;
        if (privilege == null) {
            add = true;
        }
        initialize();

        if (add) {
            this.setTitle("Create Privilege");
        } else {
            this.setTitle("Modify Privilege");
        }

    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 250);
        this.setContentPane(getJContentPane());

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
            buttonPanel.add(getCreate(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addAdmin
     * 
     * @return javax.swing.JButton
     */
    private JButton getCreate() {
        if (create == null) {
            create = new JButton();

            if (add) {
                create.setText("Create");
            } else {
                create.setText("Modify");
            }

            getRootPane().setDefaultButton(create);
            create.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            if (add) {
                                createPrivilege();
                            } else {
                                modifyPrivilege();
                            }

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
        return create;
    }


    public boolean wasPrivilegeModified() {
        return privilegeModified;
    }


    private void createPrivilege() {
        try {
            getProgress().showProgress("Creating privilege...");
            create.setEnabled(false);
            this.csm
                .createPrivilege(Utils.clean(getPrivilegeName().getText()), Utils.clean(getDescription().getText()));
            privilegeModified = true;
            getProgress().stopProgress("");
            dispose();
            GridApplication.getContext().showMessage("Successfully created the privilege.");
        } catch (Exception e) {
            getProgress().stopProgress("Error");
            ErrorDialog.showError(e);
            create.setEnabled(true);
        }
    }


    private void modifyPrivilege() {
        try {
            getProgress().showProgress("Modifying privilege...");
            create.setEnabled(false);
            this.privilege.setName(Utils.clean(getPrivilegeName().getText()));
            this.privilege.setDescription(Utils.clean(getDescription().getText()));
            this.privilege.modify();
            privilegeModified = true;
            getProgress().stopProgress("");
            dispose();
            GridApplication.getContext().showMessage("Successfully modified the privilege.");
        } catch (Exception e) {
            getProgress().stopProgress("Error");
            ErrorDialog.showError(e);
            create.setEnabled(true);
        }
    }


    /**
     * This method initializes groupPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupPanel() {
        if (groupPanel == null) {
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints41.gridy = 3;
            gridBagConstraints41.weightx = 1.0;
            gridBagConstraints41.anchor = GridBagConstraints.WEST;
            gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints41.gridx = 1;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.gridy = 0;
            gridBagConstraints31.weightx = 1.0;
            gridBagConstraints31.anchor = GridBagConstraints.WEST;
            gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints31.gridx = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridy = 3;
            jLabel3 = new JLabel();
            jLabel3.setText("Last Updated");
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.anchor = GridBagConstraints.WEST;
            gridBagConstraints13.gridy = 0;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.gridx = 0;
            jLabel2 = new JLabel();
            jLabel2.setText("Privilege Id");
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.gridy = 2;
            jLabel1 = new JLabel();
            jLabel1.setText("Description");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 1;
            jLabel = new JLabel();
            jLabel.setText("Privilege Name");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.weightx = 1.0;
            groupPanel = new JPanel();
            groupPanel.setLayout(new GridBagLayout());
            groupPanel.add(getPrivilegeName(), gridBagConstraints7);
            groupPanel.add(jLabel, gridBagConstraints);
            groupPanel.add(jLabel1, gridBagConstraints12);
            groupPanel.add(getDescription(), gridBagConstraints2);
            groupPanel.add(jLabel2, gridBagConstraints13);
            groupPanel.add(jLabel3, gridBagConstraints21);
            groupPanel.add(getPrivilegeId(), gridBagConstraints31);
            groupPanel.add(getLastUpdated(), gridBagConstraints41);

            if (add) {
                jLabel2.setVisible(false);
                jLabel3.setVisible(false);
            }
        }
        return groupPanel;
    }


    /**
     * This method initializes privilegeName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPrivilegeName() {
        if (privilegeName == null) {
            privilegeName = new JTextField();
            if (!add) {
                privilegeName.setText(privilege.getName());
            }
        }
        return privilegeName;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            if (add) {
                titlePanel = new TitlePanel("Create Privilege",
                    "Create a privilege that can be used for provisioning access control.");
            } else {
                titlePanel = new TitlePanel(this.privilege.getName() + " Privilege", "Edit the "
                    + this.privilege.getName() + " privilege.");
            }
        }
        return titlePanel;
    }


    /**
     * This method initializes description
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDescription() {
        if (description == null) {
            description = new JTextField();
            if (!add) {
                description.setText(privilege.getDescription());
            }
        }
        return description;
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
     * This method initializes privilegeId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPrivilegeId() {
        if (privilegeId == null) {
            privilegeId = new JTextField();
            privilegeId.setEditable(false);
            if (!add) {
                privilegeId.setText(String.valueOf(privilege.getId()));
            } else {
                privilegeId.setVisible(false);
            }
        }
        return privilegeId;
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
            if (!add) {
                Calendar c = privilege.getLastUpdated();
                if (c != null) {
                    getLastUpdated().setText(DateFormat.getDateInstance().format(c.getTime()));
                }
            } else {
                lastUpdated.setVisible(false);
            }
        }
        return lastUpdated;
    }

}
