package org.cagrid.identifiers.namingauthority.impl;

import org.cagrid.identifiers.core.*;
import org.cagrid.identifiers.namingauthority.*;
import org.cagrid.identifiers.namingauthority.util.Database;

import org.cagrid.identifiers.namingauthority.util.*;
import org.cagrid.identifiers.namingauthority.datatype.DataTypeService;
import org.cagrid.identifiers.namingauthority.http.HttpServer;

public class NamingAuthorityImpl extends NamingAuthority implements IdentifierMaintainer, IdentifierUser {

	private HttpServer _httpServer = null;
	private Database db = null;
	private DataTypeService dataTypeSvc = null;
	
	public NamingAuthorityImpl(NamingAuthorityConfig config) {
		super(config);
		
		// Initialize Database factory
		db = new Database(config.getDbUrl(), config.getDbUserName(), config.getDbPassword());
		
		// Initialize data types factory
		dataTypeSvc = new DataTypeService();
		
		System.out.println("Created NA with prefix: " + config.getPrefix());
	}

	public String create(IdentifierValues values) throws Exception {
		if (values == null)
			throw new Exception("Input IdentifierValues can't be null");
		
		validateDataTypes( values );
		
		String identifier = IdentifierUtil.generate(getConfig().getPrefix());
		db.save(identifier, values);
        return identifier;
	}
	
	public IdentifierValues getValues( String identifier ) {
		return db.getValues(identifier);
	}
	
	public synchronized void startHttpServer() {
		if (_httpServer == null) {
			_httpServer = new HttpServer(this, getConfig().getHttpServerPort());
			_httpServer.start();
		}
	}
	
	protected void validateDataTypes( IdentifierValues values ) throws Exception {
		for( String type : values.getTypes() ) {
			if (!dataTypeSvc.getFactory().containsType(type)) {
				throw new Exception("Input value type [" + type + "] is not a valid type");
			}
		}
	}
}
