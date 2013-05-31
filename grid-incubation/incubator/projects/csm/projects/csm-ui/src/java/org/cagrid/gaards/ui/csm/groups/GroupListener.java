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
