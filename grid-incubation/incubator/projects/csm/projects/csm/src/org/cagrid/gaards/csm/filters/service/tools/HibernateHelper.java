package org.cagrid.gaards.csm.filters.service.tools;

import gov.nih.nci.security.authorization.domainobjects.FilterClause;
import gov.nih.nci.security.exceptions.CSException;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.AssociationType;
import org.hibernate.type.Type;

public class HibernateHelper {

	public static SessionFactory loadSessionFactory(String fileName,
			List<File> fileArray) throws Exception {
		SessionFactory sessionFactory = null;
		try {
			ClassPathLoader.addFile(fileArray.get(0));
			ClassPathLoader.addFile(fileArray.get(1));
			AnnotationConfiguration configuration = null;
			if (fileName != null && fileName.length()>0) {
				configuration = new AnnotationConfiguration()
						.configure(fileName);
			} else {
				configuration = new AnnotationConfiguration()
						.configure("hibernate.cfg.xml");
			}
			if (configuration
					.getProperty("hibernate.cache.use_second_level_cache") == null
					|| configuration
							.getProperty("cache.use_second_level_cache") == null) {
				configuration.setProperty(
						"hibernate.cache.use_second_level_cache", "false");
				configuration.setProperty("cache.use_second_level_cache",
						"false");
				// configuration.setProperty("hibernate.show_sql", "true");

			}
		    JDBCHelper.testConnectionHibernate(configuration);

			sessionFactory = configuration.buildSessionFactory();
		} catch (CacheException e) {

			ClassPathLoader.releaseJarsFromClassPath(sessionFactory, fileArray);
			throw new Exception(
					"Error in loading the Session Factory from the Hibernate File."
							+ "" + e.getMessage());
		} catch (Exception exception) {
			throw new Exception(
					"Error in loading the Session Factory from the Hibernate File."
							+ "" + exception.getMessage(), exception);
		}

		if (null == sessionFactory)
			throw new Exception(
					"Error in loading the Session Factory from the Hibernate File");
		else {
			Session session = null;
			try {
				session = sessionFactory.openSession();
			} catch (Exception exception) {
				throw new Exception(
						"Error in creating a Session from the Loaded Session Factory");
			}
			if (null == session)
				throw new Exception(
						"Error in creating a Session from the Loaded Session Factory");
		}
		return sessionFactory;
	}

	@SuppressWarnings("unchecked")
    public static List<String> getAllClassNames(SessionFactory sessionFactory) {
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		Set<String> set = map.keySet();
		ArrayList<String> list = new ArrayList<String>();
		for (String className : set) {
			list.add(className);
		}
		return list;
	}

	public static HashMap<String, String> getAssociatedClasses(String className,
			SessionFactory sessionFactory) throws Exception {

		boolean isParentClass = false;

		if (className.contains(" - self")) {
			throw new Exception(
					"No Associations allowed for direct security filter clause.");
		}

		HashMap<String, String> map = new HashMap<String, String>();
		if (!(className.contains(" - "))) {
			isParentClass = true;
		} else {
			className = className.substring(0, className.indexOf(" - "));
		}
		ClassMetadata classMetadata = sessionFactory
				.getClassMetadata(className);
		String[] properties = classMetadata.getPropertyNames();
		for (int i = 0; i < properties.length; i++) {
			Type type = classMetadata.getPropertyType(properties[i]);
			if (type instanceof AssociationType) {
				try {
					AssociationType associationType = (AssociationType) type;
					map.put(	properties[i],
								associationType.getAssociatedEntityName((SessionFactoryImplementor) sessionFactory)
											+ " - " + properties[i]);
				} catch (Exception e) {
					throw new Exception("Hibernate Error: " + e.getMessage());
				}
			}
		}
		if (isParentClass) {
			map.put(className, className + " - self");
		}
		// if (map.size() == 0)
		// throw new Exception("No associated Classes Found!");

		return map;
	}

	public static HashMap<String, String> getAssociatedAttributes(String className,
			SessionFactory sessionFactory) throws CSException {
		className = className.substring(0, className.indexOf(" - "));
		HashMap<String, String> map = new HashMap<String, String>();
		ClassMetadata classMetadata = sessionFactory
				.getClassMetadata(className);

		List<String> propertiesList = new ArrayList<String>();
		String[] properties1 = classMetadata.getPropertyNames();
		for (int count = 0; count < properties1.length; count++) {
			propertiesList.add(new String(properties1[count]));
		}
		propertiesList
				.add(new String(classMetadata.getIdentifierPropertyName()));
		Iterator<String> propertiesIterator = propertiesList.iterator();
		while (propertiesIterator.hasNext()) {
			String property = (String) propertiesIterator.next();
			Type type = classMetadata.getPropertyType(property);
			if (!(type instanceof AssociationType)) {
				map.put(property + "-_-" + (type.getReturnedClass()).getName(),
						property);
				System.out.println(type.getReturnedClass().getName());
			}
		}

		// if (map.size() == 0)
		// throw new CSException("No associated Classes Found!");

		return map;
	}

	public static String getAssociatedAttributeType(String className,
			String attributeName, SessionFactory sessionFactory)
			throws CSException {
		ClassMetadata classMetadata = sessionFactory
				.getClassMetadata(className);

		List<String> propertiesList = new ArrayList<String>();
		String[] properties1 = classMetadata.getPropertyNames();
		for (int count = 0; count < properties1.length; count++) {
			propertiesList.add(new String(properties1[count]));
		}
		propertiesList
				.add(new String(classMetadata.getIdentifierPropertyName()));
		Iterator<String> propertiesIterator = propertiesList.iterator();
		while (propertiesIterator.hasNext()) {
			String property = (String) propertiesIterator.next();
			if (property.equals(attributeName)) {
				Type type = classMetadata.getPropertyType(property);
				System.out.println(type.getReturnedClass().getName());
				return (type.getReturnedClass()).getName();
			}

		}

		return null;

	}

	public static String getGeneratedSQL(FilterClause filterClause,
			SessionFactory sessionFactory, boolean isSecurityForGroup) {
		Session session = sessionFactory.openSession();
		Criteria queryCriteria = createCriterias(filterClause, session);

		String generatedSQL = generateSQL(filterClause, queryCriteria, session,
				isSecurityForGroup);
		if (isSecurityForGroup)
			filterClause.setGeneratedSQLForGroup(generatedSQL);
		else
			filterClause.setGeneratedSQLForUser(generatedSQL);

		return generatedSQL;
	}

	private static String generateSQL(FilterClause filterClause,
			Criteria criteria, Session session, boolean isSecurityForGroup) {
		String capturedSQL = null;
		StringWriter writer = new StringWriter();
		WriterAppender appender = new WriterAppender(new SimpleLayout(), writer);
		appender.setImmediateFlush(true);
		Logger logger = Logger.getLogger("org.hibernate.SQL");
		logger.setLevel(Level.DEBUG);
		logger.addAppender(appender);
		criteria.list();
		capturedSQL = writer.getBuffer().toString();
		logger.removeAppender(appender);

		String filterSQL;
		if (isSecurityForGroup) {
			filterSQL = modifySQLForGroup(filterClause, capturedSQL, session);
		} else {
			filterSQL = modifySQLForUser(filterClause, capturedSQL, session);
		}
		return filterSQL;
	}

	private static Criteria createCriterias(FilterClause filterClause,
			Session session) {
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		criteriaList
				.add(0, session.createCriteria(filterClause.getClassName()));
		StringTokenizer stringTokenizer = new StringTokenizer(filterClause
				.getFilterChain(), ",");
		int count = 0;
		while (stringTokenizer.hasMoreTokens()) {
			String attributeName = stringTokenizer.nextToken();
			if (attributeName.substring(0, attributeName.indexOf(" - ")).trim()
					.equals(filterClause.getClassName()))
				break;
			attributeName = attributeName.substring(attributeName
					.indexOf(" - ") + 3);

			count++;
			Criteria parentCriteria = criteriaList.get(count - 1);
			Criteria childCriteria = parentCriteria
					.createCriteria(attributeName.trim());
			criteriaList.add(count, childCriteria);
		}
		Criteria targetCriteria = criteriaList.get(count);
		String attributeName = filterClause.getTargetClassAttributeName();
		Class<?> attributeType = null;
		Class<Integer> IntegerType = null;
		Class<Long> LongType = null;
		try {
			attributeType = Class.forName(filterClause
					.getTargetClassAttributeType());
			IntegerType = Integer.class;
			LongType = Long.class;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object valueArray = Array.newInstance(attributeType, 1);

		try {
			if (attributeType.equals(IntegerType)) {
				Array.set(valueArray, 0, new Integer(0));
			} else if (attributeType.equals(LongType)) {
				Array.set(valueArray, 0, new Long(0));
			} else {
				Array.set(valueArray, 0, attributeType.newInstance());
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		targetCriteria.add(Expression.in(attributeName, (Object[]) valueArray));
		Criteria mainCriteria = (Criteria) criteriaList.get(0);
		mainCriteria.setProjection(Projections.id());

		return mainCriteria;
	}

	private static String modifySQLForUser(FilterClause filterClause,
			String generatedSQL, Session session) {
		String targetClassName = null;
		if (StringUtils.isBlank(filterClause.getTargetClassAlias()))
			targetClassName = filterClause.getTargetClassName().substring(0,
					filterClause.getTargetClassName().indexOf(" - "));
		else
			targetClassName = filterClause.getTargetClassAlias();
		String targetClassAttributeName = null;
		if (StringUtils.isBlank(filterClause.getTargetClassAttributeAlias()))
			targetClassAttributeName = filterClause
					.getTargetClassAttributeName();
		else
			targetClassAttributeName = filterClause
					.getTargetClassAttributeAlias();

		String CSM_QUERY = " select pe.attribute_value from "
				+ "csm_protection_group pg, " + "csm_protection_element pe, "
				+ "csm_pg_pe pgpe, " + "csm_user_group_role_pg ugrpg, "
				+ "csm_user u, " + "csm_role_privilege rp, " + "csm_role r, "
				+ "csm_privilege p " + "where ugrpg.role_id = r.role_id "
				+ "and ugrpg.user_id = u.user_id and "
				+ "ugrpg.protection_group_id = ANY "
				+ "(select pg1.protection_group_id "
				+ "from csm_protection_group pg1 "
				+ "where pg1.protection_group_id = pg.protection_group_id "
				+ "or pg1.protection_group_id = "
				+ "(select pg2.parent_protection_group_id "
				+ "from csm_protection_group pg2 "
				+ "where pg2.protection_group_id = pg.protection_group_id)) "
				+ "and pg.protection_group_id = pgpe.protection_group_id "
				+ "and pgpe.protection_element_id = pe.protection_element_id "
				+ "and r.role_id = rp.role_id "
				+ "and rp.privilege_id = p.privilege_id "
				+ "and pe.object_id= '" + targetClassName + "' "
				+ "and pe.attribute='" + targetClassAttributeName + "' "
				+ "and p.privilege_name='READ' "
				+ "and u.login_name=:USER_NAME "
				+ "and pe.application_id=:APPLICATION_ID";

		StringBuffer result = new StringBuffer();
		String query = generatedSQL.substring(generatedSQL.indexOf('-') + 1,
				generatedSQL.length());
		query = query.trim();
		query = query.substring(0, query.indexOf('?'));
		String delimiters = "+-*/(),. ";
		StringTokenizer st = new StringTokenizer(query, delimiters, true);
		while (st.hasMoreTokens()) {
			String w = st.nextToken();
			if (w.equals("this_")) {
				result = result.append("table_name_csm_");
			} else if (w.equals("y0_")) {
				result = result.append("");
			} else if (w.equals("as")) {
				result = result.append("");
			} else {
				result = result.append(w);
			}
		}
		System.out.println("The Query is : " + result.toString());
		SessionFactory sessionFactory = session.getSessionFactory();
		ClassMetadata classMetadata = sessionFactory
				.getClassMetadata(filterClause.getClassName());
		String columnName = null;
		if (classMetadata instanceof AbstractEntityPersister) {
			AbstractEntityPersister abstractEntityPersister = (AbstractEntityPersister) classMetadata;
			String Id = abstractEntityPersister.getIdentifierPropertyName();
			String[] columns = abstractEntityPersister
					.getPropertyColumnNames(Id);
			columnName = columns[0];
		}
		query = columnName + " in (" + result.toString() + CSM_QUERY + "))";
		return query.toString();
	}

	private static String modifySQLForGroup(FilterClause filterClause,
			String generatedSQL, Session session) {
		String targetClassName = null;
		if (StringUtils.isBlank(filterClause.getTargetClassAlias()))
			targetClassName = filterClause.getTargetClassName().substring(0,
					filterClause.getTargetClassName().indexOf(" - "));
		else
			targetClassName = filterClause.getTargetClassAlias();
//		String targetClassAttributeName = null;
//		if (StringUtils.isBlank(filterClause.getTargetClassAttributeAlias()))
//			targetClassAttributeName = filterClause
//					.getTargetClassAttributeName();
//		else
//			targetClassAttributeName = filterClause
//					.getTargetClassAttributeAlias();

		String CSM_QUERY = "SELECT Distinct pe.attribute_value "
				+ "FROM CSM_PROTECTION_GROUP pg, "
				+ "	CSM_PROTECTION_ELEMENT pe, "
				+ "	CSM_PG_PE pgpe,"
				+ "	CSM_USER_GROUP_ROLE_PG ugrpg, "
				+ "	CSM_GROUP g, "
				+ "	CSM_ROLE_PRIVILEGE rp, "
				+ "	CSM_ROLE r, "
				+ "	CSM_PRIVILEGE p "
				+ "WHERE ugrpg.role_id = r.role_id "
				+ "AND ugrpg.group_id = g.group_id "
				+ "AND ugrpg.protection_group_id = ANY "
				+ "( select pg1.protection_group_id from csm_protection_group pg1 "
				+ " where pg1.protection_group_id = pg.protection_group_id OR pg1.protection_group_id = "
				+ " (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)"
				+ " ) "
				+ "AND pg.protection_group_id = pgpe.protection_group_id "
				+ "AND pgpe.protection_element_id = pe.protection_element_id "
				+ "AND r.role_id = rp.role_id "
				+ "AND rp.privilege_id = p.privilege_id "
				+ "AND pe.object_id= '" + targetClassName + "' "
				+ "AND p.privilege_name='READ' "
				+ "AND g.group_name IN (:GROUP_NAMES ) "
				+ "AND pe.application_id=:APPLICATION_ID";

		/*
		 * String CSM_QUERY = " select pe.attribute_value from " +
		 * "csm_protection_group pg, " + "csm_protection_element pe, " +
		 * "csm_pg_pe pgpe, " + "csm_user_group_role_pg ugrpg, " +
		 * "csm_user u, " + "csm_role_privilege rp, " + "csm_role r, " +
		 * "csm_privilege p " + "where ugrpg.role_id = r.role_id " +
		 * "and ugrpg.user_id = u.user_id and " +
		 * "ugrpg.protection_group_id = ANY " +
		 * "(select pg1.protection_group_id " + "from csm_protection_group pg1 "
		 * + "where pg1.protection_group_id = pg.protection_group_id " +
		 * "or pg1.protection_group_id = " +
		 * "(select pg2.parent_protection_group_id " +
		 * "from csm_protection_group pg2 " +
		 * "where pg2.protection_group_id = pg.protection_group_id)) " +
		 * "and pg.protection_group_id = pgpe.protection_group_id " +
		 * "and pgpe.protection_element_id = pe.protection_element_id " +
		 * "and r.role_id = rp.role_id " +
		 * "and rp.privilege_id = p.privilege_id " + "and pe.object_id= '" +
		 * targetClassName + "' " + "and pe.attribute='" +
		 * targetClassAttributeName + "' " + "and p.privilege_name='READ' " +
		 * "and u.login_name=:USER_NAME " +
		 * "and pe.application_id=:APPLICATION_ID" ;
		 */

		StringBuffer result = new StringBuffer();
		String query = generatedSQL.substring(generatedSQL.indexOf('-') + 1,
				generatedSQL.length());
		query = query.trim();
		query = query.substring(0, query.indexOf('?'));
		String delimiters = "+-*/(),. ";
		StringTokenizer st = new StringTokenizer(query, delimiters, true);
		while (st.hasMoreTokens()) {
			String w = st.nextToken();
			if (w.equals("this_")) {
				result = result.append("table_name_csm_");
			} else if (w.equals("y0_")) {
				result = result.append("");
			} else if (w.equals("as")) {
				result = result.append("");
			} else {
				result = result.append(w);
			}
		}
		System.out.println("The Query is : " + result.toString());
		SessionFactory sessionFactory = session.getSessionFactory();
		ClassMetadata classMetadata = sessionFactory
				.getClassMetadata(filterClause.getClassName());
		String columnName = null;
		if (classMetadata instanceof AbstractEntityPersister) {
			AbstractEntityPersister abstractEntityPersister = (AbstractEntityPersister) classMetadata;
			String Id = abstractEntityPersister.getIdentifierPropertyName();
			String[] columns = abstractEntityPersister
					.getPropertyColumnNames(Id);
			columnName = columns[0];
		}
		query = columnName + " in (" + result.toString() + CSM_QUERY + "))";
		return query.toString();
	}

//	private static Appender startSQLCapture(
//			ByteArrayOutputStream byteArrayOutputStream) {
//		Appender appender = new WriterAppender(new SimpleLayout(),
//				byteArrayOutputStream);
//		Logger logger = Logger.getLogger("org.hibernate.SQL");
//		logger.addAppender(appender);
//		return appender;
//	}
//
//	private static String stopSQLCapture(Appender appender,
//			ByteArrayOutputStream byteArrayOutputStream) {
//		Logger logger = Logger.getLogger("org.hibernate.SQL");
//		logger.removeAppender(appender);
//		DataInputStream dataInputStream = new DataInputStream(
//				new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//		try {
//			while (dataInputStream.available() != 0) {
//				String line = dataInputStream.readLine();
//				if (line.startsWith("DEBUG - select this_")) {
//					return line;
//				}
//			}
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//		return null;
//	}

}
