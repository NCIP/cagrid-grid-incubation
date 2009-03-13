module namespace administered-item="http://www.cancergrid.org/xquery/library/administered-item";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace html="http://www.w3.org/1999/xhtml";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util"
   at "../library/m-lib-util.xquery";

declare function administered-item:preferred-name($administered_item as element()?) as xs:string?
{
  let $name := $administered_item//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name[1]/text()
  return
    if (empty($name)) then
      string($administered_item//cgMDR:containing/cgMDR:name[1]/text())
    else
      string($name)
};

declare function administered-item:preferred-definition($administered_item as element()) as xs:string?
{
  for $preferred_containing in
    $administered_item//cgMDR:containing[cgMDR:preferred_designation='true'][1]

  let $name := $preferred_containing/cgMDR:definition_text/text()

  return
    if (empty($name)) then
      string($administered_item//cgMDR:containing/cgMDR:definition_text[1]/text())
    else
      string($name)
};

declare function administered-item:preferred-name($administered-item-type as xs:string,
  $id as xs:string) as xs:string?
{
  for $elem in lib-util:mdrElement($administered-item-type, $id)
  return administered-item:preferred-name($elem)
};

(: --- handy anchor functions --- :)
declare function administered-item:data-element-summary-anchor($id as xs:string) as element(a)?
{
    let $uri as xs:anyURI := session:encode-url(xs:anyURI(concat("../web/data_element_summary.xquery?compound_id=", $id)))
    return
        <a xmlns="http://www.w3.org/1999/xhtml" href="{$uri}">
            {administered-item:preferred-name("data_element", $id)}
        </a>
};

declare function administered-item:data-element-summary-anchor($data-element as element(cgMDR:Data_Element)) as element(a)?
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
