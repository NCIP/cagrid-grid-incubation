import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace request="http://exist-db.org/xquery/request";

declare option exist:serialize "media-type=plain/text method=text";

let $phrase := request:get-parameter("phrase", ())

let $names :=
for $administered-item in lib-util:mdrElements("data_element")
            [.//cgMDR:registration_status ne 'Superseded']
            [.//cgMDR:name&=$phrase or .//cgMDR:definition_text&=$phrase]
         let $preferred-name := $administered-item//cgMDR:containing[cgMDR:preferred_designation='true']/cgMDR:name
         order by $preferred-name
         return
            concat('&quot;', data($preferred-name), '&quot;')
let $end := if (count($names) > 10)
                    then 10
                    else count($names)
return
    if (count($names) > 0)
    then
        (
           xs:string('[&quot;'),$phrase,xs:string('&quot;, ['), 
           for $i in 1 to $end 
           return
           (
               item-at($names, $i),
               if ($i < 10)
               then xs:string(', ')
               else ()
           ),
           xs:string(']]')
        )
   else
       (xs:string('[&quot;'), $phrase, xs:string('&quot;, []]'))
