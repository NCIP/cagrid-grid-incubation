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
package org.cagrid.gaards.ui.csm.groups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.bean.GroupSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Group;
import org.cagrid.gaards.csm.client.LocalGroup;
import org.cagrid.gaards.csm.client.RemoteGroup;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.LookAndFeel;
import org.cagrid.grape.utils.ErrorDialog;


public class GroupsPanel extends JPanel {

    private static final String LOCAL_GROUP = "LocalGroup";
    private static final String REMOTE_GROUP = "RemoteGroup";

    private static final long serialVersionUID = 1L;
    private JSplitPane jSplitPane = null;
    private JPanel leftPanel = null;
    private JPanel rightPanel = null;
    private JPanel searchPanel = null;
    private JLabel jLabel = null;
    private JTextField groupName = null;
    private JButton groupSearch = null;
    private JScrollPane jScrollPane = null;
    private JPanel groupsPanel = null;
    private GroupsList groups = null;
    private Application application;
    private ProgressPanel progress;
    private JPanel groupPanel = null;
    private LocalGroupPanel localGroup = null;
    private RemoteGroupPanel remoteGroup = null;
    private CardLayout groupContext;
    private JPanel membersPanel = null;
    private JScrollPane memberPane = null;
    private MembersTable members = null;
    private JPanel groupActionPanel = null;
    private JButton addGroup = null;
    private JButton linkRemoteGroup = null;
    private JButton removeGroup = null;
    private JButton unlinkRemoteGroup = null;
    private Group currentGroup; // @jve:decl-index=0:
    private boolean successfullSearch = false;
    private JPanel membersActionPanel = null;
    private JButton addMember = null;
    private JButton removeMember = null;


    /**
     * This is the default constructor
     */
    public GroupsPanel(Application application, ProgressPanel progress) {
        super();
        this.application = application;
        this.progress = progress;
        initialize();
        enableGroupActions();
    }


    private void disableAll() {
        getGroupSearch().setEnabled(false);
        getGroups().setEnabled(false);
        disableAllGroupActions();
    }


    private void disableAllGroupActions() {
        getAddGroup().setEnabled(false);
        getRemoveGroup().setEnabled(false);
        getLinkRemoteGroup().setEnabled(false);
        getUnlinkRemoteGroup().setEnabled(false);
        getAddMember().setEnabled(false);
        getRemoveMember().setEnabled(false);
    }


    private void enableGroupActions() {
        getAddGroup().setEnabled(true);
        getLinkRemoteGroup().setEnabled(true);
        if (currentGroup != null) {
            if (currentGroup instanceof LocalGroup) {
                getUnlinkRemoteGroup().setEnabled(false);
                getRemoveGroup().setEnabled(true);
                getAddMember().setEnabled(true);
                getRemoveMember().setEnabled(true);
            } else if (currentGroup instanceof RemoteGroup) {
                getUnlinkRemoteGroup().setEnabled(true);
                getRemoveGroup().setEnabled(false);
                getAddMember().setEnabled(false);
                getRemoveMember().setEnabled(false);
            }
        } else {
            getUnlinkRemoteGroup().setEnabled(false);
            getRemoveGroup().setEnabled(false);
            getAddMember().setEnabled(false);
            getRemoveMember().setEnabled(false);
        }
    }


    private void enableAll() {
        getGroupSearch().setEnabled(true);
        getGroups().setEnabled(true);
        enableGroupActions();
    }


    private void clearAllGroups(final boolean clearSelectedGroup) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getGroups().clearGroups();
                if (clearSelectedGroup) {
                    currentGroup = null;
                    getLocalGroup().clearAll();
                    getRemoteGroup().clearAll();
                    getMembers().clearTable();
                }

            }
        });

    }


    public void setGroup(GroupEntry grp) {
        disableAll();
        getLocalGroup().clearAll();
        getRemoteGroup().clearAll();
        currentGroup = grp.getGroup();
        if (currentGroup instanceof LocalGroup) {
            getLocalGroup().setGroup((LocalGroup) currentGroup);
            this.groupContext.show(getGroupPanel(), LOCAL_GROUP);
            memberSearch(currentGroup);
        } else if (currentGroup instanceof RemoteGroup) {
            getRemoteGroup().setGroup((RemoteGroup) currentGroup);
            this.groupContext.show(getGroupPanel(), REMOTE_GROUP);
            memberSearch(currentGroup);
        }
        enableAll();
    }


    private synchronized void memberSearch(Group grp) {
        this.progress.showProgress("Searching for members...");
        getMembers().clearTable();
        try {
            List<String> list = grp.getMembers();

            for (int i = 0; i < list.size(); i++) {
                getMembers().addMember(list.get(i));
            }

            this.progress.stopProgress(list.size() + " member(s) found.");

        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        }
    }


    private synchronized void groupSearch() {
        groupSearch(true);
    }


    private synchronized void groupSearch(boolean clearLocalGroup) {
        this.progress.showProgress("Searching...");
        clearAllGroups(clearLocalGroup);
        try {
            GroupSearchCriteria search = new GroupSearchCriteria();
            search.setApplicationId(this.application.getId());
            search.setName(Utils.clean(getGroupName().getText()));
            final List<Group> list = this.application.getGroups(search);
            getGroups().setGroups(list);
            successfullSearch = true;
            this.progress.stopProgress(list.size() + " groups(s) found.");

        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            enableAll();
        }
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        this.setSize(700, 500);
        this.setLayout(new GridBagLayout());
        this.add(getJSplitPane(), gridBagConstraints);
    }


    /**
     * This method initializes jSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private JSplitPane getJSplitPane() {
        if (jSplitPane == null) {
            jSplitPane = new JSplitPane();
            jSplitPane.setLeftComponent(getLeftPanel());
            jSplitPane.setRightComponent(getRightPanel());
            jSplitPane.setDividerLocation(300);

        }
        return jSplitPane;
    }


    /**
     * This method initializes leftPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLeftPanel() {
        if (leftPanel == null) {
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.weightx = 1.0D;
            gridBagConstraints9.gridy = 2;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.weightx = 1.0D;
            gridBagConstraints5.weighty = 1.0D;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridy = 0;
            leftPanel = new JPanel();
            leftPanel.setLayout(new GridBagLayout());
            leftPanel.add(getSearchPanel(), gridBagConstraints1);
            leftPanel.add(getGroupsPanel(), gridBagConstraints5);
            leftPanel.add(getGroupActionPanel(), gridBagConstraints9);
        }
        return leftPanel;
    }


    /**
     * This method initializes rightPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRightPanel() {
        if (rightPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.weightx = 1.0D;
            gridBagConstraints7.weighty = 1.0D;
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.gridy = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.weightx = 1.0D;
            gridBagConstraints6.gridy = 0;
            rightPanel = new JPanel();
            rightPanel.setLayout(new GridBagLayout());
            rightPanel.add(getGroupPanel(), gridBagConstraints6);
            rightPanel.add(getMembersPanel(), gridBagConstraints7);
        }
        return rightPanel;
    }


    /**
     * This method initializes searchPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.weightx = 1.0;
            jLabel = new JLabel();
            jLabel.setText("Name");
            searchPanel = new JPanel();
            searchPanel.setLayout(new GridBagLayout());
            searchPanel.add(jLabel, new GridBagConstraints());
            searchPanel.add(getGroupName(), gridBagConstraints2);
            searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Group Search",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            searchPanel.add(getGroupSearch(), gridBagConstraints3);
        }
        return searchPanel;
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
     * This method initializes groupSearch
     * 
     * @return javax.swing.JButton
     */
    private JButton getGroupSearch() {
        if (groupSearch == null) {
            groupSearch = new JButton();
            groupSearch.setText("Search");
            groupSearch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    disableAll();
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
        return groupSearch;
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
     * This method initializes groupsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupsPanel() {
        if (groupsPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.BOTH;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            groupsPanel = new JPanel();
            groupsPanel.setLayout(new GridBagLayout());
            groupsPanel.add(getJScrollPane(), gridBagConstraints4);
            groupsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Groups",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
        }
        return groupsPanel;
    }


    /**
     * This method initializes groups
     * 
     * @return javax.swing.JList
     */
    private GroupsList getGroups() {
        if (groups == null) {
            groups = new GroupsList();
            groups.addListSelectionListener(new GroupListener(this));
        }
        return groups;
    }


    /**
     * This method initializes groupPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupPanel() {
        if (groupPanel == null) {
            groupPanel = new JPanel();
            groupContext = new CardLayout();
            groupPanel.setLayout(groupContext);
            groupPanel.add(getLocalGroup(), getLocalGroup().getName());
            groupPanel.add(getRemoteGroup(), getRemoteGroup().getName());
            groupContext.show(groupPanel, LOCAL_GROUP);
        }
        return groupPanel;
    }


    /**
     * This method initializes localGroup
     * 
     * @return javax.swing.JPanel
     */
    private LocalGroupPanel getLocalGroup() {
        if (localGroup == null) {
            localGroup = new LocalGroupPanel(this);
            localGroup.setName(LOCAL_GROUP);
        }
        return localGroup;
    }


    /**
     * This method initializes remoteGroup
     * 
     * @return javax.swing.JPanel
     */
    private RemoteGroupPanel getRemoteGroup() {
        if (remoteGroup == null) {
            remoteGroup = new RemoteGroupPanel();
            remoteGroup.setName(REMOTE_GROUP);
        }
        return remoteGroup;
    }


    /**
     * This method initializes membersPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMembersPanel() {
        if (membersPanel == null) {
            GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
            gridBagConstraints14.gridx = 0;
            gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints14.weightx = 1.0D;
            gridBagConstraints14.gridy = 1;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.BOTH;
            gridBagConstraints8.weighty = 1.0;
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.gridy = 0;
            gridBagConstraints8.weightx = 1.0;
            membersPanel = new JPanel();
            membersPanel.setLayout(new GridBagLayout());
            membersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Members",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, LookAndFeel.getPanelLabelColor()));
            membersPanel.add(getMemberPane(), gridBagConstraints8);
            membersPanel.add(getMembersActionPanel(), gridBagConstraints14);
        }
        return membersPanel;
    }


    /**
     * This method initializes memberPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getMemberPane() {
        if (memberPane == null) {
            memberPane = new JScrollPane();
            memberPane.setViewportView(getMembers());
        }
        return memberPane;
    }


    /**
     * This method initializes members
     * 
     * @return javax.swing.JTable
     */
    private MembersTable getMembers() {
        if (members == null) {
            members = new MembersTable();
        }
        return members;
    }


    /**
     * This method initializes groupActionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGroupActionPanel() {
        if (groupActionPanel == null) {
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 1;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.gridy = 1;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.gridy = 1;
            gridBagConstraints12.gridwidth = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.gridx = 1;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridy = 0;
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridx = 0;
            groupActionPanel = new JPanel();
            groupActionPanel.setLayout(new GridBagLayout());
            groupActionPanel.add(getAddGroup(), gridBagConstraints10);
            groupActionPanel.add(getRemoveGroup(), gridBagConstraints11);
            groupActionPanel.add(getLinkRemoteGroup(), gridBagConstraints12);
            groupActionPanel.add(getUnlinkRemoteGroup(), gridBagConstraints13);
        }
        return groupActionPanel;
    }


    /**
     * This method initializes addGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddGroup() {
        if (addGroup == null) {
            addGroup = new JButton();
            addGroup.setText("Create Group");
            addGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    CreateGroupWindow window = new CreateGroupWindow(application);
                    window.setModal(true);
                    GridApplication.getContext().showDialog(window);
                    if (window.wasGroupCreated() && (successfullSearch)) {
                        disableAll();
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

                }
            });
        }
        return addGroup;
    }


    /**
     * This method initializes linkRemoteGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getLinkRemoteGroup() {
        if (linkRemoteGroup == null) {
            linkRemoteGroup = new JButton();
            linkRemoteGroup.setText("Link Group");

            linkRemoteGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    LinkRemoteGroupWindow window = new LinkRemoteGroupWindow(application);
                    window.setModal(true);
                    GridApplication.getContext().showDialog(window);
                    if (window.wasRemoteGroupLinked() && (successfullSearch)) {
                        disableAll();
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

                }
            });
        }
        return linkRemoteGroup;
    }


    /**
     * This method initializes removeGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveGroup() {
        if (removeGroup == null) {
            removeGroup = new JButton();
            removeGroup.setText("Remove Group");
            removeGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    disableAll();
                    Runner runner = new Runner() {
                        public void execute() {
                            removeGroup();
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
        return removeGroup;
    }


    private void removeGroup() {
        this.progress.showProgress("Removing group...");
        try {
            GroupEntry entry = (GroupEntry) this.getGroups().getSelectedValue();
            if (entry.getGroup() instanceof LocalGroup) {
                application.removeGroup((LocalGroup) entry.getGroup());
                groupSearch();
                this.progress.stopProgress("Successfully removed the group.");
            } else {
                this.progress.stopProgress("Cannot remove a remote group.");
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            enableAll();
        }
    }


    private void unlinkRemoteGroup() {
        this.progress.showProgress("Unlinking group...");
        try {
            GroupEntry entry = (GroupEntry) this.getGroups().getSelectedValue();
            if (entry.getGroup() instanceof RemoteGroup) {
                application.unlinkRemoteGroup((RemoteGroup) entry.getGroup());
                groupSearch();
                this.progress.stopProgress("Successfully unlinked the remote group.");
            } else {
                this.progress.stopProgress("Cannot unlink a local group.");
            }
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            enableAll();
        }
    }


    /**
     * This method initializes unlinkRemoteGroup
     * 
     * @return javax.swing.JButton
     */
    private JButton getUnlinkRemoteGroup() {
        if (unlinkRemoteGroup == null) {
            unlinkRemoteGroup = new JButton();
            unlinkRemoteGroup.setText("Unlink Group");
            unlinkRemoteGroup.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    disableAll();
                    Runner runner = new Runner() {
                        public void execute() {
                            unlinkRemoteGroup();
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
        return unlinkRemoteGroup;
    }


    protected void modifyGroup(LocalGroup grp) {
        disableAll();
        this.progress.showProgress("Modifying Group...");
        try {
            grp.modify();
            groupSearch(false);
            this.progress.stopProgress("Successfully modified group.");
        } catch (Exception e) {
            ErrorDialog.showError(e);
            this.progress.stopProgress("Error");
        } finally {
            enableAll();
        }
    }


    /**
     * This method initializes membersActionPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMembersActionPanel() {
        if (membersActionPanel == null) {
            membersActionPanel = new JPanel();
            membersActionPanel.setLayout(new GridBagLayout());
            membersActionPanel.add(getAddMember(), new GridBagConstraints());
            membersActionPanel.add(getRemoveMember(), new GridBagConstraints());
        }
        return membersActionPanel;
    }


    /**
     * This method initializes addMember
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddMember() {
        if (addMember == null) {
            addMember = new JButton();
            addMember.setText("Add");
            addMember.setEnabled(false);
            addMember.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (currentGroup instanceof LocalGroup) {
                        disableAll();
                        AddMemberWindow window = new AddMemberWindow((LocalGroup) currentGroup);
                        window.setModal(true);
                        GridApplication.getContext().showDialog(window);
                        if (window.wasMemberAdded()) {

                            Runner runner = new Runner() {
                                public void execute() {
                                    memberSearch(currentGroup);
                                    enableAll();
                                    progress.stopProgress("Successfully added member to the group.");
                                }
                            };
                            try {
                                GridApplication.getContext().executeInBackground(runner);
                            } catch (Exception t) {
                                t.getMessage();
                            }
                        }
                    }
                }
            });
        }
        return addMember;
    }


    /**
     * This method initializes removeMember
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveMember() {
        if (removeMember == null) {
            removeMember = new JButton();
            removeMember.setText("Remove");
            removeMember.setEnabled(false);
            removeMember.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (currentGroup instanceof LocalGroup) {
                        disableAll();
                        Runner runner = new Runner() {
                            public void execute() {
                                progress.showProgress("Removing member from group...");
                                try {
                                    LocalGroup local = (LocalGroup) currentGroup;
                                    local.removeMember(getMembers().getSelectedMember());
                                    memberSearch(currentGroup);
                                    enableAll();
                                    progress.stopProgress("Successfully removed a member from the group.");
                                } catch (Exception e) {
                                    ErrorDialog.showError(e);
                                    progress.stopProgress("Error");
                                } finally {
                                    enableAll();
                                }
                            }
                        };
                        try {
                            GridApplication.getContext().executeInBackground(runner);
                        } catch (Exception t) {
                            t.getMessage();
                        }
                    }

                }
            });
        }
        return removeMember;
    }

}
