﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Linq;
using System.Collections;

namespace QueryServiceControl
{
    public partial class QueryServiceControl : UserControl
    {
        private BackgroundWorker bgWorker;
        private BackgroundWorker searchWorker;
        private BackgroundWorker searchCLSWorker;

        private QueryServiceManager.MDRQueryService qsm = null;
        private List<QueryServiceManager.query_service> resources = null;
        private List<QueryServiceManager.classification_scheme> classification_schemes = null;

        protected QueryServiceManager.resultset lastResult = null;
        protected QueryServiceManager.resultset lastClassificationQueryResult = null;
        protected XmlNamespaceManager nsmanager = null;
        protected Hashtable contextHash = new Hashtable();
        protected int currentPage = 0;
        protected int pageSize = 20;

        protected int currentPageCLS = 0;
        protected int pageSizeCLS = 20;

        private String TERM_SEARCH_TYPE = "Term";
        private String ID_SEARCH_TYPE = "Id";
        private String EXACTTERM_SEARCH_TYPE = "Exact Term";

        private System.Windows.Forms.ToolTip tooltip;

        public QueryServiceControl()
        {
            InitializeComponent();
            qsm = new QueryServiceManager.MDRQueryService();

            bgWorker = new BackgroundWorker();
            bgWorker.DoWork += new DoWorkEventHandler(bgWorker_DoWork);
            bgWorker.RunWorkerCompleted += new RunWorkerCompletedEventHandler(bgWorker_RunWorkerCompleted);

            searchWorker = new BackgroundWorker();
            searchWorker.WorkerSupportsCancellation = true;
            searchWorker.DoWork += new DoWorkEventHandler(searchWorker_DoWork);
            searchWorker.RunWorkerCompleted += new RunWorkerCompletedEventHandler(searchWorker_RunWorkerCompleted);

            searchCLSWorker = new BackgroundWorker();
            searchCLSWorker.WorkerSupportsCancellation = true;
            searchCLSWorker.DoWork += new DoWorkEventHandler(searchCLSWorker_DoWork);
            searchCLSWorker.RunWorkerCompleted += new RunWorkerCompletedEventHandler(searchCLSWorker_RunWorkerCompleted);

            //OSUMC: hide classification tab. Not used.
            tabControl.Controls.Remove(tabClassified);

            // Set default search type to "Term"
            cbSearchType.Items.Add(TERM_SEARCH_TYPE);
            cbSearchType.Items.Add(EXACTTERM_SEARCH_TYPE);
            cbSearchType.Items.Add(ID_SEARCH_TYPE);
            cbSearchType.SelectedItem = TERM_SEARCH_TYPE;

            //Rakesh wants to get rid of the Use button
            btnUse.Hide();
        }

        void searchCLSWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                
            }
        }

        void searchCLSWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            searchCLS(sender, e);
        }

        void searchWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Error == null)
            {

            }
        }

        void searchWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            search(sender, e);
        }

        void PreInitResources()
        {
            this.Cursor = Cursors.WaitCursor;
            btnSearch.Enabled = false;
            btnSearchCLS.Enabled = false;
            btnContextList.Enabled = false;

            SetStatus("Please wait. Initializing...");
        }

        void PostInitResources()
        {
            this.Cursor = Cursors.Default;
            btnSearch.Enabled = true;
            btnSearchCLS.Enabled = true;
            btnContextList.Enabled = true;

            SetStatus("");
        }

        void bgWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                InitResourceDropDown();
                PostInitResources();
            }
            else
            {
                MessageBox.Show("Error loading Query Service panel: "+e.Error.Message);
            }
        }

        void bgWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            InitResources();
            //OSUMC - commented out - not supported
            //InitClassificationSchemes();
        }

        private void QueryServiceControl_Load(object sender, EventArgs e)
        {
            this.tooltip = new ToolTip();
            this.cbContextList.DrawMode = DrawMode.OwnerDrawFixed;
            this.cbContextList.DrawItem += new DrawItemEventHandler(cbContextList_DrawItem);

            //statusMsg.ForeColor = Color.Blue;
            //statusMsg.Text = "Initializing...";

            PreInitResources();

            InitServiceURL();

            bgWorker.RunWorkerAsync();
        }

        private void InitResourceDropDown()
        {
            if (resources != null)
            {
                cbResources.DataSource = resources;
                cbResources.DisplayMember = "name";
                cbResources.ValueMember = "name";
                cbResources.SelectedIndex = 0;
            }

            if (classification_schemes != null)
            {
                cbClassificationSchemes.DataSource = classification_schemes;
                cbClassificationSchemes.DisplayMember = "Value";
                cbClassificationSchemes.ValueMember = "uri";
                cbClassificationSchemes.SelectedIndex = 0;
                updateClassification_Tree();
            }
        }

        private void cbContextList_DrawItem(object sender, DrawItemEventArgs e)
        {
            String text = null;

            String resource = cbResources.SelectedValue.ToString();
            if (resource == null)
            {
                return;
            }

            List<ContextBean> ctxList = (List<ContextBean>)contextHash[resource];
            if (ctxList == null)
            {
                return;
            }

            String selectedValue = cbContextList.GetItemText(cbContextList.Items[e.Index]);
            if (selectedValue == null)
            {
                return;
            }

            foreach (ContextBean bean in ctxList)
            {
                if (bean.Name.Equals(selectedValue))
                {
                    text = bean.Tooltip;
                    break;
                }
            }

            if (text == null)
            {
                return;
            }

            e.DrawBackground();
            using (SolidBrush br = new SolidBrush(e.ForeColor))
            {
                e.Graphics.DrawString(selectedValue, e.Font, br, e.Bounds);
            }

            if ((e.State & DrawItemState.Selected) == DrawItemState.Selected)
            {
                this.tooltip.Show(text, cbContextList, e.Bounds.Right, e.Bounds.Bottom);
            }
            else
            {
                this.tooltip.Hide(cbContextList);
            }

            e.DrawFocusRectangle();
        }

        private void InitServiceURL()
        {
            System.Collections.Specialized.StringCollection userURLs = 
                global::QueryServiceControl.Properties.Settings.Default.User_QueryServiceControl_QueryServiceManager_MDRQueryService;

            String lastUsedURL = global::QueryServiceControl.Properties.Settings.Default.Last_QueryServiceControl_QueryServiceManager_MDRQueryService;

            cbServiceUrls.Items.Clear();

            int selectedIndex = 0;

            if (lastUsedURL != null && lastUsedURL.Length > 0)
            {
                cbServiceUrls.Items.Add(lastUsedURL);
            }

            String defaultURL = global::QueryServiceControl.Properties.Settings.Default.QueryServiceControl_QueryServiceManager_MDRQueryService;
            if (lastUsedURL == null || !defaultURL.Equals(lastUsedURL))
            {
                cbServiceUrls.Items.Add(defaultURL);
            }

            if (userURLs != null)
            {
                for (int i = 0; i < userURLs.Count; i++)
                {
                    if (lastUsedURL != null &&
                          userURLs[i].Equals(lastUsedURL))
                    {
                        continue;
                    }
                    cbServiceUrls.Items.Add(userURLs[i]);
                 }
            }

            cbServiceUrls.SelectedIndex = selectedIndex;
        }

        private void UpdateUserServiceURL()
        {
            String inputURL = cbServiceUrls.Text;
            String appURL = global::QueryServiceControl.Properties.Settings.Default.QueryServiceControl_QueryServiceManager_MDRQueryService;
            System.Collections.Specialized.StringCollection userURLs = global::QueryServiceControl.Properties.Settings.Default.User_QueryServiceControl_QueryServiceManager_MDRQueryService;

            bool changes = false;
            if (!inputURL.Equals(appURL, StringComparison.CurrentCultureIgnoreCase))
            {
                if (userURLs == null) 
                    userURLs = new System.Collections.Specialized.StringCollection();

                if (!userURLs.Contains(inputURL))
                {
                    userURLs.Add(inputURL);
                    global::QueryServiceControl.Properties.Settings.Default.User_QueryServiceControl_QueryServiceManager_MDRQueryService = userURLs;
                    changes = true;
                }
            }

            global::QueryServiceControl.Properties.Settings.Default.Last_QueryServiceControl_QueryServiceManager_MDRQueryService = inputURL;
            global::QueryServiceControl.Properties.Settings.Default.Save();

            if (changes)
            {
                InitServiceURL();
            }
        }

        private void InitResources()
        {
            try
            {
                String lastUsedURL = global::QueryServiceControl.Properties.Settings.Default.Last_QueryServiceControl_QueryServiceManager_MDRQueryService;
                if (lastUsedURL != null && lastUsedURL.Length > 0)
                {
                    qsm.Url = lastUsedURL;
                }

                resources = qsm.getQueryResources().ToList<QueryServiceManager.query_service>();
                //OSUMC - don't display concept (EVS) resources
                //resources.RemoveAll(NotDataElementAndNotConcept);
                resources.RemoveAll(NotDataElement);
            }
            catch (Exception e)
            {
                String errMsg = "Failed to initialize MDR query resources.\n";
                Boolean stack = true;

                if (e.Message != null && e.Message.Length > 0) {
                    errMsg += "\n[" + e.Message + "]";
                }

                if (e.InnerException != null) {
                    if (e.InnerException.Message != null && e.InnerException.Message.Length > 0) {
                        errMsg += "\n[" + e.InnerException.Message + "]"
                            + "\n\n" + e.InnerException.StackTrace;
                        stack = false;
                    }
                }

                if (stack)
                {
                    errMsg += "\n\n" + e.StackTrace;
                }

                MessageBox.Show(errMsg);
            }
        }

        private static bool NotDataElementAndNotConcept(QueryServiceManager.query_service qs)
        {
            return (qs.category != QueryServiceManager.category.CDE && qs.category != QueryServiceManager.category.CONCEPT);
        }

        private static bool NotDataElement(QueryServiceManager.query_service qs)
        {
            return (qs.category != QueryServiceManager.category.CDE);
        }

        private void InitClassificationSchemes()
        {
            try
            {
                QueryServiceManager.QueryRequestQuery req = new QueryServiceManager.QueryRequestQuery();
                req.query = new QueryServiceManager.query();
                req.query.resource = "openMDR-Classification-Schemes";
                
                QueryServiceManager.resultset r = qsm.query(req);
                if (r.Items.Length > 0)
                {
                    classification_schemes = new List<QueryServiceManager.classification_scheme>();
                    foreach (QueryServiceManager.classification_scheme cs in r.Items)
                    {
                        classification_schemes.Add(cs);
                    }
                }
            }
            catch (Exception)
            {
                MessageBox.Show("Fail to initialize classification resources");
            }
        }

        private void cbClassificationSchemes_SelectedIndexChanged(object sender, EventArgs e)
        {
            updateClassification_Tree();
        }

        private void updateClassification_Tree()
        {
            try
            {
                QueryServiceManager.QueryRequestQuery req = new QueryServiceManager.QueryRequestQuery();
                req.query = new QueryServiceManager.query();
                req.query.resource = "openMDR-Classification-Tree";
                req.query.ItemsElementName = new global::QueryServiceControl.QueryServiceManager.ItemsChoiceType[1];
                req.query.ItemsElementName[0] = global::QueryServiceControl.QueryServiceManager.ItemsChoiceType.term;
                req.query.Items = new string[1];
                req.query.Items[0] = (string)cbClassificationSchemes.SelectedValue;

                QueryServiceManager.resultset r = qsm.query(req);
                if (r.Items.Length != 1)
                {
                    MessageBox.Show("Error getting classification tree for: " + req.query.Items[0]);
                }

                QueryServiceManager.node root = (QueryServiceManager.node)r.Items[0];
                classificationTree.BeginUpdate();
                TreeNode rootNode = buildTree(root);
                classificationTree.Nodes.Clear();
                classificationTree.Nodes.Add(rootNode);
                classificationTree.EndUpdate();
                rootNode.Expand();
            }
            catch (Exception)
            {
                //MessageBox.Show("cbClassificationSchemes_SelectedIndexChanged: " + ex.Message);
            }
        }

        protected void search(object sender, EventArgs e)
        {
            try
            {
                if (txtTerm.Text == null || txtTerm.Text.Length == 0)
                {
                    SetErrorStatus("No search term.");
                    return;
                }

                setButtonsState(false);
    
                lstResults.Items.Clear();
                lstResults.Update();
                wbDetailsDef.DocumentText = "";
                wbDetailsPropsValues.DocumentText = "";
                wbOcProps.DocumentText = "";
                wbDetailsOther.DocumentText = "";

                SetStatus("Querying...Please wait...");
                this.Cursor = Cursors.WaitCursor;

                QueryServiceManager.QueryRequestQuery req = new global::QueryServiceControl.QueryServiceManager.QueryRequestQuery();
                req.query = new QueryServiceManager.query();
                req.query.resource = cbResources.SelectedValue.ToString();

                req.query.Items = new string[] { txtTerm.Text };

                if (cbSearchType.SelectedItem.Equals(TERM_SEARCH_TYPE)) {
                    req.query.ItemsElementName = new global::QueryServiceControl.QueryServiceManager.ItemsChoiceType[] {
                        global::QueryServiceControl.QueryServiceManager.ItemsChoiceType.term
                    };
                }
                else if (cbSearchType.SelectedItem.Equals(EXACTTERM_SEARCH_TYPE))
                {
                    req.query.ItemsElementName = new global::QueryServiceControl.QueryServiceManager.ItemsChoiceType[] {
                        global::QueryServiceControl.QueryServiceManager.ItemsChoiceType.exactTerm
                    };
                }
                else
                {
                    req.query.ItemsElementName = new global::QueryServiceControl.QueryServiceManager.ItemsChoiceType[] {
                        global::QueryServiceControl.QueryServiceManager.ItemsChoiceType.id
                    };
                }

                if (cbContextList.Text.Length > 0 && !cbContextList.Text.Equals("All"))
                {
                    req.query.queryFilter = new global::QueryServiceControl.QueryServiceManager.queryFilter();
                    req.query.queryFilter.context = cbContextList.Text;
                }

                if (currentPage == 0)
                {
                    req.query.startIndex = currentPage;
                }
                else
                {
                    req.query.startIndex = currentPage * pageSize;
                }
                req.query.numResults = pageSize + 1;

                qsm.Url = cbServiceUrls.Text;

                lastResult = qsm.query(req);
                if (lastResult == null || lastResult.Items == null || lastResult.Items.Length <= 0)
                {
                    SetStatus("No result");
                    this.Cursor = Cursors.Default;
                    return;
                }

                listResults(lastResult.Items, lstResults);
                if (lastResult.Items.Length >= pageSize)
                {
                    btnForward.Enabled = true;
                }

                btnUse.Enabled = true;
                btnAnnotate.Enabled = true;

                if (currentPage > 0)
                {
                    btnBack.Enabled = true;
                }

                SetStatus("Query completed");
            }
            catch (Exception ex)
            {
                MessageBox.Show("Search failed: " + ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                SetErrorStatus("Query failed");
            }
            this.Cursor = Cursors.Default;

            // Update service URLs combo box to remember any new
            // URL specified by the user
            UpdateUserServiceURL();
        }

        private void listResults(object[] results, ListBox target)
        {
            target.Items.Clear();
            
            foreach (QueryServiceManager.commoninfo de in results)
            {
                string id = de.names.id;
                string name = de.names.preferred;

                string suffix = "";
                if (de.registrationstatus != null)
                {
                    suffix = "  (" + de.registrationstatus;

                    if (de.workflowstatus != null)
                        suffix += ":" + de.workflowstatus;

                    suffix += ")";
                }

                target.Items.Add(new QueryListItem(id, name + suffix));
            }
            if (target.Items.Count == pageSize + 1)
            {
                target.Items.RemoveAt(pageSize);
            }
            target.DisplayMember = "NAME";
            target.ValueMember = "ID";    
        }

        protected QueryListItem getSelectedItem(ListBox lb)
        {
            return (QueryListItem)lb.SelectedItem;
        }

        protected virtual void use(object sender, EventArgs e)
        {
            throw new NotImplementedException("This function has not been implmented.");
        }

        protected virtual void useCLS(object sender, EventArgs e)
        {
            throw new NotImplementedException("This function has not been implmented.");
        }

        private void txtTerm_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == (char)13)
            {
                search(sender, e);
            }
        }

        private void btnForward_Click(object sender, EventArgs e)
        {
            currentPage++;
            //if (searchWorker.IsBusy)
            //{
            //    searchWorker.CancelAsync();
            //}
            //searchWorker.RunWorkerAsync();
            search(sender, e);
        }

        private void btnSearch_Click(object sender, EventArgs e)
        {
            currentPage = 0;
            //if (searchWorker.IsBusy)
            //{
            //    searchWorker.CancelAsync();
            //}
            //searchWorker.RunWorkerAsync();
            search(sender, e);
        }

        private void btnBack_Click(object sender, EventArgs e)
        {
            currentPage--;
            //if (searchWorker.IsBusy)
            //{
            //    searchWorker.CancelAsync();
            //}
            //searchWorker.RunWorkerAsync();
            search(sender, e);
        }

        private TreeNode buildTree(QueryServiceManager.node root)
        {
            TreeNode newNode = new TreeNode();
            newNode.Name = root.label;
            newNode.Text = root.label;
            newNode.Tag = root.prefix+"#"+root.id;

            if (root.node1 == null || root.node1.Length == 0)
            {
                return newNode;
            }

            foreach (QueryServiceManager.node n in root.node1)
            {
                newNode.Nodes.Add(buildTree(n));
            }

            return newNode;
        }

        private void searchCLS(object sender, EventArgs e)
        {
            try
            {
                if (classificationTree.SelectedNode == null)
                {
                    SetErrorStatus("No node selected");
                    return;
                }
                TreeNode selectedNode = classificationTree.SelectedNode;
                QueryServiceManager.query query = new QueryServiceManager.query();
                query.resource = "openMDR-with-Classification";
                query.Items = new string[] { "*" };
                query.ItemsElementName = new global::QueryServiceControl.QueryServiceManager.ItemsChoiceType[]{
                    global::QueryServiceControl.QueryServiceManager.ItemsChoiceType.term };
                query.src = selectedNode.Tag.ToString();
                if (currentPageCLS == 0)
                {
                    query.startIndex = currentPageCLS;
                }
                else
                {
                    query.startIndex = currentPageCLS * pageSizeCLS;
                }
                query.numResults = pageSizeCLS + 1;
                
                lstClassificationQueryResult.Items.Clear();
                wbClassificationQueryResultDef.DocumentText = "";
                wbClassificationQueryResultValueDomain.DocumentText = "";

                SetStatus("Querying...");
                this.Cursor = Cursors.WaitCursor;
                btnCLSUse.Enabled = false;

                QueryServiceManager.QueryRequestQuery req = new global::QueryServiceControl.QueryServiceManager.QueryRequestQuery();
                req.query = query;
                lastClassificationQueryResult = qsm.query(req);
                
                if (lastClassificationQueryResult.Items.Length <= 0)
                {
                    SetStatus("No result");
                    this.Cursor = Cursors.Default;
                    return;
                }

                listResults(lastClassificationQueryResult.Items, lstClassificationQueryResult);
                if (lastClassificationQueryResult.Items.Length > 0)
                {
                    btnCLSUse.Enabled = true;
                }

                if (currentPageCLS > 0)
                {
                    btnBackCLS.Enabled = true;
                }

                if (lastClassificationQueryResult.Items.Length >= pageSizeCLS)
                {
                    btnForwardCLS.Enabled = true;
                }

                SetStatus("Query complete");
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
                SetErrorStatus("Query failed");
            }
            this.Cursor = Cursors.Default;
        }

        private void classificationTree_NodeMouseDoubleClick(object sender, TreeNodeMouseClickEventArgs e)
        {
            searchCLS(sender, e);
        }

        public static QueryServiceManager.commoninfo getCommonInfo(string id, object[] items)
        {
            foreach (QueryServiceManager.commoninfo ci in items)
            {
                if (ci.names.id.Equals(id))
                    return ci;
            }

            return null;
        }

        public static string getMeaning(QueryServiceManager.meaning objmeaning)
        {
            string meaning = "";
            if (objmeaning.conceptRef != null && objmeaning.conceptRef.Length > 0)
                meaning += objmeaning.conceptRef + " ";

            meaning += objmeaning.Value;
            return meaning.Trim();
        }

        public static string getDefinition(QueryServiceManager.definition objdef)
        {
            string definition;

            if (objdef == null || objdef.value == null || objdef.value.Length == 0)
            {
                definition = "(No definition supplied)";
            }
            else
            {
                //Handle special caDSR format
                definition = objdef.value;
                if (objdef.source != null)
                {
                    definition += " (Source: " +
                        objdef.source.abbreviation + ", " +
                        objdef.source.code + ", " +
                        objdef.source.description + ")";
                }
            }

            return definition;
        }

        private String BuildContextStr( global::QueryServiceControl.QueryServiceManager.context context )
        {
            if (context == null) {
                return null;
            }

            return context.name 
                + " ("
                + context.description 
                + ", Version "
                + context.version
                + ")";
        }

        private void updateDetails(object sender, EventArgs e)
        {
            try
            {
                string definition = null;
                string values = "";
                string other = "";
                string workflow = null;
                string context = null;

                QueryServiceManager.commoninfo ci = null;

                if (sender.Equals(lstClassificationQueryResult))
                {
                    other = "ID: " + getSelectedItem(lstClassificationQueryResult).ID;
                    ci = getCommonInfo(getSelectedItem(lstClassificationQueryResult).ID, lastClassificationQueryResult.Items);
                }
                else if (sender.Equals(lstResults))
                {
                    other = "ID: " + getSelectedItem(lstResults).ID;
                    ci = getCommonInfo(getSelectedItem(lstResults).ID, lastResult.Items);
                }
                else
                {
                    return;
                }
                       
                if (ci.definition == null || ci.definition.value == null || ci.definition.value.Length == 0)
                {
                    definition = "<div style=\"font-size: 14px; text-aligh: justify;\">(No definition supplied)</div>";
                }
                else
                {
                    definition = "";
                    if (ci.definition.source != null)
                    {
                        if (ci.definition.source.abbreviation != null && 
                            ci.definition.source.abbreviation.Length > 0)
                            definition += ci.definition.source.abbreviation + ", ";

                        if (ci.definition.source.code != null && 
                            ci.definition.source.code.Length > 0)
                            definition += ci.definition.source.code + ", ";

                        if (ci.definition.source.description != null && 
                            ci.definition.source.description.Length > 0)
                            definition += ci.definition.source.description + ", ";
                    }

                    definition += ci.definition.value;
                    definition = "<div style=\"font-size: 14px; text-aligh: justify;\">" + definition + "</div>";
                }

                QueryServiceManager.dataelement dataelement = ci as QueryServiceManager.dataelement;
                QueryServiceManager.objectclass objectclass = ci as QueryServiceManager.objectclass;
                QueryServiceManager.property property = ci as QueryServiceManager.property;
                QueryServiceManager.concept concept = ci as QueryServiceManager.concept;

                if (dataelement != null)
                {
                    context = BuildContextStr(dataelement.context);
                   
                    QueryServiceManager.enumerated en = dataelement.values.Item as QueryServiceManager.enumerated;
                    QueryServiceManager.nonenumerated nen = dataelement.values.Item as QueryServiceManager.nonenumerated;

                    if (en != null)
                    {
                        bool hasConceptCollections = false;
                        foreach (QueryServiceManager.validvalue vv in en.validvalue)
                        {
                            if (vv.conceptCollection != null)
                            {
                                hasConceptCollections = true;
                                string conceptConcat = "";
                                foreach (QueryServiceManager.conceptRef con in vv.conceptCollection)
                                {
                                    conceptConcat += ":" + con.name;
                                }
                                conceptConcat = conceptConcat.Substring(1);
                                values += "<tr><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + vv.code + "</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + vv.meaning.Value + "</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + conceptConcat + "</td></tr>";
                            }
                            else
                            {
                                values += "<tr><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + vv.code + "</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + vv.meaning.Value + "</td></tr>";
                            }
                        }

                        if (hasConceptCollections)
                        {
                            values = "<table style=\"width: 100%;border: 1px solid #ddd;border-collapse: collapse;\"><tr><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Code</th><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Meaning</th><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Concept</th></tr>"
                                + values;
                        }
                        else
                        {
                            values = "<table style=\"width: 100%;border: 1px solid #ddd;border-collapse: collapse;\"><tr><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Code</th><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Meaning</th></tr>"
                                + values;
                        }
                        values += "</table>";
                    }
                    else if (nen != null)
                    {
                        values = "<table style=\"width: 100%;border: 1px solid #ddd;border-collapse: collapse;\">";
                        values += "<tr><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">data-type</th><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + nen.datatype + "</td></tr>";
                        values += "<tr><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">units</th><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + nen.units + "</td></tr>";
                        values += "</table>";
                    }

                    if (sender.Equals(lstClassificationQueryResult))
                    {
                        wbClassificationQueryResultValueDomain.DocumentText = values;
                    }
                    else if (sender.Equals(lstResults))
                    {
                        wbDetailsPropsValues.DocumentText = values;
                    }

                    // Take care of object classes/properties
                    if ((dataelement.objectclass != null && dataelement.objectclass.Length > 0) || 
                        (dataelement.property != null && dataelement.property.Length > 0))
                    {
                        String tableStr = "<table style=\"width: 100%;border: 1px solid #ddd;border-collapse: collapse;\"><tr><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Type</th><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Name</th><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Concept(s)</th></tr>";
                  
                        foreach (QueryServiceManager.objectclass oc in dataelement.objectclass)
                        {
                            String concepts = "";
                            for(int i=0; i < oc.conceptCollection.Length;i++)
                            {
                                if (i > 0) concepts += ", ";
                                concepts += oc.conceptCollection[i].name +
                                    " (" + oc.conceptCollection[i].id + ")";
                            }
                            tableStr += "<tr><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">Object Class</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + oc.names.preferred + "</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + concepts + "</td></tr>";
                        }

                        foreach (QueryServiceManager.property prop in dataelement.property)
                        {
                            String concepts = "";
                            for (int i = 0; i < prop.conceptCollection.Length; i++)
                            {
                                if (i > 0) concepts += ", ";
                                concepts += prop.conceptCollection[i].name +
                                    " (" + prop.conceptCollection[i].id + ")";
                            }
                            tableStr += "<tr><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">Property</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + prop.names.preferred + "</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" + concepts + "</td></tr>";
                        }
                        tableStr += "</table>";
                        wbOcProps.DocumentText = tableStr;
                    }
                }
                else if (objectclass != null || property != null)
                {
                    QueryServiceManager.conceptRef[] concepts = null;
                    if (objectclass != null)
                    {
                        context = BuildContextStr(objectclass.context);
                        concepts = objectclass.conceptCollection;
                    }
                    else
                    {
                        context = BuildContextStr(property.context);
                        concepts = property.conceptCollection;
                    }

                    if (concepts != null && concepts.Length > 0)
                    {
                        values = "<table style=\"width: 100%;border: 1px solid #ddd;border-collapse: collapse;\"><tr><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Position</th><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Concept</th></tr>";

                        for (int i = 0; i < concepts.Length; i++)
                        {
                            values += "<tr><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">";
                            values += (i == 0) ? "Primary" : "Qualifier";
                            values += "</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">";
                            values += concepts[i].name + "</td></tr>";
                        }

                        values += "</table>";
                        wbDetailsPropsValues.DocumentText = values;
                    }
                }
                else if (concept != null && concept.properties != null && concept.properties.Length > 0)
                {
                    if (sender.Equals(lstResults))
                    {
                        values = "<table style=\"border: 1px solid #ddd;border-collapse: collapse;\"><tr><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Name</th><th style=\"background-color: #ddd;color: #000;text-align: left;padding: 5px;\">Value</th></tr>";
                        foreach(QueryServiceManager.propertiesProperty p in concept.properties)
                        {
                            values += "<tr><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" 
                                + p.name + "</td><td style=\"border: 1px solid #ddd;padding: 5px;vertical-align: top;\">" 
                                + p.value + "</td></tr>";
                        }
                        values += "</table>";
                               
                        wbDetailsPropsValues.DocumentText = values;
                    }
                }

                string otherHTML = "<div style=\"font-size: 14px; text-aligh: justify;\">";
                otherHTML += other;

                if (ci.workflowstatus == null || ci.workflowstatus.Length == 0)
                {
                    workflow = "Workflow Status: None supplied";
                }
                else
                {
                    workflow = "Workflow Status: " + ci.workflowstatus;
                }
                otherHTML += "<br><br>" + workflow;

                if (context != null)
                {
                    otherHTML += "<br><br>Context: " + context;
                }
                
                otherHTML += "</div>";

                if (sender.Equals(lstClassificationQueryResult))
                {
                    wbClassificationQueryResultDef.DocumentText = definition;
                }
                else if (sender.Equals(lstResults))
                {
                    wbDetailsDef.DocumentText = definition;
                    wbDetailsOther.DocumentText = otherHTML;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void btnForwardCLS_Click(object sender, EventArgs e)
        {
            currentPageCLS++;
            //if (searchCLSWorker.IsBusy)
            //{
            //    searchCLSWorker.CancelAsync();
            //}
            //searchCLSWorker.RunWorkerAsync();
            searchCLS(sender, e);
        }

        private void btnSearchCLS_Click(object sender, EventArgs e)
        {
            currentPageCLS = 0;
            //if (searchCLSWorker.IsBusy)
            //{
            //    searchCLSWorker.CancelAsync();
            //}
            //searchCLSWorker.RunWorkerAsync();
            searchCLS(sender, e);
        }

        private void btnBackCLS_Click(object sender, EventArgs e)
        {
            currentPageCLS--;
            if (searchCLSWorker.IsBusy)
            {
                searchCLSWorker.CancelAsync();
            }
            searchCLSWorker.RunWorkerAsync();
            //searchCLS(sender, e);
        }

        delegate void SetStatusCallback(string text);
        delegate void SetErrorStatusCallback(string text);

        private void SetStatus(string text)
        {
            if (this.statusMsg.InvokeRequired)
            {
                SetStatusCallback callback = new SetStatusCallback(SetStatus);
                this.Invoke(callback, new object[] { text });
            }
            else
            {
                statusMsg.ForeColor = Color.Blue;
                statusMsg.Text = text;
                statusMsg.Update();
            }
        }

        private void SetErrorStatus(string text)
        {
            if (this.statusMsg.InvokeRequired)
            {
                SetErrorStatusCallback callback = new SetErrorStatusCallback(SetErrorStatus);
                this.Invoke(callback, new object[] { text });
            }
            else
            {
                statusMsg.ForeColor = Color.Red;
                statusMsg.Text = text;
                statusMsg.Update();
            }
        }

        private void grpDetails_Enter(object sender, EventArgs e)
        {

        }

        protected virtual void annotate(object sender, EventArgs e)
        {
            throw new NotImplementedException("This function has not been implmented.");
        }

        private void txtTerm_TextChanged(object sender, EventArgs e)
        {

        }

        private void btnCleanURLs_Click(object sender, EventArgs e)
        {
            global::QueryServiceControl.Properties.Settings.Default.User_QueryServiceControl_QueryServiceManager_MDRQueryService
                = new System.Collections.Specialized.StringCollection();
            global::QueryServiceControl.Properties.Settings.Default.Save();
            InitServiceURL();
        }

        private void setButtonsState(Boolean state)
        {
            btnUse.Enabled = state;
            btnAnnotate.Enabled = state;
            btnBack.Enabled = state;
            btnForward.Enabled = state;
        }

        private List<ContextBean> queryContextList()
        {
            List<ContextBean> ctxList = new List<ContextBean>();

            try
            {
                SetStatus("Querying...Please wait...");
                this.Cursor = Cursors.WaitCursor;

                QueryServiceManager.QueryRequestQuery req = new global::QueryServiceControl.QueryServiceManager.QueryRequestQuery();
                req.query = new QueryServiceManager.query();
                req.query.resource = cbResources.SelectedValue.ToString();

                req.query.Items = new string[] { req.query.resource };

                req.query.ItemsElementName = new global::QueryServiceControl.QueryServiceManager.ItemsChoiceType[] {
                        global::QueryServiceControl.QueryServiceManager.ItemsChoiceType.contextList
                    };

                qsm.Url = cbServiceUrls.Text;

                QueryServiceManager.resultset result = qsm.query(req);
                if (result == null || result.Items == null || result.Items.Length <= 0)
                {
                    SetStatus("No result");
                    this.Cursor = Cursors.Default;
                    return ctxList;
                }

                // add default empty context
                ctxList.Add(new ContextBean("All", "", ""));

                foreach (QueryServiceManager.context ctx in result.Items)
                {
                    ctxList.Add(new ContextBean(ctx));
                }

                ctxList.Sort(new ContextBeanComparer());
            }
            catch (Exception ex)
            {
                MessageBox.Show("queryContextList failed: " + ex.ToString(), "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                SetErrorStatus("Query failed");
            }

            // Update service URLs combo box to remember any new
            // URL specified by the user
            UpdateUserServiceURL();

            SetStatus("Query completed");

            this.Cursor = Cursors.Default;

            return ctxList;
        }

        private void btnContextList_Click(object sender, EventArgs e)
        {
            String resource = cbResources.SelectedValue.ToString();
            List<ContextBean> ctxList = (List<ContextBean>)contextHash[resource];
            if (ctxList == null)
            {
                ctxList = queryContextList();
                contextHash.Add(resource, ctxList);
            }

            cbContextList.DataSource = ctxList;
            cbContextList.ValueMember = "Name";
            cbContextList.DisplayMember = "Name";  
        }

        private void cbContextList_DropDownClosed(object sender, EventArgs e)
        {
            this.tooltip.Hide(cbContextList);
        }

        private void cbResources_SelectedIndexChanged(object sender, EventArgs e)
        {
            String resource = cbResources.SelectedValue.ToString();
            List<ContextBean> ctxList = (List<ContextBean>)contextHash[resource];
            if (ctxList == null)
            {
                ctxList = new List<ContextBean>();
            }

            cbContextList.Text = "";
            cbContextList.DataSource = ctxList;
            cbContextList.ValueMember = "Name";
            cbContextList.DisplayMember = "Name";

        }

        private void btnGo_Click(object sender, EventArgs e)
        {
            PreInitResources();

            try
            {
                UpdateUserServiceURL();
                InitResources();
                InitResourceDropDown();
            }
            catch (Exception)
            {
            }

            PostInitResources();
        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

    }

    public class QueryListItem
    {
        public string ID { get; set; }
        public string NAME { get; set; }

        public QueryListItem(string id, string name)
        {
            this.ID = id;
            this.NAME = name;
        }
    }

}
