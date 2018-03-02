package com.oracle.eloqua.appframework.util;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

public class Util {
	public static void appendIfNonNull(StringBuilder s, String name, String value) {
		if (value != null) {
			if (s.length() > 0) {
				s.append("\n");
			}
			s.append(padRight(name, 20) + ": ");
			s.append(value);

		}
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static void logIncomingRequest(Logger log, HttpServletRequest request) {

		logIncomingRequest(log, request, null);

	}

	public static void logIncomingRequest(Logger log, HttpServletRequest request, String requestBody) {
		log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		log.info("   >   Request Method : " + request.getMethod());
		log.info("   >   Incoming URL   : " + request.getRequestURL().toString());

		Map<String, String[]> allParams = request.getParameterMap();
		log.info("   >   URL Params:");
		for (Map.Entry<String, String[]> p : allParams.entrySet()) {

			log.info(String.format("      >   %s = %s", p.getKey(), Arrays.toString(p.getValue())));
		}
		
		log.info("   >   Request Body:");
		log.info("         " + requestBody);;
		
		/*if ("POST".equalsIgnoreCase(request.getMethod())) 
		{
		   try {
			String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			log.info("   >   POST Body:");
			log.info(test);
			log.info("   >   END POST Body:");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		//log.info("   >   Form Params:");

		*/
		log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
		
	}
}
