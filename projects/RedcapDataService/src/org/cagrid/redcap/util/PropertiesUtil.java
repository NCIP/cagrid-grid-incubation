package org.cagrid.redcap.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static String getQuery(String key) {
		InputStream inStream = PropertiesUtil.class
				.getResourceAsStream("/rc_auth_queries.properties");
		String query = loadProperties(key, inStream);
		return query;
	}

	public static String getUsernames(String key) {
		InputStream inStream = PropertiesUtil.class
				.getResourceAsStream("/rc_user_names.properties");
		String value = loadProperties(key,inStream);
		return value;
	}

	private static String loadProperties(String key, InputStream inputStream) {
		Properties props = new Properties();
		String query = null;
		try {
			props.load(inputStream);
			query = props.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return query;
	}
}
