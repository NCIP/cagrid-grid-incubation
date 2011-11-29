package org.cagrid.gaards.ui.csm.protectionelements;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;

public class ProtectionElementListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ProtectionElement el = (ProtectionElement)value;
        this.setText(el.getName());
        this.setToolTipText("| ID " + el.getId() + "\n | NAME: " + el.getName() + "\n | TYPE: " + el.getType() + "\n | DESCRIPTION: " + el.getDescription());
        this.setIcon(CSMLookAndFeel.getProtectionElementIcon());
        
        return this;
    }

}
