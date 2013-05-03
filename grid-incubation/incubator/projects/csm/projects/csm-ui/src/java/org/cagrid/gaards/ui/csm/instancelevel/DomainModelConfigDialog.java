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
package org.cagrid.gaards.ui.csm.instancelevel;

import javax.swing.JDialog;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.filters.client.FilterCreatorClient;
import org.cagrid.grape.GridApplication;

public class DomainModelConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private Application application;
	private DomainModelConfigPanel configPanel;

	/**
	 * @param owner
	 */
	public DomainModelConfigDialog(Application application) {
		super(GridApplication.getContext().getApplication());
		this.application = application;
		setTitle("Configure the domain model");
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 350);
		this.configPanel = new DomainModelConfigPanel(this.application, this);
		this.setContentPane(configPanel);
	}

	public FilterCreatorClient createFilterCreatorClient() {
		return configPanel.getClient();
	}

}
