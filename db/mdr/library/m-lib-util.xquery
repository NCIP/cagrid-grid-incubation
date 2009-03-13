module namespace lib-util="http://www.cancergrid.org/xquery/library/util";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace text="http://exist-db.org/xquery/text";

(: ----- MDR access functions ----- :)

(:~
  Simple ID of an MDR element
  @param $element The MDR element
  
  generalised to allow removal of document wrapper and extended to 
  return admin item id as well
:)

(:change this if moving root...:)
declare variable $lib-util:rootURI as xs:anyURI := xs:anyURI('/db/mdr');

declare function lib-util:mdrElementId($element as element()) as xs:string?
{
      if ($element//@classification_scheme_item_identifier)
      then xs:string($element//@classification_scheme_item_identifier)
      else
         if ($element//@organization_identifier)
         then xs:string($element//@organization_identifier)
         else
            if ($element//@reference_document_identifier)
            then xs:string($element//@reference_document_identifier)
            else
               if ($element//@organization_identifier)
               then xs:string($element//@organization_identifier)
               else
                  if ($element//@datatype_identifier)
                  then xs:string($element//@datatype_identifier)
                  else 
                     if ($element//@unit_of_measure_identifier)
                     then xs:string($element//@unit_of_measure_identifier)
                     else 
                        if ($element//@rating-identifier)
                        then xs:string($element//@rating-identifier)
                        else 
                           if ($element//@data_identifier)
                           then (concat(xs:string($element//@item_registration_authority_identifier), '-', xs:string($element//@data_identifier), '-', xs:string($element//@version)))
                           else ('oops')
                           

                        
};

declare function lib-util:mdrElementType($mdr-element as element()) as xs:string
{
   let $mdr-element-type := lower-case(local-name($mdr-element))
   return
      if (ends-with($mdr-element-type, "_conceptual_domain"))
      then "conceptual_domain"
      else
         if (ends-with($mdr-element-type, "_value_domain"))
         then "value_domain"
         else 
            if ($mdr-element-type = "concept")
            then "object_class"
            else 
            if ($mdr-element-type = "concept_relationship")
               then "object_class"
               else 
               if ($mdr-element-type = "cgdatatype")
                  then "data_type"
                  else $mdr-element-type
               
               
};

declare function lib-util:mdrElementTypeFriendly($mdr-element as element()) as xs:string
{
   replace(lib-util:mdrElementType($mdr-element),'_',' ')
};

declare function lib-util:mdrElementTypeFriendly($mdr-element-type as xs:string) as xs:string
{
   lib-util:sentence-case(replace($mdr-element-type,'_',' '))
};

(: ------ eXist absolute URI path definitions ------ :)

declare function lib-util:config() as element()
{
   doc(concat($lib-util:rootURI, '/config.xml'))/*
};

declare function lib-util:getServer() as xs:anyURI
{
   xs:anyURI(string-join(tokenize(request:get-url(), "/")[position() le 3], "/"))
};

declare function lib-util:getCollectionPath($collection_name as xs:string) as xs:anyURI
{
  concat($lib-util:rootURI, "/data/", $collection_name)	cast as xs:anyURI
};

declare function lib-util:dataPath() as xs:anyURI
{
  concat($lib-util:rootURI, "/data/") cast as xs:anyURI
};


declare function lib-util:webPath() as xs:anyURI
{
  concat($lib-util:rootURI, "/web/") cast as xs:anyURI
};

declare function lib-util:resolverPath() as xs:anyURI
{
  concat($lib-util:rootURI, "/resolver/") cast as xs:anyURI
};

declare function lib-util:editPath() as xs:anyURI
{
concat($lib-util:rootURI, "/edit/") cast as xs:anyURI
};

declare function lib-util:schemaPath() as xs:anyURI
{
  concat($lib-util:rootURI, "/schemas/") cast as xs:anyURI
};

declare function lib-util:classificationPath() as xs:anyURI
{
   concat($lib-util:rootURI, "/classification/") cast as xs:anyURI
};

declare function lib-util:imagePath() as xs:anyURI
{
   concat($lib-util:rootURI, "/web/images/") cast as xs:anyURI
};

declare function lib-util:formsPath() as xs:anyURI
{
   concat($lib-util:rootURI, "/xforms/") cast as xs:anyURI
};

declare function lib-util:getResourcePath($resource-name as xs:string) as xs:anyURI
{
   concat($lib-util:rootURI, "/", $resource-name ,"/") cast as xs:anyURI
};

declare function lib-util:getResourceLocation($resource-name as xs:string, $file-name as xs:string) as xs:anyURI
{
   xs:anyURI(concat(lib-util:getResourcePath($resource-name), $file-name))
};



(: --- end of eXist absolute URI path definitions --- :)





declare function lib-util:search($mdr-element-type as xs:string, $phrase as xs:string) as element()* 
{
   for $doc in lib-util:mdrElements($mdr-element-type)[.//cgMDR:registration_status/text() != 'Superseded'][.&=concat('*', $phrase, '*')] 
   return $doc      
};

declare function lib-util:searchWithClassification($mdr-element-type as xs:string, $classified-by as xs:anyURI, $phrase as xs:string) as element()* 
{
   for $doc in collection(lib-util:getCollectionPath($mdr-element-type))/*[cgMDR:classified_by=$classified-by]
   where not ($doc//cgMDR:registration_status = 'Superseded')
               and $doc&=$phrase
   return $doc
};



(: --- element accessors --- :)

declare function lib-util:mdrElements($mdr-element-type as xs:string) as element()*
{
	collection(lib-util:getCollectionPath($mdr-element-type))/*
};


(: if nothing supplied, get all documents :)
declare function lib-util:mdrElements() as element()*
{
    collection(concat($lib-util:rootURI,'/data'))/*
};

(: for when you want the whole document :)
declare function lib-util:mdrDocuments($mdr-element-type as xs:string) as item()*
{
   collection(lib-util:getCollectionPath($mdr-element-type))
};


(:~
  MDR element with a specified type and ID
  @param $mdr-element-type The element type
  @param $id The ID
:)
declare function lib-util:mdrElement($mdr-element-type as xs:string,
  $id as xs:string?) as element()*
{
   doc(concat(lib-util:getCollectionPath($mdr-element-type), '/', $id, '.xml'))/*
};

(:note invoking mdrElement without a collection will be slow :)
declare function lib-util:mdrElement($id as xs:string?) as element()*
{
   (: note that mdrElements already retreives the main element :)
     lib-util:mdrElements()[lib-util:mdrElementId(.) eq $id]
};

(: --- End of Document index based functions --- :)





(: --- security related functions --- :)

(: from cgprotocolLib :) 
declare function lib-util:checkSSL() as xs:boolean
{
   let $url := request:get-url()
   return 
      if (starts-with($url, "https://"))
      then true()
      else false()
};

declare function lib-util:checkLogin() as xs:boolean
{
   let $username as xs:string? := session:get-attribute("username")
   let $ssl as xs:boolean := lib-util:checkSSL()
   
   return
      if ($username and $ssl = true())
      then true()
      else false()
};

declare function lib-util:mdrElementName($mdrElement as element()) as xs:string
{
   if (lib-util:mdrElementType($mdrElement) = "reference_document")
   then (string($mdrElement/cgMDR:reference_document_title))
   else (
      if (lib-util:mdrElementType($mdrElement) = "unit_of_measure")
      then (string($mdrElement/cgMDR:unit_of_measure_name))
      else (
         if (lib-util:mdrElementType($mdrElement) = "registration_authority")
         then (string($mdrElement/cgMDR:organization_name))
         else (
            if (lib-util:mdrElementType($mdrElement) = "data_type")
            then (string($mdrElement/cgMDR:datatype_name))
            else (
               if ($mdrElement//cgMDR:name)
               then (string($mdrElement//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name[1]))
               else ('undefined'
               )
            )
         )
      )
   )
};

declare function lib-util:sentence-case($input) as xs:string
{
    string-join(
        for $token in tokenize($input, ' ')
        return concat(upper-case(substring($token,1,1)), substring($token, 2))
    ,' ')
};


(:for backward compatibility:)
declare function lib-util:rootURI() as xs:anyURI
{
   $lib-util:rootURI
};


