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
    names = document.getElementById('names');
    
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
    if (names.value == "")
    { 
        errors[i] = "Name" ;
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


