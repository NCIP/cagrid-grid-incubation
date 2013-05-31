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
package org.cagrid.gaards.ui.csm.permissions;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Group;
import org.cagrid.gaards.csm.client.Permission;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.csm.groups.GroupSearchDialog;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

public class ManagePermissionsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JPanel searchPanel = null;
    private JLabel jLabel = null;
    private PermissionTypeComboBox searchType = null;
    private JLabel identityLabel = null;
    private JTextField targetIdentity = null;
    private JButton find = null;
    private JButton search = null;
    private JPanel permissionsPanel = null;
    private JScrollPane jScrollPane = null;
    private PermissionsTable permissions = null;
    private Application application;
    private ProgressPanel progress;
    private Group searchGroup;
    private JPanel searchActionPanel = null;
    private JPanel buttonPanel = null;
    private JButton modify = null;
    private boolean searchCompleted = false;
    private JButton create = null;
    private JButton revoke = null;

    /**
     * This is the default constructor
     */
    public ManagePermissionsPanel(Application application, ProgressPanel progress) {
        super();
        this.application = application;
        this.progress = progress;
        initialize();
        searchTypeChanged();
    }

    private void toggleAccess(boolean enabled) {
        getSearchType().setEnabled(enabled);
        getTargetIdentity().setEnabled(enabled);
        getFind().setEnabled(enabled);
        getSearch().setEnabled(enabled);
    }

    protected void modifyPermission() {
        toggleAccess(false);
        try {
            progress.showProgress("Modifying permission...");
            ModifyPermissionDialog dialog = new ModifyPermissionDialog(getPermissions().getSelectedPermission());
            dialog.setModal(true);
            GridApplication.getContext().showDialog(dialog);
            if (dialog.isModified()) {
                if (searchCompleted) {
                    search();
                }
                progress.stopProgress("Successfully modified the permission.");
            } else {
                progress.stopProgress("Modification of the permission cancelled.");
            }

        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }

    private void revokePermission() {
        toggleAccess(false);
        try {
            this.progress.showProgress("Revoking permission...");
            Permission permission = getPermissions().getSelectedPermission();
            permission.getApplication().revokePermission(permission);
            getPermissions().removePermission();
            this.progress.stopProgress("Successfully revoked the permission.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }

    protected void createPermission() {
        toggleAccess(false);
        try {
            progress.showProgress("Creating permission...");
            ModifyPermissionDialog dialog = new ModifyPermissionDialog(this.application);
            dialog.setModal(true);
            GridApplication.getContext().showDialog(dialog);
            if (dialog.isModified()) {
                if (searchCompleted) {
                    search();
                }
                progress.stopProgress("Successfully created the permission.");
            } else {
                progress.stopProgress("Creation of the permission cancelled.");
            }

        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }

    private void search() {
        toggleAccess(false);
        progress.showProgress("Searching...");
        try {
            List<Permission> results = null;
            String target = null;
            String st = (String) getSearchType().getSelectedItem();
            if (st.equals(PermissionTypeComboBox.USER_HOST_TYPE)) {
                String identity = Utils.clean(getTargetIdentity().getText());
                
                // strip out asterisk characters since wildcard user identity searches should not be supported
                if (identity != null) {
                    identity = identity.replaceAll("\\*","");
                    getTargetIdentity().setText(identity);
                }
                
                if (identity == null) {
                    throw new Exception("Please specify the identity of the party to search for permissions for.");
                }
                target = identity;
                getPermissions().setUserGroupColumnName(PermissionsTable.USER);
                results = this.application.getPermissions(identity);
            } else if (st.equals(PermissionTypeComboBox.GROUP_TYPE)) {
                if (searchGroup == null) {
                    throw new Exception("Please specify the group to search for permissions for.");
                }
                target = searchGroup.getName();

                getPermissions().setUserGroupColumnName(PermissionsTable.GROUP);
                results = this.application.getPermissions(searchGroup);
            }
            getPermissionsPanel().setBorder(
                    BorderFactory.createTitledBorder(null, "Permissions for " + target,
                            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                                    .getPanelLabelColor()));
            getPermissions().clearTable();
            getPermissions().addPermissions(results);
            searchCompleted = true;
            
            String progressMessage = results.size() + " permission" + ((results.size() != 1) ? "s" : "") + " found.";
            progress.stopProgress(progressMessage);
        } catch (Exception e) {
            progress.stopProgress("Error");
            ErrorDialog.showError(e);
        } finally {
            toggleAccess(true);
        }
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 0;
        gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints12.weightx = 1.0D;
        gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints12.gridy = 3;
        GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
        gridBagConstraints41.gridx = 0;
        gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints41.weightx = 1.0D;
        gridBagConstraints41.gridy = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.fill = GridBagConstraints.BOTH;
        gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints11.weightx = 1.0D;
        gridBagConstraints11.weighty = 1.0D;
        gridBagConstraints11.gridy = 2;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridy = 0;
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        this.add(getSearchPanel(), gridBagConstraints);
        this.add(getPermissionsPanel(), gridBagConstraints11);
        this.add(getSearchActionPanel(), gridBagConstraints41);
        this.add(getButtonPanel(), gridBagConstraints12);
    }

    /**
     * This method initializes searchPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 2;
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            searchPanel = new JPanel();
            searchPanel.setLayout(new GridBagLayout());
            searchPanel.add(getJLabel(), gridBagConstraints1);
            searchPanel.add(getSearchType(), gridBagConstraints2);
            searchPanel.add(getIdentityLabel(), gridBagConstraints3);
            searchPanel.add(getTargetIdentity(), gridBagConstraints4);
            searchPanel.add(getFind(), gridBagConstraints5);
            searchPanel.setBorder(BorderFactory.createTitledBorder(null, "Search Criteria",
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                            .getPanelLabelColor()));
        }
        return searchPanel;
    }

    /**
     * This method initializes jLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new JLabel();
            jLabel.setText("Search Type");
        }
        return jLabel;
    }

    /**
     * This method initializes searchType
     * 
     * @return javax.swing.JComboBox
     */
    private PermissionTypeComboBox getSearchType() {
        if (searchType == null) {
            searchType = new PermissionTypeComboBox();
            searchType.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    searchTypeChanged();
                }
            });
            searchType.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    searchTypeChanged();
                }
            });

        }
        return searchType;
    }

    /**
     * This method initializes identityLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getIdentityLabel() {
        if (identityLabel == null) {
            identityLabel = new JLabel();
        }
        return identityLabel;
    }

    private void searchTypeChanged() {
        String st = (String) getSearchType().getSelectedItem();
        if (st.equals(PermissionTypeComboBox.USER_HOST_TYPE)) {
            getIdentityLabel().setText("User/Host");
            getTargetIdentity().setText("");
            getTargetIdentity().setEditable(true);
        } else if (st.equals(PermissionTypeComboBox.GROUP_TYPE)) {
            getIdentityLabel().setText("Group");
            getTargetIdentity().setText("");
            getTargetIdentity().setEditable(false);
        }
    }

    /**
     * This method initializes targetIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTargetIdentity() {
        if (targetIdentity == null) {
            targetIdentity = new JTextField();
        }
        return targetIdentity;
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
                    String st = (String) getSearchType().getSelectedItem();
                    if (st.equals(PermissionTypeComboBox.USER_HOST_TYPE)) {
                        UserSearchDialog dialog = new UserSearchDialog();
                        dialog.setModal(true);
                        GridApplication.getContext().showDialog(dialog);
                        if (dialog.getSelectedUser() != null) {
                            getTargetIdentity().setText(dialog.getSelectedUser());
                        }
                    } else if (st.equals(PermissionTypeComboBox.GROUP_TYPE)) {
                        GroupSearchDialog dialog = new GroupSearchDialog(application);
                        dialog.setModal(true);
                        GridApplication.getContext().showDialog(dialog);
                        if (dialog.getSelectedGroup() != null) {
                            searchGroup = dialog.getSelectedGroup();
                            getTargetIdentity().setText(searchGroup.getName());
                        }
                    }
                }
            });
        }
        return find;
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
                            search();
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

    /**
     * This method initializes permissionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPermissionsPanel() {
        if (permissionsPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.weighty = 1.0;
            gridBagConstraints7.weightx = 1.0;
            permissionsPanel = new JPanel();
            permissionsPanel.setLayout(new GridBagLayout());
            permissionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Permissions",
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                            .getPanelLabelColor()));
            permissionsPanel.add(getJScrollPane(), gridBagConstraints7);
        }
        return permissionsPanel;
    }

    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getPermissions());
        }
        return jScrollPane;
    }

    /**
     * This method initializes permissions
     * 
     * @return javax.swing.JTable
     */
    private PermissionsTable getPermissions() {
        if (permissions == null) {
            permissions = new PermissionsTable(this);
        }
        return permissions;
    }

    /**
     * This method initializes searchActionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchActionPanel() {
        if (searchActionPanel == null) {
            searchActionPanel = new JPanel();
            searchActionPanel.setLayout(new FlowLayout());
            searchActionPanel.add(getSearch(), null);
        }
        return searchActionPanel;
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
            buttonPanel.add(getModify(), null);
            buttonPanel.add(getRevoke(), null);
        }
        return buttonPanel;
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
            modify.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            modifyPermission();
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

    /**
     * This method initializes create
     * 
     * @return javax.swing.JButton
     */
    private JButton getCreate() {
        if (create == null) {
            create = new JButton();
            create.setText("Create");
            create.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            createPermission();
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

    /**
     * This method initializes revoke
     * 
     * @return javax.swing.JButton
     */
    private JButton getRevoke() {
        if (revoke == null) {
            revoke = new JButton();
            revoke.setText("Revoke");
            revoke.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            revokePermission();
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
        return revoke;
    }

}
