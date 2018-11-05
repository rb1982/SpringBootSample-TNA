package com.centurylink.cgs.TechnicianNonAvailability;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.centurylink.cgs.dispatchcommon.model.VersionHealthResponse;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityRequest;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TechnicianNonAvailabilityApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void createNonAvailability() {
		TechNonAvailabilityRequest techNonAvailabilityRequest = new TechNonAvailabilityRequest();	
		techNonAvailabilityRequest.setRequestId("MAX_123");
		techNonAvailabilityRequest.setCallingSystem("SYSCALL");
		techNonAvailabilityRequest.setTechId("HJACOB");
		techNonAvailabilityRequest.setTechDistrict("Sparta WI");
		techNonAvailabilityRequest.setNonAvailability("true");
		techNonAvailabilityRequest.setNonAvailabilityType("Holiday");
		ResponseEntity<TechNonAvailabilityResponse> responseEntity = restTemplate.postForEntity("/TechNonAvailability", techNonAvailabilityRequest, TechNonAvailabilityResponse.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());		
	}
	
	@Test
	public void getVersionDetails() {
		TechNonAvailabilityRequest techNonAvailabilityRequest = new TechNonAvailabilityRequest();	
		techNonAvailabilityRequest.setRequestId("MAX_123");
		techNonAvailabilityRequest.setCallingSystem("SYSCALL");
		techNonAvailabilityRequest.setTechId("HJACOB");
		techNonAvailabilityRequest.setTechDistrict("Sparta WI");
		techNonAvailabilityRequest.setNonAvailability("true");
		techNonAvailabilityRequest.setNonAvailabilityType("Holiday");
		ResponseEntity<VersionHealthResponse> responseEntity = restTemplate.getForEntity("/TechNonAvailability/version", VersionHealthResponse.class);			
		VersionHealthResponse response = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("Success",response.getBaseResponse().getResponseStatus());
	}

}
