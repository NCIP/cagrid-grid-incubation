package org.cagrid.gaards.ui.csm.protectiongroups;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.cagrid.gaards.ui.csm.common.TransferableTreeNode;

public class ProtectionGroupTreeDragSource implements DragSourceListener,
		DragGestureListener {

	DragSource source;

	DragGestureRecognizer recognizer;

	TransferableTreeNode transferable;

	DefaultMutableTreeNode oldNode;

	JTree sourceTree;

	public ProtectionGroupTreeDragSource(JTree tree, int actions) {
		sourceTree = tree;
		source = new DragSource();
		recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
				actions, this);
	}

	/*
	 * Drag Gesture Handler
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		TreePath path = sourceTree.getSelectionPath();
		if ((path == null) || (path.getPathCount() <= 1)) {
			return;
		}
		oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		transferable = new TransferableTreeNode(new TreePath("Empty Data"));
		source.startDrag(dge, DragSource.DefaultMoveDrop, transferable, this);
	}

	/*
	 * Drag Event Handlers
	 */
	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragOver(DragSourceDragEvent dsde) {
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

}