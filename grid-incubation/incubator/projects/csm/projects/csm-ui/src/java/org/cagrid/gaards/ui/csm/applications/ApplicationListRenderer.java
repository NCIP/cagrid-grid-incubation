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
