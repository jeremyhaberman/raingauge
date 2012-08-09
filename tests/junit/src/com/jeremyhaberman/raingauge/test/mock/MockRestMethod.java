package com.jeremyhaberman.raingauge.test.mock;

import com.jeremyhaberman.raingauge.rest.method.RestMethod;
import com.jeremyhaberman.raingauge.rest.method.RestMethodResult;
import com.jeremyhaberman.raingauge.rest.resource.Resource;

public class MockRestMethod implements RestMethod {

	private Resource mResource;
	private int mStatusCode;

	public MockRestMethod(int statusCode, Resource resource) {
		mStatusCode = statusCode;
		mResource = resource;
	}
	
	@Override
	public RestMethodResult execute() {
		return new RestMethodResult<Resource>(mStatusCode, mResource);
	}

}
