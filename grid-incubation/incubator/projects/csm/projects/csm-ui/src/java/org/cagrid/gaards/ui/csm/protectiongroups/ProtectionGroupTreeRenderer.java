package org.cagrid.gaards.ui.csm.protectiongroups;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.cagrid.gaards.ui.csm.CSMLookAndFeel;

public class ProtectionGroupTreeRenderer extends DefaultTreeCellRenderer {


    public ProtectionGroupTreeRenderer() {
		super();

	}

	public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean sel, boolean expanded,
        boolean leaf, int row, boolean localHasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);      
        if (value instanceof ProtectionGroupTreeNode) {
            ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) value;
            this.setText(node.toString());
            //this.setOpenIcon(CSMLookAndFeel.getProtectionGroupIcon());
            //this.setClosedIcon(CSMLookAndFeel.getProtectionGroupIcon());
            this.setIcon(CSMLookAndFeel.getProtectionGroupIcon());
            this.setToolTipText(node.getProtectionGroup().getDescription());  
        } else if(value instanceof ProtectionGroupsTreeNode){
            ProtectionGroupsTreeNode node = (ProtectionGroupsTreeNode) value;
            this.setText(node.toString());
            this.setIcon(CSMLookAndFeel.getCSMIcon22());
            //this.setOpenIcon(CSMLookAndFeel.getCSMIcon22());
            //this.setClosedIcon(CSMLookAndFeel.getCSMIcon22());      
        }
        return this;
    }
}
