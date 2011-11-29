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

----------------------------------------------------------------
-- Script to delete all csm-related tables and other objects. --
---------------------------------------------------------------- 

ALTER TABLE ONLY public.csm_user_group_role_pg DROP CONSTRAINT fk_user_group_role_protection_group_user;

ALTER TABLE ONLY public.csm_user_group_role_pg DROP CONSTRAINT fk_user_group_role_protection_group_role;

ALTER TABLE ONLY public.csm_user_group_role_pg DROP CONSTRAINT fk_user_group_role_protection_group_protection_group;

ALTER TABLE ONLY public.csm_user_group_role_pg DROP CONSTRAINT fk_user_group_role_protection_group_groups;

ALTER TABLE ONLY public.csm_user_group DROP CONSTRAINT fk_user_group;

ALTER TABLE ONLY public.csm_user_group DROP CONSTRAINT fk_ug_group;

ALTER TABLE ONLY public.csm_role_privilege DROP CONSTRAINT fk_role;

ALTER TABLE ONLY public.csm_pg_pe DROP CONSTRAINT fk_protection_group_protection_element;

ALTER TABLE ONLY public.csm_protection_group DROP CONSTRAINT fk_protection_group;

ALTER TABLE ONLY public.csm_user_pe DROP CONSTRAINT fk_protection_element_user;

ALTER TABLE ONLY public.csm_pg_pe DROP CONSTRAINT fk_protection_element_protection_group;

ALTER TABLE ONLY public.csm_role_privilege DROP CONSTRAINT fk_privilege_role;

ALTER TABLE ONLY public.csm_protection_group DROP CONSTRAINT fk_pg_application;

ALTER TABLE ONLY public.csm_user_pe DROP CONSTRAINT fk_pe_user;

ALTER TABLE ONLY public.csm_protection_element DROP CONSTRAINT fk_pe_application;

ALTER TABLE ONLY public.csm_role DROP CONSTRAINT fk_application_role;

ALTER TABLE ONLY public.csm_group DROP CONSTRAINT fk_application_group;

ALTER TABLE ONLY public.csm_remote_group_sync_record DROP CONSTRAINT fk2a8a7cfedb129989;

DROP TRIGGER set_csm_pg_pe_update_date ON public.csm_pg_pe;

DROP INDEX public.idx_user_id_upe;

DROP INDEX public.idx_user_id_ugrpg;

DROP INDEX public.idx_user_id;

DROP INDEX public.idx_role_id_ugrpg;

DROP INDEX public.idx_role_id;

DROP INDEX public.idx_protection_group_id_ugrpg;

DROP INDEX public.idx_protection_group_id_pgpe;

DROP INDEX public.idx_protection_element_id_upe;

DROP INDEX public.idx_protection_element_id;

DROP INDEX public.idx_protection_element_atrribute_value;

DROP INDEX public.idx_privilege_id;

DROP INDEX public.idx_parent_protection_group_id;

DROP INDEX public.idx_group_id_ugrpg;

DROP INDEX public.idx_group_id;

DROP INDEX public.idx_application_id_r;

DROP INDEX public.idx_application_id_pg;

DROP INDEX public.idx_application_id_pe;

DROP INDEX public.idx_application_id;

ALTER TABLE ONLY public.csm_user_pe DROP CONSTRAINT uq_user_protection_element_protection_element_id;

ALTER TABLE ONLY public.csm_role DROP CONSTRAINT uq_role_role_name;

ALTER TABLE ONLY public.csm_role_privilege DROP CONSTRAINT uq_role_privilege_role_id;

ALTER TABLE ONLY public.csm_protection_group DROP CONSTRAINT uq_protection_group_protection_group_name;

ALTER TABLE ONLY public.csm_pg_pe DROP CONSTRAINT uq_protection_group_protection_element_protection_group_id;

ALTER TABLE ONLY public.csm_privilege DROP CONSTRAINT uq_privilege_name;

ALTER TABLE ONLY public.csm_protection_element DROP CONSTRAINT uq_pe_pe_name_attribute_app_id;

ALTER TABLE ONLY public.csm_user DROP CONSTRAINT uq_login_name;

ALTER TABLE ONLY public.csm_group DROP CONSTRAINT uq_group_group_name;

ALTER TABLE ONLY public.csm_application DROP CONSTRAINT uq_application_name;

ALTER TABLE ONLY public.csm_user DROP CONSTRAINT csm_user_pkey;

ALTER TABLE ONLY public.csm_user_pe DROP CONSTRAINT csm_user_pe_pkey;

ALTER TABLE ONLY public.csm_user_group_role_pg DROP CONSTRAINT csm_user_group_role_pg_pkey;

ALTER TABLE ONLY public.csm_user_group DROP CONSTRAINT csm_user_group_pkey;

ALTER TABLE ONLY public.csm_role_privilege DROP CONSTRAINT csm_role_privilege_pkey;

ALTER TABLE ONLY public.csm_role DROP CONSTRAINT csm_role_pkey;

ALTER TABLE ONLY public.csm_remote_group_sync_record DROP CONSTRAINT csm_remote_group_sync_record_pkey;

ALTER TABLE ONLY public.csm_remote_group DROP CONSTRAINT csm_remote_group_pkey;

ALTER TABLE ONLY public.csm_protection_group DROP CONSTRAINT csm_protection_group_pkey;

ALTER TABLE ONLY public.csm_protection_element DROP CONSTRAINT csm_protection_element_pkey;

ALTER TABLE ONLY public.csm_privilege DROP CONSTRAINT csm_privilege_pkey;

ALTER TABLE ONLY public.csm_pg_pe DROP CONSTRAINT csm_pg_pe_pkey;

ALTER TABLE ONLY public.csm_group DROP CONSTRAINT csm_group_pkey;

ALTER TABLE ONLY public.csm_filter_clause DROP CONSTRAINT csm_filter_clause_pkey;

ALTER TABLE ONLY public.csm_application DROP CONSTRAINT csm_application_pkey;

DROP VIEW public.csm_user_protection_elements;

DROP TABLE public.csm_user_pe;

DROP SEQUENCE public.csm_user_pe_user_protectio_seq;

DROP TABLE public.csm_user_group_role_pg;

DROP TABLE public.csm_user_group;

DROP SEQUENCE public.csm_user_grou_user_group_r_seq;

DROP SEQUENCE public.csm_user_grou_user_group_i_seq;

DROP TABLE public.csm_user;

DROP SEQUENCE public.csm_user_user_id_seq;

DROP VIEW public.csm_role_priv_vw;

DROP TABLE public.csm_role_privilege;

DROP SEQUENCE public.csm_role_priv_role_privile_seq;

DROP TABLE public.csm_role;

DROP SEQUENCE public.csm_role_role_id_seq;

DROP SEQUENCE public.csm_remote_group_sync_record_id_seq;

DROP TABLE public.csm_remote_group_sync_record;

DROP TABLE public.csm_remote_group;

DROP TABLE public.csm_protection_group;

DROP SEQUENCE public.csm_protectio_protection_g_seq;

DROP TABLE public.csm_privilege;

DROP SEQUENCE public.csm_privilege_privilege_id_seq;

DROP VIEW public.csm_pe_pg_vw;

DROP TABLE public.csm_protection_element;

DROP SEQUENCE public.csm_protectio_protection_e_seq;

DROP TABLE public.csm_pg_pe;

DROP SEQUENCE public.csm_pg_pe_pg_pe_id_seq;

DROP TABLE public.csm_group;

DROP SEQUENCE public.csm_group_group_id_seq;

DROP TABLE public.csm_filter_clause;

DROP SEQUENCE public.csm_filter_clause_seq;

DROP TABLE public.csm_application;

DROP SEQUENCE public.csm_applicati_application__seq;

DROP FUNCTION public.set_csm_pg_pe_update_date();
