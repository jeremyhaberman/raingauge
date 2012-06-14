package com.jeremyhaberman.raingauge.rest;

public interface RestClient {

	public abstract Response execute(Request request);

}