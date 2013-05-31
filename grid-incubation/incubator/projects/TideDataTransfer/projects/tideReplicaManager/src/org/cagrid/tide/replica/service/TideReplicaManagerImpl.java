/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.tide.replica.service;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.tide.descriptor.TideInformation;
import org.cagrid.tide.descriptor.TideReplicaDescriptor;
import org.cagrid.tide.descriptor.TideReplicasDescriptor;
import org.cagrid.tide.replica.context.service.globus.resource.TideReplicaManagerContextResource;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.descriptor.DataDescriptor;
import org.globus.wsrf.impl.SimpleResourceKey;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TideReplicaManagerImpl extends TideReplicaManagerImplBase {

	public TideReplicaManagerImpl() throws RemoteException {
		super();
	}

  public org.cagrid.tide.replica.context.stubs.types.TideReplicaManagerContextReference createTideReplicaManagerContext(org.cagrid.tide.descriptor.TideDescriptor tideDescriptor) throws RemoteException {
		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		org.cagrid.tide.replica.context.service.globus.resource.TideReplicaManagerContextResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
				.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
				+ servicePath + "/" + "tideReplicaManagerContextHome";

		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (org.cagrid.tide.replica.context.service.globus.resource.TideReplicaManagerContextResourceHome) initialContext
					.lookup(homeName);
			resourceKey = home.createResource(tideDescriptor
					.getTideInformation().getId());

			// Grab the newly created resource
			org.cagrid.tide.replica.context.service.globus.resource.TideReplicaManagerContextResource thisResource = (org.cagrid.tide.replica.context.service.globus.resource.TideReplicaManagerContextResource) home
					.find(resourceKey);

			// This is where the creator of this resource type can set whatever
			// needs
			// to be set on the resource so that it can function appropriatly
			// for instance
			// if you want the resouce to only have the query string then there
			// is where you would
			// give it the query string.
			TideReplicasDescriptor replicas = new TideReplicasDescriptor();
			replicas.setTideDescriptor(tideDescriptor);
			thisResource.setTideReplicasDescriptor(replicas);

			// sample of setting creator only security. This will only allow the
			// caller that created
			// this resource to be able to use it.
			// thisResource.setSecurityDescriptor(gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils.createCreatorOnlyResourceSecurityDescriptor());

			String transportURL = (String) ctx
					.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL
					.lastIndexOf('/') + 1);
			transportURL += "TideReplicaManagerContext";
			epr = org.globus.wsrf.utils.AddressingUtils
					.createEndpointReference(transportURL, resourceKey);
		} catch (Exception e) {
			throw new RemoteException(
					"Error looking up TideReplicaManagerContext home:"
							+ e.getMessage(), e);
		}

		// return the typed EPR
		org.cagrid.tide.replica.context.stubs.types.TideReplicaManagerContextReference ref = new org.cagrid.tide.replica.context.stubs.types.TideReplicaManagerContextReference();
		ref.setEndpointReference(epr);

		return ref;
	}

  public org.cagrid.tide.replica.context.stubs.types.TideReplicaManagerContextReference getTideReplicaManagerContext(java.lang.String tideID) throws RemoteException {
		org.cagrid.tide.replica.context.service.globus.resource.TideReplicaManagerContextResourceHome home = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
				.getCurrentContext();
		String servicePath = ctx.getTargetService();
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
				+ servicePath + "/" + "tideReplicaManagerContextHome";

		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (org.cagrid.tide.replica.context.service.globus.resource.TideReplicaManagerContextResourceHome) initialContext
					.lookup(homeName);

			return home.getResourceReference(tideID);

		} catch (Exception e) {
			throw new RemoteException(
					"Error looking up TideReplicaManagerContext home:"
							+ e.getMessage(), e);
		}
	}

	private List<TideInformation> getTides() {
		List<TideInformation> tides = new ArrayList<TideInformation>();
		try {
			File persistentDir = new File(TideReplicaManagerConfiguration
					.getConfiguration().getEtcDirectoryPath()
					+ File.separator
					+ "persisted"
					+ File.separator
					+ "TideReplicaManagerContextResourceProperties");
			File[] files = persistentDir.listFiles();
			for (File file : files) {
				TideReplicaManagerContextResource resource = getTideReplicaManagerContextResourceHome()
						.getResource(
								file.getName().substring(0,
										file.getName().lastIndexOf(".")));
				if (resource.getTideReplicasDescriptor().getTideDescriptor()
						.getTideInformation() != null) {
					tides.add(resource.getTideReplicasDescriptor()
							.getTideDescriptor().getTideInformation());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tides;
	}

  public org.cagrid.tide.descriptor.TideInformation[] listTides() throws RemoteException {
		List tidesL = getTides();
		TideInformation[] tides = new TideInformation[tidesL.size()];
		tidesL.toArray(tides);
		return tides;
	}

  public org.cagrid.tide.descriptor.TideInformation[] queryTides(java.lang.String string) throws RemoteException {
		string = string.toLowerCase();
		List<TideInformation> tidesL = getTides();
		List<TideInformation> newTidesL = new ArrayList<TideInformation>();
		for (TideInformation tide : tidesL) {
			if ((tide.getName()!=null && tide.getName().toLowerCase().indexOf(string) >= 0)
					|| (tide.getDescription()!=null && tide.getDescription().toLowerCase().indexOf(string) >= 0)
					|| (tide.getType()!=null && tide.getType().toLowerCase().indexOf(string) >= 0)) {
				newTidesL.add(tide);
			}
		}
		TideInformation[] tides = new TideInformation[newTidesL.size()];
		newTidesL.toArray(tides);
		return tides;
	}

}
