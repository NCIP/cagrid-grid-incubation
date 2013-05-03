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
package edu.umn.msi.cagrid.introduce.interfaces.spring;

import edu.umn.msi.cagrid.introduce.interfaces.services.CaGridService;


public class CLASS {
  private CaGridService caGridService;
  
  public String getPackage() {
    return caGridService.getMainService().getServicePackageName();
  }
  
  public CaGridService getCaGridService() {
    return caGridService;
  }
  
  public void setCaGridService(CaGridService caGridService) {
    this.caGridService = caGridService;
  }
 
  public String generate() {
    return "";
  }
}
