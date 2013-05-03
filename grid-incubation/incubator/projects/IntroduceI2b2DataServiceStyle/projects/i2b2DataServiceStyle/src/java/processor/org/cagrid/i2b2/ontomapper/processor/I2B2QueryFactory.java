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
package org.cagrid.i2b2.ontomapper.processor;


public class I2B2QueryFactory {

    // placeholder for table name prefixes in queries
    private static final String PREFIX = "<!--TablePrefix-->";
    // query to get CONCEPT_PATHs from a given CDE number
    private static final String CDE_PATHS = 
        "select cd.concept_path from " + PREFIX + "concept_dimension cd, " + PREFIX + "ENCODING_DIMENSION ed, " 
            + PREFIX + "ENCODING_PROJECT ep, " + PREFIX + "ENCODING_PROJECT_LINK epl, " + PREFIX + "ENCODING_SERVICE es "
            + "where cd.encoding_cd = ed.encoding_cd " 
            + "and ed.encoding_cd = epl.encoding_cd "
            + "and epl.encoding_project_id = ep.encoding_project_id "
            + "and ep.encoding_service_id = es.encoding_service_id "
            + "and ed.cde_public_id = ? and es.service_url = ? and ep.project_name = ? and ep.project_version = ?";
    // fields required to get anything useful out of a fact table
    public static final String VALUE_TYPE_FIELD = "ValType_Cd";
    public static final String TEXT_VALUE_FIELD = "TVal_Char";
    public static final String NUMERIC_VALUE_FIELD = "NVal_Num";
    public static final String ENCOUNTER_NUMBER_FIELD = "Encounter_Num";
    public static final String PATIENT_NUMBER_FIELD = "Patient_Num";
    private static final String REQUIRED_FACT_FIELDS = VALUE_TYPE_FIELD + ", " + TEXT_VALUE_FIELD + ", " + NUMERIC_VALUE_FIELD;
    // optional fields that help correlate data in fact tables
    private static final String OPTIONAL_FACT_FIELDS = ENCOUNTER_NUMBER_FIELD + ", " + PATIENT_NUMBER_FIELD;
    // query for the observation_fact table
    private static final String OBSERVATIONS_BY_PATH_A = 
        "select " + REQUIRED_FACT_FIELDS + ", " + OPTIONAL_FACT_FIELDS + " from " + PREFIX + "observation_fact obs where ";
    private static final String OBSERVATIONS_BY_PATH_COLUMN = "obs.concept_path";
    private static final String OBSERVATIONS_BY_PATH_B = " order by obs.encounter_num, obs.patient_num";
    // query for the map_data_fact table
    private static final String MAP_DATA_BY_PATH_A = 
        "select " + REQUIRED_FACT_FIELDS + ", " + OPTIONAL_FACT_FIELDS + " from " + PREFIX + "map_data_fact mdf where ";
    private static final String MAP_DATA_BY_PATH_COLUMN = "mdf.concept_path";
    private static final String MAP_DATA_BY_PATH_B = " order by mdf.encounter_num, mdf.patient_num";
    // query for map_aggr_fact table (no optional fields here)
    private static final String MAP_AGGR_BY_PATH_A = 
        "select " + REQUIRED_FACT_FIELDS + " from " + PREFIX + "map_aggr_fact maf where ";
    private static final String MAP_AGGR_BY_PATH_COLUMN = "maf.concept_path";
    private static final String MAP_AGGR_BY_PATH_B = " order by maf.map_id"; // TODO: check this field
    // query for encoding service URLs
    private static final String ENCODING_SERVICE_URL = "select service_url from " + PREFIX + "encoding_service";
    
    private String databasePrefix = null;
    
    /**
     * Creates a new i2b2 query factory using the given prefix
     * for table names in the underlying database
     * 
     * @param databasePrefix
     *      The prefix for table names in generated SQL queries.  May be null.
     */
    public I2B2QueryFactory(String databasePrefix) {
        this.databasePrefix = databasePrefix;
    }
    
    
    public String getCdePathsQuery() {
        return replacePrefixes(CDE_PATHS);
    }
    
    
    public String getObservationsByPathQuery(int pathCount) {
        StringBuffer query = new StringBuffer();
        query.append(replacePrefixes(OBSERVATIONS_BY_PATH_A));
        String clause = getParameterizedOrClause(OBSERVATIONS_BY_PATH_COLUMN, "=", pathCount);
        query.append(clause);
        query.append(replacePrefixes(OBSERVATIONS_BY_PATH_B));
        return query.toString();
    }
    
    
    public String getMapDataByPathQuery(int pathCount) {
        StringBuffer query = new StringBuffer();
        query.append(replacePrefixes(MAP_DATA_BY_PATH_A));
        String clause = getParameterizedOrClause(MAP_DATA_BY_PATH_COLUMN, "=", pathCount);
        query.append(clause);
        query.append(replacePrefixes(MAP_DATA_BY_PATH_B));
        return query.toString();
    }
    
    
    public String getMapAggrByPathQuery(int pathCount) {
        StringBuffer query = new StringBuffer();
        query.append(replacePrefixes(MAP_AGGR_BY_PATH_A));
        String clause = getParameterizedOrClause(MAP_AGGR_BY_PATH_COLUMN, "=", pathCount);
        query.append(clause);
        query.append(replacePrefixes(MAP_AGGR_BY_PATH_B));
        return query.toString();
    }
    
    
    public String getEncodingServiceQuery() {
        return replacePrefixes(ENCODING_SERVICE_URL);
    }
    
    
    private String getParameterizedOrClause(String columnId, String prefix, int items) {
        StringBuffer clause = new StringBuffer();
        for (int i = 0; i < items; i++) {
            clause.append(columnId).append(' ').append(prefix).append(" ?");
            if (i + 1 < items) {
                clause.append(" or ");
            }
        }
        return clause.toString();
    }
    
    
    private String replacePrefixes(String query) {
        // if null prefix, replace with an empty string
        String replacement = databasePrefix != null ? databasePrefix : "";
        String editedQuery = query.replace(PREFIX, replacement);
        return editedQuery;
    }
}
