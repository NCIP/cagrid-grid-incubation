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

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SybaseExtractor {
    // JDBC for Sybase w/ JTDS: jdbc:sybase:Tds://<server>:<port>/database

    public static void main(String[] args) {
        File outDir = new File(args[0]);
        try {
            // load the sybase driver
            Class.forName(com.sybase.jdbc3.jdbc.SybDriver.class.getName());
            
            /* UCSF i2b2 demo database */
            String inputDb = "jdbc:sybase:Tds:192.168.24.21:2638/i2b2rc4";
            String inputUser = "DBA";
            String inputPasswd = "SQL";
            
            // connect to sybase database
            Connection connection = DriverManager.getConnection(inputDb, inputUser, inputPasswd);
            System.out.println("Got a connection to the sybase database");
            
            for (String tableName : TableNames.allTableNames) {
                long start = System.currentTimeMillis();
                System.out.println("Querying " + tableName);
                File outFile = new File(outDir, tableName + ".csv");
                FileWriter writer = new FileWriter(outFile);
                outFile.createNewFile();
                System.out.println("Storing data to " + outFile.getAbsolutePath());
                // query for * from the table in sybase
                Statement inputStatement = connection.createStatement();
                ResultSet inputResults = inputStatement.executeQuery("select * from i2b2demodata." + tableName);
                ResultSetMetaData inputMetadata = inputResults.getMetaData();
                
                // NOTE: From here on out, it gets weird.  JDBC uses 1 indexed lists / arrays, 
                // not 0 indexed like everything else in every programming language and convention ever
                
                // print column names
                int columns = inputMetadata.getColumnCount();
                for (int i = 1; i <= columns; i++) {
                    writer.write(inputMetadata.getColumnName(i));
                    if (i + 1 <= columns) {
                        writer.write("\t");
                    }
                }
                writer.write("\n");
                
                // write data
                while (inputResults.next()) {
                    for (int i = 1; i <= columns; i++) {
                        Object data = inputResults.getObject(i);
                        String strVal = null;
                        if (data != null) {
                            System.out.println("Data of type " + data.getClass().getName());
                            // try this on QT_QUERY_RESULT_INSTANCE
                            if (data instanceof Date) {
                                strVal = new SimpleDateFormat("yyyy-mm-dd").format((Date) data);
                            } else if (data instanceof Time) {
                                strVal = DateFormat.getTimeInstance().format((Time) data);
                            } else {
                                strVal = String.valueOf(data);
                            }
                        }
                        writer.write(String.valueOf(strVal));
                        if (i + 1 <= columns) {
                            writer.write("\t");
                        }
                    }
                    writer.append("\n");
                }
                writer.close();
                System.out.println("Done writing " + outFile.getAbsolutePath());
                System.out.println("Completed in " + (System.currentTimeMillis() - start) / 1000d + " sec");
            }
            System.out.println("All done");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
