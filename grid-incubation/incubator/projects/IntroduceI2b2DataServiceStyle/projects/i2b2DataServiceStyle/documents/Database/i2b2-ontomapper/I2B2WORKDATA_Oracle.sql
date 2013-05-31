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
--   Schema             : I2B2WORKDATA 
--   Script Created by  : I2B2WORKDATA 
--   Script Created at  : 6/30/2009 4:28:47 PM 
--   Physical Location  :  
--   Notes              :  
--

-- Object Counts: 
--   Tables: 2          Columns: 30         Constraints: 2      


CREATE TABLE I2B2WORKDATA.WORKPLACE
(
  C_NAME                VARCHAR2(255 BYTE)      NOT NULL,
  C_USER_ID             VARCHAR2(255 BYTE)      NOT NULL,
  C_GROUP_ID            VARCHAR2(255 BYTE)      NOT NULL,
  C_SHARE_ID            VARCHAR2(255 BYTE),
  C_INDEX               VARCHAR2(255 BYTE)      NOT NULL,
  C_PARENT_INDEX        VARCHAR2(255 BYTE),
  C_VISUALATTRIBUTES    CHAR(3 BYTE)            NOT NULL,
  C_PROTECTED_ACCESS    CHAR(1 BYTE),
  C_TOOLTIP             VARCHAR2(255 BYTE),
  C_WORK_XML            CLOB,
  C_WORK_XML_SCHEMA     CLOB,
  C_WORK_XML_I2B2_TYPE  VARCHAR2(255 BYTE),
  C_ENTRY_DATE          DATE,
  C_CHANGE_DATE         DATE,
  C_STATUS_CD           CHAR(1 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE I2B2WORKDATA.WORKPLACE_ACCESS
(
  C_TABLE_CD          VARCHAR2(255 BYTE)        NOT NULL,
  C_TABLE_NAME        VARCHAR2(255 BYTE)        NOT NULL,
  C_PROTECTED_ACCESS  CHAR(1 BYTE),
  C_HLEVEL            INTEGER                   NOT NULL,
  C_NAME              VARCHAR2(255 BYTE)        NOT NULL,
  C_USER_ID           VARCHAR2(255 BYTE)        NOT NULL,
  C_GROUP_ID          VARCHAR2(255 BYTE)        NOT NULL,
  C_SHARE_ID          VARCHAR2(255 BYTE),
  C_INDEX             VARCHAR2(255 BYTE)        NOT NULL,
  C_PARENT_INDEX      VARCHAR2(255 BYTE),
  C_VISUALATTRIBUTES  CHAR(3 BYTE)              NOT NULL,
  C_TOOLTIP           VARCHAR2(255 BYTE),
  C_ENTRY_DATE        DATE,
  C_CHANGE_DATE       DATE,
  C_STATUS_CD         CHAR(1 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


ALTER TABLE I2B2WORKDATA.WORKPLACE ADD (
  CONSTRAINT WORKPLACE_PK
 PRIMARY KEY
 (C_INDEX));

ALTER TABLE I2B2WORKDATA.WORKPLACE_ACCESS ADD (
  CONSTRAINT WORKPLACE_ACCESS_PK
 PRIMARY KEY
 (C_INDEX));

