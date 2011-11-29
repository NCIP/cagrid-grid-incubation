package org.cagrid.gaards.ui.csm.groups;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class GroupListRenderer implements ListCellRenderer {

    public GroupListRenderer() {

    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        GroupEntry entry = (GroupEntry) value;
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setIconTextGap(12);
        label.setIcon(entry.getImage());
        label.setText(entry.getName());
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
