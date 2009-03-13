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
