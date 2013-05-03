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
package org.cagrid.gaards.ui.csm.permissions;

import javax.swing.JComboBox;


public class PermissionTypeComboBox extends JComboBox {
    /**
     * Hash code for serialization.
     */
    private static final long serialVersionUID = 5677909963359676468L;
    public static final String USER_HOST_TYPE = "User / Host"; 
    public static final String GROUP_TYPE = "Group";


    public PermissionTypeComboBox() {
        super();
        addItem(GROUP_TYPE);
        addItem(USER_HOST_TYPE);
    }


    public String getPermissionType() {
        return (String) getSelectedItem();
    }

}
