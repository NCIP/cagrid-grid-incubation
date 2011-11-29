package org.cagrid.gaards.ui.csm.applications;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class ApplicationListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

        Application el = (Application) value;
        if (el != null) {
            this.setText(el.getName());
            this.setToolTipText(el.getDescription());
            this.setIcon(CSMLookAndFeel.getProtectionElementIcon());
        }
        return this;
    }

}
