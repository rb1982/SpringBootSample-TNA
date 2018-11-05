package com.centurylink.cgs.TechnicianNonAvailability;

import static org.junit.Assert.*;
import java.text.ParseException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityRequest;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityResponse;
import com.centurylink.cgs.technicianNonAvailability.service.CreateNonAvailabilityClickClient;
import com.centurylink.cgs.technicianNonAvailability.service.TechNonAvailabilityServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class TechNonAvailabilityServiceImplTest {
	@Mock
	private CreateNonAvailabilityClickClient createNonAvailabilityClickClient;
	
	@InjectMocks
	TechNonAvailabilityServiceImpl techNonAvailabilityServiceImpl;
	
	
	@Test
	public void testCreateTechNonAvailabilityInCLickValidRequest() throws DatatypeConfigurationException, ParseException {
		TechNonAvailabilityRequest techNonAvailabilityRequest = new TechNonAvailabilityRequest();	
		techNonAvailabilityRequest.setRequestId("MAX_123");
		techNonAvailabilityRequest.setCallingSystem("SYSCALL");
		techNonAvailabilityRequest.setTechId("HJACOB");
		techNonAvailabilityRequest.setTechDistrict("Sparta WI");
		techNonAvailabilityRequest.setNonAvailability("true");
		techNonAvailabilityRequest.setNonAvailabilityType("Holiday");
		
		TechNonAvailabilityResponse techNonAvailabilityResponse = techNonAvailabilityServiceImpl.createTechNonAvailabilityInCLick(techNonAvailabilityRequest);	
		assertEquals("Success", techNonAvailabilityResponse.getResponseStatus().toString());	
	}
	
	@Test
	public void testCreateTechNonAvailabilityInCLickInvalidRequest() throws DatatypeConfigurationException, ParseException {
		TechNonAvailabilityRequest techNonAvailabilityRequest = new TechNonAvailabilityRequest();	
		techNonAvailabilityRequest.setRequestId("MAX_123");
		techNonAvailabilityRequest.setCallingSystem("SYSCALL");
		techNonAvailabilityRequest.setTechDistrict("Sparta WI");
		techNonAvailabilityRequest.setNonAvailability("true");
		techNonAvailabilityRequest.setNonAvailabilityType("Holiday");
		
		TechNonAvailabilityResponse techNonAvailabilityResponse = techNonAvailabilityServiceImpl.createTechNonAvailabilityInCLick(techNonAvailabilityRequest);	
		assertEquals("Failure", techNonAvailabilityResponse.getResponseStatus().toString());	
	}
}
