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
package org.cagrid.i2b2.ontomapper.utils;

public class ObjectAssemblyException extends Exception {

    public ObjectAssemblyException() {
        super();
    }


    public ObjectAssemblyException(String message) {
        super(message);
    }


    public ObjectAssemblyException(Throwable cause) {
        super(cause);
    }


    public ObjectAssemblyException(String message, Throwable cause) {
        super(message, cause);
    }
}
