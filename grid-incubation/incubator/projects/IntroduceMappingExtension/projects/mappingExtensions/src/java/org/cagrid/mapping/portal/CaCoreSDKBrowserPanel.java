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
package org.cagrid.mapping.portal;

import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ResourceManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CaCoreSDKBrowserPanel extends JPanel {

	public static final String CA_CORE_SDK_V41 = "caCORE SDK v 4.1";
	public static final String CA_CORE_SDK_V40 = "caCORE SDK v 4.0";

	private JPanel mappingPanel = null;
	private JLabel sdkVersionNameLabel = null;
	private JComboBox sdkVersionComboBox = null;
	private JLabel sdkDirLabel = null;
	private JTextField sdkDirTextField = null;
	private JButton sdkDirBrowseButton = null;
	private JLabel schemaLabel = null;
	private JTextField schemaTextField = null;
	private JButton schemaBrowseButton = null;
	
	public CaCoreSDKBrowserPanel() {
		if (this.mappingPanel == null) {
			this.mappingPanel = new JPanel();
			this.mappingPanel.setLayout(new GridBagLayout());

			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 1;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.gridx = 2;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints5.gridy = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints6.gridy = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 0;

			mappingPanel.add(getSDKVersionNameLabel(), gridBagConstraints1);
			mappingPanel.add(getSDKVersionComboBox(), gridBagConstraints2);
			mappingPanel.add(getSDKDirLabel(), gridBagConstraints3);
			mappingPanel.add(getSDKDirTextField(), gridBagConstraints4);
			mappingPanel.add(getSdkDirBrowseButton(), gridBagConstraints7);
//			mappingPanel.add(getSchemaLabel(), gridBagConstraints5);
//			mappingPanel.add(getSchemaTextField(), gridBagConstraints6);
//			mappingPanel.add(getSchemaBrowseButton(), gridBagConstraints8);

			add(mappingPanel, gridBagConstraints);
		}

	}

	private JLabel getSDKVersionNameLabel() {
		if (sdkVersionNameLabel == null) {
			sdkVersionNameLabel = new JLabel();
			sdkVersionNameLabel.setText("caCORE SDK Version:");
		}
		return sdkVersionNameLabel;
	}

	private JComboBox getSDKVersionComboBox() {
		if (sdkVersionComboBox == null) {
			sdkVersionComboBox = new JComboBox();
			sdkVersionComboBox.addItem(CA_CORE_SDK_V40);
			sdkVersionComboBox.addItem(CA_CORE_SDK_V41);
		}
		return sdkVersionComboBox;
	}

	private JLabel getSDKDirLabel() {
		if (sdkDirLabel == null) {
			sdkDirLabel = new JLabel();
			sdkDirLabel.setText("caCORE SDK directory:");
		}
		return sdkDirLabel;
	}

	private JTextField getSDKDirTextField() {
		if (sdkDirTextField == null) {
			sdkDirTextField = new JTextField();
			sdkDirTextField.setEditable(true);
		}
		return sdkDirTextField;
	}

	private JLabel getSchemaLabel() {
		if (schemaLabel == null) {
			schemaLabel = new JLabel();
			schemaLabel.setText("Schema file:");
		}
		return schemaLabel;
	}

	private JTextField getSchemaTextField() {
		if (schemaTextField == null) {
			schemaTextField = new JTextField();
			schemaTextField.setEditable(true);
		}
		return schemaTextField;
	}
	
    private JButton getSchemaBrowseButton() {
        if (schemaBrowseButton == null) {
        	schemaBrowseButton = new JButton();
        	schemaBrowseButton.setText("Browse...");
        	schemaBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    launchSchemaFileBrowser();
                }
            });
        }
        return schemaBrowseButton;
    }

    protected void launchSchemaFileBrowser() {
        String selectedFilename = null;
        try {
            selectedFilename = ResourceManager.promptFile(null, FileFilters.XSD_FILTER);
        } catch (IOException e) {
            return;
        }

        if (selectedFilename != null) {
            getSchemaTextField().setText(new File(selectedFilename).getAbsolutePath());
        }
    }

    private JButton getSdkDirBrowseButton() {
        if (sdkDirBrowseButton == null) {
        	sdkDirBrowseButton = new JButton();
        	sdkDirBrowseButton.setText("Browse...");
        	sdkDirBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    launchSdkDirFileBrowser();
                }
            });
        }
        return sdkDirBrowseButton;
    }

    protected void launchSdkDirFileBrowser() {
        String selectedDir = null;
        try {
            selectedDir = ResourceManager.promptDir(null);
        } catch (IOException e) {
            return;
        }

        if (selectedDir != null) {
            getSDKDirTextField().setText(new File(selectedDir).getAbsolutePath());
        }
    }


	public String getSdkVersion() {
		return getSDKVersionComboBox().getSelectedItem().toString();
	}

	public String getSdkDir() {
		return getSDKDirTextField().getText();
	}
	
	public String getSchema() {
		return getSchemaTextField().getText();
	}

	public static void main(String[] args) {
		CaCoreSDKBrowserPanel panel = new CaCoreSDKBrowserPanel();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	

}
