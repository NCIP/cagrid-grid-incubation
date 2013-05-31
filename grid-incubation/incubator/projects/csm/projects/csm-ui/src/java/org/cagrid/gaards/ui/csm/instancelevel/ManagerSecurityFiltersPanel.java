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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cagrid.gaards.csm.bean.FilterClauseSearchCriteria;
import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.FilterClause;
import org.cagrid.gaards.csm.filters.client.FilterCreatorClient;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.csm.CSMLookAndFeel;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;

public class ManagerSecurityFiltersPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private FilterClause currentFilter = null;
	private ApplicationConfigDialog applicationConfiguration = null;
	private DomainModelConfigDialog domainModelConfiguration = null;
	private JSplitPane jSplitPane = null;
	private JPanel searchPanel = null;
	private JPanel editPanel = null;
	private JPanel infoPanel = null;
	private JPanel savePanel = null;
	private JButton saveButton = null;
	private ProgressPanel progress;
	private Application application;
	private JButton searchButton = null;
	private JScrollPane jScrollPane = null;
	private FiltersList filtersList = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField classNameText = null;
	private JTextField targetClassText = null;
	private JTextField targetClassAttText = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JScrollPane jScrollPane1 = null;
	private JScrollPane jScrollPane2 = null;
	private JTextArea groupSQLText = null;
	private JTextArea userSQLText = null;
	private JButton jButton = null;
	private JScrollPane jScrollPane3 = null;
	private JTextArea filterChainTextArea = null;
	private JButton createFiltersButton = null;
	/**
	 * This is the default constructor
	 */
	public ManagerSecurityFiltersPanel(Application application,
			ProgressPanel progress) {
		super();
		this.application = application;
		this.progress = progress;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		applicationConfiguration = new ApplicationConfigDialog(application);
		domainModelConfiguration = new DomainModelConfigDialog(application);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		this.setSize(600, 400);
		this.setLayout(new GridBagLayout());
		this.add(getJSplitPane(), gridBagConstraints);
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getSearchPanel());
			jSplitPane.setRightComponent(getEditPanel());
			jSplitPane.setDividerLocation(.20);
		}
		return jSplitPane;
	}

	/**
	 * This method initializes searchPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 3;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.gridx = 0;
			searchPanel = new JPanel();
			searchPanel.setLayout(new GridBagLayout());
			searchPanel.add(getSearchButton(), gridBagConstraints4);
			searchPanel.add(getJScrollPane(), gridBagConstraints3);
			searchPanel.add(getJButton(), gridBagConstraints17);
			searchPanel.add(getCreateFiltersButton(), gridBagConstraints10);
		}
		return searchPanel;
	}

	/**
	 * This method initializes editPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getEditPanel() {
		if (editPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weightx = 1.0D;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints1.gridy = 0;
			editPanel = new JPanel();
			editPanel.setLayout(new GridBagLayout());
			editPanel.add(getInfoPanel(), gridBagConstraints1);
			editPanel.add(getSavePanel(), gridBagConstraints2);
		}
		return editPanel;
	}

	/**
	 * This method initializes infoPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.BOTH;
			gridBagConstraints19.gridy = 2;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.weighty = 1.0;
			gridBagConstraints19.gridwidth = 2;
			gridBagConstraints19.gridx = 0;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.BOTH;
			gridBagConstraints16.gridy = 8;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.weighty = 1.0;
			gridBagConstraints16.gridwidth = 2;
			gridBagConstraints16.gridx = 0;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.BOTH;
			gridBagConstraints15.gridy = 6;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.weighty = 1.0;
			gridBagConstraints15.gridwidth = 2;
			gridBagConstraints15.gridx = 0;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.fill = GridBagConstraints.BOTH;
			gridBagConstraints14.gridwidth = 2;
			gridBagConstraints14.gridy = 7;
			jLabel5 = new JLabel();
			jLabel5.setText("Group Query");
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.gridwidth = 2;
			gridBagConstraints13.gridy = 5;
			jLabel4 = new JLabel();
			jLabel4.setText("User Query");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 4;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridy = 3;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 4;
			jLabel3 = new JLabel();
			jLabel3.setText("Target Class Attribute");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 3;
			jLabel2 = new JLabel();
			jLabel2.setText("Target Class");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Filter Chain");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Class Name");
			infoPanel = new JPanel();
			infoPanel.setLayout(new GridBagLayout());
			infoPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Security Filter Information",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, CSMLookAndFeel
							.getPanelLabelColor()));
			infoPanel.add(jLabel, gridBagConstraints5);
			infoPanel.add(jLabel1, gridBagConstraints6);
			infoPanel.add(jLabel2, gridBagConstraints7);
			infoPanel.add(jLabel3, gridBagConstraints8);
			infoPanel.add(getClassNameText(), gridBagConstraints9);
			infoPanel.add(getTargetClassText(), gridBagConstraints11);
			infoPanel.add(getTargetClassAttText(), gridBagConstraints12);
			infoPanel.add(jLabel4, gridBagConstraints13);
			infoPanel.add(jLabel5, gridBagConstraints14);
			infoPanel.add(getJScrollPane1(), gridBagConstraints15);
			infoPanel.add(getJScrollPane2(), gridBagConstraints16);
			infoPanel.add(getJScrollPane3(), gridBagConstraints19);
		}
		return infoPanel;
	}

	/**
	 * This method initializes savePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSavePanel() {
		if (savePanel == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.gridy = 0;
			savePanel = new JPanel();
			savePanel.setLayout(new GridBagLayout());
			savePanel.add(getSaveButton(), gridBagConstraints18);
		}
		return savePanel;
	}

	/**
	 * This method initializes saveButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText("save");
			saveButton.setEnabled(false);
			saveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {

						public void execute() {
							progress.showProgress("Saving filter...");
							try {
								disableAll();
								currentFilter.setSQLForGroup(getGroupSQLText().getText());
								currentFilter.setSQLForUser(getUserSQLText().getText());
								currentFilter.modify();
								currentFilter = null;
								getClassNameText().setText("");
								getFilterChainTextArea().setText("");
								getTargetClassText().setText("");
								getTargetClassAttText().setText("");
								getUserSQLText().setText("");
								getGroupSQLText().setText("");
								getSaveButton().setEnabled(false);
								progress.stopProgress("Filters saved.");
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
								progress.stopProgress("Error");
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
		return saveButton;
	}

	/**
	 * This method initializes searchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setText("load");
			searchButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Runner runner = new Runner() {

						public void execute() {
							progress.showProgress("Loading filters...");
							try {
								disableAll();
								getSaveButton().setEnabled(false);
								getClassNameText().setText("");
								getFilterChainTextArea().setText("");
								getTargetClassText().setText("");
								getTargetClassAttText().setText("");
								getUserSQLText().setText("");
								getGroupSQLText().setText("");
								
								FilterClauseSearchCriteria fcsc = new FilterClauseSearchCriteria();
								List<FilterClause> filters = application
										.getFilterClauses(fcsc);
								filtersList.clearFilters();
								filtersList.setFilters(filters);
								
								progress.stopProgress("Filters loaded.");
							} catch (Exception ex) {
								ErrorDialog.showError(ex);
								progress.stopProgress("Error");
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
		return searchButton;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getFiltersList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes filtersList
	 * 
	 * @return javax.swing.JList
	 */
	private FiltersList getFiltersList() {
		if (filtersList == null) {
			filtersList = new FiltersList();
			filtersList.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					if (SwingUtilities.isRightMouseButton(e)
							&& getFiltersList().getSelectedValue() != null) {
						FiltersPopUpMenu menu = new FiltersPopUpMenu(
								ManagerSecurityFiltersPanel.this);
						menu.show(ManagerSecurityFiltersPanel.this, e.getX(), e.getY());
					}
				}

			});
			filtersList.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					currentFilter = (FilterClause) getFiltersList()
							.getSelectedValue();
					if (currentFilter != null) {
						getClassNameText()
								.setText(currentFilter.getClassName());
						getFilterChainTextArea().setText(
								currentFilter.getFilterChain().replaceAll(",", "\n"));
						getTargetClassText().setText(
								currentFilter.getTargetClassName());
						getTargetClassAttText().setText(
								currentFilter.getTargetClassAttributeName());
						getUserSQLText().setText(currentFilter.getSQLForUser());
						getGroupSQLText().setText(
								currentFilter.getSQLForGroup());
						getSaveButton().setEnabled(false);
					} else {
						getSaveButton().setEnabled(false);
						getClassNameText().setText("");
						getFilterChainTextArea().setText("");
						getTargetClassText().setText("");
						getTargetClassAttText().setText("");
						getUserSQLText().setText("");
						getGroupSQLText().setText("");
					}
				}
			});

		}
		return filtersList;
	}

	/**
	 * This method initializes classNameText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getClassNameText() {
		if (classNameText == null) {
			classNameText = new JTextField();
			classNameText.setEditable(false);
			classNameText.setEnabled(false);
		}
		return classNameText;
	}

	/**
	 * This method initializes targetClassText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTargetClassText() {
		if (targetClassText == null) {
			targetClassText = new JTextField();
			targetClassText.setEditable(false);
			targetClassText.setEnabled(false);
		}
		return targetClassText;
	}

	/**
	 * This method initializes targetClassAttText
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTargetClassAttText() {
		if (targetClassAttText == null) {
			targetClassAttText = new JTextField();
			targetClassAttText.setEditable(false);
			targetClassAttText.setEnabled(false);
		}
		return targetClassAttText;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane1.setViewportView(getUserSQLText());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jScrollPane2
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane2.setViewportView(getGroupSQLText());
		}
		return jScrollPane2;
	}

	/**
	 * This method initializes groupSQLText
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getGroupSQLText() {
		if (groupSQLText == null) {
			groupSQLText = new JTextArea();
			groupSQLText.setLineWrap(true);
			groupSQLText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					getSaveButton().setEnabled(true);
				}
			});
		}
		return groupSQLText;
	}

	/**
	 * This method initializes userSQLText
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getUserSQLText() {
		if (userSQLText == null) {
			userSQLText = new JTextArea();
			userSQLText.setLineWrap(true);
			userSQLText.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					getSaveButton().setEnabled(true);
				}
			});
		}
		return userSQLText;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("create via application jars");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					applicationConfiguration.setModal(true);
					GridApplication.getContext().showDialog(
							applicationConfiguration);

					FilterCreatorClient creator = applicationConfiguration
							.createFilterCreatorClient();
					if (creator != null) {
						NewSecurityFilterDialog dialog = new NewSecurityFilterDialog(
								creator, application);
						dialog.setModal(true);
						GridApplication.getContext().showDialog(dialog);
						try {
							creator.destroy();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
					}
				}
			});

		}
		return jButton;
	}

	public void removeSelectedFilter() {
		if (getFiltersList().getSelectedValue() != null) {

			Runner runner = new Runner() {

				public void execute() {
					progress.showProgress("Deleting filter...");
					try {
						disableAll();
						application
								.removeFilterClause(((FilterClause) getFiltersList()
										.getSelectedValue()).getId());
						getFiltersList().removeSelected();
						progress.stopProgress("Filter deleted.");
					} catch (Exception ex) {
						ErrorDialog.showError(ex);
						progress.stopProgress("Error");
					} finally {
						enableAll();
					}
				}
			};
			try {
				GridApplication.getContext().executeInBackground(runner);
			} catch (Exception t) {
				t.getMessage();
			}
		}
	}

	public void enableAll() {
		
		getUserSQLText().setEnabled(true);
		getGroupSQLText().setEnabled(true);
		getJButton().setEnabled(true);
		getSearchButton().setEnabled(true);
		getCreateFiltersButton().setEnabled(true);
	}

	public void disableAll() {
		getUserSQLText().setEnabled(false);
		getGroupSQLText().setEnabled(false);
		getJButton().setEnabled(false);
		getSearchButton().setEnabled(false);
		getCreateFiltersButton().setEnabled(false);
	}

	/**
	 * This method initializes jScrollPane3	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setPreferredSize(new Dimension(75, 300));
			jScrollPane3.setViewportView(getFilterChainTextArea());
		}
		return jScrollPane3;
	}

	/**
	 * This method initializes filterChainTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getFilterChainTextArea() {
		if (filterChainTextArea == null) {
			filterChainTextArea = new JTextArea();
			filterChainTextArea.setEditable(false);
			filterChainTextArea.setEnabled(false);
		}
		return filterChainTextArea;
	}

	/**
	 * This method initializes createFiltersButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCreateFiltersButton() {
		if (createFiltersButton == null) {
			createFiltersButton = new JButton();
			createFiltersButton.setText("create via domain model");
			createFiltersButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					domainModelConfiguration.setModal(true);
					GridApplication.getContext().showDialog(
							domainModelConfiguration);

					FilterCreatorClient creator = domainModelConfiguration
							.createFilterCreatorClient();
					if (creator != null) {
						NewSecurityFilterDialog dialog = new NewSecurityFilterDialog(
								creator, application);
						dialog.setModal(true);
						GridApplication.getContext().showDialog(dialog);
						try {
							creator.destroy();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
		return createFiltersButton;
	}

}
