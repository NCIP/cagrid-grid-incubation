/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.i2b2.ontomapper.query;



public class Queries {
    
    // placeholder for table name prefixes in queries
    private static final String PREFIX = "<!--TablePrefix-->";
    
    public static final String CONCEPT_QUERY = 
        "SELECT " +
        "<!--TablePrefix-->CONCEPT_DIMENSION.CONCEPT_PATH," +
        "<!--TablePrefix-->CONCEPT_DIMENSION.NAME_CHAR," +
        "<!--TablePrefix-->ENCODING_DIMENSION.CDE_PUBLIC_ID," +
        "<!--TablePrefix-->ENCODING_DIMENSION.CDE_VERSION," +
        "<!--TablePrefix-->ENCODING_PROJECT.PROJECT_NAME," +
        "<!--TablePrefix-->ENCODING_PROJECT.PROJECT_VERSION," +
        "<!--TablePrefix-->ENCODING_SERVICE.SERVICE_URL " +
        "FROM " +
        "<!--TablePrefix-->CONCEPT_DIMENSION " +
        "INNER JOIN <!--TablePrefix-->ENCODING_DIMENSION " +
        "ON (<!--TablePrefix-->CONCEPT_DIMENSION.ENCODING_CD = <!--TablePrefix-->ENCODING_DIMENSION.ENCODING_CD) " +
        "INNER JOIN <!--TablePrefix-->ENCODING_PROJECT_LINK " +
        "ON (<!--TablePrefix-->ENCODING_DIMENSION.ENCODING_CD = <!--TablePrefix-->ENCODING_PROJECT_LINK.ENCODING_CD) " +
        "INNER JOIN <!--TablePrefix-->ENCODING_PROJECT " +
        "ON (<!--TablePrefix-->ENCODING_PROJECT_LINK.ENCODING_PROJECT_ID = <!--TablePrefix-->ENCODING_PROJECT.ENCODING_PROJECT_ID) " +
        "INNER JOIN <!--TablePrefix-->ENCODING_SERVICE " +
        "ON (<!--TablePrefix-->ENCODING_PROJECT.ENCODING_SERVICE_ID = <!--TablePrefix-->ENCODING_SERVICE.ENCODING_SERVICE)";
    
    public static final String OBSERVATION_QUERY = 
        "SELECT " +
        "<!--TablePrefix-->OBSERVATION_FACT.NVAL_NUM, " +
        "<!--TablePrefix-->OBSERVATION_FACT.TVAL_CHAR, " +
        "<!--TablePrefix-->OBSERVATION_FACT.VALUEFLAG_CD, " +
        "<!--TablePrefix-->OBSERVATION_FACT.QUANTITY_NUM, " +
        "<!--TablePrefix-->OBSERVATION_FACT.UNITS_CD, " +
        "<!--TablePrefix-->OBSERVATION_FACT.END_DATE, " +
        "<!--TablePrefix-->OBSERVATION_FACT.LOCATION_CD, " +
        "<!--TablePrefix-->OBSERVATION_FACT.CONFIDENCE_NUM, " +
        "<!--TablePrefix-->OBSERVATION_FACT.UPDATE_DATE, " +
        "<!--TablePrefix-->OBSERVATION_FACT.DOWNLOAD_DATE, " +
        "<!--TablePrefix-->OBSERVATION_FACT.IMPORT_DATE, " +
        "<!--TablePrefix-->OBSERVATION_FACT.SOURCESYSTEM_CD " +
        "FROM " +
        "<!--TablePrefix-->OBSERVATION_FACT";
    
    public static final String PATIENT_QUERY = 
        "SELECT " +
        "<!--TablePrefix-->PATIENT_DIMENSION.VITAL_STATUS_CD, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.BIRTH_DATE, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.DEATH_DATE, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.SEX_CD, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.AGE_IN_YEARS_NUM, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.LANGUAGE_CD, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.RACE_CD, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.MARITAL_STATUS_CD, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.RELIGION_CD, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.ZIP_CD, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.STATECITYZIP_PATH, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.UPDATE_DATE, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.DOWNLOAD_DATE, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.IMPORT_DATE, " +
        "<!--TablePrefix-->PATIENT_DIMENSION.SOURCESYSTEM_CD " +
        "FROM " +
        "<!--TablePrefix-->PATIENT_DIMENSION";
    
    public static final String PROVIDER_QUERY = 
        "SELECT " +
        "I2B2DEMODATA.PROVIDER_DIMENSION.NAME_CHAR, " +
        "I2B2DEMODATA.PROVIDER_DIMENSION.UPDATE_DATE, " +
        "I2B2DEMODATA.PROVIDER_DIMENSION.DOWNLOAD_DATE, " +
        "I2B2DEMODATA.PROVIDER_DIMENSION.IMPORT_DATE, " +
        "I2B2DEMODATA.PROVIDER_DIMENSION.SOURCESYSTEM_CD " +
        "FROM " +
        "I2B2DEMODATA.PROVIDER_DIMENSION";
    
    public static final String VISIT_QUERY = 
        "SELECT " +
        "I2B2DEMODATA.VISIT_DIMENSION.ACTIVE_STATUS_CD, " +
        "I2B2DEMODATA.VISIT_DIMENSION.START_DATE, " +
        "I2B2DEMODATA.VISIT_DIMENSION.END_DATE, " +
        "I2B2DEMODATA.VISIT_DIMENSION.INOUT_CD, " +
        "I2B2DEMODATA.VISIT_DIMENSION.LOCATION_CD, " +
        "I2B2DEMODATA.VISIT_DIMENSION.LOCATION_PATH, " +
        "I2B2DEMODATA.VISIT_DIMENSION.UPDATE_DATE, " +
        "I2B2DEMODATA.VISIT_DIMENSION.DOWNLOAD_DATE, " +
        "I2B2DEMODATA.VISIT_DIMENSION.IMPORT_DATE, " +
        "I2B2DEMODATA.VISIT_DIMENSION.SOURCESYSTEM_CD " +
        "FROM " +
        "I2B2DEMODATA.VISIT_DIMENSION";
    
    public static String getConceptQuery(String tablePrefix) {
        return replacePrefixes(CONCEPT_QUERY, tablePrefix);
    }
    
    
    public static String getObservationQuery(String tablePrefix) {
        return replacePrefixes(OBSERVATION_QUERY, tablePrefix);
    }
    
    
    public static String getPatientQuery(String tablePrefix) {
        return replacePrefixes(PATIENT_QUERY, tablePrefix);
    }
    
    
    public static String getProviderQuery(String tablePrefix) {
        return replacePrefixes(PROVIDER_QUERY, tablePrefix);
    }
    
    
    public static String getVisitQuery(String tablePrefix) {
        return replacePrefixes(PROVIDER_QUERY, tablePrefix);
    }
    
    
    private static String replacePrefixes(String query, String tablePrefix) {
        // if null prefix, replace with an empty string
        String replacement = tablePrefix != null ? tablePrefix : "";
        String editedQuery = query.replace(PREFIX, replacement);
        return editedQuery;
    }
}
