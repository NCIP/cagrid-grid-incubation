update i2b2demodata.concept_dimension
set concept_path = '\~~Pharmacy\' || '~~~~~~~~~' || upper(substring(name_char,1,1)) || '\' 
|| insertstr(0,substring(concept_cd,8), substring('~~~~~~~~~~',length(substring(concept_cd,8))+1)) || '\' 
where sourcesystem_cd = 'Pharmacy';

insert into i2b2demodata.OBSERVATION_FACT (ENCOUNTER_NUM, PATIENT_NUM, CONCEPT_CD, PROVIDER_ID, START_DATE, MODIFIER_CD, VALTYPE_CD, TVAL_CHAR, NVAL_NUM, VALUEFLAG_CD, QUANTITY_NUM, UNITS_CD, END_DATE, 
LOCATION_CD, CONFIDENCE_NUM, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, UPLOAD_ID, CONCEPT_PATH)
select 1031133,31133,concept_cd,'@','2010-01-01','@','T',name_char,null,null,null,null,'2010-01-01','@',null,'2010-01-01','2010-01-01','2010-01-01','Pharmacy',1,concept_path  from i2b2demodata.concept_dimension
where sourcesystem_cd = 'Pharmacy';

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
select 2, concept_path, name_char, 'N', 'LA', concept_cd, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', concept_path, name_char, '2010-01-01','2010-01-01','2010-01-01','Pharmacy',concept_path
from i2b2demodata.concept_dimension where sourcesystem_cd = 'Pharmacy';

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(0, '\~~Pharmacy\', 'Pharmacy', 'N', 'CA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\', 'Pharmacy', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.TABLE_ACCESS (C_TABLE_CD, C_TABLE_NAME, C_PROTECTED_ACCESS, C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, 
C_VISUALATTRIBUTES, C_FACTTABLECOLUMN, C_DIMTABLENAME, C_COLUMNNAME, C_COLUMNDATATYPE, 
C_OPERATOR, C_DIMCODE, C_TOOLTIP, C_ENTRY_DATE)  
values ('Pharmacy','i2b2','N', 0, '\~~Pharmacy\', 'Pharmacy', 'N', 'CA', 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\', 'Pharmacy', 
'2010-01-01');



insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~A\', 'A', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~A\', 'A', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~B\', 'B', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~B\', 'B', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~C\', 'C', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~C\', 'C', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~D\', 'D', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~D\', 'D', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~E\', 'E', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~E\', 'E', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~F\', 'F', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~F\', 'F', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~G\', 'G', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~G\', 'G', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~H\', 'H', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~H\', 'H', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~I\', 'I', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~I\', 'I', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~J\', 'J', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~J\', 'J', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~K\', 'K', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~K\', 'K', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~L\', 'L', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~L\', 'L', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~M\', 'M', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~M\', 'M', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~N\', 'N', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~N\', 'N', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~O\', 'O', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~O\', 'O', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~P\', 'P', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~P\', 'P', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~Q\', 'Q', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~Q\', 'Q', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~R\', 'R', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~R\', 'R', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~S\', 'S', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~S\', 'S', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~T\', 'T', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~T\', 'T', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~U\', 'U', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~U\', 'U', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~V\', 'V', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~V\', 'V', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~W\', 'W', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~W\', 'W', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~X\', 'X', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~X\', 'X', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~Y\', 'Y', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~Y\', 'Y', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

insert into i2b2metadata.I2B2 (C_HLEVEL, C_FULLNAME, C_NAME, C_SYNONYM_CD, C_VISUALATTRIBUTES, C_BASECODE, C_FACTTABLECOLUMN, C_TABLENAME, C_COLUMNNAME, 
C_COLUMNDATATYPE, C_OPERATOR, C_DIMCODE, C_TOOLTIP, UPDATE_DATE, DOWNLOAD_DATE, IMPORT_DATE, SOURCESYSTEM_CD, CONCEPT_PATH) 
values(1, '\~~Pharmacy\~~~~~~~~~Z\', 'Z', 'N', 'FA', null, 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\~~Pharmacy\~~~~~~~~~Z\', 'Z', 
'2010-01-01','2010-01-01','2010-01-01','Pharmacy',null);

commit;


