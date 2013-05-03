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
