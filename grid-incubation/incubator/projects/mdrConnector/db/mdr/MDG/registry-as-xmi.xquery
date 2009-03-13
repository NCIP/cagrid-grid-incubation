declare namespace cgMDR = "http://www.cancergrid.org/schema/cgMDR";
declare namespace ISO11179= "http://www.cancergrid.org/schema/ISO11179";
declare namespace UML="omg.org/UML1.3";

import module namespace 
   lib-util="http://www.cancergrid.org/xquery/library/util" 
   at "../library/m-lib-util.xquery";
   
import module namespace 
   lib-forms="http://www.cancergrid.org/xquery/library/forms" 
   at "../edit/m-lib-forms.xquery";
   
declare function local:xmi-id() as attribute()
{
    attribute xmi.id {local:guid()}
};
   
   
declare function local:guid() as xs:string
{
    concat('CGMDRID_', lib-forms:random-name((), 12))
};

let $simple-types as element()* := 
    for $simple-type in lib-util:mdrElements('value_domain')
    return
        element simple-type {
            local:xmi-id(),
            $simple-type}

let $simple-elements as element()* := 
    for $simple-element in lib-util:mdrElements('data_element')
    return
        element simple-element {
            local:xmi-id(),
            $simple-element}


return
   
<XMI xmi.version="1.1" xmlns:UML="omg.org/UML1.3" timestamp="{current-dateTime()}">
	<XMI.header>
		<XMI.documentation>
			<XMI.exporter>The CancerGrid Metadata Registry</XMI.exporter>
			<XMI.exporterVersion>0.1</XMI.exporterVersion>
		</XMI.documentation>
	</XMI.header>

	<XMI.content>
	
			<UML:Model name="EA Model" xmi.id="{local:guid()}">
			<UML:Namespace.ownedElement>
				<UML:Class name="EARootClass" xmi.id="{local:guid()}" isRoot="true" isLeaf="false" isAbstract="false"/>
				<UML:Package name="data_elements" xmi.id="{local:guid()}" isRoot="false" isLeaf="false" isAbstract="false" visibility="public">
					<UML:ModelElement.stereotype>
						<UML:Stereotype xmi.idref="EAID_93C36F9B_F4A2_4677_9A41_61D6823A43CA"/>
					</UML:ModelElement.stereotype>	
					<UML:Namespace.ownedElement>
		
{

for $simple-type in $simple-types
return element UML:Class {
    $simple-type/@xmi.id,
    attribute name {lib-util:mdrElementName($simple-type)}, 
    attribute visibility {"public"},
    attribute isRoot {"false"},
    attribute isLeaf {"false"},
    attribute isAbstract {"false"},
    attribute isActive {"false"}
},

for $simple-element in $simple-elements
return element UML:Class {
    $simple-element/@xmi.id,
    attribute name {$simple-element/cgMDR:field_name[@preferred='true']}, 
    attribute visibility {"public"},
    attribute isRoot {"false"},
    attribute isLeaf {"false"},
    attribute isAbstract {"false"},
    attribute isActive {"false"}
    }

,

for $simple-element in $simple-elements
for $simple-type in $simple-types
where $simple-element//cgMDR:representing = lib-util:mdrElementId($simple-type)
return element UML:Generalization {
            attribute subtype {$simple-element/@xmi.id},
            attribute supertype {$simple-type/@xmi.id},
            attribute xmi.id {local:guid()},
            attribute visibility {'public'}
            }

}
                    </UML:Namespace.ownedElement>
				</UML:Package>
				<UML:Stereotype xmi.id="{local:guid()}" name="XSDschema" isRoot="false" isLeaf="false" isAbstract="false">
					<UML:Stereotype.baseClass>Package</UML:Stereotype.baseClass>
				</UML:Stereotype>
            </UML:Namespace.ownedElement>
        </UML:Model>


{
(: xmi tagged values:) 

for $administered-item in ($simple-elements|$simple-types)
return 
    element UML:TaggedValue {
        local:xmi-id(),
        element tag {"AdministeredItemIdentifier"}, 
	    element value {lib-util:mdrElementId($administered-item)}, 
	    element modelElement {data($administered-item/@xmi.id)}
}
}
    </XMI.content>
    <XMI.extensions xmi.extender="Enterprise Architect 2.5">
		<EAStub xmi.id="EAID_0BDD02C7_E5E8_453f_AE07_10AF0036F85D" name="string" UMLType="Class"/>
	</XMI.extensions>
</XMI>