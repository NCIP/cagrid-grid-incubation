namespace QueryServiceControl
{
    partial class QueryServiceControl
    {
        /// <summary> 
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.txtTerm = new System.Windows.Forms.TextBox();
            this.btnSearch = new System.Windows.Forms.Button();
            this.grpControls = new System.Windows.Forms.GroupBox();
            this.tableLayoutPanel4 = new System.Windows.Forms.TableLayoutPanel();
            this.cbResources = new System.Windows.Forms.ComboBox();
            this.statusMsg = new System.Windows.Forms.Label();
            this.cbSearchType = new System.Windows.Forms.ComboBox();
            this.grpResults = new System.Windows.Forms.GroupBox();
            this.tableLayoutPanelResults = new System.Windows.Forms.TableLayoutPanel();
            this.lstResults = new System.Windows.Forms.ListBox();
            this.btnUse = new System.Windows.Forms.Button();
            this.btnBack = new System.Windows.Forms.Button();
            this.btnForward = new System.Windows.Forms.Button();
            this.btnAnnotate = new System.Windows.Forms.Button();
            this.grpDetails = new System.Windows.Forms.GroupBox();
            this.tabControlDetails = new System.Windows.Forms.TabControl();
            this.tabPageDef = new System.Windows.Forms.TabPage();
            this.wbDetailsDef = new System.Windows.Forms.WebBrowser();
            this.tabPagePropsValues = new System.Windows.Forms.TabPage();
            this.wbDetailsPropsValues = new System.Windows.Forms.WebBrowser();
            this.tabOcProps = new System.Windows.Forms.TabPage();
            this.wbOcProps = new System.Windows.Forms.WebBrowser();
            this.otherDetails = new System.Windows.Forms.TabPage();
            this.wbDetailsOther = new System.Windows.Forms.WebBrowser();
            this.tabControl = new System.Windows.Forms.TabControl();
            this.tabFreeText = new System.Windows.Forms.TabPage();
            this.tableLayoutPanel3 = new System.Windows.Forms.TableLayoutPanel();
            this.tableLayoutPanel5 = new System.Windows.Forms.TableLayoutPanel();
            this.btnCleanURLs = new System.Windows.Forms.Button();
            this.cbServiceUrls = new System.Windows.Forms.ComboBox();
            this.tabClassified = new System.Windows.Forms.TabPage();
            this.tableLayoutPanelClassification = new System.Windows.Forms.TableLayoutPanel();
            this.grpClassificationResults = new System.Windows.Forms.GroupBox();
            this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
            this.lstClassificationQueryResult = new System.Windows.Forms.ListBox();
            this.btnCLSUse = new System.Windows.Forms.Button();
            this.btnForwardCLS = new System.Windows.Forms.Button();
            this.btnBackCLS = new System.Windows.Forms.Button();
            this.grpClassificationScheme = new System.Windows.Forms.GroupBox();
            this.tableLayoutPanel2 = new System.Windows.Forms.TableLayoutPanel();
            this.cbClassificationSchemes = new System.Windows.Forms.ComboBox();
            this.classificationTree = new System.Windows.Forms.TreeView();
            this.btnSearchCLS = new System.Windows.Forms.Button();
            this.statusMsgCLS = new System.Windows.Forms.Label();
            this.grpClassificationResultDetail = new System.Windows.Forms.GroupBox();
            this.tabControlCLSDetails = new System.Windows.Forms.TabControl();
            this.tabPageCLSDetailsDef = new System.Windows.Forms.TabPage();
            this.wbClassificationQueryResultDef = new System.Windows.Forms.WebBrowser();
            this.tabPageCLSDetailsValueDomain = new System.Windows.Forms.TabPage();
            this.wbClassificationQueryResultValueDomain = new System.Windows.Forms.WebBrowser();
            this.txtContext = new System.Windows.Forms.TextBox();
            this.lblContext = new System.Windows.Forms.Label();
            this.grpControls.SuspendLayout();
            this.tableLayoutPanel4.SuspendLayout();
            this.grpResults.SuspendLayout();
            this.tableLayoutPanelResults.SuspendLayout();
            this.grpDetails.SuspendLayout();
            this.tabControlDetails.SuspendLayout();
            this.tabPageDef.SuspendLayout();
            this.tabPagePropsValues.SuspendLayout();
            this.tabOcProps.SuspendLayout();
            this.otherDetails.SuspendLayout();
            this.tabControl.SuspendLayout();
            this.tabFreeText.SuspendLayout();
            this.tableLayoutPanel3.SuspendLayout();
            this.tableLayoutPanel5.SuspendLayout();
            this.tabClassified.SuspendLayout();
            this.tableLayoutPanelClassification.SuspendLayout();
            this.grpClassificationResults.SuspendLayout();
            this.tableLayoutPanel1.SuspendLayout();
            this.grpClassificationScheme.SuspendLayout();
            this.tableLayoutPanel2.SuspendLayout();
            this.grpClassificationResultDetail.SuspendLayout();
            this.tabControlCLSDetails.SuspendLayout();
            this.tabPageCLSDetailsDef.SuspendLayout();
            this.tabPageCLSDetailsValueDomain.SuspendLayout();
            this.SuspendLayout();
            // 
            // txtTerm
            // 
            this.txtTerm.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.txtTerm.Location = new System.Drawing.Point(83, 33);
            this.txtTerm.Name = "txtTerm";
            this.txtTerm.Size = new System.Drawing.Size(210, 20);
            this.txtTerm.TabIndex = 0;
            this.txtTerm.TextChanged += new System.EventHandler(this.txtTerm_TextChanged);
            this.txtTerm.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.txtTerm_KeyPress);
            // 
            // btnSearch
            // 
            this.btnSearch.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.btnSearch.Location = new System.Drawing.Point(3, 93);
            this.btnSearch.Name = "btnSearch";
            this.btnSearch.Size = new System.Drawing.Size(74, 20);
            this.btnSearch.TabIndex = 1;
            this.btnSearch.Text = "Search";
            this.btnSearch.UseVisualStyleBackColor = true;
            this.btnSearch.Click += new System.EventHandler(this.btnSearch_Click);
            // 
            // grpControls
            // 
            this.grpControls.Controls.Add(this.tableLayoutPanel4);
            this.grpControls.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grpControls.Location = new System.Drawing.Point(3, 3);
            this.grpControls.Name = "grpControls";
            this.grpControls.Size = new System.Drawing.Size(302, 135);
            this.grpControls.TabIndex = 2;
            this.grpControls.TabStop = false;
            this.grpControls.Text = "Controls";
            // 
            // tableLayoutPanel4
            // 
            this.tableLayoutPanel4.ColumnCount = 2;
            this.tableLayoutPanel4.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 80F));
            this.tableLayoutPanel4.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel4.Controls.Add(this.cbResources, 0, 0);
            this.tableLayoutPanel4.Controls.Add(this.txtTerm, 1, 1);
            this.tableLayoutPanel4.Controls.Add(this.cbSearchType, 0, 1);
            this.tableLayoutPanel4.Controls.Add(this.txtContext, 1, 2);
            this.tableLayoutPanel4.Controls.Add(this.statusMsg, 1, 3);
            this.tableLayoutPanel4.Controls.Add(this.btnSearch, 0, 3);
            this.tableLayoutPanel4.Controls.Add(this.lblContext, 0, 2);
            this.tableLayoutPanel4.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel4.Location = new System.Drawing.Point(3, 16);
            this.tableLayoutPanel4.Name = "tableLayoutPanel4";
            this.tableLayoutPanel4.RowCount = 4;
            this.tableLayoutPanel4.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel4.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel4.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel4.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel4.Size = new System.Drawing.Size(296, 116);
            this.tableLayoutPanel4.TabIndex = 4;
            // 
            // cbResources
            // 
            this.tableLayoutPanel4.SetColumnSpan(this.cbResources, 2);
            this.cbResources.Dock = System.Windows.Forms.DockStyle.Fill;
            this.cbResources.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cbResources.FormattingEnabled = true;
            this.cbResources.Location = new System.Drawing.Point(3, 3);
            this.cbResources.Name = "cbResources";
            this.cbResources.Size = new System.Drawing.Size(290, 21);
            this.cbResources.TabIndex = 2;
            // 
            // statusMsg
            // 
            this.statusMsg.AutoSize = true;
            this.statusMsg.Location = new System.Drawing.Point(83, 92);
            this.statusMsg.Margin = new System.Windows.Forms.Padding(3, 2, 3, 0);
            this.statusMsg.Name = "statusMsg";
            this.statusMsg.Size = new System.Drawing.Size(0, 13);
            this.statusMsg.TabIndex = 3;
            this.statusMsg.TextAlign = System.Drawing.ContentAlignment.BottomLeft;
            // 
            // cbSearchType
            // 
            this.cbSearchType.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.ListItems;
            this.cbSearchType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cbSearchType.FormattingEnabled = true;
            this.cbSearchType.Location = new System.Drawing.Point(3, 33);
            this.cbSearchType.Name = "cbSearchType";
            this.cbSearchType.Size = new System.Drawing.Size(74, 21);
            this.cbSearchType.TabIndex = 4;
            // 
            // grpResults
            // 
            this.grpResults.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.grpResults.Controls.Add(this.tableLayoutPanelResults);
            this.grpResults.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grpResults.Location = new System.Drawing.Point(3, 144);
            this.grpResults.Name = "grpResults";
            this.grpResults.Size = new System.Drawing.Size(302, 303);
            this.grpResults.TabIndex = 3;
            this.grpResults.TabStop = false;
            this.grpResults.Text = "Results";
            // 
            // tableLayoutPanelResults
            // 
            this.tableLayoutPanelResults.AutoScroll = true;
            this.tableLayoutPanelResults.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.tableLayoutPanelResults.ColumnCount = 4;
            this.tableLayoutPanelResults.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanelResults.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 35F));
            this.tableLayoutPanelResults.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 114F));
            this.tableLayoutPanelResults.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 85F));
            this.tableLayoutPanelResults.Controls.Add(this.lstResults, 0, 0);
            this.tableLayoutPanelResults.Controls.Add(this.btnUse, 3, 1);
            this.tableLayoutPanelResults.Controls.Add(this.btnBack, 0, 1);
            this.tableLayoutPanelResults.Controls.Add(this.btnForward, 1, 1);
            this.tableLayoutPanelResults.Controls.Add(this.btnAnnotate, 2, 1);
            this.tableLayoutPanelResults.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanelResults.Location = new System.Drawing.Point(3, 16);
            this.tableLayoutPanelResults.Name = "tableLayoutPanelResults";
            this.tableLayoutPanelResults.RowCount = 2;
            this.tableLayoutPanelResults.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanelResults.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanelResults.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 20F));
            this.tableLayoutPanelResults.Size = new System.Drawing.Size(296, 284);
            this.tableLayoutPanelResults.TabIndex = 1;
            // 
            // lstResults
            // 
            this.tableLayoutPanelResults.SetColumnSpan(this.lstResults, 4);
            this.lstResults.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lstResults.FormattingEnabled = true;
            this.lstResults.HorizontalScrollbar = true;
            this.lstResults.Location = new System.Drawing.Point(3, 3);
            this.lstResults.Name = "lstResults";
            this.lstResults.Size = new System.Drawing.Size(290, 238);
            this.lstResults.TabIndex = 0;
            this.lstResults.SelectedIndexChanged += new System.EventHandler(this.updateDetails);
            // 
            // btnUse
            // 
            this.btnUse.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnUse.Enabled = false;
            this.btnUse.Location = new System.Drawing.Point(219, 257);
            this.btnUse.Name = "btnUse";
            this.btnUse.Size = new System.Drawing.Size(74, 22);
            this.btnUse.TabIndex = 1;
            this.btnUse.Text = "Use";
            this.btnUse.UseVisualStyleBackColor = true;
            this.btnUse.Click += new System.EventHandler(this.use);
            // 
            // btnBack
            // 
            this.btnBack.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnBack.Enabled = false;
            this.btnBack.Location = new System.Drawing.Point(31, 257);
            this.btnBack.MinimumSize = new System.Drawing.Size(28, 22);
            this.btnBack.Name = "btnBack";
            this.btnBack.Size = new System.Drawing.Size(28, 22);
            this.btnBack.TabIndex = 3;
            this.btnBack.Text = "<<";
            this.btnBack.UseVisualStyleBackColor = true;
            this.btnBack.Click += new System.EventHandler(this.btnBack_Click);
            // 
            // btnForward
            // 
            this.btnForward.Enabled = false;
            this.btnForward.Location = new System.Drawing.Point(65, 257);
            this.btnForward.MinimumSize = new System.Drawing.Size(28, 22);
            this.btnForward.Name = "btnForward";
            this.btnForward.Size = new System.Drawing.Size(28, 22);
            this.btnForward.TabIndex = 2;
            this.btnForward.Text = ">>";
            this.btnForward.UseVisualStyleBackColor = true;
            this.btnForward.Click += new System.EventHandler(this.btnForward_Click);
            // 
            // btnAnnotate
            // 
            this.btnAnnotate.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnAnnotate.Enabled = false;
            this.btnAnnotate.Location = new System.Drawing.Point(100, 257);
            this.btnAnnotate.Name = "btnAnnotate";
            this.btnAnnotate.Size = new System.Drawing.Size(108, 22);
            this.btnAnnotate.TabIndex = 4;
            this.btnAnnotate.Text = "Annotate with CDE";
            this.btnAnnotate.UseVisualStyleBackColor = true;
            this.btnAnnotate.Click += new System.EventHandler(this.annotate);
            // 
            // grpDetails
            // 
            this.grpDetails.Controls.Add(this.tabControlDetails);
            this.grpDetails.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grpDetails.Location = new System.Drawing.Point(3, 453);
            this.grpDetails.Name = "grpDetails";
            this.grpDetails.Size = new System.Drawing.Size(302, 303);
            this.grpDetails.TabIndex = 4;
            this.grpDetails.TabStop = false;
            this.grpDetails.Text = "Details";
            // 
            // tabControlDetails
            // 
            this.tabControlDetails.Controls.Add(this.tabPageDef);
            this.tabControlDetails.Controls.Add(this.tabPagePropsValues);
            this.tabControlDetails.Controls.Add(this.tabOcProps);
            this.tabControlDetails.Controls.Add(this.otherDetails);
            this.tabControlDetails.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControlDetails.Location = new System.Drawing.Point(3, 16);
            this.tabControlDetails.Name = "tabControlDetails";
            this.tabControlDetails.SelectedIndex = 0;
            this.tabControlDetails.Size = new System.Drawing.Size(296, 284);
            this.tabControlDetails.TabIndex = 1;
            // 
            // tabPageDef
            // 
            this.tabPageDef.Controls.Add(this.wbDetailsDef);
            this.tabPageDef.Location = new System.Drawing.Point(4, 22);
            this.tabPageDef.Name = "tabPageDef";
            this.tabPageDef.Padding = new System.Windows.Forms.Padding(3);
            this.tabPageDef.Size = new System.Drawing.Size(288, 258);
            this.tabPageDef.TabIndex = 0;
            this.tabPageDef.Text = "Definition";
            this.tabPageDef.UseVisualStyleBackColor = true;
            // 
            // wbDetailsDef
            // 
            this.wbDetailsDef.Dock = System.Windows.Forms.DockStyle.Fill;
            this.wbDetailsDef.IsWebBrowserContextMenuEnabled = false;
            this.wbDetailsDef.Location = new System.Drawing.Point(3, 3);
            this.wbDetailsDef.MinimumSize = new System.Drawing.Size(20, 20);
            this.wbDetailsDef.Name = "wbDetailsDef";
            this.wbDetailsDef.Size = new System.Drawing.Size(282, 252);
            this.wbDetailsDef.TabIndex = 0;
            // 
            // tabPagePropsValues
            // 
            this.tabPagePropsValues.Controls.Add(this.wbDetailsPropsValues);
            this.tabPagePropsValues.Location = new System.Drawing.Point(4, 22);
            this.tabPagePropsValues.Name = "tabPagePropsValues";
            this.tabPagePropsValues.Padding = new System.Windows.Forms.Padding(3);
            this.tabPagePropsValues.Size = new System.Drawing.Size(288, 273);
            this.tabPagePropsValues.TabIndex = 1;
            this.tabPagePropsValues.Text = "Props/Values";
            this.tabPagePropsValues.UseVisualStyleBackColor = true;
            // 
            // wbDetailsPropsValues
            // 
            this.wbDetailsPropsValues.Dock = System.Windows.Forms.DockStyle.Fill;
            this.wbDetailsPropsValues.Location = new System.Drawing.Point(3, 3);
            this.wbDetailsPropsValues.MinimumSize = new System.Drawing.Size(20, 20);
            this.wbDetailsPropsValues.Name = "wbDetailsPropsValues";
            this.wbDetailsPropsValues.Size = new System.Drawing.Size(282, 267);
            this.wbDetailsPropsValues.TabIndex = 0;
            // 
            // tabOcProps
            // 
            this.tabOcProps.Controls.Add(this.wbOcProps);
            this.tabOcProps.Location = new System.Drawing.Point(4, 22);
            this.tabOcProps.Name = "tabOcProps";
            this.tabOcProps.Size = new System.Drawing.Size(288, 273);
            this.tabOcProps.TabIndex = 3;
            this.tabOcProps.Text = "OC/Props";
            this.tabOcProps.UseVisualStyleBackColor = true;
            // 
            // wbOcProps
            // 
            this.wbOcProps.Dock = System.Windows.Forms.DockStyle.Fill;
            this.wbOcProps.Location = new System.Drawing.Point(0, 0);
            this.wbOcProps.MinimumSize = new System.Drawing.Size(20, 20);
            this.wbOcProps.Name = "wbOcProps";
            this.wbOcProps.Size = new System.Drawing.Size(288, 273);
            this.wbOcProps.TabIndex = 0;
            // 
            // otherDetails
            // 
            this.otherDetails.Controls.Add(this.wbDetailsOther);
            this.otherDetails.Location = new System.Drawing.Point(4, 22);
            this.otherDetails.Name = "otherDetails";
            this.otherDetails.Padding = new System.Windows.Forms.Padding(3);
            this.otherDetails.Size = new System.Drawing.Size(288, 273);
            this.otherDetails.TabIndex = 2;
            this.otherDetails.Text = "Other";
            this.otherDetails.UseVisualStyleBackColor = true;
            // 
            // wbDetailsOther
            // 
            this.wbDetailsOther.Dock = System.Windows.Forms.DockStyle.Fill;
            this.wbDetailsOther.Location = new System.Drawing.Point(3, 3);
            this.wbDetailsOther.MinimumSize = new System.Drawing.Size(20, 20);
            this.wbDetailsOther.Name = "wbDetailsOther";
            this.wbDetailsOther.Size = new System.Drawing.Size(282, 267);
            this.wbDetailsOther.TabIndex = 0;
            // 
            // tabControl
            // 
            this.tabControl.Controls.Add(this.tabFreeText);
            this.tabControl.Controls.Add(this.tabClassified);
            this.tabControl.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl.Location = new System.Drawing.Point(0, 0);
            this.tabControl.Name = "tabControl";
            this.tabControl.SelectedIndex = 0;
            this.tabControl.Size = new System.Drawing.Size(322, 821);
            this.tabControl.TabIndex = 5;
            // 
            // tabFreeText
            // 
            this.tabFreeText.Controls.Add(this.tableLayoutPanel3);
            this.tabFreeText.Location = new System.Drawing.Point(4, 22);
            this.tabFreeText.Name = "tabFreeText";
            this.tabFreeText.Padding = new System.Windows.Forms.Padding(3);
            this.tabFreeText.Size = new System.Drawing.Size(314, 795);
            this.tabFreeText.TabIndex = 0;
            this.tabFreeText.Text = "Free Text";
            this.tabFreeText.UseVisualStyleBackColor = true;
            // 
            // tableLayoutPanel3
            // 
            this.tableLayoutPanel3.AutoScroll = true;
            this.tableLayoutPanel3.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.tableLayoutPanel3.ColumnCount = 1;
            this.tableLayoutPanel3.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel3.Controls.Add(this.grpDetails, 0, 2);
            this.tableLayoutPanel3.Controls.Add(this.grpControls, 0, 0);
            this.tableLayoutPanel3.Controls.Add(this.grpResults, 0, 1);
            this.tableLayoutPanel3.Controls.Add(this.tableLayoutPanel5, 0, 3);
            this.tableLayoutPanel3.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel3.Location = new System.Drawing.Point(3, 3);
            this.tableLayoutPanel3.Name = "tableLayoutPanel3";
            this.tableLayoutPanel3.RowCount = 4;
            this.tableLayoutPanel3.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 141F));
            this.tableLayoutPanel3.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel3.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 50F));
            this.tableLayoutPanel3.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel3.Size = new System.Drawing.Size(308, 789);
            this.tableLayoutPanel3.TabIndex = 5;
            // 
            // tableLayoutPanel5
            // 
            this.tableLayoutPanel5.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.tableLayoutPanel5.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.tableLayoutPanel5.ColumnCount = 2;
            this.tableLayoutPanel5.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 84F));
            this.tableLayoutPanel5.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 16F));
            this.tableLayoutPanel5.Controls.Add(this.btnCleanURLs, 1, 0);
            this.tableLayoutPanel5.Controls.Add(this.cbServiceUrls, 0, 0);
            this.tableLayoutPanel5.Location = new System.Drawing.Point(3, 762);
            this.tableLayoutPanel5.Name = "tableLayoutPanel5";
            this.tableLayoutPanel5.RowCount = 1;
            this.tableLayoutPanel5.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel5.Size = new System.Drawing.Size(302, 24);
            this.tableLayoutPanel5.TabIndex = 6;
            // 
            // btnCleanURLs
            // 
            this.btnCleanURLs.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.btnCleanURLs.Location = new System.Drawing.Point(256, 3);
            this.btnCleanURLs.Name = "btnCleanURLs";
            this.btnCleanURLs.Size = new System.Drawing.Size(43, 18);
            this.btnCleanURLs.TabIndex = 0;
            this.btnCleanURLs.Text = "Reset";
            this.btnCleanURLs.UseVisualStyleBackColor = true;
            this.btnCleanURLs.Click += new System.EventHandler(this.btnCleanURLs_Click);
            // 
            // cbServiceUrls
            // 
            this.cbServiceUrls.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.cbServiceUrls.DropDownWidth = 356;
            this.cbServiceUrls.FormattingEnabled = true;
            this.cbServiceUrls.Location = new System.Drawing.Point(3, 3);
            this.cbServiceUrls.Name = "cbServiceUrls";
            this.cbServiceUrls.Size = new System.Drawing.Size(247, 21);
            this.cbServiceUrls.TabIndex = 5;
            // 
            // tabClassified
            // 
            this.tabClassified.Controls.Add(this.tableLayoutPanelClassification);
            this.tabClassified.Location = new System.Drawing.Point(4, 22);
            this.tabClassified.Name = "tabClassified";
            this.tabClassified.Padding = new System.Windows.Forms.Padding(3);
            this.tabClassified.Size = new System.Drawing.Size(314, 795);
            this.tabClassified.TabIndex = 1;
            this.tabClassified.Text = "Classification";
            this.tabClassified.UseVisualStyleBackColor = true;
            // 
            // tableLayoutPanelClassification
            // 
            this.tableLayoutPanelClassification.AutoScroll = true;
            this.tableLayoutPanelClassification.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.tableLayoutPanelClassification.ColumnCount = 1;
            this.tableLayoutPanelClassification.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanelClassification.Controls.Add(this.grpClassificationResults, 0, 1);
            this.tableLayoutPanelClassification.Controls.Add(this.grpClassificationScheme, 0, 0);
            this.tableLayoutPanelClassification.Controls.Add(this.grpClassificationResultDetail, 0, 2);
            this.tableLayoutPanelClassification.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanelClassification.Location = new System.Drawing.Point(3, 3);
            this.tableLayoutPanelClassification.Name = "tableLayoutPanelClassification";
            this.tableLayoutPanelClassification.RowCount = 3;
            this.tableLayoutPanelClassification.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 34F));
            this.tableLayoutPanelClassification.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 33F));
            this.tableLayoutPanelClassification.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 33F));
            this.tableLayoutPanelClassification.Size = new System.Drawing.Size(308, 789);
            this.tableLayoutPanelClassification.TabIndex = 0;
            // 
            // grpClassificationResults
            // 
            this.grpClassificationResults.Controls.Add(this.tableLayoutPanel1);
            this.grpClassificationResults.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grpClassificationResults.Location = new System.Drawing.Point(3, 271);
            this.grpClassificationResults.Name = "grpClassificationResults";
            this.grpClassificationResults.Size = new System.Drawing.Size(302, 254);
            this.grpClassificationResults.TabIndex = 3;
            this.grpClassificationResults.TabStop = false;
            this.grpClassificationResults.Text = "Results";
            // 
            // tableLayoutPanel1
            // 
            this.tableLayoutPanel1.ColumnCount = 4;
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 59F));
            this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 80F));
            this.tableLayoutPanel1.Controls.Add(this.lstClassificationQueryResult, 0, 0);
            this.tableLayoutPanel1.Controls.Add(this.btnCLSUse, 3, 1);
            this.tableLayoutPanel1.Controls.Add(this.btnForwardCLS, 2, 1);
            this.tableLayoutPanel1.Controls.Add(this.btnBackCLS, 1, 1);
            this.tableLayoutPanel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel1.Location = new System.Drawing.Point(3, 16);
            this.tableLayoutPanel1.Name = "tableLayoutPanel1";
            this.tableLayoutPanel1.RowCount = 2;
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel1.Size = new System.Drawing.Size(296, 235);
            this.tableLayoutPanel1.TabIndex = 0;
            // 
            // lstClassificationQueryResult
            // 
            this.tableLayoutPanel1.SetColumnSpan(this.lstClassificationQueryResult, 4);
            this.lstClassificationQueryResult.Dock = System.Windows.Forms.DockStyle.Fill;
            this.lstClassificationQueryResult.FormattingEnabled = true;
            this.lstClassificationQueryResult.Location = new System.Drawing.Point(3, 3);
            this.lstClassificationQueryResult.Name = "lstClassificationQueryResult";
            this.lstClassificationQueryResult.Size = new System.Drawing.Size(290, 199);
            this.lstClassificationQueryResult.TabIndex = 0;
            this.lstClassificationQueryResult.SelectedIndexChanged += new System.EventHandler(this.updateDetails);
            // 
            // btnCLSUse
            // 
            this.btnCLSUse.Enabled = false;
            this.btnCLSUse.Location = new System.Drawing.Point(219, 208);
            this.btnCLSUse.Name = "btnCLSUse";
            this.btnCLSUse.Size = new System.Drawing.Size(74, 22);
            this.btnCLSUse.TabIndex = 1;
            this.btnCLSUse.Text = "Use";
            this.btnCLSUse.UseVisualStyleBackColor = true;
            this.btnCLSUse.Click += new System.EventHandler(this.useCLS);
            // 
            // btnForwardCLS
            // 
            this.btnForwardCLS.Enabled = false;
            this.btnForwardCLS.Location = new System.Drawing.Point(160, 208);
            this.btnForwardCLS.MinimumSize = new System.Drawing.Size(28, 22);
            this.btnForwardCLS.Name = "btnForwardCLS";
            this.btnForwardCLS.Size = new System.Drawing.Size(28, 22);
            this.btnForwardCLS.TabIndex = 2;
            this.btnForwardCLS.Text = ">>";
            this.btnForwardCLS.UseVisualStyleBackColor = true;
            this.btnForwardCLS.Click += new System.EventHandler(this.btnForwardCLS_Click);
            // 
            // btnBackCLS
            // 
            this.btnBackCLS.Enabled = false;
            this.btnBackCLS.Location = new System.Drawing.Point(130, 208);
            this.btnBackCLS.MinimumSize = new System.Drawing.Size(28, 22);
            this.btnBackCLS.Name = "btnBackCLS";
            this.btnBackCLS.Size = new System.Drawing.Size(28, 22);
            this.btnBackCLS.TabIndex = 3;
            this.btnBackCLS.Text = "<<";
            this.btnBackCLS.UseVisualStyleBackColor = true;
            this.btnBackCLS.Click += new System.EventHandler(this.btnBackCLS_Click);
            // 
            // grpClassificationScheme
            // 
            this.grpClassificationScheme.Controls.Add(this.tableLayoutPanel2);
            this.grpClassificationScheme.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grpClassificationScheme.Location = new System.Drawing.Point(3, 3);
            this.grpClassificationScheme.Name = "grpClassificationScheme";
            this.grpClassificationScheme.Size = new System.Drawing.Size(302, 262);
            this.grpClassificationScheme.TabIndex = 2;
            this.grpClassificationScheme.TabStop = false;
            this.grpClassificationScheme.Text = "Classification Scheme";
            // 
            // tableLayoutPanel2
            // 
            this.tableLayoutPanel2.ColumnCount = 2;
            this.tableLayoutPanel2.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel2.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Absolute, 80F));
            this.tableLayoutPanel2.Controls.Add(this.cbClassificationSchemes, 0, 0);
            this.tableLayoutPanel2.Controls.Add(this.classificationTree, 0, 1);
            this.tableLayoutPanel2.Controls.Add(this.btnSearchCLS, 1, 2);
            this.tableLayoutPanel2.Controls.Add(this.statusMsgCLS, 0, 2);
            this.tableLayoutPanel2.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayoutPanel2.Location = new System.Drawing.Point(3, 16);
            this.tableLayoutPanel2.Name = "tableLayoutPanel2";
            this.tableLayoutPanel2.RowCount = 3;
            this.tableLayoutPanel2.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel2.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayoutPanel2.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 30F));
            this.tableLayoutPanel2.Size = new System.Drawing.Size(296, 243);
            this.tableLayoutPanel2.TabIndex = 1;
            // 
            // cbClassificationSchemes
            // 
            this.tableLayoutPanel2.SetColumnSpan(this.cbClassificationSchemes, 2);
            this.cbClassificationSchemes.Dock = System.Windows.Forms.DockStyle.Fill;
            this.cbClassificationSchemes.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.cbClassificationSchemes.FormattingEnabled = true;
            this.cbClassificationSchemes.Location = new System.Drawing.Point(3, 3);
            this.cbClassificationSchemes.Name = "cbClassificationSchemes";
            this.cbClassificationSchemes.Size = new System.Drawing.Size(290, 21);
            this.cbClassificationSchemes.TabIndex = 0;
            this.cbClassificationSchemes.SelectedIndexChanged += new System.EventHandler(this.cbClassificationSchemes_SelectedIndexChanged);
            // 
            // classificationTree
            // 
            this.tableLayoutPanel2.SetColumnSpan(this.classificationTree, 2);
            this.classificationTree.Dock = System.Windows.Forms.DockStyle.Fill;
            this.classificationTree.Location = new System.Drawing.Point(3, 33);
            this.classificationTree.Name = "classificationTree";
            this.classificationTree.Size = new System.Drawing.Size(290, 177);
            this.classificationTree.TabIndex = 0;
            this.classificationTree.NodeMouseDoubleClick += new System.Windows.Forms.TreeNodeMouseClickEventHandler(this.classificationTree_NodeMouseDoubleClick);
            // 
            // btnSearchCLS
            // 
            this.btnSearchCLS.Location = new System.Drawing.Point(219, 216);
            this.btnSearchCLS.Name = "btnSearchCLS";
            this.btnSearchCLS.Size = new System.Drawing.Size(74, 23);
            this.btnSearchCLS.TabIndex = 1;
            this.btnSearchCLS.Text = "Search";
            this.btnSearchCLS.UseVisualStyleBackColor = true;
            this.btnSearchCLS.Click += new System.EventHandler(this.btnSearchCLS_Click);
            // 
            // statusMsgCLS
            // 
            this.statusMsgCLS.AutoSize = true;
            this.statusMsgCLS.Dock = System.Windows.Forms.DockStyle.Fill;
            this.statusMsgCLS.Location = new System.Drawing.Point(3, 213);
            this.statusMsgCLS.Name = "statusMsgCLS";
            this.statusMsgCLS.Size = new System.Drawing.Size(210, 30);
            this.statusMsgCLS.TabIndex = 2;
            // 
            // grpClassificationResultDetail
            // 
            this.grpClassificationResultDetail.Controls.Add(this.tabControlCLSDetails);
            this.grpClassificationResultDetail.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grpClassificationResultDetail.Location = new System.Drawing.Point(3, 531);
            this.grpClassificationResultDetail.Name = "grpClassificationResultDetail";
            this.grpClassificationResultDetail.Size = new System.Drawing.Size(302, 255);
            this.grpClassificationResultDetail.TabIndex = 4;
            this.grpClassificationResultDetail.TabStop = false;
            this.grpClassificationResultDetail.Text = "Details";
            // 
            // tabControlCLSDetails
            // 
            this.tabControlCLSDetails.Controls.Add(this.tabPageCLSDetailsDef);
            this.tabControlCLSDetails.Controls.Add(this.tabPageCLSDetailsValueDomain);
            this.tabControlCLSDetails.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControlCLSDetails.Location = new System.Drawing.Point(3, 16);
            this.tabControlCLSDetails.Name = "tabControlCLSDetails";
            this.tabControlCLSDetails.SelectedIndex = 0;
            this.tabControlCLSDetails.Size = new System.Drawing.Size(296, 236);
            this.tabControlCLSDetails.TabIndex = 1;
            // 
            // tabPageCLSDetailsDef
            // 
            this.tabPageCLSDetailsDef.Controls.Add(this.wbClassificationQueryResultDef);
            this.tabPageCLSDetailsDef.Location = new System.Drawing.Point(4, 22);
            this.tabPageCLSDetailsDef.Name = "tabPageCLSDetailsDef";
            this.tabPageCLSDetailsDef.Padding = new System.Windows.Forms.Padding(3);
            this.tabPageCLSDetailsDef.Size = new System.Drawing.Size(288, 210);
            this.tabPageCLSDetailsDef.TabIndex = 0;
            this.tabPageCLSDetailsDef.Text = "Definition";
            this.tabPageCLSDetailsDef.UseVisualStyleBackColor = true;
            // 
            // wbClassificationQueryResultDef
            // 
            this.wbClassificationQueryResultDef.Dock = System.Windows.Forms.DockStyle.Fill;
            this.wbClassificationQueryResultDef.Location = new System.Drawing.Point(3, 3);
            this.wbClassificationQueryResultDef.Name = "wbClassificationQueryResultDef";
            this.wbClassificationQueryResultDef.Size = new System.Drawing.Size(282, 204);
            this.wbClassificationQueryResultDef.TabIndex = 0;
            // 
            // tabPageCLSDetailsValueDomain
            // 
            this.tabPageCLSDetailsValueDomain.Controls.Add(this.wbClassificationQueryResultValueDomain);
            this.tabPageCLSDetailsValueDomain.Location = new System.Drawing.Point(4, 22);
            this.tabPageCLSDetailsValueDomain.Name = "tabPageCLSDetailsValueDomain";
            this.tabPageCLSDetailsValueDomain.Padding = new System.Windows.Forms.Padding(3);
            this.tabPageCLSDetailsValueDomain.Size = new System.Drawing.Size(288, 210);
            this.tabPageCLSDetailsValueDomain.TabIndex = 1;
            this.tabPageCLSDetailsValueDomain.Text = "Values";
            this.tabPageCLSDetailsValueDomain.UseVisualStyleBackColor = true;
            // 
            // wbClassificationQueryResultValueDomain
            // 
            this.wbClassificationQueryResultValueDomain.Dock = System.Windows.Forms.DockStyle.Fill;
            this.wbClassificationQueryResultValueDomain.Location = new System.Drawing.Point(3, 3);
            this.wbClassificationQueryResultValueDomain.MinimumSize = new System.Drawing.Size(20, 20);
            this.wbClassificationQueryResultValueDomain.Name = "wbClassificationQueryResultValueDomain";
            this.wbClassificationQueryResultValueDomain.Size = new System.Drawing.Size(282, 204);
            this.wbClassificationQueryResultValueDomain.TabIndex = 0;
            // 
            // txtContext
            // 
            this.txtContext.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.txtContext.Location = new System.Drawing.Point(83, 63);
            this.txtContext.Name = "txtContext";
            this.txtContext.Size = new System.Drawing.Size(210, 20);
            this.txtContext.TabIndex = 5;
            // 
            // lblContext
            // 
            this.lblContext.AutoSize = true;
            this.lblContext.Dock = System.Windows.Forms.DockStyle.Right;
            this.lblContext.Location = new System.Drawing.Point(34, 60);
            this.lblContext.Margin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.lblContext.Name = "lblContext";
            this.lblContext.RightToLeft = System.Windows.Forms.RightToLeft.No;
            this.lblContext.Size = new System.Drawing.Size(46, 30);
            this.lblContext.TabIndex = 6;
            this.lblContext.Text = "Context:";
            this.lblContext.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
            // 
            // QueryServiceControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.Controls.Add(this.tabControl);
            this.Name = "QueryServiceControl";
            this.Size = new System.Drawing.Size(322, 821);
            this.Load += new System.EventHandler(this.QueryServiceControl_Load);
            this.grpControls.ResumeLayout(false);
            this.tableLayoutPanel4.ResumeLayout(false);
            this.tableLayoutPanel4.PerformLayout();
            this.grpResults.ResumeLayout(false);
            this.tableLayoutPanelResults.ResumeLayout(false);
            this.grpDetails.ResumeLayout(false);
            this.tabControlDetails.ResumeLayout(false);
            this.tabPageDef.ResumeLayout(false);
            this.tabPagePropsValues.ResumeLayout(false);
            this.tabOcProps.ResumeLayout(false);
            this.otherDetails.ResumeLayout(false);
            this.tabControl.ResumeLayout(false);
            this.tabFreeText.ResumeLayout(false);
            this.tableLayoutPanel3.ResumeLayout(false);
            this.tableLayoutPanel5.ResumeLayout(false);
            this.tabClassified.ResumeLayout(false);
            this.tableLayoutPanelClassification.ResumeLayout(false);
            this.grpClassificationResults.ResumeLayout(false);
            this.tableLayoutPanel1.ResumeLayout(false);
            this.grpClassificationScheme.ResumeLayout(false);
            this.tableLayoutPanel2.ResumeLayout(false);
            this.tableLayoutPanel2.PerformLayout();
            this.grpClassificationResultDetail.ResumeLayout(false);
            this.tabControlCLSDetails.ResumeLayout(false);
            this.tabPageCLSDetailsDef.ResumeLayout(false);
            this.tabPageCLSDetailsValueDomain.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TextBox txtTerm;
        private System.Windows.Forms.Button btnSearch;
        private System.Windows.Forms.GroupBox grpControls;
        private System.Windows.Forms.GroupBox grpResults;
        protected System.Windows.Forms.ListBox lstResults;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanelResults;
        private System.Windows.Forms.GroupBox grpDetails;
        private System.Windows.Forms.WebBrowser wbDetailsDef;
        private System.Windows.Forms.Button btnUse;
        private System.Windows.Forms.ComboBox cbResources;
        private System.Windows.Forms.Label statusMsg;
        private System.Windows.Forms.Button btnForward;
        private System.Windows.Forms.Button btnBack;
        private System.Windows.Forms.TabControl tabControl;
        private System.Windows.Forms.TabPage tabFreeText;
        private System.Windows.Forms.TabPage tabClassified;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanelClassification;
        private System.Windows.Forms.TreeView classificationTree;
        private System.Windows.Forms.GroupBox grpClassificationScheme;
        private System.Windows.Forms.GroupBox grpClassificationResults;
        private System.Windows.Forms.ComboBox cbClassificationSchemes;
        private System.Windows.Forms.GroupBox grpClassificationResultDetail;
        private System.Windows.Forms.WebBrowser wbClassificationQueryResultDef;
        protected System.Windows.Forms.ListBox lstClassificationQueryResult;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
        private System.Windows.Forms.Button btnCLSUse;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel2;
        private System.Windows.Forms.TabControl tabControlCLSDetails;
        private System.Windows.Forms.TabPage tabPageCLSDetailsDef;
        private System.Windows.Forms.TabPage tabPageCLSDetailsValueDomain;
        private System.Windows.Forms.WebBrowser wbClassificationQueryResultValueDomain;
        private System.Windows.Forms.TabControl tabControlDetails;
        private System.Windows.Forms.TabPage tabPageDef;
        private System.Windows.Forms.TabPage tabPagePropsValues;
        private System.Windows.Forms.WebBrowser wbDetailsPropsValues;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel4;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel3;
        private System.Windows.Forms.Button btnSearchCLS;
        private System.Windows.Forms.Label statusMsgCLS;
        private System.Windows.Forms.Button btnForwardCLS;
        private System.Windows.Forms.Button btnBackCLS;
        private System.Windows.Forms.TabPage otherDetails;
        private System.Windows.Forms.WebBrowser wbDetailsOther;
        private System.Windows.Forms.Button btnAnnotate;
        private System.Windows.Forms.ComboBox cbSearchType;
        private System.Windows.Forms.TabPage tabOcProps;
        private System.Windows.Forms.WebBrowser wbOcProps;
        private System.Windows.Forms.ComboBox cbServiceUrls;
        private System.Windows.Forms.TableLayoutPanel tableLayoutPanel5;
        private System.Windows.Forms.Button btnCleanURLs;
        private System.Windows.Forms.TextBox txtContext;
        private System.Windows.Forms.Label lblContext;
    }
}
