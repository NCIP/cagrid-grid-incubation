function fullModelRebuild(myModel) 
   {
   void myModel.rebuild();
   void myModel.recalculate();
   void myModel.revalidate();
   void myModel.refresh();
   }

function updateOpener(tag, value) 
   {
   var myDocument = window.opener.document;
   var myModel = myDocument.getElementById("myModel");
   var myInstance = myModel.getInstanceDocument("main");
   //var myNode = myInstance.getElementsByTagName(tag.substring(tag.indexOf(':') + 1));
   var myNode = myInstance.getElementsByTagName(tag);
   
   myNode[myNode.length - 1].textContent = value;   
   void fullModelRebuild(myModel);
   self.close();
   }	