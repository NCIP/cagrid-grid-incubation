package org.cagrid.tide.tools.client.retriever.common;



public interface FinishedCollectorCallback {
    public void finishedCollector(CurrentCollector collector);
    public void finishedCurrent(CurrentCollector collector,  CurrentCollectionInformation colInfo);
}
