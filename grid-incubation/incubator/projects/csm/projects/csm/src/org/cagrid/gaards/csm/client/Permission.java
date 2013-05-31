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
package org.cagrid.gaards.csm.client;

import java.util.ArrayList;
import java.util.List;


public class Permission {
    private Application application;
    private ProtectionGroup protectionGroup;
    private List<Role> roles;
    private String user;
    private Group group;


    public Permission(Application application, org.cagrid.gaards.csm.bean.Permission bean) {
        setApplication(application);
        if (bean.getUser() != null) {
            setUser(bean.getUser());
        }
        if (bean.getGroup() != null) {
            org.cagrid.gaards.csm.bean.Group grp = bean.getGroup();
            if (grp instanceof org.cagrid.gaards.csm.bean.LocalGroup) {
                setGroup(new LocalGroup(application, (org.cagrid.gaards.csm.bean.LocalGroup) grp));
            } else if (grp instanceof org.cagrid.gaards.csm.bean.RemoteGroup) {
                setGroup(new RemoteGroup(application, (org.cagrid.gaards.csm.bean.RemoteGroup) grp));
            }
        }

        setProtectionGroup(new ProtectionGroup(application, bean.getProtectionGroup()));

        roles = new ArrayList<Role>();
        org.cagrid.gaards.csm.bean.Role[] list = bean.getRoles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                roles.add(new Role(application, list[i]));
            }
        }

    }


    /**
     * This method returns the application that the permission is associated
     * with.
     * 
     * @return The application that the permission is associated with.
     */

    public Application getApplication() {
        return application;
    }


    private void setApplication(Application application) {
        this.application = application;
    }


    /**
     * This method returns the protection group that the permission is granting
     * access to.
     * 
     * @return The protection group that the permission is granting access to.
     */

    public ProtectionGroup getProtectionGroup() {
        return protectionGroup;
    }


    private void setProtectionGroup(ProtectionGroup protectionGroup) {
        this.protectionGroup = protectionGroup;
    }


    /**
     * This method returns the user that is being granted the permission.
     * 
     * @return The user that is being granted the permission.
     */
    public String getUser() {
        return user;
    }


    private void setUser(String user) {
        this.user = user;
    }


    /**
     * This method returns the group that is being granted the permission.
     * 
     * @return The group that is being granted the permission.
     */
    public Group getGroup() {
        return group;
    }


    private void setGroup(Group group) {
        this.group = group;
    }


    /**
     * This method returns a list of roles that are being granted.
     * 
     * @return The list of roles that are being granted.
     */
    public List<Role> getRoles() {
        return roles;
    }


    /**
     * This method returns whether or not this permission was granted to a user.
     * 
     * @return Whether or not this permission was granted to a user.
     */

    public boolean isUserPermission() {
        if (getUser() != null) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * This method returns whether or not this permission was granted to a
     * group.
     * 
     * @return Whether or not this permission was granted to a group.
     */

    public boolean isGroupPermission() {
        if (getGroup() != null) {
            return true;
        } else {
            return false;
        }
    }

}
