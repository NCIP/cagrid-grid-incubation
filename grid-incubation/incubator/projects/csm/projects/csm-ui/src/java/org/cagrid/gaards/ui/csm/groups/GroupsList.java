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
package org.cagrid.gaards.ui.csm.groups;

import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.Group;


public class GroupsList extends JList {

    private DefaultListModel model;


    public GroupsList() {
        super();
        this.model = new DefaultListModel();
        this.setModel(model);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellRenderer(new GroupListRenderer());
    }


    public void addGroup(final Group group) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.addElement(new GroupEntry(group));
            }
        });
    }


    public void setGroups(final List<Group> groups) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();
                for (int i = 0; i < groups.size(); i++) {
                    model.addElement(new GroupEntry(groups.get(i)));
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
    

	
	

}
