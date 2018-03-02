package com.oracle.eloqua.appframework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.oracle.eloqua.appframework.auth.OAuth2SessionManager;
import com.oracle.eloqua.appframework.service.ActionServiceInstancePool;

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger log = LoggerFactory.getLogger(ContextRefreshedListener.class);

	@Autowired
	ActionServiceInstancePool actionServicePool;

	@Autowired
	OAuth2SessionManager oAuth2SessionManager;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		log.info("Context Refreshed Event Received: " + contextRefreshedEvent.toString());

		actionServicePool.startExisting();

		oAuth2SessionManager.refreshAllTokens();

	}
}