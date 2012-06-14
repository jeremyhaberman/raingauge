package com.jeremyhaberman.raingauge.rest.method;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.jeremyhaberman.raingauge.rest.DefaultRestClient;
import com.jeremyhaberman.raingauge.rest.Request;
import com.jeremyhaberman.raingauge.rest.Response;
import com.jeremyhaberman.raingauge.rest.RestClient;
import com.jeremyhaberman.raingauge.rest.resource.Resource;
import com.jeremyhaberman.raingauge.util.Logger;

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

	protected abstract Context getContext();

	/**
	 * Subclasses can overwrite for full control, eg. need to do special
	 * inspection of response headers, etc.
	 * 
	 * @param response
	 * @return
	 */
	protected RestMethodResult<T> buildResult(Response response) {

		int status = response.status;
		String statusMsg = "";
		String responseBody = null;
		T resource = null;

		try {
			responseBody = new String(response.body, getCharacterEncoding(response.headers));
			logResponse(status, responseBody);
			resource = parseResponseBody(responseBody);
		} catch (Exception ex) {
			// our own internal error code, not from service
			// spec only defines up to 505
			status = 506; 
			statusMsg = ex.getMessage();
		}
		return new RestMethodResult<T>(status, statusMsg, resource);
	}

	protected abstract URI getURI();

	protected String buildQueryString(Map<String, String> params) {

		StringBuilder queryStringBuilder = new StringBuilder();

		Iterator<String> paramKeyIter = params.keySet().iterator();

		if (paramKeyIter.hasNext()) {
			queryStringBuilder.append(getKeyValuePair(params, paramKeyIter.next()));
		}

		while (paramKeyIter.hasNext()) {
			queryStringBuilder.append("&" + getKeyValuePair(params, paramKeyIter.next()));
		}

		return queryStringBuilder.toString();
	}

	private String getKeyValuePair(Map<String, String> params, String key) {
		return key + "=" + params.get(key);
	}

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

	/**
	 * Determines whether the REST method requires authentication
	 * 
	 * @return <code>true</code> if authentication is required,
	 *         <code>false</code> otherwise
	 */
	protected boolean requiresAuthorization() {
		return true;
	}

	protected abstract T parseResponseBody(String responseBody) throws Exception;

	protected Response doRequest(Request request) {

		if (mRestClient == null) {
			mRestClient = new DefaultRestClient();
		}

		logRequest(request);
		return mRestClient.execute(request);
	}

	private String getCharacterEncoding(Map<String, List<String>> headers) {
		// TODO get value from headers
		return DEFAULT_ENCODING;
	}

	private void logRequest(Request request) {
		Logger.debug(getLogTag(), "Request: " + request.getMethod().toString() + " "
				+ request.getRequestUri().toASCIIString());
	}

	private void logResponse(int status, String responseBody) {
		Logger.debug(getLogTag(), "Response: status=" + status + ", body=" + responseBody);
	}

}
