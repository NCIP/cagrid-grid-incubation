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
package edu.umn.msi.cagrid.introduce.interfaces.types.mapping;

import javax.xml.namespace.QName;

public interface TypeMapping {
  public QName getQName(String canonicalName);
  public QName getQName(Class<? extends Object> class_);
}
