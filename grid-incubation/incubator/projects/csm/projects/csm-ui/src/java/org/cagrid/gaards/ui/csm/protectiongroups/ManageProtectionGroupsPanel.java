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
package org.cagrid.gaards.ui.csm.protectiongroups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.gaards.csm.client.ProtectionGroup;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;
import org.cagrid.gaards.ui.csm.CSMUIUtils;
import org.cagrid.gaards.ui.csm.protectionelements.ProtectionElementSearchI;
import org.cagrid.gaards.ui.csm.protectionelements.ProtectionElementSearchPanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class ManageProtectionGroupsPanel extends JPanel implements ProtectionElementSearchI {

    private Application csmApp;
    private static final long serialVersionUID = 1L;
    private JPanel protectionGroupsPanel = null;
    private JPanel protectionElementsPanel = null;
    private JPanel buttonsPanel = null;
    private JButton addButton = null;
    private JButton removeButton = null;
    private JTextField searchProtectionGroupNameTextField = null;
    private JButton searchProtectionGroupsButton = null;
    private ProtectionElementSearchPanel searchPanel = null;
    private ProtectionGroupTree protectionGroupsTree = null;
    private JPanel pgSearchPanel = null;
    private JLabel pgNameLabel = null;
    private JSplitPane groupsSplitPane = null;
    private ProgressPanel progress = null;
    private JScrollPane jScrollPane = null;
    private JPanel groupsPanel = null;
    private JPanel currentPEPanel = null;
    private JPanel unusedPEPanel = null;
    private JScrollPane jScrollPane1 = null;
    private ProtectionElementsTable currentProtectionElements = null;
    private JScrollPane jScrollPane2 = null;
    private ProtectionElementsTable unusedProtectionElements = null;
    private List<ProtectionElement> search;
    private List<ProtectionElement> usedProtectionElements;
    private ProtectionGroup currentGroup; // @jve:decl-index=0:
    private JPanel groupsActionPanel = null;
    private JButton createProtectionGroup = null;
    private JButton removeProtectionGroup = null;
    private JButton modifyProtectionGroup = null;
    private JButton loadChildren = null;


    /**
     * This is the default constructor
     */
    public ManageProtectionGroupsPanel(Application app, ProgressPanel progress) {
        super();
        this.csmApp = app;
        this.progress = progress;
        initialize();
        unsetProtectionGroup();
    }


    private void toggleAccess(boolean enabled) {
        getSearchPanel().toggleAccess(enabled);
        getSearchProtectionGroupNameTextField().setEnabled(enabled);
        getSearchProtectionGroupsButton().setEnabled(enabled);
        getCreateProtectionGroup().setEnabled(enabled);
        getRemoveProtectionGroup().setEnabled(enabled);
        getModifyProtectionGroup().setEnabled(enabled);
        if (currentGroup == null) {
            getAddButton().setEnabled(false);
            getRemoveButton().setEnabled(false);
        } else {
            getAddButton().setEnabled(enabled && getUnusedProtectionElements().getSelectedRowCount()>0);
            getRemoveButton().setEnabled(enabled && getCurrentProtectionElements().getSelectedRowCount()>0);

        }
    }


    private void unsetProtectionGroup() {
        this.currentGroup = null;
        getUnusedProtectionElements().clearTable();
        getCurrentProtectionElements().clearTable();
        getAddButton().setEnabled(false);
        getRemoveButton().setEnabled(false);
    }


    private void setSelectedProtectionGroup(ProtectionGroup pg) {
        toggleAccess(false);
        progress.showProgress("Searching...");
        try {
            unsetProtectionGroup();
            this.currentGroup = pg;
            List<ProtectionElement> list = this.currentGroup.getProtectionElements();
            this.usedProtectionElements = CSMUIUtils.sortProtectionElements(list);
            List<ProtectionElement> filtered = filterProtectionElements(this.usedProtectionElements, this.search);
            List<ProtectionElement> sorted = CSMUIUtils.sortProtectionElements(filtered);
            getCurrentProtectionElements().addProtectionElements(this.usedProtectionElements);
            getUnusedProtectionElements().addProtectionElements(sorted);
            progress.stopProgress(usedProtectionElements.size() + " protection elements found.");
        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    private void addProtectionElementToProtectionGroup() {
        toggleAccess(false);
        progress.showProgress("Adding protection element to protection group...");
        try {
            ProtectionElement pe = this.getUnusedProtectionElements().getSelectedProtectionElement();
            List<ProtectionElement> list = new ArrayList<ProtectionElement>();
            list.add(pe);
            currentGroup.assignProtectionElements(list);
            setSelectedProtectionGroup(currentGroup);
            progress.stopProgress("Successfully add the selected protection element to the protection group.");
        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    private void remoteProtectionElementFromProtectionGroup() {
        toggleAccess(false);
        progress.showProgress("Removing protection element from protection group...");
        try {
            ProtectionElement pe = this.getCurrentProtectionElements().getSelectedProtectionElement();
            List<ProtectionElement> list = new ArrayList<ProtectionElement>();
            list.add(pe);
            currentGroup.unassignProtectionElements(list);
            setSelectedProtectionGroup(currentGroup);
            progress.stopProgress("Successfully removed the selected protection element from the protection group.");
        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    public void protectionElementSearch() {
        toggleAccess(false);
        try {
            progress.showProgress("Searching...");
            getUnusedProtectionElements().clearTable();
            this.search = getSearchPanel().performSearch();
            List<ProtectionElement> filtered = filterProtectionElements(this.usedProtectionElements, this.search);
            List<ProtectionElement> sorted = CSMUIUtils.sortProtectionElements(filtered);
            getUnusedProtectionElements().addProtectionElements(sorted);
            progress.stopProgress(search.size() + " protection elements found.");
        } catch (Exception ex) {
            ErrorDialog.showError(ex);
            progress.stopProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    private List<ProtectionElement> filterProtectionElements(List<ProtectionElement> used, List<ProtectionElement> list) {
        if (list == null) {
            return new ArrayList<ProtectionElement>();
        } else {
            if (used == null) {
                return list;
            } else {
                List<ProtectionElement> filtered = new ArrayList<ProtectionElement>();
                for (int i = 0; i < list.size(); i++) {
                    boolean found = false;
                    for (int j = 0; j < used.size(); j++) {
                        if (list.get(i).getId() == used.get(j).getId()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        filtered.add(list.get(i));
                    }
                }
                return filtered;
            }
        }
    }


   

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
        gridBagConstraints19.fill = GridBagConstraints.BOTH;
        gridBagConstraints19.weighty = 1.0;
        gridBagConstraints19.gridx = 0;
        gridBagConstraints19.gridy = 0;
        gridBagConstraints19.weightx = 1.0;
        this.setSize(700, 500);
        this.setLayout(new GridBagLayout());
        this.add(getGroupsSplitPane(), gridBagConstraints19);
    }


    /**
     * This method initializes protectionGroupsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProtectionGroupsPanel() {
        if (protectionGroupsPanel == null) {
            GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
            gridBagConstraints25.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints25.gridy = 2;
            gridBagConstraints25.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints25.weightx = 1.0D;
            gridBagConstraints25.gridx = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.fill = GridBagConstraints.BOTH;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.weighty = 1.0D;
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
            gridBagConstraints17.gridx = 0;
            gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints17.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints17.gridy = 0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.weightx = 1.0;
            gridBagConstraints11.gridx = 1;
            protectionGroupsPanel = new JPanel();
            protectionGroupsPanel.setLayout(new GridBagLayout());

            protectionGroupsPanel.add(getPgSearchPanel(), gridBagConstraints17);
            protectionGroupsPanel.add(getGroupsPanel(), gridBagConstraints3);
            protectionGroupsPanel.add(getGroupsActionPanel(), gridBagConstraints25);
        }
        return protectionGroupsPanel;
    }


    /**
     * This method initializes protectionElementsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProtectionElementsPanel() {
        if (protectionElementsPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.weighty = 1.0D;
            gridBagConstraints1.gridy = 2;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.weighty = 1.0D;
            gridBagConstraints6.gridy = 0;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.fill = GridBagConstraints.BOTH;
            gridBagConstraints13.gridwidth = 1;
            gridBagConstraints13.weighty = 0.0D;
            gridBagConstraints13.weightx = 1.0D;
            gridBagConstraints13.gridy = 3;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.NONE;
            gridBagConstraints2.weighty = 0.0D;
            gridBagConstraints2.gridy = 1;
            protectionElementsPanel = new JPanel();
            protectionElementsPanel.setLayout(new GridBagLayout());
            protectionElementsPanel.setEnabled(false);
            protectionElementsPanel.add(getButtonsPanel(), gridBagConstraints2);
            protectionElementsPanel.add(getSearchPanel(), gridBagConstraints13);
            protectionElementsPanel.add(getCurrentPEPanel(), gridBagConstraints6);
            protectionElementsPanel.add(getUnusedPEPanel(), gridBagConstraints1);
        }
        return protectionElementsPanel;
    }


    /**
     * This method initializes buttonsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonsPanel() {
        if (buttonsPanel == null) {
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 1;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 0;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 0;
            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new GridBagLayout());
            buttonsPanel.add(getAddButton(), gridBagConstraints10);
            buttonsPanel.add(getRemoveButton(), gridBagConstraints5);
        }
        return buttonsPanel;
    }


    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setIcon(CSMLookAndFeel.getAddProtectionElementToProtectionGroupIcon());
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            addProtectionElementToProtectionGroup();

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
        return addButton;
    }


    /**
     * This method initializes removeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveButton() {
        if (removeButton == null) {
            removeButton = new JButton();
            removeButton.setIcon(CSMLookAndFeel.getRemoveProtectionElementFromProtectionGroupIcon());
            removeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    remoteProtectionElementFromProtectionGroup();
                }
            });
        }
        return removeButton;
    }


    /**
     * This method initializes searchProtectionGroupNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSearchProtectionGroupNameTextField() {
        if (searchProtectionGroupNameTextField == null) {
            searchProtectionGroupNameTextField = new JTextField();
        }
        return searchProtectionGroupNameTextField;
    }


    /**
     * This method initializes searchProtectionGroupsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSearchProtectionGroupsButton() {
        if (searchProtectionGroupsButton == null) {
            searchProtectionGroupsButton = new JButton();
            searchProtectionGroupsButton.setText("Search");
            searchProtectionGroupsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {

                        public void execute() {
                            try {
                                progress.showProgress("Searching...");
                                toggleAccess(false);
                                ProtectionGroupSearchCriteria pgsc = new ProtectionGroupSearchCriteria();
                                pgsc.setApplicationId(csmApp.getId());
                                pgsc.setName(Utils.clean(getSearchProtectionGroupNameTextField().getText()));
                                List<ProtectionGroup> pgs = null;
                                pgs = csmApp.getProtectionGroups(pgsc);
                                getProtectionGroupsTree().setProtectionGroups(pgs);
                                progress.stopProgress(pgs.size() + " Protection Groups Found.");

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
        return searchProtectionGroupsButton;
    }


    /**
     * This method initializes searchPanel
     * 
     * @return javax.swing.JPanel
     */
    private ProtectionElementSearchPanel getSearchPanel() {
        if (searchPanel == null) {
            searchPanel = new ProtectionElementSearchPanel(this.csmApp, this);
        }
        return searchPanel;
    }


    /**
     * This method initializes protectionGroupsTree
     * 
     * @return javax.swing.JTree
     */
    protected ProtectionGroupTree getProtectionGroupsTree() {

        if (protectionGroupsTree == null) {
            protectionGroupsTree = new ProtectionGroupTree(this, csmApp);
            protectionGroupsTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
                    if (e.getNewLeadSelectionPath() != null
                        && e.getNewLeadSelectionPath().getLastPathComponent() != null
                        && e.getNewLeadSelectionPath().getLastPathComponent() instanceof ProtectionGroupTreeNode) {
                        getProtectionElementsPanel().setEnabled(true);
                        ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) e.getNewLeadSelectionPath()
                            .getLastPathComponent();
                        final ProtectionGroup group = node.getProtectionGroup();
                        Runner runner = new Runner() {

                            public void execute() {
                                setSelectedProtectionGroup(group);

                            }
                        };
                        try {
                            GridApplication.getContext().executeInBackground(runner);
                        } catch (Exception t) {
                            t.getMessage();
                        }
                    } else {
                        getProtectionElementsPanel().setEnabled(false);
                    }
                }
            });
        }
        return protectionGroupsTree;
    }


    /**
     * This method initializes pgSearchPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPgSearchPanel() {
        if (pgSearchPanel == null) {
            GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
            gridBagConstraints18.gridx = 0;
            gridBagConstraints18.gridy = 0;
            pgNameLabel = new JLabel();
            pgNameLabel.setText("Name");
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 1;
            gridBagConstraints12.gridwidth = 1;
            gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints12.weightx = 1.0D;
            gridBagConstraints12.gridy = 0;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.gridwidth = 2;
            gridBagConstraints21.gridy = 1;
            pgSearchPanel = new JPanel();
            pgSearchPanel.setLayout(new GridBagLayout());
            pgSearchPanel.setBorder(BorderFactory.createTitledBorder(null, "Protection Group Search",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            pgSearchPanel.add(getSearchProtectionGroupNameTextField(), gridBagConstraints12);
            pgSearchPanel.add(getSearchProtectionGroupsButton(), gridBagConstraints21);
            pgSearchPanel.add(pgNameLabel, gridBagConstraints18);
        }
        return pgSearchPanel;
    }


    /**
     * This method initializes groupsSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getGroupsSplitPane() {
        if (groupsSplitPane == null) {
            groupsSplitPane = new JSplitPane();
            groupsSplitPane.setDividerLocation(275);
            groupsSplitPane.setLeftComponent(getProtectionGroupsPanel());
            groupsSplitPane.setRightComponent(getProtectionElementsPanel());
        }
        return groupsSplitPane;
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setPreferredSize(new Dimension(200, 550));
            jScrollPane.setViewportView(getProtectionGroupsTree());
        }
        return jScrollPane;
    }


    /**
     * This method initializes groupsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupsPanel() {
        if (groupsPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            groupsPanel = new JPanel();
            groupsPanel.setLayout(new GridBagLayout());
            groupsPanel.setBorder(BorderFactory.createTitledBorder(null, "Protection Groups",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
            groupsPanel.add(getJScrollPane(), gridBagConstraints);
        }
        return groupsPanel;
    }


    /**
     * This method initializes currentPEPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCurrentPEPanel() {
        if (currentPEPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.weightx = 1.0;
            currentPEPanel = new JPanel();
            currentPEPanel.setLayout(new GridBagLayout());
            currentPEPanel.setPreferredSize(new Dimension(500, 300));
            currentPEPanel.add(getJScrollPane1(), gridBagConstraints4);
            currentPEPanel.setBorder(BorderFactory.createTitledBorder(null, "Protection Elements in Protection Group",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return currentPEPanel;
    }


    /**
     * This method initializes unusedPEPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getUnusedPEPanel() {
        if (unusedPEPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.weighty = 1.0;
            gridBagConstraints7.weightx = 1.0;
            unusedPEPanel = new JPanel();
            unusedPEPanel.setLayout(new GridBagLayout());
            unusedPEPanel.setPreferredSize(new Dimension(500, 300));
            unusedPEPanel.add(getJScrollPane2(), gridBagConstraints7);
            unusedPEPanel.setBorder(BorderFactory.createTitledBorder(null, "Available Protection Elements",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, LookAndFeel
                    .getPanelLabelColor()));
        }
        return unusedPEPanel;
    }


    /**
     * This method initializes jScrollPane1
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
            jScrollPane1 = new JScrollPane();
            jScrollPane1.setViewportView(getCurrentProtectionElements());
        }
        return jScrollPane1;
    }


    /**
     * This method initializes currentProtectionElements
     * 
     * @return javax.swing.JTable
     */
    private ProtectionElementsTable getCurrentProtectionElements() {
        if (currentProtectionElements == null) {
            currentProtectionElements = new ProtectionElementsTable();
            currentProtectionElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    toggleAccess(true);
                }
            });
        }
        return currentProtectionElements;
    }


    /**
     * This method initializes jScrollPane2
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane2() {
        if (jScrollPane2 == null) {
            jScrollPane2 = new JScrollPane();
            jScrollPane2.setViewportView(getUnusedProtectionElements());
        }
        return jScrollPane2;
    }


    /**
     * This method initializes unusedProtectionElements
     * 
     * @return javax.swing.JTable
     */
    private ProtectionElementsTable getUnusedProtectionElements() {
        if (unusedProtectionElements == null) {
            unusedProtectionElements = new ProtectionElementsTable();
            unusedProtectionElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    toggleAccess(true);
                }
            });
        }
        return unusedProtectionElements;
    }


    /**
     * This method initializes groupsActionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupsActionPanel() {
        if (groupsActionPanel == null) {
            GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
            gridBagConstraints29.gridx = 1;
            gridBagConstraints29.gridy = 1;
            GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
            gridBagConstraints28.gridx = 0;
            gridBagConstraints28.gridy = 1;
            GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
            gridBagConstraints27.gridx = 1;
            gridBagConstraints27.gridy = 0;
            GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
            gridBagConstraints26.gridx = 0;
            gridBagConstraints26.gridy = 0;
            groupsActionPanel = new JPanel();
            groupsActionPanel.setLayout(new GridBagLayout());
            groupsActionPanel.add(getCreateProtectionGroup(), gridBagConstraints26);
            groupsActionPanel.add(getRemoveProtectionGroup(), gridBagConstraints27);
            groupsActionPanel.add(getModifyProtectionGroup(), gridBagConstraints28);
            groupsActionPanel.add(getLoadChildren(), gridBagConstraints29);
        }
        return groupsActionPanel;
    }


    /**
     * This method initializes createProtectionGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getCreateProtectionGroup() {
        if (createProtectionGroup == null) {
            createProtectionGroup = new JButton();
            createProtectionGroup.setText("Create");
            createProtectionGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    createProtectionGroup();
                }
            });
        }
        return createProtectionGroup;
    }


    protected void createProtectionGroup() {
        toggleAccess(false);
        CreateModifyProtectionGroupDialog dialog = new CreateModifyProtectionGroupDialog(csmApp);
        dialog.setModal(true);
        GridApplication.getContext().showDialog(dialog);
        if (dialog.getProtectionGroup() != null) {
            ProtectionGroup newGroup = dialog.getProtectionGroup();
            ProtectionGroupTreeNode node = new ProtectionGroupTreeNode(newGroup);
            ((DefaultTreeModel) getProtectionGroupsTree().getModel()).insertNodeInto(node,
                (DefaultMutableTreeNode) getProtectionGroupsTree().getModel().getRoot(), 0);
            TreePath path = new TreePath(((DefaultMutableTreeNode) getProtectionGroupsTree().getModel().getRoot())
                .getPath());
            getProtectionGroupsTree().expandRow(getProtectionGroupsTree().getRowForPath(path));
            progress.stopProgress("Protection Group successfully created.");
        }
        toggleAccess(true);

    }


    /**
     * This method initializes removeProtectionGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveProtectionGroup() {
        if (removeProtectionGroup == null) {
            removeProtectionGroup = new JButton();
            removeProtectionGroup.setText("Remove");
            removeProtectionGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            if ((getProtectionGroupsTree().getSelectionPath() != null)
                                && (getProtectionGroupsTree().getSelectionPath().getLastPathComponent() != null)
                                && (getProtectionGroupsTree().getSelectionPath().getLastPathComponent() instanceof ProtectionGroupTreeNode)) {
                                ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) getProtectionGroupsTree()
                                    .getSelectionPath().getLastPathComponent();
                                removeProtectionGroup(node);
                            } else {
                                ErrorDialog.showError("Please select a protection group to remove.");
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
        return removeProtectionGroup;
    }


    protected void modifyProtecionGroup(ProtectionGroup grp) {
        toggleAccess(false);
        CreateModifyProtectionGroupDialog dialog = new CreateModifyProtectionGroupDialog(grp);
        dialog.setModal(true);
        GridApplication.getContext().showDialog(dialog);
        ((DefaultTreeModel) getProtectionGroupsTree().getModel()).reload();
        if (dialog.getProtectionGroup() != null) {
            progress.stopProgress("Successfully modified the protection group.");
        }
        toggleAccess(true);
    }


    protected void removeProtectionGroup(ProtectionGroupTreeNode node) {
        toggleAccess(false);
        progress.showProgress("Removing protection group...");
        try {
            csmApp.removeProtectionGroup(node.getProtectionGroup().getId());
            ((DefaultTreeModel) getProtectionGroupsTree().getModel()).removeNodeFromParent(node);
            unsetProtectionGroup();
            progress.stopProgress("Successfully removed the protection group.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            progress.showProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }


    /**
     * This method initializes modifyProtectionGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getModifyProtectionGroup() {
        if (modifyProtectionGroup == null) {
            modifyProtectionGroup = new JButton();
            modifyProtectionGroup.setText("Modify");
            modifyProtectionGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if ((getProtectionGroupsTree().getSelectionPath() != null)
                        && (getProtectionGroupsTree().getSelectionPath().getLastPathComponent() != null)
                        && (getProtectionGroupsTree().getSelectionPath().getLastPathComponent() instanceof ProtectionGroupTreeNode)) {
                        ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) getProtectionGroupsTree()
                            .getSelectionPath().getLastPathComponent();
                        modifyProtecionGroup(node.getProtectionGroup());

                    } else {
                        ErrorDialog.showError("Please select a protection group to modify.");
                    }

                }
            });
        }
        return modifyProtectionGroup;
    }


    /**
     * This method initializes loadChildren
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadChildren() {
        if (loadChildren == null) {
            loadChildren = new JButton();
            loadChildren.setText("Load Children");
            loadChildren.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            if ((getProtectionGroupsTree().getSelectionPath() != null)
                                && (getProtectionGroupsTree().getSelectionPath().getLastPathComponent() != null)
                                && (getProtectionGroupsTree().getSelectionPath().getLastPathComponent() instanceof ProtectionGroupTreeNode)) {
                                loadProtectionGroupChildren();
                            } else {
                                ErrorDialog.showError("Please select a protection group to load the children for.");
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
        return loadChildren;
    }


    protected void loadProtectionGroupChildren() {
        toggleAccess(false);
        progress.showProgress("Loading children...");
        try {
            getProtectionGroupsTree().loadProtectionGroupChildren();
            progress.stopProgress("Successfully loaded the children of the protection group.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            progress.showProgress("Error");
        } finally {
            toggleAccess(true);
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
