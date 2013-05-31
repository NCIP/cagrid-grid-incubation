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
--   Schema             : I2B2WORKDATA 
--   Script Created by  : I2B2WORKDATA 
--   Script Created at  : 6/30/2009 4:28:47 PM 


CREATE TABLE I2B2WORKDATA.WORKPLACE
(
  C_NAME                VARCHAR(255)      NOT NULL,
  C_USER_ID             VARCHAR(255)      NOT NULL,
  C_GROUP_ID            VARCHAR(255)      NOT NULL,
  C_SHARE_ID            VARCHAR(255),
  C_INDEX               VARCHAR(255)      NOT NULL,
  C_PARENT_INDEX        VARCHAR(255),
  C_VISUALATTRIBUTES    CHAR(3)            NOT NULL,
  C_PROTECTED_ACCESS    CHAR(1),
  C_TOOLTIP             VARCHAR(255),
  C_WORK_XML            VARCHAR(8192),
  C_WORK_XML_SCHEMA     VARCHAR(8192),
  C_WORK_XML_I2B2_TYPE  VARCHAR(255),
  C_ENTRY_DATE          DATETIME,
  C_CHANGE_DATE         DATETIME,
  C_STATUS_CD           CHAR(1)
)
;


CREATE TABLE I2B2WORKDATA.WORKPLACE_ACCESS
(
  C_TABLE_CD          VARCHAR(255)        NOT NULL,
  C_TABLE_NAME        VARCHAR(255)        NOT NULL,
  C_PROTECTED_ACCESS  CHAR(1),
  C_HLEVEL            INT                   NOT NULL,
  C_NAME              VARCHAR(255)        NOT NULL,
  C_USER_ID           VARCHAR(255)        NOT NULL,
  C_GROUP_ID          VARCHAR(255)        NOT NULL,
  C_SHARE_ID          VARCHAR(255),
  C_INDEX             VARCHAR(255)        NOT NULL,
  C_PARENT_INDEX      VARCHAR(255),
  C_VISUALATTRIBUTES  CHAR(3)              NOT NULL,
  C_TOOLTIP           VARCHAR(255),
  C_ENTRY_DATE        DATETIME,
  C_CHANGE_DATE       DATETIME,
  C_STATUS_CD         CHAR(1)
)
;


ALTER TABLE I2B2WORKDATA.WORKPLACE ADD (
  CONSTRAINT WORKPLACE_PK
 PRIMARY KEY
 (C_INDEX));

ALTER TABLE I2B2WORKDATA.WORKPLACE_ACCESS ADD (
  CONSTRAINT WORKPLACE_ACCESS_PK
 PRIMARY KEY
 (C_INDEX));

