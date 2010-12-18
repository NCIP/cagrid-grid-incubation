(:~ Value meaning class :)
module namespace value-meaning="http://www.cagrid.org/xquery/library/value-meaning";

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";

declare namespace openMDR="http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179="http://www.cagrid.org/schema/ISO11179";

declare namespace util="http://exist-db.org/xquery/util";

(:~
  Constructor
  @param $permissible_value Permissible value
:)

(: this routine is confusing to the potential user - you pass a node with a chunk of data
which contains the reference id you are interested in.  Much more obvious/predictable to 
get the actual reference id as a string and pass that

Of course, you might not have the host node - you may just have an ID, and under these
circumstances this routine is unwieldy

use openMDR-lib:value_meaning
:)
declare function value-meaning:containing($permissible_value as node(), $value-domain as node()?)
as node()*
{
   if ($cd-identifier >"")
   then(
      for $cd in lib-util:mdrElement("conceptual_domain", lib-util:mdrElementId($value-domain))[string(.//openMDR:registration_status)!='Superseded'][string(.//openMDR:registration_status)!='Retired']
      return
         $cd//openMDR:Value_Meaning[string(./openMDR:value_meaning_identifier)=string($permissible_value//openMDR:contained_in)]

   )
   else(
      for $cd in lib-util:mdrElements("conceptual_domain")[data(.//openMDR:registration_status)!='Superseded']
      return
         $cd//openMDR:Value_Meaning[string(./openMDR:value_meaning_identifier)=string($permissible_value//openMDR:contained_in)]
         )
      
};

(:~
  Description
  @param $value_meaning Value meaning
:)
declare function value-meaning:description($value_meaning as node())
as node()?
{
  for $desc in $value_meaning/.
  return
     if ($desc//openMDR:reference_uri)
     then (
     element a {
        attribute href {$desc//openMDR:reference_uri},
        if ($desc//openMDR:value_meaning_description)
        then string($desc//openMDR:value_meaning_description)
        else string($desc//openMDR:value_meaning_description)
        }
     )
     else ($desc//openMDR:value_meaning_description)
};

declare function value-meaning:containing_document($permissible_value as xs:string?) as xs:string?
{
   (: goes and gets the conceptual domain from the value meaning - permissible value relation :)
   (:returns the first match as there will be many :)
   let $a := (
      for $item in lib-util:mdrElements("conceptual_domain")[.//openMDR:registration_status != 'Superseded'][.//openMDR:registration_status != 'Retired']
      where string($item//openMDR:value_meaning_identifier) = $permissible_value
      or string($item//openMDR:value_meaning_identifier) = $permissible_value
      return lib-util:mdrElementId($item)
      )
   return $a[1]
};


declare function value-meaning:value-meaning($value-meaning-identifier as xs:string?) as node()*
{
for $cd in lib-util:mdrElements("conceptual_domain")
   [.//openMDR:registration_status != 'Superseded']
   [.//openMDR:value_meaning_identifier=$value-meaning-identifier]
   return
      for $vm in $cd//openMDR:Value_Meaning[openMDR:value_meaning_identifier=$value-meaning-identifier]
      return $vm
};

declare function value-meaning:value-meaning($value-meaning-identifier as xs:string?, $conceptual-domain-identifier as xs:string?) as node()*
{
if ($conceptual-domain-identifier > "")
then (
   for $cd in lib-util:mdrElement("conceptual_domain", $conceptual-domain-identifier)
      return
         for $vm in $cd//openMDR:Value_Meaning[openMDR:value_meaning_identifier=$value-meaning-identifier]
         return $vm
)
else (
   for $cd in lib-util:mdrElements("conceptual_domain")
      [.//openMDR:registration_status != 'Superseded']
      [.//openMDR:value_meaning_identifier=$value-meaning-identifier]
      return
         for $vm in $cd//openMDR:Value_Meaning[openMDR:value_meaning_identifier=$value-meaning-identifier]
         return $vm
      )
};
