package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.common.portal.DocumentChangeAdapter;
import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;
import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.style.wizard.config.DatabaseConfigurationManager;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

/**
 * DatabasePanel
 * Prompts the service developer for database parameters
 * to connect to the i2b2-ontomapper SQL database
 * 
 * @author David
 */
public class DatabasePanel extends AbstractWizardPanel {
    
    private static final Log LOG = LogFactory.getLog(DatabasePanel.class);
    
    // keys for validation
    private static final String KEY_DRIVER_JAR = "driverJar";
    private static final String KEY_DRIVER_CLASS = "driverClass";
    private static final String KEY_CONNECT_STRING = "connectString";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TEST_RESULTS = "testResults";
    private static final String KEY_TABLE_PREFIX = "tablePrefix";
    
    // consistent error message for test results
    private static final String TEST_ERROR_PREFIX = "TEST ERROR";
    
    private ValidationResultModel validationModel = null;
    private IconFeedbackPanel validationPanel = null;
    
    private DatabaseConfigurationManager configurationManager = null;
    
    private JPanel mainPanel = null;
    private JLabel jdbcJarLabel = null;
    private JTextField jdbcJarTextField = null;
    private JButton jdbcJarBrowseButton = null;
    private JLabel driverClassnameLabel = null;
    private JComboBox driverClassnameComboBox = null;
    private JLabel connectStringLabel = null;
    private JTextField connectStringTextField = null;
    private JLabel usernameLabel = null;
    private JTextField usernameTextField = null;
    private JLabel passwordLabel = null;
    private JPasswordField passwordField = null;
    private JLabel tablePrefixLabel = null;
    private JTextField tablePrefixTextField = null;
    private JButton testConnectionButton = null;
    private JTextArea testResultsTextArea = null;
    private JScrollPane testResultsScrollPane = null;
    

    public DatabasePanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.validationModel = new DefaultValidationResultModel();
        this.configurationManager = new DatabaseConfigurationManager(extensionDescription, info);
        configureValidation();
        initialize();
    }


    public String getPanelShortName() {
        return "Database";
    }


    public String getPanelTitle() {
        return "SQL Database Configuration";
    }


    public void update() {
        getJdbcJarTextField().setText(configurationManager.getDriverJarFilename());
        populateDriverClassDropdown();
        
        if (configurationManager.getDriverClassname() != null) {
            getDriverClassnameComboBox().setSelectedItem(configurationManager.getDriverClassname());
        }
        
        getConnectStringTextField().setText(configurationManager.getConnection());
        
        getUsernameTextField().setText(configurationManager.getUser());
        
        getPasswordField().setText(configurationManager.getPassword());
        
        getTablePrefixTextField().setText(configurationManager.getTablePrefix());
        
        validateInput();
    }
    
    
    public void movingNext() {
        try {
            configurationManager.applyConfigration();
        } catch (Exception ex) {
            LOG.error("Error applying database configuration: " + ex.getMessage(), ex);
            ex.printStackTrace();
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
    
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridy = 5;
            gridBagConstraints21.weightx = 1.0;
            gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints21.gridwidth = 2;
            gridBagConstraints21.gridx = 1;
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints13.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints13.gridy = 5;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.BOTH;
            gridBagConstraints12.gridy = 6;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.weighty = 1.0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.gridwidth = 2;
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.anchor = GridBagConstraints.NORTH;
            gridBagConstraints11.gridy = 6;
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridy = 4;
            gridBagConstraints10.weightx = 1.0;
            gridBagConstraints10.gridwidth = 2;
            gridBagConstraints10.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints10.gridx = 1;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.gridy = 4;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.gridy = 3;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.gridy = 3;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.gridy = 2;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints6.gridwidth = 2;
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints5.gridy = 2;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridx = 1;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 2;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.gridy = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints1.gridx = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.setSize(new Dimension(631, 221));
            mainPanel.add(getJdbcJarLabel(), gridBagConstraints);
            mainPanel.add(getJdbcJarTextField(), gridBagConstraints1);
            mainPanel.add(getJdbcJarBrowseButton(), gridBagConstraints2);
            mainPanel.add(getJdbcDriverClassLabel(), gridBagConstraints3);
            mainPanel.add(getDriverClassnameComboBox(), gridBagConstraints4);
            mainPanel.add(getConnectStringLabel(), gridBagConstraints5);
            mainPanel.add(getConnectStringTextField(), gridBagConstraints6);
            mainPanel.add(getUsernameLabel(), gridBagConstraints7);
            mainPanel.add(getUsernameTextField(), gridBagConstraints8);
            mainPanel.add(getPasswordLabel(), gridBagConstraints9);
            mainPanel.add(getPasswordField(), gridBagConstraints10);
            mainPanel.add(getTestConnectionButton(), gridBagConstraints11);
            mainPanel.add(getTestResultsScrollPane(), gridBagConstraints12);
            mainPanel.add(getTablePrefixLabel(), gridBagConstraints13);
            mainPanel.add(getTablePrefixTextField(), gridBagConstraints21);
        }
        return mainPanel;
    }


    /**
     * This method initializes jdbcJarLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getJdbcJarLabel() {
        if (jdbcJarLabel == null) {
            jdbcJarLabel = new JLabel();
            jdbcJarLabel.setText("JDBC Jar:");
        }
        return jdbcJarLabel;
    }


    /**
     * This method initializes jdbcJarTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJdbcJarTextField() {
        if (jdbcJarTextField == null) {
            jdbcJarTextField = new JTextField();
            jdbcJarTextField.setEditable(false);
        }
        return jdbcJarTextField;
    }


    /**
     * This method initializes jdbcJarBrowseButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJdbcJarBrowseButton() {
        if (jdbcJarBrowseButton == null) {
            jdbcJarBrowseButton = new JButton();
            jdbcJarBrowseButton.setText("Browse");
            jdbcJarBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectJdbcJar();
                    validateInput();
                }
            });
        }
        return jdbcJarBrowseButton;
    }


    /**
     * This method initializes jdbcDriverClassLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getJdbcDriverClassLabel() {
        if (driverClassnameLabel == null) {
            driverClassnameLabel = new JLabel();
            driverClassnameLabel.setText("Driver Classname:");
        }
        return driverClassnameLabel;
    }


    /**
     * This method initializes driverClassnameComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getDriverClassnameComboBox() {
        if (driverClassnameComboBox == null) {
            driverClassnameComboBox = new JComboBox();
            driverClassnameComboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getItem() != null) {
                        configurationManager.setDriverClassname(e.getItem().toString());
                    }
                    validateInput();
                }
            });
        }
        return driverClassnameComboBox;
    }


    /**
     * This method initializes connectStringLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getConnectStringLabel() {
        if (connectStringLabel == null) {
            connectStringLabel = new JLabel();
            connectStringLabel.setText("Connect String:");
        }
        return connectStringLabel;
    }


    /**
     * This method initializes connectStringTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getConnectStringTextField() {
        if (connectStringTextField == null) {
            connectStringTextField = new JTextField();
            connectStringTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configurationManager.setConnection(getConnectStringTextField().getText());
                    validateInput();
                }
            });
        }
        return connectStringTextField;
    }


    /**
     * This method initializes usernameLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getUsernameLabel() {
        if (usernameLabel == null) {
            usernameLabel = new JLabel();
            usernameLabel.setText("Username:");
        }
        return usernameLabel;
    }


    /**
     * This method initializes usernameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getUsernameTextField() {
        if (usernameTextField == null) {
            usernameTextField = new JTextField();
            usernameTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configurationManager.setUser(getUsernameTextField().getText());
                    validateInput();
                }
            });
        }
        return usernameTextField;
    }


    /**
     * This method initializes passwordLabel	
     * 	
     * @return javax.swing.JLabel	
     */
    private JLabel getPasswordLabel() {
        if (passwordLabel == null) {
            passwordLabel = new JLabel();
            passwordLabel.setText("Password:");
        }
        return passwordLabel;
    }


    /**
     * This method initializes passwordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getPasswordField() {
        if (passwordField == null) {
            passwordField = new JPasswordField();
            passwordField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configurationManager.setPassword(new String(getPasswordField().getPassword()));
                    validateInput();
                }
            });
        }
        return passwordField;
    }
    
    
    private JLabel getTablePrefixLabel() {
        if (tablePrefixLabel == null) {
            tablePrefixLabel = new JLabel();
            tablePrefixLabel.setText("Table Name Prefix:");
        }
        return tablePrefixLabel;
    }
    
    
    private JTextField getTablePrefixTextField() {
        if (tablePrefixTextField == null) {
            tablePrefixTextField = new JTextField();
            tablePrefixTextField.getDocument().addDocumentListener(new DocumentChangeAdapter() {
                public void documentEdited(DocumentEvent e) {
                    configurationManager.setTablePrefix(getTablePrefixTextField().getText());
                    validateInput();
                }
            });
        }
        return tablePrefixTextField;
    }


    /**
     * This method initializes testConnectionButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getTestConnectionButton() {
        if (testConnectionButton == null) {
            testConnectionButton = new JButton();
            testConnectionButton.setText("Test Connection");
            testConnectionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    testJdbcConnection();
                }
            });
        }
        return testConnectionButton;
    }


    /**
     * This method initializes testResultsTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getTestResultsTextArea() {
        if (testResultsTextArea == null) {
            testResultsTextArea = new JTextArea();
            testResultsTextArea.setLineWrap(true);
            testResultsTextArea.setEditable(false);
            testResultsTextArea.setWrapStyleWord(true);
        }
        return testResultsTextArea;
    }


    /**
     * This method initializes testResultsScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getTestResultsScrollPane() {
        if (testResultsScrollPane == null) {
            testResultsScrollPane = new JScrollPane();
            testResultsScrollPane.setViewportView(getTestResultsTextArea());
        }
        return testResultsScrollPane;
    }
    
    
    private void selectJdbcJar() {
        String defaultDir = null;
        try {
            defaultDir = ResourceManager.getStateProperty(ResourceManager.LAST_FILE);
        } catch (Exception ex) {
            LOG.warn("Unable to obtain last file selected from Resource Manager: " + ex.getMessage(), ex);
        }
        JFileChooser chooser = new JFileChooser(defaultDir);
        chooser.setFileFilter(FileFilters.JAR_FILTER);
        chooser.setApproveButtonText("Select");
        int choice = chooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String selectedJarFilename = chooser.getSelectedFile().getAbsolutePath();
            // set the jar file in the configuration
            configurationManager.setDriverJarFilename(selectedJarFilename);
            // set the jar file in the GUI's text field
            getJdbcJarTextField().setText(selectedJarFilename);
            // re-populate the driver class dropdown from the contents of this jar
            populateDriverClassDropdown();
        }
    }
    
    
    private void populateDriverClassDropdown() {
        String jdbcJarFilename = getJdbcJarTextField().getText();
        if (jdbcJarFilename.length() != 0 && new File(jdbcJarFilename).exists()) {
            try {
                // create a URLClassLoader to pick up the JDBC jar
                URL jarUrl = new File(jdbcJarFilename).toURL();
                URLClassLoader jdbcClassLoader = new URLClassLoader(new URL[] {jarUrl}, getClass().getClassLoader());
                // list .class files from the jar
                JarFile jdbcJar = new JarFile(jdbcJarFilename);
                Enumeration<JarEntry> entries = jdbcJar.entries();
                SortedSet<String> driverClassnames = new TreeSet<String>();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        // load up the class, see if it's a driver
                        String className = entry.getName().substring(0, entry.getName().length() - 6);
                        className = className.replace('/', '.');
                        LOG.debug("Trying to load class " + className);
                        try {
                            Class clazz = jdbcClassLoader.loadClass(className);
                            if (Driver.class.isAssignableFrom(clazz)) {
                                // keep drivers in a sorted set
                                LOG.debug(className + " appears to be a JDBC driver");
                                driverClassnames.add(className);
                            }
                        } catch (Throwable th) {
                            // catching anything here since there's likely to be many errors
                            LOG.warn("Error loading class " + className + " (" + th.getMessage() + ")");
                        }
                    }
                }
                jdbcJar.close();
                // set the combo box's choices
                getDriverClassnameComboBox().removeAllItems();
                getDriverClassnameComboBox().addItem("");
                for (String driverClassname : driverClassnames) {
                    getDriverClassnameComboBox().addItem(driverClassname);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                LOG.error("Error loading classes from selected JDBC jar: " + ex.getMessage(), ex);
            }
        }
    }
    
    
    private void testJdbcConnection() {
        StringBuffer results = new StringBuffer();
        // make sure we're at least in a valid state before trying a connection
        ValidationResult validation = getCurrentValidationState();
        if (validation.hasErrors()) {
            results.append(TEST_ERROR_PREFIX).append("\n");
            for (ValidationMessage message : validation.getErrors()) {
                results.append("\t").append(message.formattedText()).append("\n");
            }
        } else {
            // create new classloader with no parent to include the JDBC 
            // jar and register it to its own DriverManager class
            String jdbcJarFilename = getJdbcJarTextField().getText();
            try {
                URL jarUrl = new File(jdbcJarFilename).toURL();
                URLClassLoader jdbcClassLoader = new URLClassLoader(new URL[] {jarUrl}, getClass().getClassLoader());
                // load the driver
                Class driverClass = jdbcClassLoader.loadClass(getDriverClassnameComboBox().getSelectedItem().toString());
                results.append("Loaded driver...\n");
                // register the driver
                Driver driver = (Driver) driverClass.newInstance();
                DriverManager.registerDriver(driver);
                results.append("Registered driver...\n");
                String connect = getConnectStringTextField().getText();
                String user = getUsernameTextField().getText();
                String pass = new String(getPasswordField().getPassword());
                Connection jdbcConnection = DriverManager.getConnection(connect, user, pass);
                // COOL! we got a connection
                results.append("Connection successfully obtained!\n");
                jdbcConnection.close();
                results.append("Connection successfully closed!\n");
                DriverManager.deregisterDriver(driver);
                results.append("Deregistered driver.\n\tTEST PASSED");
            } catch (Exception ex) {
                LOG.error("Error testing JDBC connection: " + ex.getMessage(), ex);
                StringWriter exceptionWriter = new StringWriter();
                ex.printStackTrace(new PrintWriter(exceptionWriter));
                results.insert(0, TEST_ERROR_PREFIX + "\n");
                results.append(exceptionWriter.getBuffer().toString());
            }
        }
        getTestResultsTextArea().setText(results.toString());
        validateInput();
    }
    
    
    private void configureValidation() {
        ValidationComponentUtils.setMessageKey(getJdbcJarTextField(), KEY_DRIVER_JAR);
        ValidationComponentUtils.setMessageKey(getDriverClassnameComboBox(), KEY_DRIVER_CLASS);
        ValidationComponentUtils.setMessageKey(getConnectStringTextField(), KEY_CONNECT_STRING);
        ValidationComponentUtils.setMessageKey(getUsernameTextField(), KEY_USERNAME);
        ValidationComponentUtils.setMessageKey(getPasswordField(), KEY_PASSWORD);
        ValidationComponentUtils.setMessageKey(getTablePrefixTextField(), KEY_TABLE_PREFIX);
        ValidationComponentUtils.setMessageKey(getTestResultsTextArea(), KEY_TEST_RESULTS);
        
        validateInput();
        updateComponentTreeSeverity();
    }
    
    
    private ValidationResult getCurrentValidationState() {
        ValidationResult result = new ValidationResult();
        
        // the first three items are required
        if (ValidationUtils.isBlank(getJdbcJarTextField().getText())) {
            result.add(new SimpleValidationMessage("Must select a JDBC driver Jar", Severity.ERROR, KEY_DRIVER_JAR));
        }
        Object selectedDriverClass = getDriverClassnameComboBox().getSelectedItem();
        if (selectedDriverClass == null || ValidationUtils.isBlank(selectedDriverClass.toString())) {
            result.add(new SimpleValidationMessage("Must select a JDBC driver class", Severity.ERROR, KEY_DRIVER_CLASS));
        }
        if (ValidationUtils.isBlank(getConnectStringTextField().getText())) {
            result.add(new SimpleValidationMessage("Must supply a JDBC connect string", Severity.ERROR, KEY_CONNECT_STRING));
        }
        
        // the rest of the items are not required, but recommended
        if (ValidationUtils.isBlank(getUsernameTextField().getText())) {
            result.add(new SimpleValidationMessage(
                "A username is recommended for connecting to the database over JDBC", Severity.WARNING, KEY_USERNAME));
        }
        if (getPasswordField().getPassword().length == 0) {
            result.add(new SimpleValidationMessage(
                "A password is recommended for connecting to the database over JDBC", Severity.WARNING, KEY_PASSWORD));
        }
        if (ValidationUtils.isBlank(getTablePrefixLabel().getText())) {
            result.add(new SimpleValidationMessage(
                "A table name prefix is recommended", Severity.WARNING, KEY_TABLE_PREFIX));
        }
        if (ValidationUtils.isBlank(getTestResultsTextArea().getText())) {
            result.add(new SimpleValidationMessage(
                "A test of the connection information is recommended to ensure the supplied values are correct",
                Severity.WARNING, KEY_TEST_RESULTS));
        } else if (getTestResultsTextArea().getText().startsWith(TEST_ERROR_PREFIX)) {
            result.add(new SimpleValidationMessage(
                "The connection test failed.  Please ensure the connection information is correct",
                Severity.WARNING, KEY_TEST_RESULTS));
        }
        
        return result;
    }
    
    

    private void validateInput() {
        ValidationResult result = getCurrentValidationState();
        
        validationModel.setResult(result);
        
        updateComponentTreeSeverity();
        
        // update next button enabled
        setNextEnabled(!validationModel.hasErrors());
        
        // TODO: remove this if we add a panel for schemas
        setWizardComplete(!validationModel.hasErrors());
    }
    
    
    private void updateComponentTreeSeverity() {
        ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(this);
        ValidationComponentUtils.updateComponentTreeSeverityBackground(this, validationModel.getResult());
    }
}
