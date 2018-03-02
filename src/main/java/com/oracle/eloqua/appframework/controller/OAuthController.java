package com.oracle.eloqua.appframework.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oracle.eloqua.appframework.auth.OAuth2SessionManager;
import com.oracle.eloqua.appframework.util.Util;

@Controller
@RequestMapping("/auth")
public class OAuthController {

	@Autowired
	private OAuth2SessionManager oAuth2SessionManager;

	private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

	private String appInstallUrl = "https://login.eloqua.com/Apps/Cloud/Admin/Catalog/Add/e0d9dfe3-3023-4253-8f13-d11ab40dd8c8/FE-CE-6B-0D-40-96-3E-FE-76-4B-DF-C4-F3-A6-AC-C5";

	// Redirects to the App Install Url in Eloqua
	@RequestMapping("/home")
	public String home(HttpServletRequest request) {

		log.info("App Home Page");
		Util.logIncomingRequest(log, request);

		return "redirect:" + appInstallUrl;

	}

	// Handle the callback from Eloqua once access has been authorised. Contains
	// the code that will be exchanged for an access_token and refresh_token
	@RequestMapping("/callback")
	public String callback(String code, String installId, HttpServletRequest request) {

		log.info("OAuth Callback");
		Util.logIncomingRequest(log, request);

		log.info("   code: " + code);

		String eloquaCallbackUrl = oAuth2SessionManager.doCodeExchange(code, installId);

		return "redirect:" + eloquaCallbackUrl;

	}

}