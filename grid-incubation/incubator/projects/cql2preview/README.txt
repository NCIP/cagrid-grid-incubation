This project houses the CQL 2.0 technology preview.

It depends on caGrid 1.2 via a remote Ivy repository.  To build it, run the following commands:

ant -f remote-ivy-build.xml all
	--This will use Ivy to grab artifacts from the caGrid 1.2 Ivy remote repository.  This requires an open internet connection.
ant all
	--This compiles the source code.  

Once the artifacts have been retrieve with Ivy, they need not be retrieved again unless they change in the remote repository.