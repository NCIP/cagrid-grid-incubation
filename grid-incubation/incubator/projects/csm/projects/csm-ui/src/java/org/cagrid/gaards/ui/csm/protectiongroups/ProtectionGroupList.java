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
package org.cagrid.gaards.ui.csm.protectiongroups;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.ProtectionGroup;


public class ProtectionGroupList extends JList {

    private List<ProtectionGroup> data = new ArrayList<ProtectionGroup>();
    private List<ProtectionGroup> filters = new ArrayList<ProtectionGroup>();
    private DefaultListModel model;


    public ProtectionGroupList() {
        super();
        this.model = new DefaultListModel();
        this.setModel(model);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setCellRenderer(new ProtectionGroupListRenderer());
    }



    public List<ProtectionGroup> getData() {
        return data;
    }


    public List<ProtectionGroup> getFilters() {
        return filters;
    }


    public void setData(List<ProtectionGroup> els, List<ProtectionGroup> filters) {
        this.data = els;
        filter(filters);
    }


    private void filter(List<ProtectionGroup> filters) {
        this.filters = filters;
        for (Iterator iterator = this.data.iterator(); iterator.hasNext();) {
            ProtectionGroup el = (ProtectionGroup) iterator.next();
            if (this.filters != null) {
                boolean found = false;
                for (Iterator iterator2 = filters.iterator(); iterator2.hasNext();) {
                    ProtectionGroup filter = (ProtectionGroup) iterator2.next();
                    if (filter.getId() == el.getId()) {
                        found = true;
                    }
                }
                if (!found) {
                    model.addElement(el);
                }
            } else {
                model.addElement(el);
            }

        }
    }


    public void setData(List<ProtectionGroup> els) {
        setData(els, null);
    }


    public void setProtectionGroups(final List<ProtectionGroup> groups) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DefaultListModel model = (DefaultListModel) getModel();
                model.removeAllElements();
                for (int i = 0; i < groups.size(); i++) {
                    model.addElement(groups.get(i));
                }
            }
        });
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 100);

    }

}
