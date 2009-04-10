using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Linq;

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
        protected int currentPage = 0;
        protected int pageSize = 20;

        protected int currentPageCLS = 0;
        protected int pageSizeCLS = 20;

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

        void bgWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Error == null)
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

                btnSearch.Enabled = true;
                btnSearchCLS.Enabled = true;

                //statusMsg.Text = "";
                SetStatus("");
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
            //statusMsg.ForeColor = Color.Blue;
            //statusMsg.Text = "Initializing...";
            SetStatus("Initializing...");

            btnSearch.Enabled = false;
            btnSearchCLS.Enabled = false;
            bgWorker.RunWorkerAsync();
        }

        private void InitResources()
        {
            try
            {
                resources = qsm.getQueryResources().ToList<QueryServiceManager.query_service>();
                //OSUMC - don't display concept (EVS) resources
                //resources.RemoveAll(NotDataElementAndNotConcept);
                resources.RemoveAll(NotDataElement);
            }
            catch (Exception)
            {
                MessageBox.Show("Fail to initialize query resources.");
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
                req.query.resource = "cgMDR-Classification-Schemes";
                
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
                req.query.resource = "cgMDR-Classification-Tree";
                req.query.ItemElementName = global::QueryServiceControl.QueryServiceManager.ItemChoiceType.term;
                req.query.Item = (string)cbClassificationSchemes.SelectedValue;

                QueryServiceManager.resultset r = qsm.query(req);
                if (r.Items.Length != 1)
                {
                    MessageBox.Show("Error getting classification tree for: " + req.query.Item);
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

                btnUse.Enabled = false;
                btnAnnotate.Enabled = false;
                btnBack.Enabled = false;
                btnForward.Enabled = false;
                lstResults.Items.Clear();
                lstResults.Update();
                wbDetailsDef.DocumentText = "";
                wbDetailsPropsValues.DocumentText = "";

                SetStatus("Querying...");
                this.Cursor = Cursors.WaitCursor;

                QueryServiceManager.QueryRequestQuery req = new global::QueryServiceControl.QueryServiceManager.QueryRequestQuery();
                req.query = new QueryServiceManager.query();
                req.query.resource = cbResources.SelectedValue.ToString();

                req.query.Item = txtTerm.Text;
                req.query.ItemElementName = global::QueryServiceControl.QueryServiceManager.ItemChoiceType.term;
                if (currentPage == 0)
                {
                    req.query.startIndex = currentPage;
                }
                else
                {
                    req.query.startIndex = currentPage * pageSize;
                }
                req.query.numResults = pageSize + 1;
                
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
                query.resource = "cgMDR-with-Classification";
                query.Item = "*";
                query.ItemElementName = global::QueryServiceControl.QueryServiceManager.ItemChoiceType.term;
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

        private void updateDetails(object sender, EventArgs e)
        {
            try
            {
                string definition = null;
                string values = "";
                string other = "";
                string workflow = null;

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
                otherHTML += "<br><br>" + workflow + "</div>";

                if (sender.Equals(lstClassificationQueryResult))
                {
                    wbClassificationQueryResultDef.DocumentText = definition;
                }
                else if (sender.Equals(lstResults))
                {
                    wbDetailsDef.DocumentText = definition;
                    wbDetailsOther.DocumentText = otherHTML;
                }

                QueryServiceManager.dataelement dataelement = ci as QueryServiceManager.dataelement;
                QueryServiceManager.objectclass objectclass = ci as QueryServiceManager.objectclass;
                QueryServiceManager.property property = ci as QueryServiceManager.property;
                QueryServiceManager.concept concept = ci as QueryServiceManager.concept;

                if (dataelement != null)
                {
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
                }
                else if (objectclass != null || property != null)
                {
                    QueryServiceManager.conceptRef[] concepts = null;
                    if (objectclass != null)
                        concepts = objectclass.conceptCollection;
                    else
                        concepts = property.conceptCollection;

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
