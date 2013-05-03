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
package org.cagrid.gaards.ui.csm.roles;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.cagrid.gaards.csm.client.Role;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class RoleListRenderer implements ListCellRenderer {

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        Role r = (Role) value;
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setIconTextGap(12);
        label.setIcon(CSMLookAndFeel.getPrivilegeIcon());
        label.setText(r.getName());
        label.setToolTipText("| ID " + r.getId() + "\n | NAME: " + r.getName() + "\n | DESCRIPTION: "
            + r.getDescription());
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
