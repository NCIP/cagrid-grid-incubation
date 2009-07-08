package org.cagrid.i2b2.ontomapper.processor;


public class I2B2QueryFactory {

    
    //Placeholder for table name prefixes in queries
    private static final String HOLDER = "<!--TablePrefix-->";
    private static final String CDE_PATHS = 
        "select cd.concept_path from " + HOLDER + "concept_dimension cd, " + HOLDER + "ENCODING_DIMENSION ed, " 
            + HOLDER + "ENCODING_PROJECT ep, " + HOLDER + "ENCODING_PROJECT_LINK epl, " + HOLDER + "ENCODING_SERVICE es "
            + "where cd.encoding_cd = ed.encoding_cd " 
            + "and ed.encoding_cd = epl.encoding_cd "
            + "and epl.encoding_project_id = ep.encoding_project_id "
            + "and ep.encoding_service_id = es.encoding_service_id "
            + "and ed.cde_public_id = ? and es.service_url = ? and ep.project_name = ? and ep.project_version = ?";
    
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
        String editedQuery = query.replace(HOLDER, replacement);
        return editedQuery;
    }
}
