package com.jeremyhaberman.raingauge.processor;

import com.jeremyhaberman.raingauge.service.WeatherService.ResourceType;

public interface ProcessorFactory {

	public abstract ResourceProcessor getProcessor(ResourceType resourceType);

}