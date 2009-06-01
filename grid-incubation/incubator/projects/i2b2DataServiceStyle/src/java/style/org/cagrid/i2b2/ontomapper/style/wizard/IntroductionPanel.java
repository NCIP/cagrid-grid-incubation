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


public class IntroductionPanel extends AbstractWizardPanel {
    
    private static final Log LOG = LogFactory.getLog(IntroductionPanel.class);
    
    private JLabel logoLabel = null;
    private JTextArea informationTextArea = null;
    private JScrollPane informationScrollPane = null;
    private JPanel iconPanel = null;

    public IntroductionPanel(ServiceExtensionDescriptionType extensionDescription, ServiceInformation info) {
        super(extensionDescription, info);
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
            InputStream imageStream = getClass().getResourceAsStream("resources/i2b2_dataservices.gif");
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
