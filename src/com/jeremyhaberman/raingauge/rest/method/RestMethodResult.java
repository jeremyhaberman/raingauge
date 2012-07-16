package com.jeremyhaberman.raingauge.rest.method;

import com.jeremyhaberman.raingauge.rest.resource.Resource;
import org.json.JSONException;
import org.json.JSONObject;

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

	@Override
	public String toString() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("statusCode", statusCode);
			obj.put("resource", resource);
			return obj.toString();
		} catch (JSONException e) {
			return super.toString();
		}
	}
}
