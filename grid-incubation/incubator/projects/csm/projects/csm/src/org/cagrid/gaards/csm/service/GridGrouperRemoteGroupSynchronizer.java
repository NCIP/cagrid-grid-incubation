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

import edu.internet2.middleware.subject.provider.SubjectTypeEnum;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.client.Member;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;
import org.cagrid.gaards.csm.common.RemoteGroupConstants;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;


public class GridGrouperRemoteGroupSynchronizer implements RemoteGroupSynchronizer {

    public RemoteGroupSynchronizationRecord synchronizeRemoteGroup(AuthorizationManager auth, RemoteGroupDescriptor grp) {
        RemoteGroupSynchronizationRecord record = new RemoteGroupSynchronizationRecord();
        record.setGroupId(grp.getGroupId());
        record.setSyncDate(new GregorianCalendar());
        GridGrouper grouper = new GridGrouper(grp.getGridGrouperURL());
        try {
            Group g = auth.getGroupById(String.valueOf(grp.getGroupId()));
            GroupI group = grouper.findGroup(grp.getGridGrouperGroupName());
            g.setGroupDesc(group.getDescription());
            auth.modifyGroup(g);

            // Merge Members
            Set<Member> members = group.getMembers();
            Set<User> users = auth.getUsers(String.valueOf(g.getGroupId()));
            // First remove members that dont exist
            Iterator<User> u = users.iterator();
            while (u.hasNext()) {
                User user = u.next();
                boolean found = false;
                Iterator<Member> itr = members.iterator();
                while (itr.hasNext()) {
                    Member m = itr.next();

                    if (m.getSubjectId().equals(user.getLoginName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    auth.removeUserFromGroup(String.valueOf(g.getGroupId()), String.valueOf(user.getUserId()));
                }
            }

            // Now find new users to add
            List<String> add = new ArrayList<String>();
            Iterator<Member> m = members.iterator();
            while (m.hasNext()) {
                Member member = m.next();
                if ((member.getSubjectType().equals(SubjectTypeEnum.PERSON))
                    || (member.getSubjectType().equals(SubjectTypeEnum.APPLICATION))) {
                    boolean found = false;
                    Iterator<User> itr = users.iterator();
                    while (itr.hasNext()) {
                        User user = itr.next();
                        if (user.getLoginName().equals(member.getSubjectId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        User usr = CSMInitializer.getUserCreateIfNeeded(auth, member.getSubjectId());
                        add.add(String.valueOf(usr.getUserId()));
                    }
                }
            }
            if (add.size() > 0) {
                String[] list = new String[add.size()];
                list = add.toArray(list);
                auth.addUsersToGroup(String.valueOf(g.getGroupId()), list);
            }

            record.setResult(RemoteGroupConstants.SUCCESSFUL_SYNC_RESULT);
            record.setMessage("");

        } catch (Exception e) {
            record.setResult(RemoteGroupConstants.FAILED_SYNC_RESULT);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            record.setMessage(sw.toString());
        }
        return record;
    }

}
