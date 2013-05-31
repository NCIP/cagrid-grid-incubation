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
package org.cagrid.monitor.service.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;
import org.cagrid.monitor.db.StatusType;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;


public class StatusTypeUserType implements UserType {

	private static final int[] SQL_TYPES = { Types.VARCHAR };

	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object deepCopy(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable disassemble(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean equals(Object arg0, Object arg1) throws HibernateException {
		// TODO Auto-generated method stub
		return false;
	}

	public int hashCode(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException {
		StatusType result = null;
		String statusTypeAsString = resultSet.getString(names[0]);
		if (!resultSet.wasNull()) {
			result = StringUtils.isEmpty(statusTypeAsString) ? null
					: StatusType.fromValue(statusTypeAsString);
		}
		return result;
	}

	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			statement.setString(index, null);
		} else {
			String statusTypeAsString = ((StatusType) value).toString();
			statement.setString(index, statusTypeAsString);
		}
	}

	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Class returnedClass() {
		return StatusType.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
