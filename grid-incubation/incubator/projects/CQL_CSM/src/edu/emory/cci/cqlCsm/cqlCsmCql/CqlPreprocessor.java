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
 * Interface implemented by classes that pre-processes/re-writes/transforms CQL
 * queries.
 * 
 * @author Mark Grand
 */
public interface CqlPreprocessor {
    /**
     * Pre-process/re-write/transform the given CQL query and return the
     * transformed CQL query.
     * 
     * @param query The query to be pre-processed/re-written/transformed.
     * @param user The identity of the user this is for.
     * @return The pre-processed/re-written/transformed CQL query.
     */
    public CQLQuery preprocess(CQLQuery query, String user);
}
