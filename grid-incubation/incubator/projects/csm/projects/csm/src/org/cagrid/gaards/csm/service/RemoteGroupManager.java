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
import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.ThreadManager;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.dao.GroupSearchCriteria;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;
import org.hibernate.Criteria;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

public class RemoteGroupManager extends Runner {

    private SessionFactory sessionFactory;
    private Log log;
    private ThreadManager tm;
    private RemoteGroupSynchronizer synchronizer;
    private CSMProperties properties;
    private AuthorizationManagerFactory factory;
    
    // The thread that is currently running in execute.  
    // We assume that there will be no more than one.
    private Thread executeThread;

    // If true, a shutdown has been requested.
    // When sessionFactory.isClosed() returns true, the shutdown is complete.
    private boolean shutdown = false;

    // The number of active sessions being used from the sessionFactory.
    // This is used to coordinate an orderly shutdown.
    private int activeSessionCount = 0;

    public RemoteGroupManager(AuthorizationManagerFactory factory, CSMProperties properties, RemoteGroupSynchronizer synchronizer)
            throws CSMInternalFault {
        this.log = LogFactory.getLog(getClass().getName());
        this.shutdown = false;
        this.properties = properties;
        this.factory = factory;
        this.synchronizer = synchronizer;
        this.tm = new ThreadManager();
        this.setupSessionFactory(properties.getDatabaseProperties());
        try {
            this.tm.executeInBackground(this);
        } catch (Exception e) {
            String error = "An unexpected error occurred in initializing the Remote Group Manager:\n" + e.getMessage();
            log.error(error, e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMInternalFault) helper.getFault();
            throw fault;
        }
    }

    /**
     * If a shutdown has been requested and activeSessionCount is zero then
     * complete the shutdown by closing the session factory.
     */
    private void processShutdown() {
        if (shutdown && activeSessionCount==0) {
            synchronized(this) {
                sessionFactory.close();
                notify();
            }
        }
    }

    public boolean doesRemoteGroupExist(long groupId) throws CSMInternalFault {
        if (getRemoteGroup(groupId) != null) {
            return true;
        } else {
            return false;
        }
    }

    public RemoteGroupDescriptor getRemoteGroup(long groupId) throws CSMInternalFault {
        Session s = null;
        activeSessionCount += 1;
        try {
            s = sessionFactory.openSession();
            RemoteGroupDescriptor grp = (RemoteGroupDescriptor) s.load(RemoteGroupDescriptor.class, groupId);
            return grp;
        } catch (ObjectNotFoundException e) {
            return null;
        } catch (Exception e) {
            String error = "Error loading the remote group " + groupId + ":\n" + e.getMessage();
            log.error(error, e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMInternalFault) helper.getFault();
            throw fault;
        } finally {
            activeSessionCount -= 1;
            if (s != null) {
                try {
                    s.close();
                } catch (Exception ex2) {
                }
                processShutdown();
            }
        }

    }

    public synchronized void unlinkRemoteGroup(AuthorizationManager am, long groupId) throws CSMInternalFault, CSMTransactionFault {
        RemoteGroupDescriptor des = getRemoteGroup(groupId);
        if (des == null) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot unlink the remote group " + groupId + ", the group specified is not a remote group.");
            throw fault;
        } else {
            // Delete Remote Group
            try {
                am.removeGroup(String.valueOf(groupId));
            } catch (Exception e) {
                String error = "Error unlinking the group " + groupId
                        + ", the following error occurred when trying to remove the local group from CSM :\n" + e.getMessage();
                log.error(error, e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMInternalFault) helper.getFault();
                throw fault;
            }

            Session s = null;
            activeSessionCount++;
            try {
                s = sessionFactory.openSession();
                s.beginTransaction();
                s.delete(des);
                s.getTransaction().commit();
            } catch (Exception e) {
                String error = "Error unlinking the group " + groupId
                        + ", the following error occurred when trying to remove the remote group :\n" + e.getMessage();
                log.error(error, e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString(error);
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMInternalFault) helper.getFault();
                throw fault;
            } finally {
                activeSessionCount--;
                if (s != null) {
                    try {
                        s.close();
                    } catch (Exception ex2) {
                    }
                    processShutdown();
                }
            }

        }

    }

    public List<RemoteGroupSynchronizationRecord> getRemoteGroupsSynchronizationRecords(long groupId) throws CSMInternalFault {
        Session s = null;
        activeSessionCount++;
        try {
            s = sessionFactory.openSession();
            Criteria c = s.createCriteria(RemoteGroupSynchronizationRecord.class);
            c.add(Restrictions.eq("groupId", groupId));
            List<RemoteGroupSynchronizationRecord> groups = c.list();
            if (groups == null) {
                groups = new ArrayList<RemoteGroupSynchronizationRecord>();
            }
            return groups;
        } catch (Exception e) {

            String error = "Error getting the list of remote groups :\n" + e.getMessage();
            log.error(error, e);

            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMInternalFault) helper.getFault();
            throw fault;
        } finally {
            activeSessionCount--;
            if (s != null) {
                try {
                    s.close();
                } catch (Exception ex2) {
                }
                processShutdown();
            }
        }

    }

    public List<RemoteGroupDescriptor> getRemoteGroups() throws CSMInternalFault {
        Session s = null;
        activeSessionCount++;
        try {
            s = sessionFactory.openSession();
            Criteria c = s.createCriteria(RemoteGroupDescriptor.class);
            List<RemoteGroupDescriptor> groups = c.list();
            if (groups == null) {
                groups = new ArrayList<RemoteGroupDescriptor>();
            }
            return groups;
        } catch (Exception e) {

            String error = "Error getting the list of remote groups :\n" + e.getMessage();
            log.error(error, e);

            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMInternalFault) helper.getFault();
            throw fault;
        } finally {
            activeSessionCount--;
            if (s != null) {
                try {
                    s.close();
                } catch (Exception ex2) {
                }
                processShutdown();
            }
        }

    }

    public synchronized RemoteGroupDescriptor linkRemoteGroup(AuthorizationManager am, String gridGrouperURL, String gridGrouperGroupName,
            String localGroupName) throws CSMTransactionFault {
        // Check to see if a group exists
        Group grp = new Group();
        grp.setGroupName(localGroupName);
        List<Group> groups = am.getObjects(new GroupSearchCriteria(grp));
        if (groups.size() > 0) {
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString("Cannot link the remote group (" + gridGrouperURL + ", " + gridGrouperGroupName
                    + "), a group with the local group name provided (" + localGroupName + ") already exists.");
            throw fault;
        }

        try {
            // Create CSM Group
            am.createGroup(grp);
        } catch (Exception e) {
            String error = "Error creating the remote group (" + gridGrouperURL + ", " + gridGrouperGroupName + "):\n" + e.getMessage();
            log.error(error, e);
            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        }

        groups = am.getObjects(new GroupSearchCriteria(grp));

        Group localGroup = groups.get(0);

        // Create Remote Group
        RemoteGroupDescriptor remote = new RemoteGroupDescriptor();
        remote.setGroupId(localGroup.getGroupId());
        remote.setGridGrouperURL(gridGrouperURL);
        remote.setGridGrouperGroupName(gridGrouperGroupName);
        remote.setApplicationId(am.getApplicationContext().getApplicationId());
        Session s = null;
        activeSessionCount++;
        try {
            s = sessionFactory.openSession();
            s.beginTransaction();
            s.save(remote);
            s.getTransaction().commit();
            return remote;
        } catch (Exception e) {

            String error = "Error creating the remote group (" + gridGrouperURL + ", " + gridGrouperGroupName + "):\n" + e.getMessage();
            log.error(error, e);

            try {
                am.removeGroup(String.valueOf(localGroup.getGroupId()));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }

            CSMTransactionFault fault = new CSMTransactionFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMTransactionFault) helper.getFault();
            throw fault;
        } finally {
            activeSessionCount--;
            if (s != null) {
                try {
                    s.close();
                } catch (Exception ex2) {
                }
            }
            processShutdown();
        }
    }

    public synchronized void addRemoteGroupSynchronizationRecord(RemoteGroupSynchronizationRecord record) throws CSMInternalFault {
        Session s = null;
        activeSessionCount++;
        try {
            s = sessionFactory.openSession();
            s.beginTransaction();
            s.save(record);
            s.getTransaction().commit();
        } catch (Exception e) {

            String error = "Error adding a synchronization record for the remote group " + record.getGroupId() + ":\n" + e.getMessage();
            log.error(error, e);

            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString(error);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CSMInternalFault) helper.getFault();
            throw fault;
        } finally {
            activeSessionCount--;
            if (s != null) {
                try {
                    s.close();
                } catch (Exception ex2) {
                }
                processShutdown();
            }
        }
    }

    private void synchronize() {
        Session s = null;
        activeSessionCount++;
        try {
            s = sessionFactory.openSession();
            Criteria c = s.createCriteria(RemoteGroupDescriptor.class);
            List<RemoteGroupDescriptor> groups = c.list();
            if (groups == null) {
                groups = new ArrayList<RemoteGroupDescriptor>();
            }
            for (int i = 0; i < groups.size(); i++) {
                synchronized (this) {
                    RemoteGroupDescriptor grp = groups.get(i);
                    AuthorizationManager am = factory.getAuthorizationManager(grp.getApplicationId());
                    RemoteGroupSynchronizationRecord record = this.synchronizer.synchronizeRemoteGroup(am, grp);

                    if (record != null) {
                        s.beginTransaction();
                        s.save(record);
                        s.getTransaction().commit();
                    }
                }
            }
        } catch (Exception e) {
            String error = "An unexpected error occurred synchronizing remote groups:\n" + e.getMessage();
            log.error(error, e);
        } finally {
            activeSessionCount--;
            if (s != null) {
                try {
                    s.close();
                } catch (Exception ex2) {
                }
            }
            processShutdown();
        }
    }

    public void execute() {
        executeThread = Thread.currentThread();
        try {
            while (!(Thread.interrupted() || shutdown)) {
                synchronize();
                try {
                    Thread.sleep(this.properties.getSecondsBetweenRemoteGroupSyncs() * 1000);
                } catch (InterruptedException e) {
                    // do nothing
                } catch (Exception e) {
                    String error = "An unexpected error occurred synchronizing remote groups:\n" + e.getMessage();
                    log.error(error, e);
                }
            }
        } finally {
            synchronized (this) {
                executeThread = null;
            }
        }

    }

    public void shutdown() throws Exception {
        this.shutdown = true;
        synchronized (this) {
            if (executeThread != null) {
                executeThread.interrupt();
            }
        }
        while (activeSessionCount>0) {
            wait();
        }
        sessionFactory.close();
    }

    private void setupSessionFactory(DatabaseProperties db) throws CSMInternalFault {
        if (this.sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.addResource("org/cagrid/gaards/csm/service/hibernate/RemoteGroup.hbm.xml");
                configuration.addResource("org/cagrid/gaards/csm/service/hibernate/RemoteGroupSynchronizationRecord.hbm.xml");
                configuration.setProperty("hibernate.connection.url", db.getConnectionURL());
                configuration.setProperty("hibernate.connection.username", db.getUserId());
                configuration.setProperty("hibernate.connection.password", db.getPassword());
                configuration.setProperty("hibernate.dialect", db.getHibernateDialect());
                configuration.setProperty("hibernate.connection.driver_class", db.getDriver());
                configuration.setProperty("hibernate.c3p0.min_size", "5");
                configuration.setProperty("hibernate.c3p0.max_size", "20");
                configuration.setProperty("hibernate.c3p0.timeout", "300");
                configuration.setProperty("hibernate.c3p0.max_statements", "50");
                configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");
                configuration.setProperty("hibernate.hbm2ddl.auto", db.getRemoteGroupDatabaseCreationPolicy());
                this.sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                CSMInternalFault fault = new CSMInternalFault();
                fault.setFaultString("Error starting up the remote group manager.");
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                fault = (CSMInternalFault) helper.getFault();
                throw fault;
            }

        }
    }

}
