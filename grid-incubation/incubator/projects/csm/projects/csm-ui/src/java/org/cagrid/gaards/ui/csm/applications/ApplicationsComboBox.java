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
