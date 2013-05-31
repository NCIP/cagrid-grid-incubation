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
--   Schema             : I2B2HIVE 
--   Script Created by  : I2B2HIVE 
--   Script Created at  : 6/30/2009 4:30:57 PM 


CREATE TABLE I2B2HIVE.CRC_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR(255)           NOT NULL,
  C_PROJECT_PATH   VARCHAR(255)           NOT NULL,
  C_OWNER_ID       VARCHAR(255)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR(255)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR(255)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR(255)           NOT NULL,
  C_DB_NICENAME    VARCHAR(255),
  C_DB_TOOLTIP     VARCHAR(255),
  C_COMMENT        VARCHAR(8192),
  C_ENTRY_DATE     DATETIME,
  C_CHANGE_DATE    DATETIME,
  C_STATUS_CD      CHAR(1)
)
;


CREATE TABLE I2B2HIVE.WORK_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR(255)           NOT NULL,
  C_PROJECT_PATH   VARCHAR(255)           NOT NULL,
  C_OWNER_ID       VARCHAR(255)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR(255)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR(255)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR(255)           NOT NULL,
  C_DB_NICENAME    VARCHAR(255),
  C_DB_TOOLTIP     VARCHAR(255),
  C_COMMENT        VARCHAR(8192),
  C_ENTRY_DATE     DATETIME,
  C_CHANGE_DATE    DATETIME,
  C_STATUS_CD      CHAR(1)
)
;


CREATE TABLE I2B2HIVE.ONT_DB_LOOKUP
(
  C_DOMAIN_ID      VARCHAR(255)           NOT NULL,
  C_PROJECT_PATH   VARCHAR(255)           NOT NULL,
  C_OWNER_ID       VARCHAR(255)           NOT NULL,
  C_DB_FULLSCHEMA  VARCHAR(255)           NOT NULL,
  C_DB_DATASOURCE  VARCHAR(255)           NOT NULL,
  C_DB_SERVERTYPE  VARCHAR(255)           NOT NULL,
  C_DB_NICENAME    VARCHAR(255),
  C_DB_TOOLTIP     VARCHAR(255),
  C_COMMENT        VARCHAR(8192),
  C_ENTRY_DATE     DATETIME,
  C_CHANGE_DATE    DATETIME,
  C_STATUS_CD      CHAR(1)
)
;


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

