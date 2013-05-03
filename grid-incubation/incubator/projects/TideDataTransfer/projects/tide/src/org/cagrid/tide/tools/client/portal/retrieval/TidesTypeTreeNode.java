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
