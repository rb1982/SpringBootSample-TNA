package com.centurylink.cgs.technicianNonAvailability.dao;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.centurylink.cgs.technicianNonAvailability.logging.TechNonAvailabilityLogger;

@Repository
public class TechNonAvailabilityDaoImpl implements TechNonAvailabilityDao {
	private static final TechNonAvailabilityLogger LOG = TechNonAvailabilityLogger.getLogger(TechNonAvailabilityDaoImpl.class);

	@Autowired 
	@Qualifier("jobsJdbc") 
	protected JdbcTemplate jdbcTemplate; 
	
	@Autowired 
	@Qualifier("spJdbc") 
	protected JdbcTemplate spJdbcTemplate; 

	public List<Map<String, Object>> getDispatchRefData(String s) throws DataAccessException{
		LOG.debug("TechNonAvailabilityDaoImpl::getDispatchRefData::start");
		String GET_REF_DATA = "SELECT REF_KEY, REF_VALUE FROM DISPATCH_REF WHERE TABLE_NM=?";
		
			List<Map<String, Object>> refData =	jdbcTemplate.queryForList(GET_REF_DATA,s);	
			LOG.info("TechNonAvailabilityDaoImpl::getDispatchRefData::RefData from Database::"+ refData); 
			LOG.debug("TechNonAvailabilityDaoImpl::getDispatchRefData::end");
			return refData;	
	}
	
	public Map<String, Object> retrieveServiceAreaHirarchy(String district) throws IncorrectResultSizeDataAccessException {
		LOG.info("TechNonAvailabilityDaoImpl::retrieveServiceAreaHirarchy::district::"+district);
		String GET_TIMEZONE = "SELECT STATE, MARKET, TIMEZONE FROM DISPATCH.SERVICE_AREA_HIERARCHY WHERE service_area = ?";
		Map<String, Object>  serviceAreaHierarchy= null;	
		serviceAreaHierarchy = spJdbcTemplate.queryForMap(GET_TIMEZONE, district);		
		return serviceAreaHierarchy;
	}
}
