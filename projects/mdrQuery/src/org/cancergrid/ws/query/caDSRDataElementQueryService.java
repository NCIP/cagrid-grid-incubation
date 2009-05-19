package org.cancergrid.ws.query;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.cancergrid.schema.query.Query;
import org.cancergrid.ws.util.HttpContentReader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class caDSRDataElementQueryService extends XSLTransformQueryService {
    private static Logger LOG = Logger.getLogger(caDSRDataElementQueryService.class);


    public caDSRDataElementQueryService(File transformTemplatesDir) {
        super(transformTemplatesDir);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected String executeQuery(Query query) {
        try {
            StringWriter writer = new StringWriter();
            Utils.serializeObject(query, query.getTypeDesc().getXmlType(), writer);
            LOG.debug("Query: " + writer.toString());
            String queryString = transform.applyTemplates(writer.toString(), requestSequence);
            LOG.debug("Query String: " + queryString);
            String dataElementResults = HttpContentReader.getHttpContent(queryString);
            Document document = XMLUtilities.stringToDocument(dataElementResults);
            List children = document.getRootElement().getChild("queryResponse").getChildren("class");
            for (Iterator iterator = children.iterator(); iterator.hasNext();) {
                Element child = (Element) iterator.next();
                List fields = child.getChildren("field");
                Element newChild = null;
                for (Iterator iterator2 = fields.iterator(); iterator2.hasNext();) {
                    Element field = (Element) iterator2.next();
                    if(field.getAttributeValue("name").equals("dataElementConcept")){
                        String queryS = field.getAttributeValue("href", Namespace.getNamespace("http://www.w3.org/1999/xlink"));
                        String dataElementConceptResults = HttpContentReader.getHttpContent(queryS);
                        Document decDocument = XMLUtilities.stringToDocument(dataElementConceptResults);
                        Element dataElementConceptClass = (Element)decDocument.getRootElement().getChild("queryResponse").getChild("class").clone();
                        newChild = (Element)dataElementConceptClass.detach();
                    }
                }
                if(newChild!=null){
                    child.addContent(newChild);
                }
                
      
                
            }
            return XMLUtilities.documentToString(document);
        } catch (Exception e) {
            LOG.error("RestQueryService.executeQuery: " + e);
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void initService() {
        // TODO Auto-generated method stub

    }

}
