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
package org.cagrid.gaards.ui.csm.protectiongroups;

import javax.swing.tree.DefaultMutableTreeNode;

import org.cagrid.gaards.csm.client.ProtectionGroup;

public class ProtectionGroupTreeNode extends DefaultMutableTreeNode  {
 
    private static final long serialVersionUID = 1L;

    ProtectionGroup pg;
    
    public ProtectionGroupTreeNode(ProtectionGroup pg){
        this.pg = pg;
    }
    
    public ProtectionGroup getProtectionGroup(){
        return this.pg;
    }
    
    @Override
    public String toString() {
        return pg.getName();
    }

    
    
}
