package com.oracle.eloqua.appframework.auth;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.oracle.eloqua.appframework.entity.AppInstallation;
import com.oracle.eloqua.appframework.repository.AppInstallationRepository;
import com.oracle.eloqua.appframework.rest.RestTemplateFactory;
import com.oracle.eloqua.appframework.util.Util;

@Component
@EnableAutoConfiguration
public class OAuth2SessionManager {
	private static final Logger log = LoggerFactory.getLogger(OAuth2SessionManager.class);

	@Autowired
	AppInstallationRepository appInstallationRepository;

	@Value("${oauth.clientId}")
	private String clientId;

	@Value("${oauth.clientSecret}")
	private String clientSecret;

	@Value("${oauth.tokenUrl}")
	private String tokenUrl;

	@Value("${oauth.redirectUri}")
	private String redirectUri;

	// The user has clicked to install the app. This created the database
	// instance of the app, along with the callback URL to be triggered once the
	// OAuth process has completed.
	public void startInstall(String installId, String appId, String userName, String eloquaCallbackUrl) {

		AppInstallation appInstallation = appInstallationRepository.findOne(installId);

		if (appInstallation == null) {
			appInstallation = new AppInstallation();
			appInstallation.setInstallId(installId);
			appInstallation.setAppId(appId);
			appInstallation.setEloquaCallbackUrl(eloquaCallbackUrl);
		}
		
		appInstallation.setUserName(userName);

		appInstallationRepository.save(appInstallation);

	}

	@PostConstruct
	private void logConfig() {
		log.info("App Config:");
		log.info("   clientId:       " + clientId);
		log.info("   clientSecret:   " + clientSecret);
		log.info("   tokenUrl:       " + tokenUrl);
		log.info("   redirectUri:    " + redirectUri);

	}

	// once the user has completed the OAuth process, this method exchanges the
	// code for the access and refresh tokens

	public String doCodeExchange(String code, String installId) {
		log.info("Starting Code Exchange");

		HttpHeaders requestHeaders = headersForAppBasedRequest();

		String requestBody = createCodeExchangeBody(code, installId);

		HttpEntity<String> request = new HttpEntity<String>(requestBody, requestHeaders);

		log.info("   URL:     " + tokenUrl);
		log.info("   Body:    " + request.getBody());
		log.info("   Headers: " + request.getHeaders().toString());

		RestTemplate restTemplate = RestTemplateFactory.newInstance();
		log.info("   doing exchange...");
		ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);
		log.info("   ...done");
		try {
			log.info("   Exchange Http Status:   " + response.getStatusCode().toString());
			log.info("   Exchange response body: " + response.getBody());
			JSONObject authResponse = new JSONObject(response.getBody());

			String accessToken = authResponse.getString("access_token");
			String refreshToken = authResponse.getString("refresh_token");

			log.info("   Access token  : " + accessToken);
			log.info("   Refresh token : " + refreshToken);

			AppInstallation appInstallation = appInstallationRepository.findOne(installId);

			if (appInstallation == null) {
				log.error("installId cannot be null here");
			}

			appInstallation.setAccessToken(accessToken);
			appInstallation.setRefreshToken(refreshToken);

			appInstallationRepository.save(appInstallation);
			return appInstallation.getEloquaCallbackUrl();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public void refreshAllTokens() {
		log.info("Refreshing all tokens");
		Iterable<AppInstallation> allInstalls = appInstallationRepository.findAll();

		for (AppInstallation appInstallation : allInstalls) {
			doRefresh(appInstallation);
		}

	}

	// Use the refresh token to get a new access token
	public void doRefresh(AppInstallation appInstallation) {
		log.info("Refreshing token for install: " + appInstallation.toString());

		HttpHeaders requestHeaders = headersForAppBasedRequest();

		String requestBody = createRefreshBody(appInstallation.getRefreshToken());

		HttpEntity<String> request = new HttpEntity<String>(requestBody, requestHeaders);

		log.info("   URL:     " + tokenUrl);
		log.info("   Body:    " + request.getBody());
		log.info("   Headers: " + request.getHeaders().toString());

		RestTemplate restTemplate = new RestTemplate();

		try {

			ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

			Util.logPrettyJson(log, "refresh token request body: ", response.getBody());

			JSONObject authResponse = new JSONObject(response.getBody());

			String accessToken = authResponse.getString("access_token");
			String refreshToken = authResponse.getString("refresh_token");

			log.info("Http Status: " + response.getStatusCode().toString());

			log.info("access token : " + accessToken);
			log.info("refresh token : " + refreshToken);

			appInstallation.setAccessToken(accessToken);
			appInstallation.setRefreshToken(refreshToken);

			appInstallationRepository.save(appInstallation);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized String retrieveAccessToken(String installId) {
		return appInstallationRepository.findOne(installId).getAccessToken();
	}

	private String createCodeExchangeBody(String code, String installId) {

		JSONObject requestBody = new JSONObject();

		try {
			requestBody.put("grant_type", "authorization_code");
			requestBody.put("code", code);
			requestBody.put("redirect_uri",
					"https://eloqua-app-framework.ngrok.io/auth/callback?installId=" + installId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return requestBody.toString();
	}

	private String createRefreshBody(String refreshToken) {

		JSONObject requestBody = new JSONObject();

		try {
			requestBody.put("grant_type", "refresh_token");
			requestBody.put("refresh_token", refreshToken);
			requestBody.put("scope", "full");
			requestBody.put("redirect_uri", redirectUri);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return requestBody.toString();

	}

	// Creates the HttpHeaders for an app-level request
	private HttpHeaders headersForAppBasedRequest() {
		HttpHeaders headers = new HttpHeaders();

		String plainCreds = clientId + ":" + clientSecret;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		headers.add("Authorization", "Basic " + base64Creds);
		headers.add("Content-Type", "application/json");
		return headers;
	}

	// Creates the HttpHeaders for a install-level request.
	public HttpHeaders headersForInstallBasedRequest(String installId) {

		log.debug("Building auth headers for installId: " + installId);

		AppInstallation appInstallation = appInstallationRepository.findOne(installId);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add("Host", "api.eloqua.com");
		headers.add("Authorization", "Bearer " + appInstallation.getAccessToken());

		Map<String, String> headerMap = headers.toSingleValueMap();

		for (Map.Entry<String, String> h : headerMap.entrySet()) {

			log.debug(String.format("      >   %s = %s", h.getKey(), h.getValue()));
		}

		return headers;

	}

	// Builds the URL to start the OAuth process. The redirect URI includes the
	// installId so that the code can be associated with the install
	public String authorizeUrl(String installId) {
		URIBuilder uriBuilder = new URIBuilder();

		uriBuilder.setScheme("https");
		uriBuilder.setHost("login.eloqua.com");
		uriBuilder.setPath("auth/oauth2/authorize");
		uriBuilder.addParameter("response_type", "code");
		uriBuilder.addParameter("client_id", clientId);

		uriBuilder.addParameter("redirect_uri",
				"https://eloqua-app-framework.ngrok.io/auth/callback?installId=" + installId);

		uriBuilder.addParameter("scope", "full");
		// uriBuilder.addParameter("installId", installId);

		// uriBuilder.addParameter("state", "xyx");

		log.info("Authorize URL: " + uriBuilder.toString());
		return uriBuilder.toString();
	}

}
