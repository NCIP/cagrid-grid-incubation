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

import gov.nih.nci.cagrid.common.FaultUtil;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.util.optional.NoExitSecurityManager;
import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionElement;
import org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria;
import org.cagrid.gaards.csm.bean.ProtectionGroup;
import org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;

/**
 * PostgreSQL version of integration test for CSM service.
 * 
 * @author Mark Grand
 */
public class PostgresqlCSM1Test extends TestCase {
    private static String SUPER_ADMIN = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=superadmin";
    private static String GENERAL_USER = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe";

    public void testCSMInitialization() {
        System.out.println("Starting createCsmTables");
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            findApplications(csm, null, 1);
            List<Application> apps = findApplications(csm, Constants.CSM_WEB_SERVICE_CONTEXT, 1);
            assertEquals(1, apps.size());
            assertEquals(Constants.CSM_WEB_SERVICE_CONTEXT, apps.get(0).getName());
            assertEquals(Constants.CSM_WEB_SERVICE_DESCRIPTION, apps.get(0).getDescription());
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
        System.out.println("Ending createCsmTables");
    }

    public void testCreateAndDeleteApplication() {
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

            // Try removing the default app

            try {
                csm.removeApplication(SUPER_ADMIN, apps.get(0).getId());
                fail("Should not be able to remove the application " + Constants.CSM_WEB_SERVICE_CONTEXT + ".");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString()
                        .indexOf("The application, " + Constants.CSM_WEB_SERVICE_CONTEXT + " cannot be removed."));
            }

            Application a = new Application();
            a.setName("myapp");
            a.setDescription("This is my application!!!");

            // Test that a non admin cannot add an application
            try {
                csm.createApplication(GENERAL_USER, a);
                fail("Only CSM Web Service admins should be able to add applications");
            } catch (AccessDeniedFault e) {

            }

            Application result = csm.createApplication(SUPER_ADMIN, a);
            assertNotNull(result.getId());
            assertEquals(a.getName(), result.getName());
            assertEquals(a.getDescription(), result.getDescription());
            findApplications(csm, null, 2);
            List<Application> myapps = findApplications(csm, a.getName(), 1);
            assertEquals(result, myapps.get(0));

            try {
                csm.createApplication(SUPER_ADMIN, a);
                fail("Should not be able to create an application that already exists.");
            } catch (CSMTransactionFault e) {
            }

            // Test that a non admin cannot remove an application
            try {
                csm.removeApplication(GENERAL_USER, result.getId().longValue());
                fail("Only CSM Web Service admins should be able to remove applications");
            } catch (AccessDeniedFault e) {

            }

            csm.removeApplication(SUPER_ADMIN, result.getId().longValue());
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

    public void testModifyProtectionElement() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String protectionElementName = "my protection element";
            ProtectionElement e1 = getProtectionElement(a1.getId(), protectionElementName);
            ProtectionElement pe1 = csm.createProtectionElement(user1, e1);
            compareProtectionElements(e1, pe1);

            ProtectionElementSearchCriteria s1 = getProtectionElementSearchCriteria(a1.getId(), protectionElementName);
            List<ProtectionElement> r1 = csm.getProtectionElements(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pe1, r1.get(0));

            // Test modify a protection element without an id
            try {
                csm.modifyProtectionElement(user1, e1);
                fail("Should not be able to modify a protection element without specifying an id");
            } catch (CSMTransactionFault e) {

            }

            try {
                ProtectionElement dne = new ProtectionElement();
                dne.setId(Long.MAX_VALUE);
                csm.modifyProtectionElement(SUPER_ADMIN, dne);
                fail("Should not be able to modify a protection element that does not exist.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("because it does no exist."));
            }

            // Test modify a protection element with the wrong application id
            long id = pe1.getApplicationId();
            pe1.setApplicationId(5);

            try {
                csm.modifyProtectionElement(user1, pe1);
                fail("Should not be able to modify the application a protection element is associated with.");
            } catch (CSMTransactionFault e) {

            }
            pe1.setApplicationId(id);

            try {
                csm.modifyProtectionElement(GENERAL_USER, pe1);
                fail("Only admins should be able to modify protection elements");
            } catch (AccessDeniedFault e) {

            }

            pe1.setAttribute("Updated Attribute");
            pe1.setAttributeValue("Updated Attribute Value");
            pe1.setDescription("Updated Description");
            pe1.setName("Updated Name");
            pe1.setObjectId("Updated Object Id");
            pe1.setType("Updated Type");

            ProtectionElement updated = csm.modifyProtectionElement(user1, pe1);
            compareProtectionElements(pe1, updated);

            csm.removeProtectionElement(user1, pe1.getId().longValue());
            s1.setName(null);
            r1 = csm.getProtectionElements(user1, s1);
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

    public void testCreateAndDeleteProtectionElement() {
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

            String protectionElementName = "my protection element";
            ProtectionElement e1 = getProtectionElement(a1.getId(), protectionElementName);
            // Test that a non admin cannot create protection element
            try {
                csm.createProtectionElement(user2, e1);
                fail("Only admins should be able to create protection elements");
            } catch (AccessDeniedFault e) {

            }

            ProtectionElement pe1 = csm.createProtectionElement(user1, e1);
            compareProtectionElements(e1, pe1);

            // Test to see that a protection element that already exists cannot
            // be created.
            try {
                csm.createProtectionElement(user1, e1);
                fail("Should not be able to create a protection element that already exists.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("already exists."));
            }

            ProtectionElement e2 = getProtectionElement(a2.getId(), protectionElementName);
            // Test that a non admin cannot create protection element
            try {
                csm.createProtectionElement(user1, e2);
                fail("Only admins should be able to create protection elements");
            } catch (AccessDeniedFault e) {

            }

            ProtectionElement pe2 = csm.createProtectionElement(user2, e2);
            compareProtectionElements(e2, pe2);

            ProtectionElementSearchCriteria s1 = getProtectionElementSearchCriteria(a1.getId(), protectionElementName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getProtectionElements(user2, s1);
                fail("Only admins should be able to search for protection elements");
            } catch (AccessDeniedFault e) {

            }

            List<ProtectionElement> r1 = csm.getProtectionElements(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pe1, r1.get(0));

            ProtectionElementSearchCriteria s2 = getProtectionElementSearchCriteria(a2.getId(), protectionElementName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getProtectionElements(user1, s2);
                fail("Only admins should be able to search for protection elements");
            } catch (AccessDeniedFault e) {

            }

            List<ProtectionElement> r2 = csm.getProtectionElements(user2, s2);
            assertEquals(1, r2.size());
            assertEquals(pe2, r2.get(0));

            try {
                csm.removeProtectionElement(user2, pe1.getId().longValue());
                fail("Only admins should be able to remove protection elements");
            } catch (AccessDeniedFault e) {

            }

            csm.removeProtectionElement(user1, pe1.getId().longValue());
            r1 = csm.getProtectionElements(user1, s1);
            assertEquals(0, r1.size());

            try {
                csm.removeProtectionElement(user1, pe2.getId().longValue());
                fail("Only admins should be able to remove protection elements");
            } catch (AccessDeniedFault e) {

            }

            csm.removeProtectionElement(user2, pe2.getId().longValue());
            r2 = csm.getProtectionElements(user2, s2);
            assertEquals(0, r2.size());

            // Test that a super admin, can create search for, and remove a
            // protection element.
            ProtectionElement ea = getProtectionElement(a1.getId(), "admin protection element");
            ProtectionElement pea = csm.createProtectionElement(SUPER_ADMIN, ea);
            compareProtectionElements(ea, pea);
            ProtectionElementSearchCriteria sa = getProtectionElementSearchCriteria(a1.getId(), ea.getName());
            List<ProtectionElement> ra = csm.getProtectionElements(SUPER_ADMIN, sa);
            assertEquals(1, ra.size());
            assertEquals(pea, ra.get(0));

            csm.removeProtectionElement(SUPER_ADMIN, pea.getId().longValue());
            ra = csm.getProtectionElements(SUPER_ADMIN, sa);
            assertEquals(0, ra.size());

            // Test Remove a protection element that does not exist
            try {
                csm.removeProtectionElement(SUPER_ADMIN, Long.MAX_VALUE);
                fail("Should not be able to remove a protection element that does not exist.");
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

    public void testCreateAndDeleteProtectionGroup() {
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

            String protectionGroupName = "my protection group";
            ProtectionGroup g1 = getProtectionGroup(a1.getId(), protectionGroupName);
            // Test that a non admin cannot create protection element
            try {
                csm.createProtectionGroup(user2, g1);
                fail("Only admins should be able to create protection groups.");
            } catch (AccessDeniedFault e) {

            }

            ProtectionGroup pg1 = csm.createProtectionGroup(user1, g1);
            compareProtectionGroups(g1, pg1);

            // Test to see that a protection group that already exists cannot be
            // created.
            try {
                csm.createProtectionGroup(user1, g1);
                fail("Should not be able to create a protection group that already exists.");
            } catch (CSMTransactionFault e) {
                assertTrue(-1 != e.getFaultString().indexOf("already exists."));
            }

            ProtectionGroup p2 = getProtectionGroup(a2.getId(), protectionGroupName);
            // Test that a non admin cannot create protection element
            try {
                csm.createProtectionGroup(user1, p2);
                fail("Only admins should be able to create protection groups");
            } catch (AccessDeniedFault e) {

            }

            ProtectionGroup pg2 = csm.createProtectionGroup(user2, p2);
            compareProtectionGroups(p2, pg2);

            ProtectionGroupSearchCriteria s1 = getProtectionGroupSearchCriteria(a1.getId(), protectionGroupName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getProtectionGroups(user2, s1);
                fail("Only admins should be able to search for protection groups");
            } catch (AccessDeniedFault e) {

            }

            List<ProtectionGroup> r1 = csm.getProtectionGroups(user1, s1);
            assertEquals(1, r1.size());
            assertEquals(pg1, r1.get(0));

            ProtectionGroupSearchCriteria s2 = getProtectionGroupSearchCriteria(a2.getId(), protectionGroupName);
            // Test that a non admin cannot search for protection elements
            try {
                csm.getProtectionGroups(user1, s2);
                fail("Only admins should be able to search for protection groups");
            } catch (AccessDeniedFault e) {

            }

            List<ProtectionGroup> r2 = csm.getProtectionGroups(user2, s2);
            assertEquals(1, r2.size());
            assertEquals(pg2, r2.get(0));

            try {
                csm.removeProtectionGroup(user2, pg1.getId().longValue());
                fail("Only admins should be able to remove protection groups");
            } catch (AccessDeniedFault e) {

            }

            csm.removeProtectionGroup(user1, pg1.getId().longValue());
            r1 = csm.getProtectionGroups(user1, s1);
            assertEquals(0, r1.size());

            try {
                csm.removeProtectionGroup(user1, pg2.getId().longValue());
                fail("Only admins should be able to remove protection groups");
            } catch (AccessDeniedFault e) {

            }

            csm.removeProtectionGroup(user2, pg2.getId().longValue());
            r2 = csm.getProtectionGroups(user2, s2);
            assertEquals(0, r2.size());

            // Test that a super admin, can create search for, and remove a
            // protection element.
            ProtectionGroup pa = getProtectionGroup(a1.getId(), "admin protection group");
            ProtectionGroup pga = csm.createProtectionGroup(SUPER_ADMIN, pa);
            compareProtectionGroups(pa, pga);
            ProtectionGroupSearchCriteria sa = getProtectionGroupSearchCriteria(a1.getId(), pa.getName());
            List<ProtectionGroup> ra = csm.getProtectionGroups(SUPER_ADMIN, sa);
            assertEquals(1, ra.size());
            assertEquals(pga, ra.get(0));

            csm.removeProtectionGroup(SUPER_ADMIN, pga.getId().longValue());
            ra = csm.getProtectionGroups(SUPER_ADMIN, sa);
            assertEquals(0, ra.size());

            // Test Remove a protection group that does not exist
            try {
                csm.removeProtectionGroup(SUPER_ADMIN, Long.MAX_VALUE);
                fail("Should not be able to remove a protection group that does not exist.");
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

    public void testAssignProtectionGroupToProtectionGroup() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";

            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String parentGroupName = "parent group";
            ProtectionGroup p = getProtectionGroup(a1.getId(), parentGroupName);

            ProtectionGroup parent = csm.createProtectionGroup(user1, p);
            compareProtectionGroups(p, parent);

            ProtectionGroupSearchCriteria gs = getProtectionGroupSearchCriteria(a1.getId(), parentGroupName);
            List<ProtectionGroup> gr = csm.getProtectionGroups(user1, gs);
            assertEquals(1, gr.size());
            assertEquals(parent, gr.get(0));

            List<ProtectionGroup> children = new ArrayList<ProtectionGroup>();
            int childCount = 3;
            for (int i = 0; i < childCount; i++) {

                String childGroupName = "child group " + i;
                ProtectionGroup c = getProtectionGroup(a1.getId(), childGroupName);

                ProtectionGroup child = csm.createProtectionGroup(user1, c);
                compareProtectionGroups(c, child);

                ProtectionGroupSearchCriteria cgs = getProtectionGroupSearchCriteria(a1.getId(), childGroupName);
                List<ProtectionGroup> cgr = csm.getProtectionGroups(user1, cgs);
                assertEquals(1, cgr.size());
                assertEquals(child, cgr.get(0));
                // Test Invalid Permission
                try {
                    csm.assignProtectionGroup(GENERAL_USER, parent.getId(), child.getId());
                    fail("Only admins should be able to assign protection groups to protection groups.");
                } catch (AccessDeniedFault e) {

                }

                try {
                    csm.getParentProtectionGroup(GENERAL_USER, child.getId());
                    fail("Only admins should be able to get a protection group's parent.");
                } catch (AccessDeniedFault e) {

                }

                assertNull(csm.getParentProtectionGroup(user1, child.getId()));

                csm.assignProtectionGroup(user1, parent.getId(), child.getId());

                assertEquals(parent, csm.getParentProtectionGroup(user1, child.getId()));

                // Test Invalid Permission
                try {
                    csm.getChildProtectionGroups(GENERAL_USER, parent.getId());
                    fail("Only admins should be able to get the child protection groups for a protection group.");
                } catch (AccessDeniedFault e) {

                }

                List<ProtectionGroup> kids = csm.getChildProtectionGroups(user1, parent.getId());
                assertEquals((i + 1), kids.size());
                children.add(child);
                compareProtectionGroupList(children, kids);
            }

            for (int i = 0; i < children.size(); i++) {
                ProtectionGroup g = children.get(i);
                g.setDescription(g.getName() + " Updated");
                csm.modifyProtectionGroup(user1, g);
                ProtectionGroupSearchCriteria search = new ProtectionGroupSearchCriteria();
                search.setApplicationId(g.getApplicationId());
                search.setName(g.getName());
                List<ProtectionGroup> result = csm.getProtectionGroups(user1, search);
                assertEquals(g, result.get(0));
            }

            List<ProtectionGroup> list = csm.getChildProtectionGroups(user1, parent.getId());
            compareProtectionGroupList(children, list);

            for (int i = 0; i < childCount; i++) {
                ProtectionGroup g = children.remove(0);

                // Test Invalid Permission
                try {
                    csm.unassignProtectionGroup(GENERAL_USER, parent.getId(), g.getId());
                    fail("Only admins should be able to unassign protection groups from protection groups.");
                } catch (AccessDeniedFault e) {

                }

                csm.unassignProtectionGroup(user1, parent.getId(), g.getId());
                List<ProtectionGroup> kids = csm.getChildProtectionGroups(user1, parent.getId());
                compareProtectionGroupList(children, kids);
            }

            assertEquals(0, csm.getChildProtectionGroups(user1, parent.getId()).size());

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

    public void testAssignProtectionGroupToProtectionGroupOfAnotherApplication() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String appName2 = "myapp2";

            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            Application a2 = createApplication(csm, appName2);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a2.getName(), user1);

            String parentGroupName = "parent group";
            ProtectionGroup p = getProtectionGroup(a1.getId(), parentGroupName);

            ProtectionGroup parent = csm.createProtectionGroup(user1, p);
            compareProtectionGroups(p, parent);

            String childGroupName = "child group";
            ProtectionGroup c = getProtectionGroup(a2.getId(), childGroupName);
            ProtectionGroup child = csm.createProtectionGroup(user1, c);
            compareProtectionGroups(c, child);

            try {
                csm.assignProtectionGroup(user1, parent.getId(), child.getId());
                fail("Should not be able to assign a protection group from one application to a protection group of another application");
            } catch (AccessDeniedFault f) {

            }

            try {
                csm.unassignProtectionGroup(user1, parent.getId(), child.getId());
                fail("Should not be able to unassign a protection group from one application to a protection group of another application");
            } catch (AccessDeniedFault f) {

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

    public void testInvalidUnAssignOfProtectionGroups() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String appName2 = "myapp2";

            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            Application a2 = createApplication(csm, appName2);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a2.getName(), user1);

            String parentGroupName1 = "parent group 1";
            ProtectionGroup p1 = getProtectionGroup(a1.getId(), parentGroupName1);
            ProtectionGroup parent1 = csm.createProtectionGroup(user1, p1);
            compareProtectionGroups(p1, parent1);

            String parentGroupName2 = "parent group 2";
            ProtectionGroup p2 = getProtectionGroup(a2.getId(), parentGroupName2);
            ProtectionGroup parent2 = csm.createProtectionGroup(user1, p2);
            compareProtectionGroups(p2, parent2);

            String parentGroupName3 = "parent group 3";
            ProtectionGroup p3 = getProtectionGroup(a2.getId(), parentGroupName3);
            ProtectionGroup parent3 = csm.createProtectionGroup(user1, p3);
            compareProtectionGroups(p3, parent3);

            String childGroupName1 = "child group 1";
            ProtectionGroup c1 = getProtectionGroup(a1.getId(), childGroupName1);
            ProtectionGroup child1 = csm.createProtectionGroup(user1, c1);
            compareProtectionGroups(c1, child1);
            csm.assignProtectionGroup(user1, parent1.getId(), child1.getId());

            String childGroupName2 = "child group 2";
            ProtectionGroup c2 = getProtectionGroup(a2.getId(), childGroupName2);
            ProtectionGroup child2 = csm.createProtectionGroup(user1, c2);
            compareProtectionGroups(c2, child2);

            String childGroupName3 = "child group 3";
            ProtectionGroup c3 = getProtectionGroup(a2.getId(), childGroupName3);
            ProtectionGroup child3 = csm.createProtectionGroup(user1, c3);
            compareProtectionGroups(c3, child3);

            // Test unassigning a group that has not been assigned

            try {
                csm.unassignProtectionGroup(user1, parent2.getId(), child2.getId());
                fail("Should not be able to unassign a protection group that has not been assigned to a parent.");
            } catch (CSMTransactionFault e) {

            }
            csm.assignProtectionGroup(user1, parent2.getId(), child2.getId());
            csm.assignProtectionGroup(user1, parent3.getId(), child3.getId());

            // Test unassigning a group that belongs to a different parent
            try {
                csm.unassignProtectionGroup(user1, parent2.getId(), child3.getId());
                fail("Should not be able to unassign a protection group from the wrong parent.");
            } catch (CSMTransactionFault e) {

            }

            // Test unassigning across application spaces.
            try {
                csm.unassignProtectionGroup(user1, parent1.getId(), child2.getId());
                fail("Should not be able to unassign a protection group from one application to a protection group of another application");
            } catch (AccessDeniedFault f) {

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

    public void testAssignProtectionGroupToProtectionGroupThatAlreadyHasParent() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";

            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String parentGroupName = "parent group";
            ProtectionGroup p = getProtectionGroup(a1.getId(), parentGroupName);

            ProtectionGroup parent = csm.createProtectionGroup(user1, p);
            compareProtectionGroups(p, parent);

            String childGroupName = "child group";
            ProtectionGroup c = getProtectionGroup(a1.getId(), childGroupName);
            ProtectionGroup child = csm.createProtectionGroup(user1, c);
            compareProtectionGroups(c, child);
            csm.assignProtectionGroup(user1, parent.getId(), child.getId());
            try {
                csm.assignProtectionGroup(user1, parent.getId(), child.getId());
                fail("Should not be able to assign a protection group to another protectiong group when it already has a parent");
            } catch (CSMTransactionFault f) {

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

    public void testAssignProtectionElementsToProtectionGroup() {
        CSM csm = null;
        try {
            csm = new CSM(CSMUtils.getCSMProperties(),
                    new DoNothingRemoteGroupSynchronizer());
            CSMInitializer.addWebServiceAdmin(csm.getAuthorizationManager(), SUPER_ADMIN);
            String appName1 = "myapp1";
            String user1 = GENERAL_USER + "1";
            Application a1 = createApplication(csm, appName1);
            CSMInitializer.addApplicationAdmin(csm.getAuthorizationManager(), a1.getName(), user1);

            String protectionGroupName = "my protection group";
            ProtectionGroup g1 = getProtectionGroup(a1.getId(), protectionGroupName);

            ProtectionGroup pg1 = csm.createProtectionGroup(user1, g1);
            compareProtectionGroups(g1, pg1);

            ProtectionGroupSearchCriteria gs = getProtectionGroupSearchCriteria(a1.getId(), protectionGroupName);
            List<ProtectionGroup> gr = csm.getProtectionGroups(user1, gs);
            assertEquals(1, gr.size());
            assertEquals(pg1, gr.get(0));

            ProtectionElement e1 = getProtectionElement(a1.getId(), "pe1");
            ProtectionElement pe1 = csm.createProtectionElement(user1, e1);
            compareProtectionElements(e1, pe1);

            ProtectionElementSearchCriteria es1 = getProtectionElementSearchCriteria(a1.getId(), e1.getName());
            List<ProtectionElement> er = csm.getProtectionElements(user1, es1);
            assertEquals(1, er.size());
            assertEquals(pe1, er.get(0));

            ProtectionElement e2 = getProtectionElement(a1.getId(), "pe2");

            ProtectionElement pe2 = csm.createProtectionElement(user1, e2);
            compareProtectionElements(e2, pe2);

            ProtectionElementSearchCriteria es2 = getProtectionElementSearchCriteria(a1.getId(), e2.getName());
            er = csm.getProtectionElements(user1, es2);
            assertEquals(1, er.size());
            assertEquals(pe2, er.get(0));

            List<Long> protectionElements = new ArrayList<Long>();
            protectionElements.add(pe1.getId());
            protectionElements.add(pe2.getId());

            try {
                csm.assignProtectionElements(GENERAL_USER, pg1.getId(), protectionElements);
                fail("Only admins should be able to assign protection elements to protection groups.");
            } catch (AccessDeniedFault e) {

            }

            csm.assignProtectionElements(user1, pg1.getId(), protectionElements);

            pg1.setDescription("Modified Description...");

            csm.modifyProtectionGroup(user1, pg1);

            try {
                csm.getProtectionElementsAssignedToProtectionGroup(GENERAL_USER, pg1.getId());
                fail("Only admins should be able to get protection elements assigned protection groups.");
            } catch (AccessDeniedFault e) {

            }

            // Validate that the protection elements are assigned

            List<ProtectionElement> elements1 = csm.getProtectionElementsAssignedToProtectionGroup(user1, pg1.getId());
            List<ProtectionElement> elements2 = new ArrayList<ProtectionElement>();
            elements2.add(pe1);
            elements2.add(pe2);
            compareProtectionElementLists(elements1, elements2);

            protectionElements.remove(pe2.getId());
            elements2.remove(pe1);

            try {
                csm.unassignProtectionElements(GENERAL_USER, pg1.getId(), protectionElements);
                fail("Only admins should be able to unassign protection elements to protection groups.");
            } catch (AccessDeniedFault e) {

            }

            csm.unassignProtectionElements(user1, pg1.getId(), protectionElements);
            elements1 = csm.getProtectionElementsAssignedToProtectionGroup(user1, pg1.getId());
            compareProtectionElementLists(elements1, elements2);

            csm.removeProtectionElement(user1, pe2.getId());
            elements2.remove(pe2);

            elements1 = csm.getProtectionElementsAssignedToProtectionGroup(user1, pg1.getId());
            compareProtectionElementLists(elements1, elements2);

            csm.removeProtectionGroup(user1, pg1.getId().longValue());
            gr = csm.getProtectionGroups(user1, gs);
            assertEquals(0, gr.size());

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

    public void compareProtectionElementLists(List<ProtectionElement> l1, List<ProtectionElement> l2) {
        assertEquals(l1.size(), l2.size());
        for (int i = 0; i < l1.size(); i++) {
            boolean found = false;
            for (int j = 0; j < l2.size(); j++) {
                if (l1.get(i).equals(l2.get(j))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail(l1.get(i).getName() + " was not found but was expected.");
            }
        }
    }

    private void compareProtectionGroupList(List<ProtectionGroup> listA, List<ProtectionGroup> listB) {
        assertEquals(listA.size(), listB.size());
        for (int i = 0; i < listA.size(); i++) {
            boolean found = false;
            for (int j = 0; j < listB.size(); j++) {
                if (listA.get(i).equals(listB.get(j))) {
                    found = true;
                }
            }
            if (!found) {
                fail("The protection group " + listA.get(i).getName() + " (" + listA.get(i).getId() + ") was not found but was expected.");
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

    private ProtectionElementSearchCriteria getProtectionElementSearchCriteria(long applicationId, String name) {
        ProtectionElementSearchCriteria pe = new ProtectionElementSearchCriteria();
        pe.setApplicationId(applicationId);
        pe.setName(name);
        return pe;
    }

    private ProtectionElement getProtectionElement(long applicationId, String name) {
        ProtectionElement pe = new ProtectionElement();
        pe.setApplicationId(applicationId);
        pe.setName(name);
        pe.setObjectId(name);
        pe.setDescription("Protection Element " + name + ".");
        return pe;
    }

    private ProtectionGroupSearchCriteria getProtectionGroupSearchCriteria(long applicationId, String name) {
        ProtectionGroupSearchCriteria pg = new ProtectionGroupSearchCriteria();
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
