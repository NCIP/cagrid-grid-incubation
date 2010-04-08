package org.cagrid.i2b2.ontomapper.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.cagrid.i2b2.domain.Concept;
import org.cagrid.i2b2.domain.Observation;
import org.cagrid.i2b2.domain.Patient;
import org.cagrid.i2b2.domain.Provider;
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
        ResultSet results = executeQuery(query, criteria);
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
        closeResultSet(results);
        // wrap the results as an unmodifiable list so users don't try
        // to mess with the results and have unexpected things happen
        return Collections.unmodifiableList(concepts);
    }
    
    
    public List<Observation> getObservations(List<QueryColumnCriteria> criteria) throws SQLException {
        List<Observation> observations = new ArrayList<Observation>();
        String query = Queries.getObservationQuery(tablePrefix);
        ResultSet results = executeQuery(query, criteria);
        while (results.next()) {
            Observation obs = new Observation();
            obs.setNumericValue(results.getDouble(1));
            obs.setTextValue(results.getString(2));
            obs.setValueFlag(results.getString(3));
            obs.setQuantity(results.getDouble(4));
            obs.setUnits(results.getString(5));
            obs.setEndDate(results.getDate(6));
            obs.setLocation(results.getString(7));
            obs.setConfidence(results.getDouble(8));
            obs.setUpdateDate(results.getDate(9));
            obs.setDownloadDate(results.getDate(10));
            obs.setImportDate(results.getDate(11));
            obs.setSourceSystemCd(results.getString(12));
            observations.add(obs);
        }
        closeResultSet(results);
        return Collections.unmodifiableList(observations);
    }
    
    
    public List<Patient> getPatients(List<QueryColumnCriteria> criteria) throws SQLException {
        List<Patient> patients = new ArrayList<Patient>();
        String query = Queries.getPatientQuery(tablePrefix);
        ResultSet results = executeQuery(query, criteria);
        while (results.next()) {
            Patient p = new Patient();
            p.setVitalStatus(results.getString(1));
            p.setBirthDate(results.getDate(2));
            p.setDeathDate(results.getDate(3));
            p.setSex(results.getString(4));
            p.setAgeInYears(results.getInt(5));
            p.setLanguage(results.getString(6));
            p.setRace(results.getString(7));
            p.setMaritalStatus(results.getString(8));
            p.setZip(results.getString(9));
            p.setCityStateZipPath(results.getString(10));
            p.setUpdateDate(results.getDate(11));
            p.setDownloadDate(results.getDate(12));
            p.setImportDate(results.getDate(13));
            p.setSourceSystemCd(results.getString(14));
            patients.add(p);
        }
        closeResultSet(results);
        return Collections.unmodifiableList(patients);
    }
    
    
    public List<Provider> getProviders(List<QueryColumnCriteria> criteria) throws SQLException {
        List<Provider> providers = new ArrayList<Provider>();
        String query = Queries.getProviderQuery(tablePrefix);
        ResultSet results = executeQuery(query, criteria);
        while (results.next()) {
            Provider p = new Provider();
            p.setName(results.getString(1));
            p.setUpdateDate(results.getDate(2));
            p.setDownloadDate(results.getDate(3));
            p.setImportDate(results.getDate(4));
            p.setSourceSystemCd(results.getString(5));
            providers.add(p);
        }
        closeResultSet(results);
        return Collections.unmodifiableList(providers);
    }
    
    
    private ResultSet executeQuery(String baseQuery, List<QueryColumnCriteria> criteria) throws SQLException {
        String query = baseQuery;
        List<Object> parameters = new ArrayList<Object>();
        if (criteria != null && criteria.size() != 0) {
            // if some criteria are to be applied, 
            // do it with a where clause and positional parameters
            ParameterizedSqlFragment whereClause = criteriaToClause(criteria, "AND");
            query = query + " where " + whereClause.getSql();
            parameters = whereClause.getParameters();
        }
        query += ";";
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
        return results;
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
    
    
    private void closeResultSet(ResultSet res) throws SQLException {
        if (res.getStatement() != null) {
            Statement s = res.getStatement();
            if (s.getConnection() != null) {
                s.getConnection().close();
            }
            s.close();
        }
        res.close();
    }
}
