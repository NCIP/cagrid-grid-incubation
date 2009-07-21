package org.cagrid.identifiers.namingauthority.datatype;

import java.util.List;

public class DataTypeFactory {
	private List<String> types;
	
	public DataTypeFactory( List<String> types ) {
		this.types = types;
	}
	
	public List<String> getTypes() {
		return types;
	}
	
	public boolean containsType( String type ) {
		return types.contains(type);
	}
}
