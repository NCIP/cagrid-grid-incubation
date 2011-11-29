package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;

import java.util.HashMap;
import java.util.Map;

import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;


public class AuthorizationManagerFactory {

    private Map<String, AuthorizationManager> managers;
    private AuthorizationManager auth;
    private static CSMProperties configuration;


    public AuthorizationManagerFactory(CSMProperties configuration) throws CSMInternalFault {
        this.configuration = configuration;
        this.managers = new HashMap<String, AuthorizationManager>();
        this.auth = CSMInitializer.getAuthorizationManager(configuration.getDatabaseProperties());
    }

    public AuthorizationManager getDefaultAuthorizationManager() {
        return this.auth;
    }


    public AuthorizationManager getAuthorizationManager(long applicationId) throws CSMInternalFault {
        String key = String.valueOf(applicationId);
        if (this.managers.containsKey(key)) {
            return this.managers.get(key);
        } else {
            AuthorizationManager am = CSMUtils.getAuthorizationManager(this.auth, configuration.getDatabaseProperties(),
                applicationId);
            this.managers.put(key, am);
            return am;
        }
    }

}
