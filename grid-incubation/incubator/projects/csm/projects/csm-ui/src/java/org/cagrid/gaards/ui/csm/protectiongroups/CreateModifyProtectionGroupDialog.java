package org.cagrid.gaards.ui.csm.protectiongroups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionGroup;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class CreateModifyProtectionGroupDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private TitlePanel titlePanel = null;
    private Application application;
    private ProtectionGroup protectionGroup;
    private JPanel informationPanel = null;
    private JLabel idLabel = null;
    private JTextField protectionGroupId = null;
    private JLabel jLabel = null;
    private JTextField protectionGroupName = null;
    private JPanel descriptionPanel = null;
    private JScrollPane jScrollPane = null;
    private JTextArea description = null;
    private JPanel buttonPanel = null;
    private JButton addModify = null;
    private JButton cancel = null;
    private ProgressPanel progress = null;
    private JLabel largeCountLabel = null;
    private JComboBox largeElementCount = null;
    private JLabel lastUpdatedLabel = null;
    private JTextField lastUpdated = null;


    /**
     * @param owner
     */
    public CreateModifyProtectionGroupDialog(Application application) {
        super(GridApplication.getContext().getApplication());
        this.application = application;
        initialize();

        this.setTitle("Create Protection Group");
    }


    public CreateModifyProtectionGroupDialog(ProtectionGroup pg) {
        super(GridApplication.getContext().getApplication());
        this.protectionGroup = pg;
        initialize();
        this.setTitle("Edit Protection Group");
    }


    public ProtectionGroup getProtectionGroup() {
        return protectionGroup;
    }


    public void createModifyProtectionGroup() {
        try {
            getAddModify().setEnabled(false);
            getCancel().setEnabled(false);
            if (protectionGroup == null) {
                getProgress().showProgress("Creating protection group...");
                this.protectionGroup = this.application.createProtectionGroup(Utils.clean(getProtectionGroupName()
                    .getText()), Utils.clean(getDescription().getText()));
                getProgress().stopProgress("Successfully created the protection group.");
                dispose();
            } else {
                getProgress().showProgress("Modifying protection group...");
                this.protectionGroup.setName(Utils.clean(getProtectionGroupName().getText()));
                this.protectionGroup.setDescription(Utils.clean(getDescription().getText()));
                Boolean largeElements = (Boolean) getLargeElementCount().getSelectedItem();
                this.protectionGroup.setLargeElementCount(largeElements.booleanValue());
                this.protectionGroup.modify();
                getProgress().stopProgress("Successfully modified the protection group.");
                dispose();
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
            getProgress().stopProgress("Error");
        } finally {
            getAddModify().setEnabled(true);
            getCancel().setEnabled(true);
        }
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(500, 350);
        this.setContentPane(getJContentPane());
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridy = 4;
            gridBagConstraints7.weightx = 1.0D;
            gridBagConstraints7.weighty = 0.0D;
            gridBagConstraints7.gridx = 0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridy = 3;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.weightx = 1.0D;
            gridBagConstraints5.weighty = 1.0D;
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getTitlePanel(), gridBagConstraints1);
            jContentPane.add(getInformationPanel(), gridBagConstraints2);
            jContentPane.add(getDescriptionPanel(), gridBagConstraints5);
            jContentPane.add(getButtonPanel(), gridBagConstraints6);
            jContentPane.add(getProgress(), gridBagConstraints7);
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
            if (this.protectionGroup != null) {
                titlePanel = new TitlePanel(this.protectionGroup.getName(), "Modify the protection group "
                    + this.protectionGroup.getName());
            } else {
                titlePanel = new TitlePanel("Create Protection Group",
                    "Create a protection group which can be used to group together protection elements");
            }
        }
        return titlePanel;
    }


    /**
     * This method initializes informationPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInformationPanel() {
        if (informationPanel == null) {
            GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
            gridBagConstraints71.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints71.gridy = 3;
            gridBagConstraints71.weightx = 1.0;
            gridBagConstraints71.anchor = GridBagConstraints.WEST;
            gridBagConstraints71.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints71.gridx = 1;
            GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
            gridBagConstraints61.gridx = 0;
            gridBagConstraints61.anchor = GridBagConstraints.WEST;
            gridBagConstraints61.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints61.gridy = 3;
            lastUpdatedLabel = new JLabel();
            lastUpdatedLabel.setText("Last Updated");
            GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
            gridBagConstraints51.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints51.gridy = 2;
            gridBagConstraints51.weightx = 1.0;
            gridBagConstraints51.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints51.anchor = GridBagConstraints.WEST;
            gridBagConstraints51.gridx = 1;
            GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
            gridBagConstraints41.gridx = 0;
            gridBagConstraints41.anchor = GridBagConstraints.WEST;
            gridBagConstraints41.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints41.gridy = 2;
            largeCountLabel = new JLabel();
            largeCountLabel.setText("Large Element Count");
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.anchor = GridBagConstraints.WEST;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridx = 0;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridx = 1;
            gridBagConstraints21.gridy = 1;
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.weightx = 1.0;
            jLabel = new JLabel();
            jLabel.setText("Name");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridy = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            idLabel = new JLabel();
            idLabel.setText("Id");
            informationPanel = new JPanel();
            informationPanel.setLayout(new GridBagLayout());
            if (protectionGroup != null) {
                informationPanel.add(idLabel, gridBagConstraints1);
                informationPanel.add(getProtectionGroupId(), gridBagConstraints);
                informationPanel.add(largeCountLabel, gridBagConstraints41);
                informationPanel.add(getLargeElementCount(), gridBagConstraints51);
                informationPanel.add(lastUpdatedLabel, gridBagConstraints61);
                informationPanel.add(getLastUpdated(), gridBagConstraints71);
            }
            informationPanel.add(jLabel, gridBagConstraints3);
            informationPanel.add(getProtectionGroupName(), gridBagConstraints21);

        }
        return informationPanel;
    }


    /**
     * This method initializes protectionGroupId
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionGroupId() {
        if (protectionGroupId == null) {
            protectionGroupId = new JTextField();
            if (protectionGroup != null) {
                protectionGroupId.setText(String.valueOf(protectionGroup.getId()));
                protectionGroupId.setEditable(false);
            }
        }
        return protectionGroupId;
    }


    /**
     * This method initializes protectionGroupName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProtectionGroupName() {
        if (protectionGroupName == null) {
            protectionGroupName = new JTextField();
            if (protectionGroup != null) {
                protectionGroupName.setText(protectionGroup.getName());
            }
        }
        return protectionGroupName;
    }


    /**
     * This method initializes descriptionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDescriptionPanel() {
        if (descriptionPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0;
            descriptionPanel = new JPanel();
            descriptionPanel.setLayout(new GridBagLayout());
            descriptionPanel.add(getJScrollPane(), gridBagConstraints4);
            descriptionPanel.setBorder(BorderFactory.createTitledBorder(null, "Description",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return descriptionPanel;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getDescription());
        }
        return jScrollPane;
    }


    /**
     * This method initializes description
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getDescription() {
        if (description == null) {
            description = new JTextArea();
            description.setLineWrap(true);
            description.setWrapStyleWord(true);
            if (protectionGroup != null) {
                description.setText(protectionGroup.getDescription());
            }
        }
        return description;
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
            buttonPanel.add(getAddModify(), null);
            buttonPanel.add(getCancel(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addModify
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddModify() {
        if (addModify == null) {
            addModify = new JButton();
            addModify.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            createModifyProtectionGroup();

                        }
                    };
                    try {
                        GridApplication.getContext().executeInBackground(runner);
                    } catch (Exception t) {
                        t.getMessage();
                    }
                }
            });
            if (protectionGroup == null) {
                addModify.setText("Create");
            } else {
                addModify.setText("Modify");
            }
        }
        return addModify;
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


    /**
     * This method initializes largeElementCount
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getLargeElementCount() {
        if (largeElementCount == null) {
            largeElementCount = new JComboBox();
            largeElementCount.addItem(Boolean.FALSE);
            largeElementCount.addItem(Boolean.TRUE);
            if (protectionGroup != null) {
                largeElementCount.setSelectedItem(Boolean.valueOf(protectionGroup.hasLargeElementCount()));
            }
        }
        return largeElementCount;
    }


    /**
     * This method initializes lastUpdated
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLastUpdated() {
        if (lastUpdated == null) {
            lastUpdated = new JTextField();
            lastUpdated.setEditable(false);
            if (protectionGroup != null) {
                Calendar c = protectionGroup.getLastUpdated();
                if (c != null) {
                    getLastUpdated().setText(DateFormat.getDateInstance().format(c.getTime()));
                }
            }
        }
        return lastUpdated;
    }

}
