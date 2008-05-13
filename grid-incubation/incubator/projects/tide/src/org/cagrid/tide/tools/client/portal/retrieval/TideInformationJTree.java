package org.cagrid.tide.tools.client.portal.retrieval;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.cagrid.tide.descriptor.TideInformation;


public class TideInformationJTree extends JTree {
	TidesTypeTreeNode root;
	DefaultTreeModel model;
	TideInformation[] tides;

	public TideInformationJTree() {
		setRootVisible(false);
		setCellRenderer(new TideInformationTreeRenderer(model));
	}

	public TideInformationJTree(TideInformation[] tides) {
		this.tides = tides;
		setRootVisible(false);
		setCellRenderer(new TideInformationTreeRenderer(model));
		setTides(tides);
	}


	private void initialize() {
		for(TideInformation tide : this.tides){
			addNode(tide);
		}

		expandAll(true);
	}


	public void setTides(TideInformation[] tides) {
		this.root = new TidesTypeTreeNode();
		this.model = new DefaultTreeModel(root, false);
		this.tides = tides;
		setModel(model);
		initialize();
	}


	public void addNode(TideInformation tide) {
		
			TideInformationTreeNode newNode = new TideInformationTreeNode(tide, model);
			model.insertNodeInto(newNode, root, 0);
			expandPath(new TreePath(model.getPathToRoot(newNode)));
		
	}


	public DefaultMutableTreeNode getCurrentNode() {
		TreePath currentSelection = this.getSelectionPath();
		if (currentSelection != null) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentSelection.getLastPathComponent();
			if (currentNode != this.root) {
				return currentNode;
			}
		}
		return null;
	}


	public List getSelectedNodes() {
		List selected = new LinkedList();
		TreePath[] currentSelection = this.getSelectionPaths();
		if (currentSelection != null) {
			for (int i = 0; i < currentSelection.length; i++) {
				TreePath path = currentSelection[i];
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				if (currentNode != this.root) {
					selected.add(currentNode);
				}
			}
		}
		return selected;
	}


	public void removeSelectedNode() {
		DefaultMutableTreeNode currentNode = getCurrentNode();

		if (currentNode != null) {
			model.removeNodeFromParent(currentNode);
		}
	}


	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	public void expandAll(boolean expand) {
		JTree tree = this;
		TreeNode rootNode = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(new TreePath(rootNode), expand);
	}


	private void expandAll(TreePath parent, boolean expand) {
		JTree tree = this;
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (java.util.Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}


	protected void setExpandedState(TreePath path, boolean state) {
		// Ignore all collapse requests; collapse events will not be fired
		if (path.getLastPathComponent() != root) {
			super.setExpandedState(path, state);
		} else if (state && path.getLastPathComponent() == root) {
			super.setExpandedState(path, state);
		}
	}
}
