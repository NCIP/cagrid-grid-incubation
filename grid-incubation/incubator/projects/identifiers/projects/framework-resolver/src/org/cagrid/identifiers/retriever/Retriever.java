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
