Introduce Generated Service Skeleton:
======================================
This is an Introduce generated service.  

All that is needed for this service at this point is to populate the 
service side implementation in the <service package>.service.<service name>Impl.java

Prerequisites:
=======================================
Java 1.5 and JAVA_HOME env defined
Ant 1.7.0 and ANT_HOME env defined
Globus 4.0.3 installed and GLOBUS_LOCATION env defined
(optional) Tomcat 5.5.27 installed and "CATALINA_HOME" env defined with Globus deployed to it
(optional) JBoss 4.0.5.GA installed and "JBOSS_HOME" env defined with Globus deployed to it


To Build:
=======================================
"ant all" will build

"ant deployGlobus" will deploy to "GLOBUS_LOCATION"
"ant undeployGlobus" will undeploy from "GLOBUS_LOCATION"

"ant deployTomcat" will deploy to "CATALINA_HOME"
"ant undeployTomcat" will undeploy from "CATALINA_HOME"

"ant deployJBoss" will deploy to "JBOSS_HOME"
"ant undeployJBoss" will undeploy from "JBOSS_HOME"

REDCap Configuration:
=======================================
For more information please check the REDCap Users Guide at

https://www.cagrid.org/display/rcds/Users+Guide

For running system tests TEMPORARILY: (until configured to automatically test using Hudson)
==============================================================================
1. Edit the target named test in dev-build.xml
2. Add redcapDBsetup in depends attribute for target test: This will setup the DB for testing 
when the test target is run.
3.uncomment the line <include name="**/*StoryBook.java" /> and comment other *.Test.java, *.SampleTest.java, *.TestCase.java above it.
4. Add the containers to be tested against under ext/dependencies/resources.
So it should have:
ext\dependencies\resources\containers\minimal-secure-tomcat-5.0.28-with-globus-4.0.3.zip
ext\dependencies\resources\containers\minimal-tomcat-5.0.28-with-globus-4.0.3.zip
ext\dependencies\resources\containers\minimal-ws-core-enum-4.0.3.zip
5. Testing is done using non secured tomcat container only for now.
6. run> ant test for complete Redcap project



