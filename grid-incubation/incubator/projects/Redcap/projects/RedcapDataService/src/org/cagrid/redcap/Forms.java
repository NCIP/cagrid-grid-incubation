/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.redcap;

import org.cagrid.redcap.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "redcap_metadata")

public class Forms implements java.io.Serializable {

	private FormsPk pk;
	
	@EmbeddedId
	public FormsPk getPk() {
		return pk;
	}

	public void setPk(FormsPk pk) {
		this.pk = pk;
	}

	private Data[] formsRef;
	
	private java.lang.String fieldName;

	private float fieldOrder;

	private java.lang.String formName;

	private int projectId;
	
	private org.cagrid.redcap.Projects projectsFormsRef;
	
	private org.cagrid.redcap.Events[] eventsRefsCollection;
	
	
	private java.lang.String branchingLogic;

	private java.lang.String elementEnum;

	private java.lang.String elementLabel;

	private java.lang.String elementNote;

	private java.lang.String elementPreceedingHeader;

	private java.lang.String elementType;

	private java.lang.String elementValidationCheck;

	private java.lang.String elementValidationMax;

	private java.lang.String elementValidationMin;

	private java.lang.String elementValidationType;

	private java.lang.String fieldPhi;

	private int fieldReq;

	private java.lang.String fieldUnits;

	private java.lang.String formMenuDescription;

	@Column(name="field_name",insertable=false,updatable=false)
	public java.lang.String getFieldName() {
		return getPk().getFieldName();
	}

	public void setFieldName(java.lang.String fieldName) {
		getPk().setFieldName(fieldName);
	}

	@Column(name="project_id",insertable=false,updatable=false)
	public int getProjectId() {
		//return projectId;
		return getPk().getProjectId();
	}

	public void setProjectId(int projectId) {
		//this.projectId = projectId;
		getPk().setProjectId(projectId);
	}

	public Forms() {
	}

	public Forms(java.lang.String branchingLogic, java.lang.String elementEnum,
			java.lang.String elementLabel, java.lang.String elementNote,
			java.lang.String elementPreceedingHeader,
			java.lang.String elementType,
			java.lang.String elementValidationCheck,
			java.lang.String elementValidationMax,
			java.lang.String elementValidationMin,
			java.lang.String elementValidationType,
			org.cagrid.redcap.Events[] eventsRefsCollection,
			java.lang.String fieldName, float fieldOrder,
			java.lang.String fieldPhi, int fieldReq,
			java.lang.String fieldUnits, java.lang.String formMenuDescription,
			java.lang.String formName, org.cagrid.redcap.Data[] formsRef,
			int projectId, org.cagrid.redcap.Projects projectsFormsRef) {
			this.projectsFormsRef = projectsFormsRef;
			this.eventsRefsCollection = eventsRefsCollection;
			this.formsRef = formsRef;
		
			this.fieldOrder = fieldOrder;
			this.formName = formName;
			getPk().setFieldName(fieldName);
			getPk().setProjectId(projectId);
			
			this.branchingLogic = branchingLogic;
			this.elementEnum = elementEnum;
			this.elementLabel = elementLabel;
			this.elementNote = elementNote;
			this.elementPreceedingHeader = elementPreceedingHeader;
			this.elementType = elementType;
			this.elementValidationCheck = elementValidationCheck;
			this.elementValidationMax = elementValidationMax;
			this.elementValidationMin = elementValidationMin;
			this.elementValidationType = elementValidationType;
			this.fieldPhi = fieldPhi;
			this.fieldReq = fieldReq;
			this.fieldUnits = fieldUnits;
			this.formMenuDescription = formMenuDescription;
	}
	
	//BIDIRECTIONAL forms-data
	@LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(mappedBy="dataRef",fetch=FetchType.LAZY)
	@IndexColumn(name="record")
	
	public  Data[] getFormsRef() {
		return formsRef;
	}

	/**
	 * Sets the formsRef value for this Forms.
	 * 
	 * @param formsRef
	 */
	public void setFormsRef( Data[] formsRef) {
		this.formsRef = formsRef;
	}
	@LazyCollection(LazyCollectionOption.EXTRA)
	public  Data getFormsRef(int i) {
		return this.formsRef[i];
	}

	public void setFormsRef(int i,  Data _value) {
		this.formsRef[i] = _value;
	}

	
	@Column(name="field_order")
	public float getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(float fieldOrder) {
		this.fieldOrder = fieldOrder;
	}

	@Column(name="form_name")
	public java.lang.String getFormName() {
		return formName;
	}

	public void setFormName(java.lang.String formName) {
		this.formName = formName;
	}

	//BIDIRECTIONAL
	@ManyToOne
	@JoinColumn(name="project_id",referencedColumnName="project_id",insertable=false,updatable=false)
	
	public org.cagrid.redcap.Projects getProjectsFormsRef() {
		return projectsFormsRef;
	}
	
	public void setProjectsFormsRef(org.cagrid.redcap.Projects projectsFormsRef) {
		this.projectsFormsRef = projectsFormsRef;
	}

	//BIDIRECTIONAL
	@LazyCollection(value=LazyCollectionOption.FALSE)  
	@ManyToMany
	  @JoinTable(name="redcap_data",
				joinColumns={@JoinColumn(name="project_id",
						referencedColumnName="project_id",
						insertable=false,updatable=false),
						@JoinColumn(name="field_name",
								referencedColumnName="field_name",
								insertable=false,updatable=false)}
				,
				inverseJoinColumns={@JoinColumn(name="event_id", 
					unique=true)
			}
		)
		@IndexColumn(name="record")
		@NotFound(action=NotFoundAction.IGNORE)
	
	public org.cagrid.redcap.Events[] getEventsRefsCollection() {
		return eventsRefsCollection;
	}
	
	public void setEventsRefsCollection(
			org.cagrid.redcap.Events[] eventsRefsCollection) {
		this.eventsRefsCollection = eventsRefsCollection;
	}
	
	public org.cagrid.redcap.Events getEventsRefsCollection(int i) {
		return this.eventsRefsCollection[i];
	}

	public void setEventsRefsCollection(int i, org.cagrid.redcap.Events _value) {
		this.eventsRefsCollection[i] = _value;
	}
	
	@Column(name = "branching_logic")
	public java.lang.String getBranchingLogic() {
		return branchingLogic;
	}

	/**
	 * Sets the branchingLogic value for this Forms.
	 * 
	 * @param branchingLogic
	 */
	public void setBranchingLogic(java.lang.String branchingLogic) {
		this.branchingLogic = branchingLogic;
	}

	/**
	 * Gets the elementEnum value for this Forms.
	 * 
	 * @return elementEnum
	 */
	@Column(name = "element_enum")
	public java.lang.String getElementEnum() {
		return elementEnum;
	}

	/**
	 * Sets the elementEnum value for this Forms.
	 * 
	 * @param elementEnum
	 */
	public void setElementEnum(java.lang.String elementEnum) {
		this.elementEnum = elementEnum;
	}

	/**
	 * Gets the elementLabel value for this Forms.
	 * 
	 * @return elementLabel
	 */
	@Column(name = "element_label")
	public java.lang.String getElementLabel() {
		return elementLabel;
	}

	/**
	 * Sets the elementLabel value for this Forms.
	 * 
	 * @param elementLabel
	 */
	public void setElementLabel(java.lang.String elementLabel) {
		this.elementLabel = elementLabel;
	}

	/**
	 * Gets the elementNote value for this Forms.
	 * 
	 * @return elementNote
	 */
	@Column(name = "element_note")
	public java.lang.String getElementNote() {
		return elementNote;
	}

	/**
	 * Sets the elementNote value for this Forms.
	 * 
	 * @param elementNote
	 */
	public void setElementNote(java.lang.String elementNote) {
		this.elementNote = elementNote;
	}

	/**
	 * Gets the elementPreceedingHeader value for this Forms.
	 * 
	 * @return elementPreceedingHeader
	 */
	@Column(name = "element_preceding_header")
	public java.lang.String getElementPreceedingHeader() {
		return elementPreceedingHeader;
	}

	/**
	 * Sets the elementPreceedingHeader value for this Forms.
	 * 
	 * @param elementPreceedingHeader
	 */
	public void setElementPreceedingHeader(
			java.lang.String elementPreceedingHeader) {
		this.elementPreceedingHeader = elementPreceedingHeader;
	}

	/**
	 * Gets the elementType value for this Forms.
	 * 
	 * @return elementType
	 */
	@Column(name = "element_type")
	public java.lang.String getElementType() {
		return elementType;
	}

	/**
	 * Sets the elementType value for this Forms.
	 * 
	 * @param elementType
	 */
	public void setElementType(java.lang.String elementType) {
		this.elementType = elementType;
	}

	/**
	 * Gets the elementValidationCheck value for this Forms.
	 * 
	 * @return elementValidationCheck
	 */
	@Column(name = "element_validation_checktype")
	public java.lang.String getElementValidationCheck() {
		return elementValidationCheck;
	}

	/**
	 * Sets the elementValidationCheck value for this Forms.
	 * 
	 * @param elementValidationCheck
	 */
	public void setElementValidationCheck(
			java.lang.String elementValidationCheck) {
		this.elementValidationCheck = elementValidationCheck;
	}

	/**
	 * Gets the elementValidationMax value for this Forms.
	 * 
	 * @return elementValidationMax
	 */
	@Column(name = "element_validation_max")
	public java.lang.String getElementValidationMax() {
		return elementValidationMax;
	}

	/**
	 * Sets the elementValidationMax value for this Forms.
	 * 
	 * @param elementValidationMax
	 */
	public void setElementValidationMax(java.lang.String elementValidationMax) {
		this.elementValidationMax = elementValidationMax;
	}

	/**
	 * Gets the elementValidationMin value for this Forms.
	 * 
	 * @return elementValidationMin
	 */
	@Column(name = "element_validation_min")
	public java.lang.String getElementValidationMin() {
		return elementValidationMin;
	}

	/**
	 * Sets the elementValidationMin value for this Forms.
	 * 
	 * @param elementValidationMin
	 */
	public void setElementValidationMin(java.lang.String elementValidationMin) {
		this.elementValidationMin = elementValidationMin;
	}

	/**
	 * Gets the elementValidationType value for this Forms.
	 * 
	 * @return elementValidationType
	 */
	@Column(name = "element_validation_type")
	public java.lang.String getElementValidationType() {
		return elementValidationType;
	}

	/**
	 * Sets the elementValidationType value for this Forms.
	 * 
	 * @param elementValidationType
	 */
	public void setElementValidationType(java.lang.String elementValidationType) {
		this.elementValidationType = elementValidationType;
	}
	
	/**
	 * Gets the fieldPhi value for this Forms.
	 * 
	 * @return fieldPhi
	 */
	@Column(name = "field_phi")
	public java.lang.String getFieldPhi() {
		return fieldPhi;
	}

	/**
	 * Sets the fieldPhi value for this Forms.
	 * 
	 * @param fieldPhi
	 */
	public void setFieldPhi(java.lang.String fieldPhi) {
		this.fieldPhi = fieldPhi;
	}

	/**
	 * Gets the fieldReq value for this Forms.
	 * 
	 * @return fieldReq
	 */
	@Column(name = "field_req")
	public int getFieldReq() {
		return fieldReq;
	}

	/**
	 * Sets the fieldReq value for this Forms.
	 * 
	 * @param fieldReq
	 */
	public void setFieldReq(int fieldReq) {
		this.fieldReq = fieldReq;
	}

	/**
	 * Gets the fieldUnits value for this Forms.
	 * 
	 * @return fieldUnits
	 */
	@Column(name = "field_units")
	public java.lang.String getFieldUnits() {
		return fieldUnits;
	}

	/**
	 * Sets the fieldUnits value for this Forms.
	 * 
	 * @param fieldUnits
	 */
	public void setFieldUnits(java.lang.String fieldUnits) {
		this.fieldUnits = fieldUnits;
	}

	/**
	 * Gets the formMenuDescription value for this Forms.
	 * 
	 * @return formMenuDescription
	 */
	@Column(name = "form_menu_description")
	public java.lang.String getFormMenuDescription() {
		return formMenuDescription;
	}

	/**
	 * Sets the formMenuDescription value for this Forms.
	 * 
	 * @param formMenuDescription
	 */
	public void setFormMenuDescription(java.lang.String formMenuDescription) {
		this.formMenuDescription = formMenuDescription;
	}
	
	@Transient
	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Forms))
			return false;
		Forms other = (Forms) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
			&& ((this.formsRef == null && other.getFormsRef() == null) || (this.formsRef != null && java.util.Arrays
				.equals(this.formsRef, other.getFormsRef())))
				
					&& ((this.eventsRefsCollection == null && other
						.getEventsRefsCollection() == null) || (this.eventsRefsCollection != null && java.util.Arrays
						.equals(this.eventsRefsCollection, other
								.getEventsRefsCollection())))
								
				&&
				((this.projectsFormsRef == null && other
						.getProjectsFormsRef() == null) || (this.projectsFormsRef != null && this.projectsFormsRef.equals(other.getProjectsFormsRef())))
				&&				
		
				((getPk().getFieldName() == null && other.getPk().getFieldName() == null) || (getPk().getFieldName() != null && getPk().getFieldName()
						.equals(other.getPk().getFieldName())))
						
				&&

				this.fieldOrder == other.getFieldOrder()

				&& ((this.formName == null && other.getFormName() == null) || (this.formName != null && this.formName
						.equals(other.getFormName())))

				&&

				getPk().getProjectId() == other.getPk().getProjectId()
				
		&& ((this.branchingLogic == null && other.getBranchingLogic() == null) || (this.branchingLogic != null && this.branchingLogic
				.equals(other.getBranchingLogic())))
		&& ((this.elementEnum == null && other.getElementEnum() == null) || (this.elementEnum != null && this.elementEnum
				.equals(other.getElementEnum())))
		&& ((this.elementLabel == null && other.getElementLabel() == null) || (this.elementLabel != null && this.elementLabel
				.equals(other.getElementLabel())))
		&& ((this.elementNote == null && other.getElementNote() == null) || (this.elementNote != null && this.elementNote
				.equals(other.getElementNote())))
		&& ((this.elementPreceedingHeader == null && other
				.getElementPreceedingHeader() == null) || (this.elementPreceedingHeader != null && this.elementPreceedingHeader
				.equals(other.getElementPreceedingHeader())))
		&& ((this.elementType == null && other.getElementType() == null) || (this.elementType != null && this.elementType
				.equals(other.getElementType())))
		&& ((this.elementValidationCheck == null && other
				.getElementValidationCheck() == null) || (this.elementValidationCheck != null && this.elementValidationCheck
				.equals(other.getElementValidationCheck())))
		&& ((this.elementValidationMax == null && other
				.getElementValidationMax() == null) || (this.elementValidationMax != null && this.elementValidationMax
				.equals(other.getElementValidationMax())))
		&& ((this.elementValidationMin == null && other
				.getElementValidationMin() == null) || (this.elementValidationMin != null && this.elementValidationMin
				.equals(other.getElementValidationMin())))
		&& ((this.elementValidationType == null && other
				.getElementValidationType() == null) || (this.elementValidationType != null && this.elementValidationType
				.equals(other.getElementValidationType())))
		&& this.fieldReq == other.getFieldReq()
				&& ((this.fieldUnits == null && other.getFieldUnits() == null) || (this.fieldUnits != null && this.fieldUnits
						.equals(other.getFieldUnits())))
				&& ((this.formMenuDescription == null && other
						.getFormMenuDescription() == null) || (this.formMenuDescription != null && this.formMenuDescription
						.equals(other.getFormMenuDescription())));
		
		
		__equalsCalc = null;
		return _equals;
	}

	@Transient
	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		
		if (getFormsRef() != null) {
			for (int i = 0; i < java.lang.reflect.Array
					.getLength(getFormsRef()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(
						getFormsRef(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}

		if (getEventsRefsCollection() != null) {
			for (int i = 0; i < java.lang.reflect.Array
					.getLength(getEventsRefsCollection()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(
						getEventsRefsCollection(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		
		_hashCode += new Float(getFieldOrder()).hashCode();

		if (getFormName() != null) {
			_hashCode += getFormName().hashCode();
		}
		
		if (getProjectsFormsRef() != null) {
			_hashCode += getProjectsFormsRef().hashCode();
		}
		
		if (getBranchingLogic() != null) {
			_hashCode += getBranchingLogic().hashCode();
		}
		if (getElementEnum() != null) {
			_hashCode += getElementEnum().hashCode();
		}
		if (getElementLabel() != null) {
			_hashCode += getElementLabel().hashCode();
		}
		if (getElementNote() != null) {
			_hashCode += getElementNote().hashCode();
		}
		if (getElementPreceedingHeader() != null) {
			_hashCode += getElementPreceedingHeader().hashCode();
		}
		if (getElementType() != null) {
			_hashCode += getElementType().hashCode();
		}
		if (getElementValidationCheck() != null) {
			_hashCode += getElementValidationCheck().hashCode();
		}
		if (getElementValidationMax() != null) {
			_hashCode += getElementValidationMax().hashCode();
		}
		if (getElementValidationMin() != null) {
			_hashCode += getElementValidationMin().hashCode();
		}
		if (getElementValidationType() != null) {
			_hashCode += getElementValidationType().hashCode();
		}
		_hashCode += getFieldReq();
		if (getFieldUnits() != null) {
			_hashCode += getFieldUnits().hashCode();
		}
		if (getFormMenuDescription() != null) {
			_hashCode += getFormMenuDescription().hashCode();
		}
		if (getFormName() != null) {
			_hashCode += getFormName().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

}
