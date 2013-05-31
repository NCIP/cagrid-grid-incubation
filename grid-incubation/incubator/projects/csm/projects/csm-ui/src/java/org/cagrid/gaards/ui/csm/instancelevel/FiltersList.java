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
package org.cagrid.gaards.ui.csm.instancelevel;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.FilterClause;

public class FiltersList extends JList {

	/**
     * 
     */
    private static final long serialVersionUID = -3169563662407602797L;
    private DefaultListModel model;

	public FiltersList() {
		super();
		this.model = new DefaultListModel();
		this.setModel(model);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellRenderer(new FiltersListRenderer());
	}

	public void addFilter(final FilterClause clause) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				model.addElement(clause);
			}
		});
	}

	public void setFilters(final List<FilterClause> filters) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				model.removeAllElements();
				for (int i = 0; i < filters.size(); i++) {
					model.addElement(filters.get(i));
				}
			}
		});
	}

	public void clearFilters() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				model.removeAllElements();

			}
		});
	}

	public void removeSelected() {
		model.removeElement(getSelectedValue());
	}

}
