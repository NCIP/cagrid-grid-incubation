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
package org.cagrid.i2b2.ontomapper.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ObjectAssembler
 * Builds java object instances from data retrieved out of an i2b2 database
 * 
 * @author David
 */
public class ObjectAssembler {
    
    private static final Log LOG = LogFactory.getLog(ObjectAssembler.class);
    
    
    private ObjectAssembler() {
        // prevents unnecessary and confusing instantiation
    }
    
    
    public static <T> List<T> assembleObjects(ResultSet results, Class<T> type) throws ObjectAssemblyException, SQLException {
        List<T> objects = new LinkedList<T>();
        while (results.next()) {
            objects.add(assembleObject(results, type));
        }
        return objects;
    }
    

    public static <T> T assembleObject(ResultSet results, Class<T> type) 
        throws ObjectAssemblyException, SQLException {
        T instance = null;
        try {
            instance = type.newInstance();
        } catch (InstantiationException ex) {
            throw new ObjectAssemblyException("Error instantiating new object instance: " + ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ObjectAssemblyException("Error instantiating new object instance: " + ex.getMessage(), ex);
        }
        ResultSetMetaData metadata = results.getMetaData();
        int count = metadata.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String columnName = metadata.getColumnName(i);
            Object value = results.getObject(i);
            try {
                setValue(instance, type, columnName, value);
            } catch (Exception ex) {
                throw new ObjectAssemblyException("Error setting values on new object instance: " + ex.getMessage(), ex);
            }
        }
        return instance;
    }
    
    
    private static void setValue(Object instance, Class<?> type, String fieldName, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        // prefer setters over accessing the field directly
        Method setter = findSetter(type, fieldName);
        if (setter != null) {
            setter.invoke(instance, value);
        } else {
            synchronized (type) {
                Field field = type.getField(fieldName);
                boolean accessable = field.isAccessible();
                field.setAccessible(true);
                field.set(instance, value);
                field.setAccessible(accessable);
            }
        }
    }
    
    
    private static Method findSetter(Class<?> type, String fieldName) {
        String firstCapitalized = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
        String getterName = "get" + firstCapitalized;
        Method getter = null;
        Method[] candidateMethods = type.getMethods();
        for (Method candidate : candidateMethods) {
            if (candidate.getName().equals(getterName) && candidate.isAccessible() 
                && candidate.getReturnType() == null && candidate.getParameterTypes().length == 1) {
                getter = candidate;
                break;
            }
        }
        if (getter == null) {
            LOG.debug("Did not find a getter for field " + fieldName + " of " + type.getName());
        }
        return getter;
    }
}
