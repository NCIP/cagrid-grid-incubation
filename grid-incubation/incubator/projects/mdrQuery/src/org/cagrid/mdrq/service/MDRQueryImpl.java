package org.cagrid.mdrq.service;

import java.io.File;
import java.rmi.RemoteException;

import org.cancergrid.ws.query.QueryServiceManager;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 */
public class MDRQueryImpl extends MDRQueryImplBase {

    QueryServiceManager queryManager;

    public MDRQueryImpl() throws RemoteException {
        super();
        try {
            queryManager = new QueryServiceManager(new File(MDRQueryConfiguration.getConfiguration()
                .getEtcDirectoryPath()
                + File.separator + "stylesheets"), new File(MDRQueryConfiguration.getConfiguration().getConfigFile()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getMessage(), e);
        }
    }

  public org.cancergrid.schema.result_set.ResultSet query(org.cancergrid.schema.query.Query query) throws RemoteException {
        return queryManager.query(query);
    }

  public org.cancergrid.schema.config.Resources getQueryResources() throws RemoteException {
        return queryManager.getQueryServiceConfig().getConfig().getResources();
    }

}
