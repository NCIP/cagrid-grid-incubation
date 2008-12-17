package org.cagrid.workflow.manager.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.OutputReady;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.globus.wsrf.NotifyCallback;

public class NotificationProxy implements NotifyCallback {

	
	private static Log logger = LogFactory.getLog(NotificationProxy.class);


	// Synchronization for asynchronous calls
	private Map<String, Lock> asynchronousStartLock = new HashMap<String, Lock>();
	private Map<String, Boolean> asynchronousStartCallbackReceived = new HashMap<String, Boolean>();


	private Lock allNotificationsKey = new ReentrantLock();
	private Condition allNotificationsReceivedCondition = allNotificationsKey.newCondition();
	private boolean allNotificationsReceived = false;

	
	
	/**
	 * Wait for all registered notifications to be received 
	 *  */
	public void synchronize(){
		
		
		this.allNotificationsKey.lock();
		try{
			
			if(!this.allNotificationsReceived){
				this.allNotificationsReceivedCondition.await();
			}
			
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		} finally{
			this.allNotificationsKey.unlock();
		}
		
	}
	
	
	/** Add the ID of an entity that is supposed to send a notification
	 * */
	public void registerNotifierEPR(String notifierEPR){
		
		if( !this.asynchronousStartLock.containsKey(notifierEPR) ){
						
			Lock key = new ReentrantLock();
			this.asynchronousStartCallbackReceived.put(notifierEPR, Boolean.FALSE);
			this.asynchronousStartLock.put(notifierEPR, key);
		}		
	}
	
	
	
	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {

		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isOutputReady = message_qname.equals(OutputReady.getTypeDesc().getXmlType());
		String stageKey = null;
		try {
			stageKey = new WorkflowManagerInstanceClient(arg1).getEPRString(); 
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}   


		if(isOutputReady){

			stageKey = null;
			try {
				stageKey = new WorkflowManagerInstanceClient(arg1).getEPRString();   

				if(stageKey == null){
					logger.error("[NotificationProxy::deliver] Unable to retrieve stageKey");
				}

			} catch (RemoteException e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage(), e1);
			} catch (MalformedURIException e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage(), e1);
			} 


			OutputReady callback = null;
			try {
				callback = (OutputReady) actual_property.getValueAsType(message_qname, OutputReady.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Boolean notificationValue = new Boolean(callback.equals(OutputReady.TRUE));


			// Store/Update the value stored internally for the current InvocationHelper
			Lock mutex = this.asynchronousStartLock.get(stageKey);
			mutex.lock();
			try {


				this.asynchronousStartCallbackReceived.put(stageKey, notificationValue);

				// If the execution is finished, report the user
				boolean allCallbacksReceived = !this.asynchronousStartCallbackReceived.containsValue(Boolean.FALSE);
				if(allCallbacksReceived){

					System.out.println("[NotificationProxy::deliver] All callbacks received. Execution is finished.");   //DEBUG
					this.allNotificationsReceivedCondition.signalAll();
				}


			} finally {
				mutex.unlock();
			}
		}
		else{
			logger.error("[NotificationProxy::deliver] Callback received from an unknown stage: "+ stageKey);
		}
	}
}
