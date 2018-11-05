package com.centurylink.cgs.technicianNonAvailability.logging;

import com.centurylink.cgs.dispatchcommon.logging.DispatchLogger;
import com.centurylink.cgs.technicianNonAvailability.util.Constants;

public class TechNonAvailabilityLogger extends DispatchLogger {
	
	private static final String SERVICE_NAME = Constants.APPLICATION_SERVICE_NAME; 
	
	private TechNonAvailabilityLogger(Class clazz, String serviceName) {
		super(clazz, serviceName);
	}
	public static TechNonAvailabilityLogger getLogger(Class clazz) {
		TechNonAvailabilityLogger instance = new TechNonAvailabilityLogger(clazz, SERVICE_NAME);
		return instance;
	}
}
