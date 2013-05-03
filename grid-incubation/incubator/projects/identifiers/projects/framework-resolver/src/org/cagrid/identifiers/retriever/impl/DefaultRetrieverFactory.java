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
package org.cagrid.identifiers.retriever.impl;

import java.util.Map;

import org.cagrid.identifiers.core.IdentifierValues;
import org.cagrid.identifiers.retriever.Retriever;
import org.cagrid.identifiers.retriever.RetrieverFactory;

public class DefaultRetrieverFactory implements RetrieverFactory {
	private Map<String, Retriever> retrievers;
	
	public DefaultRetrieverFactory(Map<String, Retriever> retrievers) {
		this.retrievers = retrievers;
	}
	
	public Retriever getRetriever( IdentifierValues ivs ) throws Exception {
		//TODO
		throw new Exception("Not implemented yet");
	}
	
	public Retriever getRetriever( String name ) throws Exception {
		Retriever retriever = retrievers.get(name);
		if (retriever == null)
			throw new Exception("No retriever defined for [" + name + "]");
		
		return retriever;
	}
}
