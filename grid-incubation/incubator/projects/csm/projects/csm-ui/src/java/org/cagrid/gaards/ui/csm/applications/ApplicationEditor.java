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
package org.cagrid.gaards.ui.csm.applications;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JTabbedPane;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.csm.groups.GroupsPanel;
import org.cagrid.gaards.ui.csm.instancelevel.ManagerSecurityFiltersPanel;
import org.cagrid.gaards.ui.csm.permissions.ManagePermissionsPanel;
import org.cagrid.gaards.ui.csm.protectionelements.ManageProtectionElementsPanel;
import org.cagrid.gaards.ui.csm.protectiongroups.ManageProtectionGroupsPanel;
import org.cagrid.gaards.ui.csm.roles.ManageRolesPanel;
import org.cagrid.grape.ApplicationComponent;

public class ApplicationEditor extends ApplicationComponent {
	private Application csmApp = null;
	private static final long serialVersionUID = 1L;
	private JTabbedPane operationsTabbedPane = null;
	private ManageProtectionGroupsPanel protectionGroupsPanel = null;
	private ManageRolesPanel rolesPanel = null;
	private GroupsPanel groupsPanel = null;
	private ManageProtectionElementsPanel protectionElementsPanel = null;
	private ManagePermissionsPanel associationsPanel = null;
	private javax.swing.JPanel jContentPane = null;
	private TitlePanel titlePanel = null;
	private ProgressPanel progress = null;
	private ManagerSecurityFiltersPanel instanceLevelPanel = null;

	/**
	 * This is the default constructor
	 */
	public ApplicationEditor(Application app) {
		super();
		this.csmApp = app;
		initialize();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 0;
			jContentPane = new javax.swing.JPanel();
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.gridx = 0;
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getOperationsTabbedPane(), gridBagConstraints);
			jContentPane.add(getTitlePanel(), gridBagConstraints1);
			jContentPane.add(getProgress(), gridBagConstraints2);
		}
		return jContentPane;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(500, 500);
		this.setTitle("Access Control Management");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes operationsTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getOperationsTabbedPane() {
		if (operationsTabbedPane == null) {
			operationsTabbedPane = new JTabbedPane();
			operationsTabbedPane.addTab("Protection Elements", null,
					getProtectionElementsPanel(), null);
			operationsTabbedPane.addTab("Protection Groups", null,
					getProtectionGroupsPanel(), null);
			operationsTabbedPane.addTab("Roles", null, getRolesPanel(), null);
			operationsTabbedPane.addTab("Groups", null, getGroupsPanel(), null);
			operationsTabbedPane.addTab("Permissions", null,
					getAssociationsPanel(), null);
			operationsTabbedPane.addTab("Instance Level", null, getJPanel(), null);
		}
		return operationsTabbedPane;
	}

	/**
	 * This method initializes protectionGroupsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ManageProtectionGroupsPanel getProtectionGroupsPanel() {
		if (protectionGroupsPanel == null) {
			protectionGroupsPanel = new ManageProtectionGroupsPanel(csmApp,
					getProgress());
		}
		return protectionGroupsPanel;
	}

	/**
	 * This method initializes rolesPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ManageRolesPanel getRolesPanel() {
		if (rolesPanel == null) {
			rolesPanel = new ManageRolesPanel(csmApp, getProgress());
		}
		return rolesPanel;
	}

	/**
	 * This method initializes groupsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private GroupsPanel getGroupsPanel() {
		if (groupsPanel == null) {
			groupsPanel = new GroupsPanel(this.csmApp, getProgress());
		}
		return groupsPanel;
	}

	/**
	 * This method initializes protectionElementsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ManageProtectionElementsPanel getProtectionElementsPanel() {
		if (protectionElementsPanel == null) {
			protectionElementsPanel = new ManageProtectionElementsPanel(csmApp,
					getProgress());
		}
		return protectionElementsPanel;
	}

	/**
	 * This method initializes associationsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ManagePermissionsPanel getAssociationsPanel() {
		if (associationsPanel == null) {
			associationsPanel = new ManagePermissionsPanel(csmApp,
					getProgress());
		}
		return associationsPanel;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private TitlePanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel(this.csmApp.getName(), this.csmApp
					.getDescription());
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
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private ManagerSecurityFiltersPanel getJPanel() {
		if (instanceLevelPanel == null) {
			instanceLevelPanel = new ManagerSecurityFiltersPanel(csmApp,progress);
		}
		return instanceLevelPanel;
	}

} // @jve:decl-index=0:visual-constraint="9,6"
