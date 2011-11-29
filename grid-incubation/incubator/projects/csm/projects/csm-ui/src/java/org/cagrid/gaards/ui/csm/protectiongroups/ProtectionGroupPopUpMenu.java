package org.cagrid.gaards.ui.csm.protectiongroups;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cagrid.grape.GridApplication;


public class ProtectionGroupPopUpMenu extends JPopupMenu {

   
    private boolean showLoadChildren = false;
    private String loadChildrenText = ""; // @jve:decl-index=0:
    private boolean showNewModify = false;
    private String newModifyText = ""; // @jve:decl-index=0:
    private JMenuItem loadChildGroups = null;
    private ProtectionGroupTree tree = null;
    private JMenuItem newGroupMenuItem = null;
    private JMenuItem removeGroupMenuItem = null;
    private ManageProtectionGroupsPanel protectionGroupManager;


    /**
     * This method initializes
     */
    public ProtectionGroupPopUpMenu(boolean showNewModify, String newModifyText,
        boolean showLoadChildren, String loadChildrenText, ManageProtectionGroupsPanel panel) {
        super();
        this.protectionGroupManager = panel;
        this.tree = panel.getProtectionGroupsTree();
        this.showLoadChildren = showLoadChildren;
        this.loadChildrenText = loadChildrenText;
        this.showNewModify = showNewModify;
        this.newModifyText = newModifyText;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        if (this.showLoadChildren) {
            this.add(loadChildGroupsMenuItem());
        }
        if (this.showNewModify) {
            this.add(getNewGroupMenuItem());
            this.add(getRemoveGroupMenuItem());
        }
    }


    /**
     * This method initializes removeMethodMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem loadChildGroupsMenuItem() {
        if (loadChildGroups == null) {
            loadChildGroups = new JMenuItem();
            loadChildGroups.setText(this.loadChildrenText);
            loadChildGroups.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    Runner runner = new Runner() {

                        public void execute() {
                            protectionGroupManager.loadProtectionGroupChildren();

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
        return loadChildGroups;
    }


    /**
     * This method initializes newGroupMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getNewGroupMenuItem() {
        if (newGroupMenuItem == null) {
            newGroupMenuItem = new JMenuItem();
            newGroupMenuItem.setText(this.newModifyText);
            newGroupMenuItem.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (tree.getSelectionPath().getLastPathComponent() instanceof ProtectionGroupTreeNode) {
                        ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) tree.getSelectionPath()
                            .getLastPathComponent();
                        protectionGroupManager.modifyProtecionGroup(node.getProtectionGroup());
                    } else {
                        protectionGroupManager.createProtectionGroup();
                    }

                }
            });
        }
        return newGroupMenuItem;
    }


    /**
     * This method initializes removeGroupMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getRemoveGroupMenuItem() {
        if (removeGroupMenuItem == null) {
            removeGroupMenuItem = new JMenuItem();
            removeGroupMenuItem.setText("Remove Protection Group");
            removeGroupMenuItem.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub
                    super.mousePressed(e);
                    if (tree.getSelectionPath().getLastPathComponent() instanceof ProtectionGroupTreeNode) {
                        final ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) tree.getSelectionPath()
                            .getLastPathComponent();
                        Runner runner = new Runner() {

                            public void execute() {
                                protectionGroupManager.removeProtectionGroup(node);

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
        return removeGroupMenuItem;
    }
}
