package com.centurylink.cgs.technicianNonAvailability.exception;

import com.centurylink.cgs.dispatchcommon.exception.DispatchException;
import com.centurylink.cgs.dispatchcommon.logging.LogContext;

public class TechNonAvailabilityException extends DispatchException {
	/**
	 *  Common Exception class for TechNonAvailability service
	 */
	private static final long serialVersionUID = 1L;

	public TechNonAvailabilityException(String message, int alarmId, LogContext context) {
		super(message, alarmId, context);
	}
	public TechNonAvailabilityException(String message, Throwable cause, int alarmId, LogContext context) {
		super(message, cause, alarmId, context);
	}
		
}
