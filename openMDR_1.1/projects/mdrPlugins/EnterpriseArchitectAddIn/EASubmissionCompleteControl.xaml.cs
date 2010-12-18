using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace EnterpriseArchitectAddIn
{
    /// <summary>
    /// Interaction logic for EASubmissionCompleteControl.xaml
    /// </summary>
    public partial class EASubmissionCompleteControl : QueryServiceControl.SubmissionCompleteControl
    {
        public event EventHandler OnInsertTopXSDElement;
        public event EventHandler OnInsertTopXSDAttribute;

        public EASubmissionCompleteControl()
        {
            InitializeComponent();
        }

        private void btnInsertTopXSDElement_Click(object sender, RoutedEventArgs e)
        {
            if (OnInsertTopXSDElement != null)
            {
                OnInsertTopXSDElement(this, e);
            }
        }

        private void btnInsertTopXSDAttribute_Click(object sender, RoutedEventArgs e)
        {
            if (OnInsertTopXSDAttribute != null)
            {
                OnInsertTopXSDAttribute(this, e);
            }
        }

        private void btnClose_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void SubmissionCompleteControl_Loaded(object sender, RoutedEventArgs e)
        {
            this.txtID.Text = ID;
            this.preferredName.Text = PREFERRED;
        }
    }
}
