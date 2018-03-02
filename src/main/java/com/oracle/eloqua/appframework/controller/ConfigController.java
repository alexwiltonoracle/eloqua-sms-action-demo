package com.oracle.eloqua.appframework.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.oracle.eloqua.appframework.service.ActionServiceInstance;
import com.oracle.eloqua.appframework.service.ActionServiceInstancePool;
import com.oracle.eloqua.appframework.util.Util;

//handles service level webhook calls from Eloqua
@Controller
@RequestMapping("/service")
public class ConfigController {

	@Autowired
	ActionServiceInstancePool actionServicePool;

	private static final Logger log = LoggerFactory.getLogger(ConfigController.class);

	// Triggered when the configure button is pressed on the service instance
	@RequestMapping(value = "/smsService/configure", method = RequestMethod.GET)
	public String loadForm(HttpServletRequest request, String instanceId, Model model) {
		log.info("Load configuration form for instanceId: " + instanceId);

		Util.logIncomingRequest(log, request);
		
		ActionServiceInstance instance = actionServicePool.findServiceInstance(instanceId);
		
		model.addAttribute("instance", instance);

		return "configForm";
	}

	@RequestMapping(value = "/smsService/configure", method = RequestMethod.POST)
	public String submitForm(HttpServletRequest request, String instanceId, String smsBody, Model model) {
		log.info("Submit configuration form...");

		log.info("Saving configuration. InstanceId = " + instanceId + ", SMSBody=" + smsBody);

		ActionServiceInstance instance = actionServicePool.saveConfiguration(instanceId, smsBody);

		Util.logIncomingRequest(log, request);

		model.addAttribute("instance", instance);



		return "configClose";

	}

}