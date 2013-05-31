/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.i2b2.ontomapper.utils;

import java.util.ArrayList;
import java.util.List;

public class TableNames {
    
    public static final String[] I2B2DEMODATA = {
        "ARCHIVE_OBSERVATION_FACT", "CODE_LOOKUP", "concept_dimension", "CONCEPT_DIMENSION", 
        "CRC_DB_LOOKUP", "DATA_REQUEST", "DATAMART_REPORT", "Dept", "ENCODING_DIMENSION", 
        "ENCODING_PROJECT", "ENCODING_PROJECT_LINK", "ENCODING_SERVICE", "Encounter_Mapping", 
        "HILOSEQUENCES", "IRB_APPROVAL_TYPE", "JMS_MESSAGES", "JMS_ROLES", "JMS_SUBSCRIPTIONS", 
        "JMS_TRANSACTIONS", "JMS_USERS", "MAP_AGGR_FACT", "MAP_DATA_FACT", "MAP_DIMENSION", "MAP_REQUEST",
        "Observation_Fact", "Patient_Dimension", "Patient_Mapping", "Provider_Dimension", 
        "QT_PATIENT_ENC_COLLECTION", "QT_PATIENT_SET_COLLECTION", "QT_QUERY_INSTANCE",
        "QT_QUERY_MASTER", "QT_QUERY_RESULT_INSTANCE", "QT_QUERY_RESULT_TYPE", "QT_QUERY_STATUS_TYPE",
        "QT_XML_RESULT", "QUERY_CRITERIA", "QUERY_DIMENSION", "REQUEST_STATUS", "RESEARCH_TYPE",
        "rhw", "rules", "SET_TYPE", "SET_UPLOAD_STATUS", "SOURCE_MASTER", "STUDY_CRITERIA",
        "TIMERS", "UPLOAD_STATUS", "USER_INFO", "Visit_Dimension"
    };
    
    public static final String[] I2B2HIVE = {
        "CRC_DB_LOOKUP", "JMS_MESSAGES", "JMS_ROLES", "JMS_SUBSCRIPTIONS",
        "JMS_TRANSACTIONS", "JMS_USERS", "ONT_DB_LOOKUP", "WORK_DB_LOOKUP"
    };
    
    public static final String[] I2B2METADATA = {
        "BIRN", "I2B2", "SCHEMES", "TABLE_ACCESS"
    };
    
    public static final String[] I2B2WORKDATA = {
        "WORKPLACE", "WORKPLACE_ACCESS"
    };
    
    
    public static List<String> allTableNames = null;
    static {
        allTableNames = new ArrayList<String>();
        for (String name : I2B2DEMODATA) {
            allTableNames.add("i2b2demodata." + name);
        }
        for (String name : I2B2HIVE) {
            allTableNames.add("i2b2hive." + name);
        }
        for (String name : I2B2METADATA) {
            allTableNames.add("i2b2metadata." + name);
        }
        for (String name : I2B2WORKDATA) {
            allTableNames.add("i2b2workdata." + name);
        }
    }
}
