package org.cagrid.tide.service;

import java.rmi.RemoteException;

import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.tide.service.ingestor.TideIngestor;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.wsrf.ResourceException;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TideImpl extends TideImplBase {
    TideIngestor ingestor = null;

    public TideImpl() throws RemoteException {
        super();
        try {
            TideConfiguration.getConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            ingestor = (TideIngestor)Class.forName(TideConfiguration.getConfiguration().getTideIngestor()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(ingestor == null){
            try {
                throw new RemoteException("Cannot load the ingestion class: " + TideConfiguration.getConfiguration().getTideIngestor());
            } catch (Exception e) {
               throw new RemoteException("Cannot loat the TideConfiguration");
            }
        }
    }

  public org.cagrid.tide.context.stubs.types.TideContextReference getTideContext(java.lang.String tideID) throws RemoteException {
        org.cagrid.tide.context.service.globus.resource.TideContextResourceHome home = null;
        org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
        String servicePath = ctx.getTargetService();
        String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + "tideContextHome";

        try {
            javax.naming.Context initialContext = new javax.naming.InitialContext();
            home = (org.cagrid.tide.context.service.globus.resource.TideContextResourceHome) initialContext
                .lookup(homeName);
            return home.getResourceReference(tideID);
        } catch (Exception e) {
            throw new RemoteException("Error looking up TideContext home:" + e.getMessage(), e);
        }

    }

    public void createTide(TideDescriptor tideDescriptor) throws RemoteException {

        org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
        org.cagrid.tide.context.service.globus.resource.TideContextResourceHome home = null;
        org.globus.wsrf.ResourceKey resourceKey = null;
        org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
        String servicePath = ctx.getTargetService();
        String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + "tideContextHome";

        try {
            javax.naming.Context initialContext = new javax.naming.InitialContext();
            home = (org.cagrid.tide.context.service.globus.resource.TideContextResourceHome) initialContext
                .lookup(homeName);
            resourceKey = home.createResource(tideDescriptor.getTideInformation().getId());

            // Grab the newly created resource
            org.cagrid.tide.context.service.globus.resource.TideContextResource thisResource = (org.cagrid.tide.context.service.globus.resource.TideContextResource) home
                .find(resourceKey);

            // This is where the creator of this resource type can set whatever
            // needs
            // to be set on the resource so that it can function appropriatly
            // for instance
            // if you want the resouce to only have the query string then there
            // is where you would
            // give it the query string.
            thisResource.setTideDescriptor(tideDescriptor);

            // sample of setting creator only security. This will only allow the
            // caller that created
            // this resource to be able to use it.
            // thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils
            // .createCreatorOnlyResourceSecurityDescriptor());

            String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
            transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
            transportURL += "TideContext";
            epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);
        } catch (Exception e) {
            throw new RemoteException("Error looking up TideContext home:" + e.getMessage(), e);
        }

    }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference putTide(org.cagrid.tide.descriptor.TideDescriptor tideDescriptor) throws RemoteException {
        final org.cagrid.tide.descriptor.TideDescriptor ftideDescriptor = tideDescriptor;
        final TideIngestor fingestor = ingestor;
        DataStagedCallback callback = new DataStagedCallback() {
            public void dataStaged(TransferServiceContextResource resource) {
                // TODO Auto-generated method stub
                try {
                    fingestor.ingest(ftideDescriptor, resource.getDataStorageDescriptor());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    resource.remove();
                } catch (ResourceException e) {
                    e.printStackTrace();
                }
            }

        };
        createTide(tideDescriptor);
        return TransferServiceHelper.createTransferContext(new DataDescriptor(null, tideDescriptor.getTideInformation().getId()), callback);
    }

}
