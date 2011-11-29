package org.cagrid.gaards.ui.csm.protectionelements;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.ProtectionElement;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class ProtectionElementPopUpMenu extends JPopupMenu {

    private JMenuItem removeMenu = null;
    private ProtectionElementsList list = null;
    private Application csmApp = null;

    /**
     * This method initializes
     */
    public ProtectionElementPopUpMenu(ProtectionElementsList list, Application csmApp) {
        super();
        this.list = list;
        this.csmApp = csmApp;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.add(loadRemovePEMenu());
    }


    /**
     * This method initializes removeMethodMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem loadRemovePEMenu() {
        if (removeMenu == null) {
            removeMenu = new JMenuItem();
            removeMenu.setText("Delete Protection Element");
            removeMenu.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    ProtectionElement el = (ProtectionElement)list.getSelectedValue();
                    try {
						csmApp.removeProtectionElement(el.getId());
						List data = list.getData();
						List filters = list.getFilters();
						data.remove(el);
						list.setData(data,filters);
					} catch (CSMInternalFault e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (AccessDeniedFault e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (CSMTransactionFault e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    
                }
            });
        }
        return removeMenu;
    }
}
