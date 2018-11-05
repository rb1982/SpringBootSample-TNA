package com.centurylink.cgs.TechnicianNonAvailability;


import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.centurylink.cgs.technicianNonAvailability.TechNonAvailabilityController;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityRequest;
import com.centurylink.cgs.technicianNonAvailability.service.TechNonAvailabilityService;
import com.centurylink.cgs.technicianNonAvailability.service.TechNonAvailabilityVersionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;


@RunWith(SpringRunner.class)
@WebMvcTest(value = TechNonAvailabilityController.class, secure = false)
public class TechNonAvailabilityControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private TechNonAvailabilityVersionService techNonAvailabilityVersionService;
	@MockBean
	private TechNonAvailabilityService techNonAvailabilityService;
	@Autowired 
	private ObjectMapper mapper;

	@Test
	public void testGetVersionDetails() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/TechNonAvailability/version").accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	
	@Test
	public void testTechNonAvailabilityInClick()
			throws Exception {

		TechNonAvailabilityRequest techNonAvailabilityRequest = new TechNonAvailabilityRequest();	
		techNonAvailabilityRequest.setRequestId("MAX_123");
		techNonAvailabilityRequest.setCallingSystem("SYSCALL");
		techNonAvailabilityRequest.setTechId("HJACOB");
		techNonAvailabilityRequest.setTechDistrict("Sparta WI");
		techNonAvailabilityRequest.setNonAvailability("true");
		techNonAvailabilityRequest.setNonAvailabilityType("Holiday");

		String request = mapper.writeValueAsString(techNonAvailabilityRequest);
		// Send request as body to path "/TechNonAvailability"
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/TechNonAvailability")
				.accept(MediaType.APPLICATION_JSON).content(request)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());

	}
}
