package com.centurylink.cgs.technicianNonAvailability.model;

import javax.annotation.ManagedBean;

import org.springframework.web.context.annotation.RequestScope;

@ManagedBean
@RequestScope
public class RequestContext {

	private TechNonAvailabilityRequest techNonAvailabilityRequest;

	public TechNonAvailabilityRequest getTechNonAvailabilityRequest() {
		return techNonAvailabilityRequest;
	}

	public void setTechNonAvailabilityRequest(TechNonAvailabilityRequest techNonAvailabilityRequest) {
		this.techNonAvailabilityRequest = techNonAvailabilityRequest;
	}
	
}
