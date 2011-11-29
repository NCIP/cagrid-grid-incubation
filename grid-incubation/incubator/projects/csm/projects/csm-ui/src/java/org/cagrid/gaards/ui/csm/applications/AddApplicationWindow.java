package org.cagrid.gaards.ui.csm.applications;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.client.CSM;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.csm.CSMSessionProvider;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


public class AddApplicationWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton addApplication = null;

    private CSMSessionProvider session;

    private JPanel applicationPanel = null;

    private JTextField applicationName = null;

    private JPanel titlePanel = null;

    private JLabel jLabel = null;

    private JLabel jLabel1 = null;

    private JTextField description = null;


    /**
     * This is the default constructor
     */
    public AddApplicationWindow(CSMSessionProvider session) {
        super(GridApplication.getContext().getApplication());
        this.session = session;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("Create Application");
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.weighty = 1.0D;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getButtonPanel(), gridBagConstraints1);
            jContentPane.add(getApplicationPanel(), gridBagConstraints11);
            jContentPane.add(getTitlePanel(), gridBagConstraints3);
        }
        return jContentPane;
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
            buttonPanel.add(getAddApplication(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addAdmin
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddApplication() {
        if (addApplication == null) {
            addApplication = new JButton();
            addApplication.setText("Add");
            getRootPane().setDefaultButton(addApplication);
            addApplication.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            addApplication();
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
        return addApplication;
    }


    private void addApplication() {
        try {
            addApplication.setEnabled(false);
            CSM csm = this.session.getSession().getCSM();
            csm.createApplication(getApplicationName().getText(), getDescription().getText());
            dispose();
        } catch (Exception e) {
            ErrorDialog.showError(e);
            addApplication.setEnabled(true);
        }
    }


    /**
     * This method initializes applicationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getApplicationPanel() {
        if (applicationPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("Description");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Name");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.weightx = 1.0;
            applicationPanel = new JPanel();
            applicationPanel.setLayout(new GridBagLayout());
            applicationPanel.add(getApplicationName(), gridBagConstraints7);
            applicationPanel.add(jLabel, gridBagConstraints);
            applicationPanel.add(jLabel1, gridBagConstraints12);
            applicationPanel.add(getDescription(), gridBagConstraints2);
        }
        return applicationPanel;
    }


    /**
     * This method initializes applicationName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getApplicationName() {
        if (applicationName == null) {
            applicationName = new JTextField();
        }
        return applicationName;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Create Application",
                "Create an application in CSM, such that one can manage the access control policy for the application using CSM.");
        }
        return titlePanel;
    }


    /**
     * This method initializes description
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDescription() {
        if (description == null) {
            description = new JTextField();
        }
        return description;
    }

}
