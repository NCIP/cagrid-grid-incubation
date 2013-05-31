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

import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.cagrid.gaards.csm.client.Application;


public class ApplicationsComboBox extends JComboBox {

    public void setData(List<Application> els) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        setRenderer(new ApplicationListRenderer());
        for (Iterator iterator = els.iterator(); iterator.hasNext();) {
            model.addElement(iterator.next());
        }
        this.setModel(model);
    }

}
