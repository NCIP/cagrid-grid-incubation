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
package org.cagrid.gaards.ui.csm;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.client.CSM;
import org.cagrid.gaards.ui.common.CredentialComboBox;
import org.cagrid.grape.LookAndFeel;
import org.globus.gsi.GlobusCredential;


public class SessionPanel extends JPanel implements CSMSessionProvider {

    private static final long serialVersionUID = 1L;

    private JLabel jLabel = null;

    private CSMServiceList service = null;

    private JLabel jLabel1 = null;

    private CredentialComboBox cred = null;

    private boolean showBorder;
    private boolean allowAnonymous;


    /**
     * This is the default constructor
     */
    public SessionPanel() {
        this(true, false);
    }


    public SessionPanel(boolean showBorder, boolean allowAnonymous) {
        super();
        this.showBorder = showBorder;
        this.allowAnonymous = allowAnonymous;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.anchor = GridBagConstraints.WEST;
        gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints3.gridx = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.gridy = 1;
        jLabel1 = new JLabel();
        jLabel1.setText("Credential");
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        jLabel = new JLabel();
        jLabel.setText("Service");
        this.setSize(300, 200);
        this.setLayout(new GridBagLayout());
        if (showBorder) {
            this.setBorder(BorderFactory.createTitledBorder(null, "Session Information",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        this.add(jLabel, gridBagConstraints1);
        this.add(getService(), gridBagConstraints);
        this.add(jLabel1, gridBagConstraints2);
        this.add(getCred(), gridBagConstraints3);
    }


    /**
     * This method initializes service
     * 
     * @return javax.swing.JComboBox
     */
    private CSMServiceList getService() {
        if (service == null) {
            service = new CSMServiceList();
        }
        return service;
    }


    /**
     * This method initializes cred
     * 
     * @return javax.swing.JComboBox
     */
    private CredentialComboBox getCred() {
        if (cred == null) {
            cred = new CredentialComboBox(this.allowAnonymous);
        }
        return cred;
    }


    public CSMSession getSession() throws Exception {
        return new CSMSession(getService().getSelectedService(), getCredential());
    }


    public CSM getCSM() throws Exception {
        return getSession().getCSM();
    }


    public GlobusCredential getCredential() throws Exception {
        return getCred().getSelectedCredential();
    }

}
