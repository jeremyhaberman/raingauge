package com.jeremyhaberman.raingauge.processor;


public interface ResourceProcessorCallback {

	/**
	 * Returns the result of the resource request
	 * 
	 * @param remoteResultCode
	 * @param localResultCode
	 */
	void send(int resultCode, int statusCode);

}
