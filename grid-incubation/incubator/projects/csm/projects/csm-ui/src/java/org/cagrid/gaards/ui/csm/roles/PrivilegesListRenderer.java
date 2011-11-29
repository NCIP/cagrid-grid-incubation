package org.cagrid.gaards.ui.csm.roles;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.cagrid.gaards.csm.client.Privilege;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class PrivilegesListRenderer implements ListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        Privilege p = (Privilege) value;
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setIconTextGap(12);
        label.setIcon(CSMLookAndFeel.getPrivilegeIcon());
        label.setText(p.getName());
        label.setToolTipText("| ID " + p.getId() + "\n | NAME: " + p.getName() + "\n | DESCRIPTION: "
            + p.getDescription());
        if (isSelected) {
            label.setBackground(CSMLookAndFeel.getPanelLabelColor());
            label.setForeground(Color.white);
        } else {
            label.setBackground(Color.white);
            label.setForeground(Color.black);
        }
        return label;
    }

}
