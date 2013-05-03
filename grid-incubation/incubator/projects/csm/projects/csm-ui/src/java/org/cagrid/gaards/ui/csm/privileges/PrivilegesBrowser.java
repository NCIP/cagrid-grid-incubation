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
package org.cagrid.gaards.ui.csm.privileges;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cagrid.gaards.csm.bean.PrivilegeSearchCriteria;
import org.cagrid.gaards.csm.client.CSM;
import org.cagrid.gaards.csm.client.Privilege;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;
import org.cagrid.gaards.ui.csm.SessionPanel;
import org.cagrid.grape.ApplicationComponent;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Langella </A>
 * @version $Id: TrustedIdPsWindow.java,v 1.5 2008-11-20 15:29:42 langella Exp $
 */
public class PrivilegesBrowser extends ApplicationComponent {

    private static final long serialVersionUID = 1L;

    private javax.swing.JPanel jContentPane = null;

    private JPanel mainPanel = null;

    private JPanel contentPanel = null;

    private JPanel buttonPanel = null;

    private PrivilegesTable privileges = null;

    private JScrollPane jScrollPane = null;

    private JButton viewApplication = null;

    private SessionPanel session = null;

    private JPanel queryPanel = null;

    private JButton query = null;

    private JButton removeApplication = null;

    private JButton addPrivilegeButton = null;

    private JPanel titlePanel = null;

    private ProgressPanel progressPanel = null;

    private boolean searchCompleted;


    /**
     * This is the default constructor
     */
    public PrivilegesBrowser() {
        super();
        initialize();
        this.setFrameIcon(CSMLookAndFeel.getCSMIcon());
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setTitle("Manage Privileges");

    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 5;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 0;
            GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
            gridBagConstraints33.gridx = 0;
            gridBagConstraints33.gridy = 2;
            GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
            gridBagConstraints35.gridx = 0;
            gridBagConstraints35.weightx = 1.0D;
            gridBagConstraints35.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints35.gridheight = 1;
            gridBagConstraints35.gridwidth = 1;
            gridBagConstraints35.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints35.weighty = 0.0D;
            gridBagConstraints35.ipadx = 0;
            gridBagConstraints35.ipady = 0;
            gridBagConstraints35.gridy = 1;

            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 3;
            gridBagConstraints1.ipadx = 0;
            gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 4;
            gridBagConstraints2.insets = new java.awt.Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTH;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            mainPanel.add(getButtonPanel(), gridBagConstraints2);
            mainPanel.add(getContentPanel(), gridBagConstraints1);
            mainPanel.add(getSession(), gridBagConstraints35);
            mainPanel.add(getQueryPanel(), gridBagConstraints33);
            mainPanel.add(getTitlePanel(), gridBagConstraints);
            mainPanel.add(getProgressPanel(), gridBagConstraints11);
        }
        return mainPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPanel() {
        if (contentPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Privileges",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            contentPanel.add(getJScrollPane(), gridBagConstraints4);
        }
        return contentPanel;
    }


    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getViewApplication(), null);
            buttonPanel.add(getAddPrivilegeButton(), null);
            buttonPanel.add(getRemoveApplication(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private PrivilegesTable getPrivileges() {
        if (privileges == null) {
            privileges = new PrivilegesTable(this);
        }
        return privileges;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getPrivileges());
        }
        return jScrollPane;
    }


    /**
     * This method initializes manageUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewApplication() {
        if (viewApplication == null) {
            viewApplication = new JButton();
            viewApplication.setText("View");
            viewApplication.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            viewPrivilege();
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

        return viewApplication;
    }


    public void viewPrivilege() {
        try {
            PrivilegeWindow window = new PrivilegeWindow(getSession().getCSM(), getPrivileges().getSelectedPrivilege());
            window.setModal(true);
            GridApplication.getContext().showDialog(window);
            if ((searchCompleted) && (window.wasPrivilegeModified())) {
                findPrivileges();
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
        }
    }


    public void addPrivilege() {
        try {
            PrivilegeWindow window = new PrivilegeWindow(getSession().getCSM());
            window.setModal(true);
            GridApplication.getContext().showDialog(window);
            if ((searchCompleted) && (window.wasPrivilegeModified())) {
                findPrivileges();
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
        }
    }


    /**
     * This method initializes session
     * 
     * @return javax.swing.JPanel
     */
    private SessionPanel getSession() {
        if (session == null) {
            session = new SessionPanel(false, false);
        }
        return session;
    }


    /**
     * This method initializes queryPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getQueryPanel() {
        if (queryPanel == null) {
            queryPanel = new JPanel();
            queryPanel.add(getQuery(), null);
        }
        return queryPanel;
    }


    /**
     * This method initializes query
     * 
     * @return javax.swing.JButton
     */
    private JButton getQuery() {
        if (query == null) {
            query = new JButton();
            query.setText("Search");
            getRootPane().setDefaultButton(query);
            query.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    disableAllButtons();
                    Runner runner = new Runner() {
                        public void execute() {
                            findPrivileges();
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
        return query;
    }


    private void findPrivileges() {
        this.getPrivileges().clearTable();
        getProgressPanel().showProgress("Searching...");

        try {
            CSM csm = getSession().getCSM();

            List<Privilege> privs = csm.getPrivileges(new PrivilegeSearchCriteria());

            for (int i = 0; i < privs.size(); i++) {
                this.getPrivileges().addPrivilege(privs.get(i));
            }

            getProgressPanel().stopProgress(privs.size() + " privilege(s) found.");
            searchCompleted = true;
        } catch (AccessDeniedFault pdf) {
            searchCompleted = false;
            ErrorDialog.showError(pdf);
            getProgressPanel().stopProgress("Error");
        } catch (Exception e) {
            searchCompleted = false;
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error");
        } finally {
            enableAllButtons();
        }
    }


    /**
     * This method initializes removeUser
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveApplication() {
        if (removeApplication == null) {
            removeApplication = new JButton();
            removeApplication.setText("Remove");
            removeApplication.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            removePrivilege();
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
        return removeApplication;
    }


    private void removePrivilege() {
        try {
            getProgressPanel().showProgress("Removing...");
            disableAllButtons();
            CSM csm = getSession().getCSM();
            csm.removePrivilege(getPrivileges().getSelectedPrivilege());
            if (searchCompleted) {
                findPrivileges();
            }
            getProgressPanel().stopProgress("Successfully removed the privilege.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgressPanel().stopProgress("Error.");
        } finally {
            enableAllButtons();
        }
    }


    /**
     * This method initializes addPrivilegeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddPrivilegeButton() {
        if (addPrivilegeButton == null) {
            addPrivilegeButton = new JButton();
            addPrivilegeButton.setText("Add");
            addPrivilegeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addPrivilege();
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
        return addPrivilegeButton;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Manage Privileges",
                "Manage privileges that CSM applications can use for provisioning access control.");
        }
        return titlePanel;
    }


    private void disableAllButtons() {
        getAddPrivilegeButton().setEnabled(false);
        getViewApplication().setEnabled(false);
        getRemoveApplication().setEnabled(false);
        getQuery().setEnabled(false);
    }


    private void enableAllButtons() {
        getAddPrivilegeButton().setEnabled(true);
        getViewApplication().setEnabled(true);
        getRemoveApplication().setEnabled(true);
        getQuery().setEnabled(true);

    }


    /**
     * This method initializes progressPanel
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgressPanel() {
        if (progressPanel == null) {
            progressPanel = new ProgressPanel();
        }
        return progressPanel;
    }

}
