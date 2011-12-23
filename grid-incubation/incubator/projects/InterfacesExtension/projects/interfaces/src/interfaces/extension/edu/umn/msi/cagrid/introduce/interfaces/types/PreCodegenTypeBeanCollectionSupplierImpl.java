package edu.umn.msi.cagrid.introduce.interfaces.types;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.utils.SyncUtils;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List; 
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.commons.io.FileUtils;

public class PreCodegenTypeBeanCollectionSupplierImpl implements TypeBeanCollectionSupplier {
  private Collection<TypeBean> typeMappingBeans = new LinkedList<TypeBean>();
  private ServiceInformation information;
  
  public PreCodegenTypeBeanCollectionSupplierImpl(ServiceInformation information) throws IOException {
    this.information = information;
    init(information);
  }

  public static boolean canConstruct(ServiceInformation information) {
    String namespace2PackagePath = 
      information.getBaseDirectory().getAbsolutePath() + File.separator + "build" 
      + File.separator + IntroduceConstants.NAMESPACE2PACKAGE_MAPPINGS_FILE;
    File namespace2PackageFile = new File(namespace2PackagePath);
    if(!namespace2PackageFile.exists()) {
      // Precodegen step of first creation, cannot build types.
      return false;
    } else {
      return true;
    }
  }
  
  
  private void init(ServiceInformation information) throws IOException {
    Set<String> excludeSet = generateSOAPStubExcludesSet();
    MultiServiceSymbolTable table = new MultiServiceSymbolTable(information.getBaseDirectory(), information, excludeSet);
    table.generateSymbolTable();

    if ((information.getNamespaces() != null) && (information.getNamespaces().getNamespace() != null)) {
      for(NamespaceType namespace : information.getNamespaces().getNamespace()) {
        if(namespace.getSchemaElement() != null) {
          for(SchemaElementType schemaElement : namespace.getSchemaElement()) {
            TypeBean bean = new TypeBean();
            bean.setNamespace(namespace.getNamespace());
            bean.setLocalPart(schemaElement.getType());
            QName qName = new QName(namespace.getNamespace(), schemaElement.getType());
            if(schemaElement.getClassName() == null) {
              if (namespace.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                // Do nothing, these are populated in the W3XmlSchemaTypeBeanCollectionProvider class
                // and added manually at the end of init.
              } else {
                Element element = table.getElement(qName);
                if(element == null) {
                  System.out.println("Warning : Unable to find element in symbol table for: {" + qName.getNamespaceURI() + "}" + qName.getLocalPart());
                } else {
                  bean.setJavaPackage(getPackageName(element.getName()));
                  bean.setJavaClassName(getRelativeClassName(element.getName()));
                  typeMappingBeans.add(bean);
                }
              }
            } else { // Already have class name, just create the bean.
              bean.setJavaPackage(namespace.getPackageName());
              bean.setJavaClassName(schemaElement.getClassName());
              typeMappingBeans.add(bean);
            }
          }
        }
      }
    }
    typeMappingBeans.addAll(new W3XmlSchemaTypeBeanCollectionSupplier().get());
  }

  public Collection<TypeBean> get() {
    return typeMappingBeans;
  }

  // Adapted from SyncTools.java of the Introduce project of caGrid.
  @SuppressWarnings("unchecked")
  private Set<String> generateSOAPStubExcludesSet() throws IllegalStateException {
    File baseDirectory = information.getBaseDirectory();
    Set<String> excludeSet = new HashSet<String>();
    File schemaDir = new File(baseDirectory.getAbsolutePath() + File.separator + "schema" + File.separator
        + information.getIntroduceServiceProperties().getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
    // exclude namespaces that have FQN for metadata class
    // get the classnames from the axis symbol table
    if ((information.getNamespaces() != null) && (information.getNamespaces().getNamespace() != null)) {
      for (int i = 0; i < information.getNamespaces().getNamespace().length; i++) {
        NamespaceType ntype = information.getNamespaces().getNamespace(i);
        if (ntype.getSchemaElement() != null) {
          for (int j = 0; j < ntype.getSchemaElement().length; j++) {
            SchemaElementType type = ntype.getSchemaElement(j);
            if (type.getClassName() != null) {
              if (ntype.getLocation() != null) {
                // the namespace contains customly serialized
                // beans... so don't generate stubs
                excludeSet.add(ntype.getNamespace());
                try {
                  SyncUtils.walkSchemasGetNamespaces(schemaDir + File.separator + ntype.getLocation(),
                      excludeSet, new HashSet(), new HashSet());
                } catch(Exception e) {
                  throw new IllegalStateException("Failed to execute walkSchemasGetNamespaces");
                }

                // this schema is excluded.. no need to check
                // the rest of the schemaelements
                break;
              }
            }
          }
        }
      }
    }
    return excludeSet;
  }

  // Adapted from SyncTools.java of the Introduce project of caGrid.
  private String getRelativeClassName(String fullyQualifiedClassName) {
    int index = fullyQualifiedClassName.lastIndexOf(".");
    if (index >= 0) {
      return fullyQualifiedClassName.substring(index + 1);
    } else {
      return fullyQualifiedClassName;
    }
  }

  // Adapted from SyncTools.java of the Introduce project of caGrid.
  private String getPackageName(String fullyQualifiedClassName) {
    int index = fullyQualifiedClassName.lastIndexOf(".");
    if (index >= 0) {
      return fullyQualifiedClassName.substring(0, index);
    }
    return null;
  }



}


//Adapted from SyncTools.java of the Introduce project of caGrid.
class MultiServiceSymbolTable {

  ServiceInformation info;

  Set<String> excludedSet;

  List<SymbolTable> symbolTables;

  File baseDirectory;


  public MultiServiceSymbolTable(File baseDirectory, ServiceInformation info, Set<String> excludedSet) throws IOException {
    this.baseDirectory = baseDirectory;
    this.info = info;
    this.excludedSet = excludedSet;
    this.symbolTables = new ArrayList<SymbolTable>();
  }


  public Element getElement(QName qname) {
    Element element = null;
    for (int i = 0; i < this.symbolTables.size(); i++) {

      element = ((SymbolTable) this.symbolTables.get(i)).getElement(qname);
      if (element != null) {
        break;
      }
    }
    return element;
  }


  public Type getType(QName qname) {
    Type type = null;
    for (int i = 0; i < this.symbolTables.size(); i++) {

      type = ((SymbolTable) this.symbolTables.get(i)).getType(qname);
      if (type != null) {
        break;
      }
    }
    return type;
  }


  public MessageEntry getMessageEntry(QName qname) {
    MessageEntry type = null;
    for (int i = 0; i < this.symbolTables.size(); i++) {

      type = ((SymbolTable) this.symbolTables.get(i)).getMessageEntry(qname);
      if (type != null) {
        break;
      }
    }
    return type;
  }


  public void dump(PrintStream stream) {
    for (int i = 0; i < this.symbolTables.size(); i++) {
      ((SymbolTable) this.symbolTables.get(i)).dump(stream);
    }
  }


  public void generateSymbolTable() throws IOException {

    if ((this.info.getServices() != null) && (this.info.getServices().getService() != null)) {
      for (int serviceI = 0; serviceI < this.info.getServices().getService().length; serviceI++) {

        ServiceType service = this.info.getServices().getService(serviceI);

        Emitter parser = new Emitter();
        SymbolTable table = null;

        parser.setQuiet(true);
        parser.setImports(true);

        List<String> excludeList = new ArrayList<String>();
        // one hammer(List), one solution
        excludeList.addAll(this.excludedSet); 
        parser.setNamespaceExcludes(excludeList);

        parser.setOutputDir(baseDirectory.getAbsolutePath() + File.separator + "tmp");
        parser.setNStoPkg(baseDirectory.getAbsolutePath() + File.separator + "build"
            + File.separator + IntroduceConstants.NAMESPACE2PACKAGE_MAPPINGS_FILE);
        try {
          parser.run(new File(baseDirectory.getAbsolutePath()
              + File.separator
              + "build"
              + File.separator
              + "schema"
              + File.separator
              + this.info.getIntroduceServiceProperties().get(
                  IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME) + File.separator
                  + service.getName() + ".wsdl").getAbsolutePath());
        } catch (Exception e) {
          continue;
        }
        table = parser.getSymbolTable();

        this.symbolTables.add(table);
        parser = null;
        System.gc();
      }

    }
    FileUtils.deleteDirectory(new File(baseDirectory.getAbsolutePath() + File.separator + "tmp"));
  }
}

