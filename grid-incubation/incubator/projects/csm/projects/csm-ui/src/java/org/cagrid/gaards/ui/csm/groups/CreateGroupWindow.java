package org.cagrid.gaards.ui.csm.groups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


public class CreateGroupWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton create = null;

    private Application application;

    private JPanel groupPanel = null;

    private JTextField groupName = null;

    private JPanel titlePanel = null;

    private JLabel jLabel = null;

    private JLabel jLabel1 = null;

    private JTextField description = null;

    private ProgressPanel progress = null;

    private boolean groupCreated = false;


    /**
     * This is the default constructor
     */
    public CreateGroupWindow(Application application) {
        super(GridApplication.getContext().getApplication());
        this.application = application;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(500, 200);
        this.setContentPane(getJContentPane());
        this.setTitle("Create Group");
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 3;
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
            jContentPane.add(getGroupPanel(), gridBagConstraints11);
            jContentPane.add(getTitlePanel(), gridBagConstraints3);
            jContentPane.add(getProgress(), gridBagConstraints4);
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
            buttonPanel.add(getCreate(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addAdmin
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
                            createGroup();
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


    public boolean wasGroupCreated() {
        return groupCreated;
    }


    private void createGroup() {
        try {
            getProgress().showProgress("Creating group...");
            create.setEnabled(false);
            this.application
                .createGroup(Utils.clean(getGroupName().getText()), Utils.clean(getDescription().getText()));
            groupCreated = true;
            getProgress().stopProgress("");
            dispose();
            GridApplication.getContext().showMessage("Successfully created the group.");
        } catch (Exception e) {
            getProgress().stopProgress("Error");
            ErrorDialog.showError(e);
            create.setEnabled(true);
        }
    }


    /**
     * This method initializes groupPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupPanel() {
        if (groupPanel == null) {
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
            jLabel.setText("Group Name");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.weightx = 1.0;
            groupPanel = new JPanel();
            groupPanel.setLayout(new GridBagLayout());
            groupPanel.add(getGroupName(), gridBagConstraints7);
            groupPanel.add(jLabel, gridBagConstraints);
            groupPanel.add(jLabel1, gridBagConstraints12);
            groupPanel.add(getDescription(), gridBagConstraints2);
        }
        return groupPanel;
    }


    /**
     * This method initializes groupName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGroupName() {
        if (groupName == null) {
            groupName = new JTextField();
        }
        return groupName;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Create Group",
                "Create a local group in CSM that may be used for provisioning access control policy.");
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

}
