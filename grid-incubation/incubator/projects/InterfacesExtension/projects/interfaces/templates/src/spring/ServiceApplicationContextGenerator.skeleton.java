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
