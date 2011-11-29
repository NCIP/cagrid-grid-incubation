package org.cagrid.gaards.ui.csm.associations;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.bean.GroupSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria;
import org.cagrid.gaards.csm.bean.RoleSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Group;
import org.cagrid.gaards.csm.client.ProtectionGroup;
import org.cagrid.gaards.csm.client.Role;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.csm.groups.GroupEntry;
import org.cagrid.gaards.ui.csm.groups.GroupsList;
import org.cagrid.gaards.ui.csm.protectiongroups.ProtectionGroupList;
import org.cagrid.gaards.ui.csm.roles.RolesList;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class CreateAssociationsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Application csmApp = null;

	private ProgressPanel progress = null;

	private JPanel rolesPanel = null;

	private JPanel protectionGroupPanel = null;

	private JPanel userGroupsPanel = null;

	private JPanel createAssociationPanel = null;

	private JButton addAssociationButton = null;

	private JScrollPane groupsScrollPane = null;

	private GroupsList groupsList = null;

	private DefaultListModel groupsModel = null;

	private JPanel groupsSearchPanel = null;

	private JLabel groupName = null;

	private JTextField groupNameText = null;

	private JButton groupSearchButton = null;

	private JScrollPane pgScrollPane = null;

	private ProtectionGroupList pgList = null;

	private DefaultListModel groupsModel1 = null;

	private JScrollPane rolesScrollPane = null;

	private RolesList rolesList = null;

	private DefaultListModel groupsModel2 = null;

	private JPanel pgSearchPanel = null;

	private JLabel pgName = null;

	private JTextField pgNameText = null;

	private JButton pgSearchButton = null;

	private JPanel rolesSearchPanel = null;

	private JLabel roleName = null;

	private JTextField roleNameText = null;

	private JButton searchRolesButton = null;

	private JPanel infoPanel = null;

	private JLabel infoLabel = null;

	private JPanel userPanel = null;

	private JPanel groupsPanel = null;

	private JLabel userNameLabel = null;

	private JTextField userNameText = null;

	private JPanel wrapperPanel = null;

	/**
	 * This is the default constructor
	 */
	public CreateAssociationsPanel(Application app, ProgressPanel progress) {
		super();
		this.csmApp = app;
		this.progress = progress;
		initialize();

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
		gridBagConstraints17.gridx = 0;
		gridBagConstraints17.fill = GridBagConstraints.BOTH;
		gridBagConstraints17.weighty = 1.0D;
		gridBagConstraints17.weightx = 1.0D;
		gridBagConstraints17.gridy = 0;
		this.setSize(535, 405);

		this.setLayout(new GridBagLayout());
		this.add(getWrapperPanel(), gridBagConstraints17);
	}

	/**
	 * This method initializes rolesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRolesPanel() {
		if (rolesPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.gridy = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.weightx = 1.0;
			rolesPanel = new JPanel();
			rolesPanel.setLayout(new GridBagLayout());
			rolesPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Roles", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			rolesPanel.add(getRolesScrollPane(), gridBagConstraints7);
			rolesPanel.add(getRolesSearchPanel(), gridBagConstraints8);
		}
		return rolesPanel;
	}

	/**
	 * This method initializes protectionGroupPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getProtectionGroupPanel() {
		if (protectionGroupPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			protectionGroupPanel = new JPanel();
			protectionGroupPanel.setLayout(new GridBagLayout());
			protectionGroupPanel.setBorder(BorderFactory.createTitledBorder(
					null, "Protection Groups",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			protectionGroupPanel.add(getPgScrollPane(), gridBagConstraints6);
			protectionGroupPanel.add(getPgSearchPanel(), gridBagConstraints9);
		}
		return protectionGroupPanel;
	}

	/**
	 * This method initializes userGroupsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUserGroupsPanel() {
		if (userGroupsPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.weightx = 1.0D;
			gridBagConstraints13.weighty = 1.0D;
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.gridy = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.weighty = 0.0D;
			gridBagConstraints12.weightx = 1.0D;
			gridBagConstraints12.gridy = 0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = -1;
			gridBagConstraints11.gridwidth = 3;
			gridBagConstraints11.gridy = -1;
			userGroupsPanel = new JPanel();
			userGroupsPanel.setLayout(new GridBagLayout());
			userGroupsPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Users or Groups", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			userGroupsPanel.add(getUserPanel(), gridBagConstraints12);
			userGroupsPanel.add(getGroupsPanel(), gridBagConstraints13);
		}
		return userGroupsPanel;
	}

	/**
	 * This method initializes createAssociationPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCreateAssociationPanel() {
		if (createAssociationPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 0;
			createAssociationPanel = new JPanel();
			createAssociationPanel.setLayout(new GridBagLayout());
			createAssociationPanel.setBorder(BorderFactory.createTitledBorder(
					null, "Create Association",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			createAssociationPanel.add(getAddAssociationButton(),
					gridBagConstraints10);
		}
		return createAssociationPanel;
	}

	/**
	 * This method initializes addAssociationButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddAssociationButton() {
		if (addAssociationButton == null) {
			addAssociationButton = new JButton();
			addAssociationButton.setText("create");
			addAssociationButton.setEnabled(false);
			addAssociationButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Runner runner = new Runner() {
								public void execute() {
									try {
										progress
												.showProgress("Creating association...");
										SwingUtilities
												.invokeLater(new Runnable() {

													public void run() {
														setEnableSelection(false);

													}
												});
										if (groupsList.getSelectedValue() != null) {
											Object[] roles = getRolesList().getSelectedValues();
											for (int i = 0; i < roles.length; i++) {
												csmApp
												.grantPermission(
														((GroupEntry) groupsList
																.getSelectedValue())
																.getGroup(),
														(Role) roles[i],
														(ProtectionGroup) getPgList()
																.getSelectedValue());
											}
											
										} else {
											Object[] roles = getRolesList().getSelectedValues();
											for (int i = 0; i < roles.length; i++) {
												csmApp
												.grantPermission(
														getUserNameText()
																.getText(),
														(Role) roles[i],
														(ProtectionGroup) getPgList()
																.getSelectedValue());
											}
											
										}
										progress
												.stopProgress("Association created.");
									} catch (Exception e) {
										ErrorDialog.showError(e);
										progress
												.stopProgress("Error creating association");

									} finally {
										SwingUtilities
												.invokeLater(new Runnable() {

													public void run() {
														setEnableSelection(true);
														clearSelections();
														validateInput();
													}
												});
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
					});
		}
		return addAssociationButton;
	}

	/**
	 * This method initializes groupsScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getGroupsScrollPane() {
		if (groupsScrollPane == null) {
			groupsScrollPane = new JScrollPane();
			groupsScrollPane.setViewportView(getGroupsList());
		}
		return groupsScrollPane;
	}

	/**
	 * This method initializes groupsList
	 * 
	 * @return javax.swing.JList
	 */
	private GroupsList getGroupsList() {
		if (groupsList == null) {
			groupsList = new GroupsList();
			groupsList.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					if (getGroupsList().getSelectedValue() != null) {
						getUserNameText().setText("");
					}
					validateInput();
				}

			});
		}
		return groupsList;
	}

	/**
	 * This method initializes groupsSearchPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGroupsSearchPanel() {
		if (groupsSearchPanel == null) {
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridy = 1;
			gridBagConstraints31.gridwidth = 2;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			groupName = new JLabel();
			groupName.setText("Name");
			groupsSearchPanel = new JPanel();
			groupsSearchPanel.setLayout(new GridBagLayout());
			groupsSearchPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Group Search", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			groupsSearchPanel.add(groupName, new GridBagConstraints());
			groupsSearchPanel.add(getGroupNameText(), gridBagConstraints21);
			groupsSearchPanel.add(getGroupSearchButton(), gridBagConstraints31);
		}
		return groupsSearchPanel;
	}

	/**
	 * This method initializes groupNameText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getGroupNameText() {
		if (groupNameText == null) {
			groupNameText = new JTextField();
			groupNameText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					validateInput();
				}
			});
		}
		return groupNameText;
	}

	/**
	 * This method initializes groupSearchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getGroupSearchButton() {
		if (groupSearchButton == null) {
			groupSearchButton = new JButton();
			groupSearchButton.setText("Search");
			groupSearchButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Runner runner = new Runner() {
								public void execute() {
									try {
										progress.showProgress("Searching...");
										GroupSearchCriteria search = new GroupSearchCriteria();
										search.setApplicationId(csmApp.getId());
										search.setName(Utils
												.clean(getGroupNameText()
														.getText()));
										List<Group> list;

										list = csmApp.getGroups(search);
										getGroupsList().setGroups(list);
										progress.stopProgress(list.size()
												+ " groups(s) found.");
									} catch (Exception e) {
										ErrorDialog.showError(e);
										progress
												.stopProgress("Error searching groups");

									} finally {
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
					});
		}
		return groupSearchButton;
	}

	/**
	 * This method initializes pgScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getPgScrollPane() {
		if (pgScrollPane == null) {
			pgScrollPane = new JScrollPane();
			pgScrollPane.setViewportView(getPgList());
		}
		return pgScrollPane;
	}

	/**
	 * This method initializes pgList
	 * 
	 * @return javax.swing.JList
	 */
	private ProtectionGroupList getPgList() {
		if (pgList == null) {
			pgList = new ProtectionGroupList();
			pgList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			pgList.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					validateInput();
				}

			});
		}
		return pgList;
	}

	/**
	 * This method initializes rolesScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getRolesScrollPane() {
		if (rolesScrollPane == null) {
			rolesScrollPane = new JScrollPane();
			rolesScrollPane.setViewportView(getRolesList());
		}
		return rolesScrollPane;
	}

	/**
	 * This method initializes rolesList
	 * 
	 * @return javax.swing.JList
	 */
	private RolesList getRolesList() {
		if (rolesList == null) {
			rolesList = new RolesList();
			rolesList
					.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			rolesList.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					validateInput();
				}

			});
		}
		return rolesList;
	}

	/**
	 * This method initializes pgSearchPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPgSearchPanel() {
		if (pgSearchPanel == null) {
			GridBagConstraints gridBagConstraints311 = new GridBagConstraints();
			gridBagConstraints311.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints311.gridx = 0;
			gridBagConstraints311.gridy = 1;
			gridBagConstraints311.gridwidth = 2;
			GridBagConstraints gridBagConstraints211 = new GridBagConstraints();
			gridBagConstraints211.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints211.gridx = 1;
			gridBagConstraints211.gridy = 0;
			gridBagConstraints211.weightx = 1.0;
			gridBagConstraints211.insets = new Insets(2, 2, 2, 2);
			pgName = new JLabel();
			pgName.setText("Name");
			pgSearchPanel = new JPanel();
			pgSearchPanel.setLayout(new GridBagLayout());
			pgSearchPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Protection Group Search",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			pgSearchPanel.add(pgName, new GridBagConstraints());
			pgSearchPanel.add(getPgNameText(), gridBagConstraints211);
			pgSearchPanel.add(getPgSearchButton(), gridBagConstraints311);
		}
		return pgSearchPanel;
	}

	/**
	 * This method initializes pgNameText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getPgNameText() {
		if (pgNameText == null) {
			pgNameText = new JTextField();
			pgNameText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					validateInput();
				}
			});
		}
		return pgNameText;
	}

	/**
	 * This method initializes pgSearchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getPgSearchButton() {
		if (pgSearchButton == null) {
			pgSearchButton = new JButton();
			pgSearchButton.setText("Search");
			pgSearchButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Runner runner = new Runner() {
								public void execute() {
									try {
										progress.showProgress("Searching...");
										ProtectionGroupSearchCriteria pgsc = new ProtectionGroupSearchCriteria();
										pgsc.setApplicationId(csmApp.getId());
										pgsc.setName(Utils
												.clean(getPgNameText()
														.getText()));

										List<ProtectionGroup> list = null;
										list = csmApp.getProtectionGroups(pgsc);
										getPgList().setData(list);
										progress
												.stopProgress(list.size()
														+ " protection group(s) found.");
									} catch (Exception e) {
										ErrorDialog.showError(e);
										progress
												.stopProgress("Error searching groups");

									} finally {
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
					});
		}
		return pgSearchButton;
	}

	/**
	 * This method initializes rolesSearchPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRolesSearchPanel() {
		if (rolesSearchPanel == null) {
			GridBagConstraints gridBagConstraints312 = new GridBagConstraints();
			gridBagConstraints312.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints312.gridx = 0;
			gridBagConstraints312.gridy = 1;
			gridBagConstraints312.gridwidth = 2;
			GridBagConstraints gridBagConstraints212 = new GridBagConstraints();
			gridBagConstraints212.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints212.gridx = 1;
			gridBagConstraints212.gridy = 0;
			gridBagConstraints212.weightx = 1.0;
			gridBagConstraints212.insets = new Insets(2, 2, 2, 2);
			roleName = new JLabel();
			roleName.setText("Name");
			rolesSearchPanel = new JPanel();
			rolesSearchPanel.setLayout(new GridBagLayout());
			rolesSearchPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Role Search", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			rolesSearchPanel.add(roleName, new GridBagConstraints());
			rolesSearchPanel.add(getRoleNameText(), gridBagConstraints212);
			rolesSearchPanel.add(getSearchRolesButton(), gridBagConstraints312);
		}
		return rolesSearchPanel;
	}

	/**
	 * This method initializes roleNameText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getRoleNameText() {
		if (roleNameText == null) {
			roleNameText = new JTextField();
			roleNameText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					validateInput();
				}
			});
		}
		return roleNameText;
	}

	/**
	 * This method initializes searchRolesButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSearchRolesButton() {
		if (searchRolesButton == null) {
			searchRolesButton = new JButton();
			searchRolesButton.setText("Search");
			searchRolesButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Runner runner = new Runner() {
								public void execute() {
									try {
										progress.showProgress("Searching...");
										RoleSearchCriteria crit = new RoleSearchCriteria();
										crit.setApplicationId(csmApp.getId());
										crit.setName(Utils
												.clean(getRoleNameText()
														.getText()));
										List<Role> list = csmApp.getRoles(crit);
										getRolesList().setRoles(list);
										progress.stopProgress(list.size()
												+ " roles(s) found.");
									} catch (Exception e) {
										ErrorDialog.showError(e);
										progress
												.stopProgress("Error searching roles");

									} finally {
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
					});
		}
		return searchRolesButton;
	}

	/**
	 * This method initializes infoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoLabel = new JLabel();
			infoLabel
					.setText("To create a permission first provide a user name or select a group, then a protection group, then role, and click create.");
			infoPanel = new JPanel();
			infoPanel.setLayout(new GridBagLayout());
			infoPanel.add(infoLabel, new GridBagConstraints());
		}
		return infoPanel;
	}

	/**
	 * This method initializes userPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUserPanel() {
		if (userPanel == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.gridy = 1;
			gridBagConstraints14.weightx = 1.0;
			userNameLabel = new JLabel();
			userNameLabel.setText("Name");
			userPanel = new JPanel();
			userPanel.setLayout(new GridBagLayout());
			userPanel.setBorder(BorderFactory.createTitledBorder(null, "User",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			userPanel.add(userNameLabel, gridBagConstraints15);
			userPanel.add(getUserNameText(), gridBagConstraints14);
		}
		return userPanel;
	}

	/**
	 * This method initializes groupsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGroupsPanel() {
		if (groupsPanel == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 1.0D;
			gridBagConstraints5.gridx = 0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.gridx = 0;
			groupsPanel = new JPanel();
			groupsPanel.setLayout(new GridBagLayout());
			groupsPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Groups", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			groupsPanel.add(getGroupsScrollPane(), gridBagConstraints4);
			groupsPanel.add(getGroupsSearchPanel(), gridBagConstraints5);
		}
		return groupsPanel;
	}

	/**
	 * This method initializes userNameText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUserNameText() {
		if (userNameText == null) {
			userNameText = new JTextField();
			userNameText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					getGroupsList().clearSelection();
					validateInput();
				}
			});
		}
		return userNameText;
	}

	private void validateInput() {

		if (((getUserNameText().getText() != null && getUserNameText()
				.getText().length() > 0) || getGroupsList().getSelectedValue() != null)
				&& getPgList().getSelectedValue() != null
				&& getRolesList().getSelectedValue() != null) {
			getAddAssociationButton().setEnabled(true);
		} else {
			getAddAssociationButton().setEnabled(false);
		}

	}

	private void setEnableSelection(boolean enabled) {
		getGroupsList().setEnabled(enabled);
		getGroupNameText().setEnabled(enabled);
		getGroupSearchButton().setEnabled(enabled);
		getUserNameText().setEnabled(enabled);
		getPgList().setEnabled(enabled);
		getPgNameText().setEnabled(enabled);
		getPgSearchButton().setEnabled(enabled);
		getRolesList().setEnabled(enabled);
		getRoleNameText().setEnabled(enabled);
		getSearchRolesButton().setEnabled(enabled);
	}

	private void clearSelections() {
		getUserNameText().setText("");
		getGroupsList().clearSelection();
		getPgList().clearSelection();
		getRolesList().clearSelection();
	}

	/**
	 * This method initializes wrapperPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getWrapperPanel() {
		if (wrapperPanel == null) {
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridwidth = 3;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.weightx = 0.0D;
			gridBagConstraints3.gridwidth = 3;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0D;
			gridBagConstraints.weighty = 1.0D;
			gridBagConstraints.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.ipadx = 0;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.gridx = 2;
			wrapperPanel = new JPanel();
			wrapperPanel.setLayout(new GridBagLayout());
			wrapperPanel.add(getRolesPanel(), gridBagConstraints2);
			wrapperPanel.add(getProtectionGroupPanel(), gridBagConstraints1);
			wrapperPanel.add(getUserGroupsPanel(), gridBagConstraints);
			wrapperPanel.add(getCreateAssociationPanel(), gridBagConstraints3);
			wrapperPanel.add(getInfoPanel(), gridBagConstraints16);
		}
		return wrapperPanel;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
