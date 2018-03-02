package com.oracle.eloqua.appframework.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.oracle.eloqua.appframework.enums.InstanceStatus;
import com.oracle.eloqua.appframework.enums.ServiceType;

@Entity
public class InstanceConfiguration {

	@Id
	private String instanceId;
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType;
	@Enumerated(EnumType.STRING)
	private InstanceStatus instanceStatus;

	/* private String appId; */
	private String installId;
	private int siteId;
	private String userName;
	private String smsBody;

	public InstanceConfiguration() {
	}

	public InstanceConfiguration(ServiceType serviceType, InstanceStatus instanceStatus, String installId, String instanceId, int siteId, String userName) {
		this.installId = installId;
		this.instanceId = instanceId;
		// this.appId = params.getAppId();
		this.siteId = siteId;
		this.userName = userName;
		this.serviceType = serviceType;
	}

	@Override
	public String toString() {
		return String.format(
				"Instance [instanceId=%s, serviceType=%s, installId=%s, siteId=%s, userName=%s, smsBody=%s]",
				instanceId, serviceType, installId, siteId, userName, smsBody);
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	/*
	 * public String getAppId() { return appId; }
	 * 
	 * public void setAppId(String appId) { this.appId = appId; }
	 */

	public String getInstallId() {
		return installId;
	}

	public void setInstallId(String installId) {
		this.installId = installId;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public InstanceStatus getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(InstanceStatus instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public String getSmsBody() {
		return smsBody;
	}

	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
	}

}