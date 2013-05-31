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
package org.cagrid.gaards.ui.csm.protectionelements;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionElement;

public class ProtectionElementsList extends JList {

	private List<ProtectionElement> data = new ArrayList<ProtectionElement>();
	private List<ProtectionElement> filters = new ArrayList<ProtectionElement>();
	private Application csmApp = null;
	private ProtectionElementPopUpMenu menu = null;

	public ProtectionElementsList(Application csmApp) {
		super();
		this.csmApp = csmApp;
		initialize();
	}

	public ProtectionElementsList() {
		super();
		initialize();
	}

	private void initialize() {
		if (csmApp != null) {
			menu = new ProtectionElementPopUpMenu(this, csmApp);
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					if (SwingUtilities.isRightMouseButton(e) && getSelectedValue() != null) {
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
		}
	}

	public ProtectionElementsList(ListModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}

	public ProtectionElementsList(Object[] listData) {
		super(listData);
		// TODO Auto-generated constructor stub
	}

	public ProtectionElementsList(Vector<?> listData) {
		super(listData);
		// TODO Auto-generated constructor stub
	}

	public List<ProtectionElement> getData() {
		return data;
	}

	public List<ProtectionElement> getFilters() {
		return filters;
	}

	public void setData(List<ProtectionElement> els,
			List<ProtectionElement> filters) {
		this.data = els;
		filter(filters);
	}

	private void filter(List<ProtectionElement> filters) {
		this.filters = filters;
		DefaultListModel model = new DefaultListModel();
		setCellRenderer(new ProtectionElementListRenderer());
		for (Iterator iterator = this.data.iterator(); iterator.hasNext();) {
			ProtectionElement el = (ProtectionElement) iterator.next();
			if (this.filters != null) {
				boolean found = false;
				for (Iterator iterator2 = filters.iterator(); iterator2
						.hasNext();) {
					ProtectionElement filter = (ProtectionElement) iterator2
							.next();
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
		this.setModel(model);
	}

	public void setData(List<ProtectionElement> els) {
		setData(els, null);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800,100);
		
	}
	

}
