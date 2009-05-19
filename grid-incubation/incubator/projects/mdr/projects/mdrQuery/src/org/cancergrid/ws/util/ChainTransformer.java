/**
 * Copyright (c) 2005-2008 CancerGrid Consortium <http://www.cancergrid.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,  
 * and to permit persons to whom the Software is furnished to do so, subject to the 
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies 
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package org.cancergrid.ws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * This is a utility class to help construct a chain of XSLT templates to be applied to
 * an input XML one after another in sequence.
 * 
 * @author <a href="mailto:Andrew.Tsui@comlab.ox.ac.uk">Andrew Tsui</a> (<a href="http://www.cancergrid.org">CancerGrid Consortium</a>)
 * @version 1.1
 */
public class ChainTransformer 
{
	/**
	 * Log4J Logger
	 */
	private static Logger LOG = Logger.getLogger(ChainTransformer.class);
	
	/**
	 * container to store XSLT templates
	 */
	protected Map<String, Templates> chain = null;
	
	private static SAXTransformerFactory factory = null; 
	
	/**
	 * Constructor. Initialises the transform chain
	 *
	 */
	public ChainTransformer()
	{
		if (factory == null)
		{
			try {
				factory = (SAXTransformerFactory)Class.forName("net.sf.saxon.TransformerFactoryImpl").newInstance();
			} catch (Exception e) {
				LOG.error("Unable to create Saxon transformer factory. Using default transformer factory instead.");
				factory = (SAXTransformerFactory)TransformerFactory.newInstance();
			}
		}
		chain = new Hashtable<String, Templates>();
	}
	
	/**
	 * Add a template into the chain
	 * @param label used to reference the templates in the chain
	 * @param t the template to add
	 */
	public void addTemplate(String label, Templates t)
	{
		chain.put(label, t);
	}
	
	/**
	 * Create a template from the given XSLT file and add it into the chain
	 * 
	 * @param label used to reference the template in the chain
	 * @param xsl_path path to an XSLT file
	 */
	public void addTemplate(String label, String xsl_path)
	{
		try 
		{
			chain.put(label, createTemplate(xsl_path));
		} 
		catch (Exception e)
		{
			LOG.error("Unable to add template to chain.");
		}
	}
	
	/**
	 * Remove the specified template from the chain
	 * 
	 * @param label used to reference the template in the chain
	 */
	public void deleteTemplate(String label)
	{
		chain.remove(label);
	}
	
	/**
	 * Get a reference to a template in the chain
	 * @param label
	 * @return template specified
	 */
	public Templates getTemplate(String label)
	{
		return chain.get(label);
	}
	
	/**
	 * Apply the transform chain in default sequence to a given XML
	 * 
	 * @param input XML to transform
	 * @return result of the XSLT transforms
	 */
	public String applyTemplates(String input)
	{
		try {
			String buffer = input;
			for (String label : chain.keySet())
			{
				buffer = transform(buffer, chain.get(label));
			}
			return buffer;
		} catch (Exception e)
		{
			LOG.error("applyTemplate: "+e);
			return null;
		}
	}
	
	/**
	 * Apply the transform chain in custom sequence to a given XML. This is useful for
	 * controlling the stages of transform. e.g. for inspecting intermediate transform 
	 * results.
	 * 
	 * @param input XML to transform
	 * @param sequence The sequence of templates to apply
	 * @return result of the XSLT transforms
	 */
	public String applyTemplates(String input, List<String> sequence)
	{
		try {
			String buffer = input;
			LOG.debug("applyTemplates (start): "+buffer);
			if (buffer.startsWith("&lt;result-set"))
			{
				buffer = buffer.replace("&lt;", "<");
				buffer = buffer.replace("&gt;", ">");
			}
			
			for (String label : sequence)
			{
				LOG.debug("Label: "+label);
				if (chain.get(label) == null)
					break;
				buffer = transform(buffer, chain.get(label));
				LOG.debug(buffer);
			}
			return buffer;
		} catch (Exception e)
		{
			LOG.error("ChainTransformer.applyTemplates: " + e);
			return null;
		}
	}
	
	/**
	 * Clear the chain
	 *
	 */
	public void clearChain()
	{
		chain.clear();
	}
	
	/**
	 * Convenient method to do a single transform.
	 * 
	 * @param input XML to transform
	 * @param template template to use
	 * @return result of the XSLT transforms
	 * @throws Exception 
	 */
	public static String transform(String input, Templates template) throws Exception
	{
		//if (System.getProperty("javax.xml.transform.TransformerFactory") == null || !System.getProperty("javax.xml.transform.TransformerFactory").equals("net.sf.saxon.TransformerFactoryImpl"))
		//	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		StringWriter output = new StringWriter();
		Source source = new StreamSource(new StringReader(input));
		Result result = new StreamResult(output);
		Transformer transformer = template.newTransformer();
		transformer.transform(source, result);
		transformer.clearParameters();
		transformer.reset();
		return output.toString();
	}
	
	/**
	 * Convenient method to do a single transform.
	 * 
	 * @param n an XML DOM node
	 * @param template template to use
	 * @return result of the XSLT transforms
	 */
	public static String transform(Node n, Templates template) throws Exception
	{
		//if (System.getProperty("javax.xml.transform.TransformerFactory") == null || !System.getProperty("javax.xml.transform.TransformerFactory").equals("net.sf.saxon.TransformerFactoryImpl"))
		//	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		StringWriter output = new StringWriter();
		Source source = new DOMSource(n);
		Result result = new StreamResult(output);
		Transformer transformer = template.newTransformer();
		transformer.transform(source, result);
		return output.toString();
	}
	
	/**
	 * Convenient method to do a single transform.
	 * 
	 * @param xml XML to transform
	 * @param xsl XSL to use
	 * @return result of the XSLT transforms
	 * @throws Exception 
	 */
	public static String transform(String xml, String xsl) throws Exception
	{
		//if (System.getProperty("javax.xml.transform.TransformerFactory") == null || !System.getProperty("javax.xml.transform.TransformerFactory").equals("net.sf.saxon.TransformerFactoryImpl"))
		//	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		if (factory == null)
		{
			try {
				factory = (SAXTransformerFactory)Class.forName("net.sf.saxon.TransformerFactoryImpl").newInstance();
			} catch (Exception e) {
				LOG.error("Unable to create Saxon transformer factory. Using default transformer factory instead.");
				factory = (SAXTransformerFactory)TransformerFactory.newInstance();
			}
		}
		StringWriter output = new StringWriter();
		Source source = new StreamSource(new StringReader(xml));
		Result result = new StreamResult(output);
		//TransformerFactory factory = TransformerFactory.newInstance();
		Templates template = factory.newTemplates(new StreamSource(new StringReader(xsl)));
		Transformer transform = template.newTransformer();
		transform.transform(source, result);
		return output.toString();
	}   
	
	/**
	 * Convenient method to create a new transformer from a given file path
	 * 
	 * @param xsl_path path to XSLT file
	 * @return a template created using the given XSLT
	 */
	public static Templates createTemplate(String xsl_path) throws Exception
	{
		//if (System.getProperty("javax.xml.transform.TransformerFactory") == null || !System.getProperty("javax.xml.transform.TransformerFactory").equals("net.sf.saxon.TransformerFactoryImpl"))
		//	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		//TransformerFactory factory = TransformerFactory.newInstance();
		if (factory == null)
		{
			try {
				factory = (SAXTransformerFactory)Class.forName("net.sf.saxon.TransformerFactoryImpl").newInstance();
			} catch (Exception e) {
				LOG.error("Unable to create Saxon transformer factory. Using default transformer factory instead.");
				factory = (SAXTransformerFactory)TransformerFactory.newInstance();
			}
		}
		Templates template = factory.newTemplates(new StreamSource(new FileInputStream(xsl_path)));
		return template;
	}
	
	/**
	 * Convenient method to create a new transformer from a given file path
	 * 
	 * @param xsl XSLT file
	 * @return a template created using the given XSLT
	 */
	public static Templates createTemplate(File xsl) throws Exception
	{
		//if (System.getProperty("javax.xml.transform.TransformerFactory") == null || !System.getProperty("javax.xml.transform.TransformerFactory").equals("net.sf.saxon.TransformerFactoryImpl"))
		//	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		//TransformerFactory factory = TransformerFactory.newInstance();
		if (factory == null)
		{
			try {
				factory = (SAXTransformerFactory)Class.forName("net.sf.saxon.TransformerFactoryImpl").newInstance();
			} catch (Exception e) {
				LOG.error("Unable to create Saxon transformer factory. Using default transformer factory instead.");
				factory = (SAXTransformerFactory)TransformerFactory.newInstance();
			}
		}
		Templates template = factory.newTemplates(new StreamSource(new FileInputStream(xsl)));
		return template;
	}
	
	/**
	 * Convenient method to create a new transformer from a given file path
	 * 
	 * @param xsl input stream to XSLT content
	 * @return a template created using the given XSLT
	 */
	public static Templates createTemplate(InputStream xsl) throws Exception
	{
		//if (System.getProperty("javax.xml.transform.TransformerFactory") == null || !System.getProperty("javax.xml.transform.TransformerFactory").equals("net.sf.saxon.TransformerFactoryImpl"))
		//	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		//TransformerFactory factory = TransformerFactory.newInstance();
		if (factory == null)
		{
			try {
				factory = (SAXTransformerFactory)Class.forName("net.sf.saxon.TransformerFactoryImpl").newInstance();
			} catch (Exception e) {
				LOG.error("Unable to create Saxon transformer factory. Using default transformer factory instead.");
				factory = (SAXTransformerFactory)TransformerFactory.newInstance();
			}
		}
		Templates template = factory.newTemplates(new StreamSource(xsl));
		return template;
	}
	
	/**
	 * Convenient method to create a new transformer from a given file path
	 * 
	 * @param xsl XSLT content
	 * @return a template created using the given XSLT
	 */
	public static Templates createTemplate(StringReader xsl) throws Exception
	{
		//if (System.getProperty("javax.xml.transform.TransformerFactory") == null || !System.getProperty("javax.xml.transform.TransformerFactory").equals("net.sf.saxon.TransformerFactoryImpl"))
		//	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		//TransformerFactory factory = TransformerFactory.newInstance();
		if (factory == null)
		{
			try {
				factory = (SAXTransformerFactory)Class.forName("net.sf.saxon.TransformerFactoryImpl").newInstance();
			} catch (Exception e) {
				LOG.error("Unable to create Saxon transformer factory. Using default transformer factory instead.");
				factory = (SAXTransformerFactory)TransformerFactory.newInstance();
			}
		}
		Templates template = factory.newTemplates(new StreamSource(xsl));
		return template;
	}
}
