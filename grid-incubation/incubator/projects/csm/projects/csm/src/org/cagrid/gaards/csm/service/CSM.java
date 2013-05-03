/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroupRoleContext;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.bean.FilterClause;
import org.cagrid.gaards.csm.bean.Group;
import org.cagrid.gaards.csm.bean.GroupSearchCriteria;
import org.cagrid.gaards.csm.bean.LinkRemoteGroupRequest;
import org.cagrid.gaards.csm.bean.LocalGroup;
import org.cagrid.gaards.csm.bean.Permission;
import org.cagrid.gaards.csm.bean.Privilege;
import org.cagrid.gaards.csm.bean.PrivilegeSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionElement;
import org.cagrid.gaards.csm.bean.ProtectionGroup;
import org.cagrid.gaards.csm.bean.RemoteGroup;
import org.cagrid.gaards.csm.bean.Role;
import org.cagrid.gaards.csm.bean.RoleSearchCriteria;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class CSM {

    private CSMProperties conf;
    private gov.nih.nci.security.authorization.domainobjects.Application webService;
    private Log log;
    private AuthorizationManagerFactory factory;
    private AuthorizationManager auth;
    private RemoteGroupManager remote;


    public CSM(CSMProperties conf, RemoteGroupSynchronizer sync) throws Exception {
        this.conf = conf;
        this.log = LogFactory.getLog(getClass().getName());
        this.factory = new AuthorizationManagerFactory(conf);
        this.auth = factory.getDefaultAuthorizationManager();
        this.remote = new RemoteGroupManager(factory, conf, sync);
        this.webService = auth.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT);

    }


    public void addWebServiceAdmin(String gridIdentity) throws CSMInternalFault {
        CSMInitializer.addWebServiceAdmin(auth, gridIdentity);
    }


    public void shutdown() throws Exception {
        this.remote.shutdown();
    }


    public List<Application> getApplications(ApplicationSearchCriteria applicationSearchCriteria)
        throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
        List<gov.nih.nci.security.authorization.domainobjects.Application> apps = this.auth.getObjects(CSMUtils
            .convert(applicationSearchCriteria));
        return CSMUtils.convert(apps);
    }


    private boolean applicationExist(String name) {
        try {
            auth.getApplication(name);
        } catch (CSObjectNotFoundException e) {
            return false;
        }
        return true;
    }


    public Application createApplication(String callerIdentity, Application app) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        checkWebServiceAdmin(callerIdentity);

        if (Utils.clean(app.getName()) == null) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot create application, no name specified!!");
            throw fault;
        }

        if (applicationExist(app.getName())) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot create the application " + app.getName()
                + ", an application with that name already exists.");
            throw fault;
        }

        gov.nih.nci.security.authorization.domainobjects.Application a = CSMUtils.convert(app);
        try {
            this.auth.createApplication(a);
            gov.nih.nci.security.authorization.domainobjects.Application search = new gov.nih.nci.security.authorization.domainobjects.Application();
            search.setApplicationName(app.getName());
            List<gov.nih.nci.security.authorization.domainobjects.Application> apps = this.auth
                .getObjects(new gov.nih.nci.security.dao.ApplicationSearchCriteria(search));
            gov.nih.nci.security.authorization.domainobjects.Application created = apps.get(0);
            CSMInitializer.initializeApplication(auth, auth.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT), created);
            return CSMUtils.convert(created);
        } catch (Exception e) {
            String error = "Error creating the application " + app.getName() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removeApplication(String callerIdentity, long applicationId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        checkWebServiceAdmin(callerIdentity);

        gov.nih.nci.security.authorization.domainobjects.Application app = null;
        try {
            app = auth.getApplicationById(String.valueOf(applicationId));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot remove the application (" + applicationId + ") because it does no exist.");
            throw fault;
        }

        if (app.getApplicationName().equals(Constants.CSM_WEB_SERVICE_CONTEXT)) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("The application, " + Constants.CSM_WEB_SERVICE_CONTEXT + " cannot be removed.");
            throw fault;
        }
        try {
            auth.removeApplication(String.valueOf(applicationId));

            // Remove protection element
            gov.nih.nci.security.authorization.domainobjects.ProtectionElement pe = new gov.nih.nci.security.authorization.domainobjects.ProtectionElement();
            pe.setApplication(webService);
            pe.setProtectionElementName(app.getApplicationName());
            List<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> pes = auth
                .getObjects(new gov.nih.nci.security.dao.ProtectionElementSearchCriteria(pe));
            if (pes.size() > 0) {
                auth.removeProtectionElement(String.valueOf(pes.get(0).getProtectionElementId()));
            }
            // Remove protection group
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = new gov.nih.nci.security.authorization.domainobjects.ProtectionGroup();
            pg.setApplication(webService);
            pg.setProtectionGroupName(app.getApplicationName());
            List<gov.nih.nci.security.authorization.domainobjects.ProtectionGroup> pgs = auth
                .getObjects(new gov.nih.nci.security.dao.ProtectionGroupSearchCriteria(pg));
            if (pgs.size() > 0) {
                auth.removeProtectionGroup(String.valueOf(pgs.get(0).getProtectionGroupId()));
            }

            // Remove Group

            gov.nih.nci.security.authorization.domainobjects.Group grp = new gov.nih.nci.security.authorization.domainobjects.Group();
            grp.setApplication(webService);
            grp.setGroupName(app.getApplicationName() + " " + Constants.ADMIN_GROUP_SUFFIX);
            List<gov.nih.nci.security.authorization.domainobjects.Group> grps = auth
                .getObjects(new gov.nih.nci.security.dao.GroupSearchCriteria(grp));
            if (grps.size() > 0) {
                auth.removeGroup(String.valueOf(grps.get(0).getGroupId()));
            }

        } catch (Exception e) {
            String error = "Error removing the application " + applicationId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public ProtectionElement createProtectionElement(String callerIdentity, ProtectionElement pe)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        if (pe.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to add the protection element to.!!!");
            throw fault;
        } else {
            long applicationId = pe.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            if (Utils.clean(pe.getName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create protection element, no name specified!!");
                throw fault;
            }
            try {
                gov.nih.nci.security.authorization.domainobjects.ProtectionElement search = new gov.nih.nci.security.authorization.domainobjects.ProtectionElement();
                search.setProtectionElementName(pe.getName());
                search.setObjectId(pe.getObjectId());
                List<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> result = am
                    .getObjects(new gov.nih.nci.security.dao.ProtectionElementSearchCriteria(search));

                if (result.size() > 0) {
                    CSMTransactionFault fault = new CSMTransactionFault();
                    fault
                        .setFaultString("Cannot create the protection element because the protection element specified already exists.");
                    throw fault;
                }

                am.createProtectionElement(CSMUtils.convert(pe));

                result = am.getObjects(new gov.nih.nci.security.dao.ProtectionElementSearchCriteria(search));
                return CSMUtils.convert(result.get(0));
            } catch (CSMTransactionFault e) {
                throw e;
            } catch (Exception e) {
                String error = "Error creating the protection element " + pe.getName() + ":\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public List<ProtectionElement> getProtectionElements(String callerIdentity,
        org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria criteria) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if (criteria.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("No application specified in the protection element search criteria when one is required!!!");
            throw fault;
        } else {
            long applicationId = criteria.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {

                List<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> result = am
                    .getObjects(CSMUtils.convert(criteria));
                List<ProtectionElement> pes = new ArrayList<ProtectionElement>();
                for (int i = 0; i < result.size(); i++) {
                    pes.add(CSMUtils.convert(result.get(i)));
                }
                return pes;
            } catch (Exception e) {
                String error = "Error searching for protection elements:\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public ProtectionElement modifyProtectionElement(String callerIdentity, ProtectionElement pe)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        // Check to make sure an protection element id is provided
        if (pe.getId() == null) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify protection element, no id provided!!");
            throw fault;
        }
        gov.nih.nci.security.authorization.domainobjects.ProtectionElement existing = null;

        try {
            existing = auth.getProtectionElementById(String.valueOf(pe.getId().longValue()));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify the protection element (" + pe.getId() + ") because it does no exist.");
            throw fault;
        }

        try {

            // Ensure the application id has not changed.
            if (pe.getApplicationId() <= 0) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify protection element, no application specified!!!");
                throw fault;
            } else if (existing.getApplication().getApplicationId().longValue() != pe.getApplicationId()) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault
                    .setFaultString("Cannot modify protection element, you cannot change the application it belongs to!!");
                throw fault;
            } else {
                long applicationId = pe.getApplicationId();
                AuthorizationManager am = factory.getAuthorizationManager(applicationId);
                checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
                existing.setAttribute(pe.getAttribute());
                existing.setObjectId(pe.getObjectId());
                existing.setProtectionElementDescription(pe.getDescription());
                existing.setProtectionElementType(pe.getType());
                existing.setProtectionElementName(pe.getName());
                existing.setValue(pe.getAttributeValue());
                am.modifyProtectionElement(existing);
                return CSMUtils.convert(auth.getProtectionElementById(String.valueOf(pe.getId().longValue())));
            }
        } catch (CSException e) {
            String error = "Error modifying the protection element " + pe.getId().longValue() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removeProtectionElement(String callerIdentity, long protectionElementId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {

        gov.nih.nci.security.authorization.domainobjects.ProtectionElement pe = null;

        try {
            pe = auth.getProtectionElementById(String.valueOf(protectionElementId));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot remove the protection element (" + protectionElementId
                + ") because it does no exist.");
            throw fault;
        }
        try {
            long applicationId = pe.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            am.removeProtectionElement(String.valueOf(protectionElementId));
        } catch (CSException e) {
            String error = "Error removing the protection element " + protectionElementId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public ProtectionGroup createProtectionGroup(String callerIdentity, ProtectionGroup pg) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if (pg.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to add the protection group to.!!!");
            throw fault;
        } else {
            long applicationId = pg.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            if (Utils.clean(pg.getName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create protection group, no name specified!!");
                throw fault;
            }
            try {

                gov.nih.nci.security.authorization.domainobjects.ProtectionGroup search = new gov.nih.nci.security.authorization.domainobjects.ProtectionGroup();
                search.setProtectionGroupName(pg.getName());
                List<gov.nih.nci.security.authorization.domainobjects.ProtectionGroup> result = am
                    .getObjects(new gov.nih.nci.security.dao.ProtectionGroupSearchCriteria(search));
                if (result.size() > 0) {
                    CSMTransactionFault fault = new CSMTransactionFault();
                    fault.setFaultString("Cannot create the protection group " + pg.getName()
                        + " because it already exists.");
                    throw fault;
                }
                am.createProtectionGroup(CSMUtils.convert(pg));
                result = am.getObjects(new gov.nih.nci.security.dao.ProtectionGroupSearchCriteria(search));
                return CSMUtils.convert(result.get(0));
            } catch (CSMTransactionFault e) {
                throw e;
            } catch (Exception e) {
                String error = "Error creating the protection group " + pg.getName() + ":\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public List<ProtectionGroup> getProtectionGroups(String callerIdentity,
        org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria criteria) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (criteria.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("No application specified in the protection group search criteria when one is required!!!");
            throw fault;
        } else {
            long applicationId = criteria.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {

                List<gov.nih.nci.security.authorization.domainobjects.ProtectionGroup> result = am.getObjects(CSMUtils
                    .convert(criteria));
                List<ProtectionGroup> peg = new ArrayList<ProtectionGroup>();
                for (int i = 0; i < result.size(); i++) {
                    peg.add(CSMUtils.convert(result.get(i)));
                }
                return peg;
            } catch (Exception e) {
                String error = "Error searching for protection groups:\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public ProtectionGroup modifyProtectionGroup(String callerIdentity, ProtectionGroup pg) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        // Check to make sure an protection element id is provided
        if (pg.getId() == null) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify protection group, no id provided!!");
            throw fault;
        }
        gov.nih.nci.security.authorization.domainobjects.ProtectionGroup existing = null;
        try {
            existing = auth.getProtectionGroupById(String.valueOf(pg.getId().longValue()));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify the protection group (" + pg.getId() + ") because it does no exist.");
            throw fault;
        }

        try {

            // Ensure the application id has not changed.
            if (pg.getApplicationId() <= 0) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify protection group, no application specified!!!");
                throw fault;
            } else if (existing.getApplication().getApplicationId().longValue() != pg.getApplicationId()) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault
                    .setFaultString("Cannot modify protection group, you cannot change the application it belongs to!!");
                throw fault;
            } else {
                long applicationId = pg.getApplicationId();
                AuthorizationManager am = factory.getAuthorizationManager(applicationId);
                checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
                existing.setProtectionGroupDescription(pg.getDescription());
                existing.setProtectionGroupName(pg.getName());
                byte largeCount = 0;
                if (pg.isLargeElementCount()) {
                    largeCount = 1;
                }
                existing.setLargeElementCountFlag(largeCount);

                am.modifyProtectionGroup(existing);
                return CSMUtils.convert(auth.getProtectionGroupById(String.valueOf(pg.getId().longValue())));
            }
        } catch (CSException e) {
            String error = "Error modifying the protection group " + pg.getId().longValue() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removeProtectionGroup(String callerIdentity, long protectionGroupId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = null;
        try {
            pg = auth.getProtectionGroupById(String.valueOf(protectionGroupId));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot remove the protection group (" + protectionGroupId
                + ") because it does no exist.");
            throw fault;
        }
        try {
            long applicationId = pg.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            am.removeProtectionGroup(String.valueOf(protectionGroupId));
        } catch (CSException e) {
            String error = "Error removing the protection group " + protectionGroupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void assignProtectionElements(String callerIdentity, long protectionGroupId, List<Long> protectionElements)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = auth.getProtectionGroupById(String
                .valueOf(protectionGroupId));
            long applicationId = pg.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            String[] protectionElementIds = new String[protectionElements.size()];
            for (int i = 0; i < protectionElements.size(); i++) {
                gov.nih.nci.security.authorization.domainobjects.ProtectionElement pe = auth
                    .getProtectionElementById(String.valueOf(protectionElements.get(i)));
                if (applicationId != pe.getApplication().getApplicationId().longValue()) {
                    AccessDeniedFault fault = new AccessDeniedFault();
                    fault
                        .setFaultString("Cannot assign a protection element that belongs to one application to a protection group that belongs to another!!!");
                    throw fault;
                }
                protectionElementIds[i] = String.valueOf(pe.getProtectionElementId());
            }
            am.addProtectionElements(String.valueOf(protectionGroupId), protectionElementIds);
        } catch (CSException e) {
            String error = "Error assigning protection elements to the group " + protectionGroupId + ":\n"
                + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void unassignProtectionElements(String callerIdentity, long protectionGroupId, List<Long> protectionElements)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = auth.getProtectionGroupById(String
                .valueOf(protectionGroupId));
            long applicationId = pg.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            String[] protectionElementIds = new String[protectionElements.size()];
            for (int i = 0; i < protectionElements.size(); i++) {
                gov.nih.nci.security.authorization.domainobjects.ProtectionElement pe = auth
                    .getProtectionElementById(String.valueOf(protectionElements.get(i)));
                if (applicationId != pe.getApplication().getApplicationId().longValue()) {
                    AccessDeniedFault fault = new AccessDeniedFault();
                    fault
                        .setFaultString("Cannot unassign a protection element that belongs to one application to a protection group that belongs to another!!!");
                    throw fault;
                }
                protectionElementIds[i] = String.valueOf(pe.getProtectionElementId());
            }
            am.removeProtectionElementsFromProtectionGroup(String.valueOf(protectionGroupId), protectionElementIds);
        } catch (CSException e) {
            String error = "Error unassigning protection elements to the group " + protectionGroupId + ":\n"
                + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public List<ProtectionElement> getProtectionElementsAssignedToProtectionGroup(String callerIdentity,
        long protectionGroupId) throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = auth.getProtectionGroupById(String
                .valueOf(protectionGroupId));
            long applicationId = pg.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            Set<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> set = am
                .getProtectionElements(String.valueOf(protectionGroupId));
            List<ProtectionElement> list = new ArrayList<ProtectionElement>();
            Iterator<gov.nih.nci.security.authorization.domainobjects.ProtectionElement> itr = set.iterator();
            while (itr.hasNext()) {
                list.add(CSMUtils.convert(itr.next()));
            }
            return list;
        } catch (CSException e) {
            String error = "Error getting protection elements assigned to the group " + protectionGroupId + ":\n"
                + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    private void checkApplictionAdmin(String callerIdentity, String applicationName) throws CSMInternalFault,
        AccessDeniedFault {
        try {
            if (!auth.checkPermission(callerIdentity, applicationName, Constants.ADMIN_PRIVILEGE)) {
                AccessDeniedFault fault = new AccessDeniedFault();
                fault.setFaultString("You are not an administrator for the application " + applicationName + "!!!");
                throw fault;
            }
        } catch (CSException e) {
            log.error(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault
                .setFaultString("An unexpected error occurred determining administrative access to the CSM Web Service.");
            throw fault;
        }

    }


    public void assignProtectionGroup(String callerIdentity, long parentProtectionGroupId, long childProtectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup parent = auth
                .getProtectionGroupById(String.valueOf(parentProtectionGroupId));
            long applicationId = parent.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup child = auth.getProtectionGroupById(String
                .valueOf(childProtectionGroupId));
            if (applicationId != child.getApplication().getApplicationId().longValue()) {
                AccessDeniedFault fault = new AccessDeniedFault();
                fault
                    .setFaultString("Cannot assign a protection group that belongs to one application to a protection group that belongs to another!!!");
                throw fault;
            }

            if (child.getParentProtectionGroup() != null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot assign protection group " + child.getProtectionGroupName() + " ("
                    + child.getProtectionGroupId() + ") to the protection group " + parent.getProtectionGroupName()
                    + " (" + parent.getProtectionGroupId() + ") because it is already a child of the protection group "
                    + child.getParentProtectionGroup().getProtectionGroupName() + " ("
                    + child.getParentProtectionGroup().getProtectionGroupId()
                    + "), protection groups can only be assigned as a child of one protection group. ");
                throw fault;
            }
            am.assignParentProtectionGroup(String.valueOf(parent.getProtectionGroupId()), String.valueOf(child
                .getProtectionGroupId()));
        } catch (CSException e) {
            String error = "Error assigning the protection group " + childProtectionGroupId
                + " as a child of the protection group " + parentProtectionGroupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void unassignProtectionGroup(String callerIdentity, long parentProtectionGroupId, long childProtectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup parent = auth
                .getProtectionGroupById(String.valueOf(parentProtectionGroupId));
            long applicationId = parent.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup child = auth.getProtectionGroupById(String
                .valueOf(childProtectionGroupId));
            if (applicationId != child.getApplication().getApplicationId().longValue()) {
                AccessDeniedFault fault = new AccessDeniedFault();
                fault
                    .setFaultString("Cannot unassign a protection group that belongs to one application to a protection group that belongs to another!!!");
                throw fault;
            }

            if ((child.getParentProtectionGroup() == null)
                || (child.getParentProtectionGroup().getProtectionGroupId().longValue() != parentProtectionGroupId)) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot unassign the protection group " + child.getProtectionGroupName() + " ("
                    + child.getProtectionGroupId() + ") from the protection group " + parent.getProtectionGroupName()
                    + " (" + parent.getProtectionGroupId() + ") because it is not assigned to that group. ");
                throw fault;
            }
            am.assignParentProtectionGroup(null, String.valueOf(child.getProtectionGroupId()));
        } catch (CSException e) {
            String error = "Error in unassigning the protection group " + childProtectionGroupId
                + " as a child from the protection group " + parentProtectionGroupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public List<ProtectionGroup> getChildProtectionGroups(String callerIdentity, long protectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = auth.getProtectionGroupById(String
                .valueOf(protectionGroupId));
            long applicationId = pg.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

            // TODO: We need to optimize this.
            List<gov.nih.nci.security.authorization.domainobjects.ProtectionGroup> result = am.getProtectionGroups();
            List<ProtectionGroup> list = new ArrayList<ProtectionGroup>();

            for (int i = 0; i < result.size(); i++) {
                gov.nih.nci.security.authorization.domainobjects.ProtectionGroup child = result.get(i);
                if ((child.getParentProtectionGroup() != null)
                    && (child.getParentProtectionGroup().getProtectionGroupId() == protectionGroupId)) {
                    list.add(CSMUtils.convert(child));
                }
            }
            return list;
        } catch (CSException e) {
            String error = "Error getting protection groups assigned to the protection group " + protectionGroupId
                + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public ProtectionGroup getParentProtectionGroup(String callerIdentity, long protectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = auth.getProtectionGroupById(String
                .valueOf(protectionGroupId));
            long applicationId = pg.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            gov.nih.nci.security.authorization.domainobjects.ProtectionGroup parent = pg.getParentProtectionGroup();
            if (parent != null) {
                return CSMUtils.convert(parent);
            } else {
                return null;
            }
        } catch (CSException e) {
            String error = "Error getting parent protection group for the protection group " + protectionGroupId
                + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public RemoteGroup linkRemoteGroup(String callerIdentity, LinkRemoteGroupRequest req) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {

        if (req.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to link the remote group to.!!!");
            throw fault;
        } else {
            long applicationId = req.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            if (Utils.clean(req.getGridGrouperURL()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot link remote group, no Grid Grouper URL specified!!!");
                throw fault;
            }
            if (Utils.clean(req.getRemoteGroupName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot link remote group, no remote group specified!!!");
                throw fault;
            }

            if (Utils.clean(req.getLocalGroupName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot link remote group, no local group name specified!!!");
                throw fault;
            }

            RemoteGroupDescriptor des = this.remote.linkRemoteGroup(am, req.getGridGrouperURL(), req
                .getRemoteGroupName(), req.getLocalGroupName());
            try {
                gov.nih.nci.security.authorization.domainobjects.Group grp = am.getGroupById(String.valueOf(des
                    .getGroupId()));
                return CSMUtils.convert(grp, des);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("An unexpected error occurred linking the remote group.");
                throw fault;
            }
        }
    }


    public void unlinkRemoteGroup(String callerIdentity, long applicationId, long groupId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if (applicationId <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to unlink the remote group from.!!!");
            throw fault;
        } else {
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            this.remote.unlinkRemoteGroup(am, groupId);
        }

    }


    public List<Group> getGroups(String callerIdentity, GroupSearchCriteria criteria) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if (criteria.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified in the pgroup search criteria when one is required!!!");
            throw fault;
        } else {
            long applicationId = criteria.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {

                List<gov.nih.nci.security.authorization.domainobjects.Group> result = am.getObjects(CSMUtils
                    .convert(criteria));
                List<Group> list = new ArrayList<Group>();
                for (int i = 0; i < result.size(); i++) {
                    gov.nih.nci.security.authorization.domainobjects.Group grp = result.get(i);
                    RemoteGroupDescriptor des = this.remote.getRemoteGroup(grp.getGroupId());
                    if (des != null) {
                        list.add(CSMUtils.convert(grp, des));
                    } else {
                        list.add(CSMUtils.convert(grp));
                    }
                }
                return list;
            } catch (Exception e) {
                String error = "Error searching for protection groups:\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public LocalGroup createGroup(String callerIdentity, LocalGroup grp) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (grp.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to add the group to.!!!");
            throw fault;
        } else {
            long applicationId = grp.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            if (Utils.clean(grp.getName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create group, no name specified!!");
                throw fault;
            }
            try {
                gov.nih.nci.security.authorization.domainobjects.Group search = new gov.nih.nci.security.authorization.domainobjects.Group();
                search.setGroupName(grp.getName());
                List<gov.nih.nci.security.authorization.domainobjects.Group> result = am
                    .getObjects(new gov.nih.nci.security.dao.GroupSearchCriteria(search));
                if (result.size() > 0) {
                    CSMTransactionFault fault = new CSMTransactionFault();
                    fault.setFaultString("Cannot create the group " + grp.getName() + " because it already exists.");
                    throw fault;
                }

                am.createGroup(CSMUtils.convert(grp));
                result = am.getObjects(new gov.nih.nci.security.dao.GroupSearchCriteria(search));
                return CSMUtils.convert(result.get(0));
            } catch (CSMTransactionFault e) {
                throw e;
            } catch (Exception e) {

                String error = "Error creating the group " + grp.getName() + ":\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public void removeGroup(String callerIdentity, long groupId) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {

        gov.nih.nci.security.authorization.domainobjects.Group grp = null;
        try {
            grp = auth.getGroupById(String.valueOf(groupId));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot remove the group (" + groupId + ") because it does no exist.");
            throw fault;
        }
        try {
            long applicationId = grp.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            if (this.remote.doesRemoteGroupExist(groupId)) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot remove the group " + grp.getName()
                    + " because it is a remote group, remote groups must be unlinked.");
                throw fault;
            } else {
                am.removeGroup(String.valueOf(groupId));
            }
        } catch (CSException e) {
            String error = "Error removing the group " + groupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public LocalGroup modifyGroup(String callerIdentity, LocalGroup grp) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        // Check to make sure an protection element id is provided
        if (grp.getId() == null) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify group, no id provided!!");
            throw fault;
        }
        gov.nih.nci.security.authorization.domainobjects.Group existing = null;

        try {
            existing = auth.getGroupById(String.valueOf(grp.getId().longValue()));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify the group (" + grp.getId() + ") because it does no exist.");
            throw fault;
        }

        try {

            // Ensure the application id has not changed.
            if (grp.getApplicationId() <= 0) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify group, no application specified!!!");
                throw fault;
            }

            long applicationId = grp.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

            if (existing.getApplication().getApplicationId().longValue() != grp.getApplicationId()) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify group, you cannot change the application it belongs to!!");
                throw fault;
            } else if (this.remote.doesRemoteGroupExist(grp.getId())) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify group, the group specified is a remote group");
                throw fault;
            } else {
                existing.setGroupDesc(grp.getDescription());
                existing.setGroupName(grp.getName());
                am.modifyGroup(existing);
                return CSMUtils.convert(auth.getGroupById(String.valueOf(grp.getId().longValue())));
            }
        } catch (CSException e) {
            String error = "Error modifying the group " + grp.getId().longValue() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void addUsersToGroup(String callerIdentity, long groupId, List<String> users) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        try {

            gov.nih.nci.security.authorization.domainobjects.Group existing = null;
            try {
                existing = auth.getGroupById(String.valueOf(groupId));
            } catch (CSObjectNotFoundException e) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot add users to the group (" + groupId + ") because it does no exist.");
                throw fault;
            }

            long applicationId = existing.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

            if (this.remote.doesRemoteGroupExist(groupId)) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot add users to a remote group.");
                throw fault;
            } else {
                String[] array = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {

                    User u = CSMInitializer.getUserCreateIfNeeded(auth, users.get(i));
                    array[i] = String.valueOf(u.getUserId());
                }
                am.addUsersToGroup(String.valueOf(groupId), array);
            }
        } catch (CSException e) {
            String error = "Error adding users to the group " + groupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public List<String> getUsersInGroup(String callerIdentity, long groupId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        try {

            gov.nih.nci.security.authorization.domainobjects.Group existing = null;
            try {
                existing = auth.getGroupById(String.valueOf(groupId));
            } catch (CSObjectNotFoundException e) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot get users in the group (" + groupId + ") because it does no exist.");
                throw fault;
            }
            long applicationId = existing.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            Set<User> set = am.getUsers(String.valueOf(groupId));
            List<String> users = new ArrayList<String>();
            Iterator<User> itr = set.iterator();
            while (itr.hasNext()) {
                users.add(itr.next().getLoginName());
            }
            return users;
        } catch (CSException e) {
            String error = "Error adding users to the group " + groupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removeUsersFromGroup(String callerIdentity, long groupId, List<String> users) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        try {

            gov.nih.nci.security.authorization.domainobjects.Group existing = null;
            try {
                existing = auth.getGroupById(String.valueOf(groupId));
            } catch (CSObjectNotFoundException e) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot remove users from the group (" + groupId + ") because it does no exist.");
                throw fault;
            }

            long applicationId = existing.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

            if (this.remote.doesRemoteGroupExist(groupId)) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot remove users from a remote group.");
                throw fault;
            } else {
                for (int i = 0; i < users.size(); i++) {
                    User u = CSMInitializer.getUserCreateIfNeeded(am, users.get(i));
                    am.removeUserFromGroup(String.valueOf(groupId), String.valueOf(u.getUserId()));
                }
            }
        } catch (CSException e) {
            String error = "Error adding users to the group " + groupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public Privilege createPrivilege(String callerIdentity, Privilege priv) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        checkWebServiceAdmin(callerIdentity);

        if (Utils.clean(priv.getName()) == null) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot create privilege, no name specified!!");
            throw fault;
        }

        gov.nih.nci.security.authorization.domainobjects.Privilege search = new gov.nih.nci.security.authorization.domainobjects.Privilege();
        search.setName(priv.getName());

        List<gov.nih.nci.security.authorization.domainobjects.Privilege> list = this.auth
            .getObjects(new gov.nih.nci.security.dao.PrivilegeSearchCriteria(search));

        if (list.size() > 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot create the privilege " + priv.getName()
                + ", a privilege with that name already exists.");
            throw fault;

        }

        gov.nih.nci.security.authorization.domainobjects.Privilege p = CSMUtils.convert(priv);
        try {
            this.auth.createPrivilege(p);

            list = this.auth.getObjects(new gov.nih.nci.security.dao.PrivilegeSearchCriteria(search));
            gov.nih.nci.security.authorization.domainobjects.Privilege created = list.get(0);

            return CSMUtils.convert(created);
        } catch (Exception e) {
            String error = "Error creating the privilege " + priv.getName() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public List<Privilege> getPrivileges(PrivilegeSearchCriteria criteria) throws CSMInternalFault, CSMTransactionFault {
        try {
            List<gov.nih.nci.security.authorization.domainobjects.Privilege> result = auth.getObjects(CSMUtils
                .convert(criteria));
            List<Privilege> privs = new ArrayList<Privilege>();
            for (int i = 0; i < result.size(); i++) {
                privs.add(CSMUtils.convert(result.get(i)));
            }
            return privs;
        } catch (Exception e) {
            String error = "Error searching for privileges:\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

    }


    public Privilege modifyPrivilege(String callerIdentity, Privilege priv) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        this.checkWebServiceAdmin(callerIdentity);
        try {
            // Check to make sure an protection element id is provided
            if ((priv.getId() == null) || (priv.getId() <= 0)) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify privilege, no id provided!!");
                throw fault;
            }
            gov.nih.nci.security.authorization.domainobjects.Privilege existing = null;

            try {
                existing = auth.getPrivilegeById(String.valueOf(priv.getId().longValue()));
            } catch (CSObjectNotFoundException e) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify the privilege (" + priv.getId() + ") because it does no exist.");
                throw fault;
            }

            if (existing.getName().equals(Constants.ADMIN_PRIVILEGE)) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify the " + Constants.ADMIN_PRIVILEGE + " privilege.");
                throw fault;
            }

            existing.setDesc(priv.getDescription());
            existing.setName(priv.getName());
            auth.modifyPrivilege(existing);
            return CSMUtils.convert(auth.getPrivilegeById(String.valueOf(priv.getId().longValue())));

        } catch (CSException e) {
            String error = "Error modifying the privilege " + priv.getId().longValue() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removePrivilege(String callerIdentity, long privilegeId) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        this.checkWebServiceAdmin(callerIdentity);
        try {
            gov.nih.nci.security.authorization.domainobjects.Privilege existing = null;

            try {
                existing = auth.getPrivilegeById(String.valueOf(privilegeId));
            } catch (CSObjectNotFoundException e) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot remove the privilege (" + privilegeId + ") because it does no exist.");
                throw fault;
            }

            if (existing.getName().equals(Constants.ADMIN_PRIVILEGE)) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot remove the " + Constants.ADMIN_PRIVILEGE + " privilege.");
                throw fault;
            }

            auth.removePrivilege(String.valueOf(privilegeId));
        } catch (CSException e) {
            String error = "Error removing the privilege " + privilegeId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public Role createRole(String callerIdentity, Role role) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (role.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to add the role to.!!!");
            throw fault;
        } else {
            long applicationId = role.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

            if (Utils.clean(role.getName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create role, no name specified!!");
                throw fault;
            }

            try {

                gov.nih.nci.security.authorization.domainobjects.Role search = new gov.nih.nci.security.authorization.domainobjects.Role();
                search.setName(role.getName());
                List<gov.nih.nci.security.authorization.domainobjects.Role> result = am
                    .getObjects(new gov.nih.nci.security.dao.RoleSearchCriteria(search));
                if (result.size() > 0) {
                    CSMTransactionFault fault = new CSMTransactionFault();
                    fault.setFaultString("Cannot create the role " + role.getName() + " because it already exists.");
                    throw fault;
                }
                am.createRole(CSMUtils.convert(role));
                result = am.getObjects(new gov.nih.nci.security.dao.RoleSearchCriteria(search));
                return CSMUtils.convert(result.get(0));
            } catch (CSMTransactionFault e) {
                throw e;
            } catch (Exception e) {
                String error = "Error creating the role " + role.getName() + ":\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public List<Role> getRoles(String callerIdentity, RoleSearchCriteria criteria) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if (criteria.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified in the role search criteria when one is required!!!");
            throw fault;
        } else {
            long applicationId = criteria.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {

                List<gov.nih.nci.security.authorization.domainobjects.Role> result = am.getObjects(CSMUtils
                    .convert(criteria));
                List<Role> list = new ArrayList<Role>();
                for (int i = 0; i < result.size(); i++) {
                    list.add(CSMUtils.convert(result.get(i)));
                }
                return list;
            } catch (Exception e) {
                String error = "Error searching for roles:\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public Role modifyRole(String callerIdentity, Role role) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {

        // Check to make sure an protection element id is provided
        if (role.getId() == null) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify the role, no id provided!!");
            throw fault;
        }
        gov.nih.nci.security.authorization.domainobjects.Role existing = null;
        try {
            existing = auth.getRoleById(String.valueOf(role.getId().longValue()));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify the role (" + role.getId() + ") because it does no exist.");
            throw fault;
        }
        try {
            // Ensure the application id has not changed.
            if (role.getApplicationId() <= 0) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify the role, no application specified!!!");
                throw fault;
            } else if (existing.getApplication().getApplicationId().longValue() != role.getApplicationId()) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot modify the role, you cannot change the application it belongs to!!");
                throw fault;
            } else {
                long applicationId = role.getApplicationId();
                AuthorizationManager am = factory.getAuthorizationManager(applicationId);
                checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
                existing.setDesc(role.getDescription());
                existing.setName(role.getName());
                am.modifyRole(existing);
                return CSMUtils.convert(auth.getRoleById(String.valueOf(role.getId().longValue())));
            }
        } catch (CSException e) {
            String error = "Error modifying the role " + role.getId().longValue() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removeRole(String callerIdentity, long roleId) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        try {
            gov.nih.nci.security.authorization.domainobjects.Role role = null;
            try {
                role = auth.getRoleById(String.valueOf(roleId));
            } catch (CSObjectNotFoundException e) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot remove the role (" + roleId + ") because it does no exist.");
                throw fault;
            }

            long applicationId = role.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            am.removeRole(String.valueOf(roleId));
        } catch (CSException e) {
            String error = "Error removing the role " + roleId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void setPrivilegesForRole(String callerIdentity, long roleId, List<Long> privs) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {

        gov.nih.nci.security.authorization.domainobjects.Role role = null;
        try {
            role = auth.getRoleById(String.valueOf(roleId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot assign the privileges to the role (" + roleId
                + ") because the role does not exist.");
            throw fault;
        }

        long applicationId = role.getApplication().getApplicationId().longValue();
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

        String[] list = new String[privs.size()];
        for (int i = 0; i < privs.size(); i++) {
            long privilegeId = privs.get(i);
            try {
                gov.nih.nci.security.authorization.domainobjects.Privilege priv = auth.getPrivilegeById(String
                    .valueOf(privilegeId));
                list[i] = String.valueOf(priv.getId());
            } catch (CSObjectNotFoundException e) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot assign the privilege (" + privilegeId + ") to the role (" + roleId
                    + ") because the privilege does not exist.");
                throw fault;
            }
        }
        try {
            am.assignPrivilegesToRole(String.valueOf(roleId), list);
        } catch (CSException e) {
            String error = "Error assigning privileges to the role " + roleId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public List<Privilege> getPrivilegesAssignedToRole(String callerIdentity, long roleId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        gov.nih.nci.security.authorization.domainobjects.Role role = null;
        try {
            role = auth.getRoleById(String.valueOf(roleId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot get the privileges assigned to the role (" + roleId
                + ") because the role does not exist.");
            throw fault;
        }

        long applicationId = role.getApplication().getApplicationId().longValue();
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
        try {
            Set<gov.nih.nci.security.authorization.domainobjects.Privilege> set = am.getPrivileges(String
                .valueOf(roleId));
            List<Privilege> privs = new ArrayList<Privilege>();
            Iterator<gov.nih.nci.security.authorization.domainobjects.Privilege> itr = set.iterator();
            while (itr.hasNext()) {
                privs.add(CSMUtils.convert(itr.next()));
            }
            return privs;
        } catch (CSException e) {
            String error = "Error getting the privileges assigned to the role " + roleId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void grantGroupPermission(String callerIdentity, long groupId, long roleId, long protectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        gov.nih.nci.security.authorization.domainobjects.Role role = null;
        try {
            role = auth.getRoleById(String.valueOf(roleId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot grant permission, the role specified does not exist.");
            throw fault;
        }

        gov.nih.nci.security.authorization.domainobjects.Group grp = null;
        try {
            grp = auth.getGroupById(String.valueOf(groupId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot grant permission, the group specified does not exist.");
            throw fault;
        }

        gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = null;
        try {
            pg = auth.getProtectionGroupById(String.valueOf(protectionGroupId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot grant permission, the protection group specified does not exist.");
            throw fault;
        }
        long applicationId = pg.getApplication().getApplicationId();
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
        long roleApplicationId = role.getApplication().getApplicationId();
        long grpApplicationId = grp.getApplication().getApplicationId();
        if ((applicationId != roleApplicationId) || (applicationId != grpApplicationId)) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("Cannot grant permission, the protection group, the role, and the group specified do not all belong to the same application.");
            throw fault;
        }

        String[] roles = new String[1];
        roles[0] = String.valueOf(roleId);
        try {
            am.addGroupRoleToProtectionGroup(String.valueOf(protectionGroupId), String.valueOf(groupId), roles);
        } catch (CSException e) {
            String error = "Error granting permission:\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

    }


    public void revokeGroupPermission(String callerIdentity, long groupId, long roleId, long protectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        gov.nih.nci.security.authorization.domainobjects.Role role = null;
        try {
            role = auth.getRoleById(String.valueOf(roleId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot revoke permission, the role specified does not exist.");
            throw fault;
        }

        gov.nih.nci.security.authorization.domainobjects.Group grp = null;
        try {
            grp = auth.getGroupById(String.valueOf(groupId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot revoke permission, the group specified does not exist.");
            throw fault;
        }

        gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = null;
        try {
            pg = auth.getProtectionGroupById(String.valueOf(protectionGroupId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot revoke permission, the protection group specified does not exist.");
            throw fault;
        }
        long applicationId = pg.getApplication().getApplicationId();
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

        long roleApplicationId = role.getApplication().getApplicationId();
        long grpApplicationId = grp.getApplication().getApplicationId();
        if ((applicationId != roleApplicationId) || (applicationId != grpApplicationId)) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("Cannot revoke permission, the protection group, the role, and the group specified do not all belong to the same application.");
            throw fault;
        }
        String[] roles = new String[1];
        roles[0] = String.valueOf(roleId);
        try {
            am.removeGroupRoleFromProtectionGroup(String.valueOf(protectionGroupId), String.valueOf(groupId), roles);
        } catch (CSException e) {
            String error = "Error revoking permission:\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

    }


    public void grantUserPermission(String callerIdentity, String userIdentity, long roleId, long protectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {

        gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = null;
        try {
            pg = auth.getProtectionGroupById(String.valueOf(protectionGroupId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot grant permission, the protection group specified does not exist.");
            throw fault;
        }
        long applicationId = pg.getApplication().getApplicationId();
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

        gov.nih.nci.security.authorization.domainobjects.Role role = null;
        try {
            role = auth.getRoleById(String.valueOf(roleId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot grant permission, the role specified does not exist.");
            throw fault;
        }

        long roleApplicationId = role.getApplication().getApplicationId();
        if (applicationId != roleApplicationId) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("Cannot grant permission, the protection group and the role specified do not belong to the same application.");
            throw fault;
        }

        User usr = CSMInitializer.getUserCreateIfNeeded(am, userIdentity);
        String[] roles = new String[1];
        roles[0] = String.valueOf(roleId);
        try {
            am.addUserRoleToProtectionGroup(String.valueOf(usr.getUserId()), roles, String.valueOf(protectionGroupId));
        } catch (CSException e) {
            String error = "Error granting permission:\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

    }


    public void revokeUserPermission(String callerIdentity, String userIdentity, long roleId, long protectionGroupId)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        gov.nih.nci.security.authorization.domainobjects.Role role = null;
        try {
            role = auth.getRoleById(String.valueOf(roleId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot revoke permission, the role specified does not exist.");
            throw fault;
        }

        gov.nih.nci.security.authorization.domainobjects.ProtectionGroup pg = null;
        try {
            pg = auth.getProtectionGroupById(String.valueOf(protectionGroupId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot revoke permission, the protection group specified does not exist.");
            throw fault;
        }
        long applicationId = pg.getApplication().getApplicationId();
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

        long roleApplicationId = role.getApplication().getApplicationId();
        if (applicationId != roleApplicationId) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("Cannot revoke permission, the protection group and the role specified do not belong to the same application.");
            throw fault;
        }
        User usr = CSMInitializer.getUserCreateIfNeeded(am, userIdentity);
        String[] roles = new String[1];
        roles[0] = String.valueOf(roleId);
        try {
            am.removeUserRoleFromProtectionGroup(String.valueOf(protectionGroupId), String.valueOf(usr.getUserId()),
                roles);
        } catch (CSException e) {
            String error = "Error revoking permission:\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

    }


    public List<Permission> getPermissions(String callerIdentity, long applicationId, String userIdentity)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
        User usr = CSMInitializer.getUserCreateIfNeeded(auth, userIdentity);
        try {
            Set<ProtectionGroupRoleContext> set = am.getProtectionGroupRoleContextForUser(String.valueOf(usr
                .getUserId()));
            List<Permission> list = new ArrayList<Permission>();
            Iterator<ProtectionGroupRoleContext> itr = set.iterator();
            while (itr.hasNext()) {
                ProtectionGroupRoleContext ctx = itr.next();
                Permission p = new Permission();
                p.setProtectionGroup(CSMUtils.convert(ctx.getProtectionGroup()));
                p.setUser(userIdentity);
                Set rolesSet = ctx.getRoles();
                Iterator<gov.nih.nci.security.authorization.domainobjects.Role> itr2 = rolesSet.iterator();
                Role[] roles = new Role[rolesSet.size()];
                int count = 0;
                while (itr2.hasNext()) {
                    roles[count] = CSMUtils.convert(itr2.next());
                    count = count + 1;
                }
                p.setRoles(roles);
                list.add(p);
            }
            return list;
        } catch (CSException e) {
            String error = "Error getting permissions for the user " + userIdentity + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

    }


    public List<Permission> getPermissions(String callerIdentity, long groupId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        gov.nih.nci.security.authorization.domainobjects.Group grp = null;
        try {
            grp = auth.getGroupById(String.valueOf(groupId));
        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot get permissions, the group specified does not exist.");
            throw fault;
        }

        AuthorizationManager am = factory.getAuthorizationManager(grp.getApplication().getApplicationId());
        checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());

        try {
            Set<ProtectionGroupRoleContext> set = am.getProtectionGroupRoleContextForGroup(String.valueOf(grp
                .getGroupId()));
            List<Permission> list = new ArrayList<Permission>();
            Iterator<ProtectionGroupRoleContext> itr = set.iterator();
            while (itr.hasNext()) {
                ProtectionGroupRoleContext ctx = itr.next();
                Permission p = new Permission();
                if (remote.doesRemoteGroupExist(grp.getGroupId())) {
                    RemoteGroupDescriptor des = remote.getRemoteGroup(grp.getGroupId());
                    p.setGroup(CSMUtils.convert(grp, des));
                } else {
                    p.setGroup(CSMUtils.convert(grp));
                }

                p.setProtectionGroup(CSMUtils.convert(ctx.getProtectionGroup()));

                Set rolesSet = ctx.getRoles();
                Iterator<gov.nih.nci.security.authorization.domainobjects.Role> itr2 = rolesSet.iterator();
                Role[] roles = new Role[rolesSet.size()];
                int count = 0;
                while (itr2.hasNext()) {
                    roles[count] = CSMUtils.convert(itr2.next());
                    count = count + 1;
                }
                p.setRoles(roles);
                list.add(p);
            }
            return list;
        } catch (CSException e) {
            String error = "Error getting permissions for the group " + grp.getName() + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

    }


    public FilterClause createFilterClause(String callerIdentity, FilterClause f) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if (f.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("No application specified to add the filter clause to.!!!");
            throw fault;
        } else {
            long applicationId = f.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            if (Utils.clean(f.getClassName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create filter clause, no classname specified!!");
                throw fault;
            }

            if (Utils.clean(f.getFilterChain()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create filter clause, no filter chain specified!!");
                throw fault;
            }
            
//            if (Utils.clean(f.getGeneratedSQLForGroup()) == null) {
//                CSMTransactionFault fault = new CSMTransactionFault();
//                fault.setFaultString("Cannot create filter clause, no sql for group specified!!");
//                throw fault;
//            }
//
//            if (Utils.clean(f.getGeneratedSQLForUser()) == null) {
//                CSMTransactionFault fault = new CSMTransactionFault();
//                fault.setFaultString("Cannot create filter clause, no sql for user specified!!");
//                throw fault;
//            }

            if (Utils.clean(f.getTargetClassAttributeName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create filter clause, no target class attribute name specified!!");
                throw fault;
            }

            if (Utils.clean(f.getTargetClassAttributeType()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create filter clause, no target class attribute type specified!!");
                throw fault;
            }

            if (Utils.clean(f.getTargetClassName()) == null) {
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString("Cannot create filter clause, no target classname specified!!");
                throw fault;
            }

            try {

                gov.nih.nci.security.authorization.domainobjects.FilterClause search = new gov.nih.nci.security.authorization.domainobjects.FilterClause();
                search.setClassName(f.getClassName());
                List<gov.nih.nci.security.authorization.domainobjects.FilterClause> result = am
                    .getObjects(new gov.nih.nci.security.dao.FilterClauseSearchCriteria(search));
//                if (result.size() > 0) {
//                    CSMTransactionFault fault = new CSMTransactionFault();
//                    fault.setFaultString("Cannot create the filter clause " + f.getClassName()
//                        + " because it already exists.");
//                    throw fault;
//                }
                am.createFilterClause(CSMUtils.convert(f));
                result = am.getObjects(new gov.nih.nci.security.dao.FilterClauseSearchCriteria(search));
                return CSMUtils.convert(result.get(0));
//            } catch (CSMTransactionFault e) {
//                throw e;
            } catch (Exception e) {
                String error = "Error creating the protection group " + f.getClassName() + ":\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public List<FilterClause> getFilterClauses(String callerIdentity,
        org.cagrid.gaards.csm.bean.FilterClauseSearchCriteria criteria) throws CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (criteria.getApplicationId() <= 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault
                .setFaultString("No application specified in the filter clause search criteria when one is required!!!");
            throw fault;
        } else {
            long applicationId = criteria.getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            try {

                List<gov.nih.nci.security.authorization.domainobjects.FilterClause> result = am.getObjects(CSMUtils
                    .convert(criteria));
                List<FilterClause> list = new ArrayList<FilterClause>();
                for (int i = 0; i < result.size(); i++) {
                    list.add(CSMUtils.convert(result.get(i)));
                }
                return list;
            } catch (Exception e) {
                String error = "Error searching for filter clauses:\n" + e.getMessage();
                log.error(error, e);
                CSMTransactionFault fault = new CSMTransactionFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMTransactionFault) helper.getFault();
                throw fault;
            }
        }
    }


    public FilterClause modifyFilterClause(String callerIdentity, long id, String sqlForGroup, String sqlForUser)
        throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {

    	
        gov.nih.nci.security.authorization.domainobjects.FilterClause existing = null;
        try {
            existing = auth.getFilterClauseById(String.valueOf(id));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot modify the filter clause (" + id + ") because it does no exist.");
            throw fault;
        }

        try {
            // Ensure the application id has not changed.
            long applicationId = existing.getApplication().getApplicationId();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            existing.setGeneratedSQLForGroup(sqlForGroup);
            existing.setGeneratedSQLForUser(sqlForUser);
            am.modifyFilterClause(existing);
            return CSMUtils.convert(auth.getFilterClauseById(String.valueOf(id)));

        } catch (CSException e) {
            String error = "Error modifying the filter clause " + id + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public void removeFilterClause(String callerIdentity, long filterClauseId) throws CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        gov.nih.nci.security.authorization.domainobjects.FilterClause f = null;
        try {
            f = auth.getFilterClauseById(String.valueOf(filterClauseId));

        } catch (CSObjectNotFoundException e) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot remove the filter clause (" + filterClauseId + ") because it does no exist.");
            throw fault;
        }
        try {
            long applicationId = f.getApplication().getApplicationId().longValue();
            AuthorizationManager am = factory.getAuthorizationManager(applicationId);
            checkApplictionAdmin(callerIdentity, am.getApplicationContext().getApplicationName());
            am.removeFilterClause(String.valueOf(filterClauseId));
        } catch (CSException e) {
            String error = "Error removing the filter clause " + filterClauseId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public boolean checkPermission(long applicationId, String userIdentity, String objectId, String privilege)
        throws CSMInternalFault, CSMTransactionFault {
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        try {
            return am.checkPermission(userIdentity, objectId, privilege);
        } catch (CSException e) {
            String error = "Error determining whether or not the user is permitted:\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    public boolean checkPermission(long applicationId, String userIdentity, String objectId, String attribute,
        String value, String privilege) throws CSMInternalFault, CSMTransactionFault {
        AuthorizationManager am = factory.getAuthorizationManager(applicationId);
        try {
            return am.checkPermission(userIdentity, objectId, attribute, value, privilege);
        } catch (CSException e) {
            String error = "Error determining whether or not the user is permitted:\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }
    }


    private void checkWebServiceAdmin(String callerIdentity) throws CSMInternalFault, AccessDeniedFault {
        try {
            if (!auth.checkPermission(callerIdentity, Constants.CSM_WEB_SERVICE_CONTEXT, Constants.ADMIN_PRIVILEGE)) {
                AccessDeniedFault fault = new AccessDeniedFault();
                fault.setFaultString("You are not a CSM Web Service administrator!!!");
                throw fault;
            }
        } catch (CSException e) {
            log.error(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault
                .setFaultString("An unexpected error occurred determining administrative access to the CSM Web Service.");
            throw fault;
        }

    }


    public AuthorizationManager getAuthorizationManager() {
        return auth;
    }
 
}
