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

namespace ExcelQueryServiceAddIn
{
    /// <summary>
    /// Interaction logic for ExcelSubmissionCompleteControl.xaml
    /// </summary>
    public partial class ExcelSubmissionCompleteControl : QueryServiceControl.SubmissionCompleteControl
    {
        public event EventHandler OnUse;

        public ExcelSubmissionCompleteControl()
        {
            InitializeComponent();
        }

        private void btnUse_Click(object sender, RoutedEventArgs e)
        {
            if (OnUse != null)
            {
                OnUse(this, new EventArgs());
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
