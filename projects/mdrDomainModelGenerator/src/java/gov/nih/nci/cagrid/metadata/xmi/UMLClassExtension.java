package gov.nih.nci.cagrid.metadata.xmi;

public class UMLClassExtension extends gov.nih.nci.cagrid.metadata.dataservice.UMLClass{
	
	private gov.nih.nci.cagrid.metadata.common.ValueDomain[] valueDomainMetadata;

	public gov.nih.nci.cagrid.metadata.common.ValueDomain[] getValueDomainMetadata() {
		return valueDomainMetadata;
	}

	public void setValueDomainMetadata(
			gov.nih.nci.cagrid.metadata.common.ValueDomain[] valueDomainMetadata) {
		this.valueDomainMetadata = valueDomainMetadata;
	}
	
}
