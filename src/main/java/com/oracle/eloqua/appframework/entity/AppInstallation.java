package com.oracle.eloqua.appframework.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AppInstallation {

	@Id
	private String installId;
	private String appId;
	private String eloquaCallbackUrl;
	private String accessToken;
	private String refreshToken;

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

}