package org.cagrid.gaards.ui.csm.protectiongroups;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.cagrid.gaards.csm.client.ProtectionGroup;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;

public class ProtectionGroupListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ProtectionGroup el = (ProtectionGroup)value;
        this.setText(el.getName());
        this.setToolTipText("| ID " + el.getId() + "\n | NAME: " + el.getName() + "\n | DESCRIPTION: " + el.getDescription());
        this.setIcon(CSMLookAndFeel.getProtectionGroupIcon());
        
        return this;
    }

}
