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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.cagrid.gaards.csm.client.Role;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class RolesListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Role el = (Role) value;
        this.setText(el.getName());
        this.setOpaque(true);
        this.setToolTipText("| ID " + el.getId() + "\n | NAME: " + el.getName() + "\n | DESCRIPTION: "
            + el.getDescription());
        this.setIcon(CSMLookAndFeel.getRoleIcon());
        if (isSelected) {
            this.setBackground(CSMLookAndFeel.getPanelLabelColor());
            this.setForeground(Color.white);
        } else {
            this.setBackground(Color.white);
            this.setForeground(Color.black);
        }
        return this;
    }

}
