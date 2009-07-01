package org.cagrid.identifiers.core;

public enum DataType {
	CQL("CQL"), EPR("EPR"), URL("URL");
	
	private final String strValue;
	
	DataType( String value ) {
		this.strValue = value;
	}
	
	public String toString() {
		return this.strValue;
	}
}
