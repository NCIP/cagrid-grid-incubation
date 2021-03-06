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
package org.cagrid.tide.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.tide.common.TideI;
import org.cagrid.tide.context.client.TideContextClient;
import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.tide.descriptor.WaveDescriptor;
import org.cagrid.tide.descriptor.WaveRequest;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.Status;
import org.globus.ftp.dc.TransferContext;
import org.globus.gsi.GlobusCredential;

import com.twmacinta.util.MD5InputStream;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS
 * METHODS. This client is generated automatically by Introduce to provide a
 * clean unwrapped API to the service. On construction the class instance will
 * contact the remote service and retrieve it's security metadata description
 * which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TideClient extends TideClientBase implements TideI {

    public TideClient(String url) throws MalformedURIException, RemoteException {
        this(url, null);
    }

    public TideClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
        super(url, proxy);
    }

    public TideClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
        this(epr, null);
    }

    public TideClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
        super(epr, proxy);
    }

    public static void usage() {
        System.out.println(TideClient.class.getName() + " -url <service url>");
    }

    public static void main(String[] args) {
        System.out.println("Running the Grid Service Client");
        try {
            if (!(args.length < 2)) {
                if (args[0].equals("-url")) {
//                    TideClient client = new TideClient(args[1]);
//                    // place client calls here if you want to use this main as a
//                    // test....
//
//                    File f = new File("c:/apache-ant-1.7.0-bin.zip");
//                    FileInputStream fis = new FileInputStream(f);
//                    TideDescriptor tide = new TideDescriptor();
//                    tide.setId("tideID2");
//                    tide.setChunkSize(1024*512);
//                    long numChunks = f.length() / (512*1024);
//                    if (f.length() % (512*1024) != 0) {
//                        numChunks += 1;
//                    }
//
//                    tide.setChunks(numChunks);
//                    tide.setName(f.getName());
//                    MD5InputStream mis = new MD5InputStream(fis);
//                    mis.getMD5().asHex();
//                    tide.setMd5Sum(mis.getMD5().asHex());

//                     TransferServiceContextReference tref =
//                     client.putTide(tide);
//                     TransferServiceContextClient tclient = new
//                     TransferServiceContextClient(tref.getEndpointReference());
//                     TransferClientHelper.putData(mis, f.length(),
//                     tclient.getDataTransferDescriptor());
//                     tclient.setStatus(Status.Staged);

//                    TideContextClient tideCclient = client.getTideContext(tide);
//                    long chunk = 0;
//                    while (chunk < numChunks) {
//                        WaveRequest wave = new WaveRequest(chunk++, "tideID2");
//
//                        WaveDescriptor desc = tideCclient.getWave(wave);
//                        System.out.println("Wave Size: " + desc.getSize());
//                        System.out.println("Wave StartP: " + desc.getStartpos());
//                        System.out.println("Wave StopP: " + desc.getStoppos());
//                        TransferServiceContextReference ref = desc.getTransferServiceContextReference();
//                        TransferServiceContextClient transclient = new TransferServiceContextClient(ref
//                            .getEndpointReference());
//                        InputStream is = TransferClientHelper.getData(transclient.getDataTransferDescriptor());
//                        int read;
//                        int count = 0;
//                        while ((read = is.read()) != -1) {
//                            count++;
//                        }
//                        System.out.println("READ: " + count);
//                        
//                        transclient.destroy();
//                    }

                } else {
                    usage();
                    System.exit(1);
                }
            } else {
                usage();
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

  public org.cagrid.tide.context.client.TideContextClient getTideContext(java.lang.String tideID) throws RemoteException, org.apache.axis.types.URI.MalformedURIException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getTideContext");
    org.cagrid.tide.stubs.GetTideContextRequest params = new org.cagrid.tide.stubs.GetTideContextRequest();
    params.setTideID(tideID);
    org.cagrid.tide.stubs.GetTideContextResponse boxedResult = portType.getTideContext(params);
    EndpointReferenceType ref = boxedResult.getTideContextReference().getEndpointReference();
    return new org.cagrid.tide.context.client.TideContextClient(ref);
    }
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference putTide(org.cagrid.tide.descriptor.TideDescriptor tideDescriptor) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"putTide");
    org.cagrid.tide.stubs.PutTideRequest params = new org.cagrid.tide.stubs.PutTideRequest();
    org.cagrid.tide.stubs.PutTideRequestTideDescriptor tideDescriptorContainer = new org.cagrid.tide.stubs.PutTideRequestTideDescriptor();
    tideDescriptorContainer.setTideDescriptor(tideDescriptor);
    params.setTideDescriptor(tideDescriptorContainer);
    org.cagrid.tide.stubs.PutTideResponse boxedResult = portType.putTide(params);
    return boxedResult.getTransferServiceContextReference();
    }
  }

}
