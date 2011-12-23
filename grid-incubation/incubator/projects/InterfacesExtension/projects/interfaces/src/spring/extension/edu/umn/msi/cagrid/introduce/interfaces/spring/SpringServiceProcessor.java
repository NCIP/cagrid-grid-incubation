package edu.umn.msi.cagrid.introduce.interfaces.spring;

import edu.umn.msi.cagrid.introduce.interfaces.services.CaGridService;
import edu.umn.msi.cagrid.introduce.interfaces.services.Service;
import edu.umn.msi.cagrid.introduce.interfaces.services.ServiceProcessor;

public class SpringServiceProcessor implements ServiceProcessor {
  private SpringConfiguration springConfiguration;
  
  public SpringServiceProcessor(CaGridService caGridService, SpringConfiguration springConfiguration) {
    this.springConfiguration = springConfiguration;
  }
  
  public void execute(Service service) throws Exception {
    String serviceName = service.getName();
    StringBuffer serviceSource = new StringBuffer(service.getServiceImplContents());
    ServiceImplProcessor implProcessor = new ServiceImplProcessor(serviceSource,  serviceName, service.getCaGridService().getMainService().getName(), springConfiguration);
    implProcessor.execute();
    service.setServiceImplContents(serviceSource.toString());
  }
}
