using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Linq;
using Excel = Microsoft.Office.Interop.Excel;

namespace ExcelQueryServiceAddIn
{
    public partial class DataElementCreationForm : Form
    {
        private static XNamespace rs = "http://cancergrid.org/schema/result-set";
        private Excel.Worksheet cdeList = null;
        private Excel.Application application = Globals.ThisAddIn.Application;
        private static readonly string dummyPass = "dummy_password";

        public DataElementCreationForm()
        {
            InitializeComponent();
            dataElementCreationControl.parent = this;
            ExcelSubmissionCompleteControl scControl = new ExcelSubmissionCompleteControl();
            scControl.OnUse += new EventHandler(scControl_OnUse);
            dataElementCreationControl.scControl = scControl;
            //dataElementCreationControl.OnUseResult += new EventHandler(dataElementCreationControl_OnUseResult);
        }

        void scControl_OnUse(object sender, EventArgs e)
        {
            if (dataElementCreationControl.LastResult == null)
            {
                MessageBox.Show("Error inserting data element");
                return;
            }
            XElement selectedNode = XElement.Parse(dataElementCreationControl.LastResult);
            handleCDE(selectedNode);
        }
        /*
        void dataElementCreationControl_OnUseResult(object sender, EventArgs e)
        {
            if (dataElementCreationControl.LastResult == null)
            {
                MessageBox.Show("Error inserting data element");
                return;
            }
            XElement selectedNode = XElement.Parse(dataElementCreationControl.LastResult);
            handleCDE(selectedNode);
        }
        */
        protected void handleCDE(XElement selectedNode)
        {
            string id = selectedNode.Element("names").Element("id").Value;
            string preferredName = selectedNode.Element("names").Element("preferred").Value;
            string preferredNameTag = preferredName.Replace(" ", "_");
            string definition = selectedNode.Element("definition").Value;
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

            Excel.Workbook workbook = (Excel.Workbook)application.ActiveWorkbook;
            Excel.Range selected = (Excel.Range)application.Selection;

            Excel.XmlMap xmlMap = null;

            string attr = "";
            string attrWithDef = "";

            if (selectedNode.Element("values").Element("enumerated") != null)
            {

                var validValues = from vv in selectedNode.Element("values").Element("enumerated").Elements("valid-value")
                                  select new
                                  {
                                      Code = vv.Element("code").Value,
                                      Meaning = vv.Element("meaning").Value
                                  };

                XNamespace xs = "http://www.w3.org/2001/XMLSchema";
                XDocument x =
                    new XDocument(
                        new XDeclaration("1.0", "utf-8", "yes"),
                        new XElement(xs + "schema",
                            new XAttribute(XNamespace.Xmlns + "xs", "http://www.w3.org/2001/XMLSchema"),
                            new XAttribute("xmlns", "http://cancergrid.org/schema/result-set"),
                            new XAttribute("targetNamespace", "http://cancergrid.org/schema/result-set"),
                            new XElement(xs + "element",
                                new XAttribute("name", preferredNameTag + "List"),
                                new XAttribute("type", preferredNameTag + "List")
                                ),
                            new XElement(xs + "complexType",
                                new XAttribute("name", preferredNameTag + "List"),
                                new XElement(xs + "sequence",
                                    new XElement(xs + "element",
                                        new XAttribute("ref", preferredNameTag),
                                        new XAttribute("minOccurs", "0"),
                                        new XAttribute("maxOccurs", "unbounded")
                                        )
                                    )
                                ),
                            new XElement(xs + "element",
                                new XAttribute("name", preferredNameTag),
                                new XAttribute("type", preferredNameTag)
                            ),
                            new XElement(xs + "simpleType",
                                new XAttribute("name", preferredNameTag),
                                new XElement(xs + "restriction",
                                    new XAttribute("base", "xs:string")
                                    )
                                )
                            )
                        );

                XElement restrictionNode = x.Element(xs + "schema").Element(xs + "simpleType").Element(xs + "restriction");

                foreach (var vv in validValues)
                {
                    XElement enumNode = new XElement(xs + "enumeration",
                        new XAttribute("value", vv.Code),
                        new XElement(xs + "annotation", vv.Meaning)
                        );
                    restrictionNode.Add(enumNode);

                    attr += vv.Code + ",";
                    attrWithDef += vv.Code + " : (" + vv.Meaning.Replace(",", "&#44;").Replace("&lt;", "<").Replace("&gt;", ">") + "),";

                }

                if (attr != null && attr.Contains(","))
                {
                    attr.Remove(attr.LastIndexOf(','));
                    attr.Trim();
                }
                try
                {
                    xmlMap = workbook.XmlMaps.Add(x.ToString(), (selected.Count > 1) ? preferredNameTag + "List" : preferredNameTag);
                    xmlMap.ShowImportExportValidationErrors = true;
                    xmlMap.AdjustColumnWidth = true;
                    selected.XPath.SetValue(xmlMap, (selected.Count > 1) ? "/rs:" + preferredNameTag + "List/rs:" + preferredNameTag : "/rs:" + preferredNameTag, "xmlns:rs=\"" + rs.NamespaceName + "\"", selected.Count > 1);
                }
                catch (ArgumentException ex)
                {
                    MessageBox.Show(ex.Message, "Range Selection Error");
                    xmlMap.Delete();
                    return;
                }
                
                selected.Validation.Delete();

                //xs:enumeration
                selected.Validation.Add(Excel.XlDVType.xlValidateList, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, attr, attr);
                selected.Validation.InputTitle = "Select a value from the list";
                selected.Validation.InputMessage = attrWithDef.Replace(",", "\n");
                selected.Validation.ErrorMessage = "Please select a value from the enumeration list.";
            }
            else if (selectedNode.Element( "values").Element( "non-enumerated") != null)
            {
                attr = selectedNode.Element( "values").Element( "non-enumerated").Element( "data-type").Value;
                attrWithDef = "data-type: " + selectedNode.Element("values").Element("non-enumerated").Element("data-type").Value + "\nUnits: " + selectedNode.Element("values").Element("non-enumerated").Element("units").Value;

                XNamespace xs = "http://www.w3.org/2001/XMLSchema";
                XDocument x =
                    new XDocument(
                        new XDeclaration("1.0", "utf-8", "yes"),
                        new XElement(xs + "schema",
                            new XAttribute(XNamespace.Xmlns + "xs", "http://www.w3.org/2001/XMLSchema"),
                            new XAttribute("xmlns", "http://cancergrid.org/schema/result-set"),
                            new XAttribute("targetNamespace", "http://cancergrid.org/schema/result-set"),
                            new XElement(xs + "element",
                                new XAttribute("name", preferredNameTag + "List"),
                                new XAttribute("type", preferredNameTag + "List")
                                ),
                            new XElement(xs + "complexType",
                                new XAttribute("name", preferredNameTag + "List"),
                                new XElement(xs + "sequence",
                                    new XElement(xs + "element",
                                        new XAttribute("ref", preferredNameTag),
                                        new XAttribute("minOccurs", "0"),
                                        new XAttribute("maxOccurs", "unbounded")
                                        )
                                    )
                                ),
                            new XElement(xs + "element",
                                new XAttribute("name", preferredNameTag),
                                new XAttribute("type", preferredNameTag)
                            ),
                            new XElement(xs + "simpleType",
                                new XAttribute("name", preferredNameTag),
                                new XElement(xs + "annotation", definition),
                                new XElement(xs + "restriction",
                                    new XAttribute("base", attr)
                                    )
                                )
                            )
                        );
                try
                {
                    xmlMap = workbook.XmlMaps.Add(x.ToString(), (selected.Count > 1) ? preferredNameTag + "List" : preferredNameTag);
                    xmlMap.ShowImportExportValidationErrors = true;
                    xmlMap.AdjustColumnWidth = true;
                    selected.XPath.SetValue(xmlMap, (selected.Count > 1) ? "/rs:" + preferredNameTag + "List/rs:" + preferredNameTag : "/rs:" + preferredNameTag, "xmlns:rs=\"" + rs.NamespaceName + "\"", selected.Count > 1);
                }
                catch (ArgumentException ex)
                {
                    MessageBox.Show(ex.Message, "Range Selection Error");
                    xmlMap.Delete();
                    return;
                }

                selected.Validation.Delete();
                if (attr == "xs:nonNegativeInteger" || attr == "xs:positiveInteger")
                {
                    //xs:nonNegativeInteger
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlGreaterEqual, 0, 0);
                    selected.Validation.InputTitle = "Enter a positive integer";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please enter a positive number";
                }
                else if (attr == "xs:nonPositiveInteger")
                {
                    //xs:integer
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlLessEqual, 0, 0);
                    selected.Validation.InputTitle = "Enter a negative integer";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please enter a negative number";
                }
                else if (attr == "xs:positiveInteger")
                {
                    //xs:integer
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlGreater, 0, 0);
                    selected.Validation.InputTitle = "Enter an integer number greater than 0(zero)";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please enter an integer number 0(zero)";
                }
                else if (attr == "xs:integer")
                {
                    //xs:integer
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Int32.MinValue, Int32.MaxValue);
                    selected.Validation.InputTitle = "Enter an integer number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please enter an integer number";
                }
                else if (attr == "xs:int")
                {
                    //xs:int
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Int32.MinValue, Int32.MaxValue);
                    selected.Validation.InputTitle = "Enter an integer number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please enter an integer number";
                }
                else if (attr == "xs:long")
                {
                    //xs:long
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Int64.MinValue, Int64.MaxValue);
                    selected.Validation.InputTitle = "Enter an integer number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please enter an integer number";
                }
                else if (attr == "xs:date")
                {
                    //xs:date
                    selected.Validation.Add(Excel.XlDVType.xlValidateDate, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlGreaterEqual, "=DATE(1900,1,1)", "=DATE(1900,1,1)");
                    selected.Validation.InputTitle = "Enter a date";
                    selected.Validation.InputMessage = "Date format: =DATE(year,month,day)";
                    selected.Validation.ErrorMessage = "Please enter correct date format: =DATE(year,month,day)";
                }
                else if (attr == "xs:boolean")
                {
                    //xs:boolean
                    selected.Validation.Add(Excel.XlDVType.xlValidateList, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, "true,false", "true,false");
                    selected.Validation.InputTitle = "Select True or False";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a boolean value from the list";
                }
                else if (attr == "xs:double")
                {
                    //xs:double
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:float")
                {
                    //xs:float
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Single.MinValue, Single.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:unsignedInt")
                {
                    //xs:boolean
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, UInt32.MinValue, UInt32.MaxValue);
                    selected.Validation.InputTitle = "Enter an unsigned integer number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select an unsigned integer number";
                }
                else if (attr == "xs:unsignedLong")
                {
                    //xs:boolean
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, UInt64.MinValue, UInt64.MaxValue);
                    selected.Validation.InputTitle = "Enter an unsigned integer number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select an unsigned integer number";
                }
                else if (attr == "xs:unsignedShort")
                {
                    //xs:boolean
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, UInt16.MinValue, UInt16.MaxValue);
                    selected.Validation.InputTitle = "Enter an unsigned integer number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select an unsigned integer number";
                }
                /*
                else if (attr == "xs:duration")
                {
                    //xs:duration
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:dateTime")
                {
                    //xs:dateTime
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:time")
                {
                    //xs:time
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:gYearMonth")
                {
                    //xs:gYearMonth
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:gYear")
                {
                    //xs:gYear
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:gMonthDay")
                {
                    //xs:gMonthDay
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:gDay")
                {
                    //xs:gDay
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:gMonth")
                {
                    //xs:gMonth
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                else if (attr == "xs:anyURI")
                {
                    //xs:anyURI
                    selected.Validation.Add(Excel.XlDVType.xlValidateDecimal, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Decimal.MinValue, Decimal.MaxValue);
                    selected.Validation.InputTitle = "Enter a decimal number";
                    selected.Validation.InputMessage = " ";
                    selected.Validation.ErrorMessage = "Please select a decimal number";
                }
                */
            }


            selected.Name = xmlMap.Name;

            //Create CDE list is not exist
            if (!isCDEListExists())
            {
                createCDEList();
            }

            //Add new CDE entry to cde_list
            cdeList.Unprotect(dummyPass);
            Excel.Range c = (Excel.Range)cdeList.Cells[2, 1];

            for (int i = 3; c.Value2 != null; i++)
            {
                c = (Excel.Range)cdeList.Cells[i, 1];
            }

            string instanceNum = xmlMap.Name.Substring(xmlMap.Name.LastIndexOf("_Map") + 4);
            c.Hyperlinks.Add(c, "", xmlMap.Name, Type.Missing, id + ((selected.Count > 1) ? "(List)" : "(Single)") + ((instanceNum != null && instanceNum.Length > 0) ? "(" + instanceNum + ")" : "") + "\n\nRange: " + getSelectedRangeAddress(selected));
            c.Next.Value2 = preferredName;
            c.Next.Next.Value2 = definition.Trim().Replace("&gt;", ">").Replace("&lt;", "<").Replace("&amp;", "&");
            c.Next.Next.Next.Value2 = attrWithDef.Trim().Replace(",", "\n\n").Replace("&#44;", ", ").Replace("&gt;", ">").Replace("&lt;", "<").Replace("&amp;", "&");
            cdeList.Protect(dummyPass, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing);

            // Create/Format Header
            if (selected.Count > 1)
            {
                Excel.Range firstCell = (Excel.Range)selected.Cells[1, 1];
                firstCell.Validation.Delete();
                firstCell.Value2 = ((string)firstCell.Value2).Substring(((string)firstCell.Value2).IndexOf(":") + 1).Replace("_", " ");
                firstCell.Hyperlinks.Add(firstCell, "", getSelectedRangeAddress(c), Type.Missing, firstCell.Value2);
                firstCell.Font.ColorIndex = 2;
                firstCell.Font.Underline = false;
            }
            else if (selected.Count == 1)
            {
                selected.Interior.ThemeColor = Excel.XlThemeColor.xlThemeColorAccent1;
                selected.Interior.TintAndShade = 0.6;

                if (selected.Column != 1)
                {
                    try
                    {
                        Excel.Range leftCell = selected.get_Offset(0, -1);
                        if (leftCell != null && (leftCell.Value2 == null || ((string)leftCell.Value2).Length == 0))
                        {
                            leftCell.Value2 = preferredName;
                            leftCell.Hyperlinks.Add(leftCell, "", getSelectedRangeAddress(c), Type.Missing, leftCell.Value2);
                            leftCell.Font.Bold = true;
                            leftCell.Font.Underline = false;
                            leftCell.Font.ColorIndex = 1;
                            return;
                        }

                        Excel.Range upCell = selected.get_Offset(-1, 0);
                        if (upCell != null && (upCell.Value2 == null || ((string)upCell.Value2).Length == 0))
                        {
                            upCell.Value2 = preferredName;
                            upCell.Hyperlinks.Add(upCell, "", getSelectedRangeAddress(c), Type.Missing, upCell.Value2);
                            upCell.Font.Bold = true;
                            upCell.Font.Underline = false;
                            upCell.Font.ColorIndex = 1;
                            return;
                        }
                    }
                    catch (Exception)
                    {
                        //Unable to get valid cell
                        MessageBox.Show("Unable to find suitable location to insert header/label", "Header/Label not created");
                    }
                }
                else
                {
                    try
                    {
                        Excel.Range upCell = selected.get_Offset(-1, 0);
                        if (upCell != null && (upCell.Value2 == null || ((string)upCell.Value2).Length == 0))
                        {
                            upCell.Value2 = preferredName;
                            upCell.Hyperlinks.Add(upCell, "", getSelectedRangeAddress(c), Type.Missing, upCell.Value2);
                            upCell.Font.Bold = true;
                            upCell.Font.Underline = false;
                            upCell.Font.ColorIndex = 1;
                            return;
                        }
                    }
                    catch (Exception)
                    {
                        MessageBox.Show("Unable to find suitable location to insert header/label", "Header/Label not created");
                    }
                }
            }
        }

        protected string getSelectedRangeAddress(Excel.Range r)
        {
            return ((Excel.Worksheet)r.Parent).Name + "!" + r.get_Address(Type.Missing, Type.Missing, Excel.XlReferenceStyle.xlA1, Type.Missing, Type.Missing);
        }

        private bool isCDEListExists()
        {
            try
            {
                cdeList = (Excel.Worksheet)application.Worksheets.get_Item("cde_list");
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        private void createCDEList()
        {
            Excel.Worksheet origin = (Excel.Worksheet)application.ActiveSheet;
            cdeList = (Excel.Worksheet)application.ActiveWorkbook.Worksheets.Add(Type.Missing, application.ActiveSheet, Type.Missing, Type.Missing);
            cdeList.Name = "cde_list";

            Excel.Range column = ((Excel.Range)cdeList.Cells[1, 1]);

            //ID
            column.Value2 = "ID";
            column.Font.Bold = true;
            column.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
            column.EntireColumn.ColumnWidth = 25;
            column.EntireColumn.WrapText = true;
            column.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;

            //Name
            column.Next.Value2 = "Name";
            column.Next.Font.Bold = true;
            column.Next.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
            column.Next.EntireColumn.ColumnWidth = 30;
            column.Next.EntireColumn.WrapText = true;
            column.Next.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;

            //Definition
            column.Next.Next.Value2 = "Definition";
            column.Next.Next.Font.Bold = true;
            column.Next.Next.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
            column.Next.Next.EntireColumn.ColumnWidth = 60;
            column.Next.Next.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;
            column.Next.Next.EntireColumn.WrapText = true;
            column.Next.Next.EntireColumn.HorizontalAlignment = Excel.XlHAlign.xlHAlignJustify;

            //Properties/Values
            column.Next.Next.Next.Value2 = "Properties/Values";
            column.Next.Next.Next.Font.Bold = true;
            column.Next.Next.Next.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
            column.Next.Next.Next.EntireColumn.ColumnWidth = 50;
            column.Next.Next.Next.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;
            column.Next.Next.Next.EntireColumn.WrapText = true;
            column.Next.Next.Next.EntireColumn.HorizontalAlignment = Excel.XlHAlign.xlHAlignJustify;

            cdeList.Protect(dummyPass, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing);

            //((Excel.Worksheet)application.Worksheets.get_Item(origin.Name)).Activate();
            (origin as Microsoft.Office.Interop.Excel._Worksheet).Activate();
        }
/*
        private void insertCDE(Excel.Application application)
        {
            Excel.Worksheet cdeList = null;

            //Applying validation rules based on selected CDE
            XmlDocument xDoc = new XmlDocument();
            xDoc.LoadXml(dataElementCreationControl.LastResult);
            XmlNode srcNode = xDoc.DocumentElement.SelectSingleNode("/data-element");
            XmlNode names = srcNode.SelectSingleNode("names");
            XmlNode attributes = srcNode.SelectSingleNode("values");
            XmlNodeList validValues = attributes.SelectNodes("enumerated/valid-value");
            string attr = "";

            application.ActiveCell.Validation.Delete();
            string id = names.SelectSingleNode("id").InnerXml;
            string preferred = names.SelectSingleNode("preferred").InnerXml;
            string definition = srcNode.SelectSingleNode("definition").InnerXml;

            Excel.Range selected = (Excel.Range)application.Selection;
            if (validValues != null && validValues.Count != 0)
            {
                foreach (XmlNode n in validValues)
                {
                    attr += n.SelectSingleNode("code").InnerXml + " : (" + n.SelectSingleNode("meaning").InnerXml.Replace(",", "&#44;") + "),";
                }
                attr.Remove(attr.LastIndexOf(','));
                attr.Trim();

                //xs:enumeration
                selected.Validation.Add(Excel.XlDVType.xlValidateList, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, attr, attr);
                //application.ActiveCell.Validation.InputTitle = name.InnerXml;
                //application.ActiveCell.Validation.ErrorTitle = name.InnerXml;
                selected.Validation.InputMessage = "Select a value from the list.";
                selected.Validation.ErrorMessage = "You must select a value from the enumeration list.";
            }
            else
            {
                attr = attributes.SelectSingleNode("non-enumerated/data-type").InnerXml;
                if (attr == "xs:nonNegativeInteger")
                {
                    //xs:nonNegativeInteger
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlGreaterEqual, 0, 0);
                    //application.ActiveCell.Validation.InputTitle = name.InnerXml;
                    //application.ActiveCell.Validation.ErrorTitle = name.InnerXml;
                    selected.Validation.InputMessage = "Enter a positive integer";
                    selected.Validation.ErrorMessage = "You must enter a positive number";
                }
                else if (attr == "xs:integer")
                {
                    //xs:integer
                    selected.Validation.Add(Excel.XlDVType.xlValidateWholeNumber, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, Int32.MinValue, Int32.MaxValue);
                    //application.ActiveCell.Validation.InputTitle = name.InnerXml;
                    //application.ActiveCell.Validation.ErrorTitle = name.InnerXml;
                    selected.Validation.InputMessage = "Enter an integer";
                    selected.Validation.ErrorMessage = "You must enter an integer number";
                }
                else if (attr == "xs:date")
                {
                    //xs:date
                    selected.Validation.Add(Excel.XlDVType.xlValidateDate, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlGreaterEqual, "=DATE(1900,1,1)", "=DATE(1900,1,1)");
                    //application.ActiveCell.Validation.InputTitle = name.InnerXml;
                    //application.ActiveCell.Validation.ErrorTitle = name.InnerXml;
                    selected.Validation.InputMessage = "Enter a date";
                    selected.Validation.ErrorMessage = "You must enter correct date format";
                }
                else if (attr == "xs:boolean")
                {
                    //xs:boolean
                    selected.Validation.Add(Excel.XlDVType.xlValidateList, Excel.XlDVAlertStyle.xlValidAlertStop, Excel.XlFormatConditionOperator.xlBetween, "true,false", "true,false");
                    //application.ActiveCell.Validation.InputTitle = name.InnerXml;
                    //application.ActiveCell.Validation.ErrorTitle = name.InnerXml;
                    selected.Validation.InputMessage = "Select true or false";
                    selected.Validation.ErrorMessage = "You must select a boolean value";
                }
            }
            if (selected.Count > 1 && selected.Areas.Count == 1)
            {
                Excel.Range firstCell = ((Excel.Range)selected.Cells[1, 1]);
                firstCell.Value2 = preferred;
                firstCell.Font.Bold = true;
                selected.ColumnWidth = firstCell.Value2.ToString().Length;
                firstCell.Validation.Delete();
            }

            //Create CDE list
            try
            {
                cdeList = (Excel.Worksheet)application.Worksheets.get_Item("cde_list");
            }
            catch (Exception)
            {
                Excel.Worksheet origin = (Excel.Worksheet)application.ActiveSheet;
                cdeList = (Excel.Worksheet)application.ActiveWorkbook.Worksheets.Add(Type.Missing, application.ActiveSheet, Type.Missing, Type.Missing);
                cdeList.Name = "cde_list";

                Excel.Range column = ((Excel.Range)cdeList.Cells[1, 1]);

                //ID
                column.Value2 = "ID";
                column.Font.Bold = true;
                column.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
                column.EntireColumn.ColumnWidth = 25;
                column.EntireColumn.WrapText = true;
                column.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;

                //Name
                column.Next.Value2 = "Name";
                column.Next.Font.Bold = true;
                column.Next.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
                column.Next.EntireColumn.ColumnWidth = 30;
                column.Next.EntireColumn.WrapText = true;
                column.Next.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;

                //Definition
                column.Next.Next.Value2 = "Definition";
                column.Next.Next.Font.Bold = true;
                column.Next.Next.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
                column.Next.Next.EntireColumn.ColumnWidth = 60;
                column.Next.Next.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;
                column.Next.Next.EntireColumn.WrapText = true;
                column.Next.Next.EntireColumn.HorizontalAlignment = Excel.XlHAlign.xlHAlignJustify;

                //Properties/Values
                column.Next.Next.Next.Value2 = "Properties/Values";
                column.Next.Next.Next.Font.Bold = true;
                column.Next.Next.Next.Font.Background = Excel.XlBackground.xlBackgroundOpaque;
                column.Next.Next.Next.EntireColumn.ColumnWidth = 50;
                column.Next.Next.Next.EntireColumn.VerticalAlignment = Excel.XlVAlign.xlVAlignTop;
                column.Next.Next.Next.EntireColumn.WrapText = true;
                column.Next.Next.Next.EntireColumn.HorizontalAlignment = Excel.XlHAlign.xlHAlignJustify;

                //((Excel.Worksheet)application.Worksheets.get_Item(origin.Name)).Activate();
                (origin as Microsoft.Office.Interop.Excel._Worksheet).Activate();
            }
            cdeList.Unprotect("dummy_password");
            Excel.Range c = (Excel.Range)cdeList.Cells[2, 1];

            for (int i = 3; c.Value2 != null; i++)
            {
                c = (Excel.Range)cdeList.Cells[i, 1];
            }

            c.Value2 = id;
            c.Next.Value2 = preferred;
            c.Next.Next.Value2 = definition.Trim().Replace("&gt;", ">").Replace("&lt;", "<").Replace("&amp;", "&");
            c.Next.Next.Next.Value2 = attr.Trim().Replace(",", "\n\n").Replace("&#44;", ", ").Replace("&gt;", ">").Replace("&lt;", "<").Replace("&amp;", "&");
            cdeList.Protect("dummy_password", Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing, Type.Missing);
        }
 */ 
    }
}
