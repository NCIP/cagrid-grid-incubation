using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Linq;

namespace EnterpriseArchitectAddIn
{
    public partial class DataElementCreationForm : Form
    {
        public EA.Repository m_Repository { set; get; }

        public DataElementCreationForm()
        {
            InitializeComponent();

            dataElementCreationControl.parent = this;
            EASubmissionCompleteControl scControl = new EASubmissionCompleteControl();
            scControl.OnInsertTopXSDElement += new EventHandler(scControl_OnInsertTopXSDElement);
            scControl.OnInsertTopXSDAttribute += new EventHandler(scControl_OnInsertTopXSDAttribute);
            dataElementCreationControl.scControl = scControl;
            //dataElementCreationControl.OnUseResult += new EventHandler(dataElementCreationControl_OnUseResult);
        }

        void scControl_OnInsertTopXSDAttribute(object sender, EventArgs e)
        {
            if (dataElementCreationControl.LastResult == null)
            {
                MessageBox.Show("Error inserting data element");
                return;
            }

            //XmlDocument xDoc = new XmlDocument();
            //xDoc.LoadXml(dataElementCreationControl.LastResult);
            XElement x = XElement.Parse(dataElementCreationControl.LastResult.Replace("<", "<rs:").Replace("<rs:/", "</rs:").Replace("<rs:data-element>", "<rs:data-element xmlns:rs=\"http://cancergrid.org/schema/result-set\">"));
            EAUtil.insertCDE(m_Repository, x, EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ATTRIBUTE);
        }

        void scControl_OnInsertTopXSDElement(object sender, EventArgs e)
        {
            if (dataElementCreationControl.LastResult == null)
            {
                MessageBox.Show("Error inserting data element");
                return;
            }
            //XmlDocument xDoc = new XmlDocument();
            //xDoc.LoadXml(dataElementCreationControl.LastResult);
            XElement x = XElement.Parse(dataElementCreationControl.LastResult.Replace("<", "<rs:").Replace("<rs:/", "</rs:").Replace("<rs:data-element>", "<rs:data-element xmlns:rs=\"http://cancergrid.org/schema/result-set\">"));
            EAUtil.insertCDE(m_Repository, x, EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ELEMENT);
        }
        /*
        void dataElementCreationControl_OnUseResult(object sender, EventArgs e)
        {
            if (dataElementCreationControl.LastResult == null)
            {
                MessageBox.Show("Error inserting data element");
                return;
            }

            XmlDocument xDoc = new XmlDocument();
            xDoc.LoadXml(dataElementCreationControl.LastResult);
            nsmanager = new XmlNamespaceManager(xDoc.NameTable);
            nsmanager.AddNamespace("rs", "http://cancergrid.org/schema/result-set");
            insertCDE(xDoc.DocumentElement.SelectSingleNode("/data-element"), INSERT_XSD_TYPE.NOT_APPLICABLE);
        }
        */
    }
}
