package org.cagrid.gaards.ui.csm.protectiongroups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionGroup;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class ProtectionGroupSearchDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel titlePanel = null;
    private JPanel searchPanel = null;
    private JPanel resultsPanel = null;
    private JPanel buttonPanel = null;
    private JButton select = null;
    private JButton cancel = null;
    private ProgressPanel progress = null;
    private JLabel jLabel = null;
    private JTextField groupName = null;
    private JPanel searchActionPanel = null;
    private JButton search = null;
    private JScrollPane jScrollPane = null;
    private ProtectionGroupList groups = null;
    private Application application;
    private ProtectionGroup selectedProtectionGroup;


    /**
     * @param owner
     */
    public ProtectionGroupSearchDialog(Application application) {
        super(GridApplication.getContext().getApplication());
        this.application = application;
        initialize();
    }


    private void toggleAccess(boolean enabled) {
        getGroupName().setEditable(enabled);
        getSearch().setEnabled(enabled);
        getSelect().setEnabled(enabled);
        getCancel().setEnabled(enabled);
        getGroups().setEnabled(enabled);
    }


    private synchronized void groupSearch() {
        toggleAccess(false);
        this.progress.showProgress("Searching...");
        try {
            ProtectionGroupSearchCriteria search = new ProtectionGroupSearchCriteria();
            search.setApplicationId(this.application.getId());
            search.setName(Utils.clean(getGroupName().getText()));
            final List<ProtectionGroup> list = this.application.getProtectionGroups(search);
            getGroups().setProtectionGroups(list);
            this.progress.stopProgress(list.size() + " protection groups(s) found.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
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
        this.setSize(600, 400);
        this.setContentPane(getJContentPane());
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.gridx = 0;
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints31.weightx = 1.0D;
            gridBagConstraints31.gridy = 2;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.weightx = 1.0D;
            gridBagConstraints21.gridy = 5;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 4;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints2.gridy = 3;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.gridy = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getTitlePanel(), gridBagConstraints);
            jContentPane.add(getSearchPanel(), gridBagConstraints1);
            jContentPane.add(getResultsPanel(), gridBagConstraints2);
            jContentPane.add(getButtonPanel(), gridBagConstraints3);
            jContentPane.add(getProgress(), gridBagConstraints21);
            jContentPane.add(getSearchActionPanel(), gridBagConstraints31);
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
            titlePanel = new TitlePanel("Select Protection Group",
                "A protection group is a collection of protection elements in which access control can be enforced.");
        }
        return titlePanel;
    }


    /**
     * This method initializes searchPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.anchor = GridBagConstraints.WEST;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.weightx = 1.0D;
            gridBagConstraints5.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.fill = GridBagConstraints.NONE;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.gridy = 0;
            searchPanel = new JPanel();
            searchPanel.setLayout(new GridBagLayout());
            searchPanel.add(getJLabel(), gridBagConstraints4);
            searchPanel.add(getGroupName(), gridBagConstraints5);
            searchPanel.setBorder(BorderFactory.createTitledBorder(null, "Search Criteria",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return searchPanel;
    }


    /**
     * This method initializes resultsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getResultsPanel() {
        if (resultsPanel == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.weighty = 1.0;
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            resultsPanel = new JPanel();
            resultsPanel.setLayout(new GridBagLayout());
            resultsPanel.setBorder(BorderFactory.createTitledBorder(null, "Protection Groups", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            resultsPanel.add(getJScrollPane(), gridBagConstraints6);
        }
        return resultsPanel;
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
            buttonPanel.add(getSelect(), null);
            buttonPanel.add(getCancel(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes select
     * 
     * @return javax.swing.JButton
     */
    private JButton getSelect() {
        if (select == null) {
            select = new JButton();
            select.setText("Select");
            select.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ProtectionGroup entry = (ProtectionGroup) getGroups().getSelectedValue();
                    if (entry == null) {
                        ErrorDialog.showError("Please select a protection group.");
                    } else {
                        selectedProtectionGroup = entry;
                        dispose();
                    }
                }
            });
        }
        return select;
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
        }
        return progress;
    }


    /**
     * This method initializes jLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getJLabel() {
        if (jLabel == null) {
            jLabel = new JLabel();
            jLabel.setText("Protection Group Name");
        }
        return jLabel;
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
     * This method initializes searchActionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchActionPanel() {
        if (searchActionPanel == null) {
            searchActionPanel = new JPanel();
            searchActionPanel.setLayout(new FlowLayout());
            searchActionPanel.add(getSearch(), null);
        }
        return searchActionPanel;
    }


    /**
     * This method initializes search
     * 
     * @return javax.swing.JButton
     */
    private JButton getSearch() {
        if (search == null) {
            search = new JButton();
            search.setText("Search");
            search.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            groupSearch();
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
        return search;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getGroups());
        }
        return jScrollPane;
    }


    /**
     * This method initializes groups
     * 
     * @return javax.swing.JList
     */
    private ProtectionGroupList getGroups() {
        if (groups == null) {
            groups = new ProtectionGroupList();
        }
        return groups;
    }


    public ProtectionGroup getSelectedProtectionGroup() {
        return selectedProtectionGroup;
    }

}
