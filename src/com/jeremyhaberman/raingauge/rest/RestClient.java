package com.jeremyhaberman.raingauge.rest;

public interface RestClient {

	int STATUS_CODE_IO_ERROR = -1;

    /**
	 * Executes a {@link Request}.
	 * @param request
	 * @return the response
	 * @throws IllegalArgumentException if request is <code>null</code> or the request's method is unsupported
	 */
	public abstract Response execute(Request request);

}