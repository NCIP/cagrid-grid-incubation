package org.cagrid.gaards.ui.csm.protectiongroups;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionGroup;


public class ProtectionGroupTree extends JTree implements Autoscroll {
    private int margin = 12;
    private DefaultTreeModel model;
    Application csmApp = null;
    private ProtectionGroupsTreeNode root;
    private ManageProtectionGroupsPanel protectionGroupManager;


    public ProtectionGroupTree(ManageProtectionGroupsPanel panel, Application csmApp) {
        super();
        this.protectionGroupManager = panel;
        this.csmApp = csmApp;
        this.root = new ProtectionGroupsTreeNode();
        this.model = new DefaultTreeModel(this.root);
        this.setModel(model);
        this.initialize();
        this.setDragEnabled(true);
        DropTarget t = new DropTarget(this, new ProtectionGroupTreeDropTarget(this));
        this.setDropTarget(t);
        this.setCellRenderer(new ProtectionGroupTreeRenderer());
    }


    public void setProtectionGroups(List<ProtectionGroup> pgs) {
        this.root.setProtectionGroups(pgs);
        this.model.reload();
    }


    protected void loadProtectionGroupChildren() throws Exception {
        ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) getSelectionPath().getLastPathComponent();
        ProtectionGroup group = node.getProtectionGroup();

        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            model.removeNodeFromParent(childNode);
        }
        List<ProtectionGroup> children = group.getChildProtectionGroups();
        for (Iterator iterator = children.iterator(); iterator.hasNext();) {
            ProtectionGroup protectionGroup = (ProtectionGroup) iterator.next();
            ProtectionGroupTreeNode childNode = new ProtectionGroupTreeNode(protectionGroup);

            model.insertNodeInto(childNode, node, 0);
            TreePath path = new TreePath(node.getPath());
            expandRow(getRowForPath(path));
            childCount = ((DefaultMutableTreeNode) model.getRoot()).getChildCount();
            List<ProtectionGroupTreeNode> toBeDeleted = new ArrayList<ProtectionGroupTreeNode>();
            for (int i = 0; i < childCount; i++) {
                ProtectionGroupTreeNode cnode = (ProtectionGroupTreeNode) ((DefaultMutableTreeNode) model.getRoot())
                    .getChildAt(i);
                if (cnode.pg.getId() == childNode.pg.getId()) {
                    toBeDeleted.add(cnode);
                }
            }

            for (int i = 0; i < toBeDeleted.size(); i++) {
                model.removeNodeFromParent(toBeDeleted.get(i));
            }
        }

    }


    protected void initialize() {
        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = getRowForLocation(e.getX(), e.getY());
                    if (row < 0)
                        return;
                    setSelectionRow(row);
                    if (getSelectionPath().getLastPathComponent() instanceof ProtectionGroupTreeNode) {
                        ProtectionGroupPopUpMenu menu = new ProtectionGroupPopUpMenu(true, "Modify Protection Group",
                            true, "Load Children Groups", protectionGroupManager);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (getSelectionPath().getLastPathComponent() instanceof ProtectionGroupsTreeNode) {
                        ProtectionGroupPopUpMenu menu = new ProtectionGroupPopUpMenu(true, "New Protection Group",
                            false, null, protectionGroupManager);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

    }


    public void autoscroll(Point p) {
        int realrow = getRowForLocation(p.x, p.y);
        Rectangle outer = getBounds();
        realrow = (p.y + outer.y <= margin ? realrow < 1 ? 0 : realrow - 1 : realrow < getRowCount() - 1
            ? realrow + 1
            : realrow);
        scrollRowToVisible(realrow);
    }


    public Insets getAutoscrollInsets() {
        Rectangle outer = getBounds();
        Rectangle inner = getParent().getBounds();
        return new Insets(inner.y - outer.y + margin, inner.x - outer.x + margin, outer.height - inner.height - inner.y
            + outer.y + margin, outer.width - inner.width - inner.x + outer.x + margin);
    }


    public void expandAll() {
        int row = 0;
        while (row < this.getRowCount()) {
            this.expandRow(row);
            row++;
        }
    }

}
