
package org.cagrid.tide.tools.client.portal.retrieval;

import javax.swing.tree.DefaultMutableTreeNode;

/** 
 *  Node for representing namepspace
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * 
 * @created Nov 22, 2004 
 * @version $Id: TidesTypeTreeNode.java,v 1.1 2008/04/08 13:31:48 hastings Exp $ 
 */
public class TidesTypeTreeNode extends DefaultMutableTreeNode {

	public TidesTypeTreeNode() {
		super();
		this.setUserObject("Data Types");
	}
	
	public String toString(){
		return this.getUserObject().toString();
	}
}
