package com.centurylink.cgs.technicianNonAvailability.model;

import java.util.HashMap;

import javax.annotation.ManagedBean;

import org.springframework.web.context.annotation.ApplicationScope;


@ManagedBean
@ApplicationScope
public class ApplicationContext {

	HashMap<String, String> clickEndPointMap;

	public HashMap<String, String> getClickEndPointMap() {
		return clickEndPointMap;
	}

	public void setClickEndPointMap(HashMap<String, String> clickEndPointMap) {
		this.clickEndPointMap = clickEndPointMap;
	}
	
	
}
