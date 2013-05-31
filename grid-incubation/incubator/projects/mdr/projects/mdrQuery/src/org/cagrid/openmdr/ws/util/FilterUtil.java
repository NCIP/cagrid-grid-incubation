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
package org.cagrid.openmdr.ws.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cancergrid.schema.query.QueryFilter;
import org.cancergrid.schema.result_set.DataElement;
import org.cancergrid.schema.result_set.ObjectClass;
import org.cancergrid.schema.result_set.Property;
import org.cancergrid.schema.result_set.ResultSet;

public class FilterUtil {
	
	private static Logger LOG = Logger.getLogger(FilterUtil.class);
	
	// This method is destructive (it overwrites results)
    public static void filter(QueryFilter queryFilter, ResultSet results) {
		if (results == null || queryFilter == null)
			return;
		
		filterByContext(queryFilter.getContext(), results);
	}

    // This method is destructive (it overwrites results)
    public static void filterByContext(String context, ResultSet results) {
		if (context == null || context.length() == 0) {
			return;
		}
		
		filterDataElementsByContext(context, results);
		filterObjectClassesByContext(context, results);
		filterPropertiesByContext(context, results);
	}
	
    public static void filterPropertiesByContext(String context, ResultSet results) {
		Property[] props = results.getProperty();
		if (props == null) {
			return;
		}
		
		LOG.debug("Filtering properties by context: " + context);
		
		List<Property> newProps = new ArrayList<Property>();
		for(Property p : props) {
			if (p.getContext().getName().equalsIgnoreCase(context)) {
				newProps.add(p);
			}
		}

		LOG.debug("Total matching properties: " + newProps.size());
		results.setProperty(newProps.toArray(new Property[newProps.size()]));
	}

    public static void filterObjectClassesByContext(String context, ResultSet results) {
		ObjectClass[] ocs = results.getObjectClass();
		if (ocs == null) {
			return;
		}
		
		LOG.debug("Filtering object classes by context: " + context);
		
		List<ObjectClass> newObjectClasses = new ArrayList<ObjectClass>();
		for(ObjectClass oc : ocs) {
			if (oc.getContext().getName().equalsIgnoreCase(context)) {
				newObjectClasses.add(oc);
			}
		}

		LOG.debug("Total matching object classes: " + newObjectClasses.size());
		results.setObjectClass(newObjectClasses.toArray(new ObjectClass[newObjectClasses.size()]));
	}

    public static void filterDataElementsByContext(String context, ResultSet results) {

		DataElement[] des = results.getDataElement();
		if (des == null) {
			return;
		}
		
		LOG.debug("Filtering data elements by context: " + context);
		
		List<DataElement> newDataElements = new ArrayList<DataElement>();
		for(DataElement de : des) {
			if (de.getContext().getName().equalsIgnoreCase(context)) {
				newDataElements.add(de);
			}
		}
		
		LOG.debug("Total matching data elements: " + newDataElements.size());
		results.setDataElement(newDataElements.toArray(new DataElement[newDataElements.size()]));
	}
}
