package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.ui.SchemaResolutionDialog;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.i2b2.ontomapper.style.wizard.config.SchemaTypesConfigurationManager;

/** 
 *  PackageSchemasTable
 *  Table for showing cadsr packages and schema types mapped to them
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 26, 2006 
 * @version $Id: PackageSchemasTable.java,v 1.8 2009-01-30 17:32:54 dervin Exp $ 
 */
public class PackageSchemasTable extends JTable {

    private SchemaTypesConfigurationManager configuration = null;    

    public PackageSchemasTable(SchemaTypesConfigurationManager configuration) {
        setModel(new PackageSchemasTableModel());
        setDefaultRenderer(Object.class, new PackageSchemasTableRenderer());
        setDefaultEditor(Object.class, new PackageSchemasTableEditor());
        this.configuration = configuration;
    }


    public boolean isPackageInTable(ModelPackage pack) {
        String packageName = pack.getPackageName();
        String namespace = configuration.getMappedNamespace(packageName);
        for (int i = 0; i < getRowCount(); i++) {
            if (packageName.equals(getValueAt(i, 0)) 
                && namespace.equals(getValueAt(i, 1))) {
                return true;
            }
        }
        return false;
    }


    public void addNewCadsrPackage(ServiceInformation serviceInfo, ModelPackage pack) {
        String packageName = pack.getPackageName();
        String namespace = configuration.getMappedNamespace(packageName);
        Vector<Object> row = new Vector<Object>(4);
        row.add(packageName);
        row.add(namespace);
        row.add(SchemaMappingStatus.NOT_TRIED);
        row.add(getResolveButton(serviceInfo, pack));

        ((DefaultTableModel) getModel()).addRow(row);
    }


    public void removeCadsrPackage(String packName) {
        for (int i = 0; i < getRowCount(); i++) {
            if (getValueAt(i, 0).equals(packName)) {
                ((DefaultTableModel) getModel()).removeRow(i);
                break;
            }
        }
    }


    /**
     * Creates a new JButton to handle schema resolution
     * @param pack
     * @return
     * 		A JButton to resolve schemas
     */
    private JButton getResolveButton(
        final ServiceInformation serviceInfo, final ModelPackage pack) {
        JButton resolveButton = new JButton("Resolve");
        resolveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // find the table row for this button / package
                int row = 0;
                while (getValueAt(row, 3) != e.getSource()) {
                    row++;
                } 

                // figure out what the current status is
                String status = (String) getValueAt(row, 2);
                if (status.equals(SchemaMappingStatus.FOUND)) {
                    String[] message = {
                        "This package already has a schema associated with it.",
                        "Replace the schema with a different one?"
                    };
                    int choice = JOptionPane.showConfirmDialog(
                        (JButton) e.getSource(), message, "Replace?", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        // remove associated schema and namespace types
                        removeAssociatedSchema(pack);
                    } else {
                        return;
                    }
                }
                resolveSchema(serviceInfo, pack, row);
            }
        });
        return resolveButton;
    }


    private void resolveSchema(ServiceInformation info, ModelPackage pack, int dataRow) {
        // resolve the schemas manually
        NamespaceType[] resolved = SchemaResolutionDialog.resolveSchemas(info);
        if (resolved != null) {
            if (resolved.length != 0 && packageResolvedByNamespace(pack, resolved[0])) {
                // store the mapping to the configuration
                configuration.mapPackageToSchema(pack.getPackageName(), resolved);
                // set the resolution status on the table
                setValueAt(resolved[0].getNamespace(), dataRow, 1);
                setValueAt(SchemaMappingStatus.FOUND, dataRow, 2);
            } else {
                setValueAt(SchemaMappingStatus.MAPPING_ERROR, dataRow, 2);
            }
        } else {
            CompositeErrorDialog.showErrorDialog("Error retrieving schemas!");
        }
    }
    
    
    private boolean packageResolvedByNamespace(ModelPackage pkg, NamespaceType namespace) {
        Set<String> classNames = new HashSet<String>();
        for (ModelClass clazz : pkg.getModelClass()) {
            classNames.add(clazz.getShortClassName());
        }
        Set<String> elementNames = new HashSet<String>();
        for (SchemaElementType element : namespace.getSchemaElement()) {
            elementNames.add(element.getType());
        }
        if (elementNames.containsAll(classNames)) {
            return true;
        }
        
        // sort out the resolution errors
        Set<String> nonResolvedClasses = new HashSet<String>();
        nonResolvedClasses.addAll(classNames);
        nonResolvedClasses.removeAll(elementNames);
        
        // display the errors
        new PackageSchemaMappingErrorDialog(nonResolvedClasses);
        
        // return error condition
        return false;
    }


    private void removeAssociatedSchema(ModelPackage pack) {
        configuration.removeNamespaceAndSchemas(pack.getPackageName());
    }
    
    
    public enum SchemaMappingStatus {
        FOUND, MAPPING_ERROR, NOT_TRIED;
        
        public String toString() {
            String s = null;
            switch (this) {
                case FOUND:
                    s = "Found";
                    break;
                case MAPPING_ERROR:
                    s = "Mapping Error";
                    break;
                case NOT_TRIED:
                    s = "Unknown";
                    break;
            }
            return s;
        }
    }


    private static class PackageSchemasTableModel extends DefaultTableModel {

        public PackageSchemasTableModel() {
            addColumn("Package Name");
            addColumn("Namespace");
            addColumn("Status");
            addColumn("Manual Resolution");
        }


        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }
    }


    private static class PackageSchemasTableRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Component) {
                return (Component) value;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }


    private static class PackageSchemasTableEditor extends AbstractCellEditor implements TableCellEditor {

        private Object editorValue = null;

        public PackageSchemasTableEditor() {
            editorValue = null;
        }


        public Object getCellEditorValue() {
            return editorValue;
        }


        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editorValue = value;
            return (Component) value;
        }
    }
}
