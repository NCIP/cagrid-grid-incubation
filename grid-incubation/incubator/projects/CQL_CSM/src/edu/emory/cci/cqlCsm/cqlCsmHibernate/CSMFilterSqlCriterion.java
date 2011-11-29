/**
 * Copyright 2009,2010 Emory University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated  documentation files (the 
 * "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the
 * Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package edu.emory.cci.cqlCsm.cqlCsmHibernate;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SQLCriterion;
import org.hibernate.util.ArrayHelper;

/**
 * Subclass of SQLCriterion that assumes that the first token of its SQL string
 * is an unqualified column name. It renders the SQL by prepending it with the
 * table alias associated with the specified alias that is defined in the
 * Criteria object that this object belongs to.
 * <p>
 * <b>This class also assumes that the name of the identity property of the
 * entity associated with the specified alias is</b> <tt>id</tt>.
 * 
 * @author Mark Grand
 */
class CSMFilterSqlCriterion extends SQLCriterion {
    /**
     * Hash code for serialization.
     */
    private static final long serialVersionUID = 2381981091982903361L;

    private String alias;

    /**
     * Constructor
     * 
     * @param alias
     *            An alias that will be defined in criteria queries used to
     *            render this SQL.
     * @param sql
     *            An SQL string. The first token in this SQL is assumed to be a
     *            column in the table being filtered.
     */
    public CSMFilterSqlCriterion(String alias, String sql) {
        super(sql, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY);
        if (alias == null) {
            throw new NullPointerException();
        }
        this.alias = alias;
    }

    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String qualifiedColumnName;
        if (alias.length() > 0) {
            qualifiedColumnName = criteriaQuery.getColumn(criteria, alias + ".id");
        } else {
            // empty path
            qualifiedColumnName = criteriaQuery.getIdentifierColumns(criteria)[0];
        }

        // Remove the column name part of the qualified column name since we
        // just want the qualification and not the column name.
        int dotIndex = qualifiedColumnName.lastIndexOf('.');
        String qualification;
        if (dotIndex > 0) {
            qualification = qualifiedColumnName.substring(0, dotIndex + 1);
        } else {
            qualification = "";
        }

        return qualification + super.toSqlString(criteria, criteriaQuery);
    }
}
