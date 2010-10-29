function showScheme(schemeURI)
   {
      var form = document.getElementById('index');
      form.showScheme.value = schemeURI;
      form.start.value = 1;
      form.extent.value = 5;
      form.submit();
   }
   
function showTerm(termURI)
   {
      var form = document.getElementById('index');
      form.term_id.value = termURI;
      form.start.value = 1;
      form.extent.value = 5;
      form.submit();
   }
   
function showPage(start, extent)
   {
      var form = document.getElementById('index');
      form.start.value = start;
      form.extent.value = extent;
      form.submit();
   }
   
function changeType(type)
    {
      var form = document.getElementById('index');
      form.letter.value = null;
      form.type.value = type;
      form.start.value = 1;
      form.submit();
    }

function changeLetter(letter)
    {
      var form = document.getElementById('index');
      form.letter.value = letter;
      form.start.value = 1;
      form.submit();
    }

function validate_AnnotatedModel ()
{
    valid = true;
    errors = new Array();
    i=0;
    
    file = document.getElementById('file');
    projectLongName = document.getElementById('project_long_name');
    projectShortName = document.getElementById('project_short_name');
    //projectVersion = document.getElementById('project_version');
    projectDescription = document.getElementById('project_description')
    serviceURL = document.getElementById('service_url')
    
    if(file.value > "" && file.value.lastIndexOf(".xmi")==-1)
    {
        errors[i] = "Select a valid XMI File" ;
        i++;
    }
    
    //if(projectVersion.value > ""  && !projectVersion.value.match(/^\d+\.\d{1}$/) )
    //{
    //    errors[i] = "Project version must be decimal of format x.x (Ex: 1.0 or 1.1)" ;
    //    i++;
    //}
    
    if (file.value == "")
    { 
        errors[i] = "Valid XMI File" ;
        i++;
    }
    
    if (projectLongName.value == "")
    { 
        errors[i] = "Project Long Name" ;
        i++;
    }
    
    if (projectShortName.value == "")
    { 
        errors[i] = "Project Short Name" ;
        i++;
    }
    
    //if (projectVersion.value == "")
    //{ 
    //    errors[i] = "Project Version" ;
    //    i++;
    //}
    
    if (projectDescription.value == "")
    { 
        errors[i] = "Project Description" ;
        i++;
    }
    
    if (serviceURL.value == "")
    { 
        errors[i] = "Service URL" ;
        i++;
    }
    
    if(errors.length>0)
    {
        errMsg = "Please make sure you enter/select the following items :- \n ";
        for(j=0; j<errors.length; j++){
            errMsg = errMsg + "\n" + errors[j];
        }
        alert(errMsg);
        valid = false;
    }    
    
    return valid;
}

function validate_adminItems ()
{
    valid = true;
    errors = new Array();
    i=0;
    
    regauth = document.getElementById('registration-authority');
    regby = document.getElementById('registered-by');
    adminby = document.getElementById('administered-by');
    submitby = document.getElementById('submitted-by');
    adminstatus = document.getElementById('administrative-status');
    regstatus = document.getElementById('registration_status');
    names = document.getElementById('names');
    context = document.getElementById('context-ids');
    
    if (regauth.options[regauth.selectedIndex].value == "")
    { 
        errors[i] = "Registration Authority" ;
        i = i+1;
        valid = false;
        
    }
    
    if (regby.options[regby.selectedIndex].value == "")
    { 
        errors[i] = "Registered By" ;
        i++;
    }

    if (adminby.options[adminby.selectedIndex].value == "")
    { 
        errors[i] = "Administered By" ;
        i++;
    }
    
    if (submitby.options[submitby.selectedIndex].value == "")
    { 
        errors[i] = "Submitted By" ;
        i++;
    }

    if (adminstatus.options[adminstatus.selectedIndex].value == "")
    { 
        errors[i] = "Administrative Status" ;
        i++;
    }
    
    if (regstatus.options[regstatus.selectedIndex].value == "")
    { 
        errors[i] = "Registration Status" ;
        i++;
    }
    
    if (names.value == "")
    { 
        errors[i] = "Name" ;
        i++;
    }
    
    if (context.value == "")
    { 
        errors[i] = "Context" ;
        i++;
    }
   
    if(errors.length>0)
    {
        errMsg = "Please make sure you enter/select the following items :- \n ";
        for(j=0; j<errors.length; j++){
            errMsg = errMsg + "\n" + errors[j];
        }
        alert(errMsg);
        valid = false;
    }    
    
    return valid;
}

function validate_valueDomain(){
    isValid = true;
    if(validate_adminItems()){
        for (var i = 0; i<document.new_value_domain.elements.length; i++) {
            if (document.new_value_domain.elements[i].name == "values") {
                for (var j = 0; j<document.new_value_domain.elements.length; j++) {           
                    if (document.new_value_domain.elements[j].name == "values" & document.new_value_domain.elements[i].value == document.new_value_domain.elements[j].value & i!=j) {
                        isValid = false;
                    }
                } 
             }
        }
       if(!isValid){
        alert('Permissable Values cannot be equal!');
       }
    }else{
        isValid=false;
    }
    return isValid; 
}

function validate_editValueDomain(){
    isValid = true;
    if(validate_adminItems()){
        for (var i = 0; i<document.edit_value_domain.elements.length; i++) {
            if (document.edit_value_domain.elements[i].name == "values") {
                for (var j = 0; j<document.edit_value_domain.elements.length; j++) {           
                    if (document.edit_value_domain.elements[j].name == "values" & document.edit_value_domain.elements[i].value == document.edit_value_domain.elements[j].value & i!=j) {
                        isValid = false;
                    }
                } 
             }
        }
       if(!isValid){
        alert('Permissable Values cannot be equal!');
       }
    }else{
        isValid=false;
    }
    return isValid; 
}

function validate_Organization()
{
    valid = true;
    errors = new Array();
    i=0;
    orgname = document.getElementsByName('org_name')[0].value;
    if (orgname == ""){ 
            errors[i] = "Organization Name" ;
            i = i+1;
            valid = false;
    }
    
    var noofcontacts = document.getElementsByName('contact-name').length;
    contactname = document.getElementsByName('contact-name')[noofcontacts-1].value;
    contactinformation = document.getElementsByName('contact-information')[noofcontacts-1].value;
    
    if (contactname == ""){ 
        errors[i] = "Contact Name" ;
        i++;
    }

    if (contactinformation == ""){ 
        errors[i] = "Contact Information" ;
        i++;
    }
    
    for(var size=0;size<noofcontacts;size++){
        for(var j=size+1;j<noofcontacts;j++){
            if(document.getElementsByName('contact-name')[size].value == 
               document.getElementsByName('contact-name')[j].value){
               errors[i] = "Contact Names cannot be same" ;
               i++;  
               break;               
            }   
        }    
    }
    
    if(errors.length>0){
        errMsg = "Please make sure you enter/select the following items :- \n ";
        for(j=0; j<errors.length; j++){
            errMsg = errMsg + "\n" + errors[j];
        }
        alert(errMsg);
        valid = false;
    }    
    return valid;
}

function addNewContact(obj) {
    if(validate_Organization()){
        addHTML(document.getElementById('Container'),'<tr><td class="row-header-cell" colspan="6">New Contact</td></tr><tr><td class="left_header_cell">Name<font color="red">*</font></td><td><input id ="contact-name" type="text" name="contact-name"/></td></tr>',false);
        addHTML(document.getElementById('Container'),'<tr><td class="left_header_cell">Title</td><td><input type="text" name="contact-title"/></td></tr>',false);
        addHTML(document.getElementById('Container'),'<tr><td class="left_header_cell">Information:Email/Phone<font color="red">*</font></td><td><input type="text" name="contact-information"/></td></tr>',false);
        addHTML(document.getElementById('Container'),'<tr><td class="left_header_cell"></td><td><input type="button" name="submit" value="Delete Contact" onClick="deleteContact(this);"/></td></tr>',false);
    }
}

function addNewContactInfo(obj) {
    if(validate_Organization()){
        addHTML(document.getElementById('Container'),'<tr><td class="row-header-cell" colspan="6">New Contact</td></tr><tr><td class="left_header_cell">Name<font color="red">*</font></td><td><input id ="contact-name" type="text" name="contact-name"/></td></tr>',false);
        addHTML(document.getElementById('Container'),'<tr><td class="left_header_cell">Title</td><td><input type="text" name="contact-title"/></td></tr>',false);
        addHTML(document.getElementById('Container'),'<tr><td class="left_header_cell">Information:Email/Phone<font color="red">*</font></td><td><input type="text" name="contact-information"/></td></tr>',false);
    }
}

function deleteContact(obj) {
    var d1=document.getElementById('parent');
    var d2=document.getElementById('Container');
    d1.removeChild(d2);
}

function addHTML(o,p,q){function r(a){var b;if(typeof DOMParser!="undefined")b=(new DOMParser()).parseFromString(a,"application/xml");else{var c=["MSXML2.DOMDocument","MSXML.DOMDocument","Microsoft.XMLDOM"];for(var i=0;i<c.length&&!b;i++){try{b=new ActiveXObject(c[i]);b.loadXML(a)}catch(e){}}}return b}function s(a,b,c){a[b]=function(){return eval(c)}}function t(b,c,d){if(typeof d=="undefined")d=1;if(d>1){if(c.nodeType==1){var e=document.createElement(c.nodeName);var f={};for(var a=0,g=c.attributes.length;a<g;a++){var h=c.attributes[a].name,k=c.attributes[a].value,l=(h.substr(0,2)=="on");if(l)f[h]=k;else{switch(h){case"class":e.className=k;break;case"for":e.htmlFor=k;break;default:e.setAttribute(h,k)}}}b=b.appendChild(e);for(l in f)s(b,l,f[l])}else if(c.nodeType==3){var m=(c.nodeValue?c.nodeValue:"");var n=m.replace(/^\s*|\s*$/g,"");if(n.length<7||(n.indexOf("<!--")!=0&&n.indexOf("-->")!=(n.length-3)))b.appendChild(document.createTextNode(m))}}for(var i=0,j=c.childNodes.length;i<j;i++)t(b,c.childNodes[i],d+1)}p="<root>"+p+"</root>";var u=r(p);if(o&&u){if(q!=false)while(o.lastChild)o.removeChild(o.lastChild);t(o,u.documentElement)}}

function validate(obj)
{
    valid = true;
    errors = new Array();
    i=0;
    
    page = document.getElementById('move');
     
    if(page.value=='Next->Conceptual Domain'){
         regauth = document.getElementById('registration-authority');
         regby = document.getElementById('registered-by');
         adminby = document.getElementById('administered-by');
         submitby = document.getElementById('submitted-by');
         adminstatus = document.getElementById('administrative-status');
         regstatus = document.getElementById('registration-status');
         
         if (regauth.options[regauth.selectedIndex].value == ""){ 
             errors[i] = "Registration Authority" ;
             i++;
         }
         
         if (regby.options[regby.selectedIndex].value == ""){ 
             errors[i] = "Registered By" ;
             i++;
         }
     
         if (adminby.options[adminby.selectedIndex].value == ""){ 
             errors[i] = "Administered By" ;
             i++;
         }
         
         if (submitby.options[submitby.selectedIndex].value == ""){ 
             errors[i] = "Submitted By" ;
             i++;
         }
     
         if (adminstatus.options[adminstatus.selectedIndex].value == ""){ 
             errors[i] = "Administrative Status" ;
             i++;
         }
             
         if (regstatus.options[regstatus.selectedIndex].value == ""){ 
             errors[i] = "Registration Status" ;
             i++;
         }
    }
   
    if(page.value=='Next->Object Class'){

       var answer = confirm("Save Conceptual Domain and Continue")
	   if (answer){        
            choose = document.getElementById('choose-conceptual-domain');
            if(choose.value== "existing"){
                cdid = document.getElementById('conceptual_domain_id');
                if(cdid.value == ""){
                    errors[i] = "Choose existing conceptual domain";
                    i++;
                }
            }else{
             names = document.getElementById('name_cd');
             context = document.getElementById('preferred_name_context_cd');
             if(context.value == ""){
                 errors[i] = "Context" ;
                 i++;
             }
             if (names.value == ""){ 
                  errors[i] = "Conceptual Domain Name" ;
                  i++;
             }
            }
        }else{return false;}
    }
    
    if(page.value=='Next->Property Class'){
       var answer = confirm("Save Object Class and Continue")
	   if (answer){ 
            choose = document.getElementById('choose-object-class');
            if(choose.value== "existing"){
                ocid = document.getElementById('object_class_id');
                if(ocid.value == ""){
                    errors[i] = "Choose existing object class";
                    i++;
                }
            }else{
                names = document.getElementById('name_oc');
                context = document.getElementById('preferred_name_context_oc');
                
                if(context.value == ""){
                    errors[i] = "Context" ;
                    i++;
                }
                
                if (names.value == ""){ 
                     errors[i] = "Object Class Name" ;
                     i++;
                }
           }
        }else{return false;}
    }
    
     if(page.value=='Next->Data Element Concept'){
       var answer = confirm("Save Property Class and Continue")
	   if (answer){ 
            choose = document.getElementById('choose-property-class');
            if(choose.value== "existing"){
                pcid = document.getElementById('property_id');
                if(pcid.value == ""){
                    errors[i] = "Choose existing property class";
                    i++;
                }
            }else{
                 names = document.getElementById('name_pc');
                 context = document.getElementById('preferred_name_context_pc');
                 
                 if(context.value == ""){
                     errors[i] = "Context" ;
                     i++;
                 }
                    
                 if (names.value == ""){ 
                      errors[i] = "Property Name" ;
                      i++;
                 }
           }
       }else{return false;}  
    }
    
     if(page.value=='Next->Value Domain'){
       var answer = confirm("Save Data Element Concept and Continue")
	   if (answer){ 
            choose = document.getElementById('choose-data-element-concept');
            if(choose.value== "existing"){
                decid = document.getElementById('data_element_concept_id');
                if(decid.value == ""){
                    errors[i] = "Choose existing data element concept";
                    i++;
                }
            }else{
                 names = document.getElementById('name_dec');
                 context = document.getElementById('preferred_name_context_dec');
              
                 if(context.value == ""){
                     errors[i] = "Context" ;
                     i++;
                 }
                 
                 if (names.value == ""){ 
                      errors[i] = "Data Element Concept Name" ;
                      i++;
                 }
            }
       }else{return false;}  
    }
    
    if(page.value=='Next->Data Element'){
       var answer = confirm("Save Value Domain and Continue")
	   if (answer){ 
            choose = document.getElementById('choose-value-domain');
            if(choose.value== "existing"){
                vdid = document.getElementById('value_domain_id');
                if(vdid.value == ""){
                    errors[i] = "Choose existing value domain";
                    i++;
                }
            }else{
                names = document.getElementById('name_vd');
                context = document.getElementById('preferred_name_context_vd');
             
                if(context.value == ""){
                    errors[i] = "Context" ;
                    i++;
                }
                if (names.value == ""){ 
                     errors[i] = "Value Domain Name" ;
                     i++;
                }
            }
        }else{return false;}  
    }
    
    
    if(page.value == 'Next->Reference Doc'){
       var answer = confirm("Save Data Element and Continue")
	   if (answer){        
          names = document.getElementById('name_de');
          context = document.getElementById('preferred_name_context_de');
         
          if(context.value == ""){
              errors[i] = "Context" ;
              i++;
          }          
          if (names.value == ""){ 
                errors[i] = "Data Element Name" ;
                i++;
           }
        }else{return false;}  
    }
    
    if(page.value=='Next->Confirm'){
       var answer = confirm("Save Reference Docs")
	   if (answer){ 
         var isValid = true;
         for (var k = 0; k<document.edit_admin_item.elements.length; k++) {
             if (document.edit_admin_item.elements[k].name == "values") {
                 for (var j = 0; j<document.edit_admin_item.elements.length; j++) {           
                     if (document.edit_admin_item.elements[j].name == "values" & document.edit_admin_item.elements[k].value.toLowerCase() == document.edit_admin_item.elements[j].value.toLowerCase() & k!=j) {
                         isValid = false;
                     }
                 } 
              }
         }
         if(isValid == false){
             errors[i] = "Values cannot be the same";
             i++;
         }
        }else{return false;}   
    }
    
    if(errors.length>0){
        errMsg = "Please make sure you enter/select the following items :- \n ";
        for(j=0; j<errors.length; j++){
            errMsg = errMsg + "\n" + errors[j];
        }
        alert(errMsg);
        valid = false;
    }    
    return valid;
}

function validatePrev(obj){
    valid = true;
    errors = new Array();
    i=0;
   
    pagePrev = document.getElementById('movePrev');
      
    if(pagePrev.value == 'Previous->Admin Items' ||
       pagePrev.value == 'Previous->Conceptual Domain' ||
       pagePrev.value == 'Previous->Object Class' ||
       pagePrev.value == 'Previous->Property Class' ||
       pagePrev.value == 'Previous->Data Element Concept' ||
       pagePrev.value == 'Previous->Value Domain' ||
       pagePrev.value == 'Previous->Data Element' ){
       var str = 'move to '+pagePrev.value+'?';
              
       var answer = confirm(str)
    	   if (answer){   
    	       valid = true;
    	   }else{
    	       valid = false;
    	   }
    }
    return valid;
    
}

function checkFile(obj){
    valid = true;
    errors = new Array();
    i=0;
    file = document.getElementById('file');
    if(file.value == ""){
       errors[i] = "Choose a file" ;
       i++;
    }
     if(errors.length>0){
        errMsg = "Please make sure you enter/select the following items :- \n ";
        for(j=0; j<errors.length; j++){
            errMsg = errMsg + "\n" + errors[j];
        }
        alert(errMsg);
        valid = false;
    }  
    return valid;
}

