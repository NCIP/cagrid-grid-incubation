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
package org.cagrid.gaards.ui.csm.associations;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Permission;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;

public class AssociationsPopUpMenu extends JPopupMenu {

	private Application csmApp = null;
	private ManageAssociationsPanel mp = null;
	private JMenuItem removePermission = null;

	/**
	 * This method initializes
	 */
	public AssociationsPopUpMenu(Application csmApp, ManageAssociationsPanel mp) {
		super();
		this.mp = mp;
		this.csmApp = csmApp;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.add(getPermissionMenuItem());
	}

	/**
	 * This method initializes removeGroupMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getPermissionMenuItem() {
		if (removePermission == null) {
			removePermission = new JMenuItem();
			removePermission.setText("Remove Permission");
			removePermission.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					Runner runner = new Runner() {
						public void execute() {
							try {
								mp.progress.showProgress("Removing permission...");
								try {
									if (mp.getAssociationsTable().getSelectedPermission() != null) {
										Permission perm = mp.getAssociationsTable().getSelectedPermission();

										csmApp.revokePermission(perm);
										Runnable r = new Runnable() {
											
											public void run() {
												try{
												mp.getAssociationsTable().removeSelectedPermission();
												} catch(Exception e){
													e.printStackTrace();
												}
											}
										};
										SwingUtilities.invokeLater(r);
										
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								mp.progress.stopProgress("Permission removed.");
							} catch (Exception e) {
								ErrorDialog.showError(e);
								mp.progress
										.stopProgress("Error removing permission.");

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
		return removePermission;
	}
}
