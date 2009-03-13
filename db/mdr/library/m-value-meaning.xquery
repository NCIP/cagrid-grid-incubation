(:~ Value meaning class :)
module namespace value-meaning="http://www.cancergrid.org/xquery/library/value-meaning";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";

declare namespace cgMDR="http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179="http://www.cancergrid.org/schema/ISO11179";

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

use cgMDR-lib:value_meaning
:)
declare function value-meaning:containing($permissible_value as node(), $value-domain as node()?)
as node()*
{
   if ($cd-identifier >"")
   then(
      for $cd in lib-util:mdrElement("conceptual_domain", lib-util:mdrElementId($value-domain))[string(.//cgMDR:registration_status)!='Superseded'][string(.//cgMDR:registration_status)!='Retired']
      return
         $cd//cgMDR:Value_Meaning[string(./cgMDR:value_meaning_identifier)=string($permissible_value//cgMDR:contained_in)]

   )
   else(
      for $cd in lib-util:mdrElements("conceptual_domain")[data(.//cgMDR:registration_status)!='Superseded']
      return
         $cd//cgMDR:Value_Meaning[string(./cgMDR:value_meaning_identifier)=string($permissible_value//cgMDR:contained_in)]
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
     if ($desc//cgMDR:reference_uri)
     then (
     element a {
        attribute href {$desc//cgMDR:reference_uri},
        if ($desc//cgMDR:value_meaning_description)
        then string($desc//cgMDR:value_meaning_description)
        else string($desc//cgMDR:value_meaning_description)
        }
     )
     else ($desc//cgMDR:value_meaning_description)
};

declare function value-meaning:containing_document($permissible_value as xs:string?) as xs:string?
{
   (: goes and gets the conceptual domain from the value meaning - permissible value relation :)
   (:returns the first match as there will be many :)
   let $a := (
      for $item in lib-util:mdrElements("conceptual_domain")[.//cgMDR:registration_status != 'Superseded'][.//cgMDR:registration_status != 'Retired']
      where string($item//cgMDR:value_meaning_identifier) = $permissible_value
      or string($item//cgMDR:value_meaning_identifier) = $permissible_value
      return lib-util:mdrElementId($item)
      )
   return $a[1]
};


declare function value-meaning:value-meaning($value-meaning-identifier as xs:string?) as node()*
{
for $cd in lib-util:mdrElements("conceptual_domain")
   [.//cgMDR:registration_status != 'Superseded']
   [.//cgMDR:value_meaning_identifier=$value-meaning-identifier]
   return
      for $vm in $cd//cgMDR:Value_Meaning[cgMDR:value_meaning_identifier=$value-meaning-identifier]
      return $vm
};

declare function value-meaning:value-meaning($value-meaning-identifier as xs:string?, $conceptual-domain-identifier as xs:string?) as node()*
{
if ($conceptual-domain-identifier > "")
then (
   for $cd in lib-util:mdrElement("conceptual_domain", $conceptual-domain-identifier)
      return
         for $vm in $cd//cgMDR:Value_Meaning[cgMDR:value_meaning_identifier=$value-meaning-identifier]
         return $vm
)
else (
   for $cd in lib-util:mdrElements("conceptual_domain")
      [.//cgMDR:registration_status != 'Superseded']
      [.//cgMDR:value_meaning_identifier=$value-meaning-identifier]
      return
         for $vm in $cd//cgMDR:Value_Meaning[cgMDR:value_meaning_identifier=$value-meaning-identifier]
         return $vm
      )
};
