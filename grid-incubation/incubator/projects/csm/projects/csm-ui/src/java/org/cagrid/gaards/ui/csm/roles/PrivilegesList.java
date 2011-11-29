package org.cagrid.gaards.ui.csm.roles;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.cagrid.gaards.csm.client.Privilege;


public class PrivilegesList extends JList {

    private DefaultListModel model;


    public PrivilegesList() {
        super();
        this.model = new DefaultListModel();
        this.setModel(model);
        this.setCellRenderer(new PrivilegesListRenderer());
    }


    public void addPrivilege(final Privilege privilege) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.addElement(privilege);
            }
        });
    }


    public List<Privilege> getSelectPrivileges() {
        List<Privilege> privileges = new ArrayList<Privilege>();
        Object[] list = getSelectedValues();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                privileges.add((Privilege) list[i]);
            }
        }
        return privileges;
    }


    public List<Privilege> getPrivileges() {
        List<Privilege> privileges = new ArrayList<Privilege>();
        for (int i = 0; i < model.size(); i++) {
            privileges.add((Privilege) model.getElementAt(i));
        }
        return privileges;
    }


    public void setDifference(final List<Privilege> list, final List<Privilege> excludes) {
        List<Privilege> diff = new ArrayList<Privilege>();
        for (int i = 0; i < list.size(); i++) {
            boolean isFound = false;
            for (int j = 0; j < excludes.size(); j++) {
                if (list.get(i).getId() == excludes.get(j).getId()) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                diff.add(list.get(i));
            }
        }
        setPrivileges(diff);
    }


    public void setPrivileges(final List<Privilege> privileges) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();
                for (int i = 0; i < privileges.size(); i++) {
                    model.addElement(privileges.get(i));
                }
            }
        });
    }


    public void clearAllPrivileges() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model.removeAllElements();
            }
        });
    }
    

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800,100);
		
	}
	

}
