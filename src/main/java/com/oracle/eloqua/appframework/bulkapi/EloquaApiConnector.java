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
import com.oracle.eloqua.appframework.util.Util;

@Component
public class EloquaApiConnector {

	private static final Logger log = LoggerFactory.getLogger(EloquaApiConnector.class);

	private String baseUrl = "https://secure.p04.eloqua.com/api/";

	@Autowired
	private OAuth2SessionManager oAuth2SessionManager;

	public String doBulkPost(String body, String path, String installId) throws JSONException {

		String url = fullBulkPath(path);

		return eloquaApiExchange(installId, url, HttpMethod.POST, body);

	}

	public String doBulkGet(String path, String installId) throws JSONException {

		String url = fullBulkPath(path);

		return eloquaApiExchange(installId, url, HttpMethod.GET, null);

	}

	public String doRest(String version, String path, String installId, HttpMethod method, String body) {

		String url = fullRestPath(version, path);

		return eloquaApiExchange(installId, url, method, body);

	}

	private String eloquaApiExchange(String installId, String url, HttpMethod method, String body) {

		logDivide();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders requestHeaders = oAuth2SessionManager.headersForInstallBasedRequest(installId);

		HttpEntity<String> requestEntity;

		if (method.equals(HttpMethod.GET)) {
			requestEntity = new HttpEntity<String>(requestHeaders);
		} else {
			requestEntity = new HttpEntity<String>(body, requestHeaders);
		}

		log.info("Calling " + method);
		log.info("   > URL           : " + url);
		if (body != null) {
			Util.logPrettyJson(log, "   > Request Body   : ", body);
		}

		ResponseEntity<String> createResponse = restTemplate.exchange(url, method, requestEntity, String.class);

		log.info("GET Response");
		log.info("   > Status        : " + createResponse.getStatusCodeValue());
		Util.logPrettyJson(log, "   > Response Body : ", createResponse.getBody());

		logDivide();

		return createResponse.getBody();

	}

	private String fullBulkPath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		String url = baseUrl + "bulk/2.0" + path;
		return url;
	}

	private String fullRestPath(String version, String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		String url = baseUrl + "REST/" + version + path;
		return url;
	}

	private String fullCloudPath(String version, String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		String url = baseUrl + "cloud/" + version + path;
		return url;
	}

	private void logDivide() {
		log.info("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
	}

	public String doCloud(String version, String path, String installId, HttpMethod method, String body) {

		String url = fullCloudPath(version, path);

		return eloquaApiExchange(installId, url, method, body);

	}

}
