namespace EnterpriseArchitectAddIn
{
    partial class EAQueryServiceForm
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
            this.eaQueryServiceControl = new EnterpriseArchitectAddIn.EAQueryServiceControl();
            this.SuspendLayout();
            // 
            // eaQueryServiceControl1
            // 
            this.eaQueryServiceControl.AutoSize = true;
            this.eaQueryServiceControl.Dock = System.Windows.Forms.DockStyle.Fill;
            this.eaQueryServiceControl.Location = new System.Drawing.Point(0, 0);
            this.eaQueryServiceControl.Name = "eaQueryServiceControl1";
            this.eaQueryServiceControl.Size = new System.Drawing.Size(392, 787);
            this.eaQueryServiceControl.TabIndex = 0;
            // 
            // EAQueryServiceForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            //this.ClientSize = new System.Drawing.Size(392, 787);
            //this.Size = new System.Drawing.Size(392, (int)(System.Windows.SystemParameters.PrimaryScreenHeight * 0.8));
            this.Controls.Add(this.eaQueryServiceControl);
            this.MinimumSize = new System.Drawing.Size(400, 821);
            this.Name = "EAQueryServiceForm";
            this.Text = "CancerGrid Query Service";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public EAQueryServiceControl eaQueryServiceControl = null;//{get; set;}
    }
}