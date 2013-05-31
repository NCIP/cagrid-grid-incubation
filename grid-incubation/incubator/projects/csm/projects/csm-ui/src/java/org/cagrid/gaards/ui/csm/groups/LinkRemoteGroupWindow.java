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
package org.cagrid.gaards.ui.csm.groups;

import gov.nih.nci.cagrid.common.Runner;
import gov.nih.nci.cagrid.common.Utils;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cagrid.gaards.csm.client.Application;
import org.cagrid.gaards.ui.common.ProgressPanel;
import org.cagrid.gaards.ui.common.TitlePanel;
import org.cagrid.gaards.ui.gridgrouper.selector.GroupSelector;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.utils.ErrorDialog;


public class LinkRemoteGroupWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JPanel buttonPanel = null;

    private JButton link = null;

    private Application application;

    private JPanel remoteGroupPanel = null;

    private JTextField gridGrouper = null;

    private JPanel titlePanel = null;

    private JLabel jLabel = null;

    private JLabel jLabel1 = null;

    private JTextField remoteGroupName = null;

    private JButton find = null;

    private JLabel jLabel2 = null;

    private JTextField localGroupName = null;

    private ProgressPanel progress = null;

    private boolean groupLinked = false;


    /**
     * This is the default constructor
     */
    public LinkRemoteGroupWindow(Application application) {
        super(GridApplication.getContext().getApplication());
        this.application = application;
        initialize();
    }


    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(600, 225);
        this.setContentPane(getJContentPane());
        this.setTitle("Link Remote Group");
    }


    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.weightx = 1.0D;
            gridBagConstraints4.gridy = 3;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.weightx = 1.0D;
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints11.weightx = 1.0D;
            gridBagConstraints11.fill = GridBagConstraints.BOTH;
            gridBagConstraints11.weighty = 1.0D;
            gridBagConstraints11.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.gridx = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getButtonPanel(), gridBagConstraints1);
            jContentPane.add(getRemoteGroupPanel(), gridBagConstraints11);
            jContentPane.add(getTitlePanel(), gridBagConstraints3);
            jContentPane.add(getProgress(), gridBagConstraints4);
        }
        return jContentPane;
    }


    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(getLink(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes addAdmin
     * 
     * @return javax.swing.JButton
     */
    private JButton getLink() {
        if (link == null) {
            link = new JButton();
            link.setText("Link Remote Group");
            getRootPane().setDefaultButton(link);
            link.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runner runner = new Runner() {
                        public void execute() {
                            linkRemoteGroup();
                        }
                    };
                    try {
                        GridApplication.getContext().executeInBackground(runner);
                    } catch (Exception t) {
                        t.getMessage();
                    }

                }
            });

        }
        return link;
    }


    public boolean wasRemoteGroupLinked() {
        return groupLinked;
    }


    private void linkRemoteGroup() {
        try {
            getProgress().showProgress("Linking remote group...");
            link.setEnabled(false);
            this.application.linkRemoteGroup(Utils.clean(getGridGrouper().getText()), Utils.clean(getRemoteGroupName()
                .getText()), Utils.clean(getLocalGroupName().getText()));
            groupLinked = true;
            getProgress().stopProgress("");
            dispose();
            GridApplication.getContext().showMessage("Successfully linked the remote group.");
        } catch (Exception e) {
            getProgress().stopProgress("Error");
            ErrorDialog.showError(e);
            link.setEnabled(true);
        }
    }


    /**
     * This method initializes remoteGroupPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRemoteGroupPanel() {
        if (remoteGroupPanel == null) {
            int topInset = 10;
            GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
            gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints31.gridy = 2;
            gridBagConstraints31.weightx = 1.0;
            gridBagConstraints31.anchor = GridBagConstraints.WEST;
            gridBagConstraints31.insets = new Insets(topInset, 2, 2, 2);
            gridBagConstraints31.gridwidth = 2;
            gridBagConstraints31.gridx = 1;
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.anchor = GridBagConstraints.WEST;
            gridBagConstraints21.insets = new Insets(topInset, 2, 2, 2);
            gridBagConstraints21.gridy = 2;
            jLabel2 = new JLabel();
            jLabel2.setText("Local Group Name");
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 2;
            gridBagConstraints13.gridheight = 2;
            gridBagConstraints13.gridy = 0;
            gridBagConstraints13.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints13.insets = new Insets(2, 0, 2, 0);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.weightx = 1.0;
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.gridx = 0;
            gridBagConstraints12.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints12.anchor = GridBagConstraints.WEST;
            gridBagConstraints12.gridy = 1;
            jLabel1 = new JLabel();
            jLabel1.setText("Remote Group Name");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Grid Grouper");
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.weightx = 1.0;
            remoteGroupPanel = new JPanel();
            remoteGroupPanel.setLayout(new GridBagLayout());
            remoteGroupPanel.add(getGridGrouper(), gridBagConstraints7);
            remoteGroupPanel.add(jLabel, gridBagConstraints);
            remoteGroupPanel.add(jLabel1, gridBagConstraints12);
            remoteGroupPanel.add(getRemoteGroupName(), gridBagConstraints2);
            remoteGroupPanel.add(getFind(), gridBagConstraints13);
            remoteGroupPanel.add(jLabel2, gridBagConstraints21);
            remoteGroupPanel.add(getLocalGroupName(), gridBagConstraints31);
        }
        return remoteGroupPanel;
    }


    /**
     * This method initializes gridGrouper
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getGridGrouper() {
        if (gridGrouper == null) {
            gridGrouper = new JTextField();
        }
        return gridGrouper;
    }


    /**
     * This method initializes titlePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTitlePanel() {
        if (titlePanel == null) {
            titlePanel = new TitlePanel("Link Remote Group",
                "Link a Grid Grouper group to CSM, such that is may be used for provisioning access control policy.");
        }
        return titlePanel;
    }


    /**
     * This method initializes remoteGroupName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getRemoteGroupName() {
        if (remoteGroupName == null) {
            remoteGroupName = new JTextField();
        }
        return remoteGroupName;
    }


    /**
     * This method initializes find
     * 
     * @return javax.swing.JButton
     */
    private JButton getFind() {
        if (find == null) {
            find = new JButton();
            find.setText("Find...");
            find.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    GroupSelector selector = new GroupSelector(GridApplication.getContext().getApplication());
                    selector.setModal(true);
                    GridApplication.getContext().showDialog(selector);
                    gov.nih.nci.cagrid.gridgrouper.client.Group group = selector.getSelectedGroup();
                    getGridGrouper().setText(group.getGridGrouper().getName());
                    getRemoteGroupName().setText(group.getName());
                }
            });
        }
        return find;
    }


    /**
     * This method initializes localGroupName
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLocalGroupName() {
        if (localGroupName == null) {
            localGroupName = new JTextField();
        }
        return localGroupName;
    }


    /**
     * This method initializes progress
     * 
     * @return javax.swing.JPanel
     */
    private ProgressPanel getProgress() {
        if (progress == null) {
            progress = new ProgressPanel();
            progress.stopProgress("");
        }
        return progress;
    }

}
