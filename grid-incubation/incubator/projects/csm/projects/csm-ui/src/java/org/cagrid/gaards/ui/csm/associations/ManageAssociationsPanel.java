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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.bean.GroupSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Group;
import org.cagrid.gaards.csm.client.Permission;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;
import org.cagrid.gaards.ui.csm.groups.GroupEntry;
import org.cagrid.gaards.ui.csm.groups.GroupsList;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class ManageAssociationsPanel extends JPanel {


	private Application csmApp;
	public ProgressPanel progress = null;
	private static final long serialVersionUID = 1L;
	private JPanel associationInfoPanel = null;
	private JPanel associationsSearchPanel = null;
	private JPanel managePanel = null;
	private JTabbedPane optionsTabPane = null;
	private CreateAssociationsPanel createPanel = null;
	private JPanel userGroupsPanel = null;
	private JPanel userPanel = null;
	private JLabel userNameLabel = null;
	private JTextField userNameText = null;
	private JPanel groupsPanel = null;
	private JScrollPane groupsScrollPane = null;
	private GroupsList groupsList = null;
	private JPanel groupsSearchPanel = null;
	private JLabel groupName = null;
	private JTextField groupNameText = null;
	private JButton groupSearchButton = null;
	private JButton loadAssociationsButton = null;
	private JScrollPane associationsScrollPane = null;
	private AssociationsTable associationsTable = null;
	private JPanel wrapperPanel = null;

	/**
	 * This is the default constructor
	 */
	public ManageAssociationsPanel(Application app, ProgressPanel progress) {
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
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		gridBagConstraints3.weightx = 1.0D;
		gridBagConstraints3.weighty = 1.0D;
		gridBagConstraints3.gridy = 0;
		this.setSize(689, 356);
		this.setLayout(new GridBagLayout());
		//feedbackPanel = new IconFeedbackPanel(this.validationModel, getWrapperPanel());
		this.add(getWrapperPanel(), gridBagConstraints3);
	}

	/**
	 * This method initializes associationInfoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAssociationInfoPanel() {
		if (associationInfoPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.weightx = 1.0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = -1;
			gridBagConstraints6.weightx = 3.0;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridx = -1;
			associationInfoPanel = new JPanel();
			associationInfoPanel.setLayout(new GridBagLayout());
			associationInfoPanel.setBorder(BorderFactory.createTitledBorder(
					null, "Permissions", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, CSMLookAndFeel
							.getPanelLabelColor()));
			associationInfoPanel.add(getAssociationsScrollPane(),
					gridBagConstraints);
		}
		return associationInfoPanel;
	}

	/**
	 * This method initializes associationsSearchPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAssociationsSearchPanel() {
		if (associationsSearchPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.fill = GridBagConstraints.NONE;
			gridBagConstraints4.gridwidth = 1;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.weighty = 1.0D;
			gridBagConstraints7.ipadx = 0;
			gridBagConstraints7.weightx = 1.0D;
			gridBagConstraints7.gridwidth = 1;
			gridBagConstraints7.gridy = 0;
			associationsSearchPanel = new JPanel();
			associationsSearchPanel.setLayout(new GridBagLayout());
			associationsSearchPanel.add(getUserGroupsPanel(),
					gridBagConstraints7);
			associationsSearchPanel.add(getLoadAssociationsButton(),
					gridBagConstraints4);
		}
		return associationsSearchPanel;
	}

	/**
	 * This method initializes managePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getManagePanel() {
		if (managePanel == null) {
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.BOTH;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.ipadx = 1;
			gridBagConstraints16.weightx = 1.0D;
			gridBagConstraints16.weighty = 1.0D;
			gridBagConstraints16.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 3.0D;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints1.gridheight = 1;
			gridBagConstraints1.gridx = 1;
			managePanel = new JPanel();
			managePanel.setLayout(new GridBagLayout());
			managePanel.add(getAssociationInfoPanel(), gridBagConstraints1);
			managePanel.add(getAssociationsSearchPanel(), gridBagConstraints16);
		}
		return managePanel;
	}

	/**
	 * This method initializes optionsTabPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getOptionsTabPane() {
		if (optionsTabPane == null) {
			optionsTabPane = new JTabbedPane();
			optionsTabPane.addTab("Manage", null, getManagePanel(), null);
			optionsTabPane.addTab("Create", null, getCreatePanel(), null);
		}
		return optionsTabPane;
	}

	/**
	 * This method initializes createPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private CreateAssociationsPanel getCreatePanel() {
		if (createPanel == null) {
			createPanel = new CreateAssociationsPanel(csmApp, progress);
		}
		return createPanel;
	}

	/**
	 * This method initializes userGroupsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUserGroupsPanel() {
		if (userGroupsPanel == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.gridy = 1;
			gridBagConstraints13.weightx = 1.0D;
			gridBagConstraints13.weighty = 1.0D;
			gridBagConstraints13.gridx = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.weightx = 1.0D;
			gridBagConstraints12.weighty = 0.0D;
			gridBagConstraints12.gridx = 0;
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
	 * This method initializes userPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getUserPanel() {
		if (userPanel == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridy = 0;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 0;
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
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.fill = GridBagConstraints.BOTH;
			gridBagConstraints41.gridy = 0;
			gridBagConstraints41.weightx = 1.0;
			gridBagConstraints41.weighty = 1.0;
			gridBagConstraints41.gridx = 0;
			groupsPanel = new JPanel();
			groupsPanel.setLayout(new GridBagLayout());
			groupsPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Groups", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, LookAndFeel
							.getPanelLabelColor()));
			groupsPanel.add(getGroupsScrollPane(), gridBagConstraints41);
			groupsPanel.add(getGroupsSearchPanel(), gridBagConstraints5);
		}
		return groupsPanel;
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
	 * @return org.cagrid.gaards.ui.csm.groups.GroupsList
	 */
	private GroupsList getGroupsList() {
		if (groupsList == null) {
			groupsList = new GroupsList();
			groupsList.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					getUserNameText().setText("");
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
	 * This method initializes loadAssociationsButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getLoadAssociationsButton() {
		if (loadAssociationsButton == null) {
			loadAssociationsButton = new JButton();
			loadAssociationsButton.setText("load permissions");
			loadAssociationsButton.setEnabled(false);
			loadAssociationsButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Runner runner = new Runner() {
								public void execute() {
									try {
										progress.showProgress("Loading...");
										List<Permission> list = null;
										if (getUserNameText().getText() != null
												&& getUserNameText().getText()
														.length() > 0) {
											list = csmApp
													.getPermissions(getUserNameText()
															.getText());
										} else {
											list = csmApp
													.getPermissions(((GroupEntry) getGroupsList()
															.getSelectedValue())
															.getGroup());
										}
										getAssociationsTable().setPermissions(
												list);
										progress.stopProgress(list.size()
												+ " permission(s) found.");
									} catch (Exception e) {
										ErrorDialog.showError(e);
										progress
												.stopProgress("Error loading permissions.");

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
		return loadAssociationsButton;
	}

	private void validateInput() {
		if ((getUserNameText().getText() != null && getUserNameText().getText()
				.length() > 0)
				|| getGroupsList().getSelectedValue() != null) {
			getLoadAssociationsButton().setEnabled(true);
		} else {
			getLoadAssociationsButton().setEnabled(false);
		}

	}

	/**
	 * This method initializes associationsScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getAssociationsScrollPane() {
		if (associationsScrollPane == null) {
			associationsScrollPane = new JScrollPane();
			associationsScrollPane.setViewportView(getAssociationsTable());
		}
		return associationsScrollPane;
	}

	/**
	 * This method initializes associationsTable
	 * 
	 * @return javax.swing.JTable
	 */
	public AssociationsTable getAssociationsTable() {
		if (associationsTable == null) {
			associationsTable = new AssociationsTable();
			associationsTable.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					try {
						int row = getAssociationsTable().rowAtPoint(
								e.getPoint());
						if (row >= 0
								&& row < getAssociationsTable().getRowCount()) {
							getAssociationsTable().changeSelection(row, 0,
									false, false);
						}
						if (SwingUtilities.isRightMouseButton(e)
								&& getAssociationsTable()
										.getSelectedPermission() != null) {
							AssociationsPopUpMenu menu = new AssociationsPopUpMenu(
									csmApp, ManageAssociationsPanel.this);
							menu.show(e.getComponent(), e.getX(), e.getY());
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return associationsTable;
	}

	/**
	 * This method initializes wrapperPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getWrapperPanel() {
		if (wrapperPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = -1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridx = -1;
			wrapperPanel = new JPanel();
			wrapperPanel.setLayout(new GridBagLayout());
			wrapperPanel.add(getOptionsTabPane(), gridBagConstraints2);
		}
		return wrapperPanel;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
