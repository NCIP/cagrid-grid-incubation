package org.cagrid.gaards.ui.csm.instancelevel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class FiltersPopUpMenu extends JPopupMenu {

    /**
     * 
     */
    private static final long serialVersionUID = -9004868310224230367L;

    //private FiltersList list = null;
    private JMenuItem removeFilterMenuItem = null;
    private ManagerSecurityFiltersPanel filtersPanel;


    /**
     * This method initializes
     */
    public FiltersPopUpMenu(ManagerSecurityFiltersPanel panel) {
        super();
        this.filtersPanel = panel;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
            this.add(getRemoveGroupMenuItem());
    }



    /**
     * This method initializes removeGroupMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getRemoveGroupMenuItem() {
        if (removeFilterMenuItem == null) {
        	removeFilterMenuItem = new JMenuItem();
        	removeFilterMenuItem.setText("Remove Filter");
        	removeFilterMenuItem.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    filtersPanel.removeSelectedFilter();
                }
            });
        }
        return removeFilterMenuItem;
    }
}
