package com.centurylink.cgs.technicianNonAvailability.service;

import java.text.ParseException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.dao.DataAccessException;

import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityRequest;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityResponse;

public interface TechNonAvailabilityService {
	
	public TechNonAvailabilityResponse createTechNonAvailabilityInCLick(TechNonAvailabilityRequest techNonAvailabilityRequest) throws DatatypeConfigurationException, ParseException;

}
