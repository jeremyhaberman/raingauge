package com.jeremyhaberman.raingauge.rest;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

	private URI mUri;
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();


	public Request(URI uri) {
		if (uri == null) {
			throw new IllegalArgumentException("uri is null");
		}
		this.mUri = uri;
	}

	public void addHeader(String key, List<String> value) {
		headers.put(key, value);
	}


	public URI getUri() {
		return mUri;
	}


	public Map<String, List<String>> getHeaders() {
		return headers;
	}
}
