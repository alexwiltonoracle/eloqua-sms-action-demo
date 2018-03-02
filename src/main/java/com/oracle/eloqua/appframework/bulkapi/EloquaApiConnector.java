package com.oracle.eloqua.appframework.bulkapi;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.oracle.eloqua.appframework.auth.OAuth2SessionManager;

@Component
public class EloquaApiConnector {

	private static final Logger log = LoggerFactory.getLogger(EloquaApiConnector.class);

	private String baseUrl = "https://secure.p04.eloqua.com/api/";

	@Autowired
	private OAuth2SessionManager oAuth2SessionManager;

	public String doPost(String body, String path, String installId) throws JSONException {

		logDivide();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders requestHeaders = oAuth2SessionManager.headersForInstallBasedRequest(installId);

		HttpEntity<String> requestEntity = new HttpEntity<String>(body, requestHeaders);

		String url = baseUrl + "bulk/2.0/" + path;

		log("Calling POST");
		log("   > URL            : " + url);
		log("   > Request Body   : " + body);

		ResponseEntity<String> createResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
				String.class);

		log("POST Response");
		log("   > Status         : " + createResponse.getStatusCodeValue());
		log("   > Response Body  : " + createResponse.getBody());

		logDivide();

		return createResponse.getBody();

	}

	public String doGet(String path, String installId) throws JSONException {

		logDivide();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders requestHeaders = oAuth2SessionManager.headersForInstallBasedRequest(installId);

		HttpEntity<String> requestEntity = new HttpEntity<String>(requestHeaders);

		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		String url = baseUrl + "bulk/2.0" + path;

		log("Calling GET");
		log("   > URL           : " + url);

		ResponseEntity<String> createResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		log("GET Response");
		log("   > Status        : " + createResponse.getStatusCodeValue());
		log("   > Response Body : " + createResponse.getBody());

		logDivide();

		return createResponse.getBody();

	}

	private void log(String msg) {
		log.info("EloquaApi: " + msg);
	}

	private void logDivide() {
		log("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
	}

}
