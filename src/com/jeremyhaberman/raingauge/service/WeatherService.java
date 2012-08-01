package com.jeremyhaberman.raingauge.service;

import com.jeremyhaberman.raingauge.rest.Method;

public interface WeatherService {

	public static final String METHOD_EXTRA = "com.jeremyhaberman.raingauge.service.METHOD_EXTRA";
	public static final String RESOURCE_TYPE_EXTRA = "com.jeremyhaberman.raingauge.service.RESOURCE_TYPE_EXTRA";
	public static final String SERVICE_CALLBACK_EXTRA = "com.jeremyhaberman.raingauge.service.SERVICE_CALLBACK";
	public static final String ORIGINAL_INTENT_EXTRA = "com.jeremyhaberman.raingauge.service.ORIGINAL_INTENT_EXTRA";
	public static final String EXTRA_REQUEST_PARAMETERS = "com.jeremyhaberman.raingauge.service.EXTRA_REQUEST_PARAMETERS";
	
	public static final int REQUEST_INVALID = -1;
	public static final String EXTRA_STATUS_CODE = "status_code";

	public static final int RESOURCE_TYPE_OBSERVATIONS = 1;

	public static final String METHOD_GET = Method.GET.toString();
	
	public static final String ZIP_CODE = "zipCode";
	
	
	
}