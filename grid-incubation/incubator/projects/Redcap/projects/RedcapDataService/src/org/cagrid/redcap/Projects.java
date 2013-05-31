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
/**
 * Projects.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Apr 28, 2006 (12:42:00 EDT) WSDL2Java emitter.
 */

package org.cagrid.redcap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name = "redcap_projects")

 public class Projects  implements java.io.Serializable {
	
	
    private org.cagrid.redcap.EventArms[] eventArmsCollection;
	
    private org.cagrid.redcap.EventsCalendar[] eventsCalendarRef;
	
	private org.cagrid.redcap.Forms[] formsRefCollection;
   
	private int allowPkEdit;

	private java.lang.String appTitle;

	private java.lang.String authMeth;

	private int autoIncSet;

	private java.lang.String contextDetail;

	private int countProject;

	private String createdBy;

	private java.util.Date creationTime;

	private java.lang.String customDataEntryNote;

	private java.lang.String customIndexPageNote;

	private String customReports;

	private java.lang.String customText1;

	private java.lang.String customText2;

	private int dateShiftMax;

	private int doubleDataEntry;

	private int draftMode;

	private int enableAlterRecordPulldown;

	private java.lang.String grantCite;

	private java.lang.String headerLogo;

	private java.util.Date inactiveTime;

	private java.lang.String institution;

	private java.lang.String investigators;

	private java.lang.String isChildOf;

	private int online_offline;

	private java.lang.String orderIdBy;

	private java.util.Date productionTime;

	private java.lang.String projectContactEmail;

	private java.lang.String projectContactProdChangesName;

	private int projectId;

	private java.lang.String projectName;

	private java.lang.String projectNote;

	private java.lang.String pulldownConcatItem1;

	private java.lang.String pulldownConcatItem2;

	private java.lang.String pulldownConcatItem3;

	private java.lang.String purpose;

	private java.lang.String purposeOther;

	private int recordDeleteFlag;

	private java.lang.String recordSelect1;

	private java.lang.String recordSelect2;

	private int repeatForms;

	private java.lang.String reportBuilder;

	private java.lang.String salt;

	private int scheduling;

	private int showWhichRecords;

	private java.lang.String siteOrgType;

	private int status;

    public Projects() {
    }

    public Projects(
           int allowPkEdit,
           java.lang.String appTitle,
           java.lang.String authMeth,
           int autoIncSet,
           java.lang.String contextDetail,
           int countProject,
           java.lang.String createdBy,
           java.util.Date creationTime,
           java.lang.String customDataEntryNote,
           java.lang.String customIndexPageNote,
           java.lang.String customReports,
           java.lang.String customText1,
           java.lang.String customText2,
           int dateShiftMax,
           int doubleDataEntry,
           int draftMode,
           int enableAlterRecordPulldown,
           org.cagrid.redcap.EventArms[] eventArmsCollection,
           org.cagrid.redcap.EventsCalendar[] eventsCalendarRef,
           org.cagrid.redcap.Forms[] formsRefCollection,
           java.lang.String grantCite,
           java.lang.String headerLogo,
           java.util.Date inactiveTime,
           java.lang.String institution,
           java.lang.String investigators,
           java.lang.String isChildOf,
           int online_offline,
           java.lang.String orderIdBy,
           java.util.Date productionTime,
           java.lang.String projectContactEmail,
           java.lang.String projectContactProdChangesName,
           int projectId,
           java.lang.String projectName,
           java.lang.String projectNote,
           java.lang.String pulldownConcatItem1,
           java.lang.String pulldownConcatItem2,
           java.lang.String pulldownConcatItem3,
           java.lang.String purpose,
           java.lang.String purposeOther,
           int recordDeleteFlag,
           java.lang.String recordSelect1,
           java.lang.String recordSelect2,
           int repeatForms,
           java.lang.String reportBuilder,
           java.lang.String salt,
           int scheduling,
           int showWhichRecords,
           java.lang.String siteOrgType,
           int status) {
           this.eventArmsCollection = eventArmsCollection;
           this.eventsCalendarRef = eventsCalendarRef;
           this.formsRefCollection = formsRefCollection;
           this.allowPkEdit = allowPkEdit;
           this.appTitle = appTitle;
           this.authMeth = authMeth;
           this.autoIncSet = autoIncSet;
           this.contextDetail = contextDetail;
           this.countProject = countProject;
           this.createdBy = createdBy;
           this.creationTime = creationTime;
           this.customDataEntryNote = customDataEntryNote;
           this.customIndexPageNote = customIndexPageNote;
           this.customReports = customReports;
           this.customText1 = customText1;
           this.customText2 = customText2;
           this.dateShiftMax = dateShiftMax;
           this.doubleDataEntry = doubleDataEntry;
           this.draftMode = draftMode;
           this.enableAlterRecordPulldown = enableAlterRecordPulldown;
           this.grantCite = grantCite;
           this.headerLogo = headerLogo;
           this.inactiveTime = inactiveTime;
           this.institution = institution;
           this.investigators = investigators;
           this.isChildOf = isChildOf;
           this.online_offline = online_offline;
           this.orderIdBy = orderIdBy;
           this.productionTime = productionTime;
           this.projectContactEmail = projectContactEmail;
           this.projectContactProdChangesName = projectContactProdChangesName;
           this.projectId = projectId;
           this.projectName = projectName;
           this.projectNote = projectNote;
           this.pulldownConcatItem1 = pulldownConcatItem1;
           this.pulldownConcatItem2 = pulldownConcatItem2;
           this.pulldownConcatItem3 = pulldownConcatItem3;
           this.purpose = purpose;
           this.purposeOther = purposeOther;
           this.recordDeleteFlag = recordDeleteFlag;
           this.recordSelect1 = recordSelect1;
           this.recordSelect2 = recordSelect2;
           this.repeatForms = repeatForms;
           this.reportBuilder = reportBuilder;
           this.salt = salt;
           this.scheduling = scheduling;
           this.showWhichRecords = showWhichRecords;
           this.siteOrgType = siteOrgType;
           this.status = status;
    }
    
	//BIDIRECTIONAL
    @OneToMany(mappedBy="projectsRef")
    @IndexColumn(name = "arm_id")
    
    public org.cagrid.redcap.EventArms[] getEventArmsCollection() {
        return eventArmsCollection;
    }


    public void setEventArmsCollection(org.cagrid.redcap.EventArms[] eventArmsCollection) {
        this.eventArmsCollection = eventArmsCollection;
    }

    public org.cagrid.redcap.EventArms getEventArmsCollection(int i) {
        return this.eventArmsCollection[i];
    }

    public void setEventArmsCollection(int i, org.cagrid.redcap.EventArms _value) {
        this.eventArmsCollection[i] = _value;
    }


    //BIDIRECTIONAL
    @OneToMany(mappedBy="projectsCalendarRef")
    @IndexColumn(name="cal_id")
    public org.cagrid.redcap.EventsCalendar[] getEventsCalendarRef() {
        return eventsCalendarRef;
    }


    public void setEventsCalendarRef(org.cagrid.redcap.EventsCalendar[] eventsCalendarRef) {
        this.eventsCalendarRef = eventsCalendarRef;
    }

    public org.cagrid.redcap.EventsCalendar getEventsCalendarRef(int i) {
        return this.eventsCalendarRef[i];
    }

    public void setEventsCalendarRef(int i, org.cagrid.redcap.EventsCalendar _value) {
        this.eventsCalendarRef[i] = _value;
    }

	//BIDIRECTIONAL projects-forms
	@OneToMany(mappedBy="projectsFormsRef")
	@IndexColumn(name="field_order")
    public org.cagrid.redcap.Forms[] getFormsRefCollection() {
        return formsRefCollection;
    }


    public void setFormsRefCollection(org.cagrid.redcap.Forms[] formsRefCollection) {
        this.formsRefCollection = formsRefCollection;
    }

    public org.cagrid.redcap.Forms getFormsRefCollection(int i) {
        return this.formsRefCollection[i];
    }

    public void setFormsRefCollection(int i, org.cagrid.redcap.Forms _value) {
        this.formsRefCollection[i] = _value;
    }

    @Column(name = "allow_pk_edit")
    public int getAllowPkEdit() {
        return allowPkEdit;
    }


    public void setAllowPkEdit(int allowPkEdit) {
        this.allowPkEdit = allowPkEdit;
    }


    @Column(name = "app_title")
    public java.lang.String getAppTitle() {
        return appTitle;
    }


    public void setAppTitle(java.lang.String appTitle) {
        this.appTitle = appTitle;
    }


    @Column(name = "auth_meth")
    public java.lang.String getAuthMeth() {
        return authMeth;
    }


    public void setAuthMeth(java.lang.String authMeth) {
        this.authMeth = authMeth;
    }


    @Column(name = "auto_inc_set")
    public int getAutoIncSet() {
        return autoIncSet;
    }

    public void setAutoIncSet(int autoIncSet) {
        this.autoIncSet = autoIncSet;
    }


    @Column(name = "context_detail")
    public java.lang.String getContextDetail() {
        return contextDetail;
    }


    public void setContextDetail(java.lang.String contextDetail) {
        this.contextDetail = contextDetail;
    }


    @Column(name = "count_project")
    public int getCountProject() {
        return countProject;
    }


    public void setCountProject(int countProject) {
        this.countProject = countProject;
    }


    @Column(name = "created_by")
    public java.lang.String getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(java.lang.String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "creation_time")
    public java.util.Date getCreationTime() {
        return creationTime;
    }


    public void setCreationTime(java.util.Date creationTime) {
        this.creationTime = creationTime;
    }

    @Column(name = "custom_data_entry_note")
    public java.lang.String getCustomDataEntryNote() {
        return customDataEntryNote;
    }


    public void setCustomDataEntryNote(java.lang.String customDataEntryNote) {
        this.customDataEntryNote = customDataEntryNote;
    }


    @Column(name = "custom_index_page_note")
    public java.lang.String getCustomIndexPageNote() {
        return customIndexPageNote;
    }


    public void setCustomIndexPageNote(java.lang.String customIndexPageNote) {
        this.customIndexPageNote = customIndexPageNote;
    }


    @Column(name = "custom_reports")
    public java.lang.String getCustomReports() {
        return customReports;
    }


    public void setCustomReports(java.lang.String customReports) {
        this.customReports = customReports;
    }


    @Column(name = "custom_text1")
    public java.lang.String getCustomText1() {
        return customText1;
    }


    public void setCustomText1(java.lang.String customText1) {
        this.customText1 = customText1;
    }


    @Column(name = "custom_text2")
    public java.lang.String getCustomText2() {
        return customText2;
    }


    /**
     * Sets the customText2 value for this Projects.
     * 
     * @param customText2
     */
    public void setCustomText2(java.lang.String customText2) {
        this.customText2 = customText2;
    }


    /**
     * Gets the dateShiftMax value for this Projects.
     * 
     * @return dateShiftMax
     */
    @Column(name = "date_shift_max")
    public int getDateShiftMax() {
        return dateShiftMax;
    }


    /**
     * Sets the dateShiftMax value for this Projects.
     * 
     * @param dateShiftMax
     */
    public void setDateShiftMax(int dateShiftMax) {
        this.dateShiftMax = dateShiftMax;
    }


    /**
     * Gets the doubleDataEntry value for this Projects.
     * 
     * @return doubleDataEntry
     */
    @Column(name = "double_data_entry")
    public int getDoubleDataEntry() {
        return doubleDataEntry;
    }


    /**
     * Sets the doubleDataEntry value for this Projects.
     * 
     * @param doubleDataEntry
     */
    public void setDoubleDataEntry(int doubleDataEntry) {
        this.doubleDataEntry = doubleDataEntry;
    }


    /**
     * Gets the draftMode value for this Projects.
     * 
     * @return draftMode
     */
    @Column(name = "draft_mode")
    public int getDraftMode() {
        return draftMode;
    }


    /**
     * Sets the draftMode value for this Projects.
     * 
     * @param draftMode
     */
    public void setDraftMode(int draftMode) {
        this.draftMode = draftMode;
    }


    /**
     * Gets the enableAlterRecordPulldown value for this Projects.
     * 
     * @return enableAlterRecordPulldown
     */
    @Column(name = "enable_alter_record_pulldown")
    public int getEnableAlterRecordPulldown() {
        return enableAlterRecordPulldown;
    }


    /**
     * Sets the enableAlterRecordPulldown value for this Projects.
     * 
     * @param enableAlterRecordPulldown
     */
    public void setEnableAlterRecordPulldown(int enableAlterRecordPulldown) {
        this.enableAlterRecordPulldown = enableAlterRecordPulldown;
    }


    /**
     * Gets the grantCite value for this Projects.
     * 
     * @return grantCite
     */
    @Column(name = "grant_cite")
    public java.lang.String getGrantCite() {
        return grantCite;
    }


    /**
     * Sets the grantCite value for this Projects.
     * 
     * @param grantCite
     */
    public void setGrantCite(java.lang.String grantCite) {
        this.grantCite = grantCite;
    }


    /**
     * Gets the headerLogo value for this Projects.
     * 
     * @return headerLogo
     */
    @Column(name = "headerlogo")
    public java.lang.String getHeaderLogo() {
        return headerLogo;
    }


    /**
     * Sets the headerLogo value for this Projects.
     * 
     * @param headerLogo
     */
    public void setHeaderLogo(java.lang.String headerLogo) {
        this.headerLogo = headerLogo;
    }


    /**
     * Gets the inactiveTime value for this Projects.
     * 
     * @return inactiveTime
     */
    @Column(name = "inactive_time")
    public java.util.Date getInactiveTime() {
        return inactiveTime;
    }


    /**
     * Sets the inactiveTime value for this Projects.
     * 
     * @param inactiveTime
     */
    public void setInactiveTime(java.util.Date inactiveTime) {
        this.inactiveTime = inactiveTime;
    }


    /**
     * Gets the institution value for this Projects.
     * 
     * @return institution
     */
    @Column(name = "institution")
    public java.lang.String getInstitution() {
        return institution;
    }


    /**
     * Sets the institution value for this Projects.
     * 
     * @param institution
     */
    public void setInstitution(java.lang.String institution) {
        this.institution = institution;
    }


    /**
     * Gets the investigators value for this Projects.
     * 
     * @return investigators
     */
    @Column(name = "investigators")
    public java.lang.String getInvestigators() {
        return investigators;
    }


    /**
     * Sets the investigators value for this Projects.
     * 
     * @param investigators
     */
    public void setInvestigators(java.lang.String investigators) {
        this.investigators = investigators;
    }


    /**
     * Gets the isChildOf value for this Projects.
     * 
     * @return isChildOf
     */
    @Column(name = "is_child_of")
    public java.lang.String getIsChildOf() {
        return isChildOf;
    }


    /**
     * Sets the isChildOf value for this Projects.
     * 
     * @param isChildOf
     */
    public void setIsChildOf(java.lang.String isChildOf) {
        this.isChildOf = isChildOf;
    }


    /**
     * Gets the online_offline value for this Projects.
     * 
     * @return online_offline
     */
    @Column(name = "online_offline")
    public int getOnline_offline() {
        return online_offline;
    }


    /**
     * Sets the online_offline value for this Projects.
     * 
     * @param online_offline
     */
    public void setOnline_offline(int online_offline) {
        this.online_offline = online_offline;
    }


    /**
     * Gets the orderIdBy value for this Projects.
     * 
     * @return orderIdBy
     */
    @Column(name = "order_id_by")
    public java.lang.String getOrderIdBy() {
        return orderIdBy;
    }


    /**
     * Sets the orderIdBy value for this Projects.
     * 
     * @param orderIdBy
     */
    public void setOrderIdBy(java.lang.String orderIdBy) {
        this.orderIdBy = orderIdBy;
    }


    /**
     * Gets the productionTime value for this Projects.
     * 
     * @return productionTime
     */
    @Column(name = "production_time")
    public java.util.Date getProductionTime() {
        return productionTime;
    }


    /**
     * Sets the productionTime value for this Projects.
     * 
     * @param productionTime
     */
    public void setProductionTime(java.util.Date productionTime) {
        this.productionTime = productionTime;
    }


    /**
     * Gets the projectContactEmail value for this Projects.
     * 
     * @return projectContactEmail
     */
    @Column(name = "project_contact_email")
    public java.lang.String getProjectContactEmail() {
        return projectContactEmail;
    }


    /**
     * Sets the projectContactEmail value for this Projects.
     * 
     * @param projectContactEmail
     */
    public void setProjectContactEmail(java.lang.String projectContactEmail) {
        this.projectContactEmail = projectContactEmail;
    }


    /**
     * Gets the projectContactProdChangesName value for this Projects.
     * 
     * @return projectContactProdChangesName
     */
	@Column(name = "project_contact_prod_changes_name")
    public java.lang.String getProjectContactProdChangesName() {
        return projectContactProdChangesName;
    }


    /**
     * Sets the projectContactProdChangesName value for this Projects.
     * 
     * @param projectContactProdChangesName
     */
    public void setProjectContactProdChangesName(java.lang.String projectContactProdChangesName) {
        this.projectContactProdChangesName = projectContactProdChangesName;
    }


    /**
     * Gets the projectId value for this Projects.
     * 
     * @return projectId
     */

	@Id
	@Column(name = "project_id")
    public int getProjectId() {
        return projectId;
    }


    /**
     * Sets the projectId value for this Projects.
     * 
     * @param projectId
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    /**
     * Gets the projectName value for this Projects.
     * 
     * @return projectName
     */
    @Column(name = "project_name")
    public java.lang.String getProjectName() {
        return projectName;
    }


    /**
     * Sets the projectName value for this Projects.
     * 
     * @param projectName
     */
    public void setProjectName(java.lang.String projectName) {
        this.projectName = projectName;
    }


    /**
     * Gets the projectNote value for this Projects.
     * 
     * @return projectNote
     */
    @Column(name = "project_note")
    public java.lang.String getProjectNote() {
        return projectNote;
    }


    /**
     * Sets the projectNote value for this Projects.
     * 
     * @param projectNote
     */
    public void setProjectNote(java.lang.String projectNote) {
        this.projectNote = projectNote;
    }


    /**
     * Gets the pulldownConcatItem1 value for this Projects.
     * 
     * @return pulldownConcatItem1
     */
    @Column(name = "pulldown_concat_item1")
    public java.lang.String getPulldownConcatItem1() {
        return pulldownConcatItem1;
    }


    /**
     * Sets the pulldownConcatItem1 value for this Projects.
     * 
     * @param pulldownConcatItem1
     */
    public void setPulldownConcatItem1(java.lang.String pulldownConcatItem1) {
        this.pulldownConcatItem1 = pulldownConcatItem1;
    }


    /**
     * Gets the pulldownConcatItem2 value for this Projects.
     * 
     * @return pulldownConcatItem2
     */
    @Column(name = "pulldown_concat_item2")
    public java.lang.String getPulldownConcatItem2() {
        return pulldownConcatItem2;
    }


    /**
     * Sets the pulldownConcatItem2 value for this Projects.
     * 
     * @param pulldownConcatItem2
     */
    public void setPulldownConcatItem2(java.lang.String pulldownConcatItem2) {
        this.pulldownConcatItem2 = pulldownConcatItem2;
    }


    /**
     * Gets the pulldownConcatItem3 value for this Projects.
     * 
     * @return pulldownConcatItem3
     */
    @Column(name = "pulldown_concat_item3")
    public java.lang.String getPulldownConcatItem3() {
        return pulldownConcatItem3;
    }


    /**
     * Sets the pulldownConcatItem3 value for this Projects.
     * 
     * @param pulldownConcatItem3
     */
    public void setPulldownConcatItem3(java.lang.String pulldownConcatItem3) {
        this.pulldownConcatItem3 = pulldownConcatItem3;
    }


    /**
     * Gets the purpose value for this Projects.
     * 
     * @return purpose
     */
    @Column(name = "purpose")
    public java.lang.String getPurpose() {
        return purpose;
    }


    /**
     * Sets the purpose value for this Projects.
     * 
     * @param purpose
     */
    public void setPurpose(java.lang.String purpose) {
        this.purpose = purpose;
    }


    /**
     * Gets the purposeOther value for this Projects.
     * 
     * @return purposeOther
     */
    @Column(name = "purpose_other")
    public java.lang.String getPurposeOther() {
        return purposeOther;
    }


    /**
     * Sets the purposeOther value for this Projects.
     * 
     * @param purposeOther
     */
    public void setPurposeOther(java.lang.String purposeOther) {
        this.purposeOther = purposeOther;
    }


    /**
     * Gets the recordDeleteFlag value for this Projects.
     * 
     * @return recordDeleteFlag
     */
    @Column(name = "record_delete_flag")
    public int getRecordDeleteFlag() {
        return recordDeleteFlag;
    }


    /**
     * Sets the recordDeleteFlag value for this Projects.
     * 
     * @param recordDeleteFlag
     */
    public void setRecordDeleteFlag(int recordDeleteFlag) {
        this.recordDeleteFlag = recordDeleteFlag;
    }


    /**
     * Gets the recordSelect1 value for this Projects.
     * 
     * @return recordSelect1
     */
    @Column(name = "record_select1")
    public java.lang.String getRecordSelect1() {
        return recordSelect1;
    }


    /**
     * Sets the recordSelect1 value for this Projects.
     * 
     * @param recordSelect1
     */
    public void setRecordSelect1(java.lang.String recordSelect1) {
        this.recordSelect1 = recordSelect1;
    }


    /**
     * Gets the recordSelect2 value for this Projects.
     * 
     * @return recordSelect2
     */
    @Column(name = "record_select2")
    public java.lang.String getRecordSelect2() {
        return recordSelect2;
    }


    /**
     * Sets the recordSelect2 value for this Projects.
     * 
     * @param recordSelect2
     */
    public void setRecordSelect2(java.lang.String recordSelect2) {
        this.recordSelect2 = recordSelect2;
    }


    /**
     * Gets the repeatForms value for this Projects.
     * 
     * @return repeatForms
     */
    @Column(name = "repeatforms")
    public int getRepeatForms() {
        return repeatForms;
    }


    /**
     * Sets the repeatForms value for this Projects.
     * 
     * @param repeatForms
     */
    public void setRepeatForms(int repeatForms) {
        this.repeatForms = repeatForms;
    }


    /**
     * Gets the reportBuilder value for this Projects.
     * 
     * @return reportBuilder
     */
    @Column(name = "report_builder")
    public java.lang.String getReportBuilder() {
        return reportBuilder;
    }


    /**
     * Sets the reportBuilder value for this Projects.
     * 
     * @param reportBuilder
     */
    public void setReportBuilder(java.lang.String reportBuilder) {
        this.reportBuilder = reportBuilder;
    }


    /**
     * Gets the salt value for this Projects.
     * 
     * @return salt
     */
    @Column(name = "__SALT__")
    public java.lang.String getSalt() {
        return salt;
    }


    /**
     * Sets the salt value for this Projects.
     * 
     * @param salt
     */
    public void setSalt(java.lang.String salt) {
        this.salt = salt;
    }


    /**
     * Gets the scheduling value for this Projects.
     * 
     * @return scheduling
     */
    @Column(name = "scheduling")
    public int getScheduling() {
        return scheduling;
    }


    /**
     * Sets the scheduling value for this Projects.
     * 
     * @param scheduling
     */
    public void setScheduling(int scheduling) {
        this.scheduling = scheduling;
    }


    /**
     * Gets the showWhichRecords value for this Projects.
     * 
     * @return showWhichRecords
     */
    @Column(name = "show_which_records")
    public int getShowWhichRecords() {
        return showWhichRecords;
    }


    /**
     * Sets the showWhichRecords value for this Projects.
     * 
     * @param showWhichRecords
     */
    public void setShowWhichRecords(int showWhichRecords) {
        this.showWhichRecords = showWhichRecords;
    }


    /**
     * Gets the siteOrgType value for this Projects.
     * 
     * @return siteOrgType
     */
    @Column(name = "site_org_type")
    public java.lang.String getSiteOrgType() {
        return siteOrgType;
    }


    /**
     * Sets the siteOrgType value for this Projects.
     * 
     * @param siteOrgType
     */
    public void setSiteOrgType(java.lang.String siteOrgType) {
        this.siteOrgType = siteOrgType;
    }


    /**
     * Gets the status value for this Projects.
     * 
     * @return status
     */
    @Column(name = "status")
    public int getStatus() {
        return status;
    }


    /**
     * Sets the status value for this Projects.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    @Transient
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Projects)) return false;
        Projects other = (Projects) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eventArmsCollection==null && other.getEventArmsCollection()==null) || 
             (this.eventArmsCollection!=null &&
              java.util.Arrays.equals(this.eventArmsCollection, other.getEventArmsCollection()))) &&
            ((this.eventsCalendarRef==null && other.getEventsCalendarRef()==null) || 
             (this.eventsCalendarRef!=null &&
              java.util.Arrays.equals(this.eventsCalendarRef, other.getEventsCalendarRef()))) &&
            ((this.formsRefCollection==null && other.getFormsRefCollection()==null) || 
             (this.formsRefCollection!=null &&
              java.util.Arrays.equals(this.formsRefCollection, other.getFormsRefCollection()))) &&
            this.allowPkEdit == other.getAllowPkEdit() &&
            ((this.appTitle==null && other.getAppTitle()==null) || 
             (this.appTitle!=null &&
              this.appTitle.equals(other.getAppTitle()))) &&
            ((this.authMeth==null && other.getAuthMeth()==null) || 
             (this.authMeth!=null &&
              this.authMeth.equals(other.getAuthMeth()))) &&
            this.autoIncSet == other.getAutoIncSet() &&
            ((this.contextDetail==null && other.getContextDetail()==null) || 
             (this.contextDetail!=null &&
              this.contextDetail.equals(other.getContextDetail()))) &&
            this.countProject == other.getCountProject() &&
            ((this.createdBy==null && other.getCreatedBy()==null) || 
             (this.createdBy!=null &&
              this.createdBy.equals(other.getCreatedBy()))) &&
            ((this.creationTime==null && other.getCreationTime()==null) || 
             (this.creationTime!=null &&
              this.creationTime.equals(other.getCreationTime()))) &&
            ((this.customDataEntryNote==null && other.getCustomDataEntryNote()==null) || 
             (this.customDataEntryNote!=null &&
              this.customDataEntryNote.equals(other.getCustomDataEntryNote()))) &&
            ((this.customIndexPageNote==null && other.getCustomIndexPageNote()==null) || 
             (this.customIndexPageNote!=null &&
              this.customIndexPageNote.equals(other.getCustomIndexPageNote()))) &&
            ((this.customReports==null && other.getCustomReports()==null) || 
             (this.customReports!=null &&
              this.customReports.equals(other.getCustomReports()))) &&
            ((this.customText1==null && other.getCustomText1()==null) || 
             (this.customText1!=null &&
              this.customText1.equals(other.getCustomText1()))) &&
            ((this.customText2==null && other.getCustomText2()==null) || 
             (this.customText2!=null &&
              this.customText2.equals(other.getCustomText2()))) &&
            this.dateShiftMax == other.getDateShiftMax() &&
            this.doubleDataEntry == other.getDoubleDataEntry() &&
            this.draftMode == other.getDraftMode() &&
            this.enableAlterRecordPulldown == other.getEnableAlterRecordPulldown() &&
            ((this.grantCite==null && other.getGrantCite()==null) || 
             (this.grantCite!=null &&
              this.grantCite.equals(other.getGrantCite()))) &&
            ((this.headerLogo==null && other.getHeaderLogo()==null) || 
             (this.headerLogo!=null &&
              this.headerLogo.equals(other.getHeaderLogo()))) &&
            ((this.inactiveTime==null && other.getInactiveTime()==null) || 
             (this.inactiveTime!=null &&
              this.inactiveTime.equals(other.getInactiveTime()))) &&
            ((this.institution==null && other.getInstitution()==null) || 
             (this.institution!=null &&
              this.institution.equals(other.getInstitution()))) &&
            ((this.investigators==null && other.getInvestigators()==null) || 
             (this.investigators!=null &&
              this.investigators.equals(other.getInvestigators()))) &&
            ((this.isChildOf==null && other.getIsChildOf()==null) || 
             (this.isChildOf!=null &&
              this.isChildOf.equals(other.getIsChildOf()))) &&
            this.online_offline == other.getOnline_offline() &&
            ((this.orderIdBy==null && other.getOrderIdBy()==null) || 
             (this.orderIdBy!=null &&
              this.orderIdBy.equals(other.getOrderIdBy()))) &&
            ((this.productionTime==null && other.getProductionTime()==null) || 
             (this.productionTime!=null &&
              this.productionTime.equals(other.getProductionTime()))) &&
            ((this.projectContactEmail==null && other.getProjectContactEmail()==null) || 
             (this.projectContactEmail!=null &&
              this.projectContactEmail.equals(other.getProjectContactEmail()))) &&
            ((this.projectContactProdChangesName==null && other.getProjectContactProdChangesName()==null) || 
             (this.projectContactProdChangesName!=null &&
              this.projectContactProdChangesName.equals(other.getProjectContactProdChangesName()))) &&
            this.projectId == other.getProjectId() &&
            ((this.projectName==null && other.getProjectName()==null) || 
             (this.projectName!=null &&
              this.projectName.equals(other.getProjectName()))) &&
            ((this.projectNote==null && other.getProjectNote()==null) || 
             (this.projectNote!=null &&
              this.projectNote.equals(other.getProjectNote()))) &&
            ((this.pulldownConcatItem1==null && other.getPulldownConcatItem1()==null) || 
             (this.pulldownConcatItem1!=null &&
              this.pulldownConcatItem1.equals(other.getPulldownConcatItem1()))) &&
            ((this.pulldownConcatItem2==null && other.getPulldownConcatItem2()==null) || 
             (this.pulldownConcatItem2!=null &&
              this.pulldownConcatItem2.equals(other.getPulldownConcatItem2()))) &&
            ((this.pulldownConcatItem3==null && other.getPulldownConcatItem3()==null) || 
             (this.pulldownConcatItem3!=null &&
              this.pulldownConcatItem3.equals(other.getPulldownConcatItem3()))) &&
            ((this.purpose==null && other.getPurpose()==null) || 
             (this.purpose!=null &&
              this.purpose.equals(other.getPurpose()))) &&
            ((this.purposeOther==null && other.getPurposeOther()==null) || 
             (this.purposeOther!=null &&
              this.purposeOther.equals(other.getPurposeOther()))) &&
            this.recordDeleteFlag == other.getRecordDeleteFlag() &&
            ((this.recordSelect1==null && other.getRecordSelect1()==null) || 
             (this.recordSelect1!=null &&
              this.recordSelect1.equals(other.getRecordSelect1()))) &&
            ((this.recordSelect2==null && other.getRecordSelect2()==null) || 
             (this.recordSelect2!=null &&
              this.recordSelect2.equals(other.getRecordSelect2()))) &&
            this.repeatForms == other.getRepeatForms() &&
            ((this.reportBuilder==null && other.getReportBuilder()==null) || 
             (this.reportBuilder!=null &&
              this.reportBuilder.equals(other.getReportBuilder()))) &&
            ((this.salt==null && other.getSalt()==null) || 
             (this.salt!=null &&
              this.salt.equals(other.getSalt()))) &&
            this.scheduling == other.getScheduling() &&
            this.showWhichRecords == other.getShowWhichRecords() &&
            ((this.siteOrgType==null && other.getSiteOrgType()==null) || 
             (this.siteOrgType!=null &&
              this.siteOrgType.equals(other.getSiteOrgType()))) &&
            this.status == other.getStatus();
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
        if (getEventArmsCollection() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEventArmsCollection());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEventArmsCollection(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEventsCalendarRef() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEventsCalendarRef());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEventsCalendarRef(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFormsRefCollection() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFormsRefCollection());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFormsRefCollection(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getAllowPkEdit();
        if (getAppTitle() != null) {
            _hashCode += getAppTitle().hashCode();
        }
        if (getAuthMeth() != null) {
            _hashCode += getAuthMeth().hashCode();
        }
        _hashCode += getAutoIncSet();
        if (getContextDetail() != null) {
            _hashCode += getContextDetail().hashCode();
        }
        _hashCode += getCountProject();
        if (getCreatedBy() != null) {
            _hashCode += getCreatedBy().hashCode();
        }
        if (getCreationTime() != null) {
            _hashCode += getCreationTime().hashCode();
        }
        if (getCustomDataEntryNote() != null) {
            _hashCode += getCustomDataEntryNote().hashCode();
        }
        if (getCustomIndexPageNote() != null) {
            _hashCode += getCustomIndexPageNote().hashCode();
        }
        if (getCustomReports() != null) {
            _hashCode += getCustomReports().hashCode();
        }
        if (getCustomText1() != null) {
            _hashCode += getCustomText1().hashCode();
        }
        if (getCustomText2() != null) {
            _hashCode += getCustomText2().hashCode();
        }
        _hashCode += getDateShiftMax();
        _hashCode += getDoubleDataEntry();
        _hashCode += getDraftMode();
        _hashCode += getEnableAlterRecordPulldown();
        if (getGrantCite() != null) {
            _hashCode += getGrantCite().hashCode();
        }
        if (getHeaderLogo() != null) {
            _hashCode += getHeaderLogo().hashCode();
        }
        if (getInactiveTime() != null) {
            _hashCode += getInactiveTime().hashCode();
        }
        if (getInstitution() != null) {
            _hashCode += getInstitution().hashCode();
        }
        if (getInvestigators() != null) {
            _hashCode += getInvestigators().hashCode();
        }
        if (getIsChildOf() != null) {
            _hashCode += getIsChildOf().hashCode();
        }
        _hashCode += getOnline_offline();
        if (getOrderIdBy() != null) {
            _hashCode += getOrderIdBy().hashCode();
        }
        if (getProductionTime() != null) {
            _hashCode += getProductionTime().hashCode();
        }
        if (getProjectContactEmail() != null) {
            _hashCode += getProjectContactEmail().hashCode();
        }
        if (getProjectContactProdChangesName() != null) {
            _hashCode += getProjectContactProdChangesName().hashCode();
        }
        _hashCode += getProjectId();
        if (getProjectName() != null) {
            _hashCode += getProjectName().hashCode();
        }
        if (getProjectNote() != null) {
            _hashCode += getProjectNote().hashCode();
        }
        if (getPulldownConcatItem1() != null) {
            _hashCode += getPulldownConcatItem1().hashCode();
        }
        if (getPulldownConcatItem2() != null) {
            _hashCode += getPulldownConcatItem2().hashCode();
        }
        if (getPulldownConcatItem3() != null) {
            _hashCode += getPulldownConcatItem3().hashCode();
        }
        if (getPurpose() != null) {
            _hashCode += getPurpose().hashCode();
        }
        if (getPurposeOther() != null) {
            _hashCode += getPurposeOther().hashCode();
        }
        _hashCode += getRecordDeleteFlag();
        if (getRecordSelect1() != null) {
            _hashCode += getRecordSelect1().hashCode();
        }
        if (getRecordSelect2() != null) {
            _hashCode += getRecordSelect2().hashCode();
        }
        _hashCode += getRepeatForms();
        if (getReportBuilder() != null) {
            _hashCode += getReportBuilder().hashCode();
        }
        if (getSalt() != null) {
            _hashCode += getSalt().hashCode();
        }
        _hashCode += getScheduling();
        _hashCode += getShowWhichRecords();
        if (getSiteOrgType() != null) {
            _hashCode += getSiteOrgType().hashCode();
        }
        _hashCode += getStatus();
        __hashCodeCalc = false;
        return _hashCode;
    }

}
