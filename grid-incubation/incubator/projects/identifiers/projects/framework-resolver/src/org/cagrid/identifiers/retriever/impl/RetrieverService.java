package org.cagrid.identifiers.retriever.impl;

import org.cagrid.identifiers.core.IdentifierValues;
import org.cagrid.identifiers.retriever.Retriever;
import org.cagrid.identifiers.retriever.RetrieverFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RetrieverService {
	private ApplicationContext appCtx;
	private RetrieverFactory factory;
	
	public RetrieverService() {
		init( "/resources/spring/framework-resolver-context.xml",
				"RetrieverFactory");
	}
	
	public RetrieverService( String contextFilePath, String contextFactoryName ) {
		init( contextFilePath, contextFactoryName );
	}
	
	private void init( String contextFilePath, String contextFactoryName ) {
		appCtx = new ClassPathXmlApplicationContext( contextFilePath );
		factory = (RetrieverFactory) appCtx.getBean( contextFactoryName );
	}
	
	public RetrieverFactory getFactory() {
		return factory;
	}

	public Object retrieve( String retrieverName, IdentifierValues ivs ) throws Exception {
		Retriever retriever = factory.getRetriever( retrieverName );
		return retriever.retrieve(ivs);
	}
	
	public Object retrieve( IdentifierValues ivs ) throws Exception {
		Retriever retriever = factory.getRetriever(ivs);
		return retriever.retrieve(ivs);
	}
}
