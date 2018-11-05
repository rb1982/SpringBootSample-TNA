package com.centurylink.cgs.technicianNonAvailability.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.centurylink.cgs.dispatchcommon.encryption.EncryptionHelper;
import com.centurylink.cgs.dispatchcommon.healthcheck.VersionHealthInfo;
import com.centurylink.cgs.dispatchcommon.logging.LogContext;
import com.centurylink.cgs.dispatchcommon.model.BaseResponse;
import com.centurylink.cgs.dispatchcommon.model.VersionHealthResponse;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;
import com.centurylink.cgs.technicianNonAvailability.model.ApplicationContext;
import com.centurylink.cgs.technicianNonAvailability.util.Constants;

@Service
public class TechNonAvailabilityVersionServiceImpl implements TechNonAvailabilityVersionService {
	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(CreateNonAvailabilityClickClient.class);
	private final static String ENVIRONMENT_KEY = "SPRING_PROFILES_ACTIVE";

	@Autowired 
	@Qualifier("jobsDataSource") 
	protected DataSource dataSource; 

	@Value("${client.timeout}")
	private int timeout;

	@Autowired
	private ApplicationContext applicationContext;
	
	HashMap<String, String> clickEndPointMap = new HashMap<String, String>();
			
	public VersionHealthResponse getVersionDetails(){

		clickEndPointMap = this.applicationContext.getClickEndPointMap();
		VersionHealthResponse healthResponse = new VersionHealthResponse();
		BaseResponse base = new BaseResponse();
		healthResponse.setBaseResponse(base);
		boolean success = true;
		BasicDataSource basicDataSrc  = (BasicDataSource) dataSource; 

		LogContext context = new LogContext().appendMessage("Version and Health Check").add("DB", "jobsDataSource");
		LOG.info(context);

		if (!VersionHealthInfo.setVersionInfo(healthResponse, Constants.APPLICATION_SERVICE_NAME))
			success = false;	

		if (!VersionHealthInfo.addDBConnection(healthResponse, basicDataSrc))
			success = false;

		if (!success) {
			base.setResponseStatus(BaseResponse.responseStatusValues.Failure);
			base.setMessage("Health Check Failed");
			base.setReasonCode(-1);
		} else {
			base.setResponseStatus(BaseResponse.responseStatusValues.Success);
			base.setMessage("Health Check Succeeded");
			base.setReasonCode(1);
		}

		String clickStatus = "NOT CHECKED";

		try {
			if (checkClickService()) {
				clickStatus = "SUCCESS";
			} else {
				clickStatus = "FAILED";
				success = false;
			}
		} catch (Exception e) {
			clickStatus = "FAILED: " + e.getMessage();
			success = false;
		}
				
		if (!VersionHealthInfo.addServiceEndpoint(healthResponse, 
				clickEndPointMap.get("clickSOSEndpoint") != null ? clickEndPointMap.get("clickSOSEndpoint") : "",
				clickEndPointMap.get("clickUsername") != null ? clickEndPointMap.get("clickUsername") : "", clickStatus))
			success = false;
		
		LOG.info("VersionHealthCheck output: \n" + healthResponse.toJSONString());
		return healthResponse;	
	}

	private boolean checkClickService() throws URISyntaxException, ClientProtocolException, IOException {

		URI clickURI = new URIBuilder()
				.setScheme(clickEndPointMap != null ? clickEndPointMap.get("clickHealthScheme") : "")
				.setHost(clickEndPointMap != null ? clickEndPointMap.get("clickHealthHost") : "")
				.setPath(clickEndPointMap != null ? clickEndPointMap.get("clickHealthPath") : "")
				.setUserInfo(clickEndPointMap != null ? clickEndPointMap.get("clickUsername") : "",
						clickEndPointMap != null ? EncryptionHelper.decrypt(clickEndPointMap.get("clickEncryptedPassword")) : "")
				.build();
				
		HttpGet httpget = new HttpGet(clickURI);
		RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(timeout)
				.setSocketTimeout(timeout).setConnectionRequestTimeout(timeout).build();

		HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
		HttpResponse response = httpClient.execute(httpget);

		String responseString = new BasicResponseHandler().handleResponse(response);
		LOG.info("Click HealthCheck Response: " + responseString);
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() >= 300) {
			return false;
		}
		return true;
	}
}
