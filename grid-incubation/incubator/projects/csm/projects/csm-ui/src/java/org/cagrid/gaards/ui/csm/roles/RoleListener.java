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
package org.cagrid.gaards.ui.csm.roles;

import gov.nih.nci.cagrid.common.Runner;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cagrid.gaards.csm.client.Role;
import org.cagrid.grape.GridApplication;


public class RoleListener implements ListSelectionListener {

    private ManageRolesPanel roles;


    public RoleListener(ManageRolesPanel roles) {
        this.roles = roles;
    }


    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            JList list = (JList) evt.getSource();
            final Object obj = list.getSelectedValue();
            if (obj instanceof Role) {
                Runner runner = new Runner() {
                    public void execute() {
                        Role role = (Role) obj;
                        roles.setRole(role);
                    }
                };
                try {
                    GridApplication.getContext().executeInBackground(runner);
                } catch (Exception t) {
                    t.getMessage();
                }

            }
        }
    }
}
