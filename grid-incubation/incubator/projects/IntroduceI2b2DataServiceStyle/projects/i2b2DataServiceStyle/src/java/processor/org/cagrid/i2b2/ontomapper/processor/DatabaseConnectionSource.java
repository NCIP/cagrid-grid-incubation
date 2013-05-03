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
