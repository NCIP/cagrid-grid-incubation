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

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  UnzipStep
 *  Step unpacks a zip file
 * 
 * @author David Ervin
 * 
 * @created Nov 8, 2007 10:56:21 AM
 * @version $Id: UnzipStep.java,v 1.2 2007/12/03 16:27:18 hastings Exp $ 
 */
public class UnzipStep extends Step {
    private String zipLocation;
    private String unzipLocation;
    
    public UnzipStep(String zip, String unzipDir) {
        this.zipLocation = zip;
        this.unzipLocation = unzipDir;
    }
    

    public void runStep() throws Throwable {
        File zipFile = new File(zipLocation);
        File outDir = new File(unzipLocation);
        assertTrue("Zip file (" + zipFile.getAbsolutePath() + ") not found", 
            zipFile.exists());
        if (outDir.exists()) {
            Utils.deleteDir(outDir);
        }
        ZipUtilities.unzip(zipFile, outDir);
    }
}
