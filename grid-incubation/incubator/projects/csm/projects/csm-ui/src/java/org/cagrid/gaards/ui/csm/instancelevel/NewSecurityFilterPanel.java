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
package org.cagrid.gaards.ui.csm.instancelevel;

import gov.nih.nci.cagrid.common.Runner;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.filters.client.FilterCreatorClient;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;

public class NewSecurityFilterPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Application application;
	private FilterCreatorClient client;

	private JDialog dialog = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JLabel jLabel2 = null;

	private JLabel jLabel3 = null;

	private JLabel jLabel4 = null;

	private JScrollPane jScrollPane = null;

	private JList filterChainList = null;

	private JPanel jPanel = null;

	private JComboBox associatedClassesComboBox = null;

	private JButton addFCButton = null;

	private JComboBox classNamesCombo = null;

	private JTextField classAliasText = null;

	private JTextField classAttributeAliasText = null;

	private JComboBox associatedAttributesComboBox = null;

	private JButton removeLFCButton = null;

	private JPanel buttonPanel = null;

	private ProgressPanel progressPanel = null;

	private JButton jButton = null;

	private JButton jButton1 = null;

	private JPanel titlePanel = null;

	/**
	 * This is the default constructor
	 */
	public NewSecurityFilterPanel(FilterCreatorClient client,
			Application application, JDialog dialog) {
		super();
		this.dialog = dialog;
		this.application = application;
		this.client = client;
		initialize();
		populate();

	}

	private void populate() {
		try {
			String[] classNames = client.getClassNames();
			Arrays.sort(classNames);
			for (int i = 0; i < classNames.length; i++) {
				getClassNamesCombo().addItem(classNames[i]);
			}
			if (associatedAttributesComboBox.getItemCount() <= 0) {
				getJButton().setEnabled(false);
			} else {
				getJButton().setEnabled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		gridBagConstraints14.gridx = 0;
		gridBagConstraints14.gridwidth = 2;
		gridBagConstraints14.fill = GridBagConstraints.BOTH;
		gridBagConstraints14.gridy = 0;
		GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
		gridBagConstraints22.gridx = 0;
		gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints22.weightx = 1.0D;
		gridBagConstraints22.gridwidth = 2;
		gridBagConstraints22.gridy = 9;
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		gridBagConstraints13.gridx = 0;
		gridBagConstraints13.gridwidth = 2;
		gridBagConstraints13.weightx = 1.0D;
		gridBagConstraints13.fill = GridBagConstraints.BOTH;
		gridBagConstraints13.gridy = 8;
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints31.gridx = 1;
		gridBagConstraints31.gridy = 5;
		gridBagConstraints31.weightx = 1.0;
		gridBagConstraints31.gridwidth = 2;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 0;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.gridy = 7;
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.gridx = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 6;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.gridy = 1;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.gridx = 1;
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.gridx = 1;
		gridBagConstraints10.fill = GridBagConstraints.BOTH;
		gridBagConstraints10.weightx = 1.0D;
		gridBagConstraints10.weighty = 1.0D;
		gridBagConstraints10.gridy = 2;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.gridy = 5;
		jLabel4 = new JLabel();
		jLabel4.setText("Target Class Attribute");
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.gridy = 7;
		jLabel3 = new JLabel();
		jLabel3.setText("Target Class Attribute Alias");
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.gridy = 6;
		jLabel2 = new JLabel();
		jLabel2.setText("Target Class Alias");
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints3.gridy = 2;
		jLabel1 = new JLabel();
		jLabel1.setText("Filter Chain");
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.gridy = 1;
		jLabel = new JLabel();
		jLabel.setText("Class Name");
		this.setSize(514, 331);
		this.setLayout(new GridBagLayout());
		this.add(jLabel, gridBagConstraints2);
		this.add(jLabel1, gridBagConstraints3);
		this.add(jLabel2, gridBagConstraints4);
		this.add(jLabel3, gridBagConstraints5);
		this.add(jLabel4, gridBagConstraints6);
		this.add(getJPanel(), gridBagConstraints10);
		this.add(getClassNamesCombo(), gridBagConstraints11);
		this.add(getClassAliasText(), gridBagConstraints1);
		this.add(getClassAttributeAliasText(), gridBagConstraints7);
		this.add(getAssociatedAttributesComboBox(), gridBagConstraints31);
		this.add(getButtonPanel(), gridBagConstraints13);
		this.add(getProgressPanel(), gridBagConstraints22);
		this.add(getTitlePanel(), gridBagConstraints14);
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getFilterChainList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes filterChainList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getFilterChainList() {
		if (filterChainList == null) {
			filterChainList = new JList(new DefaultListModel());
		}
		return filterChainList;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 1.0D;
			gridBagConstraints8.gridwidth = 2;
			gridBagConstraints8.gridx = 0;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.gridwidth = 1;
			gridBagConstraints21.fill = GridBagConstraints.NONE;
			gridBagConstraints21.weightx = 1.0D;
			gridBagConstraints21.gridy = 3;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.fill = GridBagConstraints.NONE;
			gridBagConstraints12.weightx = 1.0D;
			gridBagConstraints12.gridy = 3;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.gridwidth = 2;
			gridBagConstraints41.weightx = 1.0D;
			gridBagConstraints41.gridy = 2;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getAddFCButton(), gridBagConstraints12);
			jPanel.add(getRemoveLFCButton(), gridBagConstraints21);
			jPanel.add(getAssociatedClassesComboBox(), gridBagConstraints41);
			jPanel.add(getJScrollPane(), gridBagConstraints8);
		}
		return jPanel;
	}

	/**
	 * This method initializes associatedClassesComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getAssociatedClassesComboBox() {
		if (associatedClassesComboBox == null) {
			associatedClassesComboBox = new JComboBox();
		}
		return associatedClassesComboBox;
	}

	/**
	 * This method initializes addFCButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddFCButton() {
		if (addFCButton == null) {
			addFCButton = new JButton();
			addFCButton.setText("add filter");
			addFCButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {

						public void execute() {
							progressPanel.showProgress("Loading attributes...");
							try {
								disableAll();
								String targetClass = (String) getAssociatedClassesComboBox()
										.getSelectedItem();

								if (!targetClass.contains(" - self")) {

									String[] strings = client
											.getAssociatedClassNames(targetClass);
									getAssociatedClassesComboBox()
											.removeAllItems();
									if (strings != null) {
										for (int i = 0; i < strings.length; i++) {
											getAssociatedClassesComboBox()
													.addItem(strings[i]);
										}
									}

								} else {
									getAssociatedClassesComboBox()
											.removeAllItems();
								}
								String[] strings = client
										.getAssociatedAttributes(targetClass);
								getAssociatedAttributesComboBox()
										.removeAllItems();
								if (strings != null) {
									Arrays.sort(strings);
									for (int i = 0; i < strings.length; i++) {
										getAssociatedAttributesComboBox()
												.addItem(strings[i]);
									}
								}
								((DefaultListModel) getFilterChainList()
										.getModel()).addElement(targetClass);
								progressPanel
										.stopProgress("Attributes loaded.");
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
								progressPanel.stopProgress("Error");
							} finally {
								enableAll();
							}
							if (associatedAttributesComboBox.getItemCount() <= 0) {
								getJButton().setEnabled(false);
							} else {
								getJButton().setEnabled(true);
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
		return addFCButton;
	}

	/**
	 * This method initializes classNamesCombo
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getClassNamesCombo() {
		if (classNamesCombo == null) {
			classNamesCombo = new JComboBox();
			classNamesCombo
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							Runner runner = new Runner() {

								public void execute() {
									progressPanel
											.showProgress("Loading associated classes...");
									try {
										disableAll();
										getAssociatedClassesComboBox()
												.removeAllItems();
										getAssociatedAttributesComboBox()
												.removeAllItems();
										((DefaultListModel) getFilterChainList()
												.getModel()).clear();
										String className = (String) classNamesCombo
												.getSelectedItem();
										String[] strings = client
												.getAssociatedClassNames(className);
										getAssociatedClassesComboBox()
												.removeAllItems();
										if (strings != null) {
											Arrays.sort(strings);
											for (int i = 0; i < strings.length; i++) {
												getAssociatedClassesComboBox()
														.addItem(strings[i]);
											}
										}
										progressPanel
												.stopProgress("Loaded Associated Classes");
									} catch (Exception ex) {
										ErrorDialog.showError(ex);
										progressPanel.stopProgress("Error");
									} finally {
										enableAll();
									}
									if (associatedAttributesComboBox.getItemCount() <= 0) {
										getJButton().setEnabled(false);
									} else {
										getJButton().setEnabled(true);
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
		return classNamesCombo;
	}

	/**
	 * This method initializes classAliasText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getClassAliasText() {
		if (classAliasText == null) {
			classAliasText = new JTextField();
		}
		return classAliasText;
	}

	/**
	 * This method initializes classAttributeAliasText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getClassAttributeAliasText() {
		if (classAttributeAliasText == null) {
			classAttributeAliasText = new JTextField();
		}
		return classAttributeAliasText;
	}

	/**
	 * This method initializes associatedAttributesComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getAssociatedAttributesComboBox() {
		if (associatedAttributesComboBox == null) {
			associatedAttributesComboBox = new JComboBox();

		}
		return associatedAttributesComboBox;
	}

	/**
	 * This method initializes removeLFCButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getRemoveLFCButton() {
		if (removeLFCButton == null) {
			removeLFCButton = new JButton();
			removeLFCButton.setText("remove last filter");
			removeLFCButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (((DefaultListModel) getFilterChainList()
									.getModel()).getSize() > 0) {
								getAssociatedAttributesComboBox()
										.removeAllItems();
								((DefaultListModel) getFilterChainList()
										.getModel())
										.removeElementAt(((DefaultListModel) getFilterChainList()
												.getModel()).size() - 1);
								String targetClass = null;
								if (((DefaultListModel) getFilterChainList()
										.getModel()).size() > 0) {
									targetClass = (String) ((DefaultListModel) getFilterChainList()
											.getModel())
											.getElementAt(((DefaultListModel) getFilterChainList()
													.getModel()).size() - 1);
									try {
										String[] strings = client
												.getAssociatedAttributes(targetClass);
										getAssociatedAttributesComboBox()
												.removeAllItems();
										if (strings != null) {
											Arrays.sort(strings);
											for (int i = 0; i < strings.length; i++) {
												getAssociatedAttributesComboBox()
														.addItem(strings[i]);
											}
										}
									} catch (RemoteException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								} else {
									targetClass = (String) getClassNamesCombo()
											.getSelectedItem();
								}
								if (!targetClass.contains(" - self")) {

									try {
										String[] strings = client
												.getAssociatedClassNames(targetClass);
										getAssociatedClassesComboBox()
												.removeAllItems();
										if (strings != null) {
											for (int i = 0; i < strings.length; i++) {
												getAssociatedClassesComboBox()
														.addItem(strings[i]);
											}
										}
									} catch (RemoteException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

								} else {
									getAssociatedClassesComboBox()
											.removeAllItems();
								}

							}
							if (associatedAttributesComboBox.getItemCount() <= 0) {
								getJButton().setEnabled(false);
							} else {
								getJButton().setEnabled(true);
							}

						}
					});
		}
		return removeLFCButton;
	}

	/**
	 * This method initializes buttonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getJButton(), gridBagConstraints15);
			buttonPanel.add(getJButton1(), gridBagConstraints9);
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
			jButton.setText("create");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					progressPanel.showProgress("Creating filter...");

					Runner runner = new Runner() {

						public void execute() {
							try {

								disableAll();
								String[] filters = new String[((DefaultListModel) getFilterChainList()
										.getModel()).size()];
								for (int i = 0; i < filters.length; i++) {
									filters[i] = (String) ((DefaultListModel) getFilterChainList()
											.getModel()).get(i);
								}
								org.cagrid.gaards.csm.bean.FilterClause bean = client
										.getFilterClauseBean(
												(String) getClassNamesCombo()
														.getSelectedItem(),
												filters,
												(String) getAssociatedAttributesComboBox()
														.getSelectedItem(),
												getClassAliasText().getText(),
												getClassAttributeAliasText()
														.getText());
								application.createFilterClause(bean);
								getAssociatedClassesComboBox().removeAllItems();
								getAssociatedAttributesComboBox()
										.removeAllItems();
								getClassAliasText().setText("");
								getClassAttributeAliasText().setText("");
								((DefaultListModel) getFilterChainList()
										.getModel()).clear();
								progressPanel.stopProgress("Filter Created.");
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
								progressPanel.stopProgress("Error");
							} finally {
								enableAll();

							}
							if (associatedAttributesComboBox.getItemCount() <= 0) {
								getJButton().setEnabled(false);
							} else {
								getJButton().setEnabled(true);
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
			jButton1.setText("done");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dialog.dispose();
				}
			});

		}
		return jButton1;
	}

	/**
	 * This method initializes titlePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new TitlePanel("Instance Level Security",
					"Create instance level security filters.");
		}
		return titlePanel;
	}

	public void enableAll() {
		getAddFCButton().setEnabled(true);
		getRemoveLFCButton().setEnabled(true);
		getJButton().setEnabled(true);
		getJButton1().setEnabled(true);
		getFilterChainList().setEnabled(true);
		getAssociatedAttributesComboBox().setEnabled(true);
		getAssociatedClassesComboBox().setEnabled(true);
		getClassAliasText().setEnabled(true);
		getClassAttributeAliasText().setEnabled(true);
		getClassNamesCombo().setEnabled(true);
	}

	public void disableAll() {
		getAddFCButton().setEnabled(false);
		getRemoveLFCButton().setEnabled(false);
		getJButton().setEnabled(false);
		getJButton1().setEnabled(false);
		getFilterChainList().setEnabled(false);
		getAssociatedAttributesComboBox().setEnabled(false);
		getAssociatedClassesComboBox().setEnabled(false);
		getClassAliasText().setEnabled(false);
		getClassAttributeAliasText().setEnabled(false);
		getClassNamesCombo().setEnabled(false);
	}
} // @jve:decl-index=0:visual-constraint="-359,554"
