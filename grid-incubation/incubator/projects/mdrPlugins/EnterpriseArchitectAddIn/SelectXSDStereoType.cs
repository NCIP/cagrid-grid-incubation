using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace EnterpriseArchitectAddIn
{
    public partial class SelectXSDStereoType : Form
    {
        public SelectXSDStereoType()
        {
            InitializeComponent();
        }

        private void btnXSDTopLevelElem_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Yes;
        }

        private void btnXSDTopLevelAttr_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.No;
        }


    }
}
