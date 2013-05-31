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
--   Schema             : I2B2METADATA 
--   Script Created by  : I2B2METADATA 
--   Script Created at  : 6/30/2009 4:27:37 PM 
--   Physical Location  :  
--   Notes              :  
--

-- Object Counts: 
--   Tables: 4          Columns: 68         Constraints: 2      


CREATE TABLE I2B2METADATA.I2B2
(
  C_HLEVEL            NUMBER(22)                NOT NULL,
  C_FULLNAME          VARCHAR2(900 BYTE)        NOT NULL,
  C_NAME              VARCHAR2(2000 BYTE)       NOT NULL,
  C_SYNONYM_CD        CHAR(1 BYTE)              NOT NULL,
  C_VISUALATTRIBUTES  CHAR(3 BYTE)              NOT NULL,
  C_TOTALNUM          NUMBER(22),
  C_BASECODE          VARCHAR2(450 BYTE),
  C_METADATAXML       CLOB,
  C_FACTTABLECOLUMN   VARCHAR2(50 BYTE)         NOT NULL,
  C_TABLENAME         VARCHAR2(50 BYTE)         NOT NULL,
  C_COLUMNNAME        VARCHAR2(50 BYTE)         NOT NULL,
  C_COLUMNDATATYPE    VARCHAR2(50 BYTE)         NOT NULL,
  C_OPERATOR          VARCHAR2(10 BYTE)         NOT NULL,
  C_DIMCODE           VARCHAR2(900 BYTE)        NOT NULL,
  C_COMMENT           CLOB,
  C_TOOLTIP           VARCHAR2(900 BYTE),
  UPDATE_DATE         DATE,
  DOWNLOAD_DATE       DATE,
  IMPORT_DATE         DATE,
  SOURCESYSTEM_CD     VARCHAR2(50 BYTE),
  VALUETYPE_CD        VARCHAR2(50 BYTE),
  CONCEPT_PATH        VARCHAR2(700 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE I2B2METADATA.TABLE_ACCESS
(
  C_TABLE_CD          VARCHAR2(50 BYTE)         NOT NULL,
  C_TABLE_NAME        VARCHAR2(50 BYTE)         NOT NULL,
  C_PROTECTED_ACCESS  CHAR(1 BYTE),
  C_HLEVEL            NUMBER(22)                NOT NULL,
  C_FULLNAME          VARCHAR2(900 BYTE)        NOT NULL,
  C_NAME              VARCHAR2(2000 BYTE)       NOT NULL,
  C_SYNONYM_CD        CHAR(1 BYTE)              NOT NULL,
  C_VISUALATTRIBUTES  CHAR(3 BYTE)              NOT NULL,
  C_TOTALNUM          NUMBER(22),
  C_BASECODE          VARCHAR2(450 BYTE),
  C_METADATAXML       CLOB,
  C_FACTTABLECOLUMN   VARCHAR2(50 BYTE)         NOT NULL,
  C_DIMTABLENAME      VARCHAR2(50 BYTE)         NOT NULL,
  C_COLUMNNAME        VARCHAR2(50 BYTE)         NOT NULL,
  C_COLUMNDATATYPE    VARCHAR2(50 BYTE)         NOT NULL,
  C_OPERATOR          VARCHAR2(10 BYTE)         NOT NULL,
  C_DIMCODE           VARCHAR2(900 BYTE)        NOT NULL,
  C_COMMENT           CLOB,
  C_TOOLTIP           VARCHAR2(900 BYTE),
  C_ENTRY_DATE        DATE,
  C_CHANGE_DATE       DATE,
  C_STATUS_CD         CHAR(1 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE I2B2METADATA.SCHEMES
(
  C_KEY          VARCHAR2(50 BYTE)              NOT NULL,
  C_NAME         VARCHAR2(50 BYTE)              NOT NULL,
  C_DESCRIPTION  VARCHAR2(100 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE TABLE I2B2METADATA.BIRN
(
  C_HLEVEL            NUMBER(22)                NOT NULL,
  C_FULLNAME          VARCHAR2(900 BYTE)        NOT NULL,
  C_NAME              VARCHAR2(2000 BYTE)       NOT NULL,
  C_SYNONYM_CD        CHAR(1 BYTE)              NOT NULL,
  C_VISUALATTRIBUTES  CHAR(3 BYTE)              NOT NULL,
  C_TOTALNUM          NUMBER(22),
  C_BASECODE          VARCHAR2(450 BYTE),
  C_METADATAXML       CLOB,
  C_FACTTABLECOLUMN   VARCHAR2(50 BYTE)         NOT NULL,
  C_TABLENAME         VARCHAR2(50 BYTE)         NOT NULL,
  C_COLUMNNAME        VARCHAR2(50 BYTE)         NOT NULL,
  C_COLUMNDATATYPE    VARCHAR2(50 BYTE)         NOT NULL,
  C_OPERATOR          VARCHAR2(10 BYTE)         NOT NULL,
  C_DIMCODE           VARCHAR2(900 BYTE)        NOT NULL,
  C_COMMENT           CLOB,
  C_TOOLTIP           VARCHAR2(900 BYTE),
  UPDATE_DATE         DATE,
  DOWNLOAD_DATE       DATE,
  IMPORT_DATE         DATE,
  SOURCESYSTEM_CD     VARCHAR2(50 BYTE),
  VALUETYPE_CD        VARCHAR2(50 BYTE)
)
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


ALTER TABLE I2B2METADATA.TABLE_ACCESS ADD (
  CONSTRAINT TABLE_ACCESS_PK
 PRIMARY KEY
 (C_TABLE_CD));

ALTER TABLE I2B2METADATA.SCHEMES ADD (
  CONSTRAINT SCHEMES_PK
 PRIMARY KEY
 (C_KEY));

