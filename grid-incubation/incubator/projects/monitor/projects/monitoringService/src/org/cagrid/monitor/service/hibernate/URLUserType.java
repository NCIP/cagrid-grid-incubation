package org.cagrid.monitor.service.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class URLUserType implements UserType {

	private static final int[] SQL_TYPES = { Types.VARCHAR };

	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
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
		URI result = null;
		String urlAsString = resultSet.getString(names[0]);
		if (!resultSet.wasNull()) {
			try {
				result = StringUtils.isEmpty(urlAsString) ? null : new URI(urlAsString);
			} catch (MalformedURIException e) {
				throw new HibernateException(e);
			}
		}
		return result;
	}

	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {
	    if (value == null) {
	        statement.setString(index, null);
	      } else {
	        String urlAsString = ((URI) value).toString();
	        statement.setString(index, urlAsString);
	      }
	}

	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	public Class returnedClass() {
		return URI.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
