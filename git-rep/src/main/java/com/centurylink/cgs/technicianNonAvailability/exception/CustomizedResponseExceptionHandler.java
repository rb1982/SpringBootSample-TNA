package com.centurylink.cgs.technicianNonAvailability.exception;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.ParseException;
import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.soap.client.SoapFaultClientException;
import com.centurylink.cgs.dispatchcommon.logging.LogContext;
import com.centurylink.cgs.dispatchlog.DispatchLog;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;
import com.centurylink.cgs.technicianNonAvailability.model.ReasonCodes;
import com.centurylink.cgs.technicianNonAvailability.model.RequestContext;
import com.centurylink.cgs.technicianNonAvailability.model.TechNonAvailabilityResponse;
import com.centurylink.cgs.technicianNonAvailability.util.Constants;

@ControllerAdvice(basePackages = {"com.centurylink.cgs.technicianNonAvailability"} )
@RestController
public class CustomizedResponseExceptionHandler extends ResponseEntityExceptionHandler{

	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(CustomizedResponseExceptionHandler.class);
	@Autowired 
	@Qualifier("jobsDataSource") 
	protected DataSource dataSource; 
	
	@Autowired
	private RequestContext requestContext;
	
	private ResponseEntity<TechNonAvailabilityResponse> createResponseEntityAndStoreException(int reasonCode, String message, 
			TechNonAvailabilityResponse.responseStatusValues responseStatus, HttpStatus httpStatus, Exception ex, int alarmId) {
		TechNonAvailabilityResponse response = new TechNonAvailabilityResponse();
		response.setReasonCode(reasonCode);
		response.setMessage(message);
		response.setResponseStatus(responseStatus);
		TechNonAvailabilityException exception = new TechNonAvailabilityException(message, ex, alarmId,	new LogContext().setMessage(message));
		LOG.error(exception);
		saveToDispatchLog(exception, message, alarmId);
		return new ResponseEntity<>(response, httpStatus);
	}

	@ExceptionHandler(SoapFaultClientException.class)
	public final ResponseEntity<TechNonAvailabilityResponse>  handleSoapFaultException(SoapFaultClientException ex) {
		
		String message = ex.getSoapFault().getFaultCode().getLocalPart() + " - " + ex.getSoapFault().getFaultStringOrReason();
 		return createResponseEntityAndStoreException(ReasonCodes.CLICK_SOAP_FAULT, message, TechNonAvailabilityResponse.responseStatusValues.Failure,
 				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.SOAP_FAULT_EXCEPTION);
 
	}

	@ExceptionHandler(WebServiceClientException.class)
	public final ResponseEntity<TechNonAvailabilityResponse>  handleClickException(WebServiceClientException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.CLICK_EXCEPTION, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.CLICK_EXCEPTION);
	}

	@ExceptionHandler(WebServiceTransportException.class)
	public final ResponseEntity<TechNonAvailabilityResponse>  handleClickTransportException(WebServiceTransportException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.CLICK_EXCEPTION, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.CLICK_ClIENT_TRANSPORT_EXCEPTION);
	}

	@ExceptionHandler(ConnectException.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleClickConnectException(ConnectException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.CLICK_CONNECTION_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.CONNECTION_EXCEPTION);
	}

	@ExceptionHandler(DatatypeConfigurationException.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleDatatypeConfigurationException(DatatypeConfigurationException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.GREG_CAL_XML_REPRESENTATION_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure,
				 HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.DATATYPE_INSTANTIATION_EXCEPTION);
	}

	@ExceptionHandler(IOException.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleIOException(IOException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.INPUT_OUTPUT_EXCEPTION, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.INPUT_OUTPUT_EXCEPTION);
	}

	@ExceptionHandler(TransformerException.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleTransformerException(TransformerException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.MARSHALLING_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.MARSHALLING_EXCEPTION);
	}
	
	@ExceptionHandler(SQLException.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleSQLException(SQLException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.SQL_EXCEPTION, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.SQL_GENERIC_EXCEPTION);
	}
	
	@ExceptionHandler(DataAccessException.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleDataAccessException(DataAccessException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.SQL_GRAMMAR_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.DATA_ACCESS_EXCEPTION);
	}
	
	@ExceptionHandler(CannotGetJdbcConnectionException.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleCannotGetJdbcConnectionException(CannotGetJdbcConnectionException ex) {
		
		return createResponseEntityAndStoreException(ReasonCodes.JDBC_CONNECTION_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.JDBC_CONNECTION_EXCEPTION);
	}

	@ExceptionHandler(URISyntaxException.class)
	public final ResponseEntity<TechNonAvailabilityResponse>  handleURISyntaxException(URISyntaxException ex) {
		return createResponseEntityAndStoreException(ReasonCodes.URI_SYNTAX_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.URI_SYNTAX_EXCEPTION);
 
	}
	@ExceptionHandler(ClientProtocolException.class)
	public final ResponseEntity<TechNonAvailabilityResponse>  handleClientProtocolException(ClientProtocolException ex) {
		return createResponseEntityAndStoreException(ReasonCodes.CLIENT_PTOTOCOL_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.CLIENT_PTOTOCOL_EXCEPTION);
 
	}
	@ExceptionHandler(ParseException.class)
	public final ResponseEntity<TechNonAvailabilityResponse>  handleParseException(ParseException ex) {
		return createResponseEntityAndStoreException(ReasonCodes.DATE_PARSE_ERROR, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.DATE_PARSE_EXCEPTION);
 
	}
	
	@ExceptionHandler(IncorrectResultSizeDataAccessException.class)
	public final ResponseEntity<TechNonAvailabilityResponse>  handleIncorrectResultSizeException(IncorrectResultSizeDataAccessException ex) {
		return createResponseEntityAndStoreException(ReasonCodes.INCORRECT_RESULT_SIZE_EXCEPTION, "Valid TIMEZONE is not Present for ServiceArea: "+ this.requestContext.getTechNonAvailabilityRequest().getTechDistrict() +", Exception:"+ ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.DATA_ACCESS_EXCEPTION);
 
	}
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<TechNonAvailabilityResponse> handleAllExceptions(Exception ex)  {

		return createResponseEntityAndStoreException(ReasonCodes.GENERIC_EXCEPTION, ex.getMessage(), TechNonAvailabilityResponse.responseStatusValues.Failure, 
				HttpStatus.INTERNAL_SERVER_ERROR, ex, AlarmId.GENERIC_EXCEPTION);
	}
	
	
	//saving exceptions details to DispatchLog table
	private void saveToDispatchLog(TechNonAvailabilityException exception, String alarmMessage, int errorNumber){
		
		DispatchLog log = new DispatchLog();
		log.setServiceName(Constants.APPLICATION_SERVICE_NAME);
		String requestId = this.requestContext.getTechNonAvailabilityRequest().getRequestId();
		if (requestId != null) {
			log.setCallId(requestId.substring(0, Math.min(requestId.length(), 64)));
		}
		if (exception != null) {
			log.setAlarmId(exception.getAlarmId());
			log.setAlarmMessage(alarmMessage);
			if (exception.getContext() != null){
				log.setAlarmContext(exception.getContext().toString());
			}
			if(null != exception.getCause()){
				log.setErrorMessage(exception.getCause().toString());
			}
			log.setStackTrace(exception.getStackTrace().toString());
		}
		log.setErrorNumber(errorNumber);
		try {
			log.save(dataSource);
			LOG.info("Saved exception details to dispatch log");
		} catch (Exception e) {
			LogContext context = new LogContext().add("DispatchLog",log);
			TechNonAvailabilityException dtex = new TechNonAvailabilityException("Unable to save to dispatch log", e,
					AlarmId.LOG_SAVE_EXCEPTION, context);
			LOG.error(dtex);
		}
	}
}
