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
