package org.cagrid.i2b2.ontomapper.query;

public class QueryColumnCriteria {

    private String columnName = null;
    private String predicate = null;
    private Object value = null;


    public QueryColumnCriteria(String columnName, String predicate, Object value) {
        this.columnName = columnName;
        this.predicate = predicate;
        this.value = value;
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
