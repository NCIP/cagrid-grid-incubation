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
package org.cagrid.tide.tools.client.retriever.common;

import org.cagrid.tide.descriptor.TideReplicaDescriptor;


public class CurrentsCollectionInformation {
    TideReplicaDescriptor tideRep;


    public CurrentsCollectionInformation(TideReplicaDescriptor tideRep) {
        this.tideRep = tideRep;
    }

}
