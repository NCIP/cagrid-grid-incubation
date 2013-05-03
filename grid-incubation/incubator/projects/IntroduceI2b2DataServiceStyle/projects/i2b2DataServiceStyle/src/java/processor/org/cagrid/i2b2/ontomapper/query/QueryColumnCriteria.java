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
