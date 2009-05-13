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

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // load the sybase driver
            Class.forName(com.sybase.jdbc3.jdbc.SybDriver.class.getName());
            // and the mysql driver
            Class.forName(com.mysql.jdbc.Driver.class.getName());
            
            // connect to database alpha
            Connection sybaseConnection = DriverManager.getConnection("jdbc:sybase:Tds:192.168.24.21:2638/i2b2rc4", "DBA", "SQL");
            System.out.println("Got a connection to sybase");
            
            // connect to database beta
            Connection mysqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/i2b2_alpha", "root", "");
            System.out.println("Got a connection to mysql");
            
            // query for * from the concept_dimension table in alpha
            Statement sybaseStatement = sybaseConnection.createStatement();
            ResultSet sybaseResults = sybaseStatement.executeQuery("select * from i2b2demodata.CONCEPT_DIMENSION2");
            ResultSetMetaData sybaseMetadata = sybaseResults.getMetaData();
            
            // NOTE: From here on out, it gets weird.  JDBC uses 1 indexed lists / arrays, not 0 indexed like everything else in every programming language and convention ever
            int columns = sybaseMetadata.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                System.out.print(sybaseMetadata.getColumnName(i));
                if (i + 1 <= columns) {
                    System.out.print("\t\t");
                }
            }
            System.out.println();
            System.out.println("-------------");
            
            // create a prepared statement for inserting data
            System.out.println("Source table has " + sybaseMetadata.getColumnCount() + " columns");
            StringBuffer insertSql = new StringBuffer();
            insertSql.append("insert into concept_dimension (");
            for (int i = 0; i < sybaseMetadata.getColumnCount(); i++) {
                insertSql.append(sybaseMetadata.getColumnName(i + 1));
                if (i + 1 < sybaseMetadata.getColumnCount()) {
                    insertSql.append(", ");
                }
            }
            insertSql.append(") values(");
            for (int i = 0; i < sybaseMetadata.getColumnCount(); i++) {
                insertSql.append("?");
                if (i + 1 < sybaseMetadata.getColumnCount()) {
                    insertSql.append(", ");
                }
            }
            insertSql.append(")");
            System.out.println("INSERT SQL:");
            System.out.println(insertSql.toString());
            PreparedStatement preparedInsert = mysqlConnection.prepareStatement(insertSql.toString());
            while (sybaseResults.next()) {
                for (int i = 1; i <= columns; i++) {
                    Object value = sybaseResults.getObject(i);
                    if (value == null) {
                        System.out.print("null (<null>)");
                    } else {
                        System.out.print(value + " (" + value.getClass().getName() + ")");
                    }
                    if (i + 1 <= columns) {
                        System.out.print("\t\t");
                    }
                    preparedInsert.setObject(i, value);
                }
                preparedInsert.execute();
                System.out.println();
                System.out.println(preparedInsert.getUpdateCount() + " rows inserted");
            }
            sybaseConnection.close();
            mysqlConnection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
