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

public class QueryColumnCriteria {

    private String tableName = null;
    private String columnName = null;
    private String predicate = null;
    private Object value = null;


    public QueryColumnCriteria(String tableName, String columnName, String predicate, Object value) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.predicate = predicate;
        this.value = value;
    }
    
    
    public String getTableName() {
        return this.tableName;
    }


    public String getColumnName() {
        return columnName;
    }


    public String getPredicate() {
        return predicate;
    }


    public Object getValue() {
        return value;
    }
}
