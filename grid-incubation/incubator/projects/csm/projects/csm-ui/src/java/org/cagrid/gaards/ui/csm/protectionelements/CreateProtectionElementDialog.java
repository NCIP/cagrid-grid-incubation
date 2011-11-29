package org.cagrid.gaards.ui.csm.protectionelements;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


public class CreateProtectionElementDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel titlePanel = null;
    private ProtectionElementPanel protectionElementPanel = null;
    private JPanel buttonPanel = null;
    private ProgressPanel progress = null;
    private Application application;
    private JButton create = null;
    private JButton cancel = null;
    private boolean modified = false;


    /**
     * @param owner
     */
    public CreateProtectionElementDialog(Application application) {
        super(GridApplication.getContext().getApplication());
        this.application = application;
        initialize();
    }


    public boolean isModified() {
        return modified;
    }


    private void toggleAccess(boolean enabled) {
        getProtectionElementPanel().toggleAccess(enabled);
        getCreate().setEnabled(enabled);
        getCancel().setEnabled(enabled);
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(600, 350);
        this.setContentPane(getJContentPane());
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.gridy = 3;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 2;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getTitlePanel(), gridBagConstraints);
            jContentPane.add(getProtectionElementPanel(), gridBagConstraints1);
            jContentPane.add(getButtonPanel(), gridBagConstraints2);
            jContentPane.add(getProgress(), gridBagConstraints11);
        }
        return jContentPane;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Create Protection Element", "Create a protection element to provide access control to a resource.");
        }
        return titlePanel;
    }


    /**
     * This method initializes protectionElementPanel
     * 
     * @return javax.swing.JPanel
     */
    private ProtectionElementPanel getProtectionElementPanel() {
        if (protectionElementPanel == null) {
            protectionElementPanel = new ProtectionElementPanel(this.application);
        }
        return protectionElementPanel;
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
            buttonPanel.add(getCancel(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes progress
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgress() {
        if (progress == null) {
            progress = new ProgressPanel();
        }
        return progress;
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
            getRootPane().setDefaultButton(create);
            create.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            toggleAccess(false);
                            try {
                                progress.showProgress("Creating protection element...");
                                getProtectionElementPanel().createProtectionElement();
                                modified = true;
                                progress.stopProgress("Successfully created the protection element.");
                                dispose();
                            } catch (Exception ex) {
                                ErrorDialog.showError(ex);
                                progress.stopProgress("Error");
                            } finally {
                                toggleAccess(true);
                            }
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
     * This method initializes cancel
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancel() {
        if (cancel == null) {
            cancel = new JButton();
            cancel.setText("Cancel");
            cancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dispose();
                }
            });
        }
        return cancel;
    }

}
