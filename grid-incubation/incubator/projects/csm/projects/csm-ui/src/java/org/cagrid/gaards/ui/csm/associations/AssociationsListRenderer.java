package org.cagrid.gaards.ui.csm.associations;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.cagrid.gaards.csm.client.Permission;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;

public class AssociationsListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Permission el = (Permission)value;
        this.setText(el.toString());
        //this.setToolTipText("| ID " + el.getId() + "\n | NAME: " + el.getName() + "\n | DESCRIPTION: " + el.getDescription());
        this.setIcon(CSMLookAndFeel.getAssociationIcon());
        
        return this;
    }

}
