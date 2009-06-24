package org.cagrid.identifiers.namingauthority;

import java.util.ArrayList;
import java.util.HashMap;

public class IdentifierValues {
	private HashMap<String, ArrayList<String>> _values =
		new HashMap<String, ArrayList<String>>();
	
	public String[] getValues( String type ) {
		return _values.get(type).toArray( new String[ _values.get(type).size() ]);
	}
	
	public String[] getTypes() {
		return _values.keySet().toArray(new String[ _values.keySet().size() ]);
	}

	public void add(String type, String data) {
		ArrayList<String> currValues = _values.get(type);
		if (currValues == null) {
			currValues = new ArrayList<String>();
			_values.put(type, currValues);
		}
		currValues.add(data);
	}
}
