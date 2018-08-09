package com.oracle.eloqua.appframework.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.oracle.eloqua.appframework.enums.RecordStatus;

@Entity
public class SMSActivity {

	private String campaignName;

	private String campaignRegion;

	private String contactId;

	private String emailAddress;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String instanceId;

	private String message;

	private String mobileNumber;

	private int executionId;

	@Enumerated(EnumType.STRING)
	private RecordStatus status;

	@CreationTimestamp
	private Date createDateTime;

	@UpdateTimestamp
	private Date updateDateTime;

	public SMSActivity() {
	}

	public String getCampaignName() {
		return campaignName;
	}

	public String getCampaignRegion() {
		return campaignRegion;
	}

	public String getContactId() {
		return contactId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public Long getId() {
		return id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getMessage() {
		return message;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public void setCampaignRegion(String campaignRegion) {
		this.campaignRegion = campaignRegion;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setStatus(RecordStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return String.format("SMSActivity [instanceId=%s, mobileNumber=%s, message=%s]", instanceId, mobileNumber,
				message);
	}

	public int getExecutionId() {
		return executionId;
	}

	public void setExecutionId(int executionId) {
		this.executionId = executionId;
	}

}