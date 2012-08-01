package com.jeremyhaberman.raingauge.processor;

public interface ProcessorFactory {

	public abstract ResourceProcessor getProcessor(int resourceType);

}