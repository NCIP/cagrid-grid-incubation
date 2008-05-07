package org.cagrid.cql2.preview.processor;

import java.util.Iterator;
import java.util.List;

/** 
 *  ParameterizedHqlQuery
 *  Represents an HQL query using positional parameters
 * 
 * @author David Ervin
 * 
 * @created Dec 12, 2007 12:35:41 PM
 * @version $Id: ParameterizedHqlQuery.java,v 1.1 2008/04/02 14:46:19 dervin Exp $ 
 */
public class ParameterizedHqlQuery {

    private String hql;
    private List<Object> parameters;
    
    public ParameterizedHqlQuery(String hql, List<Object> parameters) {
        this.hql = hql;
        this.parameters = parameters;
    }
    
    
    public String getHql() {
        return hql;
    }
    
    
    public List<Object> getParameters() {
        return parameters;
    }
    
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(hql).append(" [");
        Iterator<Object> parameterIter = parameters.iterator();
        while (parameterIter.hasNext()) {
            builder.append(String.valueOf(parameterIter.next()));
            if (parameterIter.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
