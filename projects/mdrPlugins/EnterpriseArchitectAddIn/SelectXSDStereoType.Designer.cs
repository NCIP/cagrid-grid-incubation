namespace EnterpriseArchitectAddIn
{
    partial class SelectXSDStereoType
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
            this.btnXSDTopLevelAttr = new System.Windows.Forms.Button();
            this.btnXSDTopLevelElem = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // btnXSDTopLevelAttr
            // 
            this.btnXSDTopLevelAttr.Location = new System.Drawing.Point(45, 42);
            this.btnXSDTopLevelAttr.Name = "btnXSDTopLevelAttr";
            this.btnXSDTopLevelAttr.Size = new System.Drawing.Size(199, 23);
            this.btnXSDTopLevelAttr.TabIndex = 0;
            this.btnXSDTopLevelAttr.Text = "Insert as XSD Top Level Attribute";
            this.btnXSDTopLevelAttr.UseVisualStyleBackColor = true;
            this.btnXSDTopLevelAttr.Click += new System.EventHandler(this.btnXSDTopLevelAttr_Click);
            // 
            // btnXSDTopLevelElem
            // 
            this.btnXSDTopLevelElem.Location = new System.Drawing.Point(45, 13);
            this.btnXSDTopLevelElem.Name = "btnXSDTopLevelElem";
            this.btnXSDTopLevelElem.Size = new System.Drawing.Size(199, 23);
            this.btnXSDTopLevelElem.TabIndex = 1;
            this.btnXSDTopLevelElem.Text = "Insert as XSD Top Level Element";
            this.btnXSDTopLevelElem.UseVisualStyleBackColor = true;
            this.btnXSDTopLevelElem.Click += new System.EventHandler(this.btnXSDTopLevelElem_Click);
            // 
            // SelectXSDStereoType
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(287, 77);
            this.Controls.Add(this.btnXSDTopLevelElem);
            this.Controls.Add(this.btnXSDTopLevelAttr);
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "SelectXSDStereoType";
            this.Text = "Select XSD Stereotype to Use";
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button btnXSDTopLevelAttr;
        private System.Windows.Forms.Button btnXSDTopLevelElem;
    }
}