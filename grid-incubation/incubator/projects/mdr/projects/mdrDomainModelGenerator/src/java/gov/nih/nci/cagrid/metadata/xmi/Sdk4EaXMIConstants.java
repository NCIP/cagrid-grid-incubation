/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package gov.nih.nci.cagrid.metadata.xmi;

/** 
 *  XMIConstants specific to SDK 4 EA models
 * 
 * @author David Ervin
 * 
 * @created Oct 22, 2007 10:28:52 AM
 * @version $Id: Sdk4EaXMIConstants.java,v 1.1.8.1 2009/03/24 15:00:31 dervin Exp $ 
 */
public class Sdk4EaXMIConstants {
    // classifier constants
    public static final String XMI_UML_CLASSIFIER = "UML:Classifier";
    
    // multiplicity
    public static final String MULTIPLICITY_ATTRIBUTE = "multiplicity";
    public static final String MULTIPLICITY_RANGE_SEPARATOR = "..";
    
    private Sdk4EaXMIConstants() {
        // no instantiation
    }
}
