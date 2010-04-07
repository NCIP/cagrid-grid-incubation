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
    
    public static String getConceptQuery(String tablePrefix) {
        return replacePrefixes(CONCEPT_QUERY, tablePrefix);
    }
    
    
    public static String getObservationQuery(String tablePrefix) {
        return replacePrefixes(OBSERVATION_QUERY, tablePrefix);
    }
    
    
    private static String replacePrefixes(String query, String tablePrefix) {
        // if null prefix, replace with an empty string
        String replacement = tablePrefix != null ? tablePrefix : "";
        String editedQuery = query.replace(PREFIX, replacement);
        return editedQuery;
    }
}
