package gov.nih.nci.cagrid.identifiers.na.impl;

import java.util.List;

import gov.nih.nci.cagrid.identifiers.Type;
import gov.nih.nci.cagrid.identifiers.TypeValuesMap;
import gov.nih.nci.cagrid.identifiers.Values;
import gov.nih.nci.cagrid.identifiers.hibernate.IdentifierValue;
import gov.nih.nci.cagrid.identifiers.na.IdentifierValues;
import gov.nih.nci.cagrid.identifiers.util.HibernateUtil;

import org.hibernate.Session;

public class Database {

	public static void save( String identifier, IdentifierValues values ) {
			
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        for( String type : values.getTypes()) {
        	for( String value : values.getValues(type) ) {
        		IdentifierValue iv = new IdentifierValue();
        		iv.setName( identifier );
        		iv.setData( value );
        		iv.setType(type);
         		
            	session.save(iv);
        	}
        }
      
        session.getTransaction().commit();
	}
	
	public static IdentifierValues getValues( String identifier ) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<IdentifierValue> values = session.
			createQuery("from IdentifierValue as iv where iv.name = :name").
				setParameter("name", identifier).list();
		session.getTransaction().commit();
		
		if (values.size() == 0)
			return null;
		
		IdentifierValues ivs = new IdentifierValues();
		for( int i=0; i < values.size(); i++ ) {
			ivs.add(values.get(i).getType(), values.get(i).getData());
		}
		
		return ivs;
	}
}
