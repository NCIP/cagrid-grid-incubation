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
package org.cagrid.gaards.csm.service;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.AbstractResource;

public class BeanUtils {

	private XmlBeanFactory factory;

	public BeanUtils(AbstractResource csmConf, AbstractResource csmProperties) {
		this.factory = new XmlBeanFactory(csmConf);
		PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
		cfg.setLocation(csmProperties);
		cfg.postProcessBeanFactory(factory);
	}

	public CSMProperties getCSMProperties() throws Exception {
		CSMProperties props = (CSMProperties) factory
				.getBean(Constants.CSM_PROPERTIES);
		return props;
	}
}
