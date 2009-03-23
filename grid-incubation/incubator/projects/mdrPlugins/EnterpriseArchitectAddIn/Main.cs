using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
using System.Windows.Forms;

namespace EnterpriseArchitectAddIn
{
    public class Main
    {
        public const string ROOT_MENU = "&MDR Query Service Panel";

        public String EA_Connect(EA.Repository Repository)
        {
            // No special processing req'd
            return "";
        }

        public void EA_ShowHelp(EA.Repository Repository, string Location, string MenuName, string ItemName)
        {
            MessageBox.Show("Help for: " + MenuName + "/" + ItemName);
        }

        public object EA_GetMenuItems(EA.Repository Repository, string Location, string MenuName)
        {
            /* nb example of out parameter:
            object item;
            EA.ObjectType t = Repository.GetTreeSelectedItem(out item);
            EA.Package r = (EA.Package) item;
            */

            switch (MenuName)
            {
                case "":
                    return ROOT_MENU;
                    //return "-&CancerGrid";
                    
                    /*
                case "-&CancerGrid":
                    string[] ar = { "&Open Query Service Panel", "&Create CDE" };
                    //string[] ar = { "&Add Element from CDE", "&Create CDE", "&View Selected Element Details" };
                    return ar;
                     */ 
                    
            }
            return "";
        }

        public void EA_GetMenuState(EA.Repository Repository, string Location, string MenuName, string ItemName, ref bool IsEnabled, ref bool IsChecked)
        {
            if (IsProjectOpen(Repository))// && SelectedElement(Repository))
            {
                IsEnabled = true;
            }
            else
                // If no open project, disable all menu options
                IsEnabled = false;
        }

        public void EA_MenuClick(EA.Repository Repository, string Location, string MenuName, string ItemName)
        {
            EAQueryServiceForm eaq;
            DataElementCreationForm der;
            switch (ItemName)
            {
                case ROOT_MENU:
                //case "&Open Query Service Panel":
                
                    eaq = new EAQueryServiceForm();
                    eaq.eaQueryServiceControl.m_Repository = Repository;
                    eaq.eaQueryServiceControl.m_IncludeElements = false;
                    eaq.Show();
                    break;
                    /*
                case "&Create CDE":
                    der = new DataElementCreationForm();
                    der.m_Repository = Repository;
                    der.Show();
                    break;
                    */
                /* 
                case "&View Selected Element Details":
                    ShowElementDetails(Repository);
                    break;
                 */ 
            }
        }
           
        public void EA_Disconnect()
        {
            GC.Collect();
            GC.WaitForPendingFinalizers();
        }
/*
        private void ShowElementDetails(EA.Repository repository)
        {
            if (!SelectedElement(repository))
            {
                MessageBox.Show("No element selected");
            }

            object item;
            EA.ObjectType t = repository.GetTreeSelectedItem(out item);
            EA.Diagram r = (EA.Diagram)item;
            string s = "Author: " + r.Author + "\n";
            s += "CreatedDate: " + r.CreatedDate + "\n";
            s += "DiagramGUID: " + r.DiagramGUID + "\n";
            s += "ExtendedStyle: " + r.ExtendedStyle + "\n";
            s += "MetaType: " + r.MetaType + "\n";
            s += "Name: " + r.Name + "\n";
            s += "Notes: " + r.Notes + "\n";
            s += "ObjectType: " + r.ObjectType.ToString() + "\n";
            s += "Orientation: " + r.Orientation + "\n";
            s += "Stereotype: " + r.Stereotype + "\n";
            s += "StereotypeEx: " + r.StereotypeEx + "\n";
            s += "StyleEx: " + r.StyleEx + "\n";
            s += "Swimlanes: " + r.Swimlanes + "\n";
            s += "Type: " + r.Type + "\n";
            s += "Version: " + r.Version + "\n";
            MessageBox.Show(s);

            EA.DiagramObject dObj = (EA.DiagramObject)r.SelectedObjects.GetAt(0);
            EA.Element el = repository.GetElementByID(dObj.ElementID);

            s = "Abstract: " + el.Abstract + "\n";
            s += "ActionFlags: " + el.ActionFlags + "\n";
            s += "Alias: " + el.Alias + "\n";
            s += "Author: " + el.Author + "\n";
            s += "ClassifierName: " + el.ClassifierName + "\n";
            s += "ClassifierType: " + el.ClassifierType + "\n";
            s += "Complexity: " + el.Complexity + "\n";
            s += "Difficulty: " + el.Difficulty + "\n";
            s += "ElementGUID: " + el.ElementGUID + "\n";
            s += "EventFlags: " + el.EventFlags + "\n";
            s += "ExtensionPoints: " + el.ExtensionPoints + "\n";
            s += "Genfile: " + el.Genfile + "\n";
            s += "Genlinks: " + el.Genlinks + "\n";
            s += "Gentype: " + el.Gentype + "\n";
            s += "MetaType: " + el.MetaType + "\n";
            s += "Multiplicity: " + el.Multiplicity + "\n";
            s += "Name: " + el.Name + "\n";
            s += "Notes: " + el.Notes + "\n";
            s += "Phase: " + el.Phase + "\n";
            s += "Priority: " + el.Priority + "\n";
            s += "Status: " + el.Status + "\n";
            s += "Stereotype: " + el.Stereotype + "\n";
            s += "StereotypeEx: " + el.StereotypeEx + "\n";
            s += "StyleEx: " + el.StyleEx + "\n";
            s += "Tag: " + el.Tag + "\n";
            s += "Type: " + el.Type + "\n";
            s += "Version: " + el.Version + "\n";
            s += "Visibility: " + el.Visibility + "\n";
            MessageBox.Show(s);
        }
*/
        private bool SelectedElement(EA.Repository repository)
        {
            try
            {
                object item;
                EA.ObjectType t = repository.GetTreeSelectedItem(out item);
                EA.Diagram r = (EA.Diagram)item;
                if (r.SelectedObjects.Count != 1)
                {
                    return false;
                }
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        private bool IsProjectOpen(EA.Repository repository)
        {
            try
            {
                EA.Collection c = repository.Models;
                return true;
            }
            catch
            {
                return false;
            }
        }
    }
}
