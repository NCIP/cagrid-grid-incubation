var newID

function findAdminItem(element, type) 
   { 
   var uri = "popup-search.xquery?node=" + element + "&type=" + type; 
   var windowName = "pop-up search"; 
   var windowFeatures = "resizable=yes,width=1024,height=400,menubar=no,left=100,top=100"; 
   window.open (uri, windowName, windowFeatures); 
   }
   
function findClassifier(element, index) 
   { 
   var uri = "popup-search-classifier.xquery?element=" + element; 
   var windowName = "pop-up search"; 
   var windowFeatures = "resizable=no,width=750,height=350,menubar=no,left=100,top=100"; 
   window.open (uri, windowName, windowFeatures); 
   }
   
function findRefDoc() 
   { 
   var uri = "popup-search.xquery?node=cgMDR:described_by&type=reference_document"; 
   var windowName = "pop-up search"; 
   var windowFeatures = "resizable=no,width=750,height=400,menubar=no,left=100,top=100"; 
   window.open (uri, windowName, windowFeatures); 
   }  
   
   
function newSaveSubmit(modelID, instanceID)
	{
	  var oModel = document.getElementById(modelID);
	  var oInstance = oModel.getInstanceDocument(instanceID);
     var submitTrigger = document.getElementById("write-instance");
     
     var doc_type = docType(oInstance);
     var doc_name = docName(oInstance);
     var form_path = location.href;
     var doc_path = form_path.substring(0,form_path.indexOf('xforms'));
     var doc_path = doc_path.replace('rest','webdav');
     var submit_path = doc_path + 'data/' + doc_type + '/' + doc_name;
     
     void submitTrigger.setAttribute('action',submit_path);
    }  

function docID(instance)
   {
     var doc_type = docType(instance)
     
     switch (doc_type)
     {
     case 'reference_document':
        var rdi = instance.documentElement.getAttribute("reference_document_identifier");
        var doc_id = rdi;
        break;
     default:
        var irai = instance.documentElement.getAttribute("item_registration_authority_identifier");
        var di = instance.documentElement.getAttribute("data_identifier");
        var ver = instance.documentElement.getAttribute("version");
        var doc_id = irai + '-' + di + '-' + ver;
     }
     
     return doc_id
   }
   
function docName(instance)
   {
      return docID(instance) + ".xml";
   }
   
function docType(instance)
   {
   var doc_type = instance.documentElement.localName.toLowerCase();
   switch (doc_type)
   {
      case "non_enumerated_conceptual_domain":
         doc_type = "conceptual_domain";
         break;
      case "non_enumerated_conceptual_domain":
         doc_type = "conceptual_domain";
         break;
      case "enumerated_conceptual_domain":
         doc_type = "conceptual_domain";
         break;         
      case "non_enumerated_value_domain":
         doc_type = "value_domain";
         break;
      case "enumerated_value_domain":
         doc_type = "value_domain";
         break;
      case "reference_document":
         doc_type = "reference_document";
         break;
   }
   return doc_type
   }
   
function showPage(modelID, instanceID)
   {
	  var oModel = document.getElementById(modelID);
	  var oInstance = oModel.getInstanceDocument(instanceID);
      
     var uri = "../web/" + docType(oInstance) + ".xquery?compound_id=" + docID(oInstance);
     window.location = uri;
   }
   
   function findReferenceUri(element, index) 
   { 
   var uri = "popup-search-reference-uri.xquery?element=" + element; 
   var windowName = "pop-up search"; 
   var windowFeatures = "resizable=yes,width=1024,height=500,menubar=no,left=100,top=100"; 
   window.open (uri, windowName, windowFeatures); 
   }
   