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
package org.cagrid.iso21090.tests.integration.story;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.iso21090.tests.integration.steps.InvokeLocalIntegratedCqlStep;
import org.cagrid.iso21090.tests.integration.steps.InvokeLocalTranslatedCqlStep;

public class TranslateAndExecuteCqlStory extends Story {
    
    public TranslateAndExecuteCqlStory() {
        super();
    }
    

    public String getDescription() {
        return "Translates CQL to HQL and runs it against the caCORE SDK local API";
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new InvokeLocalTranslatedCqlStep());
        steps.add(new InvokeLocalIntegratedCqlStep());
        return steps;
    }
}
