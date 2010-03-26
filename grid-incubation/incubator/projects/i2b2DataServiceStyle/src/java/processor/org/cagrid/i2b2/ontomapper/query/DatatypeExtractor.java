package org.cagrid.i2b2.ontomapper.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cagrid.i2b2.domain.Concept;
import org.cagrid.i2b2.ontomapper.processor.DatabaseConnectionSource;

public class DatatypeExtractor {
    
    // placeholder for table name prefixes in queries
    private static final String PREFIX = "<!--TablePrefix-->";

    private DatabaseConnectionSource connectionSource = null;
    private String tablePrefix = null;
    
    
    public DatatypeExtractor(DatabaseConnectionSource connectionSource, String tablePrefix) {
        this.connectionSource = connectionSource;
        this.tablePrefix = tablePrefix;
    }
    
    
    public List<Concept> getConcepts(List<QueryColumnCriteria> criteria) throws SQLException {
        List<Concept> concepts = new ArrayList<Concept>();
        Connection connection = connectionSource.getConnection();
        String query = replacePrefixes(Queries.CONCEPT_QUERY);
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet results = statement.executeQuery();
        while(results.next()) {
            Concept concept = new Concept();
            concept.setConceptPath(results.getString(1));
            concept.setName(results.getString(2));
            concept.setCdePublicId(Double.valueOf(results.getDouble(3)));
            concept.setCdeVersion(results.getString(4));
            concept.setProjectName(results.getString(5));
            concept.setProjectVersion(Double.valueOf(results.getDouble(6)));
            concept.setEncodingServiceURL(results.getString(7));
            concepts.add(concept);
        }
        connection.close();
        return Collections.unmodifiableList(concepts);
    }
    
    
    private String replacePrefixes(String query) {
        // if null prefix, replace with an empty string
        String replacement = tablePrefix != null ? tablePrefix : "";
        String editedQuery = query.replace(PREFIX, replacement);
        return editedQuery;
    }
}
