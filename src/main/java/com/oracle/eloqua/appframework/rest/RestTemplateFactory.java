package com.oracle.eloqua.appframework.rest;

import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory {
	public static RestTemplate newInstance() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new RestErrorHandler());
		return restTemplate;
	}

}
