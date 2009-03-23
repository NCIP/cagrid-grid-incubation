The MDR project contains the ISO11179 database built utilizing the exist database.  
The MDR can be deployed to exist easily.  First, follow the instructions from the exist site
on installing and starting the exist service (note, https must also be available).  Once
exist is up and running simply run "ant install" and the MDR database will be created in
exist and the management web application will be deployed.  Once installed
restart the exist service and connect to localhost:8080