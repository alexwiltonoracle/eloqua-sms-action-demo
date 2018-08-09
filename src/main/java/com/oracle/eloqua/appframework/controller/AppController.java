package com.oracle.eloqua.appframework.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oracle.eloqua.appframework.auth.OAuth2SessionManager;
import com.oracle.eloqua.appframework.util.Util;

// Handles App level webhook calls from Eloqua.
@RestController
@RequestMapping("/app")
public class AppController {

	private static final Logger log = LoggerFactory.getLogger(AppController.class);

	@Autowired
	private OAuth2SessionManager oAuth2SessionManager;

	@RequestMapping("/enable")
	public void enable(String installId, String appId, String callbackUrl, String userName,
			HttpServletResponse response, HttpServletRequest request) throws IOException {
		Util.logIncomingRequest(log, request);

		log.info("Enabling app...");

		log.info("   installId    : " + installId);
		log.info("   appId        : " + appId);
		log.info("   userName     : " + userName);
		log.info("   callbackUrl  : " + callbackUrl);

		oAuth2SessionManager.startInstall(installId, appId, userName, callbackUrl);

		response.sendRedirect(oAuth2SessionManager.authorizeUrl(installId));

	}

	@RequestMapping("/configure")
	public String configure(String installId, String appId, HttpServletRequest request) {
		Util.logIncomingRequest(log, request);
		log.info("Configuring app...");

		return "";

	}

	@RequestMapping("/uninstall")
	public String uninstall(String installId, String appId, HttpServletRequest request) {
		Util.logIncomingRequest(log, request);
		log.info("Uninstalling app...");
		return "";

	}

	@RequestMapping("/status")
	public String status(HttpServletRequest request) {
		Util.logIncomingRequest(log, request);
		log.info("App status...");
		return "";

	}

}