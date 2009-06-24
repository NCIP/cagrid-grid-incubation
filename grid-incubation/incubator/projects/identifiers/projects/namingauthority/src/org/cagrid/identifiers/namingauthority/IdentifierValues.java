package org.cagrid.identifiers.namingauthority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IdentifierValues {
	private HashMap<String, ArrayList<String>> _values =
		new HashMap<String, ArrayList<String>>();
	
	public String[] getValues( String type ) {
		return (String[])_values.get(type).toArray();
	}
	
	public String[] getTypes() {
		return (String[])_values.keySet().toArray();
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
