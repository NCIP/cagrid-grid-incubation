package org.cagrid.identifiers.resolver;

import org.cagrid.identifiers.core.DataType;

public abstract class ResolutionProfile {
	public enum Type { 
		CQL ( new DataType[] { DataType.CQL, DataType.EPR } );
		
		private DataType[] requiredTypes;
		
		Type( DataType[] requiredTypes ) {
			this.requiredTypes = requiredTypes;
		}
		
		DataType[] getRequiredTypes() {
			return this.requiredTypes;
		}
	};
}
