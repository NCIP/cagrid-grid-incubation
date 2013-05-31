/*
============================================================================
  The Ohio State University Research Foundation, Emory University,
  the University of Minnesota Supercomputing Institute

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
============================================================================
*/
--
-- Create Schema Script 
--   Database Version   : 10.2.0.1.0 
--   TOAD Version       : 9.7.2.5 
--   DB Connect String  : VMWARE_XE 
--   Schema             : I2B2HIVE 
--   Script Created by  : I2B2HIVE 
--   Script Created at  : 6/30/2009 4:30:57 PM 
--   Physical Location  :  
--   Notes              :  
--

-- Object Counts: 
--   Tables: 3          Columns: 36         Constraints: 3      


CREATE TABLE I2B2HIVE.CRC_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR2(255 BYTE)           NOT NULL,
  C_PROJECT_PATH   VARCHAR2(255 BYTE)           NOT NULL,
  C_OWNER_ID       VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_NICENAME    VARCHAR2(255 BYTE),
  C_DB_TOOLTIP     VARCHAR2(255 BYTE),
  C_COMMENT        CLOB,
  C_ENTRY_DATE     DATE,
  C_CHANGE_DATE    DATE,
  C_STATUS_CD      CHAR(1 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE I2B2HIVE.WORK_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR2(255 BYTE)           NOT NULL,
  C_PROJECT_PATH   VARCHAR2(255 BYTE)           NOT NULL,
  C_OWNER_ID       VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_NICENAME    VARCHAR2(255 BYTE),
  C_DB_TOOLTIP     VARCHAR2(255 BYTE),
  C_COMMENT        CLOB,
  C_ENTRY_DATE     DATE,
  C_CHANGE_DATE    DATE,
  C_STATUS_CD      CHAR(1 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE I2B2HIVE.ONT_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR2(255 BYTE)           NOT NULL,
  C_PROJECT_PATH   VARCHAR2(255 BYTE)           NOT NULL,
  C_OWNER_ID       VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR2(255 BYTE)           NOT NULL,
  C_DB_NICENAME    VARCHAR2(255 BYTE),
  C_DB_TOOLTIP     VARCHAR2(255 BYTE),
  C_COMMENT        CLOB,
  C_ENTRY_DATE     DATE,
  C_CHANGE_DATE    DATE,
  C_STATUS_CD      CHAR(1 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


ALTER TABLE I2B2HIVE.CRC_DB_LOOKUP ADD (
  CONSTRAINT CRC_DB_LOOKUP_PK
 PRIMARY KEY
 (C_DOMAIN_ID, C_PROJECT_PATH, C_OWNER_ID));

ALTER TABLE I2B2HIVE.WORK_DB_LOOKUP ADD (
  CONSTRAINT WORK_DB_LOOKUP_PK
 PRIMARY KEY
 (C_DOMAIN_ID, C_PROJECT_PATH, C_OWNER_ID));

ALTER TABLE I2B2HIVE.ONT_DB_LOOKUP ADD (
  CONSTRAINT ONT_DB_LOOKUP_PK
 PRIMARY KEY
 (C_DOMAIN_ID, C_PROJECT_PATH, C_OWNER_ID));

