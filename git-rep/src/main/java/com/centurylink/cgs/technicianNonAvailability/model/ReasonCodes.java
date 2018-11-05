package com.centurylink.cgs.technicianNonAvailability.model;

public class ReasonCodes {
	public static final int SUCCESS = 1;	

	/*
	 * Click Processor
	 */
	public static final int CLICK_SOAP_FAULT = 1011;
	public static final int CLICK_SCHEDULE_ERROR = 1012;
	public static final int CLICK_REQUIRED_PROPERTY = 1013;
	public static final int CLICK_EXCEPTION = 1014;
	/*
	 *  TechNonAvailability Processing
	 */
	public static final int INPUT_VALIDATION_ERROR = 2011;
	public static final int JDBC_CONNECTION_ERROR = 2012;
	public static final int SQL_GRAMMAR_ERROR = 2013;
	public static final int SQL_EXCEPTION = 2014;
	public static final int GENERIC_EXCEPTION = 2015;
	public static final int GENERIC_PROCESSING_ERROR = 2016;
	public static final int CLICK_CONNECTION_ERROR=2017;
	public static final int REQUEST_VALIDATION_ERROR=2018;
	public static final int GREG_CAL_XML_REPRESENTATION_ERROR=2019;
	public static final int INPUT_OUTPUT_EXCEPTION = 2020;
	public static final int MARSHALLING_ERROR = 2021;
	public static final int URI_SYNTAX_ERROR = 2022;
	public static final int CLIENT_PTOTOCOL_ERROR = 2023;
	public static final int DATE_PARSE_ERROR= 2024;
	public static final int INCORRECT_RESULT_SIZE_EXCEPTION = 2025;
}
