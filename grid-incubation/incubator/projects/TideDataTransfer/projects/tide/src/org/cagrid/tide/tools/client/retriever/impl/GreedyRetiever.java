package org.cagrid.tide.tools.client.retriever.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.tide.descriptor.Current;
import org.cagrid.tide.descriptor.TideReplicaDescriptor;
import org.cagrid.tide.descriptor.TideReplicasDescriptor;
import org.cagrid.tide.replica.stubs.types.TideReplicaManagerReference;
import org.cagrid.tide.tools.client.retriever.common.CurrentCollector;
import org.cagrid.tide.tools.client.retriever.common.RetrieverWorkerPool;
import org.cagrid.tide.tools.client.retriever.common.TideRetriever;


/**
 * Simple retriever algorithm for balancing the data to be retrieved across the
 * registered data servers.
 * 
 * @author hastings
 */
public class GreedyRetiever extends TideRetriever {
    

    public GreedyRetiever(String tideID, File tideStorageFile, TideReplicaManagerReference replicaServer,
        TideReplicasDescriptor replicasDescriptor) throws Exception {
        super(tideID, tideStorageFile, replicaServer, replicasDescriptor);
    }


    public void executeRetrievalAlgothim() throws Exception {

        // currently assumes that the number of chunks is more than the number
        // of replications......
        // currently assumes that it should use all the replicas.....
        int chunksPerReplica = getReplicasDescriptor().getTideDescriptor().getTideInformation().getChunks()
            / getReplicasDescriptor().getTideReplicaDescriptor().length;
        int chunkNum = 0;
        for (int i = 0; i < getReplicasDescriptor().getTideReplicaDescriptor().length; i++) {
            TideReplicaDescriptor tideRep = getReplicasDescriptor().getTideReplicaDescriptor()[i];
            System.out.println("Retrieve from: " + tideRep.getEndpointReference());
            List<Current> currentList = new ArrayList<Current>();
            for (int j = 0; j < chunksPerReplica; j++) {
                Current curr = getReplicasDescriptor().getTideDescriptor().getCurrents().getCurrent(chunkNum++);
                if (curr != null) {
                    currentList.add(curr);
                }

            }
            Current[] currs = new Current[currentList.size()];
            currentList.toArray(currs);
            CurrentCollector collector = new CurrentCollector(currs, getWriter(), getReplicasDescriptor()
                .getTideDescriptor(), tideRep, GreedyRetiever.this);

            RetrieverWorkerPool.getInstance().submit(collector);
        }

    }

}