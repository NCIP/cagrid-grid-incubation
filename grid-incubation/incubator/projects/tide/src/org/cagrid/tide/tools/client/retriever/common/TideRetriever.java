package org.cagrid.tide.tools.client.retriever.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.cagrid.tide.descriptor.Current;
import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.tide.descriptor.TideReplicaDescriptor;
import org.cagrid.tide.descriptor.TideReplicasDescriptor;
import org.cagrid.tide.replica.stubs.types.TideReplicaManagerReference;


/**
 * Base Class for a Retriever Algorithm.
 * 
 * @author hastings
 */
public abstract class TideRetriever implements FailedWriterCallback, FailedCollectorCallback, FinishedCollectorCallback {
    private String tideID;
    private TideReplicaManagerReference replicaServer;
    private TideReplicasDescriptor replicasDescriptor;
    private CurrentWriter writer;
    private Map<TideReplicaDescriptor, CurrentsCollectionInformation> currentsCollectionInformation;


    public TideRetriever(String tideID, File tideStorageFile, TideReplicaManagerReference replicaServer,
        TideReplicasDescriptor replicasDescriptor) throws Exception {
        this.tideID = tideID;
        this.replicaServer = replicaServer;
        this.replicasDescriptor = replicasDescriptor;
        this.writer = new CurrentWriter(tideStorageFile, replicasDescriptor.getTideDescriptor(), this);
        this.currentsCollectionInformation = new HashMap<TideReplicaDescriptor, CurrentsCollectionInformation>();
    }


    public void retrieve() throws Exception {
        Thread th = new Thread(writer);
        th.start();
        executeRetrievalAlgothim();
        th.join();
    }


    public abstract void executeRetrievalAlgothim() throws Exception;


    public String getTideID() {
        return tideID;
    }


    public TideReplicaManagerReference getReplicaServer() {
        return replicaServer;
    }


    public TideReplicasDescriptor getReplicasDescriptor() {
        return replicasDescriptor;
    }


    public CurrentWriter getWriter() {
        return writer;
    }


    public void failedCollector(CurrentCollector collector) {
        System.out.println("Failed Collector");
    }


    public void failedWriter(CurrentWriter writer) {
        System.out.println("Failed Writer");

    }


    public void failedCurrent(Current current, TideDescriptor tideDescriptor, TideReplicaDescriptor tideRepDescriptor) {
        System.out.println("Failed Current in Collector");
    }


    public void finishedCollector(CurrentCollector collector) {
        // TODO Auto-generated method stub

    }


    public void finishedCurrent(CurrentCollector collector, CurrentCollectionInformation colInfo) {
        CurrentsCollectionInformation currentsCollectorInfo = null;
        if(this.currentsCollectionInformation.containsKey(collector.getTideRep())){
            currentsCollectorInfo = new CurrentsCollectionInformation(collector.getTideRep());
           this.currentsCollectionInformation.put(collector.getTideRep(), currentsCollectorInfo);
        } else {
            currentsCollectorInfo = this.currentsCollectionInformation.get(collector.getTideRep());
        }
        
    }

}
