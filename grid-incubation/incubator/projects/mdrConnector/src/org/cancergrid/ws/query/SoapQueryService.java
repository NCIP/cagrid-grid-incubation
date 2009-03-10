/**
 * Copyright (c) 2005-2008 CancerGrid Consortium <http://www.cancergrid.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,  
 * and to permit persons to whom the Software is furnished to do so, subject to the 
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies 
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package org.cancergrid.ws.query;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.log4j.Logger;
import org.cancergrid.schema.query.Query;

/**
 * Query service that works with web services the uses SOAP interface. This service works
 * directly with XML rather than going through web service stubs generation
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cancergrid.org">CancerGrid Consortium</a>)
 * @version 1.0
 * 
 * @see org.cancergrid.ws.query.QueryService
 */

public class SoapQueryService extends QueryService
{
	/**
	 * Log4J Logger
	 */
	private static Logger LOG = Logger.getLogger(SoapQueryService.class);
	
	/**
	 * Generic web service service client
	 */
	private ServiceClient service = null;
	
	/**
	 * Option settings for the service client
	 */
	private Options options = null;
	
	@Override
	public void initService() 
	{
		/**
		 * Initialize the service client
		 */
		try {
			if (options == null)
			{
				EndpointReference targetEPR = new EndpointReference(serviceUrl.toExternalForm());
				options = new Options();
				options.setTo(targetEPR);
				options.setTimeOutInMilliSeconds(60*1000);
			}

			service = new ServiceClient();
			service.setOptions(options);
		} catch (Exception e) {
			LOG.error("SoapQueryService.initService: " + e);
		}
	}
	
	/**
	 * Get current settings for service client
	 * @return settings for service client
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * Set settings for service client
	 * @param options settings to apply
	 */
	public void setOptions(Options options) {
		this.options = options;
		service.setOptions(options);
	}

	@Override
	protected String executeQuery(Query query)
	{
		try {
			OMElement method = constructMethod(query);
			OMElement result = invoke(method);
			return result.getFirstElement().toString();
		} catch (Exception e) {
			LOG.error("SoapQueryService.executeQuery: "+e);
			return null;
		}
	}
	
	/**
	 * Construct service specific soap request using the given query request
	 * @param query query request
	 * @return service specific soap request
	 */
	protected OMElement constructMethod(Query query)
	{
		try {
			org.cancergrid.schema.query.QueryDocument qrDoc = org.cancergrid.schema.query.QueryDocument.Factory.newInstance();
			qrDoc.setQuery(query);
			String method = transform.applyTemplates(qrDoc.toString(), requestSequence);
			XMLStreamReader p = XMLInputFactory.newInstance().createXMLStreamReader(new java.io.StringReader(method.toString()));
			StAXOMBuilder builder = new StAXOMBuilder(p);
			OMElement methodOM = builder.getDocumentElement();
			return methodOM;
		} catch (Exception e)
		{
			LOG.error("SoapQueryService.constructMethod: "+e);
			return null;
		}
	}
	
	/**
	 * Make the soap request to vocabulary/metadata services
	 * @param method soap request to use
	 * @return soap response
	 * @throws Exception
	 */
	protected OMElement invoke(OMElement method) throws Exception
	{
		try {
			//Initialize the soapAction value
			String action = method.getNamespace().getNamespaceURI();
			if (!action.endsWith("/"))
			{
				action += "/";
			}
			action += method.getLocalName();
			options.setAction(action);
			
			//Axis2 seems to have problem understanding soapAction
			options.setProperty(org.apache.axis2.addressing.AddressingConstants.DISABLE_ADDRESSING_FOR_OUT_MESSAGES, Boolean.TRUE);
			
			return service.sendReceive(method);
		} catch (Exception e) {
			LOG.error("SoapQueryService.invoke: "+e);
			return null;
		}
	}
}
