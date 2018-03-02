package com.oracle.eloqua.appframework.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.oracle.eloqua.appframework.enums.RecordStatus;

@Entity
public class SMSActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String mobileNumber;
	
	private String emailAddress;
	
	private String contactId;

	private String message;

	private String instanceId;
	
	@Enumerated(EnumType.STRING)
	private RecordStatus status;

	public SMSActivity() {
	}

	@Override
	public String toString() {
		return String.format("SMSActivity [instanceId=%s, mobileNumber=%s, message=%s]", instanceId, mobileNumber, message);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RecordStatus getStatus() {
		return status;
	}

	public void setStatus(RecordStatus status) {
		this.status = status;
	}

}