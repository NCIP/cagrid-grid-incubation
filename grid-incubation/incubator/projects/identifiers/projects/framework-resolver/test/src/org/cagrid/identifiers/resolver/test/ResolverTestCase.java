package org.cagrid.identifiers.resolver.test;

/*
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.identifiers.core.IdentifierValues;
import org.cagrid.identifiers.resolver.ResolverUtil;

import junit.framework.TestCase;

public class ResolverTestCase extends TestCase {

	private static Log log = LogFactory.getLog(ResolverTestCase.class);
	
	private String purl = "http://purl.cagrid.org:8090/cagrid/3c70f038-8773-4749-a5ea-37f5246d609d";

	public void testGridResolution() {
		try {
			IdentifierValues ivs = ResolverUtil.resolveGrid(purl);
			System.out.println(ivs.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testHttpResolution() {
		try {
			IdentifierValues ivs = ResolverUtil.resolveHttp(purl);
			System.out.println(ivs.toString());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ResolverTestCase.class);
	}
}

