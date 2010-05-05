package org.cagrid.redcap.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * Utility to get the properties from 
 * the properties file.
 */
public class PropertiesUtil {

	private static final Log LOG = LogFactory.getLog(PropertiesUtil.class);
	
	/*
	 * Gets the required query from the properties
	 * with the specified key.
	 * @key: key in the properties file
	 * @file: the location of the properties file(usually in etc)
	 */
	public static String getQuery(String key, String file) {
		String query = loadProperties(key, file);
		LOG.debug("Retrieving key["+key+"] from file ["+file+"] value: "+query);
		return query;
	}

	/*
	 * Gets the required user name from the properties file
	 * with the specified key to map Grid users with database users
	 * @key: key in the properties file
	 * @file: the location of the properties file(usually in etc)
	 */
	public static String getUsernames(String key,String file) {
		String value = loadProperties(key,file);
		LOG.debug("Retrieving key["+key+"] from file ["+file+"] value:"+value);
		return value;
	}

	private static String loadProperties(String key, String file) {
		Properties props = new Properties();
		String query = null;
		try {
			props.load(new FileInputStream(file));
			query = props.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return query;
	}
}
