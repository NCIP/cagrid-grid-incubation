package gov.nih.nci.cagrid.identifiers.service;

import org.cagrid.identifiers.namingauthority.impl.NamingAuthorityImpl;

import gov.nih.nci.cagrid.identifiers.common.MappingUtil;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class IdentifiersNAServiceImpl extends IdentifiersNAServiceImplBase {

	private NamingAuthorityImpl _na;
	
	public IdentifiersNAServiceImpl() throws RemoteException {
		super();
		
		NamingAuthorityConfigImpl config = new NamingAuthorityConfigImpl();
		try {
			config.setPrefix(getConfiguration().getIdentifiersNaPrefix());
			config.setHttpServerPort(Integer.parseInt(getConfiguration().getIdentifiersNaHttpServerPort()));
			config.setGridSvcUrl(getConfiguration().getIdentifiersNaGridSvcUrl());
			config.setDbUrl(getConfiguration().getIdentifiersNaDbUrl());
			config.setDbUser(getConfiguration().getIdentifiersNaDbUser());
			config.setDbPassword(getConfiguration().getIdentifiersNaDbPassword());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_na = new NamingAuthorityImpl(config);
		System.out.println("IdentifiersNAServiceImpl Created...");
		
		//Start a web server for http resolution
		_na.startHttpServer();
	}
	

  public java.lang.String createIdentifier(gov.nih.nci.cagrid.identifiers.TypeValuesMap typeValues) throws RemoteException {
	  return _na.create(MappingUtil.toIdentifierValues(typeValues));
  }

  public gov.nih.nci.cagrid.identifiers.TypeValuesMap getTypeValues(java.lang.String identifier) throws RemoteException {
	  return MappingUtil.toTypeValuesMap(_na.getValues(identifier));
  }

}

