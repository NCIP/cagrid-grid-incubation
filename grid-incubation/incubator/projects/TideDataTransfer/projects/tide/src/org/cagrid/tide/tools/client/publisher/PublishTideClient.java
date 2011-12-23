package org.cagrid.tide.tools.client.publisher;

import gov.nih.nci.cagrid.common.FixedPortionFileInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.tide.client.TideClient;
import org.cagrid.tide.descriptor.Current;
import org.cagrid.tide.descriptor.Currents;
import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.tide.descriptor.TideInformation;
import org.cagrid.tide.replica.client.TideReplicaManagerClient;
import org.cagrid.tide.replica.context.client.TideReplicaManagerContextClient;
import org.cagrid.tide.replica.stubs.types.TideReplicaManagerReference;
import org.cagrid.tide.stubs.types.TideReference;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.Status;

import com.twmacinta.util.MD5InputStream;


public class PublishTideClient {

    
    
    public static TideDescriptor publishNewTideFixedChunks(File data, long chunkSize, TideReference tideServer,
        TideReplicaManagerReference replicaServer) throws Exception {
        String newID = UUID.randomUUID().toString();
        FileInputStream fis = new FileInputStream(data);
        TideDescriptor tide = new TideDescriptor();
        TideInformation info = new TideInformation();
        info.setId(newID);
        info.setSize(data.length());
        int numChunks = ((int)(data.length() / (chunkSize)));
        if (data.length() % (chunkSize) != 0) {
            numChunks += 1;
        }

        info.setChunks(numChunks);
        info.setName(data.getName());
        
        MD5InputStream mis = new MD5InputStream(fis);
        byte[] buf = new byte[65536];
        int num_read;
        while ((num_read = mis.read(buf)) != -1);
        mis.getMD5().asHex();
        
        info.setMd5Sum(mis.getMD5().asHex());
        
        tide.setTideInformation(info);
        
        Current[] currents = new Current[tide.getTideInformation().getChunks()];
        
        for(int i = 0; i < tide.getTideInformation().getChunks(); i++){
           MD5InputStream portionmis = new MD5InputStream(new FixedPortionFileInputStream(data, i*chunkSize, chunkSize));
           long chunkActualSize = 0;
           while ((num_read = portionmis.read(buf)) != -1){
               chunkActualSize +=num_read;
           }
           Current newCurrent = new Current(i,portionmis.getMD5().asHex(),i*chunkSize,chunkActualSize);
           currents[i] = newCurrent;
        }
     
        Currents newCurrents = new Currents(currents);
        tide.setCurrents(newCurrents);
        
        TideReplicaManagerClient repclient = new TideReplicaManagerClient(replicaServer.getEndpointReference());
        TideReplicaManagerContextClient repcontextclient = repclient.createTideReplicaManagerContext(tide);
        
        return publishTide(data, tide, tideServer, replicaServer);

    }
    
    public static TideDescriptor publishTide(File data, TideDescriptor tide, TideReference tideServer,
        TideReplicaManagerReference replicaServer) throws Exception {
        TideClient client = new TideClient(tideServer.getEndpointReference());

        TransferServiceContextReference tref = client.putTide(tide);
        
        System.out.println("Published Tide: " + tide.getTideInformation().getId());

        TransferServiceContextClient tclient = new TransferServiceContextClient(tref.getEndpointReference());
        InputStream is = new FileInputStream(data);
        TransferClientHelper.putData(is, data.length(), tclient.getDataTransferDescriptor());
        tclient.setStatus(Status.Staged);

        TideReplicaManagerClient repclient = new TideReplicaManagerClient(replicaServer.getEndpointReference());
        TideReplicaManagerContextClient repcontextclient = repclient.getTideReplicaManagerContext(tide.getTideInformation().getId());
        repcontextclient.addReplicaHost(client.getTideContext(tide.getTideInformation().getId()).getEndpointReference());
        
        return tide;

    }



    public static TideDescriptor publishNewTide(File data, TideReference tideServer, TideReplicaManagerReference replicaServer)
        throws Exception {
        return PublishTideClient.publishNewTideFixedChunks(data, 1024 * 512, tideServer, replicaServer);
    }
    
    
    public static void main(String [] args){
        File f = new File("c:/apache-ant-1.7.0-bin.zip");
        try {
            TideReference tideRef = new TideReference(new EndpointReferenceType(new Address("http://localhost:8080/wsrf/services/cagrid/Tide")));
            //TideReference tideRef2 = new TideReference(new EndpointReferenceType(new Address("http://localhost:8080/wsrf/services/cagrid/Tide")));
            TideReplicaManagerReference tideRepRef = new TideReplicaManagerReference(new EndpointReferenceType(new Address("http://localhost:8080/wsrf/services/cagrid/TideReplicaManager")));
            TideDescriptor desc = PublishTideClient.publishNewTide(f, tideRef, tideRepRef);
           // TideDescriptor anotherDesc = PublishTideClient.publishTide(f, desc, tideRef2, tideRepRef);
                    
        } catch (MalformedURIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }

}
