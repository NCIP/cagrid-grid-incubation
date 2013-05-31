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
package org.cagrid.i2b2.ontomapper.processor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DatabaseConnectionSource
 * Simple interface to return JDBC database connections to 
 * the i2b2 query processor
 * 
 * @author David
 */
public interface DatabaseConnectionSource {

    public Connection getConnection() throws SQLException;
    
    public void shutdown() throws SQLException;
}
