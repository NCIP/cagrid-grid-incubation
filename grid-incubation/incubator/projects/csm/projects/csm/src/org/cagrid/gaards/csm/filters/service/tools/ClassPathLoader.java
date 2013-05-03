/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.csm.filters.service.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Vector;

import org.hibernate.SessionFactory;

public class ClassPathLoader {

	private static final Class<?>[] parameters = new Class[] { URL.class };

	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}// end method

	public static void addFile(File f) throws IOException {
		addURL(f.toURI().toURL());
	}// end method

	public static void addURL(URL u) throws IOException {

		URLClassLoader sysloader = (URLClassLoader) Thread.currentThread()
				.getContextClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			throw new IOException(
					"Error, could not add URL to system classloader");
		}

	}
	
	public static void releaseJarsFromClassPath(SessionFactory sf, List<File> fileArray){
		
	    if(sf!=null){
	    	sf.close();
	    	sf = null;
	    }
	   
	    Vector<String> v = new Vector<String>();
	    URLClassLoader sysloader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
	 	ClassLoaderUtil.releaseLoader(sysloader,v);
	    
//	    if(fileArray!=null){
//	       
//		    for(int i=0; i<fileArray.size();i++){
//		    	File file = fileArray.get(i);
//		    	if(file!=null && file.exists()){
//		    		file.delete();
//		    		file = null;
//		    	}
//				
//			}
//	    }
		
	}

}
