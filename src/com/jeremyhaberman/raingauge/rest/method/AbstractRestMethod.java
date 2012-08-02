package com.jeremyhaberman.raingauge.rest.method;

import com.jeremyhaberman.raingauge.rest.DefaultRestClient;
import com.jeremyhaberman.raingauge.rest.Request;
import com.jeremyhaberman.raingauge.rest.Response;
import com.jeremyhaberman.raingauge.rest.RestClient;
import com.jeremyhaberman.raingauge.rest.resource.Resource;
import com.jeremyhaberman.raingauge.util.Logger;

import java.net.URI;
import java.util.List;
import java.util.Map;

public abstract class AbstractRestMethod<T extends Resource> implements RestMethod<T> {

	private static final String DEFAULT_ENCODING = "UTF-8";
	private RestClient mRestClient;

	public RestMethodResult<T> execute() {

		Request request = buildRequest();
		Response response = doRequest(request);
		return buildResult(response);
	}

	public void setRestClient(RestClient client) {
		mRestClient = client;
	}

	/**
	 * Subclasses can overwrite for full control, eg. need to do special
	 * inspection of response headers, etc.
	 *
	 * @param response
	 * @return
	 */
	protected RestMethodResult<T> buildResult(Response response) {

		int status = response.getStatus();
		String responseBody = null;
		T resource = null;

		try {
			responseBody = new String(response.getBody(), getCharacterEncoding(response.getHeaders()));
			logResponse(status, responseBody);
			resource = parseResponseBody(responseBody);
		} catch (Exception ex) {
			Logger.error(getLogTag(), "Error in buildResult", ex);
			// our own internal error code, not from service
			// spec only defines up to 505
			status = 506;
		}
		return new RestMethodResult<T>(status, resource);
	}

	protected abstract URI getURI();

	/**
	 * Returns the log tag for the class extending AbstractRestMethod
	 *
	 * @return log tag
	 */
	protected abstract String getLogTag();

	/**
	 * Build the {@link Request}.
	 *
	 * @return Request for this REST method
	 */
	protected abstract Request buildRequest();

	protected abstract T parseResponseBody(String responseBody) throws Exception;

	protected Response doRequest(Request request) {

		if (mRestClient == null) {
			mRestClient = DefaultRestClient.newInstance();
		}

		logRequest(request);
		return mRestClient.execute(request);
	}

	private String getCharacterEncoding(Map<String, List<String>> headers) {
		// TODO get value from headers
		return DEFAULT_ENCODING;
	}

	private void logRequest(Request request) {
		Logger.debug(getLogTag(), "Request: " + request.getUri().toASCIIString());
	}

	private void logResponse(int status, String responseBody) {
		Logger.debug(getLogTag(), "Response: status=" + status + ", body=" + responseBody);
	}

}
