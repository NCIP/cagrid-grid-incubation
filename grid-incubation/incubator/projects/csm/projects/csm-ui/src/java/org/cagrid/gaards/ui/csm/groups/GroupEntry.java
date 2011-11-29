package org.cagrid.gaards.ui.csm.groups;

import javax.swing.ImageIcon;

import org.cagrid.gaards.csm.client.Group;
import org.cagrid.gaards.csm.client.RemoteGroup;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;


public class GroupEntry {

    private ImageIcon image;
    private Group group;


    public GroupEntry(Group group) {
        this.group = group;
        if (group instanceof RemoteGroup) {
            this.image = CSMLookAndFeel.getGrouperIcon22x22();
        } else {
            this.image = CSMLookAndFeel.getCSMIcon22();
        }
    }


    public String getName() {
        return group.getName();
    }


    public ImageIcon getImage() {
        return image;
    }


    public String toString() {
        return group.getName();
    }


    public Group getGroup() {
        return this.group;
    }
}
