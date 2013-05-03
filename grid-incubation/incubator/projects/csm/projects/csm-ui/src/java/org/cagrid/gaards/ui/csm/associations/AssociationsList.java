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
package org.cagrid.gaards.ui.csm.associations;

import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.Permission;


public class AssociationsList extends JList {

    private DefaultListModel model;


    public AssociationsList() {
        super();
        this.model = new DefaultListModel();
        this.setModel(model);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellRenderer(new AssociationsListRenderer());
    }


    public void addAssociation(final Permission perm) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.addElement(perm);
            }
        });
    }


    public void setAssociations(final List<Permission> perm) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();
                for (int i = 0; i < perm.size(); i++) {
                    model.addElement(perm.get(i));
                }
            }
        });
    }


    public void clearGroups() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();

            }
        });
    }
    

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800,100);
		
	}
	

}
