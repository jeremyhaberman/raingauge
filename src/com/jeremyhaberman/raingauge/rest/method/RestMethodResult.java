package com.jeremyhaberman.raingauge.rest.method;

import com.jeremyhaberman.raingauge.rest.resource.Resource;

public class RestMethodResult<T extends Resource> {
	
	private int statusCode = 0;
	private T resource;
	
	public RestMethodResult(int statusCode, T resource) {
		super();
		this.statusCode = statusCode;
		this.resource = resource;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public T getResource() {
		return resource;
	}
	
}
