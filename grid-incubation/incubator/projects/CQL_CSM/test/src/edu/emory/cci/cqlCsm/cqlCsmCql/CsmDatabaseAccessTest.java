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
/**
 */
package edu.emory.cci.cqlCsm.cqlCsmCql;

//import static org.junit.Assert.*;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.Statement;
//import java.util.List;
//import java.util.Set;
//
//import org.junit.Before;
//import org.junit.BeforeClass;
import org.junit.Test;

//import edu.emory.cci.cqlCsm.CsmDatabaseAccess;
//import edu.emory.cci.cqlCsm.CsmProperties;
//import edu.emory.cci.cqlCsm.Filter;

/**
 * @author Mark Grand
 * 
 */
public class CsmDatabaseAccessTest {
//    private static final String BOGUS1_IDENTITY = "/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus1"; 
//    private static final String BOGUS2_IDENTITY = "/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus2"; 
//    private static final String BOGUS3_IDENTITY = "/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus3";
    
    /**
     * @throws java.lang.Exception
     */
//    @BeforeClass
//    public static void setUpBeforeClass() throws Exception {
//        long appId = CsmDatabaseAccess.getInstance().fetchApplicationId();
//        CsmProperties csmProperties = new CsmProperties();
//        String url = "jdbc:postgresql://" + csmProperties.getDatabaseHost() + ":" + csmProperties.getDatabasePort() + "/"
//                + csmProperties.getDatabaseName();
//        String userName = csmProperties.getOwnerId();
//        String password = csmProperties.getOwnerPassword();
//        Class.forName("org.postgresql.Driver");
//        Connection conn = DriverManager.getConnection(url, userName, password);
//        conn.setAutoCommit(true);
//        try {
//            Statement stmt = conn.createStatement();
//            stmt.executeUpdate("truncate table csm_filter_clause");
//            stmt.executeUpdate("delete from csm_user_pe");
//            stmt.executeUpdate("delete from csm_user_group_role_pg");
//            stmt.executeUpdate("delete from csm_protection_element");
//            stmt.executeUpdate("delete from csm_protection_group");
//            stmt.executeUpdate("delete from csm_pg_pe");
//            stmt.executeUpdate("delete from csm_user_group");
//            stmt.executeUpdate("delete from csm_group");
//            stmt.executeUpdate("delete from csm_privilege");
//            stmt.executeUpdate("delete from csm_role");
//            stmt.executeUpdate("delete from csm_remote_group");
//            stmt.executeUpdate("delete from csm_user");
//
//            String sql;
//
//            sql = "INSERT INTO csm_remote_group (group_id, application_id, grid_grouper_url, grid_grouper_group_name) VALUES (26, 2, 'https://grouper.cvrgrid.cci.emory.edu:8443/wsrf/services/cagrid/GridGrouper', 'groupsForTesting:OpenClinica with CSM:Big Study')";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_user"
//                    + " (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization,"
//                    + " department, title, phone_number, \"password\", email_id, start_date, end_date, update_date)"
//                    + " VALUES (6, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus1', 'Bo', 'Gus', '', '', '', '', '', '', NULL, NULL, '2009-09-30'),"
//                    + "(9, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus2', 'Bo', 'Gus', '', '', '', '', '', '', NULL, NULL, '2009-11-05'),"
//                    + "(10, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=mgrand', '', '', '', '', '', '', '', '', NULL, NULL, '2010-01-05'),"
//                    + "(13, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus3', '', '', '', '', '', '', '', '', NULL, NULL, '2010-01-08')";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_protection_group"
//                    + " (protection_group_id, protection_group_name, protection_group_description, application_id,"
//                    + " large_element_count_flag, update_date, parent_protection_group_id)"
//                    + "VALUES (1, 'OpenClinicaPG', 'Service-Level protection group'," + appId + ", 0, '2009-09-30', NULL),"
//                    + "       (3, 'Big Study Group', ''," + appId + ", 0, '2009-11-05', NULL)," + "       (4, 'Fancy Study Group', '',"
//                    + appId + ", 0, '2009-11-05', NULL)," + "       (5, 'Attribute Group', ''," + appId + ", 0, '2009-11-22', NULL)";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_group (group_id, group_name, group_desc, update_date, application_id) VALUES (26, 'Big Study User Group', '', '2010-04-07', "
//                    + appId + ")";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_user_group (user_group_id, user_id, group_id) VALUES (23, 9, 26)";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (1, 'CREATE', 'This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection', '2009-09-21')";
//            stmt.executeUpdate(sql);
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (2, 'ACCESS', 'This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself', '2009-09-21')";
//            stmt.executeUpdate(sql);
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (3, 'READ', 'This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry', '2009-09-21')";
//            stmt.executeUpdate(sql);
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (4, 'WRITE', 'This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity', '2009-09-21')";
//            stmt.executeUpdate(sql);
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (5, 'UPDATE', 'This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc', '2009-09-21')";
//            stmt.executeUpdate(sql);
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (6, 'DELETE', 'This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc', '2009-09-21')";
//            stmt.executeUpdate(sql);
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (7, 'EXECUTE', 'This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc', '2009-09-21')";
//            stmt.executeUpdate(sql);
//            sql = "INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (8, 'ADMIN', 'This privilege permits a user to administrate a particular resource or system.', '2010-01-05')";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_role (role_id, role_name, role_description, application_id, active_flag, update_date) VALUES (4, 'Reader', '', "
//                    + appId + ", 1, '2009-11-20')";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_role_privilege (role_privilege_id, role_id, privilege_id) VALUES (7, 4, 3)";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_protection_element"
//                    + " (protection_element_id, protection_element_name, protection_element_description, object_id,"
//                    + "  attribute, attribute_value, protection_element_type, application_id, update_date)"
//                    + "VALUES (7, 'Big Study (16)', '', 'org.cvrg.domain.Study', 'id', '16', ''," + appId + ", '2009-11-05'),"
//                    + "  (11, 'Fancy Study (17)', '', 'org.cvrg.domain.Study', 'id', '17', ''," + appId + ", '2009-11-05'),"
//                    + "  (13, 'Subject Identifier', '', 'org.cvrg.domain.Subject', 'uniqueIdentifier','',''," + appId + ", '2009-11-22')," //
//                    + "  (14, 'Study Name', '', 'org.cvrg.domain.Study', 'name', '', ''," + appId + ", '2009-11-22')";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date)"
//                    + " VALUES (6, 3, 7, '2009-11-05'),(7, 4, 11, '2009-11-05'),(8, 5, 13, '2009-11-22'),(9, 5, 14, '2009-11-22')";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_user_group_role_pg"
//                    + " (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date)"
//                    + "VALUES (9, 6, NULL, 4, 3, '2009-11-20')," + " (10, 6, NULL, 4, 5, '2009-11-22'),"
//                    + " (24, NULL, 26, 4, 3, '2010-01-24')";
//            stmt.executeUpdate(sql);
//
//            sql = "INSERT INTO csm_filter_clause"
//                    + " (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name,"
//                    + "  target_class_attribute_type, target_class_alias, target_class_attribute_alias,"
//                    + "  generated_sql_user, generated_sql_group, application_id, update_date)"
//                    + " VALUES (16, 'org.cvrg.domain.Subject', 'studySubjectCollection, study',"
//                    + "         'org.cvrg.domain.Study - study', 'id', 'java.lang.Integer', '', '', '', ''," + appId + ", '2009-11-05'),"
//                    + "        (17, 'org.cvrg.domain.Study', 'org.cvrg.domain.Study', 'org.cvrg.domain.Study',"
//                    + "         'id', 'java.lang.Integer', '', '', '', '', " + appId + ", '2009-11-19'),"
//                    + "        (19, 'org.cvrg.domain.Study', 'org.cvrg.domain.Study - self', 'org.cvrg.domain.Study - self',"
//                    + "         'gender', 'java.lang.String', '', '', '', ''," + appId + ", '2010-02-28')";
//            stmt.executeUpdate(sql);
//
//        } finally {
//            conn.close();
//        }
//    }
//
//    /**
//     * @throws java.lang.Exception
//     */
//    @Before
//    public void setUp() throws Exception {
//    }
//
//    /**
//     * Test method for
//     * {@link edu.emory.cci.cqlCsm.CsmDatabaseAccess#getInstance()}.
//     */
    @Test
    public void testGetInstance() {
//        CsmDatabaseAccess cda1 = CsmDatabaseAccess.getInstance();
//        CsmDatabaseAccess cda2 = CsmDatabaseAccess.getInstance();
//        assertSame(cda1, cda2);
    }
//
//    /**
//     * Test method for
//     * {@link edu.emory.cci.cqlCsm.CsmDatabaseAccess#getFiltersForClass(java.lang.String)}
//     * .
//     */
//    @Test
//    public void testGetFiltersForClass() {
//        CsmDatabaseAccess cda = CsmDatabaseAccess.getInstance();
//        List<Filter> bogusFilters = cda.getFiltersForClass("com.bogus");
//        assertTrue(bogusFilters.isEmpty());
//
//        List<Filter> subjectFilters = cda.getFiltersForClass("org.cvrg.domain.Subject");
//        assertEquals(1, subjectFilters.size());
//        Filter subjectFilter1 = new Filter("org.cvrg.domain.Subject", "studySubjectCollection, study", "id",
//                "org.cvrg.domain.Study - study");
//        assertEquals(subjectFilter1, subjectFilters.get(0));
//
//        List<Filter> studyFilters = cda.getFiltersForClass("org.cvrg.domain.Study");
//        assertEquals(2, studyFilters.size());
//        Filter studyFilter1 = new Filter("org.cvrg.domain.Study", "org.cvrg.domain.Study", "id", "org.cvrg.domain.Study");
//        Filter studyFilter2 = new Filter("org.cvrg.domain.Study", "org.cvrg.domain.Study - self", "gender", "org.cvrg.domain.Study - self");
//        assertFalse(studyFilters.get(0).equals(studyFilters.get(1)));
//        assertTrue(studyFilters.get(0).equals(studyFilter1) || studyFilters.get(0).equals(studyFilter2));
//        assertTrue(studyFilters.get(1).equals(studyFilter1) || studyFilters.get(1).equals(studyFilter2));
//    }
//
//    /**
//     * Test method for
//     * {@link edu.emory.cci.cqlCsm.CsmDatabaseAccess#getAuthorizedAttributeValues(java.lang.String, java.lang.String, java.lang.String)}
//     * .
//     */
//    @Test
//    public void testGetAuthorizedAttributeValues() {
//        CsmDatabaseAccess cda = CsmDatabaseAccess.getInstance();
//        Set<String> sd1 = cda.getAuthorizedAttributeValues(BOGUS1_IDENTITY, "org.cvrg.domain.Study", "id");
//        assertNotNull(sd1);
//        assertEquals(1, sd1.size());
//
//        Set<String> sd2 = cda.getAuthorizedAttributeValues(BOGUS2_IDENTITY, "org.cvrg.domain.Study", "id");
//        assertNotNull(sd2);
//        assertEquals(1, sd2.size());
//
//        Set<String> sd3 = cda.getAuthorizedAttributeValues(BOGUS3_IDENTITY, "org.cvrg.domain.Study", "id");
//        assertNotNull(sd3);
//        assertEquals(0, sd3.size());
//
//        Set<String> sj1 = cda.getAuthorizedAttributeValues(BOGUS1_IDENTITY, "org.cvrg.domain.Subject", "id");
//        assertNotNull(sj1);
//        assertEquals(0, sj1.size());
//
//        Set<String> sj2 = cda.getAuthorizedAttributeValues(BOGUS2_IDENTITY, "org.cvrg.domain.Saudjecd9", "id");
//        assertNotNull(sj2);
//        assertEquals(0, sj2.size());
//
//        Set<String> sx1 = cda.getAuthorizedAttributeValues(BOGUS1_IDENTITY, "org.xyz.asdkf", "id");
//        assertNotNull(sx1);
//        assertEquals(0, sx1.size());
//
//        Set<String> sx2 = cda.getAuthorizedAttributeValues(BOGUS1_IDENTITY, "org.cvrg.domain.Study", "sdfgid");
//        assertNotNull(sx2);
//        assertEquals(0, sx2.size());
//
//    }

}
