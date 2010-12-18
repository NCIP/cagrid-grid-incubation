xquery version "1.0";
declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace util = "http://exist-db.org/xquery/util";

declare variable $local:triggerEvent external;  
declare variable $local:eventType external;  
declare variable $local:collectionName external;  
declare variable $local:documentName external;
declare variable $local:document external;

(: log the trigger details to the log file :)  
let $new-id := 
    util:catch( "java.lang.Exception",
        concat($local:document//@item_registration_authority_identifier, '_', $local:document//@data_identifier, '_', $local:document//@version),
        ())

return
    if ($new-id and $local:eventType = 'finish')
    then (
        if ($local:document//openMDR:administered_item_administration_record)
        then (
            for $old_version in collection($local:collectionName)[.//openMDR:registration_status != 'Superseded']
            let $old-id := concat($old_version//@item_registration_authority_identifier, '_', $old_version//@data_identifier, '_', $old_version//@version)
            where 
                $old_version//@item_registration_authority_identifier = $local:document//@item_registration_authority_identifier and 
                $old_version//@data_identifier = $local:document//@data_identifier and
                $old_version//@version != $local:document//@version
            return
                (
                update value $old_version//openMDR:registration_status with 'Superseded',
                update value $old_version//openMDR:administrative_status with 'noPendingChanges',
                update value $old_version//openMDR:last_changed_date with current-date(),
                
                if (exists($old_version//openMDR:until_date)) 
                then update value $old_version//openMDR:until_date with current-date()
                else update insert element openMDR:until_date {current-date()} into $old_version//openMDR:administered_item_administration_record,
                
                (:update all pointers to the old item with the new item id:)
                for $item in collection('//db/mdr/data')//@*
                where data($item) = $old-id
                return update value $item with $new-id,
                
                for $item in collection('//db/mdr/data')//*
                where data($item) = $old-id
                return update value $item with $new-id,
                
                if (ends-with($local:collectionName, 'data_element'))
                then (update insert  
                    <openMDR:input_to deriving="{$new-id}">
                        <openMDR:derivation_rule_specification>superseded by</openMDR:derivation_rule_specification>
                    </openMDR:input_to>
                    into $old_version/openMDR:Data_Element)
                else(),
                
                if (ends-with($local:collectionName, 'value_domain'))
                then (
                    update insert 
                        <openMDR:related_to>
                            <openMDR:value_domain_relationship_type_description>useInstead</openMDR:value_domain_relationship_type_description>
                            <openMDR:related_to>{$new-id}</openMDR:related_to>
                        </openMDR:related_to>
                     into $old_version/*)                
                else(),
             
                if (ends-with($local:collectionName, 'data_element_concept'))
                then
                (
                update insert 
                    <openMDR:related_to>
                        <openMDR:data_element_concept_relationship_type_description>useInstead</openMDR:data_element_concept_relationship_type_description>
                        <openMDR:related_to>{$new-id}</openMDR:related_to>
                    </openMDR:related_to>
                into $old_version/openMDR:Data_Element_Concept
                ) 
                else(),
             
                if (ends-with($local:collectionName,'conceptual_domain'))
                then
                (update insert 
                element openMDR:related_to
                {
                element openMDR:conceptual_domain_relationship_type_description {'useInstead'},
                element openMDR:related_to {$new-id}
                }
                into $old_version/*)
                
                else()
                )
            )
        else ()
        )
    else ()

