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
package org.cagrid.gaards.ui.csm.associations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.cagrid.gaards.csm.client.Permission;
import org.cagrid.gaards.csm.client.Role;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedIdPTable.java,v 1.3 2008-11-20 15:29:42 langella Exp $
 */
public class AssociationsTable extends GrapeBaseTable {

    private static final long serialVersionUID = 1L;

    public final static String USER_GROUP_IDENTITY = "User/Group Identity";
    public final static String PROTECTION_GROUP = "Protection Group";
    public final static String ROLES = "Roles";
    private List<Permission> perms = null;

    public AssociationsTable() {
        super(createTableModel());
        this.perms = new ArrayList();
        this.clearTable();
    }


    public static DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(USER_GROUP_IDENTITY);
        model.addColumn(PROTECTION_GROUP);
        model.addColumn(ROLES);
        return model;

    }
    
    public void setPermissions(List<Permission> permissions){
    	this.perms = new ArrayList();
    	this.clearTable();
    	for (Iterator iterator = permissions.iterator(); iterator.hasNext();) {
			Permission permission = (Permission) iterator.next();
			addPermission(permission);
		}
    }

    public void addPermission(final Permission permission) {
        Vector v = new Vector();
        if(permission.getGroup()!=null){
        	v.add("Group:" + permission.getGroup().getName());
        } else {
        	v.add("User:" + permission.getUser());
        }
        v.add(permission.getProtectionGroup().getId()+":"+permission.getProtectionGroup().getName());
        String roles = "";
        for (Iterator iterator = permission.getRoles().iterator(); iterator.hasNext();) {
			Role role = (Role) iterator.next();
			roles += role.getName();
			if(iterator.hasNext()){
				roles += ",";
			}
		}
        v.add(roles);
        addRow(v);
        perms.add(permission);
    }


    public synchronized Permission getSelectedPermission() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            return perms.get(row);
        } else {
            throw new Exception("Please select a row!!!");
        }
    }

    public synchronized void removeSelectedPermission() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            removeRow(row);
            perms.remove(row);
        } else {
            throw new Exception("Please select a row!!!");
        }
    }


    public void doubleClick() throws Exception {

    }


    public void singleClick() throws Exception {

    }

}
