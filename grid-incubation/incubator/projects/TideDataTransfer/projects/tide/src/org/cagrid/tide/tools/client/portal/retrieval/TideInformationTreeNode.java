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
import org.cagrid.tide.service.globus.TideAuthorization;

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
public class TideInformationTreeNode extends DefaultMutableTreeNode {

	public class TideInformationTypeSorter implements
			Comparator<TideInformation> {

		public int compare(TideInformation o1, TideInformation o2) {
			return o1.getName().toLowerCase().compareTo(
					o2.getName().toLowerCase());
		}

	}

	public TideInformationTreeNode(TideInformation tide, DefaultTreeModel model) {
		super();
		this.setUserObject(tide);
		TideInformationAttTreeNode desc = new TideInformationAttTreeNode("Description: " + tide.getDescription(),model);
		model.insertNodeInto(desc, this, 0);
		TideInformationAttTreeNode type = new TideInformationAttTreeNode("Type: " + tide.getType(),model);
		model.insertNodeInto(type, this, 1);
		TideInformationAttTreeNode size = new TideInformationAttTreeNode("Size: " + String.valueOf(tide.getSize()),model);
		model.insertNodeInto(size, this, 2);
		TideInformationAttTreeNode chunks = new TideInformationAttTreeNode("Currents: " + String.valueOf(tide.getChunks()),model);
		model.insertNodeInto(chunks, this, 3);
		TideInformationAttTreeNode md5 = new TideInformationAttTreeNode("MD5Sum: " + tide.getMd5Sum(),model);
		model.insertNodeInto(md5, this, 4);
	}
	
	public String toString(){
		return ((TideInformation)this.getUserObject()).getName();
	}

}
