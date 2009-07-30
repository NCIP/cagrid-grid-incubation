package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.processor.I2B2QueryFactory;
import org.cagrid.i2b2.ontomapper.processor.I2B2QueryProcessor;
import org.cagrid.i2b2.ontomapper.style.wizard.config.EncodingServiceConfigurationManager;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;

/**
 * EncodingServicePanel
 * Panel for service developer to specify the encoding service URL
 * used when annotating the domain model with CDEs
 * 
 * @author David
 */
public class EncodingServicePanel extends AbstractWizardPanel {
    
    private static final Log LOG = LogFactory.getLog(EncodingServicePanel.class);
    
    public static final String KEY_ENCODING_URL = "Encoding Service URL";
    
    private EncodingServiceConfigurationManager configuration = null;

    private JLabel serviceUrlLabel = null;
    private JComboBox serviceUrlComboBox = null;
    private JButton loadUrlsButton = null;
    private JPanel mainPanel = null;
    
    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationPanel = null;

    public EncodingServicePanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        configuration = new EncodingServiceConfigurationManager(extensionDescription, info);
        validationModel = new DefaultValidationResultModel();
        configureValidation();
        initialize();
    }


    public String getPanelShortName() {
        return "Encoding Service";
    }


    public String getPanelTitle() {
        return "Encoding Service URL";
    }
    
    
    public void movingNext() {
        try {
            configuration.applyConfigration();
        } catch (Exception ex) {
            LOG.error("Error applying encoding service configuration: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
    

    public void update() {
        String url = configuration.getEncodingServiceURL();
        if (url != null) {
            LOG.debug("Setting selected URL to " + url);
            getServiceUrlComboBox().setSelectedItem(url);
        }
    }
    
    
    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = 0;
        cons.gridy = 0;
        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1.0D;
        cons.weighty = 1.0D;
        add(getValidationPanel(), cons);
    }
    
    
    private IconFeedbackPanel getValidationPanel() {
        if (validationPanel == null) {
            validationPanel = new IconFeedbackPanel(validationModel, getMainPanel());
        }
        return validationPanel;
    }


    /**
     * This method initializes serviceUrlLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getServiceUrlLabel() {
        if (serviceUrlLabel == null) {
            serviceUrlLabel = new JLabel();
            serviceUrlLabel.setText("Encoding Service URL:");
        }
        return serviceUrlLabel;
    }


    /**
     * This method initializes serviceUrlComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getServiceUrlComboBox() {
        if (serviceUrlComboBox == null) {
            serviceUrlComboBox = new JComboBox();
            serviceUrlComboBox.setEditable(true);
            serviceUrlComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    Object selection = getServiceUrlComboBox().getSelectedItem();
                    if (selection != null) {
                        configuration.setEncodingServiceURL(selection.toString());
                    }
                    validateInput();
                }
            });
            serviceUrlComboBox.getEditor().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object selection = getServiceUrlComboBox().getSelectedItem();
                    if (selection != null) {
                        configuration.setEncodingServiceURL(selection.toString());
                    }
                    validateInput();
                }
            });
        }
        return serviceUrlComboBox;
    }


    /**
     * This method initializes loadUrlsButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getLoadUrlsButton() {
        if (loadUrlsButton == null) {
            loadUrlsButton = new JButton();
            loadUrlsButton.setToolTipText("Load URLs from the database specified previously");
            loadUrlsButton.setText("Load URLs From Database");
            loadUrlsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    populateUrlsFromDatabase();
                }
            });
        }
        return loadUrlsButton;
    }


    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(334, 78));
            mainPanel.add(getServiceUrlLabel(), gridBagConstraints);
            mainPanel.add(getServiceUrlComboBox(), gridBagConstraints1);
            mainPanel.add(getLoadUrlsButton(), gridBagConstraints2);
        }
        return mainPanel;
    }
    
    
    private void populateUrlsFromDatabase() {
        Driver driver = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try {
            // get jar URLs for service lib dir, which will include the JDBC jar
            File libDir = new File(getServiceInformation().getBaseDirectory(), "lib");
            File[] jars = libDir.listFiles(new FileFilters.JarFileFilter());
            URL[] jarUrls = new URL[jars.length];
            for (int i = 0; i < jars.length; i++) {
                jarUrls[i] = jars[i].toURL();
            }
            URLClassLoader jdbcClassLoader = new URLClassLoader(jarUrls, getClass().getClassLoader());
            // load the selected JDBC driver
            String jdbcDriverName = configuration.getServiceProperty(I2B2QueryProcessor.JDBC_DRIVER_NAME);
            Class<?> driverClass = jdbcClassLoader.loadClass(jdbcDriverName);
            LOG.debug("Loaded JDBC driver " + jdbcDriverName);
            // register the driver
            driver = (Driver) driverClass.newInstance();
            DriverManager.registerDriver(driver);
            LOG.debug("Registered driver");
            String connect = configuration.getServiceProperty(I2B2QueryProcessor.JDBC_CONNECT_STRING);
            String user = configuration.getServiceProperty(I2B2QueryProcessor.JDBC_USERNAME);
            String pass = configuration.getServiceProperty(I2B2QueryProcessor.JDBC_PASSWORD);
            connection = DriverManager.getConnection(connect, user, pass);
            // we got a connection
            LOG.debug("Connection successfully obtained!");
            String tablePrefix = configuration.getServiceProperty(I2B2QueryProcessor.TABLE_NAME_PREFIX);
            LOG.debug("Table prefix discovered: " + tablePrefix);
            String sql = new I2B2QueryFactory(tablePrefix).getEncodingServiceQuery();
            LOG.debug("Executing SQL: " + sql);
            statement = connection.createStatement();
            results = statement.executeQuery(sql);
            // clear out the combo box
            getServiceUrlComboBox().removeAllItems();
            // put service URLs from the query results in the combo
            while (results.next()) {
                getServiceUrlComboBox().addItem(results.getString(1));
            }
        } catch (Exception ex) {
            String message = "Error loading service URLs from database: " + ex.getMessage();
            LOG.error(message, ex);
            ex.printStackTrace();
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing results: " + ex.getMessage(), ex);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing statement: " + ex.getMessage(), ex);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing connection: " + ex.getMessage(), ex);
                }
            }
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException ex) {
                LOG.error("Error deregistering driver: " + ex.getMessage(), ex);
            }
            LOG.debug("Deregistered driver");
        }
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getServiceUrlComboBox(), KEY_ENCODING_URL);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private void validateInput() {
        ValidationResult result = new ValidationResult();
        
        Object selectedUrl = getServiceUrlComboBox().getSelectedItem();
        if (selectedUrl == null || !(selectedUrl instanceof String) || ((String) selectedUrl).length() == 0) {
            result.add(new SimpleValidationMessage("An encoding service URL must be selected", Severity.ERROR));
        }
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        
        // update next button enabled
        setNextEnabled(!validationModel.hasErrors());
        
        setWizardComplete(true);
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
}
