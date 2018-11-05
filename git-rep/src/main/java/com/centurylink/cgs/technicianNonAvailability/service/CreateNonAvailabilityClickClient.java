package com.centurylink.cgs.technicianNonAvailability.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

import com.centurylink.cgs.technicianNonAvailability.dao.TechNonAvailabilityDaoImpl;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityRequest;
import com.centurylink.cgs.technicianNonAvailability.util.Constants;
import com.clicksoftware.serviceoptimizeservice.Assignment;
import com.clicksoftware.serviceoptimizeservice.Assignment.Engineers;
import com.clicksoftware.serviceoptimizeservice.CreateNonAvailability;
import com.clicksoftware.serviceoptimizeservice.CreateNonAvailabilityResponse;
import com.clicksoftware.serviceoptimizeservice.DistrictReference;
import com.clicksoftware.serviceoptimizeservice.EngineerReference;
import com.clicksoftware.serviceoptimizeservice.NonAvailabilityTypeReference;
import com.clicksoftware.serviceoptimizeservice.OptionalParameters;

public class CreateNonAvailabilityClickClient extends WebServiceGatewaySupport {

	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(CreateNonAvailabilityClickClient.class);

	@Autowired 
	private TechNonAvailabilityDaoImpl techNonAvailabilityDaoImpl;

	@Value("${click.date.format}")
	private String clickDateFormat;

	@Value("${click.service.soap.action}")
	private String clickSoapAction;

	public CreateNonAvailabilityResponse callCreateNonAvailability(TechNonAvailabilityRequest techNonAvailabilityRequest) throws DatatypeConfigurationException {
		LOG.debug("CreateNonAvailabilityClickClient::callCreateNonAvailability::start");
		CreateNonAvailabilityResponse response= new CreateNonAvailabilityResponse();

		CreateNonAvailability request = constructRequest(techNonAvailabilityRequest);	

		WebServiceTemplate wsTemplate = getWebServiceTemplate();		
		ClickInterceptor interceptor = (ClickInterceptor) wsTemplate.getInterceptors()[0];
		interceptor.setRequestId(techNonAvailabilityRequest.getRequestId());
	
		response = (CreateNonAvailabilityResponse) wsTemplate.marshalSendAndReceive(request, 
					new WebServiceMessageCallback() {
				@Override
				public void doWithMessage(WebServiceMessage requestMessage) throws IOException, TransformerException {
					try {
						SoapMessage soapMsg = ((SoapMessage) requestMessage);
						// get the header from the SOAP message		
						SoapHeader soapHeader = soapMsg.getSoapHeader();
						soapMsg.setSoapAction(clickSoapAction);					
						// create the header element
						OptionalParameters optionalParameters = new OptionalParameters();
						optionalParameters.setCallerIdentity(Constants.CALLER_IDENTY); 
						optionalParameters.setErrorOnNonExistingDictionaries(true);
						// create a marshaller
						JAXBContext context = JAXBContext.newInstance(OptionalParameters.class);
						Marshaller marshaller = context.createMarshaller();
						// marshal the headers into the specified result
						marshaller.marshal(optionalParameters, soapHeader.getResult());
					} catch (JAXBException  e) {
						throw new TransformerException("Error while marshalling");
					}
				}
			});
		LOG.debug("CreateNonAvailabilityClickClient::callCreateNonAvailability::end");	
		return response;
	}

	//create the request for click
	private CreateNonAvailability constructRequest(TechNonAvailabilityRequest techNonAvailabilityRequest) throws DatatypeConfigurationException, DataAccessException  {
		LOG.debug("CreateNonAvailabilityClickClient::constructRequest::start");

		CreateNonAvailability request = new CreateNonAvailability();
		Assignment assignment = new Assignment();		
		EngineerReference engineer = new EngineerReference();
		DistrictReference dt = new DistrictReference();
		Engineers engineersList = new Engineers();
		NonAvailabilityTypeReference nonAvalRef = new NonAvailabilityTypeReference();
		String nonAvailabilityType = null;

		if(techNonAvailabilityRequest!=null){
			dt.setName(techNonAvailabilityRequest.getTechDistrict());
			engineer.setID(techNonAvailabilityRequest.getTechId());	
			engineer.setDistrict(dt);
			engineersList.getEngineer().add(engineer);
			if(techNonAvailabilityRequest.getNonAvailabilityType() != null){
				nonAvalRef.setName(techNonAvailabilityRequest.getNonAvailabilityType());
			}else {
				List<Map<String, Object>> nonAvailabilityRefData = techNonAvailabilityDaoImpl.getDispatchRefData(Constants.TECH_NON_AVAILABILITY_REF_DATA_TABLE);
				for(Map<String,Object> map: nonAvailabilityRefData){
					if(map.get("REF_KEY").toString().equalsIgnoreCase("NON_AVAILABILITY_TYPE")){
						nonAvailabilityType=(String)map.get("REF_VALUE");			
					}
					nonAvalRef.setName(nonAvailabilityType);
				}
			}	
			assignment.setStart(techNonAvailabilityRequest.getXmlGregorianCalendarStart());
			assignment.setFinish(techNonAvailabilityRequest.getXmlGregorianCalendarFinish());
			assignment.setComment(techNonAvailabilityRequest.getTechComments());
			assignment.setNonAvailabilityType(nonAvalRef);
			assignment.setEngineers(engineersList);
		}
		request.setAssignment(assignment);
		LOG.debug("CreateNonAvailabilityClickClient::constructRequest::end");
		return request;
	}

	public Map<String, Object> getTechDetails(String techDistrict) {
		LOG.debug("CreateNonAvailabilityClickClient::getTechDetails::start");
		Map<String, Object> serviceAreaHirarchyMap = techNonAvailabilityDaoImpl.retrieveServiceAreaHirarchy(techDistrict);
		LOG.debug("CreateNonAvailabilityClickClient::getTechDetails::end");	
		return serviceAreaHirarchyMap;
	}
	
}
