package org.cagrid.identifiers.namingauthority.datatype;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataTypeService {
	private ApplicationContext appCtx;
	private DataTypeFactory factory;
	
	public DataTypeService() {
		init( new String[] { 
				"/resources/spring/framework-namingauthority-context.xml"},
			"DataTypeFactory");
	}
	
	public DataTypeService( String[] contextList, String factoryName ) {
		init( contextList, factoryName );
	}
	
	private void init( String[] contextList, String factoryName ) {
		appCtx = new ClassPathXmlApplicationContext( contextList );
		factory = (DataTypeFactory) appCtx.getBean( factoryName );
	}
	
	public DataTypeFactory getFactory() {
		return factory;
	}
}
