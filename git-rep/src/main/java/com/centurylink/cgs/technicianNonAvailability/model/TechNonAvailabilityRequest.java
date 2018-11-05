package com.centurylink.cgs.technicianNonAvailability.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;
import com.centurylink.cgs.technicianNonAvailability.service.CreateNonAvailabilityClickClient;
import com.centurylink.cgs.technicianNonAvailability.util.DerivedDataHelper;

@Service
public class TechNonAvailabilityRequest {
	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(TechNonAvailabilityRequest.class);

	@Autowired
	private CreateNonAvailabilityClickClient createNonAvailabilityClickClient;
	
	@Value("${click.date.format}")
	private String clickDateFormat;

	private String requestId;
	private String callingSystem;
	private String originatingSystem;
	private String techId;
	private String techDistrict;
	private String nonAvailability;
	private String nonAvailabilityType;
	private String nonAvailabilityStart;
	private String nonAvailabilityEnd;
	private String techComments;
	private String timeZone;
	private String state;
	public XMLGregorianCalendar xmlGregorianCalendarStart;
	public XMLGregorianCalendar xmlGregorianCalendarFinish;

	private List<String> errors = new ArrayList<>();

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getCallingSystem() {
		return callingSystem;
	}
	public void setCallingSystem(String callingSystem) {
		this.callingSystem = callingSystem;
	}
	public String getOriginatingSystem() {
		return originatingSystem;
	}
	public void setOriginatingSystem(String originatingSystem) {
		this.originatingSystem = originatingSystem;
	}
	public String getTechId() {
		return techId;
	}
	public void setTechId(String techId) {
		this.techId = techId;
	}
	public String getTechDistrict() {
		return techDistrict;
	}
	public void setTechDistrict(String techDistrict) {
		this.techDistrict = techDistrict;
	}
	public String getNonAvailability() {
		return nonAvailability;
	}
	public void setNonAvailability(String nonAvailability) {
		this.nonAvailability = nonAvailability;
	}
	public String getNonAvailabilityType() {
		return nonAvailabilityType;
	}
	public void setNonAvailabilityType(String nonAvailabilityType) {
		this.nonAvailabilityType = nonAvailabilityType;
	}
	public String getNonAvailabilityStart() {
		return nonAvailabilityStart;
	}
	public void setNonAvailabilityStart(String nonAvailabilityStart) {
		this.nonAvailabilityStart = nonAvailabilityStart;
	}
	public String getNonAvailabilityEnd() {
		return nonAvailabilityEnd;
	}
	public void setNonAvailabilityEnd(String nonAvailabilityEnd) {
		this.nonAvailabilityEnd = nonAvailabilityEnd;
	}
	public String getTechComments() {
		return techComments;
	}
	public void setTechComments(String techComments) {
		this.techComments = techComments;
	}

	public XMLGregorianCalendar getXmlGregorianCalendarStart() {
		return xmlGregorianCalendarStart;
	}
	public void setXmlGregorianCalendarStart(XMLGregorianCalendar xmlGregorianCalendarStart) {
		this.xmlGregorianCalendarStart = xmlGregorianCalendarStart;
	}
	public XMLGregorianCalendar getXmlGregorianCalendarFinish() {
		return xmlGregorianCalendarFinish;
	}
	public void setXmlGregorianCalendarFinish(XMLGregorianCalendar xmlGregorianCalendarFinish) {
		this.xmlGregorianCalendarFinish = xmlGregorianCalendarFinish;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "TechNonAvailabilityRequest [requestId=" + requestId + ", callingSystem=" + callingSystem
				+ ", originatingSystem=" + originatingSystem + ", techId=" + techId + ", techDistrict=" + techDistrict
				+ ", nonAvailability=" + nonAvailability + ", nonAvailabilityType=" + nonAvailabilityType
				+ ", nonAvailabilityStart=" + nonAvailabilityStart + ", nonAvailabilityEnd=" + nonAvailabilityEnd
				+ ", techComments=" + techComments + ", xmlGregorianCalendarStart=" + xmlGregorianCalendarStart
				+ ", xmlGregorianCalendarFinish=" + xmlGregorianCalendarFinish + "]";
	}	

	private boolean validateRequired(String aField, String aValue) {
		boolean invalid = (aValue == null || aValue.isEmpty()); 
		if (invalid)  {
			errors.add(String.format("['%s' is required]", aField));
		}
		return !invalid;
	}

	public boolean isValid() throws DatatypeConfigurationException, ParseException {
		LOG.debug("TechNonAvailabilityRequest::isValid::start");
		if("true".equalsIgnoreCase(getNonAvailability())) {	
			this.validateRequired("requestId", this.getRequestId());
			this.validateRequired("techId", this.getTechId());
			this.validateRequired("techDistrict", this.getTechDistrict());
			boolean valid = this.validateRequired("callingSystem", this.getCallingSystem());
			if (valid) {
				if(this.getOriginatingSystem() == null || this.getOriginatingSystem().isEmpty()){
					this.setOriginatingSystem(this.getCallingSystem());
				}	
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								
				String startDateStr = sdf.format((this.getNonAvailabilityStart() != null) ?  sdf.parse(this.getNonAvailabilityStart()) : DerivedDataHelper.localTimeBasedOnTZ(getTimeZone(), getState()));			
				this.xmlGregorianCalendarStart = DatatypeFactory.newInstance().newXMLGregorianCalendar(startDateStr);
				
				String endDateStr = sdf.format((this.getNonAvailabilityEnd() != null) ?  sdf.parse(this.getNonAvailabilityEnd()) : getEndOfDay(DerivedDataHelper.localTimeBasedOnTZ(getTimeZone(), getState())));			
				this.xmlGregorianCalendarFinish = DatatypeFactory.newInstance().newXMLGregorianCalendar(endDateStr);}

		} else {
			errors.add("['nonAvailability' must always be 'true']");
		}
		LOG.debug("TechNonAvailabilityRequest::isValid::end");
		return this.errors.isEmpty();
	}

	public List<String> getValidationErrors() {
		return Collections.unmodifiableList(this.errors);
	}
	
	public static Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}
}
