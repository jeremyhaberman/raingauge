package com.jeremyhaberman.raingauge.service;

import com.jeremyhaberman.raingauge.rest.Method;

public interface WeatherService {

	public static final String METHOD_EXTRA = "mn.aug.restfulandroid.service.METHOD_EXTRA";
	public static final String RESOURCE_TYPE_EXTRA = "mn.aug.restfulandroid.service.RESOURCE_TYPE_EXTRA";
	public static final String EXTRA_PROCESSOR = "com.jeremyhaberman.raingauge.service.WeatherService.EXTRA_PROCESSOR";
	public static final String SERVICE_CALLBACK_EXTRA = "mn.aug.restfulandroid.service.SERVICE_CALLBACK";
	public static final String ORIGINAL_INTENT_EXTRA = "mn.aug.restfulandroid.service.ORIGINAL_INTENT_EXTRA";
	public static final String EXTRA_REQUEST_PARAMETERS = "mn.aug.restfulandroid.service.REQUEST_PARAMS_EXTRA";
	
	public static final int REQUEST_INVALID = -1;
	public static final String EXTRA_STATUS_CODE = "status_code";
	
	public enum ResourceType {
		OBSERVATIONS
	};

	public static final String METHOD_GET = Method.GET.toString();
	
	public static final String ZIP_CODE = "zipCode";
	
	
	
}