package com.centurylink.cgs.technicianNonAvailability.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;

public class DerivedDataHelper {
	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(DerivedDataHelper.class);
	// Regular Expression to match tokens like [LB = Y, or LB:Y, or DB = Y, or DB:Y, or QAE# where # = 3-4]
	private static final String regExToMatchIWMFlagAsY = "LB\\s=\\sY|LB:Y|DB\\s=\\sY|DB:Y|QAE[3-4]"; 
	
	public static Date localTimeBasedOnTZ(String timeZone, String state) throws ParseException{
		
		if(timeZone!=null && (timeZone.startsWith("Eastern") || timeZone.startsWith("EASTERN"))){
	    	timeZone = "EST";
	    }else if(timeZone!=null && (timeZone.startsWith("Mountain") || timeZone.startsWith("MOUNTAIN"))){
	    	timeZone = "MST";
	    }else if(timeZone!=null && (timeZone.startsWith("Central") || timeZone.startsWith("CENTRAL"))){
	    	timeZone = "CST";
	    }else if(timeZone!=null && (timeZone.startsWith("Pacific") || timeZone.startsWith("PACIFIC"))){
	    	timeZone = "PST";
	    }else if(timeZone!=null && (timeZone.startsWith("Atlantic") || timeZone.startsWith("ATLANTIC"))){
	    	timeZone = "AST";
	    }else if(timeZone!=null && (timeZone.startsWith("Alaska") || timeZone.startsWith("ALASKA"))){
	    	timeZone = "AKT";
	    }else if(timeZone!=null && (timeZone.contains("Hawaii") || timeZone.contains("HAWAII"))){
	    	timeZone = "HST";
	    }
	    	
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(getTimeZoneObj(timeZone, state));
	    String serverDate = sdf.format(date);

	    sdf.setTimeZone(TimeZone.getDefault());
	    date =  sdf.parse(serverDate);
	    return date;
	}
	
	public static TimeZone getTimeZoneObj(String timeZoneStr,String businessUnit){
		
		   TimeZone tz=TimeZone.getDefault();
		   if(timeZoneStr!=null){
	           if(timeZoneStr.matches("[a-zA-Z]+")){
		          if("MST".equalsIgnoreCase(timeZoneStr)){
		        	  //For AZ there is no daylight saving so separate handling for this
		        	  if("AZ".equalsIgnoreCase(businessUnit)){
		        		  tz=TimeZone.getTimeZone("America/Phoenix");
		        	  }
		        	  else{
		                 tz=TimeZone.getTimeZone("America/Denver");
		        	  }
		          }
		          else if("AST".equalsIgnoreCase(timeZoneStr)){
		                 tz=TimeZone.getTimeZone("AST");
		          }
		          else if("EST".equalsIgnoreCase(timeZoneStr)){
		                 tz=TimeZone.getTimeZone("America/New_York");
		          }
		          else if("CST".equalsIgnoreCase(timeZoneStr)){
		                 tz=TimeZone.getTimeZone("America/Chicago");
		          }
		          else if("PST".equalsIgnoreCase(timeZoneStr)){
		        	  tz=TimeZone.getTimeZone("America/Los_Angeles");
		          }
		          else if("AKT".equalsIgnoreCase(timeZoneStr)){
		                 tz=TimeZone.getTimeZone("AKT");
		          }
		          else if("HST".equalsIgnoreCase(timeZoneStr)){
		                 tz=TimeZone.getTimeZone("HST");
		          }
	          }
	          else if(isInteger(timeZoneStr)){
	        	  
	        	  tz = convertOffsetToTimeZone(timeZoneStr, businessUnit);
	          }
	          else{
	        	  tz = convertGMTStrngOffset(timeZoneStr);
	          }
		  }
          return tz;
	}
	
	public static TimeZone convertGMTStrngOffset(String customerTimeZone){
		return TimeZone.getTimeZone(customerTimeZone);
	}
	
	public static boolean isInteger(String s) {
		if(s.matches("-?\\d+"))
			return true;
		else
			return false;
	}
	
	public static TimeZone convertOffsetToTimeZone(String offset,String state) {
		String customerTimeZone="";
		int offsetInt=0;
		if (StringUtils.isNotEmpty(offset)) {
			try{
				offsetInt = Integer.parseInt(offset);
			}
			catch(NumberFormatException e){
				e.printStackTrace();
			}
		
			String[] timeZones = TimeZone
					.getAvailableIDs(offsetInt * 60 * 60 * 1000);
			if ("AZ".equalsIgnoreCase(state)) {
				customerTimeZone = "America/Phoenix";
			} else {
				for (String each : timeZones) {
					if (each.contains("GMT")) {
						customerTimeZone = each;
						LOG.debug("Timezone according to the offset is "
								+ each);
						break;
					}
				}
			}
		}
		return TimeZone.getTimeZone(customerTimeZone);
	}
	
	public static Long getDateAfterHolidayOrWeekEndCalc(Set<Long> holidaySet, Long assignmentDate){
        boolean cont = false;
        while (!cont) {
      	if (holidaySet.contains(Long.valueOf(assignmentDate))) {
      		assignmentDate = assignmentDate + 86400000L; continue;
      	}if (new Date(assignmentDate).getDay() == 0) {
      		assignmentDate = assignmentDate + 86400000L; continue;
      	}if (new Date(assignmentDate).getDay() == 6) {
      		assignmentDate = assignmentDate + 86400000L; continue;
      	}
      	cont = true;
        }
        return assignmentDate;
	}
	
	public static Date getDateObjectWith5PMTime(Date intermidiateDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(intermidiateDate);
		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.SECOND, 00);
		return cal.getTime();
	}
	
	public static Date getCurrentDateWithStartOfDay(){
		Calendar c = Calendar.getInstance();
		Date d = new GregorianCalendar(c.get(Calendar.YEAR), 
		                               c.get(Calendar.MONTH), 
		                               c.get(Calendar.DAY_OF_MONTH)).getTime();

		return d;
	}
}
