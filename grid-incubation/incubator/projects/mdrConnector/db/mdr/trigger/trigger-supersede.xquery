xquery version "1.0";
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace util = "http://exist-db.org/xquery/util";

declare variable $local:triggerEvent external;  
declare variable $local:eventType external;  
declare variable $local:collectionName external;  
declare variable $local:documentName external;
declare variable $local:document external;

(: log the trigger details to the log file :)  
let $new-id := 
    util:catch( "java.lang.Exception",
        concat($local:document//@item_registration_authority_identifier, '-', $local:document//@data_identifier, '-', $local:document//@version),
        ())

return
    if ($new-id and $local:eventType = 'finish')
    then (
        if ($local:document//cgMDR:administered_item_administration_record)
        then (
            for $old_version in collection($local:collectionName)[.//cgMDR:registration_status != 'Superseded']
            let $old-id := concat($old_version//@item_registration_authority_identifier, '-', $old_version//@data_identifier, '-', $old_version//@version)
            where 
                $old_version//@item_registration_authority_identifier = $local:document//@item_registration_authority_identifier and 
                $old_version//@data_identifier = $local:document//@data_identifier and
                $old_version//@version != $local:document//@version
            return
                (
                update value $old_version//cgMDR:registration_status with 'Superseded',
                update value $old_version//cgMDR:administrative_status with 'noPendingChanges',
                update value $old_version//cgMDR:last_changed_date with current-date(),
                
                if (exists($old_version//cgMDR:until_date)) 
                then update value $old_version//cgMDR:until_date with current-date()
                else update insert element cgMDR:until_date {current-date()} into $old_version//cgMDR:administered_item_administration_record,
                
                (:update all pointers to the old item with the new item id:)
                for $item in collection('//db/mdr/data')//@*
                where data($item) = $old-id
                return update value $item with $new-id,
                
                for $item in collection('//db/mdr/data')//*
                where data($item) = $old-id
                return update value $item with $new-id,
                
                if (ends-with($local:collectionName, 'data_element'))
                then (update insert  
                    <cgMDR:input_to deriving="{$new-id}">
                        <cgMDR:derivation_rule_specification>superseded by</cgMDR:derivation_rule_specification>
                    </cgMDR:input_to>
                    into $old_version/cgMDR:Data_Element)
                else(),
                
                if (ends-with($local:collectionName, 'value_domain'))
                then (
                    update insert 
                        <cgMDR:related_to>
                            <cgMDR:value_domain_relationship_type_description>useInstead</cgMDR:value_domain_relationship_type_description>
                            <cgMDR:related_to>{$new-id}</cgMDR:related_to>
                        </cgMDR:related_to>
                     into $old_version/*)                
                else(),
             
                if (ends-with($local:collectionName, 'data_element_concept'))
                then
                (
                update insert 
                    <cgMDR:related_to>
                        <cgMDR:data_element_concept_relationship_type_description>useInstead</cgMDR:data_element_concept_relationship_type_description>
                        <cgMDR:related_to>{$new-id}</cgMDR:related_to>
                    </cgMDR:related_to>
                into $old_version/cgMDR:Data_Element_Concept
                ) 
                else(),
             
                if (ends-with($local:collectionName,'conceptual_domain'))
                then
                (update insert 
                element cgMDR:related_to
                {
                element cgMDR:conceptual_domain_relationship_type_description {'useInstead'},
                element cgMDR:related_to {$new-id}
                }
                into $old_version/*)
                
                else()
                )
            )
        else ()
        )
    else ()

