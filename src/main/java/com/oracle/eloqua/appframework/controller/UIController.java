package com.oracle.eloqua.appframework.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.oracle.eloqua.appframework.entity.SMSActivity;
import com.oracle.eloqua.appframework.enums.RecordStatus;
import com.oracle.eloqua.appframework.repository.SMSActivityRepository;
import com.oracle.eloqua.appframework.service.ActionServiceInstancePool;

//handles service level webhook calls from Eloqua
@Controller
@RequestMapping("/ui")
public class UIController {

	@Autowired
	ActionServiceInstancePool actionServicePool;

	@Autowired
	SMSActivityRepository smsActivityRepository;

	private static final Logger log = LoggerFactory.getLogger(UIController.class);

	@RequestMapping(value = "/smsActivities", method = RequestMethod.GET)
	public String loadForm(HttpServletRequest request, String instanceId, Model model) {

		Iterable<SMSActivity> smsActivities = smsActivityRepository.findAll();

		model.addAttribute("smsActivities", smsActivities);

		return "smsActivities";
	}

	@RequestMapping(value = "/completeAll", method = RequestMethod.GET)
	public String completeAll(HttpServletRequest request, String instanceId, Model model) {
		List<SMSActivity> smsActivities = smsActivityRepository.findByStatus(RecordStatus.NEW);

		for (SMSActivity smsActivity : smsActivities) {
			log.info("Completing SMS Send: " + smsActivity.toString());
			smsActivity.setStatus(RecordStatus.SMS_SENT);
			smsActivityRepository.save(smsActivity);
		}

		return "redirect:/ui/smsActivities";
	}

}