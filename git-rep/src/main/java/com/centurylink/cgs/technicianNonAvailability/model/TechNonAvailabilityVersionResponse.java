package com.centurylink.cgs.technicianNonAvailability.model;

import org.springframework.stereotype.Component;

@Component
public class TechNonAvailabilityVersionResponse {

	
	private String version;
	private String buildTimeStamp;
	private String builtBy;
	private String buildJdk;
	private String springProfile;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBuildTimeStamp() {
		return buildTimeStamp;
	}
	public void setBuildTimeStamp(String buildTimeStamp) {
		this.buildTimeStamp = buildTimeStamp;
	}
	public String getBuiltBy() {
		return builtBy;
	}
	public void setBuiltBy(String builtBy) {
		this.builtBy = builtBy;
	}
	public String getBuildJdk() {
		return buildJdk;
	}
	public void setBuildJdk(String buildJdk) {
		this.buildJdk = buildJdk;
	}
	public String getSpringProfile() {
		return springProfile;
	}
	public void setSpringProfile(String springProfile) {
		this.springProfile = springProfile;
	}	
}
