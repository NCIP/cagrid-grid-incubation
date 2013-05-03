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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.Role;


public class RolesList extends JList {

    private DefaultListModel model;


    public RolesList() {
        super();
        this.model = new DefaultListModel();
        this.setModel(model);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellRenderer(new RolesListRenderer());
    }


    public void addRole(final Role role) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.addElement(role);
            }
        });
    }


    public void setRoles(final List<Role> roles) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();
                for (int i = 0; i < roles.size(); i++) {
                    model.addElement(roles.get(i));
                }
            }
        });
    }


    public void clearRoles() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();

            }
        });
    }


    public Role getSelectedRole() {
        return (Role) this.getSelectedValue();
    }
    
    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<Role>();
        for (int i = 0; i < model.size(); i++) {
            roles.add((Role) model.getElementAt(i));
        }
        return roles;
    }
    


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 100);

    }

}
