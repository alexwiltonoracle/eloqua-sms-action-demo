package com.oracle.eloqua.appframework.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oracle.eloqua.appframework.service.ActionServiceInstance;
import com.oracle.eloqua.appframework.service.ActionServiceInstancePool;
import com.oracle.eloqua.appframework.util.Util;

//handles service level webhook calls from Eloqua
@RestController
@RequestMapping("/service")
public class ActionServiceController {

	@Autowired
	ActionServiceInstancePool actionServicePool;

	private static final Logger log = LoggerFactory.getLogger(ActionServiceController.class);

	// Triggered when a new instances has been added to the campaign canvas
	@RequestMapping("/action/create")
	public String create(String installId, String instanceId, int siteId, String userName, HttpServletRequest request) {
		log.info("Incoming create request...");
		Util.logIncomingRequest(log, request);
		ActionServiceInstance service = actionServicePool.create(installId, instanceId, siteId, userName);
		JSONObject createResponse = service.buildCreateResponse();
		return createResponse.toString();
	}

	// Triggered when an event happens on the instance (activated, draft etc...)
	@RequestMapping("/action/notify")
	public String notify(HttpServletResponse response, String instanceId, @RequestBody String requestBody,
			HttpServletRequest request) {
		log.info("Incoming notify request...");
		Util.logIncomingRequest(log, request, requestBody);
		actionServicePool.notification(instanceId, requestBody);
		response.setStatus(HttpStatus.SC_NO_CONTENT);
		return null;
	}

	// triggered when the service instance is deleted
	@RequestMapping("/action/delete")
	public String delete(String instanceId, HttpServletRequest request) {
		log.info("Incoming delete request...");
		Util.logIncomingRequest(log, request);
		actionServicePool.delete(instanceId);
		return null;
	}

}