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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.bean.RoleSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Group;
import org.cagrid.gaards.csm.client.Permission;
import org.cagrid.gaards.csm.client.ProtectionGroup;
import org.cagrid.gaards.csm.client.Role;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.csm.CSMUIUtils;
import org.cagrid.gaards.ui.csm.groups.GroupSearchDialog;
import org.cagrid.gaards.ui.csm.protectiongroups.ProtectionGroupSearchDialog;
import org.cagrid.gaards.ui.csm.roles.RolesList;
import org.cagrid.gaards.ui.dorian.federation.UserSearchDialog;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class ModifyPermissionDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel titlePanel = null;
    private JPanel informationPanel = null;
    private JPanel rolesPanel = null;
    private JPanel buttonPanel = null;
    private JButton cancel = null;
    private ProgressPanel progress = null;
    private JLabel jLabel = null;
    private Application application;
    private Group selectedGroup;
    private PermissionTypeComboBox permissionType = null;
    private Permission permission;
    private JLabel identityLabel = null;
    private JTextField targetIdentity = null;
    private JButton findTarget = null;
    private Group targetGroup;
    private ProtectionGroup targetProtectionGroup;
    private JLabel jLabel1 = null;
    private JTextField protectionGroupName = null;
    private JButton findProtectionGroup = null;
    private JPanel grantedRolesPanel = null;
    private JPanel rolesActionPanel = null;
    private JPanel availableRolesPanel = null;
    private JButton addRole = null;
    private JButton removeRole = null;
    private JScrollPane jScrollPane = null;
    private RolesList grantedRoles = null;
    private JScrollPane jScrollPane1 = null;
    private RolesList availableRoles = null;
    private List<Role> roles;
    private boolean modified = false;
    private JButton create = null;
    private JButton remove = null;


    /**
     * @param owner
     */
    public ModifyPermissionDialog(Application application) {
        super(GridApplication.getContext().getApplication());
        this.application = application;
        initialize();
    }


    public ModifyPermissionDialog(Permission permission) {
        super(GridApplication.getContext().getApplication());
        this.permission = permission;
        initialize();
    }


    private void toggleAccess(boolean enabled) {

        getCancel().setEnabled(enabled);
        getAvailableRoles().setEnabled(enabled);
        getGrantedRoles().setEnabled(enabled);
        getAddRole().setEnabled(enabled);
        getRemoveRole().setEnabled(enabled);

        if (permission == null) {
            getPermissionType().setEnabled(enabled);
            getCreate().setEnabled(enabled);
            getRemove().setEnabled(false);
            getFindTarget().setEnabled(enabled);
            getFindProtectionGroup().setEnabled(enabled);
            String pt = getPermissionType().getPermissionType();
            if (pt.equals(PermissionTypeComboBox.USER_HOST_TYPE)) {
                getTargetIdentity().setEditable(enabled);
            } else {
                getTargetIdentity().setEditable(false);
            }
        } else {
            getPermissionType().setEditable(false);
            getTargetIdentity().setEditable(false);
            getFindTarget().setEnabled(false);
            getFindProtectionGroup().setEnabled(false);
            getCreate().setEnabled(false);
            getRemove().setEnabled(enabled);

        }
    }


    private void permissionTypeChanged() {
        if (permission == null) {
            String pt = getPermissionType().getPermissionType();
            if (pt.equals(PermissionTypeComboBox.USER_HOST_TYPE)) {
                getIdentityLabel().setText("User/Host");
                getTargetIdentity().setText("");
                getTargetIdentity().setEditable(true);
            } else if (pt.equals(PermissionTypeComboBox.GROUP_TYPE)) {
                getIdentityLabel().setText("Group");
                getTargetIdentity().setText("");
                getTargetIdentity().setEditable(false);
            }
        }

    }


    private void revokeRole() {
        toggleAccess(false);
        if (permission != null) {
            this.progress.showProgress("Revoking role...");
        }
        try {

            Role role = getGrantedRoles().getSelectedRole();
            if (role == null) {
                throw new Exception("Please select a role to revoke.");
            }

            if (permission != null) {
                if (permission.getGroup() != null) {
                    this.permission.getApplication().revokePermission(this.permission.getGroup(), role,
                        this.permission.getProtectionGroup());
                    this.permission.getRoles().remove(role);
                    modified = true;
                }
                if (permission.getUser() != null) {
                    this.permission.getApplication().revokePermission(this.permission.getUser(), role,
                        this.permission.getProtectionGroup());
                    this.permission.getRoles().remove(role);
                    modified = true;
                }
            }

            List<Role> list = getGrantedRoles().getRoles();
            list.remove(role);

            List<Role> filtered = CSMUIUtils.filterRoles(this.roles, list);
            getGrantedRoles().setRoles(list);
            getAvailableRoles().setRoles(CSMUIUtils.sortRoles(filtered));
            if (permission != null) {
                this.progress.stopProgress("Successfully revoked the role.");
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    private void grantRole() {
        toggleAccess(false);
        if (permission != null) {
            this.progress.showProgress("Granting role...");
        }
        try {

            Role role = getAvailableRoles().getSelectedRole();
            if (role == null) {
                throw new Exception("Please select an available role to grant.");
            }

            if (permission != null) {
                if (permission.getGroup() != null) {
                    this.permission.getApplication().grantPermission(this.permission.getGroup(), role,
                        this.permission.getProtectionGroup());
                    this.permission.getRoles().add(role);
                    modified = true;
                }

                if (permission.getUser() != null) {
                    this.permission.getApplication().grantPermission(this.permission.getUser(), role,
                        this.permission.getProtectionGroup());
                    this.permission.getRoles().add(role);
                    modified = true;
                }

            }

            List<Role> list = getGrantedRoles().getRoles();
            list.add(role);

            List<Role> filtered = CSMUIUtils.filterRoles(this.roles, list);
            getGrantedRoles().setRoles(list);
            getAvailableRoles().setRoles(CSMUIUtils.sortRoles(filtered));
            if (permission != null) {
                this.progress.stopProgress("Successfully granted the role.");
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    private void removePermission() {
        toggleAccess(false);
        if (permission == null) {
            toggleAccess(true);
            return;
        }
        try {
            this.progress.showProgress("Revoking permission...");
            this.permission.getApplication().revokePermission(this.permission);
            modified = true;
            this.progress.stopProgress("Successfully revoked the permission.");
            dispose();
            GridApplication.getContext().showMessage("Successfully revoked the entire permission.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    private void createPermission() {
        toggleAccess(false);
        if (permission != null) {
            toggleAccess(true);
            return;
        }
        try {
            this.progress.showProgress("Creating Permission...");
            if (targetProtectionGroup == null) {
                throw new Exception("You must specify a protection group in order to create a permission.");
            }

            List<Role> list = getGrantedRoles().getRoles();
            if (list.size() == 0) {
                throw new Exception("You must specify at least one role in order to create a permission.");
            }

            String pt = getPermissionType().getPermissionType();
            if (pt.equals(PermissionTypeComboBox.USER_HOST_TYPE)) {
                String identity = Utils.clean(getTargetIdentity().getText());
                if (identity == null) {
                    throw new Exception("Please specify the identity of the party to create the permission for.");
                }
                for (int i = 0; i < list.size(); i++) {
                    this.application.grantPermission(identity, list.get(i), targetProtectionGroup);
                }
                modified = true;
                this.progress.stopProgress("Successfully created the permission.");
                dispose();
                GridApplication.getContext().showMessage("Successfully created the permission.");
            } else if (pt.equals(PermissionTypeComboBox.GROUP_TYPE)) {
                if (targetGroup == null) {
                    throw new Exception("Please specify a group to create the permission for.");
                }
                for (int i = 0; i < list.size(); i++) {
                    this.application.grantPermission(targetGroup, list.get(i), targetProtectionGroup);
                }
                modified = true;
                this.progress.stopProgress("Successfully created the permission.");
                dispose();
                GridApplication.getContext().showMessage("Successfully created the permission.");
            }

        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    private void loadRoles() {
        toggleAccess(false);
        this.progress.showProgress("Loading roles...");
        try {

            if (permission != null) {
                this.roles = permission.getApplication().getRoles(new RoleSearchCriteria());
            } else {
                this.roles = application.getRoles(new RoleSearchCriteria());
            }

            List<Role> filtered = null;

            if (permission != null) {
                getGrantedRoles().setRoles(CSMUIUtils.sortRoles(permission.getRoles()));
                filtered = CSMUIUtils.filterRoles(this.roles, permission.getRoles());
            } else {
                getGrantedRoles().setRoles(new ArrayList<Role>());
                filtered = this.roles;
            }

            getAvailableRoles().setRoles(CSMUIUtils.sortRoles(filtered));

            this.progress.stopProgress(this.roles.size() + " role(s) found.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
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
        this.setSize(600, 500);
        this.setContentPane(getJContentPane());
        permissionTypeChanged();
        Runner runner = new Runner() {

            public void execute() {
                loadRoles();
            }
        };
        try {
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            t.getMessage();
        }
    }


    public boolean isModified() {
        return modified;
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.gridy = 5;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 4;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints2.gridy = 3;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getTitlePanel(), gridBagConstraints);
            jContentPane.add(getInformationPanel(), gridBagConstraints1);
            jContentPane.add(getRolesPanel(), gridBagConstraints2);
            jContentPane.add(getButtonPanel(), gridBagConstraints3);
            jContentPane.add(getProgress(), gridBagConstraints21);
        }
        return jContentPane;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            String prefix = "Create";
            if (permission != null) {
                prefix = "Modify";
            }
            titlePanel = new TitlePanel(prefix + " Permission",
                "A permission grants a user or group roles on a protection group.");
        }
        return titlePanel;
    }


    /**
     * This method initializes informationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInformationPanel() {
        if (informationPanel == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 2;
            gridBagConstraints12.gridy = 2;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 1;
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 2;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.anchor = GridBagConstraints.WEST;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 2;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 2;
            gridBagConstraints9.gridy = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.anchor = GridBagConstraints.WEST;
            gridBagConstraints8.weightx = 1.0D;
            gridBagConstraints8.gridy = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.gridy = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridwidth = 2;
            gridBagConstraints5.weightx = 1.0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.NONE;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridy = 0;
            informationPanel = new JPanel();
            informationPanel.setLayout(new GridBagLayout());
            informationPanel.add(getIdentityLabel(), gridBagConstraints7);
            informationPanel.add(getTargetIdentity(), gridBagConstraints8);
            informationPanel.add(getJLabel1(), gridBagConstraints10);
            informationPanel.add(getProtectionGroupName(), gridBagConstraints11);
            if (permission == null) {
                informationPanel.add(getJLabel(), gridBagConstraints4);
                informationPanel.add(getPermissionType(), gridBagConstraints5);
                informationPanel.add(getFindTarget(), gridBagConstraints9);
                informationPanel.add(getFindProtectionGroup(), gridBagConstraints12);

            }
        }
        return informationPanel;
    }


    /**
     * This method initializes rolesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRolesPanel() {
        if (rolesPanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.fill = GridBagConstraints.BOTH;
            gridBagConstraints14.gridy = 0;
            gridBagConstraints14.weightx = 1.0D;
            gridBagConstraints14.weighty = 1.0D;
            gridBagConstraints14.gridx = 2;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints13.gridy = 0;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.weighty = 1.0D;
            gridBagConstraints6.gridx = 0;
            rolesPanel = new JPanel();
            rolesPanel.setLayout(new GridBagLayout());
            rolesPanel.setBorder(BorderFactory.createTitledBorder(null, "Roles", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            rolesPanel.add(getGrantedRolesPanel(), gridBagConstraints6);
            rolesPanel.add(getRolesActionPanel(), gridBagConstraints13);
            rolesPanel.add(getAvailableRolesPanel(), gridBagConstraints14);
        }
        return rolesPanel;
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
            if (permission == null) {
                buttonPanel.add(getCreate(), null);
            } else {
                buttonPanel.add(getRemove(), null);
            }
            buttonPanel.add(getCancel(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes cancel
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancel() {
        if (cancel == null) {
            cancel = new JButton();
            if (permission != null) {
                cancel.setText("Close");
            } else {
                cancel.setText("Cancel");
            }
            cancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return cancel;
    }


    /**
     * This method initializes progress
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgress() {
        if (progress == null) {
            progress = new ProgressPanel();
        }
        return progress;
    }


    /**
     * This method initializes jLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new JLabel();
            jLabel.setText("Permission Type");
        }
        return jLabel;
    }


    public Group getSelectedGroup() {
        return selectedGroup;
    }


    /**
     * This method initializes permissionType
     * 
     * @return javax.swing.JComboBox
     */
    private PermissionTypeComboBox getPermissionType() {
        if (permissionType == null) {
            permissionType = new PermissionTypeComboBox();
            permissionType.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    permissionTypeChanged();
                }
            });
            if (permission != null) {
                if (permission.getGroup() != null) {
                    getIdentityLabel().setText("Group");
                    permissionType.setSelectedItem(PermissionTypeComboBox.GROUP_TYPE);
                } else {
                    getIdentityLabel().setText("User/Host");
                    permissionType.setSelectedItem(PermissionTypeComboBox.USER_HOST_TYPE);
                }
                permissionType.setEnabled(false);

            }
        }
        return permissionType;
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


    /**
     * This method initializes targetIdentity
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTargetIdentity() {
        if (targetIdentity == null) {
            targetIdentity = new JTextField();
            if (permission != null) {
                if (permission.getGroup() != null) {
                    targetIdentity.setText(permission.getGroup().getName());
                    targetIdentity.setToolTipText(permission.getGroup().getDescription());
                } else {
                    targetIdentity.setText(permission.getUser());
                }
                targetIdentity.setEditable(false);
            }
        }
        return targetIdentity;
    }


    /**
     * This method initializes findTarget
     * 
     * @return javax.swing.JButton
     */
    private JButton getFindTarget() {
        if (findTarget == null) {
            findTarget = new JButton();
            findTarget.setText("Find...");
            findTarget.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String pt = getPermissionType().getPermissionType();
                    if (pt.equals(PermissionTypeComboBox.USER_HOST_TYPE)) {
                        UserSearchDialog dialog = new UserSearchDialog();
                        dialog.setModal(true);
                        GridApplication.getContext().showDialog(dialog);
                        if (dialog.getSelectedUser() != null) {
                            getTargetIdentity().setText(dialog.getSelectedUser());
                            getTargetIdentity().setToolTipText("");
                        }
                    } else if (pt.equals(PermissionTypeComboBox.GROUP_TYPE)) {
                        GroupSearchDialog dialog = new GroupSearchDialog(application);
                        dialog.setModal(true);
                        GridApplication.getContext().showDialog(dialog);
                        if (dialog.getSelectedGroup() != null) {
                            targetGroup = dialog.getSelectedGroup();
                            getTargetIdentity().setText(targetGroup.getName());
                            getTargetIdentity().setToolTipText(targetGroup.getDescription());
                        }
                    }
                }
            });
            if (permission != null) {
                findTarget.setEnabled(false);

            }
        }
        return findTarget;
    }


    /**
     * This method initializes jLabel1
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getJLabel1() {
        if (jLabel1 == null) {
            jLabel1 = new JLabel();
            jLabel1.setText("Protection Group");
        }
        return jLabel1;
    }


    /**
     * This method initializes protectionGroupName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionGroupName() {
        if (protectionGroupName == null) {
            protectionGroupName = new JTextField();
            if (permission != null) {
                protectionGroupName.setText(permission.getProtectionGroup().getName());
                protectionGroupName.setToolTipText(permission.getProtectionGroup().getDescription());
            }
            protectionGroupName.setEditable(false);
        }
        return protectionGroupName;
    }


    /**
     * This method initializes findProtectionGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getFindProtectionGroup() {
        if (findProtectionGroup == null) {
            findProtectionGroup = new JButton();
            findProtectionGroup.setText("Find...");
            findProtectionGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ProtectionGroupSearchDialog dialog = new ProtectionGroupSearchDialog(application);
                    dialog.setModal(true);
                    GridApplication.getContext().showDialog(dialog);
                    if (dialog.getSelectedProtectionGroup() != null) {
                        targetProtectionGroup = dialog.getSelectedProtectionGroup();
                        getProtectionGroupName().setText(targetProtectionGroup.getName());
                        getProtectionGroupName().setToolTipText(targetProtectionGroup.getDescription());
                    }
                }
            });
            if (permission != null) {
                findProtectionGroup.setEnabled(false);
            }
        }
        return findProtectionGroup;
    }


    /**
     * This method initializes grantedRolesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGrantedRolesPanel() {
        if (grantedRolesPanel == null) {
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.fill = GridBagConstraints.BOTH;
            gridBagConstraints17.weighty = 1.0;
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.gridy = 0;
            gridBagConstraints17.weightx = 1.0;
            grantedRolesPanel = new JPanel();
            grantedRolesPanel.setLayout(new GridBagLayout());
            grantedRolesPanel.setBorder(BorderFactory.createTitledBorder(null, "Granted Roles",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            grantedRolesPanel.add(getJScrollPane(), gridBagConstraints17);
        }
        return grantedRolesPanel;
    }


    /**
     * This method initializes rolesActionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRolesActionPanel() {
        if (rolesActionPanel == null) {
            GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
            gridBagConstraints16.gridx = 0;
            gridBagConstraints16.gridy = 0;
            GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
            gridBagConstraints15.gridx = 0;
            gridBagConstraints15.gridy = 1;
            rolesActionPanel = new JPanel();
            rolesActionPanel.setLayout(new GridBagLayout());
            rolesActionPanel.add(getAddRole(), gridBagConstraints16);
            rolesActionPanel.add(getRemoveRole(), gridBagConstraints15);
        }
        return rolesActionPanel;
    }


    /**
     * This method initializes availableRolesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAvailableRolesPanel() {
        if (availableRolesPanel == null) {
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.fill = GridBagConstraints.BOTH;
            gridBagConstraints18.weighty = 1.0;
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.gridy = 0;
            gridBagConstraints18.weightx = 1.0;
            availableRolesPanel = new JPanel();
            availableRolesPanel.setLayout(new GridBagLayout());
            availableRolesPanel.setBorder(BorderFactory.createTitledBorder(null, "Available Roles",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            availableRolesPanel.add(getJScrollPane1(), gridBagConstraints18);
        }
        return availableRolesPanel;
    }


    /**
     * This method initializes addRole
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddRole() {
        if (addRole == null) {
            addRole = new JButton();
            addRole.setText("<<");
            addRole.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            grantRole();
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
        return addRole;
    }


    /**
     * This method initializes removeRole
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveRole() {
        if (removeRole == null) {
            removeRole = new JButton();
            removeRole.setText(">>");
            removeRole.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            revokeRole();
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
        return removeRole;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getGrantedRoles());
        }
        return jScrollPane;
    }


    /**
     * This method initializes grantedRoles
     * 
     * @return javax.swing.JList
     */
    private RolesList getGrantedRoles() {
        if (grantedRoles == null) {
            grantedRoles = new RolesList();
        }
        return grantedRoles;
    }


    /**
     * This method initializes jScrollPane1
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setViewportView(getAvailableRoles());
        }
        return jScrollPane1;
    }


    /**
     * This method initializes availableRoles
     * 
     * @return javax.swing.JList
     */
    private RolesList getAvailableRoles() {
        if (availableRoles == null) {
            availableRoles = new RolesList();
        }
        return availableRoles;
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
     * This method initializes remove
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemove() {
        if (remove == null) {
            remove = new JButton();
            remove.setText("Revoke All");
            remove.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            removePermission();
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
        return remove;
    }

}
