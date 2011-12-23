package org.cagrid.tide.replica.context.service;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.tide.descriptor.TideReplicaDescriptor;
import org.cagrid.tide.descriptor.TideReplicasDescriptor;
import org.cagrid.tide.replica.service.TideReplicaManagerConfiguration;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TideReplicaManagerContextImpl extends TideReplicaManagerContextImplBase {

    public TideReplicaManagerContextImpl() throws RemoteException {
        super();
    }

  public void addReplicaHost(org.apache.axis.message.addressing.EndpointReferenceType tideContextReference) throws RemoteException {
        try {
            TideReplicasDescriptor replicas = getResourceHome().getAddressedResource().getTideReplicasDescriptor();
            TideReplicaDescriptor[] oldreps = replicas.getTideReplicaDescriptor();
            TideReplicaDescriptor[] newreps;
            if (oldreps != null) {
                newreps = new TideReplicaDescriptor[oldreps.length + 1];
                System.arraycopy(oldreps, 0, newreps, 0, oldreps.length);
                newreps[oldreps.length] = new TideReplicaDescriptor(tideContextReference, 0);
            } else {
                newreps = new TideReplicaDescriptor[]{new TideReplicaDescriptor(tideContextReference, 0)};
            }
            replicas.setTideReplicaDescriptor(newreps);
            getResourceHome().getAddressedResource().setTideReplicasDescriptor(replicas);

        } catch (Exception e) {
            throw new RemoteException("Error", e);
        }
    }

  public org.cagrid.tide.descriptor.TideReplicasDescriptor getReplicas() throws RemoteException {
        try {
            return getResourceHome().getAddressedResource().getTideReplicasDescriptor();
        } catch (Exception e) {
            throw new RemoteException("Error", e);
        }
    }

    private void removeReplicaHost(EndpointReferenceType tideContextReference) throws Exception {
        TideReplicasDescriptor replicas = getResourceHome().getAddressedResource().getTideReplicasDescriptor();
        TideReplicaDescriptor[] oldreps = replicas.getTideReplicaDescriptor();
        if (oldreps != null && oldreps.length > 0) {
            TideReplicaDescriptor[] newreps = new TideReplicaDescriptor[oldreps.length - 1];
            int newI = 0;
            for (int i = 0; i < oldreps.length; i++) {
                if (!oldreps[i].getEndpointReference().equals(tideContextReference)) {
                    newreps[newI++] = oldreps[i];
                }
            }
            getResourceHome().getAddressedResource().setTideReplicasDescriptor(replicas);
        }
    }

  public void reportUnreachableReplicaHost(org.apache.axis.message.addressing.EndpointReferenceType tideContextReference) throws RemoteException {
        try {
            TideReplicasDescriptor replicas = getResourceHome().getAddressedResource().getTideReplicasDescriptor();
            TideReplicaDescriptor[] oldreps = replicas.getTideReplicaDescriptor();
            if (oldreps != null && oldreps.length > 0) {
                for (int i = 0; i < oldreps.length; i++) {
                    if (oldreps[i].getEndpointReference().equals(tideContextReference)) {
                        oldreps[i].setUnreachableCount(oldreps[i].getUnreachableCount() + 1);
                        if (oldreps[i].getUnreachableCount() >= Integer.parseInt(TideReplicaManagerConfiguration
                            .getConfiguration().getUnreachableHostRemovalLevel())) {
                            removeReplicaHost(tideContextReference);
                        }
                        return;
                    }
                }
            }
        } catch (Exception e) {
            throw new RemoteException("Error", e);
        }
    }

}
