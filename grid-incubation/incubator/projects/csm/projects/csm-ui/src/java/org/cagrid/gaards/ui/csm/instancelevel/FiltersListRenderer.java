package org.cagrid.gaards.ui.csm.instancelevel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.cagrid.gaards.csm.client.FilterClause;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class FiltersListRenderer implements ListCellRenderer {

    public FiltersListRenderer() {

    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        FilterClause entry = (FilterClause) value;
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setIconTextGap(12);
//        label.setIcon();
        label.setText(entry.getClassName());
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
