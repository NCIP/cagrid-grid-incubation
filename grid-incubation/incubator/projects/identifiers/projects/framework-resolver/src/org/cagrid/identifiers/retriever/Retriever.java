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
package org.cagrid.identifiers.retriever;

import org.cagrid.identifiers.core.IdentifierValues;

public abstract class Retriever {
	private String[] requiredTypes;
	
	public abstract Object retrieve( IdentifierValues ivs ) throws Exception;
	
	public String[] getRequiredTypes() {
		return this.requiredTypes;
	}
	
	public void setRequiredTypes( String[] types ) {
		this.requiredTypes = types;
	}
	
	protected void validateTypes( IdentifierValues ivs ) throws Exception {
		for( String type : requiredTypes ) {
			String[] values = ivs.getValues( type );
			if (values == null || values.length == 0)
				throw new Exception("No type ["+ type +"] found in indetifier values");
		}
	}
}
