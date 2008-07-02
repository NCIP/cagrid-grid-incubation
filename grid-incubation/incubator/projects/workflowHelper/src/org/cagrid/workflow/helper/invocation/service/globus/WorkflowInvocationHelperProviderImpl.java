package org.cagrid.workflow.helper.invocation.service.globus;

import org.cagrid.workflow.helper.invocation.service.WorkflowInvocationHelperImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the WorkflowHelperImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInvocationHelperProviderImpl{
	
	WorkflowInvocationHelperImpl impl;
	
	public WorkflowInvocationHelperProviderImpl() throws RemoteException {
		impl = new WorkflowInvocationHelperImpl();
	}
	

    public org.cagrid.workflow.helper.invocation.stubs.ConfigureInputResponse configureInput(org.cagrid.workflow.helper.invocation.stubs.ConfigureInputRequest params) throws RemoteException {
    org.cagrid.workflow.helper.invocation.stubs.ConfigureInputResponse boxedResult = new org.cagrid.workflow.helper.invocation.stubs.ConfigureInputResponse();
    impl.configureInput(params.getOperationInputMessageDescriptor().getOperationInputMessageDescriptor());
    return boxedResult;
  }

    public org.cagrid.workflow.helper.invocation.stubs.ConfigureOutputResponse configureOutput(org.cagrid.workflow.helper.invocation.stubs.ConfigureOutputRequest params) throws RemoteException {
    org.cagrid.workflow.helper.invocation.stubs.ConfigureOutputResponse boxedResult = new org.cagrid.workflow.helper.invocation.stubs.ConfigureOutputResponse();
    impl.configureOutput(params.getOperationOutputTransportDescriptor().getOperationOutputTransportDescriptor());
    return boxedResult;
  }

    public org.cagrid.workflow.helper.invocation.stubs.SetParameterResponse setParameter(org.cagrid.workflow.helper.invocation.stubs.SetParameterRequest params) throws RemoteException {
    org.cagrid.workflow.helper.invocation.stubs.SetParameterResponse boxedResult = new org.cagrid.workflow.helper.invocation.stubs.SetParameterResponse();
    impl.setParameter(params.getInputParameter().getInputParameter());
    return boxedResult;
  }

    public org.cagrid.workflow.helper.invocation.stubs.SetParametersResponse setParameters(org.cagrid.workflow.helper.invocation.stubs.SetParametersRequest params) throws RemoteException {
    org.cagrid.workflow.helper.invocation.stubs.SetParametersResponse boxedResult = new org.cagrid.workflow.helper.invocation.stubs.SetParametersResponse();
    impl.setParameters(params.getInputParameters().getInputParameter());
    return boxedResult;
  }

    public org.cagrid.workflow.helper.invocation.stubs.GetEPRStringResponse getEPRString(org.cagrid.workflow.helper.invocation.stubs.GetEPRStringRequest params) throws RemoteException {
    org.cagrid.workflow.helper.invocation.stubs.GetEPRStringResponse boxedResult = new org.cagrid.workflow.helper.invocation.stubs.GetEPRStringResponse();
    boxedResult.setResponse(impl.getEPRString());
    return boxedResult;
  }

    public org.cagrid.workflow.helper.invocation.stubs.StartResponse start(org.cagrid.workflow.helper.invocation.stubs.StartRequest params) throws RemoteException {
    org.cagrid.workflow.helper.invocation.stubs.StartResponse boxedResult = new org.cagrid.workflow.helper.invocation.stubs.StartResponse();
    impl.start();
    return boxedResult;
  }

}
