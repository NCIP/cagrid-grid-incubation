package org.cagrid.gaards.csm.client;

import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.csm.bean.FilterClauseSearchCriteria;
import org.cagrid.gaards.csm.bean.GroupSearchCriteria;
import org.cagrid.gaards.csm.bean.LinkRemoteGroupRequest;
import org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria;
import org.cagrid.gaards.csm.bean.RoleSearchCriteria;
import org.cagrid.gaards.csm.filters.client.FilterCreatorClient;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;
import org.cagrid.gaards.csm.stubs.types.HibernateSessionCreationFault;

public class Application {

	private CSM csm;
	private long id;
	private String name;
	private String description;
	private Calendar lastUpdated;

	public Application(CSM csm, org.cagrid.gaards.csm.bean.Application bean) {
		setCSM(csm);
		if (bean.getId() != null) {
			setId(bean.getId().longValue());
		}
		setName(bean.getName());
		setDescription(bean.getDescription());
		setLastUpdated(bean.getLastUpdated());
	}

	/**
	 * Returns the CSM Web Service managing the application.
	 * 
	 * @return The CSM managing the application.
	 */

	public CSM getCSM() {
		return csm;
	}

	private void setCSM(CSM csm) {
		this.csm = csm;
	}

	/**
	 * Returns the CSM assigned Id of the application.
	 * 
	 * @return The CSM assigned Id of the application.
	 */

	public long getId() {
		return id;
	}

	private void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the application.
	 * 
	 * @return The name of the application.
	 */

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a description of the application.
	 * 
	 * @return A description of the application.
	 */
	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the date the application was last updated.
	 * 
	 * @return The date the application was last updated.
	 */

	public Calendar getLastUpdated() {
		return lastUpdated;
	}

	private void setLastUpdated(Calendar lastUpdate) {
		this.lastUpdated = lastUpdate;
	}

	/**
	 * This method allows an administrator of the application to create a
	 * protection element.
	 * 
	 * @param name
	 *            The name of the protection element, the name will also be used
	 *            as the object id.
	 * @param description
	 *            A description of the protection element.
	 * @return The protection element that was created.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public ProtectionElement createProtectionElement(String name,
			String description) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		return createProtectionElement(name, description, name, null, null,
				null);
	}

	/**
	 * This method allows an administrator of the application to create a
	 * protection element.
	 * 
	 * @param name
	 *            The name of the protection element.
	 * @param description
	 *            A description of the protection element.
	 * @param objectId
	 *            The object id of the protection element.
	 * @return The protection element that was created.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public ProtectionElement createProtectionElement(String name,
			String description, String objectId) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		return createProtectionElement(name, description, objectId, null, null,
				null);
	}

	/**
	 * This method allows an administrator of the application to create a
	 * protection element.
	 * 
	 * @param name
	 *            The name of the protection element.
	 * @param description
	 *            A description of the protection element.
	 * @param objectId
	 *            The object id of the protection element.
	 * @param attribute
	 *            The attribute of the protection element
	 * @param attributeValue
	 *            The value of the attribute of the protection element.
	 * @param type
	 *            The type of the protection element.
	 * @return The protection element that was created.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public ProtectionElement createProtectionElement(String name,
			String description, String objectId, String attribute,
			String attributeValue, String type) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		org.cagrid.gaards.csm.bean.ProtectionElement bean = new org.cagrid.gaards.csm.bean.ProtectionElement();
		bean.setApplicationId(getId());
		bean.setAttribute(attribute);
		bean.setAttributeValue(attributeValue);
		bean.setDescription(description);
		bean.setName(name);
		bean.setObjectId(objectId);
		bean.setType(type);
		org.cagrid.gaards.csm.bean.ProtectionElement pe = getCSM().getClient()
				.createProtectionElement(bean);
		return new ProtectionElement(this, pe);
	}

	/**
	 * This method allows an application administrator to search for protection
	 * elements associated with the application base on provided search
	 * criteria.
	 * 
	 * @param criteria
	 *            Search criteria for limiting the protection elements returned.
	 * @return The set of protection elements satisfying the search criteria.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public List<ProtectionElement> getProtectionElements(
			ProtectionElementSearchCriteria criteria) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		if (criteria == null) {
			criteria = new ProtectionElementSearchCriteria();
		}
		criteria.setApplicationId(getId());
		org.cagrid.gaards.csm.bean.ProtectionElement[] result = getCSM()
				.getClient().getProtectionElements(criteria);
		List<ProtectionElement> list = new ArrayList<ProtectionElement>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				list.add(new ProtectionElement(this, result[i]));
			}
		}
		return list;
	}

	/**
	 * This method an allows an application admin to remove a protection
	 * element.
	 * 
	 * @param pe
	 *            The protection element to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public void removeProtectionElement(ProtectionElement pe)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		if (pe != null) {
			removeProtectionElement(pe.getId());
		}
	}

	/**
	 * This method an allows an application admin to remove a protection
	 * element.
	 * 
	 * @param protectionElementId
	 *            The id of the protection element to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void removeProtectionElement(long protectionElementId)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		getCSM().getClient().removeProtectionElement(protectionElementId);
	}

	/**
	 * This method allows an administrator of the application to create a
	 * protection group.
	 * 
	 * @param name
	 *            The name of the protection element.
	 * @param description
	 *            A description of the protection element.
	 * @return The protection group that was created.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public ProtectionGroup createProtectionGroup(String name, String description)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		org.cagrid.gaards.csm.bean.ProtectionGroup bean = new org.cagrid.gaards.csm.bean.ProtectionGroup();
		bean.setApplicationId(getId());
		bean.setDescription(description);
		bean.setName(name);
		org.cagrid.gaards.csm.bean.ProtectionGroup pg = getCSM().getClient()
				.createProtectionGroup(bean);
		return new ProtectionGroup(this, pg);
	}

	/**
	 * This method allows an application administrator to search for protection
	 * groups associated with the application based on provided search criteria.
	 * 
	 * @param criteria
	 *            Search criteria for limiting the protection groups returned.
	 * @return The set of protection groups satisfying the search criteria.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public List<ProtectionGroup> getProtectionGroups(
			ProtectionGroupSearchCriteria criteria) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		if (criteria == null) {
			criteria = new ProtectionGroupSearchCriteria();
		}
		criteria.setApplicationId(getId());
		org.cagrid.gaards.csm.bean.ProtectionGroup[] result = getCSM()
				.getClient().getProtectionGroups(criteria);
		List<ProtectionGroup> list = new ArrayList<ProtectionGroup>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				list.add(new ProtectionGroup(this, result[i]));
			}
		}
		return list;
	}

	/**
	 * This method an allows an application admin to remove a protection group.
	 * 
	 * @param pg
	 *            The protection group to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public void removeProtectionGroup(ProtectionGroup pg)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		if (pg != null) {
			removeProtectionGroup(pg.getId());
		}
	}

	/**
	 * This method an allows an application admin to remove a protection group.
	 * 
	 * @param protectionGroupId
	 *            The id of the protection group to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void removeProtectionGroup(long protectionGroupId)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		getCSM().getClient().removeProtectionGroup(protectionGroupId);
	}

	/**
	 * This method links a Grid Grouper Group to CSM such that access control
	 * policy may be provisioned based on membership to it.
	 * 
	 * @param gridGrouperURL
	 *            The URL of the Grid Grouper managing the group to link.
	 * @param gridGrouperGroupName
	 *            The name of the group in Grid Grouper.
	 * @param localGroupName
	 *            A local name for the group within CSM.
	 * @return The remote group that was linked.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public RemoteGroup linkRemoteGroup(String gridGrouperURL,
			String gridGrouperGroupName, String localGroupName)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		LinkRemoteGroupRequest req = new LinkRemoteGroupRequest();
		req.setApplicationId(getId());
		req.setRemoteGroupName(gridGrouperGroupName);
		req.setGridGrouperURL(gridGrouperURL);
		req.setLocalGroupName(localGroupName);
		org.cagrid.gaards.csm.bean.RemoteGroup rg = getCSM().getClient()
				.linkRemoteGroup(req);
		return new RemoteGroup(this, rg);
	}

	/**
	 * This method unlinks a Grid Grouper group from CSM such that the group can
	 * no longer be used to provision access control policy.
	 * 
	 * @param group
	 *            The group to unlink
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void unlinkRemoteGroup(RemoteGroup grp) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		this.unlinkRemoteGroup(grp.getId());
	}

	/**
	 * This method unlinks a Grid Grouper group from CSM such that the group can
	 * no longer be used to provision access control policy.
	 * 
	 * @param groupId
	 *            The id of the group to unlink
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void unlinkRemoteGroup(long groupId) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		getCSM().getClient().unlinkRemoteGroup(getId(), groupId);
	}

	/**
	 * This method obtains a list of user groups that can be used for
	 * provisioning access control policy.
	 * 
	 * @param criteria
	 *            The search criteria to limit the result set of user groups
	 *            returned.
	 * @return The list of user groups meeting the search criteria.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public List<Group> getGroups(GroupSearchCriteria criteria)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		if (criteria == null) {
			criteria = new GroupSearchCriteria();
		}
		criteria.setApplicationId(getId());
		List<Group> groups = new ArrayList<Group>();
		org.cagrid.gaards.csm.bean.Group[] list = getCSM().getClient()
				.getGroups(criteria);
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				if (list[i] instanceof org.cagrid.gaards.csm.bean.LocalGroup) {
					groups.add(new LocalGroup(this,
							(org.cagrid.gaards.csm.bean.LocalGroup) list[i]));
				} else if (list[i] instanceof org.cagrid.gaards.csm.bean.RemoteGroup) {
					groups.add(new RemoteGroup(this,
							(org.cagrid.gaards.csm.bean.RemoteGroup) list[i]));
				}
			}
		}
		return groups;
	}

	/**
	 * This method allows an application admin to create a local user group.
	 * 
	 * @param name
	 *            The name of the group to create.
	 * @param description
	 *            The description of the group to create.
	 * @return The group created.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public LocalGroup createGroup(String name, String description)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		org.cagrid.gaards.csm.bean.LocalGroup grp = new org.cagrid.gaards.csm.bean.LocalGroup();
		grp.setApplicationId(getId());
		grp.setName(name);
		grp.setDescription(description);
		org.cagrid.gaards.csm.bean.LocalGroup created = this.getCSM()
				.getClient().createGroup(grp);
		return new LocalGroup(this, created);
	}

	/**
	 * This method allows an application admin to remove a local user group.
	 * 
	 * @param groupId
	 *            The local user group to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void removeGroup(LocalGroup group) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		this.removeGroup(group.getId());
	}

	/**
	 * This method allows an application admin to remove a local user group.
	 * 
	 * @param groupId
	 *            The id of the local user group to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void removeGroup(long groupId) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		getCSM().getClient().removeGroup(groupId);
	}

	/**
	 * This method allows an application admin to create a role.
	 * 
	 * @param name
	 *            The name of the role to create.
	 * @param description
	 *            The description of the role to create.
	 * @return The role that was created.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public Role createRole(String name, String description)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		org.cagrid.gaards.csm.bean.Role role = new org.cagrid.gaards.csm.bean.Role();
		role.setApplicationId(getId());
		role.setName(name);
		role.setDescription(description);
		org.cagrid.gaards.csm.bean.Role created = this.getCSM().getClient()
				.createRole(role);
		return new Role(this, created);
	}

	/**
	 * This method allows an application admin to get a list of roles associated
	 * with the application.
	 * 
	 * @param criteria
	 *            The search criteria to limit the list or roles by.
	 * @return The list of roles associated with the application that meet the
	 *         specified search criteria.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public List<Role> getRoles(RoleSearchCriteria criteria)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		if (criteria == null) {
			criteria = new RoleSearchCriteria();
		}
		criteria.setApplicationId(getId());
		org.cagrid.gaards.csm.bean.Role[] result = getCSM().getClient()
				.getRoles(criteria);
		List<Role> list = new ArrayList<Role>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				list.add(new Role(this, result[i]));
			}
		}
		return list;
	}

	/**
	 * This method allows an application admin to remove a role.
	 * 
	 * @param roleId
	 *            The role id of the role to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void removeRole(long roleId) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		this.getCSM().getClient().removeRole(roleId);
	}

	/**
	 * This method allows an application admin to remove a role.
	 * 
	 * @param role
	 *            The role to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void removeRole(Role role) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		this.removeRole(role.getId());
	}

	/**
	 * This method allows an application admin to grant a permission to a user.
	 * 
	 * @param userIdentity
	 *            The user to grant the permission to.
	 * @param role
	 *            The role the user is being granted.
	 * @param pg
	 *            The protection group in which the permission is being granted
	 *            on.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void grantPermission(String userIdentity, Role role,
			ProtectionGroup pg) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		this.grantPermission(userIdentity, role.getId(), pg.getId());
	}

	/**
	 * This method allows an application admin to grant a permission to a user.
	 * 
	 * @param userIdentity
	 *            The user identity of the user being granted the permission
	 * @param roleId
	 *            The role id of the role in which the user is being granted.
	 * @param protectionGroupId
	 *            the protection group id of the protection group in which the
	 *            user is being granted permission to.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void grantPermission(String userIdentity, long roleId,
			long protectionGroupId) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		this.csm.getClient().grantUserPermission(userIdentity, roleId,
				protectionGroupId);
	}

	/**
	 * This method allows an application admin to revoke a permission from a
	 * user.
	 * 
	 * @param userIdentity
	 *            The user to revoke the permission from.
	 * @param role
	 *            The role that is being revoked from the user.
	 * @param pg
	 *            The protection group in which the permission is being revoked
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void revokePermission(String userIdentity, Role role,
			ProtectionGroup pg) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		this.revokePermission(userIdentity, role.getId(), pg.getId());
	}

	/**
	 * This method allows an application admin to revoke a permission from a
	 * user.
	 * 
	 * @param userIdentity
	 *            The user identity of the user that the permission is being
	 *            revoked from.
	 * @param roleId
	 *            The role id of the role that will be revoked from the user.
	 * @param protectionGroupId
	 *            the protection group id of the protection group in which the
	 *            user will be revoked permission from.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void revokePermission(String userIdentity, long roleId,
			long protectionGroupId) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		this.csm.getClient().revokeUserPermission(userIdentity, roleId,
				protectionGroupId);
	}

	/**
	 * This method allows an application admin to grant a permission to a user
	 * group.
	 * 
	 * @param grp
	 *            The user group to grant the permission to.
	 * @param role
	 *            The role the group is being granted.
	 * @param pg
	 *            The protection group in which the permission is being granted
	 *            on.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void grantPermission(Group grp, Role role, ProtectionGroup pg)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		this.grantPermission(grp.getId(), role.getId(), pg.getId());
	}

	/**
	 * This method allows an application admin to grant a permission to a user.
	 * 
	 * @param groupId
	 *            The groupId of the group being granted the permission
	 * @param roleId
	 *            The role id of the role in which the group is being granted.
	 * @param protectionGroupId
	 *            the protection group id of the protection group in which the
	 *            group is being granted permission to.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void grantPermission(long groupId, long roleId,
			long protectionGroupId) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		this.csm.getClient().grantGroupPermission(groupId, roleId,
				protectionGroupId);
	}

	/**
	 * This method allows an application admin to revoke a permission from a
	 * group.
	 * 
	 * @param grp
	 *            The group to revoke the permission from.
	 * @param role
	 *            The role that is being revoked from the group.
	 * @param pg
	 *            The protection group in which the permission is being revoked
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void revokePermission(Group grp, Role role, ProtectionGroup pg)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		this.revokePermission(grp.getId(), role.getId(), pg.getId());
	}

	/**
	 * This method allows an application admin to revoke a permission from a
	 * group.
	 * 
	 * @param groupId
	 *            The group id of the group that the permission is being revoked
	 *            from.
	 * @param roleId
	 *            The role id of the role that will be revoked from the group.
	 * @param protectionGroupId
	 *            the protection group id of the protection group in which the
	 *            group will be revoked permission from.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void revokePermission(long groupId, long roleId,
			long protectionGroupId) throws RemoteException, CSMInternalFault,
			AccessDeniedFault, CSMTransactionFault {
		this.csm.getClient().revokeGroupPermission(groupId, roleId,
				protectionGroupId);
	}

	/**
	 * This method allows an application admin to revoke a permission.
	 * 
	 * @param permission
	 *            The permission to revoke.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void revokePermission(Permission permission) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		if (permission.isGroupPermission()) {
			List<Role> roles = permission.getRoles();
			for (int i = 0; i < roles.size(); i++) {
				revokePermission(permission.getGroup(), roles.get(i),
						permission.getProtectionGroup());
			}
		} else if (permission.isUserPermission()) {
			List<Role> roles = permission.getRoles();
			for (int i = 0; i < roles.size(); i++) {
				revokePermission(permission.getUser(), roles.get(i), permission
						.getProtectionGroup());
			}
		}
	}

	/**
	 * This method allows an application admin to get a list of permissions
	 * granted to a group.
	 * 
	 * @param groupId
	 *            The groupId of the group in which to get the permissions
	 *            granted.
	 * @return The permissions granted to the group.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public List<Permission> getPermissions(long groupId)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		org.cagrid.gaards.csm.bean.Permission[] result = getCSM().getClient()
				.getPermissionForGroup(groupId);
		List<Permission> list = new ArrayList<Permission>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				list.add(new Permission(this, result[i]));
			}
		}
		return list;
	}

	/**
	 * This method allows an application admin to get a list of permissions
	 * granted to a group.
	 * 
	 * @param group
	 *            The group in which to get the permissions granted.
	 * @return The permissions granted to the group.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public List<Permission> getPermissions(Group grp) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		return getPermissions(grp.getId());
	}

	/**
	 * This method allows an application admin to get a list of permissions
	 * granted to a user.
	 * 
	 * @param user
	 *            The user identity of the user in which to get the permissions
	 *            granted.
	 * @return The permissions granted to the user.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public List<Permission> getPermissions(String user) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		org.cagrid.gaards.csm.bean.Permission[] result = getCSM().getClient()
				.getPermissionsForUser(getId(), user);
		List<Permission> list = new ArrayList<Permission>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				list.add(new Permission(this, result[i]));
			}
		}
		return list;
	}

	/**
	 * This method allow one to check whether or not a user has a specified
	 * privilege on a specified resource.
	 * 
	 * @param userIdentity
	 *            The identity of the user
	 * @param objectId
	 *            The resource
	 * @param privilege
	 *            The privilege to check if the user has on the resource.
	 * @return
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws CSMTransactionFault
	 */
	public boolean checkPermission(String userIdentity, String objectId,
			String privilege) throws RemoteException, CSMInternalFault,
			CSMTransactionFault {
		return getCSM().getClient().checkPermission(getId(), userIdentity,
				objectId, privilege);
	}

	/**
	 * This method allow one to check whether or not a user has a specified
	 * privilege on a specified resource.
	 * 
	 * @param userIdentity
	 *            The identity of the user
	 * @param objectId
	 *            The name or type of resource
	 * @param attributeName
	 *            The name of an attribute type on the resource.
	 * @param attributeValue
	 *            The instance value of the attribute.
	 * @param privilege
	 *            The privilege to check if the user has on the resource.
	 * @return
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws CSMTransactionFault
	 */

	public boolean checkPermission(String userIdentity, String objectId,
			String attributeName, String attributeValue, String privilege)
			throws RemoteException, CSMInternalFault, CSMTransactionFault {
		return getCSM().getClient().checkAttributeBasedPermission(getId(),
				userIdentity, objectId, attributeName, attributeValue,
				privilege);
	}

	/**
	 * This method allows an application admin to add a filter clause.
	 * 
	 * @param bean
	 * @return The created filter clause
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public FilterClause createFilterClause(
			org.cagrid.gaards.csm.bean.FilterClause bean)
			throws RemoteException, CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault {
		org.cagrid.gaards.csm.bean.FilterClause f = getCSM().getClient()
				.createFilterClause(bean);
		return new FilterClause(this, f);
	}

	/**
	 * This method allows an application administrator to search for filter
	 * clauses associated with the application based on provided search
	 * criteria.
	 * 
	 * @param criteria
	 *            Search criteria for limiting the filter clauses returned.
	 * @return The set of filter clauses satisfying the search criteria.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public List<FilterClause> getFilterClauses(
			FilterClauseSearchCriteria criteria) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		if (criteria == null) {
			criteria = new FilterClauseSearchCriteria();
		}
		criteria.setApplicationId(getId());
		org.cagrid.gaards.csm.bean.FilterClause[] result = getCSM().getClient()
				.getFilterClauses(criteria);
		List<FilterClause> list = new ArrayList<FilterClause>();
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				list.add(new FilterClause(this, result[i]));
			}
		}
		return list;
	}

	/**
	 * This method an allows an application admin to remove a filter clause.
	 * 
	 * @param f
	 *            The filter clause to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */

	public void removeFilterClause(FilterClause f) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		if (f != null) {
			removeFilterClause(f.getId());
		}
	}

	/**
	 * This method an allows an application admin to remove a filter clause.
	 * 
	 * @param id
	 *            The id of the filter clause to remove.
	 * @throws RemoteException
	 * @throws CSMInternalFault
	 * @throws AccessDeniedFault
	 * @throws CSMTransactionFault
	 */
	public void removeFilterClause(long id) throws RemoteException,
			CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		getCSM().getClient().removeFilterClause(id);
	}

	/**
	 * Method creates a resource on the service to be used to create security
	 * filters for instance level data
	 * 
	 * @param hibernateConfig
	 * @param beansJar
	 * @param ormJar
	 * @return
	 * @throws HibernateSessionCreationFault
	 * @throws RemoteException
	 * @throws MalformedURIException
	 */
	public FilterCreatorClient createFilterCreatorClient(File beansJar,
			File ormJar, String hibernateConfig)
			throws HibernateSessionCreationFault, RemoteException,
			MalformedURIException {
		return getCSM().getClient().createFilterCreator(getId(),
				getBytesFromFile(beansJar), getBytesFromFile(ormJar),
				hibernateConfig);
	}
	
	/**
	 * Method creates a resource on the service to be used to create security
	 * filters for instance level data
	 * 
	 * @param domainModel
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURIException
	 */
	public FilterCreatorClient createFilterCreatorClient(DomainModel domainModel)
			throws RemoteException,
			MalformedURIException {
		return getCSM().getClient().createFilterCreatorFromDomainModel(getId(),domainModel);
	}

	private static byte[] getBytesFromFile(File file) {
		InputStream is;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			return null;
		}

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		try {
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ file.getName());
			}

			// Close the input stream and return bytes
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bytes;
	}

}
