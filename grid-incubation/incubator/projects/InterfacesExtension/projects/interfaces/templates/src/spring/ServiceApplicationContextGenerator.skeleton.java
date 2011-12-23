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