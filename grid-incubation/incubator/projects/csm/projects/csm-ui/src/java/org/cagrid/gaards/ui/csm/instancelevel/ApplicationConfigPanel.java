/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.ui.csm.instancelevel;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.filters.client.FilterCreatorClient;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;

public class ApplicationConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Application application;

	private JDialog dialog;

	private JLabel appJarLabel = null;

	private JLabel appJar2Label = null;

	private JLabel hibernateFileLabel = null;

	private JTextField jarFile = null;

	private JTextField jarFile2 = null;

	private JTextField hibernateFile = null;

	private JButton browseJar1 = null;

	private JButton browseJar2 = null;

	private JPanel titlePanel = null;

	private JPanel buttonPanel = null;

	private ProgressPanel progressPanel = null;

	private JButton jButton = null;

	private JButton jButton1 = null;

	private JButton jButton2 = null;

	private FilterCreatorClient client = null;

	private JPanel jPanel = null;

	/**
	 * This is the default constructor
	 */
	public ApplicationConfigPanel(Application application, JDialog dialog) {
		super();
		this.dialog = dialog;
		this.application = application;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		gridBagConstraints41.gridx = 1;
		gridBagConstraints41.fill = GridBagConstraints.BOTH;
		gridBagConstraints41.weightx = 1.0D;
		gridBagConstraints41.weighty = 1.0D;
		gridBagConstraints41.gridy = 5;
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.gridx = 0;
		gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints31.weightx = 1.0D;
		gridBagConstraints31.gridwidth = 3;
		gridBagConstraints31.gridy = 7;
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.weightx = 1.0D;
		gridBagConstraints21.gridwidth = 3;
		gridBagConstraints21.gridy = 6;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.fill = GridBagConstraints.BOTH;
		gridBagConstraints11.weightx = 1.0D;
		gridBagConstraints11.gridwidth = 3;
		gridBagConstraints11.gridy = 0;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 2;
		gridBagConstraints7.gridy = 3;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 2;
		gridBagConstraints6.gridy = 2;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.gridy = 4;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.gridwidth = 2;
		gridBagConstraints5.gridx = 1;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 3;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 4;
		hibernateFileLabel = new JLabel();
		hibernateFileLabel.setToolTipText("This is the path in the jar file to the hibernate configuration file is.");
		hibernateFileLabel.setText("Hibernate Configuration");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 3;
		appJar2Label = new JLabel();
		appJar2Label.setText("Application ORM File");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 2;
		appJarLabel = new JLabel();
		appJarLabel.setText("Application Beans File*");
		this.setSize(474, 363);
		this.setLayout(new GridBagLayout());
		this.add(appJarLabel, gridBagConstraints);
		this.add(appJar2Label, gridBagConstraints1);
		this.add(hibernateFileLabel, gridBagConstraints2);
		this.add(getJarFile(), gridBagConstraints3);
		this.add(getJarFile2(), gridBagConstraints4);
		this.add(getHibernateFile(), gridBagConstraints5);
		this.add(getBrowseJar1(), gridBagConstraints6);
		this.add(getBrowseJar2(), gridBagConstraints7);
		this.add(getTitlePanel(), gridBagConstraints11);
		this.add(getButtonPanel(), gridBagConstraints21);
		this.add(getProgressPanel(), gridBagConstraints31);
		this.add(getJPanel(), gridBagConstraints41);
	}

	/**
	 * This method initializes jarFile
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getJarFile() {
		if (jarFile == null) {
			jarFile = new JTextField();
			jarFile
					.setText("");
		}
		return jarFile;
	}

	/**
	 * This method initializes jarFile2
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getJarFile2() {
		if (jarFile2 == null) {
			jarFile2 = new JTextField();
			jarFile2
					.setText("");
		}
		return jarFile2;
	}

	/**
	 * This method initializes hibernateFile
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getHibernateFile() {
		if (hibernateFile == null) {
			hibernateFile = new JTextField();
			hibernateFile.setToolTipText("This is the path in the jar file to the hibernate configuration file is.");
			hibernateFile
					.setText("hibernate.cfg.xml");
		}
		return hibernateFile;
	}

	/**
	 * This method initializes browseJar1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseJar1() {
		if (browseJar1 == null) {
			browseJar1 = new JButton();
			browseJar1.setText("browse");
			browseJar1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Choose Application Jar File");
					chooser.setFileFilter(new FileFilter() {

						@Override
						public String getDescription() {
							return "*.jar";
						}

						@Override
						public boolean accept(File f) {
							return f.getName().endsWith(".jar") || f.isDirectory();
						}
					});
					int value = chooser.showDialog(ApplicationConfigPanel.this,
							"select");
					if (value != JFileChooser.CANCEL_OPTION) {
						getJarFile().setText(
								chooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
		}
		return browseJar1;
	}

	/**
	 * This method initializes browseJar2
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBrowseJar2() {
		if (browseJar2 == null) {
			browseJar2 = new JButton();
			browseJar2.setText("browse");
			browseJar2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Choose Application Jar File");
					chooser.setFileFilter(new FileFilter() {

						@Override
						public String getDescription() {
							return "*.jar";
						}

						@Override
						public boolean accept(File f) {
							return f.getName().endsWith(".jar");
						}
					});
					int value = chooser.showDialog(ApplicationConfigPanel.this,
							"select");
					if (value != JFileChooser.CANCEL_OPTION) {
						getJarFile2().setText(
								chooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
		}
		return browseJar2;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel("Configure Application",
					"Provide the application jars and the resource path to the hibernate config file.");

		}
		return titlePanel;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getJButton(), new GridBagConstraints());
			buttonPanel.add(getJButton1(), gridBagConstraints9);
			buttonPanel.add(getJButton2(), gridBagConstraints10);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes progressPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private ProgressPanel getProgressPanel() {
		if (progressPanel == null) {
			progressPanel = new ProgressPanel();
		}
		return progressPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("cancel");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getJButton2().setEnabled(false);
					getProgressPanel().stopProgress("");
					dialog.dispose();
					try {
						if (client != null) {
							client.destroy();
						}
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					client = null;
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("verify");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {

						public void execute() {
							progressPanel
									.showProgress("Verifying application configuration...");
							try {
								disableAll();
								client = application.createFilterCreatorClient(
										new File(getJarFile().getText()),
										new File(getJarFile2().getText()),
										getHibernateFile().getText());
								if (client != null) {
									getJButton2().setEnabled(true);
								}
								progressPanel.stopProgress("Verified.");
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
								progressPanel.stopProgress("Error");
							} finally {
								enableAll();
							}
						}
					};
					try {
						GridApplication.getContext()
								.executeInBackground(runner);
					} catch (Exception t) {
						t.getMessage();
					}

				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("next");
			jButton2.setEnabled(false);
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getJButton2().setEnabled(false);
					getProgressPanel().stopProgress("");
					dialog.dispose();
				}
			});
		}
		return jButton2;
	}

	public FilterCreatorClient getClient() {
		return client;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}

	public void enableAll() {
		getBrowseJar1().setEnabled(true);
		getBrowseJar2().setEnabled(true);
		getHibernateFile().setEnabled(true);
		getJarFile().setEnabled(true);
		getJarFile2().setEnabled(true);
	}

	public void disableAll() {
		getBrowseJar1().setEnabled(false);
		getBrowseJar2().setEnabled(false);
		getHibernateFile().setEnabled(false);
		getJarFile().setEnabled(false);
		getJarFile2().setEnabled(false);
	}
} // @jve:decl-index=0:visual-constraint="10,10"
