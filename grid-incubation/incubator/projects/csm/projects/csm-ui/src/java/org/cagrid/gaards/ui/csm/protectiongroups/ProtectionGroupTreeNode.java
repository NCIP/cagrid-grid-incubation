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
