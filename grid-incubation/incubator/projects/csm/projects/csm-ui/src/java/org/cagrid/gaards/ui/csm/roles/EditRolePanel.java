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
package org.cagrid.gaards.ui.csm.roles;

import gov.nih.nci.cagrid.common.portal.validation.IconFeedbackPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.rmi.RemoteException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.csm.client.Role;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class EditRolePanel extends JPanel {

	private int OK = 0;
	private int CANCEL = 1;
	private static final long serialVersionUID = 1L;
	private ValidationResultModel validationModel = null; // @jve:decl-index=0:
	private final String NAME = "Protection Group Name"; // @jve:decl-index=0:
	private Application csmApp = null;
	private Role role = null; // @jve:decl-index=0:
	private JLabel nameLabel = null;
	private JLabel descriptionLabel = null;
	private JTextField nameTextField = null;
	private JTextArea descriptionTextField = null;
	private JPanel feedbackPanel = null;
	private JScrollPane jScrollPane = null;
	private JLabel idLabel = null;
	private JLabel idText = null;

	/**
	 * This is the default constructor
	 */
	public EditRolePanel(Role role, Application csmApp) {
		super();
		this.csmApp = csmApp;
		this.role = role;
		initialize();
		initValidation();
	}

	public EditRolePanel(Application csmApp) {
		super();
		this.csmApp = csmApp;
		initialize();
		initValidation();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.fill = GridBagConstraints.BOTH;
		gridBagConstraints4.weightx = 1.0D;
		gridBagConstraints4.weighty = 1.0D;
		gridBagConstraints4.gridy = 0;
		this.setSize(326, 141);
		this.setLayout(new GridBagLayout());
		validationModel = new DefaultValidationResultModel();
		this.add(new IconFeedbackPanel(validationModel, getFeedbackPanel()),
				gridBagConstraints4);
		if (role != null) {
			idText.setText(String.valueOf(role.getId()));
			getNameTextField().setText(role.getName());
			if (role.getDescription() != null) {
				getDescriptionTextField().setText(role.getDescription());
			} else {
				getDescriptionTextField().setText("");
			}
		} else {
			getNameTextField().setText("");
			getDescriptionTextField().setText("");
		}
	}

	private void initValidation() {
		ValidationComponentUtils.setMessageKey(getNameTextField(), NAME);

		validateInput();
		updateComponentTreeSeverity();
	}

	private void validateInput() {

		ValidationResult result = new ValidationResult();

		if (ValidationUtils.isNotBlank(this.getNameTextField().getText())) {

		} else {
			result.add(new SimpleValidationMessage(NAME + " cannot be blank.",
					Severity.ERROR, NAME));
		}

		this.validationModel.setResult(result);
		updateComponentTreeSeverity();

	}

	private void updateComponentTreeSeverity() {
		ValidationComponentUtils
				.updateComponentTreeMandatoryAndBlankBackground(this);
		ValidationComponentUtils.updateComponentTreeSeverityBackground(this,
				this.validationModel.getResult());
	}

	public Role getRole() throws CSMInternalFault, AccessDeniedFault,
			CSMTransactionFault, RemoteException {
		if (this.role == null) {
			role = csmApp.createRole(this.getNameTextField().getText(), this
					.getDescriptionTextField().getText());
		} else {
			role.setName(this.getNameTextField().getText());
			role.setDescription(this.getDescriptionTextField().getText());
			role.modify();
		}
		return role;
	}

	/**
	 * This method initializes nameTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					validateInput();
				}
			});
		}
		return nameTextField;
	}

	/**
	 * This method initializes descriptionTextField
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getDescriptionTextField() {
		if (descriptionTextField == null) {
			descriptionTextField = new JTextArea();
			descriptionTextField.setLineWrap(true);
			descriptionTextField
					.addKeyListener(new java.awt.event.KeyAdapter() {
						public void keyTyped(java.awt.event.KeyEvent e) {
							validateInput();
						}
					});
		}
		return descriptionTextField;
	}

	/**
	 * This method initializes feedbackPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFeedbackPanel() {
		if (feedbackPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints21.gridy = 0;
			idText = new JLabel();
			idText.setText("");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.weighty = 0.0D;
			gridBagConstraints12.weightx = 0.0D;
			gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints12.gridy = 0;
			idLabel = new JLabel();
			idLabel.setText("ID");
			idLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.weightx = 1.0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridx = -1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.ipadx = 2;
			gridBagConstraints1.ipady = 2;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.ipadx = 2;
			gridBagConstraints.ipady = 2;
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			descriptionLabel = new JLabel();
			descriptionLabel.setText("Description");
			nameLabel = new JLabel();
			nameLabel.setText("Name");
			nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			nameLabel.setHorizontalTextPosition(SwingConstants.TRAILING);
			feedbackPanel = new JPanel();
			feedbackPanel.setLayout(new GridBagLayout());
			feedbackPanel.add(nameLabel, gridBagConstraints);
			feedbackPanel.add(descriptionLabel, gridBagConstraints1);
			feedbackPanel.add(getNameTextField(), gridBagConstraints2);
			feedbackPanel.add(getJScrollPane(), gridBagConstraints11);
			feedbackPanel.add(idLabel, gridBagConstraints12);
			feedbackPanel.add(idText, gridBagConstraints21);
		}
		return feedbackPanel;
	}

	public int showDialog(Component parent) {
		JOptionPane optionPane = new JOptionPane(null);
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		optionPane.add(this, 0);
		String title = "";
		if (role != null) {
			title = "Edit Role";
		} else {
			title = "New Role";
		}
		JDialog dialog = optionPane.createDialog(parent, title);
		dialog.setResizable(true);
		dialog.pack();
		dialog.setVisible(true);
		while (validationModel.getResult().hasErrors()
				&& (Integer) optionPane.getValue() == JOptionPane.OK_OPTION) {
			JOptionPane.showMessageDialog(dialog, validationModel.getResult()
					.getMessages().get(0).formattedText());
			dialog.setVisible(true);
		}
		return (Integer) optionPane.getValue();

	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane.setPreferredSize(new Dimension(300, 100));
			jScrollPane.setViewportView(getDescriptionTextField());
		}
		return jScrollPane;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JOptionPane optionPane = new JOptionPane(null);
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		optionPane.add(new EditRolePanel(null), 0);
		JDialog dialog = optionPane.createDialog(frame, "Testing");
		dialog.setResizable(true);
		dialog.pack();
		dialog.setVisible(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
