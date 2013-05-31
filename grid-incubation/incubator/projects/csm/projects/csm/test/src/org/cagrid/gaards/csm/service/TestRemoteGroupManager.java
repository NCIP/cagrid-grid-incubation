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

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;
import org.cagrid.gaards.csm.common.RemoteGroupConstants;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class TestRemoteGroupManager extends TestCase {

    private AuthorizationManager am;


    public void testLinkRemoteGroupWhenLocalGroupExists() {
        RemoteGroupManager rm = null;
        CSMProperties conf = null;
        try {
            AuthorizationManager auth = createApplication("myapp");
            String groupName = "bar";
            Group grp = new Group();
            grp.setGroupName(groupName);
            auth.createGroup(grp);
            conf = CSMUtils.getCSMProperties();
            AuthorizationManagerFactory factory = new AuthorizationManagerFactory(conf);
            RemoteGroupTestSynchronizer sync = new RemoteGroupTestSynchronizer();
            rm = new RemoteGroupManager(factory, conf, sync);
            try {
                rm.linkRemoteGroup(auth, "fooURL", "foobar", grp.getGroupName());
                fail("Should not be able to link a group with a name of a local group that already exists.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("a group with the local group name provided"));
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (rm != null) {
                    rm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testUnlinkRemoteGroupThatDoesNotExists() {
        RemoteGroupManager rm = null;
        CSMProperties conf = null;
        try {
            AuthorizationManager auth = createApplication("myapp");
            conf = CSMUtils.getCSMProperties();
            AuthorizationManagerFactory factory = new AuthorizationManagerFactory(conf);
            RemoteGroupTestSynchronizer sync = new RemoteGroupTestSynchronizer();
            rm = new RemoteGroupManager(factory, conf, sync);
            assertFalse(rm.doesRemoteGroupExist(500));
            try {
                rm.unlinkRemoteGroup(auth, 500);
                fail("Should not be able to unlink a remote group that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("the group specified is not a remote group."));
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (rm != null) {
                    rm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testLinkRemoteGroup() {
        RemoteGroupManager rm = null;
        CSMProperties conf = null;
        try {
            int count = 5;
            AuthorizationManager auth = createApplication("myapp");
            conf = CSMUtils.getCSMProperties();
            AuthorizationManagerFactory factory = new AuthorizationManagerFactory(conf);
            RemoteGroupTestSynchronizer sync = new RemoteGroupTestSynchronizer();
            sync.setExpectedSyncCount(count);
            rm = new RemoteGroupManager(factory, conf, sync);

            List<RemoteGroupDescriptor> groups = new ArrayList<RemoteGroupDescriptor>();
            for (int i = 0; i < count; i++) {
                String localName = "bar" + i;
                RemoteGroupDescriptor rg1 = rm.linkRemoteGroup(auth, "fooURL", "foobar" + i, localName);
                assertTrue(rm.doesRemoteGroupExist(rg1.getGroupId()));
                RemoteGroupDescriptor rg2 = rm.getRemoteGroup(rg1.getGroupId());
                assertEquals(rg1, rg2);
                assertEquals((i + 1), rm.getRemoteGroups().size());
                Group g = auth.getGroupById(String.valueOf(rg2.getGroupId()));
                assertEquals(localName, g.getGroupName());
                groups.add(rg2);
            }
            // Wait until remote sync has been completed
            while (sync.getSyncsCompleted() != count) {
                Thread.currentThread().sleep(conf.getSecondsBetweenRemoteGroupSyncs() * 1000);
            }

            // Ensure that the sync was done successfully
            for (int i = 0; i < count; i++) {
                RemoteGroupDescriptor grp = groups.get(i);
                List<String> expected = sync.getUsersForGroup(grp.getGroupId());
                Set<User> users = auth.getUsers(String.valueOf(grp.getGroupId()));
                compareUsers(expected, users);
                List<RemoteGroupSynchronizationRecord> syncs = rm.getRemoteGroupsSynchronizationRecords(grp
                    .getGroupId());
                assertEquals(1, syncs.size());
                RemoteGroupSynchronizationRecord s = syncs.get(0);
                assertEquals(grp.getGroupId(), s.getGroupId());
                assertEquals(RemoteGroupConstants.SUCCESSFUL_SYNC_RESULT, s.getResult());
                assertEquals("", s.getMessage());
            }
            sync.setReadyToFail(true);

            // Wait until remote sync has been completed
            while (sync.getSyncsCompleted() != (count * 2)) {
                Thread.currentThread().sleep(conf.getSecondsBetweenRemoteGroupSyncs() * 1000);
            }

            // Check failed sync

            for (int i = 0; i < count; i++) {
                RemoteGroupDescriptor grp = groups.get(i);
                List<RemoteGroupSynchronizationRecord> syncs = rm.getRemoteGroupsSynchronizationRecords(grp
                    .getGroupId());
                assertEquals(2, syncs.size());
                RemoteGroupSynchronizationRecord s1 = syncs.get(0);
                RemoteGroupSynchronizationRecord s2 = syncs.get(1);
                RemoteGroupSynchronizationRecord pass = null;
                RemoteGroupSynchronizationRecord fail = null;
                if (s1.getSyncDate().before(s2.getSyncDate())) {
                    pass = s1;
                    fail = s2;
                } else {
                    pass = s2;
                    fail = s1;
                }
                assertEquals(grp.getGroupId(), pass.getGroupId());
                assertEquals(RemoteGroupConstants.SUCCESSFUL_SYNC_RESULT, pass.getResult());
                assertEquals("", pass.getMessage());
                assertEquals(grp.getGroupId(), fail.getGroupId());
                assertEquals(RemoteGroupConstants.FAILED_SYNC_RESULT, fail.getResult());
                assertEquals(RemoteGroupTestSynchronizer.FAILED_MESSAGE, fail.getMessage());
            }

            for (int i = 0; i < count; i++) {
                RemoteGroupDescriptor grp = groups.get(i);
                rm.unlinkRemoteGroup(am, grp.getGroupId());
                assertNull(rm.getRemoteGroup(grp.getGroupId()));
                assertFalse(rm.doesRemoteGroupExist(grp.getGroupId()));
                try {
                    auth.getGroupById(String.valueOf(grp.getGroupId()));
                    fail("Should not be able to get local group from CSM after remote group has been unlinked.");
                } catch (CSObjectNotFoundException e) {

                }
                assertEquals((count - (i + 1)), rm.getRemoteGroups().size());

                List<RemoteGroupSynchronizationRecord> syncs = rm.getRemoteGroupsSynchronizationRecords(grp
                    .getGroupId());
                assertEquals(0, syncs.size());
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (rm != null) {
                    rm.shutdown();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void compareUsers(List<String> expected, Set<User> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            boolean found = false;
            Iterator<User> itr = actual.iterator();
            while (itr.hasNext()) {
                User usr = itr.next();
                if (usr.getLoginName().equals(expected.get(i))) {
                    found = true;
                }
            }
            if (!found) {
                fail("The user " + expected.get(i) + " was not found but was expected.");
            }
        }
    }


    private AuthorizationManager createApplication(String name) throws Exception {
        Application a = new Application();
        a.setApplicationName(name);
        a.setApplicationDescription("Application description");
        am.createApplication(a);
        List<gov.nih.nci.security.authorization.domainobjects.Application> apps = am
            .getObjects(new gov.nih.nci.security.dao.ApplicationSearchCriteria(a));
        assertEquals(1, apps.size());
        gov.nih.nci.security.authorization.domainobjects.Application created = apps.get(0);
        assertEquals(name, created.getApplicationName());
        return CSMUtils.getAuthorizationManager(am, CSMUtils.getCSMProperties().getDatabaseProperties(), created
            .getApplicationId());
    }


    protected void setUp() throws Exception {
        super.setUp();
        CSMUtils.createCSMDatabase();
        am = CSMInitializer.getAuthorizationManager(CSMUtils.getCSMProperties().getDatabaseProperties());
    }

}
