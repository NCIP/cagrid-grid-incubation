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
package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import org.springframework.beans.factory.support.MethodReplacer;

public interface BeanProxyMethodReplacer extends MethodReplacer {
  public void setBeanProxyInfo(BeanProxyInfo beanProxyInfo);
}
