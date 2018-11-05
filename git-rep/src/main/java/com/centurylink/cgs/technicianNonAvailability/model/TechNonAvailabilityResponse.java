package com.centurylink.cgs.technicianNonAvailability.model;

import org.springframework.stereotype.Component;

@Component
public class TechNonAvailabilityResponse {
	private String message;
	private responseStatusValues responseStatus;
	private int reasonCode;
	
	public enum responseStatusValues{
		Success,Error, Failure, Informational, Warning
	};

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public responseStatusValues getResponseStatus() {
		return responseStatus;
	}
	public void setResponseStatus(responseStatusValues responseStatus) {
		this.responseStatus = responseStatus;
	}
	public int getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}
	
	@Override
	public String toString() {
		return "TechNonAvailabilityResponse [message=" + message + ", responseStatus=" + responseStatus
				+ ", reasonCode=" + reasonCode + "]";
	}
	
}
