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

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;
import org.cagrid.gaards.csm.common.RemoteGroupConstants;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;


public class RemoteGroupTestSynchronizer implements RemoteGroupSynchronizer {

    private final static String USER_PREFIX = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe";
    public static final String FAILED_MESSAGE = "Could not sync remote group because it was already synced.";
    private int syncsCompleted = 0;
    public int expectedSyncCount = 0;
    private boolean readyToFail;


    public void setExpectedSyncCount(int expectedSyncCount) {
        this.expectedSyncCount = expectedSyncCount;
    }


    public synchronized RemoteGroupSynchronizationRecord synchronizeRemoteGroup(AuthorizationManager auth,
        RemoteGroupDescriptor grp) {

        if (syncsCompleted < expectedSyncCount) {
            try {
                List<String> users = getUsersForGroup(grp.getGroupId());

                String[] usrs = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    String usr = users.get(i);
                    User u = CSMInitializer.getUserCreateIfNeeded(auth, usr);
                    usrs[i] = String.valueOf(u.getUserId());
                }
                auth.addUsersToGroup(String.valueOf(grp.getGroupId()), usrs);
                RemoteGroupSynchronizationRecord record = new RemoteGroupSynchronizationRecord();
                record.setGroupId(grp.getGroupId());
                record.setSyncDate(new GregorianCalendar());
                record.setResult(RemoteGroupConstants.SUCCESSFUL_SYNC_RESULT);
                record.setMessage("");
                incrementSyncsCompleted();
                return record;

            } catch (Exception e) {
                RemoteGroupSynchronizationRecord record = new RemoteGroupSynchronizationRecord();
                record.setGroupId(grp.getGroupId());
                record.setSyncDate(new GregorianCalendar());
                record.setResult(RemoteGroupConstants.FAILED_SYNC_RESULT);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                record.setMessage(sw.toString());
                incrementSyncsCompleted();
                return record;
            }

        } else if ((syncsCompleted < (expectedSyncCount * 2)) && (isReadyToFail())) {
            RemoteGroupSynchronizationRecord record = new RemoteGroupSynchronizationRecord();
            record.setGroupId(grp.getGroupId());
            record.setSyncDate(new GregorianCalendar());
            record.setResult(RemoteGroupConstants.FAILED_SYNC_RESULT);
            record.setMessage(FAILED_MESSAGE);
            incrementSyncsCompleted();
            return record;
        } else {
            return null;
        }
    }


    public synchronized void incrementSyncsCompleted() {
        syncsCompleted = syncsCompleted + 1;
    }


    public synchronized int getSyncsCompleted() {
        return syncsCompleted;
    }


    public List<String> getUsersForGroup(long groupId) {
        List<String> users = new ArrayList<String>();
        for (int i = 0; i < groupId; i++) {
            users.add(USER_PREFIX + i);
        }
        return users;
    }


    public boolean isReadyToFail() {
        return readyToFail;
    }


    public void setReadyToFail(boolean readyToFail) {
        this.readyToFail = readyToFail;
    }

}
