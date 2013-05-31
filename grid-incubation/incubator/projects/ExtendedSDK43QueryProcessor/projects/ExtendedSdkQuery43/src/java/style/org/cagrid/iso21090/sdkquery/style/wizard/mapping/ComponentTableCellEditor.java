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
package org.cagrid.iso21090.sdkquery.style.wizard.mapping;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ComponentTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private Object editorValue = null;

    public ComponentTableCellEditor() {
        editorValue = null;
    }


    public Object getCellEditorValue() {
        return editorValue;
    }


    public Component getTableCellEditorComponent(JTable table, Object value, 
        boolean isSelected, int row, int column) {
        editorValue = value;
        return (Component) value;
    }
}
