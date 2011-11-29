package org.cagrid.gaards.csm.filters.service;

import gov.nih.nci.security.authorization.domainobjects.FilterClause;

import java.rmi.RemoteException;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class FilterCreatorImpl extends FilterCreatorImplBase {

	public FilterCreatorImpl() throws RemoteException {
		super();
	}

	public java.lang.String[] getClassNames() throws RemoteException,
			org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
		try {
			String[] classNames = getResourceHome().getAddressedResource()
					.getClassNames();
			return classNames;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), e);
		}
	}

	public java.lang.String[] getAssociatedClassNames(java.lang.String className)
			throws RemoteException {
		try {
			String[] classes = getResourceHome().getAddressedResource()
					.getAssociatedClasses(className);
			return classes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), e);
		}
	}

	public java.lang.String[] getAssociatedAttributes(java.lang.String className)
			throws RemoteException {
		try {
			return getResourceHome().getAddressedResource()
					.getAssociatedAttributes(className);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), e);
		}
	}

	public org.cagrid.gaards.csm.bean.FilterClause getFilterClauseBean(
			java.lang.String startingClass, java.lang.String[] filters,
			java.lang.String targetClassAttribute,
			java.lang.String targetClassAlias,
			java.lang.String targetClassAttributeAlias) throws RemoteException {
		try {
			FilterClause clause = getResourceHome().getAddressedResource()
					.createFilterClause(startingClass, filters,
							targetClassAttribute, targetClassAlias,
							targetClassAttributeAlias);
			org.cagrid.gaards.csm.bean.FilterClause bean = new org.cagrid.gaards.csm.bean.FilterClause();
			bean.setClassName(clause.getClassName());
			bean.setFilterChain(clause.getFilterChain());
			bean.setGeneratedSQLForGroup(clause.getGeneratedSQLForGroup());
			bean.setGeneratedSQLForUser(clause.getGeneratedSQLForUser());
			bean.setTargetClassAlias(clause.getTargetClassAlias());
			bean.setTargetClassAttributeAlias(clause
					.getTargetClassAttributeAlias());
			bean.setTargetClassAttributeName(clause
					.getTargetClassAttributeName());
			bean.setTargetClassAttributeType(clause
					.getTargetClassAttributeType());
			bean.setTargetClassName(clause.getTargetClassName());
			bean.setApplicationId(getResourceHome().getAddressedResource().getApplicationID().longValue());
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage(), e);
		}
	}

}
