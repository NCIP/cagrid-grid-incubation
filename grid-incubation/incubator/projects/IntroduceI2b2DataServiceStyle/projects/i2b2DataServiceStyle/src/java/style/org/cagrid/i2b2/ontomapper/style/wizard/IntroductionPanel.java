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
package org.cagrid.i2b2.ontomapper.style.wizard;

import gov.nih.nci.cagrid.data.ui.wizard.AbstractWizardPanel;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.processor.I2B2QueryProcessor;
import org.cagrid.i2b2.ontomapper.style.wizard.config.IntroductionConfigurationManager;

import javax.swing.JTextArea;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.SoftBevelBorder;


/**
 * IntroductionPanel
 * Simple informational panel to get the i2b2-ontomapper
 * data service creation wizard started
 * 
 * @author David
 */
public class IntroductionPanel extends AbstractWizardPanel {
    
    public static final String STYLE_LOGO_LOCATION = "/org/cagrid/i2b2/ontomapper/style/resources/i2b2_dataservices.gif";

    private static final Log LOG = LogFactory.getLog(IntroductionPanel.class);
    
    private static final String INFO_TEXT = 
        "This wizard will guide you througha simplified process of creating " +
        "a caGrid Data Service backed by an i2b2 datamart.  In the following " +
        "screens, you will be prompted to select your domain model for the " +
        "subset of datatypes you wish to expose to the grid, and the " +
        "connection information to the i2b2 database";
    
    private JLabel logoLabel = null;
    private JTextArea informationTextArea = null;
    private JScrollPane informationScrollPane = null;
    private JPanel iconPanel = null;
    
    private IntroductionConfigurationManager configurationManager = null;

    public IntroductionPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
        this.configurationManager = new IntroductionConfigurationManager(extensionDescription, info);
        this.configurationManager.setQueryProcessorClassName(I2B2QueryProcessor.class.getName());
        initialize();
    }


    public String getPanelShortName() {
        return "Initialization";
    }


    public String getPanelTitle() {
        return "i2b2 Data Service Initialization";
    }


    public void update() {
        // nothing to update, just an informational panel
    }
    
    
    public void movingNext() {
        try {
            configurationManager.applyConfigration();
        } catch (Exception ex) {
            LOG.error("Error applying configuration: " + ex.getMessage(), ex);
        }
    }
    
    
    private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.insets = new Insets(15, 15, 15, 15);
        gridBagConstraints2.gridx = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.anchor = GridBagConstraints.NORTH;
        gridBagConstraints1.insets = new Insets(15, 15, 15, 15);
        gridBagConstraints1.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(912, 285));
        this.add(getIconPanel(), gridBagConstraints1);
        this.add(getInformationScrollPane(), gridBagConstraints2);        
    }
    
    
    private JLabel getLogoLabel() {
        if (logoLabel == null) {
            logoLabel = new JLabel();
            // load the logo
            InputStream imageStream = getClass().getResourceAsStream(STYLE_LOGO_LOCATION);
            if (imageStream != null) {
                try {
                    BufferedImage image = ImageIO.read(imageStream);
                    logoLabel.setIcon(new ImageIcon(image));
                } catch (Exception ex) {
                    LOG.error("Error reading logo image: " + ex.getMessage(), ex);
                }
            } else {
                LOG.error("Logo image not found!");
            }
        }
        return logoLabel;
    }


    /**
     * This method initializes informationTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getInformationTextArea() {
        if (informationTextArea == null) {
            informationTextArea = new JTextArea();
            informationTextArea.setLineWrap(true);
            informationTextArea.setWrapStyleWord(true);
            informationTextArea.setEditable(false);
            informationTextArea.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 12));
            informationTextArea.setText(INFO_TEXT);
        }
        return informationTextArea;
    }


    /**
     * This method initializes informationScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getInformationScrollPane() {
        if (informationScrollPane == null) {
            informationScrollPane = new JScrollPane();
            informationScrollPane.setBorder(BorderFactory.createTitledBorder(
                null, "Information", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            informationScrollPane.setViewportView(getInformationTextArea());
            informationScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }
        return informationScrollPane;
    }


    /**
     * This method initializes iconPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getIconPanel() {
        if (iconPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            iconPanel = new JPanel();
            iconPanel.setLayout(new GridBagLayout());
            iconPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
            iconPanel.add(getLogoLabel(), gridBagConstraints);
        }
        return iconPanel;
    }
}
