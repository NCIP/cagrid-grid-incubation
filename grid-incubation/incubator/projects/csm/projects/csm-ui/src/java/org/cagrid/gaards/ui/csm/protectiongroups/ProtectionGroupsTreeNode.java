package org.cagrid.gaards.ui.csm.protectiongroups;

import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.cagrid.gaards.csm.client.ProtectionGroup;


public class ProtectionGroupsTreeNode extends DefaultMutableTreeNode {

    public ProtectionGroupsTreeNode() {
    }


    public void setProtectionGroups(List<ProtectionGroup> pgs) {
        this.removeAllChildren();
        for (Iterator iterator = pgs.iterator(); iterator.hasNext();) {
            ProtectionGroup protectionGroup = (ProtectionGroup) iterator.next();
            ProtectionGroupTreeNode node = new ProtectionGroupTreeNode(protectionGroup);
            this.add(node);
        }
    }


    @Override
    public String toString() {
        return "Protection Groups";
    }

}
