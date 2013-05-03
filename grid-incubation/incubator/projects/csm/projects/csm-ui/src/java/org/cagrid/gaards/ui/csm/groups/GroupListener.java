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

import gov.nih.nci.cagrid.common.Runner;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cagrid.grape.GridApplication;


public class GroupListener implements ListSelectionListener {

    private GroupsPanel groups;


    public GroupListener(GroupsPanel groups) {
        this.groups = groups;
    }


    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            JList list = (JList) evt.getSource();
            final Object obj = list.getSelectedValue();
            if (obj instanceof GroupEntry) {
                Runner runner = new Runner() {
                    public void execute() {
                        GroupEntry grp = (GroupEntry) obj;
                        groups.setGroup(grp);
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
