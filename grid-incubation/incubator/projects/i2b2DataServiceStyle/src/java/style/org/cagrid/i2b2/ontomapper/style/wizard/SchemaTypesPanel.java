package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.grape.utils.CompositeErrorDialog;
import org.cagrid.i2b2.ontomapper.style.wizard.PackageSchemasTable.SchemaMappingStatus;
import org.cagrid.i2b2.ontomapper.style.wizard.config.SchemaTypesConfigurationManager;


/**
 * SchemaTypesPanel 
 * Panel to match up schema types with exposed packages from a
 * domain model
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created Sep 26, 2006
 * @version $Id: SchemaTypesPanel.java,v 1.11 2009-01-29 19:23:56 dervin Exp $
 */
public class SchemaTypesPanel extends AbstractWizardPanel {
    
    private static Log LOG = LogFactory.getLog(SchemaTypesPanel.class);

    private JScrollPane packageNamespaceScrollPane = null;
    private PackageSchemasTable packageNamespaceTable = null;
    
    private SchemaTypesConfigurationManager configurationManager = null;
    
    
    public SchemaTypesPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.configurationManager = new SchemaTypesConfigurationManager(extensionDescription, info);
        initialize();
    }


    public void update() {
        try {
            Data data = ExtensionDataUtils.getExtensionData(getExtensionData());
            ModelInformation info = data.getModelInformation();
            Set<String> currentPackageNames = new HashSet<String>();
            for (int i = 0; i < getPackageNamespaceTable().getRowCount(); i++) {
                currentPackageNames.add((String) getPackageNamespaceTable().getValueAt(i, 0));
            }
            if (info != null && info.getModelPackage() != null) {
                ModelPackage[] packs = info.getModelPackage();
                if (packs != null && packs.length != 0) {
                    // add any new packages to the table
                    for (int i = 0; i < packs.length; i++) {
                        if (!getPackageNamespaceTable().isPackageInTable(packs[i])) {
                            getPackageNamespaceTable().addNewCadsrPackage(getServiceInformation(), packs[i]);
                        }
                        currentPackageNames.remove(packs[i].getPackageName());
                    }
                }
            }
            Iterator<String> invalidPackageNameIter = currentPackageNames.iterator();
            while (invalidPackageNameIter.hasNext()) {
                String invalidName = invalidPackageNameIter.next();
                getPackageNamespaceTable().removeCadsrPackage(invalidName);
            }
            setWizardComplete(allSchemasResolved());
        } catch (Exception ex) {
            ex.printStackTrace();
            CompositeErrorDialog.showErrorDialog("Error populating the packages table", ex);
        }
    }


    public String getPanelTitle() {
        return "Schema Type Selection";
    }


    public String getPanelShortName() {
        return "Schemas";
    }
    
    
    public void movingNext() {
        try {
            configurationManager.applyConfigration();
        } catch (Exception ex) {
            LOG.error("Error applying schema mapping configuration: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }


    private void initialize() {
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.weighty = 1.0;
        gridBagConstraints4.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.add(getPackageNamespaceScrollPane(), gridBagConstraints4);
    }


    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getPackageNamespaceScrollPane() {
        if (this.packageNamespaceScrollPane == null) {
            this.packageNamespaceScrollPane = new JScrollPane();
            this.packageNamespaceScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            this.packageNamespaceScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "Schema Mappings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, PortalLookAndFeel.getPanelLabelColor()));
            this.packageNamespaceScrollPane.setViewportView(getPackageNamespaceTable());
        }
        return this.packageNamespaceScrollPane;
    }


    /**
     * This method initializes jTable
     * 
     * @return javax.swing.JTable
     */
    private PackageSchemasTable getPackageNamespaceTable() {
        if (this.packageNamespaceTable == null) {
            this.packageNamespaceTable = new PackageSchemasTable(configurationManager);
            this.packageNamespaceTable.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        setWizardComplete(allSchemasResolved());
                    }
                }
            });
        }
        return this.packageNamespaceTable;
    }


    private boolean allSchemasResolved() {
        for (int i = 0; i < getPackageNamespaceTable().getRowCount(); i++) {
            String status = (String) getPackageNamespaceTable().getValueAt(i, 2);
            if (!status.equals(SchemaMappingStatus.FOUND)) {
                return false;
            }
        }
        return true;
    }
}
