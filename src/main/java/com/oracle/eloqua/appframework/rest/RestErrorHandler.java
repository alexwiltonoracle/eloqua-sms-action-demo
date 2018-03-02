package com.oracle.eloqua.appframework.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class RestErrorHandler extends DefaultResponseErrorHandler {

	private static final Logger log = LoggerFactory.getLogger(RestErrorHandler.class);

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		log.error("Http Status: " + response.getStatusCode() + " / " + response.getStatusText());

		if (response.getBody() != null) {
			log.error("Response body: ");
			BufferedReader rdr = new BufferedReader(new InputStreamReader(response.getBody()));
			try {
				for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
					log.error(" >> " + line);
				}
			} finally {
				rdr.close();
			}

		}

		throw new IOException("Http Status: " + response.getStatusCode());
	}

}
