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
package org.cagrid.gaards.ui.csm.groups;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedIdPTable.java,v 1.3 2008-11-20 15:29:42 langella Exp $
 */
public class MembersTable extends GrapeBaseTable {

    private static final long serialVersionUID = 1L;

    public final static String MEMBER_IDENTITY = "Member Identity";


    public MembersTable() {
        super(createTableModel());
        this.clearTable();
    }


    public static DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(MEMBER_IDENTITY);
        return model;

    }


    public void addMember(final String memberIdentity) {
        Vector v = new Vector();
        v.add(memberIdentity);
        addRow(v);
    }


    public synchronized String getSelectedMember() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            return (String) getValueAt(row, 0);
        } else {
            throw new Exception("Please select a Member!!!");
        }
    }


    public synchronized void removeMember() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            removeRow(row);
        } else {
            throw new Exception("Please select a Member!!!");
        }
    }


    public void doubleClick() throws Exception {

    }


    public void singleClick() throws Exception {

    }

}
