using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Linq;
using Excel = Microsoft.Office.Interop.Excel;
using Office = Microsoft.Office.Core;

namespace ExcelQueryServiceAddIn
{
    public partial class ThisAddIn
    {
        private ExcelQueryServiceControl queryServiceControl;
        private Microsoft.Office.Tools.CustomTaskPane myCustomTaskPane;
        private Office.CommandBarButton XMLUnmapButton;
        private Office.CommandBarButton CellUnmapButton;

        private void ThisAddIn_Startup(object sender, System.EventArgs e)
        {
            AddUnmapButtonMenuCommand();
        }

        private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
        {
            RemoveQueryServiceTaskPane();
        }

        public void AddUnmapButtonMenuCommand() 
        {
            Office._CommandBarButtonEvents_ClickEventHandler xButtonHandler =
                new Office._CommandBarButtonEvents_ClickEventHandler(unmapXMLClick);

            Office._CommandBarButtonEvents_ClickEventHandler cButtonHandler =
                new Office._CommandBarButtonEvents_ClickEventHandler(unmapHeaderClick);

            XMLUnmapButton = (Office.CommandBarButton)Application.CommandBars["XML Range Popup"].Controls.Add(Office.MsoControlType.msoControlButton, missing, missing, missing, true);
            XMLUnmapButton.Click += xButtonHandler;
            XMLUnmapButton.Caption = "Unmap Values";
            XMLUnmapButton.Visible = true;

            CellUnmapButton = (Office.CommandBarButton)Application.CommandBars["Cell"].Controls.Add(Office.MsoControlType.msoControlButton, missing, missing, missing, true);
            CellUnmapButton.Click += cButtonHandler;
            CellUnmapButton.Caption = "Clear Cell";
            CellUnmapButton.Visible = true;
        }

        private void unmapXMLClick(Office.CommandBarButton button, ref bool Cancel)
        {
            Excel.Range selected = (Excel.Range)this.Application.Selection;           
            selected.Cells.Clear();
            selected.Cells.ClearContents();
            selected.Cells.ClearFormats();
            selected.Cells.ClearNotes();

            string selectedRangeString = ((Excel.Worksheet)selected.Parent).Name + "!" + selected.get_Address(Type.Missing, Type.Missing, Excel.XlReferenceStyle.xlA1, Type.Missing, Type.Missing);
            Excel.Worksheet cdeList = (Excel.Worksheet)this.Application.Sheets["cde_list"];

            Excel.Range c = (Excel.Range)cdeList.Cells[2, 1];
            for (int i = 2; i < 10000; i++)
            {
                c = (Excel.Range)cdeList.Cells[i, 1];
                string text = c.Text.ToString();
                if (text.Contains(selectedRangeString))
                    break;
            }

            cdeList.Unprotect("dummy_password");
            c.Clear();
            c.Next.Clear();
            c.Next.Next.Clear();
            c.Next.Next.Next.Clear();
            cdeList.Protect("dummy_password", Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing);

        }

        private void unmapHeaderClick(Office.CommandBarButton button, ref bool Cancel)
        {
            Excel.Range selected = (Excel.Range)this.Application.Selection;
            Excel.Worksheet conceptList = (Excel.Worksheet)this.Application.Sheets["concept_list"];

            string[] codes = selected.Text.ToString().Split(';');
            selected.Cells.Clear();
            selected.Cells.ClearContents();
            selected.Cells.ClearFormats();
            selected.Cells.ClearNotes();

            if (conceptList != null)
            {
                conceptList.Unprotect("dummy_password");

                //Use Excel built-in Find feature to search for matched row
                foreach (String code in codes)
                {
                    string c = code.Split(':')[0];
                    Excel.Range found = conceptList.Cells.Find(c, Type.Missing, Excel.XlFindLookIn.xlValues, Excel.XlLookAt.xlPart, Excel.XlSearchOrder.xlByRows, Excel.XlSearchDirection.xlNext, false, Type.Missing, Type.Missing);
                    if (found != null)
                    {
                        int counter = Convert.ToInt16(found.Next.Next.Next.Next.Value2.ToString()) - 1;
                        if (counter < 1)
                        {
                            found.EntireRow.Delete(Type.Missing); //Remove entire row
                        }
                        else
                        {
                            found.Next.Next.Next.Next.Value2 = counter;
                        }
                    }
                }

                /*
                Excel.Range c = (Excel.Range)conceptList.Cells[2, 1];
                bool loop = true;
                for (int i = 2; loop; i++)
                {
                    c = (Excel.Range)conceptList.Cells[i, 1];

                    //Go through all concepts in the field
                    //If the concept is already mapped, decrement it's Mapped counter
                    
                    for (int a = 0; a < codes.Length; a++)
                    {
                        string code = codes[a].Split(':')[0];
                        if (c.Text.ToString().Contains(code))
                        {
                            int counter = Convert.ToInt16(c.Next.Next.Next.Next.Text);
                            
                            //If no more mapped, clear the row.
                            if (counter - 1 == 0)
                            {
                                c.Clear();
                                c.Next.Clear();
                                c.Next.Next.Clear();
                                c.Next.Next.Next.Clear();
                                c.Next.Next.Next.Next.Clear();
                            }
                            //Otherwise just decrement counter.
                            else
                            {
                                c.Next.Next.Next.Next.Value2 = counter - 1;
                            }
                            loop = false;
                            break;
                        }
                    }
                    
                }
                */
                conceptList.Protect("dummy_password", Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing);
            }

        }

        public void AddQueryServiceTaskPane()
        {
            if (myCustomTaskPane == null)
            {
                queryServiceControl = new ExcelQueryServiceControl();
                myCustomTaskPane = this.CustomTaskPanes.Add(queryServiceControl, "Query Service Control");
                myCustomTaskPane.DockPosition = Microsoft.Office.Core.MsoCTPDockPosition.msoCTPDockPositionRight;
                myCustomTaskPane.Width = 300;
                myCustomTaskPane.Visible = true;
            }
            else if (myCustomTaskPane.Visible == false)
            {
                myCustomTaskPane.Visible = true;
            }
        }

        public void RemoveQueryServiceTaskPane()
        {
            try
            {
                if (myCustomTaskPane != null && this.CustomTaskPanes.Contains(myCustomTaskPane))
                {
                    this.CustomTaskPanes.Remove(myCustomTaskPane);
                }
            }
            catch (Exception) { }
            myCustomTaskPane = null;
        }

        protected override Microsoft.Office.Core.IRibbonExtensibility CreateRibbonExtensibilityObject()
        {
            return new ExcelQueryServiceRibbon();
        }

        #region VSTO generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(ThisAddIn_Startup);
            this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
        }
        
        #endregion
    }
}
