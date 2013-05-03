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
package org.cagrid.gaards.ui.csm.protectionelements;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.csm.CSMUIUtils;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class ManageProtectionElementsPanel extends JPanel implements ProtectionElementSearchI {

    private static final long serialVersionUID = 1L;
    private ProtectionElementSearchPanel protectionElementSearchPanel = null;
    private Application application;
    private JPanel protectionElementsPanel = null;
    private JScrollPane jScrollPane = null;
    private ProtectionElementsTable protectionElements = null;
    private ProgressPanel progress;
    private JPanel buttonPanel = null;
    private JButton create = null;
    private JButton modify = null;
    private JButton remove = null;
    private boolean searchCompleted = false;


    /**
     * This is the default constructor
     */
    public ManageProtectionElementsPanel(Application application, ProgressPanel progress) {
        super();
        this.application = application;
        this.progress = progress;
        initialize();
    }


    private void toggleAccess(boolean enabled) {
        protectionElementSearchPanel.toggleAccess(enabled);
        getCreate().setEnabled(enabled);
        getModify().setEnabled(enabled);
        getRemove().setEnabled(enabled);
    }


    public void protectionElementSearch() {
        toggleAccess(false);
        try {
            progress.showProgress("Searching...");
            getProtectionElements().clearTable();
            List<ProtectionElement> search = getProtectionElementSearchPanel().performSearch();
            List<ProtectionElement> sorted = CSMUIUtils.sortProtectionElements(search);
            getProtectionElements().addProtectionElements(sorted);
            progress.stopProgress(search.size() + " protection elements found.");
            searchCompleted = true;
        } catch (Exception e) {
            ErrorDialog.showError(e);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints21.weightx = 1.0D;
        gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints21.gridy = 2;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.weightx = 1.0D;
        gridBagConstraints2.weighty = 1.0D;
        gridBagConstraints2.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0D;
        gridBagConstraints.gridy = 0;
        this.setSize(900, 600);
        this.setLayout(new GridBagLayout());
        this.add(getProtectionElementSearchPanel(), gridBagConstraints);
        this.add(getProtectionElementsPanel(), gridBagConstraints2);
        this.add(getButtonPanel(), gridBagConstraints21);
    }


    /**
     * This method initializes protectionElementSearchPanel
     * 
     * @return javax.swing.JPanel
     */
    private ProtectionElementSearchPanel getProtectionElementSearchPanel() {
        if (protectionElementSearchPanel == null) {
            protectionElementSearchPanel = new ProtectionElementSearchPanel(this.application, this);

        }
        return protectionElementSearchPanel;
    }


    /**
     * This method initializes protectionElementsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProtectionElementsPanel() {
        if (protectionElementsPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.weighty = 1.0;
            gridBagConstraints1.weightx = 1.0;
            protectionElementsPanel = new JPanel();
            protectionElementsPanel.setLayout(new GridBagLayout());
            protectionElementsPanel.add(getJScrollPane(), gridBagConstraints1);
            protectionElementsPanel.setBorder(BorderFactory.createTitledBorder(null, "Protection Elements",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return protectionElementsPanel;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getProtectionElements());
        }
        return jScrollPane;
    }


    /**
     * This method initializes protectionElements
     * 
     * @return javax.swing.JTable
     */
    private ProtectionElementsTable getProtectionElements() {
        if (protectionElements == null) {
            protectionElements = new ProtectionElementsTable(this);
        }
        return protectionElements;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getCreate(), null);
            buttonPanel.add(getModify(), null);
            buttonPanel.add(getRemove(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes create
     * 
     * @return javax.swing.JButton
     */
    private JButton getCreate() {
        if (create == null) {
            create = new JButton();
            create.setText("Create");
            create.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            createProtectionElement();
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
        return create;
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
                    Runner runner = new Runner() {

                        public void execute() {
                            modifyProtectionElement();
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
        return modify;
    }


    protected void modifyProtectionElement() {
        toggleAccess(false);
        try {
            progress.showProgress("Modifying protection element...");
            EditProtectionElementDialog dialog = new EditProtectionElementDialog(getProtectionElements()
                .getSelectedProtectionElement());
            dialog.setModal(true);
            GridApplication.getContext().showDialog(dialog);
            if (dialog.isModified()) {
                if (searchCompleted) {
                    List<ProtectionElement> list = getProtectionElementSearchPanel().performSearch();
                    getProtectionElements().clearTable();
                    getProtectionElements().addProtectionElements(CSMUIUtils.sortProtectionElements(list));
                }
                progress.stopProgress("Successfully modified the protection element.");
            } else {
                progress.stopProgress("Modification of the protection element cancelled.");
            }

        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    protected void removeProtectionElement() {
        toggleAccess(false);
        try {
            progress.showProgress("Removing protection element...");
            this.application.removeProtectionElement(getProtectionElements().getSelectedProtectionElement());
            if (searchCompleted) {
                List<ProtectionElement> list = getProtectionElementSearchPanel().performSearch();
                getProtectionElements().clearTable();
                getProtectionElements().addProtectionElements(CSMUIUtils.sortProtectionElements(list));
            }
            progress.stopProgress("Successfully removed the protection element.");

        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    protected void createProtectionElement() {
        toggleAccess(false);
        try {
            progress.showProgress("Create protection element...");
            CreateProtectionElementDialog dialog = new CreateProtectionElementDialog(this.application);
            dialog.setModal(true);
            GridApplication.getContext().showDialog(dialog);
            if (dialog.isModified()) {
                if (searchCompleted) {
                    List<ProtectionElement> list = getProtectionElementSearchPanel().performSearch();
                    getProtectionElements().clearTable();
                    getProtectionElements().addProtectionElements(CSMUIUtils.sortProtectionElements(list));
                }
                progress.stopProgress("Successfull modified the protection element.");
            } else {
                progress.stopProgress("Modification of protection element cancelled.");
            }

        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    /**
     * This method initializes remove
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemove() {
        if (remove == null) {
            remove = new JButton();
            remove.setText("Remove");
            remove.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            removeProtectionElement();
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
        return remove;
    }

}
