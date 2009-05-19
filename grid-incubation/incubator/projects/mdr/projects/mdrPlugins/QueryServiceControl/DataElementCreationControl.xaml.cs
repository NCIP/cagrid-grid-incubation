using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using WinForm=System.Windows.Forms;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Xml.Linq;
using System.Collections.ObjectModel;
using System.Xml;
using System.ComponentModel;

using System.Net;
using System.Security.Cryptography.X509Certificates;
using System.Net.Security;


namespace QueryServiceControl
{
    public partial class DataElementCreationControl : StackPanel//UserControl
    {
        public SubmissionCompleteControl scControl { set; get; }

        private BackgroundWorker bgWorker;

        public WinForm.Form parent { get; set; }
        public event EventHandler OnUseResult;
        private string lastResult = null;

        private XNamespace der_xs = "http://cancergrid.org/schema/DataElementReduced";

        public ObservableCollection<Contact> orgContacts { get; set; }
        public ObservableCollection<Contact> regAuths { get; set; }
        public ObservableCollection<DERItem> dataTypes { get; set; }
        public ObservableCollection<DERItem> UOMs { get; set; }
        public ObservableCollection<string> altNames { get; set; }
        public ObservableCollection<ValidValue> validValues { get; set; }

        public bool isEnumerated = false;

        public DataElementReduced.DataElementReducedPortTypeClient derClient {get; set;}
        public DataElementReduced.DataElementReducedPortTypeClient submitClient {get; set;}

        public DataElementCreationControl()
        {
            InitializeComponent();

            scrollView.MinHeight = System.Windows.SystemParameters.PrimaryScreenHeight*0.8;
            scrollView.Height = System.Windows.SystemParameters.PrimaryScreenHeight*0.8;
            
            //Remove this for better security
            ServicePointManager.ServerCertificateValidationCallback = delegate(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
            {
                //ignore invalid SSL: allow this client to communicate with unauthenticated servers
                return true;
            };

            this.Loaded += new RoutedEventHandler(DataElementCreationControl_Loaded);

            derClient = new DataElementReduced.DataElementReducedPortTypeClient("DataElementReducedSOAP12port_https");
            derClient.ClientCredentials.UserName.UserName = "guest";
            derClient.ClientCredentials.UserName.Password = "guest";

            altNames = new ObservableCollection<string>();
            otherNames.ItemsSource = altNames;

            validValues = new ObservableCollection<ValidValue>();
            validValueList.ItemsSource = validValues;

        }

        void DataElementCreationControl_Loaded(object sender, RoutedEventArgs e)
        {
            loadingPane.Visibility = Visibility.Visible;
            formContent.IsEnabled = false;

            bgWorker = ((BackgroundWorker)FindResource("bgWorker"));
            bgWorker.RunWorkerAsync();
        }

        public string LastResult
        {
            get
            {
                return lastResult;
            }
        }

        private void LoadDefaultContacts()
        {
            try
            {
                string response = derClient.getOrganizationContacts();
                XDocument xDoc = XDocument.Parse(response);
                var contactList = from contact in xDoc.Root.Elements(der_xs + "contact")
                               select new Contact
                               {
                                   ID = contact.Attribute("id").Value,
                                   Name = contact.Attribute("name").Value,
                                   Title = contact.Attribute("title").Value
                               };
                orgContacts = new ObservableCollection<Contact>(contactList.ToList<Contact>());

                //submitter.ItemsSource = orgContacts;
                //administrator.ItemsSource = orgContacts;

            } catch (Exception ex)
            {
                MessageBox.Show("Unable to get organization contact names:\n\n"+ex.Message);
                //submitter.IsEnabled = false;
            }
        }

        private void LoadDefaultRegAuths()
        {
            try
            {
                string response = derClient.getRegAuth();
                XDocument xDoc = XDocument.Parse(response);
                var regAuthList = from contact in xDoc.Root.Elements(der_xs + "contact")
                               select new Contact
                               {
                                   ID = contact.Attribute("id").Value,
                                   Name = contact.Attribute("name").Value,
                                   Organization = contact.Attribute("organization").Value
                               };
                regAuths = new ObservableCollection<Contact>(regAuthList.ToList<Contact>());

                //registrar.ItemsSource = regAuths;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Unable to get registration authority contact names:\n\n" + ex.Message);
                //submitter.IsEnabled = false;
            }
        }

        private void initSubmitClient(String username, String password)
        {
            submitClient = new DataElementReduced.DataElementReducedPortTypeClient("DataElementReducedSOAP12port_https");
            submitClient.ClientCredentials.UserName.UserName = username;
            submitClient.ClientCredentials.UserName.Password = password;
        }

        private void btnSubmit_Click(object sender, RoutedEventArgs e)
        {
            if (txtUsername.Text == null || txtUsername.Text.Length == 0 || txtPassword.Password == null || txtPassword.Password.Length == 0)
            {
                MessageBox.Show("Username and/or password cannot be empty");
                return;
            }

            if (submitClient == null || submitClient.ClientCredentials.UserName.UserName != txtUsername.Text || submitClient.ClientCredentials.UserName.Password != txtPassword.Password)
            {
                initSubmitClient(txtUsername.Text, txtPassword.Password);
            }

            DataElementReduced.newdataelementreduced newDER = new DataElementReduced.newdataelementreduced();
            newDER.preferredname = getRichTextBoxContent(preferredName);
            newDER.othernames = altNames.ToArray<string>();
            newDER.definition = getRichTextBoxContent(definition);
            
            DataElementReduced.values valueDomain = new DataElementReduced.values();
            if (isEnumerated)
            {
                DataElementReduced.enumerated enumerated = new DataElementReduced.enumerated();
                List<DataElementReduced.value> values = new List<DataElementReduced.value>();

                foreach (ValidValue vv in validValues)
                {
                    DataElementReduced.value v = new DataElementReduced.value() { code = vv.Code, meaning = vv.Meaning };
                    values.Add(v);
                }
                enumerated.value = values.ToArray<DataElementReduced.value>();
                valueDomain.Item = enumerated;
            }
            else
            {
                DataElementReduced.nonenumerated nonEnumerated = new DataElementReduced.nonenumerated();
                nonEnumerated.datatype = ((DERItem)dataTypesList.SelectedItem).ID;
                nonEnumerated.uom = ((DERItem)uomList.SelectedItem).ID;
                valueDomain.Item = nonEnumerated;
            }

            newDER.values = valueDomain;

            newDER.submitter = ((Contact)submitter.SelectedItem).ID;
            newDER.administrator = ((Contact)administrator.SelectedItem).ID;
            newDER.registrar = ((Contact)registrar.SelectedItem).ID;
            try
            {
                lastResult = submitClient.createDataElementReduced(newDER);
                if (lastResult == null || lastResult.Length == 0)
                {
                    throw new Exception("Error submitting new data element");
                }
                this.parent.Visible = false;

                /*
                ResponseControl responseControl = new ResponseControl();
                responseControl.OnUse += new EventHandler(responseControl_OnUse);
                XmlDocument xDoc = new XmlDocument();
                xDoc.LoadXml(lastResult);
                responseControl.txtID.Text = xDoc.DocumentElement.SelectSingleNode("/data-element/names/id").InnerXml;
                responseControl.preferredName.Text = xDoc.DocumentElement.SelectSingleNode("/data-element/names/preferred").InnerXml;
                responseControl.Show();
                */
                if (scControl != null)
                {
                    XmlDocument xDoc = new XmlDocument();
                    xDoc.LoadXml(lastResult);
                    scControl.ID = xDoc.DocumentElement.SelectSingleNode("/data-element/names/id").InnerXml;
                    scControl.PREFERRED = xDoc.DocumentElement.SelectSingleNode("/data-element/names/preferred").InnerXml;
                    scControl.Show();
                }
                this.parent.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Submit: " + ex.Message);
                //MessageBox.Show("Error submitting new data element");
            }
        }

        void responseControl_OnUse(object sender, EventArgs e)
        {
            if (OnUseResult != null)
            {
                OnUseResult(this, new EventArgs());
            }
        }

        private void btnCancel_Click(object sender, RoutedEventArgs e)
        {
            if (parent != null)
            {
                parent.Close();
            }
        }

        private string getRichTextBoxContent(RichTextBox myRichTextBox)
        {
            try
            {
                TextRange tr = new TextRange(myRichTextBox.Document.ContentStart, myRichTextBox.Document.ContentEnd);
                System.IO.MemoryStream ms = new System.IO.MemoryStream();
                tr.Save(ms, DataFormats.Text);
                return ASCIIEncoding.Default.GetString(ms.ToArray());
            }
            catch (Exception)
            {
                return null;
            }
        }

        private void btnNewAltName_Click(object sender, RoutedEventArgs e)
        {
            if (txtNewAltName.Text != null && txtNewAltName.Text.Length > 0 && !altNames.Contains(txtNewAltName.Text))
            {
                altNames.Add(txtNewAltName.Text);
                txtNewAltName.Text = "";
            }
        }

        private void btnRemoveAltName_Click(object sender, RoutedEventArgs e)
        {
            if (otherNames.SelectedItem != null)
                altNames.Remove(otherNames.SelectedItem.ToString());
        }

        private void enumeratedValue_Checked(object sender, RoutedEventArgs e)
        {
            if (enumeratedValueControl != null) enumeratedValueControl.Visibility = Visibility.Visible;
            if (nonEumeratedValueControl != null) nonEumeratedValueControl.Visibility = Visibility.Collapsed;
            isEnumerated = true;
        }

        private void nonEnumeratedValue_Checked(object sender, RoutedEventArgs e)
        {
            if (enumeratedValueControl != null) enumeratedValueControl.Visibility = Visibility.Collapsed;
            if (nonEumeratedValueControl != null) nonEumeratedValueControl.Visibility = Visibility.Visible;
            isEnumerated = false;
        }

        private void LoadDefaultDataTypes()
        {
            try
            {
                string response = derClient.getDataTypes();
                XDocument xDoc = XDocument.Parse(response);
                var dt = from contact in xDoc.Root.Elements(der_xs + "datatype")
                         select new DERItem
                         {
                             ID = contact.Attribute("id").Value,
                             Name = contact.Attribute("name").Value,
                             Scheme = contact.Attribute("scheme").Value
                         };
                dataTypes = new ObservableCollection<DERItem>(dt.ToList<DERItem>());

                //dataTypesList.ItemsSource = dataTypes;


            }
            catch (Exception)
            {
                //dataTypesList.IsEnabled = false;
            }
        }

        private void LoadDefaultUOM()
        {
            try
            {
                string response = derClient.getUOM();
                XDocument xDoc = XDocument.Parse(response);
                var u = from contact in xDoc.Root.Elements(der_xs + "unit_of_measure")
                        select new DERItem
                        {
                            ID = contact.Attribute("id").Value,
                            Name = contact.Attribute("name").Value,
                        };
                UOMs = new ObservableCollection<DERItem>(u.ToList<DERItem>());

                //uomList.ItemsSource = UOMs;

            }
            catch (Exception)
            {
                //uomList.IsEnabled = false;
            }
        }

        private void btnRemoveValidValue_Click(object sender, RoutedEventArgs e)
        {
            if (validValueList.SelectedItem != null)
                validValues.Remove((ValidValue)validValueList.SelectedItem);
        }

        private void btnNewValidValue_Click(object sender, RoutedEventArgs e)
        {
            if (txtNewCode.Text != null && txtNewCode.Text.Length > 0)
            {
                foreach (ValidValue v in validValues)
                {
                    if (v.Code == txtNewCode.Text)
                    {
                        return;
                    }
                }

                ValidValue vv = new ValidValue { Code = txtNewCode.Text, Meaning = txtNewMeaning.Text };
                validValues.Add(vv);
                txtNewCode.Text = "";
                txtNewMeaning.Text = "";
            }
        }

        private void btnReset_Click(object sender, RoutedEventArgs e)
        {
            preferredName.Document.Blocks.Clear();
            altNames.Clear();
            definition.Document.Blocks.Clear();
            validValues.Clear();
            dataTypesList.SelectedIndex = -1;
            uomList.SelectedIndex = -1;
            submitter.SelectedIndex = -1;
            administrator.SelectedIndex = -1;
            registrar.SelectedIndex = -1;
        }

        private void bgWorker_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (e.Error == null)
            {
                submitter.ItemsSource = orgContacts;
                administrator.ItemsSource = orgContacts;
                registrar.ItemsSource = regAuths;
                dataTypesList.ItemsSource = dataTypes;
                uomList.ItemsSource = UOMs;

                loadingPane.Visibility = Visibility.Collapsed;
                formContent.IsEnabled = true;
            }
            else
            {
                MessageBox.Show("Worker error: " + e.Error.Message);
                this.parent.Close();
            }
        }

        private void bgWorker_DoWork(object sender, DoWorkEventArgs e)
        {
            LoadDefaultContacts();
            LoadDefaultRegAuths();
            LoadDefaultDataTypes();
            LoadDefaultUOM();
        }

        private void bgWorker_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            loadingStatus.Content += ".";
        }
    }

    public class Contact
    {
        public string ID { get; set; }
        public string Name { get; set; }
        public string Title { get; set; }
        public string Organization { get; set; }
    }

    public class DERItem
    {
        public string ID { get; set; }
        public string Name { get; set; }
        public string Scheme { get; set; }
    }

    public class ValidValue
    {
        public string Code { get; set; }
        public string Meaning { get; set; }
    }
}
