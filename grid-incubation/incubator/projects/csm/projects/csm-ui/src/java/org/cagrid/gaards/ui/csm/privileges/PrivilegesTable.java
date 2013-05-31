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
package org.cagrid.gaards.ui.csm.privileges;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.csm.client.Privilege;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedIdPTable.java,v 1.3 2008-11-20 15:29:42 langella Exp $
 */
public class PrivilegesTable extends GrapeBaseTable {

    private static final long serialVersionUID = 1L;

    public final static String PRIVILEGE = "privilege";

    public final static String ID = "Id";

    public final static String NAME = "Name";

    public final static String DESCRIPTION = "Description";

    private PrivilegesBrowser window;


    public PrivilegesTable() {
        this(null);
    }


    public PrivilegesTable(PrivilegesBrowser window) {
        super(createTableModel());
        this.window = window;
        TableColumn c = this.getColumn(PRIVILEGE);
        c.setMaxWidth(0);
        c.setMinWidth(0);
        c.setPreferredWidth(0);
        c.setResizable(false);

        c = this.getColumn(ID);
        c.setMaxWidth(35);
        c.setMinWidth(35);
        this.clearTable();
    }


    public static DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(PRIVILEGE);
        model.addColumn(ID);
        model.addColumn(NAME);
        model.addColumn(DESCRIPTION);
        return model;

    }


    public void addPrivilege(final Privilege priv) {
        Vector v = new Vector();
        v.add(priv);
        v.add(String.valueOf(priv.getId()));
        v.add(priv.getName());
        v.add(priv.getDescription());
        addRow(v);
    }


    public synchronized Privilege getSelectedPrivilege() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            return (Privilege) getValueAt(row, 0);
        } else {
            throw new Exception("Please select a privilege!!!");
        }
    }


    public synchronized void removePrivilege() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            removeRow(row);
        } else {
            throw new Exception("Please select a privilege!!!");
        }
    }


    public void doubleClick() throws Exception {
        if (window != null) {
            int row = getSelectedRow();
            if ((row >= 0) && (row < getRowCount())) {
                window.viewPrivilege();
            } else {
                throw new Exception("Please select an Application!!!");
            }
        }
    }


    public void singleClick() throws Exception {

    }

}
