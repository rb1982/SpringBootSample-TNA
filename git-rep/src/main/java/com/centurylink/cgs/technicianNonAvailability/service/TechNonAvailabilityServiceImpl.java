package com.centurylink.cgs.technicianNonAvailability.service;

import java.text.ParseException;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;
import com.centurylink.cgs.technicianNonAvailability.model.ReasonCodes;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityRequest;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityResponse;
import com.centurylink.cgs.technicianNonAvailability.util.Constants;

@Service
public class TechNonAvailabilityServiceImpl implements TechNonAvailabilityService {
	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(TechNonAvailabilityServiceImpl.class);

	@Autowired
	private CreateNonAvailabilityClickClient createNonAvailabilityClickClient;

	public TechNonAvailabilityResponse createTechNonAvailabilityInCLick(TechNonAvailabilityRequest techNonAvailabilityRequest) throws DatatypeConfigurationException, ParseException  {
		LOG.debug("TechNonAvailabilityServiceImpl::createTechNonAvailabilityInCLick::start");
		TechNonAvailabilityResponse techNonAvailabilityResponse = new TechNonAvailabilityResponse();
		Map<String, Object> techDetailMap = createNonAvailabilityClickClient.getTechDetails(techNonAvailabilityRequest.getTechDistrict());
		techNonAvailabilityRequest.setTimeZone((String)techDetailMap.get("TIMEZONE"));
		techNonAvailabilityRequest.setState((String)techDetailMap.get("STATE"));
		LOG.info("TimeZone retrieved from SP is::"+techDetailMap.get("TIMEZONE")+" and State is::"+techDetailMap.get("STATE"));
		if (techNonAvailabilityRequest.isValid()) {	
			//call click service
			createNonAvailabilityClickClient.callCreateNonAvailability(techNonAvailabilityRequest);
			//sending success response
			techNonAvailabilityResponse.setResponseStatus(TechNonAvailabilityResponse.responseStatusValues.Success);
			techNonAvailabilityResponse.setMessage(String.format(Constants.SUCCESS_MSG, techNonAvailabilityRequest.getTechId(), techNonAvailabilityRequest.getRequestId()));
			techNonAvailabilityResponse.setReasonCode(ReasonCodes.SUCCESS);
		} else {      
			//sending validation failure response
			techNonAvailabilityResponse.setResponseStatus(TechNonAvailabilityResponse.responseStatusValues.Failure);
			techNonAvailabilityResponse.setMessage("Input validation errors: "+techNonAvailabilityRequest.getValidationErrors());
			techNonAvailabilityResponse.setReasonCode(ReasonCodes.INPUT_VALIDATION_ERROR);
		}
		LOG.debug("TechNonAvailabilityServiceImpl::createTechNonAvailabilityInCLick::end");
		return techNonAvailabilityResponse;
	}
}
