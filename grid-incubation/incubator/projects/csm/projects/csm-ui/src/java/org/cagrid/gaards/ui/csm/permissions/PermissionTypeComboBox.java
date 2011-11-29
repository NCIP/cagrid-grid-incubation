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
