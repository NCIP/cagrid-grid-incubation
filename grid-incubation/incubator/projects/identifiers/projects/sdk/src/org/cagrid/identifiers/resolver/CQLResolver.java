package org.cagrid.identifiers.resolver;

import gov.nih.nci.cagrid.identifiers.TypeValues;

import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.apache.axis.utils.XMLUtils;
import org.cagrid.identifiers.core.IdentifierValues;

public class CQLResolver implements Resolver{

	public Object resolve( IdentifierValues ivs ) {
		
		String[] eprStrs = ivs.getValues("EPR");
		String[] cqlStrs = ivs.getValues("CQL");
		
		String cqlStr = null;
		for( TypeValues tv : tvm.getTypeValues() ) {
			if (tv.getType() == gov.nih.nci.cagrid.identifiers.Type.CQL) {
				for( String data : tv.getValues().getValue() ) {
					System.out.println("Type["+tv.getType().getValue()+"]={"+data+"}");
					cqlStr = data;
					break;
				}
			}
			
			if (cqlStr != null) break;
		}
		gov.nih.nci.cagrid.cqlresultset.CQLQueryResults results = 
			query();
		return results;
	}

	public static gov.nih.nci.cagrid.cqlresultset.CQLQueryResults 
		query(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery, String cqlStr) throws Exception {

		String operationName = "query";
		String portName = "SDKTestModelModifiedPortTypePort";

		System.out.println("portName[" + portName + "]");
		System.out.println("operationName[" + operationName + "]");

		gov.nih.nci.cagrid.data.stubs.QueryRequest params = new gov.nih.nci.cagrid.data.stubs.QueryRequest();
		gov.nih.nci.cagrid.data.stubs.QueryRequestCqlQuery cqlQueryContainer = new gov.nih.nci.cagrid.data.stubs.QueryRequestCqlQuery();
		cqlQueryContainer.setCQLQuery(cqlQuery);
		params.setCqlQuery(cqlQueryContainer);

		org.apache.axis.client.Service service = new org.apache.axis.client.Service();
		
//		Call call = service.createCall(QName.valueOf(portName),
//				QName.valueOf(operationName));
//		call.setTargetEndpointAddress("http://localhost:8082/wsrf/services/cagrid/SDKTestModelModified");
//		//((org.apache.axis.client.Call)call).setOperation(QName.valueOf(portName), "query");
//		((org.apache.axis.client.Call)call).setTimeout(new Integer(15*1000));
//		((org.apache.axis.client.Call)call).setProperty(ElementDeserializer.DESERIALIZE_CURRENT_ELEMENT, Boolean.TRUE);
//		//((org.apache.axis.client.Call)call).setReturnType(new QName("http://gov.nih.nci.cagrid.data/DataService", "QueryResponse"));
//	
//		//call.addParameter("QueryRequest", new QName("http://gov.nih.nci.cagrid.data/DataService", "QueryRequest"), ParameterMode.IN);
//		((org.apache.axis.client.Call)call).registerTypeMapping(org.w3c.dom.Element.class, 
//				new QName("http://gov.nih.nci.cagrid.data/DataService", "QueryResponse"),
//				new ElementSerializerFactory(),
//				new ElementDeserializerFactory());

		org.apache.axis.description.OperationDesc oper = 
			new org.apache.axis.description.OperationDesc();
        oper.setName("query");
       
        oper.addParameter(
        		new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataService", "QueryRequest"), 
        		new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataService", ">QueryRequest"), 
        		gov.nih.nci.cagrid.data.stubs.QueryRequest.class, 
        		org.apache.axis.description.ParameterDesc.IN, false, false);
      
        oper.setReturnType(new QName("http://gov.nih.nci.cagrid.data/DataService", ">QueryResponse"));
        oper.setReturnClass(gov.nih.nci.cagrid.data.stubs.QueryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataService", "QueryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        //oper.addFault(new org.apache.axis.description.FaultDesc(
        //              new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataServiceExceptions", "MalformedQueryException"),
        //              "gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType",
        //              new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataServiceExceptions", "MalformedQueryExceptionType"),
        //              true
        //             ));
        //oper.addFault(new org.apache.axis.description.FaultDesc(
        //              new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataServiceExceptions", "QueryProcessingException"),
        //              "gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType",
        //              new javax.xml.namespace.QName("http://gov.nih.nci.cagrid.data/DataServiceExceptions", "QueryProcessingExceptionType"),
        //              true
        //             ));
      
		org.apache.axis.client.Call _call = (org.apache.axis.client.Call) service.createCall();
		_call.setTargetEndpointAddress("http://localhost:8082/wsrf/services/cagrid/SDKTestModelModified");
        _call.setOperation(oper);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://data.cagrid.nci.nih.gov/DataService/QueryRequest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "query"));
		Object ret = _call.invoke( new Object[]{ params } );
		
		gov.nih.nci.cagrid.data.stubs.QueryResponse resp = null;

		if (ret instanceof Element) {
			XMLUtils.ElementToStream((Element) ret, System.out);
		} else {
			resp = (gov.nih.nci.cagrid.data.stubs.QueryResponse)ret;
		}

		System.out.println("\nDone!");
		return (resp != null ? resp.getCQLQueryResultCollection() : null);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
