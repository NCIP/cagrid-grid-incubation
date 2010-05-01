package org.cagrid.iso21090.tests.integration.story;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.iso21090.tests.integration.steps.InvokeLocalCqlStep;

public class TranslateAndExecuteCqlStory extends Story {
    
    public TranslateAndExecuteCqlStory() {
        super();
    }
    

    public String getDescription() {
        return "Translates CQL to HQL and runs it against the caCORE SDK local API";
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new InvokeLocalCqlStep());
        return steps;
    }
}
