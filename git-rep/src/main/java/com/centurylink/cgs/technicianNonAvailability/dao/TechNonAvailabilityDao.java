package com.centurylink.cgs.technicianNonAvailability.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface TechNonAvailabilityDao  {
	public List<Map<String, Object>> getDispatchRefData(String s) throws DataAccessException;
	public Map<String, Object> retrieveServiceAreaHirarchy(String district) throws DataAccessException;
}
