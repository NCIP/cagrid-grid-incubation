module namespace administered-item="http://www.cagrid.org/xquery/library/administered-item";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace html="http://www.w3.org/1999/xhtml";

import module namespace 
   lib-util="http://www.cagrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";

declare function administered-item:preferred-name($administered_item as element()?) as xs:string?
{
  let $name := $administered_item//openMDR:containing[openMDR:preferred_designation='true']/openMDR:name[1]/text()
  return
    if (empty($name)) then
      string($administered_item//openMDR:containing/openMDR:name[1]/text())
    else
      string($name)
};

declare function administered-item:preferred-definition($administered_item as element()) as xs:string?
{
  for $preferred_containing in
    $administered_item//openMDR:containing[openMDR:preferred_designation='true'][1]

  let $name := $preferred_containing/openMDR:definition_text/text()

  return
    if (empty($name)) then
      string($administered_item//openMDR:containing/openMDR:definition_text[1]/text())
    else
      string($name)
};

declare function administered-item:preferred-name($administered-item-type as xs:string,
  $id as xs:string) as xs:string?
{
  for $elem in lib-util:mdrElement($administered-item-type, $id)
  return administered-item:preferred-name($elem)
};


declare function administered-item:data-element-summary-anchor($data-element as element(openMDR:Data_Element)) as element(a)?
{
    element a {
        attribute href {
            session:encode-url(
                xs:anyURI(
                concat("../web/data_element_summary.xquery?compound_id=", lib-util:mdrElementId($data-element))))},
        administered-item:preferred-name($data-element)
    }
};



declare function administered-item:html-anchor($collection_name as xs:string?, $id as xs:string?) as element(a)?
   {
       if (empty($collection_name))
       then ()
       else (
           if (empty($id))
           then ()
           else (
                let $uri as xs:anyURI := session:encode-url(xs:anyURI(concat('../web/',$collection_name,".xquery?compound_id=", $id)))
                return
                    <a xmlns="http://www.w3.org/1999/xhtml" href="{$uri}">
                        {administered-item:preferred-name($collection_name, $id)}
                    </a>
                  ))
};

declare function administered-item:html-anchor($admin-item as node()?) as element(a)?
{
    if (empty($admin-item))
    then ()
    else (administered-item:html-anchor(lib-util:mdrElementType($admin-item),lib-util:mdrElementId($admin-item)))
};
