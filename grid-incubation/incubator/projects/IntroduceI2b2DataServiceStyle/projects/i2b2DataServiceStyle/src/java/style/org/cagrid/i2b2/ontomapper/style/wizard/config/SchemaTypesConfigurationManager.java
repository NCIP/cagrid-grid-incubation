package org.cagrid.i2b2.ontomapper.style.wizard.config;

import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SchemaTypesConfigurationManager extends BaseConfigurationManager {
    
    private ModelInformationUtil modelInfoUtil = null;
    private Map<String, NamespaceType[]> packageToNamespace = null;

    public SchemaTypesConfigurationManager(ServiceExtensionDescriptionType extensionDescription,
        ServiceInformation serviceInformation) {
        super(extensionDescription, serviceInformation);
        this.modelInfoUtil = new ModelInformationUtil(serviceInformation.getServiceDescriptor());
        this.packageToNamespace = new HashMap<String, NamespaceType[]>();
    }


    public void applyConfigration() throws Exception {
        addNamespaceTypesToService();
        storePackageMappings();
    }
    
    
    public void mapPackageToSchema(String packageName, NamespaceType[] namespaces) {
        packageToNamespace.put(packageName, namespaces);
    }
    
    
    public String getMappedNamespace(String packageName) {
        String namespace = null;
        NamespaceType[] mapped = packageToNamespace.get(packageName);
        if (mapped != null && mapped.length != 0) {
            namespace = mapped[0].getNamespace();
        }
        return namespace;
    }
    
    
    public void removeNamespaceAndSchemas(String packageName) {
        NamespaceType[] mappedNamespaces = packageToNamespace.get(packageName);
        if (mappedNamespaces != null && mappedNamespaces.length != 0) {
            for (NamespaceType ns : mappedNamespaces) {
                // verify the namespace isn't already used somewhere else in the service
                // or mapped to another package
                if (!CommonTools.isNamespaceTypeInUse(ns, getServiceInformation().getServiceDescriptor()) &&
                    !namespaceMappedElsewhere(packageName, ns)) {
                    File schemaFile = getFullSchemaLocation(ns);
                    schemaFile.delete();
                }
            }
        }
    }
    
    
    private boolean namespaceMappedElsewhere(String packageName, NamespaceType ns) {
        for (String mappedPackage : packageToNamespace.keySet()) {
            if (!mappedPackage.equals(packageName)) {
                NamespaceType[] mappedNamespaces = packageToNamespace.get(mappedPackage);
                for (NamespaceType namespace : mappedNamespaces) {
                    if (ns.getNamespace().equals(namespace.getNamespace())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    
    private File getFullSchemaLocation(NamespaceType ns) {
        String mainServiceName = getServiceInformation().getServices().getService(0).getName();
        File schemaDir = new File(getServiceInformation().getBaseDirectory(), "schema" + File.separator + mainServiceName);
        File fullSchemaLocation = new File(schemaDir, ns.getLocation());
        return fullSchemaLocation;
    }
    
    
    private void addNamespaceTypesToService() throws Exception {
        for (NamespaceType[] namespaces : packageToNamespace.values()) {
            for (NamespaceType ns : namespaces) {
                if (!namespaceAlreadyInService(ns)) {
                    CommonTools.addNamespace(getServiceInformation().getServiceDescriptor(), ns);
                }
            }
        }
    }
    
    
    private boolean namespaceAlreadyInService(NamespaceType ns) {
        NamespaceType found = CommonTools.getNamespaceType(getServiceInformation().getNamespaces(), ns.getNamespace());
        return found != null;
    }
    
    
    private void storePackageMappings() throws Exception {
        Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
        ModelInformation info = data.getModelInformation();
        if (info == null) {
            info = new ModelInformation();
            info.setSource(ModelSourceType.mms);
            data.setModelInformation(info);
        }
        for (int i = 0; info.getModelPackage() != null && i < info.getModelPackage().length; i++) {
            ModelPackage currentPackage = info.getModelPackage(i);
            // find the package's namespace mapping
            NamespaceType[] mappedNamespaces = packageToNamespace.get(currentPackage.getPackageName());
            if (mappedNamespaces != null && mappedNamespaces.length != 0) {
                modelInfoUtil.setMappedNamespace(currentPackage.getPackageName(), mappedNamespaces[0].getNamespace());
            } else {
                modelInfoUtil.unsetMappedNamespace(currentPackage.getPackageName());
            }
        }
        ExtensionDataUtils.storeExtensionData(getExtensionData(), data);
    }
    
    
    protected ExtensionTypeExtensionData getExtensionData() {
        return ExtensionTools.getExtensionData(getExtensionDescription(), getServiceInformation());
    }
}
