module namespace lib-supersede="http://www.cancergrid.org/xquery/library/supersede";

declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace session="http://exist-db.org/xquery/session";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";
    
import module namespace 
    lib-util="http://www.cancergrid.org/xquery/library/util" 
    at "../library/m-lib-util.xquery";    


(: this is really easy actually :)
declare function lib-supersede:admin-item($collection as xs:string, $reg-auth-id as xs:string, $data-id as xs:string, $version as xs:string)
{
   let $new-id := concat($reg-auth-id, '-', $data-id, '-', $version)
   let $admin-item as node() := lib-util:mdrElement($collection, $new-id)
   return
       lib-supersede:admin-item($admin-item)   
};

declare function lib-supersede:admin-item($admin-item as node())
{
   let $new-id := lib-util:mdrElementId($admin-item)
   let $collection := lib-util:mdrElementType($admin-item)
   return (
       for $old_version in lib-util:mdrElements($collection)[.//cgMDR:registration_status != 'Superseded']
       let $old-id := lib-util:mdrElementId($old_version)
       where 
          data($old_version//@version) < $admin-item//@version and 
          data($old_version//@item_registration_authority_identifier) = $admin-item//@reg-auth-id and 
          data($old_version//@data_identifier) = $admin-item//@data-id 
       return
             (
             (: set superseded item values :)
             update insert element test {'test'} into $old_version//cgMDR:having,

             update value $old_version//cgMDR:registration_status with 'Superseded',
             
             update value $old_version//cgMDR:administrative_status with 'noPendingChanges1',
             
             update value $old_version//cgMDR:last_changed_date with current-date(),
             
             if (exists($old_version//cgMDR:until_date)) 
             then update value $old_version//cgMDR:until_date with current-date()
             else update insert element cgMDR:until_date {current-date()} into $old_version//cgMDR:administered_item_administration_record,
    
             (:things to do to administered items pointing at the data element
             we should ensure that the relationship mechanism between admin items is the
             same - this could be 3 lines of code.... :)
             
             
             if ($collection = 'data_element')
             then 
                (
                update insert 
                   element cgMDR:input_to
                      {
                      attribute deriving {$new-id},
                      element cgMDR:derivation_rule_specification {'superseded by'}
                      }
                into $old_version
                )
             else(), 
             
             if ($collection = 'value_domain')
             then
             (
                update insert 
                   element cgMDR:related_to
                      {
                         element cgMDR:value_domain_relationship_type_description {'useInstead'},
                         element cgMDR:related_to {$new-id}
                      }
                following $old_version)
                
             else(),
             
             if ($collection = 'data_element_concept')
             then
                (
                update insert 
                   element cgMDR:related_to
                   {
                      element cgMDR:data_element_concept_relationship_type_description {'useInstead'},
                      element cgMDR:related_to {$new-id}
                   }
                into $old_version
                ) 
             else(),
             
             if ($collection = 'conceptual_domain')
             then
             (update insert 
                element cgMDR:related_to
                {
                   element cgMDR:conceptual_domain_relationship_type_description {'useInstead'},
                   element cgMDR:related_to {$new-id}
                }
                into $old_version
                )
                
             else(),
             
             (:update all pointers to the old item with the new item id:)
             for $item in lib-util:mdrElements()//@*
             where data($item) = $old-id
             return
                update value $item with $new-id,
                
             for $item in lib-util:mdrElements()//*
             where data($item) = $old-id
             return
                update value $item with $new-id
             )
       )
};



(: this version takes two IDs and the collection containing them :)
declare function lib-supersede:admin-item(
   $collection as xs:string, 
   $old-id as xs:string, 
   $new-id as xs:string)
{

   let $user := session:get-attribute("username")
   let $password := session:get-attribute("password")
   let $is-ssl :=  lib-util:checkSSL()
   
   let $resource_location := lib-util:getCollectionPath($collection)
   
   return 
      if (xmldb:collection-exists($resource_location) = true())
      then (
         (: check session :)
         if ($is-ssl=true() and $user and $password)
         then (
            (: authenticate :)
            if (xmldb:authenticate(concat("xmldb:exist://", $resource_location), $user, $password)=true())
            then (
               let $old_version := lib-util:mdrElement($collection, $old-id)
               return local:supersede-update($collection, $old_version, $old-id, $new-id))              
            else element div{'could not authenticate'}
            )
         else element div{'no SSL, UN or PW'}
         )
      else element div{'Could not confirm collection'}
};


declare function local:supersede-update($collection as xs:string, $old_version as node(), $old-id as xs:string, $new-id as xs:string)
{
      update value $old_version//cgMDR:registration_status with 'Superseded',       
      update value $old_version//cgMDR:administrative_status with 'noPendingChanges',
      update value $old_version//cgMDR:last_changed_date with current-date(),
         
      if (exists($old_version//cgMDR:until_date)) 
      then update value $old_version//cgMDR:until_date with current-date()
      else update insert element cgMDR:until_date {current-date()} following $old_version//cgMDR:last_change_date,
         
      (:replace all pointers to the old item with the new item id:)
      for $item in lib-util:mdrElements()//@*
      where data($item) = $old-id
      return
         update value $item with $new-id,
         
      for $item in lib-util:mdrElements()//*
      where data($item) = $old-id
      return
         update value $item with $new-id,

      (:things to do to administered items pointing at the data element:)
      if ($collection = 'data_element')
      then 
         (
         update insert 
            element cgMDR:input_to
               {
               attribute deriving {$new-id},
               element cgMDR:derivation_rule_specification {'supersededBy'}
               }
         into $old_version
         )
      else(), 
         if ($collection = 'value_domain')
         then
         (
            update insert 
               element cgMDR:related_to
                  {
                     element cgMDR:value_domain_relationship_type_description {'useInstead'},
                     element cgMDR:related_to {$new-id}
                  }
            following $old_version)
            
         else(),
         
         if ($collection = 'data_element_concept')
         then
            (
            update insert 
               element cgMDR:related_to
               {
                  element cgMDR:data_element_concept_relationship_type_description {'useInstead'},
                  element cgMDR:related_to {$new-id}
               }
            into $old_version
            ) 
         else(),
         
         if ($collection = 'conceptual_domain')
         then
         (update insert 
            element cgMDR:related_to
            {
               element cgMDR:conceptual_domain_relationship_type_description {'useInstead'},
               element cgMDR:related_to {$new-id}
            }
            into $old_version
            )
            
         else(),
         element div{'stored'}
};