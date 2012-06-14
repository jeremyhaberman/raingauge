package com.jeremyhaberman.raingauge.test.mock;

import com.jeremyhaberman.raingauge.rest.method.RestMethod;
import com.jeremyhaberman.raingauge.rest.method.RestMethodResult;
import com.jeremyhaberman.raingauge.rest.resource.Resource;

public class MockRestMethod implements RestMethod {

	private Resource mResource;
	private int mStatusCode;
	private String mStatusMessage;

	public MockRestMethod(int statusCode, String statusMessage, Resource resource) {
		mStatusCode = statusCode;
		mStatusMessage = statusMessage;
		mResource = resource;
	}
	
	@Override
	public RestMethodResult execute() {
		return new RestMethodResult<Resource>(mStatusCode, mStatusMessage, mResource);
	}

}
