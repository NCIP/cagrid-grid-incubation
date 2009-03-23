using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Xml.Linq;
using System.Windows.Forms;

namespace EnterpriseArchitectAddIn
{
    class EAUtil
    {
        public enum INSERT_XSD_TYPE
        {
            TOP_LEVEL_ELEMENT,
            TOP_LEVEL_ATTRIBUTE,
            NOT_APPLICABLE
        }

        public static XNamespace rs = "http://cancergrid.org/schema/result-set";

        public static void annotateWithCDE(EA.Repository m_Repository, QueryServiceControl.QueryServiceManager.dataelement de)
        {
            object item;
            const string CDEREF = "CDERef";
            const string PREFNAME = "preferred name";

            try
            {
                string id = de.names.id;
                string name = de.names.preferred;

                EA.ObjectType type = m_Repository.GetTreeSelectedItem(out item);
                if (type != EA.ObjectType.otAttribute)
                {
                    MessageBox.Show("No class attribute has been selected in the Project Browser", "Error", 
                        MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
            
                EA.Attribute attr = (EA.Attribute)item;

                //string txt = "Container["+attr.Container;
                //txt += "]\nContainment["+attr.Containment;
                //txt += "]\nDefault["+attr.Default;
                //txt += "]\nParentID["+attr.ParentID;
                //txt += "]\nPos["+attr.Pos;
                //txt += "]\nStereotype["+attr.Stereotype;
                //txt += "]\nStereotypeEx["+attr.StereotypeEx;
                //txt += "]\nStyle["+attr.Style;
                //txt += "]\nStyleEx["+attr.StyleEx;
                //txt += "]\n";
                //MessageBox.Show(txt);

                EA.Collection tvs = attr.TaggedValues;
                if (tvs.GetByName(CDEREF) != null)
                {
                    //DialogResult dg = MessageBox.Show(
                    //    el.Name + " already has a " + CDEREF + " annotation. Replace?", 
                    //    "Confirm", MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
                    //if (dg == DialogResult.Cancel)
                    //    return;

                    MessageBox.Show(attr.Name + " already has a " + CDEREF + " annotation. Please remove and try again.");
                    return;
                }

                if (tvs.GetByName(PREFNAME) != null)
                {
                    //DialogResult dg = MessageBox.Show(
                    //    el.Name + " already has a " + PREFNAME + " annotation. Replace?", 
                    //    "Confirm", MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
                    //if (dg == DialogResult.Cancel)
                    //    return;
                    MessageBox.Show(attr.Name + " already has a " + PREFNAME + " annotation. Please remove and try again.");
                    return;
                }
  
                EAUtil.addTaggedValue(CDEREF, id, attr);
                EAUtil.addTaggedValue(PREFNAME, name, attr);

                //
                // All done, just display a successful message window
                //
                string attrName = "";
                EA.Element pel = m_Repository.GetElementByID(attr.ParentID);
                if (pel != null && pel.Name != null && pel.Name.Length > 0)
                    attrName = pel.Name + ".";
                attrName += attr.Name;

                MessageBox.Show("Attribute [" + attrName + "] successfully annotated with:\n\n"
                    + CDEREF + " =>   " + id + "\n"
                    + PREFNAME + " =>   " + name + "\n",
                    "Success",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Information);
            }
            catch(Exception exp)
            {
                MessageBox.Show("Error annotating with CDE. " + exp.Message);
            }
        }

        public static void insertConceptRef(EA.Repository m_Repository, QueryServiceControl.QueryServiceManager.concept concept)
        {
            try
            {
                string id = concept.names.id;
                if (id.StartsWith("http:"))
                {
                    id = id.Substring(id.IndexOf("#") + 1);
                }
                else
                {
                    id = id.Substring(id.LastIndexOf("-") + 1);
                }

                object item;
                EA.ObjectType t = m_Repository.GetTreeSelectedItem(out item);

                if (t.ToString() == "otDiagram")
                {
                    MessageBox.Show("Please select an package or element in the Project Browser.");
                }
                else if (t.ToString() == "otElement")
                {
                    EA.Element el = (EA.Element)item;
                    string note = el.Notes;
                    if (note.Contains("ConceptRefs: ["))
                    {
                        if (!note.Substring(note.IndexOf("ConceptRefs: [") + 14).Replace("])", "").Contains(id))
                        {
                            el.Notes = note.Substring(0, note.IndexOf("ConceptRefs: [") + 14) + note.Substring(note.IndexOf("ConceptRefs: [") + 14).Replace("])", ", " + id + "])");
                        }
                    }
                    else
                    {
                        el.Notes += " (ConceptRefs: [" + id + "])";
                    }
                    el.Update();
                    el.Refresh();
                    MessageBox.Show("Concept reference, " + id + ", inserted to selected element.");
                }
                else if (t.ToString() == "otPackage")
                {
                    EA.Package pkg = (EA.Package)item;
                    string note = pkg.Notes;
                    if (note.Contains("ConceptRefs: ["))
                    {
                        if (!note.Substring(note.IndexOf("ConceptRefs: [") + 14).Replace("])", "").Contains(id))
                        {
                            pkg.Notes = note.Substring(0, note.IndexOf("ConceptRefs: [") + 14) + note.Substring(note.IndexOf("ConceptRefs: [") + 14).Replace("])", ", " + id + "])");
                        }
                    }
                    else
                    {
                        pkg.Notes += " (ConceptRefs: [" + id + "])";
                    }
                    pkg.Update();
                    MessageBox.Show("Concept reference, " + id + ", inserted to selected package.");
                }
                else
                {
                    MessageBox.Show("Please select a valid element (class, package) for the concept reference to be inserted into.");
                }
            }
            catch (Exception exp)
            {
                MessageBox.Show("Error inserting concept reference. " + exp.Message);
            }
        }

        //deprecated
        public static void insertCDE(EA.Repository m_Repository, XElement node, EAUtil.INSERT_XSD_TYPE xsdType)
        {
            try
            {
                string id = node.Element(rs + "names").Element(rs + "id").Value;
                string name = node.Element(rs + "names").Element(rs + "preferred").Value;
                string definition = node.Element(rs + "definition").Value;

                if (definition == null || definition.Length == 0)
                {
                    definition = "(No definition supplied)";
                }
                else
                {
                    //Handle special caDSR format
                    definition = definition.Trim().Replace("&gt;", ">").Replace("&lt;", "<").Replace("<![CDATA[", "").Replace("]]>", "");
                    if (definition.Contains("<def-source>"))
                    {
                        XElement e = XElement.Parse("<def>" + definition + "</def>");
                        definition = e.Element("def-definition").Value + "\n(Source: " + e.Element("def-source").Value + ")";
                    }
                }

                EA.Package package = m_Repository.GetPackageByID(m_Repository.GetCurrentDiagram().PackageID);
                EA.Element o = (EA.Element)package.Elements.AddNew(name.Replace(" ", "_"), "Class");
                o.Notes = definition + " (ID: " + id + ")";

                EAUtil.addTaggedValue("CDERef", id, o);
                EAUtil.addTaggedValue("preferred name", name, o);

                if (package.StereotypeEx == "XSDschema")
                {
                    if (xsdType == EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ELEMENT)
                    {
                        o.Stereotype = "XSDtopLevelElement";
                    }
                    else if (xsdType == EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ATTRIBUTE)
                    {
                        o.Stereotype = "XSDtopLevelAttribute";
                    }
                }

                if (node.Element(rs + "values").Element(rs + "non-enumerated") != null)
                {
                    string dataType = node.Element(rs + "values").Element(rs + "non-enumerated").Element(rs + "data-type").Value;
                    if (dataType.StartsWith("xs:"))// || dataType.StartsWith("xsd:"))
                    {
                        //o.Genlinks = "Parent=" + dataType.Substring(dataType.IndexOf(":") + 1) + ";";
                        string simpleTypeClassName = name.Replace(" ", "_") + "_Type";
                        EA.Element simpleTypeClass = (EA.Element)package.Elements.AddNew(simpleTypeClassName, "Class");
                        simpleTypeClass.Stereotype = "XSDsimpleType";
                        simpleTypeClass.Genlinks = "Parent=" + dataType.Substring(dataType.IndexOf(":") + 1) + ";";
                        simpleTypeClass.Update();
                        simpleTypeClass.Refresh();

                        EA.DiagramObject simpleTypeDiag = (EA.DiagramObject)(m_Repository.GetCurrentDiagram().DiagramObjects.AddNew("", ""));
                        simpleTypeDiag.ElementID = simpleTypeClass.ElementID;
                        simpleTypeDiag.Update();

                        EA.Connector simpleTypeConnector = (EA.Connector)o.Connectors.AddNew("", "Generalization");
                        simpleTypeConnector.SupplierID = simpleTypeDiag.ElementID;
                        simpleTypeConnector.Update();
                        o.Connectors.Refresh();
                    }
                    else
                    {
                        o.Genlinks = "Parent=" + dataType + ";";
                    }
                }
                else if (node.Element(rs + "values").Element(rs + "enumerated") != null)
                {
                    string enumClassName = "Enum_" + name.Replace(" ", "_");
                    EA.Element enumClass = (EA.Element)package.Elements.AddNew(enumClassName, "Class");
                    enumClass.Stereotype = "enumeration";
                    enumClass.Genlinks = "Parent=string;";
                    foreach (XElement n in node.Element(rs + "values").Element(rs + "enumerated").Elements(rs + "valid-value"))
                    {
                        EA.Attribute at = (EA.Attribute)enumClass.Attributes.AddNew("", "");
                        at.Stereotype = "enum";
                        at.Name = n.Element(rs + "code").Value;
                        at.Notes = n.Element(rs + "meaning").Value;
                        at.Update();
                    }
                    enumClass.Attributes.Refresh();

                    EA.DiagramObject enumDiag = (EA.DiagramObject)(m_Repository.GetCurrentDiagram().DiagramObjects.AddNew("", ""));
                    enumDiag.ElementID = enumClass.ElementID;
                    enumDiag.Update();

                    EA.Connector enumConnector = (EA.Connector)o.Connectors.AddNew("", "Generalization");
                    enumConnector.SupplierID = enumDiag.ElementID;
                    enumConnector.Update();
                    o.Connectors.Refresh();

                    enumClass.Update();
                    enumClass.Refresh();
                }
                //attr.Update();

                //o.Attributes.Refresh();

                o.Update();
                o.Refresh();

                EA.DiagramObject diagram = (EA.DiagramObject)(m_Repository.GetCurrentDiagram().DiagramObjects.AddNew("", ""));
                diagram.ElementID = o.ElementID;
                diagram.Update();

                m_Repository.ReloadDiagram(diagram.DiagramID);

                //this.Close();
            }
            catch (Exception exp)
            {
                MessageBox.Show("Please make sure you have an active diagram openned." + "\n" + exp.Message + "\n" + exp.StackTrace, "No active diagram currently selected", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
        }

        public static void insertCDE(EA.Repository m_Repository, QueryServiceControl.QueryServiceManager.dataelement de, EAUtil.INSERT_XSD_TYPE xsdType)
        {
            try
            {
                string id = de.names.id;
                string name = de.names.preferred;
                string definition = QueryServiceControl.QueryServiceControl.getDefinition(de.definition);

                EA.Package package = m_Repository.GetPackageByID(m_Repository.GetCurrentDiagram().PackageID);
                EA.Element o = (EA.Element)package.Elements.AddNew(name.Replace(" ", "_"), "Class");
                o.Notes = definition + " (ID: " + id + ")";

                EAUtil.addTaggedValue("CDERef", id, o);
                EAUtil.addTaggedValue("preferred name", name, o);

                if (package.StereotypeEx == "XSDschema")
                {
                    if (xsdType == EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ELEMENT)
                    {
                        o.Stereotype = "XSDtopLevelElement";
                    }
                    else if (xsdType == EAUtil.INSERT_XSD_TYPE.TOP_LEVEL_ATTRIBUTE)
                    {
                        o.Stereotype = "XSDtopLevelAttribute";
                    }
                }

                if (de.values != null && de.values.Item is QueryServiceControl.QueryServiceManager.nonenumerated)
                {
                    string dataType = (de.values.Item as QueryServiceControl.QueryServiceManager.nonenumerated).datatype;
                    if (dataType.StartsWith("xs:"))// || dataType.StartsWith("xsd:"))
                    {
                        //o.Genlinks = "Parent=" + dataType.Substring(dataType.IndexOf(":") + 1) + ";";
                        string simpleTypeClassName = name.Replace(" ", "_") + "_Type";
                        EA.Element simpleTypeClass = (EA.Element)package.Elements.AddNew(simpleTypeClassName, "Class");
                        simpleTypeClass.Stereotype = "XSDsimpleType";
                        simpleTypeClass.Genlinks = "Parent=" + dataType.Substring(dataType.IndexOf(":") + 1) + ";";
                        simpleTypeClass.Update();
                        simpleTypeClass.Refresh();

                        EA.DiagramObject simpleTypeDiag = (EA.DiagramObject)(m_Repository.GetCurrentDiagram().DiagramObjects.AddNew("", ""));
                        simpleTypeDiag.ElementID = simpleTypeClass.ElementID;
                        simpleTypeDiag.Update();

                        EA.Connector simpleTypeConnector = (EA.Connector)o.Connectors.AddNew("", "Generalization");
                        simpleTypeConnector.SupplierID = simpleTypeDiag.ElementID;
                        simpleTypeConnector.Update();
                        o.Connectors.Refresh();
                    }
                    else
                    {
                        o.Genlinks = "Parent=" + dataType + ";";
                    }
                }
                else if (de.values != null && de.values.Item is QueryServiceControl.QueryServiceManager.enumerated)
                {
                    string enumClassName = "Enum_" + name.Replace(" ", "_");
                    EA.Element enumClass = (EA.Element)package.Elements.AddNew(enumClassName, "Class");
                    enumClass.Stereotype = "enumeration";
                    enumClass.Genlinks = "Parent=string;";

                    QueryServiceControl.QueryServiceManager.enumerated en =
                        de.values.Item as QueryServiceControl.QueryServiceManager.enumerated;

                    foreach (QueryServiceControl.QueryServiceManager.validvalue vv in en.validvalue)
                    {
                        EA.Attribute at = (EA.Attribute)enumClass.Attributes.AddNew("", "");
                        at.Stereotype = "enum";
                        at.Name = vv.code;
                        at.Notes = QueryServiceControl.QueryServiceControl.getMeaning(vv.meaning);
                        at.Update();
                    }
                    enumClass.Attributes.Refresh();

                    EA.DiagramObject enumDiag = (EA.DiagramObject)(m_Repository.GetCurrentDiagram().DiagramObjects.AddNew("", ""));
                    enumDiag.ElementID = enumClass.ElementID;
                    enumDiag.Update();

                    EA.Connector enumConnector = (EA.Connector)o.Connectors.AddNew("", "Generalization");
                    enumConnector.SupplierID = enumDiag.ElementID;
                    enumConnector.Update();
                    o.Connectors.Refresh();

                    enumClass.Update();
                    enumClass.Refresh();
                }
                //attr.Update();

                //o.Attributes.Refresh();

                o.Update();
                o.Refresh();

                EA.DiagramObject diagram = (EA.DiagramObject)(m_Repository.GetCurrentDiagram().DiagramObjects.AddNew("", ""));
                diagram.ElementID = o.ElementID;
                diagram.Update();

                m_Repository.ReloadDiagram(diagram.DiagramID);

                //this.Close();
            }
            catch (Exception exp)
            {
                MessageBox.Show("Please make sure you have an active diagram openned." + "\n" + exp.Message + "\n" + exp.StackTrace, "No active diagram currently selected", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
        }

        public static void addTaggedValue(string name, string value, EA.Element o)
        {
            EA.TaggedValue tv = (EA.TaggedValue)o.TaggedValues.AddNew(name, "string");
            tv.Value = value;
            tv.Update();
            o.TaggedValues.Refresh();
        }

        public static void addTaggedValue(string name, string value, EA.Attribute attr)
        {
            EA.AttributeTag at = (EA.AttributeTag)attr.TaggedValues.AddNew(name, "string");
            at.Value = value;
            at.Update();
            attr.TaggedValues.Refresh();
        }
    }
}
