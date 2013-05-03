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
package org.cagrid.gaards.ui.csm.common;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreePath;

public class TransferableTreeNode implements Transferable {

	  public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
	      "Tree Path");

	  DataFlavor flavors[] = { TREE_PATH_FLAVOR };

	  TreePath path;

	  public TransferableTreeNode(TreePath tp) {
	    path = tp;
	  }

	  public synchronized DataFlavor[] getTransferDataFlavors() {
	    return flavors;
	  }

	  public boolean isDataFlavorSupported(DataFlavor flavor) {
	    return (flavor.getRepresentationClass() == TreePath.class);
	  }

	  public synchronized Object getTransferData(DataFlavor flavor)
	      throws UnsupportedFlavorException, IOException {
	    if (isDataFlavorSupported(flavor)) {
	      return (Object) path;
	    } else {
	      throw new UnsupportedFlavorException(flavor);
	    }
	  }
	}
