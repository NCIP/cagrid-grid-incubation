package org.cagrid.workflow.helper.invocation.client;

import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.invocation.stubs.WorkflowInvocationHelperPortType;
import org.cagrid.workflow.helper.invocation.stubs.service.WorkflowInvocationHelperServiceAddressingLocator;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NotificationConsumerManager;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.container.ContainerException;
import org.oasis.wsrf.lifetime.ImmediateResourceTermination;
import org.oasis.wsrf.lifetime.WSResourceLifetimeServiceAddressingLocator;


/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 * 
 * @created by Introduce Toolkit version 1.2
 */
public abstract class WorkflowInvocationHelperClientBase extends ServiceSecurityClient implements NotifyCallback {	
	protected WorkflowInvocationHelperPortType portType;
	protected Object portTypeMutex;
    protected NotificationConsumerManager consumer = null;
    protected EndpointReferenceType consumerEPR = null;

	public WorkflowInvocationHelperClientBase(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(url,proxy);
	   	initialize();
	}
	
	public WorkflowInvocationHelperClientBase(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
	   	super(epr,proxy);
		initialize();
	}
	
	private void initialize() throws RemoteException {
	    this.portTypeMutex = new Object();
		this.portType = createPortType();
	}

	private WorkflowInvocationHelperPortType createPortType() throws RemoteException {

		WorkflowInvocationHelperServiceAddressingLocator locator = new WorkflowInvocationHelperServiceAddressingLocator();
		// attempt to load our context sensitive wsdd file
		InputStream resourceAsStream = getClass().getResourceAsStream("client-config.wsdd");
		if (resourceAsStream != null) {
			// we found it, so tell axis to configure an engine to use it
			EngineConfiguration engineConfig = new FileProvider(resourceAsStream);
			// set the engine of the locator
			locator.setEngine(new AxisClient(engineConfig));
		}
		WorkflowInvocationHelperPortType port = null;
		try {
			port = locator.getWorkflowInvocationHelperPortTypePort(getEndpointReference());
		} catch (Exception e) {
			throw new RemoteException("Unable to locate portType:" + e.getMessage(), e);
		}

		return port;
	}
	
	public org.oasis.wsrf.lifetime.DestroyResponse destroy() throws RemoteException {
        synchronized (portTypeMutex) {
            org.oasis.wsrf.lifetime.Destroy params = new org.oasis.wsrf.lifetime.Destroy();
            configureStubSecurity((Stub) portType, "destroy");
            return portType.destroy(params);
        }
    }


    public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(Calendar terminationTime)
        throws RemoteException {
        synchronized (portTypeMutex) {
            configureStubSecurity((Stub) portType, "setTerminationTime");
            org.oasis.wsrf.lifetime.SetTerminationTime params = new org.oasis.wsrf.lifetime.SetTerminationTime(
                terminationTime);
            return portType.setTerminationTime(params);

        }
    }
    
    public void unSubscribe(EndpointReferenceType subscriptionEPR) throws Exception {
        WSResourceLifetimeServiceAddressingLocator locator = new WSResourceLifetimeServiceAddressingLocator();
        ImmediateResourceTermination port = locator.getImmediateResourceTerminationPort(subscriptionEPR);
        port.destroy(new org.oasis.wsrf.lifetime.Destroy());
    }


    public org.oasis.wsn.SubscribeResponse subscribe(QName qname) throws RemoteException, ContainerException, MalformedURIException {
        synchronized (portTypeMutex) {
            configureStubSecurity((Stub) portType, "subscribe");

            if (consumer == null) {
                // Create client side notification consumer
                consumer = org.globus.wsrf.NotificationConsumerManager.getInstance();
                consumer.startListening();
                consumerEPR = consumer.createNotificationConsumer(this);
            }

            org.oasis.wsn.Subscribe params = new org.oasis.wsn.Subscribe();
            params.setUseNotify(Boolean.TRUE);
            params.setConsumerReference(consumerEPR);
            org.oasis.wsn.TopicExpressionType topicExpression = new org.oasis.wsn.TopicExpressionType();
            topicExpression.setDialect(org.globus.wsrf.WSNConstants.SIMPLE_TOPIC_DIALECT);
            topicExpression.setValue(qname);
            params.setTopicExpression(topicExpression);
            return portType.subscribe(params);
       }
    }


    public void deliver(List topicPath, EndpointReferenceType producer, Object message) {
        org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) message)
            .getResourcePropertyValueChangeNotification();

        if (changeMessage != null) {
            System.out.println("Got notification");
        }
    }

}