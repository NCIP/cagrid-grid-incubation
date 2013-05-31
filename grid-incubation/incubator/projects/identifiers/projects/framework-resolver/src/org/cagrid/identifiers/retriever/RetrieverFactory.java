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
package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.core.IdentifierValues;

public interface RetrieverFactory {
	Retriever getRetriever( IdentifierValues ivs ) throws Exception;
	Retriever getRetriever( String name ) throws Exception;
}
