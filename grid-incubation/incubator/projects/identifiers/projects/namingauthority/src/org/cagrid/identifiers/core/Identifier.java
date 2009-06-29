package org.cagrid.identifiers.core;

import java.util.HashMap;
import java.util.List;

public class Identifier {
	private String _name;
	private HashMap<String, List<Object>> _values;
	
	public String getName() {
		return _name;
	}
	
	public void setName( String name ) {
		_name = name;
	}
	
	public  HashMap<String, List<Object>> getValues() {
		return _values;
	}
	
	public void setValues( HashMap<String, List<Object>> values ) {
		_values = values;
	}
}
