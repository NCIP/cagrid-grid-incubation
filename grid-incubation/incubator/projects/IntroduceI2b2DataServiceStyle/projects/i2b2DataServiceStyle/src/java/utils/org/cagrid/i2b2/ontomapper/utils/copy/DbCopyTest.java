package org.cagrid.i2b2.ontomapper.utils.copy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;


public class DbCopyTest {
    
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
            Connection alphaConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha", "root", "");
            System.out.println("Got a connection to alpha");
            
            // connect to database beta
            Connection betaConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/beta", "root", "");
            System.out.println("Got a connection to beta");
            
            // query for * from the ponies table in alpha
            Statement alphaStatement = alphaConnection.createStatement();
            ResultSet alphaResults = alphaStatement.executeQuery("select * from ponies");
            ResultSetMetaData alphaMetadata = alphaResults.getMetaData();
            
            // NOTE: From here on out, it gets weird.  JDBC uses 1 indexed lists / arrays, not 0 indexed like everything else in every programming language and convention ever
            int columns = alphaMetadata.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                System.out.print(alphaMetadata.getColumnName(i));
                if (i + 1 <= columns) {
                    System.out.print("\t\t");
                }
            }
            System.out.println();
            System.out.println("-------------");
            
            // create a prepared statement for inserting data
            PreparedStatement preparedInsert = betaConnection.prepareStatement("insert into ponies values (?, ?)");
            while (alphaResults.next()) {
                for (int i = 1; i <= columns; i++) {
                    Object value = alphaResults.getObject(i);
                    System.out.print(value + " (" + value.getClass().getName() + ")");
                    if (i + 1 <= columns) {
                        System.out.print("\t\t");
                    }
                    preparedInsert.setObject(i, value);
                }
                preparedInsert.execute();
                System.out.println();
                System.out.println(preparedInsert.getUpdateCount() + " rows inserted");
            }
            alphaConnection.close();
            betaConnection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
