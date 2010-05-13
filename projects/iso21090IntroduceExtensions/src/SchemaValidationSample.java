import gov.nih.nci.cagrid.common.SchemaValidationException;
import gov.nih.nci.cagrid.common.SchemaValidator;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.iso21090.grid.ser.Filter;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.axis.encoding.SerializationContext;
import org.iso._21090.Ad;


public class SchemaValidationSample {

    public static void main(String[] args) {
        String pathToIsoSchema = "ext/dependencies/xsds/ISO_extensions.xsd"; // populate this appropriately
        
        // create an ISO type
        org.iso._21090.Ad ad = new Ad();
        // populate its fields
        // ...
        // serialize that to a StringWriter so we get the XML 
        StringWriter writer = new StringWriter();
        try {
            SerializationContext serializationContext = new SerializationContext(writer);
            // probably cache this context for performance reasons
            JAXBContext jaxbContext = JAXBContext.newInstance(ad.getClass().getPackage().getName());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(ad, new Filter(serializationContext));
        } catch (JAXBException ex) {
            ex.printStackTrace();
            // serialization failed
            System.exit(1);
        }
        
        try {
            SchemaValidator validator = new SchemaValidator(pathToIsoSchema);
            String xml = writer.getBuffer().toString();
            // this whole try..catch block is optional and just pretty-prints your XML for readability
            try {
                xml = XMLUtilities.formatXML(xml);
            } catch (Exception e) {
                // ignore it
            }
            System.out.println("Validating XML:");
            System.out.println(xml);
            validator.validate(xml);
            System.out.println("Validation Passed");
        } catch (SchemaValidationException ex) {
            System.err.println("Validation Failed");
            // the exception message will tell you what the failure was (missing attribute, unexpected element, etc) and on what line of the XML
            ex.printStackTrace();
            // fail...
        }
    }

}
