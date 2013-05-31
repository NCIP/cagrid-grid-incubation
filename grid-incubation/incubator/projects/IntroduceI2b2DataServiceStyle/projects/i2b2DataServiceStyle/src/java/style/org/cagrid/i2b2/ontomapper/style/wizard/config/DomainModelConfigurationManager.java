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
package org.cagrid.i2b2.ontomapper.style.wizard.config;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cagrid.i2b2.ontomapper.processor.I2B2QueryProcessor;
import org.cagrid.mms.domain.UMLProjectIdentifer;

/**
 * DomainModelConfigurationManager
 * Configuration manager for the Domain Model panel
 * 
 * @author David
 */
public class DomainModelConfigurationManager extends BaseConfigurationManager {
    
    private String domainModelFilename = null;
    
    public DomainModelConfigurationManager(ServiceExtensionDescriptionType extensionDescription,
        ServiceInformation serviceInformation) {
        super(extensionDescription, serviceInformation);
    }
    
    
    public void setDomainModelFilename(String filename) {
        this.domainModelFilename = filename;
    }
    
    
    public String getDomainModelFilename() {
        return domainModelFilename;
    }


    public void applyConfigration() throws Exception {
        // copy the domain model from its original location to the service
        File originalModel = new File(getDomainModelFilename());
        File modelCopy = new File(getServiceInformation().getBaseDirectory(), 
            "etc" + File.separator + originalModel.getName());
        Utils.copyFile(originalModel, modelCopy);
        
        // create the domain model resource property
        ResourcePropertyType resourceProperty = new ResourcePropertyType();
        resourceProperty.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
        resourceProperty.setFileLocation(modelCopy.getName());
        resourceProperty.setPopulateFromFile(true);
        resourceProperty.setRegister(true);
        
        // add the RP to the service description
        // have to locate the main service type
        ServiceType service = getServiceInformation().getServices().getService(0);
        // remove any existing resource property of the domain model type
        CommonTools.removeResourceProperty(service, DataServiceConstants.DOMAIN_MODEL_QNAME);
        // add the domain model RP
        CommonTools.addResourcePropety(service, resourceProperty);
        
        // set the service property so the query processor can locate the domain model
        setServiceProperty(I2B2QueryProcessor.DOMAIN_MODEL_FILE_NAME, modelCopy.getName(), true);
        
        // generate the right Model Information block in the extension data
        ExtensionDataManager manager = new ExtensionDataManager(getExtensionData());
        ModelInformation modelInfo = manager.getModelInformation();
        // read the domain model
        FileReader reader = new FileReader(modelCopy);
        DomainModel model = MetadataUtils.deserializeDomainModel(reader);
        reader.close();
        // build package -> list of classes
        Map<String, List<UMLClass>> packageClasses = 
            new HashMap<String, List<UMLClass>>();
        // sort out package names
        for (UMLClass clazz : model.getExposedUMLClassCollection().getUMLClass()) {
            List<UMLClass> classList = packageClasses.get(clazz.getPackageName());
            if (classList == null) {
                classList = new LinkedList<UMLClass>();
                packageClasses.put(clazz.getPackageName(), classList);
            }
            classList.add(clazz);
        }
        List<ModelPackage> packageList = 
            new ArrayList<ModelPackage>(packageClasses.size());
        for (String packageName : packageClasses.keySet()) {
            ModelPackage pack = new ModelPackage();
            pack.setPackageName(packageName);
            List<ModelClass> classes = new ArrayList<ModelClass>();
            for (UMLClass clazz : packageClasses.get(packageName)) {
                ModelClass c = new ModelClass();
                c.setSelected(true);
                c.setShortClassName(clazz.getClassName());
                c.setTargetable(true);
                classes.add(c);
            }
            ModelClass[] classArray = new ModelClass[classes.size()];
            classes.toArray(classArray);
            pack.setModelClass(classArray);
            packageList.add(pack);
        }
        ModelPackage[] packArray = new ModelPackage[packageList.size()];
        packageList.toArray(packArray);
        modelInfo.setModelPackage(packArray);
        modelInfo.setSource(ModelSourceType.preBuilt);
        UMLProjectIdentifer projectId = new UMLProjectIdentifer();
        projectId.setIdentifier(model.getProjectShortName());
        projectId.setVersion(model.getProjectVersion());
        modelInfo.setUMLProjectIdentifer(projectId);
        manager.storeModelInformation(modelInfo);
    }
    
    
    protected ExtensionTypeExtensionData getExtensionData() {
        return ExtensionTools.getExtensionData(getExtensionDescription(), getServiceInformation());
    }
}
