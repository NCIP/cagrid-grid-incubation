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
 */

package org.cagrid.openmdr.ws.query;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.StringWriter;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.cagrid.openmdr.ws.util.HttpContentReader;
import org.cancergrid.schema.query.Query;

/**
 * Query service that works with web services the uses REST interface
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cagrid.org">OpenMDR Consortium</a>)
 * @version 1.0
 * 
 * @see org.cagrid.openmdr.ws.query.XSLTransformQueryService
 */

public class RestQueryService extends XSLTransformQueryService
{
	/**
	 * Log4J Logger
	 */
	private static Logger LOG = Logger.getLogger(RestQueryService.class);
	
	public RestQueryService(File transformTemplatesDir) 
	{
		super(transformTemplatesDir, QueryMode.CDE);
		initService();
	}

	public RestQueryService(File transformTempatesDir, java.net.URL wsURL) 
	{
		super(transformTempatesDir, QueryMode.CDE);
		try {
            this.serviceUrl = new org.apache.axis.types.URI(wsURL.toExternalForm());
        } catch (MalformedURIException e) {
            e.printStackTrace();
        }
		initService();
	}
	
	@Override
	public void initService() 
	{
		
	}
	
	@Override
	protected String executeQuery(Query query) 
	{
		try {
		    StringWriter writer = new StringWriter();
            Utils.serializeObject(query, query.getTypeDesc().getXmlType(), writer);
			LOG.debug("Query: " + writer.toString());
			String queryString = transform.applyTemplates(writer.toString(), requestSequence);
			LOG.debug("Query String: " + queryString);
			return HttpContentReader.getHttpContent(queryString);
		} catch (Exception e)
		{
			LOG.error("RestQueryService.executeQuery: "+e);
			e.printStackTrace();
			return null;
		}
	}
}
