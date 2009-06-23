package org.cagrid.identifiers.namingauthority.impl;

import org.cagrid.identifiers.namingauthority.*;
import org.cagrid.identifiers.namingauthority.util.Database;

import org.cagrid.identifiers.namingauthority.util.*;
import org.cagrid.identifiers.namingauthority.http.HttpServer;

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
				_httpServer = new HttpServer(this, getHttpServerPort());
				_httpServer.start();
			}
		}
	}
}
