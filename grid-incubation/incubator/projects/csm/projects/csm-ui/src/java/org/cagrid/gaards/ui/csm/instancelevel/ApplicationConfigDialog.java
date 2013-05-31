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
package org.cagrid.gaards.ui.csm.instancelevel;

import javax.swing.JDialog;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.filters.client.FilterCreatorClient;
import org.cagrid.grape.GridApplication;

public class ApplicationConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private Application application;
	private ApplicationConfigPanel configPanel;

	/**
	 * @param owner
	 */
	public ApplicationConfigDialog(Application application) {
		super(GridApplication.getContext().getApplication());
		this.application = application;
		setTitle("Configure the application");
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 350);
		this.configPanel = new ApplicationConfigPanel(this.application, this);
		this.setContentPane(configPanel);
	}

	public FilterCreatorClient createFilterCreatorClient() {
		return configPanel.getClient();
	}

}
