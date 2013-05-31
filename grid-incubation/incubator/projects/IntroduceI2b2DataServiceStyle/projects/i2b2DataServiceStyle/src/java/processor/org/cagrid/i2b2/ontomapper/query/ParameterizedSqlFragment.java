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
package org.cagrid.i2b2.ontomapper.query;

import java.util.List;

public class ParameterizedSqlFragment {

    private String sql = null;
    private List<Object> parameters = null;
    
    public ParameterizedSqlFragment(String sql, List<Object> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }
    
    
    public String getSql() {
        return this.sql;
    }
    
    
    public List<Object> getParameters() {
        return this.parameters;
    }
}
