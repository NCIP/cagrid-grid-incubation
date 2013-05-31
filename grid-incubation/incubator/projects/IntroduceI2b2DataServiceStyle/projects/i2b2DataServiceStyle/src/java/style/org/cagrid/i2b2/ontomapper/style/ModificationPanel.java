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
package org.cagrid.i2b2.ontomapper.style;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.ui.DataServiceModificationSubPanel;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import java.awt.Dimension;


public class ModificationPanel extends DataServiceModificationSubPanel {
    
    public static final String STYLE_LOGO_LOCATION = "/org/cagrid/i2b2/ontomapper/style/resources/i2b2_dataservices.gif";
    
    private static final Log LOG = LogFactory.getLog(ModificationPanel.class);

    private JLabel logoLabel = null;
    private JPanel logoPanel = null;

    public ModificationPanel(ServiceInformation serviceInfo, ExtensionDataManager extensionDataManager) {
        super(serviceInfo, extensionDataManager);
        initialize();
    }
    
    
    private void initialize() {
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(439, 292));
        this.add(getIconPanel(), gridBagConstraints1);        
    }


    public void updateDisplayedConfiguration() throws Exception {
        // If there's every anything interesting to display, update it here
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
     * This method initializes logoPanel    
     *  
     * @return javax.swing.JPanel   
     */
    private JPanel getIconPanel() {
        if (logoPanel == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            logoPanel = new JPanel();
            logoPanel.setLayout(new GridBagLayout());
            logoPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
            logoPanel.add(getLogoLabel(), gridBagConstraints);
        }
        return logoPanel;
    }
}
