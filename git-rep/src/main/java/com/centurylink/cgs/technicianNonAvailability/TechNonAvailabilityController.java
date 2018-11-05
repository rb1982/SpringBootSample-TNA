package com.centurylink.cgs.technicianNonAvailability;

import java.text.ParseException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.centurylink.cgs.dispatchcommon.model.VersionHealthResponse;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;
import com.centurylink.cgs.technicianNonAvailability.model.RequestContext;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityRequest;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityResponse;
import com.centurylink.cgs.technicianNonAvailability.service.TechNonAvailabilityService;
import com.centurylink.cgs.technicianNonAvailability.service.TechNonAvailabilityVersionService;


@RestController
@RequestMapping("/TechNonAvailability")
public class TechNonAvailabilityController {
	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(TechNonAvailabilityController.class);
	
	@Autowired
	private RequestContext requestContext;
	@Autowired
	private TechNonAvailabilityVersionService techNonAvailabilityVersionService;
	@Autowired
	private TechNonAvailabilityService techNonAvailabilityService;

	@GetMapping(value = "/version", produces = "application/json")
    public VersionHealthResponse getVersion() {
        return techNonAvailabilityVersionService.getVersionDetails();
    }
	
	@PostMapping(produces = "application/json")
	public TechNonAvailabilityResponse TechNonAvailabilityInClick(@RequestBody TechNonAvailabilityRequest techNonAvailabilityRequest) throws DatatypeConfigurationException, ParseException {
		LOG.info("TechNonAvailabilityController::TechNonAvailabilityInClick::TechNonAvailabilityRequest for requestId: "+techNonAvailabilityRequest.getRequestId()+"::"+techNonAvailabilityRequest);
		this.requestContext.setTechNonAvailabilityRequest(techNonAvailabilityRequest);
		TechNonAvailabilityResponse techNonAvailabilityResponse = new TechNonAvailabilityResponse();
		techNonAvailabilityResponse = techNonAvailabilityService.createTechNonAvailabilityInCLick(techNonAvailabilityRequest);
		LOG.info("TechNonAvailabilityController::TechNonAvailabilityInClick::TechNonAvailabilityResponse for requestId: "+techNonAvailabilityRequest.getRequestId()+"::"+techNonAvailabilityResponse);
		return techNonAvailabilityResponse;
    }
}
