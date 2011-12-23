package org.cagrid.tide.tools.client.portal;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import java.rmi.RemoteException;

import javax.swing.JProgressBar;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.tide.descriptor.TideInformation;
import org.cagrid.tide.replica.client.TideReplicaManagerClient;
import org.cagrid.tide.tools.client.portal.retrieval.TideInformationJTree;

public class Retriever extends JFrame {

	private JSplitPane mainSplitPane = null;
	private JPanel replicaManagerPanel = null;
	private JLabel replicaManagerLabel = null;
	private JTextField replicaManagerTextField = null;
	private JPanel replicaManagerInfoPanel = null;
	private JPanel replicaManagerSearchPanel = null;
	private JScrollPane queryResultsScrollPane = null;
	private TideInformationJTree queryResultsTree = null;
	private JLabel queryStringLabel = null;
	private JTextField queryStringTextField = null;
	private JButton queryButton = null;
	private JButton listButton = null;
	private JPanel queryPanel = null;
	private JPanel retrievalPanel = null;
	private JPanel progressPanel = null;
	private JProgressBar retrievalProgressBar = null;
	private JSplitPane retrievalsSplitPane = null;
	private JPanel retrievalsPanel = null;
	private JPanel retrievalInformationPanel = null;
	private JScrollPane retrievalsScrollPane = null;
	private JTable retrievalsTable = null;
	/**
	 * This method initializes 
	 * 
	 */
	public Retriever() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(862, 617));
        this.setTitle("Tide Retriever");
        this.setContentPane(getMainSplitPane());
			
	}

	/**
	 * This method initializes mainSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getMainSplitPane() {
		if (mainSplitPane == null) {
			mainSplitPane = new JSplitPane();
			mainSplitPane.setLeftComponent(getReplicaManagerPanel());
			mainSplitPane.setRightComponent(getRetrievalPanel());
		}
		return mainSplitPane;
	}

	/**
	 * This method initializes replicaManagerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getReplicaManagerPanel() {
		if (replicaManagerPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.weightx = 1.0D;
			gridBagConstraints4.weighty = 1.0D;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 0;
			replicaManagerLabel = new JLabel();
			replicaManagerLabel.setText("Replica Manager");
			replicaManagerPanel = new JPanel();
			replicaManagerPanel.setLayout(new GridBagLayout());
			replicaManagerPanel.add(getReplicaManagerInfoPanel(), gridBagConstraints3);
			replicaManagerPanel.add(getReplicaManagerSearchPanel(), gridBagConstraints4);
		}
		return replicaManagerPanel;
	}

	/**
	 * This method initializes replicaManagerTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getReplicaManagerTextField() {
		if (replicaManagerTextField == null) {
			replicaManagerTextField = new JTextField();
			replicaManagerTextField.setPreferredSize(new Dimension(100, 20));
		}
		return replicaManagerTextField;
	}

	/**
	 * This method initializes replicaManagerInfoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getReplicaManagerInfoPanel() {
		if (replicaManagerInfoPanel == null) {
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.gridwidth = 2;
			gridBagConstraints51.gridy = 2;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.gridwidth = 2;
			gridBagConstraints41.gridy = 1;
			queryStringLabel = new JLabel();
			queryStringLabel.setText("Query");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.gridy = 0;
			replicaManagerInfoPanel = new JPanel();
			replicaManagerInfoPanel.setLayout(new GridBagLayout());
			replicaManagerInfoPanel.add(replicaManagerLabel, gridBagConstraints1);
			replicaManagerInfoPanel.add(getReplicaManagerTextField(), gridBagConstraints);
			replicaManagerInfoPanel.add(getListButton(), gridBagConstraints41);
			replicaManagerInfoPanel.add(getQueryPanel(), gridBagConstraints51);
		}
		return replicaManagerInfoPanel;
	}

	/**
	 * This method initializes replicaManagerSearchPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getReplicaManagerSearchPanel() {
		if (replicaManagerSearchPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.weightx = 1.0;
			replicaManagerSearchPanel = new JPanel();
			replicaManagerSearchPanel.setLayout(new GridBagLayout());
			replicaManagerSearchPanel.setBorder(BorderFactory.createTitledBorder(null, "Replica Manager Results", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			replicaManagerSearchPanel.add(getQueryResultsScrollPane(), gridBagConstraints2);
		}
		return replicaManagerSearchPanel;
	}

	/**
	 * This method initializes queryResultsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getQueryResultsScrollPane() {
		if (queryResultsScrollPane == null) {
			queryResultsScrollPane = new JScrollPane();
			queryResultsScrollPane.setViewportView(getQueryResultsTree());
		}
		return queryResultsScrollPane;
	}

	/**
	 * This method initializes queryResultsTree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private TideInformationJTree getQueryResultsTree() {
		if (queryResultsTree == null) {
			queryResultsTree = new TideInformationJTree();
		}
		return queryResultsTree;
	}

	/**
	 * This method initializes queryStringTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getQueryStringTextField() {
		if (queryStringTextField == null) {
			queryStringTextField = new JTextField();
			queryStringTextField.setPreferredSize(new Dimension(100, 20));
		}
		return queryStringTextField;
	}

	/**
	 * This method initializes queryButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQueryButton() {
		if (queryButton == null) {
			queryButton = new JButton();
			queryButton.setText("Query");
			queryButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						TideReplicaManagerClient client = new TideReplicaManagerClient(getReplicaManagerTextField().getText());
						TideInformation[] tides = client.queryTides(getQueryStringTextField().getText());
						getQueryResultsTree().setTides(tides);
					} catch (MalformedURIException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
		return queryButton;
	}

	/**
	 * This method initializes listButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getListButton() {
		if (listButton == null) {
			listButton = new JButton();
			listButton.setText("List All");
			listButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						TideReplicaManagerClient client = new TideReplicaManagerClient(getReplicaManagerTextField().getText());
						TideInformation[] tides = client.listTides();
						getQueryResultsTree().setTides(tides);
					} catch (MalformedURIException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
		return listButton;
	}

	/**
	 * This method initializes queryPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getQueryPanel() {
		if (queryPanel == null) {
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.NONE;
			gridBagConstraints7.gridx = -1;
			gridBagConstraints7.gridy = -1;
			gridBagConstraints7.gridwidth = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints6.gridx = -1;
			gridBagConstraints6.gridy = -1;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			gridBagConstraints5.insets = new Insets(2, 2, 2, 2);
			queryPanel = new JPanel();
			queryPanel.setLayout(new GridBagLayout());
			queryPanel.setBorder(BorderFactory.createTitledBorder(null, "Query", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			queryPanel.add(queryStringLabel, gridBagConstraints5);
			queryPanel.add(getQueryStringTextField(), gridBagConstraints6);
			queryPanel.add(getQueryButton(), gridBagConstraints7);
		}
		return queryPanel;
	}

	/**
	 * This method initializes retrievalPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRetrievalPanel() {
		if (retrievalPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.BOTH;
			gridBagConstraints10.weighty = 1.0;
			gridBagConstraints10.weightx = 1.0;
			retrievalPanel = new JPanel();
			retrievalPanel.setLayout(new GridBagLayout());
			retrievalPanel.add(getRetrievalsSplitPane(), gridBagConstraints10);
		}
		return retrievalPanel;
	}

	/**
	 * This method initializes progressPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getProgressPanel() {
		if (progressPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints8.weightx = 1.0D;
			gridBagConstraints8.gridx = 0;
			progressPanel = new JPanel();
			progressPanel.setLayout(new GridBagLayout());
			progressPanel.setBorder(BorderFactory.createTitledBorder(null, "Progress", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			progressPanel.add(getRetrievalProgressBar(), gridBagConstraints8);
		}
		return progressPanel;
	}

	/**
	 * This method initializes retrievalProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getRetrievalProgressBar() {
		if (retrievalProgressBar == null) {
			retrievalProgressBar = new JProgressBar();
		}
		return retrievalProgressBar;
	}

	/**
	 * This method initializes retrievalsSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getRetrievalsSplitPane() {
		if (retrievalsSplitPane == null) {
			retrievalsSplitPane = new JSplitPane();
			retrievalsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			retrievalsSplitPane.setBottomComponent(getRetrievalInformationPanel());
			retrievalsSplitPane.setTopComponent(getRetrievalsPanel());
		}
		return retrievalsSplitPane;
	}

	/**
	 * This method initializes retrievalsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRetrievalsPanel() {
		if (retrievalsPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.weightx = 1.0;
			retrievalsPanel = new JPanel();
			retrievalsPanel.setLayout(new GridBagLayout());
			retrievalsPanel.add(getRetrievalsScrollPane(), gridBagConstraints11);
		}
		return retrievalsPanel;
	}

	/**
	 * This method initializes retrievalInformationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRetrievalInformationPanel() {
		if (retrievalInformationPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridx = -1;
			gridBagConstraints9.gridy = -1;
			gridBagConstraints9.weightx = 1.0D;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			retrievalInformationPanel = new JPanel();
			retrievalInformationPanel.setLayout(new GridBagLayout());
			retrievalInformationPanel.add(getProgressPanel(), gridBagConstraints9);
		}
		return retrievalInformationPanel;
	}

	/**
	 * This method initializes retrievalsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getRetrievalsScrollPane() {
		if (retrievalsScrollPane == null) {
			retrievalsScrollPane = new JScrollPane();
			retrievalsScrollPane.setViewportView(getRetrievalsTable());
		}
		return retrievalsScrollPane;
	}

	/**
	 * This method initializes retrievalsTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getRetrievalsTable() {
		if (retrievalsTable == null) {
			retrievalsTable = new JTable();
		}
		return retrievalsTable;
	}
	
	public static void main(String [] args){
		Retriever ret = new Retriever();
		ret.setVisible(true);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"