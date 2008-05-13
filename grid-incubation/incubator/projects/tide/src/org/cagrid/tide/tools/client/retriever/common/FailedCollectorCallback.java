package org.cagrid.tide.tools.client.retriever.common;

import org.cagrid.tide.descriptor.Current;
import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.tide.descriptor.TideReplicaDescriptor;


public interface FailedCollectorCallback {
    public void failedCollector(CurrentCollector collector);
    public void failedCurrent(Current current, TideDescriptor tideDescriptor, TideReplicaDescriptor tideRepDescriptor);
}
