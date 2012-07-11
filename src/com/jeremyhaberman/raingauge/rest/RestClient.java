package com.jeremyhaberman.raingauge.rest;

public interface RestClient {

	/**
	 * Executes a {@link Request}.
	 * @param request
	 * @return the response
	 * @throws IllegalArgumentException if request is <code>null</code> or the request's method is unsupported
	 */
	public abstract Response execute(Request request);

}