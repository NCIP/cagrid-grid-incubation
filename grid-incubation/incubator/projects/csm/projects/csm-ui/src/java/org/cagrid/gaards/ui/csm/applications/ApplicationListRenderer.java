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
package org.cagrid.gaards.ui.csm.applications;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class ApplicationListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
        boolean cellHasFocus) {

        Application el = (Application) value;
        if (el != null) {
            this.setText(el.getName());
            this.setToolTipText(el.getDescription());
            this.setIcon(CSMLookAndFeel.getProtectionElementIcon());
        }
        return this;
    }

}
