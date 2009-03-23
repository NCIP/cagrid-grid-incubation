namespace EnterpriseArchitectAddIn
{
    partial class DataElementCreationForm
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

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.elementHost = new System.Windows.Forms.Integration.ElementHost();
            this.dataElementCreationControl = new QueryServiceControl.DataElementCreationControl();
            this.SuspendLayout();
            // 
            // elementHost
            // 
            this.elementHost.Dock = System.Windows.Forms.DockStyle.Fill;
            this.elementHost.Location = new System.Drawing.Point(0, 0);
            this.elementHost.Name = "elementHost";
            this.elementHost.Size = new System.Drawing.Size(792, 826);
            //this.elementHost.Size = new System.Drawing.Size(792, (int)System.Windows.SystemParameters.PrimaryScreenHeight-100);
            //this.elementHost.AutoSize = true;

            this.elementHost.TabIndex = 0;
            this.elementHost.Text = "elementHost";
            this.elementHost.Child = this.dataElementCreationControl;
            this.elementHost.AutoSize = true;
            // 
            // DataElementCreationForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoScroll = true;
            this.AutoSize = false;
            this.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            //this.ClientSize = new System.Drawing.Size(792, 826);
            this.Size = new System.Drawing.Size(792, (int)(System.Windows.SystemParameters.PrimaryScreenHeight * 0.9));
            this.Controls.Add(this.elementHost);
            //this.MinimumSize = new System.Drawing.Size(800, 860);
            this.Name = "DataElementCreationForm";
            this.Text = "Data Element Creation Form";
            this.ResumeLayout(false);
            this.MaximizeBox = false;
        }

        #endregion

        private System.Windows.Forms.Integration.ElementHost elementHost;
        private QueryServiceControl.DataElementCreationControl dataElementCreationControl;
    }
}