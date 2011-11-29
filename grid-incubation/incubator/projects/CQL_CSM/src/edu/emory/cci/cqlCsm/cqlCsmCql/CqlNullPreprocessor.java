/**
 * Copyright 2010 Emory University
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
package edu.emory.cci.cqlCsm.cqlCsmCql;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

/**
 * This implementation of CqlPreprocessor always returns that same query that it is passed.
 * @author Mark Grand
 */
public class CqlNullPreprocessor implements CqlPreprocessor {

    /* (non-Javadoc)
     * @see edu.emory.cci.cqlCsm.cqlCsmCql.CqlPreprocessor#preprocess(gov.nih.nci.cagrid.cqlquery.CQLQuery, java.lang.String)
     */
    public CQLQuery preprocess(CQLQuery query, String user) {
        return query;
    }
}
