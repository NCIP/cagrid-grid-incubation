CancerGrid Web Service Interface to Vocabulary and Metadata Services
====================================================================

This document assumes all the package file is located in directory refer to as <QueryService>.

File Structures
---------------
<QueryService>
|
|- resources
   |
   |- QueryServiceManager.wsdl (WSDL for the web service)
   |
   |- query.xsd (XSD defining the structure of a query parameter used in QueryServiceManager.wsdl)
   |
   |- result-set.xsd (XSD defining the structure of query results used in QueryServiceManager.wsdl)
   |
   |- services.xml (Axis2 web service descriptor)
   |
   |- *.xsl (XSL used to patch QueryServiceManager.wsdl during build for compatibility with InfoPath 2007)
|
|- src (Source code)
|
|- stylesheets (stylesheets for query processing)
|
|- build.xml (Ant build script for building the web service)
|
|- dist.properties (configuration properties for generating distributable package for the web service)
|
|- config.xml (configuration for query services)


Pre-Requisites
--------------
Java 5.0, Ant 1.7.0 or above, Tomcat 5.5.x or above, Axis2 1.3 or above, Saxon 8.9 or above


Quick Start
-----------

Creating an Axis2 AAR build:

(1) In a command console at <QueryService>, execute "ant dist-aar"
(2) Deploy the <QueryService>/metadata-connector-<version>.aar file into an existing Axis2 installation
    - Note: the Axis2 host must have Saxon (8.9 or above) jars in <axis2>/WEB-INF/lib. The
      jars required are - 

	Saxon8 - saxon8.jar, saxon8-dom.jar, and saxon8-xpath.jar, or

	Saxon9 - saxon9.jar, saxon9-dom.jar, and saxon9-xpath.jar

(3) Copy <QueryService>/config.xml into <axis2>/WEB-INF/classes

After deploying the web service:

(4) Check that the web service is running by openning a web browser and navigate to

    - http://<host>:<port>/<axis2_installation>/services/QueryServiceManager?wsdl
	
(5) Run a few queries to test it

    - http://<host>:<port>/<axis2_installation>/services/QueryServiceManager/listResources

Javadoc
=======

In a command console at <QueryService>, execute "ant javadoc"
