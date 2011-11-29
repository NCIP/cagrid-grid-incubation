package org.cagrid.gaards.ui.csm.permissions;

import gov.nih.nci.cagrid.common.Runner;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.csm.client.Permission;
import org.cagrid.gaards.csm.client.Role;
import org.cagrid.gaards.ui.csm.CSMUIUtils;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.table.GrapeBaseTable;

/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedIdPTable.java,v 1.3 2008-11-20 15:29:42 langella Exp $
 */
public class PermissionsTable extends GrapeBaseTable {

    private static final long serialVersionUID = 1L;

    public final static String PERMISSION = "Permission";
    public final static String PROTECTION_GROUP = "Protection Group";
    public final static String ROLES = "Roles";
    public final static String USER = "User/Host";
    public final static String GROUP = "Group";

    /**
     * If columns are added, removed or rearranged, these values should be updated.
     */
    public final static int USER_GROUP_COLUMN = 1;
    public final static int PROTECTION_GROUP_COLUMN = 2;
    public final static int ROLES_COLUMN = 3;

    private final static int USER_COLUMN_DEFAULT_WIDTH = 100;
    private final static int PROTECTION_GROUP_COLUMN_DEFAULT_WIDTH = 200;

    private ManagePermissionsPanel panel;
    private String userGroupColumnName = null;

    public PermissionsTable(ManagePermissionsPanel panel) {
        super(createTableModel());
        this.panel = panel;
        TableColumn c = this.getColumn(PERMISSION);
        c.setMaxWidth(0);
        c.setMinWidth(0);
        c.setPreferredWidth(0);
        c.setResizable(false);

        c = this.getColumn(USER);
        c.setPreferredWidth(USER_COLUMN_DEFAULT_WIDTH);
        userGroupColumnName = USER;

        c = this.getColumn(PROTECTION_GROUP);
        c.setPreferredWidth(PROTECTION_GROUP_COLUMN_DEFAULT_WIDTH);

        this.clearTable();
    }

    /**
     * 
     * @param columnName
     */
    public void setUserGroupColumnName(String columnName) {
        TableColumn c = null;
        c = this.getColumn(this.userGroupColumnName);
        if (c != null) {
            userGroupColumnName = columnName;
            c.setHeaderValue(columnName);
        }
    }

    public static DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(PERMISSION);
        model.addColumn(USER);
        model.addColumn(PROTECTION_GROUP);
        model.addColumn(ROLES);
        return model;
    }

    public void addPermissions(List<Permission> list) {
        for (int i = 0; i < list.size(); i++) {
            addPermission(list.get(i));
        }
    }

    public void addPermission(final Permission p) {
        Vector<Object> v = new Vector<Object>();
        v.add(p);
        // add the user name or group name
        if (p.getUser() != null) {
            v.add(p.getUser());
        } else if (p.getGroup() != null && p.getGroup().getName() != null) {
            v.add(p.getGroup().getName());
        } else {
            // add placeholder column if neither user nor group column is added
            v.add("");
        }
        v.add(p.getProtectionGroup().getName());
        StringBuffer sb = new StringBuffer();
        List<Role> roles = p.getRoles();
        roles = CSMUIUtils.sortRoles(roles);
        for (int i = 0; i < roles.size(); i++) {
            if (i == 0) {
                sb.append(roles.get(i).getName());
            } else {
                sb.append(", " + roles.get(i).getName());
            }
        }
        v.add(sb.toString());
        addRow(v);
    }

    public synchronized Permission getSelectedPermission() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            return (Permission) getValueAt(row, 0);
        } else {
            throw new Exception("Please select a permission!!!");
        }
    }

    public synchronized void removePermission() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            removeRow(row);
        } else {
            throw new Exception("Please select a permission to remove!!!");
        }
    }

    public void doubleClick() throws Exception {
        Runner runner = new Runner() {

            public void execute() {
                panel.modifyPermission();
            }
        };
        try {
            GridApplication.getContext().executeInBackground(runner);
        } catch (Exception t) {
            t.getMessage();
        }
    }

    public void singleClick() throws Exception {

    }

}