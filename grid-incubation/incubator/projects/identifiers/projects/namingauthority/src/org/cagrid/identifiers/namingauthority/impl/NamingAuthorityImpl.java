package gov.nih.nci.cagrid.identifiers.na.impl;

import gov.nih.nci.cagrid.identifiers.na.*;
import gov.nih.nci.cagrid.identifiers.util.*;

import gov.nih.nci.cagrid.identifiers.http.HttpServer;


public class NamingAuthorityImpl extends NamingAuthority implements IdentifierMaintainer, IdentifierUser {

	private HttpServer _httpServer = null;
	private Object lock = new Object();
	
	public NamingAuthorityImpl(NamingAuthorityConfig config) {
		super(config);
		System.out.println("Created NA with prefix: " + config.getPrefix());
	}

	public String create(IdentifierValues values) {
		String identifier = IdentifierUtil.generate(getPrefix());
		Database.save(identifier, values);
        return identifier;
	}
	
	public IdentifierValues getValues( String identifier ) {
		return Database.getValues(identifier);
	}
	
	public void startHttpServer() {
		synchronized(lock) {
			if (_httpServer == null) {
				//TODO: read this port number from config
				_httpServer = new HttpServer(this, 8081);
				_httpServer.start();
			}
		}
	}
}
