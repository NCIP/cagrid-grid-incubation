package org.cagrid.identifiers.namingauthority.util;

import java.util.List;



import org.cagrid.identifiers.core.IdentifierValues;
import org.cagrid.identifiers.namingauthority.hibernate.IdentifierValue;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Database {

	private SessionFactory dbFactory = null;
	
	public Database( String dbUrl, String dbUser, String dbPassword ) {
		dbFactory = HibernateUtil.initFactory(dbUrl, dbUser, dbPassword);
	}
		
	public void save( String identifier, IdentifierValues values ) {
			
        Session session = dbFactory.getCurrentSession();
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
	
	public IdentifierValues getValues( String identifier ) {
		
		Session session = dbFactory.getCurrentSession();
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
