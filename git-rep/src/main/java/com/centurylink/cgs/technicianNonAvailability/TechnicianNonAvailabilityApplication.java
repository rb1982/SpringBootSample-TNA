package com.centurylink.cgs.technicianNonAvailability;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender.RemoveSoapHeadersInterceptor;

import com.centurylink.cgs.dispatchcommon.encryption.EncryptedPasswordDataSource;
import com.centurylink.cgs.dispatchcommon.encryption.EncryptionHelper;
import com.centurylink.cgs.dispatchcommon.reference.DispatchReference;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;
import com.centurylink.cgs.technicianNonAvailability.model.ApplicationContext;
import com.centurylink.cgs.technicianNonAvailability.service.ClickInterceptor;
import com.centurylink.cgs.technicianNonAvailability.service.CreateNonAvailabilityClickClient;

import com.centurylink.cgs.technicianNonAvailability.util.Constants;


@SpringBootApplication
@ComponentScan(basePackages = "com.centurylink.cgs.technicianNonAvailability")
public class TechnicianNonAvailabilityApplication {
	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(TechnicianNonAvailabilityApplication.class);

	@Value("${client.timeout}")
	private int timeout;

	@Value("${client.ssl.trust-store}")
	private Resource trustStore;

	@Value("${client.ssl.trust-store-password}")
	private String trustStorePassword;

	@Value("${jobs.datasource.username}")
	private String jobsUsername;

	@Value("${jobs.datasource.encryptedPassword}")
	private String jobsPassword;
	
	@Value("${sp.datasource.username}")
	private String spUsername;

	@Value("${sp.datasource.encryptedPassword}")
	private String spPassword;

	@Autowired
	private ApplicationContext applicationContext;
	
	HashMap<String, String> clickEndPointMap = new HashMap<String, String>();

	public static void main(String[] args) {
		LOG.info("TechnicianNonAvailabilityApplication::main::start");
		SpringApplication.run(TechnicianNonAvailabilityApplication.class, args);

		LOG.info("  >>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		LOG.info("  >> Tech NonAvailability Service is STARTED       <<");
		LOG.info("  >>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

	@Bean(name = "jobsDataSource")
	@ConfigurationProperties(prefix = "jobs.datasource")
	public DataSource dataSource() {
		EncryptedPasswordDataSource source = new EncryptedPasswordDataSource();
		source.setUsername(jobsUsername);
		source.setEncryptedPassword(jobsPassword);
		return source;
	}

	@Bean(name = "spDataSource")
	@ConfigurationProperties(prefix = "sp.datasource")
	public DataSource spDataSource() {
		EncryptedPasswordDataSource source = new EncryptedPasswordDataSource();
		source.setUsername(spUsername);
		source.setEncryptedPassword(spPassword);
		return source;
	}
	
	@Bean(name = "jobsJdbc") 
	public JdbcTemplate jdbcTemplate(DataSource jobsDataSource) { 
		return new JdbcTemplate(jobsDataSource); 
	} 
	
	@Bean(name = "spJdbc") 
	public JdbcTemplate spJdbcTemplate(DataSource spDataSource) { 
		return new JdbcTemplate(spDataSource); 
	} 

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("com.clicksoftware.serviceoptimizeservice");
		return marshaller;
	}

	@Bean
	public CreateNonAvailabilityClickClient clickClient(Jaxb2Marshaller marshaller) throws Exception {
		CreateNonAvailabilityClickClient client = new CreateNonAvailabilityClickClient();	
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		client.setMessageSender(httpComponentsMessageSender());	
		client.setDefaultUri(clickEndPointMap.get("clickSOSEndpoint"));
		ClientInterceptor[] interceptors  = client.getInterceptors();
		interceptors =  (ClientInterceptor[]) ArrayUtils.add(interceptors, new ClickInterceptor());
		client.setInterceptors(interceptors);
		return client;
	}

	@Bean
	public HttpComponentsMessageSender httpComponentsMessageSender() throws Exception {
		HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender(); 
		httpComponentsMessageSender.setConnectionTimeout(timeout);
		httpComponentsMessageSender.setReadTimeout(timeout);
		httpComponentsMessageSender.setHttpClient(httpClient());
		return httpComponentsMessageSender;
	}

	public HttpClient httpClient() throws Exception {
		DispatchReference ref = new DispatchReference();
		clickEndPointMap = ref.getReferenceMap(dataSource(), Constants.CLICK_ENDPOINT_CONFIGURATION);
		this.applicationContext.setClickEndPointMap(clickEndPointMap);
		LOG.info("TechnicianNonAvailabilityApplication::click properties from DB::"+clickEndPointMap);
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(clickEndPointMap.get("clickUsername"), EncryptionHelper.decrypt(clickEndPointMap.get("clickEncryptedPassword")));
		provider.setCredentials(AuthScope.ANY, credentials);		
		return HttpClientBuilder.create().setDefaultCredentialsProvider(provider).setSSLSocketFactory(sslConnectionSocketFactory())
				.addInterceptorFirst(new RemoveSoapHeadersInterceptor()).build();
	}

	public SSLConnectionSocketFactory sslConnectionSocketFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException  {
		// NoopHostnameVerifier essentially turns hostname verification off as otherwise following error
		// is thrown: java.security.cert.CertificateException: No name matching localhost found
		return new SSLConnectionSocketFactory(sslContext(), NoopHostnameVerifier.INSTANCE);
	}

	public SSLContext sslContext() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException  {
		return SSLContextBuilder.create()
				.loadTrustMaterial(trustStore.getFile(), trustStorePassword.toCharArray()).build();
	}	
}

