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
