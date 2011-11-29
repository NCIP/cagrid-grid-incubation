-- Copyright © 2010 Emory University
-- 
-- Permission is hereby granted, free of charge, to any person obtaining a
-- copy of this software and associated  documentation files (the "Software"),
-- to deal in the Software without restriction, including without limitation 
-- the rights to use, copy, modify, merge, publish, distribute, sublicense, 
-- and/or sell copies of the Software, and to permit persons to whom the
-- Software is furnished to do so, subject to the following conditions: The
-- above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software. 
-- 
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
-- EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
-- MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
-- EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
-- OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
-- ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
-- DEALINGS IN THE SOFTWARE.

-- Populate tables with data for testing CqlCsmPreprocessor

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Name: csm_applicati_application__seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_applicati_application__seq', 6, true);


--
-- Name: csm_filter_clause_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_filter_clause_seq', 1, true);


--
-- Name: csm_group_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_group_group_id_seq', 31, true);


--
-- Name: csm_pg_pe_pg_pe_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_pg_pe_pg_pe_id_seq', 30, true);


--
-- Name: csm_protectio_protection_e_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_protectio_protection_e_seq', 28, true);


--
-- Name: csm_privilege_privilege_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_privilege_privilege_id_seq', 8, true);


--
-- Name: csm_protectio_protection_g_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_protectio_protection_g_seq', 14, true);


--
-- Name: csm_remote_group_sync_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: clinica
--

SELECT pg_catalog.setval('csm_remote_group_sync_record_id_seq', 43334, true);


--
-- Name: csm_role_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_role_role_id_seq', 6, true);


--
-- Name: csm_role_priv_role_privile_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_role_priv_role_privile_seq', 9, true);


--
-- Name: csm_user_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_user_user_id_seq', 14, true);


--
-- Name: csm_user_grou_user_group_i_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_user_grou_user_group_i_seq', 24, true);


--
-- Name: csm_user_grou_user_group_r_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_user_grou_user_group_r_seq', 32, true);


--
-- Name: csm_user_pe_user_protectio_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('csm_user_pe_user_protectio_seq', 2, true);


--
-- Data for Name: csm_application; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_application (application_id, application_name, application_description, update_date, database_url, database_user_name, database_password, database_dialect, database_driver, declarative_flag, active_flag) VALUES (1, 'csmupt', 'CSM UPT Super Admin Application', '2009-09-21', NULL, NULL, NULL, NULL, NULL, '0', '0');
INSERT INTO csm_application (application_id, application_name, application_description, update_date, database_url, database_user_name, database_password, database_dialect, database_driver, declarative_flag, active_flag) VALUES (2, 'openclinica', 'openclinica', '2009-09-30', 'jdbc:postgresql://localhost:5432/openclinica', 'clinica', 'BV9q0wiXTT0=', 'org.hibernate.dialect.PostgreSQLDialect', 'org.postgresql.Driver', '1', '1');
INSERT INTO csm_application (application_id, application_name, application_description, update_date, database_url, database_user_name, database_password, database_dialect, database_driver, declarative_flag, active_flag) VALUES (3, 'CSM Web Service', 'This application is for managing access to the CSM Web service.', '2010-01-05', NULL, NULL, NULL, NULL, NULL, '0', '0');
INSERT INTO csm_application (application_id, application_name, application_description, update_date, database_url, database_user_name, database_password, database_dialect, database_driver, declarative_flag, active_flag) VALUES (6, :APP_NAME, 'Application name used for testing CqlCsmPreprocessor', '2010-05-05', NULL, NULL, NULL, NULL, NULL, '0', '0');


--
-- Data for Name: csm_filter_clause; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_filter_clause (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name, target_class_attribute_type, target_class_alias, target_class_attribute_alias, generated_sql_user, generated_sql_group, application_id, update_date) VALUES (16, 'org.cvrg.domain.Subject', 'studySubjectCollection, study', 'org.cvrg.domain.Study - study', 'id', 'java.lang.Integer', '', '', 'subject_id in (select table_name_csm_.subject_id   from subject table_name_csm_ inner join study_subject studysubje1_ on table_name_csm_.subject_id=studysubje1_.subject_id inner join study study2_ on studysubje1_.study_id=study2_.study_id where study2_.study_id in ( select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= ''org.cvrg.domain.Study'' and pe.attribute=''id'' and p.privilege_name=''READ'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID))', 'subject_id in (select table_name_csm_.subject_id   from subject table_name_csm_ inner join study_subject studysubje1_ on table_name_csm_.subject_id=studysubje1_.subject_id inner join study study2_ on studysubje1_.study_id=study2_.study_id where study2_.study_id in (SELECT Distinct pe.attribute_value FROM CSM_PROTECTION_GROUP pg, 	CSM_PROTECTION_ELEMENT pe, 	CSM_PG_PE pgpe,	CSM_USER_GROUP_ROLE_PG ugrpg, 	CSM_GROUP g, 	CSM_ROLE_PRIVILEGE rp, 	CSM_ROLE r, 	CSM_PRIVILEGE p WHERE ugrpg.role_id = r.role_id AND ugrpg.group_id = g.group_id AND ugrpg.protection_group_id = ANY ( select pg1.protection_group_id from csm_protection_group pg1  where pg1.protection_group_id = pg.protection_group_id OR pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id) ) AND pg.protection_group_id = pgpe.protection_group_id AND pgpe.protection_element_id = pe.protection_element_id AND r.role_id = rp.role_id AND rp.privilege_id = p.privilege_id AND pe.object_id= ''org.cvrg.domain.Study'' AND p.privilege_name=''READ'' AND g.group_name IN (:GROUP_NAMES ) AND pe.application_id=:APPLICATION_ID))', 2, '2009-11-05');
INSERT INTO csm_filter_clause (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name, target_class_attribute_type, target_class_alias, target_class_attribute_alias, generated_sql_user, generated_sql_group, application_id, update_date) VALUES (17, 'org.cvrg.domain.Study', 'org.cvrg.domain.Study', 'org.cvrg.domain.Study', 'id', 'java.lang.Integer', '', '', 'study_id in (select table_name_csm_.study_id   from study table_name_csm_ where table_name_csm_.study_id in ( select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= ''org.cvrg.domain.Study'' and pe.attribute=''id'' and p.privilege_name=''READ'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID))', 'study_id in (select table_name_csm_.study_id   from study table_name_csm_ where table_name_csm_.study_id in (SELECT Distinct pe.attribute_value FROM CSM_PROTECTION_GROUP pg, 	CSM_PROTECTION_ELEMENT pe, 	CSM_PG_PE pgpe,	CSM_USER_GROUP_ROLE_PG ugrpg, 	CSM_GROUP g, 	CSM_ROLE_PRIVILEGE rp, 	CSM_ROLE r, 	CSM_PRIVILEGE p WHERE ugrpg.role_id = r.role_id AND ugrpg.group_id = g.group_id AND ugrpg.protection_group_id = ANY ( select pg1.protection_group_id from csm_protection_group pg1  where pg1.protection_group_id = pg.protection_group_id OR pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id) ) AND pg.protection_group_id = pgpe.protection_group_id AND pgpe.protection_element_id = pe.protection_element_id AND r.role_id = rp.role_id AND rp.privilege_id = p.privilege_id AND pe.object_id= ''org.cvrg.domain.Study'' AND p.privilege_name=''READ'' AND g.group_name IN (:GROUP_NAMES ) AND pe.application_id=:APPLICATION_ID))', 2, '2009-11-19');
INSERT INTO csm_filter_clause (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name, target_class_attribute_type, target_class_alias, target_class_attribute_alias, generated_sql_user, generated_sql_group, application_id, update_date) VALUES (19, 'org.cvrg.domain.Study', 'org.cvrg.domain.Study - self', 'org.cvrg.domain.Study - self', 'gender', 'java.lang.String', '', '', 'study_id in (select table_name_csm_.study_id   from study table_name_csm_ where table_name_csm_.gender in ( select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= ''org.cvrg.domain.Study'' and pe.attribute=''gender'' and p.privilege_name=''READ'' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID))', 'study_id in (select table_name_csm_.study_id   from study table_name_csm_ where table_name_csm_.gender in (SELECT Distinct pe.attribute_value FROM CSM_PROTECTION_GROUP pg, 	CSM_PROTECTION_ELEMENT pe, 	CSM_PG_PE pgpe,	CSM_USER_GROUP_ROLE_PG ugrpg, 	CSM_GROUP g, 	CSM_ROLE_PRIVILEGE rp, 	CSM_ROLE r, 	CSM_PRIVILEGE p WHERE ugrpg.role_id = r.role_id AND ugrpg.group_id = g.group_id AND ugrpg.protection_group_id = ANY ( select pg1.protection_group_id from csm_protection_group pg1  where pg1.protection_group_id = pg.protection_group_id OR pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id) ) AND pg.protection_group_id = pgpe.protection_group_id AND pgpe.protection_element_id = pe.protection_element_id AND r.role_id = rp.role_id AND rp.privilege_id = p.privilege_id AND pe.object_id= ''org.cvrg.domain.Study'' AND p.privilege_name=''READ'' AND g.group_name IN (:GROUP_NAMES ) AND pe.application_id=:APPLICATION_ID))', 2, '2010-02-28');
INSERT INTO csm_filter_clause (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name, target_class_attribute_type, target_class_alias, target_class_attribute_alias, generated_sql_user, generated_sql_group, application_id, update_date) VALUES (24, 'v3.hl7_org.CD', 'v3.hl7_org.CD - self', 'v3.hl7_org.CD - self', 'nullFlavor', '*', '', '', '', '', 6, '2010-05-05');
INSERT INTO csm_filter_clause (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name, target_class_attribute_type, target_class_alias, target_class_attribute_alias, generated_sql_user, generated_sql_group, application_id, update_date) VALUES (25, 'v3.hl7_org.CD', 'qualifier', 'qualifier - v3.hl7_org.CR', 'code', '*', '', '', '', '', 6, '2010-05-05');
INSERT INTO csm_filter_clause (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name, target_class_attribute_type, target_class_alias, target_class_attribute_alias, generated_sql_user, generated_sql_group, application_id, update_date) VALUES (26, 'v3.hl7_org.CD', 'qualifier, name', 'name - v3.hl7_org.CV', 'nullFlavor', '*', '', '', '', '', 6, '2010-05-05');
INSERT INTO csm_filter_clause (filter_clause_id, class_name, filter_chain, target_class_name, target_class_attribute_name, target_class_attribute_type, target_class_alias, target_class_attribute_alias, generated_sql_user, generated_sql_group, application_id, update_date) VALUES (27, 'v3.hl7_org.ED', 'v3.hl7_org.ED - self', 'v3.hl7_org.ED - self', 'mediaType', '*', '', '', '', '', 6, '2010-05-05');


--
-- Data for Name: csm_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_group (group_id, group_name, group_desc, update_date, application_id) VALUES (18, 'CSM Web Service Administrators', 'CSM Web Service administrators group', '2010-01-18', 3);
INSERT INTO csm_group (group_id, group_name, group_desc, update_date, application_id) VALUES (19, 'csmupt Administrators', 'CSM Web Service administrator group for the application csmupt.', '2010-01-18', 3);
INSERT INTO csm_group (group_id, group_name, group_desc, update_date, application_id) VALUES (20, 'openclinica Administrators', 'CSM Web Service administrator group for the application openclinica.', '2010-01-18', 3);
INSERT INTO csm_group (group_id, group_name, group_desc, update_date, application_id) VALUES (30, 'csm_test Administrators', 'CSM Web Service administrator group for the application csm_test.', '2010-05-05', 3);
INSERT INTO csm_group (group_id, group_name, group_desc, update_date, application_id) VALUES (31, 'Access Group', 'Users who have access to objects', '2010-05-06', 6);
INSERT INTO csm_group (group_id, group_name, group_desc, update_date, application_id) VALUES (26, 'Big Study User Group', '', '2010-05-07', 2);


--
-- Data for Name: csm_privilege; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_privilege (privilege_id, privilege_name, privilege_description, update_date) VALUES (8, 'ADMIN', 'This privilege permits a user to administrate a particular resource or system.', '2010-01-05');


--
-- Data for Name: csm_protection_element; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (1, 'csmupt', 'CSM UPT Super Admin Application Protection Element', 'csmupt', NULL, NULL, NULL, 1, '2009-09-21');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (2, 'openclinica', 'OpenClinica Admin Application Protection Element', 'openclinica', '', '', '', 1, '2009-09-30');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (3, 'OpenClinicaCRF', '', 'https://csm.cci.emory.edu:8448/OpenClinicaCRF', '', '', '', 2, '2009-09-30');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (7, 'Big Study (16)', '', 'org.cvrg.domain.Study', 'id', '16', '', 2, '2009-11-05');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (11, 'Fancy Study (17)', '', 'org.cvrg.domain.Study', 'id', '17', '', 2, '2009-11-05');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (13, 'Subject Identifier', '', 'org.cvrg.domain.Subject', 'uniqueIdentifier', '', '', 2, '2009-11-22');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (14, 'Study Name', '', 'org.cvrg.domain.Study', 'name', '', '', 2, '2009-11-22');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (15, 'CSM Web Service', 'CSM Web Service protection element for the application CSM Web Service.', 'CSM Web Service', '', '', '', 3, '2010-01-05');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (16, 'csmupt', 'CSM Web Service protection element for the application csmupt.', 'csmupt', '', '', '', 3, '2010-01-05');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (17, 'openclinica', 'CSM Web Service protection element for the application openclinica.', 'openclinica', '', '', '', 3, '2010-01-05');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (22, 'csm_test', 'CSM Web Service protection element for the application csm_test.', 'csm_test', '', '', '', 3, '2010-05-05');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (23, 'pe_CD_nullFlavor_true', '', 'v3.hl7_org.CD', 'nullFlavor', 'true', '', 6, '2010-05-07');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (24, 'pe_CR_code_z1', '', 'v3.hl7_org.CR', 'code', 'z1', '', 6, '2010-05-07');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (25, 'pe_CR_code_z2', '', 'v3.hl7_org.CR', 'code', 'z2', '', 6, '2010-05-07');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (26, 'pe_CR_code_z3', '', 'v3.hl7_org.CR', 'code', 'z3', '', 6, '2010-05-07');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (27, 'pe_ED_mediaType_3', '', 'v3.hl7_org.ED', 'mediaType', '3', '', 6, '2010-05-07');
INSERT INTO csm_protection_element (protection_element_id, protection_element_name, protection_element_description, object_id, attribute, attribute_value, protection_element_type, application_id, update_date) VALUES (28, 'pe_CV_nullFlavor_false', '', 'v3.hl7_org.CV', 'nullFlavor', 'false', '', 6, '2010-05-07');


--
-- Data for Name: csm_protection_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (1, 'OpenClinicaPG', 'Service-Level protection group', 2, 0, '2009-09-30', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (3, 'Big Study Group', '', 2, 0, '2009-11-05', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (4, 'Fancy Study Group', '', 2, 0, '2009-11-05', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (5, 'Attribute Group', '', 2, 0, '2009-11-22', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (6, 'CSM Web Service', 'CSM Web Service protection group for the application CSM Web Service.', 3, 0, '2010-01-05', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (7, 'csmupt', 'CSM Web Service protection group for the application csmupt.', 3, 0, '2010-01-05', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (8, 'openclinica', 'CSM Web Service protection group for the application openclinica.', 3, 0, '2010-01-05', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (13, 'csm_test', 'CSM Web Service protection group for the application csm_test.', 3, 0, '2010-05-05', NULL);
INSERT INTO csm_protection_group (protection_group_id, protection_group_name, protection_group_description, application_id, large_element_count_flag, update_date, parent_protection_group_id) VALUES (14, 'access_pg', '', 6, 0, '2010-05-06', NULL);


--
-- Data for Name: csm_remote_group; Type: TABLE DATA; Schema: public; Owner: clinica
--

INSERT INTO csm_remote_group (group_id, application_id, grid_grouper_url, grid_grouper_group_name) VALUES (26, 2, 'https://grouper.cvrgrid.cci.emory.edu:8443/wsrf/services/cagrid/GridGrouper', 'groupsForTesting:OpenClinica with CSM:Big Study');


--
-- Data for Name: csm_remote_group_sync_record; Type: TABLE DATA; Schema: public; Owner: clinica
--

INSERT INTO csm_remote_group_sync_record (record_id, group_id, result, message, sync_date) VALUES (8507, 26, 'SUCCESSFUL', '', '2010-01-20 16:35:49.485');
INSERT INTO csm_remote_group_sync_record (record_id, group_id, result, message, sync_date) VALUES (8508, 26, 'SUCCESSFUL', '', '2010-01-20 16:37:50.136');
INSERT INTO csm_remote_group_sync_record (record_id, group_id, result, message, sync_date) VALUES (8509, 26, 'SUCCESSFUL', '', '2010-01-20 16:39:50.664');


--
-- Data for Name: csm_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_role (role_id, role_name, role_description, application_id, active_flag, update_date) VALUES (1, 'ServiceAccessor', 'A role that is entitled to access the data service', 2, 1, '2009-09-30');
INSERT INTO csm_role (role_id, role_name, role_description, application_id, active_flag, update_date) VALUES (4, 'Reader', '', 2, 1, '2009-11-20');
INSERT INTO csm_role (role_id, role_name, role_description, application_id, active_flag, update_date) VALUES (5, 'Administrator', 'This role permits a user to administrate a particular resource or system.', 3, 0, '2010-01-05');
INSERT INTO csm_role (role_id, role_name, role_description, application_id, active_flag, update_date) VALUES (6, 'Reader', '', 6, 0, '2010-05-06');


--
-- Data for Name: csm_role_privilege; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_role_privilege (role_privilege_id, role_id, privilege_id) VALUES (2, 1, 2);
INSERT INTO csm_role_privilege (role_privilege_id, role_id, privilege_id) VALUES (7, 4, 3);
INSERT INTO csm_role_privilege (role_privilege_id, role_id, privilege_id) VALUES (8, 5, 8);
INSERT INTO csm_role_privilege (role_privilege_id, role_id, privilege_id) VALUES (9, 6, 3);


--
-- Data for Name: csm_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (1, NULL, '0', 'uptSuper', 'UPT', 'Super-Administrator', NULL, NULL, NULL, NULL, 'zJPWCwDeSgG8j2uyHEABIQ==', 'mgrand@emory.edu', NULL, NULL, '2009-09-21');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (2, '', '0', 'Admin', 'Admini', 'Strator', '', '', '', '', '5lFdo8cE0Iw=', '', NULL, NULL, '2009-09-29');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (6, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus1', 'Bo', 'Gus', '', '', '', '', '', '', NULL, NULL, '2009-09-30');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (9, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus2', 'Bo', 'Gus', '', '', '', '', '', '', NULL, NULL, '2009-11-05');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (10, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=mgrand', '', '', '', '', '', '', '', '', NULL, NULL, '2010-01-05');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (11, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=mansour', '', '', '', '', '', '', '', '', NULL, NULL, '2010-01-05');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (12, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=mtoerper', '', '', '', '', '', '', '', '', NULL, NULL, '2010-01-05');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (13, '', '0', '/O=CVRG/OU=LOA1/OU=Dorian/CN=bogus3', '', '', '', '', '', '', '', '', NULL, NULL, '2010-01-08');
INSERT INTO csm_user (user_id, premgrt_login_name, migrated_flag, login_name, first_name, last_name, organization, department, title, phone_number, "password", email_id, start_date, end_date, update_date) VALUES (14, '', '0', '/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe', '', '', '', '', '', '', '', '', NULL, NULL, '2010-05-06');


--
-- Data for Name: csm_user_group; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_user_group (user_group_id, user_id, group_id) VALUES (20, 10, 18);
INSERT INTO csm_user_group (user_group_id, user_id, group_id) VALUES (23, 9, 26);
INSERT INTO csm_user_group (user_group_id, user_id, group_id) VALUES (24, 14, 31);


--
-- Data for Name: csm_user_group_role_pg; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (2, 6, NULL, 1, 1, '2009-09-30');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (9, 6, NULL, 4, 3, '2009-11-20');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (10, 6, NULL, 4, 5, '2009-11-22');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (18, 9, NULL, 1, 1, '2010-01-08');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (20, NULL, 19, 5, 7, '2010-01-18');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (21, NULL, 18, 5, 7, '2010-01-18');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (22, NULL, 20, 5, 8, '2010-01-18');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (23, NULL, 18, 5, 8, '2010-01-18');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (24, NULL, 26, 4, 3, '2010-01-24');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (25, NULL, 18, 5, 6, '2010-04-13');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (30, NULL, 30, 5, 13, '2010-05-05');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (31, NULL, 18, 5, 13, '2010-05-05');
INSERT INTO csm_user_group_role_pg (user_group_role_pg_id, user_id, group_id, role_id, protection_group_id, update_date) VALUES (32, NULL, 31, 6, 14, '2010-05-06');


--
-- Data for Name: csm_user_pe; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_user_pe (user_protection_element_id, protection_element_id, user_id) VALUES (1, 1, 1);
INSERT INTO csm_user_pe (user_protection_element_id, protection_element_id, user_id) VALUES (2, 2, 2);


--
-- Data for Name: csm_pg_pe; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (1, 1, 3, '2009-09-30');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (6, 3, 7, '2009-11-05');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (7, 4, 11, '2009-11-05');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (8, 5, 13, '2009-11-22');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (9, 5, 14, '2009-11-22');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (10, 6, 15, '2010-01-05');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (11, 7, 16, '2010-01-05');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (12, 8, 17, '2010-01-05');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (15, 13, 22, '2010-05-05');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (16, 14, 23, '2010-05-06');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (17, 14, 26, '2010-05-06');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (18, 14, 25, '2010-05-06');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (19, 14, 24, '2010-05-06');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (20, 14, 27, '2010-05-06');
INSERT INTO csm_pg_pe (pg_pe_id, protection_group_id, protection_element_id, update_date) VALUES (21, 14, 28, '2010-05-06');
