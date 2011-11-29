package org.cagrid.gaards.csm.client;

public class RemoteGroup extends Group {

    private org.cagrid.gaards.csm.bean.RemoteGroup grp;


    public RemoteGroup(Application app, org.cagrid.gaards.csm.bean.RemoteGroup grp) {
        super(app, grp);
        this.grp = grp;
    }


    /**
     * Sets the name of the group
     * 
     * @param name
     *            The name of the group
     */
    public void setName(String name) {
        super.setName(name);
    }


    /**
     * Gets the remote name of the grid grouper group.
     * 
     * @return The name for the group in Grid Grouper.
     */
    public String getRemoteGroupName() {
        return this.grp.getRemoteGroupName();
    }


    /**
     * Get the URL of the Grid Grouper that maintains the remote group.
     * 
     * @return The URL of the Grid Grouper that maintains the remote group.
     */

    public String getGridGrouperURL() {
        return this.grp.getGridGrouperURL();
    }

}
