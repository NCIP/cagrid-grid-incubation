package gov.nih.nci.cagrid.identifiers.na;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IdentifierValues {
	private HashMap<String, ArrayList<String>> _values;
	
	public List<String> getValues( String type ) {
		return _values.get(type);
	}
	
	public Set<String> getTypes() {
		return _values.keySet();
	}

	public void add(String type, String data) {
		ArrayList<String> currValues = (ArrayList<String>)getValues( type );
		if (currValues == null) {
			currValues = new ArrayList<String>();
			_values.put(type, currValues);
		}
		currValues.add(data);
	}
}
