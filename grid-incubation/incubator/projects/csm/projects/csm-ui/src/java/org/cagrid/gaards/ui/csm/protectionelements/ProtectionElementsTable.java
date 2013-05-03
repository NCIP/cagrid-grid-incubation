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
package org.cagrid.gaards.ui.csm.protectionelements;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.grape.table.GrapeBaseTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedIdPTable.java,v 1.3 2008-11-20 15:29:42 langella Exp $
 */
public class ProtectionElementsTable extends GrapeBaseTable {

    private static final long serialVersionUID = 1L;

    public final static String PROTECTION_ELEMENT = "ProtectionElement";

    public final static String ID = "Id";

    public final static String NAME = "Name";

    public final static String OBJECT_ID = "Object Id";

    public final static String TYPE = "Type";

    public final static String ATTRIBUTE = "Attribute";

    public final static String ATTRIBUTE_VALUE = "Value";

    private ManageProtectionElementsPanel panel;


    public ProtectionElementsTable(ManageProtectionElementsPanel panel) {
        super(createTableModel());
        this.panel = panel;
        TableColumn c = this.getColumn(PROTECTION_ELEMENT);
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
        model.addColumn(PROTECTION_ELEMENT);
        model.addColumn(ID);
        model.addColumn(NAME);
        model.addColumn(OBJECT_ID);
        model.addColumn(ATTRIBUTE);
        model.addColumn(ATTRIBUTE_VALUE);
        model.addColumn(TYPE);
        return model;

    }


    public void addProtectionElements(List<ProtectionElement> list) {
        for (int i = 0; i < list.size(); i++) {
            addProtectionElement(list.get(i));
        }
    }


    public void addProtectionElement(final ProtectionElement pe) {
        Vector v = new Vector();
        v.add(pe);
        v.add(String.valueOf(pe.getId()));
        v.add(pe.getName());
      
        v.add(pe.getObjectId());
        if (pe.getAttribute() != null) {
            v.add(pe.getAttribute());
        } else {
            v.add("");
        }
        if (pe.getAttributeValue() != null) {
            v.add(pe.getAttributeValue());
        } else {
            v.add("");
        }
        if (pe.getType() == null) {
            v.add("");
        } else {
            v.add(pe.getType());
        }
        addRow(v);
    }


    public synchronized ProtectionElement getSelectedProtectionElement() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            return (ProtectionElement) getValueAt(row, 0);
        } else {
            throw new Exception("Please select a Protection Element!!!");
        }
    }


    public synchronized void removeProtectionElement() throws Exception {
        int row = getSelectedRow();
        if ((row >= 0) && (row < getRowCount())) {
            removeRow(row);
        } else {
            throw new Exception("Please select a Protection Element!!!");
        }
    }


    public void doubleClick() throws Exception {
        panel.modifyProtectionElement();
    }


    public void singleClick() throws Exception {

    }

}
