﻿using System;
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

namespace QueryServiceControl
{
    /// <summary>
    /// Interaction logic for DetailsUserControl.xaml
    /// </summary>
    public partial class ResponseControl : Window
    {
        public event EventHandler OnUse;

        public ResponseControl()
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


    }
}
