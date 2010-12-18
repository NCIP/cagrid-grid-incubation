using System;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Linq;

namespace EnterpriseArchitectAddIn
{
    public partial class EAQueryServiceControl : QueryServiceControl.QueryServiceControl
    {
        public EA.Repository m_Repository { set; get; }
        public bool m_IncludeElements { set; get; }
        QueryServiceControl.QueryServiceManager.dataelement selectedNode = null;

        public EAQueryServiceControl()
        {
            InitializeComponent();
        }

        protected override void annotate(object sender, EventArgs e)
        {
            string selectedId = null;
            string selectedName = null;
            try
            {
                selectedId = getSelectedItem(lstResults).ID;
                selectedName = getSelectedItem(lstResults).NAME;
            }
            catch (Exception)
            {
                MessageBox.Show("No element selected");
                return;
            }

            //MessageBox.Show("OSUMC (annotate): selectedId[" + selectedId + "] selectedName[" + selectedName + "]");

            QueryServiceControl.QueryServiceManager.commoninfo ci = 
                QueryServiceControl.QueryServiceControl.getCommonInfo(selectedId, lastResult.Items);

            QueryServiceControl.QueryServiceManager.dataelement de =
                (ci as QueryServiceControl.QueryServiceManager.dataelement);

            if (de == null)
            {
                MessageBox.Show("This functionality applies to CDEs only. Use the 'Use' button to insert concept references",
                    "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
                //selectedNode = srcNode.SelectSingleNode("/rs:result-set/rs:concept[rs:names/rs:id=\"" + selectedId + "\"]", nsmanager);
                //if (selectedNode != null)
                //{
                //    EAUtil.insertConceptRef(m_Repository, XElement.Parse(selectedNode.OuterXml));
                //}
                //else
                //{
                //    MessageBox.Show("Error getting selected data element");
                //}
                //return;
            }

            //MessageBox.Show(selectedNode.OuterXml);
            EAUtil.annotateWithCDE(m_Repository, de);
        }

        protected override void use(object sender, EventArgs e)
        {
            QueryServiceControl.QueryServiceManager.resultset results = null;
            string selectedId = null;
            string selectedName = null;
            try
            {
                if (sender is Button)
                {
                    if (((Button)sender).Name == "btnCLSUse")
                    {
                        results = lastClassificationQueryResult;
                        selectedId = getSelectedItem(lstClassificationQueryResult).ID;
                        selectedName = getSelectedItem(lstClassificationQueryResult).NAME;
                    }
                    else if (((Button)sender).Name == "btnUse")
                    {
                        results = lastResult;
                        selectedId = getSelectedItem(lstResults).ID;
                        selectedName = getSelectedItem(lstResults).NAME;
                    }
                    else
                    {
                        return;
                    }
                }
            }
            catch (Exception)
            {
                MessageBox.Show("No element selected");
                return;
            }

            QueryServiceControl.QueryServiceManager.commoninfo ci =
                QueryServiceControl.QueryServiceControl.getCommonInfo(selectedId, results.Items);

            if (ci is QueryServiceControl.QueryServiceManager.concept)
            {
                EAUtil.insertConceptRef(m_Repository, ci as QueryServiceControl.QueryServiceManager.concept);
            }
            else if (ci is QueryServiceControl.QueryServiceManager.dataelement)
            {
                selectedNode = ci as QueryServiceControl.QueryServiceManager.dataelement;

                EA.Diagram diagram = m_Repository.GetCurrentDiagram();
                if (diagram == null)
                {
                    MessageBox.Show("Please open a diagram first", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                EA.Package package = m_Repository.GetPackageByID(diagram.PackageID);
                if (package.StereotypeEx == "XSDschema")
                {
                    EASubmissionCompleteControl scControl = new EASubmissionCompleteControl();
                    scControl.ID = selectedId;
                    scControl.PREFERRED = selectedName;
                    scControl.OnInsertTopXSDElement += new EventHandler(scControl_OnInsertTopXSDElement);
                    scControl.OnInsertTopXSDAttribute += new EventHandler(scControl_OnInsertTopXSDAttribute);
                    scControl.Show();
                }
                else
                {
                    EAUtil.insertCDE(m_Repository, 
                        selectedNode, 
                        EAUtil.INSERT_XSD_TYPE.NOT_APPLICABLE);
                }
            }
            else
            {
                MessageBox.Show("Error getting selected data element");
            }
        }

        protected override void useCLS(object sender, EventArgs e)
        {
            use(sender, e);
        }

        private EA.Element getSelectedElement()
        {
            try
            {
                object item;
                EA.ObjectType t = m_Repository.GetTreeSelectedItem(out item);
                EA.Diagram r = (EA.Diagram)item;
                if (r.SelectedObjects.Count != 1)
                {
                    MessageBox.Show("Please select single element");
                    return null;
                }
                EA.DiagramObject dObj = (EA.DiagramObject)r.SelectedObjects.GetAt(0);
                EA.Element el = m_Repository.GetElementByID(dObj.ElementID);
                return el;
            }
            catch (Exception)
            {
                MessageBox.Show("No valid selection");
                return null;
            }
        }

        void scControl_OnInsertTopXSDAttribute(object sender, EventArgs e)
        {
            if (selectedNode == null)
            {
                MessageBox.Show("Error inserting data element");
                return;
            }
            EAUtil.insertCDE(m_Repository, selectedNode, EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ATTRIBUTE);
        }

        void scControl_OnInsertTopXSDElement(object sender, EventArgs e)
        {
            if (selectedNode == null)
            {
                MessageBox.Show("Error inserting data element");
                return;
            }
            EAUtil.insertCDE(m_Repository, selectedNode, EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ELEMENT);
        }
    }
}
