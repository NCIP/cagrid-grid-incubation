package org.cagrid.gaards.ui.csm.instancelevel;

import javax.swing.JDialog;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.filters.client.FilterCreatorClient;
import org.cagrid.grape.GridApplication;

public class NewSecurityFilterDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private Application application;
	private FilterCreatorClient client;
	private NewSecurityFilterPanel securityPanel;

	/**
	 * @param owner
	 */
	public NewSecurityFilterDialog(FilterCreatorClient client,
			Application application) {
		super(GridApplication.getContext().getApplication());
		this.application = application;
		this.client = client;
		setTitle("Create security filters.");
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 500);
		this.securityPanel = new NewSecurityFilterPanel(client, application, this);
		this.setContentPane(securityPanel);
	}

}
