package org.cagrid.gaards.ui.csm.roles;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.bean.PrivilegeSearchCriteria;
import org.cagrid.gaards.csm.bean.RoleSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Privilege;
import org.cagrid.gaards.csm.client.Role;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

public class ManageRolesPanel extends JPanel {
	private Application csmApp;
	private static final long serialVersionUID = 1L;
	private JPanel rolesPanel = null;
	private JPanel rolePanel = null;
	private JPanel buttonPanel = null;
	private JPanel usedPrivilagesPanel = null;
	private RolesList rolesList = null;
	private JButton addPrivilege = null;
	private JButton removePrivilegeButton = null;
	private PrivilegesList availablePrivilegesList = null;
	private PrivilegesList usedPrivilegesList = null;
	private JButton addRoleButton = null;
	private JPanel availablePrivilegesPanel = null;
	private JPanel searchRolesPanel = null;
	private JTextField nameSearchTextField = null;
	private JButton searchButton = null;
	private ProgressPanel progress = null;
	private JPanel rolesListPanel = null;
	private JScrollPane jScrollPane = null;
	private JSplitPane jSplitPane = null;
	private JPanel roleButtonPanel = null;
	private JButton removeRole = null;
	private JPanel privileges = null;
	private JScrollPane jScrollPane1 = null;
	private JScrollPane jScrollPane2 = null;
	private JPanel roleOverview = null;
	private JLabel jLabel = null;
	private JTextField roleId = null;
	private JLabel jLabel1 = null;
	private JTextField roleName = null;
	private JLabel jLabel2 = null;
	private JTextField description = null;
	private JLabel jLabel3 = null;
	private JTextField lastUpdated = null;

	private Role currentRole;
	private JButton modify = null;
	private boolean successfulSearch = false;

	/**
	 * This is the default constructor
	 */
	public ManageRolesPanel(Application app, ProgressPanel progress) {
		super();
		this.csmApp = app;
		this.progress = progress;
		initialize();
		clearRole();
	}

	private void disableAll() {
		setAllEnabled(false);
	}

	private void enableAll() {
		setAllEnabled(true);
	}

	private void clearRole() {
		currentRole = null;
		this.getRoleId().setText("");
		this.getRoleName().setText("");
		this.getDescription().setText("");
		this.getLastUpdated().setText("");
		getUsedPrivilegesList().clearAllPrivileges();
		getAvailablePrivilegesList().clearAllPrivileges();
		enableRoleModification(false);
	}

	private void enableRoleModification(boolean enabled) {
		if (currentRole == null) {
			getAddPrivilege().setEnabled(false);
			getRemovePrivilegeButton().setEnabled(false);
			this.getRoleId().setEnabled(false);
			this.getRoleName().setEnabled(false);
			this.getDescription().setEnabled(false);
			this.getLastUpdated().setEnabled(false);
			this.getModify().setEnabled(false);
		} else {
			getAddPrivilege().setEnabled(enabled);
			getRemovePrivilegeButton().setEnabled(enabled);
			this.getRoleId().setEnabled(enabled);
			this.getRoleName().setEnabled(enabled);
			this.getDescription().setEnabled(enabled);
			this.getLastUpdated().setEnabled(enabled);
			this.getModify().setEnabled(enabled);
		}
	}

	protected void setRole(Role role) {

		try {
			disableAll();
			progress.showProgress("Loading privileges....");

			clearRole();
			List<Privilege> privs = this.csmApp.getCSM().getPrivileges(
					new PrivilegeSearchCriteria());
			List<Privilege> rolePrivs = role.getPrivileges();
			this.getRoleId().setText(String.valueOf(role.getId()));
			this.getRoleName().setText(String.valueOf(role.getName()));
			this.getDescription()
					.setText(String.valueOf(role.getDescription()));
			Calendar c = role.getLastUpdated();
			if (c != null) {
				getLastUpdated().setText(
						DateFormat.getDateInstance().format(c.getTime()));
			}
			getUsedPrivilegesList().setPrivileges(rolePrivs);
			getAvailablePrivilegesList().setDifference(privs, rolePrivs);
			currentRole = role;
			progress.stopProgress("Successfully loaded privileges.");
		} catch (Exception ex) {
			ErrorDialog.showError(ex);
			progress.stopProgress("Error.");
		} finally {
			enableAll();
		}
	}

	private void setAllEnabled(boolean enabled) {
		enableRoleModification(enabled);
		getAvailablePrivilegesList().setEnabled(enabled);
		getUsedPrivilegesList().setEnabled(enabled);
		getRolesList().setEnabled(enabled);
		getAddRoleButton().setEnabled(enabled);
		getNameSearchTextField().setEnabled(enabled);
		getSearchButton().setEnabled(enabled);
		getRemoveRole().setEnabled(enabled);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
		gridBagConstraints15.fill = GridBagConstraints.BOTH;
		gridBagConstraints15.weighty = 1.0;
		gridBagConstraints15.gridx = 0;
		gridBagConstraints15.gridy = 0;
		gridBagConstraints15.weightx = 1.0;
		this.setSize(700, 500);
		this.setLayout(new GridBagLayout());
		this.add(getJSplitPane(), gridBagConstraints15);
	}

	/**
	 * This method initializes rolesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRolesPanel() {
		if (rolesPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 2;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.weighty = 1.0D;
			gridBagConstraints9.gridy = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.weightx = 1.0D;
			gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints13.gridy = 0;
			rolesPanel = new JPanel();
			rolesPanel.setLayout(new GridBagLayout());
			rolesPanel.add(getSearchRolesPanel(), gridBagConstraints13);
			rolesPanel.add(getRolesListPanel(), gridBagConstraints9);
			rolesPanel.add(getRoleButtonPanel(), gridBagConstraints1);
		}
		return rolesPanel;
	}

	/**
	 * This method initializes rolePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRolePanel() {
		if (rolePanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridy = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.weightx = 1.0D;
			gridBagConstraints4.weighty = 1.0D;
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 1;
			rolePanel = new JPanel();
			rolePanel.setLayout(new GridBagLayout());
			rolePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, "Role",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					LookAndFeel.getPanelLabelColor()));
			rolePanel.add(getPrivileges(), gridBagConstraints4);
			rolePanel.add(getRoleOverview(), gridBagConstraints7);
		}
		return rolePanel;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 1;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getAddPrivilege(), new GridBagConstraints());
			buttonPanel.add(getRemovePrivilegeButton(), gridBagConstraints5);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes usedPrivilagesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUsedPrivilagesPanel() {
		if (usedPrivilagesPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints6.weightx = 1.0;
			usedPrivilagesPanel = new JPanel();
			usedPrivilagesPanel.setLayout(new GridBagLayout());
			usedPrivilagesPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Privileges in Role",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			usedPrivilagesPanel.add(getJScrollPane2(), gridBagConstraints6);
		}
		return usedPrivilagesPanel;
	}

	/**
	 * This method initializes rolesList
	 * 
	 * @return javax.swing.JList
	 */
	private RolesList getRolesList() {
		if (rolesList == null) {
			rolesList = new RolesList();
			rolesList.addListSelectionListener(new RoleListener(this));
		}
		return rolesList;
	}

	/**
	 * This method initializes addPrivilege
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddPrivilege() {
		if (addPrivilege == null) {
			addPrivilege = new JButton();
			addPrivilege.setText("<<");
			addPrivilege.setToolTipText("Add priviledge to role");
			addPrivilege.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					if (getRolesList().getSelectedValue() != null) {
						disableAll();
						Runner runner = new Runner() {

							public void execute() {
								try {
									progress
											.showProgress("Adding privilege(s) to role...");
									Role role = currentRole;
									List<Privilege> selected = getAvailablePrivilegesList()
											.getSelectPrivileges();
									List<Privilege> privs = getUsedPrivilegesList()
											.getPrivileges();
									for (int i = 0; i < selected.size(); i++) {
										privs.add(selected.get(i));
									}
									role.setPrivileges(privs);

									List<Privilege> allPrivs = csmApp
											.getCSM()
											.getPrivileges(
													new PrivilegeSearchCriteria());
									List<Privilege> rolePrivs = role
											.getPrivileges();
									getUsedPrivilegesList().setPrivileges(
											rolePrivs);
									getAvailablePrivilegesList().setDifference(
											allPrivs, rolePrivs);
									progress.stopProgress("Successfully added "
											+ selected.size()
											+ " privilege(s) to the role.");
								} catch (Exception ex) {
									ErrorDialog.showError(ex);
									progress.stopProgress("Error");
								} finally {
									enableAll();
								}

							}
						};
						try {
							GridApplication.getContext().executeInBackground(
									runner);
						} catch (Exception t) {
							t.getMessage();
						}
					}

				}

			});
		}
		return addPrivilege;
	}

	/**
	 * This method initializes removePrivilegeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemovePrivilegeButton() {
		if (removePrivilegeButton == null) {
			removePrivilegeButton = new JButton();
			removePrivilegeButton.setText(">>");
			removePrivilegeButton.setToolTipText("Remove privilege from role.");
			removePrivilegeButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							if (getRolesList().getSelectedValue() != null) {
								disableAll();
								Runner runner = new Runner() {

									public void execute() {
										try {
											progress
													.showProgress("Removing privilege(s) from role...");

											Role role = currentRole;
											List<Privilege> selected = getUsedPrivilegesList()
													.getSelectPrivileges();
											List<Privilege> privs = getUsedPrivilegesList()
													.getPrivileges();
											for (int i = 0; i < selected.size(); i++) {
												privs.remove(selected.get(i));
											}
											role.setPrivileges(privs);
											List<Privilege> allPrivs = csmApp
													.getCSM()
													.getPrivileges(
															new PrivilegeSearchCriteria());
											List<Privilege> rolePrivs = role
													.getPrivileges();
											getUsedPrivilegesList()
													.setPrivileges(rolePrivs);
											getAvailablePrivilegesList()
													.setDifference(allPrivs,
															rolePrivs);
											progress
													.stopProgress("Successfully removed "
															+ selected.size()
															+ " privilege(s) from the role.");

										} catch (Exception ex) {
											ErrorDialog.showError(ex);
											progress.stopProgress("Error");
										} finally {
											enableAll();
										}
									}
								};
								try {
									GridApplication.getContext()
											.executeInBackground(runner);
								} catch (Exception t) {
									t.getMessage();
								}

							}

						}

					});
		}
		return removePrivilegeButton;
	}

	/**
	 * This method initializes availablePrivilegesList
	 * 
	 * @return javax.swing.JList
	 */
	private PrivilegesList getAvailablePrivilegesList() {
		if (availablePrivilegesList == null) {
			availablePrivilegesList = new PrivilegesList();
			availablePrivilegesList.setPreferredSize(new Dimension(200, 200));
		}
		return availablePrivilegesList;
	}

	/**
	 * This method initializes usedPrivilegesList
	 * 
	 * @return javax.swing.JList
	 */
	private PrivilegesList getUsedPrivilegesList() {
		if (usedPrivilegesList == null) {
			usedPrivilegesList = new PrivilegesList();
			usedPrivilegesList.setPreferredSize(new Dimension(200, 200));
		}
		return usedPrivilegesList;
	}

	/**
	 * This method initializes addRoleButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddRoleButton() {
		if (addRoleButton == null) {
			addRoleButton = new JButton();
			addRoleButton.setText("Add Role");
			addRoleButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							CreateRoleWindow window = new CreateRoleWindow(
									csmApp);
							window.setModal(true);
							GridApplication.getContext().showDialog(window);
							if (window.wasRoleCreated() && (successfulSearch)) {
								disableAll();
								Runner runner = new Runner() {
									public void execute() {
										roleSearch();
									}
								};
								try {
									GridApplication.getContext()
											.executeInBackground(runner);
								} catch (Exception t) {
									t.getMessage();
								}
							}

						}

					});
		}
		return addRoleButton;
	}

	/**
	 * This method initializes availablePrivilegesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAvailablePrivilegesPanel() {
		if (availablePrivilegesPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.BOTH;
			gridBagConstraints10.weighty = 1.0;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.weightx = 1.0;
			availablePrivilegesPanel = new JPanel();
			availablePrivilegesPanel.setLayout(new GridBagLayout());
			availablePrivilegesPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Available Privileges",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			availablePrivilegesPanel.add(getJScrollPane1(),
					gridBagConstraints10);
		}
		return availablePrivilegesPanel;
	}

	/**
	 * This method initializes searchRolesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSearchRolesPanel() {
		if (searchRolesPanel == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.gridy = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.weightx = 1.0;
			searchRolesPanel = new JPanel();
			searchRolesPanel.setLayout(new GridBagLayout());
			searchRolesPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Role Search",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			searchRolesPanel
					.add(getNameSearchTextField(), gridBagConstraints12);
			searchRolesPanel.add(getSearchButton(), gridBagConstraints14);
		}
		return searchRolesPanel;
	}

	/**
	 * This method initializes nameSearchTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNameSearchTextField() {
		if (nameSearchTextField == null) {
			nameSearchTextField = new JTextField();
		}
		return nameSearchTextField;
	}

	/**
	 * This method initializes searchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setText("Search");
			searchButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					disableAll();
					Runner runner = new Runner() {
						public void execute() {
							roleSearch();
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}
		return searchButton;
	}

	private void roleSearch() {
		try {
			progress.showProgress("Searching for roles...");
			clearRole();
			getRolesList().clearRoles();
			RoleSearchCriteria crit = new RoleSearchCriteria();
			crit.setApplicationId(csmApp.getId());
			crit.setName(Utils.clean(getNameSearchTextField().getText()));

			List<Role> roles = csmApp.getRoles(crit);
			getRolesList().setRoles(roles);
			successfulSearch = true;
			progress.stopProgress(roles.size() + " Roles Found.");

		} catch (Exception ex) {
			ErrorDialog.showError(ex);
			successfulSearch = false;
			progress.stopProgress("Error");
		} finally {
			enableAll();
		}
	}

	private void modifyRole() {
		try {
			progress.showProgress("Modifying the role...");
			this.currentRole.setName(Utils.clean(getRoleName().getText()));
			this.currentRole.setDescription(Utils.clean(getDescription()
					.getText()));
			this.currentRole.modify();
			progress.stopProgress("Successfully modified the role.");
		} catch (Exception ex) {
			ErrorDialog.showError(ex);
			progress.stopProgress("Error");
		} finally {
			enableAll();
		}
	}

	/**
	 * This method initializes rolesListPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRolesListPanel() {
		if (rolesListPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.weightx = 1.0;
			rolesListPanel = new JPanel();
			rolesListPanel
					.setBorder(javax.swing.BorderFactory
							.createTitledBorder(
									null,
									"Roles",
									javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
									javax.swing.border.TitledBorder.DEFAULT_POSITION,
									null, LookAndFeel.getPanelLabelColor()));
			rolesListPanel.setLayout(new GridBagLayout());
			rolesListPanel.add(getJScrollPane(), gridBagConstraints11);
		}
		return rolesListPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getRolesList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setDividerLocation(300);
			jSplitPane.setLeftComponent(getRolesPanel());
			jSplitPane.setRightComponent(getRolePanel());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes roleButtonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRoleButtonPanel() {
		if (roleButtonPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = -1;
			gridBagConstraints.gridy = -1;
			roleButtonPanel = new JPanel();
			roleButtonPanel.setLayout(new FlowLayout());
			roleButtonPanel.add(getAddRoleButton(), null);
			roleButtonPanel.add(getRemoveRole(), null);
		}
		return roleButtonPanel;
	}

	/**
	 * This method initializes removeRole
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveRole() {
		if (removeRole == null) {
			removeRole = new JButton();
			removeRole.setText("Remove Role");
			removeRole.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					disableAll();
					Runner runner = new Runner() {
						public void execute() {
							removeRole();
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}
				}
			});
		}
		return removeRole;
	}

	private void removeRole() {
		this.progress.showProgress("Removing role...");
		try {
			Role r = (Role) this.getRolesList().getSelectedValue();
			csmApp.removeRole(r);
			roleSearch();
			this.progress.stopProgress("Successfully removed the role.");
		} catch (Exception e) {
			ErrorDialog.showError(e);
			this.progress.stopProgress("Error");
		} finally {
			enableAll();
		}
	}

	/**
	 * This method initializes privileges
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPrivileges() {
		if (privileges == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 0.0D;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0D;
			gridBagConstraints3.weighty = 1.0D;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.gridx = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.weighty = 1.0D;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridx = 2;
			privileges = new JPanel();
			privileges.setLayout(new GridBagLayout());
			privileges.add(getAvailablePrivilegesPanel(), gridBagConstraints8);
			privileges.add(getUsedPrivilagesPanel(), gridBagConstraints3);
			privileges.add(getButtonPanel(), gridBagConstraints2);
		}
		return privileges;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getAvailablePrivilegesList());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jScrollPane2
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getUsedPrivilegesList());
		}
		return jScrollPane2;
	}

	/**
	 * This method initializes roleOverview
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRoleOverview() {
		if (roleOverview == null) {
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.gridx = 0;
			gridBagConstraints24.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints24.gridwidth = 2;
			gridBagConstraints24.gridy = 4;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.gridy = 3;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints23.gridx = 1;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.anchor = GridBagConstraints.WEST;
			gridBagConstraints22.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints22.gridy = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("Last Updated");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.gridy = 2;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.anchor = GridBagConstraints.WEST;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.gridx = 0;
			gridBagConstraints20.anchor = GridBagConstraints.WEST;
			gridBagConstraints20.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints20.gridy = 2;
			jLabel2 = new JLabel();
			jLabel2.setText("Description");
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridy = 1;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints19.gridx = 1;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Name");
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.gridy = 0;
			gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints17.weightx = 1.0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints16.gridx = 0;
			jLabel = new JLabel();
			jLabel.setText("Id");
			roleOverview = new JPanel();
			roleOverview.setLayout(new GridBagLayout());
			roleOverview.add(jLabel, gridBagConstraints16);
			roleOverview.add(getRoleId(), gridBagConstraints17);
			roleOverview.add(jLabel1, gridBagConstraints18);
			roleOverview.add(getRoleName(), gridBagConstraints19);
			roleOverview.add(jLabel2, gridBagConstraints20);
			roleOverview.add(getDescription(), gridBagConstraints21);
			roleOverview.add(jLabel3, gridBagConstraints22);
			roleOverview.add(getLastUpdated(), gridBagConstraints23);
			roleOverview.add(getModify(), gridBagConstraints24);
		}
		return roleOverview;
	}

	/**
	 * This method initializes roleId
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRoleId() {
		if (roleId == null) {
			roleId = new JTextField();
			roleId.setEditable(false);
		}
		return roleId;
	}

	/**
	 * This method initializes roleName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRoleName() {
		if (roleName == null) {
			roleName = new JTextField();
		}
		return roleName;
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
					disableAll();
					Runner runner = new Runner() {
						public void execute() {
							modifyRole();
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}
		return modify;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
