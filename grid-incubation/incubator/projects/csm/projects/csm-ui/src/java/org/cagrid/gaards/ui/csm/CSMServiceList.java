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
package org.cagrid.gaards.ui.csm;

import java.util.List;

import javax.swing.JComboBox;


public class CSMServiceList extends JComboBox {

    private static final long serialVersionUID = 1L;

    private static CSMHandle lastSelectedService;


    public CSMServiceList() {
        List<CSMHandle> services = CSMUIUtils.getCSMServices();
        for (int i = 0; i < services.size(); i++) {
            this.addItem(services.get(i));
        }
        if (lastSelectedService == null) {
            lastSelectedService = getSelectedService();
        } else {
            this.setSelectedItem(lastSelectedService);
        }
        this.setEditable(false);

        setToolTipText(getSelectedService().getServiceURL());
        this.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                lastSelectedService = getSelectedService();
                setToolTipText(lastSelectedService.getServiceURL());
            }
        });
    }


    public CSMHandle getSelectedService() {
        return (CSMHandle) getSelectedItem();
    }

}
