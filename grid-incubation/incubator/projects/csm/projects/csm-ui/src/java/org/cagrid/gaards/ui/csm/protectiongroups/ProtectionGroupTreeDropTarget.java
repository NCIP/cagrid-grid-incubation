package org.cagrid.gaards.ui.csm.protectiongroups;

import java.awt.Point;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.cagrid.gaards.csm.client.ProtectionGroup;

public class ProtectionGroupTreeDropTarget implements DropTargetListener {

	ProtectionGroupTree targetTree;

	TreeNode currentDragOverNode = null;

	public TreeNode getCurrentDragOverNode() {
		return currentDragOverNode;
	}

	public ProtectionGroupTreeDropTarget(ProtectionGroupTree tree) {
		targetTree = tree;
	}

	private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
		Point p = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath path = tree.getClosestPathForLocation(p.x, p.y);
		return (TreeNode) path.getLastPathComponent();
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		TreeNode node = getNodeForEvent(dtde);
		currentDragOverNode = node;
		if (node.getParent() != null
				&& node.getParent().equals(
						targetTree.getSelectionPath().getLastPathComponent())) {
			dtde.rejectDrag();
		} else {
			dtde.acceptDrag(dtde.getDropAction());

		}

	}

	public void dragOver(DropTargetDragEvent dtde) {
		TreeNode node = getNodeForEvent(dtde);
		if (node.getParent() != null
				&& node.getParent().equals(
						targetTree.getSelectionPath().getLastPathComponent())) {
			dtde.rejectDrag();
		} else {
			dtde.acceptDrag(dtde.getDropAction());

		}
	}

	public void dragExit(DropTargetEvent dte) {
		currentDragOverNode = null;
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {
		Point pt = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		TreePath parentpath = targetTree.getClosestPathForLocation(pt.x, pt.y);
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath
				.getLastPathComponent();

		try {

			dtde.acceptDrop(dtde.getDropAction());

			DefaultTreeModel model = (DefaultTreeModel) targetTree.getModel();
			ProtectionGroupTreeNode node = (ProtectionGroupTreeNode) targetTree
					.getSelectionPath().getLastPathComponent();
			if (node.getParent() instanceof ProtectionGroupTreeNode) {
				((ProtectionGroupTreeNode) node.getParent()).pg
						.unassignProtectionGroup(((ProtectionGroupTreeNode) node).pg);
			} else {
				ProtectionGroup parentPG = node.pg.getParentProtectionGroup();
				if(parentPG!=null){
					parentPG.unassignProtectionGroup(node.pg);
				}
			}
			model.removeNodeFromParent(node);
			if (parent instanceof ProtectionGroupTreeNode) {
				((ProtectionGroupTreeNode) parent).pg
						.assignProtectionGroup(((ProtectionGroupTreeNode) node).pg);
			}
			model.insertNodeInto(node, parent, 0);
			targetTree.expandAll();
			dtde.dropComplete(true);
			targetTree.expandAll();
			return;

		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}
}