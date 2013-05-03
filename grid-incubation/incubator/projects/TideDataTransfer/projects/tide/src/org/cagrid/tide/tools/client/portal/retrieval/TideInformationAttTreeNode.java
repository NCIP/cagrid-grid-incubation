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
/*-----------------------------------------------------------------------------
 *---------------------------------------------------------------------------*/

package org.cagrid.tide.tools.client.portal.retrieval;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.cagrid.tide.descriptor.TideInformation;

/**
 * Node for representing namepspace
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings</A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster</A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella</A>
 * @created Nov 22, 2004
 * @version $Id: MakoGridServiceTreeNode.java,v 1.21 2005/04/20 17:28:54 ervin
 *          Exp $
 */
public class TideInformationAttTreeNode extends DefaultMutableTreeNode {


	public TideInformationAttTreeNode(String att, DefaultTreeModel model) {
		super();
		this.setUserObject(att);
	}
	
	public String toString(){
		return (String)this.getUserObject();
	}

}
