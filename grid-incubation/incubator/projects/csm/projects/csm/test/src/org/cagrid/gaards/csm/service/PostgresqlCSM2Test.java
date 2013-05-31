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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.util.optional.NoExitSecurityManager;
import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.bean.Group;
import org.cagrid.gaards.csm.bean.GroupSearchCriteria;
import org.cagrid.gaards.csm.bean.LocalGroup;
import org.cagrid.gaards.csm.bean.Permission;
import org.cagrid.gaards.csm.bean.Privilege;
import org.cagrid.gaards.csm.bean.PrivilegeSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionElement;
import org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionGroup;
import org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria;
import org.cagrid.gaards.csm.bean.Role;
import org.cagrid.gaards.csm.bean.RoleSearchCriteria;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;

/**
 * PostgreSQL version of integration test for CSM service.
 * 
 * @author Mark Grand
 */
public class PostgresqlCSM2Test extends TestCase {
    private static String SUPER_ADMIN = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=superadmin";
    private static String GENERAL_USER = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe";

    public void testModifyProtectionGroup() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String protectionGroupName = "my protection Group";
            ProtectionGroup g1 = getProtectionGroup(a1.getId(), protectionGroupName);
            ProtectionGroup pg1 = csm.createProtectionGroup(user1, g1);
            compareProtectionGroups(g1, pg1);

            ProtectionGroupSearchCriteria s1 = getProtectionGroupSearchCriteria(a1.getId(), protectionGroupName);
            List<ProtectionGroup> r1 = csm.getProtectionGroups(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pg1, r1.get(0));

            // Test modify a protection element without an id
            try {
                csm.modifyProtectionGroup(user1, g1);
                fail("Should not be able to modify a protection group without specifying an id");
            } catch (CSMTransactionFault e) {

            }

            try {
                ProtectionGroup dne = new ProtectionGroup();
                dne.setId(Long.MAX_VALUE);
                csm.modifyProtectionGroup(SUPER_ADMIN, dne);
                fail("Should not be able to modify a protection group that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
            }

            // Test modify a protection element with the wrong application id
            long id = pg1.getApplicationId();
            pg1.setApplicationId(5);

            try {
                csm.modifyProtectionGroup(user1, pg1);
                fail("Should not be able to modify the application a protection group is associated with.");
            } catch (CSMTransactionFault e) {

            }
            pg1.setApplicationId(id);

            try {
                csm.modifyProtectionGroup(GENERAL_USER, pg1);
                fail("Only admins should be able to modify protection groups");
            } catch (AccessDeniedFault e) {

            }

            pg1.setDescription("Updated Description");
            pg1.setName("Updated Name");
            pg1.setLargeElementCount(true);

            ProtectionGroup updated = csm.modifyProtectionGroup(user1, pg1);
            compareProtectionGroups(pg1, updated);

            csm.removeProtectionGroup(user1, pg1.getId().longValue());
            s1.setName(null);
            r1 = csm.getProtectionGroups(user1, s1);
            assertEquals(0, r1.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testCreateAndDeleteLocalGroup() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String appName2 = "myapp2";
            String user2 = GENERAL_USER + "2";
            Application a2 = createApplication(csm, appName2);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a2.getName(), user2);

            String groupName = "my group";
            LocalGroup g1 = getLocalGroup(a1.getId(), groupName);
            // Test that a non admin cannot create protection element
            try {
                csm.createGroup(user2, g1);
                fail("Only admins should be able to create groups.");
            } catch (AccessDeniedFault e) {

            }

            LocalGroup lg1 = csm.createGroup(user1, g1);
            compareLocalGroups(g1, lg1);

            // Test to see that a protection group that already exists cannot be
            // created.
            try {
                csm.createGroup(user1, g1);
                fail("Should not be able to create a group that already exists.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("already exists."));
            }

            LocalGroup g2 = getLocalGroup(a2.getId(), groupName);
            // Test that a non admin cannot create protection element
            try {
                csm.createGroup(user1, g2);
                fail("Only admins should be able to create groups");
            } catch (AccessDeniedFault e) {

            }

            LocalGroup lg2 = csm.createGroup(user2, g2);
            compareLocalGroups(g2, lg2);

            GroupSearchCriteria s1 = getGroupSearchCriteria(a1.getId(), groupName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getGroups(user2, s1);
                fail("Only admins should be able to search for groups");
            } catch (AccessDeniedFault e) {

            }

            List<Group> r1 = csm.getGroups(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(lg1, r1.get(0));

            GroupSearchCriteria s2 = getGroupSearchCriteria(a2.getId(), groupName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getGroups(user1, s2);
                fail("Only admins should be able to search for groups");
            } catch (AccessDeniedFault e) {

            }

            List<Group> r2 = csm.getGroups(user2, s2);
            assertEquals(1, r2.size());
            assertEquals(lg2, r2.get(0));

            try {
                csm.removeGroup(user2, lg1.getId().longValue());
                fail("Only admins should be able to remove groups");
            } catch (AccessDeniedFault e) {

            }

            csm.removeGroup(user1, lg1.getId().longValue());
            r1 = csm.getGroups(user1, s1);
            assertEquals(0, r1.size());

            try {
                csm.removeGroup(user1, lg2.getId().longValue());
                fail("Only admins should be able to remove groups");
            } catch (AccessDeniedFault e) {

            }

            csm.removeGroup(user2, lg2.getId().longValue());
            r2 = csm.getGroups(user2, s2);
            assertEquals(0, r2.size());

            // Test that a super admin, can create search for, and remove a
            // protection element.
            LocalGroup pa = getLocalGroup(a1.getId(), "admin group");
            LocalGroup pga = csm.createGroup(SUPER_ADMIN, pa);
            compareLocalGroups(pa, pga);
            GroupSearchCriteria sa = getGroupSearchCriteria(a1.getId(), pa.getName());
            List<Group> ra = csm.getGroups(SUPER_ADMIN, sa);
            assertEquals(1, ra.size());
            assertEquals(pga, ra.get(0));

            csm.removeGroup(SUPER_ADMIN, pga.getId().longValue());
            ra = csm.getGroups(SUPER_ADMIN, sa);
            assertEquals(0, ra.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testModifyLocalGroup() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String groupName = "my Group";
            LocalGroup g1 = getLocalGroup(a1.getId(), groupName);
            LocalGroup lg1 = csm.createGroup(user1, g1);
            compareLocalGroups(g1, lg1);

            GroupSearchCriteria s1 = getGroupSearchCriteria(a1.getId(), groupName);
            List<Group> r1 = csm.getGroups(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(lg1, r1.get(0));

            // Test modify a protection element without an id
            try {
                csm.modifyGroup(user1, g1);
                fail("Should not be able to modify a group without specifying an id");
            } catch (CSMTransactionFault e) {

            }

            // Test modify a protection element with the wrong application id
            //long id = lg1.getApplicationId();

            try {
                csm.modifyGroup(GENERAL_USER, lg1);
                fail("Only admins should be able to modify groups");
            } catch (AccessDeniedFault e) {

            }

            lg1.setDescription("Updated Description");
            lg1.setName("Updated Name");

            LocalGroup updated = csm.modifyGroup(user1, lg1);
            compareLocalGroups(lg1, updated);

            csm.removeGroup(user1, lg1.getId().longValue());
            r1 = csm.getGroups(user1, s1);
            assertEquals(0, r1.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testUserManagementOnGroups() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            String user2 = GENERAL_USER + "2";
            String user3 = GENERAL_USER + "3";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String groupName = "my Group";
            LocalGroup g1 = getLocalGroup(a1.getId(), groupName);
            LocalGroup lg1 = csm.createGroup(user1, g1);
            compareLocalGroups(g1, lg1);

            GroupSearchCriteria s1 = getGroupSearchCriteria(a1.getId(), groupName);
            List<Group> r1 = csm.getGroups(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(lg1, r1.get(0));

            List<String> users = new ArrayList<String>();
            users.add(user2);
            users.add(user3);

            // Test that non admins cannot add users to the group.
            try {
                csm.addUsersToGroup(GENERAL_USER, lg1.getId(), users);
                fail("Only admins should be able to add users.");
            } catch (AccessDeniedFault e) {

            }

            // Test add users to a group that does not exist
            try {
                csm.addUsersToGroup(user1, Integer.MAX_VALUE, users);
                fail("Should not be able to add users to a group that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
            }

            csm.addUsersToGroup(user1, lg1.getId(), users);

            // Test that non admins get the users in a group.
            try {
                csm.getUsersInGroup(GENERAL_USER, lg1.getId());
                fail("Only admins should be able to get the users in a group.");
            } catch (AccessDeniedFault e) {

            }

            // Test get users in a group that does not exist
            try {
                csm.getUsersInGroup(user1, Integer.MAX_VALUE);
                fail("Should not be able to get users in a group that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
            }

            compareUsers(users, csm.getUsersInGroup(user1, lg1.getId()));

            // Testing adding twice does not have ill effects.
            csm.addUsersToGroup(SUPER_ADMIN, lg1.getId(), users);
            compareUsers(users, csm.getUsersInGroup(user1, lg1.getId()));

            List<String> rm1 = new ArrayList<String>();
            rm1.add(user2);

            List<String> rm2 = new ArrayList<String>();
            rm2.add(user3);

            // Test that non admins cannot remove users from a group.
            try {
                csm.removeUsersFromGroup(GENERAL_USER, lg1.getId(), rm1);
                fail("Only admins should be able to remove users from a group.");
            } catch (AccessDeniedFault e) {

            }

            // Test remove users from a group that does not exist
            try {
                csm.removeUsersFromGroup(user1, Integer.MAX_VALUE, users);
                fail("Should not be able to remove users from a group that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
            }

            csm.removeUsersFromGroup(user1, lg1.getId(), rm1);

            users.remove(user2);

            compareUsers(users, csm.getUsersInGroup(user1, lg1.getId()));

            csm.removeUsersFromGroup(SUPER_ADMIN, lg1.getId(), rm2);

            users.remove(user3);

            compareUsers(users, csm.getUsersInGroup(SUPER_ADMIN, lg1.getId()));

            csm.removeGroup(user1, lg1.getId().longValue());
            r1 = csm.getGroups(user1, s1);
            assertEquals(0, r1.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void testCreateModifyAndDeletePrivilege() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            List<Privilege> privs = csm.getPrivileges(new PrivilegeSearchCriteria());
            assertEquals(8, privs.size());
            Set<String> privNames = new HashSet<String>();
            for (Privilege priv : privs) {
                privNames.add(priv.getName());
            }
            assertTrue(privNames.contains(Constants.ADMIN_PRIVILEGE));
            assertTrue(privNames.contains(Constants.CREATE_PRIVILEGE));
            assertTrue(privNames.contains(Constants.ACCESS_PRIVILEGE));
            assertTrue(privNames.contains(Constants.READ_PRIVILEGE));
            assertTrue(privNames.contains(Constants.WRITE_PRIVILEGE));
            assertTrue(privNames.contains(Constants.UPDATE_PRIVILEGE));
            assertTrue(privNames.contains(Constants.DELETE_PRIVILEGE));
            assertTrue(privNames.contains(Constants.EXECUTE_PRIVILEGE));

            String namePrefix = "privilege";
            int count = 3;

            for (int i = 0; i < count; i++) {
                String name = namePrefix + " " + i;
                Privilege p = new Privilege();
                p.setName(name);
                p.setDescription(name + " description.");

                try {
                    csm.createPrivilege(GENERAL_USER, p);
                    fail("Only admins should be able to create privileges.");
                } catch (AccessDeniedFault e) {

                }
                Privilege newPriv = csm.createPrivilege(SUPER_ADMIN, p);
                assertEquals(p.getName(), newPriv.getName());
                assertEquals(p.getDescription(), newPriv.getDescription());
                privs.add(newPriv);

                try {
                    csm.createPrivilege(SUPER_ADMIN, p);
                    fail("Should not be able to create a privilege that already exists.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("a privilege with that name already exists."));
                }

                PrivilegeSearchCriteria search = new PrivilegeSearchCriteria();
                search.setName(newPriv.getName());
                List<Privilege> result = csm.getPrivileges(search);
                assertEquals(1, result.size());
                assertEquals(newPriv, result.get(0));
                comparePrivileges(privs, csm.getPrivileges(new PrivilegeSearchCriteria()));
            }

            for (int i = 0; i < privs.size(); i++) {
                Privilege p = privs.get(i);

                try {
                    csm.modifyPrivilege(GENERAL_USER, p);
                    fail("Only admins should be able to modify privileges.");
                } catch (AccessDeniedFault e) {

                }

                long id = p.getId();
                p.setId(null);

                try {
                    csm.modifyPrivilege(SUPER_ADMIN, p);
                    fail("Should not be able to modify a privilege that does not have an id.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot modify privilege, no id provided!!"));
                }

                p.setId(new Long(0));

                try {
                    csm.modifyPrivilege(SUPER_ADMIN, p);
                    fail("Should not be able to modify a privilege that does not have an id.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot modify privilege, no id provided!!"));
                }

                p.setId(Long.MAX_VALUE);

                try {
                    csm.modifyPrivilege(SUPER_ADMIN, p);
                    fail("Should not be able to modify a privilege that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
                }

                p.setId(id);
                if (!p.getName().equals(Constants.ADMIN_PRIVILEGE)) {
                    p.setName("Updated " + p.getName());
                    p.setDescription("Updated " + p.getDescription());
                    Privilege updated = csm.modifyPrivilege(SUPER_ADMIN, p);
                    assertEquals(p.getName(), updated.getName());
                    assertEquals(p.getDescription(), updated.getDescription());
                    p = updated;
                    PrivilegeSearchCriteria search = new PrivilegeSearchCriteria();
                    search.setName(p.getName());
                    List<Privilege> result = csm.getPrivileges(search);
                    assertEquals(1, result.size());
                    assertEquals(p, result.get(0));
                    comparePrivileges(privs, csm.getPrivileges(new PrivilegeSearchCriteria()));
                } else {
                    try {
                        csm.modifyPrivilege(SUPER_ADMIN, p);
                        fail("Should not be able to modify the " + Constants.ADMIN_PRIVILEGE + " privilege.");
                    } catch (CSMTransactionFault e) {
                        assertTrue(-1 != e.getFaultString().indexOf("Cannot modify the " + Constants.ADMIN_PRIVILEGE + " privilege."));
                    }
                }

            }
            for (int i = 0; i < privs.size(); i++) {
                Privilege p = privs.get(i);
                if (!p.getName().equals(Constants.ADMIN_PRIVILEGE)) {
                    try {
                        csm.removePrivilege(GENERAL_USER, p.getId());
                        fail("Only admins should be able to remove privileges.");
                    } catch (AccessDeniedFault e) {

                    }

                    try {
                        csm.removePrivilege(SUPER_ADMIN, Long.MAX_VALUE);
                        fail("Should not be able to remove a privilege that does not exist.");
                    } catch (CSMTransactionFault e) {
                        assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
                    }

                    csm.removePrivilege(SUPER_ADMIN, p.getId());
                    PrivilegeSearchCriteria search = new PrivilegeSearchCriteria();
                    search.setName(p.getName());
                    List<Privilege> result = csm.getPrivileges(search);
                    assertEquals(0, result.size());
                } else {
                    try {
                        csm.removePrivilege(SUPER_ADMIN, p.getId());
                        fail("Should not be able to remove the " + Constants.ADMIN_PRIVILEGE + " privilege.");
                    } catch (CSMTransactionFault e) {
                        assertTrue(-1 != e.getFaultString().indexOf("Cannot remove the " + Constants.ADMIN_PRIVILEGE + " privilege."));
                    }
                }
            }
            List<Privilege> list = csm.getPrivileges(new PrivilegeSearchCriteria());
            assertEquals(1, list.size());
            assertEquals(Constants.ADMIN_PRIVILEGE, list.get(0).getName());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            String msg = gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e);
            try {
                DatabaseProperties dbp = CSMUtils.getCSMProperties().getDatabaseProperties();
                msg += "\n    Connection URL: " + dbp.getConnectionURL()
                + "\n    User IF: " + dbp.getUserId()
                + "\n    Password" + dbp.getPassword();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            fail(msg);
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testCreateAndDeleteRole() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String appName2 = "myapp2";
            String user2 = GENERAL_USER + "2";
            Application a2 = createApplication(csm, appName2);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a2.getName(), user2);

            String roleName = "my role";
            Role g1 = getRole(a1.getId(), roleName);
            // Test that a non admin cannot create protection element
            try {
                csm.createRole(user2, g1);
                fail("Only admins should be able to create roles.");
            } catch (AccessDeniedFault e) {

            }

            Role pg1 = csm.createRole(user1, g1);
            compareRoles(g1, pg1);

            // Test to see that a protection group that already exists cannot be
            // created.
            try {
                csm.createRole(user1, g1);
                fail("Should not be able to create a role that already exists.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("already exists."));
            }

            Role p2 = getRole(a2.getId(), roleName);
            // Test that a non admin cannot create protection element
            try {
                csm.createRole(user1, p2);
                fail("Only admins should be able to create roles");
            } catch (AccessDeniedFault e) {

            }

            Role pg2 = csm.createRole(user2, p2);
            compareRoles(p2, pg2);

            RoleSearchCriteria s1 = getRoleSearchCriteria(a1.getId(), roleName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getRoles(user2, s1);
                fail("Only admins should be able to search for roles");
            } catch (AccessDeniedFault e) {

            }

            List<Role> r1 = csm.getRoles(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pg1, r1.get(0));

            RoleSearchCriteria s2 = getRoleSearchCriteria(a2.getId(), roleName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getRoles(user1, s2);
                fail("Only admins should be able to search for roles");
            } catch (AccessDeniedFault e) {

            }

            List<Role> r2 = csm.getRoles(user2, s2);
            assertEquals(1, r2.size());
            assertEquals(pg2, r2.get(0));

            try {
                csm.removeRole(user2, pg1.getId().longValue());
                fail("Only admins should be able to remove roles");
            } catch (AccessDeniedFault e) {

            }

            csm.removeRole(user1, pg1.getId().longValue());
            r1 = csm.getRoles(user1, s1);
            assertEquals(0, r1.size());

            try {
                csm.removeRole(user1, pg2.getId().longValue());
                fail("Only admins should be able to remove roles");
            } catch (AccessDeniedFault e) {

            }

            csm.removeRole(user2, pg2.getId().longValue());
            r2 = csm.getRoles(user2, s2);
            assertEquals(0, r2.size());

            // Test that a super admin, can create search for, and remove a
            // protection element.
            Role pa = getRole(a1.getId(), "admin role");
            Role pga = csm.createRole(SUPER_ADMIN, pa);
            compareRoles(pa, pga);
            RoleSearchCriteria sa = getRoleSearchCriteria(a1.getId(), pa.getName());
            List<Role> ra = csm.getRoles(SUPER_ADMIN, sa);
            assertEquals(1, ra.size());
            assertEquals(pga, ra.get(0));

            csm.removeRole(SUPER_ADMIN, pga.getId().longValue());
            ra = csm.getRoles(SUPER_ADMIN, sa);
            assertEquals(0, ra.size());

            // Test Remove a protection group that does not exist
            try {
                csm.removeRole(SUPER_ADMIN, Long.MAX_VALUE);
                fail("Should not be able to remove a role that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testModifyRole() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String roleName = "my role";
            Role g1 = getRole(a1.getId(), roleName);
            Role pg1 = csm.createRole(user1, g1);
            compareRoles(g1, pg1);

            RoleSearchCriteria s1 = getRoleSearchCriteria(a1.getId(), roleName);
            List<Role> r1 = csm.getRoles(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pg1, r1.get(0));

            // Test modify a protection element without an id
            try {
                csm.modifyRole(user1, g1);
                fail("Should not be able to modify a role without specifying an id");
            } catch (CSMTransactionFault e) {

            }

            try {
                Role dne = new Role();
                dne.setId(Long.MAX_VALUE);
                csm.modifyRole(SUPER_ADMIN, dne);
                fail("Should not be able to modify a role that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
            }

            // Test modify a protection element with the wrong application id
            long id = pg1.getApplicationId();
            pg1.setApplicationId(5);

            try {
                csm.modifyRole(user1, pg1);
                fail("Should not be able to modify the application a role is associated with.");
            } catch (CSMTransactionFault e) {

            }
            pg1.setApplicationId(id);

            try {
                csm.modifyRole(GENERAL_USER, pg1);
                fail("Only admins should be able to modify roles");
            } catch (AccessDeniedFault e) {

            }

            pg1.setDescription("Updated Description");
            pg1.setName("Updated Name");

            Role updated = csm.modifyRole(user1, pg1);
            compareRoles(pg1, updated);

            csm.removeRole(user1, pg1.getId().longValue());
            s1.setName(null);
            r1 = csm.getRoles(user1, s1);
            assertEquals(0, r1.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testSetPrivilegesAssociatedWithRole() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String roleName = "my role";
            Role g1 = getRole(a1.getId(), roleName);
            Role pg1 = csm.createRole(user1, g1);
            compareRoles(g1, pg1);

            RoleSearchCriteria s1 = getRoleSearchCriteria(a1.getId(), roleName);
            List<Role> r1 = csm.getRoles(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pg1, r1.get(0));

            List<Privilege> privs = new ArrayList<Privilege>();

            for (int i = 0; i < 5; i++) {
                Privilege p = new Privilege();
                p.setName("Privilege " + i);
                p.setDescription("Privilege Description");
                Privilege priv = csm.createPrivilege(SUPER_ADMIN, p);
                privs.add(priv);
                PrivilegeSearchCriteria criteria = new PrivilegeSearchCriteria();
                assertEquals((i + 9), csm.getPrivileges(criteria).size());
                criteria.setName(priv.getName());
                List<Privilege> result = csm.getPrivileges(criteria);
                assertEquals(1, result.size());
                assertEquals(priv, result.get(0));
            }

            try {
                csm.setPrivilegesForRole(GENERAL_USER, pg1.getId(), new ArrayList<Long>());
                fail("Only admins should be able to assign privileges to roles");
            } catch (AccessDeniedFault e) {

            }

            try {
                csm.setPrivilegesForRole(user1, Integer.MAX_VALUE, new ArrayList<Long>());
                fail("Should not be able to assign a privilege to a role that does not exist");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because the role does not exist."));
            }

            try {
                List<Long> list = new ArrayList<Long>();
                list.add(Long.MAX_VALUE);
                csm.setPrivilegesForRole(user1, pg1.getId(), list);
                fail("Should not be able to assign a privilege that does not exist to  a role.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because the privilege does not exist."));
            }

            try {
                csm.getPrivilegesAssignedToRole(GENERAL_USER, pg1.getId());
                fail("Only admins should be able to get privileges assigned to roles");
            } catch (AccessDeniedFault e) {

            }

            try {
                csm.getPrivilegesAssignedToRole(user1, Integer.MAX_VALUE);
                fail("Should not be able to get privileges assigned to a role that does not exist");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because the role does not exist."));
            }

            // Test assign privileges

            for (int i = 0; i < privs.size(); i++) {
                Privilege priv = privs.get(i);
                List<Long> list = new ArrayList<Long>();
                list.add(priv.getId());
                csm.setPrivilegesForRole(user1, pg1.getId(), list);
                List<Privilege> result = csm.getPrivilegesAssignedToRole(user1, pg1.getId());
                assertEquals(1, result.size());
                assertEquals(priv, result.get(0));
            }

            csm.setPrivilegesForRole(user1, pg1.getId(), new ArrayList<Long>());
            List<Privilege> result = csm.getPrivilegesAssignedToRole(user1, pg1.getId());
            assertEquals(0, result.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testGrantRevokeUserPermission() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            findApplications(csm, null, 1);
            List<Application> apps = findApplications(csm, Constants.CSM_WEB_SERVICE_CONTEXT, 1);
            assertEquals(1, apps.size());
            assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0).getName());
            assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0).getDescription());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            Application a = new Application();
            a.setName("myapp");
            a.setDescription("This is my application!!!");

            Application application = csm.createApplication(SUPER_ADMIN, a);
            assertNotNull(application.getId());
            assertEquals(a.getName(), application.getName());
            assertEquals(a.getDescription(), application.getDescription());
            findApplications(csm, null, 2);
            List<Application> myapps = findApplications(csm, a.getName(), 1);
            assertEquals(application, myapps.get(0));

            String user1 = GENERAL_USER + "1";
            String user2 = GENERAL_USER + "2";
            String user3 = GENERAL_USER + "3";
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), application.getName(), user1);

            // Create Protection Element
            ProtectionElement pe = new ProtectionElement();
            pe.setApplicationId(application.getId());
            pe.setName("test protection element");
            pe.setObjectId("test protection element");
            pe.setDescription("My test protection element.");
            pe = csm.createProtectionElement(user1, pe);

            // Create Protection Group
            ProtectionGroup pg = new ProtectionGroup();
            pg.setApplicationId(application.getId());
            pg.setName("My protection group");
            pg.setDescription("My test protection group.");
            pg = csm.createProtectionGroup(user1, pg);
            List<Long> pes = new ArrayList<Long>();
            pes.add(pe.getId());
            csm.assignProtectionElements(user1, pg.getId(), pes);

            // Create Roles
            int roleCount = 3;
            List<Role> roles = new ArrayList<Role>();
            for (int i = 0; i < roleCount; i++) {
                Privilege p = new Privilege();
                p.setName("Privilege " + i);
                p.setDescription("Description of privilege " + i + ".");
                p = csm.createPrivilege(SUPER_ADMIN, p);
                Role r = new Role();
                r.setApplicationId(application.getId());
                r.setName(p.getName());
                r.setDescription("Description of role " + i + ".");
                r = csm.createRole(user1, r);
                roles.add(r);
                List<Long> privs = new ArrayList<Long>();
                privs.add(p.getId());
                csm.setPrivilegesForRole(user1, r.getId(), privs);
            }

            for (int i = 0; i < roleCount; i++) {
                Role r = roles.get(i);

                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user3, pe.getObjectId(), r.getName()));

                List<Role> expected = new ArrayList<Role>();
                for (int j = 0; j <= i; j++) {
                    expected.add(roles.get(j));
                }

                // Test invalid permission
                try {
                    csm.grantUserPermission(user2, user2, r.getId(), pg.getId());
                    fail("Only admins should be able to grant user permissions.");
                } catch (AccessDeniedFault e) {

                }

                // Test grant a permission where the protection group does not
                // exist
                try {
                    csm.grantUserPermission(user1, user2, r.getId(), Long.MAX_VALUE);
                    fail("Should not be able to grant a permission with a protection group that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot grant permission, the protection group specified does not exist."));
                }

                // Test grant a permission where the role does not exist
                try {
                    csm.grantUserPermission(user1, user2, Long.MAX_VALUE, pg.getId());
                    fail("Should not be able to grant a permission with a role that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot grant permission, the role specified does not exist."));
                }

                // Test invalid permission
                try {
                    csm.getPermissions(user2, application.getId(), user2);
                    fail("Only admins should be able to get permissions.");
                } catch (AccessDeniedFault e) {

                }

                csm.grantUserPermission(user1, user2, r.getId(), pg.getId());
                List<Permission> permissions = csm.getPermissions(user1, application.getId(), user2);
                assertEquals(1, permissions.size());
                checkPermission(permissions.get(0), user2, null, pg, expected);
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user3, pe.getObjectId(), r.getName()));

                csm.grantUserPermission(user1, user3, r.getId(), pg.getId());
                permissions = csm.getPermissions(user1, application.getId(), user3);
                assertEquals(1, permissions.size());
                checkPermission(permissions.get(0), user3, null, pg, expected);
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user3, pe.getObjectId(), r.getName()));

            }

            for (int i = 0; i < roleCount; i++) {
                Role r = roles.get(i);

                List<Role> expected = new ArrayList<Role>();
                for (int j = (i + 1); j < roleCount; j++) {
                    expected.add(roles.get(j));
                }

                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user3, pe.getObjectId(), r.getName()));

                // Test invalid permission
                try {
                    csm.revokeUserPermission(user2, user2, r.getId(), pg.getId());
                    fail("Only admins should be able to revoke user permissions.");
                } catch (AccessDeniedFault e) {

                }

                // Test revoke a permission where the protection group does not
                // exist
                try {
                    csm.revokeUserPermission(user1, user2, r.getId(), Long.MAX_VALUE);
                    fail("Should not be able to revoke a permission with a protection group that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot revoke permission, the protection group specified does not exist."));
                }

                // Test revoke a permission where the role does not exist
                try {
                    csm.revokeUserPermission(user1, user2, Long.MAX_VALUE, pg.getId());
                    fail("Should not be able to revoke a permission with a role that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot revoke permission, the role specified does not exist."));
                }

                csm.revokeUserPermission(user1, user2, r.getId(), pg.getId());
                List<Permission> permissions = csm.getPermissions(user1, application.getId(), user2);

                if ((i + 1) == roleCount) {
                    assertEquals(0, permissions.size());
                } else {
                    assertEquals(1, permissions.size());
                    checkPermission(permissions.get(0), user2, null, pg, expected);
                }
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user3, pe.getObjectId(), r.getName()));

                csm.revokeUserPermission(user1, user3, r.getId(), pg.getId());
                permissions = csm.getPermissions(user1, application.getId(), user3);
                if ((i + 1) == roleCount) {
                    assertEquals(0, permissions.size());
                } else {
                    assertEquals(1, permissions.size());
                    checkPermission(permissions.get(0), user3, null, pg, expected);
                }
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user3, pe.getObjectId(), r.getName()));

            }

            csm.removeApplication(SUPER_ADMIN, application.getId().longValue());
            findApplications(csm, null, 1);
            findApplications(csm, a.getName(), 0);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testGrantRevokeGroupPermission() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            findApplications(csm, null, 1);
            List<Application> apps = findApplications(csm, Constants.CSM_WEB_SERVICE_CONTEXT, 1);
            assertEquals(1, apps.size());
            assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0).getName());
            assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0).getDescription());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            Application a = new Application();
            a.setName("myapp");
            a.setDescription("This is my application!!!");

            Application application = csm.createApplication(SUPER_ADMIN, a);
            assertNotNull(application.getId());
            assertEquals(a.getName(), application.getName());
            assertEquals(a.getDescription(), application.getDescription());
            findApplications(csm, null, 2);
            List<Application> myapps = findApplications(csm, a.getName(), 1);
            assertEquals(application, myapps.get(0));

            String user1 = GENERAL_USER + "1";
            String user2 = GENERAL_USER + "2";

            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), application.getName(), user1);

            // Create Protection Element
            ProtectionElement pe = new ProtectionElement();
            pe.setApplicationId(application.getId());
            pe.setName("test protection element");
            pe.setObjectId("test protection element");
            pe.setDescription("My test protection element.");
            pe = csm.createProtectionElement(user1, pe);

            // Create Protection Group
            ProtectionGroup pg = new ProtectionGroup();
            pg.setApplicationId(application.getId());
            pg.setName("My protection group");
            pg.setDescription("My test protection group.");
            pg = csm.createProtectionGroup(user1, pg);
            List<Long> pes = new ArrayList<Long>();
            pes.add(pe.getId());
            csm.assignProtectionElements(user1, pg.getId(), pes);

            // Create Roles
            int roleCount = 3;
            List<Role> roles = new ArrayList<Role>();
            for (int i = 0; i < roleCount; i++) {
                Privilege p = new Privilege();
                p.setName("Privilege " + i);
                p.setDescription("Description of privilege " + i + ".");
                p = csm.createPrivilege(SUPER_ADMIN, p);
                Role r = new Role();
                r.setApplicationId(application.getId());
                r.setName(p.getName());
                r.setDescription("Description of role " + i + ".");
                r = csm.createRole(user1, r);
                roles.add(r);
                List<Long> privs = new ArrayList<Long>();
                privs.add(p.getId());
                csm.setPrivilegesForRole(user1, r.getId(), privs);
            }

            // Create Group
            LocalGroup grp = new LocalGroup();
            grp.setApplicationId(application.getId());
            grp.setName("My Group");
            grp.setDescription("Description of my group");
            grp = csm.createGroup(user1, grp);

            List<String> users = new ArrayList<String>();
            users.add(user2);

            for (int i = 0; i < roleCount; i++) {
                Role r = roles.get(i);

                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));

                List<Role> expected = new ArrayList<Role>();
                for (int j = 0; j <= i; j++) {
                    expected.add(roles.get(j));
                }

                // Test invalid permission
                try {
                    csm.grantGroupPermission(user2, grp.getId(), r.getId(), pg.getId());
                    fail("Only admins should be able to grant user permissions.");
                } catch (AccessDeniedFault e) {

                }

                // Test grant a permission where the protection group does not
                // exist
                try {
                    csm.grantGroupPermission(user1, grp.getId(), r.getId(), Long.MAX_VALUE);
                    fail("Should not be able to grant a permission with a protection group that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot grant permission, the protection group specified does not exist."));
                }

                // Test grant a permission where the role does not exist
                try {
                    csm.grantGroupPermission(user1, grp.getId(), Long.MAX_VALUE, pg.getId());
                    fail("Should not be able to grant a permission with a role that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot grant permission, the role specified does not exist."));
                }

                // Test grant a permission where the group does not exist
                try {
                    csm.grantGroupPermission(user1, Long.MAX_VALUE, r.getId(), pg.getId());
                    fail("Should not be able to grant a permission with group that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot grant permission, the group specified does not exist."));
                }

                // Test invalid permission
                try {
                    csm.getPermissions(user2, grp.getId());
                    fail("Only admins should be able to get permissions.");
                } catch (AccessDeniedFault e) {

                }

                // Test grant a permission where the group does not exist
                try {
                    csm.getPermissions(user1, Long.MAX_VALUE);
                    fail("Should not be able to get permissions with group that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot get permissions, the group specified does not exist."));
                }

                csm.grantGroupPermission(user1, grp.getId(), r.getId(), pg.getId());
                List<Permission> permissions = csm.getPermissions(user1, application.getId(), user2);
                assertEquals(0, permissions.size());
                permissions = csm.getPermissions(user1, grp.getId());
                assertEquals(1, permissions.size());
                checkPermission(permissions.get(0), null, grp, pg, expected);
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));

                csm.addUsersToGroup(user1, grp.getId(), users);

                permissions = csm.getPermissions(user1, grp.getId());
                assertEquals(1, permissions.size());
                checkPermission(permissions.get(0), null, grp, pg, expected);
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));

                csm.removeUsersFromGroup(user1, grp.getId(), users);

                permissions = csm.getPermissions(user1, grp.getId());
                assertEquals(1, permissions.size());
                checkPermission(permissions.get(0), null, grp, pg, expected);
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));

            }
            csm.addUsersToGroup(user1, grp.getId(), users);
            for (int i = 0; i < roleCount; i++) {
                Role r = roles.get(i);

                List<Role> expected = new ArrayList<Role>();
                for (int j = (i + 1); j < roleCount; j++) {
                    expected.add(roles.get(j));
                }

                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertTrue(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));

                // Test invalid permission
                try {
                    csm.revokeGroupPermission(user2, grp.getId(), r.getId(), pg.getId());
                    fail("Only admins should be able to revoke user permissions.");
                } catch (AccessDeniedFault e) {

                }

                // Test revoke a permission where the protection group does not
                // exist
                try {
                    csm.revokeGroupPermission(user1, grp.getId(), r.getId(), Long.MAX_VALUE);
                    fail("Should not be able to revoke a permission with a protection group that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot revoke permission, the protection group specified does not exist."));
                }

                // Test revoke a permission where the role does not exist
                try {
                    csm.revokeGroupPermission(user1, grp.getId(), Long.MAX_VALUE, pg.getId());
                    fail("Should not be able to revoke a permission with a role that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot revoke permission, the role specified does not exist."));
                }

                // Test revoke a permission where the group does not exist
                try {
                    csm.revokeGroupPermission(user1, Long.MAX_VALUE, r.getId(), pg.getId());
                    fail("Should not be able to revoke a permission with group that does not exist.");
                } catch (CSMTransactionFault e) {
                    assertTrue(-1 != e.getFaultString().indexOf("Cannot revoke permission, the group specified does not exist."));
                }

                csm.revokeGroupPermission(user1, grp.getId(), r.getId(), pg.getId());
                List<Permission> permissions = csm.getPermissions(user1, grp.getId());

                if ((i + 1) == roleCount) {
                    assertEquals(0, permissions.size());
                } else {
                    assertEquals(1, permissions.size());
                    checkPermission(permissions.get(0), null, grp, pg, expected);
                }
                assertFalse(csm.checkPermission(application.getId(), user1, pe.getObjectId(), r.getName()));
                assertFalse(csm.checkPermission(application.getId(), user2, pe.getObjectId(), r.getName()));

            }

            csm.removeApplication(SUPER_ADMIN, application.getId().longValue());
            findApplications(csm, null, 1);
            findApplications(csm, a.getName(), 0);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testCreateInstanceProtectionElements() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            ProtectionElement e1 = new ProtectionElement();
            e1.setApplicationId(a1.getId());
            e1.setName("A");
            e1.setDescription("This is A");
            e1.setObjectId("org.cagrid.gaards.csm.Example");
            e1.setAttribute("Id");
            e1.setAttributeValue("1");

            ProtectionElement e2 = new ProtectionElement();
            e2.setApplicationId(a1.getId());
            e2.setName("B");
            e2.setDescription("This is B");
            e2.setObjectId("org.cagrid.gaards.csm.Example");
            e2.setAttribute("Id");
            e2.setAttributeValue("2");

            ProtectionElement pe1 = csm.createProtectionElement(user1, e1);
            compareProtectionElements(e1, pe1);

            ProtectionElement pe2 = csm.createProtectionElement(user1, e2);
            compareProtectionElements(e2, pe2);

            ProtectionElementSearchCriteria s1 = getProtectionElementSearchCriteria(pe1);
            List<ProtectionElement> r1 = csm.getProtectionElements(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pe1, r1.get(0));

            ProtectionElementSearchCriteria s2 = getProtectionElementSearchCriteria(pe2);
            List<ProtectionElement> r2 = csm.getProtectionElements(user1, s2);
            assertEquals(1, r2.size());
            assertEquals(pe2, r2.get(0));

            csm.removeProtectionElement(user1, pe1.getId().longValue());
            r1 = csm.getProtectionElements(user1, s1);
            assertEquals(0, r1.size());

            csm.removeProtectionElement(user1, pe2.getId().longValue());
            r2 = csm.getProtectionElements(user1, s2);
            assertEquals(0, r2.size());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e));
        } finally {
            try {
                if (csm != null) {
                    csm.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermission(Permission p, String user, Group grp, ProtectionGroup pg, List<Role> roles) {
        assertEquals(user, p.getUser());
        assertEquals(grp, p.getGroup());
        assertEquals(pg, p.getProtectionGroup());
        if (p.getRoles() == null) {
            assertEquals(0, roles.size());
        } else {
            assertEquals(roles.size(), p.getRoles().length);

            for (int i = 0; i < roles.size(); i++) {
                boolean found = false;
                for (int j = 0; j < p.getRoles().length; j++) {
                    if (roles.get(i).equals(p.getRoles()[j])) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    fail("The role " + roles.get(i) + " was not found in the permission but was expected");
                }
            }
        }
    }

    private void comparePrivileges(List<Privilege> listA, List<Privilege> listB) {
        assertEquals(listA.size(), listB.size());
        for (int i = 0; i < listA.size(); i++) {
            boolean found = false;
            for (int j = 0; j < listB.size(); j++) {
                if (listA.get(i).equals(listB.get(j))) {
                    found = true;
                }
            }
            if (!found) {
                fail("The privilege " + listA.get(i).getName() + " (" + listA.get(i).getId() + ") was not found but was expected.");
            }
        }
    }

    private void compareProtectionElements(ProtectionElement e1, ProtectionElement e2) {
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getName()), gov.nih.nci.cagrid.common.Utils.clean(e2.getName()));
        assertEquals(e1.getApplicationId(), e2.getApplicationId());
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getAttribute()), gov.nih.nci.cagrid.common.Utils.clean(e2.getAttribute()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getAttributeValue()), gov.nih.nci.cagrid.common.Utils.clean(e2
                .getAttributeValue()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getDescription()), gov.nih.nci.cagrid.common.Utils.clean(e2.getDescription()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getObjectId()), gov.nih.nci.cagrid.common.Utils.clean(e2.getObjectId()));
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getType()), gov.nih.nci.cagrid.common.Utils.clean(e2.getType()));
    }

    private void compareProtectionGroups(ProtectionGroup e1, ProtectionGroup e2) {
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getName()), gov.nih.nci.cagrid.common.Utils.clean(e2.getName()));
        assertEquals(e1.getApplicationId(), e2.getApplicationId());
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getDescription()), gov.nih.nci.cagrid.common.Utils.clean(e2.getDescription()));
        assertEquals(e1.isLargeElementCount(), e2.isLargeElementCount());
    }

    private void compareRoles(Role e1, Role e2) {
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getName()), gov.nih.nci.cagrid.common.Utils.clean(e2.getName()));
        assertEquals(e1.getApplicationId(), e2.getApplicationId());
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(e1.getDescription()), gov.nih.nci.cagrid.common.Utils.clean(e2.getDescription()));
    }

    private void compareLocalGroups(LocalGroup g1, LocalGroup g2) {
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(g1.getName()), gov.nih.nci.cagrid.common.Utils.clean(g2.getName()));
        assertEquals(g1.getApplicationId(), g2.getApplicationId());
        assertEquals(gov.nih.nci.cagrid.common.Utils.clean(g1.getDescription()), gov.nih.nci.cagrid.common.Utils.clean(g2.getDescription()));
    }

    private ProtectionElementSearchCriteria getProtectionElementSearchCriteria(ProtectionElement e) {
        ProtectionElementSearchCriteria pe = new ProtectionElementSearchCriteria();
        pe.setApplicationId(e.getApplicationId());
        pe.setName(e.getName());
        pe.setObjectId(e.getObjectId());
        pe.setAttribute(e.getAttribute());
        pe.setAttributeValue(e.getAttributeValue());
        return pe;
    }

    private ProtectionGroupSearchCriteria getProtectionGroupSearchCriteria(long applicationId, String name) {
        ProtectionGroupSearchCriteria pg = new ProtectionGroupSearchCriteria();
        pg.setApplicationId(applicationId);
        pg.setName(name);
        return pg;
    }

    private RoleSearchCriteria getRoleSearchCriteria(long applicationId, String name) {
        RoleSearchCriteria pg = new RoleSearchCriteria();
        pg.setApplicationId(applicationId);
        pg.setName(name);
        return pg;
    }

    private GroupSearchCriteria getGroupSearchCriteria(long applicationId, String name) {
        GroupSearchCriteria pg = new GroupSearchCriteria();
        pg.setApplicationId(applicationId);
        pg.setName(name);
        return pg;
    }

    private ProtectionGroup getProtectionGroup(long applicationId, String name) {
        ProtectionGroup pg = new ProtectionGroup();
        pg.setApplicationId(applicationId);
        pg.setName(name);
        pg.setDescription("Protection Group " + name + ".");
        return pg;
    }

    private Role getRole(long applicationId, String name) {
        Role pg = new Role();
        pg.setApplicationId(applicationId);
        pg.setName(name);
        pg.setDescription("Role " + name + ".");
        return pg;
    }

    private void compareUsers(List<String> expected, List<String> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            boolean found = false;
            for (int j = 0; j < actual.size(); j++) {
                if (actual.get(j).equals(expected.get(i))) {
                    found = true;
                }
            }
            if (!found) {
                fail("The user " + expected.get(i) + " was not found but was expected.");
            }
        }
    }

    private LocalGroup getLocalGroup(long applicationId, String name) {
        LocalGroup localGroup = new LocalGroup();
        localGroup.setApplicationId(applicationId);
        localGroup.setName(name);
        localGroup.setDescription("Group " + name + ".");
        return localGroup;
    }

    private Application createApplication(CSM csm, String name) throws Exception {
        Application a = new Application();
        a.setName(name);
        a.setDescription("Application " + name + ".");
        Application result = csm.createApplication(SUPER_ADMIN, a);
        assertNotNull(result.getId());
        assertEquals(a.getName(), result.getName());
        assertEquals(a.getDescription(), result.getDescription());
        return result;
    }

    private List<Application> findApplications(CSM csm, String name, int expectedResult) throws Exception {
        ApplicationSearchCriteria search = new ApplicationSearchCriteria();
        // search.setId(id);
        search.setName(name);
        List<Application> apps = csm.getApplications(search);
        assertEquals(expectedResult, apps.size());
        return apps;
    }

    protected void setUp() throws Exception {
        super.setUp();
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new ExitControlSecurityManager());
        }
        String[] args = {"createCsmTables"};
        try {
            ((ExitControlSecurityManager)System.getSecurityManager()).exitPermitted = false;
            Main.main(args);
        } catch (ExitException e) {
            System.out.println("Table creation completed."); 
        } finally {
            ((ExitControlSecurityManager)System.getSecurityManager()).exitPermitted = true;
        }
    }
}
