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
package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.security.dao.ApplicationSearchCriteria;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.FilterClause;
import org.cagrid.gaards.csm.bean.Group;
import org.cagrid.gaards.csm.bean.Permission;
import org.cagrid.gaards.csm.bean.Privilege;
import org.cagrid.gaards.csm.bean.ProtectionElement;
import org.cagrid.gaards.csm.bean.ProtectionGroup;
import org.cagrid.gaards.csm.bean.Role;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;
import org.cagrid.gaards.csm.stubs.types.HibernateSessionCreationFault;
import org.globus.wsrf.impl.security.descriptor.SecurityDescriptorException;
import org.globus.wsrf.security.SecurityManager;
import org.springframework.core.io.FileSystemResource;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class CSMImpl extends CSMImplBase {
    private static Logger myLogger = LogManager.getLogger(CSMImpl.class);
	private CSM csm;

	public CSMImpl() throws RemoteException {
		super();
		String configFile = null;
		String propertiesFile = null;
		try {
			configFile = CSMConfiguration.getConfiguration().getCsmConfiguration();
			propertiesFile = CSMConfiguration.getConfiguration().getCsmProperties();
			BeanUtils utils = new BeanUtils(new FileSystemResource(configFile),
					new FileSystemResource(propertiesFile));
			CSMProperties properties = utils.getCSMProperties();
			csm = new CSM(properties, new GridGrouperRemoteGroupSynchronizer());
		} catch (Exception e) {
			String configFilePath = (configFile==null) ? "null" : new File(configFile).getAbsolutePath();
			String propertiesFilePath = (propertiesFile==null) ? "null" : new File(propertiesFile).getAbsolutePath();
			String msg = "Error initializing CSM service: Config file=\"" + configFilePath + "\"; Properties file =\"" + propertiesFilePath + "\"";
			myLogger.error(msg, e);
			throw new RemoteException(Utils.getExceptionMessage(e));
		}
	}

	private String getCallerIdentity() {
		String caller = SecurityManager.getManager().getCaller();
		// System.out.println("Caller: " + caller);
		if ((caller == null) || (caller.trim().length() == 0)
				|| (caller.equals("<anonymous>"))) {
			caller = Constants.ANONYMOUS_CALLER;
		}
		return caller;
	}

  public org.cagrid.gaards.csm.bean.Application[] getApplications(org.cagrid.gaards.csm.bean.ApplicationSearchCriteria applicationSearchCriteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
		List<Application> apps = csm.getApplications(applicationSearchCriteria);
		Application[] result = new Application[apps.size()];
		return apps.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.Application createApplication(org.cagrid.gaards.csm.bean.Application application) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.createApplication(getCallerIdentity(), application);
	}

  public void removeApplication(long applicationId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.removeApplication(getCallerIdentity(), applicationId);
	}

  public org.cagrid.gaards.csm.bean.ProtectionElement createProtectionElement(org.cagrid.gaards.csm.bean.ProtectionElement pe) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.createProtectionElement(getCallerIdentity(), pe);
	}

  public org.cagrid.gaards.csm.bean.ProtectionElement[] getProtectionElements(org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria criteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<ProtectionElement> list = csm.getProtectionElements(
				getCallerIdentity(), criteria);
		ProtectionElement[] result = new ProtectionElement[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.ProtectionElement modifyProtectionElement(org.cagrid.gaards.csm.bean.ProtectionElement protectionElement) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.modifyProtectionElement(getCallerIdentity(),
				protectionElement);
	}

  public void removeProtectionElement(long protectionElementId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.removeProtectionElement(getCallerIdentity(),
				protectionElementId);
	}

  public org.cagrid.gaards.csm.bean.ProtectionGroup createProtectionGroup(org.cagrid.gaards.csm.bean.ProtectionGroup pg) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.createProtectionGroup(getCallerIdentity(), pg);
	}

  public org.cagrid.gaards.csm.bean.ProtectionGroup[] getProtectionGroups(org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria criteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<ProtectionGroup> list = csm.getProtectionGroups(
				getCallerIdentity(), criteria);
		ProtectionGroup[] result = new ProtectionGroup[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.ProtectionGroup modifyProtectionGroup(org.cagrid.gaards.csm.bean.ProtectionGroup pg) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.modifyProtectionGroup(getCallerIdentity(), pg);
	}

  public void removeProtectionGroup(long protectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.removeProtectionGroup(getCallerIdentity(), protectionGroupId);
	}

  public void assignProtectionElements(long protectionGroupId,long[] protectionElements) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Long> list = new ArrayList<Long>();
		if (protectionElements != null) {
			for (int i = 0; i < protectionElements.length; i++) {
				list.add(protectionElements[i]);
			}
		}
		this.csm.assignProtectionElements(getCallerIdentity(),
				protectionGroupId, list);
	}

  public void unassignProtectionElements(long protectionGroupId,long[] protectionElements) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Long> list = new ArrayList<Long>();
		if (protectionElements != null) {
			for (int i = 0; i < protectionElements.length; i++) {
				list.add(protectionElements[i]);
			}
		}
		this.csm.unassignProtectionElements(getCallerIdentity(),
				protectionGroupId, list);
	}

  public org.cagrid.gaards.csm.bean.ProtectionElement[] getProtectionElementsAssignedToProtectionGroup(long protectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<ProtectionElement> list = csm
				.getProtectionElementsAssignedToProtectionGroup(
						getCallerIdentity(), protectionGroupId);
		ProtectionElement[] result = new ProtectionElement[list.size()];
		return list.toArray(result);
	}

  public void assignProtectionGroup(long parentProtectionGroupId,long childProtectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.assignProtectionGroup(getCallerIdentity(),
				parentProtectionGroupId, childProtectionGroupId);
	}

  public void unassignProtectionGroup(long parentProtectionGroupId,long childProtectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.unassignProtectionGroup(getCallerIdentity(),
				parentProtectionGroupId, childProtectionGroupId);
	}

  public org.cagrid.gaards.csm.bean.ProtectionGroup[] getChildProtectionGroups(long protectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<ProtectionGroup> list = csm.getChildProtectionGroups(
				getCallerIdentity(), protectionGroupId);
		ProtectionGroup[] result = new ProtectionGroup[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.Group[] getGroups(org.cagrid.gaards.csm.bean.GroupSearchCriteria criteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Group> list = csm.getGroups(getCallerIdentity(), criteria);
		Group[] result = new Group[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.RemoteGroup linkRemoteGroup(org.cagrid.gaards.csm.bean.LinkRemoteGroupRequest req) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return csm.linkRemoteGroup(getCallerIdentity(), req);
	}

  public void unlinkRemoteGroup(long applicationId,long groupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.unlinkRemoteGroup(getCallerIdentity(), applicationId, groupId);
	}

  public org.cagrid.gaards.csm.bean.LocalGroup createGroup(org.cagrid.gaards.csm.bean.LocalGroup grp) throws RemoteException {
		return this.csm.createGroup(getCallerIdentity(), grp);
	}

  public void removeGroup(long groupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.removeGroup(getCallerIdentity(), groupId);
	}

  public org.cagrid.gaards.csm.bean.LocalGroup modifyGroup(org.cagrid.gaards.csm.bean.LocalGroup grp) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.modifyGroup(getCallerIdentity(), grp);
	}

  public void addUsersToGroup(long groupId,java.lang.String[] users) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.addUsersToGroup(getCallerIdentity(), groupId, Utils
				.asList(users));
	}

  public void removeUsersFromGroup(long groupId,java.lang.String[] users) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault {
		this.csm.removeUsersFromGroup(getCallerIdentity(), groupId, Utils
				.asList(users));
	}

  public java.lang.String[] getUsersInGroup(long groupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<String> list = this.csm.getUsersInGroup(getCallerIdentity(),
				groupId);
		String[] result = new String[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.Privilege createPrivilege(org.cagrid.gaards.csm.bean.Privilege privilege) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.createPrivilege(getCallerIdentity(), privilege);
	}

  public org.cagrid.gaards.csm.bean.Privilege[] getPrivileges(org.cagrid.gaards.csm.bean.PrivilegeSearchCriteria criteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Privilege> list = this.csm.getPrivileges(criteria);
		Privilege[] result = new Privilege[list.size()];
		return list.toArray(result);
	}

  public void removePrivilege(long privilegeId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.removePrivilege(getCallerIdentity(), privilegeId);
	}

  public org.cagrid.gaards.csm.bean.Privilege modifyPrivilege(org.cagrid.gaards.csm.bean.Privilege privilege) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.modifyPrivilege(getCallerIdentity(), privilege);
	}

  public org.cagrid.gaards.csm.bean.Role createRole(org.cagrid.gaards.csm.bean.Role role) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.createRole(getCallerIdentity(), role);
	}

  public org.cagrid.gaards.csm.bean.Role[] getRoles(org.cagrid.gaards.csm.bean.RoleSearchCriteria criteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Role> list = this.csm.getRoles(getCallerIdentity(), criteria);
		Role[] result = new Role[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.Role modifyRole(org.cagrid.gaards.csm.bean.Role role) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.modifyRole(getCallerIdentity(), role);
	}

  public void removeRole(long roleId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.removeRole(getCallerIdentity(), roleId);
	}

  public void setPrivilegesForRole(long roleId,long[] privs) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Long> list = new ArrayList<Long>();
		if (privs != null) {
			for (int i = 0; i < privs.length; i++) {
				list.add(privs[i]);
			}
		}
		this.csm.setPrivilegesForRole(getCallerIdentity(), roleId, list);
	}

  public org.cagrid.gaards.csm.bean.Privilege[] getPrivilegesForRole(long roleId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Privilege> list = this.csm.getPrivilegesAssignedToRole(
				getCallerIdentity(), roleId);
		Privilege[] result = new Privilege[list.size()];
		return list.toArray(result);
	}

  public void grantGroupPermission(long groupId,long roleId,long protectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.grantGroupPermission(getCallerIdentity(), groupId, roleId,
				protectionGroupId);
	}

  public void revokeGroupPermission(long groupId,long roleId,long protectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.revokeGroupPermission(getCallerIdentity(), groupId, roleId,
				protectionGroupId);
	}

  public void grantUserPermission(java.lang.String userIdentity,long roleId,long protectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.grantUserPermission(getCallerIdentity(), userIdentity, roleId,
				protectionGroupId);
	}

  public void revokeUserPermission(java.lang.String userIdentity,long roleId,long protectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		this.csm.revokeUserPermission(getCallerIdentity(), userIdentity,
				roleId, protectionGroupId);
	}

  public org.cagrid.gaards.csm.bean.Permission[] getPermissionsForUser(long applicationId,java.lang.String userIdentity) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Permission> list = this.csm.getPermissions(getCallerIdentity(),
				applicationId, userIdentity);
		Permission[] result = new Permission[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.Permission[] getPermissionForGroup(long groupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<Permission> list = this.csm.getPermissions(getCallerIdentity(),
				groupId);
		Permission[] result = new Permission[list.size()];
		return list.toArray(result);
	}

  public org.cagrid.gaards.csm.bean.ProtectionGroup getParentProtectionGroup(long childProtectionGroupId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.getParentProtectionGroup(getCallerIdentity(),
				childProtectionGroupId);
	}

  public boolean checkPermission(long applicationId,java.lang.String userIdentity,java.lang.String objectId,java.lang.String privilege) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.checkPermission(applicationId, userIdentity, objectId,
				privilege);
	}

  public boolean checkAttributeBasedPermission(long applicationId,java.lang.String userIdentity,java.lang.String objectId,java.lang.String attributeName,java.lang.String attributeValue,java.lang.String privilege) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.checkPermission(applicationId, userIdentity, objectId,
				attributeName, attributeValue, privilege);
	}

  public org.cagrid.gaards.csm.bean.FilterClause createFilterClause(org.cagrid.gaards.csm.bean.FilterClause filter) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.createFilterClause(getCallerIdentity(), filter);
	}

  public org.cagrid.gaards.csm.bean.FilterClause modifyFilterClause(long filterClauseId,java.lang.String sqlForGroup,java.lang.String sqlForUser) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		return this.csm.modifyFilterClause(getCallerIdentity(), filterClauseId,
				sqlForGroup, sqlForUser);
	}

  public org.cagrid.gaards.csm.bean.FilterClause[] getFilterClauses(org.cagrid.gaards.csm.bean.FilterClauseSearchCriteria criteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault, org.cagrid.gaards.csm.stubs.types.CSMTransactionFault {
		List<FilterClause> list = this.csm.getFilterClauses(
				getCallerIdentity(), criteria);
		FilterClause[] result = new FilterClause[list.size()];
		return list.toArray(result);
	}

  public void removeFilterClause(long filterClauseId) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault, org.cagrid.gaards.csm.stubs.types.AccessDeniedFault {
		this.csm.removeFilterClause(getCallerIdentity(), filterClauseId);
	}

  public org.cagrid.gaards.csm.filters.stubs.types.FilterCreatorReference createFilterCreator(long applicationID,byte[] applicationBeansJar,byte[] applicationORMJar,java.lang.String hibernateConfig) throws RemoteException, org.cagrid.gaards.csm.stubs.types.HibernateSessionCreationFault {
		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
				.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
				+ servicePath + "/" + "filterCreatorHome";
		org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResource thisResource = null;
		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResourceHome) initialContext
					.lookup(homeName);
			resourceKey = home.createResource();

			// Grab the newly created resource
			thisResource = (org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResource) home
					.find(resourceKey);

			// This is where the creator of this resource type can set whatever
			// needs
			// to be set on the resource so that it can function appropriatly
			// for instance
			// if you want the resouce to only have the query string then there
			// is where you would
			// give it the query string.

			// sample of setting creator only security. This will only allow the
			// caller that created
			// this resource to be able to use it.
			thisResource
					.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils
							.createCreatorOnlyResourceSecurityDescriptor());

			String transportURL = (String) ctx
					.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL
					.lastIndexOf('/') + 1);
			transportURL += "FilterCreator";
			epr = org.globus.wsrf.utils.AddressingUtils
					.createEndpointReference(transportURL, resourceKey);
		} catch (Exception e) {
			throw new RemoteException("Error looking up FilterCreator home:"
					+ e.getMessage(), e);
		}

		// configure the newly created resource
		try {
			thisResource.setHibernateConfigData(hibernateConfig);
			thisResource.setApplicationBeansJar(applicationBeansJar);
			thisResource.setApplicationORMJar(applicationORMJar);
			thisResource.setApplicationID(applicationID);
			thisResource.init();
		} catch (Exception e) {
			HibernateSessionCreationFault fault = new HibernateSessionCreationFault();
			fault.setFaultString("Hibernate Configuration Error: "
					+ e.getMessage());
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (HibernateSessionCreationFault) helper.getFault();
			throw fault;
		}

		// return the typed EPR
		org.cagrid.gaards.csm.filters.stubs.types.FilterCreatorReference ref = new org.cagrid.gaards.csm.filters.stubs.types.FilterCreatorReference();
		ref.setEndpointReference(epr);

		return ref;
	}

  public org.cagrid.gaards.csm.filters.stubs.types.FilterCreatorReference createFilterCreatorFromDomainModel(long applicationID,gov.nih.nci.cagrid.metadata.dataservice.DomainModel domainModel) throws RemoteException {
	  org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
				.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
				+ servicePath + "/" + "filterCreatorHome";
		org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResource thisResource = null;
		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResourceHome) initialContext
					.lookup(homeName);
			resourceKey = home.createResource();

			// Grab the newly created resource
			thisResource = (org.cagrid.gaards.csm.filters.service.globus.resource.FilterCreatorResource) home
					.find(resourceKey);

			// This is where the creator of this resource type can set whatever
			// needs
			// to be set on the resource so that it can function appropriatly
			// for instance
			// if you want the resouce to only have the query string then there
			// is where you would
			// give it the query string.

			// sample of setting creator only security. This will only allow the
			// caller that created
			// this resource to be able to use it.
			thisResource
					.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils
							.createCreatorOnlyResourceSecurityDescriptor());

			String transportURL = (String) ctx
					.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL
					.lastIndexOf('/') + 1);
			transportURL += "FilterCreator";
			epr = org.globus.wsrf.utils.AddressingUtils
					.createEndpointReference(transportURL, resourceKey);
		} catch (Exception e) {
			throw new RemoteException("Error looking up FilterCreator home:"
					+ e.getMessage(), e);
		}

		// configure the newly created resource
		try {
			thisResource.setApplicationID(applicationID);
			thisResource.setDomainModel(domainModel);
			thisResource.init();
		} catch (Exception e) {
			RemoteException ex = new RemoteException();
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}

		// return the typed EPR
		org.cagrid.gaards.csm.filters.stubs.types.FilterCreatorReference ref = new org.cagrid.gaards.csm.filters.stubs.types.FilterCreatorReference();
		ref.setEndpointReference(epr);

		return ref;
	}

}
