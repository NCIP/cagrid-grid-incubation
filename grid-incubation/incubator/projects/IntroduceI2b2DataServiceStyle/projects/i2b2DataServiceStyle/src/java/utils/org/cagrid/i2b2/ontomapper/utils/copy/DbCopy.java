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
package org.cagrid.i2b2.ontomapper.utils.copy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;


public class DbCopy {
    
    // JDBC for MySQL: jdbc:mysql://<server>:<port>/database
    // JDBC for Sybase w/ JTDS: jdbc:sybase:Tds://<server>:<port>/database
        
    // tables that won't go in for various reasons:
    // map_aggr_fact: duplicate primary key
    // -- the UCSF database doesn't have a PK on this table
    // -- edited schema, imported data
    // map_data_fact: duplicate primary key (10000001-1001-\beefydinky\Glycemic_Med_Name-0-2009-03-11-1-\i2b2)
    // -- the UCSF database doesn't have a PK on this table
    // -- edited schema, imported data
    // observation_fact: Column 'OBSERVATION_FACT_ID' cannot be null
    // observation_fact: Column 'CONCEPT_PATH' cannot be null
    // observation_fact: Column 'LOCATION_CD' cannot be null
    // observation_fact: Data too long for column 'PROVIDER_ID' at row 1
    // observation_fact: Data too long for column 'LOCATION_CD' at row 1
    // -- the UCSF database allows nulls on all of these
    // -- the UCSF database made PROVIDER_ID (and many others) varchar(50)
    // -- edited the schema, imported the data
    // provider_dimension: duplicate primary key (BWH\Fellows, Residents and Interns\Owen, Chertow\)
    // -- the UCSF database doesn't have a PK on this table
    // -- edited the schema, imported the data
    // user_info: duplicate primary key (Test)
    // -- Despite this error, the UCSF database DOES define this PK.  Thats bad.
    // -- It'a case sensitivity on Linux (UCSF) vs not on windows (Me).  'test' != 'Test', except when it does.
    // -- added 'COLATE latin1_general_cs' to the table, imported the data
    // -- went ahead and added COLATE latin1_general_cs to all the tables
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // input database
        /* UCSF i2b2 demo database */
        String inputDb = "jdbc:sybase:Tds:192.168.24.21:2638/i2b2rc4";
        String inputUser = "DBA";
        String inputPasswd = "SQL";
        
        /*
        String inputDb = "jdbc:mysql://localhost:3306/i2b2_alpha";
        String inputUser = "root";
        String inputPasswd = "";
        */
        
        // output database
        String outputDb = "jdbc:mysql://localhost:3306/i2b2_beta";
        String outputUser = "root";
        String outputPasswd = "";
        
        // set print every line??
        boolean printEverything = false;
        
        // what table are we copying
        String tableName = "encoding_project";
        try {
            // load the sybase driver
            Class.forName(com.sybase.jdbc3.jdbc.SybDriver.class.getName());
            // and the mysql driver
            Class.forName(com.mysql.jdbc.Driver.class.getName());
            
            // connect to input database
            Connection inputConnection = DriverManager.getConnection(inputDb, inputUser, inputPasswd);
            System.out.println("Got a connection to the input database");
            
            // connect to database beta
            Connection outputConnection = DriverManager.getConnection(outputDb, outputUser, outputPasswd);
            System.out.println("Got a connection to the output database");
            
            // query for * from the table in input
            Statement inputStatement = inputConnection.createStatement();
            ResultSet inputResults = inputStatement.executeQuery("select * from i2b2demodata." + tableName);
            ResultSetMetaData inputMetadata = inputResults.getMetaData();
            
            // NOTE: From here on out, it gets weird.  JDBC uses 1 indexed lists / arrays, 
            // not 0 indexed like everything else in every programming language and convention ever
            int columns = inputMetadata.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                System.out.print(inputMetadata.getColumnName(i));
                if (i + 1 <= columns) {
                    System.out.print("\t\t");
                }
            }
            System.out.println();
            System.out.println("-------------");
            
            // create a prepared statement for inserting data
            System.out.println("Source table has " + inputMetadata.getColumnCount() + " columns");
            StringBuffer insertSql = new StringBuffer();
            insertSql.append("insert into " + tableName + " (");
            for (int i = 0; i < inputMetadata.getColumnCount(); i++) {
                insertSql.append(inputMetadata.getColumnName(i + 1));
                if (i + 1 < inputMetadata.getColumnCount()) {
                    insertSql.append(", ");
                }
            }
            insertSql.append(") values(");
            for (int i = 0; i < inputMetadata.getColumnCount(); i++) {
                insertSql.append("?");
                if (i + 1 < inputMetadata.getColumnCount()) {
                    insertSql.append(", ");
                }
            }
            insertSql.append(")");
            System.out.println("INSERT SQL:");
            System.out.println(insertSql.toString());
            System.out.println(". = 100 rows inserted");
            PreparedStatement preparedInsert = outputConnection.prepareStatement(insertSql.toString());
            int rows = 0;
            while (inputResults.next()) {
                for (int i = 1; i <= columns; i++) {
                    Object value = inputResults.getObject(i);
                    if (printEverything) {
                        if (value == null) {
                            System.out.print("null (<null>)");
                        } else {
                            System.out.print(value + " (" + value.getClass().getName() + ")");
                        }
                        if (i + 1 <= columns) {
                            System.out.print("\t\t");
                        }
                    }
                    preparedInsert.setObject(i, value);
                }
                preparedInsert.execute();
                rows++;
                if (rows % 100 == 0) {
                    System.out.print(".");
                }
                if (rows % 5000 == 0) {
                    System.out.print("  " + rows);
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println("Copied " + rows + " rows");
            inputConnection.close();
            outputConnection.close();
        } catch (Exception ex) {
            System.out.println();
            ex.printStackTrace();
        }
    }    
    
}
