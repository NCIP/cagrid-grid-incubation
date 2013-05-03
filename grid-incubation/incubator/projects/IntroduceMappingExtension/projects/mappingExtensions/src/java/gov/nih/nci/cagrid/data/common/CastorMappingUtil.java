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
package gov.nih.nci.cagrid.data.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class CastorMappingUtil {
    /**
     * Setting this feature controls how xerxes handles external DTDs
     */
    public static final String XERXES_LOAD_DTD_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    
    public static final String CASTOR_MARSHALLING_MAPPING_FILE = "xml-mapping.xml";
    public static final String EDITED_CASTOR_MARSHALLING_MAPPING_FILE = "edited-" + CASTOR_MARSHALLING_MAPPING_FILE;
    public static final String CASTOR_UNMARSHALLING_MAPPING_FILE = "unmarshaller-xml-mapping.xml";
    public static final String EDITED_CASTOR_UNMARSHALLING_MAPPING_FILE = "edited-" + CASTOR_UNMARSHALLING_MAPPING_FILE;

	public static final String CACORE_CASTOR_MAPPING_FILE = "xml-mapping.xml";

	public static String changeNamespaceOfPackage(String mapping, String packageName, String namespace) throws Exception {
        Element mappingRoot = stringToElementNoDoctypes(mapping);
		// get class elements
		String oldNamespace = null;
		String oldPrefix = null;
		Iterator classElemIter = mappingRoot.getChildren("class", mappingRoot.getNamespace()).iterator();
		while (classElemIter.hasNext()) {
			Element classElem = (Element) classElemIter.next();
			String className = classElem.getAttributeValue("name");
			int dotIndex = className.lastIndexOf('.');
			String classPackage = className.substring(0, dotIndex);
			if (classPackage.equals(packageName)) {
				Element mapToElem = classElem.getChild("map-to", classElem.getNamespace());
				if (oldNamespace == null) {
					// keep a record of the old namespace for the package
					oldNamespace = mapToElem.getAttributeValue("ns-uri");
					oldPrefix = mapToElem.getAttributeValue("ns-prefix");
				}
				// change the namespace in the map-to element
				mapToElem.setAttribute("ns-uri", namespace);
			}
		}
		if (oldNamespace != null) {
			// re-walk every class in the mapping, this time looking for attributes
			// of those classes which bind to the old namespace
			classElemIter = mappingRoot.getChildren("class", mappingRoot.getNamespace()).iterator();
			while (classElemIter.hasNext()) {
				Element classElem = (Element) classElemIter.next();
				Iterator fieldElemIter = classElem.getChildren("field", classElem.getNamespace()).iterator();
				while (fieldElemIter.hasNext()) {
					Element fieldElem = (Element) fieldElemIter.next();
					Element bindXmlElement = fieldElem.getChild("bind-xml", fieldElem.getNamespace());
					Namespace elemNamespace = bindXmlElement.getNamespace(oldPrefix);
					if (elemNamespace != null && elemNamespace.getURI().equals(oldNamespace)) {
						// TODO: This is probably horribly inefficient, see if it can be improved
						String elementString = XMLUtilities.elementToString(bindXmlElement);
						int nsStart = elementString.indexOf(oldNamespace);
						int nsEnd = nsStart + oldNamespace.length();
						String changedString = elementString.substring(0, nsStart) + namespace + elementString.substring(nsEnd);
						Element changedElement = stringToElementNoDoctypes(changedString);
						fieldElem.removeContent(bindXmlElement);
						fieldElem.addContent(changedElement);
					}
				}
			}
		}
		return XMLUtilities.elementToString(mappingRoot);
	}
    
    public static String removeAssociationMappings(String mappingText) throws Exception {
        Element mappingRoot = stringToElementNoDoctypes(mappingText);
        // <mapping>
        List classElements = mappingRoot.getChildren("class", mappingRoot.getNamespace());
        Iterator classElemIter = classElements.iterator();
        while (classElemIter.hasNext()) {
            Element classElement = (Element) classElemIter.next(); // <class>
            List fieldElements = classElement.getChildren("field", classElement.getNamespace());
            Iterator fieldElemIter = fieldElements.iterator();
            while (fieldElemIter.hasNext()) {
                Element fieldElement = (Element) fieldElemIter.next();
                Element bindElement = fieldElement.getChild("bind-xml", fieldElement.getNamespace());
                String nodeType = bindElement.getAttributeValue("node");
                // remove non-atttibutes
                if (!nodeType.equals("attribute")) {
                    fieldElemIter.remove();
                }
            }
        }
        String rawXml = XMLUtilities.elementToString(mappingRoot);
        return XMLUtilities.formatXML(rawXml);
    }
	
	
	public static String getCustomCastorMappingFileName(ServiceInformation serviceInfo) {
		String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
			+ File.separator + "src" + File.separator 
			+ getCustomCastorMappingName(serviceInfo);
		return mappingOut;
	}
	
	
	public static String getCustomCastorMappingName(ServiceInformation serviceInfo) {
		String mappingName = serviceInfo.getServices().getService(0)
				.getPackageName().replace('.', '/')
			+ '/' + serviceInfo.getServices().getService(0).getName() 
			+ '-' + CACORE_CASTOR_MAPPING_FILE;
		return mappingName;
	}
    
    public static String getMarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getMarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }  
    
    public static String getMarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName() 
            + '-' + CASTOR_MARSHALLING_MAPPING_FILE;
        return mappingName;
    }
       
    public static String getUnmarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getUnmarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }
       
    public static String getUnmarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName() 
            + '-' + CASTOR_UNMARSHALLING_MAPPING_FILE;
        return mappingName;
    }
    
    public static String getEditedMarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getEditedMarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }
        
    public static String getEditedMarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName()
            + '-' + EDITED_CASTOR_MARSHALLING_MAPPING_FILE;
        return mappingName;
    }
    
    
    public static String getEditedUnmarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getEditedUnmarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }
    
    public static String getEditedUnmarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName()
            + '-' + EDITED_CASTOR_UNMARSHALLING_MAPPING_FILE;
        return mappingName;
    }
      
    private static Element stringToElementNoDoctypes(String string) throws Exception {
        Document doc = null;
        try {
            SAXBuilder builder = new SAXBuilder(false);
            builder.setFeature(
                XERXES_LOAD_DTD_FEATURE, false);
            doc = builder.build(new ByteArrayInputStream(string.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Document construction failed:" + e.getMessage(), e);
        }
        Element root = doc.detachRootElement();
        return root;
    }
       
    public static void main(String[] args) {
        try {
            String castorMapping = Utils.fileToStringBuffer(new File("RemoteSDK321-unmarshaller-xml-mapping.xml")).toString();
            String edited = removeAssociationMappings(castorMapping);
            System.out.println(edited);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
