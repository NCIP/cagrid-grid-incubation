/*
============================================================================
  The Ohio State University Research Foundation, Emory University,
  the University of Minnesota Supercomputing Institute

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
============================================================================
*/
update i2b2demodata.concept_dimension
set concept_path = '\00Pharmacy\' || '000000000' || upper(substring(name_char,1,1)) || '\' 
|| insertstr(0,substring(concept_cd,10), substring('0000000000',length(substring(concept_cd,10))+1)) || '\' 
where sourcesystem_cd = 'Pharmacy';

insert into i2b2demodata.OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, UNITS_CD, END_DATE, 
LOCATION_CD, CONFIDENCE_NUM, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID, CONCEPT_PATH)
select 1031133,31133,concept_cd,'@','01-Jan-2010','@','T',name_char,null,null,null,null,'01-Jan-2010','@',null,'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',1,concept_path  from i2b2demodata.concept_dimension
where sourcesystem_cd = 'Pharmacy';

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
select 2, concept_path, name_char, 'N', 'LA', concept_cd, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', concept_path, name_char, '01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',concept_path
from i2b2demodata.concept_dimension where sourcesystem_cd = 'Pharmacy';

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(0, '\00Pharmacy\', 'Pharmacy', 'N', 'CA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\', 'Pharmacy', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.TABLE_ACCESS (C_TABLE_CD, C_TABLE_NAME, C_PROTECTED_ACCESS, C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, 
C_VISUALATTRIBUTES, C_FACTTABLECOLUMN, C_DIMTABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, 
C_OPERATOR, C_DIMCODE, C_TOOLTIP, C_ENTRY_DATE)  
values ('Pharmacy','i2b2','N', 0, '\00Pharmacy\', 'Pharmacy', 'N', 'CA', 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\', 'Pharmacy', 
'01-Jan-2010');



insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000A\', 'A', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000A\', 'A', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000B\', 'B', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000B\', 'B', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000C\', 'C', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000C\', 'C', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000D\', 'D', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000D\', 'D', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000E\', 'E', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000E\', 'E', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000F\', 'F', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000F\', 'F', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000G\', 'G', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000G\', 'G', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000H\', 'H', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000H\', 'H', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000I\', 'I', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000I\', 'I', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000J\', 'J', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000J\', 'J', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000K\', 'K', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000K\', 'K', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000L\', 'L', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000L\', 'L', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000M\', 'M', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000M\', 'M', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000N\', 'N', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000N\', 'N', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000O\', 'O', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000O\', 'O', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000P\', 'P', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000P\', 'P', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000Q\', 'Q', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000Q\', 'Q', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000R\', 'R', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000R\', 'R', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000S\', 'S', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000S\', 'S', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000T\', 'T', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000T\', 'T', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000U\', 'U', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000U\', 'U', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000V\', 'V', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000V\', 'V', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000W\', 'W', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000W\', 'W', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000X\', 'X', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000X\', 'X', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000Y\', 'Y', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000Y\', 'Y', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\00Pharmacy\000000000Z\', 'Z', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\00Pharmacy\000000000Z\', 'Z', 
'01-Jan-2010','01-Jan-2010','01-Jan-2010','Pharmacy',null);

commit;


