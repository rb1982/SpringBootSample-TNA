package com.centurylink.cgs.technicianNonAvailability.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;

public class ClickInterceptor extends ClientInterceptorAdapter{
private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(ClickInterceptor.class);
	
	private String requestId;
	
	public synchronized String getRequestId() {
		return requestId;
	}

	public synchronized void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

	@Override
	public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {			
		return true;
	}

	@Override
	public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
		try {
			ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
			ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
			messageContext.getRequest().writeTo(requestStream);
			LOG.info("Request to click for requestId :"+this.getRequestId()+ " : "+requestStream.toString());

			messageContext.getResponse().writeTo(responseStream);
			LOG.info("Response from click for requestId : "+this.getRequestId()+ " : "+responseStream.toString());
			
			requestStream.close();
			responseStream.close();
		} catch (IOException ignored) {
		}
	}
}
