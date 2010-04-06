package org.cagrid.i2b2.ontomapper.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.cagrid.i2b2.domain.Concept;
import org.cagrid.i2b2.ontomapper.processor.DatabaseConnectionSource;

public class DatatypeExtractor {
    
    private DatabaseConnectionSource connectionSource = null;
    private String tablePrefix = null;
    
    
    public DatatypeExtractor(DatabaseConnectionSource connectionSource, String tablePrefix) {
        this.connectionSource = connectionSource;
        this.tablePrefix = tablePrefix;
    }
    
    
    public List<Concept> getConcepts(List<QueryColumnCriteria> criteria) throws SQLException {
        List<Concept> concepts = new ArrayList<Concept>();
        // get the base query and a blank list of parameters
        String query = Queries.getConceptQuery(tablePrefix);
        List<Object> parameters = new ArrayList<Object>();
        if (criteria != null && criteria.size() != 0) {
            // if some criteria are to be applied, 
            // do it with a where clause and positional parameters
            ParameterizedSqlFragment whereClause = criteriaToClause(criteria, "AND");
            query = query + " where " + whereClause.getSql();
            parameters = whereClause.getParameters();
        }
        // get a connection
        Connection connection = connectionSource.getConnection();
        // prepare a statement w/ positional parameters
        PreparedStatement statement = connection.prepareStatement(query);
        // set all the positional parameters
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
        // execute
        ResultSet results = statement.executeQuery();
        // build up the concept object from returned columns
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
        // close out everything
        results.close();
        statement.close();
        connection.close();
        // wrap the results as an unmodifiable list so users don't try
        // to mess with the results and have unexpected things happen
        return Collections.unmodifiableList(concepts);
    }
    
    
    private ParameterizedSqlFragment criteriaToClause(List<QueryColumnCriteria> criteria, String operator) {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        Iterator<QueryColumnCriteria> criteriaIter = criteria.iterator();
        while (criteriaIter.hasNext()) {
            QueryColumnCriteria c = criteriaIter.next();
            if (tablePrefix != null) {
                sql.append(tablePrefix);
            }
            sql.append(c.getTableName()).append(".").append(c.getColumnName())
                .append(" ").append(c.getPredicate()).append(" ?");
            params.add(c.getValue());
            if (criteriaIter.hasNext()) {
                sql.append(" ").append(operator).append(" ");
            }
        }
        return new ParameterizedSqlFragment(sql.toString(), params);
    }
}
