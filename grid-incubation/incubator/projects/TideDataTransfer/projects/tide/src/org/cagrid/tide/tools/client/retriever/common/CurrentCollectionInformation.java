/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.tide.tools.client.retriever.common;


public class CurrentCollectionInformation {
    private long collectionTime = 0;
    private long bytesRead = 0;
    private long chunkNumber;
    
    public CurrentCollectionInformation(int chunkNumber){
        this.chunkNumber = chunkNumber;
    }

    public long getChunkNumber() {
        return chunkNumber;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }
    
}
