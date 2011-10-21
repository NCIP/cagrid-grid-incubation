SDK 4.3 Extended Query Processor

Includes:
-Sort/Limit Extension: caGrid-SDK43SortLimitQueryProcessor-1.4.jar
-Updated SDK: extended-sdk-system-client-4.3.jar, extended-sdk-system-core-4.3.jar
-Sort/Limit schema: CQLQueryModiferExtension.xsd

Instructions

1.	Copy the caGrid-SDK43SortLimitQueryProcessor-1.4.jar into the "<grid_service_root>/lib" directory of your service.

2.	Copy the extended-sdk-system-client-4.3.jar & extended-sdk-system-core-4.3.jar files into the same 
	"<grid_service_root>/lib" directory.

3.	REMOVE the old SDK jars sdk-system-client-4.3.jar & sdk-system-core-4.3.jar from the "<grid_service_root>/lib" 
	directory.

4.	Copy the extension schema CQLQueryModiferExtension.xsd into your Grid Service schema directory: 
	"<grid_service_root>/schema/<grid_service_name>"

5.	Edit the line cql2QueryProcessorClass in your Grid Service service.properties file.  Update the file to read: 
	cql2QueryProcessorClass=org.cagrid.sdkquery43.extension.processor.CustomQueryProcessor
	***MAKE SURE TO CHANGE CQL2 PROPERTY, NOT CQL PROPERTY***

6.	Deploy the service.

XML Examples:

Sort Queries: ***must specify an attributeName and order (ASC or DESC)***

Limit Query: ***must specify firstRow (zero is first row), numberOfRows is optional (default is SDK default)***

Object Query w/Sort 

<cql2:CQLQuery xmlns:cql2="http://CQL.caBIG/2/org.cagrid.cql2">
    <cql2:CQLTargetObject className="gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.withjoin.Bag"/>
    <cql2:CQLQueryModifier>
        <cql2:ModifierExtension>
            <ext:SortLimitExtension xmlns:ext="http://CQL.caBIG/2/org.cagrid.cql2.extras">
                <ext:Sort attributeName="style" order="desc" />
            </ext:SortLimitExtension>
        </cql2:ModifierExtension>
    </cql2:CQLQueryModifier>
</cql2:CQLQuery>

Named Attribute Query w/Sort

<cql2:CQLQuery xmlns:cql2="http://CQL.caBIG/2/org.cagrid.cql2">
 <cql2:CQLTargetObject className="gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Author"/>
 <cql2:CQLQueryModifier>
 <cql2:ModifierExtension>
    <cql2:SortLimitExtension xmlns:ext="http://CQL.caBIG/2/org.cagrid.cql2.extras">
        <cql2:NamedAttribute attributeName="id"/>
        <cql2:NamedAttribute attributeName="name"/>
        <cql2:Sort attributeName="name" order="asc" />
    </cql2:SortLimitExtension>
    </cql2:ModifierExtension>
 </cql2:CQLQueryModifier>
</cql2:CQLQuery>

Distinct Query w/Sort

<cql2:CQLQuery xmlns:cql2="http://CQL.caBIG/2/org.cagrid.cql2">
 <cql2:CQLTargetObject className="gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Parent"/>
 <cql2:CQLQueryModifier>
    <cql2:ModifierExtension>
      <cql2:SortLimitExtension xmlns:cql2="http://CQL.caBIG/2/org.cagrid.cql2.extras">
         <cql2:Sort attributeName="name" order="desc" />
         <cql2:DistinctAttribute attributeName="name" />
       </cql2:SortLimitExtension>
     </cql2:ModifierExtension>
 </cql2:CQLQueryModifier>
</cql2:CQLQuery>

Limit Query that returns first 30 rows 

<cql2:CQLQuery xmlns:cql2="http://CQL.caBIG/2/org.cagrid.cql2">
 <cql2:CQLTargetObject className="gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Author"/>
 <cql2:CQLQueryModifier>
 <cql2:ModifierExtension>
    <cql2:SortLimitExtension xmlns:ext="http://CQL.caBIG/2/org.cagrid.cql2.extras">
       <cql2:Limit firstRow="0" numberOfRows="30" />
    </cql2:SortLimitExtension>
    </cql2:ModifierExtension>
 </cql2:CQLQueryModifier>
</cql2:CQLQuery>

Limit Query that returns all rows after 20th row

<cql2:CQLQuery xmlns:cql2="http://CQL.caBIG/2/org.cagrid.cql2">
 <cql2:CQLTargetObject className="gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Author"/>
 <cql2:CQLQueryModifier>
 <cql2:ModifierExtension>
    <cql2:SortLimitExtension xmlns:ext="http://CQL.caBIG/2/org.cagrid.cql2.extras">
       <cql2:Limit firstRow="20" />
    </cql2:SortLimitExtension>
    </cql2:ModifierExtension>
 </cql2:CQLQueryModifier>
</cql2:CQLQuery>

Named Attribute Query w/Sort & Limit

<cql2:CQLQuery xmlns:cql2="http://CQL.caBIG/2/org.cagrid.cql2">
 <cql2:CQLTargetObject className="gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Author"/>
 <cql2:CQLQueryModifier>
 <cql2:ModifierExtension>
    <cql2:SortLimitExtension xmlns:ext="http://CQL.caBIG/2/org.cagrid.cql2.extras">
        <cql2:NamedAttribute attributeName="id"/>
        <cql2:NamedAttribute attributeName="name"/>
        <cql2:Sort attributeName="name" order="asc" />
        <cql2:Limit firstRow="3" numberOfRows="300" />
    </cql2:SortLimitExtension>
    </cql2:ModifierExtension>
 </cql2:CQLQueryModifier>
</cql2:CQLQuery>
