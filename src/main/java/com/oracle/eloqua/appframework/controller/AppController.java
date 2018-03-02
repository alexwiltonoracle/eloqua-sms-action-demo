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
	public void enable(String installId, String appId, String callbackUrl, HttpServletResponse response,
			HttpServletRequest request) throws IOException {

		log.info("Enabling app...");
		Util.logIncomingRequest(log, request);

		log.info("   installId    : " + installId);
		log.info("   appId        : " + appId);
		log.info("   callbackUrl  : " + callbackUrl);

		oAuth2SessionManager.startInstall(installId, appId, callbackUrl);

		response.sendRedirect(oAuth2SessionManager.authorizeUrl(installId));

	}

	@RequestMapping("/configure")
	public String configure(String installId, String appId, HttpServletRequest request) {
		log.info("Configuring app...");
		Util.logIncomingRequest(log, request);
		return "";

	}

	@RequestMapping("/uninstall")
	public String uninstall(String installId, String appId, HttpServletRequest request) {
		log.info("Uninstalling app...");
		Util.logIncomingRequest(log, request);
		return "";

	}

	@RequestMapping("/status")
	public String status(HttpServletRequest request) {
		log.info("App status...");
		Util.logIncomingRequest(log, request);
		return "";

	}

}