/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
/*-----------------------------------------------------------------------------
 *---------------------------------------------------------------------------*/

package org.cagrid.tide.tools.client.portal.retrieval;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;


/** 
 *  Renders the grid service tree
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * 
 * @created Nov 17, 2004 
 * @version $Id: TideInformationTreeRenderer.java,v 1.1 2008/04/08 13:31:48 hastings Exp $ 
 */
public class TideInformationTreeRenderer extends DefaultTreeCellRenderer {
	DefaultTreeModel model = null;
	Font normal = null;
	
	public TideInformationTreeRenderer(DefaultTreeModel model) {
		super();
		this.model = model;
		
	}
	
	
	public Component getTreeCellRendererComponent(
		JTree tree, Object value, boolean sel, boolean expanded,
		boolean leaf, int row, boolean localHasFocus) {
		super.getTreeCellRendererComponent(
			tree, value, sel, expanded,
			leaf, row, localHasFocus);
		if(normal==null){
			normal = this.getFont();
		}
		this.setFont(normal);
		if (value instanceof TideInformationTreeNode) {
			TideInformationTreeNode node = (TideInformationTreeNode) value;
			//this.setIcon(node.getOpenIcon());
			//this.setOpenIcon(node.getOpenIcon());
			//this.setClosedIcon(node.getClosedIcon());
			this.setFont(normal.deriveFont(Font.BOLD,12));
			this.setText(node.toString());
		} else if (value instanceof TidesTypeTreeNode) {
			TidesTypeTreeNode node = (TidesTypeTreeNode) value;
			this.setOpenIcon(null);
			this.setClosedIcon(null);
			this.setText(node.toString());
			this.setFont(normal.deriveFont(Font.BOLD,14));
		} 
		return this;
	}

}
