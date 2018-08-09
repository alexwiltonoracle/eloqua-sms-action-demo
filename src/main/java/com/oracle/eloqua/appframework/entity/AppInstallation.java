package com.oracle.eloqua.appframework.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
public class AppInstallation {

	@Id
	private String installId;
	private String appId;
	private String userName;
	private String eloquaCallbackUrl;
	private String accessToken;
	private String refreshToken;

	@CreationTimestamp
	private Date createDateTime;

	@UpdateTimestamp
	private Date updateDateTime;

	public AppInstallation() {
	}

	@Override
	public String toString() {
		return String.format("Instance [installId=%s, accessToken=%s, refreshToken=%s]", installId, accessToken,
				refreshToken);
	}

	public String getInstallId() {
		return installId;
	}

	public void setInstallId(String installId) {
		this.installId = installId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getEloquaCallbackUrl() {
		return eloquaCallbackUrl;
	}

	public void setEloquaCallbackUrl(String eloquaCallbackUrl) {
		this.eloquaCallbackUrl = eloquaCallbackUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}