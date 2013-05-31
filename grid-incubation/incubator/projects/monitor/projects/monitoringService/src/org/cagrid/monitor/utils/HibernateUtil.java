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
package org.cagrid.monitor.utils;import org.hibernate.*;import org.hibernate.cfg.*;public class HibernateUtil {    private static final SessionFactory sessionFactory;    static {        try {            // Create the SessionFactory from hibernate.cfg.xml            sessionFactory = new Configuration().configure().buildSessionFactory();        } catch (Throwable ex) {            // Make sure you log the exception, as it might be swallowed            System.err.println("Initial SessionFactory creation failed." + ex);            throw new ExceptionInInitializerError(ex);        }    }    public static SessionFactory getSessionFactory() {        return sessionFactory;    }        public static void save(Object obj) {		Session session = HibernateUtil.getSessionFactory().getCurrentSession();		session.beginTransaction();		session.save(obj);		session.getTransaction().commit();		    }}
