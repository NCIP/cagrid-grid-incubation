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

import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.data.QueryProcessingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.utils.AttributeNotFoundInModelException;
import org.cagrid.i2b2.ontomapper.utils.CdeIdMapper;
import org.cagrid.i2b2.ontomapper.utils.ClassNotFoundInModelException;

/**
 * I2B2DataAccessManager
 * Handles data access requests for the i2b2QueryProcessor
 * 
 * @author David
 */
public class I2B2DataAccessManager {
    
    private static final Log LOG = LogFactory.getLog(I2B2DataAccessManager.class);
    
    private DatabaseConnectionSource connectionSource = null;
    private CdeIdMapper cdeIdMapper = null;
    private String encodingServiceUrl = null;
    private I2B2QueryFactory queryFactory = null;
    
    public I2B2DataAccessManager(DatabaseConnectionSource connectionSource, CdeIdMapper cdeIdMapper, 
        String encodingServiceUrl, I2B2QueryFactory queryFactory) {
        this.connectionSource = connectionSource;
        this.cdeIdMapper = cdeIdMapper;
        this.encodingServiceUrl = encodingServiceUrl;
        this.queryFactory = queryFactory;
    }
    
    
    /**
     * Gets all attribute values from the various fact data tables for a given class
     * 
     * @param className
     *      The name of the class for which to obtain fact data
     * @return
     *      A Map from Attribute name to a list of fact data entries.  The order of
     *      these entries corresponds to the order in which objects are found in
     *      the database.  All lists are of the same length.
     * @throws QueryProcessingException
     */
    public Map<String, List<FactDataEntry>> getAttributeValues(String className) throws QueryProcessingException {
        // get all the attribute names and their associated CDEs
        Map<String, Long> attributeCdes = null;
        try {
            attributeCdes = cdeIdMapper.getCdeIdsForClass(className);
        } catch (ClassNotFoundInModelException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new QueryProcessingException(ex);
        }
        
        // query for fact data entries for each attribute
        Map<String, List<FactDataEntry>> attributeEntries = new HashMap<String, List<FactDataEntry>>();
        for (String attributeName : attributeCdes.keySet()) {
            Long cde = attributeCdes.get(attributeName);
            List<String> paths = getPathsForCde(cde);
            LinkedList<FactDataEntry> entries = new LinkedList<FactDataEntry>();
            if (paths.size() != 0) {
                entries.addAll(getFactEntriesByPaths(DatabaseFactTable.OBSERVATION, paths));
                entries.addAll(getFactEntriesByPaths(DatabaseFactTable.MAP_DATA, paths));
                // TODO: this won't have encounter_num or patient_num in it, so we probably have to omit it
                // entries.addAll(getFactEntriesByPaths(DatabaseFactTable.MAP_AGGREGATE, paths));
            }
            attributeEntries.put(attributeName, entries);
        }
        
        return attributeEntries;
    }
    
    
    /**
     * Gets attribute values of a given data type restricted by a map
     * of required attribute values
     * 
     * @param className
     *      The name of the data type
     * @param restrictAttributes
     *      A map from attribute name to a list of values which must be satisfied
     * @param operator
     *      The logical operation to perform on the attribute values' presence
     * @return
     * @throws QueryProcessingException
     */
    public Map<String, List<FactDataEntry>> getAttributeValues(String className, Map<String, List<String>> restrictAttributes, LogicalOperator operator) throws QueryProcessingException {
        // TODO: this
        return null;
    }
    
    
    /**
     * Gets a list of values of an attribute of a data type
     * 
     * @param className
     *      The class name of the data type
     * @param attributeName
     *      The name of the attribute of that data type
     * @return
     * @throws QueryProcessingException
     */
    public List<Object> getAttributeValues(String className, String attributeName) throws QueryProcessingException {
        // get fact table entries
        List<FactDataEntry> entries = getAttribteEntries(className, attributeName);
        
        // convert entries to object values
        List<Object> values = new ArrayList<Object>(entries.size());
        try {
            for (FactDataEntry entry : entries) {
                values.add(entry.getTypedValue());
            }
        } catch (ParseException ex) {
            String message = "Error getting typed value from a fact table entry: " + ex.getMessage();
            LOG.error(message, ex);
            throw new QueryProcessingException(message, ex);
        }
        return values;
    }
    
    
    public List<String> getAttributeStringValues(String className, String attributeName) throws QueryProcessingException{
        // get fact table entries
        List<FactDataEntry> entries = getAttribteEntries(className, attributeName);
        
        // convert entries to string values
        List<String> values = new ArrayList<String>(entries.size());
        for (FactDataEntry entry : entries) {
            values.add(entry.getActualValueAsString());
        }
        return values;
    }
    
    
    private List<FactDataEntry> getAttribteEntries(String className, String attributeName) throws QueryProcessingException {
        // figure out the CDE
        Long cde = getCdeForAttribute(className, attributeName);
        LOG.debug("CDE for " + className + "." + attributeName + " is " + String.valueOf(cde));
        if (cde == null) {
            throw new QueryProcessingException("No CDE found for " + className + "." + attributeName);
        }
        
        // get query paths for the CDE
        List<String> paths = getPathsForCde(cde);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Paths for CDE " + cde + ":");
            for (String path : paths) {
                LOG.debug("\t" + path);
            }
        }
        
        // get a list of all entries
        List<FactDataEntry> entries = new LinkedList<FactDataEntry>();
        if (paths.size() != 0) {
            // get observation instances from the DB based on each query path
            entries.addAll(getFactEntriesByPaths(DatabaseFactTable.OBSERVATION, paths));
            entries.addAll(getFactEntriesByPaths(DatabaseFactTable.MAP_DATA, paths));
            entries.addAll(getFactEntriesByPaths(DatabaseFactTable.MAP_AGGREGATE, paths));
        }
        return entries;
    }
    
    
    private Long getCdeForAttribute(String className, String attributeName) throws QueryProcessingException {
        // get the CDE of the attribute
        LOG.debug("Looking up CDE in id mapper");
        Long cde = null;
        try {
            cde = cdeIdMapper.getCdeIdForAttribute(className, attributeName);
        } catch (ClassNotFoundInModelException ex) {
            LOG.error(ex);
            throw new QueryProcessingException(ex.getMessage(), ex);
        } catch (AttributeNotFoundInModelException ex) {
            LOG.error(ex);
            throw new QueryProcessingException(ex.getMessage(), ex);
        }
        // CDE has to exist
        if (cde == null) {
            throw new QueryProcessingException("No CDE found for attribute " 
                + className + "." + attributeName);
        }
        return cde;
    }
    
    
    private List<FactDataEntry> getFactEntriesByPaths(DatabaseFactTable table, List<String> paths) throws QueryProcessingException {
        List<FactDataEntry> entries = null;
        String parameterisedSql = null;
        switch (table) {
            case OBSERVATION:
                parameterisedSql = queryFactory.getObservationsByPathQuery(paths.size());
                break;
            case MAP_DATA:
                parameterisedSql = queryFactory.getMapDataByPathQuery(paths.size());
                break;
            case MAP_AGGREGATE:
                parameterisedSql = queryFactory.getMapAggrByPathQuery(paths.size());
                break;
        }
        Connection dbConnection = null;
        PreparedStatement statement = null;
        ResultSet results = null;
        try {
            dbConnection = connectionSource.getConnection();
            statement = dbConnection.prepareStatement(parameterisedSql);
            int index = 1;
            for (String path : paths) {
                statement.setString(index, path);
                index++;
            }
            results = statement.executeQuery();
            entries = convertResultsToEntries(results);
        } catch (SQLException ex) {
            String message = "Error querying for fact data by CDE paths: " + ex.getMessage();
            LOG.error(message, ex);
            LOG.error("SQL:\n" + parameterisedSql);
            throw new QueryProcessingException(message, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing result set: " + ex.getMessage(), ex);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing statement: " + ex.getMessage(), ex);
                }
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException ex) {
                    LOG.error("Error releasing DB connection: " + ex.getMessage(), ex);
                }
            }
        }
        return entries;
    }
    
    
    /**
     * Gets the concept paths in the I2B2 database
     * which are mapped to a caBIG CDE
     * 
     * @param cde
     *      The CDE id
     * @return
     * @throws QueryProcessingException
     */
    private List<String> getPathsForCde(Long cde) throws QueryProcessingException {
        LOG.debug("Looking up paths for CDE " + cde);
        List<String> paths = new LinkedList<String>();
        Connection dbConnection = null;
        PreparedStatement pathsStatement = null;
        ResultSet pathsResult = null;
        String projectName = cdeIdMapper.getProjectShortName();
        String projectVersion = cdeIdMapper.getProjectVersion();
        try {
            dbConnection = connectionSource.getConnection();
            pathsStatement = dbConnection.prepareStatement(queryFactory.getCdePathsQuery());
            pathsStatement.setInt(1, cde.intValue());
            pathsStatement.setString(2, encodingServiceUrl);
            pathsStatement.setString(3, projectName);
            pathsStatement.setString(4, projectVersion);
            pathsResult = pathsStatement.executeQuery();
            while (pathsResult.next()) {
                paths.add(pathsResult.getString(1));
            }
        } catch (SQLException ex) {
            String message = "Error querying for CDE paths: " + ex.getMessage();
            LOG.error(message, ex);
            throw new QueryProcessingException(message, ex);
        } finally {
            if (pathsResult != null) {
                try {
                    pathsResult.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing result set: " + ex.getMessage(), ex);
                }
            }
            if (pathsStatement != null) {
                try {
                    pathsStatement.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing statement: " + ex.getMessage(), ex);
                }
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException ex) {
                    LOG.error("Error releasing DB connection: " + ex.getMessage(), ex);
                }
            }
        }
        return paths;
    }
    
    
    private List<FactDataEntry> convertResultsToEntries(ResultSet results) throws SQLException, QueryProcessingException {
        List<FactDataEntry> entries = new LinkedList<FactDataEntry>();
        ResultSetMetaData metadata = results.getMetaData();
        int columnCount = metadata.getColumnCount();
        if (!(columnCount == 3 || columnCount == 5)) {
            String message = "Unexpected number of columns in result set: " + metadata.getColumnCount();
            LOG.error(message);
            throw new QueryProcessingException(message);
        }
        while (results.next()) {
            FactDataEntry entry = null;
            String valueType = results.getString(I2B2QueryFactory.VALUE_TYPE_FIELD);
            String textValue = results.getString(I2B2QueryFactory.TEXT_VALUE_FIELD);
            String numValueString = results.getString(I2B2QueryFactory.NUMERIC_VALUE_FIELD);
            Double numValue = numValueString != null ? Double.valueOf(numValueString) : null;
            if (columnCount == 5) {
                String encounterNumString = results.getString(I2B2QueryFactory.ENCOUNTER_NUMBER_FIELD);
                String patientNumString = results.getString(I2B2QueryFactory.PATIENT_NUMBER_FIELD);
                Integer encounterNum = encounterNumString != null ? Integer.valueOf(encounterNumString) : null;
                Integer patientNum = patientNumString != null ? Integer.valueOf(patientNumString) : null;
                entry = new FactDataEntry(valueType, textValue, numValue, encounterNum, patientNum);
            } else {
                entry = new FactDataEntry(valueType, textValue, numValue);
            }
            entries.add(entry);
        }
        return entries;
    }
}
