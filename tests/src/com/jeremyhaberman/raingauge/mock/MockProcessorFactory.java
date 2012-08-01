package com.jeremyhaberman.raingauge.mock;

import com.jeremyhaberman.raingauge.processor.ProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ResourceProcessor;

public class MockProcessorFactory implements ProcessorFactory {
	private MockProcessor mProcessor;

	@Override
	public ResourceProcessor getProcessor(int resourceType) {
		mProcessor = new MockProcessor();
		return mProcessor;
	}

	public MockProcessor getProcessor() {
		return mProcessor;
	}
}
