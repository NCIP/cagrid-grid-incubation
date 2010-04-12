package org.cagrid.iso21090.model.tools;

import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;
import gov.nih.nci.ncicb.xmiinout.handler.XmiException;
import gov.nih.nci.ncicb.xmiinout.handler.XmiHandlerFactory;
import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ISOSupportDomainModelGenerator {

    private XmiInOutHandler handler = null;
    private String projectShortName = null;
    private String projectLongName = null;
    private String projectDescription = null;
    private String projectVersion = null;
    private String packageExcludeRegex = null;
    private float attributeVersion = 1.0f;


    public ISOSupportDomainModelGenerator(HandlerEnum xmiType) {
        this.handler = XmiHandlerFactory.getXmiHandler(xmiType);
    }


    public String getProjectShortName() {
        return projectShortName;
    }


    public void setProjectShortName(String projectShortName) {
        this.projectShortName = projectShortName;
    }


    public String getProjectLongName() {
        return projectLongName;
    }


    public void setProjectLongName(String projectLongName) {
        this.projectLongName = projectLongName;
    }


    public String getProjectDescription() {
        return projectDescription;
    }


    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }


    public String getProjectVersion() {
        return projectVersion;
    }


    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }


    public String getPackageExcludeRegex() {
        return packageExcludeRegex;
    }


    public void setPackageExcludeRegex(String packageExcludeRegex) {
        this.packageExcludeRegex = packageExcludeRegex;
    }


    public float getAttributeVersion() {
        return attributeVersion;
    }


    public void setAttributeVersion(float attributeVersion) {
        this.attributeVersion = attributeVersion;
    }


    public DomainModel generateDomainModel(String xmiFilename) throws XmiException, IOException {
        handler.load(xmiFilename);
        UMLModel umlModel = handler.getModel();
        DomainModel domain = new DomainModel();
        List<UMLClass> umlClasses = umlModel.getClasses();
        List<gov.nih.nci.cagrid.metadata.dataservice.UMLClass> domainClasses = new ArrayList<gov.nih.nci.cagrid.metadata.dataservice.UMLClass>(
            umlClasses.size());
        for (UMLClass clazz : umlClasses) {
            gov.nih.nci.cagrid.metadata.dataservice.UMLClass c = new gov.nih.nci.cagrid.metadata.dataservice.UMLClass();
            // basic class info
            c.setPackageName(clazz.getPackage().getName());
            c.setClassName(clazz.getName());
            c.setAllowableAsTarget(true);
            // attributes
            List<UMLAttribute> umlAttribs = clazz.getAttributes();
            List<gov.nih.nci.cagrid.metadata.common.UMLAttribute> attribs = new ArrayList<gov.nih.nci.cagrid.metadata.common.UMLAttribute>(
                umlAttribs.size());
            for (UMLAttribute attrib : umlAttribs) {
                gov.nih.nci.cagrid.metadata.common.UMLAttribute a = new gov.nih.nci.cagrid.metadata.common.UMLAttribute();
                a.setName(attrib.getName());
                a.setDataTypeName(attrib.getDatatype().getName());
                // TODO: get tagged value for CDE ID
                attribs.add(a);
            }
            c.setId(String.valueOf(clazz.hashCode()));
            domainClasses.add(c);
        }
        
        // associations
        return domain;
    }


    public static void main(String[] args) {
        ISOSupportDomainModelGenerator generator = new ISOSupportDomainModelGenerator(HandlerEnum.EADefault);
        try {
            generator.generateDomainModel("foo.xmi");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
